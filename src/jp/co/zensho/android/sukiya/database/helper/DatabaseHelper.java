package jp.co.zensho.android.sukiya.database.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.co.zensho.android.sukiya.bean.ProductInfo;
import jp.co.zensho.android.sukiya.database.model.CategoryInfo;
import jp.co.zensho.android.sukiya.database.model.MenuUpdateHistory;
import jp.co.zensho.android.sukiya.database.model.SukiyaSetting;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "SukiyaDatabase";

	// Table Names
	private static final String TABLE_MENU_UPDATE_HISTORY = "menu_update_history";
	private static final String TABLE_SETTING = "setting";
	private static final String TABLE_CATEGORY_HISTORY = "category_history";
	private static final String TABLE_PRODUCT_INPUT_HISTORY = "product_input_history";
	private static final String TABLE_MENU_INPUT_HISTORY = "menu_input_history";
	// Zenshou ntdat 20140418 add ++
	private static final String TABLE_PRODUCT_INFO = "product_information";
	// Table name
	private static final String STORE_DATE = "store_date";
	private static final String PRODUCT_INFO_ID = "product_info_id";
	private static final String PRODUCT_MENU_KEY = "product_menu_key";
	private static final String PRODUCT_HIN_CD = "product_hin_cd";
	private static final String PRODUCT_HIN_NAME = "product_hin_name";
	private static final String PRODUCT_IMAGE_NAME = "product_image_name";
	private static final String PRODUCT_IRI_SUM = "product_iri_sum";
	private static final String PRODUCT_EXPIRATION_DAY = "product_expiration_day";
	private static final String PRODUCT_EXPIRATION_TEXT = "product_expiration_text";
	private static final String PRODUCT_NISUGATA_CD = "product_nisugata_cd";
	private static final String PRODUCT_NISUGATA_NAME = "product_nisugata_name";
	private static final String PRODUCT_DEFAULT_TANI_CD = "product_default_tani_cd";
	private static final String PRODUCT_DEFAULT_TANI_NAME = "product_default_tani_name";
	private static final String PRODUCT_DEFAULT_KSN_KBN = "product_default_ksn_kbn";
	private static final String PRODUCT_DEFAULT_KSN_CNST = "product_default_ksn_cnst";
	private static final String PRODUCT_TANI_CD = "product_tani_cd";
	private static final String PRODUCT_TANI_NAME = "product_tani_name";
	private static final String PRODUCT_DEFAULT_LOCATION_CD = "product_default_location_cd";
	private static final String PRODUCT_NISUGATA_IMG_NAME = "product_nisugata_img_name";
	private static final String PRODUCT_THEORETICAL_STOCK = "product_theoretical_stock";
	private static final String PRODUCT_ACTUAL_STOCK = "product_actual_stock";
	private static final String PRODUCT_UNIT_STOCK = "product_unit_stock";
	private static final String PRODUCT_MEMO = "product_memo";
	private static final String PRODUCT_KSN_KBN = "product_ksn_kbn";
	private static final String PRODUCT_KSN_CNST = "product_ksn_cnst";
	private static final String PRODUCT_DSP_ORDER = "product_dsp_order";
	private static final String PRODUCT_ORG_LOCATION_CD = "product_org_location_cd";
	private static final String PRODUCT_LOCATION_CD = "product_location_cd";
	private static final String PRODUCT_DEFAULT_NISUGATA_CD = "product_default_nisugata_cd";
	private static final String PRODUCT_YEAR = "product_year";
	private static final String PRODUCT_MONTH = "product_month";
	private static final String PRODUCT_DAY = "product_day";
	private static final String PRODUCT_USER_ADD = "product_user_add";
	// Zenshou ntdat 20140418 add --

	// vdngo begin
	private static final String TABLE_INPUT = "table_input";
	private static final String QUANTITY_INPUT = "quantity";
	private static final String HIN_CD = "hin_cd";
	private static final String MENU_CODE = "menu_code";
	private static final String POSITION_X = "position_x";
	private static final String POSITION_Y = "position_y";
	private static final String TIME_CODE = "time_code";
	private static final String PRIORITY = "priority";
	private static final String LAST_UPDATE = "last_update";
	// vdngo end
	// Common column names
	private static final String KEY_ID = "id";

	// menu_update_history Table - column names
	private static final String KEY_LAST_UPDATE = "last_update";

	// setting Table - column names
	private static final String KEY_SHOP_CODE = "shop_code";
	private static final String KEY_SHOP_NAME = "shop_name";
	private static final String KEY_DOWNLOAD_ZIP_STATE = "download_zip_state";

	// product input history Table - column names
	private static final String MENU_POS = "menu_pos";
	private static final String CLEARANCE = "clearance";
	private static final String DATE = "date";
	private static final String START_TIME = "start_time";
	private static final String END_TIME = "end_time";
	private static final String PRODUCT_ID = "product_id";
	private static final String LOCATION = "location";
	private static final String NISUGATA_CD = "nisugata_cd";
	private static final String NUMBER_VAL = "num_val";
	private static final String DATE_VAL = "date_val";
	private static final String SUBMITED = "submited";

	// Table Create Statements
	// menu_update_history table create statement
	private static final String CREATE_TABLE_MENU_UPDATE_HISTORY = "CREATE TABLE "
			+ TABLE_MENU_UPDATE_HISTORY
			+ "("
			+ KEY_ID
			+ " VARCHAR(10) PRIMARY KEY,"
			+ KEY_LAST_UPDATE
			+ " VARCHAR(50)"
			+ ")";

	// vdngo begin
	// Table Create Statements
	// menu_update_history table create statement
	private static final String CREATE_TABLE_INPUT = "CREATE TABLE "
			+ TABLE_INPUT + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + HIN_CD
			+ " VARCHAR(10), " + MENU_CODE + " VARCHAR(10)," + POSITION_X
			+ " INTEGER," + POSITION_Y + " INTEGER," + TIME_CODE
			+ " VARCHAR(10)," + PRIORITY + " INTEGER," + QUANTITY_INPUT
			+ " double, " + LAST_UPDATE + "datetime default current_timestamp"
			+ ")";
	// vdngo end
	// setting table create statement
	private static final String CREATE_TABLE_SETTING = "CREATE TABLE "
			+ TABLE_SETTING + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_SHOP_CODE + " VARCHAR(10)," + KEY_SHOP_NAME + " VARCHAR(50),"
			+ KEY_DOWNLOAD_ZIP_STATE + " INTEGER" + ")";

	// CategoryHistory table create statement
	private static final String CREATE_TABLE_CATEGORY_HISTORY = "CREATE TABLE "
			+ TABLE_CATEGORY_HISTORY + "(" + KEY_ID
			+ " VARCHAR(5) PRIMARY KEY," + KEY_LAST_UPDATE + " VARCHAR(8)"
			+ ")";
	// CategoryHistory table create statement
	private static final String CREATE_TABLE_PRODUCT_INPUT_HISTORY = "CREATE TABLE "
			+ TABLE_PRODUCT_INPUT_HISTORY
			+ "("
			+ KEY_ID
			+ " INTEGER, "
			+ PRODUCT_ID
			+ " VARCHAR(15), "
			+ LOCATION
			+ " VARCHAR(2), "
			+ NISUGATA_CD
			+ " VARCHAR(2), "
			+ NUMBER_VAL
			+ " VARCHAR(6), "
			+ DATE_VAL
			+ " VARCHAR(6), "
			+ "PRIMARY KEY ("
			+ KEY_ID
			+ ", "
			+ PRODUCT_ID + ", " + LOCATION + ", " + NISUGATA_CD + ") " + ")";
	// Menu input history table create statement
	private static final String CREATE_TABLE_MENU_INPUT_HISTORY = "CREATE TABLE "
			+ TABLE_MENU_INPUT_HISTORY
			+ "("
			+ KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ MENU_POS
			+ " VARCHAR(4), "
			+ CLEARANCE
			+ " VARCHAR(2), "
			+ TIME_CODE
			+ " VARCHAR(1), "
			+ PRIORITY
			+ " INTEGER, "
			+ DATE
			+ " VARCHAR(8), "
			+ START_TIME
			+ " VARCHAR(4), "
			+ END_TIME
			+ " VARCHAR(4), " + SUBMITED + " INTEGER " + ")";

	// Zenshou ntdat 20140418 add ++
	// Menu input history table create statement
	private static final String CREATE_TABLE_PRODUCT_INFO = "CREATE TABLE "
			+ TABLE_PRODUCT_INFO + "(" + PRODUCT_INFO_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + STORE_DATE
			+ " VARCHAR(10), " + PRODUCT_MENU_KEY + " VARCHAR(2), "
			+ START_TIME + " VARCHAR(4), " + END_TIME + " VARCHAR(4), "
			+ PRODUCT_HIN_CD + " VARCHAR(15), " + PRODUCT_HIN_NAME
			+ " VARCHAR(155), " + PRODUCT_IMAGE_NAME + " VARCHAR(20), "
			+ PRODUCT_IRI_SUM + " DOUBLE, " + PRODUCT_EXPIRATION_DAY
			+ " VARCHAR(8), " + PRODUCT_EXPIRATION_TEXT + " VARCHAR(4), "
			+ PRODUCT_NISUGATA_CD + " VARCHAR(4), " + PRODUCT_NISUGATA_NAME
			+ " VARCHAR(20), " + PRODUCT_DEFAULT_TANI_CD + " VARCHAR(20), "
			+ PRODUCT_DEFAULT_TANI_NAME + " VARCHAR(20), "
			+ PRODUCT_DEFAULT_KSN_KBN + " VARCHAR(20), "
			+ PRODUCT_DEFAULT_KSN_CNST + " DOUBLE, " + PRODUCT_TANI_CD
			+ " VARCHAR(20), " + PRODUCT_TANI_NAME + " VARCHAR(20), "
			+ PRODUCT_DEFAULT_LOCATION_CD + " VARCHAR(20), "
			+ PRODUCT_NISUGATA_IMG_NAME + " VARCHAR(20), "
			+ PRODUCT_THEORETICAL_STOCK + " DOUBLE, " + PRODUCT_ACTUAL_STOCK
			+ " DOUBLE, " + PRODUCT_UNIT_STOCK + " DOUBLE, " + PRODUCT_MEMO
			+ " VARCHAR(20), " + PRODUCT_KSN_KBN + " VARCHAR(20), "
			+ PRODUCT_KSN_CNST + " DOUBLE, " + PRODUCT_DSP_ORDER + " INTEGER, "
			+ PRODUCT_ORG_LOCATION_CD + " VARCHAR(20), " + PRODUCT_LOCATION_CD
			+ " VARCHAR(20), " + PRODUCT_DEFAULT_NISUGATA_CD + " VARCHAR(20), "
			+ PRODUCT_YEAR + " VARCHAR(20), " + PRODUCT_MONTH
			+ " VARCHAR(20), " + PRODUCT_DAY + " VARCHAR(20), "
			+ PRODUCT_USER_ADD + " INTEGER " + ")";

	// Zenshou ntdat 20140418 add --

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d("DatabaseHelper", "DatabaseHelper - End");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		db.execSQL(CREATE_TABLE_MENU_UPDATE_HISTORY);
		db.execSQL(CREATE_TABLE_SETTING);
		db.execSQL(CREATE_TABLE_CATEGORY_HISTORY);
		db.execSQL(CREATE_TABLE_PRODUCT_INPUT_HISTORY);
		db.execSQL(CREATE_TABLE_MENU_INPUT_HISTORY);
		// Zenshou ntdat 20140418 add ++
		db.execSQL(CREATE_TABLE_PRODUCT_INFO);
		// Zenshou ntdat 20140418 add --
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU_UPDATE_HISTORY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTING);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_CATEGORY_HISTORY);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_INPUT);
		// Zenshou ntdat 20140418 add ++
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_PRODUCT_INFO);
		// Zenshou ntdat 20140418 add --
		// create new tables
		onCreate(db);
	}

	/*
	 * Creating a menu_update_history
	 */
	public void createMenuHistory(MenuUpdateHistory menuUpdateHistory) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, menuUpdateHistory.getId());
		values.put(KEY_LAST_UPDATE, menuUpdateHistory.getLastUpdate());

		// insert row
		db.insert(TABLE_MENU_UPDATE_HISTORY, null, values);
	}

	public String getLastupdateMenu(String menuId) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_MENU_UPDATE_HISTORY
				+ " WHERE " + KEY_ID + " = '" + menuId + "'";

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null && c.moveToFirst()) {
			return c.getString(c.getColumnIndex(KEY_LAST_UPDATE));
		} else {
			return null;
		}
	}

	/*
	 * get single menu_update_history
	 */
	public MenuUpdateHistory getMenuHistory(int menuId) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_MENU_UPDATE_HISTORY
				+ " WHERE " + KEY_ID + " = " + menuId;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		MenuUpdateHistory history = new MenuUpdateHistory();
		history.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		history.setLastUpdate(c.getString(c.getColumnIndex(KEY_LAST_UPDATE)));

		return history;
	}

	/*
	 * getting all menu_update_history
	 */
	public List<MenuUpdateHistory> getAllMenuHistory() {
		List<MenuUpdateHistory> todos = new ArrayList<MenuUpdateHistory>();
		String selectQuery = "SELECT  * FROM " + TABLE_MENU_UPDATE_HISTORY;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			MenuUpdateHistory td;
			do {
				td = new MenuUpdateHistory();
				td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				td.setLastUpdate((c.getString(c.getColumnIndex(KEY_LAST_UPDATE))));

				// adding to menu_update_history list
				todos.add(td);
			} while (c.moveToNext());
		}

		return todos;
	}

	/*
	 * Updating a menu_update_history
	 */
	public int updateMenuHistory(MenuUpdateHistory history) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_LAST_UPDATE, history.getLastUpdate());

		// updating row
		return db.update(TABLE_MENU_UPDATE_HISTORY, values, KEY_ID + " = ?",
				new String[] { String.valueOf(history.getId()) });
	}

	// Zenshou ntdat 20140418 add ++
	public boolean isStoredProduct(Date currentDate, String menuCode,
			String startTime, String endTime) {
		if (currentDate == null || menuCode == null || startTime == null
				|| endTime == null) {
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.JAPAN);
		String strCurrentDate = sdf.format(currentDate);
		String selectQuery = "SELECT  count(*) " + "FROM " + TABLE_PRODUCT_INFO
				+ " " + "WHERE " + STORE_DATE + " = '" + strCurrentDate + "' "
				+ "AND " + PRODUCT_MENU_KEY + " = '" + menuCode + "' " + "AND "
				+ START_TIME + " = '" + startTime + "' " + "AND " + END_TIME
				+ " = '" + endTime + "' ";

		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteStatement statement = db.compileStatement(selectQuery);
		long count = statement.simpleQueryForLong();
		if (count > 0) {
			return true;
		}
		return false;
	}

	public void removeOldProduct(Date currentDate) {
		if (currentDate == null) {
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.JAPAN);
		String strCurrentDate = sdf.format(currentDate);

		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PRODUCT_INFO, STORE_DATE + " < ?",
				new String[] { String.valueOf(strCurrentDate) });
	}

	public List<ProductInfo> getProductInfo(Date currentDate, String menuCode,
			String startTime, String endTime) {
		List<ProductInfo> products = new ArrayList<ProductInfo>();
		if (currentDate == null || menuCode == null || startTime == null
				|| endTime == null) {
			return products;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.JAPAN);
		String strCurrentDate = sdf.format(currentDate);
		String selectQuery = "SELECT  * " + "FROM " + TABLE_PRODUCT_INFO + " "
				+ "WHERE " + STORE_DATE + " = '" + strCurrentDate + "' "
				+ "AND " + PRODUCT_MENU_KEY + " = '" + menuCode + "' " + "AND "
				+ START_TIME + " = '" + startTime + "' " + "AND " + END_TIME
				+ " = '" + endTime + "' ";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			ProductInfo product;
			do {
				product = new ProductInfo();
				product.setKey(c.getLong(c.getColumnIndex(PRODUCT_INFO_ID)));
				product.setMenuType(c.getString(c
						.getColumnIndex(PRODUCT_MENU_KEY)));
				product.setHinCd(c.getString(c.getColumnIndex(PRODUCT_HIN_CD)));
				product.setHinNm(c.getString(c.getColumnIndex(PRODUCT_HIN_NAME)));
				product.setImageName(c.getString(c
						.getColumnIndex(PRODUCT_IMAGE_NAME)));
				product.setIriSu(c.getDouble(c.getColumnIndex(PRODUCT_IRI_SUM)));
				product.setExpirationDays(c.getString(c
						.getColumnIndex(PRODUCT_EXPIRATION_DAY)));
				product.setExpirationText(c.getString(c
						.getColumnIndex(PRODUCT_EXPIRATION_TEXT)));
				product.setNisugataCd(c.getString(c
						.getColumnIndex(PRODUCT_NISUGATA_CD)));
				product.setNisugataNm(c.getString(c
						.getColumnIndex(PRODUCT_NISUGATA_NAME)));
				product.setDefaultTaniCd(c.getString(c
						.getColumnIndex(PRODUCT_DEFAULT_TANI_CD)));
				product.setDefaultTaniNm(c.getString(c
						.getColumnIndex(PRODUCT_DEFAULT_TANI_NAME)));
				product.setDefaultKsnKbn(c.getString(c
						.getColumnIndex(PRODUCT_DEFAULT_KSN_KBN)));
				product.setDefaultKsnCnst(c.getDouble(c
						.getColumnIndex(PRODUCT_DEFAULT_KSN_CNST)));
				product.setTaniCd(c.getString(c.getColumnIndex(PRODUCT_TANI_CD)));
				product.setTaniNm(c.getString(c
						.getColumnIndex(PRODUCT_TANI_NAME)));
				product.setDefaultLocationCd(c.getString(c
						.getColumnIndex(PRODUCT_DEFAULT_LOCATION_CD)));
				product.setNisugataImageName(c.getString(c
						.getColumnIndex(PRODUCT_NISUGATA_IMG_NAME)));
				product.setTheoreticalStock(c.getDouble(c
						.getColumnIndex(PRODUCT_THEORETICAL_STOCK)));
				if (!c.isNull(c.getColumnIndex(PRODUCT_ACTUAL_STOCK))) {
					product.setActualStock(c.getDouble(c
							.getColumnIndex(PRODUCT_ACTUAL_STOCK)));
				}
				product.setUnitStock(c.getDouble(c
						.getColumnIndex(PRODUCT_UNIT_STOCK)));
				product.setMemo(c.getString(c.getColumnIndex(PRODUCT_MEMO)));
				product.setKsnKbn(c.getString(c.getColumnIndex(PRODUCT_KSN_KBN)));
				product.setKsnCnst(c.getDouble(c
						.getColumnIndex(PRODUCT_KSN_CNST)));
				product.setDspOrder(c.getInt(c
						.getColumnIndex(PRODUCT_DSP_ORDER)));
				product.setOrgLocationCd(c.getString(c
						.getColumnIndex(PRODUCT_ORG_LOCATION_CD)));
				if (!c.isNull(c.getColumnIndex(PRODUCT_LOCATION_CD))) {
					product.setLocationCd(c.getString(c
							.getColumnIndex(PRODUCT_LOCATION_CD)));
				}
				product.setDefaultNisugataCd(c.getString(c
						.getColumnIndex(PRODUCT_DEFAULT_NISUGATA_CD)));
				product.setYear(c.getString(c.getColumnIndex(PRODUCT_YEAR)));
				product.setMonth(c.getString(c.getColumnIndex(PRODUCT_MONTH)));
				product.setDay(c.getString(c.getColumnIndex(PRODUCT_DAY)));
				product.setUserAdd(c.getInt(c.getColumnIndex(PRODUCT_USER_ADD)));

				// adding to product info list
				products.add(product);
			} while (c.moveToNext());
		}

		return products;
	}

	public void addProductInfo(ProductInfo productInfo, Date currentDate,
			String menuCode, String startTime, String endTime) {
		if (currentDate == null || menuCode == null || startTime == null
				|| endTime == null) {
			return;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.JAPAN);
		String strCurrentDate = sdf.format(currentDate);

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(STORE_DATE, strCurrentDate);
		values.put(PRODUCT_MENU_KEY, menuCode);
		values.put(START_TIME, startTime);
		values.put(END_TIME, endTime);
		values.put(STORE_DATE, strCurrentDate);
		values.put(PRODUCT_HIN_CD, productInfo.getHinCd());
		values.put(PRODUCT_HIN_NAME, productInfo.getHinNm());
		values.put(PRODUCT_IMAGE_NAME, productInfo.getImageName());
		values.put(PRODUCT_IRI_SUM, productInfo.getIriSu());
		values.put(PRODUCT_EXPIRATION_DAY, productInfo.getExpirationDays());
		values.put(PRODUCT_EXPIRATION_TEXT, productInfo.getExpirationText());
		values.put(PRODUCT_NISUGATA_CD, productInfo.getNisugataCd());
		values.put(PRODUCT_NISUGATA_NAME, productInfo.getNisugataNm());
		values.put(PRODUCT_DEFAULT_TANI_CD, productInfo.getDefaultTaniCd());
		values.put(PRODUCT_DEFAULT_TANI_NAME, productInfo.getDefaultTaniNm());
		values.put(PRODUCT_DEFAULT_KSN_KBN, productInfo.getDefaultKsnKbn());
		values.put(PRODUCT_DEFAULT_KSN_CNST, productInfo.getDefaultKsnCnst());
		values.put(PRODUCT_TANI_CD, productInfo.getTaniCd());
		values.put(PRODUCT_TANI_NAME, productInfo.getTaniNm());
		values.put(PRODUCT_DEFAULT_LOCATION_CD,
				productInfo.getDefaultLocationCd());
		values.put(PRODUCT_NISUGATA_IMG_NAME,
				productInfo.getNisugataImageName());
		values.put(PRODUCT_THEORETICAL_STOCK, productInfo.getTheoreticalStock());
		values.put(PRODUCT_ACTUAL_STOCK, productInfo.getActualStock());
		values.put(PRODUCT_UNIT_STOCK, productInfo.getUnitStock());
		values.put(PRODUCT_MEMO, productInfo.getMemo());
		values.put(PRODUCT_KSN_KBN, productInfo.getKsnKbn());
		values.put(PRODUCT_KSN_CNST, productInfo.getKsnCnst());
		values.put(PRODUCT_DSP_ORDER, productInfo.getDspOrder());
		values.put(PRODUCT_ORG_LOCATION_CD, productInfo.getOrgLocationCd());
		values.put(PRODUCT_LOCATION_CD, productInfo.getLocationCd());
		values.put(PRODUCT_DEFAULT_NISUGATA_CD,
				productInfo.getDefaultNisugataCd());
		values.put(PRODUCT_YEAR, productInfo.getYear());
		values.put(PRODUCT_MONTH, productInfo.getMonth());
		values.put(PRODUCT_DAY, productInfo.getDay());
		values.put(PRODUCT_USER_ADD, productInfo.getUserAdd());

		// insert row
		long id = db.insert(TABLE_PRODUCT_INFO, null, values);
		productInfo.setKey(id);
	}

	// Delete fields
	public void deleteProductInfo(long key) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PRODUCT_INFO, PRODUCT_INFO_ID + " = ?",
				new String[] { String.valueOf(key) });
	}
	
	public void updateProductInfo(long key, ProductInfo productInfo, Date currentDate,
			String menuCode, String startTime, String endTime) {
		if (currentDate == null || menuCode == null || startTime == null
				|| endTime == null) {
			return;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.JAPAN);
		String strCurrentDate = sdf.format(currentDate);
		
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(STORE_DATE, strCurrentDate);
		values.put(PRODUCT_MENU_KEY, menuCode);
		values.put(START_TIME, startTime);
		values.put(END_TIME, endTime);
		values.put(STORE_DATE, strCurrentDate);
		values.put(PRODUCT_HIN_CD, productInfo.getHinCd());
		values.put(PRODUCT_HIN_NAME, productInfo.getHinNm());
		values.put(PRODUCT_IMAGE_NAME, productInfo.getImageName());
		values.put(PRODUCT_IRI_SUM, productInfo.getIriSu());
		values.put(PRODUCT_EXPIRATION_DAY, productInfo.getExpirationDays());
		values.put(PRODUCT_EXPIRATION_TEXT, productInfo.getExpirationText());
		values.put(PRODUCT_NISUGATA_CD, productInfo.getNisugataCd());
		values.put(PRODUCT_NISUGATA_NAME, productInfo.getNisugataNm());
		values.put(PRODUCT_DEFAULT_TANI_CD, productInfo.getDefaultTaniCd());
		values.put(PRODUCT_DEFAULT_TANI_NAME, productInfo.getDefaultTaniNm());
		values.put(PRODUCT_DEFAULT_KSN_KBN, productInfo.getDefaultKsnKbn());
		values.put(PRODUCT_DEFAULT_KSN_CNST, productInfo.getDefaultKsnCnst());
		values.put(PRODUCT_TANI_CD, productInfo.getTaniCd());
		values.put(PRODUCT_TANI_NAME, productInfo.getTaniNm());
		values.put(PRODUCT_DEFAULT_LOCATION_CD,
				productInfo.getDefaultLocationCd());
		values.put(PRODUCT_NISUGATA_IMG_NAME,
				productInfo.getNisugataImageName());
		values.put(PRODUCT_THEORETICAL_STOCK, productInfo.getTheoreticalStock());
		values.put(PRODUCT_ACTUAL_STOCK, productInfo.getActualStock());
		values.put(PRODUCT_UNIT_STOCK, productInfo.getUnitStock());
		values.put(PRODUCT_MEMO, productInfo.getMemo());
		values.put(PRODUCT_KSN_KBN, productInfo.getKsnKbn());
		values.put(PRODUCT_KSN_CNST, productInfo.getKsnCnst());
		values.put(PRODUCT_DSP_ORDER, productInfo.getDspOrder());
		values.put(PRODUCT_ORG_LOCATION_CD, productInfo.getOrgLocationCd());
		values.put(PRODUCT_LOCATION_CD, productInfo.getLocationCd());
		values.put(PRODUCT_DEFAULT_NISUGATA_CD,
				productInfo.getDefaultNisugataCd());
		values.put(PRODUCT_YEAR, productInfo.getYear());
		values.put(PRODUCT_MONTH, productInfo.getMonth());
		values.put(PRODUCT_DAY, productInfo.getDay());
		values.put(PRODUCT_USER_ADD, productInfo.getUserAdd());

		// updating row
		db.update(TABLE_PRODUCT_INFO, values, PRODUCT_INFO_ID + " = ?",
				new String[] { String.valueOf(key) });
	}

	// Zenshou ntdat 20140418 add --

	// // vdngo begin
	// /*
	// * Add a menu_update_product
	// */
	// public void addInputProduct(ProductInfo product,
	// MenuPositionInfo selectedMenu, Double quantity) {
	// insertProduct(product.getHinCd(), selectedMenu, quantity);
	//
	// }
	//
	// public long insertProduct(String hin_cd, MenuPositionInfo selectedMenu,
	// Double quantity) {
	// SQLiteDatabase db = this.getWritableDatabase();
	// // SimpleDateFormat dateFormat = new
	// // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// // Date date = new Date();
	//
	// ContentValues initialValues = new ContentValues();
	//
	// // convert date to string
	// // String dateString = dateFormat.format(date);
	// initialValues.put(HIN_CD, hin_cd);
	// initialValues.put(MENU_CODE, selectedMenu.getCode());
	// initialValues.put(POSITION_X, selectedMenu.getX());
	// initialValues.put(POSITION_Y, selectedMenu.getY());
	// initialValues.put(TIME_CODE, selectedMenu.getCurrentDisplayInfo()
	// .getTimeCode());
	// initialValues.put(PRIORITY, selectedMenu.getCurrentDisplayInfo()
	// .getPriority());
	// initialValues.put(QUANTITY_INPUT, quantity);
	//
	// return db.insert(TABLE_INPUT, null, initialValues);
	//
	// }
	//
	// public List<ProductInfo> getListProductInfo(MenuPositionInfo
	// selectedMenu) {
	// SQLiteDatabase db = this.getReadableDatabase();
	// List<ProductInfo> productList = new ArrayList<ProductInfo>();
	// String selectQuery = "SELECT  * FROM " + TABLE_INPUT + " WHERE "
	// + MENU_CODE + " = " + selectedMenu.getCode() + " AND "
	// + POSITION_X + " = " + selectedMenu.getX() + " AND "
	// + POSITION_Y + " = " + selectedMenu.getY() + " AND "
	// + TIME_CODE + " = "
	// + selectedMenu.getCurrentDisplayInfo().getTimeCode() + " AND "
	// + PRIORITY + " = "
	// + selectedMenu.getCurrentDisplayInfo().getPriority();
	//
	// Log.e(LOG, selectQuery);
	//
	// Cursor c = db.rawQuery(selectQuery, null);
	//
	// if (c != null)
	// c.moveToFirst();
	// if (c.moveToFirst()) {
	//
	// ProductInfo product;
	// do {
	// // if (selectedPosition == c.getString(c
	// // .getColumnIndex(SELECTED_POS))) {
	// product = new ProductInfo();
	// product.setHinCd(c.getString(c.getColumnIndex(KEY_ID)));
	// product.setQuantity(Double.parseDouble(c.getString(c
	// .getColumnIndex(QUANTITY_INPUT))));
	// productList.add(product);
	// Log.i("Product",
	// product.getHinCd() + "   " + product.getHinNm());
	// // }
	// } while (c.moveToNext());
	// }
	// return productList;
	// }
	//
	// public ProductInfo getProductInfo(String hincd,
	// MenuPositionInfo selectedMenu) {
	// SQLiteDatabase db = this.getReadableDatabase();
	//
	// String selectQuery = "SELECT  * FROM " + TABLE_INPUT + " WHERE "
	// + HIN_CD + " = " + hincd + " AND " + MENU_CODE + " = "
	// + selectedMenu.getCode() + " AND " + POSITION_X + " = "
	// + selectedMenu.getX() + " AND " + POSITION_Y + " = "
	// + selectedMenu.getY() + " AND " + TIME_CODE + " = "
	// + selectedMenu.getCurrentDisplayInfo().getTimeCode() + " AND "
	// + PRIORITY + " = "
	// + selectedMenu.getCurrentDisplayInfo().getPriority();
	//
	// Log.e(LOG, selectQuery);
	//
	// Cursor c = db.rawQuery(selectQuery, null);
	// ProductInfo pro = null;
	// if (c.moveToFirst()) {
	//
	// pro = new ProductInfo();
	// pro.setHinCd(c.getString(c.getColumnIndex(HIN_CD)));
	// pro.setQuantity(Double.parseDouble(c.getString(c
	// .getColumnIndex(QUANTITY_INPUT))));
	// }
	// return pro;
	// }
	//
	// // vdngo end

	public void updateMenuHistory(String menuId, String lastUpdate) {
		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase dbWrite = this.getWritableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_MENU_UPDATE_HISTORY
				+ " WHERE " + KEY_ID + " = '" + menuId + "'";

		Cursor c = db.rawQuery(selectQuery, null);
		String megerSQL = null;
		if (c != null && c.moveToFirst()) {
			megerSQL = "UPDATE " + TABLE_MENU_UPDATE_HISTORY + " SET "
					+ KEY_LAST_UPDATE + " = '" + lastUpdate + "' WHERE "
					+ KEY_ID + " = '" + menuId + "'";
		} else {
			megerSQL = "INSERT INTO " + TABLE_MENU_UPDATE_HISTORY + "("
					+ KEY_ID + ", " + KEY_LAST_UPDATE + ") VALUES ('" + menuId
					+ "', '" + lastUpdate + "')";
		}
		Log.d(LOG, megerSQL);
		dbWrite.execSQL(megerSQL);
	}

	/*
	 * Deleting a menu_update_history
	 */
	public void deleteToDo(int menuId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MENU_UPDATE_HISTORY, KEY_ID + " = ?",
				new String[] { String.valueOf(menuId) });
	}

	/*
	 * Creating a setting
	 */
	public void createSetting(SukiyaSetting setting) {
		Log.d(LOG, "createSetting - Start");
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, 1);
		values.put(KEY_SHOP_CODE, setting.getShopCode());
		values.put(KEY_SHOP_NAME, setting.getShopName());
		values.put(KEY_DOWNLOAD_ZIP_STATE, setting.getDownloadZipState());

		// insert row
		db.insert(TABLE_SETTING, null, values);
		Log.d(LOG, "createSetting - End");
	}

	/*
	 * get single setting
	 */
	public SukiyaSetting getSetting() {
		Log.d(LOG, "getSetting - Start");
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_SETTING + " WHERE "
				+ KEY_ID + " = " + 1;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		SukiyaSetting setting = new SukiyaSetting();
		if (c != null && c.moveToFirst()) {
			setting.setId(c.getInt(c.getColumnIndex(KEY_ID)));
			setting.setShopCode(c.getString(c.getColumnIndex(KEY_SHOP_CODE)));
			setting.setShopName(c.getString(c.getColumnIndex(KEY_SHOP_NAME)));
			setting.setDownloadZipState(c.getInt(c
					.getColumnIndex(KEY_DOWNLOAD_ZIP_STATE)));
		} else {
			setting.setId(1);
			setting.setDownloadZipState(0);

			this.createSetting(setting);
		}

		Log.d(LOG, "getSetting - End");
		return setting;
	}

	/*
	 * Updating a setting
	 */
	public int updateSetting(SukiyaSetting setting) {
		Log.d(LOG, "updateSetting - Start");
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SHOP_CODE, setting.getShopCode());
		values.put(KEY_SHOP_NAME, setting.getShopName());
		values.put(KEY_DOWNLOAD_ZIP_STATE, setting.getDownloadZipState());

		// updating row
		int returnValue = db.update(TABLE_SETTING, values, KEY_ID + " = 1",
				null);

		Log.d(LOG, "updateSetting - End");
		return returnValue;
	}

	/*
	 * Creating a Category history
	 */
	public void createCategoryHistory(CategoryInfo category) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, category.getId());
		values.put(KEY_LAST_UPDATE, category.getLastUpdate());

		// insert row
		db.insert(TABLE_MENU_UPDATE_HISTORY, null, values);
	}

	/*
	 * get single Category history
	 */
	public CategoryInfo getCategoryHistory(String categoryCode) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY_HISTORY
				+ " WHERE " + KEY_ID + " = " + categoryCode;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		CategoryInfo category = new CategoryInfo();
		category.setId(c.getString(c.getColumnIndex(KEY_ID)));
		category.setLastUpdate(c.getString(c.getColumnIndex(KEY_LAST_UPDATE)));

		return category;
	}

	/*
	 * getting all Category history
	 */
	public List<CategoryInfo> getAllCategoryHistory() {
		List<CategoryInfo> categoryList = new ArrayList<CategoryInfo>();

		String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY_HISTORY
				+ " WHERE " + KEY_LAST_UPDATE
				+ " = strftime('%Y%m%d', date('now'))";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			CategoryInfo hostory;
			do {
				hostory = new CategoryInfo();
				hostory.setId(c.getString((c.getColumnIndex(KEY_ID))));
				hostory.setLastUpdate((c.getString(c
						.getColumnIndex(KEY_LAST_UPDATE))));

				// adding to menu_update_history list
				categoryList.add(hostory);
			} while (c.moveToNext());
		}

		return categoryList;
	}

	/*
	 * Updating a Category history
	 */
	public int updateCategoryHistory(CategoryInfo category) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_LAST_UPDATE, category.getLastUpdate());

		// updating row
		return db.update(TABLE_CATEGORY_HISTORY, values, KEY_ID + " = ?",
				new String[] { String.valueOf(category.getId()) });
	}

	public void saveCategoryHistory(String categoryCode) {
		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase dbWrite = this.getWritableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_CATEGORY_HISTORY
				+ " WHERE " + KEY_ID + " = '" + categoryCode + "'";

		Cursor c = db.rawQuery(selectQuery, null);
		String megerSQL = null;
		if (c != null && c.moveToFirst()) {
			megerSQL = "UPDATE " + TABLE_CATEGORY_HISTORY + " SET "
					+ KEY_LAST_UPDATE
					+ " = strftime('%Y%m%d', date('now')) WHERE " + KEY_ID
					+ " = '" + categoryCode + "'";
		} else {
			megerSQL = "INSERT INTO " + TABLE_CATEGORY_HISTORY + "(" + KEY_ID
					+ ", " + KEY_LAST_UPDATE + ") VALUES ('" + categoryCode
					+ "', strftime('%Y%m%d', date('now')))";
		}
		Log.d(LOG, megerSQL);
		dbWrite.execSQL(megerSQL);
	}

	public void saveNumber(String val, int id, String productId,
			String location, String nisugataCd) {
		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase dbWrite = this.getWritableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_PRODUCT_INPUT_HISTORY
				+ " WHERE " + KEY_ID + " = '" + id + "' " + "AND " + PRODUCT_ID
				+ " = '" + productId + "' " + "AND " + LOCATION + " = '"
				+ location + "' " + "AND " + NISUGATA_CD + " = '" + nisugataCd
				+ "' ";

		Cursor c = db.rawQuery(selectQuery, null);
		String megerSQL = null;
		if (c != null && c.moveToFirst()) {
			megerSQL = "UPDATE " + TABLE_PRODUCT_INPUT_HISTORY + " " + "SET "
					+ NUMBER_VAL + " = '" + val + "' " + "WHERE " + KEY_ID
					+ " = '" + id + "' " + "AND " + PRODUCT_ID + " = '"
					+ productId + "' " + "AND " + LOCATION + " = '" + location
					+ "' " + "AND " + NISUGATA_CD + " = '" + nisugataCd + "' ";
		} else {
			megerSQL = "INSERT INTO " + TABLE_PRODUCT_INPUT_HISTORY + "("
					+ KEY_ID + ", " + PRODUCT_ID + ", " + LOCATION + ", "
					+ NISUGATA_CD + ", " + NUMBER_VAL + ", " + DATE_VAL
					+ ") VALUES (" + "'" + id + "', " + "'" + productId + "', "
					+ "'" + location + "', " + "'" + nisugataCd + "', " + "'"
					+ val + "', ''" + ")";
		}
		Log.d(LOG, megerSQL);
		try {
			dbWrite.execSQL(megerSQL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void saveDate(String val, int id, String productId, String location,
			String nisugataCd) {
		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase dbWrite = this.getWritableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_PRODUCT_INPUT_HISTORY
				+ " WHERE " + KEY_ID + " = '" + id + "' " + "AND " + PRODUCT_ID
				+ " = '" + productId + "' " + "AND " + LOCATION + " = '"
				+ location + "' " + "AND " + NISUGATA_CD + " = '" + nisugataCd
				+ "' ";

		Log.d(LOG, selectQuery);
		Cursor c = db.rawQuery(selectQuery, null);
		String megerSQL = null;
		if (c != null && c.moveToFirst()) {
			megerSQL = "UPDATE " + TABLE_PRODUCT_INPUT_HISTORY + " " + "SET "
					+ DATE_VAL + " = '" + val + "' " + "WHERE " + KEY_ID
					+ " = '" + id + "' " + "AND " + PRODUCT_ID + " = '"
					+ productId + "' " + "AND " + LOCATION + " = '" + location
					+ "' " + "AND " + NISUGATA_CD + " = '" + nisugataCd + "' ";
		} else {
			megerSQL = "INSERT INTO " + TABLE_PRODUCT_INPUT_HISTORY + "("
					+ KEY_ID + ", " + PRODUCT_ID + ", " + LOCATION + ", "
					+ NISUGATA_CD + ", " + NUMBER_VAL + ", " + DATE_VAL
					+ ") VALUES (" + "'" + id + "', " + "'" + productId + "', "
					+ "'" + location + "', " + "'" + nisugataCd + "', "
					+ "'', " + "'" + val + "'" + ")";
		}
		Log.d(LOG, megerSQL);
		dbWrite.execSQL(megerSQL);
	}

	public String getVal(int id, String productId, String location,
			String nisugataCd) {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_PRODUCT_INPUT_HISTORY
				+ " WHERE " + KEY_ID + " = '" + id + "' " + "AND " + PRODUCT_ID
				+ " = '" + productId + "' " + "AND " + LOCATION + " = '"
				+ location + "' " + "AND " + NISUGATA_CD + " = '" + nisugataCd
				+ "' ";

		Cursor c = db.rawQuery(selectQuery, null);
		String val = null;
		if (c != null && c.moveToFirst()) {
			val = c.getString(c.getColumnIndex(NUMBER_VAL));
		}
		return val;
	}

	public String getDate(int id, String productId, String location,
			String nisugataCd) {

		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_PRODUCT_INPUT_HISTORY
				+ " WHERE " + KEY_ID + " = '" + id + "' " + "AND " + PRODUCT_ID
				+ " = '" + productId + "' " + "AND " + LOCATION + " = '"
				+ location + "' " + "AND " + NISUGATA_CD + " = '" + nisugataCd
				+ "' ";

		Log.d(LOG, selectQuery);
		Cursor c = db.rawQuery(selectQuery, null);
		String val = null;
		if (c != null && c.moveToFirst()) {
			val = c.getString(c.getColumnIndex(DATE_VAL));
		}
		return val;
	}

	public void setInputDataToMenu(String menuPos, String clearance,
			String timeCode, int priority, String date, String startTime,
			String endTime) {
		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase dbWrite = this.getWritableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_MENU_INPUT_HISTORY
				+ " WHERE " + MENU_POS + " = '" + menuPos + "' " + "AND "
				+ CLEARANCE + " = '" + clearance + "' " + "AND " + TIME_CODE
				+ " = '" + timeCode + "' " + "AND " + PRIORITY + " = '"
				+ priority + "' " + "AND " + DATE + " = '" + date + "' "
				+ "AND " + START_TIME + " = '" + startTime + "' " + "AND "
				+ END_TIME + " = '" + endTime + "' ";

		Log.d(LOG, selectQuery);
		Cursor c = db.rawQuery(selectQuery, null);
		String megerSQL = null;
		if (c != null && c.moveToFirst()) {
			int id = c.getInt(c.getColumnIndex(KEY_ID));

			megerSQL = "UPDATE " + TABLE_MENU_INPUT_HISTORY + " " + "SET "
					+ SUBMITED + " = '0' " + "WHERE " + KEY_ID + " = '" + id
					+ "' ";
		} else {
			megerSQL = "INSERT INTO " + TABLE_MENU_INPUT_HISTORY + "("
					+ MENU_POS + ", " + CLEARANCE + ", " + TIME_CODE + ", "
					+ PRIORITY + ", " + DATE + ", " + START_TIME + ", "
					+ END_TIME + ", " + SUBMITED + ") VALUES (" + "'" + menuPos
					+ "', " + "'" + clearance + "', " + "'" + timeCode + "', "
					+ "'" + priority + "', " + "'" + date + "', " + "'"
					+ startTime + "', " + "'" + endTime + "', " + "'0'" + ")";
		}
		Log.d(LOG, megerSQL);
		dbWrite.execSQL(megerSQL);
	}

	public void setSubmitedDataMenu(String menuPos, String clearance,
			String timeCode, int priority, String date, String startTime,
			String endTime) {
		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase dbWrite = this.getWritableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_MENU_INPUT_HISTORY
				+ " WHERE " + MENU_POS + " = '" + menuPos + "' " + "AND "
				+ CLEARANCE + " = '" + clearance + "' " + "AND " + TIME_CODE
				+ " = '" + timeCode + "' " + "AND " + PRIORITY + " = '"
				+ priority + "' " + "AND " + DATE + " = '" + date + "' "
				+ "AND " + START_TIME + " = '" + startTime + "' " + "AND "
				+ END_TIME + " = '" + endTime + "' ";

		Log.d(LOG, selectQuery);
		Cursor c = db.rawQuery(selectQuery, null);
		String megerSQL = null;
		if (c != null && c.moveToFirst()) {
			int id = c.getInt(c.getColumnIndex(KEY_ID));

			megerSQL = "UPDATE " + TABLE_MENU_INPUT_HISTORY + " " + "SET "
					+ SUBMITED + " = '1' " + "WHERE " + KEY_ID + " = '" + id
					+ "' ";
		} else {
			megerSQL = "INSERT INTO " + TABLE_MENU_INPUT_HISTORY + "("
					+ MENU_POS + ", " + CLEARANCE + ", " + TIME_CODE + ", "
					+ PRIORITY + ", " + DATE + ", " + START_TIME + ", "
					+ END_TIME + ", " + SUBMITED + ") VALUES (" + "'" + menuPos
					+ "', " + "'" + clearance + "', " + "'" + timeCode + "', "
					+ "'" + priority + "', " + "'" + date + "', " + "'"
					+ startTime + "', " + "'" + endTime + "', " + "'1'" + ")";
		}
		Log.d(LOG, megerSQL);
		dbWrite.execSQL(megerSQL);
	}

	public int getMenuInputHistoryId(String menuPos, String clearance,
			String timeCode, int priority, String date, String startTime,
			String endTime) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_MENU_INPUT_HISTORY
				+ " WHERE " + MENU_POS + " = '" + menuPos + "' " + "AND "
				+ CLEARANCE + " = '" + clearance + "' " + "AND " + TIME_CODE
				+ " = '" + timeCode + "' " + "AND " + PRIORITY + " = '"
				+ priority + "' " + "AND " + DATE + " = '" + date + "' "
				+ "AND " + START_TIME + " = '" + startTime + "' " + "AND "
				+ END_TIME + " = '" + endTime + "' ";

		Log.d(LOG, selectQuery);
		Cursor c = db.rawQuery(selectQuery, null);
		if (c != null && c.moveToFirst()) {
			int submited = c.getInt(c.getColumnIndex(KEY_ID));
			return submited;
		}
		return -1;
	}

	public boolean isSubmitedDataMenu(String menuPos, String clearance,
			String timeCode, int priority, String date, String startTime,
			String endTime) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_MENU_INPUT_HISTORY
				+ " WHERE " + MENU_POS + " = '" + menuPos + "' " + "AND "
				+ CLEARANCE + " = '" + clearance + "' " + "AND " + TIME_CODE
				+ " = '" + timeCode + "' " + "AND " + PRIORITY + " = '"
				+ priority + "' " + "AND " + DATE + " = '" + date + "' "
				+ "AND " + START_TIME + " = '" + startTime + "' " + "AND "
				+ END_TIME + " = '" + endTime + "' ";

		Log.d(LOG, selectQuery);
		Cursor c = db.rawQuery(selectQuery, null);
		if (c != null && c.moveToFirst()) {
			int submited = c.getInt(c.getColumnIndex(SUBMITED));
			if (submited == 1) {
				return true;
			}
		}
		return false;
	}

	// private void debugTable() {
	// SQLiteDatabase db = this.getReadableDatabase();
	//
	// String selectQuery = "SELECT * FROM " + TABLE_MENU_INPUT_HISTORY;
	//
	// Log.d(LOG, selectQuery);
	// Cursor c = db.rawQuery(selectQuery, null);
	// if (c != null && c.moveToFirst()) {
	// int index = 1;
	// StringBuilder builder = null;
	// do {
	// builder = new StringBuilder();
	// builder.append("Row: ").append(index).append("\r\n");
	// builder.append(KEY_ID).append(": ").append(c.getString(c.getColumnIndex(KEY_ID))).append("\r\n");
	// builder.append(MENU_POS).append(": ").append(c.getString(c.getColumnIndex(MENU_POS))).append("\r\n");
	// builder.append(CLEARANCE).append(": ").append(c.getString(c.getColumnIndex(CLEARANCE))).append("\r\n");
	// builder.append(TIME_CODE).append(": ").append(c.getString(c.getColumnIndex(TIME_CODE))).append("\r\n");
	// builder.append(PRIORITY).append(": ").append(c.getString(c.getColumnIndex(PRIORITY))).append("\r\n");
	// builder.append(DATE).append(": ").append(c.getString(c.getColumnIndex(DATE))).append("\r\n");
	// builder.append(START_TIME).append(": ").append(c.getString(c.getColumnIndex(START_TIME))).append("\r\n");
	// builder.append(END_TIME).append(": ").append(c.getString(c.getColumnIndex(END_TIME))).append("\r\n");
	// Log.d(LOG, builder.toString());
	// index ++;
	// } while (c.moveToNext());
	// } else {
	// Log.d(LOG, "Table " + TABLE_MENU_INPUT_HISTORY + " is empty.");
	// }
	// }

	// private void debugTable() {
	// SQLiteDatabase db = this.getReadableDatabase();
	//
	// String selectQuery = "SELECT * FROM " + TABLE_PRODUCT_INPUT_HISTORY;
	//
	// Log.d(LOG, selectQuery);
	// Cursor c = db.rawQuery(selectQuery, null);
	// if (c != null && c.moveToFirst()) {
	// int index = 1;
	// StringBuilder builder = null;
	// do {
	// builder = new StringBuilder();
	// builder.append("Row: ").append(index).append("\r\n");
	// builder.append(KEY_ID).append(": ")
	// .append(c.getString(c.getColumnIndex(KEY_ID)))
	// .append("\r\n");
	// builder.append(PRODUCT_ID).append(": ")
	// .append(c.getString(c.getColumnIndex(PRODUCT_ID)))
	// .append("\r\n");
	// builder.append(LOCATION).append(": ")
	// .append(c.getString(c.getColumnIndex(LOCATION)))
	// .append("\r\n");
	// builder.append(NISUGATA_CD).append(": ")
	// .append(c.getString(c.getColumnIndex(NISUGATA_CD)))
	// .append("\r\n");
	// builder.append(NUMBER_VAL).append(": ")
	// .append(c.getString(c.getColumnIndex(NUMBER_VAL)))
	// .append("\r\n");
	// builder.append(DATE_VAL).append(": ")
	// .append(c.getString(c.getColumnIndex(DATE_VAL)))
	// .append("\r\n");
	// Log.d(LOG, builder.toString());
	// index++;
	// } while (c.moveToNext());
	// } else {
	// Log.d(LOG, "Table " + TABLE_PRODUCT_INPUT_HISTORY + " is empty.");
	// }
	// }
}
