package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.network.play.client.CPacketVehicleMove;
import org.lwjgl.input.Keyboard;

public class EntityFly extends Module {

    public EntityFly() {
        super("EntityFly", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

	@Override
	public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
        	if(mc.player.getRidingEntity() != null) {
        		Entity riden = mc.player.getRidingEntity();
        		if(riden instanceof AbstractHorse) {
        			AbstractHorse entity = (AbstractHorse)riden;
        			entity.setHorseSaddled(true);
        		}
        		if(riden instanceof EntityBoat) {
        			riden.posY+=30;
        			for(int i=0; i<10; i++) {
        				mc.getConnection().sendPacket(new CPacketVehicleMove(riden));
        			}
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