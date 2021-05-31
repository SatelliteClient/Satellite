package com.github.satellite.mixin.client;

import com.github.satellite.Satellite;
import com.github.satellite.event.EventDirection;
import com.github.satellite.event.listeners.EventPacket;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.SocketAddress;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {
    @Shadow private SocketAddress socketAddress;

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void SendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        EventPacket e = new EventPacket(packet);
        e.setDirection(EventDirection.OUTGOING);
        Satellite.onEvent(e);

        if (e.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void ChannelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
        EventPacket e = new EventPacket(packet);
        e.setDirection(EventDirection.INCOMING);
        Satellite.onEvent(e);

        if (e.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}
