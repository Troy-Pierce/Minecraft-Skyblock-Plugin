package cfd.hireme.skyblock.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import cfd.hireme.skyblock.objects.main.Island;
import cfd.hireme.skyblock.objects.main.Islander;

public class Pvp implements Listener{
	@EventHandler
	private void onDamage(EntityDamageByEntityEvent event) {
		if(event.getEntity().getWorld().getName().contains("Island_")) {
			if(event.getEntity() instanceof Player) {
				if(event.getDamager() instanceof Player) {
					event.setCancelled(true);
					Island island = Island.getIsland(event.getEntity().getWorld().getName());
					if(Islander.getUser((Player) event.getEntity()).isMemberOf(island)&&Islander.getUser((Player)event.getDamager()).isMemberOf(island)){
						if(island.getSettings().canPvp()) {
							event.setCancelled(false);
						}
					}
				}
			}else {
				if(event.getDamager() instanceof Player) {
					event.setCancelled(true);
					Island island = Island.getIsland(event.getEntity().getWorld().getName());
					if(Islander.getUser((Player)event.getDamager()).isMemberOf(island)) {
						event.setCancelled(false);
					}
				}
			}
		}
	}

}
