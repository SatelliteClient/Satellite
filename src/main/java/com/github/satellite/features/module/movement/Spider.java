package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.MovementUtils;
import org.lwjgl.input.Keyboard;

public class Spider extends Module {

    public Spider () {
        super("Spider", Keyboard.KEY_NONE, Category.MOVEMENT);
    }
    
    @Override
    public void init() {
		settings.add(new ModeSetting("Mode", "Vanilla", new String[] {"Vanilla", "Packet"}));
    	super.init();
    }

	@Override
	public void onEvent(Event<?> e) {
		switch(((ModeSetting)settings.get(1)).getMode()) {
		
		
		
		case "Vannila":
		{
	        if(e instanceof EventUpdate) {
	            if(e.isPre()) {
	            	mc.player.motionY=0.42;
	            }
	        }
	        break;
		}
		
		
		
		case "Packet":
		{
	        if(e instanceof EventUpdate) {
	            if(e.isPre()) {
	            	if(mc.player.onGround) ClientUtils.setTimer(1);
	            	double motionY=0.42;
	            	if(mc.player.collidedHorizontally) {
	            		for(; motionY>-0.15233517546680642;) {
	            			MovementUtils.vClip(motionY);
	            			MovementUtils.vClip2(0, false);
	                        motionY -= 0.08D;
	                        motionY *= 0.9800000190734863D;
	            		}
	            		mc.player.motionY=motionY;
	            	}
	            }
	        }
			if(mc.player.isInWater()) {
				mc.player.motionY=2;
			}
		}
		
		}
        super.onEvent(e);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}