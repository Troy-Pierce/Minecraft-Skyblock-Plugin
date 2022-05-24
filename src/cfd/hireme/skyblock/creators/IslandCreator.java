/*
 *        derpySkyblock - Derpy00001 | Derpy#5247
 *        discord.gg/bQxBB89
 *        Hi! :)
 */

package cfd.hireme.skyblock.creators;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.events.IslandCreationEvent;
import cfd.hireme.skyblock.events.IslandDeletionEvent;
import cfd.hireme.skyblock.exceptions.UserHasNoIslandException;
import cfd.hireme.skyblock.objects.data.IslandData;
import cfd.hireme.skyblock.objects.main.Island;
import cfd.hireme.skyblock.objects.main.Islander;


public class IslandCreator {
	private static File templates = new File(Skyblock.getPlugin(Skyblock.class).getDataFolder().getAbsoluteFile()+"\\IslandTemplates");
	public static void createIsland(Islander owner, int islandid){
		File check = new File(Skyblock.getPlugin(Skyblock.class).getServer().getWorldContainer().getAbsoluteFile()+"\\Island_"+owner.getUniqueId().toString());
		if(!owner.hasIsland()) {
			IslandCreationEvent event = new IslandCreationEvent(false, owner, islandid);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if(!event.isCancelled()) {
				for(File file : templates.listFiles()) {
					if(file.isDirectory()) {
						for(File file2 : file.listFiles()) {
							if(file2.getName().toLowerCase().endsWith(".island")) {
								IslandData data = null;
								data = new IslandData(file2);
								if(data!=null) {
									if((Integer) data.getId()!=null) {
										if(data.getId()==event.getIslandTypeId()) {
											try {
												FileUtils.copyDirectory(file, check);
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											for(File newfiles : check.listFiles()) {
												if(newfiles.getName().equals("uid.dat")) {
													newfiles.delete();
												}
											}
											World world = Bukkit.createWorld(new WorldCreator("Island_"+owner.getUniqueId().toString()));
											world.setKeepSpawnInMemory(false);
											world.setAutoSave(false);
											world.getWorldBorder().setSize(30);
											world.getWorldBorder().setCenter(world.getSpawnLocation());
											ArrayList<String> mems = new ArrayList<String>();
											FileConfiguration settings = Skyblock.manager.getIslandSettings();
											settings.set("islands."+owner.getUniqueId().toString()+".settings.pvp", false);
											settings.set("islands."+owner.getUniqueId().toString()+".settings.public", true);
											settings.set("islands."+owner.getUniqueId().toString()+".settings.edit", false);
											settings.set("islands."+owner.getUniqueId().toString()+".members", mems);
											settings.set("islands."+owner.getUniqueId().toString()+".level", 1);
											settings.set("islands."+owner.getUniqueId().toString()+".owner", owner.getUniqueId().toString());
											settings.set("islands."+owner.getUniqueId().toString()+".creationdate", new Date());
											settings.set("islands."+owner.getUniqueId().toString()+".islandtype", data.getTitle().toString());
											settings.set("islands."+owner.getUniqueId().toString()+".invites", new ArrayList<String>());
											settings.set("islands."+owner.getUniqueId().toString()+".welcomemsg", "Welcome to my island!");
											settings.set("islands."+owner.getUniqueId().toString()+".challenges", null);
											settings.set("islands."+owner.getUniqueId().toString()+".upgrades.border", 1);
											settings.set("islands."+owner.getUniqueId().toString()+".upgrades.member", 1);
											settings.set("islands."+owner.getUniqueId().toString()+".upgrades.visitor", 1);
											settings.set("islands."+owner.getUniqueId().toString()+".spawn.x", world.getSpawnLocation().getBlockX());
											settings.set("islands."+owner.getUniqueId().toString()+".spawn.y", world.getSpawnLocation().getBlockY());
											settings.set("islands."+owner.getUniqueId().toString()+".spawn.z", world.getSpawnLocation().getBlockZ());
											Skyblock.manager.saveIslandSettings();
											Island island = Island.getIsland(world.getName());
											owner.setIsland(island);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	public static void deleteIsland(UUID player) {
		if(Islander.hasIsland(player)) {
			Islander user = Islander.getUser(player);
			try {
				if(user.isIslandOwner()) {
					IslandDeletionEvent event = new IslandDeletionEvent(false, user, user.getIsland());
					Bukkit.getServer().getPluginManager().callEvent(event);
					if(!event.isCancelled()) {
						File check = new File(Skyblock.getPlugin(Skyblock.class).getServer().getWorldContainer().getAbsolutePath()+"\\"+user.getIsland().getWorld().getName());
						String id = user.getIsland().getId().toString();
						for(Player p : user.getIsland().getWorld().getPlayers()) {
							if(Skyblock.getPlugin(Skyblock.class).getConfig().getString("spawn_world_name").equals("WORLDNAME")) {
								p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
							}else {
								p.teleport(Bukkit.getWorld(Skyblock.getPlugin(Skyblock.class).getConfig().getString("spawn_world_name")).getSpawnLocation());
							}
						}
						Bukkit.unloadWorld(user.getIsland().getWorld().getName(), false);
						for(Islander members : user.getIsland().getMembers()) {
							members.setIsland(null);
						}
						if(Island.getRegistered().containsKey(UUID.fromString(id))) {
							Island.getRegistered().remove(UUID.fromString(id));
						}
						Skyblock.manager.getIslandSettings().set("island."+id, null);
						Skyblock.manager.saveIslandSettings();
						
						try {
							FileUtils.deleteDirectory(check);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} catch (UserHasNoIslandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
