package com.akulogics.gallery.bean;

import java.util.Set;

/**
 * Created by zsolt_venczel on 2016.08.30
 */
public class PermissionItem implements CacheableItem {

    private Set<String> permission = null;

    public Set<String> getPermission() {
        return permission;
    }

    private PermissionItem() {}

    public PermissionItem(Set<String> permission) {
        this.permission = permission;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.PERMISSION;
    }
}
