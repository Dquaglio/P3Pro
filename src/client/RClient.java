package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RClient extends Remote {
	public String getname()throws RemoteException;
	public void upload()throws RemoteException;
	public RClient haveresource(String n,int p)throws RemoteException;
}
