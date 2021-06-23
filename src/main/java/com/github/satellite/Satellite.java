package com.github.satellite;

import com.github.satellite.command.CommandManager;
import com.github.satellite.event.listeners.EventChat;
import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.features.module.ModuleManager;
import com.github.satellite.ui.HUD;
import com.github.satellite.ui.gui.clickGUI.GuiClickGUI;
import com.github.satellite.ui.theme.ThemeManager;
import com.github.satellite.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
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
    public static final String VERSION = "2.1.0";

	public static HUD hud = new HUD();
	public static ThemeManager themeManager = new ThemeManager();
	public static CommandManager commandManager = new CommandManager();
	public static Minecraft mc = Minecraft.getMinecraft();

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		MinecraftForge.EVENT_BUS.register(this);
		commandManager.init();
		ModuleManager.registerModules();
		ModuleManager.loadModuleSetting();
		GuiClickGUI.loadModules();
	}

	public static Event<?> onEvent(Event<?> e) {
		if (e instanceof EventPacket) {
			EventPacket event = (EventPacket)e;
			Packet p = event.getPacket();
			if (p instanceof SPacketTimeUpdate) {
				WorldUtils.onTime((SPacketTimeUpdate) p);
			}
		}
    	ModuleManager.onEvent(e);
		return e;
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
		}else {
			mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString("Command Not Found"));
		}

		if(message.startsWith(commandManager.prefix)) {
			event.setCanceled(true);
		}

		onEvent(new EventChat(message));
	}
}
