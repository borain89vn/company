package jp.co.zensho.android.sukiya.application;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.zensho.android.sukiya.bean.DailyCheckboxValue;
import jp.co.zensho.android.sukiya.bean.KPIInfo;
import jp.co.zensho.android.sukiya.bean.KPIRangeInfo;
import jp.co.zensho.android.sukiya.bean.LocationInfo;
import jp.co.zensho.android.sukiya.bean.MenuPositionInfo;
import jp.co.zensho.android.sukiya.bean.ProductExpirationInfo;
import jp.co.zensho.android.sukiya.bean.ProductInfo;
import jp.co.zensho.android.sukiya.database.model.SukiyaSetting;
import android.app.Application;

/**
 * @author lncong
 *
 */
public class SukiyaApplication extends Application {

    /** 端末のIPアドレス **/
    private String remoteAddress;
    
    private SukiyaSetting info;
    private Date currentDate;
    private KPIInfo kpiInfo;
    private KPIRangeInfo kpiRangeInfo;
    private MenuPositionInfo selectedMenu;
    private List<MenuPositionInfo> menuPositionList;
    private List<DailyCheckboxValue> dailyCheckboxList;
    private List<LocationInfo> locationList;
    private List<ProductInfo> productList;
    private List<ProductExpirationInfo> productExpirationList;
    private Map<String, String> locationMap;
    private boolean displayOnly;

    public List<ProductExpirationInfo> getProductExpirationList() {
		return productExpirationList;
	}

	public void setProductExpirationList(
			List<ProductExpirationInfo> productExpirationList) {
		this.productExpirationList = productExpirationList;
	}

	/**
     * @return 端末のIPアドレス
     */
    public String getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * @param remoteAddressは端末のIPアドレです。
     */
    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    /**
     * @return the info
     */
    public SukiyaSetting getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(SukiyaSetting info) {
        this.info = info;
    }
    
    public String getShopCode() {
        if (this.info != null) {
            return this.info.getShopCode();
        }
        return null;
    }
    
    public String getShopName() {
        if (this.info != null) {
            return this.info.getShopName();
        }
        return null;
    }
    
    public int getDownloadZipState() {
        if (this.info != null) {
            return this.info.getDownloadZipState();
        }
        return 0;
    }

    /**
     * @return the currentDate
     */
    public Date getCurrentDate() {
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * @return the kpiInfo
     */
    public KPIInfo getKpiInfo() {
        return kpiInfo;
    }

    /**
     * @param kpiInfo the kpiInfo to set
     */
    public void setKpiInfo(KPIInfo kpiInfo) {
        this.kpiInfo = kpiInfo;
    }

    /**
     * @return the kpiRangeInfo
     */
    public KPIRangeInfo getKpiRangeInfo() {
        return kpiRangeInfo;
    }

    /**
     * @param kpiRangeInfo the kpiRangeInfo to set
     */
    public void setKpiRangeInfo(KPIRangeInfo kpiRangeInfo) {
        this.kpiRangeInfo = kpiRangeInfo;
    }

    /**
     * @return the displayOnly
     */
    public boolean isDisplayOnly() {
        return displayOnly;
    }

    /**
     * @param displayOnly the displayOnly to set
     */
    public void setDisplayOnly(boolean displayOnly) {
        this.displayOnly = displayOnly;
    }

    /**
     * @return the selectedMenu
     */
    public MenuPositionInfo getSelectedMenu() {
        return selectedMenu;
    }

    /**
     * @param selectedMenu the selectedMenu to set
     */
    public void setSelectedMenu(MenuPositionInfo selectedMenu) {
        this.selectedMenu = selectedMenu;
    }

    /**
     * @return the menuPositionList
     */
    public List<MenuPositionInfo> getMenuPositionList() {
        return menuPositionList;
    }

    /**
     * @param menuPositionList the menuPositionList to set
     */
    public void setMenuPositionList(List<MenuPositionInfo> menuPositionList) {
        this.menuPositionList = menuPositionList;
    }

    /**
     * @return the dailyCheckboxList
     */
    public List<DailyCheckboxValue> getDailyCheckboxList() {
        return dailyCheckboxList;
    }

    /**
     * @param dailyCheckboxList the dailyCheckboxList to set
     */
    public void setDailyCheckboxList(List<DailyCheckboxValue> dailyCheckboxList) {
        this.dailyCheckboxList = dailyCheckboxList;
    }

    /**
     * @return the locationList
     */
    public List<LocationInfo> getLocationList() {
        return locationList;
    }

    /**
     * @param locationList the locationList to set
     */
    public void setLocationList(List<LocationInfo> locationList) {
        if (locationList != null && locationList.size() > 0) {
            if (this.locationMap == null) {
                this.locationMap = new HashMap<String, String>();
            } else {
                this.locationMap.clear();
            }
            for (LocationInfo locationInfo : locationList) {
                this.locationMap.put(locationInfo.getCode(), locationInfo.getName());
            }
        }
        this.locationList = locationList;
    }

    /**
     * @return the productList
     */
    public List<ProductInfo> getProductList() {
        return productList;
    }

    /**
     * @param productList the productList to set
     */
    public void setProductList(List<ProductInfo> productList) {
        this.productList = productList;
    }
    
    /**
     * Get location name from location code
     * @param locationCode
     * @return
     */
    public String getLoactionName(String locationCode) {
        if (this.locationMap != null && this.locationMap.size() > 0 && locationCode != null) {
            return this.locationMap.get(locationCode);
        }
        return null;
    }
}
