package com.github.satellite.command;

import com.github.satellite.command.impl.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {

    public List<Command> commands = new ArrayList<Command>();
    public static String prefix = ".";

    public void init() {
        commands.add(new Fov());
        commands.add(new LookYaw());
        commands.add(new Toggle());
        commands.add(new Yaw());
        commands.add(new Prefix());
    }

    public boolean handleCommand(String str) {
        if (str.startsWith(prefix)) {
            str = str.substring(1);

            String[] args = str.split(" ");
            if (args.length > 0) {
                String commandName = args[0];
                for (Command c : commands) {
                    if (c.aliases.contains(commandName)) {
                        if (c.onCommand(Arrays.copyOfRange(args, 1, args.length), str)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}