package cfd.hireme.skyblock.extra.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.exceptions.UserHasNoIslandException;
import cfd.hireme.skyblock.extra.Extras;
import cfd.hireme.skyblock.extra.economy.Economy;
import cfd.hireme.skyblock.objects.main.Island;
import cfd.hireme.skyblock.objects.main.Islander;
import cfd.hireme.skyblock.utils.Console;
import cfd.hireme.skyblock.utils.NumFormat;

public class Board {
	Islander islander;
	ScoreboardManager manager = Bukkit.getScoreboardManager();
	FileConfiguration config = Skyblock.getPlugin(Skyblock.class).getConfig();
	Scoreboard board;
	Objective obj;
	public Board(Islander islander) {
		this.islander=islander;
		this.board=this.manager.getNewScoreboard();
		this.obj = board.registerNewObjective("header", "dummy", ChatColor.translateAlternateColorCodes('&', Extras.Scoreboard.getHeader()));
		this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		updateBoard();
	}
	public void clearBoard() {
		for(String string:board.getEntries()) {
			board.resetScores(string);
		}
	}
	public Islander getIslander() {
		return this.islander;
	}
	public Scoreboard getBoard() {
		return this.board;
	}
	public static Board createBoard(Islander islander) {
		Board board = new Board(islander);
		BoardManager.getBoards().put(islander,board);
		islander.getOfflinePlayer().getPlayer().setScoreboard(board.getBoard());
		return board;
	}
	public void updateBoard() {
		clearBoard();
		for(String string : config.getStringList("Primary.scoreboard.player_scores")) {
			int num = Integer.parseInt(string.split("=")[1]);
			String text = string.split("=")[0];
			this.obj.getScore(convert(text, islander)).setScore(num);
		}
		if(islander.hasIsland()) {
			for(String string:config.getStringList("Primary.scoreboard.island_scores")) {
				int num = Integer.parseInt(string.split("=")[1]);
				String text = string.split("=")[0];
				this.obj.getScore(convert(text, islander)).setScore(num);
			}
		}else {
			for(String string:config.getStringList("Primary.scoreboard.no_island_scores")) {
				int num = Integer.parseInt(string.split("=")[1]);
				String text = string.split("=")[0];
				this.obj.getScore(convert(text, islander)).setScore(num);
			}
		}
		if(Extras.Scoreboard.showIpEnabled()) {
			for(String string:config.getStringList("Primary.scoreboard.ip_score")) {
				int num = Integer.parseInt(string.split("=")[1]);
				String text = string.split("=")[0];
				this.obj.getScore(convert(text, islander)).setScore(num);
			}
		}
	}
	public static String convert(String stringe, Islander islander) {
		String string = stringe;
		Island island=null;
		try {
			island = islander.getIsland();
		} catch (UserHasNoIslandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		string=string.replace("{SERVER_IP}", Bukkit.getServer().getIp());
		if(island!=null) {
			string=string.replace("{MEMBER_COUNT}", Integer.toString(island.getMemberCount()));
			string=string.replace("{MAX_MEMBER_COUNT}", Integer.toString(island.getMaxMemberCount()));
			string=string.replace("{VISITOR_COUNT}", Integer.toString(island.getVisitorCount()));
			string=string.replace("{MAX_VISITOR_COUNT}", Integer.toString(island.getMaxVisitors()));
			string=string.replace("{ISLAND_LEVEL}", Integer.toString(island.getLevel()));
		}
		String name;
		if(islander.getName().length()>=38) {
			name=islander.getName().substring(0,37);
		}else {
			name=islander.getName();
		}
		string=string.replace("{PLAYER_NAME}", name);
		String symbol;
		if(Extras.Economy.getSymbol().length()>=38) {
			symbol=Extras.Economy.getSymbol().substring(0, 37);
		}else {
			symbol=Extras.Economy.getSymbol();
		}
		string=string.replace("{SYMBOL}", symbol);
		string=string.replace("{BALANCE}", NumFormat.format((Economy.getBalance(islander.getUniqueId()))));
		string=string.replace("{ONLINE}", Integer.toString(Bukkit.getServer().getOnlinePlayers().size()));
		string=ChatColor.translateAlternateColorCodes('&', string);
		return string;
		
	}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
