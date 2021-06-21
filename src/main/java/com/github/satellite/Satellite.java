package com.github.satellite;

import com.github.satellite.command.CommandManager;
import com.github.satellite.event.listeners.EventChat;
import com.github.satellite.network.SatelliteNetClient;
import com.github.satellite.event.Event;
import com.github.satellite.features.module.ModuleManager;
import com.github.satellite.ui.HUD;
import com.github.satellite.ui.gui.clickGUI.GuiClickGUI;
import com.github.satellite.ui.theme.ThemeManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = Satellite.MOD_ID, name = Satellite.NAME, version = Satellite.VERSION)
public class Satellite
{
    public static final String MOD_ID = "satellite";
    public static final String NAME = "Satellite Utility";
    public static final String VERSION = "1.1.1";

	public static HUD hud = new HUD();
	public static SatelliteNetClient SatelliteNet;
	public static boolean isConnected;
	public static ThemeManager themeManager = new ThemeManager();
	public static CommandManager commandManager = new CommandManager();
	public static Minecraft mc = Minecraft.getMinecraft();

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		MinecraftForge.EVENT_BUS.register(this);
		try {
			SatelliteNet = new SatelliteNetClient();
			SatelliteNet.sendLoginPacket();
			isConnected=true;
		}catch(Exception e) {
			isConnected=false;
		}

		commandManager.init();
		ModuleManager.registerModules();
		ModuleManager.loadModuleSetting();
		GuiClickGUI.loadModules();
	}

	public static void onEvent(Event<?> e) {
		ModuleManager.modules.stream().forEach(m -> {
			if(m.isEnable()) m.onEvent(e);
		});
	}

	@SubscribeEvent
	public void keyEvent(InputEvent.KeyInputEvent e) {
		if (mc.currentScreen == null) {
			try {
				if (Keyboard.isCreated()) {
					if (Keyboard.getEventKeyState()) {
						int i = Keyboard.getEventKey();
						if (i != 0) {
							ModuleManager.modules.stream().forEach(m -> {
								if(m.getKeyCode() == i) m.toggle();
							});
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public void chatEvent(ClientChatEvent event) {
		String message = event.getMessage();

		if (commandManager.handleCommand(message)) {
			event.setCanceled(true);
		}

		if(message.startsWith(commandManager.prefix)) {
			event.setCanceled(true);
		}

		onEvent(new EventChat(message));
	}
}
