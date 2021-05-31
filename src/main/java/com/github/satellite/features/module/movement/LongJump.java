package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.PlayerUtils;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;

public class LongJump extends Module {

    public LongJump() {
        super("LongJump", Keyboard.KEY_NUMPAD0, Category.MOVEMENT);
    }

	ModeSetting mode;
	
    @Override
    public void init() {
		mode = new ModeSetting("Mode", "Vanilla", new String[] {"Vanilla", "NCPLow", "NCPHigh", "AAC"});
		settings.add(mode);
    	super.init();
    }
   
    int progress = 0;

	@Override
	public void onEvent(Event<?> e) {

		switch(mode.getMode()) {
		
		case "NCPLow":
		{
			if(e instanceof EventMotion && e.isPre()) {
	            if(mc.player.posY == mc.player.lastTickPosY && mc.player.collidedVertically && mc.player.onGround && PlayerUtils.isMoving()) {
                    PlayerUtils.Strafe(0.1D);
                    mc.player.setPosition(mc.player.lastTickPosX+mc.player.motionX, mc.player.lastTickPosY, mc.player.lastTickPosZ+mc.player.motionZ);
	            	if(progress>10) {
	                    PlayerUtils.Strafe(.29D);
	                    mc.player.jump();
	                    progress=0;
	            	}
	            	progress++;
	            }
	            if(mc.player.motionY == .33319999363422365 && PlayerUtils.isMoving()) {
	                if (mc.player.isPotionActive(Potion.getPotionById(1))) {
	                    PlayerUtils.Strafe(1.34D);
	                } else {
	                    PlayerUtils.Strafe(1.261D);
	                }
	            }
	            PlayerUtils.Strafe(PlayerUtils.getSpeed());
			}
			break;
		}
		case "NCPHigh":
		{
			if(e instanceof EventUpdate && e.isPre()) {
				double[] ncp = new double[] {0.425D, 0.396D, -0.122D, -0.1D, 0.423D, 0.35D, 0.28D, 0.217D, 0.15D, -0.08D};
				
				if(PlayerUtils.isMoving() || progress > 0) {
					if(mc.player.onGround || progress > 0) {
						if(progress==1) {
							PlayerUtils.Strafe(0.5D);
						}
						if(progress==4) {
							PlayerUtils.Strafe(0.29);
						}
						if(progress<ncp.length) {
							mc.player.motionY = ncp[progress];
						}else {
							progress=-1;
						}
						progress++;
					}
				}
				PlayerUtils.Strafe(PlayerUtils.getSpeed());
			}
			break;
		}
		case "AAC":
		{
			if(e instanceof EventMotion && e.isPre()) {
	        	if(mc.player.onGround) {
	        		mc.player.motionY=0.42;
	        	}
	        	mc.player.motionX *= 1.08;
	        	mc.player.motionZ *= 1.08;
	            mc.player.motionY += 0.05999D;
			}
			break;
		}
		
		}
        super.onEvent(e);
    }

    @Override
    public void onDisable() {
    	progress = 0;
    	if(mode.is("NCPLow")) {
    		progress=11;
    	}
        super.onDisable();
    }
}