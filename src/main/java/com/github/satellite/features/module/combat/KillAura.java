package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Random;

public class KillAura extends Module {
	
	BooleanSetting rotation;
	
	public KillAura() {
		super("KillAura", Keyboard.KEY_R,	Category.COMBAT);
	}
	
	@Override
	public void init() {
		rotation = new BooleanSetting("Rotation", true);
		addSetting(rotation);
		super.init();
	}
	
	ArrayList<Entity> targets;
	
	float yaw = 0, pitch = 0;

	@Override
	public void onEvent(Event<?> e) {

        if(e instanceof EventUpdate) {
            if(e.isPre()) {
            	
            	targets = new ArrayList<Entity>();
            	
            	for(Entity entity : mc.world.loadedEntityList) {
            		//if(!(entity instanceof EntityPlayer))
            		//	continue;
            		
            		if(!entity.isEntityAlive())
            			continue;
            		if(entity.isInvisible())
            			continue;
            		
            		if(entity instanceof EntityLivingBase && entity != mc.player && entity.getDistance(mc.player) < mc.playerController.getBlockReachDistance()-1) {
            			targets.add(entity);
            		}
            	}
            	
            	if(!targets.isEmpty()) {
            		Entity target = targets.get(0);
					
					if(mc.player.getCooledAttackStrength(0.0f) > 0.9f && mc.player.ticksExisted%3==0) {
	            		mc.playerController.attackEntity(mc.player, target);
	            		mc.player.swingArm(EnumHand.MAIN_HAND);
					}
            	}
            }
        }
        
        if(e instanceof EventMotion) {
        	EventMotion event = (EventMotion)e;
        	if(!targets.isEmpty() && rotation.isEnable()) {
        		Entity target = targets.get(0);
        		double dx = mc.player.posX-target.posX;
        		double dy = mc.player.posY + mc.player.getEyeHeight() + mc.player.motionY*mc.player.motionY - (target.posY + target.getEyeHeight()/2);
        		double dz = mc.player.posZ-target.posZ;
        		double distxz = Math.sqrt(dx*dx + dz*dz);
    			double x = Math.atan2(dx, dz) * 57.2958;
    			double y = Math.atan2(dy, distxz) * 57.2958;
    			
    			boolean antibot = false;
    			
    			if(antibot) {
        			double random = 1 / (Math.abs(new Random().nextInt(1)) + 1);
        			
        			x+=new Random().nextInt(1)-1.5;
        			y+=new Random().nextInt(5)-2.5;
        			
        			yaw += (180-(float)x - yaw) * random/1.33;
        			pitch += ((float) y - pitch) * random/2;
        			
        			event.yaw = yaw;
        			event.pitch = pitch;	
    			}else {
        			event.yaw = 180-(float)x;
        			event.pitch = (float) y;
    			}
        	}
        }
        
		super.onEvent(e);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
	}
}
