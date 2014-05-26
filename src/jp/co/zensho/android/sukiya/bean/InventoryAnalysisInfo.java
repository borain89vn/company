package jp.co.zensho.android.sukiya.bean;

public class InventoryAnalysisInfo {
	private String date;
	private double zaiko;
	private double nyuko;
	private double loss;
	private double ido;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getZaiko() {
		return zaiko;
	}

	public void setZaiko(double zaiko) {
		this.zaiko = zaiko;
	}

	public double getNyuko() {
		return nyuko;
	}

	public void setNyuko(double nyuko) {
		this.nyuko = nyuko;
	}

	public double getLoss() {
		return loss;
	}

	public void setLoss(double loss) {
		this.loss = loss;
	}

	public double getIdo() {
		return ido;
	}

	public void setIdo(double ido) {
		this.ido = ido;
	}
}
