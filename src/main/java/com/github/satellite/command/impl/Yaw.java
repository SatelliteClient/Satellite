package com.github.satellite.command.impl;

import com.github.satellite.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

public class Yaw extends Command {

    public Yaw() {
        super("yaw", "", "yaw <value>", "yaw");
    }

    @Override
    public boolean onCommand(String[] argsa, String command) {
        if (args.length < 1) {
            mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString("yaw: " + mc.player.cameraYaw));
        } else {
            try {
                mc.player.cameraYaw = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(args[0] + " is not number"));
            }
        }
    }

}