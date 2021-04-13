package satellite.futures.module.render;

import java.awt.Color;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL45;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import satellite.event.Event;
import satellite.event.listeners.EventRenderGUI;
import satellite.event.listeners.EventRenderWorld;
import satellite.event.listeners.EventUpdate;
import satellite.futures.module.Module;
import satellite.utils.ColorUtil;
import satellite.utils.PlayerUtil;
import satellite.utils.RenderUtil;
import satellite.utils.WVec3;

public class Tracers extends Module {

	public Tracers() {
		super("Tracers", Keyboard.KEY_F4, Category.RENDER);
	}

	@Override
	public void onEvent(Event e) {
		if(e instanceof EventRenderWorld) {
			//EntityPlayerSP thePlayer = mc.player;
			
			//test ima burokku wo fill sitanowo render sitai
			//for (Entity entity : mc.world.loadedEntityList) {
			//	mc.getRenderManager().renderEntityStatic(entity, mc.timer.partialTicks, false);
			//}

            //GlStateManager.depthFunc(519);
            //GlStateManager.disableFog();
            
			GlStateManager.pushMatrix();
			
			GL11.glBlendFunc(770, 771);
			GL11.glEnable(3042);
			GL11.glEnable(2848);
			GL11.glLineWidth(1.5F);
			GL11.glDisable(3553);
			GL11.glDisable(2929);
			GL11.glDepthMask(false);
			GL11.glBegin(1);

			for (Entity entity : mc.world.loadedEntityList) {
				if(entity.getUniqueID() == mc.player.getUniqueID())
					continue;
				boolean bool = false;
				Color color = new Color(255, 255, 255, 90);
				drawTraces(entity, color);
			}
			GL11.glEnd();
			GL11.glEnable(3553);
			GL11.glDisable(2848);
			GL11.glEnable(2929);
			GL11.glDepthMask(true);
			GL11.glDisable(3042);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			GlStateManager.popMatrix();
		}
		super.onEvent(e);
	}
  
  private final void drawTraces(Entity entity, Color color) {
      EntityPlayerSP thePlayer = mc.player;
      double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.partialTicks - 
        mc.getRenderManager().viewerPosX;
      double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.partialTicks - 
        mc.getRenderManager().viewerPosY;
      double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.partialTicks - 
        mc.getRenderManager().viewerPosZ;
      WVec3 eyeVector = (new WVec3(0.0D, 0.0D, 1.0D))
    		  .rotatePitch((float)-Math.toRadians(thePlayer.rotationPitch))
    		  .rotateYaw((float)-Math.toRadians(thePlayer.rotationYaw));
      
      RenderUtil.glColor(color);
	  GL11.glVertex3d(eyeVector.getXCoord(), thePlayer.getEyeHeight() + eyeVector.getYCoord(), eyeVector.getZCoord());
      double dir = 0.05 * Math.toRadians(  System.currentTimeMillis() );
      //GL11.glVertex3d(1000*Math.sin(dir), 1000, 1000*Math.cos(dir));
      GL11.glVertex3d(x, y, z);
      GL11.glVertex3d(x, y, z);
      GL11.glVertex3d(x, y + entity.getEyeHeight(), z);
      return;
  }
	
}
