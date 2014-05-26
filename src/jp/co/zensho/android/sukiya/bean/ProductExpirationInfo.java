package jp.co.zensho.android.sukiya.bean;

import java.util.ArrayList;
import java.util.List;

public class ProductExpirationInfo {
	private int code;
	private String name;
	private int location_code;
	private String net;
	private String expiration_text;
	private int expiration_date;
	private boolean already_input_date;
	private String unit;
	private Double per_stock;
	private Double stock;
	private Double nohin_su1;
	private Double nohin_su2;
	private Double nohin_su3;
	private String imageName;

	// added
	private String nmlTime;
	private String nohin_day1;
	private String nohin_day2;
	private String nohin_day3;
	private String date;
	private int uriYsk;
	private String hinName;
	private String taniName;
	private Double zaikosu;

	private ArrayList<ProductExpirationInfo> DUriYsk;
	private ArrayList<ProductExpirationInfo> DNohinZaiko;

	public ArrayList<ProductExpirationInfo> getDUriYsk() {
		return DUriYsk;
	}

	public void setDUriYsk(ArrayList<ProductExpirationInfo> dUriYsk) {
		DUriYsk = dUriYsk;
	}

	public ArrayList<ProductExpirationInfo> getDNohinZaiko() {
		return DNohinZaiko;
	}

	public void setDNohinZaiko(ArrayList<ProductExpirationInfo> dNohinZaiko) {
		DNohinZaiko = dNohinZaiko;
	}

	public String getNmlTime() {
		return nmlTime;
	}

	public void setNmlTime(String nmlTime) {
		this.nmlTime = nmlTime;
	}

	public String getNohin_day1() {
		return nohin_day1;
	}

	public void setNohin_day1(String nohin_day1) {
		this.nohin_day1 = nohin_day1;
	}

	public String getNohin_day2() {
		return nohin_day2;
	}

	public void setNohin_day2(String nohin_day2) {
		this.nohin_day2 = nohin_day2;
	}

	public String getNohin_day3() {
		return nohin_day3;
	}

	public void setNohin_day3(String nohin_day3) {
		this.nohin_day3 = nohin_day3;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getUriYsk() {
		return uriYsk;
	}

	public void setUriYsk(int uriYsk) {
		this.uriYsk = uriYsk;
	}

	public String getHinName() {
		return hinName;
	}

	public void setHinName(String hinName) {
		this.hinName = hinName;
	}

	public String getTaniName() {
		return taniName;
	}

	public void setTaniName(String taniName) {
		this.taniName = taniName;
	}

	public Double getZaikosu() {
		return zaikosu;
	}

	public void setZaikosu(Double zaikosu) {
		this.zaikosu = zaikosu;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public boolean isCheckToggleButton() {
		return isCheckToggleButton;
	}

	public void setCheckToggleButton(boolean isCheckToggleButton) {
		this.isCheckToggleButton = isCheckToggleButton;
	}

	// It's used in activity s301
	private boolean isCheckToggleButton;

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Double getPer_stock() {
		return per_stock;
	}

	public void setPer_stock(Double per_stock) {
		this.per_stock = per_stock;
	}

	public Double getStock() {
		return stock;
	}

	public void setStock(Double stock) {
		this.stock = stock;
	}

	public boolean getAlready_input_date() {
		return already_input_date;
	}

	public Double getNohin_su1() {
		return nohin_su1;
	}

	public void setNohin_su1(Double nohin_su1) {
		this.nohin_su1 = nohin_su1;
	}

	public Double getNohin_su2() {
		return nohin_su2;
	}

	public void setNohin_su2(Double nohin_su2) {
		this.nohin_su2 = nohin_su2;
	}

	public Double getNohin_su3() {
		return nohin_su3;
	}

	public void setNohin_su3(Double nohin_su3) {
		this.nohin_su3 = nohin_su3;
	}

	public void setAlready_input_date(boolean already_input_date) {
		this.already_input_date = already_input_date;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLocation_code() {
		return location_code;
	}

	public void setLocation_code(int location_code) {
		this.location_code = location_code;
	}

	public String getNet() {
		return net;
	}

	public void setNet(String net) {
		this.net = net;
	}

	public String getExpiration_text() {
		return expiration_text;
	}

	public void setExpiration_text(String expiration_text) {
		this.expiration_text = expiration_text;
	}

	public int getExpiration_date() {
		return expiration_date;
	}

	public void setExpiration_date(int expiration_date) {
		this.expiration_date = expiration_date;
	}

}
