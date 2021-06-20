package com.github.satellite.command.impl;

import com.github.satellite.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

public class Fov extends Command {

    public Fov() {
        super("fov", "", "fov <value>", "fov");
    }

    @Override
    public boolean onCommand(String[] args, String command) {
        if (args.length < 1) {
            Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString("fov: " + Minecraft.getMinecraft().gameSettings.fovSetting));
        } else {
            Minecraft.getMinecraft().gameSettings.fovSetting = Float.parseFloat(args[0]);
        }

        return false;
    }
}
