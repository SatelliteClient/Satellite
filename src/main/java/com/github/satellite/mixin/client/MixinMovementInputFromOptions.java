package com.github.satellite.mixin.client;

import com.github.satellite.Satellite;
import com.github.satellite.event.EventType;
import com.github.satellite.event.listeners.EventPlayerInput;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions extends MovementInput {

    @Shadow @Final private GameSettings gameSettings;

    @Inject(method = "updatePlayerMoveState", at = @At(value = "HEAD"), cancellable = true)
    private void updatePlayerMoveState(CallbackInfo ci) {
        EventPlayerInput e = new EventPlayerInput(
                this.gameSettings.keyBindForward.isKeyDown(),
                this.gameSettings.keyBindBack.isKeyDown(),
                this.gameSettings.keyBindLeft.isKeyDown(),
                this.gameSettings.keyBindRight.isKeyDown(),
                this.gameSettings.keyBindJump.isKeyDown(),
                this.gameSettings.keyBindSneak.isKeyDown()
        );

        Satellite.onEvent(e);

        if (e.isModded()) {
            this.moveStrafe = 0.0F;
            this.moveForward = 0.0F;

            if (e.isForward())
            {
                ++this.moveForward;
                this.forwardKeyDown = true;
            }
            else
            {
                this.forwardKeyDown = false;
            }

            if (e.isBack())
            {
                --this.moveForward;
                this.backKeyDown = true;
            }
            else
            {
                this.backKeyDown = false;
            }

            if (e.isLeft())
            {
                ++this.moveStrafe;
                this.leftKeyDown = true;
            }
            else
            {
                this.leftKeyDown = false;
            }

            if (e.isRight())
            {
                --this.moveStrafe;
                this.rightKeyDown = true;
            }
            else
            {
                this.rightKeyDown = false;
            }

            this.jump = e.isJump();
            this.sneak = e.isSneak();

            if (this.sneak)
            {
                this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
                this.moveForward = (float)((double)this.moveForward * 0.3D);
            }

            ci.cancel();
        }
    }
}

