package com.github.satellite.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;

public final class RayTraceUtils {

    public RayTraceUtils() {}

    public RayTraceResult rayTraceTowards(Entity entity, Rotation rotation, double blockReachDistance) {
        //return rayTraceTowards(entity, rotation, blockReachDistance, false);
        return null;
    }

    /*public static RayTraceResult rayTraceTowards(Entity entity, Rotation rotation, double blockReachDistance, boolean wouldSneak) {
        Vec3d start;
        if (wouldSneak) {
            start = inferSneakingEyePosition(entity);
        } else {
            start = entity.getPositionEyes(1.0F);
        }
        Vec3d direction = RotationUtils.calcVec3dFromRotation(rotation);
        Vec3d end = start.addVector(
                direction.x * blockReachDistance,
                direction.y * blockReachDistance,
                direction.z * blockReachDistance
        );
        return entity.world.rayTraceBlocks(start, end, false, false, true);
    }

    public static Vec3d inferSneakingEyePosition(Entity entity) {
        return new Vec3d(entity.posX, entity.posY + IPlayerContext.eyeHeight(true), entity.posZ);
    }*/
}
