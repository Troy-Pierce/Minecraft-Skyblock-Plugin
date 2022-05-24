/*
 *        derpySkyblock - Derpy00001 | Derpy#5247
 *        discord.gg/bQxBB89
 *        Hi! :)
 */

package cfd.hireme.skyblock.extra.economy;

import java.util.UUID;

import cfd.hireme.skyblock.Skyblock;

public class Economy {
	private static Skyblock plugin = Skyblock.getPlugin(Skyblock.class);
	public static void addBalance(UUID player,Double amount) {
		if(!plugin.getConfig().getBoolean("Primary.economy.ECONOMY_DISABLED")) {
			if(plugin.getConfig().getBoolean("Primary.economy.skyblock_economy")) {
				Object balance = Skyblock.manager.getIslanderSettings().get("islanders."+player.toString()+".balance");
				if(balance!=null) {
					Double bal = (Double) balance;
					bal = bal+amount;
					Skyblock.manager.getIslanderSettings().set("islanders."+player.toString()+".balance", bal);
				}else {
					Skyblock.manager.getIslanderSettings().set("islanders."+player.toString()+".balance", amount);
				}
				Skyblock.manager.saveIslanderSettings();
				return;
			}else if(plugin.getConfig().getBoolean("Primary.economy.essentials_economy")) {
				EssentialsEco.addEssentialsBalance(player, amount);
			}
		}
	}
	public static void removeBalance(UUID player, Double amount) {
		if(!plugin.getConfig().getBoolean("Primary.economy.ECONOMY_DISABLED")) {
			if(plugin.getConfig().getBoolean("Primary.economy.skyblock_economy")) {
				Object balance = Skyblock.manager.getIslanderSettings().get("islanders."+player.toString()+".balance");
				if(balance!=null) {
					Double bal = (Double) balance;
					bal = bal-amount;
					Skyblock.manager.getIslanderSettings().set("islanders."+player.toString()+".balance", bal);
				}else {
					Skyblock.manager.getIslanderSettings().set("islanders."+player.toString()+".balance", amount);
				}
				Skyblock.manager.saveIslanderSettings();
				return;
			}else if(plugin.getConfig().getBoolean("Primary.economy.essentials_economy")) {
				EssentialsEco.subtractEssentialsBalance(player, amount);
			}
		}
	}
	public static Double getBalance(UUID player) {
		if(!plugin.getConfig().getBoolean("Primary.economy.ECONOMY_DISABLED")) {
			if(plugin.getConfig().getBoolean("Primary.economy.skyblock_economy")) {
				Object balance = Skyblock.manager.getIslanderSettings().get("islanders."+player.toString()+".balance");
				if(balance!=null) {
					return (Double) balance;
				}else {
					Skyblock.manager.getIslanderSettings().set("islanders."+player.toString()+".balance", 0.0);
					Skyblock.manager.saveIslanderSettings();
					return 0.0;
				}
			}else if(plugin.getConfig().getBoolean("Primary.economy.essentials_economy")) {
				return EssentialsEco.getEssentialsBalance(player);
			}
		}
		return 0.0;
	}
}
