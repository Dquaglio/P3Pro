package risorsa;
import java.rmi.Remote;
import java.rmi.RemoteException;
public interface RRisorsa extends Remote {
	public String getnome()throws RemoteException;
}
