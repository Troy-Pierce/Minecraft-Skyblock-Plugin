package cfd.hireme.skyblock.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import cfd.hireme.skyblock.objects.main.Island;
import cfd.hireme.skyblock.objects.main.Islander;

public class Pickup implements Listener{
	@EventHandler
	private void onPickup(EntityPickupItemEvent event) {
		if(event.getEntity().getWorld().getName().contains("Island_")) {
			if(event.getEntity() instanceof Player) {
				Island island = Island.getIsland(event.getEntity().getWorld().getName());
				if(!island.getSettings().canPickup()) {
					Islander islander = Islander.getUser((Player)event.getEntity());
					if(!islander.isMemberOf(island)) {
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
