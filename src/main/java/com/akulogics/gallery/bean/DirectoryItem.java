package com.akulogics.gallery.bean;

import java.util.List;

/**
 * Created by zsolt_venczel on 2016.08.30
 */
public class DirectoryItem extends FileItem {

    private List<FileItem> files;
    private List<DirectoryItem> directories;
    private PermissionItem permissionItem;

    public List<FileItem> getFiles() {
        return files;
    }

    public void setFiles(List<FileItem> files) {
        this.files = files;
    }

    public List<DirectoryItem> getDirectories() {
        return directories;
    }

    public void setDirectories(List<DirectoryItem> directories) {
        this.directories = directories;
    }

    public PermissionItem getPermissionItem() {
        return permissionItem;
    }

    public void setPermissionItem(PermissionItem permissionItem) {
        this.permissionItem = permissionItem;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.DIRECTORY;
    }
}
