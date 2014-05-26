package jp.co.zensho.android.sukiya.bean;
/**
 * 
 * @author ntdat Class: API15_ProducNameSel_Obj
 * 
 *         Define product name object get from API 15
 * 
 *         Show object value in the list when click EditText
 *         s201_et_product_name_sel from screen 201
 * 
 */
public class API15_ProductNameSel {
	private String hinCD;
	private String hinName;

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

}
