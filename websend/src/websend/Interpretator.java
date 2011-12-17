package websend;

import java.io.PrintStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Interpretator
{
  public void Interpretate(String line, Player player, Server server)
  {
    if (line.contains(";")) {
      if (Main.debugWebsend) System.out.println("Websend: ';' found");
      String[] lineArray = line.split(";");
      for (int i = 0; i < lineArray.length; i++) {
        if (lineArray[i].contains("/Command/")) {
          if (Main.debugWebsend) System.out.println("Websend: A command line was found.");

          String[] lineTypeArray = lineArray[i].split("/Command/");
          if (lineTypeArray[1].contains("ExecuteBukkitCommand:"))
          {
            String[] commandArray = lineTypeArray[1].split("ExecuteBukkitCommand:");
            if (Main.debugWebsend) System.out.println("Websend: An ExecuteBukkitCommand was found: '" + commandArray + "'"); try {
              if (player == null) {
                System.out.println("Command dispatching from terminal is not allowed. Try again in-game."); continue;
              }
              if (player.getServer().dispatchCommand(player, commandArray[1])) continue;
              player.sendMessage("Command dispatching failed: '" + commandArray[1] + "'");
            } catch (Exception ex) {
              System.out.println("An error has occured, are you trying to execute a command from console?");
            }
          }
          else if (lineTypeArray[1].contains("ExecuteConsoleCommand:"))
          {
            String[] commandArray = lineTypeArray[1].split("ExecuteConsoleCommand:");
            if (Main.debugWebsend) System.out.println("Websend: An ExecuteConsoleCommand was found: '" + commandArray + "'");
            ConsoleCommandSender ccs = server.getConsoleSender();
            if (!server.dispatchCommand(ccs, commandArray[1])) {
              player.sendMessage("Command dispatching failed: '" + commandArray[1] + "'");
            }
          }
          else
          {
            System.out.println("ERROR: While parsing php output, websend found");
            System.out.println("an error on output line " + (i + 1) + ": Invalid command.");
          }
        }
        else {
          if (Main.debugWebsend) System.out.println("Websend: No command found, checking for a /Chatcolor tag");
          if (line.contains("/Chatcolor-")) {
            CheckColorAndSend(player, lineArray[i]);
          }
          else
            player.sendMessage(lineArray[i]);
        }
      }
    }
    else
    {
      if (Main.debugWebsend) System.out.println("Websend: No ; found. Printing input as output.");
      if (line.contains("/Chatcolor-")) {
        CheckColorAndSend(player, line);
      }
      else
        player.sendMessage(line);
    }
  }

  public void NoPlayerInterpretate(String line, Server bukkitServer)
  {
    if (line.contains(";")) {
      if (Main.debugWebsend) System.out.println("Websend: ';' found");
      String[] lineArray = line.split(";");
      System.out.println(lineArray[0]);
      for (int i = 0; i < lineArray.length; i++)
        if (lineArray[i].contains("/Command/")) {
          if (Main.debugWebsend) System.out.println("Websend: A command line was found.");

          String[] lineTypeArray = lineArray[i].split("/Command/");
          if (lineTypeArray[1].contains("ExecuteBukkitCommand:")) {
            System.out.println("ExecuteBukkitCommand is not supported in this way.");
          }
          else if (lineTypeArray[1].contains("ExecuteConsoleCommand:"))
          {
            String[] commandArray = lineTypeArray[1].split("ExecuteConsoleCommand:");
            if (Main.debugWebsend) System.out.println("Websend: An ExecuteConsoleCommand was found: '" + commandArray + "'");
            ConsoleCommandSender ccs = bukkitServer.getConsoleSender();
            if (!bukkitServer.dispatchCommand(ccs, commandArray[1])) {
              System.out.println("Command dispatching failed: '" + commandArray[1] + "'");
            }

          }
          else if (lineTypeArray[1].contains("ExecuteBukkitCommand-"))
          {
            String[] commandArray = lineTypeArray[1].split("ExecuteBukkitCommand-");
            if (Main.debugWebsend) System.out.println("Websend: An ExecuteBukkitCommand was found: '" + commandArray + "'");
            String[] argArray = commandArray[1].split(":");
            Player fakePlayer = bukkitServer.getPlayer(argArray[0].trim());
            if (!bukkitServer.dispatchCommand(fakePlayer, argArray[1])) {
              System.out.println("Command dispatching failed: '" + argArray[1] + "'");
            }
          }
          else
          {
            System.out.println("ERROR: While parsing php output, websend found");
            System.out.println("an error on line " + (i + 1) + ": Invalid command.");
          }
        }
        else {
          if (Main.debugWebsend) System.out.println("Websend: No command found, checking for a /Chatcolor tag");
          if (lineArray[i].contains("/Chatcolor-")) {
            CheckColorAndBroadcast(bukkitServer, lineArray[i]);
          }
          else
            System.out.println(lineArray[i]);
        }
    }
    else
    {
      if (Main.debugWebsend) System.out.println("Websend: No ; found. Printing input as output.");
      if (line.contains("/Chatcolor-")) {
        CheckColorAndBroadcast(bukkitServer, line);
      }
      else
        System.out.println(line);
    }
  }

  public void CheckColorAndBroadcast(Server srvr, String line) {
    boolean multipleSentence = false;
    String[] colorSplit = line.split("/Chatcolor-");
    String chatLine = "";
    if (colorSplit.length > 2) {
      multipleSentence = true;
    }
    for (int i = 0; i < colorSplit.length; i++) {
      if (colorSplit[i].contains("red:")) {
        colorSplit[i] = colorSplit[i].replace("red:", "");
        if (!multipleSentence) {
          srvr.broadcastMessage(ChatColor.RED + colorSplit[i]);
        }
        else {
          chatLine = chatLine + ChatColor.RED + colorSplit[i];
        }
      }
      else if (colorSplit[i].contains("blue:")) {
        colorSplit[i] = colorSplit[i].replace("blue:", "");
        if (!multipleSentence) {
          srvr.broadcastMessage(ChatColor.BLUE + colorSplit[i]);
        }
        else {
          chatLine = chatLine + ChatColor.BLUE + colorSplit[i];
        }
      }
      else if (colorSplit[i].contains("green:")) {
        colorSplit[i] = colorSplit[i].replace("green:", "");
        if (!multipleSentence) {
          srvr.broadcastMessage(ChatColor.GREEN + colorSplit[i]);
        }
        else {
          chatLine = chatLine + ChatColor.GREEN + colorSplit[i];
        }
      }
      else if (colorSplit[i].contains("yellow:")) {
        colorSplit[i] = colorSplit[i].replace("yellow:", "");
        if (!multipleSentence) {
          srvr.broadcastMessage(ChatColor.YELLOW + colorSplit[i]);
        }
        else {
          chatLine = chatLine + ChatColor.YELLOW + colorSplit[i];
        }
      }
      else if (colorSplit[i].contains("white:")) {
        colorSplit[i] = colorSplit[i].replace("white:", "");
        if (!multipleSentence) {
          srvr.broadcastMessage(ChatColor.WHITE + colorSplit[i]);
        }
        else {
          chatLine = chatLine + ChatColor.WHITE + colorSplit[i];
        }
      }
      else if (colorSplit[i].contains("purple:")) {
        colorSplit[i] = colorSplit[i].replace("purple:", "");
        if (!multipleSentence) {
          srvr.broadcastMessage(ChatColor.LIGHT_PURPLE + colorSplit[i]);
        }
        else {
          chatLine = chatLine + ChatColor.LIGHT_PURPLE + colorSplit[i];
        }
      }
      else if (colorSplit[i].contains("gray:")) {
        colorSplit[i] = colorSplit[i].replace("gray:", "");
        if (!multipleSentence) {
          srvr.broadcastMessage(ChatColor.GRAY + colorSplit[i]);
        }
        else {
          chatLine = chatLine + ChatColor.GRAY + colorSplit[i];
        }

      }
      else if (!colorSplit[i].isEmpty()) {
        srvr.broadcastMessage(colorSplit[i]);
      }

      if ((!multipleSentence) || 
        (i != colorSplit.length - 1)) continue;
      srvr.broadcastMessage(chatLine);
    }
  }

  public void CheckColorAndSend(Player player, String line)
  {
    boolean multipleSentence = false;
    String[] colorSplit = line.split("/Chatcolor-");
    String chatLine = "";
    if (colorSplit.length > 2) {
      multipleSentence = true;
    }
    for (int i = 0; i < colorSplit.length; i++) {
      if (colorSplit[i].contains("red:")) {
        colorSplit[i] = colorSplit[i].replace("red:", "");
        if (!multipleSentence) {
          player.sendMessage(ChatColor.RED + colorSplit[i]);
        }
        else {
          chatLine = chatLine + ChatColor.RED + colorSplit[i];
        }
      }
      else if (colorSplit[i].contains("blue:")) {
        colorSplit[i] = colorSplit[i].replace("blue:", "");
        if (!multipleSentence) {
          player.sendMessage(ChatColor.BLUE + colorSplit[i]);
        }
        else {
          chatLine = chatLine + ChatColor.BLUE + colorSplit[i];
        }
      }
      else if (colorSplit[i].contains("green:")) {
        colorSplit[i] = colorSplit[i].replace("green:", "");
        if (!multipleSentence) {
          player.sendMessage(ChatColor.GREEN + colorSplit[i]);
        }
        else {
          chatLine = chatLine + ChatColor.GREEN + colorSplit[i];
        }
      }
      else if (colorSplit[i].contains("yellow:")) {
        colorSplit[i] = colorSplit[i].replace("yellow:", "");
        if (!multipleSentence) {
          player.sendMessage(ChatColor.YELLOW + colorSplit[i]);
        }
        else {
          chatLine = chatLine + ChatColor.YELLOW + colorSplit[i];
        }
      }
      else if (colorSplit[i].contains("white:")) {
        colorSplit[i] = colorSplit[i].replace("white:", "");
        if (!multipleSentence) {
          player.sendMessage(ChatColor.WHITE + colorSplit[i]);
        }
        else {
          chatLine = chatLine + ChatColor.WHITE + colorSplit[i];
        }
      }
      else if (colorSplit[i].contains("purple:")) {
        colorSplit[i] = colorSplit[i].replace("purple:", "");
        if (!multipleSentence) {
          player.sendMessage(ChatColor.LIGHT_PURPLE + colorSplit[i]);
        }
        else {
          chatLine = chatLine + ChatColor.LIGHT_PURPLE + colorSplit[i];
        }
      }
      else if (colorSplit[i].contains("gray:")) {
        colorSplit[i] = colorSplit[i].replace("gray:", "");
        if (!multipleSentence) {
          player.sendMessage(ChatColor.GRAY + colorSplit[i]);
        }
        else {
          chatLine = chatLine + ChatColor.GRAY + colorSplit[i];
        }

      }
      else if (!colorSplit[i].isEmpty()) {
        player.sendMessage(colorSplit[i]);
      }

      if ((!multipleSentence) || 
        (i != colorSplit.length - 1)) continue;
      player.sendMessage(chatLine);
    }
  }

  public String hash(String input) throws NoSuchAlgorithmException
  {
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