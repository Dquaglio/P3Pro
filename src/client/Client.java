package client;

import gui.ClientGui;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;

import risorsa.Risorsa;
import server.RServer;
public class Client extends java.rmi.server.UnicastRemoteObject implements RClient {
	private static final String HOST = "localhost";
	private int sleep=2000;//tempo di upload
	private int download; //capacità di download del client
	private String name;   //nome client
	private RServer rs=null;    // riferimento al server a cui è connesso
	private Vector<Risorsa> risorse; //vettore di risorse di cui dispone il client
	private Integer downloadattivi=0;//numero di download attivi
	ClientGui gui=new ClientGui(this);
	//costruttore
	public Client(String n,String s,String d,Vector<Risorsa>r) throws RemoteException{// n= nome ; s=  nome server ; d capacità di download; r vettore di risorse
		download=Integer.parseInt(d);//converti d in int
		name=n;
		risorse=r;
		rs=connetti(s);
		if(rs==null){
			gui.addLog("Fallimento nella connessione al server"+s);
		}
		else{
			gui.addLog("Connesso al server"+s);
		}
		rs.addclient(this);
	}
	//metodo per la connessione del client al server
	public static RServer connetti(String  s){
		RServer ref = null;
		try{
			ref=(RServer) Naming.lookup("rmi://" + HOST + "/"+s);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		return ref;
		//ho il rif rem al server
	}
	//thread addetto al download parte alla pressione del tasto download
	class Download extends Thread{
		//CAMPI DATI
		String nome;
		int parti;
		ScaricaRisorsa[] downloads;//array di thread 
		Vector<RClient> ClientRisorsa=null;
		Download(String n,int p){
			nome=n;
			parti=p;
		}
		//cerca di scaricare la risorsa
		public void run(){
			try {
				ClientRisorsa=rs.cercarisorsa(nome,parti);//ottengo i client con la risorsa
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			//ho il vettore di client con la risorsa
			if(ClientRisorsa.size()>parti)
				downloads=new ScaricaRisorsa[ClientRisorsa.size()];//array inizializzato con il massimo numero possibile di download contemporanei
			else
				downloads=new ScaricaRisorsa[parti];
			int partiscaricate=0; //contatore di parti scaricate correttamente
			while(partiscaricate!=parti){
				synchronized(downloadattivi){//lock su downloadattivi in modo che il client non possa scaricare piu della capacità massima contemporaneamente
					while(downloadattivi<download){
						int downloadpossibili=download-downloadattivi;
						for(int i=0;i<downloadpossibili;i++){
							try{
								downloads[i]=new ScaricaRisorsa(ClientRisorsa.get(i));
								downloads[i].start();//
								downloads[i].getdone();//ritorna se successo nel donwload o n
							}
							catch(Exception e){
								
							}
						}
					}
				}
			}
		}
	}
	//metodo per il download di una risorsa
	class ScaricaRisorsa extends Thread{
		//dati della risorsa da cercare
		boolean scaricata=false;
		RClient client;
		ScaricaRisorsa(RClient c){
			client=c;
		}
		public void run(){
			try {
				client.upload();//invocato sul client da cui voglio scaricare
			} catch (RemoteException e) {
				// GESTISCO L' eccezione di fallito download
				e.printStackTrace();
			}
		}
		public boolean getdone(){
			return scaricata;
		}
	}
	
	public void upload()throws RemoteException{
		try {
			Thread.sleep(sleep);//simulo il download
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//METODI VARI GET
	public String getname() {
		return name;
	}
	//metodo per la ricerca della risorsa desiderata
	public RClient haveresource(String n,int p) throws RemoteException {
		//ricerco nell array la risorsa
		boolean	found=false;
		for(int i=0;!found || i<risorse.size();i++){
			if(risorse.get(i).getnome()==n && risorse.get(i).getparti()==p)
				found=true;
		}
		if(found)
			return this;
		else 
			return null;
	}
}
