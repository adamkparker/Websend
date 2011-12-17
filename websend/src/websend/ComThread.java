package websend;

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.entity.Player;

class ComThread extends Thread
{
  String url;
  String pass;
  String[] args;
  Player plsender = null;

  boolean altprotection = false;
  boolean forceport = false;
  Server server;
  String urlfile = "";

  public ComThread(String urlArg, String passArg, String[] argsArg, Player plsenderArg, boolean altprotect, boolean forcePort4444, Server serverNew, String file) { this.url = urlArg;
    this.pass = passArg;
    this.args = argsArg;
    if (plsenderArg != null) {
      this.plsender = plsenderArg;
    }
    this.altprotection = altprotect;
    this.server = serverNew;
    this.forceport = forcePort4444;
    this.urlfile = file; }

  public void setVariables(String urlArg, String passArg, String[] argsArg, Player plsenderArg, boolean altprotect, boolean forcePort4444, Server serverNew, String file) {
    this.url = urlArg;
    this.pass = passArg;
    this.args = argsArg;
    this.plsender = plsenderArg;
    this.altprotection = altprotect;
    this.server = serverNew;
    this.forceport = forcePort4444;
    this.urlfile = file;
  }

  public void run()
  {
    try {
      if (Main.debugWebsend) System.out.println("Websend: A bukkit -> php communicator was started.");
      Communicator com = new Communicator();
      com.sendPOST(this.url, this.pass, this.args, this.plsender, this.altprotection, this.forceport, this.server, this.urlfile);
    } catch (Exception ex) {
      Logger.getLogger(ComThread.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}

/* Location:           /Users/adamkparker/Desktop/Websend.jar
 * Qualified Name:     websend.ComThread
 * JD-Core Version:    0.6.0
 */