package com.github.satellite.features.module;

import com.github.satellite.event.Event;
import com.github.satellite.features.module.combat.*;
import com.github.satellite.features.module.exploit.CivBreak;
import com.github.satellite.features.module.misc.*;
import com.github.satellite.features.module.movement.*;
import com.github.satellite.features.module.player.*;
import com.github.satellite.features.module.render.*;
import com.github.satellite.features.module.render.Map;
import com.github.satellite.features.module.world.*;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.KeyBindSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.setting.NumberSetting;

import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
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
		modules.add(new PhaseFly());
		modules.add(new PhaseWalk());
		modules.add(new FreeLook());
		modules.add(new CevBreaker());
		modules.add(new CivBreak());
		modules.add(new Notification());
		modules.add(new HoleTP());
		modules.add(new PistonAura());
		modules.add(new PistonAura2());
		modules.add(new PistonAura3());
		modules.add(new ChatSuffix());
		modules.add(new Vclip());
		modules.add(new CrystalAura());
		modules.add(new HoleESP());
		modules.add(new AutoTotem());
		modules.add(new FakePlayer());;
		modules.add(new Burrow());
		modules.add(new Anchor());
		modules.add(new ForcePlace());
		modules.add(new Nuker());
	}

	public static class ModuleComparator implements Comparator<Module> {
		@Override
		public int compare(Module o1, Module o2) {
			if(o1.priority > o2.priority)
				return -1;
			if(o1.priority < o2.priority)
				return 1;
			return 0;
		}
	}

	public static void onEvent(Event<?> e) {
		Collections.sort(ModuleManager.modules, new ModuleComparator());

		ModuleManager.modules.stream().forEach(m -> {
			if(m.isEnable()) m.onEvent(e);
		});
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

				str[0] += m.isEnable()?"1":"0";
				str[0] += "\n";

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
					if(s instanceof NumberSetting){
						str[0] += "3"+ String.valueOf(((NumberSetting) s).value);
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
			for (Module m : modules) {
				File SettingFile = new File(setting, m.getName());
				try {
					FileReader filereader = new FileReader(SettingFile);
					int ch;
					String str = "";
					while((ch = filereader.read()) != -1){
						str += String.valueOf((char)ch);
					}
					int i = 0;
					for (String val : Arrays.asList(str.split("\n"))) {
						if(i == 0) {
							m.setEnable(val.equals("1")?true:false);
						}else {

							String dat = val.substring(1);
							if (val.startsWith("0")) {
								KeyBindSetting bind = (KeyBindSetting)m.settings.get(i-1);
								bind.keyCode = Integer.parseInt(dat);
							}
							if (val.startsWith("1")) {
								BooleanSetting bind = (BooleanSetting)m.settings.get(i-1);
								bind.setEnable(val.equals("1"));
							}
							if (val.startsWith("2")) {
								ModeSetting bind = (ModeSetting)m.settings.get(i-1);
								bind.index = Integer.parseInt(dat);
							}
							if (val.startsWith("3")) {
								NumberSetting bind = (NumberSetting)m.settings.get(i-1);
								bind.value = Double.parseDouble(dat);
							}
						}
						i++;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException | ClassCastException | StringIndexOutOfBoundsException e) {
					e.printStackTrace();
					SettingFile.delete();
				}
			}
		}
	}


}
