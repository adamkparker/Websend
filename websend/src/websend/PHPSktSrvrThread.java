package websend;

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
  boolean active = true;
  String conPort;
  String pass;

  public PHPSktSrvrThread(Server server, String password, String port)
  {
    if (Main.debugWebsend) System.out.println("Websend: ServerSocket thread made.");
    this.bukkitServer = server;
    this.pass = password;
    this.conPort = port;
  }
  public void close() {
    if (Main.debugWebsend) System.out.println("Websend: ServerSocket close function called.");
    this.active = false;
  }

  public void run()
  {
    System.out.println("Websend: ServerSocket: activated.");
    try {
      ServerSocket server = new ServerSocket(Integer.parseInt(this.conPort));
      if (Main.debugWebsend) System.out.println("Websend: ServerSocket object created.");
      label383: while (this.active) {
        Socket skt = server.accept();
        if (Main.debugWebsend) System.out.println("Websend: Accepted Socket.");
        BufferedReader reader = new BufferedReader(new InputStreamReader(skt.getInputStream()));
        Interpretator interpretator = new Interpretator();
        if (Main.debugWebsend) System.out.println("Websend: Stream reader and interpretator created.");
        boolean checkLinePassed = false;
        if (Main.debugWebsend) System.out.println("Websend: Reading started.");
        String line;
        while ((line = reader.readLine()) != null)
        {
          //String line;
          boolean containsPassword = true;
          int lineNumber = 0;
          String[] splittedLine = (String[])null;
          if (line.contains("<Password>")) {
            if (Main.debugWebsend) System.out.println("Websend: ServerSocket: Found password.");
            splittedLine = line.split("<Password>");
          }
          else {
            containsPassword = false;
            if (!checkLinePassed) {
              System.out.println("WARNING: During a web-triggered connection attempt");
              System.out.println("no password was found on the first line, any command");
              System.out.println("before the authentification will be ignored.");
            }
          }
          if ((!checkLinePassed) && (!containsPassword)) {
            try {
              if ((splittedLine.length < 1) && (lineNumber == 0)) {
                System.out.println("WARNING: During a web-triggered connection attempt");
                System.out.println("no password was found on the first line, any command");
                System.out.println("before the authentification will be ignored."); break;
              }
              if (splittedLine[0].trim().equalsIgnoreCase(interpretator.hash(this.pass).trim())) {
                checkLinePassed = true; break;
              }
              if (splittedLine[0].contains(";")) {
                System.out.println("WARNING: During a web-triggered connection attempt");
                System.out.println("no password was found on the first line, any command");
                System.out.println("before the authentification will be ignored."); break;
              }
              if (lineNumber != 0) break; System.out.println("WARNING: During a web-triggered connection attempt");
              System.out.println("no password was found on the first line, any command");
              System.out.println("before the authentification will be ignored.");
            }
            catch (NoSuchAlgorithmException ex) {
              Logger.getLogger(PHPSktSrvrThread.class.getName()).log(Level.SEVERE, null, ex);
              if (Main.debugWebsend) break label383;  } break; //System.out.println("Websend: Impossible error: Wrong hash algoritm");
          }
          else
          {
            try {
              if (splittedLine[0].trim().equalsIgnoreCase(interpretator.hash(this.pass).trim()))
                checkLinePassed = true;
            }
            catch (NoSuchAlgorithmException ex) {
              Logger.getLogger(PHPSktSrvrThread.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
          if (checkLinePassed) {
            if (Main.debugWebsend) System.out.println("Websend: Parsing line");
            if (containsPassword) {
              interpretator.NoPlayerInterpretate(splittedLine[1], this.bukkitServer);
            }
            else {
              interpretator.NoPlayerInterpretate(line, this.bukkitServer);
            }
          }
          lineNumber++;
        }
        if (Main.debugWebsend) System.out.println("Websend: Closing socket.");
        skt.close();
        if (Main.debugWebsend) System.out.println("Websend: Socket closed.");
        checkLinePassed = false;
      }
    }
    catch (IOException ex) {
      Logger.getLogger(PHPSktSrvrThread.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}

/* Location:           /Users/adamkparker/Desktop/Websend.jar
 * Qualified Name:     websend.PHPSktSrvrThread
 * JD-Core Version:    0.6.0
 */