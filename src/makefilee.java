import server.ServerStarter;


public class makefilee {
	public static void main(String[] args) throws Exception {
		String[] p={"Razorback1"};
		String[] p2={"Razorback2"};
		ServerStarter s1=new ServerStarter();
		ServerStarter s2=new ServerStarter();
		s1.main(p);
		s2.main(p2);
	}
}
