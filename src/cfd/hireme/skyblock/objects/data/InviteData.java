package cfd.hireme.skyblock.objects.data;

import java.util.UUID;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.enums.InvitationType;
import cfd.hireme.skyblock.objects.main.Island;
import cfd.hireme.skyblock.objects.main.Islander;
import cfd.hireme.skyblock.utils.Console;

public class InviteData {
	Island island;
	Islander islander;
	UUID userid;
	UUID islandid;
	InvitationType type;
	public InviteData(UUID userId, UUID islandId, InvitationType type) {
		this.type=type;
		parseData(Island.getIsland(islandId), Islander.getUser(userId));
	}
	private void parseData(Island island, Islander islander) {
		this.islander=islander;
		this.island=island;
		this.islandid=island.getId();
		this.userid=islander.getUniqueId();

	}
	public InvitationType getInvitationType() {return this.type;}
	public void setInvitationType(InvitationType type) {this.type=type;}
	public Islander getIslander() {return this.islander;}
	public Island getIsland() {return this.island;}
	public UUID getUserId() {return this.userid;}
	public UUID getIslandId() {return this.islandid;}
	public boolean isValid() {
		if(this.type==InvitationType.ISLAND) {
			return !(island.getMemberCount()>=island.getMaxMemberCount());
		}else if(this.type==InvitationType.PLAYER) {
			return !islander.hasIsland();
		}
		return false;
	}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
