package satellite.module.player;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import satellite.event.Event;
import satellite.event.listeners.EventKey;
import satellite.event.listeners.EventPlayerInput;
import satellite.event.listeners.EventRecievePacket;
import satellite.event.listeners.EventUpdate;
import satellite.module.Module;
import satellite.utils.PlayerUtil;

public class InvMove extends Module {

	public InvMove() {
		super("InvMove", Keyboard.KEY_K, Category.RENDER);
	}
	
	@Override
	public void onEvent(Event e) {
		if(e instanceof EventPlayerInput) {

			if(mc.currentScreen!=null) {
				if(Keyboard.isKeyDown(Keyboard.KEY_UP))
					mc.player.rotationPitch-=15;
				if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
					mc.player.rotationPitch+=15;
				if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
					mc.player.rotationYaw-=15;
				if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
					mc.player.rotationYaw+=15;
				
				if(mc.player.rotationPitch>90)
					mc.player.rotationPitch=90;
				if(mc.player.rotationPitch<-90)
					mc.player.rotationPitch=-90;	
			}
			
			EventPlayerInput event = (EventPlayerInput)e;
			
			if(Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()))
				event.setForward(true);

			if(Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()))
				event.setBack(true);

			if(Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()))
				event.setLeft(true);

			if(Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()))
				event.setRight(true);

			if(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()))
				event.setJump(true);
			
			if(Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()))
				event.setSneak(true);
		}
		super.onEvent(e);
	}
	
}
