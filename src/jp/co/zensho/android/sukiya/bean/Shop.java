package jp.co.zensho.android.sukiya.bean;

/**
 * 
 * @author ntdat
 * 
 *         Date: 03/19/2014
 * 
 *         Class: Shop_Obj
 * 
 *         Define shop object include shop code (tenpo code) and shop name
 * 
 */
public class Shop {
	private String tenpoCD;
	private String tenpoName;

	public String getTenpoCD() {
		return tenpoCD;
	}

	public void setTenpoCD(String tenpoCD) {
		this.tenpoCD = tenpoCD;
	}

	public String getTenpoName() {
		return tenpoName;
	}

	public void setTenpoName(String tenpoName) {
		this.tenpoName = tenpoName;
	}

}
