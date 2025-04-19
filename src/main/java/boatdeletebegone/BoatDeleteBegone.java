package boatdeletebegone;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = BoatDeleteBegone.MODID,
        version = BoatDeleteBegone.VERSION,
        name = BoatDeleteBegone.NAME,
        dependencies = "required-after:fermiumbooter",
        acceptableRemoteVersions = "*"
)
public class BoatDeleteBegone {
    public static final String MODID = "boatdeletebegone";
    public static final String VERSION = "1.1.2";
    public static final String NAME = "BoatDeleteBegone";
    public static final Logger LOGGER = LogManager.getLogger();
}