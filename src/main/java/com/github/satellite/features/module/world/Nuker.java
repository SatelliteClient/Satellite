package com.github.satellite.features.module.world;

import org.lwjgl.input.Keyboard;

import com.github.satellite.Satellite;
import com.github.satellite.command.CommandManager;
import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventSettingClicked;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.setting.Setting;
import com.github.satellite.ui.theme.ThemeManager;
import com.github.satellite.utils.render.ColorUtils;
import com.github.satellite.utils.render.RenderUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.BlockGlowstone;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class Nuker extends Module{
	
	public Nuker() {
		super("Nuker", 0, Category.WORLD);
	}
	
	NumberSetting range;
	
	@Override
	public void init() {
		addSetting(this.range = new NumberSetting("Block Id", null, 5, 0, 10, .1));
		super.init();
	}
	
	BlockPos target;
	
	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventMotion) {
//			System.out.println(mc.world.getBlockState(new BlockPos(mc.player).offset(EnumFacing.DOWN)).getPlayerRelativeBlockHardness(mc.player, mc.world, new BlockPos(mc.player).offset(EnumFacing.DOWN)));
			EventMotion event = (EventMotion)e;
			if (event.isPre()) return;
			float brk = 1;
			for (int dy = -(int)range.value+1; dy <= range.value+1; dy++) {
				for (int dx = -(int)range.value+1; dx <= range.value+1; dx++) {
					for (int dz = -(int)range.value+1; dz <= range.value+1; dz++) {
						if (brk <= 0) break;
						BlockPos pos = new BlockPos(mc.player).add(dx, dy, dz);
						if (pos.getY()<mc.player.posY) continue;
						if (mc.player.getDistanceSqToCenter(pos) > range.value*range.value) continue;
						if (mc.world.isAirBlock(pos)) continue;
						IBlockState state = mc.world.getBlockState(pos);
						if (state.getBlock() instanceof BlockEmptyDrops) continue;
						if (state.getBlock() instanceof BlockGlowstone) continue;
						if (state.getBlock() instanceof BlockLadder) continue;
						if (state.getPlayerRelativeBlockHardness(this.mc.player, this.mc.player.world, pos) == 0) continue;
						if (state.getBlock() instanceof BlockOre) continue;
						if (state.getPlayerRelativeBlockHardness(mc.player, mc.world, pos) < .5) continue;
						mc.playerController.clickBlock(pos, EnumFacing.UP);
						mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
						mc.world.setBlockToAir(pos);
						brk --;
					}
				}
			}
		}
		super.onEvent(e);
	}

}
