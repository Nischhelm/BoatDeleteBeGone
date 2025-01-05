package boatdeletebegone.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.points.AfterInvoke;

import java.util.List;

import static net.minecraft.world.chunk.storage.AnvilChunkLoader.readChunkEntity;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(AnvilChunkLoader.class)
public abstract class MixinAnvilChunkLoader implements IChunkLoader, IThreadedFileIO {

    @Shadow
    protected static Entity createEntityFromNBT(NBTTagCompound tagCompound, World world) {
        return null;
    }

    @Inject(
            method = "readChunkEntity",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/nbt/NBTTagCompound;getTagList(Ljava/lang/String;I)Lnet/minecraft/nbt/NBTTagList;",
                    shift = AFTER
                )
    )
    private static void injectVehicleNBT(NBTTagCompound tagCompound, World world, Chunk chunk, CallbackInfoReturnable<Entity> cir)  {
        NBTTagList nbttaglist = tagCompound.getTagList("Passengers", 10);
        NBTTagCompound vehicleTags = new NBTTagCompound();

        NBTTagList nbttaglistPos = tagCompound.getTagList("Pos", 6);
        double posX = nbttaglistPos.getDoubleAt(0);
        double posZ = nbttaglistPos.getDoubleAt(2);

        int chunkX = MathHelper.floor(posX/16);
        int chunkZ = MathHelper.floor(posZ/16);

        //We are NOT using the chunk values but the ones we get from the vehicle NBT so if those are wrong too, we propagate that
        //Should not make a difference but idk its safer
        vehicleTags.setInteger("vehicleChunkX",chunkX);
        vehicleTags.setInteger("vehicleChunkZ",chunkZ);

        for(int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound riderNBT = nbttaglist.getCompoundTagAt(i);
            if(riderNBT.hasKey("ForgeData")) {
                riderNBT.getCompoundTag("ForgeData").setTag("RiddenVehicle",vehicleTags);
            } else {
                NBTTagCompound riderCustomNBT = new NBTTagCompound();
                riderCustomNBT.setTag("RiddenVehicle",vehicleTags);
                riderNBT.setTag("ForgeData",riderCustomNBT);
            }
        }

    }

    @Inject(
            method = "readChunkEntity",
            at = @At(value = "RETURN"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void removeVehicleNBT(NBTTagCompound tagCompound, World world, Chunk chunk, CallbackInfoReturnable<Entity> cir, Entity entity) {

        if (tagCompound.hasKey("Passengers", 9)) {
            List<Entity> passengers = entity.getPassengers();

            for (Entity passenger : passengers) {
                passenger.getEntityData().removeTag("RiddenVehicle");
            }
        }
    }

}


