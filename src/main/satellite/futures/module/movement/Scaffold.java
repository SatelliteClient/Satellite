package satellite.futures.module.movement;

import org.lwjgl.input.Keyboard;

import satellite.event.Event;
import satellite.event.listeners.EventUpdate;
import satellite.futures.module.Module;
import satellite.utils.PlayerUtil;

public class Scaffold extends Module {

    public Scaffold () {
        super("Scaffold", Keyboard.KEY_V, Category.MOVEMENT);
    }
   
    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {
            if(e.isPre()) {
            	
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