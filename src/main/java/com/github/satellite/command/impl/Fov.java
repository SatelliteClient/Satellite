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
            mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString("fov: " + mc.gameSettings.fovSetting));
            return true;
        } else {
            try {
                mc.gameSettings.fovSetting = Float.parseFloat(args[0]);
                return true;
            } catch (NumberFormatException e) {
                mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(args[0] + " is not number"));
            }
        }
        return false;
    }
}
