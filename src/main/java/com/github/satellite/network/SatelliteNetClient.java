package com.github.satellite.network;

import com.github.satellite.network.packet.NetWorkManager;
import com.github.satellite.network.packet.packets.CPSetPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SatelliteNetClient {
	
	public Connection conn;
	public NetWorkManager netWorkManager;
	
	public SatelliteNetClient() throws UnknownHostException, IOException {
		Socket s = new Socket("localhost", 8080);
		this.netWorkManager = new NetWorkManager();
		this.netWorkManager.registerPackets();
		
		this.conn = new Connection(s);
		this.conn.start();
	}

	public void sendLoginPacket() {
		//this.netWorkManager.sendPacket(new CP);
	}
	public void sendPlayerInfo(EntityPlayerSP player) {
		this.netWorkManager.sendPacket(new CPSetPlayer(player.getEntityId(), (int)player.posX, (int)player.posY, (int)player.posZ, player.getName()));
	}
}