package com.maryf.spotkeeper.model;

/**
 * Created by maryf on 6/26/2017.
 */
public class DrawerListItem {
    public int icon;
    public String name;

    //private static DrawerListItem ourInstance = new DrawerListItem();
    //public static DrawerListItem getInstance() {
    //    return ourInstance;
    //}

    // Constructor
    public DrawerListItem(int icon, String name) {

        this.icon = icon;
        this.name = name;
    }

}
