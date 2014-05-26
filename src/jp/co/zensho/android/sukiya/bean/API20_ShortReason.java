package jp.co.zensho.android.sukiya.bean;
/**
 * 
 * @author ntdat Class: API20_LossReason_Obj
 * 
 *         Define short reason object get from API 20
 * 
 *         Show object value in the list when click EditText
 *         s205_et_short_reason from screen 205
 * 
 */
public class API20_ShortReason {
	private String reasonCD;
	private String shortReason;

	public String getReasonCD() {
		return reasonCD;
	}

	public void setReasonCD(String reasonCD) {
		this.reasonCD = reasonCD;
	}

	public String getShortReason() {
		return shortReason;
	}

	public void setShortReason(String shortReason) {
		this.shortReason = shortReason;
	}

}
