package xin.dspwljsyxgs.www.whoere.Util;

public class Getinfo {
	private String account,headicon,password,phonenum;
	private int id,kind;
	private double x,y;
	public Getinfo() {
		
	}
	public void setId(int id) {
		this.id=id;
	}
	public int getId() {
		return this.id;
	}
	public void setKind(int kind){this.kind=kind;}
	public int getKind() { return kind; }
	public void setAccount(String account) {
		this.account=account;
	}
	public String getAccount() { return this.account; }
	public void setHeadicon(String headicon) {
		this.headicon=headicon;
	}
	public String getHeadicon() {
		return this.headicon;
	}
	public void setPassword(String password) {
		this.password=password;
	}
	public String getPassword() {
		return this.password;
	}
    public void setPhonenum(String phonenum){
	    this.phonenum=phonenum;
    }
    public String getPhonenum() {
        return phonenum;
    }
    public  void setX(double x){this.x = x;}
    public double getX(){return this.x;}
    public void setY(double y){this.y=y;}
    public double getY(){return this.y;}

}