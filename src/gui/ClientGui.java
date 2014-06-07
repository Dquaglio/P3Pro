package gui;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import risorsa.Risorsa;

import client.Client;

public class ClientGui extends JFrame{
	private Client c;
	DefaultListModel<String> modelrisorsa = new DefaultListModel<String>();
	DefaultListModel<String> modelcoda = new DefaultListModel<String>();
	private JTextField ricerca=new JTextField(5);
	private JButton searchbutton=new JButton("Cerca");
	private JButton disconnect=new  JButton();
	private JList<String> fcompleti=new JList<String>();
	private JList<String> cdownload=new JList<String>();
	private Vector<oggettolista> listacdownload=new Vector<oggettolista>();
	private JTextArea Log=new JTextArea(8,20);
	private JPanel toppanel=new JPanel();
	private JPanel top=new JPanel();
	private JPanel center=new JPanel();
	private JPanel centerleft=new JPanel();
	private JPanel centerright=new JPanel();
	private JPanel bot=new JPanel();
	private Object Lock=new Object();
	/*Classe oggettolista*/
	public class oggettolista{
		String nomeclient;
		String nomerisorsa;
		int partirisorsa;
		int status;
		oggettolista(String nc,String nr,int p, Integer stat){
			nomeclient=nc;
			nomerisorsa=nr;
			partirisorsa=p;
			status=stat;
		}
		public boolean uguali(oggettolista obj){
			if(this.nomeclient.equals(obj.nomeclient) && this.nomerisorsa.equals(obj.nomerisorsa) && this.partirisorsa==obj.partirisorsa){
				return true;
			}
			else return false;
		}
		public void changestatus(int stat){//0 in corso 1 finito 2 fallito
			status=stat;
		}
		public String getstatus(){
			String state="non pervenuto";
			if(status==0){
				state="in download";
			}
			if(status==1){
				state="finito";
			}
			if(status==2){
				state="fallito";
			}
			return "Client "+nomeclient+"risorsa "+nomerisorsa+partirisorsa+"status "+state;
		}
		public String getAll(){
			return nomeclient+" "+nomerisorsa+" "+partirisorsa+" "+status;
		}
	}
	public synchronized int getListSize(){
		return listacdownload.size();
	}
	public ClientGui(Client c,Vector<Risorsa>risorse) throws RemoteException{
		this.c=c;
		//toppanel
		CreateTopPanel();
		//centerpanel
		CreateCenterPanel();
		//botpanel
		CreateBotPanel();
		setLayout(new BorderLayout());
		add(top,  BorderLayout.NORTH);
		add(center,  BorderLayout.CENTER);
		add(bot ,  BorderLayout.SOUTH);
		setTitle(c.getname());
		setSize( 600,700 );
		setVisible(true);
		setRisorse(risorse);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void RemovClientRef(){
		c=null;
	}
	public void CreateBotPanel(){
		bot.setLayout(new GridLayout(1,1));
		bot.setBorder(BorderFactory.createTitledBorder("Log"));
		bot.add(Log);
	}
	public void CreateTopPanel(){
		toppanel.setLayout(new GridLayout(1,2));
		toppanel.setBorder(BorderFactory.createTitledBorder("Cerca File"));
		toppanel.add(ricerca);
		toppanel.add(searchbutton);
		top.setLayout(new GridLayout(1,2));
		top.add(toppanel);
		top.add(disconnect);
        searchbutton.addActionListener(new ActionListener() {//gestione dell avvio download
            public void actionPerformed(ActionEvent e) {
                String s = ricerca.getText();
                String [] a = s.split("\\s+");
                if (a.length < 2) {
                    addLog("Dati inseriti non corretti");
                    return;
                }
                else{
                    try {
						c.eseguiDownload(a[0],Integer.parseInt(a[1]));
					} catch (NumberFormatException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                }
            }
        });
	}
	public void CreateCenterPanel(){
		centerleft.setLayout(new GridLayout(1,1));
		centerleft.setBorder(BorderFactory.createTitledBorder("File Completi"));
		centerright.setLayout(new GridLayout());
		centerright.setBorder(BorderFactory.createTitledBorder("Coda Download"));
		cdownload.setModel(modelcoda);
		JScrollPane scrollrightPanel = new JScrollPane(cdownload, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane scrollleftPanel = new JScrollPane(fcompleti, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		centerleft.add(scrollleftPanel);
		centerright.add(scrollrightPanel);
		center.setLayout(new GridLayout(1,2));
		center.add(centerleft);
		center.add(centerright);
	}
	
	public void addLog(String s){
		Log.append(s+"\n");
	}
	
	public void setRisorse(Vector<Risorsa> l) throws RemoteException {
		for (int i=0; i<l.size(); i++) { 
			modelrisorsa.addElement(l.get(i).getnome()+" "+l.get(i).getparti()); 
	    }
		fcompleti.setModel(modelrisorsa);
	}
	public void addRisorsa(Risorsa r) throws RemoteException{
		modelrisorsa.addElement(r.getnome()+" "+r.getparti());
		}
	/*Metodi per gestire la lista dei download*/
	public void aggiornalista(){
		synchronized(Lock){
			modelcoda=new DefaultListModel<String>();
			for(int j=0;j<listacdownload.size();j++){
				modelcoda.addElement(listacdownload.get(j).getstatus());
			}
			cdownload.setModel(modelcoda);
		}
	}
	
	public void addDownElement(String nc,String nr,int p) {
		synchronized(Lock){
		int status=0;//status=0 significa in download
		listacdownload.add(new oggettolista(nc,nr,p,status));
		aggiornalista();
		}
		
	}
	
	public void completedownload(int index) {
		synchronized(Lock){
		listacdownload.elementAt(index).changestatus(1);
		aggiornalista();
		}
	}
	
	public void faildownload(int index) {
		synchronized(Lock){
		listacdownload.elementAt(index).changestatus(2);
		aggiornalista();
		}
	}

}
