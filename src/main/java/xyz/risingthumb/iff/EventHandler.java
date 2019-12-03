package xyz.risingthumb.iff;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.risingthumb.iff.classes.Group;
import xyz.risingthumb.iff.classes.GroupPerson;
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
		tickCount+=1;
		if(tickCount%20 == 0) {
			tickCount=1;
			fixTabsForAllPlayers();
		}
		
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled = true)
	public static void onEvent(ClientConnectedToServerEvent event) {
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
	public static void onEvent(KeyInputEvent event) {
		if(!ClientProxy.singlePlayer) {
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
    public static void onEvent(NameFormat event)
    {
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
