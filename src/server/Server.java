package server;

import gui.ServerGui;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;

import client.Client;
import client.RClient;
import server.VistaModel;

public class Server extends java.rmi.server.UnicastRemoteObject implements RServer{
	private String name;
	private Vector<RServer> listaserver=new Vector<RServer>();
	private Vector<RClient> listaclient=new Vector<RClient>();
	private ServerGui gui;
	private Thread connetti;
	private Object lock=new Object();
	private final VistaModel model = new VistaModel();
	
	public Server(String n)throws RemoteException{//COSTRUTTORE
		name=n;
		System.out.println("Server creato vero"+name);
		gui=new ServerGui(this);
		connetti=new connettiserver(this);
		connetti.start();
	}
	public String getname(){
		return name;
	}
	
	public void uscita() throws RemoteException, MalformedURLException, NotBoundException{
		synchronized(lock){
			connetti.interrupt();
			Naming.unbind("rmi://localhost/"+name);
			System.out.println("Disconnesso il server"+name);
		}
	}
	public boolean checkserver(RServer rs){
		if(listaserver.contains(rs))
			return true;
		else return false;
	}
	public boolean addclient(RClient c) throws RemoteException {
		listaclient.add(c);
		gui.addLog("Si e' connesso il client"+c.getname());
		return true;
	}
	public boolean addserver(RServer s) throws RemoteException {
		listaserver.add(s);
		gui.addLog("Connesso con il server"+s.getname());
		return true;
	}
	class connettiserver extends  Thread{
		private Server s;
		public connettiserver(Server s){ 
			setDaemon(true);
			this.s=s;
		}
		public void run(){
			while(true){
				synchronized(lock){
					try {
						String[] a=Naming.list("rmi://localhost/");
						for(int i=0;i<a.length;i++){
							RServer rs=(RServer)Naming.lookup(a[i]);
							System.out.println("Controllo"+rs.getname());
							if(s.checkserver(rs)){
								s.addserver(rs);
							}
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}
		}
	}
}
