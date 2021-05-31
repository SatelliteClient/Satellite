package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.BlockUtils;
import com.github.satellite.utils.InventoryUtils;
import com.github.satellite.utils.render.RenderUtils;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class Surround extends Module {

	public Surround() {
		super("Surround", Keyboard.KEY_M, Category.COMBAT);
	}

	ModeSetting mode;
	BooleanSetting swing;

    @Override
    public void init() {
    	this.swing = new BooleanSetting("Swing", true);
    	this.mode = new ModeSetting("Mode", "Normal", new String[] {"Normal", "SelfTrap"});
		addSetting(mode, swing);
    	super.init();
    }

	@Override
	public void onEvent(Event<?> e) {
		BlockPos[] block = new BlockPos[] {};
		
		switch(mode.getMode()) {
		
		case "Normal":
		{
			block = new BlockPos[] {
					new BlockPos(0, -1, 0),
					new BlockPos(1, -1, 0), new BlockPos(-1, -1, 0), new BlockPos(0, -1, 1), new BlockPos(0, -1, -1),
					new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
			break;
		}
		
		case "SelfTrap":
		{
			block = new BlockPos[] {
					new BlockPos(0, -1, 0), new BlockPos(0, 2, 0),
					new BlockPos(0, 2, 1),
					new BlockPos(1, -1, 0), new BlockPos(-1, -1, 0), new BlockPos(0, -1, 1), new BlockPos(0, -1, -1),
					new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1),
					new BlockPos(1, 1, 0), new BlockPos(-1, 1, 0), new BlockPos(0, 1, 1), new BlockPos(0, 1, -1)};
			break;
		}
		
		}
		
		if(e instanceof EventUpdate) {
		}
		if(e instanceof EventMotion) {
			EventMotion event = (EventMotion)e;

			if(!mc.player.movementInput.sneak)
				return;
			
			BlockPos pos = new BlockPos(mc.player);
			mc.player.motionX = ((pos.getX()+0.5)-mc.player.posX)/2;
			mc.player.motionZ = ((pos.getZ()+0.5)-mc.player.posZ)/2;
			for(BlockPos add : block) {
				int sandstone = InventoryUtils.pickItem(24);
				int currentItem = mc.player.inventory.currentItem;
				mc.player.inventory.currentItem = sandstone==-1?currentItem:sandstone;
				BlockUtils util = BlockUtils.isPlaceable(pos.add(add), 0, true);
				if(util != null) {
					util.doPlace(swing.isEnable());
					event.yaw = (float) util.rotx;
					event.pitch = (float) util.roty;
					mc.player.inventory.currentItem = currentItem;
					return;
				}
				mc.player.inventory.currentItem = currentItem;
			}
		}
		if(e instanceof EventRenderWorld) {
			for(BlockPos tart : block) {
				BlockPos pos = tart.add(mc.player.getPositionVector().x, mc.player.getPositionVector().y, mc.player.getPositionVector().z);
				Color color = mc.world.getBlockState(pos).isFullBlock()?new Color(237, 78, 66, 0x40):new Color(66, 237, 95, 0x40);
				RenderUtils.drawBlockBox(pos, color);
			}
		}
		super.onEvent(e);
	}
}