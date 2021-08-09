package com.github.satellite.features.module.combat;

import org.lwjgl.input.Keyboard;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.utils.InventoryUtils;

import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AutoTotem extends Module {
	
	public AutoTotem() {
		super("AutoTotem", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventUpdate) {
			ItemStack offhand = mc.player.getHeldItemOffhand();
			
			if (offhand.getItem() != Items.TOTEM_OF_UNDYING) {
				int totem = getTotemSlot();
                if (totem != -1) {
                	try {
                    	InventoryUtils.moveItem(totem, 45);
                	}catch(IndexOutOfBoundsException ea) {
                		
                	}
                }
			}
		}
	}
	
    public int getTotemSlot() {
        for (int i = 0; i < 36; i++) {
            final Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == Items.TOTEM_OF_UNDYING) {
                if (i < 9) {
                    i += 36;
                }
                return i;
            }
        }
        return -1;
    }

}
