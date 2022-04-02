package me.TheTealViper.chatbubbles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.TheTealViper.chatbubbles.utils.FormatUtils;
import net.md_5.bungee.api.ChatColor;

public class HandleDecentHolograms implements Listener
{
	
	private ChatBubbles plugin;
	public final CBConfig config;
	public HashMap<UUID,List<Hologram>> existingHolograms;
    
	public HandleDecentHolograms(ChatBubbles main) {
		this.plugin = main;
		this.config = plugin.getCBConfig();
		this.existingHolograms = new HashMap<UUID,List<Hologram>>();
	}
	
	public void handleZero(String message, Player p){
		boolean requirePerm = plugin.getConfig().getBoolean("ConfigZero_Require_Permissions");
		String usePerm = plugin.getConfig().getString("ConfigZero_Use_Permission");
		String seePerm = plugin.getConfig().getString("ConfigZero_See_Permission");
		new BukkitRunnable() {
			@Override
			public void run() {
				if(CBConfig.requirePerm) {
					if (!p.hasPermission(usePerm))
						return;
				}
				if(!config.togglePF.getBoolean(p.getUniqueId().toString()))
					return;
				if(existingHolograms.containsKey(p.getUniqueId())) {
					for(Hologram h : existingHolograms.get(p.getUniqueId())) {
						if(!h.isEnabled())
							h.delete();
					}
				}
				final Hologram hologram = DHAPI.createHologram(FormatUtils.getRandomString(), p.getLocation().add(0.0, config.bubbleOffset, 0.0), false);
				List<Hologram> hList = new ArrayList<Hologram>();
				hList.add(hologram);
				existingHolograms.put(p.getUniqueId(), hList);
				//figure this mess out
				if(requirePerm) {
					hologram.setPermission(seePerm);
				}
				int lines = formatHologramLines(p, hologram, message);
				new BukkitRunnable() {
					int ticksRun = 0;
					@Override
					public void run() {
						ticksRun++;
						if(hologram.isEnabled())
							DHAPI.moveHologram(hologram, p.getLocation().add(0.0, config.bubbleOffset + .25 * lines, 0.0));
						if (ticksRun > config.life) {
							hologram.disable();
							hologram.delete();
							cancel();
						}
				}}.runTaskTimer(plugin, 1L, 1L);
				
				if(plugin.getConfig().getBoolean("ChatBubble_Play_Sound")) {
					String sound = plugin.getConfig().getString("ChatBubble_Sound_Name").toLowerCase();
					float volume = (float) plugin.getConfig().getDouble("ChatBubble_Sound_Volume");
					if(!sound.equals("")) {
						try {
							p.getWorld().playSound(p.getLocation(), sound, volume, 1.0f);
						}catch(Exception e) {
							plugin.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
							plugin.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
						}
					}
				}
		}}.runTask(plugin);
		
	}
	
	public void handleOne(String message, Player p){
		boolean sendOriginal = plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message");
		boolean requirePerm = plugin.getConfig().getBoolean("ConfigOne_Require_Permissions");
		String usePerm = plugin.getConfig().getString("ConfigOne_Use_Permission");
		String seePerm = plugin.getConfig().getString("ConfigOne_See_Permission");
		new BukkitRunnable() {
			@Override
			public void run() {
				if(requirePerm && !p.hasPermission(usePerm))
					return;
				if(!config.togglePF.getBoolean(p.getUniqueId().toString()))
					return;
				if(existingHolograms.containsKey(p.getUniqueId())) {
					for(Hologram h : existingHolograms.get(p.getUniqueId())) {
						if(!h.isEnabled())
							h.delete();
					}
				}
				final Hologram hologram = DHAPI.createHologram(FormatUtils.getRandomString(), p.getLocation().add(0.0, config.bubbleOffset, 0.0), false);
				List<Hologram> hList = new ArrayList<Hologram>();
				hList.add(hologram);
				existingHolograms.put(p.getUniqueId(), hList);
				if(requirePerm) {
					hologram.setPermission(seePerm);
				}
				int lines = formatHologramLines(p, hologram, message);
				if(sendOriginal)
					p.chat(message);

				new BukkitRunnable() {
					int ticksRun = 0;
					@Override
					public void run() {
						ticksRun++;
						if(hologram.isEnabled())
							DHAPI.moveHologram(hologram, p.getLocation().add(0.0, config.bubbleOffset + .25 * lines, 0.0));
						if (ticksRun > config.life) {
							hologram.disable();
							hologram.delete();
							cancel();
						}
				}}.runTaskTimer(plugin, 1L, 1L);
				
				if(plugin.getConfig().getBoolean("ChatBubble_Play_Sound")) {
					String sound = plugin.getConfig().getString("ChatBubble_Sound_Name").toLowerCase();
					float volume = (float) plugin.getConfig().getDouble("ChatBubble_Sound_Volume");
					if(!sound.equals("")) {
						try {
							p.getWorld().playSound(p.getLocation(), sound, volume, 1.0f);
						}catch(Exception e) {
							plugin.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
							plugin.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
						}
					}
				}	
		}}.runTask(plugin);
		
	}
	
	public void handleTwo(String message, Player p){
		boolean sendOriginal = plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message");
		boolean requirePerm = plugin.getConfig().getBoolean("ConfigOne_Require_Permissions");
		String usePerm = plugin.getConfig().getString("ConfigOne_Use_Permission");
		new BukkitRunnable() {
			@Override
			public void run() {
				if(requirePerm && !p.hasPermission(usePerm))
					return;
				if(!config.togglePF.getBoolean(p.getUniqueId().toString()))
					return;
				if(existingHolograms.containsKey(p.getUniqueId())) {
					for(Hologram h : existingHolograms.get(p.getUniqueId())) {
						if(!h.isEnabled())
							h.delete();
					}
				}
				String permGroup = "";
				for(String testPerm : plugin.getConfig().getStringList("ConfigTwo_Permision_Groups")){
					if(p.hasPermission(testPerm)){
						permGroup = testPerm;
						break;
					}
				}
				if(permGroup.equals(""))
					return;
				final Hologram hologram = DHAPI.createHologram(FormatUtils.getRandomString(), p.getLocation().add(0.0, config.bubbleOffset, 0.0), false);
				List<Hologram> hList = new ArrayList<Hologram>();
				hList.add(hologram);
				existingHolograms.put(p.getUniqueId(), hList);
				hologram.setPermission(permGroup);
				int lines = formatHologramLines(p, hologram, message);
				if(sendOriginal)
					p.chat(message);

				new BukkitRunnable() {
					int ticksRun = 0;
					@Override
					public void run() {
						ticksRun++;
						if(hologram.isEnabled())
							DHAPI.moveHologram(hologram, p.getLocation().add(0.0, config.bubbleOffset + .25 * lines, 0.0));
						if (ticksRun > config.life) {
							hologram.disable();
							hologram.delete();
							cancel();
						}
				}}.runTaskTimer(plugin, 1L, 1L);
				
				if(plugin.getConfig().getBoolean("ChatBubble_Play_Sound")) {
					String sound = plugin.getConfig().getString("ChatBubble_Sound_Name").toLowerCase();
					float volume = (float) plugin.getConfig().getDouble("ChatBubble_Sound_Volume");
					if(!sound.equals("")) {
						try {
							p.getWorld().playSound(p.getLocation(), sound, volume, 1.0f);
						}catch(Exception e) {
							plugin.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
							plugin.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
						}
					}
				}	
		}}.runTask(plugin);
		
	}
	
	public void handleFour(String message, Player p){
		boolean requirePerm = plugin.getConfig().getBoolean("ConfigZero_Require_Permissions");
		String usePerm = plugin.getConfig().getString("ConfigZero_Use_Permission");
		new BukkitRunnable() {
			@Override
			public void run() {
				if(requirePerm && !p.hasPermission(usePerm))
					return;
				if(!config.togglePF.getBoolean(p.getUniqueId().toString()))
					return;
				
				if(plugin.getConfig().getBoolean("ChatBubble_Play_Sound")) {
					String sound = plugin.getConfig().getString("ChatBubble_Sound_Name").toLowerCase();
					float volume = (float) plugin.getConfig().getDouble("ChatBubble_Sound_Volume");
					if(!sound.equals("")) {
						try {
							p.getWorld().playSound(p.getLocation(), sound, volume, 1.0f);
						}catch(Exception e) {
							plugin.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
							plugin.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
						}
					}
				}	
		}}.runTask(plugin);
		
	}
	
	public int formatHologramLines(Player p, Hologram hologram, String message){
		List<String> lineList = new ArrayList<String>();
		for(String formatLine : plugin.getConfig().getStringList("ChatBubble_Message_Format")){
			boolean addedToLine = false;
			if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
				formatLine = placeholderShit.formatString(p, formatLine);
			if(formatLine.contains("%chatbubble_message%")){
				addedToLine = true;
				formatLine = formatLine.replace("%chatbubble_message%", message);
				
				for(String s : formatLine.split(" ")){
					if(s.length() > plugin.getCBConfig().length){
						String insert = "-\n";
						int period = plugin.getCBConfig().length - 1;
						StringBuilder builder = new StringBuilder(
						         s.length() + insert.length() * (s.length()/plugin.getCBConfig().length)+1);

						    int index = 0;
						    String prefix = "";
						    while (index < s.length())
						    {
						        // Don't put the insert in the very first iteration.
						        // This is easier than appending it *after* each substring
						        builder.append(prefix);
						        prefix = insert;
						        builder.append(s.substring(index, 
						            Math.min(index + period, s.length())));
						        index += period;
						    }
						String replacement = builder.toString();
						formatLine = formatLine.replace(s, replacement);
						message = message.replace(s, replacement);
					}
				}
				
				StringBuilder sb = new StringBuilder(formatLine.replace(message, "") + message);
				int i = 0;
				while (i + plugin.getCBConfig().length < sb.length() && (i = sb.lastIndexOf(" ", i + plugin.getCBConfig().length)) != -1) {
				    sb.replace(i, i + 1, "\n");
				}
				for(String s : sb.toString().split("\\n")){
					if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
						s = FormatUtils.makeColors(s);
						if(plugin.getConfig().getBoolean("ChatBubble_Strip_Formatting"))
							s = ChatColor.stripColor(s);
						s = placeholderShit.formatString(p, plugin.getCBConfig().prefix + s + plugin.getCBConfig().suffix);
						s = FormatUtils.makeColors(s);
						lineList.add(s);
					} else {
						s = FormatUtils.makeColors(s);
						if(plugin.getConfig().getBoolean("ChatBubble_Strip_Formatting"))
							s = ChatColor.stripColor(s);
						s = FormatUtils.makeColors(plugin.getCBConfig().prefix + s + plugin.getCBConfig().suffix);
						lineList.add(s);
					}
				}
			}
			if(!addedToLine) {
				if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
					formatLine = FormatUtils.makeColors(formatLine);
					if(plugin.getConfig().getBoolean("ChatBubble_Strip_Formatting"))
						formatLine = ChatColor.stripColor(formatLine);
					formatLine = placeholderShit.formatString(p, plugin.getCBConfig().prefix + formatLine + plugin.getCBConfig().suffix);
					formatLine = FormatUtils.makeColors(formatLine);
					lineList.add(formatLine);
				}	else {
					formatLine = FormatUtils.makeColors(formatLine);
					if(plugin.getConfig().getBoolean("ChatBubble_Strip_Formatting"))
						formatLine = ChatColor.stripColor(formatLine);
					formatLine = FormatUtils.makeColors(plugin.getCBConfig().prefix + formatLine + plugin.getCBConfig().suffix);
					lineList.add(formatLine);
				}
			}
		}
		for(String s : lineList)
			DHAPI.addHologramLine(hologram, s);
		return lineList.size();
	}
	
	public int formatHologramLines(LivingEntity p, Hologram hologram, String message){
		List<String> lineList = new ArrayList<String>();
		for(String formatLine : plugin.getConfig().getStringList("ChatBubble_Message_Format")){
			if(formatLine.contains("%player_name%"))
				formatLine = formatLine.replace("%player_name%", p.getCustomName());
			boolean addedToLine = false;
			if(formatLine.contains("%chatbubble_message%")){		
				addedToLine = true;
				formatLine = formatLine.replace("%chatbubble_message%", message);
				
				for(String s : formatLine.split(" ")){
					if(s.length() > plugin.getCBConfig().length){
						String insert = "-\n";
						int period = plugin.getCBConfig().length - 1;
						StringBuilder builder = new StringBuilder(
						         s.length() + insert.length() * (s.length()/plugin.getCBConfig().length)+1);

						    int index = 0;
						    String prefix = "";
						    while (index < s.length())
						    {
						        // Don't put the insert in the very first iteration.
						        // This is easier than appending it *after* each substring
						        builder.append(prefix);
						        prefix = insert;
						        builder.append(s.substring(index, 
						            Math.min(index + period, s.length())));
						        index += period;
						    }
						String replacement = builder.toString();
						formatLine = formatLine.replace(s, replacement);
						message = message.replace(s, replacement);
					}
				}
				
				StringBuilder sb = new StringBuilder(formatLine.replace(message, "") + message);
				int i = 0;
				while (i + plugin.getCBConfig().length < sb.length() && (i = sb.lastIndexOf(" ", i + plugin.getCBConfig().length)) != -1) {
				    sb.replace(i, i + 1, "\n");
				}
				for(String s : sb.toString().split("\\n")){
					s = FormatUtils.makeColors(s);
					s = FormatUtils.makeColors(plugin.getCBConfig().prefix + s + plugin.getCBConfig().suffix);
					lineList.add(s);				
				}
			}
			if(!addedToLine) {
				formatLine = FormatUtils.makeColors(formatLine);
				formatLine = FormatUtils.makeColors(plugin.getCBConfig().prefix + formatLine + plugin.getCBConfig().suffix);
				lineList.add(formatLine);			
			}
		}
		for(String s : lineList)
			DHAPI.addHologramLine(hologram, s);
		return lineList.size();
	}
	
}