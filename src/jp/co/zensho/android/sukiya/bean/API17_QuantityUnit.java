package jp.co.zensho.android.sukiya.bean;

/**
 * 
 * @author ntdat
 * 
 *         Class: API17_QuantityUnit_Obj
 * 
 *         Define unit object get from API17
 * 
 *         Set unit value (Ex: g, kg, ...) into EditText p2xx_quantity_et_unit
 *         in pop-up Quantity Input (s201)
 * 
 */
public class API17_QuantityUnit {
	private String taniCD;
	private String taniName;

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
