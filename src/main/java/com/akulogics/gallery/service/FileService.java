package com.akulogics.gallery.service;

import com.akulogics.gallery.bean.CacheableItem;
import com.akulogics.gallery.bean.DirectoryItem;
import com.akulogics.gallery.bean.FileItem;
import com.akulogics.gallery.bean.PermissionItem;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by zsolt_venczel on 2016.08.23
 */
public class FileService {

    private final String PERMISSION_FILE = System.getProperty("permissionFile", "gallery.permitted.list");

    private final String GALLERY_PATH = System.getProperty("galleryPath", ".");
    private final Path GALLERY_PATH_ITEM = Paths.get(GALLERY_PATH);

    private final String[] SUPPORTED_EXTENSIONS = System.getProperty("extensions", "jpg,jpeg,png,m4v,mp4,mkv,wmv,mov").split(",");
    private final String FULL_SIZE_DIR = System.getProperty("fullSizeDir", "full");
    private final String THUMBNAIL_DIR = System.getProperty("thumbnailDir", "thumb");

    private final Tika tika = new Tika();

    private static FileService service = null;

    public static FileService getService() {
        if (service==null) {
            synchronized (FileService.class.getClass()) {
                service = new FileService();
            }
        }
        return service;
    }

    private FileService() {
        initItemCache();
    }

    protected FileItem getFileItem(File path) {
        return getFileItem(GALLERY_PATH_ITEM.relativize(path.toPath()).toString());
    }

    Predicate<File> validFileItem = file ->
            (file.isDirectory() && !file.getName().startsWith("!"))
            || Arrays.stream(SUPPORTED_EXTENSIONS).anyMatch(file.getPath().toLowerCase()::endsWith);

    protected FileItem getFileItem(String path) {
        FileItem result = null;

        Path galleryPath = Paths.get(GALLERY_PATH, path);
        if (Files.exists(galleryPath)) {
            File galleryPathFile = galleryPath.toFile();
            if (validFileItem.test(galleryPathFile)) {

                if (galleryPathFile.isDirectory()) {
                    result = new DirectoryItem();
                } else {
                    result = new FileItem();
                }
                result.setDirectory(galleryPathFile.isDirectory());
                result.setFile(galleryPathFile);
                result.setPath(path.replace("\\", "/"));
                try {
                    result.setMimeType(tika.detect(galleryPathFile));
                } catch (IOException e) {
                    result.setMimeType("");
                }

                if (!result.isDirectory()) {
                    String fileFullName = galleryPathFile.getName();
                    String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
                    String fileExtension = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

                    String fileNameLowerExt = fileName + '.' + fileExtension.toLowerCase();
                    String fileNameUpperExt = fileName + '.' + fileExtension.toUpperCase();

                    Path galleryFullPathLowerExt = Paths.get(galleryPathFile.getParent(), FULL_SIZE_DIR, fileNameLowerExt);
                    Path galleryFullPathUpperExt = Paths.get(galleryPathFile.getParent(), FULL_SIZE_DIR, fileNameUpperExt);
                    Path galleryThumbPathLowerExt = Paths.get(galleryPathFile.getParent(), THUMBNAIL_DIR, fileNameLowerExt);
                    Path galleryThumbPathUpperExt = Paths.get(galleryPathFile.getParent(), THUMBNAIL_DIR, fileNameUpperExt);

                    String pathName = path.substring(0, path.length() - fileFullName.length());

                    if (Files.exists(galleryFullPathLowerExt)) {
                        result.setPathFull(pathName + FULL_SIZE_DIR + File.separator + fileNameLowerExt);
                    } else if (Files.exists(galleryFullPathUpperExt)) {
                        result.setPathFull(pathName + FULL_SIZE_DIR + File.separator + fileNameUpperExt);
                    }

                    if (Files.exists(galleryThumbPathLowerExt)) {
                        result.setPathThumb(pathName + THUMBNAIL_DIR + File.separator + fileNameLowerExt);
                    } else if (Files.exists(galleryThumbPathUpperExt)) {
                        result.setPathThumb(pathName + THUMBNAIL_DIR + File.separator + fileNameUpperExt);
                    }

                }

                if (result.isDirectory() && galleryHasItem(result)) {
                    result.setEmpty(false);
                } else {
                    result.setEmpty(true);
                }

            }
        }

        return result;
    }

    protected List<FileItem> getFileItems(FileItem fileItem) {
        List<FileItem> result = Collections.emptyList();
        if (fileItem!=null && fileItem.isDirectory()) {
            File[] files = fileItem.getFile().listFiles();
            if (files!=null) {
                result = Arrays.stream(files).map(this::getFileItem)
                        .filter(file -> file!=null)
                        .collect(Collectors.toList());
            }
        }
        return result;
    }

    Predicate<File> validPermissionItem = file ->
            (!file.isDirectory() && PERMISSION_FILE.equals(file.getName()));

    protected PermissionItem getPermissionItem(String path) {
        PermissionItem result = null;

        Path permissionPath = Paths.get(GALLERY_PATH, path);
        if (Files.exists(permissionPath)) {
            File permissionFile = permissionPath.toFile();
            if (validPermissionItem.test(permissionFile)) {
                try (Stream<String> lines = Files.lines(permissionPath)) {
                    result = new PermissionItem(lines.collect(Collectors.toSet()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    protected boolean galleryHasItem(FileItem fileItem) {
        boolean result = false;

        File[] files = fileItem.getFile().listFiles();
        if (files!=null) {
            result = Arrays.stream(files).anyMatch(file -> Arrays.stream(SUPPORTED_EXTENSIONS).anyMatch(file.getPath().toLowerCase()::endsWith));
        }

        return result;
    }

    private Map<String, CacheableItem> itemCache = new ConcurrentHashMap<>();

    protected void initItemCache() {
        itemCache.put("", itemCacheBuilder(""));
    }

    protected CacheableItem itemCacheBuilder(String path) {
        CacheableItem result = null;

        CacheableItem pathItem = itemCache.get(path);
        if (pathItem==null) {
            pathItem = getFileItem(path);
        }

        if (pathItem!=null && pathItem.getItemType() == CacheableItem.ItemType.DIRECTORY) {
            DirectoryItem directoryItem = (DirectoryItem)pathItem;

            List<FileItem> pathItems = getFileItems(directoryItem);
            pathItems.forEach(item -> {
                item.setParent(directoryItem);
                itemCache.put(item.getPath(), item);
            });

            directoryItem.setDirectories(
                    pathItems.stream()
                            .filter(item -> item.isDirectory() &&
                                    !item.getFile().getName().startsWith(FULL_SIZE_DIR) &&
                                    !item.getFile().getName().startsWith(THUMBNAIL_DIR))
                            .map(item -> (DirectoryItem) itemCacheBuilder(item.getPath()))
                            .collect(Collectors.toList())
            );

            directoryItem.setFiles(
                    pathItems.stream()
                            .filter(item -> !item.isDirectory())
                            .collect(Collectors.toList())
            );

            directoryItem.setPermissionItem(getPermissionItem(Paths.get(path, PERMISSION_FILE).toString()));

            result = directoryItem;
        }

        return result;
    }

    protected Map<String, CacheableItem> getFileItemCache() {
        return itemCache;
    }

    public FileItem fetchFileItem(String path) {
        FileItem result;
        CacheableItem cacheableItem = getFileItemCache().get(path);

        if (cacheableItem!=null) {
            result = (FileItem)cacheableItem;
        } else {
            result = getFileItem(path);
            if (result!=null) {
                result.setParent(fetchFileItem(Paths.get(result.getPath()).getParent().toString()));
            }
        }

        return result;
    }

    public DirectoryItem fetchDirectoryItem(String path) {
        DirectoryItem result = null;
        CacheableItem cacheableItem = getFileItemCache().get(path);

        if (cacheableItem!=null && cacheableItem.getItemType() == CacheableItem.ItemType.DIRECTORY) {
            result = ((DirectoryItem)cacheableItem);
        }

        return result;
    }
}
