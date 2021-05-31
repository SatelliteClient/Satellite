package com.github.satellite.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Minecraft.class})
public interface AccessorMinecraft {
    @Accessor("timer")
    Timer getTimer();

    @Accessor("rightClickDelayTimer")
    int getRightClickDelayTimer();

    @Accessor("rightClickDelayTimer")
    void setRightClickDelayTimer(int paramInt);

    @Accessor("session")
    void setSession(Session paramSession);

    @Accessor("renderPartialTicksPaused")
    float getRenderPartialTicksPaused();

    @Accessor("leftClickCounter")
    void setLeftClickCounter(int paramInt);
}
