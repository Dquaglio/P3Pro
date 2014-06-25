package server;
import java.rmi.*;
import java.util.Vector;
import client.RClient;




public interface RServer extends Remote {
	public void addclient(RClient c)throws RemoteException;
	public String getname()throws RemoteException;
	public Vector<RClient> cercarisorsa(String nome, int parti, RClient c)throws RemoteException;
	public Vector<RClient> gotresource(String n, int p,RClient c)throws RemoteException;
	public void disconnettiClient(RClient client)throws RemoteException;
	public void exist()throws RemoteException;
}
