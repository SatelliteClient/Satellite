package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.utils.PlayerUtils;
import org.lwjgl.input.Keyboard;

public class Speed extends Module {


	public Speed () {
        super("Speed", Keyboard.KEY_Z, Category.MOVEMENT);
    }
   
    double lastSpeed;
	boolean lastGround;
	boolean moving;
    boolean OnGround = false;
	int tickTimer;
	int progress;
	double moveSpeed;
	double lastDist;

	@Override
	public void onEvent(Event<?> e) {
    	if(e instanceof EventUpdate) {
    		/*EventUpdate event = (EventUpdate)e;
    	    if (PlayerUtil.isMoving()) {
    	        if (mc.player.onGround) {
    	          mc.player.jump();
    	          mc.player.speedInAir = 0.0223F;
    	        } 
    	        PlayerUtil.Strafe(PlayerUtil.getSpeed());
    	      } else {
    	        mc.player.motionX = 0.0D;
    	        mc.player.motionZ = 0.0D;
    	      } */
    	    if (PlayerUtils.isMoving()) {
    	        if (mc.player.onGround) {
    	          mc.player.jump();
    	          mc.player.motionX *= 1.05D;
    	          mc.player.motionZ *= 1.05D;
    	          //mc.player.speedInAir = 0.02F;
    	        } 
    	        mc.player.motionY -= mc.player.motionY < .33319999363422365D ? 9.9999E-4D*5 : 0;
    	        
	        	PlayerUtils.Strafe(PlayerUtils.getSpeed() + (mc.player.motionY == .33319999363422365D && PlayerUtils.getSpeed() > 0.3 ? 0.1 : 0));
    	      } else {
    	        mc.player.motionX = 0.0D;
    	        mc.player.motionZ = 0.0D;
    	      }
    	}
    	if(e instanceof EventPlayerInput) {
    		EventPlayerInput event = (EventPlayerInput)e;
    		event.setJump(false);
    	}
        super.onEvent(e);
    }
    
    @Override
    public void onEnable() {
    	lastSpeed  = PlayerUtils.getSpeed();
    	super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}