package xyz.risingthumb.iff;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindings {
	public static KeyBinding screenshotUploadKey;
	public static KeyBinding groupKey;
	
	public void RegisterKeybinds(){
        screenshotUploadKey = new KeyBinding("Screenshot Uploader",Keyboard.KEY_F4, "IFF");
        ClientRegistry.registerKeyBinding(screenshotUploadKey);
        groupKey = new KeyBinding("Group screen",Keyboard.KEY_G, "IFF");
        ClientRegistry.registerKeyBinding(groupKey);
        
    }

}
