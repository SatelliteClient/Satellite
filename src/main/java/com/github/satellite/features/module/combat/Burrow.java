package com.github.satellite.features.module.combat;

import java.awt.Color;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.utils.BlockUtils;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.InventoryUtils;
import com.github.satellite.utils.MovementUtils;
import com.github.satellite.utils.render.RenderUtils;
import com.google.common.base.Ticker;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

public class Burrow extends Module {
	
	public Burrow() {
		super("Burrow", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
	NumberSetting range;
	BooleanSetting checkInHole;

	@Override
	public void init() {
		super.init();
		this.checkInHole = new BooleanSetting("CheckInHole", null, true);
		this.range = new NumberSetting("Range", null, 5.2, 0, 100, .1);
		addSetting(range, checkInHole);
	}
	
	BlockPos render;
	int progress;
	int sleep;
	
	@Override
	public void onEnable() {
		int pistem = InventoryUtils.pickItem(33, false);
		int powtem = InventoryUtils.pickItem(152, false);
		int obitem = InventoryUtils.pickItem(49, false);
		BlockPos pos = new BlockPos(mc.player);
		
		InventoryUtils.setSlot(obitem);
		BlockUtils.doPlace(BlockUtils.isPlaceable(pos.add(0, -1, 1), 0, false), false);
		BlockUtils.doPlace(BlockUtils.isPlaceable(pos.add(0, 0, 1), 0, false), false);
		BlockUtils.doPlace(BlockUtils.isPlaceable(pos.add(0, 1, 1), 0, false), false);
		BlockUtils.doPlace(BlockUtils.isPlaceable(pos.add(0, 2, 1), 0, false), false);
		
		InventoryUtils.setSlot(pistem);
		BlockUtils.doPlace(BlockUtils.isPlaceable(pos.offset(EnumFacing.UP, 2), 0, false), false);
		
		InventoryUtils.setSlot(powtem);
		BlockUtils.doPlace(BlockUtils.isPlaceable(pos.offset(EnumFacing.UP, 3), 0, false), false);
		
		progress = 0;
	}
	
	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventUpdate) {
			int obsiitem = InventoryUtils.pickItem(49, false);
			BlockPos pos = new BlockPos(mc.player);
			
			if (progress==1) {
				double y = mc.player.posY;
				MovementUtils.vClip(-2);
			}
			if (progress==2) {
				InventoryUtils.setSlot(obsiitem);
				BlockUtils.doPlace(BlockUtils.isPlaceable(pos.offset(EnumFacing.UP, 2), 0, false), false);
				MovementUtils.vClip(2);
			}
			if (progress == 3) {
				toggle();
			}
			
			progress ++;
		}
	}
	
	@Override
	public void onDisable() {
		
	}
}