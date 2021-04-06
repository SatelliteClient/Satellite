package satellite.module;

public class Module {

	public Category category;
	public int keyCode;
	public String name;
	public boolean enable;
	
	public Module(String name, int keyCode, Category category) {
		this.name=name;
		this.keyCode=keyCode;
		this.category=category;
		init();
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void toggle() {
		enable = !enable;
		if(enable) {
			onEnable();
		}else {
			onDisable();
		}
	}
	
	public void init() {}
	public void onEnable() {}
	public void onDisable() {}
	public void onEvent() {}
	
	public enum Category {
		COMBAT("Combat"),
		MOVEMENT("Movement"),
		PLAYER("Player"),
		RENDER("Render"),
		EXPLOIT("Exploit"),
		WORLD("World");
		
		public String name;
		
		Category(String name) {
			this.name=name;
		}
	}
	
}
