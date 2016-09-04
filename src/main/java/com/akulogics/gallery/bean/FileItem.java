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

    @Override
    public ItemType getItemType() {
        return ItemType.FILE;
    }
}
