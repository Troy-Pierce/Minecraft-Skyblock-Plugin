package cfd.hireme.skyblock.extra.challenges;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.enums.ChallengeType;
import cfd.hireme.skyblock.enums.RewardType;
import cfd.hireme.skyblock.objects.data.challenges.ChallengeData;
import cfd.hireme.skyblock.objects.data.challenges.ChallengeMaterial;
import cfd.hireme.skyblock.objects.data.challenges.ChallengeMob;
import cfd.hireme.skyblock.utils.Console;

public class ChallengeManager {
	private static List<ChallengeData> data = new ArrayList<ChallengeData>(58);
	private static boolean setup=false;
	private static FileConfiguration config = Skyblock.getPlugin(Skyblock.class).getConfig();
	public static ChallengeData[] getChallenges() {
		return data.toArray(new ChallengeData[0]);
	}
	public static void addChallenge(ChallengeData challenge) {
		data.add(challenge);
	}
	public static void removeChallenge(ChallengeData challenge) {
		data.remove(challenge);
	}
	public static ChallengeData getChallenge(String name) {
		for(ChallengeData data:getChallenges()) {
			if(data.getName().equals(name)) {
				return data;
			}
		}
		return null;
	}
	@SuppressWarnings("deprecation")
	public static void setup() {
		if(!setup) {
			setup=true;
			Set<String> set =config.getConfigurationSection("Challenges").getKeys(false);
			List<ChallengeData> data = new ArrayList<ChallengeData>();
			Console.print("Reading Challenges");
			for(String string:set) {
				String path="Challenges."+string;
				Console.print("Found "+path);
				ChallengeType type = ChallengeType.getType(config.getString(path+".type"));
				RewardType rtype = RewardType.getType(config.getString(path+".reward_type"));
				if(type!=null&&rtype!=null) {
					switch(type) {
					case COLLECT:
						data.add(new ChallengeMaterial(config.getString(path+".name"), type, rtype, config.getInt(path+".amount"), config.get(path+".reward"), Material.getMaterial(config.getString(path+".material")),Material.getMaterial(config.getString(path+".display"))));
						break;
					case MINE:
						data.add(new ChallengeMaterial(config.getString(path+".name"), type, rtype, config.getInt(path+".amount"), config.get(path+".reward"), Material.getMaterial(config.getString(path+".material")),Material.getMaterial(config.getString(path+".display"))));
						break;
					case SLAY:
						data.add(new ChallengeMob(config.getString(path+".name"), type, rtype, config.getInt(path+".amount"), config.get(path+".reward"), EntityType.fromName(config.getString(path+".mob")),Material.getMaterial(config.getString(path+".display"))));
						break;
					case ISLAND_LEVEL:
						data.add(new ChallengeData(config.getString(path+".name"), type, rtype, config.getInt(path+".level"), config.get(path+".reward"), Material.getMaterial(config.getString(path+".display")), null, null));
						break;
					default:
						Console.error("INVALID CHALLENGE TYPE: Failed to parse challenge: "+path);
						break;
					
					}
				}
			}
			for(ChallengeData dataa:data) {
				if(dataa.getDisplay()==null) {
					data.remove(dataa);
					Console.error("INVALID DISPLAY: Failed to parse challenge named: "+dataa.getName());
				}else {
					if(dataa instanceof ChallengeMaterial) {
						if(((ChallengeMaterial) dataa).getMaterial()==null) {
							data.remove(dataa);
							Console.error("INVALID MATERIAL: Failed to parse challenge named: "+dataa.getName());
						}
					}else if(dataa instanceof ChallengeMob) {
						if(((ChallengeMob) dataa).getMob()==null) {
							data.remove(dataa);
							Console.error("INVALID MOB: Failed to parse challenge named: "+dataa.getName());
						}
					}
				}
				if(getChallenges().length>0) {
					for(ChallengeData dataaa :getChallenges()) {
						if(dataaa.getName().equals(dataa.getName())) {
							data.remove(dataa);
							Console.error("ALREADY REGISTERED: Challenge name already registered: "+dataa.getName());
						}
					}
				}
				if(data.contains(dataa)) {
					addChallenge(dataa);
				}
			}
		}
	}
}
