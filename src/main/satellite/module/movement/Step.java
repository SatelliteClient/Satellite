package com.satellite.module.other;

import org.lwjgl.input.Keyboard;

import satellite.module.Category;
import satellite.module.Module;
import satellite.module.util.PlayerUtil;

public class Test extends Module {

    public Test () {
        super("Test", Keyboard.KEY_NUMPAD0, Category.OTHER);
    }

    @Override
    public void onUpdate() {

    	PlayerUtil util	 = new PlayerUtil(mc.player);

    	if(mc.player.isCollidedHorizontally == true) {

    		util.vClip(2.9);
    	}
        super.onUpdate();
    }

    @Override
    public void onEnable() {
       mc.player.stepHeight = 2.9F;
       super.onEnable();
    }

    @Override
    public void onDisable() {
    	mc.player.stepHeight = 0.5F;
        super.onDisable();
    }
}
