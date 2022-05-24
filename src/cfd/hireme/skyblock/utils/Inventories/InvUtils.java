package cfd.hireme.skyblock.utils.Inventories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;

public class InvUtils {
	public static ItemStack createItem(Material item1, boolean enchanted, boolean toggleable, String title, List<String> loree) {
		ItemStack item = new ItemStack(item1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET+title);
		ArrayList<String> lore = new ArrayList<String>();
		for(String string : loree) {
			lore.add(ChatColor.RESET+string);
		}
		lore.add("");
		if(enchanted) {
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			if(toggleable) {
				lore.add(ChatColor.GREEN+"Enabled");
			}
		}else {
			if(toggleable) {
				lore.add(ChatColor.RED+"Disabled");
			}
		}
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createItem(ItemStack item1, boolean enchanted, boolean toggleable, String title, List<String> loree) {
		ItemStack item = item1;
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET+title);
		ArrayList<String> lore = new ArrayList<String>();
		for(String string : loree) {
			lore.add(ChatColor.RESET+string);
		}
		lore.add("");
		if(enchanted) {
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			if(toggleable) {
				lore.add(ChatColor.GREEN+"Enabled");
			}
		}else {
			if(toggleable) {
				lore.add(ChatColor.RED+"Disabled");
			}
		}
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createHead(OfflinePlayer p, boolean enchanted, boolean toggleable, String title, List<String> loree) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(p);
		item.setItemMeta(meta);
		return createItem(item, enchanted, toggleable, title, loree);
	}
	public static ItemStack getPrevious() {
		ItemStack item = new ItemStack(Material.BARRIER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET+"Back");
		meta.setLore(Arrays.asList("Go to the", "previous menu"));
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack getConfirm() {
		ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET+"Confirm");
		meta.setLore(Arrays.asList(ChatColor.GOLD+"This cannot be undone"));
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack getDeny() {
		ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET+"Deny");
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack getBlank(@Nullable Material mat) {
		if(mat==null) {
			mat=Material.BLACK_STAINED_GLASS_PANE;
		}
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		return item;
	}
	public static void fillBlanks(Inventory inv) {
		int i=0;
		for(ItemStack item:inv.getContents()) {
			if(item==null) {
				inv.setItem(i, getBlank(null));
			}
			i++;
		}
	}
}
