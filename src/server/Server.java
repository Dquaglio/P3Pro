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
	private static final String HOST = "localhost";
	private String name;  //nome del server
	private Vector<RServer> listaserver=new Vector<RServer>();//lista di server connessi
	private Vector<RClient> listaclient=new Vector<RClient>();//lista di client connessi
	private ServerGui gui;
	private Thread connetti;
	private Object lock=new Object();
	
	public Server(String n)throws RemoteException{//COSTRUTTORE
		name=n;
		gui=new ServerGui(this);
		connetti=new connettiserver();
		connetti.start();
	}
	public void uscita() throws RemoteException, MalformedURLException, NotBoundException{
		synchronized(lock){
			connetti.interrupt();//interrompo il Thread che controlla i server circostanti
			Naming.unbind("rmi://" + HOST + "/"+name);
			for(int i=0;i<listaclient.size();i++){
				listaclient.get(i).disconnectServer();
			}
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
		public void run(){
			while(true){
				synchronized(lock){
						try {
							String[] a=Naming.list("rmi://" + HOST + "/");// lista di tutti i server attualmente registrati 
							listaserver.clear();
							for(int i=0;i<a.length;i++){
								RServer rs=(RServer)Naming.lookup(a[i]);
								listaserver.add(rs);
							}
						} catch (RemoteException e) {
							gui.addLog("Server non più raggiungibile");
						} catch (MalformedURLException e) {
							gui.addLog("Url server errato");
						} catch (NotBoundException e) {
							gui.addLog("Server non esistente");
						}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					
				}
			}
		}
	}
	
	//metodo di RServer chiamato da CLient
	public Vector<RClient> cercarisorsa(String n,int p,RClient c)throws RemoteException{
		//invoco il metodo che cerca tra le mie risorse la risorsa cercata
		Vector<RClient> listaconrisorsa=new Vector<RClient>();
		synchronized(lock){
			for(int i=0;i<listaserver.size();i++){//scorro tutti i server e mi faccio dare la lista di client con la risorsa
				//if(!this.equals(listaserver.get(i))){
					Vector<RClient>conrisorsa=listaserver.get(i).gotresource(n, p,c);
					if(conrisorsa!=null)
						listaconrisorsa.addAll(conrisorsa);//aggiungo alla lista gli elementi del server i
				//}	
			}
		}
		if(listaconrisorsa.contains(c)){
			listaconrisorsa.remove(c);
		}
		gui.addLog("Ritorno la lista di client con la risorsa "+n+p);
		return listaconrisorsa;
	}
	
	//metodo che ritorna la lista di client con la risorsa cercata
	public Vector<RClient> gotresource(String n, int p,RClient c)throws RemoteException{
		Vector<RClient> ritorno=new Vector<RClient>();
		gui.addLog("Cerco la risorsa che mi è stata chiesta"+n+p);
		for(int i=0;i<listaclient.size();i++){
			if(!listaclient.get(i).equals(c)){//evito di controllare il client che me l ha chiesta
				RClient rc=listaclient.get(i).haveresource(n, p);	
				if(rc!=null)
					ritorno.add(rc);
			}
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
	public void disconnettiClient(RClient client) throws RemoteException {
		listaclient.remove(client);
	}

	public boolean exist() throws RemoteException {
		return true;
	}
}
