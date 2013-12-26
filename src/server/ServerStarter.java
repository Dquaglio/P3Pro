package server;

import gui.ServerGui;

import java.rmi.Naming;

public class ServerStarter {
	private static final String HOST = "localhost";
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("No good params given. Proper usage is: `java Server.ServerStarter Server1`");
			System.exit(1);
		}
		Server s=new Server(args[0]);
		Naming.rebind( "rmi://" + HOST + "/" + s.getname() , s );
		ServerGui gui=new ServerGui(s);
		
	}
}
