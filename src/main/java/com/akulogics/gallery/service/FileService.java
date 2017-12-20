package com.akulogics.gallery.service;

import com.akulogics.gallery.bean.*;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by zsolt_venczel on 2016.08.23
 */
@Service
public class FileService {

    private String PERMISSION_FILE;
    private final String GALLERY_PATH;
    private final Path GALLERY_PATH_ITEM;

    private String[] SUPPORTED_EXTENSIONS;
    private final String FULL_SIZE_DIR;
    private final String THUMBNAIL_DIR;

    private final Tika tika = new Tika();

    public FileService(
            @Value(value = "${permissionFile}") String permissionFile,
            @Value(value = "${galleryPath}") String galleryPath,
            @Value(value = "${extensions}") String[] extensions,
            @Value(value = "${fullSizeDir}") String fullSizeDir,
            @Value(value = "${thumbnailDir}") String thumbnailDir
    ) {
        this.PERMISSION_FILE = permissionFile;
        this.GALLERY_PATH = galleryPath;

        this.GALLERY_PATH_ITEM = Paths.get(GALLERY_PATH);

        this.SUPPORTED_EXTENSIONS = extensions;
        this.FULL_SIZE_DIR = fullSizeDir;
        this.THUMBNAIL_DIR = thumbnailDir;
    }

    private FileItem getItem(File path) {
        return getItem(GALLERY_PATH_ITEM.relativize(path.toPath()).toString());
    }

    private Predicate<File> validFileItem = file ->
            (file.isDirectory() && !file.getName().startsWith("!"))
                    || Arrays.stream(SUPPORTED_EXTENSIONS).anyMatch(file.getPath().toLowerCase()::endsWith);

    private FileItem getFileItem(File file, String path) {
        FileItem result = new FileItem();

        try {
            result.setMimeType(tika.detect(file));
        } catch (IOException e) {
            result.setMimeType("");
        }

        String fileFullName = file.getName();
        String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
        String fileExtension = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);

        String fileNameLowerExt = fileName + '.' + fileExtension.toLowerCase();
        String fileNameUpperExt = fileName + '.' + fileExtension.toUpperCase();

        Path galleryFullPathLowerExt = Paths.get(file.getParent(), FULL_SIZE_DIR, fileNameLowerExt);
        Path galleryFullPathUpperExt = Paths.get(file.getParent(), FULL_SIZE_DIR, fileNameUpperExt);
        Path galleryThumbPathLowerExt = Paths.get(file.getParent(), THUMBNAIL_DIR, fileNameLowerExt);
        Path galleryThumbPathUpperExt = Paths.get(file.getParent(), THUMBNAIL_DIR, fileNameUpperExt);

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

        return result;
    }

    private DirectoryItem getDirectoryItem(File file) {
        DirectoryItem result = new DirectoryItem();

        if (galleryHasItem(file)) {
            result.setEmpty(false);
        } else {
            result.setEmpty(true);
        }

        return result;
    }

    private FileItem getItem(String path) {
        FileItem result = null;

        Path galleryPath = Paths.get(GALLERY_PATH, path);
        if (Files.exists(galleryPath)) {
            File galleryPathFile = galleryPath.toFile();
            if (validFileItem.test(galleryPathFile)) {
                if (galleryPathFile.isDirectory()) {
                    result = getDirectoryItem(galleryPathFile);
                } else {
                    result = getFileItem(galleryPathFile, path);
                }
                result.setFile(galleryPathFile);
                result.setPath(path.replace("\\", "/"));
            }
        }

        return result;
    }

    private List<FileItem> getFilesForDirectory(DirectoryItem directoryItem) {
        List<FileItem> result = Collections.emptyList();
        if (directoryItem != null) {
            File[] files = directoryItem.getFile().listFiles();
            if (files != null) {
                result = Arrays.stream(files).map(this::getItem)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
        }
        return result;
    }

    private Predicate<File> validPermissionItem = file ->
            (!file.isDirectory() && PERMISSION_FILE.equals(file.getName()));

    private PermissionItem getPermissionItem(String path) {
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

    private boolean galleryHasItem(File directoryItem) {
        boolean result = false;

        File[] files = directoryItem.listFiles();
        if (files != null) {
            result = Arrays.stream(files).anyMatch(file -> Arrays.stream(SUPPORTED_EXTENSIONS).anyMatch(file.getPath().toLowerCase()::endsWith));
        }

        return result;
    }

    private Map<String, CacheableItem> itemCache = new HashMap<>();

    public void initItemCache() {
        System.gc();
        itemCache.put("", itemCacheBuilder(""));
        System.gc();
    }

    private CacheableItem itemCacheBuilder(String path) {
        LoggerService.log("Path: " + path);

        CacheableItem result = null;

        CacheableItem pathItem = itemCache.get(path);
        if (pathItem == null) {
            pathItem = getItem(path);
        }

        if (pathItem != null && pathItem.getItemType() == CacheableItem.ItemType.DIRECTORY) {
            DirectoryItem directoryItem = (DirectoryItem) pathItem;

            List<FileItem> pathItems = getFilesForDirectory(directoryItem);
            pathItems.forEach(item -> {
                item.setParent(directoryItem);
                itemCache.put(item.getPath(), item);
            });

            directoryItem.setDirectories(
                    pathItems.stream()
                            .filter(item -> item.getItemType() == CacheableItem.ItemType.DIRECTORY &&
                                    !item.getFile().getName().startsWith(FULL_SIZE_DIR) &&
                                    !item.getFile().getName().startsWith(THUMBNAIL_DIR))
                            .map(item -> (DirectoryItem) itemCacheBuilder(item.getPath()))
                            .collect(Collectors.toList())
            );

            directoryItem.setFiles(
                    pathItems.stream()
                            .filter(item -> item.getItemType() == CacheableItem.ItemType.FILE)
                            .collect(Collectors.toList())
            );

            String permissionItemPath = Paths.get(path, PERMISSION_FILE).toString();
            CacheableItem permissionItem = itemCache.get(permissionItemPath);
            if (permissionItem == null) {
                permissionItem = getPermissionItem(permissionItemPath);
                if (permissionItem == null) {
                    permissionItem = EmptyItem.getItem();
                }
                itemCache.put(permissionItemPath, permissionItem);
            }
            if (permissionItem.getItemType() == CacheableItem.ItemType.PERMISSION) {
                directoryItem.setPermissionItem((PermissionItem) permissionItem);
            }

            result = directoryItem;
        }

        return result;
    }

    private Map<String, CacheableItem> getFileItemCache() {
        return itemCache;
    }

    public FileItem fetchFileItem(String path) {
        FileItem result;
        CacheableItem cacheableItem = getFileItemCache().get(path);

        if (cacheableItem != null) {
            result = (FileItem) cacheableItem;
        } else {
            result = getItem(path);
            if (result != null) {
                result.setParent(fetchFileItem(Paths.get(result.getPath()).getParent().toString()));
            }
        }

        return result;
    }

    public DirectoryItem fetchDirectoryItem(String path) {
        DirectoryItem result = null;
        CacheableItem cacheableItem = getFileItemCache().get(path);

        if (cacheableItem != null && cacheableItem.getItemType() == CacheableItem.ItemType.DIRECTORY) {
            result = ((DirectoryItem) cacheableItem);
        }

        return result;
    }
}
