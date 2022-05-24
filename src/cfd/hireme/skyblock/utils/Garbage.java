package cfd.hireme.skyblock.utils;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.extra.scoreboard.Board;
import cfd.hireme.skyblock.extra.scoreboard.BoardManager;
import cfd.hireme.skyblock.objects.main.Island;
import cfd.hireme.skyblock.objects.main.Islander;

public class Garbage {
	public Garbage() {
		new BukkitRunnable(){
			@Override
			public void run(){
				try {
					Iterator<Entry<UUID, Island>> iter1 = Island.getRegistered().entrySet().iterator();
					while(iter1.hasNext()) {
						Entry<UUID, Island> entry = iter1.next();
						if(entry.getValue().getWorld()==null) {
							iter1.remove();
						}else {
							boolean online=false;
							for(Islander islander:entry.getValue().getMembers()) {
								if(islander.getOfflinePlayer().getPlayer()!=null) {
									if(islander.getOfflinePlayer().isOnline()) {
										online=true;
									}
								}
							}
							if(entry.getValue().getVisitorCount()>0) {
								online=true;
							}
							if(!online) {
								iter1.remove();
								Bukkit.getServer().unloadWorld(entry.getValue().getWorld().getName(), true);
							}
						}
					}
				}catch(NullPointerException e) {e.printStackTrace();};
				try {
					Iterator<Entry<UUID, Islander>> iter = Islander.getRegistered().entrySet().iterator();
					while(iter.hasNext()) {
						Entry<UUID,  Islander> entry = iter.next();
						if(entry!=null) {
							if(entry.getValue()!=null) {
								if(entry.getValue().getOfflinePlayer().getPlayer()!=null) {
									if(!entry.getValue().getOfflinePlayer().isOnline()) {
										iter.remove();
									}
								}else {
									iter.remove();
								}
							}else {
								iter.remove();
							}
						}
					}
				}catch(NullPointerException e) {e.printStackTrace();};
				
				try {
					Iterator<Entry<Islander, Board>> iter2 = BoardManager.getBoards().entrySet().iterator();
					while(iter2.hasNext()) {
						Entry<Islander, Board> entry = iter2.next();
						if(entry.getKey().getOfflinePlayer()!=null) {
							if(!entry.getKey().getOfflinePlayer().isOnline()) {
								BoardManager.removeBoard(entry.getKey());
							}
						}else {
							BoardManager.removeBoard(entry.getKey());
						}
					}
				}catch(NullPointerException e) {e.printStackTrace();};
			}
		}.runTaskTimer(Skyblock.getPlugin(Skyblock.class), 20*10, 20*10);
	}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
