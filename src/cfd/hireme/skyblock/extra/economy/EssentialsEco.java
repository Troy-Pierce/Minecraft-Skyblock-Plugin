package cfd.hireme.skyblock.extra.economy;

import java.util.UUID;

import org.bukkit.Bukkit;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

public class EssentialsEco {
	@SuppressWarnings("deprecation")
	public static Double getEssentialsBalance(UUID player) {
		if(com.earth2me.essentials.api.Economy.playerExists(Bukkit.getServer().getOfflinePlayer(player).getName())) {
			try {
				return com.earth2me.essentials.api.Economy.getMoney(Bukkit.getServer().getOfflinePlayer(player).getName());
			} catch (UserDoesNotExistException e) {
				// TODO Auto-generated catch block
				return 0.0;
			}
		}
		return 0.0;
	}
	@SuppressWarnings("deprecation")
	public static void subtractEssentialsBalance(UUID player, Double amount) {
		if(com.earth2me.essentials.api.Economy.playerExists(Bukkit.getServer().getOfflinePlayer(player).getName())) {
			try {
				com.earth2me.essentials.api.Economy.subtract(Bukkit.getServer().getOfflinePlayer(player).getName(), amount);
			} catch (NoLoanPermittedException | UserDoesNotExistException e) {
				// TODO Auto-generated catch block
			}
		}else {
			try {
				com.earth2me.essentials.api.Economy.setMoney(Bukkit.getServer().getOfflinePlayer(player).getName(), -amount);
			} catch (NoLoanPermittedException | UserDoesNotExistException e) {
				// TODO Auto-generated catch block
			}
		}

	}
	@SuppressWarnings("deprecation")
	public static void addEssentialsBalance(UUID player, Double amount) {
		if(com.earth2me.essentials.api.Economy.playerExists(Bukkit.getServer().getOfflinePlayer(player).getName())) {
			try {
				com.earth2me.essentials.api.Economy.add(Bukkit.getServer().getOfflinePlayer(player).getName(), amount);
			} catch (NoLoanPermittedException | UserDoesNotExistException e) {
				// TODO Auto-generated catch block
			}
		}else {
			try {
				com.earth2me.essentials.api.Economy.setMoney(Bukkit.getServer().getOfflinePlayer(player).getName(), amount);
			} catch (NoLoanPermittedException | UserDoesNotExistException e) {
				// TODO Auto-generated catch block
			}
		}
	}
}
