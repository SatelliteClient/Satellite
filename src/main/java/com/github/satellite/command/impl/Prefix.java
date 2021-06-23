package com.github.satellite.command.impl;

import com.github.satellite.Satellite;
import com.github.satellite.command.Command;
import com.github.satellite.command.CommandManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

public class Prefix extends Command {

    public Prefix() {
        super("Prefix", "", "prefix <String>", "prefix");
    }

    @Override
    public boolean onCommand(String[] args, String command) {
        if (args.length == 1) {
            Satellite.commandManager.prefix = args[0];
            return true;
        }
        return false;
    }
}