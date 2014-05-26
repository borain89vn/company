package jp.co.zensho.android.sukiya.bean;
/**
 * 
 * @author ntdat Class: API16_LossReason_Obj
 * 
 *         Define loss reason object get from API 16
 * 
 *         Show object value in the list when click EditText
 *         s201_et_loss_reason from screen 201
 * 
 */
public class API16_LossReason {
	private String reasonCD;
	private String lossReason;
	private String res;

	public String getReasonCD() {
		return reasonCD;
	}

	public void setReasonCD(String reasonCD) {
		this.reasonCD = reasonCD;
	}

	public String getLossReason() {
		return lossReason;
	}

	public void setLossReason(String lossReason) {
		this.lossReason = lossReason;
	}

	public String getRes() {
		return res;
	}

	public void setRes(String res) {
		this.res = res;
	}

}
