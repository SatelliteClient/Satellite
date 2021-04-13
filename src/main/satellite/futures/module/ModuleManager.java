package satellite.futures.module;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import satellite.Client;

public class ModuleManager {
	
	public static List<Module> getModulesbyCategory(Module.Category c) {
		List<Module> modules = new ArrayList<Module>();
		for(Module m :Client.modules)
			if(m.getCategory() == c)
				modules.add(m);
		return modules;
	}
	
	public static Module getModule(Class<? extends Module> c) {
		return Client.modules.stream().filter(m -> m.getClass() == c).findFirst().orElse(null);
	}
	
	public static void toggle(Class<? extends Module> c) {
		Module module = Client.modules.stream().filter(m -> m.getClass() == c).findFirst().orElse(null);
		if(module != null)
			module.toggle();
	}
}
