package xyz.risingthumb.iff.classes;

import java.util.ArrayList;
import java.util.List;

public class Group {
	private List<GroupPerson> persons;
	private String prefix = "XYZ";
	
	public Group() {
		this.persons = new ArrayList<GroupPerson>();
		this.prefix = "XYZ";
	}
	public Group(String prefix) {
		this.persons = new ArrayList<GroupPerson>();
		this.prefix = prefix;
	}
	public Group(List<GroupPerson> persons, String prefix) {
		this.persons = new ArrayList<GroupPerson>();
		// Protective
		for(GroupPerson gp : persons) {
			this.persons.add(new GroupPerson(gp.getName()));
		}
		this.prefix = prefix;
	}
	
	public void addPerson(GroupPerson person) {
		this.persons.add(person);
	}
	
	public void removePerson(int personIndex) {
		this.persons.remove(personIndex);
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getName() {
		return this.prefix;
	}
	public int size() {
		return persons.size();
	}
	public GroupPerson getPerson(int index) {
		return persons.get(index);
	}
	public List<GroupPerson> getPersons() {
		return persons;
	}
}
