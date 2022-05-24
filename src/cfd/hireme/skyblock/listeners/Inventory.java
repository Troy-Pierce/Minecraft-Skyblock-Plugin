package cfd.hireme.skyblock.listeners;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.creators.IslandCreator;
import cfd.hireme.skyblock.creators.IslandLoader;
import cfd.hireme.skyblock.enums.InvitationResult;
import cfd.hireme.skyblock.enums.InvitationType;
import cfd.hireme.skyblock.enums.IslanderType;
import cfd.hireme.skyblock.enums.RewardType;
import cfd.hireme.skyblock.enums.UpgradeType;
import cfd.hireme.skyblock.exceptions.IslandException;
import cfd.hireme.skyblock.exceptions.IslandInvitationException;
import cfd.hireme.skyblock.exceptions.UserAlreadyHasInvite;
import cfd.hireme.skyblock.exceptions.UserHasIslandException;
import cfd.hireme.skyblock.exceptions.UserHasNoIslandException;
import cfd.hireme.skyblock.exceptions.UserInvitationListException;
import cfd.hireme.skyblock.extra.Extras;
import cfd.hireme.skyblock.extra.economy.Economy;
import cfd.hireme.skyblock.objects.Permissions;
import cfd.hireme.skyblock.objects.data.InviteData;
import cfd.hireme.skyblock.objects.data.IslandData;
import cfd.hireme.skyblock.objects.data.ShopData;
import cfd.hireme.skyblock.objects.data.UpgradeData;
import cfd.hireme.skyblock.objects.data.challenges.IslandChallenge;
import cfd.hireme.skyblock.objects.holders.GuiHolder;
import cfd.hireme.skyblock.objects.holders.GuiHolderUser;
import cfd.hireme.skyblock.objects.holders.GuiHolderUserInv;
import cfd.hireme.skyblock.objects.holders.GuiItems;
import cfd.hireme.skyblock.objects.holders.GuiSections;
import cfd.hireme.skyblock.objects.main.Islander;
import cfd.hireme.skyblock.utils.Inventories.InvUtils;
import cfd.hireme.skyblock.utils.Inventories.Inventories;
import net.md_5.bungee.api.ChatColor;

public class Inventory implements Listener{
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getInventory().getHolder() instanceof GuiHolder) {
			if(!(event.getCurrentItem()==null)) {
				event.setCancelled(true);
				if(!(event.getCurrentItem().getType()==Material.BLACK_STAINED_GLASS_PANE)) {
					Islander islander = Islander.getUser((Player)event.getWhoClicked());
					switch(event.getView().getTitle()) {
					case "Island Creation":
						if(!(event.getCurrentItem().getType()==Material.BARRIER)) {
							for(String string : event.getCurrentItem().getItemMeta().getLore()) {
								if(string.contains("Id:")) {
									String[] split = string.split(":");
									String newsplit = split[1].replace(" ", "");
									event.getView().close();
									islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+"Creating Island!");
									IslandCreator.createIsland(islander, Integer.parseInt(newsplit));;
									islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+"Island Created!");
									try {
										if(islander.getIsland().getWorld()!=null||!islander.getIsland().getWorld().getSpawnLocation().isWorldLoaded()) {
											islander.getOfflinePlayer().getPlayer().teleport(islander.getIsland().getWorld().getSpawnLocation());
										}else {
											IslandLoader.load(islander.getIsland().getName());
										}
									} catch (UserHasNoIslandException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}else {
							if(event.getCurrentItem().hasItemMeta()) {
								if(event.getCurrentItem().getItemMeta().hasLore()) {
									for(String string:event.getCurrentItem().getItemMeta().getLore()) {
										if(string.contains("Id:")) {
											IslandData data = IslandData.getFromId(Integer.parseInt(string.split(":")[1]));
											if(data.isBuyable()) {
												if(data.isLocked()) {
													if(islander.getOfflinePlayer().getPlayer().hasPermission("dskyblock.islands."+Integer.toString(data.getId()))) {
														if(Economy.getBalance(islander.getUniqueId())>=data.getCost()) {
															Economy.removeBalance(islander.getUniqueId(), data.getCost());
															islander.unlockIsland(data.getId());
															event.getView().close();
															islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getCreation(islander));
															break;
														}else {
															islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"You cannot afford this!");
															break;
														}
													}
												}else {
													if(Economy.getBalance(islander.getUniqueId())>=data.getCost()) {
														Economy.removeBalance(islander.getUniqueId(), data.getCost());
														islander.unlockIsland(data.getId());
														event.getView().close();
														islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getCreation(islander));
														break;
													}else {
														islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"You cannot afford this!");
														break;
													}
												}
											}
										}
									}
								}
							}
						}
						break;
					case "Skyblock":
						if(event.getCurrentItem().getType()==Material.OAK_SAPLING) {
							if(!islander.hasIsland()) {
								event.getView().close();
								islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getCreation(islander));
								break;
							}
						}else if(event.getCurrentItem().getType()==Material.PAPER) {
							if(islander.getOfflinePlayer().getPlayer().hasPermission(Permissions.Operator.guioperator)){
								event.getView().close();
								islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getOp(islander));
								break;
							}
						}else if(event.getCurrentItem().getType()==Material.WRITABLE_BOOK) {
							event.getView().close();
							islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getInvitations(islander));
							break;
						}
						break;
					case "Island":
						if(event.getCurrentItem().getType()==Material.GREEN_BED) {
							event.getView().close();
							try {
								if(islander.getIsland().getWorld()!=null||!islander.getIsland().getWorld().getSpawnLocation().isWorldLoaded()) {
									islander.getOfflinePlayer().getPlayer().teleport(islander.getIsland().getWorld().getSpawnLocation());
								}else {
									IslandLoader.load(islander.getIsland().getName());
								}
							} catch (UserHasNoIslandException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
						}else if(event.getCurrentItem().getType()==Material.REDSTONE) {
							event.getView().close();
							islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getSettings(islander));
							break;
							
						}else if(event.getCurrentItem().getType()==Material.DIAMOND) {
							event.getView().close();
							try {
								islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getChallenges(islander.getIsland()));
							} catch (UserHasNoIslandException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// WORK ON CHALLENGES AND CHALLENGE INV
							break;
						}else if(event.getCurrentItem().getType()==Material.WRITABLE_BOOK) {
							event.getView().close();
							islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getInvitations(islander));
							break;
						}else if(event.getCurrentItem().getType()==Material.BARRIER) {
							event.getView().close();
							try {
								if(islander.isIslandOwner()) {
									islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getConfirm(0));
									break;
								}else {
									islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getConfirm(1));
									break;
								}
							} catch (UserHasNoIslandException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else if(event.getCurrentItem().getType()==Material.PAPER) {
							if(islander.getOfflinePlayer().getPlayer().hasPermission(Permissions.Operator.guioperator)){
								event.getView().close();
								islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getOp(islander));
								break;
							}
						}else if(event.getCurrentItem().getType()==Material.EXPERIENCE_BOTTLE) {
							try {
								if(Instant.now().getEpochSecond()>islander.getIsland().getLastCalculationInSeconds()+60) {
									islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+"Calculating island level, this may take a while");
									islander.getIsland().calculateLevel();
								}else {
									islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"Please wait a full 60 seconds before calculating again");
								}
							} catch (UserHasNoIslandException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							event.getView().close();
							break;
						}else if(event.getCurrentItem().getType()==Material.GOLD_INGOT) {
							event.getView().close();
							try {
								islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getUpgrades(islander.getIsland()));
							} catch (UserHasNoIslandException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
						}else if(event.getCurrentItem().getType()==Material.NAME_TAG) {
							event.getView().close();
							islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getShopSections(islander));
							break;
						}
						break;
					case "Upgrades":
						UpgradeData upgradedata;
						try {
							upgradedata = new UpgradeData(islander.getIsland(),UpgradeType.getType(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())));
							if(!upgradedata.isMaxLevel()) {
								if(upgradedata.getIsland().getLevel()>=upgradedata.getLevelRequirement()) {
									if(Economy.getBalance(islander.getUniqueId())>=upgradedata.getCost()) {
										Economy.removeBalance(islander.getUniqueId(), upgradedata.getCost());
										switch(upgradedata.getType()) {
										case BORDER:
											islander.getIsland().setBorderLevel(upgradedata.getCurrentLevel()+1);
											event.getView().close();
											islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getUpgrades(islander.getIsland()));
											break;
										case MEMBER:
											islander.getIsland().setMemberLevel(upgradedata.getCurrentLevel()+1);
											event.getView().close();
											islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getUpgrades(islander.getIsland()));
											break;
										case VISITOR:
											islander.getIsland().setVisitorLevel(upgradedata.getCurrentLevel()+1);
											event.getView().close();
											islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getUpgrades(islander.getIsland()));
											break;
										}
									}
								}
							}
						} catch (UserHasNoIslandException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						
						break;
					case "Challenges":
						if(event.getCurrentItem().getItemMeta().hasEnchants()) {
							String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
							try {
								List<IslandChallenge> challenges = islander.getIsland().getChallenges();
								for(IslandChallenge data:challenges) {
									if(data.getData().getName().equals(name)) {
										if(data.isCompleted()) {
											if(!data.isClaimed()) {
												if(data.getData().getRewardType()==RewardType.MATERIAL) {
													String reward = (String) data.getData().getReward();
													Material mat = Material.getMaterial(reward.split(":")[0]);
													int amt = Integer.parseInt(reward.split(":")[1]);
													if(islander.getOfflinePlayer().getPlayer().getInventory().firstEmpty()!=-1){
														islander.getOfflinePlayer().getPlayer().getInventory().addItem(new ItemStack(mat, amt));
														data.setClaimed(true);
														islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+"Recieved "+Integer.toString(amt)+" "+ShopData.getProperName(mat.name())+"s");
														event.getView().close();
														islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getChallenges(islander.getIsland()));
													}else {
														islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"Your inventory is full!");
													}
												}else if(data.getData().getRewardType()==RewardType.MONEY) {
													double reward = (double) data.getData().getReward();
													Economy.addBalance(islander.getUniqueId(), reward);
													data.setClaimed(true);
													islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+"Recieved "+Extras.Economy.getSymbol()+Double.toString(reward));
													event.getView().close();
													islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getChallenges(islander.getIsland()));
												}
											}
										}
									}
								}
							} catch (UserHasNoIslandException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					case "Outgoing Invitations":
						if(event.getCurrentItem().getType()==Material.PAPER) {
							if(event.isRightClick()) {
								if(event.getCurrentItem().hasItemMeta()) {
									if(event.getCurrentItem().getItemMeta().hasLore()) {
										List<String> lore = event.getCurrentItem().getItemMeta().getLore();
										for(String string : lore) {
											if(string.contains("Invite Id:")) {
												String id = string.split(":")[1];
												InviteData data;
												try {
													data = new InviteData(UUID.fromString(id), islander.getIsland().getId(), InvitationType.ISLAND);
													try {
														data.getIslander().handleInvite(data.getIsland(), InvitationResult.REVOKED, data);
														event.getView().close();
														islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getInvitations(islander));
													} catch (UserHasIslandException | IslandInvitationException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
												} catch (UserHasNoIslandException e1) {
													// TODO Auto-generated catch block
													e1.printStackTrace();
												}
											}
										}
									}
								}
							}
						}
						if(event.getCurrentItem().getType()==Material.BARRIER) {
							event.getView().close();
							islander.getOfflinePlayer().getPlayer().getPlayer().openInventory(Inventories.getMain(islander));
						}
						break;
					case "Incoming Invitations":
						if(event.getCurrentItem().getType()==Material.PAPER) {
							if(event.getCurrentItem().hasItemMeta()) {
								if(event.getCurrentItem().getItemMeta().hasLore()) {
									List<String> lore = event.getCurrentItem().getItemMeta().getLore();
									for(String string : lore) {
										if(string.contains("Invite Id:")) {
											String id = string.split(":")[1];
											if(event.isRightClick()) {
												InviteData data = new InviteData(Islander.getUser((Player)event.getWhoClicked()).getUniqueId(), UUID.fromString(id), InvitationType.ISLAND);
												try {
													Islander.getUser((Player)event.getWhoClicked()).handleInvite(data.getIsland(), InvitationResult.DENIED, data);
													event.getView().close();
													islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getInvitations(islander));
												} catch (UserHasIslandException | IslandInvitationException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
											}else if(event.isLeftClick()) {
												InviteData data = new InviteData(Islander.getUser((Player)event.getWhoClicked()).getUniqueId(), UUID.fromString(id), InvitationType.ISLAND);
												if(data.isValid()) {
													try {
														Islander.getUser((Player)event.getWhoClicked()).handleInvite(data.getIsland(), InvitationResult.ACCEPTED, data);
														event.getView().close();
													} catch (UserHasIslandException | IslandInvitationException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
												}
											}
										}
									}
								}
							}
						}
						if(event.getCurrentItem().getType()==Material.BARRIER) {
							event.getView().close();
							islander.getOfflinePlayer().getPlayer().getPlayer().openInventory(Inventories.getNoIsland(islander));
						}
						break;
					case "Confirm Destroy":
						if(event.getCurrentItem().isSimilar(InvUtils.getConfirm())) {
							try {
								if(islander.isIslandOwner()) {
									event.getView().close();
									for(InviteData data : islander.getIsland().getOutgoingRequests()) {
										try {
											data.setInvitationType(InvitationType.ISLAND);
											data.getIslander().handleInvite(data.getIsland(), InvitationResult.REVOKED, data);
										} catch (UserHasIslandException | IslandInvitationException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									IslandCreator.deleteIsland(islander.getUniqueId());
									break;
								}else {
									event.getView().close();
									islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"You are not the owner of this island!");
									break;
								}
							} catch (UserHasNoIslandException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else if(event.getCurrentItem().isSimilar(InvUtils.getDeny())) {
							event.getView().close();
							break;
						}
						break;
					case "Confirm Leave":
						if(event.getCurrentItem().isSimilar(InvUtils.getConfirm())) {
							event.getView().close();
							try {
								islander.getIsland().removeIslander(islander);
							} catch (UserHasNoIslandException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
						}else if(event.getCurrentItem().isSimilar(InvUtils.getDeny())) {
							event.getView().close();
							break;
						}
						break;
					case "Settings":
						try {
							if(event.getCurrentItem().getType()==Material.BELL||event.getCurrentItem().getType()==Material.SPRUCE_SIGN||event.getCurrentItem().getType()==Material.BARRIER) {
								if(event.getCurrentItem().getType()==Material.BELL) {
									event.getView().close();
									islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getMembers(islander));
									break;
								}else if(event.getCurrentItem().getType()==Material.SPRUCE_SIGN) {
									event.getView().close();
									islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getVisitors(islander));
								}else if(event.getCurrentItem().getType()==Material.BARRIER) {
									event.getView().close();
									islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getMain(islander));
									break;
								}
							}else {
								if(islander.isIslandOwner()) {
									if(event.getCurrentItem().getType()==Material.PLAYER_HEAD) {
										islander.getIsland().getSettings().setPublic(!islander.getIsland().getSettings().isPublic());
										event.getView().close();
										islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getSettings(islander));
										break;
									}else if(event.getCurrentItem().getType()==Material.IRON_SWORD) {
										islander.getIsland().getSettings().setPvp(!islander.getIsland().getSettings().canPvp());
										event.getView().close();
										islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getSettings(islander));
										break;
									}else if(event.getCurrentItem().getType()==Material.IRON_PICKAXE) {
										islander.getIsland().getSettings().setEdit(!islander.getIsland().getSettings().canEdit());
										event.getView().close();
										islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getSettings(islander));
										break;
									}else if(event.getCurrentItem().getType()==Material.HOPPER) {
										islander.getIsland().getSettings().setPickup(!islander.getIsland().getSettings().canPickup());
										event.getView().close();
										islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getSettings(islander));
										break;
									}else if(event.getCurrentItem().getType()==Material.GREEN_BED) {
										if(islander.getOfflinePlayer().getPlayer().getWorld().getUID().toString().equals(islander.getIsland().getWorld().getUID().toString())) {
											if(!(islander.getOfflinePlayer().getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType()==Material.AIR)||!(islander.getOfflinePlayer().getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType()!=Material.VOID_AIR)) {
												islander.getIsland().getWorld().setSpawnLocation(islander.getOfflinePlayer().getPlayer().getLocation());
												event.getView().close();
												islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+"Island home set!");
												break;
											}else {
												islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"Cannot set island home here!");
												event.getView().close();
												break;
											}
										}else {
											islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"Cannot set island home here!");
											event.getView().close();
											break;
										}
									}
								}else {
									islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"You must be the owner of this island to edit its settings!");
								}
							}
						} catch (UserHasNoIslandException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case "Members":
						if(event.getCurrentItem().getType()==Material.PLAYER_HEAD) {
							Islander p =Islander.getUser((OfflinePlayer) ((SkullMeta) event.getCurrentItem().getItemMeta()).getOwningPlayer());
							event.getView().close();
							try {
								if(p.isIslandOwner()) {
									islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getUser(p, IslanderType.MEMBER_OWNER));
								}else {
									islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getUser(p, IslanderType.MEMBER));
								}
							} catch (UserHasNoIslandException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
						}else if(event.getCurrentItem().getType()==Material.BARRIER) {
							event.getView().close();
							islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getSettings(islander));
							break;
						}
					case "Visitors":
						if(event.getCurrentItem().getType()==Material.PLAYER_HEAD) {
							Islander p =Islander.getUser((Player) ((SkullMeta) event.getCurrentItem().getItemMeta()).getOwningPlayer());
							event.getView().close();
							if(p.getSettings().isEnterBypass()) {
								islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getUser(p, IslanderType.VISITOR_BYPASS));
							}else {
								islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getUser(p, IslanderType.VISITOR));
							}
						}else if(event.getCurrentItem().getType()==Material.BARRIER) {
							event.getView().close();
							islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getSettings(islander));
							break;
						}
					case "Operator Menu":
						if(event.getCurrentItem().getType()==Material.DIAMOND_PICKAXE) {
							if(islander.getOfflinePlayer().getPlayer().hasPermission(Permissions.Operator.islandBuildBypass)) {
								islander.getSettings().setBuildBypass(!islander.getSettings().isBuildBypass());
								event.getView().close();
								islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getOp(islander));
								break;
							}
						}else if(event.getCurrentItem().getType()==Material.ENDER_PEARL) {
							if(islander.getOfflinePlayer().getPlayer().hasPermission(Permissions.Operator.islandEnterBypass)) {
								islander.getSettings().setEnterBypass(!islander.getSettings().isEnterBypass());
								event.getView().close();
								islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getOp(islander));
								break;
							}
						}else if(event.getCurrentItem().isSimilar(InvUtils.getPrevious())) {
							event.getView().close();
							islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getMain(islander));
							break;
						}
						break;
					}	
				}
			}
		}else if(event.getInventory().getHolder() instanceof GuiHolderUser) {
			if(event.getCurrentItem()!=null) {
				event.setCancelled(true);
				if(!(event.getCurrentItem().getType()==Material.BLACK_STAINED_GLASS_PANE)) {
					Islander islander = Islander.getUser((Player)event.getWhoClicked());
					@SuppressWarnings("deprecation")
					Islander p = Islander.getUser(Bukkit.getServer().getOfflinePlayer(event.getView().getTitle()));
					GuiHolderUser holder = (GuiHolderUser) event.getInventory().getHolder();
					
					if(holder.getType()==IslanderType.MEMBER||holder.getType()==IslanderType.MEMBER_OWNER) {
						if(event.getCurrentItem().getType()==Material.WITHER_ROSE) {
							try {
								if(islander.isIslandOwner()) {
									islander.getIsland().removeIslander(p);
									islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+"Removed "+p.getName()+" from the island!");
									event.getView().close();
								}else {
									islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"You must be the owner of your island to kick this person!");
								}
							} catch (UserHasNoIslandException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else if(event.getCurrentItem().getType()==Material.ENDER_PEARL) {
							if(p.getOfflinePlayer().isOnline()) {
								try {
									if(p.getOfflinePlayer().getPlayer().getWorld()==p.getIsland().getWorld()&&islander.getOfflinePlayer().getPlayer().getWorld()==p.getIsland().getWorld()) {
										islander.getOfflinePlayer().getPlayer().teleport(p.getOfflinePlayer().getPlayer().getLocation(), TeleportCause.PLUGIN);
										islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+"Teleported to "+p.getName());
									}else {
										islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"Both players must be on the island to teleport to each other");
									}
								} catch (UserHasNoIslandException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}else {
								islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"This player is not online!");
							}
						}else if(event.getCurrentItem().getType()==Material.CHEST) {
							if(p.getOfflinePlayer().isOnline()) {
								event.getView().close();
								islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getUserContents(p, InventoryType.PLAYER));
							}else {
								islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"This player is not online!");
							}
						}else if(event.getCurrentItem().getType()==Material.ENDER_CHEST) {
							if(p.getOfflinePlayer().isOnline()) {
								event.getView().close();
								islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getUserContents(p, InventoryType.ENDER_CHEST));
							}else {
								islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"This player is not online!");
							}
						}else if(event.getCurrentItem().getType()==Material.BARRIER) {
							event.getView().close();
							islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getMembers(islander));
						}
					}else if(holder.getType()==IslanderType.VISITOR||holder.getType()==IslanderType.VISITOR_BYPASS) {
						if(event.getCurrentItem().getType()==Material.WITHER_ROSE) {
							if(p.getOfflinePlayer().isOnline()) {
								p.getOfflinePlayer().getPlayer().teleport(Skyblock.getSpawn().getSpawnLocation(), TeleportCause.PLUGIN);
							}
							islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+"Kicked "+p.getName()+" off the island!");
							event.getView().close();
							try {
								p.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"You have been kicked off "+islander.getIsland().getOwner().getName()+"'s Island!");
							} catch (UserHasNoIslandException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
						}else if(event.getCurrentItem().getType()==Material.PAPER) {
							try {
								if(islander.isIslandOwner()) {
									try {
										p.sendInvite(islander.getIsland());
									} catch (UserHasIslandException e) {
										// TODO Auto-generated catch block
										event.getView().close();
										islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"This player already has an island!");
									} catch (UserInvitationListException e) {
										// TODO Auto-generated catch block
										event.getView().close();
										islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"This player's invitation list is full!");
									} catch (IslandException e) {
										// TODO Auto-generated catch block
										event.getView().close();
										islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"This island cannot hold anymore members!");
									} catch (IslandInvitationException e) {
										// TODO Auto-generated catch block
										event.getView().close();
										islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"This island cannot send anymore invitations!");
									} catch (UserHasNoIslandException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (UserAlreadyHasInvite e) {
										// TODO Auto-generated catch block
										event.getView().close();
										islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"This player already has an invite to your island!");
									}
								}else {
									event.getView().close();
									islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"You must be the owner of this island to send invites!");
								}
							} catch (UserHasNoIslandException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else if(event.getCurrentItem().getType()==Material.ENDER_PEARL) {
							if(p.getOfflinePlayer().isOnline()) {
								try {
									if(p.getOfflinePlayer().getPlayer().getWorld()==islander.getIsland().getWorld()&&islander.getOfflinePlayer().getPlayer().getWorld()==islander.getIsland().getWorld()) {
										islander.getOfflinePlayer().getPlayer().teleport(p.getOfflinePlayer().getPlayer().getLocation(), TeleportCause.PLUGIN);
										islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.GREEN+"Teleported to "+p.getName());
									}else {
										islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"Both players must be on the island to teleport to each other");
									}
								} catch (UserHasNoIslandException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}else {
								islander.getOfflinePlayer().getPlayer().sendMessage(ChatColor.RED+"This player is not online!");
							}
						}else if(event.getCurrentItem().getType()==Material.BARRIER) {
							event.getView().close();
							islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getVisitors(islander));
						}
					}
				}
			}
		}else if(event.getInventory().getHolder() instanceof GuiHolderUserInv) {
			event.setCancelled(true);
		}else if(event.getInventory().getHolder() instanceof GuiSections) {
			event.setCancelled(true);
			if(event.getCurrentItem()!=null) {
				if(!(event.getCurrentItem().getType()==Material.BLACK_STAINED_GLASS_PANE)&&!event.getCurrentItem().getItemMeta().getDisplayName().equals(" ")) {
					event.getView().close();
					Islander islander = Islander.getUser((Player) event.getWhoClicked());
					islander.getOfflinePlayer().getPlayer().openInventory(Inventories.getShopItems(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())));
				}
			}
		}else if(event.getInventory().getHolder() instanceof GuiItems) {
			event.setCancelled(true);
			if(event.getCurrentItem()!=null) {
				if(!(event.getCurrentItem().getType()==Material.BLACK_STAINED_GLASS_PANE)&&!event.getCurrentItem().getItemMeta().getDisplayName().equals(" ")) {
					Player p = (Player) event.getWhoClicked();
					if(event.getClick()==ClickType.LEFT) {
						for(String string:event.getCurrentItem().getItemMeta().getLore()) {
							if(string.contains("Single Cost:")) {
								double cost = Double.parseDouble(string.split(":")[1].replace(Extras.Economy.getSymbol(), ""));
								if(Economy.getBalance(p.getUniqueId())>=cost) {
									if(!(p.getInventory().firstEmpty()==-1)) {
										Economy.removeBalance(p.getUniqueId(), cost);
										p.getInventory().addItem(new ItemStack(event.getCurrentItem().getType()));
										p.sendMessage(ChatColor.GREEN+"Bought 1 "+ShopData.getProperName(event.getCurrentItem().getType().name())+" for "+Extras.Economy.getSymbol()+Double.toString(cost));
									}else {
										p.sendMessage(ChatColor.RED+"Your inventory is full!");
									}
								}else {
									p.sendMessage(ChatColor.RED+"You do not have enough money to buy this!");
								}
							}
						}
					}else if(event.getClick()==ClickType.SHIFT_LEFT) {
						for(String string:event.getCurrentItem().getItemMeta().getLore()) {
							if(string.contains("Single Cost:")) {
								double cost = Double.parseDouble(string.split(":")[1].replace(Extras.Economy.getSymbol(), ""))*64;
								if(Economy.getBalance(p.getUniqueId())>=cost) {
									if(!(p.getInventory().firstEmpty()==-1)) {
										Economy.removeBalance(p.getUniqueId(), cost);
										p.getInventory().addItem(new ItemStack(event.getCurrentItem().getType(),64));
										p.sendMessage(ChatColor.GREEN+"Bought 64 "+ShopData.getProperName(event.getCurrentItem().getType().name())+" for "+Extras.Economy.getSymbol()+Double.toString(cost));
									}else {
										p.sendMessage(ChatColor.RED+"Your inventory is full!");
									}
								}else {
									p.sendMessage(ChatColor.RED+"You do not have enough money to buy this!");
								}
							}
						}
					}else if(event.getClick()==ClickType.RIGHT) {
						for(String string:event.getCurrentItem().getItemMeta().getLore()) {
							if(string.contains("Single Sell:")) {
								double sell = Double.parseDouble(string.split(":")[1].replace(Extras.Economy.getSymbol(), ""));
								if(p.getInventory().contains(event.getCurrentItem().getType())) {
									for(ItemStack item:p.getInventory().getContents()) {
										if(item!=null) {
											if(item.getType()==event.getCurrentItem().getType()) {
												item.setAmount(item.getAmount()-1);
												Economy.addBalance(p.getUniqueId(), sell);
												p.sendMessage(ChatColor.GREEN+"Sold 1 "+ShopData.getProperName(event.getCurrentItem().getType().name()+" for "+Extras.Economy.getSymbol()+Double.toString(sell)));
												return;
											}
										}
									}
								}else {
									p.sendMessage(ChatColor.RED+"You do not have enough of this item!");
								}
							}
						}
					}else if(event.getClick()==ClickType.SHIFT_RIGHT) {
						for(String string:event.getCurrentItem().getItemMeta().getLore()) {
							if(string.contains("Single Sell:")) {
								double sell = Double.parseDouble(string.split(":")[1].replace(Extras.Economy.getSymbol(), ""));
								if(p.getInventory().contains(event.getCurrentItem().getType())) {
									for(ItemStack item:p.getInventory().getContents()) {
										if(item!=null) {
											if(item.getType()==event.getCurrentItem().getType()) {
												int amt = item.getAmount();
												item.setAmount(0);
												Economy.addBalance(p.getUniqueId(), sell*amt);
												p.sendMessage(ChatColor.GREEN+"Sold "+Integer.toString(amt)+" "+ShopData.getProperName(event.getCurrentItem().getType().name()+" for "+Extras.Economy.getSymbol()+Double.toString(sell*amt)));
												return;
											}
										}
									}
								}else {
									p.sendMessage(ChatColor.RED+"You do not have enough of this item!");
								}
							}
						}
					}
				}
			}
		}
	}

}
