package com.github.satellite.mixin.client;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({EntityPlayer.class})
public interface AccessorEntityPlayer {

    @Accessor("speedInAir")
    float speedInAir();

    @Accessor("speedInAir")
    void speedInAir(float paramFloat);

}
