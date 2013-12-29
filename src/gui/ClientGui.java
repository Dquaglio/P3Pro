package gui;


import gui.ServerGui.WindowEventHandler;

import java.awt.BorderLayout;
import java.awt.GridLayout;
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
import javax.swing.WindowConstants;

import client.Client;
import client.Risorsa;

public class ClientGui extends JFrame{
	private Client c;
	DefaultListModel<Risorsa> model = new DefaultListModel<Risorsa>();
	private JTextField ricerca=new JTextField();
	private JButton go=new JButton();
	private JButton disconnect=new  JButton();
	private JList<Risorsa> fcompleti=new JList<Risorsa>();
	private JList<String> cdownload=new JList<String>();
	private JTextArea Log=new JTextArea(8,20);
	private JPanel toppanel=new JPanel();
	private JPanel top=new JPanel();
	private JPanel center=new JPanel();
	private JPanel centerleft=new JPanel();
	private JPanel centerright=new JPanel();
	private JPanel bot=new JPanel();
	public ClientGui(Client c){
		this.c=c;
		//toppanel
		toppanel.setLayout(new GridLayout(1,2));
		toppanel.setBorder(BorderFactory.createTitledBorder("Cerca File"));
		toppanel.add(ricerca);
		toppanel.add(go);
		top.setLayout(new GridLayout(1,2));
		top.add(toppanel);
		top.add(disconnect);
		//centerpanel
		centerleft.setLayout(new GridLayout(1,1));
		centerleft.setBorder(BorderFactory.createTitledBorder("File Completi"));
		centerright.setLayout(new GridLayout());
		centerright.setBorder(BorderFactory.createTitledBorder("Coda Download"));
		JScrollPane scrollrightPanel = new JScrollPane(cdownload, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane scrollleftPanel = new JScrollPane(fcompleti, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		centerleft.add(scrollleftPanel);
		centerright.add(scrollrightPanel);
			
		center.setLayout(new GridLayout(1,2));
		center.add(centerleft);
		center.add(centerright);		
		
		bot.setLayout(new GridLayout(1,1));
		bot.setBorder(BorderFactory.createTitledBorder("Log"));
		bot.add(Log);
		setLayout(new BorderLayout());
		add(top,  BorderLayout.NORTH);
		add(center,  BorderLayout.CENTER);
		add(bot ,  BorderLayout.SOUTH);
		setTitle( c.getname());
		setSize( 600,700 );
		setVisible(true);
		System.out.println("Gui client creata");	
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
}
