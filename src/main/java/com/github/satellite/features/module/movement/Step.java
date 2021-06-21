package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventStep;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.MovementUtils;
import org.lwjgl.input.Keyboard;

public class Step extends Module {

    public Step () {
        super("Step", Keyboard.KEY_NUMPAD0, Category.MOVEMENT);
    }
    
    public int state;
    public boolean inTimer;

	@Override
	public void onEvent(Event<?> e) {
    	if(e instanceof EventUpdate) {
    		EventUpdate event = (EventUpdate)e;
    		if (event.isPre())
    			if (this.state > 0) {
    				this.state--;
 	  			} else if (this.inTimer) {
    				this.inTimer = false;
    				ClientUtils.setTimer(1);
 	  			}  
    	}
        if(e instanceof EventStep) {
        	EventStep event = (EventStep)e;
        	if(e.isPre() && !mc.player.movementInput.jump && mc.player.collidedVertically && !mc.player.isInWater() && !mc.player.isOnLadder()) {
        		if(!mc.player.movementInput.sneak)
        			event.setHeight(2.5F);
        	}
        	else if(e.isPost() && (mc.player.getEntityBoundingBox()).minY - mc.player.posY >= 0.625D) {
        		this.state = 1;
        		this.inTimer = true;
        		double[] poss = positionsNCP(event.getHeight());
        		ClientUtils.setTimer(Math.min(1.0F, 1.0F / (1+poss.length)));
        		for (double y : poss) {
        			MovementUtils.vClip2(y, false);
        		}
        	}
        }
        super.onEvent(e);
    }
    
    public double[] positionsNCP(double height) {
    	if (height <= 1.0D) {
    		double offset = height / 1.0D;
    		return new double[] { 0.42D * offset, 0.7532D * offset };
    	} 
    	if (height < 1.1D)
    		return new double[] { 0.42D, 0.7532D, 1.001D }; 
    	if (height < 1.2D)
    		return new double[] { 0.42D, 0.7532D, 1.001D, 1.166D }; 
    	if (height < 1.6D)
    		return new double[] { 0.42D, 0.7532D, 1.001D, 1.084D, 1.006D }; 
    	if (height < 2.1D)
    		return new double[] { 0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D }; 
    	if (height < 2.6D)
    		return new double[] { 0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D, 2.019D, 1.907D }; 
    	return new double[] { 0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D, 2.019D, 1.907D };
    }
    
    public void onEnabled() {
    	this.inTimer = false;
    }
      
    public void onDisabled() {
    	if (this.inTimer) {
    		this.inTimer = false;
    		ClientUtils.setTimer(1);
    	} 
    }
}