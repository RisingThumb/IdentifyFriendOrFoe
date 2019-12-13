package xyz.risingthumb.iff;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.risingthumb.iff.classes.Group;
import xyz.risingthumb.iff.classes.GroupPerson;
import xyz.risingthumb.iff.gui.GuiGroups;
import xyz.risingthumb.iff.proxy.ClientProxy;
import xyz.risingthumb.iff.scheduling.ScheduledEvent;
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
	
	@SubscribeEvent
	public static void onRenderTick(final TickEvent.RenderTickEvent event) {
		for(Scheduler s: schedulerList) {
			s.tickTock();
			if (s.getTick()<=0) {
				cleanUpSchedule.add(s);
			}
		}
		// This small addition prevents a concurrency error caused by modifying the list as it's been read in the for each loop
		// Just think of it as, cleaning up your schedule
		for(Scheduler c: cleanUpSchedule) {
			schedulerList.remove(c);
		}
		cleanUpSchedule.clear();
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled = true)
	public static void onEvent(ClientConnectedToServerEvent event) {
		ClientProxy.singlePlayer = event.isLocal();
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public static void onEvent(KeyInputEvent event) {
		if(!ClientProxy.singlePlayer) {
			if (KeyBindings.groupKey.isPressed()) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiGroups());
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
				fixTabsForAllPlayers();
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				// This is to save config if you exit out of a menu the improper way
				IFFMod.saveConfig();
			}
			if (KeyBindings.screenshotUploadKey.isPressed()) {
				Minecraft.getMinecraft().player.sendMessage(new TextComponentString("s"));
				if(!ClientProxy.hasScreenshot) {
					ClientProxy.hasScreenshot = true;
					for (Group g: ClientProxy.groupManager.getGroups()) {
						for (GroupPerson gp: g.getPersons()) {
							EntityPlayer player = Minecraft.getMinecraft().world.getPlayerEntityByName(gp.getName());
							if (player!=null) {
								player.refreshDisplayName();
							}
							GuiGroups.removeTabName(gp.getName());
						}
					}
				}
				
				new Scheduler(2, new ScheduledEvent() {
					@Override
					public void run() {
						Minecraft mc = Minecraft.getMinecraft();
						Minecraft.getMinecraft().player.sendMessage(ScreenShotHelper.saveScreenshot(mc.mcDataDir, mc.displayWidth, mc.displayHeight, mc.getFramebuffer()));
						ClientProxy.hasScreenshot = false;
						fixTabsForAllPlayers();
						for (Group g: ClientProxy.groupManager.getGroups()) {
							for (GroupPerson gp: g.getPersons()) {
								EntityPlayer player = Minecraft.getMinecraft().world.getPlayerEntityByName(gp.getName());
								if (player!=null) {
									player.refreshDisplayName();
								}
								GuiGroups.removeTabName(gp.getName());
							}
						}
					}
				});
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
						if (!ClientProxy.hasScreenshot) {
							event.setDisplayname(g.getName()+" "+event.getUsername());
						}
						else {
							event.setDisplayname(event.getUsername());
						}
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
