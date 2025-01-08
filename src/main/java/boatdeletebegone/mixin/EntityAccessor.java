package boatdeletebegone.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor(value = "customEntityData", remap = false)
    void setCustomEntityData(NBTTagCompound compound);
}