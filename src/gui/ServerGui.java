package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	private JPanel toppanel=new JPanel();
	private JTextArea rightarea = new JTextArea(5,20);
	private JList leftarea = new JList();
	private JList botarea = new JList();
	
	public ServerGui(Server a){                   //costruttore
		s=a;
		class WindowEventHandler extends WindowAdapter {
			  public void windowClosing(WindowEvent evt) {
				  //try {
					s.uscita();//metodo da fare su server
				//} catch (RemoteException e) {}
				  dispose();
				  System.exit(0);
			  }
		}
		//creo i vari pannelli
		JScrollPane scrollbotPanel = new JScrollPane(botarea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		botpanel.setLayout( new GridLayout(1,1) );
		botpanel.setBorder(BorderFactory.createTitledBorder("Log"));
		botpanel.add(scrollbotPanel);
		
		FlowLayout gl2 = new FlowLayout(FlowLayout.CENTER);
		toppanel.setLayout(gl2);
		
		JScrollPane scrollrightPanel = new JScrollPane(rightarea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		rightpanel.setLayout( new GridLayout(1,1) );
		rightpanel.setBorder(BorderFactory.createTitledBorder("Server Connessi"));
		rightpanel.add(scrollrightPanel);
		
	
		JScrollPane scrollleftPanel = new JScrollPane(leftarea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		leftpanel.setLayout( new GridLayout(1,1) );
		leftpanel.setBorder(BorderFactory.createTitledBorder("Client Connessi"));
		leftpanel.add(scrollleftPanel);
		toppanel.add(leftpanel);
		toppanel.add(rightpanel);
		setLayout(new BorderLayout());
		add(toppanel,  BorderLayout.NORTH);
		add(botpanel ,  BorderLayout.SOUTH );
		setTitle( "Server "+s.getname());
		setSize( 500,600 );
		addWindowListener(new WindowEventHandler());
	    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setVisible(true);
		System.out.println("CI SONO ARRIVATO");
		
	}
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}
