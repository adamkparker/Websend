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
/*  20 */     if (line.contains(";")) {
/*  21 */       if (Main.debugWebsend) System.out.println("Websend: ';' found");
/*  22 */       String[] lineArray = line.split(";");
/*  23 */       for (int i = 0; i < lineArray.length; i++) {
/*  24 */         if (lineArray[i].contains("/Command/")) {
/*  25 */           if (Main.debugWebsend) System.out.println("Websend: A command line was found.");

/*  27 */           String[] lineTypeArray = lineArray[i].split("/Command/");
/*  28 */           if (lineTypeArray[1].contains("ExecuteBukkitCommand:"))
          {
/*  30 */             String[] commandArray = lineTypeArray[1].split("ExecuteBukkitCommand:");
/*  31 */             if (Main.debugWebsend) System.out.println("Websend: An ExecuteBukkitCommand was found: '" + commandArray + "'"); try {
/*  32 */               if (player == null) {
/*  33 */                 System.out.println("Command dispatching from terminal is not allowed. Try again in-game.");
              }
/*  35 */               else if (!player.getServer().dispatchCommand(player, commandArray[1]))
/*  36 */                 player.sendMessage("Command dispatching failed: '" + commandArray[1] + "'");
            } catch (Exception ex) {
/*  38 */               System.out.println("An error has occured, are you trying to execute a command from console?");
            }
          }
/*  41 */           else if (lineTypeArray[1].contains("ExecuteConsoleCommand:"))
          {
/*  43 */             String[] commandArray = lineTypeArray[1].split("ExecuteConsoleCommand:");
/*  44 */             if (Main.debugWebsend) System.out.println("Websend: An ExecuteConsoleCommand was found: '" + commandArray + "'");
/*  45 */             ConsoleCommandSender ccs = server.getConsoleSender();
/*  46 */             if (!server.dispatchCommand(ccs, commandArray[1])) {
/*  47 */               player.sendMessage("Command dispatching failed: '" + commandArray[1] + "'");
            }
          }
          else
          {
/*  52 */             System.out.println("ERROR: While parsing php output, websend found");
/*  53 */             System.out.println("an error on output line " + (i + 1) + ": Invalid command.");
          }
        }
        else {
/*  57 */           if (Main.debugWebsend) System.out.println("Websend: No command found, checking for a /Chatcolor tag");
/*  58 */           if (line.contains("/Chatcolor-")) {
/*  59 */             CheckColorAndSend(player, lineArray[i]);
          }
          else
/*  62 */             player.sendMessage(lineArray[i]);
        }
      }
    }
    else
    {
/*  68 */       if (Main.debugWebsend) System.out.println("Websend: No ; found. Printing input as output.");
/*  69 */       if (line.contains("/Chatcolor-")) {
/*  70 */         CheckColorAndSend(player, line);
      }
      else
/*  73 */         player.sendMessage(line);
    }
  }

  public void NoPlayerInterpretate(String line, Server bukkitServer)
  {
/*  79 */     if (line.contains(";")) {
/*  80 */       if (Main.debugWebsend) System.out.println("Websend: ';' found");
/*  81 */       String[] lineArray = line.split(";");
/*  82 */       System.out.println(lineArray[0]);
/*  83 */       for (int i = 0; i < lineArray.length; i++)
/*  84 */         if (lineArray[i].contains("/Command/")) {
/*  85 */           if (Main.debugWebsend) System.out.println("Websend: A command line was found.");

/*  87 */           String[] lineTypeArray = lineArray[i].split("/Command/");
/*  88 */           if (lineTypeArray[1].contains("ExecuteBukkitCommand:")) {
/*  89 */             System.out.println("ExecuteBukkitCommand is not supported in this way.");
          }
/*  91 */           else if (lineTypeArray[1].contains("ExecuteConsoleCommand:"))
          {
/*  93 */             String[] commandArray = lineTypeArray[1].split("ExecuteConsoleCommand:");
/*  94 */             if (Main.debugWebsend) System.out.println("Websend: An ExecuteConsoleCommand was found: '" + commandArray + "'");
/*  95 */             ConsoleCommandSender ccs = bukkitServer.getConsoleSender();
/*  96 */             if (!bukkitServer.dispatchCommand(ccs, commandArray[1])) {
/*  97 */               System.out.println("Command dispatching failed: '" + commandArray[1] + "'");
            }

          }
/* 101 */           else if (lineTypeArray[1].contains("ExecuteBukkitCommand-"))
          {
/* 103 */             String[] commandArray = lineTypeArray[1].split("ExecuteBukkitCommand-");
/* 104 */             if (Main.debugWebsend) System.out.println("Websend: An ExecuteBukkitCommand was found: '" + commandArray + "'");
/* 105 */             String[] argArray = commandArray[1].split(":");
/* 106 */             Player fakePlayer = bukkitServer.getPlayer(argArray[0].trim());
/* 107 */             if (!bukkitServer.dispatchCommand(fakePlayer, argArray[1])) {
/* 108 */               System.out.println("Command dispatching failed: '" + argArray[1] + "'");
            }
          }
          else
          {
/* 113 */             System.out.println("ERROR: While parsing php output, websend found");
/* 114 */             System.out.println("an error on line " + (i + 1) + ": Invalid command.");
          }
        }
        else {
/* 118 */           if (Main.debugWebsend) System.out.println("Websend: No command found, checking for a /Chatcolor tag");
/* 119 */           if (lineArray[i].contains("/Chatcolor-")) {
/* 120 */             CheckColorAndBroadcast(bukkitServer, lineArray[i]);
          }
          else
/* 123 */             System.out.println(lineArray[i]);
        }
    }
    else
    {
/* 128 */       if (Main.debugWebsend) System.out.println("Websend: No ; found. Printing input as output.");
/* 129 */       if (line.contains("/Chatcolor-")) {
/* 130 */         CheckColorAndBroadcast(bukkitServer, line);
      }
      else
/* 133 */         System.out.println(line);
    }
  }

  public void CheckColorAndBroadcast(Server srvr, String line) {
/* 138 */     boolean multipleSentence = false;
/* 139 */     String[] colorSplit = line.split("/Chatcolor-");
/* 140 */     String chatLine = "";
/* 141 */     if (colorSplit.length > 2) {
/* 142 */       multipleSentence = true;
    }
/* 144 */     for (int i = 0; i < colorSplit.length; i++) {
/* 145 */       if (colorSplit[i].contains("red:")) {
/* 146 */         colorSplit[i] = colorSplit[i].replace("red:", "");
/* 147 */         if (!multipleSentence) {
/* 148 */           srvr.broadcastMessage(ChatColor.RED + colorSplit[i]);
        }
        else {
/* 151 */           chatLine = chatLine + ChatColor.RED + colorSplit[i];
        }
      }
/* 154 */       else if (colorSplit[i].contains("blue:")) {
/* 155 */         colorSplit[i] = colorSplit[i].replace("blue:", "");
/* 156 */         if (!multipleSentence) {
/* 157 */           srvr.broadcastMessage(ChatColor.BLUE + colorSplit[i]);
        }
        else {
/* 160 */           chatLine = chatLine + ChatColor.BLUE + colorSplit[i];
        }
      }
/* 163 */       else if (colorSplit[i].contains("green:")) {
/* 164 */         colorSplit[i] = colorSplit[i].replace("green:", "");
/* 165 */         if (!multipleSentence) {
/* 166 */           srvr.broadcastMessage(ChatColor.GREEN + colorSplit[i]);
        }
        else {
/* 169 */           chatLine = chatLine + ChatColor.GREEN + colorSplit[i];
        }
      }
/* 172 */       else if (colorSplit[i].contains("yellow:")) {
/* 173 */         colorSplit[i] = colorSplit[i].replace("yellow:", "");
/* 174 */         if (!multipleSentence) {
/* 175 */           srvr.broadcastMessage(ChatColor.YELLOW + colorSplit[i]);
        }
        else {
/* 178 */           chatLine = chatLine + ChatColor.YELLOW + colorSplit[i];
        }
      }
/* 181 */       else if (colorSplit[i].contains("white:")) {
/* 182 */         colorSplit[i] = colorSplit[i].replace("white:", "");
/* 183 */         if (!multipleSentence) {
/* 184 */           srvr.broadcastMessage(ChatColor.WHITE + colorSplit[i]);
        }
        else {
/* 187 */           chatLine = chatLine + ChatColor.WHITE + colorSplit[i];
        }
      }
/* 190 */       else if (colorSplit[i].contains("purple:")) {
/* 191 */         colorSplit[i] = colorSplit[i].replace("purple:", "");
/* 192 */         if (!multipleSentence) {
/* 193 */           srvr.broadcastMessage(ChatColor.LIGHT_PURPLE + colorSplit[i]);
        }
        else {
/* 196 */           chatLine = chatLine + ChatColor.LIGHT_PURPLE + colorSplit[i];
        }
      }
/* 199 */       else if (colorSplit[i].contains("gray:")) {
/* 200 */         colorSplit[i] = colorSplit[i].replace("gray:", "");
/* 201 */         if (!multipleSentence) {
/* 202 */           srvr.broadcastMessage(ChatColor.GRAY + colorSplit[i]);
        }
        else {
/* 205 */           chatLine = chatLine + ChatColor.GRAY + colorSplit[i];
        }

      }
/* 209 */       else if (!colorSplit[i].isEmpty()) {
/* 210 */         srvr.broadcastMessage(colorSplit[i]);
      }

/* 213 */       if ((multipleSentence != true) || 
/* 214 */         (i != colorSplit.length - 1)) continue;
/* 215 */       srvr.broadcastMessage(chatLine);
    }
  }

  public void CheckColorAndSend(Player player, String line)
  {
/* 221 */     boolean multipleSentence = false;
/* 222 */     String[] colorSplit = line.split("/Chatcolor-");
/* 223 */     String chatLine = "";
/* 224 */     if (colorSplit.length > 2) {
/* 225 */       multipleSentence = true;
    }
/* 227 */     for (int i = 0; i < colorSplit.length; i++) {
/* 228 */       if (colorSplit[i].contains("red:")) {
/* 229 */         colorSplit[i] = colorSplit[i].replace("red:", "");
/* 230 */         if (!multipleSentence) {
/* 231 */           player.sendMessage(ChatColor.RED + colorSplit[i]);
        }
        else {
/* 234 */           chatLine = chatLine + ChatColor.RED + colorSplit[i];
        }
      }
/* 237 */       else if (colorSplit[i].contains("blue:")) {
/* 238 */         colorSplit[i] = colorSplit[i].replace("blue:", "");
/* 239 */         if (!multipleSentence) {
/* 240 */           player.sendMessage(ChatColor.BLUE + colorSplit[i]);
        }
        else {
/* 243 */           chatLine = chatLine + ChatColor.BLUE + colorSplit[i];
        }
      }
/* 246 */       else if (colorSplit[i].contains("green:")) {
/* 247 */         colorSplit[i] = colorSplit[i].replace("green:", "");
/* 248 */         if (!multipleSentence) {
/* 249 */           player.sendMessage(ChatColor.GREEN + colorSplit[i]);
        }
        else {
/* 252 */           chatLine = chatLine + ChatColor.GREEN + colorSplit[i];
        }
      }
/* 255 */       else if (colorSplit[i].contains("yellow:")) {
/* 256 */         colorSplit[i] = colorSplit[i].replace("yellow:", "");
/* 257 */         if (!multipleSentence) {
/* 258 */           player.sendMessage(ChatColor.YELLOW + colorSplit[i]);
        }
        else {
/* 261 */           chatLine = chatLine + ChatColor.YELLOW + colorSplit[i];
        }
      }
/* 264 */       else if (colorSplit[i].contains("white:")) {
/* 265 */         colorSplit[i] = colorSplit[i].replace("white:", "");
/* 266 */         if (!multipleSentence) {
/* 267 */           player.sendMessage(ChatColor.WHITE + colorSplit[i]);
        }
        else {
/* 270 */           chatLine = chatLine + ChatColor.WHITE + colorSplit[i];
        }
      }
/* 273 */       else if (colorSplit[i].contains("purple:")) {
/* 274 */         colorSplit[i] = colorSplit[i].replace("purple:", "");
/* 275 */         if (!multipleSentence) {
/* 276 */           player.sendMessage(ChatColor.LIGHT_PURPLE + colorSplit[i]);
        }
        else {
/* 279 */           chatLine = chatLine + ChatColor.LIGHT_PURPLE + colorSplit[i];
        }
      }
/* 282 */       else if (colorSplit[i].contains("gray:")) {
/* 283 */         colorSplit[i] = colorSplit[i].replace("gray:", "");
/* 284 */         if (!multipleSentence) {
/* 285 */           player.sendMessage(ChatColor.GRAY + colorSplit[i]);
        }
        else {
/* 288 */           chatLine = chatLine + ChatColor.GRAY + colorSplit[i];
        }

      }
/* 292 */       else if (!colorSplit[i].isEmpty()) {
/* 293 */         player.sendMessage(colorSplit[i]);
      }

/* 296 */       if ((multipleSentence != true) || 
/* 297 */         (i != colorSplit.length - 1)) continue;
/* 298 */       player.sendMessage(chatLine);
    }
  }

  public String hash(String input) throws NoSuchAlgorithmException
  {
/* 304 */     MessageDigest md = MessageDigest.getInstance("MD5");
/* 305 */     md.update(input.getBytes());
/* 306 */     BigInteger bigInt = new BigInteger(1, md.digest());
/* 307 */     String result = bigInt.toString(16);
/* 308 */     if (result.length() % 2 != 0) {
/* 309 */       result = "0" + result;
    }
/* 311 */     return result;
  }
}
