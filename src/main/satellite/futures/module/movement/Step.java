package satellite.futures.module.movement;

import org.lwjgl.input.Keyboard;

import satellite.event.Event;
import satellite.event.listeners.EventUpdate;
import satellite.futures.module.Module;
import satellite.utils.PlayerUtil;

public class Step extends Module {

    public Step () {
        super("Step", Keyboard.KEY_NUMPAD0, Category.MOVEMENT);
    }
   
    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {
            if(e.isPre()) {
            	if(!!true)
            		return;
                
                mc.player.stepHeight = 2.9F;
                    
    	        PlayerUtil util	 = new PlayerUtil(mc.player);
    	        if(mc.player.isCollidedHorizontally == true) {
    		        util.vClip(2.9);
                    
                }
            }
        }
        super.onEvent(e);
    }

    @Override
    public void onDisable() {
    	mc.player.stepHeight = 0.5F;
        super.onDisable();
    }
}