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
/*  34 */   static String port = "4445";
  static File configFile;
/*  36 */   static boolean altprotection = false;
/*  37 */   static boolean forcePort = false;
/*  38 */   static boolean sktActive = false;
/*  39 */   static boolean debugWebsend = false;
  static ComThread comthread;
  static String url;
  static String pass;
  static String file;
  static JavaPlugin plugin;
  PHPSktSrvrThread srvrthread;

  public void onEnable()
  {
/*  50 */     setupPermissions();
/*  51 */     absolPath = new File("").getAbsolutePath();
/*  52 */     seperator = File.separator;
/*  53 */     File configFileDir = new File(absolPath + seperator + "plugins" + seperator + "Websend");
/*  54 */     configFile = new File(absolPath + seperator + "plugins" + seperator + "Websend" + seperator + "config.txt");
/*  55 */     url = null;
/*  56 */     pass = null;
/*  57 */     plugin = this;
    try
    {
/*  60 */       if (!configFileDir.exists()) {
/*  61 */         configFileDir.mkdir();
/*  62 */         generateConfigFile();
/*  63 */         System.out.println("Websend: Config file generated. Go setup the plugin!");
/*  64 */         setEnabled(false);
      }
/*  66 */       else if (!configFile.exists()) {
/*  67 */         generateConfigFile();
/*  68 */         System.out.println("Websend: Config file generated. Go setup the plugin!");
/*  69 */         setEnabled(false);
      }
      else {
/*  72 */         parseConfig();
/*  73 */         if (debugWebsend) System.out.println("Websend: Config parsed.");
/*  74 */         if (sktActive == true) {
/*  75 */           if (debugWebsend) System.out.println("Websend: Creating ServerSocket Thread.");
/*  76 */           this.srvrthread = new PHPSktSrvrThread(getServer(), pass.trim(), port);
/*  77 */           if (debugWebsend) System.out.println("Websend: Starting ServerSocket Thread.");
/*  78 */           this.srvrthread.start();
        }
      }
    } catch (Exception ex) {
/*  81 */       ex.printStackTrace();
    }
  }

  public void onDisable() {
/*  86 */     this.srvrthread.close();
    try {
/*  88 */       this.srvrthread.join(5000L);
    } catch (InterruptedException ex) {
/*  90 */       Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void parseConfig() throws FileNotFoundException, IOException
  {
/*  96 */     BufferedReader configReader = new BufferedReader(new FileReader(configFile));
    String line;
/*  98 */     while ((line = configReader.readLine()) != null) {
/*  99 */       if (!line.startsWith("#")) {
/* 100 */         if (line.startsWith("URL=")) {
/* 101 */           String value = line.replaceFirst("URL=", "");
/* 102 */           url = value;
/* 103 */           continue;
/* 104 */         }if (line.startsWith("PASS=")) {
/* 105 */           String value = line.replaceFirst("PASS=", "");
/* 106 */           pass = value;
/* 107 */           continue;
/* 108 */         }if (line.startsWith("FILE=")) {
/* 109 */           String value = line.replaceFirst("FILE=", "");
/* 110 */           file = value;
/* 111 */           continue;
/* 112 */         }if (line.startsWith("FORCEPORT="))
        {
/* 114 */           String value = line.replaceFirst("FORCEPORT=", "");
/* 115 */           if (value.toLowerCase().trim().contains("true")) {
/* 116 */             forcePort = true;
/* 117 */             System.out.println("Forcing port 4444");
          }
          else {
/* 120 */             forcePort = false;
          }
/* 122 */           continue;
/* 123 */         }if (line.startsWith("ALTPORT="))
        {
/* 125 */           String value = line.replaceFirst("ALTPORT=", "");
/* 126 */           port = value.trim();
/* 127 */           continue;
/* 128 */         }if (line.startsWith("WEBLISTENER_ACTIVE="))
        {
/* 130 */           String value = line.replaceFirst("WEBLISTENER_ACTIVE=", "");
/* 131 */           if (value.toLowerCase().trim().contains("true")) {
/* 132 */             sktActive = true;
          }
          else {
/* 135 */             sktActive = false;
          }
/* 137 */           continue;
/* 138 */         }if (line.startsWith("DEBUG_WEBSEND="))
        {
/* 140 */           String value = line.replaceFirst("DEBUG_WEBSEND=", "");
/* 141 */           if (value.toLowerCase().trim().contains("true")) {
/* 142 */             debugWebsend = true;
          }
          else {
/* 145 */             debugWebsend = false;
          }
/* 147 */           continue;
/* 148 */         }if (line.startsWith("ALTPROTECTION=")) {
/* 149 */           String value = line.replaceFirst("ALTPROTECTION=", "");
/* 150 */           if (value.trim().equalsIgnoreCase("true")) {
/* 151 */             altprotection = true;
          }
          else {
/* 154 */             altprotection = false;
          }
/* 156 */           continue;
        }
/* 158 */         System.out.println("ERROR: error while parsing config file.");
/* 159 */         System.out.println("Invalid line: " + line);
      }
    }

/* 163 */     if (url.trim() == null) {
/* 164 */       System.out.println("ERROR: No value for URL was found. The plug-in will not function properly!");
    }
/* 166 */     if (pass.trim() == null) {
/* 167 */       System.out.println("ERROR: No value was found for PASSWORD. The plug-in will probably not function properly!");
    }
/* 169 */     if (forcePort == true)
/* 170 */       if ((url.toLowerCase().endsWith(".php")) || (url.toLowerCase().endsWith(".php/"))) {
/* 171 */         System.out.println("WARNING: URL ends with a php file, while port forcing is enabled.");
      }
/* 174 */       else if (url.contains("http://")) {
/* 175 */         url = url.replaceFirst("http://", "");
      }
/* 177 */       else if (url.contains("HTTP://"))
/* 178 */         url = url.replaceFirst("HTTP://", "");
  }

  public void generateConfigFile()
    throws IOException
  {
/* 185 */     configFile.createNewFile();
/* 186 */     BufferedWriter out = new BufferedWriter(new FileWriter(configFile));
/* 187 */     out.write("#Configuration and settings file!");
/* 188 */     out.newLine();
/* 189 */     out.write("#Help: URL: set to the full path of your server php file (see example).");
/* 190 */     out.newLine();
/* 191 */     out.write("#Help: PASS: change the password to one of your choice (set the same in the server php file).");
/* 192 */     out.newLine();
/* 193 */     out.write("URL=http://www.example.com/minecraftPHPcom.php");
/* 194 */     out.newLine();
/* 195 */     out.write("PASS=YourPassHere");
/* 196 */     out.newLine();
/* 197 */     out.write("#Optional settings. Remove the '#' to use.");
/* 198 */     out.newLine();
/* 199 */     out.write("#FORCEPORT=false");
/* 200 */     out.newLine();
/* 201 */     out.write("#FILE=");
/* 202 */     out.newLine();
/* 203 */     out.write("#WEBLISTENER_ACTIVE=false");
/* 204 */     out.newLine();
/* 205 */     out.write("#ALTPROTECTION=");
/* 206 */     out.close();
/* 207 */     out.write("#ALTPORT=");
/* 208 */     out.close();
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
			if (Main.debugWebsend) System.out.println("Websend: Command captured.");
/* 213 */    try {
/* 214 */         Player plsender = (Player)sender;
        try {
/* 217 */             comthread = new ComThread(url, pass, args, plsender, altprotection, forcePort, getServer(), file);
/* 218 */             comthread.start();
/* 219 */             return true;
        }
        catch (NoClassDefFoundError ex) {
/* 227 */             comthread = new ComThread(url, pass, args, plsender, altprotection, forcePort, getServer(), file);
/* 228 */             comthread.start();
/* 229 */             return true;
        }
      }
      catch (ClassCastException ex) {
/* 236 */         comthread = new ComThread(url, pass, args, null, altprotection, forcePort, getServer(), file);
/* 237 */         comthread.start();
/* 238 */         return true;
      }
  }

  private void setupPermissions() {
/* 246 */     if (permissionHandler != null) {
/* 247 */       return;
    }

/* 250 */     Plugin permissionsPlugin = getServer().getPluginManager().getPlugin("Permissions");

/* 252 */     if (permissionsPlugin == null) {
/* 253 */       System.out.println("Permission system not detected, defaulting to OP");
/* 254 */       return;
    }

/* 257 */     permissionHandler = ((Permissions)permissionsPlugin).getHandler();
/* 258 */     System.out.println("Found and will use plugin " + ((Permissions)permissionsPlugin).getDescription().getFullName());
  }
}