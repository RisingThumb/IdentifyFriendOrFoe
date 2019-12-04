package xyz.risingthumb.iff.gui;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import xyz.risingthumb.iff.IFFMod;
import xyz.risingthumb.iff.groups.Group;
import xyz.risingthumb.iff.groups.GroupPerson;
import xyz.risingthumb.iff.proxy.ClientProxy;

public class GuiGroups extends GuiScreen {

	private int selectedGroup = -1;
	private int selectedPlayer = -1;
	
	GuiButton buttonAddGroup;
	GuiButton buttonRemoveGroup;
	GuiButton buttonAddPlayer;
	GuiButton buttonRemovePlayer;
	// This is basically Enums, but Java fucking sucks for enums
	// So I'm just doing this. Fuck Java
	private final int BUTTONADDGROUP = 0;
	private final int BUTTONREMOVEGROUP = 1;
	private final int BUTTONADDPLAYER = 2;
	private final int BUTTONREMOVEPLAYER = 3;
	private final int BUTTONUTILITYEND = 4;
	private final int groupIDStart = 100;
	private final int playerIDStart = 50000;
	//final int playerIDStart = 10000;
	
	private GuiTextField textFieldGroupName;
	private GuiTextField textFieldPlayerName;
	
	private void fixTabName() {
		String username = textFieldPlayerName.getText();
		boolean prefixed = false;
		for(Group g: ClientProxy.groupManager.getGroups()) {
			for(GroupPerson gp: g.getPersons()) {
				if (gp.getName().equals(username)) {
					NetworkPlayerInfo npi = Minecraft.getMinecraft().getConnection().getPlayerInfo(username);
					//npi.setDisplayName(new TextComponentString(TextFormatting.AQUA+g.getName()+" "+username));
					if(npi!=null) {
						npi.setDisplayName(new TextComponentString(g.getName()+" "+username));
					}
					prefixed=true;
					break;
				}
			}
			if(prefixed)
				break;
		}
	}
	
	private void removeTabName(String name) {
		String username = name;
		NetworkPlayerInfo npi = Minecraft.getMinecraft().getConnection().getPlayerInfo(username);
		//npi.setDisplayName(new TextComponentString(TextFormatting.AQUA+g.getName()+" "+username));
		if(npi!=null) {
			npi.setDisplayName(new TextComponentString(username));
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		textFieldGroupName.drawTextBox();
		drawString(Minecraft.getMinecraft().fontRenderer,"Group Prefix:",0,6,Integer.parseInt("FFAA00", 16));
		textFieldPlayerName.drawTextBox();
		drawString(Minecraft.getMinecraft().fontRenderer,"Players Name:",this.width-81,6,Integer.parseInt("FFAA00", 16));
		drawString(Minecraft.getMinecraft().fontRenderer,"Like this Software? Go to risingthumb.xyz website for more!",1,this.height-16,Integer.parseInt("FFAA00", 16));
	}
	
	@Override
	public void initGui() {
		
		buttonsClearCreate();
		
		textFieldGroupName = new GuiTextField(-4, Minecraft.getMinecraft().fontRenderer,1,20, 80, 20);
		textFieldGroupName.setFocused(false);
		textFieldGroupName.setMaxStringLength(10);
		textFieldGroupName.setText("Prefix");
		
		textFieldPlayerName = new GuiTextField(-4, Minecraft.getMinecraft().fontRenderer,this.width-81,20, 80, 20);
		textFieldPlayerName.setFocused(false);
		textFieldPlayerName.setMaxStringLength(16);
		textFieldPlayerName.setText("Username");
		
		
		super.initGui();
	}
	
	public void updateButtons() {
		for (GuiButton button : buttonList) {
			button.enabled = true;
		}
	}
	
	private void buttonsClearCreate() {
		buttonList.clear();
		
		buttonList.add(buttonAddGroup = new GuiButton(BUTTONADDGROUP, 1, 40, 80, 20, "Add Group"));
		buttonList.add(buttonRemoveGroup = new GuiButton(BUTTONREMOVEGROUP, 1, 60, 80, 20, "Remove Group"));
		
		buttonList.add(buttonAddPlayer = new GuiButton(BUTTONADDPLAYER, this.width-81, 40, 80, 20, "Add Player"));
		buttonList.add(buttonRemovePlayer = new GuiButton(BUTTONREMOVEPLAYER, this.width-81, 60, 80, 20, "Remove Player"));
		
		for(int i = 0; i<ClientProxy.groupManager.getSize(); i++) {
			buttonList.add(new GuiButton(i+groupIDStart, 5, 100+20*(i), 60, 20, ClientProxy.groupManager.getNameOfGroup(i)));
		}
		
		if(selectedGroup != -1) {
			buttonList.get(selectedGroup+BUTTONUTILITYEND).enabled = false;
			Group group = ClientProxy.groupManager.getGroup(selectedGroup);
			for(int i = 0; i<group.size(); i++) {
				buttonList.add(new GuiButton(i+playerIDStart, this.width-146, 100+20*(i), 60, 20, group.getPerson(i).getName()));
			}
		}
		
		if(selectedGroup == -1) {
			buttonList.get(BUTTONADDPLAYER).enabled = false;
			buttonList.get(BUTTONREMOVEPLAYER).enabled = false;
		}
		
		if(selectedPlayer != -1) {
			buttonList.get(selectedPlayer+ClientProxy.groupManager.getSize()+BUTTONUTILITYEND).enabled = false;
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		EntityPlayer player;
		updateButtons();
		
		switch(button.id) {
		case BUTTONADDGROUP:
			ClientProxy.groupManager.addGroup(new Group(textFieldGroupName.getText()));
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Added group "+textFieldGroupName.getText()));
			buttonsClearCreate();
			IFFMod.saveConfig();
			break;
		case BUTTONREMOVEGROUP:
			if(selectedGroup!=-1) {
				// Need to remove the tab name of all users in the group
				Group g = ClientProxy.groupManager.getGroup(selectedGroup);
				List<GroupPerson> gp = g.getPersons();
				for(GroupPerson p:gp) {
					removeTabName(p.getName());
				}
				
				ClientProxy.groupManager.removeGroup(selectedGroup);
				selectedGroup = -1;
				IFFMod.saveConfig();
			}
			else if (selectedGroup==-1) {
				Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Must select a group in the group list"));
			}
			break;
		case BUTTONADDPLAYER:
			ClientProxy.groupManager.getGroup(selectedGroup).addPerson(new GroupPerson(textFieldPlayerName.getText()));
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Added player "+textFieldPlayerName.getText()));
			player = Minecraft.getMinecraft().world.getPlayerEntityByName(textFieldPlayerName.getText());
			if (player!=null) {
				player.refreshDisplayName();
			}
			fixTabName();
			IFFMod.saveConfig();
			break;
		case BUTTONREMOVEPLAYER:
			if(selectedGroup!=-1 && selectedPlayer!=-1) {
				player = Minecraft.getMinecraft().world.getPlayerEntityByName(ClientProxy.groupManager.getGroup(selectedGroup).getPerson(selectedPlayer).getName());
				if (player!=null) {
					player.refreshDisplayName();
				}
				removeTabName(ClientProxy.groupManager.getGroup(selectedGroup).getPerson(selectedPlayer).getName());
				ClientProxy.groupManager.getGroup(selectedGroup).removePerson(selectedPlayer);
				selectedPlayer = -1;
				IFFMod.saveConfig();
			}
			else if (selectedPlayer==-1) {
				Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Must select a player in the player list"));
			}
			break;
		default:
			// We handle the generated cases
			if(button.id >=groupIDStart && button.id<playerIDStart) {
				selectedGroup = button.id-groupIDStart;
				button.enabled = false;
				selectedPlayer = -1;
			}
			if(button.id>=playerIDStart) {
				selectedPlayer = button.id-playerIDStart;
				button.enabled = false;
			}
			break;
		}
		super.actionPerformed(button);
		buttonsClearCreate();
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException{
		super.keyTyped(typedChar, keyCode);
		
		if (textFieldGroupName.isFocused()) {
			textFieldGroupName.textboxKeyTyped(typedChar, keyCode);
		}
		
		if (textFieldPlayerName.isFocused()) {
			textFieldPlayerName.textboxKeyTyped(typedChar, keyCode);
		}
	}
	
	public void mouseClicked(int i, int j, int k) throws IOException {
		super.mouseClicked(i,j,k);
		textFieldGroupName.mouseClicked(i,j,k);
		textFieldPlayerName.mouseClicked(i,j,k);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
