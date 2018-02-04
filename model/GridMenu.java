package com.car.cartique.model;

/**
 * Created by napster on 1/16/18.
 */

public class GridMenu {
    private String title;
    private int menuIcon;

    public GridMenu(String title, int menuIcon) {
        this.title = title;
        this.menuIcon = menuIcon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }
}
