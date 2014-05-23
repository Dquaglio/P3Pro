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
	private Integer downloadattivi=0; //numero di download attivi
	private ClientGui gui;
	
	//costruttore del client
	public Client(String n,String s,String d,Vector<Risorsa>r) throws RemoteException{// n= nome ; s=  nome server ; d capacità di download; r vettore di risorse
		download=Integer.parseInt(d);//converti d in int
		name=n;
		risorse=r;
		rs=connetti(s);
		gui=new ClientGui(this);
		if(rs==null){
			gui.addLog("Fallimento nella connessione al server"+s);
		}
		else{
			gui.addLog("Connesso al server"+s);
		}
		rs.addclient(this);
	}
	
	//metodo per la connessione del client al server
	public RServer connetti(String  s){
		RServer ref = null;
		try{
			ref=(RServer)Naming.lookup("rmi://" + HOST + "/"+s);
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
	
	public void eseguiDownload(String n,int p){
		Download d=new Download(n,p,this);
		d.start();
	}
	//thread addetto al download parte alla pressione del tasto download
	class Download extends Thread{
		//CAMPI DATI
		String nome;
		int parti;
		Vector<ScaricaRisorsa> downloads=new Vector<ScaricaRisorsa>();//array di thread 
		Vector<RClient> ClientRisorsa=new Vector<RClient>();
		int downloadsessione=0;
		int partiscaricate=0; //contatore di parti scaricate correttamente
		Client c;//client che scarica
		Download(String n,int p,Client c){
			nome=n;
			parti=p;
			this.c=c;
		}
		//cerca di scaricare la risorsa
		public void run(){
				if(rs==null)
					System.out.println("LOL");
					try {
						rs.exist();
					} catch (RemoteException e) {
						rs=null;
						gui.addLog("Impossibile cercare la risorsa, server non raggiungibile");
						return;
					}	
			try {
				ClientRisorsa=rs.cercarisorsa(nome,parti,c);//ottengo i client con la risorsa
				System.out.println("Ottenuta la lista di client con la risorsa"+nome+parti);
				gui.addLog("Ottenuta la lista di client con la risorsa"+nome+parti);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				gui.addLog("Errore nel download della lista di client con la risorsa cercata");
				return;
			}
			//ClientRisorsa.remove(c);
			System.out.println("dimensione client con risorsa"+ClientRisorsa.size());
			//ho il vettore di client con la risorsa
			for(int j=0;j<ClientRisorsa.size();j++){//mi creo il vector 
				downloads.add(new ScaricaRisorsa(ClientRisorsa.get(j),this));
			}
			if(downloads==null){
				System.out.println("CAZZO");
			}
			while(partiscaricate!=parti){//finchè non ho scaricato tutte le parti o non ho nessuno da cui scaricare
				System.out.println("FLAG A");
				synchronized(downloadattivi){//lock su downloadattivi in modo che il client non possa scaricare piu della capacità massima contemporaneamente
					System.out.println("FLAG B");
					//while(downloadattivi<download){
						int downloadpossibili=download-downloadattivi;
						for(int i=0;i<downloadpossibili && downloads.size()!=0;i++){
								System.out.println("FLAG for "+i);
								System.out.println("la dimensione di Download è" +downloads.size());
								downloads.get(0).start();
								downloadattivi=downloadattivi+1;
								downloadsessione=downloadsessione+1;
								//gui.addDownElement(name,nome,parti);
								downloads.remove(0);
								System.out.println("i client rimasti da cui posso scaricare sono"+downloads.size());
						}
					//}
				}
			}
			if(downloads.size()==0 && partiscaricate!=parti){
				//gestire errore di nessun client raggiungibile con risorsa
				gui.addLog("Nessun client da cui scaricare la risorsa");
			}
			else{
				//download effettutato correttamente
				try {
					risorse.add(new Risorsa(nome,parti));
				//	gui.completedownload(name,nome,parti);
					gui.addLog("Risorsa scaricata correttamente");
				} catch (RemoteException e) {
					//errore nella creazione della risorsa
					e.printStackTrace();
				}
			}
		}
	}
	public void eseguiDownload(){
		
	}
	//metodo per il download di una parte di risorsa da un client
	class ScaricaRisorsa extends Thread{
		//dati della risorsa da cercare
		boolean scaricata=false;
		RClient client;
		Download padre;
		ScaricaRisorsa(RClient c, Download father){
			client=c;
			padre=father;
		}
		public void run(){
			try {
				System.out.println("il client"+name+"cerca la risorsa da"+client.getname());
				client.upload();//invocato sul client da cui voglio scaricare
				System.out.println("RISORSA RICEVUTA");
				padre.partiscaricate=padre.partiscaricate+1;//non arriva qui se ci sono problemi
				System.out.println("MAGIA 1");
				padre.downloads.add(new ScaricaRisorsa(client,padre));
				System.out.println("MAGIA 2 con downloads size" +padre.downloads.size());
				padre.downloadsessione=padre.downloadsessione-1;
				System.out.println("MAGIA 3");
			} catch (RemoteException e) {
				// GESTISCO L' eccezione di fallito download
				e.printStackTrace();
			}//al termine di tutto che sia andato bene o male il download è finito
			downloadattivi=downloadattivi-1;
		}
		public boolean getdone(){
			return scaricata;
		}
	}
	
	public int upload()throws RemoteException{
		try {
			System.out.println("il client sta dando la risorsa"+name);
			Thread.sleep(sleep);//simulo il download
			return 1;//tutto ok
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 2;//errore nel download
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
		System.out.println("IL client "+name+" controlla se ha la risorsa "+n+p);
		for(int i=0;!found && i<risorse.size();i++){
			System.out.println(i);
			if(risorse.get(i).getnome().equals(n)){
				if(risorse.get(i).getparti()==p){
				found=true;
			}}
		}
		if(found){
			System.out.println("IL client "+name+"ha la risorsa ");
			return this;
		}
		else{ 
			System.out.println("IL client "+name+" non ha la risorsa ");
			return null;
		}
	}
	public void disconnect() {
        if (rs != null) {
            try {
                rs.disconnettiClient(this);
            } 
            catch (RemoteException e) { 
            	gui.addLog("Errore di connessione");
            }
            //interrompo il thred che controlla se il server è attivo
        }
    }
	
	//quando si disconnette, il server richiama questo metodo
	public void disconnectServer() throws RemoteException {
		rs=null;
		gui.addLog("Server Disconnesso");
		
	}
}
