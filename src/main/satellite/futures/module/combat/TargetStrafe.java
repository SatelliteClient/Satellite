package satellite.futures.module.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockPortal;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import satellite.event.Event;
import satellite.event.listeners.EventRenderGUI;
import satellite.event.listeners.EventRenderWorld;
import satellite.event.listeners.EventUpdate;
import satellite.futures.module.Module;
import satellite.utils.ColorUtil;
import satellite.utils.RenderUtil;

public class TargetStrafe extends Module {

	public TargetStrafe() {
		super("Velocity", Keyboard.KEY_K, Category.COMBAT);
	}
	
	public ArrayList<BlockPos> searchedpos = new ArrayList<BlockPos>();
	
	@Override
	public void onEvent(Event e) {
		
		if(e instanceof EventUpdate) {
			int range = 32;

			searchedpos = new ArrayList<BlockPos>();
			
			for(int dy=-range; dy<range; dy++) {
				for(int dx=-range; dx<range; dx++) {
					for(int dz=-range; dz<range; dz++) {
						double x = dx + mc.player.posX;
						double y = dy + mc.player.posY;
						double z = dz + mc.player.posZ;
						if(!(mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() instanceof BlockAir))
							continue;
						if(!(mc.world.getBlockState(new BlockPos(x, y+1, z)).getBlock() instanceof BlockAir))
							continue;
						if(mc.world.getBlockState(new BlockPos(x, y-1, z)).getBlock() instanceof BlockAir)
							continue;
						int wall=0;
						if(mc.world.getBlockState(new BlockPos(x, y, z+1)).getBlock() instanceof BlockObsidian)
							wall++;
						if(mc.world.getBlockState(new BlockPos(x, y, z-1)).getBlock() instanceof BlockObsidian)
							wall++;
						if(mc.world.getBlockState(new BlockPos(x+1, y, z)).getBlock() instanceof BlockObsidian)
							wall++;
						if(mc.world.getBlockState(new BlockPos(x-1, y, z)).getBlock() instanceof BlockObsidian)
							wall++;
						if(wall >= 4)
							searchedpos.add(new BlockPos(x, y, z));
						/*if(!mc.world.getBlockState(new BlockPos(x, y-1, z)).isFullCube())
							continue;
						if(!(mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() instanceof BlockAir))
							continue;
						if(mc.world.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, new BlockPos(x, y, z))<5)
							searchedpos.add(new BlockPos(x, y, z));*/
					}
				}
			}
		}
		
		if(e instanceof EventRenderWorld) {
			
			
			
			

			float hue = (System.currentTimeMillis() % 4000) / 4000f;
			Color color = new Color(ColorUtil.HSBtoRGB(hue, 0.8F, 0.97F));
			color = new Color(255, 255, 255, 0x40);
			renderBox(new BlockPos(-4, 63, 0), color);
			renderBox(new BlockPos(-4, 64, 0), color);
			renderBox(new BlockPos(-4, 63, 1), color);
			renderBox(new BlockPos(-4, 64, 1), color);
			
			
			
			
			
			
			
			/*GL11.glPushMatrix();
			
			GL11.glBlendFunc(770, 771);
			GL11.glEnable(3042);
			GL11.glEnable(2848);
			GL11.glLineWidth(1);
			GL11.glDisable(3553);
			GL11.glDisable(2929);
			GL11.glDepthMask(false);
			GL11.glBegin(1);*/
			
			hue = (System.currentTimeMillis() % 4000) / 4000f;
			
			for(BlockPos pos : searchedpos)
				RenderUtil.drawBlockBox(pos, color);

			//RenderUtil.drawBlockBox(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ).offset(EnumFacing.DOWN), new Color(ColorUtil.HSBtoRGB((float) (hue), 0.8F, 0.97F)), false);
			
			/*GL11.glEnd();
			GL11.glEnable(3553);
			GL11.glDisable(2848);
			GL11.glEnable(2929);
			GL11.glDepthMask(true);
			GL11.glDisable(3042);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			GL11.glPopMatrix();*/
			
			
			
			/*GL11.glPushMatrix();
			
			GL11.glBlendFunc(770, 771);
			GL11.glEnable(3042);
			GL11.glEnable(2848);
			GL11.glLineWidth(1.5F);
			GL11.glDisable(3553);
			GL11.glDisable(2929);
			GL11.glDepthMask(false);
			GL11.glBegin(1);
			
			//float hue = (System.currentTimeMillis() % 4000) / 4000f;
			
			for (Entity entity : mc.world.loadedEntityList) {
				if(entity.getUniqueID() == mc.player.getUniqueID())
					continue;
				boolean bool = false;
				Color color1 = new Color(255, 255, 255);
				//RenderUtil.drawCircle(entity.getPositionVector(), 2, 64, new Color(0x90ffffff));
			}
			GL11.glEnd();
			GL11.glEnable(3553);
			GL11.glDisable(2848);
			GL11.glEnable(2929);
			GL11.glDepthMask(true);
			GL11.glDisable(3042);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			GL11.glPopMatrix();*/
		}
		super.onEvent(e);
	}
	
	public void renderBox(BlockPos pos, Color color) {
		double x=pos.getX();
		double y=pos.getY();
		double z=pos.getZ();
		
		x -= mc.getRenderManager().viewerPosX;
		y -= mc.getRenderManager().viewerPosY;
		z -= mc.getRenderManager().viewerPosZ;
		
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
		GL11.glDepthMask(true);
        
		AxisAlignedBB axisAlignedBB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.translate(-x, -y, -z);
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
	}
}
