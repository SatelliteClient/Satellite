package com.github.satellite.mixin.client;

import com.github.satellite.ui.gui.login.GuiAltLogin;
import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {

    @Inject(method = "initGui", at = @At("RETURN"))
    private void GuiMainMenu(CallbackInfo callbackInfo) {
        this.buttonList.add(new GuiButton(500, this.width / 2 + 104, this.height / 4 + 48 + 24 * 2, 98, 20, "login"));
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void actionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == 500)
        {
            this.mc.displayGuiScreen(new GuiAltLogin(this));
        }
    }
}
