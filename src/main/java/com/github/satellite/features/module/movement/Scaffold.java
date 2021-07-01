package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.BlockUtils;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.MovementUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;

import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Scaffold extends Module {


	public Scaffold () {
		super("Scaffold", Keyboard.KEY_V, Category.MOVEMENT);
	}

	ModeSetting mode;
	BooleanSetting keepY;
	BooleanSetting swing;
	BooleanSetting sneakSpoof;

	@Override
	public void init() {
		this.mode = new ModeSetting("Swing", "NCP", "NCP", "AAC", "Matrix");
		this.keepY = new BooleanSetting("keepY", false);
		this.swing = new BooleanSetting("Swing", true);
		this.sneakSpoof = new BooleanSetting("SneakSpoof", false);
		addSetting(mode, keepY, swing, sneakSpoof);
		super.init();
	}

	@Override
	public void onEnable() {
		lastGroundY=mc.player.posY;
		rand = new Random();
		randY = rand.nextFloat();
		super.onEnable();
	}

	ArrayList<BlockUtils> poss = new ArrayList<BlockUtils>();;

	double lastGroundY;

	boolean isDownMode = false;

	Vec3d targetPos;

	private boolean placingFlag;

	Random rand;
	float randY;

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

				placingFlag = false;
				if (sneakSpoof.isEnable())
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
				if (mode.is("NCP") || mode.is("Matrix")) {
					for(int i=0; i<1; i++) {
						if(!poss.isEmpty() && state.getBlock().isReplaceable(mc.world, pos)) {
							poss.sort(Comparator.comparingDouble(p -> p.dist));
							poss.get(0).doPlace(swing.isEnable());
							if (mc.player.movementInput.jump && mc.world.getBlockState(new BlockPos(mc.player).offset(EnumFacing.DOWN)).isFullCube()) {
								mc.player.setPosition(mc.player.posX, (int)mc.player.posY, mc.player.posZ);
								mc.player.jump();
								double motionY = .42;
								double posY = 0;
								for (int i1 = 0; i1<2; i1++) {
									mc.player.motionY = motionY;
									MovementUtils.move();
									mc.player.motionX *= .0;
									mc.player.motionZ *= .0;
									motionY -= 0.08;
									motionY *= 0.9800000190734863D;
									MovementUtils.vClip2(posY, false);
								}
								mc.player.motionY = motionY;
							}
							placingFlag = true;
						}
					}
				}
				if (mode.is("AAC") && !mc.player.onGround && mc.player.motionY<.1 && mc.player.ticksExisted%2==0) {
					for(int i=0; i<1; i++) {
						if(!poss.isEmpty() && state.getBlock().isReplaceable(mc.world, pos)) {
							poss.sort(Comparator.comparingDouble(p -> p.dist));
							poss.get(0).doPlace(swing.isEnable());
							placingFlag = true;
						}
					}
				}
				if (sneakSpoof.isEnable())
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

				if(!keepY.isEnable() && mc.world.getBlockState(new BlockPos(mc.player).offset(EnumFacing.DOWN, 2)).isFullBlock() && !poss.isEmpty() && mc.player.movementInput.jump && (int)mc.player.lastTickPosY<(int)mc.player.posY) {
					mc.player.setPosition(mc.player.posX, (int)mc.player.posY, mc.player.posZ);
					mc.player.motionY=0.42;
					MovementUtils.Strafe(mc.player.motionY/3);
				}

				if(mc.player.onGround)lastGroundY = mc.player.posY;
			}
		}
		if(e instanceof EventMotion) {
			EventMotion event = (EventMotion)e;
			if(!poss.isEmpty()) {
				if (mode.is("NCP")) {
					event.setYaw((float) poss.get(0).rotx);
					event.setPitch((float) poss.get(0).roty);
				}
				if (mode.is("AAC") && !mc.player.onGround && mc.player.motionY<.1) {
					event.yaw = mc.player.rotationYaw - 180;
					event.pitch = 83+mc.player.rotationPitch/180;
				}

				if (mode.is("Matrix")) {
					event.yaw = mc.player.rotationYaw - 180;
					event.pitch = 70f + randY*10;
					mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(String.valueOf(70f + randY*10)));
					//mc.player.rotationPitch = 80f + randY*10;
				}
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