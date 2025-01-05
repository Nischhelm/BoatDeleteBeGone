package boatdeletebegone.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public abstract class MixinChunk implements ICapabilityProvider {

    @Final @Shadow private static Logger LOGGER;
    @Final @Shadow public int x;
    @Final @Shadow public int z;

    @Inject(
            method = "addEntity",
            at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V")
    )
    public void addEntityInject(Entity entity, CallbackInfo ci) {
        int i = MathHelper.floor(entity.posX / 16.0);
        int j = MathHelper.floor(entity.posZ / 16.0);
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        
        //The following line is the only thing that is changed by this mod
        if (Math.abs(entity.posX - (this.x * 16+8)) > 8.6 || Math.abs(entity.posZ - (this.z * 16+8)) > 8.6) {
        
            LOGGER.warn("xxx Wrong location! ({}, {}) should be ({}, {}), {}", i, j, this.x, this.z, entity);
=======
=======
>>>>>>> Stashed changes

        NBTTagCompound entityData = entity.getEntityData();
        boolean hasValidVehicle = false;
        if (entityData.hasKey("RiddenVehicle")) {
            NBTTagCompound vehicle = entityData.getCompoundTag("RiddenVehicle");
            if (!vehicle.isEmpty() && vehicle.hasKey("vehicleChunkX") && vehicle.hasKey("vehicleChunkZ")) {
                int vehicleChunkX = vehicle.getInteger("vehicleChunkX");
                int vehicleChunkZ = vehicle.getInteger("vehicleChunkZ");
                hasValidVehicle = vehicleChunkX == this.x && vehicleChunkZ == this.z;
            }
        }
        if(!hasValidVehicle){
            LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", i, j, this.x, this.z, entity);
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
            entity.setDead();
        }
    }

    @Redirect(
            method = "addEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V"
            )
    )
    public void loggerWarnRedirect(Logger instance, String s, Object o1, Object o2, Object o3, Object o4, Object o5)  {
        //Nothing
    }

    @Redirect(
            method = "addEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;setDead()V"
            )
    )
    public void entitySetDeadRedirect(Entity instance)  {
        //Nothing
    }
}


