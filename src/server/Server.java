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
	private String name;  //nome del server
	private Vector<RServer> listaserver=new Vector<RServer>();//lista di server connessi
	private Vector<RClient> listaclient=new Vector<RClient>();//lista di client connessi
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
	
	class connettiserver extends  Thread{//thread per la connessione ai server
		private Server s;//contiene il server che vuole aggiornare la propria lista PROBABILMENTE s inutile perche basta usare THIS
		public connettiserver(Server s){ 
			setDaemon(true);
			this.s=s;
		}
		public void run(){
			while(true){
				synchronized(lock){
					try {
						String[] a=Naming.list("rmi://localhost/");// lista di tutti i server attualmente registrati 
						for(int i=0;i<a.length;i++){
							RServer rs=(RServer)Naming.lookup(a[i]);
							System.out.println("Controllo"+rs.getname());
							if(!s.checkserver(rs)){// se non è già nella lista lo aggiungo
								s.addserver(rs);
							}//TO DO else si potrebbe controllare se è nella lista ma non esiste(è cashato) e rimuoverlo
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
	//metodo di RServer
	public Vector<RServer> cercarisorsa(String n,int p)throws RemoteException{
		//invoco il metodo che cerca tra le mie risorse la risorsa cercata
		Vector<RServer> listaconrisorsa=new Vector<RServer>();
		listaconrisorsa.add(gotresource(n,p));
		return listaconrisorsa;
	}
	private RServer gotresource(String n, int p) {
		return this;
	}

	//public boolean downloadrequest(RRisorsa r)
	//metodi get
	public String getname(){
		return name;
	}
	//metodi set
	public boolean addserver(RServer s) throws RemoteException {//metodo che aggiunge al server s i server nuovi
		listaserver.add(s);
		gui.addLog("Connesso con il server"+s.getname());
		return true;
	}
}
