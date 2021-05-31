package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.utils.BlockUtils;
import com.github.satellite.utils.PlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Comparator;

public class Scaffold extends Module {

	
	public Scaffold () {
		super("Scaffold", Keyboard.KEY_V, Category.MOVEMENT);
	}

	BooleanSetting keepY;
	BooleanSetting swing;

	@Override
	public void init() {
		this.keepY = new BooleanSetting("keepY", false);
		this.swing = new BooleanSetting("Swing", true);
		addSetting(keepY, swing);
		super.init();
	}

	@Override
	public void onEnable() {
		lastGroundY=mc.player.posY;
		super.onEnable();
	}

	ArrayList<BlockUtils> poss = new ArrayList<BlockUtils>();;

	double lastGroundY;

	boolean isDownMode = false;

	Vec3d targetPos;

	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventUpdate) {
			if(e.isPre()) {
				poss = new ArrayList<BlockUtils>();
				
				BlockPos target = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
				targetPos = new Vec3d(target.getX(), (int)(keepY.isEnable()?lastGroundY:mc.player.lastTickPosY)-1-(isDownMode?1:0), target.getZ());
				scanNearest(mc.playerController.getBlockReachDistance());

				BlockPos pos = new BlockPos(targetPos);
				IBlockState state = mc.world.getBlockState(pos);

				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
				for(int i=0; i<1; i++) {
					if(!poss.isEmpty() && state.getBlock().isReplaceable(mc.world, pos)) {
						poss.sort(Comparator.comparingDouble(p -> p.dist));
						poss.get(0).doPlace(swing.isEnable());
					}
				}
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

				if(!keepY.isEnable() && mc.world.getBlockState(new BlockPos(mc.player).offset(EnumFacing.DOWN, 2)).isFullBlock() && !poss.isEmpty() && mc.player.movementInput.jump && (int)mc.player.lastTickPosY<(int)mc.player.posY) {
					mc.player.setPosition(mc.player.posX, (int)mc.player.posY, mc.player.posZ);
					mc.player.motionY=0.42;
					PlayerUtils.Strafe(mc.player.motionY/3);
				}

				if(mc.player.onGround)lastGroundY = mc.player.posY;
			}
		}
		if(e instanceof EventMotion) {
			EventMotion event = (EventMotion)e;
			if(!poss.isEmpty()) {
				event.setYaw((float) poss.get(0).rotx);
				event.setPitch((float) poss.get(0).roty);
			}
		}
		if(e instanceof EventPlayerInput) {
			EventPlayerInput event = (EventPlayerInput)e;

			if(event.isSneak()) {
				isDownMode=true;
				event.setSneak(false);
			}else {
				isDownMode=false;
			}
		}
		super.onEvent(e);
	}

	public void scanNearest(double reach) {
		for(int dy=(int) -reach; dy<reach; dy++)
			for(int dx=(int) -reach; dx<reach; dx++)
				for(int dz=(int) -reach; dz<reach; dz++)
					if(Math.sqrt(dx*dx+dy*dy+dz*dz) <= reach) {
						int x = (int) (mc.player.posX + dx);
						int y = (int) (mc.player.posY + dy);
						int z = (int) (mc.player.posZ + dz);
						double mx = targetPos.x - x;
						double my = targetPos.y - y;
						double mz = targetPos.z - z;
						scanPos(new BlockPos(x, y, z), Math.sqrt(mx*mx+my*my+mz*mz));
					}
	}
	
	public void scanPos(double x, double y, double z, double dist) {
		scanPos(new BlockPos(x, y, z), dist);
	}
	
	public void scanPos(BlockPos pos, double dist) {
		 BlockUtils block = BlockUtils.isPlaceable(pos, dist, true);
		 if(block != null) poss.add(block);
	}
}