package com.akulogics.gallery.service;

import com.akulogics.gallery.bean.FileItem;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by zsolt_venczel on 2016.08.23
 */
public class FileService {

    public static final String GALLERY_PATH = System.getProperty("galleryPath", ".");
    public static final Path GALLERY_PATH_ITEM = Paths.get(GALLERY_PATH);

    public static final String[] SUPPORTED_EXTENSIONS = System.getProperty("extensions", "jpg,jpeg,png,m4v,mp4,mkv,wmv,mov").split(",");
    public static final String FULL_SIZE_DIR = System.getProperty("fullSizeDir", "full");
    public static final String THUMBNAIL_DIR = System.getProperty("thumbnailDir", "thumb");

    private String userId;
    private static Tika tika = new Tika();

    private FileService(String userId) {
        this.userId = userId;
    }

    public static FileService getService(String userId) {
        return new FileService(userId);
    }

    protected FileItem getFileItem(File path) {
        return getFileItem(GALLERY_PATH_ITEM.relativize(path.toPath()).toString());
    }

    Predicate<File> validFileItem = file ->
            (file.isDirectory() && !file.getName().startsWith("!") && !file.getName().startsWith(FULL_SIZE_DIR) && !file.getName().startsWith(THUMBNAIL_DIR))
            || Arrays.stream(SUPPORTED_EXTENSIONS).anyMatch(file.getPath().toLowerCase()::endsWith);

    public FileItem getFileItem(String path) {
        FileItem result = null;

        Path galleryPath = Paths.get(GALLERY_PATH, path);
        if (Files.exists(galleryPath) && AuthenticationService.getService().checkPathPermission(galleryPath, userId)) {
            File galleryPathFile = galleryPath.toFile();
            if (validFileItem.test(galleryPathFile)) {

                result = new FileItem();
                result.setDirectory(galleryPathFile.isDirectory());
                result.setFile(galleryPathFile);
                result.setPath(path.replace("\\", "/"));
                try {
                    result.setMimeType(tika.detect(galleryPathFile));
                } catch (IOException e) {
                    result.setMimeType("");
                }

                if (!result.isDirectory()) {
                    String fileName = galleryPathFile.getName();

                    Path galleryFullPath = Paths.get(galleryPathFile.getParent(), FULL_SIZE_DIR, fileName);
                    Path galleryThumbPath = Paths.get(galleryPathFile.getParent(), THUMBNAIL_DIR, fileName);

                    if (Files.exists(galleryFullPath)) {
                        result.setPathFull(path.substring(0, path.length()-fileName.length()) + FULL_SIZE_DIR + File.separator+ galleryPathFile.getName());
                    }
                    if (Files.exists(galleryThumbPath)) {
                        result.setPathThumb(path.substring(0, path.length() - fileName.length()) + THUMBNAIL_DIR + File.separator + galleryPathFile.getName());
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

    public List<FileItem> getFileItems(FileItem fileItem) {
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

    protected boolean galleryHasItem(FileItem fileItem) {
        boolean result = false;

        File[] files = fileItem.getFile().listFiles();
        if (files!=null) {
            result = Arrays.stream(files).anyMatch(file -> Arrays.stream(SUPPORTED_EXTENSIONS).anyMatch(file.getPath().toLowerCase()::endsWith));
        }

        return result;
    }

}
