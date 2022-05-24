package cfd.hireme.skyblock.listeners;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import cfd.hireme.skyblock.creators.IslandLoader;
import cfd.hireme.skyblock.exceptions.UserHasNoIslandException;
import cfd.hireme.skyblock.extra.Extras;
import cfd.hireme.skyblock.extra.scoreboard.Board;
import cfd.hireme.skyblock.extra.scoreboard.BoardManager;
import cfd.hireme.skyblock.objects.Permissions;
import cfd.hireme.skyblock.objects.main.Islander;
import cfd.hireme.skyblock.utils.Console;
public class UserRegister implements Listener{
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		OfflinePlayer p = e.getPlayer();
		if(p!=null) {
			Islander islander = Islander.getUser(p);
			if(Islander.getUser(p).hasIsland()) {
				try {
					IslandLoader.load(islander.getIsland().getName());
				} catch (UserHasNoIslandException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(islander.getSettings().isBuildBypass()) {
				if(!islander.getOfflinePlayer().getPlayer().hasPermission(Permissions.Operator.islandBuildBypass)) {
					islander.getSettings().setBuildBypass(false);
					Console.print(islander.getName()+"'s joined with Build bypass enabled without permissions, this has been disabled");
				}
			}
			if(islander.getSettings().isEnterBypass()) {
				if(!islander.getOfflinePlayer().getPlayer().hasPermission(Permissions.Operator.islandEnterBypass)) {
					islander.getSettings().setEnterBypass(false);
					Console.print(islander.getName()+"'s joined with Enter bypass enabled without permissions, this has been disabled");
				}
			}
		}
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(Extras.Scoreboard.boardEnabled()) {
			Board.createBoard(Islander.getUser(e.getPlayer()));
		}
	}
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if(BoardManager.getBoards().containsKey(Islander.getUser(e.getPlayer()))) {
			BoardManager.removeBoard(Islander.getUser(e.getPlayer()));
		}
	}
}
