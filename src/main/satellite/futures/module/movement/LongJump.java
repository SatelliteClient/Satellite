package satellite.futures.module.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import satellite.event.Event;
import satellite.event.listeners.EventPlayerInput;
import satellite.event.listeners.EventUpdate;
import satellite.futures.module.Module;
import satellite.utils.PlayerUtil;

public class LongJump extends Module {

    public LongJump() {
        super("LongJump", Keyboard.KEY_NUMPAD0, Category.MOVEMENT);
    }
   
    int progress = 0;
    
    @Override
    public void onEvent(Event e) {
    	PlayerUtil Player = new PlayerUtil(mc.player);
		
        if(e instanceof EventUpdate) {
        	if(mc.player.isCollidedVertically) {
        		Player.Strafe(0.29);
        		mc.player.jump();
        	}
        	if(mc.player.motionY == .33319999363422365) {

        		mc.player.motionY+=0.03;
        		
        		if(mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
        			Player.Strafe(1.34);
        		}else {
        			Player.Strafe(1.4);
        		}
        	}
    		
        	Player.Strafe(Player.getSpeed());
        }
        super.onEvent(e);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}