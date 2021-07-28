package com.github.satellite.features.module.player;


import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.KeyBindSetting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InvMove extends Module {

	public InvMove() {
		super("InvMove", Keyboard.KEY_K, Category.PLAYER);
	}

	KeyBindSetting MouseMoveKey;

	@Override
	public void init() {
		MouseMoveKey = new KeyBindSetting("MouseMoveKey", null, 0);
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
				if( MouseMoveKey.getKeyCode() != 0 && Keyboard.isKeyDown(MouseMoveKey.getKeyCode())) {
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

			mc.gameSettings.keyBindForward.updateKeyBindState();
			mc.gameSettings.keyBindBack.updateKeyBindState();
			mc.gameSettings.keyBindLeft.updateKeyBindState();
			mc.gameSettings.keyBindRight.updateKeyBindState();
			mc.gameSettings.keyBindJump.updateKeyBindState();
			mc.gameSettings.keyBindSneak.updateKeyBindState();
			mc.gameSettings.keyBindSprint.updateKeyBindState();
		}
		super.onEvent(e);
	}

}
