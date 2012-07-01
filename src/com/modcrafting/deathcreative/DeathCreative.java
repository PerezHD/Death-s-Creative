package com.modcrafting.deathcreative;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathCreative extends JavaPlugin implements Listener, CommandExecutor{
	HashSet<Player> mode = new HashSet<Player>();
	public void onEnable(){
		createDefaultConfiguration("config.yml");
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getCommand("creative").setExecutor(this);
	}
	@EventHandler
	public void onPlayerAction(PlayerInteractEvent event){
		if(event.getAction().equals(Action.RIGHT_CLICK_AIR)){
			if(!mode.contains(event.getPlayer())) return;
			YamlConfiguration config = (YamlConfiguration) this.getConfig();
			Player player = (Player) event.getPlayer();
			int row = 9;
			int size = 12;
			Inventory inventory = this.getServer().createInventory(player, row*size, config.getString("WindowLabel", "Creative"));
			//84
			if(!config.getBoolean("EnableCustomList", false)){
				Integer[] blocks = {1,2,3,4,5,7,9,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,27,28,30,33,35,41,42,43,44,45,46,47,48,49,50,52,53,54,55,56,57,58,60,61,64,65,66,67,69,70,72,73,75,77,78,79,80,81,82,84,85,86,87,88,89,90,92,93,96,98,101,102,103,106,107,108,109,110,111,112,113,114};
				for (Integer i: blocks){
					ItemStack item = new ItemStack(i, 1);
					if(item != null){
						inventory.addItem(item);
					}
				}
			}else{
				List<Integer> it = config.getIntegerList("Items");
				Iterator<Integer> iter = it.iterator();
				if(it.size() > 99) return;
				while(iter.hasNext()){
					ItemStack item = new ItemStack(iter.next(), 1);
					if(item != null){
						inventory.addItem(item);
					}
				}
			}
			player.openInventory(inventory);  
		}
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		if(event.getInventory().getName().contains("Creative")){
			ItemStack max = event.getCurrentItem();
			if(max != null) max.setAmount(max.getMaxStackSize());
			event.getWhoClicked().getInventory().addItem(max);
			event.setCancelled(true);
		}
	}
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(args.length > 0) return false;
			if(!player.hasPermission("deathcreative.use")){
				player.sendMessage(ChatColor.RED+"You do not have permission to do this.");
				return true;
			}
			if(mode.contains(player)){
				mode.remove(player);
				player.getInventory().clear();
				player.sendMessage(ChatColor.DARK_RED+"Creative Inventory Disabled!");
				return true;
			}
			player.sendMessage(ChatColor.GREEN+"Creative Inventory Enabled!");
			mode.add(player);
			return true;
		}else{
			if(args.length > 1 || args.length < 1) return false;
			mode.add(this.getServer().getPlayer(args[0]));
			return true;
		}
	}
	public void createDefaultConfiguration(String name) {
		new File("plugins/DeathCreative/").mkdir();
		File actual = new File(this.getDataFolder(), name);
		if (!actual.exists()) {
			InputStream input =	this.getClass().getResourceAsStream("/" + name);
			if (input != null) {
				FileOutputStream output = null;
				try {
					output = new FileOutputStream(actual);
					byte[] buf = new byte[8192];
					int length = 0;
					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}

					System.out.println(this.getDescription().getName()
							+ ": Default configuration file written: " + name);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (input != null)
							input.close();
					} catch (IOException e) {}

					try {
						if (output != null)
							output.close();
					} catch (IOException e) {}
				}
			}
		}
	}
}
	    
