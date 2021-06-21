package com.github.satellite.features.module.misc;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.MovementUtils;
import net.minecraft.network.Packet;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

public class Debug extends Module {

    public Debug() {
        super("Debug", 0, Category.MISC);
    }

    ModeSetting mode;

    @Override
    public void init() {
        mode = new ModeSetting("Mode", "Riden", new String[] {"Riden", "Speed"});
        addSetting(mode);
        super.init();
    }

    @Override
    public void onEvent(Event<?> e) {
        switch (mode.getMode()) {
            case "Riden":
                if (e instanceof EventPacket) {
                    EventPacket event = (EventPacket)e;
                    Packet p = event.getPacket();
                    if(event.isIncoming()) {
                        if(mc.player.getRidingEntity() != null) {
                            mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(mc.player.getRidingEntity().toString()));
                            mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(String.valueOf(mc.player.getRidingEntity().getEntityId())));
                        }
                    }
                }
                break;
            case "Speed":
                if (e instanceof EventUpdate) {
                    mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(String.valueOf(MovementUtils.getSpeed())));
                }
                break;

            default:
                break;
        }
        super.onEvent(e);
    }
}
