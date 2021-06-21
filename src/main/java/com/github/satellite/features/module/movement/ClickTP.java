package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventHandleTeleport;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.ui.theme.ThemeManager;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.MovementUtils;
import com.github.satellite.utils.RayTraceUtils;
import com.github.satellite.utils.Rotation;
import com.github.satellite.utils.render.ColorUtils;
import com.github.satellite.utils.render.RenderUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class ClickTP extends Module {

    public ClickTP() {
        super("ClickTP", Keyboard.KEY_NONE, Category.MOVEMENT);
    }
    
    RayTraceResult rayTraceResult;

	@Override
	public void onEvent(Event<?> e) {
    	if(e instanceof EventHandleTeleport) {
    		EventHandleTeleport event = (EventHandleTeleport)e;
    		event.setPitch(mc.player.rotationPitch);
    		event.setYaw(mc.player.rotationYaw);
    	}
        if(e instanceof EventUpdate) {
        	this.rayTraceResult = new RayTraceUtils().rayTraceTowards(mc.player, new Rotation(mc.player.rotationYaw, mc.player.rotationPitch), 999);
        	if(Mouse.isButtonDown(1) && mc.currentScreen == null) {
            	if(rayTraceResult!=null) {
        			BlockPos pos = rayTraceResult.getBlockPos();
        			Vec3d lPos = mc.player.getPositionVector();
        			mc.player.setPosition(pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5);
        			MovementUtils.vClip2(0, true);
        			mc.player.setPosition(lPos.x, lPos.y, lPos.z);
        			MovementUtils.vClip2(-1E-10, true);
        	    	ClientUtils.setTimer(0.95F);
            	}
        	}
        }
        if(e instanceof EventMotion) {
        	if(Mouse.isButtonDown(1) && mc.currentScreen == null) {
            	EventMotion event = (EventMotion)e;
            	event.y -= 1E-10;
        	}
        }
    	if(e instanceof EventRenderWorld) {
    		if(rayTraceResult != null) {
    			BlockPos pos = rayTraceResult.getBlockPos();
    			double dx = mc.player.posX - (pos.getX()+0.5);
    			double dy = (pos.getY()+1.5) - mc.player.posY;
    			double dz = mc.player.posZ - (pos.getZ()+0.5);
    			int dist = (int)(Math.sqrt(dx*dx+dz*dz)+0.5);
    			Color solidColor = ThemeManager.getTheme().light(4);
    			RenderUtils.drawBlockSolid(pos, EnumFacing.UP, ColorUtils.alpha(solidColor, 0xff));
    			RenderUtils.drawBlockSolid(pos.offset(EnumFacing.UP), EnumFacing.DOWN, ColorUtils.alpha(solidColor, 0xff));
    			String str = "\u00A7fH:"+(dist>7?"\u00A77":"")+String.valueOf(dist)+"\u00A7f V:"+String.valueOf((int)dy);
				RenderUtils.drawPlate(mc.fontRenderer, str, new Vec3d(pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5), ColorUtils.alpha(ThemeManager.getTheme().dark(1), 0x8f));
				RenderUtils.drawString(mc.fontRenderer, str, new Vec3d(pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5), new Color(-1));
    		}
    	}
        super.onEvent(e);
    }

    @Override
    public void onDisable() {
    	ClientUtils.setTimer(1.0F);
        super.onDisable();
    }
}