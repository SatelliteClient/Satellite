package com.github.satellite.features.module.misc;

import com.github.satellite.mixin.client.AccessorCPacketChatMessage;
import org.lwjgl.input.Keyboard;

import com.github.satellite.Satellite;
import com.github.satellite.command.CommandManager;
import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;

public class ChatSuffix extends Module{
	
	public ChatSuffix() {
		super("ChatSuffix", Keyboard.KEY_NONE, Category.MISC);
	}
	
	ModeSetting Sep;
	
	@Override
	public void init() {
		this.Sep = new ModeSetting("Separator", ">>", ">>", "<<", "|");
		addSetting(Sep);
		super.init();
	}
	
	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventPacket) {
			EventPacket event = (EventPacket)e;
			Packet<?> p = event.getPacket();
			if(event.isOutgoing()) {
				if (p instanceof CPacketChatMessage) {
					CPacketChatMessage packet = (CPacketChatMessage)p;
					String message = ((CPacketChatMessage) p).getMessage();
					if (message.startsWith("/") || message.startsWith(CommandManager.prefix)) {
						return;
					}
					String Sep2 = null;
					
					switch (Sep.getMode()) {
					case "<<":
						Sep2 = " \u300a";
						break;
					case ">>":
						Sep2 = " \u300b";
						break;
					case "|":
						Sep2 = " \u23D0 ";
						break;
					}
					
					String send = message + Sep2 + "\ua731\u1d00\u1d1b\u1d07\u029f\u029f\u026a\u1d1b\u1d07";
					
					if(send.length() > 255) return;
					((AccessorCPacketChatMessage) p).message(send);
				}
			}
		}
		super.onEvent(e);
	}

}
