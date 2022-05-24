package cfd.hireme.skyblock.objects.runnables;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.utils.Console;
import net.md_5.bungee.api.ChatColor;

public class LoaderRunnable implements Runnable{
	List<String> toload;
	public LoaderRunnable(List<String> string) {
		this.toload=string;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int i=0;
		for(String string : toload) {
			i++;
			Bukkit.getConsoleSender().sendMessage(Integer.toString(i)+"|"+Double.toString(i%5));
			if(i%5==0) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+"Running Garbage Collector");
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+Long.toString(Runtime.getRuntime().freeMemory()-Runtime.getRuntime().maxMemory())+"/"+Long.toString(Runtime.getRuntime().maxMemory()));
				Runtime.getRuntime().gc();
			}
			Console.print("Loading: "+string);
			
			World world = Bukkit.createWorld(new WorldCreator(string));
			world.setKeepSpawnInMemory(false);
			world.setAutoSave(false);
			world.getWorldBorder().setSize(70);
			world.getWorldBorder().setCenter(world.getSpawnLocation());
			world=null;
			
		}
	}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
