package com.github.satellite.features.module.render;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.features.module.Module;
import com.github.satellite.utils.MovementUtils;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.input.Keyboard;

public class FreeCam extends Module {
	public FreeCam() {
		super("FreeCam", Keyboard.KEY_U, Category.RENDER);
	}

	EntityOtherPlayerMP renderViewEntity;

	@Override
	public void onEnable() {
		if (mc.player != null) {
			renderViewEntity = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
			renderViewEntity.setEntityBoundingBox(new AxisAlignedBB(0, 0, 0, 0, 0, 0));

			mc.world.addEntityToWorld(-500, renderViewEntity);

			renderViewEntity.setPositionAndRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
		}
		super.onEnable();
	}

	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventRenderWorld) {
			double deltaTime = 5.0D/mc.getDebugFPS();
			if (mc.player != null && renderViewEntity != null) {
				mc.setRenderViewEntity(renderViewEntity);
				renderViewEntity.setPositionAndRotation(renderViewEntity.prevPosX+MovementUtils.InputX()*deltaTime, renderViewEntity.prevPosY+MovementUtils.InputY()*deltaTime, renderViewEntity.prevPosZ+MovementUtils.InputZ()*deltaTime, MathHelper.wrapDegrees(mc.player.prevRotationYaw), mc.player.prevRotationPitch);
				renderViewEntity.setRotationYawHead(mc.player.prevRotationYaw);
				renderViewEntity.inventory = mc.player.inventory;
				renderViewEntity.setHealth(mc.player.getHealth());
			}
		}
		super.onEvent(e);
	}

	@Override
	public void onDisable() {
		mc.setRenderViewEntity(mc.player);
		mc.world.removeEntityFromWorld(-500);
		super.onDisable();
	}
}
