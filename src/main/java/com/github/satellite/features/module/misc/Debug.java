package com.github.satellite.features.module.misc;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

public class Debug extends Module {

    public Debug() {
        super("Debug", 0, Category.MISC);
    }

    ModeSetting mode;

    @Override
    public void init() {
        mode = new ModeSetting("Mode", "Riden", new String[] {"Riden"});
        addSetting(mode);
        super.init();
    }

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventPacket) {
            EventPacket event = (EventPacket)e;
            Packet p = event.getPacket();
            if(event.isIncoming()) {
                switch (mode.getMode()) {
                    case "Riden":
                        if(mc.player.getRidingEntity() != null) {
                            mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(mc.player.getRidingEntity().toString()));
                            mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(String.valueOf(mc.player.getRidingEntity().getEntityId())));
                        }
                        break;


                    default:
                        break;
                }
            }
        }
        super.onEvent(e);
    }
}
