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
	private int sleep=2000;//tempo di upload
	private int download; //capacità di download del client
	private boolean downloading=false;
	private String name;   //nome client
	private RServer rs=null;    // riferimento al server a cui è connesso
	private Vector<Risorsa> risorse; //vettore di risorse di cui dispone il client
	private ClientGui gui;
	
	//costruttore del client
	public Client(String n,String s,String d,Vector<Risorsa>r) throws RemoteException{// n= nome ; s=  nome server ; d capacità di download; r vettore di risorse
		download=Integer.parseInt(d);//converti d in int
		name=n;
		risorse=r;
		rs=connetti(s);
		gui=new ClientGui(this);
		gui.addWindowListener(new WindowAdapter(){	
			public void windowClosing(WindowEvent evt) {
				System.out.println("Window closed");
				disconnect();
				gui.setVisible(false);
				gui.dispose();		
			}
		});
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
		if(!downloading){
			Download d=new Download(n,p,this);
			d.start();
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
		Integer downloadsessione=0;
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
				gui.addLog("Errore nel download della lista di client con la risorsa cercata");
				return;
			}
			//ClientRisorsa.remove(c);
			System.out.println("dimensione client con risorsa"+ClientRisorsa.size());
			//ho il vettore di client con la risorsa
			for(int j=0;j<ClientRisorsa.size();j++){//mi creo il vector 
				downloads.add(new ScaricaRisorsa(ClientRisorsa.get(j),this));
			}
			if(downloads!=null){
				while(partiscaricate!=parti){//finchè non ho scaricato tutte le parti o non ho nessuno da cui scaricare
					synchronized(downloadsessione){//lock su downloadattivi in modo che il client non possa scaricare piu della capacità massima contemporaneamente
						int downloadpossibili=download-downloadsessione;//NB avendo il lock su download attivi e download non può cambiare valore so di fare un operazione sicura
						for(int i=0;i<downloadpossibili && downloads.size()!=0;i++){
								System.out.println("la dimensione di Download è" +downloads.size());
								downloads.get(0).start();
								downloadsessione=downloadsessione+1;
								System.out.println(name+nome+parti);
								gui.addDownElement(name,nome,parti);
								downloads.remove(0);//rimuovo la possibilità di scaricare da questo client in quanto un download è appena stato avviato
								System.out.println("i client rimasti da cui posso scaricare sono"+downloads.size());
						}
					}
				}
			}
			if(downloads.size()==0 && partiscaricate!=parti){
				//gestire errore di nessun client raggiungibile con risorsa
				System.out.println("NON HAI SCARICATO LA RISORSA :(");
				gui.addLog("Download della risorsa impossibile da portare a termine");
			}
			else{
				//download effettutato correttamente
				try {
					risorse.add(new Risorsa(nome,parti));
					System.out.println("HAI SCARICATO TUTTA LA RISORSA");
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
				System.out.println("il client "+name+" cerca la risorsa da"+client.getname());
				client.upload();//invocato sul client da cui voglio scaricare
				System.out.println("RISORSA RICEVUTA da "+client.getname());
				padre.partiscaricate=padre.partiscaricate+1;//non arriva qui se ci sono problemi
				padre.downloads.add(new ScaricaRisorsa(client,padre));//il download da client è finito correttamente, posso se serve riscaricare da lui ora
			} catch (RemoteException e) {
				// GESTISCO L' eccezione di fallito download
				e.printStackTrace();
			}//al termine di tutto che sia andato bene o male il download è finito
			padre.downloadsessione=padre.downloadsessione-1;
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
		System.out.println("disconnetto il client da parte client");
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
