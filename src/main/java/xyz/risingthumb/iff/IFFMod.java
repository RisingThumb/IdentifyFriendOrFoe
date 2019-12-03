package xyz.risingthumb.iff;

import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import xyz.risingthumb.iff.classes.Group;
import xyz.risingthumb.iff.classes.GroupPerson;
import xyz.risingthumb.iff.proxy.ClientProxy;
import xyz.risingthumb.iff.proxy.IProxy;

@Mod(modid=IFFMod.MODID, name=IFFMod.NAME, version= IFFMod.VERSION, acceptedMinecraftVersions=IFFMod.MC_VERSION)
public class IFFMod {
	public static final String MODID="iff";
	public static final String NAME="IFF";
	public static final String VERSION="0.1";
	public static final String MC_VERSION="[1.12.2]";
	
	private static Configuration CONFIG;
	
	public static final String CATEGORY_NAME_GROUPS = "groups";
	public static final String CATEGORY_GROUPS_SIZES = "groups_sizes";
	public static final String CATEGORY_NAME_PLAYERS = "players";

	
	public static final Logger LOGGER = LogManager.getLogger(IFFMod.MODID);
	
	public static final String CLIENT="xyz.risingthumb.iff.proxy.ClientProxy";
	public static final String SERVER="xyz.risingthumb.iff.proxy.ServerProxy";
	
	@SidedProxy(clientSide=IFFMod.CLIENT, serverSide=IFFMod.SERVER)
	public static IProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
		CONFIG = new Configuration(event.getSuggestedConfigurationFile());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		loadConfig();
	}
	
	public static Configuration getConfig() {
		return CONFIG;
	}
	
	public static void loadConfig() {
		CONFIG.load();
		// Loading markers
		Property groups = CONFIG.get(CATEGORY_NAME_GROUPS,
				"groups", new String[] {
						"Default"
				});
		String[] groupNames = groups.getStringList();
		for(String s: groupNames) {
			ClientProxy.groupManager.addGroup(new Group(s));
		}
		
		Property groupsSizes = CONFIG.get(CATEGORY_GROUPS_SIZES,
				"groupsSizes", new int[] {
						1
				});
		Property players = CONFIG.get(CATEGORY_NAME_PLAYERS,
				"players", new String[] {
						"RisingThumb"
				});
		List<Group> groupsActual = ClientProxy.groupManager.getGroups();
		int[] groupSizes = groupsSizes.getIntList();
		String[] playerNames = players.getStringList();
		int count = 0;
		
		for(int i=0; i<groupSizes.length; i++) {
			for(int j=0; j<groupSizes[i]; j++) {
				groupsActual.get(i).addPerson(new GroupPerson(playerNames[count]));
				count+=1;
			}
		}
		
		if (CONFIG.hasChanged()) {
			CONFIG.save();
		}
	}
	
	public static void saveConfig() {
		List<Group> groupsActual = ClientProxy.groupManager.getGroups();
		int[] groupSizes = new int[groupsActual.size()];
		String[] groupNames = new String[groupsActual.size()];
		
		int countNames = 0;
		
		for(int i=0; i<groupsActual.size(); i++) {
			groupNames[i] = groupsActual.get(i).getName();
			groupSizes[i] = groupsActual.get(i).size();
			countNames += groupsActual.get(i).size();
		}
		
		String[] playerNames = new String[countNames];
		int count = 0;
		
		for(int i=0; i<groupsActual.size(); i++) {
			List<GroupPerson> tempPersons = groupsActual.get(i).getPersons();
			for (int j=0; j<tempPersons.size(); j++) {
				playerNames[count] = tempPersons.get(j).getName();
				count+=1;
			}
		}
		
		Property groupsSizes = CONFIG.get(CATEGORY_GROUPS_SIZES,
				"groupsSizes", new int[] {
						1
				});
		Property players = CONFIG.get(CATEGORY_NAME_PLAYERS,
				"players", new String[] {
						"RisingThumb"
				});
		Property groups = CONFIG.get(CATEGORY_NAME_GROUPS,
				"groups", new String[] {
						"Default"
				});
		
		groups.set(groupNames);
		groupsSizes.set(groupSizes);
		players.set(playerNames);
		
		
		if (CONFIG.hasChanged()) {
			CONFIG.save();
		}
	}

}
