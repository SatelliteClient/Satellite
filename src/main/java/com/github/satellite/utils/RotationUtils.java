/*
* This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.satellite.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public final class RotationUtils {

    public static final double DEG_TO_RAD = Math.PI / 180.0;

    public static final double RAD_TO_DEG = 180.0 / Math.PI;

    private RotationUtils() {}

    public static Rotation calcRotationFromCoords(BlockPos orig, BlockPos dest) {
        return calcRotationFromVec3d(new Vec3d(orig), new Vec3d(dest));
    }

    public static Rotation wrapAnglesToRelative(Rotation current, Rotation target) {
        if (current.yawIsReallyClose(target)) {
            return new Rotation(current.getYaw(), target.getPitch());
        }
        return target.subtract(current).normalize().add(current);
    }

    public static Rotation calcRotationFromVec3d(Vec3d orig, Vec3d dest, Rotation current) {
        return wrapAnglesToRelative(current, calcRotationFromVec3d(orig, dest));
    }

    private static Rotation calcRotationFromVec3d(Vec3d orig, Vec3d dest) {
        double[] delta = {orig.x - dest.x, orig.y - dest.y, orig.z - dest.z};
        double yaw = MathHelper.atan2(delta[0], -delta[2]);
        double dist = Math.sqrt(delta[0] * delta[0] + delta[2] * delta[2]);
        double pitch = MathHelper.atan2(delta[1], dist);
        return new Rotation(
                (float) (yaw * RAD_TO_DEG),
                (float) (pitch * RAD_TO_DEG)
        );
    }

    public static Vec3d calcVec3dFromRotation(Rotation rotation) {
        float f = MathHelper.cos(-rotation.getYaw() * (float) DEG_TO_RAD - (float) Math.PI);
        float f1 = MathHelper.sin(-rotation.getYaw() * (float) DEG_TO_RAD - (float) Math.PI);
        float f2 = -MathHelper.cos(-rotation.getPitch() * (float) DEG_TO_RAD);
        float f3 = MathHelper.sin(-rotation.getPitch() * (float) DEG_TO_RAD);
        return new Vec3d((double) (f1 * f2), (double) f3, (double) (f * f2));
    }
    
    public static Optional<Rotation> reachableOffset(Entity entity, BlockPos pos, Vec3d offsetPos, double blockReachDistance, boolean wouldSneak) {
        /*Vec3d eyes = wouldSneak ? RayTraceUtils.inferSneakingEyePosition(entity) : entity.getPositionEyes(1.0F);
        Rotation rotation = calcRotationFromVec3d(eyes, offsetPos, new Rotation(entity.rotationYaw, entity.rotationPitch));
        RayTraceResult result = RayTraceUtils.rayTraceTowards(entity, rotation, blockReachDistance, wouldSneak);
        //System.out.println(result);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (result.getBlockPos().equals(pos)) {
                return Optional.of(rotation);
            }
            if (entity.world.getBlockState(pos).getBlock() instanceof BlockFire && result.getBlockPos().equals(pos.down())) {
                return Optional.of(rotation);
            }
        }
        return Optional.empty();*/
        return null;
    }
}
