package cfd.hireme.skyblock.objects.data.challenges;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.objects.main.Island;
import cfd.hireme.skyblock.utils.Console;

public class IslandChallenge {
	ChallengeData data;
	int progress;
	Island island;
	boolean claimed;
	public IslandChallenge(ChallengeData data, Island island) {
		this.data=data;
		this.island=island;
		this.progress=Skyblock.manager.getIslandSettings().getInt("islands."+island.getId().toString()+".challenges."+data.getName()+".progress");
		this.claimed=Skyblock.manager.getIslandSettings().getBoolean("islands."+this.island.getId().toString()+".challenges."+data.getName()+".claimed");
	}
	public ChallengeData getData() {return this.data;}
	public boolean isClaimed() {return this.claimed;}
	public void setClaimed(boolean b) {
		Skyblock.manager.getIslandSettings().set("islands."+this.island.getId().toString()+".challenges."+this.data.getName()+".claimed", b);
		Skyblock.manager.saveIslandSettings();
		this.claimed=b;
	}
	public int getProgress() {return this.progress;}
	public void setProgress(int i) {
		Skyblock.manager.getIslandSettings().set("islands."+this.island.getId().toString()+".challenges."+this.data.getName()+".progress", i);
		Skyblock.manager.saveIslandSettings();
		this.progress=i;
	}
	public boolean isCompleted() {
		if(this.progress>=this.data.getRequirement()) {
			return true;
		}else {
			return false;
		}
	}
	public static IslandChallenge create(ChallengeData data, Island island) {
		return new IslandChallenge(data, island);
	}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
