package com.github.satellite.features.module;

public class ModuleGuiObject extends Module {

    int x, y, width, height;
    boolean allowResize, allowMove;

    public ModuleGuiObject(String name, Category category, int x, int y, int width, int height, boolean allowResize, boolean allowMove) {
        super(name, category);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.allowResize = allowResize;
        this.allowMove = allowMove;
    }

    public ModuleGuiObject(String name, int keyCode, Category category, int x, int y, int width, int height, boolean allowResize, boolean allowMove) {
        super(name, keyCode, category);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.allowResize = allowResize;
        this.allowMove = allowMove;
    }

    public ModuleGuiObject(String name, int keyCode, Category category, boolean enable, int x, int y, int width, int height, boolean allowResize, boolean allowMove) {
        super(name, keyCode, category, enable);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.allowResize = allowResize;
        this.allowMove = allowMove;
    }
}
