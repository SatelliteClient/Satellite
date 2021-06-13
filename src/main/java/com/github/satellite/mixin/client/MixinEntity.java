package com.github.satellite.mixin.client;

import com.github.satellite.Satellite;
import com.github.satellite.event.EventDirection;
import com.github.satellite.event.listeners.EventFlag;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow @Final static DataParameter<Byte> FLAGS;
    @Shadow int entityId;
    @Shadow EntityDataManager dataManager;

    @Inject(method = {"setFlag"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void setFlag(int flag, boolean set, CallbackInfo callbackInfo) {
        EventFlag e = new EventFlag(flag, set, entityId);
        e.setDirection(EventDirection.INCOMING);
        Satellite.onEvent(e);

        if (e.isCancelled()) {
            callbackInfo.cancel();
            byte b0 = ((Byte)this.dataManager.get(FLAGS)).byteValue();

            if (set)
            {
                this.dataManager.set(FLAGS, Byte.valueOf((byte)(b0 | 1 << flag)));
            }
            else
            {
                this.dataManager.set(FLAGS, Byte.valueOf((byte)(b0 & ~(1 << flag))));
            }
        }
    }

    @Inject(method = {"getFlag"}, at = {@At(value       = "HEAD")}, cancellable = true)
    public void getFlag(int flag, CallbackInfoReturnable<Boolean> cir) {
        EventFlag e = new EventFlag(flag, (((Byte)this.dataManager.get(FLAGS)).byteValue() & 1 << flag) != 0, entityId);
        e.setDirection(EventDirection.OUTGOING);
        Satellite.onEvent(e);

        cir.setReturnValue(e.set);
    }
}
