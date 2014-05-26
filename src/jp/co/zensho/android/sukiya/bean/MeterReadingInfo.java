package jp.co.zensho.android.sukiya.bean;

import java.util.Date;
import java.util.ArrayList;

import jp.co.zensho.android.sukiya.common.StringUtils;

public class MeterReadingInfo {

	private double gas_usage;
	private String gas_meter_num;
	private String this_year_meter_num;
	private String last_year_meter_num;

	private double water_usage;
	private String water_meter_num;
	private Date egy_date;
	private String amount;
	private double usage;
	private float ratio;
	private ArrayList<MeterReadingInfo> gasListMeterInfo;
	private ArrayList<MeterReadingInfo> waterListMeterInfo;
	private ArrayList<MeterReadingInfo> electricityListMeterThisYear;
	private ArrayList<MeterReadingInfo> electricityListMeterLastYear;

	public String getThis_year_meter_num() {
		return this_year_meter_num;
	}

	public void setThis_year_meter_num(String this_year_meter_num) {
		this.this_year_meter_num = this_year_meter_num;
	}

	public String getLast_year_meter_num() {
		return last_year_meter_num;
	}

	public void setLast_year_meter_num(String last_year_meter_num) {
		this.last_year_meter_num = last_year_meter_num;
	}

	public float getRatio() {
		return ratio;
	}

	public void setRatio(float ratio) {
		this.ratio = ratio;
	}

	public ArrayList<MeterReadingInfo> getGasListMeterInfo() {
		return gasListMeterInfo;
	}

	public void setGasListMeterInfo(ArrayList<MeterReadingInfo> gasListMeterInfo) {
		this.gasListMeterInfo = gasListMeterInfo;
	}

	public ArrayList<MeterReadingInfo> getWaterListMeterInfo() {
		return waterListMeterInfo;
	}

	public void setWaterListMeterInfo(
			ArrayList<MeterReadingInfo> waterListMeterInfo) {
		this.waterListMeterInfo = waterListMeterInfo;
	}

	public ArrayList<MeterReadingInfo> getElectricityListMeterThisYear() {
		return electricityListMeterThisYear;
	}

	public void setElectricityListMeterThisYear(
			ArrayList<MeterReadingInfo> electricityListMeterThisYear) {
		this.electricityListMeterThisYear = electricityListMeterThisYear;
	}

	public ArrayList<MeterReadingInfo> getElectricityListMeterLastYear() {
		return electricityListMeterLastYear;
	}

	public void setElectricityListMeterLastYear(
			ArrayList<MeterReadingInfo> electricityListMeterLastYear) {
		this.electricityListMeterLastYear = electricityListMeterLastYear;
	}

	public MeterReadingInfo() {

	}

	public double getGas_usage() {
		return gas_usage;
	}

	public void setGas_usage(double gas_usage) {
		this.gas_usage = gas_usage;
	}

	public String getGas_meter_num() {
		return gas_meter_num;
	}

	public void setGas_meter_num(String gas_meter_num) {
		this.gas_meter_num = gas_meter_num;
	}

	public double getWater_usage() {
		return water_usage;
	}

	public void setWater_usage(double water_usage) {
		this.water_usage = water_usage;
	}

	public String getWater_meter_num() {
		return water_meter_num;
	}

	public void setWater_meter_num(String water_meter_num) {
		this.water_meter_num = water_meter_num;
	}

	public Date getEgy_date() {
		return egy_date;
	}

	public void setEgy_date(Date meter_date) {
		this.egy_date = meter_date;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public double getUsage() {
		return usage;
	}

	public void setUsage(double usage) {
		this.usage = usage;
	}

	public static String getLastMeterAmount(ArrayList<MeterReadingInfo> array) {
		String lastAmount = StringUtils.EMPTY;
		lastAmount = StringUtils.EMPTY;
		for (int i = 1; i < array.size(); i++) {
			if (array.get(i).getAmount() != null) {
				lastAmount = array.get(i).getAmount();
				break;
			}
		}
		// Old code - backup to test
		// if (array.size() < 1) {
		// lastAmount = StringUtils.EMPTY;
		// } else {
		// Date tempt = array.get(0).getMeter_date();
		// lastAmount = array.get(0).getAmount();
		// // array.get(0) is the current date record.
		// for (int i = 1; i < array.size(); i++) {
		// if (tempt.before(array.get(i).getMeter_date())
		// && array.get(i).getAmount() != null
		// && !StringUtils.EMPTY.equals(array.get(i).getAmount())) {
		// lastAmount = array.get(i).getAmount();
		//
		// }
		// }
		// if (lastAmount == null) {
		// lastAmount = StringUtils.EMPTY;
		// }
		// }
		return lastAmount;
	}
}
