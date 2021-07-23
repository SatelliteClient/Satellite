package com.github.satellite.features.module.player;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventFlag;
import com.github.satellite.event.listeners.EventHandleTeleport;
import com.github.satellite.event.listeners.EventKey;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.KeyBindSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.MovementUtils;
import com.google.common.base.Supplier;

import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.CopyOnWriteArrayList;

public class NoFall extends Module {

	public NoFall() {
		super("NoFall", Keyboard.KEY_NONE, Category.PLAYER);
		teleportId=0;
	}

	ModeSetting mode;
	BooleanSetting checkElytra;
	KeyBindSetting elytra;

	@Override
	public void init() {
		addSetting(this.mode = new ModeSetting("Mode", null, "Packet", new String[] {"Packet", "NCP", "ElytraSpoof"}));
		addSetting(this.checkElytra = new BooleanSetting("CheckElytra", null, true));
		addSetting(this.elytra = new KeyBindSetting("Toggle Elytra", new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return checkElytra.isEnable();
			}
		}, 0));
		super.init();
	}

	int teleportId;
	CopyOnWriteArrayList<Vec3d> Catch = new CopyOnWriteArrayList<Vec3d>();

	Vec3d lastCatch = Vec3d.ZERO;

	boolean isFlying;

	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventKey) {
			if (((EventKey)e).code == elytra.keyCode) {
				isFlying = !isFlying;
				if (isFlying) {
					MovementUtils.vClip2(.01, false);
					mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
				}
			}
		}

		if (e instanceof EventMotion) {
			if (checkElytra.enable && isFlying) {
				ItemStack itemstack = mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
				if (itemstack.getItem() == Items.ELYTRA && ItemElytra.isUsable(itemstack))
				{
					return;
				}
			}
		}
		if (mode.is("Packet")) {
			if(e instanceof EventMotion) {
				EventMotion event = (EventMotion)e;
				event.setOnGround(true);
			}
		}
		if (mode.is("NCP")) {
			if(e instanceof EventMotion) {
				EventMotion event = (EventMotion)e;
				if(mc.player.fallDistance>5) {
					Catch.add(new Vec3d(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ));
					mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ);
					mc.player.motionY=0;
					MovementUtils.vClip2(999, true);
					ClientUtils.setTimer(0.95F);
					teleportId++;
					mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportId));
					mc.player.fallDistance=0;
				}
			}
			if(e instanceof EventHandleTeleport) {
				EventHandleTeleport event = (EventHandleTeleport)e;
				teleportId = event.teleportId;
			}
		}
		if (mode.is("ElytraSpoof")) {
			if (e instanceof EventFlag) {
				EventFlag event = (EventFlag)e;
				if (event.isOutgoing() && event.flag == 7 && event.entity == mc.player.getEntityId()) {
					event.setSet(false);
				}
			}
			if(e instanceof EventMotion) {
				EventMotion event = (EventMotion)e;
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
				event.pitch = 0;
			}

			//Matrix Damage Highjump
			/*if(e instanceof EventPacket) {
				if (e.isIncoming()) {
					Packet p = ((EventPacket)e).getPacket();
					if (p instanceof SPacketEntityVelocity) {
						SPacketEntityVelocity packet = (SPacketEntityVelocity)p;
						if (packet.getEntityID() == mc.player.getEntityId()) {
							if (mc.player.motionY < 0) {
								mc.player.motionY = 9;
								e.cancel();
							}
						}
					}
				}
			}
			if (e instanceof EventUpdate) {
				if (mc.player.isInWater()) {
					mc.player.motionY = 1;
				}
				if (mc.world.getBlockState(new BlockPos(mc.player).offset(EnumFacing.DOWN)).getMaterial() == Material.ICE) {
					mc.player.motionX *= 1.1;
					mc.player.motionZ *= 1.1;
				}
				if (mc.player.isInWeb) {
					mc.player.isInWeb = false;
					mc.player.motionY = MovementUtils.InputY();
					mc.player.motionX *= MovementUtils.InputY() == 0 ? 1 : 0;
					mc.player.motionZ *= MovementUtils.InputY() == 0 ? 1 : 0;
				}
				if (mc.player.isOnLadder()) {
					mc.player.motionX *= 3;
					mc.player.motionZ *= 3;
					mc.player.motionY = MovementUtils.InputY()*.42;
					if (MovementUtils.InputY() > 0) {
						mc.player.jump();
					}
				}
			}*/
		}
		super.onEvent(e);
	}

}
