package server;
import java.rmi.*;

import client.Client;
import client.RClient;

public interface RServer extends Remote {
	public boolean addclient(RClient c)throws RemoteException;
}
