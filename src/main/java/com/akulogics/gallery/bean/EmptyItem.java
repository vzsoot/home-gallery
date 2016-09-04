package com.akulogics.gallery.bean;

/**
 * Created by zsolt_venczel on 2016.08.31
 */
public class EmptyItem implements CacheableItem {

    private static EmptyItem emptyItem = new EmptyItem();

    private EmptyItem() {}

    public static EmptyItem getItem() {
        return emptyItem;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.EMPTY;
    }
}
