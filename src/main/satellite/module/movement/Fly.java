package satellite.module.movement;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Timer;
import satellite.event.Event;
import satellite.event.listeners.EventMotion;
import satellite.event.listeners.EventRecievePacket;
import satellite.event.listeners.EventRenderGUI;
import satellite.event.listeners.EventUpdate;
import satellite.module.Module;
import satellite.utils.PlayerUtil;
import satellite.utils.RenderUtil;

public class Fly extends Module {

	public Fly() {
		super("Fly", Keyboard.KEY_F, Category.MOVEMENT);
	}
	
	@Override
	public void onDisable() {
		mc.player.capabilities.isFlying = false;
		tickTimer=0;
		super.onDisable();
	}

	double lastTickSpeed=0;
	double tickTimer=0;
	
	@Override
	public void onEvent(Event e) {
		PlayerUtil player = new PlayerUtil(mc.player);
			
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
			
			player.freeze();
			
			player.vClip(mc.player.onGround?0.42:0);
			player.Strafe(mc.player.onGround? 1 :lastTickSpeed<0.26?0.26:lastTickSpeed-lastTickSpeed/150);
			
			lastTickSpeed=mc.player.isCollidedHorizontally?0.24:player.getSpeed();
			
		}

		if(e instanceof EventRecievePacket) {
			EventRecievePacket event = (EventRecievePacket)e;
			Packet packet = event.getPacket();
			if(packet instanceof SPacketEntityVelocity) {
				event.setCansellReading(true);
			}
		}
		super.onEvent(e);
	}
}
