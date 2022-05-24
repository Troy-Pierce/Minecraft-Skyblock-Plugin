/*
 *        derpySkyblock - Derpy00001 | Derpy#5247
 *        discord.gg/bQxBB89
 *        Hi! :)
 */

package cfd.hireme.skyblock.objects.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.enums.InvitationResult;
import cfd.hireme.skyblock.enums.InvitationType;
import cfd.hireme.skyblock.exceptions.IslandException;
import cfd.hireme.skyblock.exceptions.IslandInvitationException;
import cfd.hireme.skyblock.exceptions.UserAlreadyHasInvite;
import cfd.hireme.skyblock.exceptions.UserHasIslandException;
import cfd.hireme.skyblock.exceptions.UserHasNoIslandException;
import cfd.hireme.skyblock.exceptions.UserInvitationListException;
import cfd.hireme.skyblock.objects.data.InviteData;
import cfd.hireme.skyblock.utils.Console;
import net.md_5.bungee.api.ChatColor;

public class Islander {
	OfflinePlayer player;
	private static Map<UUID,Islander> islanders = new HashMap<UUID,Islander>();
	
	public Islander(OfflinePlayer p) {
		player=p;
	}
	public static Map<UUID,Islander> getRegistered(){
		return islanders;
	}
	public class Settings {
		public boolean isBuildBypass() {
			return Skyblock.manager.getIslanderSettings().getBoolean("islanders."+getUniqueId().toString()+".settings.buildbypass");
		}
		public boolean isEnterBypass() {
			return Skyblock.manager.getIslanderSettings().getBoolean("islanders."+getUniqueId().toString()+".settings.enterbypass");
		}
		public void setBuildBypass(boolean bool) {
			Skyblock.manager.getIslanderSettings().set("islanders."+getUniqueId().toString()+".settings.buildbypass", bool);
			Skyblock.manager.saveIslanderSettings();
		}
		public void setEnterBypass(boolean bool) {
			Skyblock.manager.getIslanderSettings().set("islanders."+getUniqueId().toString()+".settings.enterbypass", bool);
			Skyblock.manager.saveIslanderSettings();
		}
	}
	public Settings getSettings() {
		return new Settings();
	}
	public Island getIsland() throws UserHasNoIslandException {
		return getIsland(getUniqueId());
	}
	public String getIslandJoinDate() throws UserHasNoIslandException {
		if(hasIsland()) {
			return Skyblock.manager.getIslanderSettings().getString("islanders."+getUniqueId().toString()+".islanddate");
		}else {
			throw new UserHasNoIslandException("User does not have an island!");
		}
	}
	public boolean hasIsland() {
		return hasIsland(getUniqueId());
	}
	@SuppressWarnings("unchecked")
	public void unlockIsland(int id) {
		List<String> ids;
		if(Skyblock.manager.getIslanderSettings().getList("islanders."+getUniqueId().toString()+".islands")==null) {
			ids=new ArrayList<String>();
		}else {
			ids=(List<String>) Skyblock.manager.getIslanderSettings().getList("islanders."+getUniqueId().toString()+".islands");
		}
		if(!ids.contains(Integer.toString(id))) {
			ids.add(Integer.toString(id));
		}
		Skyblock.manager.getIslanderSettings().set("islanders."+getUniqueId().toString()+".islands", ids);
		Skyblock.manager.saveIslanderSettings();
	}
	public boolean isIslandOwner() throws UserHasNoIslandException {
		if(getIsland()==null) {
			return false;
		}else {
			return getIsland().getOwner().getUniqueId().toString().equals(getUniqueId().toString());
		}
	}
	public int getMaxInviteSlots() {
		return 7;
	}
	public String getName() {
		return player.getName();
	}
	public UUID getUniqueId() {
		return player.getUniqueId();
	}
//	@Deprecated
//	public Player getPlayer() {
//		return player.getPlayer();
//	}
	public OfflinePlayer getOfflinePlayer() {
		return player;
	}
	public boolean isMemberOf(Island island) {
		return isMemberOf(this, island);
	}
	@SuppressWarnings("unchecked")
	public List<InviteData> getInvites() throws UserHasIslandException{
		if(!hasIsland()) {
			List<String> invites = null;
			try {
				invites=(List<String>) Skyblock.manager.getIslanderSettings().getList("islanders."+getUniqueId().toString()+".invites");
			}catch(Exception e) {e.printStackTrace();}
			List<InviteData> list = new ArrayList<InviteData>();
			if(invites!=null&&invites.size()>0) {
				for(String string : invites) {
					list.add(new InviteData(getUniqueId(), UUID.fromString(string), InvitationType.ISLAND));
				}
			}
			return list;
		}else {
			throw new UserHasIslandException("Users with islands do not have invitations!");
		}
	}
	@SuppressWarnings("unchecked")
	public void sendInvite(Island island) throws UserHasIslandException, UserInvitationListException, IslandException, IslandInvitationException, UserAlreadyHasInvite {
		if(!hasIsland()) {
			if(!(getInvites().size()>=getMaxInviteSlots())) {
				if(!(island.getOutgoingRequests().size()>=island.getMaxOutgoingRequests())) {
					if(!(island.getMemberCount()>=island.getMaxMemberCount())) {
						for(InviteData data:island.getOutgoingRequests()) {
							if(data.getUserId().toString().equals(getUniqueId().toString())) {
								throw new UserAlreadyHasInvite("User already has an invite from this island!");
							}
						}
						List<String> userinvites=null;
						List<String> islandinvites=null;
						try {
							userinvites=(List<String>) Skyblock.manager.getIslanderSettings().getList("islanders."+getUniqueId().toString()+".invites");
						}catch(Exception e) {e.printStackTrace();}
						try {
							islandinvites=(List<String>) Skyblock.manager.getIslandSettings().getList("islands."+island.getId().toString()+".invites");
						}catch(Exception e) {e.printStackTrace();}
						if(userinvites==null) {
							userinvites=new ArrayList<String>();
						}
						if(islandinvites==null) {
							islandinvites=new ArrayList<String>();
						}
						userinvites.add(island.getId().toString());
						islandinvites.add(getUniqueId().toString());
						Skyblock.manager.getIslanderSettings().set("islanders."+getUniqueId().toString()+".invites", userinvites);
						Skyblock.manager.getIslandSettings().set("islands."+island.getId().toString()+".invites", islandinvites);
						Skyblock.manager.saveIslanderSettings();
						Skyblock.manager.saveIslandSettings();
						if(this.getOfflinePlayer().isOnline()) {
							this.getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+"You recieved an invite to "+island.getOwner().getName()+"'s Island!");
						}
						if(island.getOwner().getOfflinePlayer().isOnline()) {
							island.getOwner().getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+"Invite Sent!");
						}
					}else {
						throw new IslandException("Island's Member list is full!");
					}
				}else {
					throw new IslandInvitationException("Island's invitation list is full!");
				}
			}else {
				throw new UserInvitationListException("User's invitation list is full!");
			}
		}else {
			throw new UserHasIslandException("User already has an island!");
		}
	}
	@SuppressWarnings("unchecked")
	public void handleInvite(Island island, InvitationResult result, InviteData islandInviteData) throws UserHasIslandException, IslandInvitationException {
		if(!(islandInviteData==null)) {
			if(!(islandInviteData.getInvitationType()==InvitationType.ISLAND)) {
				throw new IslandInvitationException("Invalid Invitation Type!\nGiven: "+islandInviteData.getInvitationType().name()+"\nAccepted: "+InvitationType.ISLAND.name());
			}
			List<String> userinvites=null;
			List<String> islandinvites=null;
			try {
				userinvites=(List<String>) Skyblock.manager.getIslanderSettings().getList("islanders."+getUniqueId().toString()+".invites");
			}catch(Exception e) {e.printStackTrace();}
			try {
				islandinvites=(List<String>) Skyblock.manager.getIslandSettings().getList("islands."+island.getId().toString()+".invites");
			}catch(Exception e) {e.printStackTrace();}
			if(islandinvites!=null) {
				if(islandinvites.contains(getUniqueId().toString())) {
					islandinvites.remove(getUniqueId().toString());
					Skyblock.manager.saveIslandSettings();
				}
			}
			if(userinvites!=null) {
				if(userinvites.contains(island.getId().toString())) {
					userinvites.remove(island.getId().toString());
					Skyblock.manager.saveIslanderSettings();
				}
			}
		}
		switch(result){
		case FORCE:
			this.setIsland(island);
			for(Islander islander : island.getMembers()) {
				if(islander.getOfflinePlayer().isOnline()) {
					islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+getOfflinePlayer().getName()+" has forcefully joined your island!");
				}
			}
			if(getOfflinePlayer().isOnline()) {
				getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+"You have forcefully joined "+island.getOwner().getName()+"'s Island!");
				getOfflinePlayer().getPlayer().teleport(island.getWorld().getSpawnLocation());
			}
			for(InviteData data : this.getInvites()) {
				handleInvite(data.getIsland(), InvitationResult.SILENT_REMOVED, data);
			}
			break;
		case ACCEPTED:
			if(islandInviteData!=null) {
				if(!islandInviteData.isValid()) {
					throw new IslandInvitationException("Invite is no longer valid!");
				}
			}
			this.setIsland(island);
			for(Islander islander : island.getMembers()) {
				if(islander.getOfflinePlayer().isOnline()) {
					islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+getOfflinePlayer().getName()+" has joined your island!");
				}
			}
			if(getOfflinePlayer().isOnline()) {
				getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+"You have joined "+island.getOwner().getName()+"'s Island!");
				getOfflinePlayer().getPlayer().teleport(island.getWorld().getSpawnLocation());
			}
			for(InviteData data : this.getInvites()) {
				handleInvite(data.getIsland(), InvitationResult.SILENT_REMOVED, data);
			}
			break;
		case ACCEPTED_SILENT:
			if(islandInviteData!=null) {
				if(!islandInviteData.isValid()) {
					throw new IslandInvitationException("Invite is no longer valid!");
				}
			}
			this.setIsland(island);
			for(InviteData data : this.getInvites()) {
				handleInvite(data.getIsland(), InvitationResult.SILENT_REMOVED, data);
			}
			break;
		case DENIED:
			for(Islander islander : island.getMembers()) {
				if(islander.getOfflinePlayer().isOnline()) {
					islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+this.getName()+" has denied your invitation!");
				}
			}
			if(this.getOfflinePlayer().isOnline()) {
				this.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"You have denied "+island.getOwner().getName()+"'s Invitation!");
			}
			break;
		case FORCE_DENY:
			for(Islander islander : island.getMembers()) {
				if(islander.getOfflinePlayer().isOnline()) {
					islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+getName()+"'s invitation has been forcefully denied!");
				}
			}
			if(getOfflinePlayer().isOnline()) {
				getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"Your invitation to "+island.getOwner().getName()+"'s Island has been forcefully denied!");
			}
			break;
		case FORCE_REVOKE:
			for(Islander islander : island.getMembers()) {
				if(islander.getOfflinePlayer().isOnline()) {
					islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+getName()+"'s invitation has been forcefully revoked!");
				}
			}
			if(getOfflinePlayer().isOnline()) {
				getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"Your invitation to "+island.getOwner().getName()+"'s Island has been forcefully revoked!");
			}
			break;
		case REVOKED:
			for(Islander islander : island.getMembers()) {
				if(islander.getOfflinePlayer().isOnline()) {
					islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+getName()+"'s invitation has been revoked!");
				}
			}
			if(getOfflinePlayer().isOnline()) {
				getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"Your invitation to "+island.getOwner().getName()+"'s Island has been revoked!");
			}
			break;
		case SILENT_REMOVED:
			break;
		default:
			break;
		}
	}
	@SuppressWarnings("unchecked")
	public void setIsland(Island island) {
		Island previsland;
		try {
			previsland=getIsland();
		} catch (UserHasNoIslandException e) {
			// TODO Auto-generated catch block
			previsland=null;
		}
		if(previsland!=null) {
			List<String> uuids = (List<String>) Skyblock.manager.getIslandSettings().getList("islands."+previsland.getId().toString()+".members");
			uuids.remove(getUniqueId().toString());
			Skyblock.manager.getIslandSettings().set("islands."+previsland.getId().toString()+".members", uuids);
		}
		if(island!=null) {
			List<String> uuids = (List<String>) Skyblock.manager.getIslandSettings().getList("islands."+island.getId().toString()+".members");
			uuids.add(getUniqueId().toString());
			Skyblock.manager.getIslandSettings().set("islands."+island.getId().toString()+".members", uuids);
			Skyblock.manager.getIslanderSettings().set("islanders."+getUniqueId().toString()+".island", island.getWorld().getName());
			Skyblock.manager.getIslanderSettings().set("islanders."+getUniqueId().toString()+".islanddate", new Date().toString());
		}else {
			Skyblock.manager.getIslanderSettings().set("islanders."+getUniqueId().toString()+".island", null);
		}
		
		Skyblock.manager.saveIslanderSettings();
		if(island!=null||previsland!=null) {
			Skyblock.manager.saveIslandSettings();
		}
	}
	public static boolean isMemberOf(Islander islander, Island island) {
		for(Islander islande : island.getMembers()) {
			if(islande.getUniqueId().toString().equals(islander.getUniqueId().toString())) {
				return true;
			}
		}
		return false;
	}
	public static Islander getUser(UUID uuid) {
		if(getRegistered().containsKey(uuid)) {
			return getRegistered().get(uuid);
		}else {
			getRegistered().put(uuid, new Islander(Bukkit.getServer().getOfflinePlayer(uuid)));
			return getRegistered().get(uuid);
		}
	}
	public static Islander getUser(OfflinePlayer p) {
		if(getRegistered().containsKey(p.getUniqueId())) {
			return getRegistered().get(p.getUniqueId());
		}else {
			getRegistered().put(p.getUniqueId(), new Islander(p));
			return getRegistered().get(p.getUniqueId());
		}
	}
	public static Islander getUser(Player p) {
		if(getRegistered().containsKey(p.getUniqueId())) {
			return getRegistered().get(p.getUniqueId());
		}else {
			getRegistered().put(p.getUniqueId(), new Islander(p));
			return getRegistered().get(p.getUniqueId());
		}
	}
	public static boolean hasIsland(UUID uuid) {
		if(Skyblock.manager.getIslanderSettings().contains("islanders."+uuid.toString())) {
			if(!(Skyblock.manager.getIslanderSettings().getString("islanders."+uuid.toString()+".island")==null)) {
				return true;
			}
		}
		return false;
	}
	public static Island getIsland(UUID uuid) throws UserHasNoIslandException {
		if(hasIsland(uuid)) {
			Island island = null;
			try {
				String isname = Skyblock.manager.getIslanderSettings().getString("islanders."+uuid.toString()+".island");
				island = Island.getIsland(isname);
			}catch(Exception e) {e.printStackTrace();}
			if(island==null) {
				throw new UserHasNoIslandException("User does not have an island!");
			}else {
				return island;
			}
		}else {
			return null;
		}
	}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
