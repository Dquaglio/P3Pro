package gui;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	DefaultListModel<Risorsa> model = new DefaultListModel<Risorsa>();
	DefaultListModel<String> modelcoda = new DefaultListModel<String>();
	private JTextField ricerca=new JTextField(5);
	private JButton searchbutton=new JButton("Cerca");
	private JButton disconnect=new  JButton();
	private JList<Risorsa> fcompleti=new JList<Risorsa>();
	private JList<String> cdownload=new JList<String>();
	private Vector<oggettolista> listacdownload=null;
	private JTextArea Log=new JTextArea(8,20);
	private JPanel toppanel=new JPanel();
	private JPanel top=new JPanel();
	private JPanel center=new JPanel();
	private JPanel centerleft=new JPanel();
	private JPanel centerright=new JPanel();
	private JPanel bot=new JPanel();
	private class oggettolista{
		String nomeclient;
		String nomerisorsa;
		int partirisorsa;
		Integer status;
		oggettolista(String nc,String nr,int p, Integer stat){
			nomeclient=nc;
			nomerisorsa=nr;
			partirisorsa=p;
			status=stat;
		}
		public void changestatus(int stat){//0 in corso 1 finito 2 fallito
			status=stat;
		}
		public String getstatus(){
			String state;
			if(status==0){
				state="in download";
			}
			if(status==1)
				state="finito";
			else
				state="fallito";
			return "Client "+nomeclient+"risorsa "+nomerisorsa+partirisorsa+"status "+state;
		}
	}
	public ClientGui(Client c){
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
	}
	class WindowEventHandler extends WindowAdapter {
        public void windowClosing(WindowEvent evt) {
            System.out.println("Window closed");
            c.disconnect();
            
        }
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
                    c.eseguiDownload(a[0],Integer.parseInt(a[1]));
                    addLog("Preparazione al download...");
                }
            }
        });
	}
	public void CreateCenterPanel(){
		centerleft.setLayout(new GridLayout(1,1));
		centerleft.setBorder(BorderFactory.createTitledBorder("File Completi"));
		centerright.setLayout(new GridLayout());
		centerright.setBorder(BorderFactory.createTitledBorder("Coda Download"));
        modelcoda.addElement("PROVA");
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
	
	public void setrisorse(Vector<Risorsa> l) {
		for (int i=0; i<l.size(); i++) { 
			model.addElement(l.get(i)); 
	    }
		fcompleti.setModel(model);
	}
	public void addDownElement(String nc,String nr,int p) {
		Integer status=0;
		listacdownload.add(new oggettolista(nc,nr,p,status));
		modelcoda.addElement(listacdownload.lastElement().getstatus());
        cdownload.setModel(modelcoda);//aggiorno la lista dei download
		
	}
	public void completedownload(String name, String nome, int parti) {
		boolean found=false;
		int i=0;
		while(!found){
			if(listacdownload.elementAt(i).equals(new oggettolista(name,nome,parti,0))){
				listacdownload.get(i).changestatus(1);
				found=true;
			}
			i=i+1;
		}
		modelcoda.clear();
		for(int j=0;j<listacdownload.size();j++){
			modelcoda.addElement(listacdownload.get(j).getstatus());
		}
		cdownload.setModel(modelcoda);
	}
	public void faildownload(String name, String nome, int parti) {
		boolean found=false;
		int i=0;
		while(!found){
			if(listacdownload.elementAt(i).equals(new oggettolista(name,nome,parti,0))){
				listacdownload.get(i).changestatus(2);
				found=true;
			}
			i=i+1;
		}
		modelcoda.clear();
		for(int j=0;j<listacdownload.size();j++){
			modelcoda.addElement(listacdownload.get(j).getstatus());
		}
		cdownload.setModel(modelcoda);
	}

}
