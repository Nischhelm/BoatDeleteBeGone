package boatdeletebegone.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilChunkLoader.class)
public abstract class AnvilChunkLoaderMixin {

    @Inject(
            method = "readChunkEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getTagList(Ljava/lang/String;I)Lnet/minecraft/nbt/NBTTagList;")
    )
    private static void insertVehicleNBTtoPassengers(NBTTagCompound vehicleNBT, World world, Chunk chunk, CallbackInfoReturnable<Entity> cir, @Local Entity entity)  {
        //Runs only for vehicles (entity with passenger). outside in, so first for lowest vehicle, last for highest vehicle

        int chunkX;
        int chunkZ;

        //If there is multiple things riding each other, give this vehicles passengers the chunk pos of this vehicle's own vehicle
        if(entity.getEntityData().hasKey("RiddenVehicle")){
            NBTTagCompound vehiclePosNBT = entity.getEntityData().getCompoundTag("RiddenVehicle");
            chunkX = vehiclePosNBT.getInteger("vehicleChunkX");
            chunkZ = vehiclePosNBT.getInteger("vehicleChunkZ");
        }
        //Otherwise if this is not riding anything else, use this vehicle's position to give to its passengers
        else {
            NBTTagList vehiclePosNBT = vehicleNBT.getTagList("Pos", 6);
            double posX = vehiclePosNBT.getDoubleAt(0);
            double posZ = vehiclePosNBT.getDoubleAt(2);

            chunkX = MathHelper.floor(posX/16);
            chunkZ = MathHelper.floor(posZ/16);
        }

        //Create TagCompound holding the chunk position of this vehicle or its own vehicle
        NBTTagCompound riddenVehicleNBT = new NBTTagCompound();
        riddenVehicleNBT.setInteger("vehicleChunkX",chunkX);
        riddenVehicleNBT.setInteger("vehicleChunkZ",chunkZ);

        //Go through all of this vehicle's passengers and set their customEntityData (ForgeData) to hold their vehicle's position as NBT
        NBTTagList passengerListNBT = vehicleNBT.getTagList("Passengers", 10);
        for(int i = 0; i < passengerListNBT.tagCount(); ++i) {
            NBTTagCompound riderNBT = passengerListNBT.getCompoundTagAt(i);
            if(riderNBT.hasKey("ForgeData")) {
                riderNBT.getCompoundTag("ForgeData").setTag("RiddenVehicle",riddenVehicleNBT);
            } else {
                NBTTagCompound riderNewForgeData = new NBTTagCompound();
                riderNewForgeData.setTag("RiddenVehicle",riddenVehicleNBT);
                riderNBT.setTag("ForgeData",riderNewForgeData);
            }
        }
    }

    @ModifyReturnValue(
            method = "readChunkEntity",
            at = @At("RETURN")
    )
    private static Entity removeVehicleNBT(Entity entity) {
        if(entity == null) return null;
        //Delete vehicle NBT at end of recursive function (inside out, so highest passenger first, lowest vehicle last)
        if(entity.getEntityData().hasKey("RiddenVehicle"))
            entity.getEntityData().removeTag("RiddenVehicle");
        //If we created the customEntityData for this entity by invoking getEntityData or by sending it its vehicle position, we delete the whole compound (will be recreated if smth else adds custom nbt)
        if(entity.getEntityData().isEmpty())
            ((EntityAccessor) entity).setCustomEntityData(null);
        return entity;
    }
}