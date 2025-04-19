package boatdeletebegone.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public abstract class ChunkMixin {

    @Final @Shadow private static Logger LOGGER;
    @Final @Shadow public int x;
    @Final @Shadow public int z;

    @Inject(
            method = "addEntity",
            at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V"),
            remap = false
    )
    public void additionallyCheckForVehicleChunkPos(Entity entity, CallbackInfo ci) {
        //Runs after check for "entity position in the chunk it was saved in" already failed

        if (!hasValidVehicleNBT(entity.getEntityData())) {
            int entityChunkX = MathHelper.floor(entity.posX / 16.0);
            int entityChunkZ = MathHelper.floor(entity.posZ / 16.0);

            //Vanilla behavior
            LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", entityChunkX, entityChunkZ, this.x, this.z, entity);
            entity.setDead();
        }
    }

    @Unique
    private boolean hasValidVehicleNBT(NBTTagCompound entityData) {
        if (entityData.hasKey("RiddenVehicle")) {
            NBTTagCompound vehiclePosNBT = entityData.getCompoundTag("RiddenVehicle");
            int vehicleChunkX = vehiclePosNBT.getInteger("vehicleChunkX");
            int vehicleChunkZ = vehiclePosNBT.getInteger("vehicleChunkZ");
            return vehicleChunkX == this.x && vehicleChunkZ == this.z;
        }
        return false;
    }

    @Redirect(
            method = "addEntity",
            at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V"),
            remap = false
    )
    public void noVanillaWarn(Logger instance, String s, Object o1, Object o2, Object o3, Object o4, Object o5)  {
        //No op
    }

    @Redirect(
            method = "addEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;setDead()V"
            )
    )
    public void noVanillaSetDead(Entity instance)  {
        //No op
    }
}