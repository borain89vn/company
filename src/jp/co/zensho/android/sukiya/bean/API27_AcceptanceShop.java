package jp.co.zensho.android.sukiya.bean;

/**
 * 
 * @author ntdat
 * 
 *         Date: 03/19/2014
 * 
 *         Class: API27_AcceptanceShop_Obj
 * 
 *         Define shop object get from api 27 and display into listview
 *         lv_left_acceptance_resource in screen S211
 * 
 */
public class API27_AcceptanceShop {
	private String tenpoCD;
	private String tenpoName;
	private String sendDateTime;

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

	public String getSendDateTime() {
		return sendDateTime;
	}

	public void setSendDateTime(String sendDateTime) {
		this.sendDateTime = sendDateTime;
	}
}
