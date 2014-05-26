package jp.co.zensho.android.sukiya.bean;

import java.util.ArrayList;

/**
 * This class is use for storing list api037->api040
 * 
 * @author ltthuc
 *
 */
public class APIModel {
	
private ArrayList<API037_NumDays> api037;
private ArrayList<API038_Unmatched> api038;
private ArrayList<API039_LossAmount> api039;
private ArrayList<API040_MoveLoad> api040;

public ArrayList<API037_NumDays> getApi037() {
	return api037;
}
public void setApi037(ArrayList<API037_NumDays> api037) {
	this.api037 = api037;
}
public ArrayList<API038_Unmatched> getApi038() {
	return api038;
}
public void setApi038(ArrayList<API038_Unmatched> api038) {
	this.api038 = api038;
}
public ArrayList<API039_LossAmount> getApi039() {
	return api039;
}
public void setApi039(ArrayList<API039_LossAmount> api039) {
	this.api039 = api039;
}
public ArrayList<API040_MoveLoad> getApi040() {
	return api040;
}
public void setApi040(ArrayList<API040_MoveLoad> api040) {
	this.api040 = api040;
}


}
