package me.TheTealViper.chatbubbles.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class FormatUtils
{
	//------------------------Utilities--------------------------------
		
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
		
		public static String getRandomString() {
		    int leftLimit = 48; // numeral '0'
		    int rightLimit = 122; // letter 'z'
		    int targetStringLength = 10;
		    Random random = new Random();

		    String generatedString = random.ints(leftLimit, rightLimit + 1)
		      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
		      .limit(targetStringLength)
		      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
		      .toString();

		    return generatedString;
		}
}