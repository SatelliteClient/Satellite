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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Tracers extends Module {

	public Tracers() {
		super("Tracers", Keyboard.KEY_F4, Category.RENDER);
	}

	@Override
	public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
        	for(int i=0; i<(mc.player.ticksExisted%20==0?20*10:0); i++) {
        		//mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        	}
        }
		if(e instanceof EventRenderWorld) {
			//EntityPlayerSP thePlayer = mc.player;
			
			//test ima burokku wo fill sitanowo render sitai
			//for (Entity entity : mc.world.loadedEntityList) {
			//	mc.getRenderManager().renderEntityStatic(entity, mc.timer.partialTicks, false);
			//}

            //GlStateManager.depthFunc(519);
            //GlStateManager.disableFog();
            
			/*GlStateManager.pushMatrix();
			GL11.glBlendFunc(770, 771);
			GL11.glEnable(3042);
			GL11.glEnable(2848);
			GL11.glLineWidth(3F);
			GL11.glDisable(3553);
			GL11.glDisable(2929);
			GL11.glDepthMask(false);
			GL11.glBegin(1);

			for (Entity entity : mc.world.loadedEntityList) {
				if(entity.getUniqueID() == mc.player.getUniqueID())
					continue;
				if(!(entity instanceof EntityPlayer))
					continue;
				if(entity.isInvisible())
					continue;
				Color color = ThemeManager.getTheme().dark(0);
				drawTraces(entity, color);
			}
			GL11.glEnd();
			GL11.glEnable(3553);
			GL11.glDisable(2848);
			GL11.glEnable(2929);
			GL11.glDepthMask(true);
			GL11.glDisable(3042);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			GlStateManager.popMatrix();*/
			
			
			
			GlStateManager.pushMatrix();
			
			GL11.glBlendFunc(770, 771);
			GL11.glLineWidth(1.5F);
			GL11.glBegin(1);

			for (Entity entity : mc.world.loadedEntityList) {
				if(entity.getUniqueID() == mc.player.getUniqueID())
					continue;
				if(!(entity instanceof EntityPlayer))
					continue;
				if(entity.isInvisible())
					continue;
				Color color = ThemeManager.getTheme().light(0);
				drawTraces(entity, color);
			}
			GL11.glEnd();

			GlStateManager.popMatrix();
		}
		super.onEvent(e);
	}
	
	private final void drawTraces(Entity entity, Color color) {
      EntityPlayerSP thePlayer = mc.player;
      double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getRenderPartialTicks() - 
        mc.getRenderManager().viewerPosX;
      double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks() - 
        mc.getRenderManager().viewerPosY;
      double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getRenderPartialTicks() - 
        mc.getRenderManager().viewerPosZ;
      WVec3 eyeVector = (new WVec3(0.0D, 0.0D, 1.0D))
    		  .rotatePitch((float)-Math.toRadians(thePlayer.rotationPitch))
    		  .rotateYaw((float)-Math.toRadians(thePlayer.rotationYaw));
      
      RenderUtils.glColor(color);
	  GL11.glVertex3d(eyeVector.getXCoord(), thePlayer.getEyeHeight() + eyeVector.getYCoord(), eyeVector.getZCoord());
      //GL11.glVertex3d(1000*Math.sin(dir), 1000, 1000*Math.cos(dir));
      GL11.glVertex3d(x, y, z);
      GL11.glVertex3d(x, y, z);
      GL11.glVertex3d(x, y + entity.getEyeHeight(), z);
      return;
  }
	
}
