package com.github.satellite.network.packet.packets;

import com.github.satellite.network.packet.Packet;
import net.minecraft.util.math.BlockPos;

public class SPRefillPoint extends Packet {

    public int x, y, z;

    @Override
    public void readPacket(String str) {
        String[] pos = str.split(",");
        this.x = Integer.parseInt(pos[0]);
        this.y = Integer.parseInt(pos[1]);
        this.z = Integer.parseInt(pos[2]);
    }

    public BlockPos getBlockPos() {
        return new BlockPos(x, y, z);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
