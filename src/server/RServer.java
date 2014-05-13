package server;
import java.rmi.*;
import java.util.Vector;

import client.Client;
import client.RClient;

public interface RServer extends Remote {
	public boolean addclient(RClient c)throws RemoteException;
	public boolean addserver(RServer s)throws RemoteException;
	public String getname()throws RemoteException;
	public Vector<RClient> cercarisorsa(String nome, int parti)throws RemoteException;
	public Vector<RClient> gotresource(String n, int p)throws RemoteException;
}
