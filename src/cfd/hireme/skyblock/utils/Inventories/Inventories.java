package cfd.hireme.skyblock.utils.Inventories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.enums.IslanderType;
import cfd.hireme.skyblock.exceptions.UserHasIslandException;
import cfd.hireme.skyblock.exceptions.UserHasNoIslandException;
import cfd.hireme.skyblock.extra.Extras;
import cfd.hireme.skyblock.extra.economy.Economy;
import cfd.hireme.skyblock.extra.shop.Shop;
import cfd.hireme.skyblock.objects.Permissions;
import cfd.hireme.skyblock.objects.data.InviteData;
import cfd.hireme.skyblock.objects.data.IslandData;
import cfd.hireme.skyblock.objects.data.SectionData;
import cfd.hireme.skyblock.objects.data.ShopData;
import cfd.hireme.skyblock.objects.data.UpgradeData;
import cfd.hireme.skyblock.objects.data.challenges.ChallengeMaterial;
import cfd.hireme.skyblock.objects.data.challenges.ChallengeMob;
import cfd.hireme.skyblock.objects.data.challenges.IslandChallenge;
import cfd.hireme.skyblock.objects.holders.GuiHolder;
import cfd.hireme.skyblock.objects.holders.GuiHolderUser;
import cfd.hireme.skyblock.objects.holders.GuiHolderUserInv;
import cfd.hireme.skyblock.objects.holders.GuiItems;
import cfd.hireme.skyblock.objects.holders.GuiSections;
import cfd.hireme.skyblock.objects.main.Island;
import cfd.hireme.skyblock.objects.main.Islander;
import net.md_5.bungee.api.ChatColor;

public class Inventories {
	private static ChatColor defaultTitle = ChatColor.WHITE;
	private static ChatColor defaultLore = ChatColor.GRAY;
	public static void setDefaultTitle(ChatColor color) {
		defaultTitle=color;
	}
	public static void setDefaultLore(ChatColor color) {
		defaultLore=color;
	}
	public static ChatColor getDefaultTitle() {
		return defaultTitle;
	}
	public static ChatColor getDefaultLore() {
		return defaultLore;
	}
	public static int getDynamicSize(int size) {
		if(size==0) {
			size=1;
		}
		double calc =(double) size/9;
		size = (int) Math.ceil(calc);
		if(size==0) {
			size=1;
		}
		return size*9;
	}
	public static Inventory getCreation(Islander islander) {
		ArrayList<IslandData> templates = null;
		templates = IslandData.getTemplates();
		int size = getDynamicSize(templates.size());
		Inventory inv = Bukkit.createInventory(new GuiHolder(), size, "Island Creation");
		inv.setMaxStackSize(1);
		if(templates!=null) {
			if(templates.size()>0) {
				for(IslandData data : templates) {
					if(data.isLocked()) {
						if(!islander.getOfflinePlayer().getPlayer().hasPermission("dskyblock.islands."+Integer.toString(data.getId()))) {
							if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("island_gui.show_locked_islands")){
								List<String> lore = data.getLore();
								lore.add(ChatColor.GOLD+"Id: "+Integer.toString(data.getId()));
								lore.add(ChatColor.RED+""+ChatColor.BOLD+"LOCKED");
								inv.addItem(InvUtils.createItem(Material.BARRIER, true, false, data.getTitle(), lore));
							}
						}else {
							List<String> lore = data.getLore();
							lore.add(ChatColor.GOLD+"Id: "+Integer.toString(data.getId()));
							inv.addItem(InvUtils.createItem(data.getDisplayItem(), data.isEnchanted(), false, data.getTitle(), lore));
						}
					}else {
						List<String> lore = data.getLore();
						lore.add(ChatColor.GOLD+"Id:"+String.valueOf(data.getId()));
						inv.addItem(InvUtils.createItem(data.getDisplayItem(), data.isEnchanted(), false, data.getTitle(), lore));
					}
					
				}
			}
		}
		InvUtils.fillBlanks(inv);
		return inv;
	}
	public static Inventory getUpgrades(Island island) {
		List<UpgradeData> data = island.getUpgrades();
		int size = getDynamicSize(data.size());
		Inventory inv = Bukkit.createInventory(new GuiHolder(), size, "Upgrades");
		for(UpgradeData upgrade:data) {
			List<String> lore = new ArrayList<String>();
			Material mat = Material.STONE;
			switch(upgrade.getType()) {
			case BORDER:
				lore.add(defaultLore+"Increases the islands");
				lore.add(defaultLore+"border size by 10");
				mat=Material.IRON_BARS;
				break;
			case MEMBER:
				lore.add(defaultLore+"Increases the islands");
				lore.add(defaultLore+"maximum member count by 1");
				mat=Material.POPPY;
				break;
			case VISITOR:
				lore.add(defaultLore+"Increases the islands");
				lore.add(defaultLore+"maximum visitor count by 1");
				mat=Material.FEATHER;
				break;
			default:
				break;
			
			}
			boolean enchanted=false;
			if(!upgrade.isMaxLevel()) {
				if(island.getLevel()>=upgrade.getLevelRequirement()) {
					lore.add(ChatColor.GREEN+"Level Requirement: "+Integer.toString(upgrade.getLevelRequirement()));
				}else {
					lore.add(ChatColor.RED+"Level Requirement: "+Integer.toString(upgrade.getLevelRequirement()));
				}
				lore.add(defaultLore+"Cost: "+ChatColor.YELLOW+Extras.Economy.getSymbol()+Double.toString(upgrade.getCost()));
			}else {
				enchanted=true;
				lore.add(ChatColor.GREEN+"Max Level");
			}
			lore.add(defaultLore+"Level: "+ChatColor.YELLOW+upgrade.getCurrentLevel());
			inv.addItem(InvUtils.createItem(mat, enchanted, false, ShopData.getProperName(upgrade.getType().name()), lore));
		}
		InvUtils.fillBlanks(inv);
		return inv;
	}
	public static Inventory getShopSections(Islander islander) {
		List<SectionData> data = Shop.getSections();
		boolean showres = Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Shop.show_restricted");
		int size = getDynamicSize(data.size());
		Inventory inv = Bukkit.createInventory(new GuiSections(), size, "Shop");
		for(SectionData sdata : data) {
			Material mat;
			ArrayList<String> lore = new ArrayList<String>();
			if(sdata.getLore().size()>0) {
				for(String string:sdata.getLore()) {
					lore.add(ChatColor.translateAlternateColorCodes('&', string));
				}
			}
			if(sdata.isRestricted()) {
				if(islander.getOfflinePlayer().getPlayer().hasPermission("dskyblock.shop."+sdata.getName())) {
					mat=sdata.getMaterial();
				}else {
					mat=Material.BARRIER;
					lore.add(ChatColor.RED+"LOCKED");
				}
			}else {
				mat=sdata.getMaterial();
			}
			if(mat==Material.BARRIER) {
				if(showres) {
					inv.addItem(InvUtils.createItem(mat, false, false, sdata.getName(), lore));
				}
			}else {
				inv.addItem(InvUtils.createItem(mat, false, false, sdata.getName(), lore));
			}
		}
		InvUtils.fillBlanks(inv);
		return inv;
	}
	public static Inventory getChallenges(Island island) {
		List<IslandChallenge> data = island.getChallenges();
		Inventory inv = Bukkit.createInventory(new GuiHolder(), getDynamicSize(data.size()), "Challenges");
		for(IslandChallenge c : data) {
			boolean enchant=false;
			Material mat = c.getData().getDisplay();
			String prefix="";
			String suffix="";
			String status ="";
			String reward="";
			if(c.isCompleted()) {
				if(c.isClaimed()) {
					status=ChatColor.GREEN+"Claimed";
					mat=Material.GREEN_STAINED_GLASS_PANE;
				}else {
					status=ChatColor.RED+"Unclaimed";
					enchant=true;
				}
			}else {
				status=ChatColor.RED+"Incomplete";
			}
			switch(c.getData().getRewardType()) {
			case MATERIAL:
				String r = (String) c.getData().getReward();
				reward=r.split(":")[1]+" "+ShopData.getProperName(r.split(":")[0]);
				break;
			case MONEY:
				reward=Extras.Economy.getSymbol()+Double.toString((double) c.getData().getReward());
				break;
			default:
				break;
			
			}
			switch(c.getData().getChallengeType()) {
			case COLLECT:
				prefix="Collect";
				suffix=ShopData.getProperName(((ChallengeMaterial) c.getData()).getMaterial().name());
				break;
			case DUMMY:
				prefix=c.getData().getPrefix();
				suffix=c.getData().getSuffix();
				break;
			case ISLAND_LEVEL:
				prefix="Reach Island Level";
				break;
			case MINE:
				prefix="Mine";
				suffix=ShopData.getProperName(((ChallengeMaterial) c.getData()).getMaterial().name());
				break;
			case SLAY:
				prefix="Slay";
				suffix=ShopData.getProperName(((ChallengeMob) c.getData()).getMob().name());
				break;
			default:
				break;
			
			}
			String message = defaultLore+prefix+" "+Integer.toString(c.getData().getRequirement())+" "+suffix;
			String progress = defaultLore+"Progress: "+Integer.toString(c.getProgress())+"/"+Integer.toString(c.getData().getRequirement());
			inv.addItem(InvUtils.createItem(mat, enchant, false, defaultTitle+c.getData().getName(), Arrays.asList(message,progress, defaultLore+"Reward: "+ChatColor.GREEN+reward,"",status)));
		}
		InvUtils.fillBlanks(inv);
		return inv;
	}
	public static Inventory getShopItems(String section) {
		String symbol = Extras.Economy.getSymbol();
		List<ShopData> data = Shop.getSectionItems(section);
		int size = getDynamicSize(data.size());
		Inventory inv = Bukkit.createInventory(new GuiItems(), size, section);
		for(ShopData item:data) {
			List<String> lore = new ArrayList<String>();
			lore.add(defaultLore+"Single Cost: "+symbol+Double.toString(item.getCost()));
			lore.add(defaultLore+"Stack Cost: "+symbol+Double.toString(item.getCost()*64));
			lore.add(defaultLore+"Single Sell: "+symbol+Double.toString(item.getSell()));
			lore.add(defaultLore+"Stack Sell: "+symbol+Double.toString(item.getSell()*64));
			lore.add(ChatColor.YELLOW+"Left Click to buy");
			lore.add(ChatColor.YELLOW+"Right Click to sell");
			inv.addItem(InvUtils.createItem(item.getMaterial(), false, false, item.getName(), lore));
		}
		InvUtils.fillBlanks(inv);
		return inv;
	}
	public static Inventory getNoIsland(Islander islander) {
		Inventory inv = Bukkit.createInventory(new GuiHolder(), 9, "Skyblock");
		inv.setItem(0,InvUtils.createItem(Material.OAK_SAPLING, false, false, defaultTitle+"Island Creation", Arrays.asList(defaultLore+"View the island creation menu")));
		inv.setItem(1, InvUtils.createItem(Material.WRITABLE_BOOK, false, false, defaultTitle+"Requests", Arrays.asList(defaultLore+"View incoming island", defaultLore+"requests")));
		if(islander.getOfflinePlayer().getPlayer().hasPermission(Permissions.Operator.guioperator)) {
			inv.setItem(8, InvUtils.createItem(Material.PAPER, true, false, defaultTitle+"Operator Menu", Arrays.asList(defaultLore+"Opens the operator menu")));
		}
		InvUtils.fillBlanks(inv);
		return inv;
	}
	public static Inventory getMain(Islander islander) {
		Inventory inv = Bukkit.createInventory(new GuiHolder(), 9, "Island");
		inv.addItem(InvUtils.createItem(Material.GREEN_BED, false, false, defaultTitle+"Home", Arrays.asList(defaultLore+"Teleport to your island")));
		inv.addItem(InvUtils.createItem(Material.REDSTONE, false, false, defaultTitle+"Settings", Arrays.asList(defaultLore+"Island Settings")));
		inv.addItem(InvUtils.createItem(Material.DIAMOND, false, false, defaultTitle+"Challenges", Arrays.asList(defaultLore+"Island Challenges")));
		inv.addItem(InvUtils.createItem(Material.WRITABLE_BOOK, false, false, defaultTitle+"Requests", Arrays.asList(defaultLore+"View outgoing invitations", defaultLore+"to your island")));
		inv.addItem(InvUtils.createItem(Material.EXPERIENCE_BOTTLE, true, false, defaultTitle+"Calculate Island Level", Arrays.asList(defaultLore+"Calculate your islands level")));
		inv.addItem(InvUtils.createItem(Material.GOLD_INGOT, false, false, defaultTitle+"Upgrades", Arrays.asList(defaultLore+"View your islands upgrades")));
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.shop.enabled")) {
			inv.addItem(InvUtils.createItem(Material.NAME_TAG, false, false, defaultTitle+"Shop", Arrays.asList(defaultLore+"Opens the server shop")));
		}
		if(islander.getOfflinePlayer().getPlayer().hasPermission(Permissions.Operator.guioperator)) {
			inv.setItem(7, InvUtils.createItem(Material.PAPER, true, false, defaultTitle+"Operator Menu", Arrays.asList(defaultLore+"Opens the operator menu")));
		}
		try {
			if(islander.isIslandOwner()) {
				inv.setItem(8, InvUtils.createItem(Material.BARRIER, false, false, defaultTitle+"Destroy Island", Arrays.asList(ChatColor.RED+"This will destroy your",ChatColor.RED+"island, this cannot be undone")));
			}else {
				inv.setItem(8, InvUtils.createItem(Material.BARRIER, false, false, defaultTitle+"Leave Island", Arrays.asList(ChatColor.RED+"Leave Island",ChatColor.RED+"Leave your current Island")));
			}
		} catch (UserHasNoIslandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		InvUtils.fillBlanks(inv);
		return inv;
	}
	public static Inventory getInvitations(Islander islander) {
		String name="Invalid Identifier";
		if(islander.hasIsland()) {
			name="Outgoing Invitations";
		}else {
			name="Incoming Invitations";
		}
		Inventory inv = Bukkit.createInventory(new GuiHolder(), 9, name);
		List<InviteData> invdata=null;
		if(!islander.hasIsland()) {
			try {
				invdata = islander.getInvites();
			} catch (UserHasIslandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(invdata!=null) {
				if(invdata.size()>0) {
					for(InviteData data : invdata) {
						List<String> lore = new ArrayList<String>();
						lore.add(ChatColor.YELLOW+"Members:");
						for(Islander isla : data.getIsland().getMembers()) {
							lore.add(ChatColor.GRAY+isla.getName());
						}
						lore.add(ChatColor.YELLOW+"Level: "+ChatColor.GRAY+Integer.toString(data.getIsland().getLevel()));
						if(data.isValid()) {
							lore.add(ChatColor.GREEN+"Valid");
						}else {
							lore.add(ChatColor.RED+"Invalid");
						}
						lore.add(ChatColor.GOLD+"Left Click to Accept");
						lore.add(ChatColor.GOLD+"Right Click to Deny");
						lore.add(ChatColor.YELLOW+"Invite Id:"+data.getIsland().getId().toString());
						inv.addItem(InvUtils.createItem(Material.PAPER, true, false, data.getIsland().getOwner().getName()+"'s Island", lore));
					}
				}
			}
		}else {
			try {
				invdata = islander.getIsland().getOutgoingRequests();
			} catch (UserHasNoIslandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(invdata!=null) {
				if(invdata.size()>0) {
					for(InviteData data : invdata) {
						List<String> lore = new ArrayList<String>();
						if(data.isValid()) {
							lore.add(ChatColor.GREEN+"Valid");
						}else {
							lore.add(ChatColor.RED+"Invalid");
						}
						lore.add(ChatColor.GOLD+"Right Click to Revoke");
						lore.add(ChatColor.YELLOW+"Invite Id:"+data.getIslander().getUniqueId().toString());
						inv.addItem(InvUtils.createItem(Material.PAPER, true, false, data.getIslander().getName(), lore));
					}
				}
			}
		}
		inv.setItem(8, InvUtils.getPrevious());
		InvUtils.fillBlanks(inv);
		return inv;
	}
	public static Inventory getOp(Islander islander) {
		Inventory inv = Bukkit.createInventory(new GuiHolder(), 9, "Operator Menu");
		Material bbypass = Material.DIAMOND_PICKAXE;
		Material tbypass = Material.ENDER_PEARL;
		List<String> bbypasss = Arrays.asList(defaultLore+"Allows a user to bypass",defaultLore+"island build restrictions", defaultLore+Permissions.Operator.islandBuildBypass.getName());
		List<String> tbypasss = Arrays.asList(defaultLore+"Allows a user to bypass",defaultLore+"private island restrictions", defaultLore+Permissions.Operator.islandEnterBypass.getName());
		if(!islander.getOfflinePlayer().getPlayer().hasPermission(Permissions.Operator.islandBuildBypass)) {
			bbypass=Material.BARRIER;
			bbypasss.add(ChatColor.RED+"LOCKED");
		}
		if(!islander.getOfflinePlayer().getPlayer().hasPermission(Permissions.Operator.islandEnterBypass)) {
			tbypass=Material.BARRIER;
			tbypasss.add(ChatColor.RED+"LOCKED");
		}
		inv.addItem(InvUtils.createItem(bbypass, islander.getSettings().isBuildBypass(), true, defaultTitle+"Build Bypass", bbypasss));
		inv.addItem(InvUtils.createItem(tbypass, islander.getSettings().isEnterBypass(), true, defaultTitle+"Enter Bypass", tbypasss));
		inv.setItem(7, InvUtils.getPrevious());
		InvUtils.fillBlanks(inv);
		return inv;
	}
	public static Inventory getSettings(Islander islander) {
		Inventory inv = Bukkit.createInventory(new GuiHolder(), 9, "Settings");
		try {
			inv.addItem(InvUtils.createItem(Material.PLAYER_HEAD, islander.getIsland().getSettings().isPublic(), true, defaultTitle+"Public", Arrays.asList(defaultLore+"Allows non-members to visit", defaultLore+"your island")));
			inv.addItem(InvUtils.createItem(Material.IRON_SWORD, islander.getIsland().getSettings().canPvp(), true, defaultTitle+"Pvp", Arrays.asList(defaultLore+"Allows pvp on", defaultLore+"your island between members")));
			inv.addItem(InvUtils.createItem(Material.IRON_PICKAXE, islander.getIsland().getSettings().canEdit(), true, defaultTitle+"Edit", Arrays.asList(defaultLore+"Allows non-members to", defaultLore+"edit on your island", ChatColor.RED+"(Dangerous")));
			inv.addItem(InvUtils.createItem(Material.HOPPER, islander.getIsland().getSettings().canPickup(), true, defaultTitle+"Pickup", Arrays.asList(defaultLore+"Allows non-members to pickup", defaultLore+"dropped items on your island")));
			inv.addItem(InvUtils.createItem(Material.GREEN_BED, false, false, defaultTitle+"Set Home", Arrays.asList(defaultLore+"Set your islands", defaultLore+"home at your current location")));
		}catch(UserHasNoIslandException e) {e.printStackTrace();}
		inv.setItem(6, InvUtils.createItem(Material.BELL, false, false, defaultTitle+"Members", Arrays.asList(defaultLore+"View a list of players", defaultLore+"that are members of", defaultLore+"your island")));
		inv.setItem(7, InvUtils.createItem(Material.SPRUCE_SIGN, false, false, defaultTitle+"Visitors", Arrays.asList(defaultLore+"View a list of players", defaultLore+"that are currently on", defaultLore+"your island")));
		inv.setItem(8, InvUtils.getPrevious());
		InvUtils.fillBlanks(inv);
		return inv;
	}
	public static Inventory getMembers(Islander islander) {
		List<Islander> mems;
		try {
			mems = islander.getIsland().getMembers();
		} catch (UserHasNoIslandException e) {
			// TODO Auto-generated catch block
			mems = new ArrayList<Islander>();
		}
		Inventory inv = Bukkit.createInventory(new GuiHolder(), getDynamicSize(mems.size()), "Members");
		inv.setItem(getDynamicSize(mems.size())-1, InvUtils.getPrevious());
		for(Islander user : mems) {
			ArrayList<String> lore = new ArrayList<String>();
			try {
				if(user.isIslandOwner()) {
					lore.add(ChatColor.GREEN+"Island Owner");
				}
			} catch (UserHasNoIslandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lore.add("");
			lore.add(ChatColor.GOLD+"Click for more options");
			inv.addItem(InvUtils.createHead(user.getOfflinePlayer(), false, false, defaultTitle+user.getName(), lore));
		}
		InvUtils.fillBlanks(inv);
		return inv;
	}
	public static Inventory getVisitors(Islander islander) {
		List<Islander> vis;
		try {
			vis = islander.getIsland().getVisitors();
		} catch (UserHasNoIslandException e) {
			// TODO Auto-generated catch block
			vis=new ArrayList<Islander>();
		}
		Inventory inv = Bukkit.createInventory(new GuiHolder(), getDynamicSize(vis.size()), "Visitors");
		inv.setItem(getDynamicSize(vis.size())-1, InvUtils.getPrevious());
		for(Islander user : vis) {
			ArrayList<String> lore = new ArrayList<String>();
			lore.add("");
			lore.add(ChatColor.GOLD+"Click for more options");
			inv.addItem(InvUtils.createHead(user.getOfflinePlayer(), false, false, defaultTitle+user.getName(), lore));
		}
		InvUtils.fillBlanks(inv);
		return inv;
	}
	public static Inventory getUserContents(Islander islander, InventoryType type) {
		String name;
		if(type==InventoryType.PLAYER) {
			name=islander.getName()+"'s Inventory";
		}else {
			name=islander.getName()+"'s Enderchest";
		}
		Inventory inv = Bukkit.createInventory(new GuiHolderUserInv(), 9*6, name);
		if(type==InventoryType.PLAYER) {
			int size = 0;
			for(ItemStack item : islander.getOfflinePlayer().getPlayer().getInventory().getContents()) {
				if(item!=null) {
					inv.setItem(size,item);
				}
				size++;
			}
		}else {
			int size = 0;
			for(ItemStack item : islander.getOfflinePlayer().getPlayer().getEnderChest().getContents()) {
				if(item!=null) {
					inv.setItem(size,item);
				}
				size++;
			}
		}
		return inv;
	}
	public static Inventory getUser(Islander islander, IslanderType type) {
		Inventory inv = Bukkit.createInventory(new GuiHolderUser(type), 9*5, islander.getName());
		Date date =new Date(islander.getOfflinePlayer().getFirstPlayed());
		Date date2 = new Date(islander.getOfflinePlayer().getLastPlayed());
		
		int box = 28;
		if(type==IslanderType.MEMBER||type==IslanderType.MEMBER_OWNER) {
			Material kick;
			String symbol = Extras.Economy.getSymbol();
			List<String> lore = new ArrayList<String>();
			List<String> ichestl = new ArrayList<String>();
			List<String> echestl = new ArrayList<String>();
			ichestl.add(defaultLore+"View this players inventory");
			echestl.add(defaultLore+"View this players enderchest");
			lore.add(defaultLore+"Remove this member from your island");
			if(type==IslanderType.MEMBER_OWNER) {
				kick=Material.RED_STAINED_GLASS_PANE;
				lore.add(" ");
				lore.add(ChatColor.RED+"LOCKED");
			}else {
				kick=Material.WITHER_ROSE;
			}
			if(!islander.getOfflinePlayer().isOnline()) {
				ichestl.add(" ");
				ichestl.add(ChatColor.RED+"Player is offline");
				echestl.add(" ");
				echestl.add(ChatColor.RED+"Player is offline");
			}
			inv.setItem(box++, InvUtils.createItem(kick, false, false, defaultTitle+"Remove", lore));
			inv.setItem(box++, InvUtils.createItem(Material.ENDER_PEARL, false, false, defaultTitle+"Teleport", Arrays.asList(defaultLore+"Teleport to this user", defaultLore+"only if both players are on", defaultLore+"the island")));
			if(Extras.Economy.economyEnabled()) {
				inv.setItem(box++, InvUtils.createItem(Material.GOLD_NUGGET, false, false, defaultTitle+"Balance", Arrays.asList(defaultLore+symbol+Economy.getBalance(islander.getOfflinePlayer().getUniqueId()))));
			}
			inv.setItem(box++, InvUtils.createItem(Material.CHEST, false, false, defaultTitle+"Inventory", ichestl));
			inv.setItem(box++, InvUtils.createItem(Material.ENDER_CHEST, false, false, defaultTitle+"Enderchest", echestl));
		}else {
			Material kick;
			List<String> lore = new ArrayList<String>();
			lore.add(defaultLore+"Remove this visitor from your island");
			if(type==IslanderType.VISITOR_BYPASS) {
				kick=Material.RED_STAINED_GLASS_PANE;
				lore.add(" ");
				lore.add(ChatColor.RED+"LOCKED");
			}else {
				kick=Material.WITHER_ROSE;
			}
			inv.setItem(box++, InvUtils.createItem(kick, false, false, defaultTitle+"Kick", lore));
			inv.setItem(box++, InvUtils.createItem(Material.PAPER, false, false, defaultTitle+"Invite", Arrays.asList(defaultLore+"Invite this player to", defaultLore+"your island")));
			inv.setItem(box++, InvUtils.createItem(Material.ENDER_PEARL, false, false, defaultTitle+"Teleport", Arrays.asList(defaultLore+"Teleport to this user", defaultLore+"only if both players are on", defaultLore+"the island")));
		}
		List<String> lore = new ArrayList<String>();
		lore.add(defaultLore+"Joined Server: "+date.toString());
		lore.add(defaultLore+"Last Active: "+date2.toString());
		switch(type) {
		case MEMBER:
			lore.add(" ");
			lore.add(defaultLore+"Role: Member");
			try {
				lore.add(defaultLore+"Joined Island: "+islander.getIslandJoinDate());
			} catch (UserHasNoIslandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case MEMBER_OWNER:
			lore.add(" ");
			lore.add(defaultLore+"Role: Owner");
			try {
				lore.add(defaultLore+"Joined Island: "+islander.getIslandJoinDate());
			} catch (UserHasNoIslandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case VISITOR:
			lore.add(" ");
			lore.add(defaultLore+"Role: Visitor");
			break;
		case VISITOR_BYPASS:
			lore.add(" ");
			lore.add(defaultLore+"Role: Visitor");
			break;
		default:
			lore.add(" ");
			lore.add(defaultLore+"Role: Visitor");
			break;
		
		}
		inv.setItem(34, InvUtils.getPrevious());
		inv.setItem(13, InvUtils.createHead(islander.getOfflinePlayer(), false, false, defaultTitle+islander.getName(), lore));
		InvUtils.fillBlanks(inv);
		return inv;
	}
	public static Inventory getConfirm(int i) {
		String name="Invalid Identifier";
		switch(i) {
		case 0:
			name="Confirm Destroy";
			break;
		case 1:
			name="Confirm Leave";
			break;
		}
		Inventory inv = Bukkit.createInventory(new GuiHolder(), 9,name);
		inv.setItem(3, InvUtils.getConfirm());
		inv.setItem(5, InvUtils.getDeny());
		InvUtils.fillBlanks(inv);
		return inv;
	}
}
