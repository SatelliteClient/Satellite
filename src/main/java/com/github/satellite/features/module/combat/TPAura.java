package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.MathUtils;
import com.github.satellite.utils.MovementUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Random;

public class TPAura extends Module {
	
	public TPAura() {
		super("TPAura", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
	ArrayList<Entity> targets;
	
	float yaw = 0, pitch = 0;

	@Override
	public void onEvent(Event<?> e) {

        if(e instanceof EventUpdate) {
            if(e.isPre()) {
            	
            	targets = new ArrayList<Entity>();
            	
            	for(Entity entity : mc.world.loadedEntityList) {
            		
            		if(!entity.isEntityAlive())
            			continue;
            		
            		if(entity instanceof EntityLivingBase && entity != mc.player && MathUtils.getDistanceSq(new double[] {entity.posX - mc.player.posX, entity.posZ - mc.player.posZ}) < 10) {
            			targets.add(entity);
            		}
            	}
            	
            	if(!targets.isEmpty()) {
            		targets.sort((a, b) -> (int)(a.getDistance(mc.player) - b.getDistance(mc.player)));
            		
            		Entity target = targets.get(0);

    				double lastX = mc.player.posX;
    				double lastY = mc.player.posY;
    				double lastZ = mc.player.posZ;

					if(mc.player.getCooledAttackStrength(0.0f) > 0.9f) {
						tpGoBrrrrr(target.posX, target.posY, target.posZ);
						
						for(int i=0; i<1; i++) {
		            		mc.playerController.attackEntity(mc.player, target);
		            		mc.player.swingArm(EnumHand.MAIN_HAND);
		            		//target.setDead(true);
						}
	            		
	    				tpGoBrrrrr(lastX, lastY, lastZ);
					}
					
					if(!Keyboard.isKeyDown(48)) {
						double r = Math.toRadians(mc.player.ticksExisted) * 6.19208751;//8.19208751
						double dist = Math.sqrt(Math.pow(target.posX - mc.player.posX, 2) + Math.pow(target.posZ - mc.player.posZ, 2));
						double dx = (target.posX+(dist>8?0:Math.sin(r)*8)) - mc.player.posX;
						double dz = (target.posZ+(dist>8?0:Math.cos(r)*8)) - mc.player.posZ;
						double dy = target.posY - mc.player.posY;
						double speed = 1;
						mc.player.motionX = Math.max(Math.min(dx, speed), -speed);
						mc.player.motionZ = Math.max(Math.min(dz, speed), -speed);
						ClientUtils.setTimer(1.2F);
						mc.player.motionY = MovementUtils.InputY()+Math.max(Math.min(dy, 1), -1);
					}
            	}
            }
        }
        
        if(e instanceof EventMotion) {
        	EventMotion event = (EventMotion)e;
    		
        	if(!targets.isEmpty()) {
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
	
	public void tpGoBrrrrr(double x, double y, double z) {
		EntityPlayerSP player = mc.player;
		double dist = 5;
		for(int i=0; i<20&&dist>1; i++)
		{
			double dx=x-player.posX;
			double dy=y-player.posY;
			double dz=z-player.posZ;
			
			double hdist = Math.sqrt(dx*dx+dz*dz);
			
			double rx=Math.atan2(dx, dz);
			double ry=Math.atan2(dy, hdist);
	
			dist = Math.sqrt(dx*dx+dy*dy+dz*dz);
			double o=dist>1?1:dist;
			Vec3d vec = new Vec3d(Math.sin(rx)*Math.cos(ry)*o, o*Math.sin(ry*1), Math.cos(rx)*Math.cos(ry)*o);
			mc.player.move(MoverType.SELF, vec.x, vec.y, vec.z);
			
			MovementUtils.vClip2(0, true);
		}
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
	}
}
