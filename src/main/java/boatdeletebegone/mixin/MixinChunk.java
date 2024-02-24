package boatdeletebegone.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.EntityEvent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;

@Mixin(Chunk.class)
public abstract class MixinChunk implements ICapabilityProvider {
    
    @Shadow
    private boolean hasEntities;
    @Final
    @Shadow
    public int x;
    @Final
    @Shadow
    public int z;

    @Final
    @Shadow
    private static Logger LOGGER;

    @Final
    @Shadow
    private ClassInheritanceMultiMap<Entity>[] entityLists;

    @Shadow
    public abstract void markDirty();

    /**
     * @author Nischhelm
     * @reason Stopping mobs from being deleted in carts+boats on chunk borders
     */
    @Overwrite
    public void addEntity(Entity entity)  {
        this.hasEntities = true;
        int i = MathHelper.floor(entity.posX / 16.0);
        int j = MathHelper.floor(entity.posZ / 16.0);
        
        //The following line is the only thing that is changed by this mod
        if (Math.abs(entity.posX - (this.x * 16+8)) > 8.6 || Math.abs(entity.posZ - (this.z * 16+8)) > 8.6) {
        
            LOGGER.warn("xxx Wrong location! ({}, {}) should be ({}, {}), {}", i, j, this.x, this.z, entity);
            entity.setDead();
        }

        int k = MathHelper.floor(entity.posY / 16.0);
        if (k < 0) {
            k = 0;
        }

        if (k >= this.entityLists.length) {
            k = this.entityLists.length - 1;
        }

        MinecraftForge.EVENT_BUS.post(new EntityEvent.EnteringChunk(entity, this.x, this.z, entity.chunkCoordX, entity.chunkCoordZ));
        entity.addedToChunk = true;
        entity.chunkCoordX = this.x;
        entity.chunkCoordY = k;
        entity.chunkCoordZ = this.z;
        this.entityLists[k].add(entity);
        this.markDirty();
    }

}


