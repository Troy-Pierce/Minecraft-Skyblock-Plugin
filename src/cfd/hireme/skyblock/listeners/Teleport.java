package cfd.hireme.skyblock.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.objects.Permissions;
import cfd.hireme.skyblock.objects.main.Island;
import cfd.hireme.skyblock.objects.main.Islander;
import net.md_5.bungee.api.ChatColor;

public class Teleport implements Listener{
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if(event.getFrom().getWorld().getName().equals(event.getTo().getWorld().getName())) {
			return;
		}
		if(event.getFrom().getWorld().getName().contains("Island_")&&!event.getTo().getWorld().getName().contains("Island_")) {
			event.getPlayer().sendTitle(ChatColor.RED+"Leaving Island", null, 10, 70, 20);
			return;
		}
		if(event.getTo().getWorld().getName().contains("Island_")) {
			Island island = Island.getIsland(event.getTo().getWorld().getName());
			Islander islander = Islander.getUser(event.getPlayer());
			String message = null;
			if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("island_settings.custom_island_welcome.enabled")) {
				message=island.getWelcome();
			}
			if(!island.getSettings().isPublic()) {
				if(!islander.isMemberOf(island)) {
					if(!islander.getSettings().isEnterBypass()) {
						event.setCancelled(true);
					}
				}
			}
			if(event.isCancelled()) {
				event.getPlayer().sendTitle("Island Locked", null, 10, 70, 20);
				for(Islander islande:island.getMembers()) {
					if(islande.getOfflinePlayer().isOnline()) {
						islande.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+event.getPlayer().getName()+" attempted to enter your island!");
					}
				}
				if(event.getPlayer().hasPermission(Permissions.Operator.islandEnterBypass)) {
					event.getPlayer().sendMessage(ChatColor.RED+"To bypass island restrictions, view the operators menu in /island");
				}
			}else {
				event.getPlayer().sendTitle(island.getOwner().getName()+"'s Island", message, 10, 70, 20);
			}
		}
	}
}
