package client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface RClient extends Remote {
	public String getname()throws RemoteException;
	public void upload()throws RemoteException;
	public RClient haveresource(String n,int p)throws RemoteException;//metodo invocato dal server per cercare se ha la risorsa X
	public void disconnectServer()throws RemoteException;
	public String getResourceList() throws RemoteException;
}
