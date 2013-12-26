package server;

import java.rmi.RemoteException;
import java.util.Vector;

import client.Client;
import server.VistaModel;

public class Server extends java.rmi.server.UnicastRemoteObject implements RServer{
	private String name;
	private Vector<Server> listaserver;
	private Vector<Client> listaclient;
	private final VistaModel model = new VistaModel();
	public Server(String n)throws RemoteException{
		name=n;
		System.out.println("Server creato"+name);
	}
	public String getname(){
		return name;
	}
	
	public void uscita(){}
}
