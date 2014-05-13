package server;

import gui.ServerGui;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;

import client.Client;
import client.RClient;
import risorsa.RRisorsa;
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
		connetti=new connettiserver();
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
		//contiene il server che vuole aggiornare la propria lista PROBABILMENTE s inutile perche basta usare THIS
		public connettiserver(){ 
			setDaemon(true);
		}
		public void run(){
			while(true){
				synchronized(lock){
					try {
						String[] a=Naming.list("rmi://localhost/");// lista di tutti i server attualmente registrati 
						listaserver.clear();
						for(int i=0;i<a.length;i++){
							RServer rs=(RServer)Naming.lookup(a[i]);
							if(!listaserver.contains(rs)){// se il server non è già in lista lo aggiungo
								listaserver.add(rs);
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
	public Vector<RClient> cercarisorsa(String n,int p)throws RemoteException{
		//invoco il metodo che cerca tra le mie risorse la risorsa cercata
		Vector<RClient> listaconrisorsa=new Vector<RClient>();
		for(int i=0;i<listaserver.size();i++){//scorro tutti i server e mi faccio dare la lista di client con la risorsa
			Vector<RClient>conrisorsa=listaserver.get(i).gotresource(n, p);
			if(conrisorsa!=null)
				listaconrisorsa.addAll(conrisorsa);//aggiungo alla lista gli elementi del server i
		}
		return listaconrisorsa;
	}
	//metodo che ritorna la lista di client con la risorsa cercata
	public Vector<RClient> gotresource(String n, int p)throws RemoteException{
		Vector<RClient> ritorno=new Vector<RClient>();
		for(int i=0;i<listaclient.size();i++){
			RClient rc=listaclient.get(i).haveresource(n, p);
			if(rc!=null)
				ritorno.add(rc);
		}
		return ritorno;
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
