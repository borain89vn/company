package jp.co.zensho.android.sukiya.bean;

/**
 * 
 * @author ntdat
 * 
 *         Class: API18_ViewHistory_Obj
 * 
 *         Define loss history object in loss report history screen
 * 
 *         Show object value in list of loss report history screen
 * 
 */
public class API18_ViewHistory {
	private String submitDate;
	private String hinName;
	private String lossReason;
	private String lossSum;
	private String taniName;

	public String getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(String submitDate) {
		this.submitDate = submitDate;
	}

	public String getHinName() {
		return hinName;
	}

	public void setHinName(String hinName) {
		this.hinName = hinName;
	}

	public String getLossReason() {
		return lossReason;
	}

	public void setLossReason(String lossReason) {
		this.lossReason = lossReason;
	}

	public String getLossSum() {
		return lossSum;
	}

	public void setLossSum(String lossSum) {
		this.lossSum = lossSum;
	}

	public String getTaniName() {
		return taniName;
	}

	public void setTaniName(String taniName) {
		this.taniName = taniName;
	}
}
