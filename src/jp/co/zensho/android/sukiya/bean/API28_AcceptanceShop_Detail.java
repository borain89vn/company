package jp.co.zensho.android.sukiya.bean;

/**
 * 
 * @author ntdat
 * 
 *         Date: 03/19/2014
 * 
 *         Class: API28_AcceptanceShop_Detail_Obj
 * 
 *         Define shop object get from api 28 and display into listview
 *         lv_right_acceptance_content when click lv_left_acceptance_resource
 *         item in screen S211
 * 
 */
public class API28_AcceptanceShop_Detail {
	private String checkedTenpoCD;
	private String checkedDate;
	private String hinCD;
	private String hinName;
	private String preRCVSum;
	private String taniCD;
	private String taniName;

	public String getCheckedTenpoCD() {
		return checkedTenpoCD;
	}

	public void setCheckedTenpoCD(String checkedTenpoCD) {
		this.checkedTenpoCD = checkedTenpoCD;
	}

	public String getCheckedDate() {
		return checkedDate;
	}

	public void setCheckedDate(String checkedDate) {
		this.checkedDate = checkedDate;
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

	public String getPreRCVSum() {
		return preRCVSum;
	}

	public void setPreRCVSum(String preRCVSum) {
		this.preRCVSum = preRCVSum;
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
