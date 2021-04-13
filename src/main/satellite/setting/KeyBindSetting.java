package satellite.setting;

public class KeyBindSetting extends Setting {

	public int keyCode;
	
	public KeyBindSetting(String name, int keyCode) {
		this.name = name;
		this.keyCode = keyCode;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}
	
}
