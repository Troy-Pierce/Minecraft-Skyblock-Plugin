package cfd.hireme.skyblock.objects.data;

import java.util.List;

import org.bukkit.Material;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.utils.Console;

public class SectionData {
	String name;
	Material mat;
	boolean rest;
	List<String> lore;
	public SectionData(String name, boolean restricted, Material material, List<String> lore) {
		this.name=name;
		this.rest=restricted;
		this.mat=material;
		this.lore=lore;
	}
	public String getName() {return this.name;}
	public Material getMaterial() {return this.mat;}
	public boolean isRestricted() {return this.rest;}
	public List<String> getLore(){return this.lore;}
	public static SectionData get(String name, boolean restricted, Material material, List<String> lore) {
		return new SectionData(name, restricted, material, lore);
	}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
