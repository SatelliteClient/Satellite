package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.mixin.client.AccessorEntityPlayer;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.PlayerUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;

public class Speed extends Module {


	public Speed () {
        super("Speed", Keyboard.KEY_Z, Category.MOVEMENT);
    }

    ModeSetting mode;

	@Override
	public void init() {
		this.mode = new ModeSetting("Mode", "NCP", new String[] {"NCP", "OldNCP", "YPort", "TEST"});
		addSetting(mode);
		super.init();
	}

	double lastSpeed;
	boolean lastGround;
	boolean moving;
    boolean OnGround = false;
	int tickTimer;
	int progress;
	double moveSpeed;
	double lastDist;
	int teleportIdF = 0;

	int teleportId = 0, clearLagTeleportId = 0;

	boolean inTimer;

	@Override
	public void onEvent(Event<?> e) {
    	if(e instanceof EventUpdate) {
    		EventUpdate event = (EventUpdate)e;
    		switch (mode.getMode()) {

				case "NCP":
					if (PlayerUtils.isMoving()) {
						if (mc.player.onGround) {
							mc.player.jump();
						}
						((AccessorEntityPlayer)mc.player).speedInAir(.0223F);
						PlayerUtils.Strafe(PlayerUtils.getSpeed());
					} else {
						mc.player.motionX = 0.0D;
						mc.player.motionZ = 0.0D;
					}
					break;
				case "OldNCP":
					if (PlayerUtils.isMoving()) {
						if (mc.player.onGround) {
							mc.player.jump();
							mc.player.motionX *= 1.05D;
							mc.player.motionZ *= 1.05D;
						}
						mc.player.motionY -= mc.player.motionY < .33319999363422365D ? 9.9999E-4D*5 : 0;

						PlayerUtils.Strafe(PlayerUtils.getSpeed() + (mc.player.motionY == .33319999363422365D && PlayerUtils.getSpeed() > 0.3 ? 0.05 : 0));
					} else {
						mc.player.motionX = 0.0D;
						mc.player.motionZ = 0.0D;
					}
					break;
				case "TEST":
					if (mc.player.motionY <= -.42D) {
						if(clearLagTeleportId<=teleportId)
						{
							clearLagTeleportId=teleportId+1;
						}

						if(clearLagTeleportId != 0) {
							PlayerUtils.vClip2(-1024, true);

							mc.getConnection().sendPacket(new CPacketConfirmTeleport(clearLagTeleportId));

							mc.player.motionY = 0;
							PlayerUtils.Strafe(.24D);
						}

						clearLagTeleportId++;
					}
			}
    	}

		if(e instanceof EventMotion) {
			EventMotion event = (EventMotion)e;
			switch (mode.getMode()) {

				case "YPort":
					if(mc.player.onGround && (mc.player.moveForward != 0 || mc.player.moveStrafing != 0)) {
						if(mc.player.ticksExisted % 2 != 0)
							event.y += .4;
						PlayerUtils.Strafe(mc.player.ticksExisted % 2 == 0 ? .45F : .2F);
						ClientUtils.setTimer(1.095F);
					}
			}
		}

		if(e instanceof EventPacket) {
			EventPacket event = (EventPacket)e;
			Packet p = event.getPacket();
			switch (mode.getMode()) {
				case "TEST":
					if(e instanceof EventPacket) {
						if(event.isIncoming()) {
							if (p instanceof SPacketPlayerPosLook) {
								SPacketPlayerPosLook packet = (SPacketPlayerPosLook)p;
								teleportId = packet.getTeleportId();
								if(mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) < 2) {
									event.setCancelled(true);
									mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportId));
								}else {
									clearLagTeleportId = teleportId;
								}
							}
						}
					}
			}
		}
    	if(e instanceof EventPlayerInput) {
    		EventPlayerInput event = (EventPlayerInput)e;
    		event.setJump(false);
    	}
        super.onEvent(e);
    }
    
    @Override
    public void onEnable() {
    	lastSpeed  = PlayerUtils.getSpeed();
		switch (mode.getMode()) {
			case "TEST":
				teleportId         = 0;
				clearLagTeleportId = 0;
				PlayerUtils.vClip2(999, true);
		}
    	super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}