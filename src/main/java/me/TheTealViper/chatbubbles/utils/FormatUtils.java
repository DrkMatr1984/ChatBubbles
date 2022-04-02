package me.TheTealViper.chatbubbles.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import me.TheTealViper.chatbubbles.ChatBubbles;
import me.TheTealViper.chatbubbles.placeholderShit;
import net.md_5.bungee.api.ChatColor;

public class FormatUtils
{
	//------------------------Utilities--------------------------------
	
		public static int formatHologramLines(Player p, Hologram hologram, String message){
			List<String> lineList = new ArrayList<String>();
			for(String formatLine : ChatBubbles.getPlugin().getConfig().getStringList("ChatBubble_Message_Format")){
				boolean addedToLine = false;
				if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
					formatLine = placeholderShit.formatString(p, formatLine);
				if(formatLine.contains("%chatbubble_message%")){
					addedToLine = true;
					formatLine = formatLine.replace("%chatbubble_message%", message);
					
					for(String s : formatLine.split(" ")){
						if(s.length() > ChatBubbles.getPlugin().getCBConfig().length){
							String insert = "-\n";
							int period = ChatBubbles.getPlugin().getCBConfig().length - 1;
							StringBuilder builder = new StringBuilder(
							         s.length() + insert.length() * (s.length()/ChatBubbles.getPlugin().getCBConfig().length)+1);

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
					while (i + ChatBubbles.getPlugin().getCBConfig().length < sb.length() && (i = sb.lastIndexOf(" ", i + ChatBubbles.getPlugin().getCBConfig().length)) != -1) {
					    sb.replace(i, i + 1, "\n");
					}
					for(String s : sb.toString().split("\\n")){
						if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
							s = makeColors(s);
							if(ChatBubbles.getPlugin().getConfig().getBoolean("ChatBubble_Strip_Formatting"))
								s = ChatColor.stripColor(s);
							s = placeholderShit.formatString(p, ChatBubbles.getPlugin().getCBConfig().prefix + s + ChatBubbles.getPlugin().getCBConfig().suffix);
							s = makeColors(s);
							lineList.add(s);
						} else {
							s = makeColors(s);
							if(ChatBubbles.getPlugin().getConfig().getBoolean("ChatBubble_Strip_Formatting"))
								s = ChatColor.stripColor(s);
							s = makeColors(ChatBubbles.getPlugin().getCBConfig().prefix + s + ChatBubbles.getPlugin().getCBConfig().suffix);
							lineList.add(s);
						}
					}
				}
				if(!addedToLine) {
					if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
						formatLine = makeColors(formatLine);
						if(ChatBubbles.getPlugin().getConfig().getBoolean("ChatBubble_Strip_Formatting"))
							formatLine = ChatColor.stripColor(formatLine);
						formatLine = placeholderShit.formatString(p, ChatBubbles.getPlugin().getCBConfig().prefix + formatLine + ChatBubbles.getPlugin().getCBConfig().suffix);
						formatLine = makeColors(formatLine);
						lineList.add(formatLine);
					}	else {
						formatLine = makeColors(formatLine);
						if(ChatBubbles.getPlugin().getConfig().getBoolean("ChatBubble_Strip_Formatting"))
							formatLine = ChatColor.stripColor(formatLine);
						formatLine = makeColors(ChatBubbles.getPlugin().getCBConfig().prefix + formatLine + ChatBubbles.getPlugin().getCBConfig().suffix);
						lineList.add(formatLine);
					}
				}
			}
			for(String s : lineList)
				hologram.appendTextLine(s);
			return lineList.size();
		}
		
		public static int formatHologramLines(LivingEntity p, Hologram hologram, String message){
			List<String> lineList = new ArrayList<String>();
			for(String formatLine : ChatBubbles.getPlugin().getConfig().getStringList("ChatBubble_Message_Format")){
				if(formatLine.contains("%player_name%"))
					formatLine = formatLine.replace("%player_name%", p.getCustomName());
				boolean addedToLine = false;
				if(formatLine.contains("%chatbubble_message%")){		
					addedToLine = true;
					formatLine = formatLine.replace("%chatbubble_message%", message);
					
					for(String s : formatLine.split(" ")){
						if(s.length() > ChatBubbles.getPlugin().getCBConfig().length){
							String insert = "-\n";
							int period = ChatBubbles.getPlugin().getCBConfig().length - 1;
							StringBuilder builder = new StringBuilder(
							         s.length() + insert.length() * (s.length()/ChatBubbles.getPlugin().getCBConfig().length)+1);

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
					while (i + ChatBubbles.getPlugin().getCBConfig().length < sb.length() && (i = sb.lastIndexOf(" ", i + ChatBubbles.getPlugin().getCBConfig().length)) != -1) {
					    sb.replace(i, i + 1, "\n");
					}
					for(String s : sb.toString().split("\\n")){
						s = makeColors(s);
						s = makeColors(ChatBubbles.getPlugin().getCBConfig().prefix + s + ChatBubbles.getPlugin().getCBConfig().suffix);
						lineList.add(s);				
					}
				}
				if(!addedToLine) {
					formatLine = makeColors(formatLine);
					formatLine = makeColors(ChatBubbles.getPlugin().getCBConfig().prefix + formatLine + ChatBubbles.getPlugin().getCBConfig().suffix);
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