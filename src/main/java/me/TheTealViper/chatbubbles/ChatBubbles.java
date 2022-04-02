package me.TheTealViper.chatbubbles;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import me.TheTealViper.chatbubbles.utils.EnableShit;
import me.TheTealViper.chatbubbles.utils.FormatUtils;
import me.TheTealViper.chatbubbles.citizens.ChatBubbleTrait;

public class ChatBubbles extends JavaPlugin {
	
	private static ChatBubbles plugin;
	private CBConfig config;
	public enum HologramType {
	    HD, DecentHolograms
	}
	public HologramType type = null;
	public HandleHolographicDisplays hdHandler;
	public Listener hologramListener;
	
	public void onEnable(){
		ChatBubbles.plugin = this;	
		this.config = new CBConfig(plugin);
		if(getServer().getPluginManager().getPlugin("HolographicDisplays") != null) {
			if(getServer().getPluginManager().getPlugin("HolographicDisplays").isEnabled()) {
				Bukkit.getServer().getConsoleSender().sendMessage("[ChatBubbles] Using HolographicDisplays to power ChatBubbles!");
				type = HologramType.HD;
				hdHandler = new HandleHolographicDisplays(ChatBubbles.plugin);
				hologramListener = hdHandler;
			}					
		}else if(type.equals(null) && getServer().getPluginManager().getPlugin("DecentHolograms") != null) {
			if(getServer().getPluginManager().getPlugin("DecentHolograms").isEnabled()) {
				Bukkit.getServer().getConsoleSender().sendMessage("[ChatBubbles] Using DecentHolograms to power ChatBubbles!");
				type = HologramType.DecentHolograms;
				hologramListener = hdHandler;
			}					
		}else {
			Bukkit.getServer().getConsoleSender().sendMessage("[ChatBubbles] No hologram provider found, shutting ChatBubbles down...");
			Bukkit.getServer().getConsoleSender().sendMessage("[ChatBubbles] Please install HolographicDisplays or DecentHolograms");
			this.setEnabled(false);
			Bukkit.getPluginManager().disablePlugin(plugin);
			return;
		}
		Bukkit.getPluginManager().registerEvents(hologramListener, plugin);
		if(getServer().getPluginManager().getPlugin("Citizens") != null) {
			if(getServer().getPluginManager().getPlugin("Citizens").isEnabled() == true && this.config.useTrait) {
				Bukkit.getServer().getConsoleSender().sendMessage("[ChatBubbles] Citizens found and trait chatbubble enabled");
				net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(ChatBubbleTrait.class).withName("chatbubble"));
			}					
		}
		EnableShit.handleOnEnable(this, "49387");
	}
	
	public void onDisable(){
		if(getServer().getPluginManager().getPlugin("Citizens") != null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == true || this.config.useTrait)
		{
			//Unhook for Apis
		}
		
		getServer().getConsoleSender().sendMessage(FormatUtils.makeColors("ChatBubbles from TheTealViper shutting down. Bshzzzzzz"));
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
					if(type.equals(HologramType.HD)) {
						hdHandler.handleOne(message, p);
					}
					if(type.equals(HologramType.DecentHolograms)) {
						//handleOne(message, p);
					}
					
				}
			}
			if((label.equalsIgnoreCase("chatbubblereload") || label.equalsIgnoreCase("cbreload")) && p.hasPermission("chatbubbles.reload")){
				reloadConfig();
				p.sendMessage("Reloaded Successfully");
			}
			if((label.equalsIgnoreCase("chatbubbletoggle") || label.equalsIgnoreCase("cbtoggle") || label.equalsIgnoreCase("cbt")) && p.hasPermission("chatbubbles.toggle")) {
				boolean currentState = this.config.togglePF.getBoolean(p.getUniqueId().toString());
				if(currentState)
					p.sendMessage("ChatBubbles toggled off!");
				else
					p.sendMessage("ChatBubbles toggled on!");
				this.config.togglePF.set(p.getUniqueId().toString(), !currentState);
				this.config.togglePF.save();
			}
		}
		return false;
	}
	
	public void handleZero(String message, Player p){
		switch(type) {
	    case HD:
	    	hdHandler.handleZero(message, p);
	    case DecentHolograms:
	    	//create DecentHolograms speech bubble
	    }
	}
	
	public void handleOne(String message, Player p){
		switch(type) {
	    case HD:
	    	hdHandler.handleOne(message, p);
	    case DecentHolograms:
	    	//create DecentHolograms speech bubble
	    }
	}
	
    public void handleTwo(String message, Player p){
    	switch(type) {
	    case HD:
	    	hdHandler.handleTwo(message, p);
	    case DecentHolograms:
	    	//create DecentHolograms speech bubble
	    }
	}
    
    public void handleThree(String message, Player p){
    	switch(type) {
	    case HD:
	    	hdHandler.handleThree(message, p);
	    case DecentHolograms:
	    	//create DecentHolograms speech bubble
	    }
    }
    
    public void handleFour(String message, Player p){
    	switch(type) {
	    case HD:
	    	hdHandler.handleFour(message, p);
	    case DecentHolograms:
	    	//create DecentHolograms speech bubble
	    }
    }
	
	public CBConfig getCBConfig() {
		return this.config;
	}
	
	public static ChatBubbles getPlugin() {
		return plugin;
	}
	
}
