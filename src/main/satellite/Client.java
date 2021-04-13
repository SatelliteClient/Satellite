package satellite;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import satellite.event.Event;
import satellite.event.listeners.EventKey;
import satellite.futures.module.Module;
import satellite.futures.module.combat.TargetStrafe;
import satellite.futures.module.combat.*;
import satellite.futures.module.movement.*;
import satellite.futures.module.player.*;
import satellite.futures.module.render.*;
import satellite.ui.HUD;

public class Client {
	
	public static String name = "Satellite", version = "1";
	public static ArrayList<Module> modules = new ArrayList<Module>();
	public static HUD hud = new HUD();
	
	public static void init() {
		modules.add(new ClickGUI());
		modules.add(new Fly());
		modules.add(new Step());
		modules.add(new Yaw());
		modules.add(new PacketFly());
		modules.add(new Fullbright());
		modules.add(new Velocity());
		modules.add(new InvMove());
		modules.add(new Tracers());
		modules.add(new LongJump());
		modules.add(new TargetStrafe());
		modules.add(new NoFall());
		modules.add(new AntiHunger());
		modules.add(new FreeCam());
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
