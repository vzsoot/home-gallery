package com.akulogics.gallery.bean;

/**
 * Created by zsolt_venczel on 2016.08.30
 */
public interface CacheableItem {

    enum ItemType {
        FILE, DIRECTORY, PERMISSION
    }

    ItemType getItemType();

}
