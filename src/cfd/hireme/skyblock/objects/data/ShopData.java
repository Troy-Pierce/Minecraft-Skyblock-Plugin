package cfd.hireme.skyblock.objects.data;

import org.bukkit.Material;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.utils.Console;

public class ShopData {
	private Material mat;
	private double cost;
	private double sell;
	public ShopData(Material material, double cost, double sell) {
		this.mat=material;
		this.cost=cost;
		this.sell=sell;
	}
	public String getName() {
		return getProperName(this.mat.name());
	}
	public Material getMaterial() {return this.mat;}
	public double getCost() {return this.cost;}
	public double getSell() {return this.sell;}
	public static ShopData get(Material material, double cost, double sell) {
		return new ShopData(material, cost, sell);
	}
	public static String getProperName(String string) {
		if(string.contains("_")) {
			String[] words =string.split("_");
			String name="";
			for(String w:words) {
				w=w.toLowerCase();
				name+=w.substring(0,1).toUpperCase()+w.substring(1)+" ";
			}
			return name.trim();
		}else {
			String name = string.toLowerCase();
			name=name.substring(0, 1).toUpperCase()+name.substring(1);
			return name.trim();
		}
	}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
