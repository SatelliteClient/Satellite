package satellite.futures.module.render;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import satellite.event.Event;
import satellite.event.listeners.EventRecievePacket;
import satellite.event.listeners.EventRenderWorld;
import satellite.event.listeners.EventUpdate;
import satellite.futures.module.Module;
import satellite.utils.PlayerUtil;

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
	public void onEvent(Event e) {
		super.onEvent(e);
	}

	@Override
	public void onDisable() {
		mc.setRenderViewEntity(mc.player);
		mc.world.removeEntityFromWorld(-2);
		super.onDisable();
	}
}
