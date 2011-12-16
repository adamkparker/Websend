

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Communicator
{
   String PAC = "";
  URL url;
   boolean closed = false;

  public void sendPOST(String urlArg, String passArg, String[] args, Player player, boolean altprotect, boolean forcePort, Server server, String file) throws Exception {
     if (altprotect == true) {
       this.PAC = URLEncoder.encode(generatePAC(passArg), "UTF-8");
    }
     String[] argsEncoded = new String[args.length];
     for (int i = 0; i < args.length; i++) {
       argsEncoded[i] = URLEncoder.encode(args[i], "UTF-8");
    }
     if (forcePort == true) {
       if (file != null) {
         this.url = new URL("http", urlArg, 4444, file);
      }

    }
    else
    {
       this.url = new URL(urlArg);
    }
     URLConnection con = this.url.openConnection();
     con.setDoOutput(true);
     OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
     if (altprotect == true) {
       out.write("pac=" + this.PAC + "&");
    }
     sendData(server, out, player);
     for (int i = 0; i < argsEncoded.length; i++) {
       out.write("args[" + i + "]=" + argsEncoded[i] + "&");
    }
     if (!altprotect) {
       out.write("authKey=" + URLEncoder.encode(hash(passArg), "UTF-8"));
    }
     out.close();
     BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

     Interpretator interpretator = new Interpretator();
     if (player != null)
    {
      String line;
       while ((line = in.readLine()) != null)
        interpretator.Interpretate(line, player, server);
    }
    String line;
     while ((line = in.readLine()) != null) {
       interpretator.NoPlayerInterpretate(line, server);
    }

     in.close();
  }

  public void sendData(Server server, OutputStreamWriter out, Player player) throws UnsupportedEncodingException, IOException {
     if (player != null) {
       String playerName = URLEncoder.encode(player.getName(), "UTF-8");
       out.write("player=" + playerName + "&");
    }
    else {
       String playerName = URLEncoder.encode("console", "UTF-8");
       out.write("player=" + playerName + "&");
    }

    for (int i = 0; i < server.getOnlinePlayers().length; i++) {
       out.write("onlinePlayers[" + i + "]=" + URLEncoder.encode(server.getOnlinePlayers()[i].getName(), "UTF-8") + "&");
    }
     out.write("maxPlayers=" + URLEncoder.encode(String.valueOf(Main.plugin.getServer().getMaxPlayers()), "UTF-8") + "&");
     out.write("curPlayers=" + URLEncoder.encode(String.valueOf(Main.plugin.getServer().getOnlinePlayers().length), "UTF-8") + "&");

     out.write("availableMemory=" + URLEncoder.encode(String.valueOf(Runtime.getRuntime().freeMemory()), "UTF-8") + "&");
     out.write("maxMemory=" + URLEncoder.encode(String.valueOf(Runtime.getRuntime().maxMemory()), "UTF-8") + "&");

     for (int i = 0; i < server.getPluginManager().getPlugins().length; i++) {
       out.write("pluginList[" + i + "]=" + URLEncoder.encode(server.getPluginManager().getPlugins()[i].getDescription().getFullName(), "UTF-8") + "&");
    }

     out.write("bukkitBuild=" + URLEncoder.encode(Main.plugin.getServer().getVersion(), "UTF-8") + "&");
     out.write("bukkitPort=" + URLEncoder.encode(String.valueOf(Main.plugin.getServer().getPort()), "UTF-8") + "&");
     out.write("serverName=" + URLEncoder.encode(String.valueOf(Main.plugin.getServer().getServerName()), "UTF-8") + "&");
     out.write("netherEnabled=" + URLEncoder.encode(String.valueOf(Main.plugin.getServer().getAllowNether()), "UTF-8") + "&");
     out.write("flyingEnabled=" + URLEncoder.encode(String.valueOf(Main.plugin.getServer().getAllowFlight()), "UTF-8") + "&");
     out.write("defaultGameMode=" + URLEncoder.encode(String.valueOf(Main.plugin.getServer().getDefaultGameMode()), "UTF-8") + "&");
     out.write("onlineMode=" + URLEncoder.encode(String.valueOf(Main.plugin.getServer().getOnlineMode()), "UTF-8") + "&");
  }
  public void close() {
     this.closed = true;
  }

  public String generatePAC(String password)
  {
     String PACstring = null;
     Calendar cal = Calendar.getInstance();
     int day = cal.get(5);
     int month = cal.get(2) + 1;
     int year = cal.get(1);
     HashMap alfabetmap = generateMap();
     password = password.toLowerCase();
     int naamvalue = 0;
     for (int i = 0; i < password.length(); i++) {
       char str = password.charAt(i);
       naamvalue += ((Integer)alfabetmap.get(Character.toString(str))).intValue();
    }

     PACstring = String.valueOf(day + month + year + naamvalue);
     return PACstring;
  }
  public HashMap generateMap() {
     HashMap map = new HashMap(26);
     map.put("a", Integer.valueOf(1));
     map.put("b", Integer.valueOf(2));
     map.put("c", Integer.valueOf(3));
     map.put("d", Integer.valueOf(4));
     map.put("e", Integer.valueOf(5));
     map.put("f", Integer.valueOf(6));
     map.put("g", Integer.valueOf(7));
     map.put("h", Integer.valueOf(8));
     map.put("i", Integer.valueOf(9));
     map.put("j", Integer.valueOf(10));
     map.put("k", Integer.valueOf(11));
     map.put("l", Integer.valueOf(12));
     map.put("m", Integer.valueOf(13));
     map.put("n", Integer.valueOf(14));
     map.put("o", Integer.valueOf(15));
     map.put("p", Integer.valueOf(16));
     map.put("q", Integer.valueOf(17));
     map.put("r", Integer.valueOf(18));
     map.put("s", Integer.valueOf(19));
     map.put("t", Integer.valueOf(20));
     map.put("u", Integer.valueOf(21));
     map.put("v", Integer.valueOf(22));
     map.put("w", Integer.valueOf(23));
     map.put("x", Integer.valueOf(24));
     map.put("y", Integer.valueOf(25));
     map.put("z", Integer.valueOf(26));
     return map;
  }
  public String hash(String input) throws NoSuchAlgorithmException {
     MessageDigest md = MessageDigest.getInstance("MD5");
     md.update(input.getBytes());
     BigInteger bigInt = new BigInteger(1, md.digest());
     String result = bigInt.toString(16);
     if (result.length() % 2 != 0) {
       result = "0" + result;
    }
     return result;
  }
}