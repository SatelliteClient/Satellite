package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.BlockUtils;
import com.github.satellite.utils.InventoryUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import org.lwjgl.input.Keyboard;

public class AntiCrystal extends Module {
	
	public AntiCrystal() {
		super("AntiCrystal", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
	ModeSetting mode;
	BooleanSetting swing;
	
    @Override
    public void init() {
    	super.init();
    	this.mode = new ModeSetting("mode", "Plate", new String[] {"Plate", "Fire"});
    	this.swing = new BooleanSetting("Swing", true);
    	addSetting(mode, swing);
    }

	@Override
	public void onEvent(Event<?> e) {
		
        if(e instanceof EventUpdate) {
        	int plate = InventoryUtils.pickItem(this.mode.is("Plate")?72:72, false);
        	if(plate != -1) {
	        	int item = mc.player.inventory.currentItem;
        		mc.player.inventory.currentItem = plate;
        		
    			for(Entity entity : mc.world.loadedEntityList) {
    				if(entity instanceof EntityEnderCrystal) {
    					if(entity.getDistance(mc.player)>8)
    						continue;
    					BlockUtils util = BlockUtils.isPlaceable(entity.getPosition(), 0, false);
    					if(util != null) {
    						util.doPlace(swing.isEnable());
    						break;
    					}
    				}
    			}

	        	mc.player.inventory.currentItem = item;
        	}
        }
		super.onEvent(e);
	}
}
