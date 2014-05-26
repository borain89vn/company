package jp.co.zensho.android.sukiya.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jp.co.zensho.android.sukiya.common.StringUtils;


public class MenuInfo {
    
    private String label;
    private String timeCode;
    private String from;
    private String to;
    private String enabledFrom;
    private String enabledTo;
    private int daily;
    private int sun;
    private int mon;
    private int tue;
    private int wed;
    private int thu;
    private int fri;
    private int sat;
    private int monthly;
    private int priority;
    private String repeatState;
    private String lastupdate;
    
    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }
    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
    /**
     * @return the from
     */
    public String getFrom() {
        return from;
    }
    /**
     * @param from the from to set
     */
    public void setFrom(String from) {
        this.from = from;
    }
    /**
     * @return the to
     */
    public String getTo() {
        return to;
    }
    /**
     * @param to the to to set
     */
    public void setTo(String to) {
        this.to = to;
    }
    /**
     * @return the daily
     */
    public int getDaily() {
        return daily;
    }
    /**
     * @param daily the daily to set
     */
    public void setDaily(int daily) {
        this.daily = daily;
    }
    /**
     * @return the sun
     */
    public int getSun() {
        return sun;
    }
    /**
     * @param sun the sun to set
     */
    public void setSun(int sun) {
        this.sun = sun;
    }
    /**
     * @return the mon
     */
    public int getMon() {
        return mon;
    }
    /**
     * @param mon the mon to set
     */
    public void setMon(int mon) {
        this.mon = mon;
    }
    /**
     * @return the tue
     */
    public int getTue() {
        return tue;
    }
    /**
     * @param tue the tue to set
     */
    public void setTue(int tue) {
        this.tue = tue;
    }
    /**
     * @return the wed
     */
    public int getWed() {
        return wed;
    }
    /**
     * @param wed the wed to set
     */
    public void setWed(int wed) {
        this.wed = wed;
    }
    /**
     * @return the thu
     */
    public int getThu() {
        return thu;
    }
    /**
     * @param thu the thu to set
     */
    public void setThu(int thu) {
        this.thu = thu;
    }
    /**
     * @return the fri
     */
    public int getFri() {
        return fri;
    }
    /**
     * @param fri the fri to set
     */
    public void setFri(int fri) {
        this.fri = fri;
    }
    /**
     * @return the sat
     */
    public int getSat() {
        return sat;
    }
    /**
     * @param sat the sat to set
     */
    public void setSat(int sat) {
        this.sat = sat;
    }
    /**
     * @return the monthly
     */
    public int getMonthly() {
        return monthly;
    }
    /**
     * @param monthly the monthly to set
     */
    public void setMonthly(int monthly) {
        this.monthly = monthly;
    }
    /**
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }
    /**
     * @param priority the priority to set
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }
    /**
     * @return the repeatState
     */
    public String getRepeatState() {
        return repeatState;
    }
    /**
     * @param repeatState the repeatState to set
     */
    public void setRepeatState(String repeatState) {
        this.repeatState = repeatState;
    }
    /**
     * @return the timeCode
     */
    public String getTimeCode() {
        return timeCode;
    }
    /**
     * @param timeCode the timeCode to set
     */
    public void setTimeCode(String timeCode) {
        this.timeCode = timeCode;
    }
    /**
     * @return the enabledFrom
     */
    public String isEnabledFrom() {
        return enabledFrom;
    }
    /**
     * @param enabledFrom the enabledFrom to set
     */
    public void setEnabledFrom(String enabledFrom) {
        this.enabledFrom = enabledFrom;
    }
    /**
     * @return the enabledTo
     */
    public String isEnabledTo() {
        return enabledTo;
    }
    /**
     * @param enabledTo the enabledTo to set
     */
    public void setEnabledTo(String enabledTo) {
        this.enabledTo = enabledTo;
    }
    /**
     * @return the lastupdate
     */
    public String getLastupdate() {
        return lastupdate;
    }
    /**
     * @param lastupdate the lastupdate to set
     */
    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }
    
    public boolean isEnable() {
        if (StringUtils.isEmpty(this.enabledFrom) && StringUtils.isEmpty(this.enabledTo)) {
            return true;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HHmm", Locale.JAPAN);
            Date today = new Date();
            int currentTime = Integer.parseInt(sdf.format(today));
            
            int fromTime;
            int toTime;
            if (!StringUtils.isEmpty(this.enabledFrom) && StringUtils.isEmpty(this.enabledTo)) {
                fromTime = Integer.parseInt(this.enabledFrom);
                if (fromTime <= currentTime) {
                    return true;
                }
            } else if (StringUtils.isEmpty(this.enabledFrom) && !StringUtils.isEmpty(this.enabledTo)) {
                toTime = Integer.parseInt(this.enabledTo);
                if (currentTime <= toTime) {
                    return true;
                }
            } else {
                fromTime = Integer.parseInt(this.enabledFrom);
                toTime = Integer.parseInt(this.enabledTo);
                if (fromTime > toTime) {
                    if (currentTime <= 2400 && fromTime <= currentTime) {
                        return true;
                    } else if (currentTime > 0 && currentTime <= toTime) {
                        return true;
                    }
                } else {
                    if (fromTime <= currentTime && currentTime <= toTime) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
