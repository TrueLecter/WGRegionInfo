package truelecter.wgregionfinder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WGFinder extends JavaPlugin {
	private WorldGuardPlugin wg;
	private WorldEditPlugin we;
	private String ANSI_RED = "\u001B[31m";
	private String ANSI_RESET = "\u001B[0m";
	private String ANSI_GREEN = "\u001B[32m";

	private List<String> getRegions(Selection sel) {
		BlockVector b1 = new BlockVector(sel.getMaximumPoint().getBlockX(), sel
				.getMaximumPoint().getBlockY(), sel.getMaximumPoint()
				.getBlockZ());
		BlockVector b2 = new BlockVector(sel.getMinimumPoint().getBlockX(), sel
				.getMinimumPoint().getBlockY(), sel.getMinimumPoint()
				.getBlockZ());

		RegionManager regionManager = wg.getRegionManager(sel.getWorld());

		ApplicableRegionSet set = regionManager
				.getApplicableRegions(new ProtectedCuboidRegion("tmpreg", b1,
						b2));

		if (set.size() == 0) {
			return null;
		}

		List<String> regs = new ArrayList<String>();
		for (ProtectedRegion r : set) {
			regs.add(r.getId());
		}
		return regs;
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		int i = 0;
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "[WGFinder] Only players!"
					+ ChatColor.RESET);
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("wgf")) {
			if (we.getSelection((Player) sender) == null) {
				sender.sendMessage(ChatColor.RED + "Ошибка" + ChatColor.RESET
						+ ": Нужно выделить регион.");
			} else {
				StringBuilder str = new StringBuilder();
				if (getRegions(we.getSelection((Player) sender)) == null) {
					sender.sendMessage(ChatColor.GOLD
							+ "Выделеный регион не пересекается с другими.");
				}
				str.append(ChatColor.GOLD + "Выделеный регион пересекается с: "
						+ ChatColor.GREEN);
				for (String s : getRegions(we.getSelection((Player) sender))) {
					if (i == 0) {
						str.append(s);
					} else {
						str.append(", " + s);
					}
					i++;
				}
				str.append(".");
				sender.sendMessage(str.toString());
			}
			return true;
		}
		return false;
	}

	public void onEnable() {
		wg = (WorldGuardPlugin) this.getServer().getPluginManager()
				.getPlugin("WorldGuard");
		if (wg == null) {
			System.out.println("[WGFinder]" + ANSI_RED
					+ " WorldGuard not found." + ANSI_RESET);
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		// we = wg.getWorldEdit();
		we = (WorldEditPlugin) this.getServer().getPluginManager()
				.getPlugin("WorldEdit");
		if (we == null) {
			System.out.println("[WGFinder]" + ANSI_RED
					+ " WorldEdit not found." + ANSI_RESET);
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		System.out.println("[WGFinder]" + ANSI_GREEN + " Loaded!" + ANSI_RESET);
	}
}
