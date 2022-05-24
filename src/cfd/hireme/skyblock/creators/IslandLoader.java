package cfd.hireme.skyblock.creators;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.objects.main.Island;
import cfd.hireme.skyblock.utils.Console;
import net.md_5.bungee.api.ChatColor;

public class IslandLoader {
	private static Skyblock plugin = Skyblock.getPlugin(Skyblock.class);
	public static World load(String worldname) {
		for(World world:Bukkit.getServer().getWorlds()) {
			if(world.getName().equals(worldname)) {
				return world;
			}
		}
		World world = new WorldCreator(worldname).createWorld();
		world.setKeepSpawnInMemory(false);
		world.setAutoSave(false);
		Island island = Island.getIsland(worldname);
		int size = (island.getBorderLevel()-1)*10;
		world.getWorldBorder().setSize(30+size);
		world.getWorldBorder().setCenter(island.getBorderCenter());
		return world;
	}
	@Deprecated
	public static void loadIslands() {
		File[] unloaded = plugin.getServer().getWorldContainer().listFiles();
		ArrayList<String> aloaded = new ArrayList<String>();
		ArrayList<String> toload = new ArrayList<String>();
		for(World world : plugin.getServer().getWorlds()) {
			for(File file : unloaded) {
				if(world.getName().equals(file.getName())) {
					aloaded.add(file.getName());
				}
			}
		}
		for(File file : unloaded) {
			if(!aloaded.contains(file.getName())) {
				if(file.getName().contains("Island_")) {
					toload.add(file.getName());
				}
			}
		}
		int i =0;
		for(String string : toload) {
			i++;
			Bukkit.getConsoleSender().sendMessage(Integer.toString(i)+"|"+Double.toString(i%5));
			if(i%5==0) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+"Running Garbage Collector");
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+Long.toString(Runtime.getRuntime().freeMemory()-Runtime.getRuntime().maxMemory())+"/"+Long.toString(Runtime.getRuntime().maxMemory()));
				Runtime.getRuntime().gc();
			}
			Console.print("Loading: "+string);
			WorldCreator creator = new WorldCreator(string);
			World world = Bukkit.createWorld(creator);
			world.setKeepSpawnInMemory(false);
			world.setAutoSave(false);
			world.getWorldBorder().setSize(70);
			world.getWorldBorder().setCenter(world.getSpawnLocation());
			world=null;
			creator=null;
		}
	}
}
