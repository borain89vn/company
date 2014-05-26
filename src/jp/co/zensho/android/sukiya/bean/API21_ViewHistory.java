package jp.co.zensho.android.sukiya.bean;

/**
 * 
 * @author ntdat
 * 
 *         Class: API21_ViewHistory_Obj
 * 
 *         Define short history object in short report history screen
 * 
 *         Show object value in list of short report history screen
 * 
 */
public class API21_ViewHistory {
	private String submitDate;
	private String hinName;
	private String shortReason;
	private String hour;

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

	public String getShortReason() {
		return shortReason;
	}

	public void setShortReason(String shortReason) {
		this.shortReason = shortReason;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}
}
