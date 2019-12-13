package xyz.risingthumb.iff.classes;

import java.util.ArrayList;
import java.util.List;

public class GroupManager {
	private List<Group> groups;
	private int groupIndex = 0;
	
	public GroupManager() {
		this.groups = new ArrayList<Group>();
	}
	
	public void addGroup(Group group) {
		this.groups.add(group);
	}
	public void removeGroup(int indexToRemove) {
		// We remove based on index
		this.groups.remove(indexToRemove);
		modifyGroupIndex(-1);
	}
	public void modifyGroupIndex(int changeValue) {
		this.groupIndex+=changeValue;
		// If there's no groups. literally do nothing
		if (groups.size()==0) {
			this.groupIndex = 0;
			return;
		}
		// Bounds
		if (groupIndex < 0) {
			groupIndex = groups.size()-1;
		}
		if (groupIndex >= groups.size()) {
			groupIndex = 0;
		}
	}

	public int getSize() {
		return groups.size();
	}

	public String getNameOfGroup(int index) {
		return groups.get(index).getName();
	}
	
	public Group getGroup(int index) {
		return groups.get(index);
	}

	public List<Group> getGroups() {
		return groups;
	}

}
