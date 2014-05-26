package jp.co.zensho.android.sukiya.common;

public class StringUtils {

	public static final String YYYYMMDD_DISP = "yyyy年MM月dd日";
	public static final String YYYYMMDD = "yyyyMMdd";
	public static final String HHMMSS_DISP = "HH:mm:ss";
	public static final String HHMMSS = "HHmmss";
	public static final String DATE_TIME_LAST_UPDATE = "yyyy/MM/dd HH:mm:ss更新";

	public static final String MINUS = "-";
	public static final String EMPTY = "";
	public static final String ZERO = "0";
	public static final String ONE = "1";
	public static final String TWO = "2";

	// Help code
	public static final String S021_HELP_CD = "01";
	public static final String S021_CONFIRM_HELP_CD = "02";
	public static final String S022_HELP_CD = "01";
	public static final String S022_CONFIRM_HELP_CD = "02";
	public static final String S023_HELP_CD = "03";
	public static final String S023_CONFIRM_HELP_CD = "04";
	public static final String S024_HELP_CD = "05";
	public static final String S024_CONFIRM_HELP_CD = "06";
	public static final String S301_HELP_CD = "21";
	public static final String S302_HELP_CD = "31";
	public static final String S303_HELP_CD = "32";
	public static final String S201_HELP_CD = "11";
	public static final String S205_HELP_CD = "12";
	public static final String S208_HELP_CD = "13";
	public static final String S211_HELP_CD = "14";
	public static final String S304_HELP_CD = "51";

	public static final String S401_1_HELP_CD = "41";
	public static final String S401_2_HELP_CD = "42";
	public static final String S401_3_HELP_CD = "43";

	public static final String S402_1_HELP_CD = "45";
	public static final String S402_2_HELP_CD = "44";

	public static boolean isEmpty(String string) {
		return string == null || string.isEmpty();
	}
}
