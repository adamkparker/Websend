package websend;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
  public static PermissionHandler permissionHandler;
  String DO_NOT_STEAL_MY_CODE__Thank_you;
  static String absolPath;
  static String seperator;
  static String port = "4445";
  static File configFile;
  static boolean altprotection = false;
  static boolean forcePort = false;
  static boolean sktActive = false;
  static boolean debugWebsend = false;
  static ComThread comthread;
  static String url;
  static String pass;
  static String file;
  static JavaPlugin plugin;
  PHPSktSrvrThread srvrthread;

  public void onEnable()
  {
    setupPermissions();
    absolPath = new File("").getAbsolutePath();
    seperator = File.separator;
    File configFileDir = new File(absolPath + seperator + "plugins" + seperator + "Websend");
    configFile = new File(absolPath + seperator + "plugins" + seperator + "Websend" + seperator + "config.txt");
    url = null;
    pass = null;
    plugin = this;
    try
    {
      if (!configFileDir.exists()) {
        configFileDir.mkdir();
        generateConfigFile();
        System.out.println("Websend: Config file generated. Go setup the plugin!");
        setEnabled(false);
      }
      else if (!configFile.exists()) {
        generateConfigFile();
        System.out.println("Websend: Config file generated. Go setup the plugin!");
        setEnabled(false);
      }
      else {
        parseConfig();
        if (debugWebsend) System.out.println("Websend: Config parsed.");
        if (sktActive) {
          if (debugWebsend) System.out.println("Websend: Creating ServerSocket Thread.");
          this.srvrthread = new PHPSktSrvrThread(getServer(), pass.trim(), port);
          if (debugWebsend) System.out.println("Websend: Starting ServerSocket Thread.");
          this.srvrthread.start();
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void onDisable() {
    this.srvrthread.close();
    try {
      this.srvrthread.join(5000L);
    } catch (InterruptedException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void parseConfig() throws FileNotFoundException, IOException
  {
    BufferedReader configReader = new BufferedReader(new FileReader(configFile));
    String line = null;
    while ((line = configReader.readLine()) != null)
    {
      if (!line.startsWith("#")) {
        if (line.startsWith("URL=")) {
          String value = line.replaceFirst("URL=", "");
          url = value;
        }
        else if (line.startsWith("PASS=")) {
          String value = line.replaceFirst("PASS=", "");
          pass = value;
        }
        else if (line.startsWith("FILE=")) {
          String value = line.replaceFirst("FILE=", "");
          file = value;
        }
        else if (line.startsWith("FORCEPORT="))
        {
          String value = line.replaceFirst("FORCEPORT=", "");
          if (value.toLowerCase().trim().contains("true")) {
            forcePort = true;
            System.out.println("Forcing port 4444");
          }
          else {
            forcePort = false;
          }
        }
        else if (line.startsWith("ALTPORT="))
        {
          String value = line.replaceFirst("ALTPORT=", "");
          port = value.trim();
        }
        else if (line.startsWith("WEBLISTENER_ACTIVE="))
        {
          String value = line.replaceFirst("WEBLISTENER_ACTIVE=", "");
          if (value.toLowerCase().trim().contains("true")) {
            sktActive = true;
          }
          else {
            sktActive = false;
          }
        }
        else if (line.startsWith("DEBUG_WEBSEND="))
        {
          String value = line.replaceFirst("DEBUG_WEBSEND=", "");
          if (value.toLowerCase().trim().contains("true")) {
            debugWebsend = true;
          }
          else {
            debugWebsend = false;
          }
        }
        else if (line.startsWith("ALTPROTECTION=")) {
          String value = line.replaceFirst("ALTPROTECTION=", "");
          if (value.trim().equalsIgnoreCase("true")) {
            altprotection = true;
          }
          else
            altprotection = false;
        }
        else
        {
          System.out.println("ERROR: error while parsing config file.");
          System.out.println("Invalid line: " + line);
        }
      }
    }
    if (url.trim() == null) {
      System.out.println("ERROR: No value for URL was found. The plug-in will not function properly!");
    }
    if (pass.trim() == null) {
      System.out.println("ERROR: No value was found for PASSWORD. The plug-in will probably not function properly!");
    }
    if (forcePort)
      if ((url.toLowerCase().endsWith(".php")) || (url.toLowerCase().endsWith(".php/"))) {
        System.out.println("WARNING: URL ends with a php file, while port forcing is enabled.");
      }
      else if (url.contains("http://")) {
        url = url.replaceFirst("http://", "");
      }
      else if (url.contains("HTTP://"))
        url = url.replaceFirst("HTTP://", "");
  }

  public void generateConfigFile()
    throws IOException
  {
    configFile.createNewFile();
    BufferedWriter out = new BufferedWriter(new FileWriter(configFile));
    out.write("#Configuration and settings file!");
    out.newLine();
    out.write("#Help: URL: set to the full path of your server php file (see example).");
    out.newLine();
    out.write("#Help: PASS: change the password to one of your choice (set the same in the server php file).");
    out.newLine();
    out.write("URL=http://www.example.com/minecraftPHPcom.php");
    out.newLine();
    out.write("PASS=YourPassHere");
    out.newLine();
    out.write("#Optional settings. Remove the '#' to use.");
    out.newLine();
    out.write("#FORCEPORT=false");
    out.newLine();
    out.write("#FILE=");
    out.newLine();
    out.write("#WEBLISTENER_ACTIVE=false");
    out.newLine();
    out.write("#ALTPROTECTION=");
    out.close();
    out.write("#ALTPORT=");
    out.close();
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if ((cmd.getName().equalsIgnoreCase("websend")) || (cmd.getName().equalsIgnoreCase("ws"))) try {
        Player plsender = (Player)sender;
        try {
          if (permissionHandler.has(plsender, "websend")) {
            comthread = new ComThread(url, pass, args, plsender, altprotection, forcePort, getServer(), file);
            comthread.start();
            return true;
          }

          plsender.sendMessage("You are not allowed to use this command.");
        }
        catch (NoClassDefFoundError ex) {
          System.out.println("No permissions found, defaulting to OP.");
          if (sender.isOp()) {
            comthread = new ComThread(url, pass, args, plsender, altprotection, forcePort, getServer(), file);
            comthread.start();
            return true;
          }

          plsender.sendMessage("You are not allowed to use this command.");
        }
      }
      catch (ClassCastException ex) {
        comthread = new ComThread(url, pass, args, null, altprotection, forcePort, getServer(), file);
        comthread.start();
        return true;
      }


    return false;
  }

  private void setupPermissions() {
    if (permissionHandler != null) {
      return;
    }

    Plugin permissionsPlugin = getServer().getPluginManager().getPlugin("Permissions");

    if (permissionsPlugin == null) {
      System.out.println("Permission system not detected, defaulting to OP");
      return;
    }

    permissionHandler = ((Permissions)permissionsPlugin).getHandler();
    System.out.println("Found and will use plugin " + ((Permissions)permissionsPlugin).getDescription().getFullName());
  }
}

/* Location:           /Users/adamkparker/Desktop/Websend.jar
 * Qualified Name:     websend.Main
 * JD-Core Version:    0.6.0
 */