package me.TheTealViper.chatbubbles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.TheTealViper.chatbubbles.utils.EnableShit;
import me.TheTealViper.chatbubbles.utils.PluginFile;
import me.TheTealViper.chatbubbles.listeners.ChatListenerHigh;
import me.TheTealViper.chatbubbles.listeners.ChatListenerHighest;
import me.TheTealViper.chatbubbles.listeners.ChatListenerLow;
import me.TheTealViper.chatbubbles.listeners.ChatListenerLowest;
import me.TheTealViper.chatbubbles.listeners.ChatListenerMonitor;
import me.TheTealViper.chatbubbles.listeners.ChatListenerNormal;
import me.TheTealViper.chatbubbles.utils.ChatBubbleTrait;
import net.md_5.bungee.api.ChatColor;

public class ChatBubbles extends JavaPlugin implements Listener{
	public int life = -1, distance = -1, length = -1;
	public String prefix = "", suffix = "";
	public boolean seeOwnBubble = false;
	private boolean useTrait = true;
	public boolean chatBubbleOverridesNPCChat;
	public double bubbleOffset = 2.5;
	public EventPriority chatPriority = EventPriority.NORMAL;
	private PluginFile togglePF;
	private ChatBubbles plugin;
	
	public Map<UUID, List<Hologram>> existingHolograms = new HashMap<UUID, List<Hologram>>();
	
	public void onEnable(){
		EnableShit.handleOnEnable(this, this, "49387");
		life = getConfig().getInt("ChatBubble_Life");
		distance = getConfig().getInt("ChatBubble_Viewing_Distance");
		length = getConfig().getInt("ChatBubble_Maximum_Line_Length");
		prefix = getConfig().getString("ChatBubble_Message_Prefix");
		if(prefix == null)
			prefix = "";
		suffix = getConfig().getString("ChatBubble_Message_Suffix");
		if(suffix == null)
			suffix = "";
		seeOwnBubble = getConfig().getBoolean("ChatBubble_See_Own_Bubbles");
		bubbleOffset = getConfig().getDouble("ChatBubble_Height_Offset");
		useTrait = getConfig().getBoolean("Use_ChatBubble_Trait_Citizens");
		chatBubbleOverridesNPCChat = getConfig().getBoolean("ChatBubble_Overrides_NPC_Chat");
		chatPriority = EventPriority.valueOf(getConfig().getString("ChatBubble_Chat_Priority").toUpperCase());
		togglePF = new PluginFile(this, "toggleData");
		if(getServer().getPluginManager().getPlugin("Citizens") != null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == true || useTrait) {
			Bukkit.getServer().getConsoleSender().sendMessage("Citizens found and trait chatbubble enabled");
			net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(ChatBubbleTrait.class).withName("chatbubble"));		
		}
		this.plugin = this;
		setChatPriority(chatPriority);		
	}
	
	public void onDisable(){
		if(getServer().getPluginManager().getPlugin("Citizens") != null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == true || useTrait)
		getServer().getConsoleSender().sendMessage(makeColors("ChatBubbles from TheTealViper shutting down. Bshzzzzzz"));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(label.equalsIgnoreCase("chatbubble") || label.equalsIgnoreCase("cb")){
				if(args.length == 0){
					return false;
				}else{
					String message = "";
					for(int i = 0;i < args.length;i++)
						if(i == args.length - 1)
							message += args[i];
						else
							message += args[i] + " ";
					handleOne(message, p);
				}
			}
			if((label.equalsIgnoreCase("chatbubblereload") || label.equalsIgnoreCase("cbreload")) && p.hasPermission("chatbubbles.reload")){
				reloadConfig();
				p.sendMessage("Reloaded Successfully");
			}
			if((label.equalsIgnoreCase("chatbubbletoggle") || label.equalsIgnoreCase("cbtoggle") || label.equalsIgnoreCase("cbt")) && p.hasPermission("chatbubbles.toggle")) {
				boolean currentState = togglePF.getBoolean(p.getUniqueId().toString());
				if(currentState)
					p.sendMessage("ChatBubbles toggled off!");
				else
					p.sendMessage("ChatBubbles toggled on!");
				togglePF.set(p.getUniqueId().toString(), !currentState);
				togglePF.save();
			}
		}
		return false;
	}
	
	private void setChatPriority(EventPriority priority) {
		switch (priority) {
			case LOWEST:
				this.getServer().getPluginManager().registerEvents(new ChatListenerLowest(this), this);
			case LOW:
				this.getServer().getPluginManager().registerEvents(new ChatListenerLow(this), this);
			case NORMAL:
				this.getServer().getPluginManager().registerEvents(new ChatListenerNormal(this), this);
			case HIGH:
				this.getServer().getPluginManager().registerEvents(new ChatListenerHigh(this), this);
			case HIGHEST:
				this.getServer().getPluginManager().registerEvents(new ChatListenerHighest(this), this);
			case MONITOR:
				this.getServer().getPluginManager().registerEvents(new ChatListenerMonitor(this), this);
			default:
				this.getServer().getPluginManager().registerEvents(new ChatListenerNormal(this), this);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(!togglePF.contains(p.getUniqueId().toString())) {
			togglePF.set(p.getUniqueId().toString(), true);
			togglePF.save();
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		existingHolograms.remove(e.getPlayer().getUniqueId());
	}
	
	public void handleZero(String message, Player p){
		boolean requirePerm = getConfig().getBoolean("ConfigZero_Require_Permissions");
		String usePerm = getConfig().getString("ConfigZero_Use_Permission");
		String seePerm = getConfig().getString("ConfigZero_See_Permission");
		new BukkitRunnable() {
			@Override
			public void run() {
				if(requirePerm && !p.hasPermission(usePerm))
					return;
				if(!togglePF.getBoolean(p.getUniqueId().toString()))
					return;
				if(existingHolograms.containsKey(p.getUniqueId())) {
					for(Hologram h : existingHolograms.get(p.getUniqueId())) {
						if(!h.isDeleted())
							h.delete();
					}
				}
				final Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().add(0.0, bubbleOffset, 0.0));
				List<Hologram> hList = new ArrayList<Hologram>();
				hList.add(hologram);
				existingHolograms.put(p.getUniqueId(), hList);
				hologram.getVisibilityManager().setVisibleByDefault(false);
				for(Player oP : Bukkit.getOnlinePlayers()){
					if(((seeOwnBubble) || (!seeOwnBubble && oP.getName() != p.getName())) 
							&& (oP.getWorld().getName().equals(p.getWorld().getName()) 
							&& oP.getLocation().distance(p.getLocation()) <= distance) 
							&& (!requirePerm || (requirePerm && oP.hasPermission(seePerm)))
							&& oP.canSee(p))
						hologram.getVisibilityManager().showTo(oP);
				}
				int lines = formatHologramLines(p, hologram, message);

				new BukkitRunnable() {
					int ticksRun = 0;
					@Override
					public void run() {
						ticksRun++;
						if(!hologram.isDeleted())
							hologram.teleport(p.getLocation().add(0.0, bubbleOffset + .25 * lines, 0.0));
						if (ticksRun > life) {
							hologram.delete();
							cancel();
						}
				}}.runTaskTimer(plugin, 1L, 1L);
				
				if(getConfig().getBoolean("ChatBubble_Play_Sound")) {
					String sound = getConfig().getString("ChatBubble_Sound_Name").toLowerCase();
					float volume = (float) getConfig().getDouble("ChatBubble_Sound_Volume");
					if(!sound.equals("")) {
						try {
							p.getWorld().playSound(p.getLocation(), sound, volume, 1.0f);
						}catch(Exception e) {
							getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
							getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
						}
					}
				}
		}}.runTask(this);
		
	}
	
	public void handleOne(String message, Player p){
		boolean sendOriginal = getConfig().getBoolean("ChatBubble_Send_Original_Message");
		boolean requirePerm = getConfig().getBoolean("ConfigOne_Require_Permissions");
		String usePerm = getConfig().getString("ConfigOne_Use_Permission");
		String seePerm = getConfig().getString("ConfigOne_See_Permission");
		new BukkitRunnable() {
			@Override
			public void run() {
				if(requirePerm && !p.hasPermission(usePerm))
					return;
				if(!togglePF.getBoolean(p.getUniqueId().toString()))
					return;
				if(existingHolograms.containsKey(p.getUniqueId())) {
					for(Hologram h : existingHolograms.get(p.getUniqueId())) {
						if(!h.isDeleted())
							h.delete();
					}
				}
				final Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().add(0.0, bubbleOffset, 0.0));
				List<Hologram> hList = new ArrayList<Hologram>();
				hList.add(hologram);
				existingHolograms.put(p.getUniqueId(), hList);
				hologram.getVisibilityManager().setVisibleByDefault(false);
				for(Player oP : Bukkit.getOnlinePlayers()){
					if(((seeOwnBubble) || (!seeOwnBubble && oP.getName() != p.getName())) 
							&& (oP.getWorld().getName().equals(p.getWorld().getName()) 
							&& oP.getLocation().distance(p.getLocation()) <= distance) 
							&& (!requirePerm || (requirePerm && oP.hasPermission(seePerm)))
							&& oP.canSee(p))
						hologram.getVisibilityManager().showTo(oP);
				}
				int lines = formatHologramLines(p, hologram, message);
				if(sendOriginal)
					p.chat(message);

				new BukkitRunnable() {
					int ticksRun = 0;
					@Override
					public void run() {
						ticksRun++;
						if(!hologram.isDeleted())
							hologram.teleport(p.getLocation().add(0.0, bubbleOffset + .25 * lines, 0.0));
						if (ticksRun > life) {
							hologram.delete();
							cancel();
						}
				}}.runTaskTimer(plugin, 1L, 1L);
				
				if(getConfig().getBoolean("ChatBubble_Play_Sound")) {
					String sound = getConfig().getString("ChatBubble_Sound_Name").toLowerCase();
					float volume = (float) getConfig().getDouble("ChatBubble_Sound_Volume");
					if(!sound.equals("")) {
						try {
							p.getWorld().playSound(p.getLocation(), sound, volume, 1.0f);
						}catch(Exception e) {
							getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
							getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
						}
					}
				}	
		}}.runTask(this);
		
	}
	
	public void handleTwo(String message, Player p){
		boolean sendOriginal = getConfig().getBoolean("ChatBubble_Send_Original_Message");
		boolean requirePerm = getConfig().getBoolean("ConfigOne_Require_Permissions");
		String usePerm = getConfig().getString("ConfigOne_Use_Permission");
		new BukkitRunnable() {
			@Override
			public void run() {
				if(requirePerm && !p.hasPermission(usePerm))
					return;
				if(!togglePF.getBoolean(p.getUniqueId().toString()))
					return;
				if(existingHolograms.containsKey(p.getUniqueId())) {
					for(Hologram h : existingHolograms.get(p.getUniqueId())) {
						if(!h.isDeleted())
							h.delete();
					}
				}
				String permGroup = "";
				for(String testPerm : getConfig().getStringList("ConfigTwo_Permision_Groups")){
					if(p.hasPermission(testPerm)){
						permGroup = testPerm;
						break;
					}
				}
				if(permGroup.equals(""))
					return;
				final Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().add(0.0, bubbleOffset, 0.0));
				List<Hologram> hList = new ArrayList<Hologram>();
				hList.add(hologram);
				existingHolograms.put(p.getUniqueId(), hList);
				hologram.getVisibilityManager().setVisibleByDefault(false);
				for(Player oP : Bukkit.getOnlinePlayers()){
					if(((seeOwnBubble) || (!seeOwnBubble && oP.getName() != p.getName())) 
							&& (oP.getWorld().getName().equals(p.getWorld().getName()) 
							&& oP.getLocation().distance(p.getLocation()) <= distance) 
							&& (oP.hasPermission(permGroup))
							&& oP.canSee(p))
						hologram.getVisibilityManager().showTo(oP);
				}
				int lines = formatHologramLines(p, hologram, message);
				if(sendOriginal)
					p.chat(message);

				new BukkitRunnable() {
					int ticksRun = 0;
					@Override
					public void run() {
						ticksRun++;
						if(!hologram.isDeleted())
							hologram.teleport(p.getLocation().add(0.0, bubbleOffset + .25 * lines, 0.0));
						if (ticksRun > life) {
							hologram.delete();
							cancel();
						}
				}}.runTaskTimer(plugin, 1L, 1L);
				
				if(getConfig().getBoolean("ChatBubble_Play_Sound")) {
					String sound = getConfig().getString("ChatBubble_Sound_Name").toLowerCase();
					float volume = (float) getConfig().getDouble("ChatBubble_Sound_Volume");
					if(!sound.equals("")) {
						try {
							p.getWorld().playSound(p.getLocation(), sound, volume, 1.0f);
						}catch(Exception e) {
							getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
							getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
						}
					}
				}	
		}}.runTask(this);
		
	}
	
	public void handleFour(String message, Player p){
		boolean requirePerm = getConfig().getBoolean("ConfigZero_Require_Permissions");
		String usePerm = getConfig().getString("ConfigZero_Use_Permission");
		new BukkitRunnable() {
			@Override
			public void run() {
				if(requirePerm && !p.hasPermission(usePerm))
					return;
				if(!togglePF.getBoolean(p.getUniqueId().toString()))
					return;
				
				if(getConfig().getBoolean("ChatBubble_Play_Sound")) {
					String sound = getConfig().getString("ChatBubble_Sound_Name").toLowerCase();
					float volume = (float) getConfig().getDouble("ChatBubble_Sound_Volume");
					if(!sound.equals("")) {
						try {
							p.getWorld().playSound(p.getLocation(), sound, volume, 1.0f);
						}catch(Exception e) {
							getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
							getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
						}
					}
				}	
		}}.runTask(this);
		
	}
	
//------------------------Utilities--------------------------------
	
	public int formatHologramLines(Player p, Hologram hologram, String message){
		List<String> lineList = new ArrayList<String>();
		for(String formatLine : getConfig().getStringList("ChatBubble_Message_Format")){
			boolean addedToLine = false;
			if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
				formatLine = placeholderShit.formatString(p, formatLine);
			if(formatLine.contains("%chatbubble_message%")){
				addedToLine = true;
				formatLine = formatLine.replace("%chatbubble_message%", message);
				
				for(String s : formatLine.split(" ")){
					if(s.length() > length){
						String insert = "-\n";
						int period = length - 1;
						StringBuilder builder = new StringBuilder(
						         s.length() + insert.length() * (s.length()/length)+1);

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
				while (i + length < sb.length() && (i = sb.lastIndexOf(" ", i + length)) != -1) {
				    sb.replace(i, i + 1, "\n");
				}
				for(String s : sb.toString().split("\\n")){
					if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
						s = makeColors(s);
						if(getConfig().getBoolean("ChatBubble_Strip_Formatting"))
							s = ChatColor.stripColor(s);
						s = placeholderShit.formatString(p, prefix + s + suffix);
						s = makeColors(s);
						lineList.add(s);
					} else {
						s = makeColors(s);
						if(getConfig().getBoolean("ChatBubble_Strip_Formatting"))
							s = ChatColor.stripColor(s);
						s = makeColors(prefix + s + suffix);
						lineList.add(s);
					}
				}
			}
			if(!addedToLine) {
				if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
					formatLine = makeColors(formatLine);
					if(getConfig().getBoolean("ChatBubble_Strip_Formatting"))
						formatLine = ChatColor.stripColor(formatLine);
					formatLine = placeholderShit.formatString(p, prefix + formatLine + suffix);
					formatLine = makeColors(formatLine);
					lineList.add(formatLine);
				}	else {
					formatLine = makeColors(formatLine);
					if(getConfig().getBoolean("ChatBubble_Strip_Formatting"))
						formatLine = ChatColor.stripColor(formatLine);
					formatLine = makeColors(prefix + formatLine + suffix);
					lineList.add(formatLine);
				}
			}
		}
		for(String s : lineList)
			hologram.appendTextLine(s);
		return lineList.size();
	}
	
	public int formatHologramLines(LivingEntity p, Hologram hologram, String message){
		List<String> lineList = new ArrayList<String>();
		for(String formatLine : getConfig().getStringList("ChatBubble_Message_Format")){
			if(formatLine.contains("%player_name%"))
				formatLine = formatLine.replace("%player_name%", p.getCustomName());
			boolean addedToLine = false;
			if(formatLine.contains("%chatbubble_message%")){		
				addedToLine = true;
				formatLine = formatLine.replace("%chatbubble_message%", message);
				
				for(String s : formatLine.split(" ")){
					if(s.length() > length){
						String insert = "-\n";
						int period = length - 1;
						StringBuilder builder = new StringBuilder(
						         s.length() + insert.length() * (s.length()/length)+1);

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
				while (i + length < sb.length() && (i = sb.lastIndexOf(" ", i + length)) != -1) {
				    sb.replace(i, i + 1, "\n");
				}
				for(String s : sb.toString().split("\\n")){
					s = makeColors(s);
					s = makeColors(prefix + s + suffix);
					lineList.add(s);				
				}
			}
			if(!addedToLine) {
				formatLine = makeColors(formatLine);
				formatLine = makeColors(prefix + formatLine + suffix);
				lineList.add(formatLine);			
			}
		}
		for(String s : lineList)
			hologram.appendTextLine(s);
		return lineList.size();
	}
	
	public final static Pattern HEXPAT = Pattern.compile("&#[a-fA-F0-9]{6}");
	public static String makeColors(String s){
		//Handle standard basic colors
		while(s.contains("&0"))s = s.replace("&0", ChatColor.BLACK + "");
		while(s.contains("&1"))s = s.replace("&1", ChatColor.DARK_BLUE + "");
		while(s.contains("&2"))s = s.replace("&2", ChatColor.DARK_GREEN + "");
		while(s.contains("&3"))s = s.replace("&3", ChatColor.DARK_AQUA + "");
		while(s.contains("&4"))s = s.replace("&4", ChatColor.DARK_RED + "");
		while(s.contains("&5"))s = s.replace("&5", ChatColor.DARK_PURPLE + "");
		while(s.contains("&6"))s = s.replace("&6", ChatColor.GOLD + "");
		while(s.contains("&7"))s = s.replace("&7", ChatColor.GRAY + "");
		while(s.contains("&8"))s = s.replace("&8", ChatColor.DARK_GRAY + "");
		while(s.contains("&9"))s = s.replace("&9", ChatColor.BLUE + "");
		while(s.contains("&a"))s = s.replace("&a", ChatColor.GREEN + "");
		while(s.contains("&b"))s = s.replace("&b", ChatColor.AQUA + "");
		while(s.contains("&c"))s = s.replace("&c", ChatColor.RED + "");
		while(s.contains("&d"))s = s.replace("&d", ChatColor.LIGHT_PURPLE + "");
		while(s.contains("&e"))s = s.replace("&e", ChatColor.YELLOW + "");
		while(s.contains("&f"))s = s.replace("&f", ChatColor.WHITE + "");
		while(s.contains("&k"))s = s.replace("&k", ChatColor.MAGIC + "");
		while(s.contains("&l"))s = s.replace("&l", ChatColor.BOLD + "");
		while(s.contains("&m"))s = s.replace("&m", ChatColor.STRIKETHROUGH + "");
		while(s.contains("&n"))s = s.replace("&n", ChatColor.UNDERLINE + "");
		while(s.contains("&o"))s = s.replace("&o", ChatColor.ITALIC + "");
		while(s.contains("&r"))s = s.replace("&r", ChatColor.RESET + "");
		//Handle custom hex codes (1.16 and up)
        Matcher match = HEXPAT.matcher(s);
        while(match.find()) {
        	String color = s.substring(match.start(), match.end());
        	s = s.replace(color, ChatColor.of(color.replace("&", "")) + "");
        }
        
		return s;
	}
	
}
