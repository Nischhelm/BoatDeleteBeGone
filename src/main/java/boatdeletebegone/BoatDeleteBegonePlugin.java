package boatdeletebegone;

import java.util.Map;
import fermiumbooter.FermiumRegistryAPI;
import org.spongepowered.asm.launch.MixinBootstrap;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class BoatDeleteBegonePlugin implements IFMLLoadingPlugin {

	public BoatDeleteBegonePlugin() {
		MixinBootstrap.init();
		FermiumRegistryAPI.enqueueMixin(false, "mixins.boatdeletebegone.json");
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[0];
	}
	
	@Override
	public String getModContainerClass()
	{
		return null;
	}
	
	@Override
	public String getSetupClass()
	{
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) { }
	
	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}