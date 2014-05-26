package jp.co.zensho.android.sukiya.bean;

public class DailyCheckboxValue {
    private String code;
    private String name;
    private boolean selected;
    private boolean canUnselect;
    
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
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }
    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    /**
     * @return the canUnselect
     */
    public boolean isCanUnselect() {
        return canUnselect;
    }
    /**
     * @param canUnselect the canUnselect to set
     */
    public void setCanUnselect(boolean canUnselect) {
        this.canUnselect = canUnselect;
    }
    
}
