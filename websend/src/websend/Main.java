package websend;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;


public class Main extends JavaPlugin
{
  static String absolPath;
  static String seperator;
  static String port = "4445";
  static Configuration config;
  static boolean altprotection = false;
  static boolean forcePort = false;
  static boolean sktActive = false;
  static boolean debugWebsend = false;
  static ComThread comthread;
  static String url = "";
  static String pass = "";
  static String file = "";
  static JavaPlugin plugin;
  PHPSktSrvrThread srvrthread;

public void onEnable()
  {
    url = null;
    pass = null;
    plugin = this;
    loadConfig();
    this.srvrthread = new PHPSktSrvrThread(getServer(), pass.trim(), port);
    this.srvrthread.start();
    PlayerListener playerListener = new PlayerHandler(this);
    getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Monitor, this);
  }

  public void onDisable() {
    this.srvrthread.close();
    try {
      this.srvrthread.join(5000L);
    } catch (InterruptedException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public void loadConfig(){
	  config = this.getConfiguration();
	  url = config.getString("url","http://www.example.com/minecraftPHPcom.php");
	  pass = config.getString("pass","yourpass");
	  sktActive = config.getBoolean("active",true);
	  forcePort = config.getBoolean("forceport",false);
	  port = config.getString("port","4445");
	  altprotection = config.getBoolean("altprotect",false);
	  debugWebsend = config.getBoolean("debug",false);
	  file = config.getString("file","");
	  config.save();
  }


}
