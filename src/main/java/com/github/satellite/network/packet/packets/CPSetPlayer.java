package com.github.satellite.network.packet.packets;

import com.github.satellite.network.packet.Packet;

public class CPSetPlayer extends Packet {

    public int entityId, x, y, z;
    public String name;

    public CPSetPlayer(int entityId, int x, int y, int z, String name) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    @Override
    public void writePacket() {
        this.data  = "01";
        this.data += String.valueOf(entityId);
        this.data += ",";
        this.data += String.valueOf(x);
        this.data += ",";
        this.data += String.valueOf(y);
        this.data += ",";
        this.data += String.valueOf(z);
        this.data += ",";
        this.data += String.valueOf(name);
    }
}
