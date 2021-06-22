package com.github.satellite.command.impl;

import com.github.satellite.command.Command;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

public class Yaw extends Command {

    public Yaw() {
        super("yaw", "", "yaw <value>", "yaw");
    }

    @Override
    public boolean onCommand(String[] args, String command) {
        if (args.length < 1) {
            mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString("yaw: " + mc.player.rotationYaw));
            return true;
        } else {
            try {
                mc.player.rotationYaw = Float.parseFloat(args[0]);
                return true;
            } catch (NumberFormatException e) {
                mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(args[0] + " is not number"));
            }
        }
        return false;
    }
}
