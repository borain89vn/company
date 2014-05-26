package jp.co.zensho.android.sukiya.bean;

/**
 * 
 * @author ntdat
 * 
 *         Class: API25_ViewHistory_Obj
 * 
 *         Define shipping history object in shipping report (export) history
 *         screen
 * 
 *         Show object value in list of shipping report history screen
 * 
 */
public class API25_ViewHistory {
	private String sendDateTime;
	private String tenpoCD;
	private String tenpoName;
	private String hinName;
	private String sendSum;
	private String taniName;

	public String getSendDateTime() {
		return sendDateTime;
	}

	public void setSendDateTime(String sendDateTime) {
		this.sendDateTime = sendDateTime;
	}

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

	public String getHinName() {
		return hinName;
	}

	public void setHinName(String hinName) {
		this.hinName = hinName;
	}

	public String getSendSum() {
		return sendSum;
	}

	public void setSendSum(String sendSum) {
		this.sendSum = sendSum;
	}

	public String getTaniName() {
		return taniName;
	}

	public void setTaniName(String taniName) {
		this.taniName = taniName;
	}

}
