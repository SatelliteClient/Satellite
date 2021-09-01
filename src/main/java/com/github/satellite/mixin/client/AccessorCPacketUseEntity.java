package com.github.satellite.mixin.client;

import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketUseEntity.class)
public interface AccessorCPacketUseEntity {

    @Accessor("entityId")
    void setEntityId(int entityId);

    @Accessor("action")
    void setAction(CPacketUseEntity.Action action);

    @Accessor("hitVec")
    void setHitVec(Vec3d vec);

    @Accessor("hand")
    void setHand(EnumHand entityId);

}