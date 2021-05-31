package com.github.satellite.features.module.world;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.InventoryUtils;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

public class AutoBuild extends Module {

    public AutoBuild() {
        super("AutoBuild", Keyboard.KEY_NONE, Category.WORLD);
    }
    
    Vec3d enablePos;
    
    @Override
    public void init() {
		settings.add(new ModeSetting("Mode", "Portal", new String[] {"Portal"}));
    	super.init();
    }
    
    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
	        setDisplayName("AutoBuild \u00A77" + ((ModeSetting)settings.get(1)).getMode());
	        int obsi = InventoryUtils.pickItem(49);
        	if(obsi != -1) {
	        	int item = mc.player.inventory.currentItem;
        		mc.player.inventory.currentItem = obsi;
	        	mc.player.inventory.currentItem = item;
        	}
        }
        super.onEvent(e);
    }
    
    @Override
    public void onEnable() {
    	enablePos = mc.player.getPositionVector();
    	super.onEnable();
    }
}