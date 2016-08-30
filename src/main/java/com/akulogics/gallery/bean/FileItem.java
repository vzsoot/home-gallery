package com.akulogics.gallery.bean;

import java.io.File;

/**
 * Created by zsolt_venczel on 2016.08.23
 */
public class FileItem implements CacheableItem {

    private FileItem parent;
    private File file;
    private String path;
    private String pathFull;
    private String pathThumb;
    private String mimeType;
    private boolean directory;
    private boolean empty;

    public FileItem getParent() {
        return parent;
    }

    public void setParent(FileItem parent) {
        this.parent = parent;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPathFull() {
        return pathFull;
    }

    public void setPathFull(String pathFull) {
        this.pathFull = pathFull;
    }

    public String getPathThumb() {
        return pathThumb;
    }

    public void setPathThumb(String pathThumb) {
        this.pathThumb = pathThumb;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.FILE;
    }
}
