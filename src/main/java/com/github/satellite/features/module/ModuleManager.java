package com.github.satellite.features.module;

import com.github.satellite.features.module.combat.*;
import com.github.satellite.features.module.exploit.*;
import com.github.satellite.features.module.misc.*;
import com.github.satellite.features.module.movement.*;
import com.github.satellite.features.module.player.*;
import com.github.satellite.features.module.render.*;
import com.github.satellite.features.module.world.*;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.KeyBindSetting;
import com.github.satellite.setting.ModeSetting;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager {

	public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>();

	public static void registerModules(){
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
		modules.add(new NoFall());
		modules.add(new AntiHunger());
		modules.add(new FreeCam());
		modules.add(new Scaffold());
		modules.add(new Speed());
		modules.add(new KillAura());
		modules.add(new Phase());
		modules.add(new StorageESP());
		modules.add(new Jesus());
		modules.add(new AutoSlime());
		modules.add(new Bot());
		modules.add(new Spider());
		modules.add(new AutoBuild());
		modules.add(new Surround());
		modules.add(new Criticals());
		modules.add(new PacketCanceller());
		modules.add(new Map());
		modules.add(new TPAura());
		modules.add(new EntityFly());
		modules.add(new AutoSkull());
		modules.add(new HoleKicker());
		modules.add(new ESP());
		modules.add(new ClickTP());
		modules.add(new AntiChunkBan());
		modules.add(new AntiCrystal());
		modules.add(new HighJump());
		modules.add(new Debug());
		modules.add(new Ground());
		modules.add(new Test());
		modules.add(new ElytraFly());
	}
	
	public static List<Module> getModulesbyCategory(Module.Category c) {
		List<Module> moduleList = new ArrayList<>();
		for(Module m : modules)
			if(m.getCategory() == c)
				moduleList.add(m);
		return moduleList;
	}

	public static Module getModulebyClass(Class<? extends Module> c) {
		return modules.stream().filter(m -> m.getClass() == c).findFirst().orElse(null);
	}

	public static Module getModulebyName(String str) {
		return modules.stream().filter(m -> m.getName() == str).findFirst().orElse(null);
	}

	public static void toggle(Class<? extends Module> c) {
		Module module = modules.stream().filter(m -> m.getClass() == c).findFirst().orElse(null);
		if(module != null)
			module.toggle();
	}

	public static void saveModuleSetting() {
		File directory = new File(Minecraft.getMinecraft().gameDir, "satellite");
		File setting = new File(directory, "setting");

		if(!directory.exists()){
			directory.mkdir();
		}
		if(!setting.exists()){
			setting.mkdir();
		}

		try{
			for (Module m : modules){
				File module = new File(setting, m.getName());
				if (!module.exists()) {
					module.createNewFile();
				}

				PrintWriter pw = new PrintWriter(module);

				final String[] str = {""};

				m.settings.forEach(s -> {
					if(s instanceof KeyBindSetting){
						str[0] += "0"+String.valueOf(((KeyBindSetting) s).getKeyCode());
					}
					if(s instanceof BooleanSetting){
						str[0] += ((BooleanSetting)s).isEnable()?"11":"10";
					}
					if(s instanceof ModeSetting){
						str[0] += "2"+ ((ModeSetting) s).index;
					}
					str[0] += "\n";
				});

				pw.print(str[0]);
				pw.close();
			}
		}catch (IOException e){

		}
	}

	public static void loadModuleSetting() {
		File directory = new File(Minecraft.getMinecraft().gameDir, "satellite");
		File setting = new File(directory, "setting");

		if (setting.isDirectory()){
			for (File m : setting.listFiles()){

			}
		}
	}

}
