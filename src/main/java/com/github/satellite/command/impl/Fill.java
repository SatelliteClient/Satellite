package com.github.satellite.command.impl;


import com.github.satellite.Satellite;
import com.github.satellite.command.Command;
import com.github.satellite.event.listeners.EventRecieveSateNet;
import com.github.satellite.features.module.Module;
import com.github.satellite.features.module.ModuleManager;
import com.github.satellite.network.packet.packets.SPFillBlocks;

public class Fill extends Command {

    public Fill() {
        super("Fill", "", "fill x z y y block", "fill");
    }

    @Override
    public boolean onCommand(String[] args, String command) {
        if (args.length == 5) {
            Satellite.onEvent(new EventRecieveSateNet(new SPFillBlocks(Integer.parseInt(args[0]), Integer.parseInt(args[2]), Integer.parseInt(args[1]), Integer.parseInt(args[0]), Integer.parseInt(args[3]), Integer.parseInt(args[1]), Integer.parseInt(args[4]))));
        }
        return false;
    }
}
