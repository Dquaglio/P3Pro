package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RClient extends Remote {
	public String getname()throws RemoteException;
}
