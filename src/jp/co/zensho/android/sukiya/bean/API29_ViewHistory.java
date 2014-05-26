package jp.co.zensho.android.sukiya.bean;

/**
 * 
 * @author ntdat
 * 
 *         Class: API29_ViewHistory_Obj
 * 
 *         Define shipping history object in acceptance report history screen
 * 
 *         Show object value in list of acceptance report history screen
 * 
 */
public class API29_ViewHistory {
	private String dataDateTime;
	private String tenpoCD;
	private String tenpoName;
	private String hinName;
	private String rcvSum;

	public String getDataDateTime() {
		return dataDateTime;
	}

	public void setDataDateTime(String dataDateTime) {
		this.dataDateTime = dataDateTime;
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

	public String getRcvSum() {
		return rcvSum;
	}

	public void setRcvSum(String rcvSum) {
		this.rcvSum = rcvSum;
	}

	public String getTaniName() {
		return taniName;
	}

	public void setTaniName(String taniName) {
		this.taniName = taniName;
	}

	private String taniName;
}
