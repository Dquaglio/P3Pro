package client;

import gui.ClientGui;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;

import risorsa.Risorsa;
import server.RServer;
import server.Server;

public class Client extends java.rmi.server.UnicastRemoteObject implements RClient {
	private static final String HOST = "localhost";
	private int download; //capacità di download del client
	private String name;   //nome client
	private RServer rs=null;    // riferimento al server a cui è connesso
	private Vector<Risorsa> risorse; //vettore di risorse di cui dispone il client
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
	//thread addetto al download 
	class Download extends Thread{
		//CAMPI DATI
		String nome;
		int parti;
		Vector<RServer> ServerRisorsa=null;
		Download(String n,int p){
			nome=n;
			parti=p;
		}
		public void run(){
			try {
				ServerRisorsa=rs.cercarisorsa(nome,parti);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//metodo per il download di una risorsa
	class ScaricaRisorsa extends Thread{
		//dati della risorsa da cercare
		String nome;
		int parti;
		boolean scaricata=false;
		
		ScaricaRisorsa(String n,int p){
			nome=n;
			parti=p;
		}
		public void run(){
			
				
			
			while(scaricata==false){//continua a cercare di scaricarla fino a che non hai la risorsa
				
			}
		}
		
	}
	//METODI VARI GET
	public String getname() {
		return name;
	}
}
