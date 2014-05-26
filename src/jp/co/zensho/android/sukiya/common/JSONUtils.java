package jp.co.zensho.android.sukiya.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jp.co.zensho.android.sukiya.S211Activity;
import jp.co.zensho.android.sukiya.bean.API037_NumDays;
import jp.co.zensho.android.sukiya.bean.API038_Unmatched;
import jp.co.zensho.android.sukiya.bean.API039_LossAmount;
import jp.co.zensho.android.sukiya.bean.API040_MoveLoad;
import jp.co.zensho.android.sukiya.bean.API14_ProductCatSel;
import jp.co.zensho.android.sukiya.bean.API15_ProductNameSel;
import jp.co.zensho.android.sukiya.bean.API16_LossReason;
import jp.co.zensho.android.sukiya.bean.API17_QuantityUnit;
import jp.co.zensho.android.sukiya.bean.API18_ViewHistory;
import jp.co.zensho.android.sukiya.bean.API20_ShortReason;
import jp.co.zensho.android.sukiya.bean.API21_ViewHistory;
import jp.co.zensho.android.sukiya.bean.API25_ViewHistory;
import jp.co.zensho.android.sukiya.bean.API27_AcceptanceShop;
import jp.co.zensho.android.sukiya.bean.API28_AcceptanceShop_Detail;
import jp.co.zensho.android.sukiya.bean.API29_ViewHistory;
import jp.co.zensho.android.sukiya.bean.DailyCheckboxValue;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import jp.co.zensho.android.sukiya.bean.InventoryAnalysisInfo;
import jp.co.zensho.android.sukiya.bean.KPIInfo;
import jp.co.zensho.android.sukiya.bean.KPIRangeInfo;
import jp.co.zensho.android.sukiya.bean.LocationInfo;
import jp.co.zensho.android.sukiya.bean.MenuInfo;
import jp.co.zensho.android.sukiya.bean.MenuPositionInfo;
import jp.co.zensho.android.sukiya.bean.MeterReadingInfo;
import jp.co.zensho.android.sukiya.bean.MeterType;
import jp.co.zensho.android.sukiya.bean.ProductExpirationInfo;
import jp.co.zensho.android.sukiya.bean.ProductInfo;
import jp.co.zensho.android.sukiya.bean.Shop;
import jp.co.zensho.android.sukiya.bean.Tenpo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONUtils {
	public static final String ERROR = "error";
	public static final String ERROR_CODE = "error_code";
	public static final String ERROR_MESSAGE = "error_message";
	public static final String CODE = "code";
	private static final String NAME = "name";
	public static final String AMOUNT = "amount";
	public static final String CHECK = "check";
	public static final String METER_CATEGORY = "meter_category";
	public static final String METER_NUM = "meter_num";
	public static final String YYYY = "yyyy";
	public static final String YYYYMM = "yyyymm";
	public static final String METER_TYPE = "meter_type";
	public static final String M_TEMPO_METER = "m_tenpo_meter";
	private static final String PER_STOCK = "per_stock";
	private static final String GAS = "gas";
	private static final String WATER = "water";
	private static final String GAS_USAGE = "gas_usage";
	private static final String WATER_USAGE = "water_usage";
	private static final String GAS_METER_NUM = "gas_meter_num";
	private static final String WATER_METER_NUM = "water_meter_num";
	// private static final String METER_DATE = "meter_date";
	private static final String AMOUNT_USED = "amount_used";
	public static final String METER_CD = "meter_cd";
	private static final String METER_NM = "meter_nm";
	private static final String LAST_YEAR = "last_year";
	private static final String THIS_YEAR = "this_year";
	private static final String THIS_YEAR_METER_NUM = "this_year_meter_num";
	private static final String LAST_YEAR_METER_NUM = "last_year_meter_num";
	private static final String CMP_LAST_YEAR = "cmp_last_year";

	// API Contants
	public static final String HOST_SELECTION = "host_selection";
	// public static String API_HOST_PATH = "http://192.168.0.191";

	// public static String API_HOST_PATH = "http://10.32.5.45:3030";
	public static String API_HOST_PATH = "http://zensho.rapinics.jp";
	public static String API_001_PATH = "/sk/api/api/init/shop";
	public static String API_001_IMAGE_PATH = "/sk/api/api/init/images";
	public static String API_002_PATH = "/sk/api/api/top/menu";
	public static String API_003_PATH = "/sk/api/api/top/kpi";
	public static String API_004_PATH = "/sk/api/api/master/kpi_threshold";
	public static String API_005_PATH = "/sk/api/api/master/shipping_category";
	public static String API_006_PATH = "/sk/api/api/post/shipping_category_check";
	public static String API_007_PATH = "/sk/api/api/master/location";
	public static String API_008_PATH = "/sk/api/api/clearance/item_list";
	public static String API_009_PATH = "/sk/api/api/clearance/update_item";
	public static String API_010_PATH = "/sk/api/api/clearance/package_list";
	public static String API_011_PATH = "/sk/api/api/clearance/update_package";
	public static String API_012_PATH = "/sk/api/api/clearance/expiration_list";
	public static String API_013_PATH = "/sk/api/api/clearance/update_expiration";
	public static String API_014_PATH = "/sk/api/api/master/item_category";
	public static String API_015_PATH = "/sk/api/api/master/item";
	public static String API_016_PATH = "/sk/api/api/master/loss_reason";
	public static String API_017_PATH = "/sk/api/api/master/item_unit";
	public static String API_018_PATH = "/sk/api/api/report/loss_history";
	public static String API_019_PATH = "/sk/api/api/report/update_loss";
	public static String API_020_PATH = "/sk/api/api/master/short_reason";
	public static String API_021_PATH = "/sk/api/api/report/short_history";
	public static String API_022_PATH = "/sk/api/api/report/update_short";
	public static String API_023_PATH = "/sk/api/api/report/shipping_shop_list";
	public static String API_024_PATH = "/sk/api/api/report/shipping_shop_search";
	public static String API_025_PATH = "/sk/api/api/report/shipping_history";
	public static String API_026_PATH = "/sk/api/api/report/update_shipping";
	public static String API_027_PATH = "/sk/api/api/report/from_shop_list";
	public static String API_028_PATH = "/sk/api/api/report/rcv_item_list";
	public static String API_029_PATH = "/sk/api/api/report/acceptance_history";
	public static String API_030_PATH = "/sk/api/api/report/update_acceptance";
	public static String API_031_PATH = "/sk/api/api/scheduled_delivery/list";
	public static String API_032_PATH = "/sk/api/api/scheduled_delivery/update";
	public static String API_033_PATH = "/sk/api/api/meter/list1";
	public static String API_034_PATH = "/sk/api/api/master/meter_type";
	public static String API_035_PATH = "/sk/api/api/meter/list2";
	public static String API_036_PATH = "/sk/api/api/meter/update";
	public static String API_037_PATH = "/sk/api/api/kpi/stock_days_list";
	public static String API_038_PATH = "/sk/api/api/kpi/unmatch_list";
	public static String API_039_PATH = "/sk/api/api/kpi/loss_worst_list";
	public static String API_040_PATH = "/sk/api/api/kpi/shipping_worst_list";
	public static String API_041_PATH = "/sk/api/api/kpi/acceptance_worst_list";
	public static String API_042_PATH = "/sk/api/api/kpi/zaiko_analyze";
	public static String API_043_PATH = "/sk/api/api/kpi/help";
	// API 001
	public static final String IP_ADDRESS = "ip_addr";
	public static final String TENPO_CODE = "tenpo_cd";
	public static final String TENPO_NAME = "tenpo_nm";
	// API 002
	public static final String API_002_RESULT_KEY = "m_tablet_menu";
	public static final String MENU_CODE = "menu_cd";
	public static final String DISPLAY_X = "display_x";
	public static final String DISPLAY_Y = "display_y";
	public static final String TENPO_TABLET_MENU = "m_tenpo_tablet_menu";
	public static final String MENU_LABEL = "menu_label";
	public static final String TIME_CODE = "time_cd";
	public static final String BALOON_FROM = "baloon_from";
	public static final String BALOON_TO = "baloon_to";
	public static final String ENABLED_FROM = "enabled_from";
	public static final String ENABLED_TO = "enabled_to";
	public static final String DAILY = "daily";
	public static final String SUN = "sun";
	public static final String MON = "mon";
	public static final String TUE = "tue";
	public static final String WED = "wed";
	public static final String THU = "thu";
	public static final String FRI = "fri";
	public static final String SAT = "sat";
	public static final String MONTHLY = "monthly";
	public static final String PRIORITY = "priority";
	// API 003
	public static final String ZAIKONISSU = "zaikonissu";
	public static final String ZAIKONISSU_PREV = "zaikonissu_prev";
	public static final String GENKARITSU = "genkaritsu";
	public static final String GENKARITSU_PREV = "genkaritsu_prev";
	public static final String UNMATCH = "unmatch";
	public static final String UNMATCH_PREV = "unmatch_prev";
	public static final String LOSS = "loss";
	public static final String LOSS_PREV = "loss_prev";
	public static final String RCV = "rcv";
	public static final String RCV_PREV = "rcv_prev";
	public static final String SND = "snd";
	public static final String SND_PREV = "snd_prev";
	// API 004
	public static final String ZAIKONISSU_MAX = "zaikonissu_max";
	public static final String ZAIKONISSU_MIN = "zaikonissu_min";
	public static final String GENKARITSU_MAX = "genkaritsu_max";
	public static final String GENKARITSU_MIN = "genkaritsu_min";
	public static final String UNMATCH_MAX = "unmatch_max";
	public static final String UNMATCH_MIN = "unmatch_min";
	public static final String LOSS_MAX = "loss_max";
	public static final String LOSS_MIN = "loss_min";
	public static final String RCV_MAX = "rcv_max";
	public static final String RCV_MIN = "rcv_min";
	public static final String SND_MAX = "snd_max";
	public static final String SND_MIN = "snd_min";
	// API 005
	public static final String API_005_RESULT_KEY = "m_delivery";
	public static final String SND_HIN_CODE = "snd_hin_cd";
	public static final String SND_HIN_NAME = "snd_hin_name";
	// API 006
	public static final String API_006_PARAMS_KEY = "d_tenpo_delivery_check";
	public static final String CHECKED = "checked";
	public static final String RESULT = "result";
	// API 007
	public static final String API_007_PARAMS_KEY = "m_loc";
	public static final String LOCATION_CODE = "location_cd";
	public static final String LOCATION_NAME = "location_nm";
	public static final String IMG_FILE_NAME = "img_file_name";
	// API 008
	public static final String CLEARANCE_TYPE = "clearance_type";
	public static final String API_008_PARAMS_KEY = "item";
	public static final String HIN_CODE = "hin_cd";
	public static final String HIN_NAME = "hin_nm";
	public static final String IRI_SU = "iri_su";
	public static final String EXPIRATION_DAYS = "expiration_days";
	public static final String EXPIRATION_TEXT = "expiration_text";
	public static final String NISUGATA_CODE = "nisugata_cd";
	public static final String NISUGATA_NAME = "nisugata_nm";
	public static final String DEFAULT_TANI_CODE = "default_tani_cd";
	public static final String DEFAULT_TANI_NAME = "default_tani_nm";
	public static final String DEFAULT_KSN_KBN = "default_ksn_kbn";
	public static final String DEFAULT_KSN_CNST = "default_ksn_cnst";
	public static final String TANI_CODE = "tani_cd";
	public static final String TANI_NAME = "tani_nm";
	public static final String DEFAULT_LOCATION_CODE = "default_location_cd";
	public static final String NISUGATA_IMG_FILE_NAME = "nisugata_img_file_name";
	public static final String THEORETICAL_STOCK = "theoretical_stock";
	public static final String MEMO = "memo";
	public static final String KSN_KBN = "ksn_kbn";
	public static final String KSN_CNST = "ksn_cnst";
	public static final String DSP_ORDER = "dsp_order";
	public static final String DEFAULT_NISUGATA_CODE = "default_nisugata_cd";
	// API 009
	public static final String EGY_DATE = "egy_date";
	public static final String KENTO_DATE = "kento_date";
	public static final String ZAIKO_SU = "zaiko_su";
	public static final String TANA_ZAIKO = "tana_zaiko";
	public static final String ORG_LOCATION_CD = "org_location_cd";
	public static final String API_009_PARAMS_KEY = "item";
	public static final String PART_CODE = "part_cd";
	// API 011
	public static final String API_011_PARAMS_KEY = "d_tana_houzai";
	// API 013
	public static final String API_013_PARAMS_KEY = "d_tana_shomikigen";
	public static final String SHOMIKIGEN = "shomikigen";
	// API 014
	public static final String ITEM_CATEGORY = "m_hin_div";
	// API 015
	public static final String API_015_PARAMS_KEY = "ht_div_cd";
	public static final String API_015_RECEIVED_CATEGORY_NAME = "m_hinmoku";
	// API 016
	public static final String API_016_RECEIVED_LOSS_REASON = "m_loss_r";
	// API 017
	public static final String API_017_PARAMS_KEY = "hin_cd";
	public static final String API_017_RECEIVED_UNIT = "m_tani";
	// API 018
	public static final String API_018_PARAMS_KEY = "yyyymm";
	public static final String API_018_RECEIVED_HISTORY = "d_loss_l";
	// API 019
	public static final String API_019_PARAMS_KEY = "d_loss";
	public static final String API_019_LOSS_REASON = "loss_r";
	public static final String API_019_LOSS_SUM = "loss_su";
	// API 020
	public static final String API_020_RECEIVED_SHORT_REASON = "m_shrt_r";
	// API 021
	public static final String API_021_RECEIVED_SHORT_HISTORY = "d_shrt";
	// API 022
	public static final String API_022_PARAMS_KEY = "d_shrt";
	public static final String API_022_SHORT_REASON = "shrt_r";
	public static final String API_022_HOUR = "hour";
	// API 023
	public static final String API_023_PARAMS_KEY = "limit";
	public static final String API_023_RECEIVED_SHOP_FULL = "tenpo";
	// API 024
	public static final String API_024_PARAMS_KEY = "keyword";
	public static final String API_024_RECEIVED_SHOP_SEARCHED = "result";
	// API 025
	public static final String API_025_RECEIVED_SHIPPING_HISTORY = "d_ido_snd";
	// API 026
	public static final String API_026_PARAMS_KEY = "d_ido_snd";
	public static final String API_026_SND_TENPO = "snd_tenpo";
	public static final String API_026_RCV_TENPO = "rcv_tenpo";
	public static final String API_026_SND_DATE = "snd_date";
	public static final String API_026_SND_TIME = "snd_time";
	public static final String API_026_HIN_KBN = "hin_kbn";
	public static final String API_026_SND_SU = "snd_su";
	// API 027
	public static final String API_027_RECEIVED_SHOP = "d_ido_deli";
	// API 028
	public static final String API_028_PARAMS_KEY_CODE = "snd_tenpo";
	public static final String API_028_PARAMS_KEY_DATE = "snd_datetime";
	public static final String API_028_RECEIVED_SHOP_DETAIL = "d_ido_deli";
	// API 029
	public static final String API_029_RECEIVED_ACCEPTANCE_HISTORY = "d_ido_rcv";
	// API 030
	public static final String API_030_PARAMS_KEY = "d_ido_rcv";
	public static final String API_030_SND_TENPO = "snd_tenpo";
	public static final String API_030_RCV_TENPO = "rcv_tenpo";
	public static final String API_030_DATA_DATE = "data_date";
	public static final String API_030_DATA_TIME = "data_time";
	public static final String API_030_HIN_KBN = "hin_kbn";
	public static final String API_030_PRE_SU = "pre_rcv_su";
	public static final String API_030_PRE_TANI = "pre_tani";
	public static final String API_030_RCV_SU = "rcv_su";

	// API 031
	public static final String NML_TIME = "nml_time";
	public static final String NOHIN_DATE1 = "nohin_date1";
	public static final String NOHIN_DATE2 = "nohin_date2";
	public static final String NOHIN_DATE3 = "nohin_date3";
	public static final String D_URI_YSK = "d_uri_ysk";
	public static final String D_NOHIN_ZAIKO = "d_nohin_zaiko";
	public static final String URI_YSK = "uri_ysk";
	public static final String NOHIN_SU1 = "nohin_su1";
	public static final String NOHIN_SU2 = "nohin_su2";
	public static final String NOHIN_SU3 = "nohin_su3";

	public static final String API_032_PARAMS_KEY = "scheduled_delivery";
	// API 037
	public static final String D_ZAIKO_NISSU = "d_zaiko_nissu";
	// API 038
	public static final String D_UNMATCH = "d_unmatch";
	// API 039
	public static final String D_LOSS_AMOUNT = "d_loss_amount";

	public static final String COUNTRY = "countries";
	// API 040
	public static final String SHIPPING_WORST = "shipping_worst";
	// API 041
	public static final String ACCEPTANCE_WORST = "acceptance_worst";

	// API 042
	private static final String ANALYZE = "analyze";
	private static final String DATE = "date";
	private static final String ZAIKO = "zaiko";
	private static final String NYUKO = "nyuko";
	// private static final String LOSS = "loss";
	private static final String IDO = "ido";

	// API 043
	public static final String HELP_CODE = "help_cd";
	private static final String CONTENTS = "contents";

	public static String getAPIUrl(String apiPath, Map<String, String> params)
			throws UnsupportedEncodingException {
		StringBuilder url = new StringBuilder();
		url.append(API_HOST_PATH);
		url.append(apiPath);
		if (params != null && params.size() > 0) {
			url.append("?");
			for (Map.Entry<String, String> entry : params.entrySet()) {
				url.append(entry.getKey());
				url.append("=");
				url.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
				url.append("&");
			}
			url.deleteCharAt(url.length() - 1);
		}
		Log.e("URL test", url.toString());
		return url.toString();
	}

	public static boolean valueIsNull(JSONObject json, String key)
			throws JSONException {
		if (json != null && !StringUtils.isEmpty(key)) {
			if (!json.isNull(key)) {
				Object obj = json.get(key);
				if (obj != null && !"null".equals(String.valueOf(obj))) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean hasError(JSONObject json) {
		return !json.isNull(ERROR);
	}

	public static ErrorInfo getFirstError(JSONObject json) throws JSONException {
		ErrorInfo errorInfo = new ErrorInfo();
		if (hasError(json)) {
			// JSONObject error = json.getJSONObject(ERROR);
			// Log.e("json object: ", error.toString());

			JSONArray errorList = json.getJSONArray(ERROR);
			Log.e("Json tra ve loi", errorList.toString());
			if (errorList != null && errorList.length() > 0) {
				JSONObject errorObj = errorList.getJSONObject(0);
				if (errorObj != null && !errorObj.isNull(ERROR_CODE)
						&& !errorObj.isNull(ERROR_MESSAGE)) {
					errorInfo.setCode(errorObj.getString(ERROR_CODE));
					// Ngo chinh sua begin
					String indexStr = "";
					if (!errorObj.isNull("indexes")) {

						JSONArray indexes = errorObj.getJSONArray("indexes");
						List<Integer> indexesList = new ArrayList<Integer>();

						Log.i("Check array", indexes.toString());
						Log.i("Check array length", indexes.length() + "");

						for (int i = 0; i < indexes.length(); i++) {
							indexesList.add(indexes.getInt(i));
							indexStr += (indexes.getInt(i) + 1);
							Log.i("Check array element at " + i,
									indexes.getInt(i) + "");
							if (i != indexes.length() - 1) {
								indexStr += "、";
							}
						}
					}
					// Ngo chinh sua end
					errorInfo.setMessage(indexStr
							+ errorObj.getString(ERROR_MESSAGE).substring(1));

					return errorInfo;
				}
			}
		}
		errorInfo.setMessage("System error");
		return errorInfo;
	}

	public static KPIInfo parseKPIInfo(JSONObject json) throws JSONException {
		if (!hasError(json)) {
			KPIInfo kpiInfo = new KPIInfo();

			if (!JSONUtils.valueIsNull(json, ZAIKONISSU)) {
				kpiInfo.setZaikonissu(json.getDouble(ZAIKONISSU));
			}
			if (!JSONUtils.valueIsNull(json, ZAIKONISSU_PREV)) {
				kpiInfo.setZaikonissuPrev(json.getDouble(ZAIKONISSU_PREV));
			}

			if (!JSONUtils.valueIsNull(json, GENKARITSU)) {
				kpiInfo.setGenkaritsu(json.getInt(GENKARITSU));
			}
			if (!JSONUtils.valueIsNull(json, GENKARITSU_PREV)) {
				kpiInfo.setGenkaritsuPrev(json.getInt(GENKARITSU_PREV));
			}

			if (!JSONUtils.valueIsNull(json, UNMATCH)) {
				kpiInfo.setUnmatch(json.getInt(UNMATCH));
			}
			if (!JSONUtils.valueIsNull(json, UNMATCH_PREV)) {
				kpiInfo.setUnmatchPrev(json.getInt(UNMATCH_PREV));
			}

			if (!JSONUtils.valueIsNull(json, LOSS)) {
				kpiInfo.setLoss(json.getInt(LOSS));
			}
			if (!JSONUtils.valueIsNull(json, LOSS_PREV)) {
				kpiInfo.setLossPrev(json.getInt(LOSS_PREV));
			}

			if (!JSONUtils.valueIsNull(json, RCV)) {
				kpiInfo.setRcv(json.getInt(RCV));
			}
			if (!JSONUtils.valueIsNull(json, RCV_PREV)) {
				kpiInfo.setRcvPrev(json.getInt(RCV_PREV));
			}

			if (!JSONUtils.valueIsNull(json, SND)) {
				kpiInfo.setSnd(json.getInt(SND));
			}
			if (!JSONUtils.valueIsNull(json, SND_PREV)) {
				kpiInfo.setSndPrev(json.getInt(SND_PREV));
			}

			return kpiInfo;
		}
		return null;
	}

	public static KPIRangeInfo parseKPIRangeInfo(JSONObject json)
			throws JSONException {
		if (!hasError(json)) {
			KPIRangeInfo kpiRangeInfo = new KPIRangeInfo();

			if (!JSONUtils.valueIsNull(json, ZAIKONISSU_MAX)) {
				kpiRangeInfo.setZaikonissuMax(json.getDouble(ZAIKONISSU_MAX));
			}
			if (!JSONUtils.valueIsNull(json, ZAIKONISSU_MIN)) {
				kpiRangeInfo.setZaikonissuMin(json.getDouble(ZAIKONISSU_MIN));
			}

			if (!JSONUtils.valueIsNull(json, GENKARITSU_MAX)) {
				kpiRangeInfo.setGenkaritsuMax(json.getInt(GENKARITSU_MAX));
			}
			if (!JSONUtils.valueIsNull(json, GENKARITSU_MIN)) {
				kpiRangeInfo.setGenkaritsuMin(json.getInt(GENKARITSU_MIN));
			}

			if (!JSONUtils.valueIsNull(json, UNMATCH_MAX)) {
				kpiRangeInfo.setUnmatchMax(json.getInt(UNMATCH_MAX));
			}
			if (!JSONUtils.valueIsNull(json, UNMATCH_MIN)) {
				kpiRangeInfo.setUnmatchMin(json.getInt(UNMATCH_MIN));
			}

			if (!JSONUtils.valueIsNull(json, LOSS_MAX)) {
				kpiRangeInfo.setLossMax(json.getInt(LOSS_MAX));
			}
			if (!JSONUtils.valueIsNull(json, LOSS_MIN)) {
				kpiRangeInfo.setLossMin(json.getInt(LOSS_MIN));
			}

			if (!JSONUtils.valueIsNull(json, RCV_MAX)) {
				kpiRangeInfo.setRcvMax(json.getInt(RCV_MAX));
			}
			if (!JSONUtils.valueIsNull(json, RCV_MIN)) {
				kpiRangeInfo.setRcvMin(json.getInt(RCV_MIN));
			}

			if (!JSONUtils.valueIsNull(json, SND_MAX)) {
				kpiRangeInfo.setSndMax(json.getInt(SND_MAX));
			}
			if (!JSONUtils.valueIsNull(json, SND_MIN)) {
				kpiRangeInfo.setSndMin(json.getInt(SND_MIN));
			}

			return kpiRangeInfo;
		}
		return null;
	}

	public static List<MenuPositionInfo> parseMenuList(JSONObject json)
			throws JSONException {
		if (!hasError(json)) {
			JSONArray positionAray = json.getJSONArray(API_002_RESULT_KEY);
			if (positionAray != null && positionAray.length() > 0) {
				List<MenuPositionInfo> listPositionMenu = new ArrayList<MenuPositionInfo>();
				List<MenuInfo> listMenu = null;
				MenuPositionInfo menuPositionInfo = null;
				MenuInfo menuInfo = null;

				JSONObject positionItem = null;
				JSONArray menuArray = null;
				JSONObject menuItem = null;
				for (int i = 0; i < positionAray.length(); i++) {
					positionItem = positionAray.getJSONObject(i);

					menuPositionInfo = new MenuPositionInfo();
					menuPositionInfo.setCode(positionItem.getString(MENU_CODE));
					menuPositionInfo.setX(positionItem.getInt(DISPLAY_X));
					menuPositionInfo.setY(positionItem.getInt(DISPLAY_Y));

					if (!positionItem.isNull(TENPO_TABLET_MENU)) {
						menuArray = positionItem
								.getJSONArray(TENPO_TABLET_MENU);
						if (menuArray != null && menuArray.length() > 0) {
							listMenu = new ArrayList<MenuInfo>();

							for (int j = 0; j < menuArray.length(); j++) {
								menuItem = menuArray.getJSONObject(j);

								menuInfo = new MenuInfo();
								menuInfo.setLabel(menuItem
										.getString(MENU_LABEL));
								menuInfo.setTimeCode(menuItem
										.getString(TIME_CODE));
								menuInfo.setFrom(menuItem
										.getString(BALOON_FROM));
								menuInfo.setTo(menuItem.getString(BALOON_TO));
								menuInfo.setEnabledFrom(menuItem
										.getString(ENABLED_FROM));
								menuInfo.setEnabledTo(menuItem
										.getString(ENABLED_TO));
								menuInfo.setDaily(menuItem.getInt(DAILY));
								menuInfo.setSun(menuItem.getInt(SUN));
								menuInfo.setMon(menuItem.getInt(MON));
								menuInfo.setTue(menuItem.getInt(TUE));
								menuInfo.setWed(menuItem.getInt(WED));
								menuInfo.setThu(menuItem.getInt(THU));
								menuInfo.setFri(menuItem.getInt(FRI));
								menuInfo.setSat(menuItem.getInt(SAT));
								menuInfo.setMonthly(menuItem.getInt(MONTHLY));
								menuInfo.setPriority(menuItem.getInt(PRIORITY));

								listMenu.add(menuInfo);
							}

							menuPositionInfo.setList(listMenu);
						}
					}

					listPositionMenu.add(menuPositionInfo);
				}

				return listPositionMenu;
			}
		}
		return null;
	}

	public static List<DailyCheckboxValue> parseCategoryInfoList(JSONObject json)
			throws JSONException {
		if (!hasError(json)) {
			JSONArray positionAray = json.getJSONArray(API_005_RESULT_KEY);
			if (positionAray != null && positionAray.length() > 0) {
				List<DailyCheckboxValue> categoryInfoList = new ArrayList<DailyCheckboxValue>();
				DailyCheckboxValue categoryItem = null;

				JSONObject jsonCategoryItem = null;
				for (int i = 0; i < positionAray.length(); i++) {
					jsonCategoryItem = positionAray.getJSONObject(i);

					if (!jsonCategoryItem.isNull(SND_HIN_CODE)
							&& !jsonCategoryItem.isNull(SND_HIN_NAME)) {
						categoryItem = new DailyCheckboxValue();
						categoryItem.setCode(jsonCategoryItem
								.getString(SND_HIN_CODE));
						categoryItem.setName(jsonCategoryItem
								.getString(SND_HIN_NAME));
						categoryItem.setSelected(false);
						categoryItem.setCanUnselect(true);

						categoryInfoList.add(categoryItem);
					}
				}

				return categoryInfoList;
			}
		}
		return null;
	}

	public static List<LocationInfo> parseLocationList(JSONObject json)
			throws JSONException {
		if (!hasError(json)) {
			JSONArray positionAray = json.getJSONArray(API_007_PARAMS_KEY);
			if (positionAray != null && positionAray.length() > 0) {
				List<LocationInfo> locationList = new ArrayList<LocationInfo>();
				LocationInfo locationItem = null;

				JSONObject jsonLocationItem = null;
				for (int i = 0; i < positionAray.length(); i++) {
					jsonLocationItem = positionAray.getJSONObject(i);

					if (!jsonLocationItem.isNull(LOCATION_CODE)
							&& !jsonLocationItem.isNull(LOCATION_NAME)) {
						locationItem = new LocationInfo();
						locationItem.setCode(jsonLocationItem
								.getString(LOCATION_CODE));
						locationItem.setName(jsonLocationItem
								.getString(LOCATION_NAME));
						locationItem.setImagePath(jsonLocationItem
								.getString(IMG_FILE_NAME));

						locationList.add(locationItem);
					}
				}

				return locationList;
			}
		}
		return null;
	}

	public static List<ProductInfo> parseProductList(JSONObject json)
			throws JSONException {
		if (!hasError(json)) {
			JSONArray positionAray = json.getJSONArray(API_008_PARAMS_KEY);
			if (positionAray != null && positionAray.length() > 0) {
				List<ProductInfo> productList = new ArrayList<ProductInfo>();
				ProductInfo productItem = null;

				JSONObject jsonLocationItem = null;
				for (int i = 0; i < positionAray.length(); i++) {
					jsonLocationItem = positionAray.getJSONObject(i);
					productItem = new ProductInfo();

					if (!JSONUtils.valueIsNull(jsonLocationItem, HIN_CODE)) {
						productItem.setHinCd(jsonLocationItem
								.getString(HIN_CODE));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, HIN_NAME)) {
						productItem.setHinNm(jsonLocationItem
								.getString(HIN_NAME));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, IMG_FILE_NAME)) {
						productItem.setImageName(jsonLocationItem
								.getString(IMG_FILE_NAME));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, IRI_SU)) {
						productItem
								.setIriSu(jsonLocationItem.getDouble(IRI_SU));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem,
							EXPIRATION_DAYS)) {
						productItem.setExpirationDays(jsonLocationItem
								.getString(EXPIRATION_DAYS));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem,
							EXPIRATION_TEXT)) {
						productItem.setExpirationText(jsonLocationItem
								.getString(EXPIRATION_TEXT));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, NISUGATA_CODE)) {
						productItem.setNisugataCd(jsonLocationItem
								.getString(NISUGATA_CODE));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, NISUGATA_NAME)) {
						productItem.setNisugataNm(jsonLocationItem
								.getString(NISUGATA_NAME));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem,
							DEFAULT_NISUGATA_CODE)) {
						productItem.setDefaultNisugataCd(jsonLocationItem
								.getString(DEFAULT_NISUGATA_CODE));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem,
							DEFAULT_TANI_CODE)) {
						productItem.setDefaultTaniCd(jsonLocationItem
								.getString(DEFAULT_TANI_CODE));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem,
							DEFAULT_TANI_NAME)) {
						productItem.setDefaultTaniNm(jsonLocationItem
								.getString(DEFAULT_TANI_NAME));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem,
							DEFAULT_KSN_KBN)) {
						productItem.setDefaultKsnKbn(jsonLocationItem
								.getString(DEFAULT_KSN_KBN));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem,
							DEFAULT_KSN_CNST)) {
						productItem.setDefaultKsnCnst(jsonLocationItem
								.getDouble(DEFAULT_KSN_CNST));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, TANI_CODE)) {
						productItem.setTaniCd(jsonLocationItem
								.getString(TANI_CODE));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, TANI_NAME)) {
						productItem.setTaniNm(jsonLocationItem
								.getString(TANI_NAME));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem,
							DEFAULT_LOCATION_CODE)) {
						productItem.setDefaultLocationCd(jsonLocationItem
								.getString(DEFAULT_LOCATION_CODE));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem,
							NISUGATA_IMG_FILE_NAME)) {
						productItem.setNisugataImageName(jsonLocationItem
								.getString(NISUGATA_IMG_FILE_NAME));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem,
							THEORETICAL_STOCK)) {
						productItem.setTheoreticalStock(jsonLocationItem
								.getDouble(THEORETICAL_STOCK));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, MEMO)) {
						productItem.setMemo(jsonLocationItem.getString(MEMO));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, KSN_KBN)) {
						productItem.setKsnKbn(jsonLocationItem
								.getString(KSN_KBN));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, KSN_CNST)) {
						productItem.setKsnCnst(jsonLocationItem
								.getDouble(KSN_CNST));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, DSP_ORDER)) {
						productItem.setDspOrder(jsonLocationItem
								.getInt(DSP_ORDER));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, LOCATION_CODE)) {
						productItem.setLocationCd(jsonLocationItem
								.getString(LOCATION_CODE));
						productItem.setOrgLocationCd(productItem
								.getLocationCd());
					}
					productList.add(productItem);
				}

				return productList;
			}
		}
		return null;
	}

	public static List<ProductExpirationInfo> parseProductListExpiration(
			JSONObject json) throws JSONException {
		if (!hasError(json)) {
			JSONArray positionAray = json.getJSONArray("expiration");
			if (positionAray != null && positionAray.length() > 0) {
				List<ProductExpirationInfo> productList = new ArrayList<ProductExpirationInfo>();
				ProductExpirationInfo productItem = null;

				JSONObject jsonLocationItem = null;
				for (int i = 0; i < positionAray.length(); i++) {
					jsonLocationItem = positionAray.getJSONObject(i);
					productItem = new ProductExpirationInfo();

					if (!JSONUtils.valueIsNull(jsonLocationItem, CODE)) {
						productItem.setCode(jsonLocationItem.getInt(CODE));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, NAME)) {
						productItem.setName(jsonLocationItem.getString(NAME));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem,
							"location_code")) {
						productItem.setLocation_code(jsonLocationItem
								.getInt("location_code"));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, "net")) {
						productItem.setNet(jsonLocationItem.getString("net"));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem,
							"expiration_text")) {
						productItem.setExpiration_text(jsonLocationItem
								.getString("expiration_text"));
					}
					if (!JSONUtils.valueIsNull(jsonLocationItem,
							"expiration_date")) {
						productItem.setExpiration_date(jsonLocationItem
								.getInt("expiration_date"));
					}
					productList.add(productItem);
				}
				return productList;
			}
		}
		return null;
	}

	public static ProductExpirationInfo parseProductScheduleDelivery(
			JSONObject json) throws JSONException {
		if (!hasError(json)) {
			ProductExpirationInfo productItem = new ProductExpirationInfo();

			if (!JSONUtils.valueIsNull(json, NML_TIME)) {
				productItem.setNmlTime(json.getString(NML_TIME));
			}

			if (!JSONUtils.valueIsNull(json, NOHIN_DATE1)) {
				productItem.setNohin_day1(json.getString(NOHIN_DATE1));
			}
			if (!JSONUtils.valueIsNull(json, NOHIN_DATE2)) {
				productItem.setNohin_day2(json.getString(NOHIN_DATE2));
			}
			if (!JSONUtils.valueIsNull(json, NOHIN_DATE3)) {
				productItem.setNohin_day3(json.getString(NOHIN_DATE3));
			}

			JSONArray uri_usk_array = json.getJSONArray(D_URI_YSK);

			if (uri_usk_array != null && uri_usk_array.length() > 0) {
				ArrayList<ProductExpirationInfo> productList = new ArrayList<ProductExpirationInfo>();
				ProductExpirationInfo item = null;

				JSONObject jsonLocationItem = null;
				for (int i = 0; i < uri_usk_array.length(); i++) {
					jsonLocationItem = uri_usk_array.getJSONObject(i);
					item = new ProductExpirationInfo();

					if (!JSONUtils.valueIsNull(jsonLocationItem, DATE)) {
						item.setDate(jsonLocationItem.getString(DATE));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, URI_YSK)) {
						item.setUriYsk(jsonLocationItem.getInt(URI_YSK));
					}
					productList.add(item);
				}
				productItem.setDUriYsk(productList);
			}

			JSONArray nohin_zaiko_array = json.getJSONArray(D_NOHIN_ZAIKO);

			if (nohin_zaiko_array != null && nohin_zaiko_array.length() > 0) {
				ArrayList<ProductExpirationInfo> productList = new ArrayList<ProductExpirationInfo>();
				ProductExpirationInfo item = null;

				JSONObject jsonLocationItem = null;
				for (int i = 0; i < nohin_zaiko_array.length(); i++) {
					jsonLocationItem = nohin_zaiko_array.getJSONObject(i);
					item = new ProductExpirationInfo();

					if (!JSONUtils.valueIsNull(jsonLocationItem, HIN_CODE)) {
						item.setCode(jsonLocationItem.getInt(HIN_CODE));
					}
					Log.i("Hin_code", jsonLocationItem.getInt(HIN_CODE) + "");

					if (!JSONUtils.valueIsNull(jsonLocationItem, HIN_NAME)) {
						item.setHinName(jsonLocationItem.getString(HIN_NAME));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, TANI_NAME)) {
						item.setTaniName(jsonLocationItem.getString(TANI_NAME));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, NOHIN_SU1)) {
						item.setNohin_su1(jsonLocationItem.getDouble(NOHIN_SU1));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, NOHIN_SU2)) {
						item.setNohin_su2(jsonLocationItem.getDouble(NOHIN_SU2));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, NOHIN_SU3)) {
						item.setNohin_su3(jsonLocationItem.getDouble(NOHIN_SU3));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, ZAIKO_SU)) {
						item.setZaikosu(jsonLocationItem.getDouble(ZAIKO_SU));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, PER_STOCK)) {
						item.setPer_stock(jsonLocationItem.getDouble(PER_STOCK));
					}

					productList.add(item);
				}

				productItem.setDNohinZaiko(productList);
			}

			return productItem;
		}
		return null;
	}

	public static MeterReadingInfo parseGasAndWaterMeterReadingInfo(
			JSONObject json) throws JSONException {
		if (!hasError(json)) {
			MeterReadingInfo meterInfor = new MeterReadingInfo();
			ArrayList<MeterReadingInfo> arraylistGas = new ArrayList<MeterReadingInfo>();
			ArrayList<MeterReadingInfo> arraylistWater = new ArrayList<MeterReadingInfo>();

			meterInfor.setGas_usage(Double.parseDouble(json
					.getString(GAS_USAGE)));
			meterInfor.setGas_meter_num(json.getString(GAS_METER_NUM));
			meterInfor.setWater_usage(Double.parseDouble(json
					.getString(WATER_USAGE)));
			meterInfor.setWater_meter_num(json.getString(WATER_METER_NUM));

			JSONObject jsonLocationItem = null;
			MeterReadingInfo listElement = null;

			JSONArray listWater = json.getJSONArray(WATER);
			JSONArray listGas = json.getJSONArray(GAS);
			for (int i = 0; i < listGas.length(); i++) {
				jsonLocationItem = (JSONObject) listGas.get(i);
				listElement = new MeterReadingInfo();

				if (!JSONUtils.valueIsNull(jsonLocationItem, EGY_DATE)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",
							java.util.Locale.getDefault());
					Date date = null;
					try {
						date = sdf.parse(jsonLocationItem.getString(EGY_DATE));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					listElement.setEgy_date(date);
				}
				if (!JSONUtils.valueIsNull(jsonLocationItem, AMOUNT)) {
					DecimalFormat df = new DecimalFormat("#0.0");
					String amountStr = df.format(jsonLocationItem
							.getDouble(AMOUNT));
					listElement.setAmount(amountStr);
				}
				if (!JSONUtils.valueIsNull(jsonLocationItem, AMOUNT_USED)) {
					String usage = jsonLocationItem.getString(AMOUNT_USED);
					Log.i("parse usage", usage);
					if (!StringUtils.EMPTY.equals(usage)) {
						listElement.setUsage(Double.parseDouble(usage));

						Log.i("parse usage", Double.parseDouble(usage) + "");
					} else {
						listElement.setUsage(0.0f);
					}
				}
				arraylistGas.add(listElement);
			}
			for (int i = 0; i < listWater.length(); i++) {
				jsonLocationItem = listWater.getJSONObject(i);
				listElement = new MeterReadingInfo();

				if (!JSONUtils.valueIsNull(jsonLocationItem, EGY_DATE)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",
							java.util.Locale.getDefault());
					Date date = null;
					try {
						date = sdf.parse(jsonLocationItem.getString(EGY_DATE));
					} catch (ParseException e) {
						e.printStackTrace();
					}

					listElement.setEgy_date(date);
				}
				if (!JSONUtils.valueIsNull(jsonLocationItem, AMOUNT)) {
					DecimalFormat df = new DecimalFormat("#0.0");
					String amountStr = df.format(jsonLocationItem
							.getDouble(AMOUNT));
					listElement.setAmount(amountStr);
				}
				if (!JSONUtils.valueIsNull(jsonLocationItem, AMOUNT_USED)) {
					listElement.setUsage(jsonLocationItem.getInt(AMOUNT_USED));
					String usage = jsonLocationItem.getString(AMOUNT_USED);
					if (!StringUtils.EMPTY.equals(usage)) {
						listElement.setUsage(Float.parseFloat(usage));
					} else {
						listElement.setUsage(0.0f);
					}
				}
				arraylistWater.add(listElement);
			}

			meterInfor.setGasListMeterInfo(arraylistGas);
			meterInfor.setWaterListMeterInfo(arraylistWater);

			return meterInfor;
		}
		return null;
	}

	public static ArrayList<MeterType> parseListMeterType(JSONObject json)
			throws JSONException {
		if (!hasError(json)) {
			JSONArray positionAray = json.getJSONArray(M_TEMPO_METER);
			if (positionAray != null && positionAray.length() > 0) {
				ArrayList<MeterType> meterList = new ArrayList<MeterType>();
				MeterType metertItem = null;

				JSONObject jsonLocationItem = null;
				for (int i = 0; i < positionAray.length(); i++) {
					jsonLocationItem = positionAray.getJSONObject(i);
					metertItem = new MeterType();

					if (!JSONUtils.valueIsNull(jsonLocationItem, METER_CD)) {
						metertItem.setCode(jsonLocationItem.getInt(METER_CD));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, METER_NM)) {
						metertItem
								.setName(jsonLocationItem.getString(METER_NM));
					}
					meterList.add(metertItem);
				}
				return meterList;
			}
		}
		return null;
	}

	public static MeterReadingInfo parseElectricityReadingInfo(JSONObject json)
			throws JSONException {
		if (!hasError(json)) {
			MeterReadingInfo meterInfor = new MeterReadingInfo();
			ArrayList<MeterReadingInfo> arraylistLastYear = new ArrayList<MeterReadingInfo>();
			ArrayList<MeterReadingInfo> arraylistThisYear = new ArrayList<MeterReadingInfo>();

			meterInfor.setThis_year_meter_num(json
					.getString(THIS_YEAR_METER_NUM));
			meterInfor.setLast_year_meter_num(json
					.getString(LAST_YEAR_METER_NUM));

			JSONObject jsonLocationItem = null;
			MeterReadingInfo listElement = null;
			JSONArray listLastYear = json.getJSONArray(LAST_YEAR);
			JSONArray listThisYear = json.getJSONArray(THIS_YEAR);
			for (int i = 0; i < listLastYear.length(); i++) {
				jsonLocationItem = listLastYear.getJSONObject(i);
				listElement = new MeterReadingInfo();

				if (!JSONUtils.valueIsNull(jsonLocationItem, EGY_DATE)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",
							java.util.Locale.getDefault());
					Date date = null;
					try {
						date = sdf.parse(jsonLocationItem.getString(EGY_DATE));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					listElement.setEgy_date(date);
				}
				if (!JSONUtils.valueIsNull(jsonLocationItem, AMOUNT)) {
					DecimalFormat df = new DecimalFormat("#0.0");
					String amountStr = df.format(jsonLocationItem
							.getDouble(AMOUNT));
					listElement.setAmount(amountStr);
				}
				if (!JSONUtils.valueIsNull(jsonLocationItem, AMOUNT_USED)) {
					String usage = jsonLocationItem.getString(AMOUNT_USED);
					if (!StringUtils.EMPTY.equals(usage)) {
						listElement.setUsage(Float.parseFloat(usage));
					} else {
						listElement.setUsage(0.0f);
					}
				}
				if (!JSONUtils.valueIsNull(jsonLocationItem, CMP_LAST_YEAR)) {
					String ratio = jsonLocationItem.getString(CMP_LAST_YEAR);
					if (!StringUtils.EMPTY.equals(ratio)) {
						listElement.setRatio(Float.parseFloat(ratio));
					} else {
						listElement.setRatio(0.0f);
					}
				}
				arraylistLastYear.add(listElement);
			}
			for (int i = 0; i < listThisYear.length(); i++) {
				jsonLocationItem = listThisYear.getJSONObject(i);
				listElement = new MeterReadingInfo();

				if (!JSONUtils.valueIsNull(jsonLocationItem, EGY_DATE)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",
							java.util.Locale.getDefault());
					Date date = null;
					try {
						date = sdf.parse(jsonLocationItem.getString(EGY_DATE));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					listElement.setEgy_date(date);
				}
				if (!JSONUtils.valueIsNull(jsonLocationItem, AMOUNT)) {
					listElement.setAmount(jsonLocationItem.getString(AMOUNT));
				}
				if (!JSONUtils.valueIsNull(jsonLocationItem, AMOUNT_USED)) {
					String usage = jsonLocationItem.getString(AMOUNT_USED);
					if (!StringUtils.EMPTY.equals(usage)) {
						listElement.setUsage(Float.parseFloat(usage));
					} else {
						listElement.setUsage(0.0f);
					}
				}
				if (!JSONUtils.valueIsNull(jsonLocationItem, CMP_LAST_YEAR)) {
					String ratio = jsonLocationItem.getString(CMP_LAST_YEAR);
					if (!StringUtils.EMPTY.equals(ratio)) {
						listElement.setRatio(Float.parseFloat(ratio));
					} else {
						listElement.setRatio(0.0f);
					}
				}
				arraylistThisYear.add(listElement);
			}

			meterInfor.setElectricityListMeterLastYear(arraylistLastYear);
			meterInfor.setElectricityListMeterThisYear(arraylistThisYear);

			return meterInfor;
		}
		return null;
	}

	public static ArrayList<API037_NumDays> parseApiAPI037Model(
			JSONObject result) throws JSONException {
		if (!hasError(result)) {
			JSONArray jsonArrApi037 = result
					.getJSONArray(JSONUtils.D_ZAIKO_NISSU);
			int size = jsonArrApi037.length();
			if (jsonArrApi037 != null && size > 0) {
				ArrayList<API037_NumDays> listApi037Model = new ArrayList<API037_NumDays>();
				API037_NumDays api037Model = null;
				JSONObject jsonObjApi037 = null;
				for (int i = 0; i < size; i++) {
					jsonObjApi037 = jsonArrApi037.getJSONObject(i);
					api037Model = new API037_NumDays();
					api037Model.setHin_cd(jsonObjApi037.getString("hin_cd"));
					api037Model.setHin_nm(jsonObjApi037.getString("hin_nm"));
					api037Model.setVal(jsonObjApi037.getString("val"));
					System.out.println(api037Model.getVal());
					listApi037Model.add(api037Model);

				}
				return listApi037Model;

			}

		}

		return null;
	}

	public static ArrayList<API038_Unmatched> parseApiAPI038Model(
			JSONObject result) throws JSONException {
		if (!hasError(result)) {
			JSONArray jsonArrApi038 = result.getJSONArray(JSONUtils.D_UNMATCH);
			int size = jsonArrApi038.length();
			if (jsonArrApi038 != null && size > 0) {
				ArrayList<API038_Unmatched> listApi038Model = new ArrayList<API038_Unmatched>();
				API038_Unmatched api038Model = null;
				JSONObject jsonObjApi038 = null;
				for (int i = 0; i < size; i++) {
					jsonObjApi038 = jsonArrApi038.getJSONObject(i);
					api038Model = new API038_Unmatched();
					api038Model.setHin_cd(jsonObjApi038.getString("hin_cd"));
					api038Model.setHin_nm(jsonObjApi038.getString("hin_nm"));
					api038Model.setVal(jsonObjApi038.getInt("val"));
					listApi038Model.add(api038Model);
				}
				return listApi038Model;

			}

		}

		return null;
	}

	public static ArrayList<API039_LossAmount> parseApiAPI039Model(
			JSONObject result) throws JSONException {
		if (!hasError(result)) {
			JSONArray jsonArrApi039 = result
					.getJSONArray(JSONUtils.D_LOSS_AMOUNT);
			int size = jsonArrApi039.length();
			if (jsonArrApi039 != null && size > 0) {
				ArrayList<API039_LossAmount> listApi039Model = new ArrayList<API039_LossAmount>();
				API039_LossAmount api039Model = null;
				JSONObject jsonObjApi039 = null;
				for (int i = 0; i < size; i++) {
					jsonObjApi039 = jsonArrApi039.getJSONObject(i);
					api039Model = new API039_LossAmount();
					api039Model.setHin_cd(jsonObjApi039.getString("hin_cd"));
					api039Model.setHin_nm(jsonObjApi039.getString("hin_nm"));
					api039Model.setAmount(jsonObjApi039.getString("amount"));
					listApi039Model.add(api039Model);
				}
				return listApi039Model;

			}

		}

		return null;
	}

	public static ArrayList<API040_MoveLoad> parseApiAPI040Model(
			JSONObject result, int apiPath) throws JSONException {
		if (!hasError(result)) {
			JSONArray jsonArrApi040 = null;
			if (apiPath == 40) {
				jsonArrApi040 = result.getJSONArray(JSONUtils.SHIPPING_WORST);
			} else {
				jsonArrApi040 = result.getJSONArray(JSONUtils.ACCEPTANCE_WORST);
			}

			int size = jsonArrApi040.length();
			if (jsonArrApi040 != null && size > 0) {
				ArrayList<API040_MoveLoad> listApi040Model = new ArrayList<API040_MoveLoad>();
				JSONObject jsonObjApi040 = null;
				JSONObject jsonObjShop = null;
				List<Tenpo> listTenpo;
				for (int i = 0; i < size; i++) {
					listTenpo = new ArrayList<Tenpo>();
					jsonObjApi040 = jsonArrApi040.getJSONObject(i);
					API040_MoveLoad api040Model = new API040_MoveLoad();
					api040Model.setHin_cd(jsonObjApi040.getString("hin_cd"));
					api040Model.setHin_nm(jsonObjApi040.getString("hin_nm"));
					api040Model.setTotal_count((jsonObjApi040
							.getInt(("total_count"))));
					JSONArray jsonArrTenpo = jsonObjApi040
							.getJSONArray("tenpo");
					int sizeTenpo = jsonArrTenpo.length();
					for (int m = 0; m < sizeTenpo; m++) {
						jsonObjShop = jsonArrTenpo.getJSONObject(m);
						Tenpo tenpo = new Tenpo(
								jsonObjShop.getString("tenpo_nm"),
								jsonObjShop.getInt("count"));
						listTenpo.add(tenpo);
					}
					api040Model.setTenpo(listTenpo);
					listApi040Model.add(api040Model);

				}
				return listApi040Model;

			}

		}

		return null;
	}

	// Zenshou datnt1 03/21/2014 add ++
	// API014
	public static List<API14_ProductCatSel> parseListItemCategory(
			JSONObject json) throws JSONException {
		if (!hasError(json)) {
			JSONArray catSelArray = json.getJSONArray(JSONUtils.ITEM_CATEGORY);
			if (catSelArray != null && catSelArray.length() > 0) {
				List<API14_ProductCatSel> categoryList = new ArrayList<API14_ProductCatSel>();
				API14_ProductCatSel categoryItem = null;

				JSONObject jsonCatItem = null;
				for (int i = 0; i < catSelArray.length(); i++) {
					jsonCatItem = catSelArray.getJSONObject(i);
					categoryItem = new API14_ProductCatSel();

					if (!JSONUtils.valueIsNull(jsonCatItem, "div_cd")) {
						categoryItem.setDivCD(jsonCatItem.getString("div_cd"));
					}

					if (!JSONUtils.valueIsNull(jsonCatItem, "div_nm")) {
						categoryItem
								.setDivName(jsonCatItem.getString("div_nm"));
					}

					categoryList.add(categoryItem);
				}
				return categoryList;
			}
		}
		return null;
	}

	// API015
	public static List<API15_ProductNameSel> parseListItem(JSONObject json)
			throws JSONException {
		if (!hasError(json)) {
			JSONArray nameSelArray = json
					.getJSONArray(JSONUtils.API_015_RECEIVED_CATEGORY_NAME);
			if (nameSelArray != null && nameSelArray.length() > 0) {
				List<API15_ProductNameSel> itemList = new ArrayList<API15_ProductNameSel>();
				API15_ProductNameSel item = null;

				JSONObject jsonLocationItem = null;
				for (int i = 0; i < nameSelArray.length(); i++) {
					jsonLocationItem = nameSelArray.getJSONObject(i);
					item = new API15_ProductNameSel();

					if (!JSONUtils.valueIsNull(jsonLocationItem, "hin_cd")) {
						item.setHinCD(jsonLocationItem.getString("hin_cd"));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, "hin_nm")) {
						item.setHinName(jsonLocationItem.getString("hin_nm"));
					}

					itemList.add(item);
				}
				return itemList;
			}
		}
		return null;
	}

	// API016
	public static List<API16_LossReason> parseListLossReason(JSONObject json)
			throws JSONException {
		if (!hasError(json)) {
			JSONArray lossReasonArray = json
					.getJSONArray(JSONUtils.API_016_RECEIVED_LOSS_REASON);
			if (lossReasonArray != null && lossReasonArray.length() > 0) {
				List<API16_LossReason> lossReasonList = new ArrayList<API16_LossReason>();
				API16_LossReason lossReasonItem = null;

				JSONObject jsonLocationItem = null;
				for (int i = 0; i < lossReasonArray.length(); i++) {
					jsonLocationItem = lossReasonArray.getJSONObject(i);
					lossReasonItem = new API16_LossReason();

					if (!JSONUtils.valueIsNull(jsonLocationItem, "r_cd")) {
						lossReasonItem.setReasonCD(jsonLocationItem
								.getString("r_cd"));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, "loss_r")) {
						lossReasonItem.setLossReason(jsonLocationItem
								.getString("loss_r"));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, "res")) {
						lossReasonItem
								.setRes(jsonLocationItem.getString("res"));
					}

					lossReasonList.add(lossReasonItem);
				}
				return lossReasonList;
			}
		}
		return null;
	}

	// API017
	public static List<API17_QuantityUnit> parseListItemUnit(JSONObject json)
			throws JSONException {
		if (!hasError(json)) {
			JSONArray taniUnitArray = json
					.getJSONArray(JSONUtils.API_017_RECEIVED_UNIT);
			if (taniUnitArray != null && taniUnitArray.length() > 0) {
				List<API17_QuantityUnit> unitList = new ArrayList<API17_QuantityUnit>();
				API17_QuantityUnit unitItem = null;

				JSONObject jsonUnitItem = null;
				for (int i = 0; i < taniUnitArray.length(); i++) {
					jsonUnitItem = taniUnitArray.getJSONObject(i);
					unitItem = new API17_QuantityUnit();

					if (!JSONUtils.valueIsNull(jsonUnitItem, "tani_cd")) {
						unitItem.setTaniCD(jsonUnitItem.getString("tani_cd"));
					}

					if (!JSONUtils.valueIsNull(jsonUnitItem, "tani_nm")) {
						unitItem.setTaniName(jsonUnitItem.getString("tani_nm"));
					}

					unitList.add(unitItem);
				}
				return unitList;
			}
		}
		return null;
	}

	// API018
	public static List<API18_ViewHistory> parseListLossHistory(JSONObject json)
			throws JSONException {
		if (!hasError(json)) {
			JSONArray lossHistoryArray = json
					.getJSONArray(JSONUtils.API_018_RECEIVED_HISTORY);
			if (lossHistoryArray != null && lossHistoryArray.length() > 0) {
				List<API18_ViewHistory> lossHistoryList = new ArrayList<API18_ViewHistory>();
				API18_ViewHistory lossHistoryItem = null;

				JSONObject jsonLossHistoryItem = null;
				for (int i = 0; i < lossHistoryArray.length(); i++) {
					jsonLossHistoryItem = lossHistoryArray.getJSONObject(i);
					lossHistoryItem = new API18_ViewHistory();

					if (!JSONUtils.valueIsNull(jsonLossHistoryItem,
							"submit_date")) {
						lossHistoryItem.setSubmitDate(jsonLossHistoryItem
								.getString("submit_date"));
					}

					if (!JSONUtils.valueIsNull(jsonLossHistoryItem, "hin_nm")) {
						lossHistoryItem.setHinName(jsonLossHistoryItem
								.getString("hin_nm"));
					}

					if (!JSONUtils.valueIsNull(jsonLossHistoryItem, "loss_r")) {
						lossHistoryItem.setLossReason(jsonLossHistoryItem
								.getString("loss_r"));
					}

					if (!JSONUtils.valueIsNull(jsonLossHistoryItem, "loss_su")) {
						lossHistoryItem.setLossSum(jsonLossHistoryItem
								.getString("loss_su"));
					}

					if (!JSONUtils.valueIsNull(jsonLossHistoryItem, "tani_nm")) {
						lossHistoryItem.setTaniName(jsonLossHistoryItem
								.getString("tani_nm"));
					}

					lossHistoryList.add(lossHistoryItem);
				}
				return lossHistoryList;
			}
		}
		return null;
	}

	// API020
	public static List<API20_ShortReason> parseListShortReason(JSONObject json)
			throws JSONException {
		if (!hasError(json)) {
			JSONArray shortReasonArray = json
					.getJSONArray(JSONUtils.API_020_RECEIVED_SHORT_REASON);
			if (shortReasonArray != null && shortReasonArray.length() > 0) {
				List<API20_ShortReason> shortReasonList = new ArrayList<API20_ShortReason>();
				API20_ShortReason shortReasonItem = null;

				JSONObject jsonShortReasonItem = null;
				for (int i = 0; i < shortReasonArray.length(); i++) {
					jsonShortReasonItem = shortReasonArray.getJSONObject(i);
					shortReasonItem = new API20_ShortReason();

					if (!JSONUtils.valueIsNull(jsonShortReasonItem, "r_cd")) {
						shortReasonItem.setReasonCD(jsonShortReasonItem
								.getString("r_cd"));
					}

					if (!JSONUtils.valueIsNull(jsonShortReasonItem, "shrt_r")) {
						shortReasonItem.setShortReason(jsonShortReasonItem
								.getString("shrt_r"));
					}

					shortReasonList.add(shortReasonItem);
				}
				return shortReasonList;
			}
		}
		return null;
	}

	// API021
	public static List<API21_ViewHistory> parseListShortHistory(JSONObject json)
			throws JSONException {
		if (!hasError(json)) {
			JSONArray shortHistoryArray = json
					.getJSONArray(JSONUtils.API_021_RECEIVED_SHORT_HISTORY);
			if (shortHistoryArray != null && shortHistoryArray.length() > 0) {
				List<API21_ViewHistory> shortHistoryList = new ArrayList<API21_ViewHistory>();
				API21_ViewHistory shortHistoryItem = null;

				JSONObject jsonShortHistoryItem = null;
				for (int i = 0; i < shortHistoryArray.length(); i++) {
					jsonShortHistoryItem = shortHistoryArray.getJSONObject(i);
					shortHistoryItem = new API21_ViewHistory();

					if (!JSONUtils.valueIsNull(jsonShortHistoryItem,
							"submit_date")) {
						shortHistoryItem.setSubmitDate(jsonShortHistoryItem
								.getString("submit_date"));
					}

					if (!JSONUtils.valueIsNull(jsonShortHistoryItem, "hin_nm")) {
						shortHistoryItem.setHinName(jsonShortHistoryItem
								.getString("hin_nm"));
					}

					if (!JSONUtils.valueIsNull(jsonShortHistoryItem, "shrt_r")) {
						shortHistoryItem.setShortReason(jsonShortHistoryItem
								.getString("shrt_r"));
					}

					if (!JSONUtils.valueIsNull(jsonShortHistoryItem, "hour")) {
						shortHistoryItem.setHour(jsonShortHistoryItem
								.getString("hour"));
					}

					shortHistoryList.add(shortHistoryItem);
				}
				return shortHistoryList;
			}
		}
		return null;
	}

	// API023 API024
	public static List<Shop> parseListShop(JSONObject json)
			throws JSONException {
		if (!hasError(json)) {
			JSONArray shopArray = json
					.getJSONArray(JSONUtils.API_023_RECEIVED_SHOP_FULL);
			if (shopArray != null && shopArray.length() > 0) {
				List<Shop> shopList = new ArrayList<Shop>();
				Shop shopItem = null;

				JSONObject jsonShopItem = null;
				for (int i = 0; i < shopArray.length(); i++) {
					jsonShopItem = shopArray.getJSONObject(i);
					shopItem = new Shop();

					if (!JSONUtils.valueIsNull(jsonShopItem, "tenpo_cd")) {
						shopItem.setTenpoCD(jsonShopItem.getString("tenpo_cd"));
					}

					if (!JSONUtils.valueIsNull(jsonShopItem, "tenpo_nm")) {
						shopItem.setTenpoName(jsonShopItem
								.getString("tenpo_nm"));
					}

					shopList.add(shopItem);
				}
				return shopList;
			}
		}
		return null;
	}

	// API025
	public static List<API25_ViewHistory> parseListShippingHistory(
			JSONObject json) throws JSONException {
		if (!hasError(json)) {
			JSONArray shippingHistoryArray = json
					.getJSONArray(JSONUtils.API_025_RECEIVED_SHIPPING_HISTORY);
			if (shippingHistoryArray != null
					&& shippingHistoryArray.length() > 0) {
				List<API25_ViewHistory> shippingHistoryList = new ArrayList<API25_ViewHistory>();
				API25_ViewHistory shippingHistoryItem = null;

				JSONObject jsonShippingHistoryItem = null;
				for (int i = 0; i < shippingHistoryArray.length(); i++) {
					jsonShippingHistoryItem = shippingHistoryArray
							.getJSONObject(i);
					shippingHistoryItem = new API25_ViewHistory();

					if (!JSONUtils.valueIsNull(jsonShippingHistoryItem,
							"snd_datetime")) {
						shippingHistoryItem
								.setSendDateTime(jsonShippingHistoryItem
										.getString("snd_datetime"));
					}

					if (!JSONUtils.valueIsNull(jsonShippingHistoryItem,
							"tenpo_cd")) {
						shippingHistoryItem.setTenpoCD(jsonShippingHistoryItem
								.getString("tenpo_cd"));
					}

					if (!JSONUtils.valueIsNull(jsonShippingHistoryItem,
							"tenpo_nm")) {
						shippingHistoryItem
								.setTenpoName(jsonShippingHistoryItem
										.getString("tenpo_nm"));
					}

					if (!JSONUtils.valueIsNull(jsonShippingHistoryItem,
							"hin_nm")) {
						shippingHistoryItem.setHinName(jsonShippingHistoryItem
								.getString("hin_nm"));
					}

					if (!JSONUtils.valueIsNull(jsonShippingHistoryItem,
							"snd_su")) {
						shippingHistoryItem.setSendSum(jsonShippingHistoryItem
								.getString("snd_su"));
					}

					if (!JSONUtils.valueIsNull(jsonShippingHistoryItem,
							"tani_nm")) {
						shippingHistoryItem.setTaniName(jsonShippingHistoryItem
								.getString("tani_nm"));
					}

					shippingHistoryList.add(shippingHistoryItem);
				}
				return shippingHistoryList;
			}
		}
		return null;
	}

	// API027
	public static List<API27_AcceptanceShop> parseListFromShop(JSONObject json)
			throws JSONException {
		if (!hasError(json)) {
			JSONArray fromShopArray = json
					.getJSONArray(JSONUtils.API_027_RECEIVED_SHOP);
			if (fromShopArray != null && fromShopArray.length() > 0) {
				List<API27_AcceptanceShop> fromShopList = new ArrayList<API27_AcceptanceShop>();
				API27_AcceptanceShop fromShopItem = null;

				JSONObject jsonShopItem = null;
				for (int i = 0; i < fromShopArray.length(); i++) {
					jsonShopItem = fromShopArray.getJSONObject(i);
					fromShopItem = new API27_AcceptanceShop();

					if (!JSONUtils.valueIsNull(jsonShopItem, "tenpo_cd")) {
						fromShopItem.setTenpoCD(jsonShopItem
								.getString("tenpo_cd"));
					}

					if (!JSONUtils.valueIsNull(jsonShopItem, "tenpo_nm")) {
						fromShopItem.setTenpoName(jsonShopItem
								.getString("tenpo_nm"));
					}

					if (!JSONUtils.valueIsNull(jsonShopItem, "snd_datetime")) {
						fromShopItem.setSendDateTime(jsonShopItem
								.getString("snd_datetime"));
					}

					fromShopList.add(fromShopItem);
				}
				return fromShopList;
			}
		}
		return null;
	}

	// API028
	public static void parseListRCVItem(JSONObject json,
			List<API28_AcceptanceShop_Detail> rcvList) throws JSONException {
		if (!hasError(json)) {
			JSONArray rcvAray = json
					.getJSONArray(JSONUtils.API_028_RECEIVED_SHOP_DETAIL);

			Log.i("ngo check length json", rcvAray.length() + "");
			S211Activity.arrStrPreRCVSum = new String[rcvAray.length()];
			if (rcvAray != null && rcvAray.length() > 0) {
				API28_AcceptanceShop_Detail rcvItem = null;

				JSONObject jsonRCVItem = null;
				for (int i = 0; i < rcvAray.length(); i++) {
					jsonRCVItem = rcvAray.getJSONObject(i);
					rcvItem = new API28_AcceptanceShop_Detail();

					rcvItem.setCheckedTenpoCD(S211Activity.strCheckedCode);
					rcvItem.setCheckedDate(S211Activity.strCheckedDate);

					if (!JSONUtils.valueIsNull(jsonRCVItem, "hin_cd")) {
						rcvItem.setHinCD(jsonRCVItem.getString("hin_cd"));
					}

					if (!JSONUtils.valueIsNull(jsonRCVItem, "hin_nm")) {
						rcvItem.setHinName(jsonRCVItem.getString("hin_nm"));
					}

					if (!JSONUtils.valueIsNull(jsonRCVItem, "pre_rcv_su")) {
						rcvItem.setPreRCVSum(jsonRCVItem
								.getString("pre_rcv_su"));
						S211Activity.arrStrPreRCVSum[i] = jsonRCVItem
								.getString("pre_rcv_su");
						Log.e("Chu choa test size",
								String.valueOf(rcvList.size()));
					}

					if (!JSONUtils.valueIsNull(jsonRCVItem, "tani_cd")) {
						rcvItem.setTaniCD(jsonRCVItem.getString("tani_cd"));
					}

					if (!JSONUtils.valueIsNull(jsonRCVItem, "tani_nm")) {
						rcvItem.setTaniName(jsonRCVItem.getString("tani_nm"));
					}

					rcvList.add(rcvItem);
				}
			}
		}
	}

	// API029
	public static List<API29_ViewHistory> parseListAcceptanceHistory(
			JSONObject json) throws JSONException {
		if (!hasError(json)) {
			JSONArray acceptanceHistoryArray = json
					.getJSONArray(JSONUtils.API_029_RECEIVED_ACCEPTANCE_HISTORY);
			if (acceptanceHistoryArray != null
					&& acceptanceHistoryArray.length() > 0) {
				List<API29_ViewHistory> acceptanceHistoryList = new ArrayList<API29_ViewHistory>();
				API29_ViewHistory acceptanceHistoryItem = null;

				JSONObject jsonShopItem = null;
				for (int i = 0; i < acceptanceHistoryArray.length(); i++) {
					jsonShopItem = acceptanceHistoryArray.getJSONObject(i);
					acceptanceHistoryItem = new API29_ViewHistory();

					if (!JSONUtils.valueIsNull(jsonShopItem, "data_datetime")) {
						acceptanceHistoryItem.setDataDateTime(jsonShopItem
								.getString("data_datetime"));
					}

					if (!JSONUtils.valueIsNull(jsonShopItem, "tenpo_cd")) {
						acceptanceHistoryItem.setTenpoCD(jsonShopItem
								.getString("tenpo_cd"));
					}

					if (!JSONUtils.valueIsNull(jsonShopItem, "tenpo_nm")) {
						acceptanceHistoryItem.setTenpoName(jsonShopItem
								.getString("tenpo_nm"));
					}

					if (!JSONUtils.valueIsNull(jsonShopItem, "hin_nm")) {
						acceptanceHistoryItem.setHinName(jsonShopItem
								.getString("hin_nm"));
					}

					if (!JSONUtils.valueIsNull(jsonShopItem, "rcv_su")) {
						acceptanceHistoryItem.setRcvSum(jsonShopItem
								.getString("rcv_su"));
					}

					if (!JSONUtils.valueIsNull(jsonShopItem, "tani_nm")) {
						acceptanceHistoryItem.setTaniName(jsonShopItem
								.getString("tani_nm"));
					}

					acceptanceHistoryList.add(acceptanceHistoryItem);
				}
				return acceptanceHistoryList;
			}
		}
		return null;
	}

	// Zenshou datnt1 03/21/2014 add --

	public static List<InventoryAnalysisInfo> parseListInventoryAnalysis(
			JSONObject json) throws JSONException {
		if (!hasError(json)) {
			JSONArray positionAray = json.getJSONArray(ANALYZE);
			Log.e("Json array in JsonUtils", String.valueOf(positionAray));
			if (positionAray != null && positionAray.length() > 0) {
				ArrayList<InventoryAnalysisInfo> invenAnaList = new ArrayList<InventoryAnalysisInfo>();
				InventoryAnalysisInfo invenAnaItem = null;

				JSONObject jsonLocationItem = null;
				for (int i = 0; i < positionAray.length(); i++) {
					jsonLocationItem = positionAray.getJSONObject(i);
					invenAnaItem = new InventoryAnalysisInfo();

					if (!JSONUtils.valueIsNull(jsonLocationItem, DATE)) {
						invenAnaItem.setDate(jsonLocationItem.getString(DATE));
						Log.e("Date format", jsonLocationItem.getString(DATE));
					}

					if (!JSONUtils.valueIsNull(jsonLocationItem, ZAIKO)) {
						invenAnaItem
								.setZaiko(Double.parseDouble(jsonLocationItem
										.getString(ZAIKO)));
					}
					if (!JSONUtils.valueIsNull(jsonLocationItem, NYUKO)) {
						invenAnaItem
								.setNyuko(Double.parseDouble(jsonLocationItem
										.getString(NYUKO)));
					}
					if (!JSONUtils.valueIsNull(jsonLocationItem, LOSS)) {
						invenAnaItem.setLoss(Double
								.parseDouble(jsonLocationItem.getString(LOSS)));
					}
					if (!JSONUtils.valueIsNull(jsonLocationItem, IDO)) {
						invenAnaItem.setIdo(Double.parseDouble(jsonLocationItem
								.getString(IDO)));
					}
					invenAnaList.add(invenAnaItem);
				}
				return invenAnaList;
			}
		}
		return null;
	}

	// vdngo
	public static String partHelpString(JSONObject json) throws JSONException {
		String content = null;
		if (!hasError(json)) {

			if (!JSONUtils.valueIsNull(json, CONTENTS)) {
				content = json.getString(CONTENTS);
			}

			return content;
		}
		return null;
	}
}
