package boatdeletebegone.core;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = boatdeletebegone.MODID, version = boatdeletebegone.VERSION, name = boatdeletebegone.NAME, acceptableRemoteVersions = "*")
public class boatdeletebegone
{
    public static final String MODID = "boatdeletebegone";
    public static final String VERSION = "1.0.0";
    public static final String NAME = "boatdeletebegone";

	@Instance(MODID)
	public static boatdeletebegone instance;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    }

}