package satellite.module.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;
import satellite.event.Event;
import satellite.event.listeners.EventHandleTeleport;
import satellite.event.listeners.EventMotion;
import satellite.event.listeners.EventRecievePacket;
import satellite.event.listeners.EventUpdate;
import satellite.module.Module;
import satellite.utils.PlayerUtil;

public class PacketFly extends Module {

	public PacketFly() {
		super("PacketFly", Keyboard.KEY_G, Category.MOVEMENT);
	}
	
	@Override
	public void onDisable() {
		teleportId=0;
		clearLagTeleportId=0;
		speed=0.02;
		
		super.onDisable();
	}
	
	int teleportId=0, clearLagTeleportId=0, CansellingTeleport=0;
	double speed=0;

	@Override
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			mc.player.moveStrafing=1;
			EventUpdate event = ((EventUpdate)e);
			if(e.isPre()) {
				PlayerUtil Player = new PlayerUtil(mc.player);
				
				mc.player.motionY=0;
				
				CansellingTeleport=0;
				
				if(clearLagTeleportId<=teleportId)
				{
					clearLagTeleportId=teleportId+1;
				}
				
				Player.vClip2(999);
				mc.getConnection().sendPacket(new CPacketConfirmTeleport(clearLagTeleportId));
				
				boolean isMoving = mc.player.lastTickPosX != mc.player.posX || mc.player.lastTickPosZ != mc.player.posZ;
				if(mc.player.ticksExisted%3==0 && isMoving) {
					mc.player.motionY=-0.04;
					mc.player.onGround=true;
					Player.Move();
					Player.freeze();
				}
				if(mc.player.ticksExisted%5==0 && !isMoving) {
					mc.player.motionY=-0.04;
					mc.player.onGround=true;
					Player.Move();
					Player.freeze();
				}
					
				
				Player.Strafe(speed);
				Player.Move();
				Player.freeze();
				
				CansellingTeleport++;
				clearLagTeleportId++;
			}
		}
		
		if(e instanceof EventMotion) {
			EventMotion event = ((EventMotion)e);
			event.setYaw(0);
			event.setPitch(0);
		}
		
		if(e instanceof EventRecievePacket) {
			EventRecievePacket event = ((EventRecievePacket)e);
			Packet packet = event.getPacket();
			if(packet instanceof SPacketPlayerPosLook) {
				//clearLagTeleportId=((SPacketPlayerPosLook) packet).getTeleportId();
			}
		}
		
		if(e instanceof EventHandleTeleport) {
			EventHandleTeleport event = ((EventHandleTeleport)e);

			event.setYaw(mc.player.cameraYaw);
			event.setPitch(mc.player.cameraPitch);
			
			teleportId=(int) event.getTeleportId();
			
			if(CansellingTeleport>0&&mc.player.getDistance(event.getX(), event.getY(), event.getZ()) < 1) {
				event.setCancellTeleporting(true);
				//CansellingTeleport--;
				//speed=0.15;
				speed+=0.1;
				speed*=0.5;
			}else
			{
				speed=0.00;
			}
		}
		super.onEvent(e);
	}
	
}
