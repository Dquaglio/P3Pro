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
		System.out.println("Server creato"+name);
	}
	public String getname(){
		return name;
	}
	
	public void uscita() throws RemoteException, MalformedURLException, NotBoundException{
		String a=(Naming.list("rmi://localhost/"))[0];
		System.out.println("ciao gay dio"+a);
		Naming.unbind("rmi://localhost/"+name);
	}
}
