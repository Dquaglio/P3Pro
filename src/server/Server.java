package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
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
		System.out.println("Server creato spero"+name);
	}
	public String getname(){
		return name;
	}
	
	public void uscita() throws RemoteException, MalformedURLException, NotBoundException{
		String[] a=(Naming.list("rmi://localhost/"));
		System.out.println("ciao gay"+a[0]+" ");
		//Naming.unbind("//localhost:1099/"+name);
	}
}
