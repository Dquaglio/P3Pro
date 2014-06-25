package server;

import gui.ServerGui;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;
import client.RClient;
public class Server extends java.rmi.server.UnicastRemoteObject implements RServer{
	private static final String HOST = "localhost";
	private String name;  //nome del server
	private Vector<RServer> listaserver=new Vector<RServer>();//lista di server connessi
	private Vector<RClient> listaclient=new Vector<RClient>();//lista di client connessi
	private ServerGui gui;
	private Thread connetti;
	private Object lockserver=new Object();
	private Object lockclient=new Object();

	public Server(String n)throws RemoteException{//COSTRUTTORE
		name=n;
		gui=new ServerGui(this);
		connetti=new connettiserver();
		connetti.start();
	}
	public void uscita() throws RemoteException, MalformedURLException, NotBoundException{
		synchronized(lockclient){
			connetti.interrupt();//interrompo il Thread che controlla i server circostanti
			Naming.unbind("rmi://" + HOST + "/"+name);
			for(int i=0;i<listaclient.size();i++){
				listaclient.get(i).disconnectServer();
			}
		}
	}
	public void addclient(RClient c) throws RemoteException {
		listaclient.add(c);
		gui.addLog("Si e' connesso il client "+c.getname() +" con le risorse "+c.getResourceList());
		gui.setClienList(listaclient);
	}

	class connettiserver extends  Thread{//thread per la connessione ai server
		//contiene il server che vuole aggiornare la propria lista PROBABILMENTE s inutile perche basta usare THIS
		public void run(){
			while(true){
				synchronized(lockserver){
					try {
						String[] a=Naming.list("rmi://" + HOST + "/");// lista di tutti i server attualmente registrati 
						listaserver.clear();
						for(int i=0;i<a.length;i++){
							RServer rs=(RServer)Naming.lookup(a[i]);
							listaserver.add(rs);
						}
						gui.setServerList(a);
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
		synchronized(lockserver){
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
	public Vector<RClient> gotresource(String n, int p,RClient c) throws RemoteException{
		synchronized(lockclient){
			Vector<RClient> ritorno=new Vector<RClient>();
			gui.addLog("Cerco la risorsa che mi è stata chiesta"+n+p);
			for(int i=0;i<listaclient.size();i++){
				if(!listaclient.get(i).equals(c)){//evito di controllare il client che me l ha chiesta
					RClient rc;
					try {
						rc = listaclient.get(i).haveresource(n, p);
						if(rc!=null)
							ritorno.add(rc);
					} catch (RemoteException e) {
						disconnettiClient(listaclient.get(i));
					}	

				}
			}
			return ritorno;
		}
	}
	//metodi get
	public String getname(){
		return name;
	}
	//metodi set
	public void disconnettiClient(RClient client) throws RemoteException {
		synchronized(lockclient){
			listaclient.remove(client);
			gui.setClienList(listaclient);
			gui.addLog(client.getname()+" Disconnesso");
		}
	}

	public void exist() throws RemoteException {
	}
}
