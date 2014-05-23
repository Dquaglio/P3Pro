package risorsa;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Risorsa extends UnicastRemoteObject implements RRisorsa{
	String name;
	int parts;
	public Risorsa(String n,String p) throws RemoteException{
		name=n;
		parts=Integer.parseInt(p);
	}
	public Risorsa(String n,int p) throws RemoteException{
		name=n;
		parts=p;
	}
	public String getnome()throws RemoteException{
		return name;
	}
	public int getparti()throws RemoteException{
		return parts;
	}
}
