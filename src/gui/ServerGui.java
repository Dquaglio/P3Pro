package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import server.RServer;
import server.Server;

public class ServerGui extends JFrame{
	private Server  s;//puntatore al server
	//parti grafiche
	private JPanel rightpanel=new JPanel();//server connessi
	private JPanel leftpanel=new JPanel();//client connessi
	private JPanel botpanel=new JPanel();//Log
	private JList<String> serverArea = new JList<String>();
	private JList<String> clientArea = new JList<String>();
	private JTextArea Log = new JTextArea(8,20);
	DefaultListModel<String> modelServer = new DefaultListModel<String>();
	DefaultListModel<String> modelClient = new DefaultListModel<String>();
	
	class WindowEventHandler extends WindowAdapter {
		  public void windowClosing(WindowEvent evt) {
				try {
					s.uscita();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
	}
	public ServerGui(Server a){            //costruttore
		s=a;
		addWindowListener(new WindowEventHandler());
		//creo i vari pannelli
		JScrollPane scrollbotPanel = new JScrollPane(Log, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		botpanel.setLayout( new GridLayout(1,1) );
		botpanel.setBorder(BorderFactory.createTitledBorder("Log"));
		botpanel.add(scrollbotPanel);
		
		
		JScrollPane scrollrightPanel = new JScrollPane(serverArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		serverArea.setModel(modelServer);
		rightpanel.setLayout( new GridLayout(1,1) );
		rightpanel.setBorder(BorderFactory.createTitledBorder("Server Connessi"));
		rightpanel.add(scrollrightPanel);
	
		JScrollPane scrollleftPanel = new JScrollPane(clientArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		clientArea.setModel(modelClient);
		leftpanel.setLayout( new GridLayout(1,1) );
		leftpanel.setBorder(BorderFactory.createTitledBorder("Client Connessi"));
		leftpanel.add(scrollleftPanel);
		
		setLayout(new BorderLayout());
		add(rightpanel,  BorderLayout.EAST);
		add(leftpanel,  BorderLayout.WEST);
		add(botpanel ,  BorderLayout.SOUTH );
		setTitle( "Server "+s.getname());
		setSize( 600,700 );
		addWindowListener(new WindowEventHandler());
	    //setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	public void addLog(String s){
		Log.append(s+"\n");
	}
	public void setServerList(String[] ServerList){
		modelServer=new DefaultListModel<String>();
		for(int i=0;i<ServerList.length;i++){
			modelServer.addElement(ServerList[i]);
		}
		serverArea.setModel(modelServer);
	}
}
