package client;

import gui.ClientGui;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;

import server.RServer;
import server.Server;

public class Client extends java.rmi.server.UnicastRemoteObject implements RClient {
	private static final String HOST = "localhost";
	private int download;
	private String name;
	private RServer rs;
	private Vector<Risorsa> risorse;
	ClientGui gui=new ClientGui(this);;
	public Client(String n,String s,String d,Vector<Risorsa>r) throws RemoteException{
		download=Integer.parseInt(d);//converti d in int
		name=n;
		risorse=r;
		rs=connetti(s);
		if(rs==null){
			gui.addLog("Fallimento nella connessione al server"+s);
		}
		else{
			gui.addLog("Connesso al server"+s);
		}
		rs.addclient(this);
	}
	
	public static RServer connetti(String  s){
		RServer ref = null;
		try{
			ref=(RServer) Naming.lookup("rmi://" + HOST + "/"+s);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		return ref;
		//ho il rif rem al server
	}

	public String getname() {
		return name;
	}
}
