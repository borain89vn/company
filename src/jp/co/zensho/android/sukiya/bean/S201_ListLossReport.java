package jp.co.zensho.android.sukiya.bean;

/**
 * 
 * @author ntdat Class: S201_ListLossReport_Obj
 * 
 *         Define the object item of listview in screen 201
 * 
 *         Add object value into ListView lv_loss_report when click button
 *         s201_add_to_list.
 * 
 */
public class S201_ListLossReport {
	private String DivCD;
	private String hinCD;
	private String hinName;
	private String lossReasonCD;
	private String lossReason;
	private String lossSum;
	private String taniCD;
	private String taniName;

	public String getDivCD() {
		return DivCD;
	}

	public void setDivCD(String divCD) {
		DivCD = divCD;
	}

	public String getHinCD() {
		return hinCD;
	}

	public void setHinCD(String hinCD) {
		this.hinCD = hinCD;
	}

	public String getHinName() {
		return hinName;
	}

	public void setHinName(String hinName) {
		this.hinName = hinName;
	}

	public String getLossReasonCD() {
		return lossReasonCD;
	}

	public void setLossReasonCD(String lossReasonCD) {
		this.lossReasonCD = lossReasonCD;
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

	public String getTaniCD() {
		return taniCD;
	}

	public void setTaniCD(String taniCD) {
		this.taniCD = taniCD;
	}

	public String getTaniName() {
		return taniName;
	}

	public void setTaniName(String taniName) {
		this.taniName = taniName;
	}

}
