package xyz.risingthumb.iff;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.risingthumb.iff.groups.Group;
import xyz.risingthumb.iff.groups.GroupPerson;
import xyz.risingthumb.iff.gui.GuiGroups;
import xyz.risingthumb.iff.proxy.ClientProxy;
import xyz.risingthumb.iff.scheduling.Scheduler;

@EventBusSubscriber(modid=IFFMod.MODID)
public class EventHandler {
	
	private static int tickCount = 1;
	
	public static ArrayList<Scheduler> schedulerList = new ArrayList<>();
	public static ArrayList<Scheduler> cleanUpSchedule = new ArrayList<>();
	
	public static void fixTabsForAllPlayers() {
		for(Group g: ClientProxy.groupManager.getGroups()) {
			for(GroupPerson gp: g.getPersons()) {
				// getPlayerInfo() returns null for singleplayer worlds
				NetworkPlayerInfo npi = Minecraft.getMinecraft().getConnection().getPlayerInfo(gp.getName());

				//npi.setDisplayName(new TextComponentString(TextFormatting.AQUA+g.getName()+" "+username));
				if(npi != null) {
					npi.setDisplayName(new TextComponentString(g.getName()+" "+gp.getName()));
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onWorldTick(final TickEvent.WorldTickEvent event) {
		tickCount++;
		if(tickCount % 20 == 0) {
			tickCount = 0;

			// Fix crash when loading into a singleplayer world
			if (event.world.isRemote) {
				fixTabsForAllPlayers();
			}
		}
		
	}

	// Not really necessary - worlds already have a variable to check if they're remote (multiplayer) or local (singleplayer)
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled = true)
	public static void onConnect(ClientConnectedToServerEvent event) {
		ClientProxy.singlePlayer = event.isLocal();
	}
	
	
	/*
	public static void onEvent(EntityJoinWorldEvent event) {
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {
		    @Override
		    public void run() {
		    	fixTabsForAllPlayers();
		    }
		});
		
	}
	*/
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public static void onKeyInput(KeyInputEvent event) {
		if(!ClientProxy.singlePlayer) {
			// TODO: Implement proper keybinding system to allow for reassigning functions
			if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiGroups());
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
				fixTabsForAllPlayers();
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				// This is to save config if you exit out of a menu the improper way
				IFFMod.saveConfig();
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void onNameEvent(NameFormat event) {
		if(!ClientProxy.singlePlayer) {
			String username = event.getUsername();
			boolean prefixed = false;
			for(Group g: ClientProxy.groupManager.getGroups()) {
				for(GroupPerson gp: g.getPersons()) {
					if (gp.getName().equals(username)) {
						event.setDisplayname(g.getName()+" "+event.getUsername());
						prefixed=true;
						break;
					}
				}
				if(prefixed)
					break;
			}
			if(!prefixed) {
				event.setDisplayname(event.getUsername());
			}
	    }
    }

}
