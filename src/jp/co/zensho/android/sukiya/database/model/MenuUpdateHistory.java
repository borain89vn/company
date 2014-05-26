package jp.co.zensho.android.sukiya.database.model;

public class MenuUpdateHistory {
    private int id;
    private String lastUpdate;
    
    public MenuUpdateHistory() {
        
    }
    
    public MenuUpdateHistory(int menuId, String strLastUpdate) {
        this.id = menuId;
        this.lastUpdate = strLastUpdate;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the lastUpdate
     */
    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @param lastUpdate the lastUpdate to set
     */
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
