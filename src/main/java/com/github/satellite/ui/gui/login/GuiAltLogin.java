package com.github.satellite.ui.gui.login;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

public final class GuiAltLogin extends GuiScreen {
    private PasswordField password;
    private final GuiScreen previousScreen;
    private AltLoginThread thread;
    private GuiTextField username;
    private GuiTextField multi;

    public GuiAltLogin(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1: {
                this.mc.displayGuiScreen(this.previousScreen);
                break;
            }
            case 0: {
            	if(!this.multi.getText().isEmpty())
            	{
            		try
            		{
            			String username=this.multi.getText().split(":")[0];
            			String password=this.multi.getText().split(":")[1];
            			
            			this.thread = new AltLoginThread(username, password, false);
            		}catch (Exception e) {
            			this.thread = new AltLoginThread("", "", false);
					}
            	}
            	else
            	{
            	    if(this.username.getText().isEmpty()&&!this.password.getText().isEmpty())
            	    	this.thread = new AltLoginThread(this.password.getText(), this.password.getText(), true);
            	    else
            	    	this.thread = new AltLoginThread(this.username.getText(), this.password.getText(), false);
            	}
                this.thread.start();
            }
        }
    }

    @Override
    public void drawScreen(int x2, int y2, float z2) {
    	this.buttonList.get(0).x=width/2-104;
    	this.buttonList.get(0).setWidth(100);
    	this.buttonList.get(1).x=width/2+4;
    	this.buttonList.get(1).setWidth(100);
    	
    	this.buttonList.get(0).y=height/2;
    	this.buttonList.get(1).y=height/2;
    	
        this.drawDefaultBackground();
        this.multi.drawTextBox();
        this.username.drawTextBox();
        this.password.drawTextBox();
        this.drawCenteredString(this.mc.fontRenderer, "Alt Login", width / 2, 20, -1);
        this.drawCenteredString(this.mc.fontRenderer, this.thread == null ? "Idle..." : this.thread.getStatus(), width / 2, 29, -1);
        if (this.multi.getText().isEmpty()) {
            this.drawString(this.mc.fontRenderer, "Username:Password", width / 2 - 96, 26, -7829368);
        }
        if (this.username.getText().isEmpty()) {
            this.drawString(this.mc.fontRenderer, "Username / E-Mail", width / 2 - 96, 66, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            this.drawString(this.mc.fontRenderer, "Password", width / 2 - 96, 106, -7829368);
        }
        super.drawScreen(x2, y2, z2);
    }

    @Override
    public void initGui() {
        int var3 = height / 4 + 24;
        this.buttonList.add(new GuiButton(0, width/2-104, var3*2+4, 100, 20, "Login"));
        this.buttonList.add(new GuiButton(1, width/2+4, var3*2+4, 100, 20, "Back"));
        this.multi = new GuiTextField(var3, this.mc.fontRenderer, width / 2 - 100, 20, 200, 20);
        this.username = new GuiTextField(var3, this.mc.fontRenderer, width / 2 - 100, 60, 200, 20);
        this.password = new PasswordField(this.mc.fontRenderer, width / 2 - 100, 100, 200, 20);
        this.multi.setFocused(true);
        
        this.multi.setMaxStringLength(128);
        this.username.setMaxStringLength(128);
        this.password.setMaxStringLength(128);
        
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t') {
            if (this.multi.isFocused()) {
            	this.multi.setFocused(false);
            	this.username.setFocused(true);
            }else if(this.username.isFocused()) {
            	this.username.setFocused(false);
            	this.password.setFocused(true);
            } else {
            	this.password.setFocused(false);
            	this.multi.setFocused(true);
            }
        }
        if (character == '\r') {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
        this.multi.textboxKeyTyped(character, key);
        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x2, int y2, int button) {
        try {
            super.mouseClicked(x2, y2, button);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.multi.mouseClicked(x2, y2, button);
        this.username.mouseClicked(x2, y2, button);
        this.password.mouseClicked(x2, y2, button);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.multi.updateCursorCounter();
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
    }
}

