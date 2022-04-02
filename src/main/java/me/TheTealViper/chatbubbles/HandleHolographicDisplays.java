package me.TheTealViper.chatbubbles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.massivecraft.factions.entity.MPlayer;

import me.TheTealViper.chatbubbles.utils.FormatUtils;

public class HandleHolographicDisplays implements Listener
{
	
	private ChatBubbles plugin;
	public final CBConfig config;
	public HashMap<UUID,List<Hologram>> existingHolograms;
    
	public HandleHolographicDisplays(ChatBubbles main) {
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
						if(!h.isDeleted())
							h.delete();
					}
				}
				final Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().add(0.0, config.bubbleOffset, 0.0));
				List<Hologram> hList = new ArrayList<Hologram>();
				hList.add(hologram);
				existingHolograms.put(p.getUniqueId(), hList);
				hologram.getVisibilityManager().setVisibleByDefault(false);
				for(Player oP : Bukkit.getOnlinePlayers()){
					if(((config.seeOwnBubble) || (config.seeOwnBubble && oP.getName() != p.getName())) 
							&& (oP.getWorld().getName().equals(p.getWorld().getName()) 
							&& oP.getLocation().distance(p.getLocation()) <= config.distance) 
							&& (!requirePerm || (requirePerm && oP.hasPermission(seePerm)))
							&& oP.canSee(p))
						hologram.getVisibilityManager().showTo(oP);
				}
				int lines = FormatUtils.formatHologramLines(p, hologram, message);

				new BukkitRunnable() {
					int ticksRun = 0;
					@Override
					public void run() {
						ticksRun++;
						if(!hologram.isDeleted())
							hologram.teleport(p.getLocation().add(0.0, config.bubbleOffset + .25 * lines, 0.0));
						if (ticksRun > config.life) {
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
						if(!h.isDeleted())
							h.delete();
					}
				}
				final Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().add(0.0, config.bubbleOffset, 0.0));
				List<Hologram> hList = new ArrayList<Hologram>();
				hList.add(hologram);
				existingHolograms.put(p.getUniqueId(), hList);
				hologram.getVisibilityManager().setVisibleByDefault(false);
				for(Player oP : Bukkit.getOnlinePlayers()){
					if(((config.seeOwnBubble) || (!config.seeOwnBubble && oP.getName() != p.getName())) 
							&& (oP.getWorld().getName().equals(p.getWorld().getName()) 
							&& oP.getLocation().distance(p.getLocation()) <= config.distance) 
							&& (!requirePerm || (requirePerm && oP.hasPermission(seePerm)))
							&& oP.canSee(p))
						hologram.getVisibilityManager().showTo(oP);
				}
				int lines = FormatUtils.formatHologramLines(p, hologram, message);
				if(sendOriginal)
					p.chat(message);

				new BukkitRunnable() {
					int ticksRun = 0;
					@Override
					public void run() {
						ticksRun++;
						if(!hologram.isDeleted())
							hologram.teleport(p.getLocation().add(0.0, config.bubbleOffset + .25 * lines, 0.0));
						if (ticksRun > config.life) {
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
						if(!h.isDeleted())
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
				final Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().add(0.0, config.bubbleOffset, 0.0));
				List<Hologram> hList = new ArrayList<Hologram>();
				hList.add(hologram);
				existingHolograms.put(p.getUniqueId(), hList);
				hologram.getVisibilityManager().setVisibleByDefault(false);
				for(Player oP : Bukkit.getOnlinePlayers()){
					if(((config.seeOwnBubble) || (!config.seeOwnBubble && oP.getName() != p.getName())) 
							&& (oP.getWorld().getName().equals(p.getWorld().getName()) 
							&& oP.getLocation().distance(p.getLocation()) <= config.distance) 
							&& (oP.hasPermission(permGroup))
							&& oP.canSee(p))
						hologram.getVisibilityManager().showTo(oP);
				}
				int lines = FormatUtils.formatHologramLines(p, hologram, message);
				if(sendOriginal)
					p.chat(message);

				new BukkitRunnable() {
					int ticksRun = 0;
					@Override
					public void run() {
						ticksRun++;
						if(!hologram.isDeleted())
							hologram.teleport(p.getLocation().add(0.0, config.bubbleOffset + .25 * lines, 0.0));
						if (ticksRun > config.life) {
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
	
	public void handleThree(String message, Player p){
		boolean sendOriginal = plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message");
		boolean requirePerm = plugin.getConfig().getBoolean("ConfigOne_Require_Permissions");
		String usePerm = plugin.getConfig().getString("ConfigOne_Use_Permission");
		new BukkitRunnable() {
			@Override
			public void run() {
				if(requirePerm && !p.hasPermission(usePerm))
					return;
				if(existingHolograms.containsKey(p.getUniqueId())) {
					for(Hologram h : existingHolograms.get(p.getUniqueId())) {
						if(!h.isDeleted())
							h.delete();
					}
				}
				MPlayer mPlayer = MPlayer.get(p);
				String faction = mPlayer.getFactionName();
				final Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().add(0.0, config.bubbleOffset, 0.0));
				List<Hologram> hList = new ArrayList<Hologram>();
				hList.add(hologram);
				existingHolograms.put(p.getUniqueId(), hList);
				hologram.getVisibilityManager().setVisibleByDefault(false);
				for(Player oP : Bukkit.getOnlinePlayers()){
					if(((config.seeOwnBubble) || (!config.seeOwnBubble && oP.getName() != p.getName())) 
							&& (oP.getWorld().getName().equals(p.getWorld().getName()) 
							&& oP.getLocation().distance(p.getLocation()) <= config.distance) 
							&& (MPlayer.get(oP).getFactionName().equals(faction))
							&& oP.canSee(p))
						hologram.getVisibilityManager().showTo(oP);
				}
				int lines = FormatUtils.formatHologramLines(p, hologram, message);
				if(sendOriginal)
					p.chat(message);

				new BukkitRunnable() {
					int ticksRun = 0;
					@Override
					public void run() {
						ticksRun++;
						if(!hologram.isDeleted())
							hologram.teleport(p.getLocation().add(0.0, config.bubbleOffset + .25 * lines, 0.0));
						if (ticksRun > config.life) {
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
}