/*
 *        derpySkyblock - Derpy00001 | Derpy#5247
 *        discord.gg/bQxBB89
 *        Hi! :)
 */

package cfd.hireme.skyblock.objects.main;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.creators.IslandLoader;
import cfd.hireme.skyblock.enums.InvitationResult;
import cfd.hireme.skyblock.enums.InvitationType;
import cfd.hireme.skyblock.enums.UpgradeType;
import cfd.hireme.skyblock.exceptions.IslandException;
import cfd.hireme.skyblock.exceptions.IslandInvitationException;
import cfd.hireme.skyblock.exceptions.UserAlreadyHasInvite;
import cfd.hireme.skyblock.exceptions.UserHasIslandException;
import cfd.hireme.skyblock.exceptions.UserInvitationListException;
import cfd.hireme.skyblock.extra.challenges.ChallengeManager;
import cfd.hireme.skyblock.objects.Threads;
import cfd.hireme.skyblock.objects.data.InviteData;
import cfd.hireme.skyblock.objects.data.UpgradeData;
import cfd.hireme.skyblock.objects.data.challenges.ChallengeData;
import cfd.hireme.skyblock.objects.data.challenges.IslandChallenge;
import cfd.hireme.skyblock.objects.runnables.CalcRunnable;
import cfd.hireme.skyblock.utils.Console;

public class Island {	
	UUID id;
	String name;
	World world;
	long lastCalc=0L;
	private static Map<UUID,Island> islands = new HashMap<UUID,Island>();
	private static int defaultMemCount = Skyblock.getPlugin(Skyblock.class).getConfig().getInt("island_settings.max_members");
	private static int defaultVisCount = Skyblock.getPlugin(Skyblock.class).getConfig().getInt("island_settings.max_visitors");
	public Island(UUID islandId) {
		this.id=islandId;
		this.name="Island_"+islandId.toString();
		this.world=IslandLoader.load(this.name);
	}
	public static Map<UUID,Island> getRegistered(){
		return islands;
	}
	public static Island getIsland(UUID islandId) {
		if(islandId==null) {
			Console.error("Null");
		}
		if(getRegistered().containsKey(islandId)) {
			return getRegistered().get(islandId);
		}else {
			getRegistered().put(islandId, new Island(islandId));
			return getRegistered().get(islandId);
		}
	}
	public static Island getIsland(String worldname) {
		String[] split = worldname.split("_");
		UUID islandId = UUID.fromString(split[1]);
		if(getRegistered().containsKey(islandId)) {
			return getRegistered().get(islandId);
		}else {
			getRegistered().put(islandId, new Island(islandId));
			return getRegistered().get(islandId);
		}
	}
	public String getWelcome() {
		return Skyblock.manager.getIslandSettings().getString("islands."+id.toString()+".welcomemsg");
	}
	public void setWelcome(String string) {
		Skyblock.manager.getIslandSettings().set("islands."+id.toString()+".welcomemsg", string);
		Skyblock.manager.saveIslandSettings();
	}
	public int getLevel() {
		return Skyblock.manager.getIslandSettings().getInt("islands."+id.toString()+".level");
	}
	public void setLevel(int i) {
		Skyblock.manager.getIslandSettings().set("islands."+id.toString()+".level", i);
		Skyblock.manager.saveIslandSettings();
	}
	public void resetChallengeProgress() {
		List<IslandChallenge> isc = getChallenges();
		for(IslandChallenge data:isc) {
			data.setProgress(0);
			if(data.isClaimed()) {
				data.setClaimed(false);
			}
		}
	}
	public List<UpgradeData> getUpgrades(){
		List<UpgradeData> data = new ArrayList<UpgradeData>();
		for(UpgradeType type : UpgradeType.values()) {
			data.add(new UpgradeData(this, type));
		}
		return data;
	}
	public List<IslandChallenge> getChallenges(){
		List<IslandChallenge> isc = new ArrayList<IslandChallenge>();
		boolean created=false;
		for(ChallengeData data : ChallengeManager.getChallenges()) {
			String path = "islands."+id.toString()+".challenges."+data.getName();
			if(!Skyblock.manager.getIslandSettings().contains("islands."+id.toString()+".challenges")) {
				Skyblock.manager.getIslandSettings().createSection("islands."+id.toString()+".challenges");
				created=true;
			}
			if(!Skyblock.manager.getIslandSettings().contains(path)) {
				created=true;
				Skyblock.manager.getIslandSettings().set(path+".name", data.getName());
				Skyblock.manager.getIslandSettings().set(path+".progress", 0);
				Skyblock.manager.getIslandSettings().set(path+".claimed", false);
				
			}
			isc.add(IslandChallenge.create(data, this));
		}
		// MAKE INV TO SHOW CHALLENGES
		// MAKE LISTENERS
		if(created) {
			Skyblock.manager.saveIslandSettings();
		}
		return isc;
	}
	@SuppressWarnings("unchecked")
	public List<InviteData> getOutgoingRequests(){
		List<String> obj = (List<String>) Skyblock.manager.getIslandSettings().getList("islands."+id.toString()+".invites");
		List<InviteData> data = new ArrayList<InviteData>();
		if(obj!=null) {
			if(obj.size()>0) {
				for(String string:obj) {
					data.add(new InviteData(UUID.fromString(string),getId(), InvitationType.PLAYER));
				}
			}
		}
		return data;
	}
	public settings getSettings() {
		return new settings();
	}
	public class settings{
		public boolean isPublic() {
			return Skyblock.manager.getIslandSettings().getBoolean("islands."+id.toString()+".settings.public");
		}
		public boolean canPvp() {
			return Skyblock.manager.getIslandSettings().getBoolean("islands."+id.toString()+".settings.pvp");
		}
		public boolean canEdit() {
			return Skyblock.manager.getIslandSettings().getBoolean("islands."+id.toString()+".settings.edit");
		}
		public boolean canPickup() {
			return Skyblock.manager.getIslandSettings().getBoolean("islands."+id.toString()+".settings.pickup");
		}
		public void setPickup(boolean b) {
			Skyblock.manager.getIslandSettings().set("islands."+id.toString()+".settings.pickup", b);
			Skyblock.manager.saveIslandSettings();
		}
		public void setPublic(boolean bool) {
			Skyblock.manager.getIslandSettings().set("islands."+id.toString()+".settings.public", bool);
			Skyblock.manager.saveIslandSettings();
			if(!bool) {
				for(Islander islander:getVisitors()) {
					islander.getOfflinePlayer().getPlayer().teleport(Skyblock.getSpawn().getSpawnLocation(), TeleportCause.PLUGIN);
				}
			}
		}
		public void setPvp(boolean bool) {
			Skyblock.manager.getIslandSettings().set("islands."+id.toString()+".settings.pvp", bool);
			Skyblock.manager.saveIslandSettings();
		}
		public void setEdit(boolean bool) {
			Skyblock.manager.getIslandSettings().set("islands."+id.toString()+".settings.edit", bool);
			Skyblock.manager.saveIslandSettings();
		}
	}
	public List<Islander> getMembers(){
		List<Islander> members = new ArrayList<Islander>();
		@SuppressWarnings("unchecked")
		List<String> uuids = (List<String>) Skyblock.manager.getIslandSettings().getList("islands."+id.toString()+".members");
		for(String string : uuids) {
			members.add(Islander.getUser(UUID.fromString(string)));
		}
		return members;
	}
	public List<Islander> getVisitors(){
		List<Islander> visitors = new ArrayList<Islander>();
		for(Player p : getWorld().getPlayers()) {
			Islander user = Islander.getUser(p);
			if(!user.isMemberOf(this)) {
				visitors.add(user);
			}
		}
		return visitors;
	}
	public Location getBorderCenter() {
		int x = Skyblock.manager.getIslandSettings().getInt("islands."+getId().toString()+".spawn.x");
		int y = Skyblock.manager.getIslandSettings().getInt("islands."+getId().toString()+".spawn.y");
		int z = Skyblock.manager.getIslandSettings().getInt("islands."+getId().toString()+".spawn.z");
		return new Location(this.getWorld(), x, y, z);
	}
	public int getBorderLevel() {
		return Skyblock.manager.getIslandSettings().getInt("islands."+getId().toString()+".upgrades.border");
	}
	public void setBorderLevel(int i) {
		Skyblock.manager.getIslandSettings().set("islands."+getId().toString()+".upgrades.border", i);
		Skyblock.manager.saveIslandSettings();
		int size = (getBorderLevel()-1)*10;
		getWorld().getWorldBorder().setSize(30+size, 10);
	}
	public int getVisitorCount() {
		return getVisitors().size();
	}
	public int getMaxOutgoingRequests() {
		return 7;
	}
	public int getVisitorLevel() {
		return Skyblock.manager.getIslandSettings().getInt("islands."+getId().toString()+".upgrades.visitor");
	}
	public void setVisitorLevel(int i) {
		Skyblock.manager.getIslandSettings().set("islands."+getId().toString()+".upgrades.visitor", i);
		Skyblock.manager.saveIslandSettings();
	}
	public int getMaxVisitors() {
		int addto = getVisitorLevel()-1;
		return defaultVisCount+addto;
	}
	public int getMemberLevel() {
		return Skyblock.manager.getIslandSettings().getInt("islands."+getId().toString()+".upgrades.member");
	}
	public void setMemberLevel(int i) {
		Skyblock.manager.getIslandSettings().set("islands."+getId().toString()+".upgrades.member", i);
		Skyblock.manager.saveIslandSettings();
	}
	public int getMemberCount() {
		return Skyblock.manager.getIslandSettings().getList("islands."+id.toString()+".members").size();
	}
	public int getMaxMemberCount() {
		int addto = getMemberLevel()-1;
		return defaultMemCount+addto;
	}
	public long getLastCalculationInSeconds() {
		return this.lastCalc;
	}
	public void calculateLevel() {
		this.lastCalc=Instant.now().getEpochSecond();
//		Bukkit.getScheduler().runTaskAsynchronously(Skyblock.getPlugin(Skyblock.class), new CalcRunnable(this))
		Thread thread = Threads.createThread(new CalcRunnable(this));
		thread.start();
	}
	public void sendInvite(Islander islander) throws UserHasIslandException, UserInvitationListException, IslandInvitationException, IslandException, UserAlreadyHasInvite {
		islander.sendInvite(this);
	}
	public void removeInvite(Islander islander, InvitationResult result, @Nullable InviteData islandInviteData) throws UserHasIslandException, IslandInvitationException {
		islander.handleInvite(this, result, islandInviteData);
	}
	public void addIslander(Islander islander) {
		islander.setIsland(this);
	}
	public void removeIslander(Islander islander) {
		islander.setIsland(null);
	}
	public Object getCreationDate() {
		return Skyblock.manager.getIslandSettings().get("islands."+id.toString()+".creationdate");
	}
	public String getType() {
		return Skyblock.manager.getIslandSettings().getString("islands."+id.toString()+".islandtype");
	}
	public Islander getOwner() {
		return Islander.getUser(UUID.fromString(Skyblock.manager.getIslandSettings().getString("islands."+id.toString()+".owner")));
	}
	public World getWorld() {
		return this.world;
	}
	public UUID getId() {
		return this.id;
	}
	public String getName() {
		return this.name;
	}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
