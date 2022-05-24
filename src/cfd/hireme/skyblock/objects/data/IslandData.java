/*
 *        derpySkyblock - Derpy00001 | Derpy#5247
 *        discord.gg/bQxBB89
 *        Hi! :)
 */

package cfd.hireme.skyblock.objects.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.utils.Console;
import net.md_5.bungee.api.ChatColor;

public class IslandData {
	private static File templates = new File(Skyblock.getPlugin(Skyblock.class).getDataFolder().getAbsoluteFile()+"\\IslandTemplates");
	int id;
	Material displayitem;
	boolean enchanted;
	ArrayList<String> lore = new ArrayList<String>();
	String title;
	boolean displayed;
	boolean locked;
	boolean buyable;
	double cost;
	public IslandData(File file){
			List<String> strings = null;
			try {
				strings = Files.readAllLines(file.toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ArrayList<String> filtered = new ArrayList<String>();
			for(String string : strings) {
				if(!(string.startsWith(">"))) {
					if(!string.equals("")||string!=null) {
						filtered.add(string);
					}
				}
			}
			for(String string : filtered) {
				String[] split = string.split(":");
				String identifier = split[0].toLowerCase();
				switch(identifier) {
					case "item-display":
						this.displayitem=Material.getMaterial(split[1]);
						if(this.displayitem==null) {
							Console.error("Unable to find Material: "+split[1]);
						}
						break;
					case "item-title":
						this.title=ChatColor.translateAlternateColorCodes('&', split[1]);
						this.title=this.title.replace("\"", "");
						break;
					case "item-enchanted":
						this.enchanted=Boolean.parseBoolean(split[1]);
						break;
					case "item-lore":
						if(split[1].startsWith("[")&&split[1].endsWith("]")) {
							String newsplit;
							newsplit= split[1].replace("[", "");
							newsplit=newsplit.replace("]", "");
							String[] splitlore = newsplit.split(",");
							for(String sls : splitlore) {
								sls=sls.replace("\"", "");
								this.lore.add(ChatColor.translateAlternateColorCodes('&', sls));
							}
						}
						break;
					case "island-id":
						this.id=Integer.parseInt(split[1]);
						break;
					case "island-displayed":
						this.displayed=Boolean.parseBoolean(split[1]);
						break;
					case "island-require-perm":
						this.locked=Boolean.parseBoolean(split[1]);
						break;
					case "island-buyable":
						this.buyable=Boolean.parseBoolean(split[1]);
						break;
					case "island-cost":
						this.cost=Double.parseDouble(split[1]);
						break;
				}
			}
			
	}
	public int getId() { return this.id;}
	public Material getDisplayItem() {return this.displayitem;}
	public boolean isEnchanted() {return this.enchanted;}
	public List<String> getLore(){return this.lore;}
	public String getTitle() {return this.title;}
	public boolean isDisplayed() {return this.displayed;}
	public boolean isLocked() {return this.locked;}
	public boolean isBuyable() {return this.buyable;}
	public double getCost() {return this.cost;}
	public static ArrayList<IslandData> getTemplates(){
		ArrayList<IslandData> isdata = new ArrayList<IslandData>();
		for(File file : templates.listFiles()) {
			if(file.isDirectory()) {
				for(File file2:file.listFiles()) {
					if(file2.getName().endsWith(".island")) {
						IslandData data = new IslandData(file2);
						isdata.add(data);
					}
				}
			}
		}
		return isdata;
	}
	public static IslandData getFromId(int i) {
		for(IslandData data: getTemplates()) {
			if(data.getId()==i) {
				return data;
			}
		}
		return null;
	}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
