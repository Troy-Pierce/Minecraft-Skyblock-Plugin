package cfd.hireme.skyblock.extra.scoreboard;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.objects.main.Islander;


public class BoardManager {
	private static Map<Islander,Board> boards = new HashMap<Islander,Board>();
	private static boolean enabled=false;
	public static Map<Islander,Board> getBoards(){
		return boards;
	}
	public static void removeBoard(Islander islander) {
		getBoards().get(islander).clearBoard();
		getBoards().remove(islander);
	}
	public static void clearBoards() {
		for(Board board:getBoards().values()) {
			board.clearBoard();
			getBoards().remove(board.getIslander());
			board=null;
		}
		
	}
	public static void startLoop() {
		if(!enabled) {
			enabled=true;
			new BukkitRunnable(){
				@Override
				public void run(){
					for(Board board:getBoards().values()) {
						board.updateBoard();
					}
				}
			}.runTaskTimer(Skyblock.getPlugin(Skyblock.class), 20*10, 20*5);
		}
	}
}
