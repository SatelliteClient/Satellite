package com.github.satellite.features.module.render;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.features.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

public class FreeCam extends Module {
	public FreeCam() {
		super("FreeCam", Keyboard.KEY_U, Category.RENDER);
	}
	
	double x = 0, y = 0, z = 0;

	@Override
	public void onEnable() {
		x = 0;
		y = 0;
		z = 0;
		super.onEnable();
	}

	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventRenderWorld) {
			GlStateManager.translate(x/mc.gameSettings.limitFramerate, y/mc.gameSettings.limitFramerate, z/mc.gameSettings.limitFramerate);
		}
		if (e instanceof EventPlayerInput) {
			EventPlayerInput event = (EventPlayerInput)e;
			y -= event.isJump()?1:0;
			y += event.isSneak()?1:0;

			double d = 0;
			float Forward = (event.isForward()?1:0)-(event.isBack()?1:0);
			float Strafing = (event.isRight()?1:0)-(event.isLeft()?1:0);

			double r = Math.atan2(Forward, Strafing)-1.57079633-Math.toRadians(mc.player.rotationYaw);

			if(Forward==0&&Strafing==0) {d=0;}

			x+=Math.sin(r)*d;
			z+=Math.cos(r)*d;
		}
		super.onEvent(e);
	}
}
