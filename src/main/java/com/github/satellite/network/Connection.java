package com.github.satellite.network;

import com.github.satellite.Satellite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Connection extends Thread{

	PrintWriter pr;
	InputStreamReader in;
	BufferedReader bf;
	Socket conn;
	
	boolean isConnected;
	
	public CopyOnWriteArrayList<String> sendQueue;
	public CopyOnWriteArrayList<String> recieveQueue;
	
	public Connection(Socket conn) throws IOException {
		this.conn = conn;
		this.pr = new PrintWriter(conn.getOutputStream());
		this.in = new InputStreamReader(conn.getInputStream());
		this.bf = new BufferedReader(in);
		this.sendQueue = new CopyOnWriteArrayList<String>();
		this.recieveQueue = new CopyOnWriteArrayList<String>();
		this.isConnected = true;
	}
	
	public void run() {
		(new Thread()
		{
		    public void run()
		    {
				while(Connection.this.isConnected) {
					for(String str : Connection.this.sendQueue) {
		        		pr.println(str);
		        		pr.flush();
		        		Connection.this.sendQueue = new CopyOnWriteArrayList<String>();
					}
				}
		    }
		}).start();
		
		(new Thread()
		{
		    public void run()
		    {
				while(Connection.this.isConnected) {
	        		String str;
					try {
						str = bf.readLine();
						Satellite.SatelliteNet.netWorkManager.readPacket(str);
						Connection.this.recieveQueue.add(str);
					} catch (IOException e) {
						e.printStackTrace();
						Connection.this.isConnected = false;
						break;
					}
				}
		    }
		}).start();
	}
	
}
