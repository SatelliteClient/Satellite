package com.github.satellite.mixin.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({CPacketChatMessage.class})
public interface AccessorCPacketChatMessage {

    @Accessor("message")
    void message(String paramString);

}
