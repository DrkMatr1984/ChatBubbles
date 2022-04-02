package me.TheTealViper.chatbubbles;

import org.bukkit.event.EventPriority;

import me.TheTealViper.chatbubbles.listeners.ChatListenerHigh;
import me.TheTealViper.chatbubbles.listeners.ChatListenerHighest;
import me.TheTealViper.chatbubbles.listeners.ChatListenerLow;
import me.TheTealViper.chatbubbles.listeners.ChatListenerLowest;
import me.TheTealViper.chatbubbles.listeners.ChatListenerMonitor;
import me.TheTealViper.chatbubbles.listeners.ChatListenerNormal;
import me.TheTealViper.chatbubbles.utils.PluginFile;

public class CBConfig
{
	protected static final boolean requirePerm = false;
	private ChatBubbles plugin;
	public int life = -1, distance = -1, length = -1;
	public String prefix = "", suffix = "";
	public boolean seeOwnBubble = false;
	public boolean useTrait = true;
	public boolean chatBubbleOverridesNPCChat;
	public double bubbleOffset = 2.5;
	public EventPriority chatPriority = EventPriority.NORMAL;
	public PluginFile togglePF;
	
	public CBConfig(ChatBubbles main) {
		this.plugin = main;
		life = this.plugin.getConfig().getInt("ChatBubble_Life");
		distance = this.plugin.getConfig().getInt("ChatBubble_Viewing_Distance");
		length = this.plugin.getConfig().getInt("ChatBubble_Maximum_Line_Length");
		prefix = this.plugin.getConfig().getString("ChatBubble_Message_Prefix");
		if(prefix == null)
			prefix = "";
		suffix = this.plugin.getConfig().getString("ChatBubble_Message_Suffix");
		if(suffix == null)
			suffix = "";
		seeOwnBubble = this.plugin.getConfig().getBoolean("ChatBubble_See_Own_Bubbles");
		bubbleOffset = this.plugin.getConfig().getDouble("ChatBubble_Height_Offset");
		useTrait = this.plugin.getConfig().getBoolean("Use_ChatBubble_Trait_Citizens");
		chatBubbleOverridesNPCChat = this.plugin.getConfig().getBoolean("ChatBubble_Overrides_NPC_Chat");
		chatPriority = EventPriority.valueOf(this.plugin.getConfig().getString("ChatBubble_Chat_Priority").toUpperCase());
		togglePF = new PluginFile(this.plugin, "toggleData");
		setChatPriority(chatPriority);
	}
	
	private void setChatPriority(EventPriority priority) {
		switch (priority) {
			case LOWEST:
				this.plugin.getServer().getPluginManager().registerEvents(new ChatListenerLowest(this.plugin), this.plugin);
			case LOW:
				this.plugin.getServer().getPluginManager().registerEvents(new ChatListenerLow(this.plugin), this.plugin);
			case NORMAL:
				this.plugin.getServer().getPluginManager().registerEvents(new ChatListenerNormal(this.plugin), this.plugin);
			case HIGH:
				this.plugin.getServer().getPluginManager().registerEvents(new ChatListenerHigh(this.plugin), this.plugin);
			case HIGHEST:
				this.plugin.getServer().getPluginManager().registerEvents(new ChatListenerHighest(this.plugin), this.plugin);
			case MONITOR:
				this.plugin.getServer().getPluginManager().registerEvents(new ChatListenerMonitor(this.plugin), this.plugin);
			default:
				this.plugin.getServer().getPluginManager().registerEvents(new ChatListenerNormal(this.plugin), this.plugin);
		}
	}
}