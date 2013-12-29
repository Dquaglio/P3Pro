package client;

public class Risorsa {
	String name;
	int parts;
	public Risorsa(String n,String p){
		name=n;
		parts=Integer.parseInt(p);
	}
	public String getnome(){
		return name;
	}
	public int getparti(){
		return parts;
	}
}
