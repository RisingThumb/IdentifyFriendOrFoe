package xyz.risingthumb.iff.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import xyz.risingthumb.iff.classes.GroupManager;

public class ClientProxy implements IProxy {
	
	//public static KeyBinding[] keyBindingsUsed;
	public static GroupManager groupManager;
	public static boolean singlePlayer = false;
	public static boolean hasScreenshot = false;
	
	public void preInit(FMLPreInitializationEvent event) {
	}
	
	public void init(FMLInitializationEvent event) {
		groupManager = new GroupManager();
		
	}
	
	public void postInit(FMLPostInitializationEvent event) {
	}

}
