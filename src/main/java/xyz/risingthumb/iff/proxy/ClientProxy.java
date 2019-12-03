package xyz.risingthumb.iff.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import xyz.risingthumb.iff.classes.GroupManager;

public class ClientProxy implements IProxy {
	
	//public static KeyBinding[] keyBindingsUsed;
	public static GroupManager groupManager;
	public static boolean singlePlayer = false;
	
	public void preInit(FMLPreInitializationEvent event) {
	}
	
	public void init(FMLInitializationEvent event) {
		groupManager = new GroupManager();
		/*
		// We initialise keybindings used
		keyBindingsUsed = new KeyBinding[1];
		keyBindingsUsed[0] = new KeyBinding("key.hud.desc", Keyboard.KEY_G, "key.groups.category");
		
		for (int i = 0; i < keyBindingsUsed.length; i++) {
			ClientRegistry.registerKeyBinding(keyBindingsUsed[i]);
		}
		*/
		
	}
	
	public void postInit(FMLPostInitializationEvent event) {
	}

}
