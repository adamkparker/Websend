package websend;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class PlayerHandler extends PlayerListener {
	
	Main plugin;
	
	public PlayerHandler(Main plugin){
		
	}
	

	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		String msg = event.getMessage();
		Player plyr = event.getPlayer();
		Main.comthread = new ComThread(Main.url, Main.pass, msg, plyr, Main.altprotection, Main.forcePort, Bukkit.getServer(), Main.file);
        Main.comthread.start();
	}
	
	
}
