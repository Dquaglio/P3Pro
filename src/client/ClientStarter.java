package client;

import gui.ClientGui;

import java.util.Vector;

public class ClientStarter {
	private static final String HOST = "localhost";
	public static void main(String[] args) throws Exception {
		if (args.length==0) {
			System.out.println("No good params given. Proper usage is: `java Client.ClientStarter Client1 3 A 1 B 3`");
			System.exit(1);
		}
		Vector<Risorsa>v=new Vector<Risorsa>();
		Risorsa a;
		for(int i=3;i<args.length;i=i+2){
			a=new Risorsa(args[i],args[i+1]);
			System.out.println(args[i]+args[i+1]);
			v.add(a);
		}
		Client c=new Client(args[0],args[1],args[2],v);
	}
}
