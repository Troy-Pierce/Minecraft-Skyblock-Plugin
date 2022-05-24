package cfd.hireme.skyblock.extra.shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.objects.data.SectionData;
import cfd.hireme.skyblock.objects.data.ShopData;
import cfd.hireme.skyblock.utils.Console;

public class Shop {
	private static FileConfiguration config = Skyblock.getPlugin(Skyblock.class).getConfig();
	public static List<SectionData> getSections() {
		Set<String> set =config.getConfigurationSection("Shop").getKeys(false);
		List<SectionData> data = new ArrayList<SectionData>();
		for(String string:set) {
			if(!string.equals("show_restricted")) {
				String path = "Shop."+string;
				boolean restricted = config.getBoolean(path+".restricted");
				Material mat = Material.getMaterial(config.getString(path+".material"));
				List<String> lore = config.getStringList(path+".lore");
				data.add(SectionData.get(string, restricted, mat, lore));
			}
		}
		return data;
	}
	public static List<ShopData> getSectionItems(String section){
		Set<String> set = config.getConfigurationSection("Shop."+section).getKeys(false);
		List<ShopData> data = new ArrayList<ShopData>();
		for(String string:set) {
			List<String> bad = Arrays.asList("restricted","material","lore");
			if(!bad.contains(string)) {
				String path = "Shop."+section+"."+string;
				Material mat = Material.getMaterial(config.getString(path+".material"));
				if(mat==null) {
					mat=Material.AIR;
					Console.error("Unable to find shop material: \""+config.getString(path+".material")+"\" for item: "+path);
				}
				double cost = config.getDouble(path+".cost");
				double sell = config.getDouble(path+".sell");
				data.add(ShopData.get(mat, cost, sell));
			}
		}
		return data;
	}
}
