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

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import server.Server;

public class ServerGui extends JFrame implements Observer {
	private Server  s;//puntatore al server
	//parti grafiche
	private JPanel rightpanel=new JPanel();//server connessi
	private JPanel leftpanel=new JPanel();//client connessi
	private JPanel botpanel=new JPanel();//Log
	private JList<String> rightarea = new JList<String>();
	private JList<String> leftarea = new JList<String>();
	private JTextArea botarea = new JTextArea(8,20);
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
	public ServerGui(Server a){                   //costruttore
		s=a;
		addWindowListener(new WindowEventHandler());
		//creo i vari pannelli
		JScrollPane scrollbotPanel = new JScrollPane(botarea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		botpanel.setLayout( new GridLayout(1,1) );
		botpanel.setBorder(BorderFactory.createTitledBorder("Log"));
		botpanel.add(scrollbotPanel);
		
		
		JScrollPane scrollrightPanel = new JScrollPane(rightarea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		rightpanel.setLayout( new GridLayout(1,1) );
		rightpanel.setBorder(BorderFactory.createTitledBorder("Server Connessi"));
		rightpanel.add(scrollrightPanel);
		
	
		JScrollPane scrollleftPanel = new JScrollPane(leftarea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
	    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setVisible(true);
		System.out.println("CI SONO ARRIVATO");
		
	}
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}
