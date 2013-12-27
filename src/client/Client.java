package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;

import server.RServer;
import server.Server;

public class Client {
	private static final String HOST = "localhost";
	private int download;
	private String name;
	private RServer rs;
	private Vector<Risorsa> risorse;
	public Client(String n,String s,String d,Vector<Risorsa>r){
		download=Integer.parseInt(d);//converti d in int
		name=n;
		risorse=r;
		try {
			rs=(RServer) Naming.lookup("rmi://" + HOST + "/"+s);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getname(){
		return name;
	}
}
