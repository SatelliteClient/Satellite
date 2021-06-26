package com.github.satellite.utils;

import net.minecraft.network.play.server.SPacketTimeUpdate;

public class WorldUtils {

    public static TimeHelper tpsCounter = new TimeHelper();

    public static double tps;

    public static void onTime(SPacketTimeUpdate packet) {
        tps = Math.round(tpsCounter.getCurrentMS() - tpsCounter.getLastMS()) / 50;
        tpsCounter.reset();
    }

}
