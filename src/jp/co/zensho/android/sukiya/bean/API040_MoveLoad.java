package jp.co.zensho.android.sukiya.bean;

import java.util.List;
public class API040_MoveLoad  extends APIBaseModel{
private int total_count;
private List<Tenpo> tenpo;
public List<Tenpo> getTenpo() {
	return tenpo;
}
public void setTenpo(List<Tenpo> tenpo) {
	this.tenpo = tenpo;
}
public int getTotal_count() {
	return total_count;
}
public void setTotal_count(int total_count) {
	this.total_count = total_count;
}


}
