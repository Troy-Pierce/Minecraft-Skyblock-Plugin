package cfd.hireme.skyblock.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import cfd.hireme.skyblock.objects.Permissions;
import cfd.hireme.skyblock.objects.main.Island;
import cfd.hireme.skyblock.objects.main.Islander;
import net.md_5.bungee.api.ChatColor;

public class OnBreak implements Listener{
	@EventHandler
	private void onBreak(BlockBreakEvent event) {
		if(event.getBlock().getWorld().getName().contains("Island_")) {
			Island island = Island.getIsland(event.getBlock().getWorld().getName());
			if(!island.getSettings().canEdit()) {
				if(!Islander.getUser(event.getPlayer()).isMemberOf(island)) {
					if(!Islander.getUser(event.getPlayer()).getSettings().isBuildBypass()) {
						event.setCancelled(true);
						if(event.getPlayer().hasPermission(Permissions.Operator.islandBuildBypass)) {
							event.getPlayer().sendMessage(ChatColor.RED+"To bypass island restrictions, view the operators menu in /island");
						}
					}
				}
			}
		}
	}
	@EventHandler
	private void onInteract(PlayerInteractEvent event) {
		if(event.getPlayer().getWorld().getName().contains("Island_")) {
			if(event.getClickedBlock()!=null) {
				Island island = Island.getIsland(event.getClickedBlock().getWorld().getName());
				Islander islander = Islander.getUser(event.getPlayer());
				if(!island.getSettings().canEdit()) {
					if(!islander.isMemberOf(island)) {
						if(!islander.getSettings().isBuildBypass()) {
							event.setCancelled(true);
							if(event.getPlayer().hasPermission(Permissions.Operator.islandBuildBypass)) {
								event.getPlayer().sendMessage(ChatColor.RED+"To bypass island restrictions, view the operators menu in /island");
							}
						}
					}
				}
			}
		}
	}
}
