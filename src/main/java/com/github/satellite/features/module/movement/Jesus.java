package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.features.module.Module;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.input.Keyboard;

public class Jesus extends Module {

    public Jesus() {
        super("Jesus", Keyboard.KEY_NONE, Category.MOVEMENT);
    }
   
    boolean lastWater = false;
    boolean lastInWater = false;
    double lastSpeed = 0;

	@Override
	public void onEvent(Event<?> e) {
		
        if(e instanceof EventMotion) {
        	mc.world.collidesWithAnyBlock(new AxisAlignedBB(0, 0, 0, 20, 70, 20));
        	if (mc.gameSettings.keyBindSneak.isKeyDown() || mc.player.fallDistance > 3.0f || !mc.player.isInWater() || mc.gameSettings.keyBindJump.isKeyDown()) 
                return;
            mc.player.motionY += 0.03999999910593033;
        }
        super.onEvent(e);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}