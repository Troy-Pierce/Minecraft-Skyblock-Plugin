/*
 *        derpySkyblock - Derpy00001 | Derpy#5247
 *        discord.gg/bQxBB89
 *        Hi! :)
 */

package cfd.hireme.skyblock.objects;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Permissions {
	public final static Permission setWelcomeMessage = new Permission("dskyblock.island.setwelcome", "Allows a user to set their islands welcome message", PermissionDefault.TRUE);
	public static class Operator{
		public final static Permission guioperator = new Permission("dskyblock.operator.gui", "Allows a user to view to operator menu", PermissionDefault.OP);
		public final static Permission islandBuildBypass = new Permission("dskyblock.operator.buildbypass", "Allows a user to bypass island build restrictions.",PermissionDefault.OP);
		public final static Permission islandEnterBypass = new Permission("dskyblock.operator.enterbypass", "Allows a user to bypass island privacy restrictions.", PermissionDefault.OP);
	}
	
}
