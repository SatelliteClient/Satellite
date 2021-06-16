package com.github.satellite.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({EntityPlayerSP.class})
public interface AccessorEntityPlayerSP {
    @Accessor("serverSneakState")
    boolean getServerSneakState();

    @Accessor("serverSneakState")
    void setServerSneakState(boolean paramBoolean);
}
