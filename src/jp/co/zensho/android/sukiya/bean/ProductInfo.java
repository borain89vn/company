package jp.co.zensho.android.sukiya.bean;

import java.text.MessageFormat;

import android.content.Context;
import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.common.StringUtils;

public class ProductInfo {
	private String hinCd;
	private String hinNm;
	private String imageName;
	private Double iriSu;
	private String expirationDays;
	private String expirationText;
	private String nisugataCd;
	private String nisugataNm;
	private String defaultTaniCd;
	private String defaultTaniNm;
	private String defaultKsnKbn;
	private Double defaultKsnCnst;
	private String taniCd;
	private String taniNm;
	private String defaultLocationCd;
	private String nisugataImageName;
	private Double theoreticalStock;
	private Double actualStock;
	private Double unitStock;
	private String memo;
	private String ksnKbn;
	private Double ksnCnst;
	private int dspOrder;
	private String orgLocationCd;
	private String locationCd;
	private String defaultNisugataCd;
	private String year;
	private String month;
	private String day;
	private int userAdd;
	private String menuType;
	private long key;

	/**
	 * @return result date is added by user or not
	 */
	public int getUserAdd() {
		return userAdd;
	}

	public void setUserAdd(int userAdd) {
		this.userAdd = userAdd;
	}

	/**
	 * @return menu type
	 */
	public String getMenuType() {
		return menuType;
	}

	public void setMenuType(String menuType) {
		this.menuType = menuType;
	}

	/**
	 * Database key
	 */
	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	// Zenshou ntdat 20140418 add --

	/**
	 * @return the hinCd
	 */
	public String getHinCd() {
		return hinCd;
	}

	/**
	 * @param hinCd
	 *            the hinCd to set
	 */
	public void setHinCd(String hinCd) {
		this.hinCd = hinCd;
	}

	/**
	 * @return the hinNm
	 */
	public String getHinNm() {
		return hinNm;
	}

	/**
	 * @param hinNm
	 *            the hinNm to set
	 */
	public void setHinNm(String hinNm) {
		this.hinNm = hinNm;
	}

	/**
	 * @return the imageName
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * @param imageName
	 *            the imageName to set
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	/**
	 * @return the iriSu
	 */
	public Double getIriSu() {
		return iriSu;
	}

	/**
	 * @param iriSu
	 *            the iriSu to set
	 */
	public void setIriSu(Double iriSu) {
		this.iriSu = iriSu;
	}

	/**
	 * @return the expirationDays
	 */
	public String getExpirationDays() {
		return expirationDays;
	}

	/**
	 * @param expirationDays
	 *            the expirationDays to set
	 */
	public void setExpirationDays(String expirationDays) {
		this.expirationDays = expirationDays;
	}

	/**
	 * @return the expirationText
	 */
	public String getExpirationText() {
		return expirationText;
	}

	/**
	 * @param expirationText
	 *            the expirationText to set
	 */
	public void setExpirationText(String expirationText) {
		this.expirationText = expirationText;
	}

	/**
	 * @return the nisugataCd
	 */
	public String getNisugataCd() {
		return nisugataCd;
	}

	/**
	 * @param nisugataCd
	 *            the nisugataCd to set
	 */
	public void setNisugataCd(String nisugataCd) {
		this.nisugataCd = nisugataCd;
	}

	/**
	 * @return the nisugataNm
	 */
	public String getNisugataNm() {
		return nisugataNm;
	}

	/**
	 * @param nisugataNm
	 *            the nisugataNm to set
	 */
	public void setNisugataNm(String nisugataNm) {
		this.nisugataNm = nisugataNm;
	}

	/**
	 * @return the defaultTaniCd
	 */
	public String getDefaultTaniCd() {
		return defaultTaniCd;
	}

	/**
	 * @param defaultTaniCd
	 *            the defaultTaniCd to set
	 */
	public void setDefaultTaniCd(String defaultTaniCd) {
		this.defaultTaniCd = defaultTaniCd;
	}

	/**
	 * @return the defaultTaniNm
	 */
	public String getDefaultTaniNm() {
		return defaultTaniNm;
	}

	/**
	 * @param defaultTaniNm
	 *            the defaultTaniNm to set
	 */
	public void setDefaultTaniNm(String defaultTaniNm) {
		this.defaultTaniNm = defaultTaniNm;
	}

	/**
	 * @return the defaultKsnKbn
	 */
	public String getDefaultKsnKbn() {
		return defaultKsnKbn;
	}

	/**
	 * @param defaultKsnKbn
	 *            the defaultKsnKbn to set
	 */
	public void setDefaultKsnKbn(String defaultKsnKbn) {
		this.defaultKsnKbn = defaultKsnKbn;
	}

	/**
	 * @return the defaultKsnCnst
	 */
	public Double getDefaultKsnCnst() {
		return defaultKsnCnst;
	}

	/**
	 * @param defaultKsnCnst
	 *            the defaultKsnCnst to set
	 */
	public void setDefaultKsnCnst(Double defaultKsnCnst) {
		this.defaultKsnCnst = defaultKsnCnst;
	}

	/**
	 * @return the taniCd
	 */
	public String getTaniCd() {
		return taniCd;
	}

	/**
	 * @param taniCd
	 *            the taniCd to set
	 */
	public void setTaniCd(String taniCd) {
		this.taniCd = taniCd;
	}

	/**
	 * @return the taniNm
	 */
	public String getTaniNm() {
		return taniNm;
	}

	/**
	 * @param taniNm
	 *            the taniNm to set
	 */
	public void setTaniNm(String taniNm) {
		this.taniNm = taniNm;
	}

	/**
	 * @return the orgLocationCd
	 */
	public String getOrgLocationCd() {
		return orgLocationCd;
	}

	/**
	 * @param orgLocationCd
	 *            the orgLocationCd to set
	 */
	public void setOrgLocationCd(String orgLocationCd) {
		this.orgLocationCd = orgLocationCd;
	}

	/**
	 * @return the defaultLocationCd
	 */
	public String getDefaultLocationCd() {
		return defaultLocationCd;
	}

	/**
	 * @param defaultLocationCd
	 *            the defaultLocationCd to set
	 */
	public void setDefaultLocationCd(String defaultLocationCd) {
		this.defaultLocationCd = defaultLocationCd;
	}

	/**
	 * @return the nisugataImageName
	 */
	public String getNisugataImageName() {
		return nisugataImageName;
	}

	/**
	 * @param nisugataImageName
	 *            the nisugataImageName to set
	 */
	public void setNisugataImageName(String nisugataImageName) {
		this.nisugataImageName = nisugataImageName;
	}

	/**
	 * @return the theoreticalStock
	 */
	public Double getTheoreticalStock() {
		return theoreticalStock;
	}

	/**
	 * @param theoreticalStock
	 *            the theoreticalStock to set
	 */
	public void setTheoreticalStock(Double theoreticalStock) {
		this.theoreticalStock = theoreticalStock;
	}

	/**
	 * @return the actualStock
	 */
	public Double getActualStock() {
		return actualStock;
	}

	/**
	 * @param actualStock
	 *            the actualStock to set
	 */
	public void setActualStock(Double actualStock) {
		actualStock = actualStock == null ? Double.valueOf(0) : actualStock;

		String defaultKsnKbn = StringUtils.isEmpty(this.defaultKsnKbn) ? StringUtils.ZERO
				: this.defaultKsnKbn;
		Double defaultKsnCnst = this.defaultKsnCnst == null ? Double.valueOf(0)
				: this.defaultKsnCnst;

		String ksnKbn = StringUtils.isEmpty(this.ksnKbn) ? StringUtils.ZERO
				: this.ksnKbn;
		Double ksnCnst = this.ksnCnst == null ? Double.valueOf(0)
				: this.ksnCnst;

		// GvLuong bugfix 20140308 start
		this.actualStock = actualStock;

		// In Case default_tani_cd = tani_cd , unitStock = actualStock
		if (this.defaultTaniCd.equals(this.taniCd)) {
			this.unitStock = this.actualStock;
			return;
		}
		// exchange actualStock to unitStock in the other case
		if (StringUtils.ZERO.equals(defaultKsnKbn)) {
			if (StringUtils.ZERO.equals(ksnKbn)) {
				this.unitStock = actualStock;
			} else if (StringUtils.ONE.equals(ksnKbn)) {
				this.unitStock = actualStock * ksnCnst;
			} else if (StringUtils.TWO.equals(ksnKbn)) {
				this.unitStock = actualStock / ksnCnst;
			}
		} else if (StringUtils.ONE.equals(defaultKsnKbn)) {
			if (StringUtils.ZERO.equals(ksnKbn)) {
				this.unitStock = actualStock / defaultKsnCnst;
			} else if (StringUtils.ONE.equals(ksnKbn)) {
				this.unitStock = actualStock * ksnCnst / defaultKsnCnst;
			} else if (StringUtils.TWO.equals(ksnKbn)) {
				this.unitStock = actualStock / (ksnCnst * defaultKsnCnst);
			}
		} else if (StringUtils.TWO.equals(defaultKsnKbn)) {
			if (StringUtils.ZERO.equals(ksnKbn)) {
				this.unitStock = actualStock * defaultKsnCnst;
			} else if (StringUtils.ONE.equals(ksnKbn)) {
				this.unitStock = actualStock * ksnCnst * defaultKsnCnst;
			} else if (StringUtils.TWO.equals(ksnKbn)) {
				this.unitStock = (actualStock * defaultKsnCnst) / ksnCnst;
			}
		}
		// GvLuong bugfix 20140308 end
	}

	/**
	 * @return the unitStock
	 */
	public Double getUnitStock() {
		return unitStock;
	}

	/**
	 * @param unitStock
	 *            the unitStock to set
	 */
	public void setUnitStock(Double unitStock) {
		this.unitStock = unitStock;
	}

	/**
	 * @return the memo
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * @param memo
	 *            the memo to set
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * @return the ksnKbn
	 */
	public String getKsnKbn() {
		return ksnKbn;
	}

	/**
	 * @param ksnKbn
	 *            the ksnKbn to set
	 */
	public void setKsnKbn(String ksnKbn) {
		this.ksnKbn = ksnKbn;
	}

	/**
	 * @return the ksnCnst
	 */
	public Double getKsnCnst() {
		return ksnCnst;
	}

	/**
	 * @param ksnCnst
	 *            the ksnCnst to set
	 */
	public void setKsnCnst(Double ksnCnst) {
		this.ksnCnst = ksnCnst;
	}

	/**
	 * @return the dspOrder
	 */
	public int getDspOrder() {
		return dspOrder;
	}

	/**
	 * @param dspOrder
	 *            the dspOrder to set
	 */
	public void setDspOrder(int dspOrder) {
		this.dspOrder = dspOrder;
	}

	/**
	 * @return the locationCd
	 */
	public String getLocationCd() {
		return locationCd;
	}

	/**
	 * @param locationCd
	 *            the locationCd to set
	 */
	public void setLocationCd(String locationCd) {
		this.locationCd = locationCd;
	}

	/**
	 * @return the defaultNisugataCd
	 */
	public String getDefaultNisugataCd() {
		return defaultNisugataCd;
	}

	/**
	 * @param defaultNisugataCd
	 *            the defaultNisugataCd to set
	 */
	public void setDefaultNisugataCd(String defaultNisugataCd) {
		this.defaultNisugataCd = defaultNisugataCd;
	}

	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * @param month
	 *            the month to set
	 */
	public void setMonth(String month) {
		this.month = month;
	}

	/**
	 * @return the day
	 */
	public String getDay() {
		return day;
	}

	/**
	 * @param day
	 *            the day to set
	 */
	public void setDay(String day) {
		this.day = day;
	}

	public boolean isEmptyDate() {
		if (StringUtils.isEmpty(year) && StringUtils.isEmpty(month)
				&& StringUtils.isEmpty(day)) {
			return true;
		}
		return false;
	}

	public String getFullDateString(Context context) {
		if (!StringUtils.isEmpty(year) && !StringUtils.isEmpty(month)
				&& !StringUtils.isEmpty(day)) {
			String format = context.getString(R.string.full_date_string);
			return MessageFormat.format(format, year, month, day);
		}
		return StringUtils.EMPTY;
	}

	public String getShortDateString() {
		if (!StringUtils.isEmpty(year) && !StringUtils.isEmpty(month)
				&& !StringUtils.isEmpty(day)) {
			StringBuilder builder = new StringBuilder();
			builder.append(20).append(year);
			builder.append(month);
			builder.append(day);
			return builder.toString();
		}
		return StringUtils.EMPTY;
	}


	public boolean equalsTo(ProductInfo product) {
		boolean result = false;
		if (this.hinCd.equals(product.hinCd)
				&& this.nisugataCd.equals(product.nisugataCd)
				&& this.locationCd.equals(product.locationCd)) {
			result = true;

		}
		return result;
	}
}
