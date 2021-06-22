package com.github.satellite.features.module.player;


import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.KeyBindSetting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InvMove extends Module {

	public InvMove() {
		super("InvMove", Keyboard.KEY_K, Category.PLAYER, 1000);
	}
	
	KeyBindSetting MouseMoveKey;
	
	@Override
	public void init() {
		MouseMoveKey = new KeyBindSetting("MouseMoveKey", 0);
		addSetting(MouseMoveKey);
		super.init();
	}

	int lastMouseX = Mouse.getX();
	int lastMouseY = Mouse.getY();
	
	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventPlayerInput) {
			if(mc.currentScreen!=null) {
				double speed=0;
				if(Keyboard.isKeyDown(MouseMoveKey.getKeyCode())) {
					mc.player.rotationYaw += Mouse.getDX() * mc.gameSettings.mouseSensitivity;
					mc.player.rotationPitch -= Mouse.getDY() * mc.gameSettings.mouseSensitivity;
					Mouse.setCursorPosition(lastMouseX, lastMouseY);
				}

		        boolean flag4 = (float)mc.player.getFoodStats().getFoodLevel() > 6.0F || mc.player.capabilities.allowFlying;
				
				if(Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode())) {
					mc.player.setSprinting(true);
				}
		        if (mc.player.isSprinting() && (mc.player.movementInput.moveForward < 0.8F || mc.player.collidedHorizontally || !flag4))
		        {
		            mc.player.setSprinting(false);
		        }
				if(Keyboard.isKeyDown(Keyboard.KEY_UP))
					mc.player.rotationPitch-=speed;
				if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
					mc.player.rotationPitch+=speed;
				if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
					mc.player.rotationYaw-=speed;
				if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
					mc.player.rotationYaw+=speed;
				
				if(mc.player.rotationPitch>90)
					mc.player.rotationPitch=90;
				if(mc.player.rotationPitch<-90)
					mc.player.rotationPitch=-90;	
				
				lastMouseX = Mouse.getX();
				lastMouseY = Mouse.getY();
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
