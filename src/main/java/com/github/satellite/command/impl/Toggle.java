package com.github.satellite.command.impl;


import com.github.satellite.Satellite;
import com.github.satellite.command.Command;
import com.github.satellite.features.module.Module;
import com.github.satellite.features.module.ModuleManager;

public class Toggle extends Command {

    public Toggle() {
        super("Toggle", "", "toggle <Module>", "toggle", "t");
    }

    @Override
    public boolean onCommand(String[] args, String command) {
        if (args.length == 1) {
            for (Module m : ModuleManager.modules) {
                if (m.getName().toLowerCase().equals(args[0].toLowerCase())) {
                    m.toggle();
                    return true;
                }
            }
        }
        return false;
    }
}
