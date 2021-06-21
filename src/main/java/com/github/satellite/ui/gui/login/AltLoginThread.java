package com.github.satellite.ui.gui.login;

import java.net.Proxy;


import com.github.satellite.mixin.client.AccessorMinecraft;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public final class AltLoginThread
extends Thread {
    private final String username;
    private final String password;
    private final boolean isThealtening;
    private String status;
    private Minecraft mc = Minecraft.getMinecraft();

    public AltLoginThread(String username, String password, boolean isThealtening) {
        super("Alt Login Thread");
        this.username = username;
        this.password = password;
        this.isThealtening = isThealtening;
        this.status = "Waiting...";
    }

    private Session createSession(String username, String password) {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(username);
        auth.setPassword(password);
        try {
            auth.logIn();
            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
        }
        catch (AuthenticationException localAuthenticationException) {
            localAuthenticationException.printStackTrace();
            return null;
        }
    }

    public String getStatus() {
        return this.status;
    }

    @Override
    public void run() {
        if (this.password.equals("")) {
            ((AccessorMinecraft)this.mc).setSession(new Session(this.username, "", "", "mojang"));
            this.status = "Logged in. (" + this.username + " - offline name)";
            return;
        }
        this.status = "Logging in...";
    	Session auth = this.createSession(this.username, this.password);
    	
        if (auth == null) {
            this.status = "Login failed!";
        } else {
            this.status = "Logged in. (" + auth.getUsername() + ")";
            ((AccessorMinecraft)this.mc).setSession(auth);
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

