package com.github.satellite.features.module.render;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.ui.theme.ThemeManager;
import com.github.satellite.utils.WVec3;
import com.github.satellite.utils.render.RenderUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Tracers extends Module {

	public Tracers() {
		super("Tracers", Keyboard.KEY_F4, Category.RENDER);
	}

	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventRenderWorld) {
			GlStateManager.pushMatrix();
			GlStateManager.disableDepth();

			WVec3 eyeVector = (new WVec3(0.0D, 0.0D, 1.0D))
					.rotatePitch((float)-Math.toRadians(mc.player.rotationPitch))
					.rotateYaw((float)-Math.toRadians(mc.player.rotationYaw));
			for (Entity e1 : mc.world.loadedEntityList) {
				RenderUtils.glColor(ThemeManager.getTheme().dark(0));
				RenderUtils.drawLine(new Vec3d(eyeVector.getXCoord(), mc.player.getEyeHeight() + eyeVector.getYCoord(), eyeVector.getZCoord()), RenderUtils.renderEntityPos(e1), 1f);
			}
			GlStateManager.enableDepth();
			GlStateManager.popMatrix();
		}
		super.onEvent(e);
	}
}
