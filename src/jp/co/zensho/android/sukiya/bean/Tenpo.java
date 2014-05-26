package jp.co.zensho.android.sukiya.bean;
public class Tenpo {
	private String tenpo_nm;
	private int count;
	private int size;
	public Tenpo(String tenpo_nm,int count) {
		this.tenpo_nm = tenpo_nm;
		this.count = count;
		
	}
	public String getTenpo_nm() {
		return tenpo_nm;
	}
	public void setTenpo_nm(String tenpo_nm) {
		this.tenpo_nm = tenpo_nm;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
}