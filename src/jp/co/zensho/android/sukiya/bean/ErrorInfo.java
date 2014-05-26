package jp.co.zensho.android.sukiya.bean;

public class ErrorInfo {
    private String code;
    private String message;
    
    /**
     * @return the error code
     */
    public String getCode() {
        return code;
    }
    /**
     * @param code the error code to set
     */
    public void setCode(String code) {
        this.code = code;
    }
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
