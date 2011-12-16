

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;

class PHPSktSrvrThread extends Thread
{
  Server bukkitServer;
/* 311 */   boolean active = true;
  String conPort;
  String pass;

  public PHPSktSrvrThread(Server server, String password, String port)
  {
/* 315 */     if (Main.debugWebsend) System.out.println("Websend: ServerSocket thread made.");
/* 316 */     this.bukkitServer = server;
/* 317 */     this.pass = password;
/* 318 */     this.conPort = port;
  }
  public void close() {
/* 321 */     if (Main.debugWebsend) System.out.println("Websend: ServerSocket close function called.");
/* 322 */     this.active = false;
  }

  public void run()
  {
/* 327 */     System.out.println("Websend: ServerSocket: activated.");
    try {
/* 329 */       ServerSocket server = new ServerSocket(Integer.parseInt(this.conPort));
/* 330 */       if (Main.debugWebsend) System.out.println("Websend: ServerSocket object created.");
/* 331 */       while (this.active == true) {
/* 332 */         Socket skt = server.accept();
/* 333 */         if (Main.debugWebsend) System.out.println("Websend: Accepted Socket.");
/* 334 */         BufferedReader reader = new BufferedReader(new InputStreamReader(skt.getInputStream()));
/* 335 */         Interpretator interpretator = new Interpretator();
/* 336 */         if (Main.debugWebsend) System.out.println("Websend: Stream reader and interpretator created.");
/* 337 */         boolean checkLinePassed = false;
/* 338 */         if (Main.debugWebsend) System.out.println("Websend: Reading started.");
        String line;
/* 339 */         while ((line = reader.readLine()) != null) {
/* 340 */           boolean containsPassword = true;
/* 341 */           int lineNumber = 0;
/* 342 */           String[] splittedLine = null;
/* 343 */           if (line.contains("<Password>")) {
/* 344 */             if (Main.debugWebsend) System.out.println("Websend: ServerSocket: Found password.");
/* 345 */             splittedLine = line.split("<Password>");
          }
          else {
/* 348 */             containsPassword = false;
/* 349 */             if (!checkLinePassed) {
/* 350 */               System.out.println("WARNING: During a web-triggered connection attempt");
/* 351 */               System.out.println("no password was found on the first line, any command");
/* 352 */               System.out.println("before the authentification will be ignored.");
            }
          }
/* 355 */           if ((!checkLinePassed) && (!containsPassword))
            try {
/* 357 */               if ((splittedLine.length < 1) && (lineNumber == 0)) {
/* 358 */                 System.out.println("WARNING: During a web-triggered connection attempt");
/* 359 */                 System.out.println("no password was found on the first line, any command");
/* 360 */                 System.out.println("before the authentification will be ignored.");
              }
/* 362 */               else if (splittedLine[0].trim().equalsIgnoreCase(interpretator.hash(this.pass).trim())) {
/* 363 */                 checkLinePassed = true;
              }
/* 365 */               else if (splittedLine[0].contains(";")) {
/* 366 */                 System.out.println("WARNING: During a web-triggered connection attempt");
/* 367 */                 System.out.println("no password was found on the first line, any command");
/* 368 */                 System.out.println("before the authentification will be ignored.");
              }
/* 370 */               else if (lineNumber == 0) {
/* 371 */                 System.out.println("WARNING: During a web-triggered connection attempt");
/* 372 */                 System.out.println("no password was found on the first line, any command");
/* 373 */                 System.out.println("before the authentification will be ignored.");
              }
            } catch (NoSuchAlgorithmException ex) {
/* 376 */               Logger.getLogger(PHPSktSrvrThread.class.getName()).log(Level.SEVERE, null, ex);
/* 377 */               if (Main.debugWebsend) System.out.println("Websend: Impossible error: Wrong hash algoritm");
            }
          else {
            try
            {
/* 382 */               if (splittedLine[0].trim().equalsIgnoreCase(interpretator.hash(this.pass).trim()))
/* 383 */                 checkLinePassed = true;
            }
            catch (NoSuchAlgorithmException ex) {
/* 386 */               Logger.getLogger(PHPSktSrvrThread.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
/* 389 */           if (checkLinePassed == true) {
/* 390 */             if (Main.debugWebsend) System.out.println("Websend: Parsing line");
/* 391 */             if (containsPassword) {
/* 392 */               interpretator.NoPlayerInterpretate(splittedLine[1], this.bukkitServer);
            }
            else {
/* 395 */               interpretator.NoPlayerInterpretate(line, this.bukkitServer);
            }
          }
/* 398 */           lineNumber++;
        }
/* 400 */         if (Main.debugWebsend) System.out.println("Websend: Closing socket.");
/* 401 */         skt.close();
/* 402 */         if (Main.debugWebsend) System.out.println("Websend: Socket closed.");
/* 403 */         checkLinePassed = false;
      }
    }
    catch (IOException ex) {
/* 407 */       Logger.getLogger(PHPSktSrvrThread.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}