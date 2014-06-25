package client;

import gui.ClientGui;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;
import risorsa.Risorsa;
import server.RServer;

public class Client extends java.rmi.server.UnicastRemoteObject implements RClient {
	private static final String HOST = "localhost";
	private int sleep=5000;//tempo di upload
	private int download; //capacità di download del client
	private boolean downloading=false;
	private String name;   //nome client
	private RServer riferimentoServer=null;    // riferimento al server a cui è connesso
	private Vector<Risorsa> risorse; //vettore di risorse di cui dispone il client
	private ClientGui gui;

	//costruttore del client
	public Client(String n,String s,String d,Vector<Risorsa>r) throws RemoteException{// n= nome ; s=  nome server ; d capacità di download; r vettore di risorse
		download=Integer.parseInt(d);//converti d in int
		name=n;
		risorse=r;
		riferimentoServer=connetti(s);
		gui=new ClientGui(this,risorse);
		gui.addWindowListener(new WindowAdapter(){	
			public void windowClosing(WindowEvent evt) {
				disconnect();
				gui.setVisible(false);
				gui.RemovClientRef();
				gui.dispose();
			}
		});
		if(riferimentoServer==null){
			gui.addLog("Fallimento nella connessione al server"+s);
		}
		else{
			gui.addLog("Connesso al server"+s);
		}
		riferimentoServer.addclient(this);
	}

	//metodo per la connessione del client al server
	public RServer connetti(String  s){
		RServer ref = null;
		try{
			ref=(RServer)Naming.lookup("rmi://" + HOST + "/"+s);
		} catch (MalformedURLException e) {
			gui.addLog("Impossibile connettersi al server desiderato");
		} catch (RemoteException e) {
			gui.addLog("Impossibile connettersi al server desiderato");
		} catch (NotBoundException e) {
			gui.addLog("Impossibile connettersi al server desiderato");
		}
		return ref;
		//ho il rif rem al server
	}

	public void eseguiDownload(String n,int p) throws RemoteException{
		if(!downloading){
			if(haveresource(n,p)==null){
				downloading=true;
				Download d=new Download(n,p,this);
				d.start();
			}
			else{
				gui.addLog("Possiedi già la risorsa cercata");
			}
		}
		else{
			gui.addLog("Stai già scaricando...Aspetta");
		}
	}
	//thread addetto al download parte alla pressione del tasto download
	class Download extends Thread{
		//CAMPI DATI
		String nome;
		int parti;
		Vector<ScaricaRisorsa> downloads=new Vector<ScaricaRisorsa>();//array di thread 
		Vector<RClient> ClientRisorsa=new Vector<RClient>();
		Integer downloadattivi=0;
		int partiscaricate=0; //contatore di parti scaricate correttamente
		Client c;//client che scarica
		Download(String n,int p,Client c){
			setDaemon(true);
			nome=n;
			parti=p;
			this.c=c;
			gui.addLog("Preparazione al download...");
		}
		//cerca di scaricare la risorsa
		public void run(){
			if(riferimentoServer!=null){
				try {
					riferimentoServer.exist();
				} catch (RemoteException e) {
					riferimentoServer=null;
					gui.addLog("Impossibile cercare la risorsa, server non raggiungibile");
					return;
				}
			}else{
				gui.addLog("Server non raggiungibile!!");
			}
			try {
				ClientRisorsa=riferimentoServer.cercarisorsa(nome,parti,c);//ottengo i client con la risorsa

				gui.addLog("Ottenuta la lista di client con la risorsa "+nome+parti);
			} catch (RemoteException e) {
				gui.addLog("Errore nel download della lista di client con la risorsa cercata");
				return;
			}
			//ho il vettore di client con la risorsa
			for(int j=0;j<ClientRisorsa.size();j++){//mi creo il vector 
				downloads.add(new ScaricaRisorsa(ClientRisorsa.get(j),this,nome,parti));
			}
			if(downloads!=null){
				while( partiscaricate!=parti &&  !(downloads.size()==0 && downloadattivi.equals(0) ) ){//finchè non ho scaricato tutte le parti o non ho nessuno da cui scaricare
					//int partirimanenti=;
					synchronized(this){//lock su downloadattivi in modo che il client non possa scaricare piu della capacità massima contemporaneamente
						int downloadpossibili=download-downloadattivi;//NB avendo il lock su downloadsessione e download non può cambiare valore so di fare un operazione sicura
						for(int i=0;i<downloadpossibili && downloads.size()!=0 && downloadattivi<(parti-partiscaricate);i++){
							downloads.get(0).start();
							downloadattivi=downloadattivi+1;
							downloads.remove(0);//rimuovo la possibilità di scaricare da questo client in quanto un download è appena stato avviato
						}
					}
				}
			}
			if(downloads.size()==0 && partiscaricate!=parti){
				gui.addLog("Download della risorsa impossibile da portare a termine");
			}
			else{
				//download effettutato correttamente
				try {
					risorse.add(new Risorsa(nome,parti));
					gui.addRisorsa(risorse.lastElement());
					gui.addLog("Risorsa scaricata correttamente");
				} catch (RemoteException e) {
					//errore nella creazione della risorsa
					gui.addLog("Errore nella creazione della risorsa");
				}
			}
			downloading=false;
		}
	}
	//metodo per il download di una parte di risorsa da un client
	class ScaricaRisorsa extends Thread{
		//dati della risorsa da cercare
		boolean scaricata=false;
		RClient client;
		Download padre;
		String nomeris;
		int partiris;
		ScaricaRisorsa(RClient c, Download father, String nome, int parti){
			setDaemon(true);
			client=c;
			padre=father;
			nomeris=nome;
			partiris=parti;
		}
		public void run(){
			int index=-1;
			try {
				/*aggiorno la gui*/
				synchronized(gui){
					index=gui.getListSize();
					gui.addDownElement(client.getname(),nomeris,partiris);
					gui.addLog("Inizio download parte da "+client.getname());
				}
				client.upload();//invocato sul client da cui voglio scaricare
				gui.addLog("Parte di risorsa ricevuta da "+client.getname());
				synchronized(padre){//in questo modo so che partiscaricate e downloadattivi non li tocca nessun altro
					padre.partiscaricate=padre.partiscaricate+1;//non arriva qui se ci sono problemi
					padre.downloadattivi=padre.downloadattivi-1;
					padre.downloads.add(new ScaricaRisorsa(client,padre,nomeris,partiris));//il download da client è finito correttamente, posso se serve riscaricare da lui ora
				}/*aggiorno la gui*/
				synchronized(gui){
					gui.completedownload(index);
				}		
			} catch (RemoteException e) {
				synchronized(padre){
					gui.addLog("Uno dei Client da cui si stava scaricando non è più raggiungibile");
					gui.faildownload(index);
					padre.downloadattivi=padre.downloadattivi-1;
				}
			}//al termine di tutto che sia andato bene o male il download è finito

		}
		public boolean getdone(){
			return scaricata;
		}
	}

	public void upload()throws RemoteException{
		try {
			Thread.sleep(sleep);//simulo il download
		} catch (InterruptedException e) {
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
		for(int i=0;!found && i<risorse.size();i++){
			if(risorse.get(i).getnome().equals(n)){
				if(risorse.get(i).getparti()==p){
					found=true;
				}
			}
		}
		if(found){
			return this;
		}
		else{ 
			return null;
		}
	}
	public void disconnect() {
		if (riferimentoServer != null) {
			try {
				riferimentoServer.disconnettiClient(this);
				riferimentoServer=null;
			} 
			catch (RemoteException e) { 
				gui.addLog("Errore di connessione");
			}
		}
		else{
			gui.addLog("Impossibile disconnettersi");
		}
	}
	public String getResourceList() throws RemoteException{
		String ListaRisorse=null;
		for(int i=0;i<risorse.size();i++){
			if(i==0)
				ListaRisorse=risorse.elementAt(i).getnome()+" "+risorse.elementAt(i).getparti()+" ";
			ListaRisorse=ListaRisorse+risorse.elementAt(i).getnome()+" "+risorse.elementAt(i).getparti()+" ";
		}
		return ListaRisorse;
	}
	//quando si disconnette, il server richiama questo metodo
	public void disconnectServer() throws RemoteException {
		riferimentoServer=null;
		gui.addLog("Server Disconnesso");

	}
}
