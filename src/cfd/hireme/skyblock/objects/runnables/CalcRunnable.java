package cfd.hireme.skyblock.objects.runnables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.events.IslandLevelChangeEvent;
import cfd.hireme.skyblock.objects.main.Island;
import cfd.hireme.skyblock.objects.main.Islander;
import cfd.hireme.skyblock.utils.Console;
import cfd.hireme.skyblock.utils.ParseMap;
import net.md_5.bungee.api.ChatColor;

public class CalcRunnable implements Runnable{
	private Island island;
	private int maxheight;
	public CalcRunnable(Island island) {
		this.island=island;
		this.maxheight=island.getWorld().getMaxHeight();
	}
	private World getWorld() {
		return this.island.getWorld();
	}
	@Override
	public void run() {
		int before=this.island.getLevel();
		List<Chunk> chunks = new ArrayList<Chunk>();
		for(int x=getWorld().getWorldBorder().getCenter().getBlockX();x<(getWorld().getWorldBorder().getSize()/1);x++) {
			for(int z=getWorld().getWorldBorder().getCenter().getBlockZ();z<(getWorld().getWorldBorder().getSize()/1);z++) {
				if(!chunks.contains(new Location(getWorld(), x, 0,z).getChunk())) {
					chunks.add(new Location(getWorld(), x,0,z).getChunk());
				}
			}
		}
		double rawlevel=0;
		for(Chunk chunk : chunks) {
			for(int y=0;y<maxheight;y++) {
				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						Map<String, Double> map = (Map<String, Double>) ParseMap.parse(Skyblock.getPlugin(Skyblock.class).getConfig().getStringList("block_values"));
						Block block = chunk.getBlock(x, y, z);
						if(map.containsKey(block.getType().name())) {
							rawlevel=rawlevel+map.get(block.getType().name());
						}else {
							if(block!=null) {
								if(!(block.getType()==Material.AIR)) {
									rawlevel=rawlevel+map.get("DEFAULT");
								}
							}
						}
					}
				}
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int after = (int) Math.floor(rawlevel);
		if(!(after==before)) {
			island.setLevel((int) Math.floor(rawlevel));
			Bukkit.getServer().getPluginManager().callEvent(new IslandLevelChangeEvent(true,this.island, before, after));
		}
		for(Islander islander:this.island.getMembers()) {
			if(islander.getOfflinePlayer().isOnline()) {
				islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+"Your island level is "+(int) Math.floor(rawlevel));
			}
		}
		
	}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
