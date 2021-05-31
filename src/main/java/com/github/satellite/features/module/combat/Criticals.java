package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import org.lwjgl.input.Keyboard;

public class Criticals extends Module {
	
	public Criticals() {
		super("Criticals", Keyboard.KEY_NONE, Category.COMBAT);
	}

	ModeSetting mode;
	BooleanSetting checkGround;

    @Override
    public void init() {
    	this.mode = new ModeSetting("Mode", "Packet", new String[] {"Packet"});
		this.checkGround = new BooleanSetting("CheckGround", true);
    	addSetting(mode, checkGround);
    	super.init();
    }

	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventUpdate) {
	        setDisplayName("Criticals \u00A77" + ((ModeSetting)settings.get(1)).getMode());
		}

		switch(mode.getMode()) {
		
		case "Packet":
		{
			if(e instanceof EventPacket) {
				EventPacket event = (EventPacket)e;
				Packet<?> p = event.getPacket();
				if(event.isOutgoing()) {
					if(p instanceof CPacketUseEntity) {
						CPacketUseEntity packet = (CPacketUseEntity)p;
						if(packet.getAction() == CPacketUseEntity.Action.ATTACK && (mc.player.onGround || !checkGround.isEnable())) {
	                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + .1625, mc.player.posZ, false));
	                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
						}
					}
				}
			}
			break;
		}
		
		}
		super.onEvent(e);
	}
}
