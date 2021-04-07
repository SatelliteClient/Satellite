package satellite;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import satellite.event.Event;
import satellite.event.listeners.EventKey;
import satellite.module.Module;
import satellite.module.movement.*;
import satellite.ui.HUD;

public class Client {
	
	public static String name = "Satellite", version = "1";
	public static ArrayList<Module> modules = new ArrayList<Module>();
	public static HUD hud = new HUD();
	
	public static void init() {
		modules.add(new PacketFly());
	}
	
	public static void keyPress(int key) {
		EventKey e = new EventKey(key);
		onEvent(e);
		
		for(Module m : modules) {
			if(m.getKeyCode() == key) {
				m.toggle();
			}
		}
	}
	
	public static void onEvent(Event e) {
		for(Module m : modules) {
			if(m.isEnable()) {
				m.onEvent(e);
			}
		}
	}
	
}
