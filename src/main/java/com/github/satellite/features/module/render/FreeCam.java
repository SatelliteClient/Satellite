package com.github.satellite.features.module.render;

import com.github.satellite.event.Event;
import com.github.satellite.features.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import org.lwjgl.input.Keyboard;

public class FreeCam extends Module {
	public FreeCam() {
		super("FreeCam", Keyboard.KEY_U, Category.RENDER);
	}
	
	EntityOtherPlayerMP renderViewEntity;
	
	@Override
	public void onEnable() {
		renderViewEntity = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
		
		mc.world.addEntityToWorld(-2, renderViewEntity);

		renderViewEntity.setPositionAndRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
		
		super.onEnable();
	}
	
	@Override
	public void onEvent(Event<?> e) {
		super.onEvent(e);
	}

	@Override
	public void onDisable() {
		mc.setRenderViewEntity(mc.player);
		mc.world.removeEntityFromWorld(-2);
		super.onDisable();
	}
}
