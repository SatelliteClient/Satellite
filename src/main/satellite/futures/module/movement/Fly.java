package satellite.futures.module.movement;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Timer;
import satellite.event.Event;
import satellite.event.listeners.EventHandleTeleport;
import satellite.event.listeners.EventMotion;
import satellite.event.listeners.EventRecievePacket;
import satellite.event.listeners.EventRenderGUI;
import satellite.event.listeners.EventSendingPacket;
import satellite.event.listeners.EventUpdate;
import satellite.futures.module.Module;
import satellite.setting.ModeSetting;
import satellite.utils.PlayerUtil;
import satellite.utils.RenderUtil;

public class Fly extends Module {

	public Fly() {
		super("Fly", Keyboard.KEY_F, Category.MOVEMENT);
	}
	
	@Override
	public void init() {
		settings.add(new ModeSetting("Mode", "Vannila", new String[] {"Vannila", "oldHypixel", "2b2t Japan"}));
		super.init();
	}
	
	@Override
	public void onDisable() {
		mc.player.capabilities.isFlying = false;
		tickTimer=0;
		teleportId=0;
		clearLagTeleportId=0;
		super.onDisable();
	}

	double lastTickSpeed=0;
	double tickTimer=0;
	int CansellingTeleport;
	int clearLagTeleportId;
	int teleportId;
	int speed;
	
	@Override
	public void onEvent(Event e) {
		PlayerUtil Player = new PlayerUtil(mc.player);
		
		switch(((ModeSetting)settings.get(1)).getMode()) {
		
		case "Vannila":
		{
			if(e instanceof EventMotion) {
				mc.player.motionY=Player.InputY();
				Player.Strafe(1);
				Player.Move();
				Player.freeze();
			}
			
			break;
		}
		
		
		
		case "oldHypixel":
		{
			if(e instanceof EventMotion) {
				EventMotion event = (EventMotion)e;
				double y;
				double y1;
				mc.player.motionY = 0;
				if(mc.player.ticksExisted % 3 == 0) {
					y = mc.player.posY - 1.0E-10D;
					event.y = y;
				}
				y1 = mc.player.posY + 1.0E-10D;
				
				mc.player.setPosition(mc.player.posX, y1, mc.player.posZ);
				
				Player.freeze();
				
				Player.vClip(mc.player.onGround?0.42:0);
				Player.Strafe(mc.player.onGround? 1 :lastTickSpeed<0.26?0.26:lastTickSpeed-lastTickSpeed/150);
				
				lastTickSpeed=mc.player.isCollidedHorizontally?0.24:Player.getSpeed();
			}

			if(e instanceof EventRecievePacket) {
				EventRecievePacket event = (EventRecievePacket)e;
				Packet packet = event.getPacket();
				if(packet instanceof SPacketEntityVelocity) {
					event.setCansellReading(true);
				}
			}
			
			break;
		}
		
		
		
		case "2b2t Japan":
		{
			if(e instanceof EventUpdate) {
				EventUpdate event = ((EventUpdate)e);
				if(e.isPre()) {
					mc.player.motionY=0;
					Player.vClip(Player.InputY());
					
					CansellingTeleport=0;
					
					for(int i = 0; i < 2*2; i++) {
						if(clearLagTeleportId<=teleportId)
						{
							clearLagTeleportId=teleportId+1;
						}
						
						Player.vClip2(999, false);
						mc.getConnection().sendPacket(new CPacketConfirmTeleport(clearLagTeleportId));
						
						boolean isMoving = mc.player.lastTickPosX != mc.player.posX || mc.player.lastTickPosZ != mc.player.posZ;
						
						Player.Strafe(2);
						Player.Move();
						Player.freeze();
						
						CansellingTeleport++;
						CansellingTeleport++;
						clearLagTeleportId++;	
					}
				}
			}
			
			if(e instanceof EventMotion) {
				EventMotion event = ((EventMotion)e);
				event.setYaw(0);
				event.setPitch(0);
			}
			
			if(e instanceof EventSendingPacket) {
				EventSendingPacket event = ((EventSendingPacket)e);
				Packet packet = event.getPacket();
				if(packet instanceof CPacketPlayer.Rotation)
					event.setCansellSending(true);

				if(packet instanceof CPacketPlayer.PositionRotation) {
					CPacketPlayer.PositionRotation pos = ((CPacketPlayer.PositionRotation) packet);
					mc.getConnection().sendPacket(new CPacketPlayer.Position(pos.getX(mc.player.posX), pos.getY(mc.player.posY), pos.getZ(mc.player.posZ), false));
				}
			}
			
			if(e instanceof EventHandleTeleport) {
				EventHandleTeleport event = ((EventHandleTeleport)e);

				event.setYaw(mc.player.cameraYaw);
				event.setPitch(mc.player.cameraPitch);
				
				teleportId=(int) event.getTeleportId();
				
				if(CansellingTeleport>0&&mc.player.getDistance(event.getX(), event.getY(), event.getZ()) < 16*2) {
					event.setCancellTeleporting(true);
				}
			}
		}
		}
		super.onEvent(e);
	}
}
