package com.github.satellite.command.impl;

import com.github.satellite.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

public class LookYaw extends Command {

    public LookYaw() {
        super("LookYaw", "", "look <x> <z>", "look", "lookyaw");
    }

    @Override
    public boolean onCommand(String[] args, String command) {
        if (args.length < 2) {
            mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(syntax));
            return true;
        } else {
            try {
                mc.player.rotationYaw = (float) (Math.atan2(-(Float.parseFloat(args[0])-mc.player.posX), Float.parseFloat(args[1])-mc.player.posZ) * 57.2958);
                return true;
            } catch (NumberFormatException e) {
                mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(args[0] + " is not number"));
            }
        }
        return false;
    }
}