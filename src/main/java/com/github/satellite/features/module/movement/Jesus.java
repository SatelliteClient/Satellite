package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.features.module.Module;
import com.github.satellite.utils.MovementUtils;

import net.minecraft.block.BlockLiquid;
import net.minecraft.util.math.BlockPos;

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
        if (e instanceof EventMotion) {
            EventMotion event = (EventMotion)e;
            if (mc.world.getBlockState(new BlockPos(mc.player.posX, ((int)mc.player.posY + .76637), mc.player.posZ)).getBlock() instanceof BlockLiquid) {
                mc.player.setPosition(mc.player.posX, ((int)mc.player.posY + .76637), mc.player.posZ);
                if (mc.player.ticksExisted%2==0) {
                    MovementUtils.Strafe(1);
                }
                event.y += mc.player.ticksExisted%2==0?.4:0;
                mc.player.motionY = 0;
            }

        }
        super.onEvent(e);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}