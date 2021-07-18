package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.utils.InventoryUtils;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

public class AutoSkull extends Module {
	
	public AutoSkull() {
		super("AutoSkull", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
    @Override
    public void init() {
    	super.init();
    }

	@Override
	public void onEvent(Event<?> e) {
		
        if(e instanceof EventUpdate) {
        	int skull = InventoryUtils.pickItem(397, false);
        	if(skull != -1) {
	        	int item = mc.player.inventory.currentItem;
        		mc.player.inventory.currentItem = skull;
        		mc.getConnection().sendPacket(new CPacketHeldItemChange(skull));
	        	mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(new BlockPos(mc.player).offset(EnumFacing.DOWN), EnumFacing.UP, EnumHand.MAIN_HAND, 0, -1, 0));
	        	mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        		mc.getConnection().sendPacket(new CPacketHeldItemChange(item));
	        	mc.player.inventory.currentItem = item;
        	}
        }
		super.onEvent(e);
	}
}
