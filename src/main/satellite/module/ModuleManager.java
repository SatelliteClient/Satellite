package satellite.module;

import java.util.ArrayList;

import satellite.module.movement.*;

public class ModuleManager {

	public static ArrayList<Module> modules = new ArrayList<Module>();
	
	public static void registerModules() {
		modules.add(new Fly());
	}
}
