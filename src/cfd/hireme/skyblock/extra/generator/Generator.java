package cfd.hireme.skyblock.extra.generator;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import cfd.hireme.skyblock.extra.Extras;

public class Generator implements Listener{
	@EventHandler
	public void gen(BlockFromToEvent e) {
		Material mat = e.getBlock().getType();
		boolean lava=false;
		boolean water=false;
		for(BlockFace face : BlockFace.values()) {
			if(e.getToBlock().getRelative(face).getType()==Material.WATER) {
				water=true;
			}
			if(e.getToBlock().getRelative(face).getType()==Material.LAVA) {
				lava=true;
			}
		}
		if(mat==Material.WATER||mat==Material.LAVA) {
			if(lava&&water) {
				e.setCancelled(true);
				e.getToBlock().setType(Extras.Generator.getGen().next());
			}
//			else {
//				e.getBlock().setType(mat);
//			}
		}
	}
}
