package cfd.hireme.skyblock.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.objects.main.Island;
import cfd.hireme.skyblock.utils.Console;

public class IslandLevelChangeEvent extends Event{
	private int before;
	private int after;
	private Island island;
	private static final HandlerList handlers = new HandlerList();
	public IslandLevelChangeEvent(boolean isAsync, Island island, int before, int after) {
		super(isAsync);
		this.before=before;
		this.after=after;
		this.island=island;
		
	}
	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}
	public int getOldLevel() {
		return this.before;
	}
	public int getNewLevel() {
		return this.after;
	}
	public Island getIsland() {
		return this.island;
	}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
