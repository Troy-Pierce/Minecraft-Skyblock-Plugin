package cfd.hireme.skyblock.objects.holders;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.enums.IslanderType;
import cfd.hireme.skyblock.utils.Console;

public class GuiHolderUser implements InventoryHolder{
	//Custom holder to tag inventories as a skyblock inventory
	IslanderType type;
	public GuiHolderUser(IslanderType type) {
		this.type=type;
	}
	public IslanderType getType() {
		return this.type;
	}
	@Override
	public Inventory getInventory() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
