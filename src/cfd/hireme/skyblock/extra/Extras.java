package cfd.hireme.skyblock.extra;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.objects.RandomCollection;
import cfd.hireme.skyblock.utils.Console;
import cfd.hireme.skyblock.utils.ParseMap;

public class Extras {
	private static boolean gen_set = false;
	private static RandomCollection<Material> genran = new RandomCollection<>();
	private static FileConfiguration config = Skyblock.getPlugin(Skyblock.class).getConfig();
	public static class Economy{
		public static boolean economyEnabled() {
			return !config.getBoolean("Primary.economy.ECONOMY_DISABLED");
		}
		public static boolean skyEcoEnabled() {
			return config.getBoolean("Primary.economy.skyblock_economy");
		}
		public static boolean essentialEcoEnabled() {
			return config.getBoolean("Primary.economy.essentials_economy");
		}
		public static String getSymbol() {
			return config.getString("Primary.economy.symbol");
		}
	}
	public static class Shop{
		public static boolean shopEnabled() {
			return config.getBoolean("Primary.shop.enabled");
		}
	}
	public static class Scoreboard{
		public static boolean boardEnabled() {
			return config.getBoolean("Primary.scoreboard.enabled");
		}
		public static boolean showIpEnabled() {
			return config.getBoolean("Primary.scoreboard.show_ip");
		}
		public static String getIp() {
			return config.getString("Primary.scoreboard.ip");
		}
		public static String getHeader() {
			return config.getString("Primary.scoreboard.header");
		}
	}
	public static class Generator{
		public static RandomCollection<Material> getGen(){
			if(!gen_set) {
				Iterator<Entry<String, Double>> iter = ((Map<String, Double>) ParseMap.parse(config.getStringList("Primary.generator.blocks"))).entrySet().iterator();
				while(iter.hasNext()) {
					Entry<String, Double> entry = iter.next();
					Material mat = Material.getMaterial(entry.getKey());
					if(mat==null) {
						Console.error("Failed to find material: \""+entry.getKey()+"\", could not add material to cobblestone generator block list");
					}else {
						genran.add(entry.getValue(), mat);
					}
				}
				gen_set=true;
			}
			return genran;
		}
		public static boolean generatorEnabled() {
			return config.getBoolean("Primary.generator.enabled");
		}
	}
}
