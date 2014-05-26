
package jp.co.zensho.android.sukiya.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.co.zensho.android.sukiya.common.DateUtils;
import jp.co.zensho.android.sukiya.common.StringUtils;
import jp.co.zensho.android.sukiya.common.SukiyaContant;

public class MenuPositionInfo {
    private String code;
    private int x;
    private int y;
    private String lastUpdate;
    private MenuInfo currentDisplayInfo;
    private List<MenuInfo> list;
    private int historyId;

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return the lastUpdate
     */
    public String getLastUpdate() {
        if (SukiyaContant.MENU_02.equals(code) && this.currentDisplayInfo != null) {
            return this.currentDisplayInfo.getLastupdate();
        }
        return lastUpdate;
    }

    /**
     * @param lastUpdate the lastUpdate to set
     */
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @return the list
     */
    public List<MenuInfo> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<MenuInfo> list) {
        this.list = list;
    }

    /**
     * @return the currentDisplayInfo
     */
    public MenuInfo getCurrentDisplayInfo() {
        return currentDisplayInfo;
    }

    /**
     * @param currentDisplayInfo the currentDisplayInfo to set
     */
    public void setCurrentDisplayInfo(MenuInfo currentDisplayInfo) {
        this.currentDisplayInfo = currentDisplayInfo;
    }

    public boolean isShowNote(Date currentDate) {
        if (currentDisplayInfo == null) {
            return false;
        }
        String from = currentDisplayInfo.getFrom();
        String to = currentDisplayInfo.getTo();
        if (!StringUtils.isEmpty(from) && !StringUtils.isEmpty(to)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HHmm", Locale.JAPAN);
            Date today = new Date();
            int currentVal = Integer.parseInt(sdf.format(today));
            
            int fromVal = Integer.parseInt(from);
            int toVal = Integer.parseInt(to);
            if (fromVal <= currentVal && currentVal <= toVal) {
                String strLastUpdate = getLastUpdate();
                if (!StringUtils.isEmpty(strLastUpdate)) {
                    try {
                        SimpleDateFormat sdfLastupdate;
                        sdfLastupdate = new SimpleDateFormat(StringUtils.DATE_TIME_LAST_UPDATE, Locale.JAPAN);
                        Date dteLastupdate = sdfLastupdate.parse(strLastUpdate);
                        Calendar calLastupdate = DateUtils.dateOnly(dteLastupdate);
                        Calendar calToday = DateUtils.dateOnly(currentDate);
                        if (DateUtils.isSameDay(calLastupdate, calToday)) {
                           int lastupdateVal = Integer.parseInt(sdf.format(dteLastupdate));
                           if (fromVal <= lastupdateVal && lastupdateVal <= toVal) {
                               return false;
                           }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * @return the historyId
     */
    public int getHistoryId() {
        return historyId;
    }

    /**
     * @param historyId the historyId to set
     */
    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }
}
