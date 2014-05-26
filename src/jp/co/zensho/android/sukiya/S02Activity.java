package jp.co.zensho.android.sukiya;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.zensho.android.sukiya.adapter.LocationAdapter;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import jp.co.zensho.android.sukiya.bean.LocationInfo;
import jp.co.zensho.android.sukiya.bean.MenuInfo;
import jp.co.zensho.android.sukiya.bean.MenuPositionInfo;
import jp.co.zensho.android.sukiya.bean.ProductInfo;
import jp.co.zensho.android.sukiya.calendar.OnSelectCalendarDailogListener;
import jp.co.zensho.android.sukiya.calendar.SukiyaCalendarDialog;
import jp.co.zensho.android.sukiya.common.DateUtils;
import jp.co.zensho.android.sukiya.common.JSONUtils;
import jp.co.zensho.android.sukiya.common.StringUtils;
import jp.co.zensho.android.sukiya.common.SukiyaContant;
import jp.co.zensho.android.sukiya.common.SukiyaNumberKeyboard;
import jp.co.zensho.android.sukiya.database.helper.DatabaseHelper;
import jp.co.zensho.android.sukiya.service.CallAPIAsyncTask;
import jp.co.zensho.android.sukiya.service.CallAPIListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class S02Activity extends S00Activity implements CallAPIListener,
		OnClickListener, OnSelectCalendarDailogListener {
	private static final String LOG = "S02Activity";

	private TextView mTxtTitle;
	private Button mBtnConfirm;
	private Button mBtnBack;

	private LinearLayout mLinearLayoutItem;
	private TextView mTextViewItemName;
	private TextView mTextViewItemNameDisp;
	private TextView mCountCurrentProduct;
	private TextView mTotalProduct;
	private TextView mCurrentPageProduct;
	private TextView mTotalPageProduct;

	private LinearLayout mScrollContent;
	private LayoutInflater myInflater;
	private ImageView mImgLocation;

	// pop-up number
	private RelativeLayout mLayoutPopupNumber;
	private TextView mTxtPopupNumberProductTitle;
	private TextView mTxtPopupNumberProductSubTitle;
	private ImageView mImgPopupNumberProductImage;
	private EditText mTxtPopupNumberProductAmount;
	// private TextView mTxtPopupNumberProductOldAmount;
	private TextView mTxtPopupNumberProductUnit;
	private Button mBtnPopupNumberPlus10;
	private Button mBtnPopupNumberPlus1;
	private Button mBtnPopupNumberPlus01;
	private Button mBtnPopupNumberMinus10;
	private Button mBtnPopupNumberMinus1;
	private Button mBtnPopupNumberMinus01;
	private Button mBtnPopupNumberClose;
	private Button mBtnPopupNumberComplete;
	private EditText mTxtPopupNumberProductMemo;

	// popup date
	private LinearLayout mLayoutPopupDate;
	private TextView mTxtPopupDateProductTitle;
	private ImageView mImgPopupDateProductImage;
	private Button mBtnPopupDatePlusYear;
	private Button mBtnPopupDatePlusMonth;
	private Button mBtnPopupDatePlusDay;
	private TextView mTxtPopupDateYear;
	private TextView mTxtPopupDateMonth;
	private TextView mTxtPopupDateDay;
	private Button mBtnPopupDateMinusYear;
	private Button mBtnPopupDateMinusMonth;
	private Button mBtnPopupDateMinusDay;
	private Button mBtnPopupDateCalendar;
	private Button mBtnPopupDateClose;
	private Button mBtnPopupDateComplete;
	// add by thuc
	private LinearLayout layout_main;
	private LinearLayout layout_main_location;
	private LocationInfo selectedLocation = null;
	private Map<String, List<ProductInfo>> productData = null;
	private List<ProductInfo> displayProduct = null;
	private int indexProductUpdating;
	private String clearanceState = null;
	private int currentLocationIndexSelect;
	private boolean shouldExecuteOnResume;
	private SukiyaNumberKeyboard customKeyboard;
	private int menuInputHistoryId;
	// Zenshou ntdat 20140417 add ++
	public static String S02_HINCODE_KEY = "hin_code";
	public static String S02_HINNAME_KEY = "hin_name";
	public static String S02_KEY = "FROM_S02";
	private String strHinName = "";
	private String strHinCode = "";
	// add by thuc
	private int saveCountCurrentProduct;
	private int saveTotalProductInLocation;
	private List<String> lstLocText = null;
	private List<Integer> listLocationCode = null;
	boolean isCallApiFinish = false;
	// help
	private Button mBtnHelp;
	private TextView mHelp;
	private LinearLayout mHelpLayout;
	// Zenshou ntdat 20140417 add --
	// add by thuc
	private String helpCode = null;
	// 2014/04/29 add ltthuc
	LocationAdapter locationAdapter;
	List<LocationInfo> listLocationInfo;
	int itemPostionClick = 0;
	// end add
	// Reload shop info dialog listener
	private DialogInterface.OnClickListener retryAPI008DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S02Activity.this.callAPI008();
		}
	};

	private DialogInterface.OnClickListener retryAPI012DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S02Activity.this.callAPI012();
		}
	};

	// Reload shop info dialog listener
	private DialogInterface.OnClickListener gotoLossReportDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S02Activity.this.gotoLossReport();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.s02);

		generateViews();
		this.myInflater = LayoutInflater.from(this);
		this.shouldExecuteOnResume = false;
		this.menuInputHistoryId = -1;
		layout_main = (LinearLayout) findViewById(R.id.layout_main);
		layout_main_location = (LinearLayout) findViewById(R.id.layout_location);
		// Update title
		mTxtTitle = (TextView) findViewById(R.id.s02_txt_title);
		String titleFormat = getString(R.string.title_screen_02);
		String clearanceTitle = StringUtils.EMPTY;
		MenuPositionInfo selectedMenu = super.app.getSelectedMenu();
		clearanceState = SukiyaContant.TANA_EIHO;
		if (selectedMenu != null) {
			this.menuInputHistoryId = selectedMenu.getHistoryId();

			if (SukiyaContant.MENU_01.equals(selectedMenu.getCode())) {
				clearanceTitle = getString(R.string.tana_eiho);
				clearanceState = SukiyaContant.TANA_EIHO;
				// add by thuc
				helpCode = StringUtils.S021_HELP_CD;
				// end add
			} else if (SukiyaContant.MENU_02.equals(selectedMenu.getCode())
					&& selectedMenu.getCurrentDisplayInfo() != null) {
				String repeatState = selectedMenu.getCurrentDisplayInfo()
						.getRepeatState();
				helpCode = StringUtils.S022_HELP_CD;
				if (SukiyaContant.TANA_DAILY.equals(repeatState)) {
					clearanceTitle = getString(R.string.tana_daily);
					clearanceState = SukiyaContant.TANA_DAILY;
				} else if (SukiyaContant.TANA_WEEKLY.equals(repeatState)) {
					clearanceTitle = getString(R.string.tana_weekly);
					clearanceState = SukiyaContant.TANA_WEEKLY;
				} else if (SukiyaContant.TANA_MONTHLY.equals(repeatState)) {
					clearanceTitle = getString(R.string.tana_monthly);
					clearanceState = SukiyaContant.TANA_MONTHLY;
				}
			} else if (SukiyaContant.MENU_03.equals(selectedMenu.getCode())) {
				helpCode = StringUtils.S023_HELP_CD;
				titleFormat = getString(R.string.title_screen_02b);
				clearanceTitle = getString(R.string.tana_hozai);
				clearanceState = SukiyaContant.TANA_HOZAI;
			} else if (SukiyaContant.MENU_04.equals(selectedMenu.getCode())) {
				helpCode = StringUtils.S024_HELP_CD;
				titleFormat = getString(R.string.title_screen_02b);
				clearanceTitle = getString(R.string.tana_shomikigen);
				clearanceState = SukiyaContant.TANA_SHOMIKIGEN;
			}
		}
		String strTitle = MessageFormat.format(titleFormat, clearanceTitle);
		mTxtTitle.setText(strTitle);

		mCountCurrentProduct = (TextView) findViewById(R.id.tv_count_current_product);
		mTotalProduct = (TextView) findViewById(R.id.tv_total_product);
		mCurrentPageProduct = (TextView) findViewById(R.id.tv_current_page_product);
		mTotalPageProduct = (TextView) findViewById(R.id.tv_total_page_product);

		mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
		mBtnConfirm.setOnClickListener(this);

		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);

		mScrollContent = (LinearLayout) findViewById(R.id.item_list_scroll_content);

		mTextViewItemName = (TextView) findViewById(R.id.textview_item_name);
		mTextViewItemNameDisp = (TextView) findViewById(R.id.textview_item_name_disp);

		// mImageViewItem1 = (ImageView) findViewById(R.id.item_1_img);
		// mImageViewItem1.setOnClickListener(new OnClickListener() {
		mImgLocation = (ImageView) findViewById(R.id.s02_img_location);
		mLinearLayoutItem = (LinearLayout) findViewById(R.id.linearlayout_item);
		mLinearLayoutItem.setOnClickListener(this);
		mBtnHelp = (Button) findViewById(R.id.btn_q);
		mHelp = (TextView) findViewById(R.id.help_content);
		mHelpLayout = (LinearLayout) findViewById(R.id.s02_help);
		mBtnHelp.setOnClickListener(this);
		mHelpLayout.setOnClickListener(this);

		// init popup number view
		this.initPopupNumber();

		// init popup date
		this.initPopupDate();

		if (SukiyaContant.MENU_04.equals(super.app.getSelectedMenu().getCode())) {
			layout_main.removeView(layout_main_location);
		} else {
			// do nothing 04.23 luongGV
		}

		this.callAPI008();
	}

	@Override
	protected void onResume() {
		if (shouldExecuteOnResume) {
			this.refreshData();
		} else {
			shouldExecuteOnResume = true;

		}
		// if(isCallApiFinish==false){
		/*
		 * if(listLocationCode!=null) { listLocationCode = new
		 * ArrayList<Integer>(); } locationButtonClicked(); }
		 */
		// Zenshou ntdat 20140417 add ++
		// hide popup date
		super.hidePopup(mLayoutPopupDate);
		// Zenshou ntdat 20140417 add --
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.input, menu);
		return true;
	}

	@Override
	protected void onStop() {
		super.removeUpdateCurrentDate();
		super.onStop();
	}

	@Override
	public void callAPIFinish(String tag, JSONObject result, ErrorInfo error) {
		// dismiss process dialog
		this.dismissProcessDialog();

		if (error == null && result != null) {
			try {
				if (!JSONUtils.hasError(result)) {
					if (JSONUtils.API_008_PATH.equals(tag)) {
						List<ProductInfo> productList = JSONUtils
								.parseProductList(result);
						this.storeProductList(productList);
						// Zenshou ntdat 20140418 add ++
						this.storeProductListToDB(productList);
						// Zenshou ntdat 20140418 add --

						// 04.23 LuongGV setselectindex at element 0 Start
						// if
						// (!SukiyaContant.TANA_SHOMIKIGEN.equals(this.clearanceState))
						// {
						// setDefaultLocation(productList);
						// }
						// 04.23 LuongGV setselectindex at element 0 End
					} else if (JSONUtils.API_043_PATH.equals(tag)) {
						// Zenshou ntdat 20140407 delete ++
						String help = null;
						try {
							help = JSONUtils.partHelpString(result);
							super.showPopup(mHelpLayout);
							mHelp.setText(help.replace("\\n", "\n"));
						} catch (JSONException e) {
							if (error == null) {
								error = new ErrorInfo();
								error.setMessage(MessageFormat.format(
										"System error: {0}", e.getMessage()));
							}
							e.printStackTrace();
						}

						Log.e("Json after parse", help);
						if (help != null) {
							this.helpString = help;
						}
					}
				} else {
					if (error == null) {
						error = JSONUtils.getFirstError(result);
					}
				}
			} catch (JSONException e) {
				if (error == null) {
					error = new ErrorInfo();
					error.setMessage(MessageFormat.format("System error: {0}",
							e.getMessage()));
				}
				e.printStackTrace();
			}
		}
		if (error != null) {
			String message = error.getMessage();
			if (!StringUtils.isEmpty(error.getCode())) {
				message = MessageFormat.format("{0}: {1}", error.getCode(),
						error.getMessage());
			}

			// Show error dialog
			super.showDialog(this, message, R.string.try_again,
					retryAPI008DialogListener, R.string.cancel,
					dismissDialogListener);
		} else {
			if (JSONUtils.API_008_PATH.equals(tag)) {
				refreshData();
				createLocationList();
				changeBackGroundLocation(listLocationInfo.get(0)
						.getInputedProduct(), listLocationInfo.get(0)
						.getTotalProduct());
			}
		}

		isCallApiFinish = true;
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mHelpLayout)) {
			super.hidePopup(mHelpLayout);
		} else if (v.equals(mBtnHelp)) {
			super.getHelp(S02Activity.this, helpCode);
		} else if (mBtnPopupNumberClose.equals(v)) {
			// Close popup number
			super.hidePopup(mLayoutPopupNumber);
		} else if (mBtnPopupNumberComplete.equals(v)) {
			this.completeInputNumber();
		} else if (mBtnPopupNumberPlus10.equals(v)) {
			// +10
			this.addProductAmount(SukiyaContant.VALUE_10);
		} else if (mBtnPopupNumberPlus1.equals(v)) {
			// +1
			this.addProductAmount(SukiyaContant.VALUE_1);
		} else if (mBtnPopupNumberPlus01.equals(v)) {
			// +0.1
			this.addProductAmount(SukiyaContant.VALUE_01);
		} else if (mBtnPopupNumberMinus10.equals(v)) {
			// -10
			this.addProductAmount(SukiyaContant.VALUE_MINUS_10);
		} else if (mBtnPopupNumberMinus1.equals(v)) {
			// -1
			this.addProductAmount(SukiyaContant.VALUE_MINUS_1);
		} else if (mBtnPopupNumberMinus01.equals(v)) {
			// -0.1
			this.addProductAmount(SukiyaContant.VALUE_MINUS_01);
		} else if (mBtnPopupDateClose.equals(v)) {
			super.hidePopup(mLayoutPopupDate);
		} else if (mBtnPopupDateComplete.equals(v)) {
			this.hideDatePopup();
		} else if (mBtnPopupDateCalendar.equals(v)) {
			this.calendarButtonClicked();
		} else if (mBtnPopupDatePlusYear.equals(v)) {
			this.addDate(Calendar.YEAR, 1);
		} else if (mBtnPopupDatePlusMonth.equals(v)) {
			this.addDate(Calendar.MONTH, 1);
		} else if (mBtnPopupDatePlusDay.equals(v)) {
			this.addDate(Calendar.DAY_OF_MONTH, 1);
		} else if (mBtnPopupDateMinusYear.equals(v)) {
			this.addDate(Calendar.YEAR, -1);
		} else if (mBtnPopupDateMinusMonth.equals(v)) {
			this.addDate(Calendar.MONTH, -1);
		} else if (mBtnPopupDateMinusDay.equals(v)) {
			this.addDate(Calendar.DAY_OF_MONTH, -1);
		} else if (mLinearLayoutItem.equals(v)) {
			this.locationButtonClicked();
		} else if (mBtnBack.equals(v)) {
			finish();
			overridePendingTransition(R.animator.slide_in_left,
					R.animator.slide_out_right);
		} else if (mBtnConfirm.equals(v)) {
			Intent intent = new Intent(this, S04Activity.class);
			startActivity(intent);
			overridePendingTransition(R.animator.slide_in_right,
					R.animator.slide_out_left);
		} else if (v != null && v.getTag() != null) {
			int tag = Integer.parseInt(v.getTag().toString());
			int index = tag % 1000;
			Log.d("onClick", "Tag: " + tag + " / Index: " + index);

			if (index >= 0 && index <= this.displayProduct.size()) {
				this.indexProductUpdating = index;
				ProductInfo product = this.displayProduct.get(index);

				if (SukiyaContant.TANA_SHOMIKIGEN.equals(this.clearanceState)) {
					this.showDatePopup(product);
				} else {
					this.setupUpdateAmountOfProduct(product);

					// show popup number
					showPopup(mLayoutPopupNumber);
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (customKeyboard.isCustomKeyboardVisible()) {
			customKeyboard.hideCustomKeyboard();
		} else {
			super.onBackPressed();
		}
	}

	private void initPopupNumber() {
		mLayoutPopupNumber = (RelativeLayout) findViewById(R.id.p01_input_number);
		mTxtPopupNumberProductTitle = (TextView) findViewById(R.id.p01_txt_product_title);
		mTxtPopupNumberProductSubTitle = (TextView) findViewById(R.id.p01_txt_sub_title);
		mImgPopupNumberProductImage = (ImageView) findViewById(R.id.p01_img_product_image);
		mTxtPopupNumberProductAmount = (EditText) findViewById(R.id.p01_txt_product_amount);
		// mTxtPopupNumberProductOldAmount = (TextView)
		// findViewById(R.id.s02_txt_popup_num_product_old_amount);
		mTxtPopupNumberProductUnit = (TextView) findViewById(R.id.p01_txt_product_unit);
		mBtnPopupNumberPlus10 = (Button) findViewById(R.id.p01_btn_plus10);
		mBtnPopupNumberPlus1 = (Button) findViewById(R.id.p01_btn_plus1);
		mBtnPopupNumberPlus01 = (Button) findViewById(R.id.p01_btn_plus01);
		mBtnPopupNumberMinus10 = (Button) findViewById(R.id.p01_btn_minus10);
		mBtnPopupNumberMinus1 = (Button) findViewById(R.id.p01_btn_minus1);
		mBtnPopupNumberMinus01 = (Button) findViewById(R.id.p01_btn_minus01);
		mBtnPopupNumberComplete = (Button) findViewById(R.id.p01_btn_complete);
		mBtnPopupNumberClose = (Button) findViewById(R.id.p01_btn_close);
		mTxtPopupNumberProductMemo = (EditText) findViewById(R.id.p01_txt_product_memo);
		// mTxtPopupNumberProductAmount.setRawInputType(Configuration.KEYBOARD);
		// mTxtPopupNumberProductAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
		customKeyboard = new SukiyaNumberKeyboard(this, R.id.keyboardview,
				R.xml.keyboard_layout);
		customKeyboard.registerEditText(R.id.p01_txt_product_amount);

		// set listener
		mBtnPopupNumberClose.setOnClickListener(this);
		mBtnPopupNumberPlus10.setOnClickListener(this);
		mBtnPopupNumberPlus1.setOnClickListener(this);
		mBtnPopupNumberPlus01.setOnClickListener(this);
		mBtnPopupNumberMinus10.setOnClickListener(this);
		mBtnPopupNumberMinus1.setOnClickListener(this);
		mBtnPopupNumberMinus01.setOnClickListener(this);
		mBtnPopupNumberComplete.setOnClickListener(this);
		// mTxtPopupNumberProductAmount.setFilters(new InputFilter[] {
		// new AmountDigitsKeyListener()
		// });
	}

	private void initPopupDate() {
		mLayoutPopupDate = (LinearLayout) findViewById(R.id.p02_input_date);
		mTxtPopupDateProductTitle = (TextView) mLayoutPopupDate
				.findViewById(R.id.p02_txt_product_title);
		mImgPopupDateProductImage = (ImageView) mLayoutPopupDate
				.findViewById(R.id.p02_img_product_image);

		mBtnPopupDatePlusYear = (Button) mLayoutPopupDate
				.findViewById(R.id.p02_btn_plus_year);
		mBtnPopupDatePlusMonth = (Button) mLayoutPopupDate
				.findViewById(R.id.p02_btn_plus_month);
		mBtnPopupDatePlusDay = (Button) mLayoutPopupDate
				.findViewById(R.id.p02_btn_plus_date);

		mTxtPopupDateYear = (TextView) mLayoutPopupDate
				.findViewById(R.id.p02_txt_year);
		mTxtPopupDateMonth = (TextView) mLayoutPopupDate
				.findViewById(R.id.p02_txt_month);
		mTxtPopupDateDay = (TextView) mLayoutPopupDate
				.findViewById(R.id.p02_txt_day);

		mBtnPopupDateMinusYear = (Button) mLayoutPopupDate
				.findViewById(R.id.p02_btn_minus_year);
		mBtnPopupDateMinusMonth = (Button) mLayoutPopupDate
				.findViewById(R.id.p02_btn_minus_month);
		mBtnPopupDateMinusDay = (Button) mLayoutPopupDate
				.findViewById(R.id.p02_btn_minus_date);

		mBtnPopupDateCalendar = (Button) mLayoutPopupDate
				.findViewById(R.id.p02_btn_calendar);
		mBtnPopupDateComplete = (Button) mLayoutPopupDate
				.findViewById(R.id.p02_btn_complete);
		mBtnPopupDateClose = (Button) mLayoutPopupDate
				.findViewById(R.id.p02_btn_close);

		mBtnPopupDateClose.setOnClickListener(this);

		mBtnPopupDatePlusYear.setOnClickListener(this);
		mBtnPopupDatePlusMonth.setOnClickListener(this);
		mBtnPopupDatePlusDay.setOnClickListener(this);

		mBtnPopupDateMinusYear.setOnClickListener(this);
		mBtnPopupDateMinusMonth.setOnClickListener(this);
		mBtnPopupDateMinusDay.setOnClickListener(this);

		mBtnPopupDateCalendar.setOnClickListener(this);
		mBtnPopupDateComplete.setOnClickListener(this);
	}

	private void locationButtonClicked() {
		createLocationList();

		// end add by ltthuc 2014-5-5
		// remove by ltthuc 2014-5-5
		/*
		 * builder.setSingleChoiceItems( tempArrLocation.toArray(new
		 * String[tempArrLocation.size()]), this.currentLocationIndexSelect, new
		 * DialogInterface.OnClickListener() {
		 *
		 * @Override public void onClick(DialogInterface arg0, int arg1) { if
		 * (arg0 != null) { arg0.dismiss(); } // LuongGV 2014/04/22 fix
		 * OutofIndexExp Start if (arg1 >= 0 && arg1 < listLocationCode.size())
		 * { S02Activity.this.currentLocationIndexSelect = arg1;
		 * S02Activity.this .selectedLocationAtIndex(listLocationCode
		 * .get(arg1)); // changeBackGroundLocation(saveCountCurrentProduct,
		 * saveTotalProductInLocation); } else { } // LuongGV 2014/04/22 fix
		 * OutofIndexExp End }
		 *
		 * });
		 */
		// end remove by ltthuc 2014-5-5
		Builder builder = new AlertDialog.Builder(this);

		if (this.currentLocationIndexSelect >= lstLocText.size()) {
			this.currentLocationIndexSelect = 0;
		}
		// add by ltthuc 2014-5-5
		locationAdapter = new LocationAdapter(this, listLocationInfo,
				lstLocText, itemPostionClick);
		LayoutInflater minflater = LayoutInflater.from(this);

		View customView = minflater
				.inflate(R.layout.color_location_dialog, null, false);
		builder.setView(customView);
		ListView list = (ListView) customView.findViewById(R.id.list_loc);
		list.setAdapter(locationAdapter);

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 >= 0 && arg2 < listLocationCode.size()) {
					S02Activity.this.currentLocationIndexSelect = arg2;
					S02Activity.this.selectedLocationAtIndex(listLocationCode
							.get(arg2));

					RadioButton rad = (RadioButton) arg1
							.findViewById(R.id.rbtn_location_choice);
					rad.setChecked(true);
					itemPostionClick = arg2;
					locationAdapter.notifyDataSetChanged();

					changeBackGroundLocation(saveCountCurrentProduct,
							saveTotalProductInLocation);
				} else {
					// do nothing
				}
				alert.dismiss();

				// LuongGV 2014/04/22 fix OutofIndexExp End
			}

		});
		super.alert = builder.show();

	}

	/*
	 * @author LuongGV create listlocation
	 */
	private void createLocationList() {
		// String[] locationArray = null;
		// add by thuc 2014/04/29
		if (listLocationInfo == null) {
			listLocationInfo = new ArrayList<LocationInfo>();
		} else {
			listLocationInfo.clear();
		}
		if (lstLocText == null) {
			lstLocText = new ArrayList<String>();
		} else {
			lstLocText.clear();
		}

		if (listLocationCode == null) {
			listLocationCode = new ArrayList<Integer>();
		} else {
			listLocationCode.clear();
		}
		List<LocationInfo> locationList = super.app.getLocationList();
		if (locationList != null && locationList.size() > 0) {
			// locationArray = new String[locationList.size()];

			String locationFormat = getString(R.string.location_format);
			LocationInfo location = null;
			List<ProductInfo> productList;
			int currentInput;
			int totalProduct;
			for (int i = 0; i < locationList.size(); i++) {
				location = locationList.get(i);

				currentInput = 0;
				totalProduct = 0;
				productList = this.productData.get(location.getCode());
				if (productList != null) {
					totalProduct = productList.size();
					if (!SukiyaContant.TANA_SHOMIKIGEN
							.equals(this.clearanceState)) {
						for (ProductInfo productInfo : productList) {
							if (productInfo.getActualStock() != null) {
								currentInput++;
							}
						}
					} else {
						for (ProductInfo productInfo : productList) {
							if (!productInfo.isEmptyDate()) {
								currentInput++;
							}
						}
					}
				}
				location.setInputedProduct(currentInput);
				location.setTotalProduct(totalProduct);

				if (location != null) {
					if (totalProduct != 0) {
						lstLocText
								.add(MessageFormat.format(locationFormat,
										location.getName(), currentInput,
										totalProduct));
						listLocationCode.add(i);
						listLocationInfo.add(location);
					}
				}

			}
		}
	}

	private void changeBackGroundLocation(int n, int m) {
		if (n < m) {
			mLinearLayoutItem.setBackgroundResource(R.drawable.bg_item_2);
		} else if (n == m && m != 0) {
			mLinearLayoutItem.setBackgroundResource(R.drawable.bg_item);
		}
	}

	private void selectedLocationAtIndex(int index) {
		Log.d(LOG, "selectedLocationAtIndex - START");
		Log.d(LOG, "index: " + index);
		List<LocationInfo> locationList = super.app.getLocationList();
		if (locationList != null && locationList.size() > 0 && index >= 0
				&& index < locationList.size()) {
			this.selectedLocation = locationList.get(index);
			Log.d(LOG,
					"selectedLocation: Code: "
							+ this.selectedLocation.getCode() + " / Name: "
							+ this.selectedLocation.getName());
		} else {
			this.selectedLocation = null;
			Log.d(LOG, "selectedLocation is null");
		}

		this.refreshDisplay();
		Log.d(LOG, "selectedLocationAtIndex - END");
	}

	/**
     *
     */
	private void refreshDisplay() {
		String selectedLocationImage = null;
		if (SukiyaContant.MENU_04.equals(super.app.getSelectedMenu().getCode())) {
			this.displayProduct = super.app.getProductList();
		} else if (this.selectedLocation != null) {
			mTextViewItemName.setText(this.selectedLocation.getName());
			mTextViewItemNameDisp.setText(this.selectedLocation.getName());
			selectedLocationImage = this.selectedLocation.getImagePath();

			if (this.productData != null && this.productData.size() > 0) {
				this.displayProduct = this.productData
						.get(this.selectedLocation.getCode());
			}
			if (this.displayProduct == null) {
				this.displayProduct = new ArrayList<ProductInfo>();
			}
		} else {
			mTextViewItemName.setText(StringUtils.EMPTY);
			mTextViewItemNameDisp.setText(StringUtils.EMPTY);

			if (this.displayProduct == null) {
				this.displayProduct = new ArrayList<ProductInfo>();
			} else {
				this.displayProduct.clear();
			}
		}

		// set select location image
		super.setImage(mImgLocation, selectedLocationImage,
				R.drawable.no_image_85);

		if (this.productData != null && this.productData.size() > 0) {
			int countCurrentProduct = 0;
			for (Map.Entry<String, List<ProductInfo>> entry : this.productData
					.entrySet()) {
				if (entry.getValue() != null && entry.getValue().size() > 0) {
					for (ProductInfo productInfo : entry.getValue()) {
						if (!SukiyaContant.TANA_SHOMIKIGEN
								.equals(this.clearanceState)) {
							if (productInfo.getActualStock() != null) {
								countCurrentProduct++;
							}
						} else if (!productInfo.isEmptyDate()) {
							countCurrentProduct++;
						}
					}
				}
			}
			this.mCountCurrentProduct.setText(String
					.valueOf(countCurrentProduct));
		}

		mScrollContent.removeAllViews();
		int countInputProductInLocation = 0;
		int totalProductInLocation = 0;

		if (this.displayProduct != null && this.displayProduct.size() > 0) {
			totalProductInLocation = this.displayProduct.size();

			LinearLayout view = null;
			TextView productItemName = null;
			TextView productItemFromName = null;
			ImageView productItemImage = null;
			TextView productItemAmount = null;
			TextView productItemUnit = null;

			LinearLayout productItemInputDate = null;
			TextView productItemYear = null;
			TextView productItemMonth = null;
			TextView productItemDay = null;

			ProductInfo productInfo = null;
			for (int i = 0; i < this.displayProduct.size(); i++) {
				productInfo = this.displayProduct.get(i);

				view = new LinearLayout(this);
				if (SukiyaContant.TANA_HOZAI.equals(this.clearanceState)) {
					view = (LinearLayout) this.myInflater.inflate(
							R.layout.product_scroll_item_2, null);
				} else if (SukiyaContant.TANA_SHOMIKIGEN
						.equals(this.clearanceState)) {
					view = (LinearLayout) this.myInflater.inflate(
							R.layout.product_scroll_item_date, null);
				} else {
					view = (LinearLayout) this.myInflater.inflate(
							R.layout.product_scroll_item, null);

					productItemFromName = (TextView) view
							.findViewById(R.id.product_item_name1);
					productItemFromName.setText(productInfo.getNisugataNm());
				}

				productItemName = (TextView) view
						.findViewById(R.id.product_item_name);
				productItemName.setText(productInfo.getHinNm());

				productItemImage = (ImageView) view
						.findViewById(R.id.product_item_image);
				int tag1 = 1000 + i;
				productItemImage.setTag(tag1);
				productItemImage.setOnClickListener(this);
				super.setImage(productItemImage, productInfo.getImageName(),
						R.drawable.no_image_191);

				if (SukiyaContant.TANA_SHOMIKIGEN.equals(this.clearanceState)) {
					if (!productInfo.isEmptyDate()) {
						countInputProductInLocation++;
					}

					productItemInputDate = (LinearLayout) view
							.findViewById(R.id.lay_input_date);
					productItemYear = (TextView) view
							.findViewById(R.id.product_item_year);
					productItemMonth = (TextView) view
							.findViewById(R.id.product_item_month);
					productItemDay = (TextView) view
							.findViewById(R.id.product_item_day);

					productItemYear.setText(productInfo.getYear());
					productItemMonth.setText(productInfo.getMonth());
					productItemDay.setText(productInfo.getDay());

					int tag = 1000 + i;
					productItemInputDate.setTag(tag);
					productItemInputDate.setOnClickListener(this);
				} else {
					if (productInfo.getActualStock() != null) {
						countInputProductInLocation++;
					}

					productItemAmount = (TextView) view
							.findViewById(R.id.product_item_amount);
					if (productInfo.getActualStock() == null) {
						productItemAmount.setText(StringUtils.EMPTY);
					} else {
						productItemAmount.setText(strDouble(productInfo
								.getActualStock()));
					}
					int tag = 1000 + i;
					productItemAmount.setTag(tag);
					productItemAmount.setOnClickListener(this);

					productItemUnit = (TextView) view
							.findViewById(R.id.product_item_unit);
					productItemUnit.setText(productInfo.getTaniNm());
				}

				this.mScrollContent.addView(view);
			}
		}
		mCurrentPageProduct
				.setText(String.valueOf(countInputProductInLocation));
		saveCountCurrentProduct = countInputProductInLocation;
		mTotalPageProduct.setText(String.valueOf(totalProductInLocation));
		saveTotalProductInLocation = totalProductInLocation;
		changeBackGroundLocation(saveCountCurrentProduct,
				saveTotalProductInLocation);
	}

	private void callAPI008() {
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			super.db = new DatabaseHelper(getApplicationContext());

			boolean isLoadedData = false;
			String menuCode = null;
			String startTime = null;
			String endTime = null;
			MenuPositionInfo mnPos = super.app.getSelectedMenu();
			if (mnPos != null) {
				menuCode = mnPos.getCode();

				MenuInfo mnInf = mnPos.getCurrentDisplayInfo();
				if (mnInf != null) {
					startTime = mnInf.getFrom();
					endTime = mnInf.getTo();
				}
			}

			Date currDate = super.app.getCurrentDate();
			if (super.db
					.isStoredProduct(currDate, menuCode, startTime, endTime)) {
				List<ProductInfo> productList = super.db.getProductInfo(
						currDate, menuCode, startTime, endTime);
				if (productList.size() > 0) {
					isLoadedData = true;
					storeProductList(productList);
					this.refreshData();
					// 04.23 LuongGV setselectindex at element 0 Start
					// if
					// (!SukiyaContant.TANA_SHOMIKIGEN.equals(this.clearanceState))
					// {
					// setDefaultLocation(productList);
					// }
					// 04.23 LuongGV setselectindex at element 0 End
				}
			}
			super.db.close();

			if (!isLoadedData) {
				// Get KPI info from API
				Map<String, String> params = new HashMap<String, String>();
				params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
				params.put(JSONUtils.CLEARANCE_TYPE, this.clearanceState);

				try {
					String url = JSONUtils.getAPIUrl(JSONUtils.API_008_PATH,
							params);

					// show process dialog
					this.showProcessDialog(this, null, null);

					new CallAPIAsyncTask(this, JSONUtils.API_008_PATH)
							.execute(url);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();

					// Show error dialog
					String message = MessageFormat.format("System error: {0}",
							e.getMessage());
					super.showDialog(this, message, R.string.try_again,
							retryAPI008DialogListener, R.string.cancel,
							dismissDialogListener);
				}
			}
		} else {
			// Show error dialog
			super.showDialog(this, R.string.msg_004_shop_code_not_found,
					R.string.try_again, retryAPI008DialogListener,
					R.string.cancel, dismissDialogListener);
		}
	}

	/*
	 * @author LuongGV copy of CallAPI008 to use for Shomikigen callApi012
	 */
	private void callAPI012() {
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			super.db = new DatabaseHelper(getApplicationContext());

			boolean isLoadedData = false;
			String menuCode = null;
			String startTime = null;
			String endTime = null;
			MenuPositionInfo mnPos = super.app.getSelectedMenu();
			if (mnPos != null) {
				menuCode = mnPos.getCode();

				MenuInfo mnInf = mnPos.getCurrentDisplayInfo();
				if (mnInf != null) {
					startTime = mnInf.getFrom();
					endTime = mnInf.getTo();
				}
			}

			Date currDate = super.app.getCurrentDate();
			if (super.db
					.isStoredProduct(currDate, menuCode, startTime, endTime)) {
				List<ProductInfo> productList = super.db.getProductInfo(
						currDate, menuCode, startTime, endTime);
				if (productList.size() > 0) {
					isLoadedData = true;
					storeProductList(productList);
					this.refreshData();
				}
			}
			super.db.close();

			if (!isLoadedData) {
				// Get KPI info from API
				Map<String, String> params = new HashMap<String, String>();
				params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
				params.put(JSONUtils.CLEARANCE_TYPE, this.clearanceState);

				try {
					String url = JSONUtils.getAPIUrl(JSONUtils.API_012_PATH,
							params);

					// show process dialog
					this.showProcessDialog(this, null, null);

					new CallAPIAsyncTask(this, JSONUtils.API_012_PATH)
							.execute(url);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();

					// Show error dialog
					String message = MessageFormat.format("System error: {0}",
							e.getMessage());
					super.showDialog(this, message, R.string.try_again,
							retryAPI012DialogListener, R.string.cancel,
							dismissDialogListener);
				}
			}
		} else {
			// Show error dialog
			super.showDialog(this, R.string.msg_004_shop_code_not_found,
					R.string.try_again, retryAPI012DialogListener,
					R.string.cancel, dismissDialogListener);
		}
	}

	private void setupUpdateAmountOfProduct(ProductInfo product) {
		if (product != null) {
			// Title
			if (StringUtils.isEmpty(product.getHinNm())) {
				mTxtPopupNumberProductTitle.setText(StringUtils.EMPTY);
			} else {
				mTxtPopupNumberProductTitle.setText(product.getHinNm());
			}
			if (SukiyaContant.TANA_HOZAI.equals(clearanceState)
					|| StringUtils.isEmpty(product.getNisugataNm())) {
				mTxtPopupNumberProductSubTitle.setText(StringUtils.EMPTY);
			} else {
				mTxtPopupNumberProductSubTitle.setText(product.getNisugataNm());
			}

			// image
			super.setImage(mImgPopupNumberProductImage, product.getImageName(),
					R.drawable.no_image_191);

			// Amount
			if (product.getActualStock() == null
					|| product.getActualStock() < 0) {
				mTxtPopupNumberProductAmount.setText(StringUtils.EMPTY);
			} else {
				mTxtPopupNumberProductAmount.setText(strDouble(product
						.getActualStock()));
			}
			// if (product.getTheoreticalStock() == null ||
			// product.getTheoreticalStock() < 0) {
			// mTxtPopupNumberProductOldAmount.setText(StringUtils.EMPTY);
			// } else {
			// mTxtPopupNumberProductOldAmount.setText(product.getTheoreticalStock().toString());
			// }
			if (StringUtils.isEmpty(product.getTaniNm())) {
				mTxtPopupNumberProductUnit.setText(StringUtils.EMPTY);
			} else {
				mTxtPopupNumberProductUnit.setText(product.getTaniNm());
			}

			// set memo
			if (StringUtils.isEmpty(product.getMemo())) {
				mTxtPopupNumberProductMemo.setText(StringUtils.EMPTY);
			} else {
				mTxtPopupNumberProductMemo.setText(product.getMemo());
			}
		}
	}

	private void completeInputNumber() {
		String currentVal = mTxtPopupNumberProductAmount.getText().toString();
		lstLocText = new ArrayList<String>();
		if (StringUtils.isEmpty(currentVal)) {
			currentVal = StringUtils.ZERO;
		}
		Double newAmount = Double.valueOf(currentVal);

		// get product
		if (this.indexProductUpdating >= 0
				&& this.indexProductUpdating <= this.displayProduct.size()) {
			ProductInfo product = this.displayProduct
					.get(this.indexProductUpdating);
			if (product != null) {
				if (newAmount < 0) {
					product.setActualStock(null);
				} else {
					product.setActualStock(newAmount);
					super.db = new DatabaseHelper(getApplicationContext());

					super.db.saveNumber(currentVal, menuInputHistoryId,
							product.getHinCd(), product.getOrgLocationCd(),
							product.getNisugataCd());

					super.db.close();
				}
				this.refreshDisplay();
			}
		}

		// Close popup number
		super.hidePopup(mLayoutPopupNumber);
	}

	private void addProductAmount(String value) {
		String currentVal = mTxtPopupNumberProductAmount.getText().toString();
		if (StringUtils.isEmpty(currentVal)) {
			currentVal = StringUtils.ZERO;
		}
		BigDecimal currentValb = new BigDecimal(currentVal);
		BigDecimal subV = new BigDecimal(value);
		BigDecimal r = currentValb.add(subV);
		if (r.compareTo(BigDecimal.ZERO) < 0) {
			r = BigDecimal.ZERO;
		}
		if (r.compareTo(MAX_AMOUNT_VALUE) >= 0) {
			r = currentValb;
		}
		mTxtPopupNumberProductAmount.setText(r.toString());
	}

	private void refreshData() {
		if (this.productData == null) {
			this.productData = new HashMap<String, List<ProductInfo>>();
		} else {
			this.productData.clear();
		}

		List<ProductInfo> productList = super.app.getProductList();
		if (productList != null && productList.size() > 0) {
			String key = null;
			List<ProductInfo> value = null;
			for (ProductInfo productInfo : productList) {
				Log.d(LOG, "Product: " + productInfo.getHinCd()
						+ " / Location: " + productInfo.getLocationCd());
				key = productInfo.getLocationCd();
				value = this.productData.get(key);
				if (value == null) {
					value = new ArrayList<ProductInfo>();
				}
				value.add(productInfo);

				this.productData.put(key, value);
			}
			super.app.setProductList(productList);

			// update layout
			this.mCountCurrentProduct.setText(StringUtils.ZERO);
			this.mTotalProduct.setText(String.valueOf(productList.size()));
		}
		// 04.23 LuongGV add

		if (listLocationCode == null) {
			listLocationCode = new ArrayList<Integer>();
		} else {
			listLocationCode.clear();
		}

		List<LocationInfo> locationList = super.app.getLocationList();
		if (locationList != null && locationList.size() > 0) {
			// locationArray = new String[locationList.size()];
			LocationInfo location = null;
			int currentInput;
			int totalProduct;
			for (int i = 0; i < locationList.size(); i++) {
				location = locationList.get(i);

				currentInput = 0;
				totalProduct = 0;
				productList = this.productData.get(location.getCode());
				if (productList != null) {
					totalProduct = productList.size();
				}

				if (location != null) {
					if (totalProduct != 0) {
						listLocationCode.add(i);
					}
				}
			}
		}
		selectedLocationAtIndex(listLocationCode.get(0));
		// 04.23 LuongGV add
		// this.refreshDisplay();
	}

	private void showDatePopup(ProductInfo p) {
		if (p == null) {
			return;
		}
		mTxtPopupDateProductTitle.setText(p.getHinNm());
		// Zenshou ntdat 20140417 add ++
		strHinCode = p.getHinCd();
		strHinName = p.getHinNm();
		// Zenshou ntdat 20140417 add --
		// image
		super.setImage(mImgPopupDateProductImage, p.getImageName(),
				R.drawable.no_image_191);
		// date
		mTxtPopupDateYear.setText(p.getYear());
		mTxtPopupDateMonth.setText(p.getMonth());
		mTxtPopupDateDay.setText(p.getDay());

		// show popup number
		super.showPopup(mLayoutPopupDate);
	}

	private void hideDatePopup() {
		String strYear = mTxtPopupDateYear.getText().toString().trim();
		String strMonth = mTxtPopupDateMonth.getText().toString().trim();
		String strDay = mTxtPopupDateDay.getText().toString().trim();
		int intYear = 0;
		int intMonth = 1;
		int intDay = 1;
		if (!StringUtils.isEmpty(strYear)) {
			intYear = Integer.parseInt(strYear);
		}
		if (!StringUtils.isEmpty(strMonth)) {
			intMonth = Integer.parseInt(strMonth);
		}
		if (!StringUtils.isEmpty(strDay)) {
			intDay = Integer.parseInt(strDay);
		}
		intYear += SukiyaContant.BASE_YEAR;

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, intYear);
		cal.set(Calendar.MONTH, intMonth - 1);
		cal.set(Calendar.DAY_OF_MONTH, intDay);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Date inputDate = cal.getTime();
		Date currentDate = super.app.getCurrentDate();
		if (inputDate.compareTo(currentDate) < 0) {
			super.showDialog(this, R.string.msg_014_product_expired,
					R.string.yes, gotoLossReportDialogListener, R.string.no,
					dismissDialogListener);
		} else {
			// get product
			if (this.indexProductUpdating >= 0
					&& this.indexProductUpdating <= this.displayProduct.size()) {
				ProductInfo product = this.displayProduct
						.get(this.indexProductUpdating);
				if (product != null) {
					product.setYear(strYear);
					product.setMonth(strMonth);
					product.setDay(strDay);

					String strDate = String.format("%s%s%s", strYear, strMonth,
							strDay);
					super.db = new DatabaseHelper(getApplicationContext());

					super.db.saveDate(strDate, menuInputHistoryId,
							product.getHinCd(), product.getOrgLocationCd(),
							product.getNisugataCd());

					super.db.close();

					this.refreshDisplay();
				}
			}
			// hide popup number
			super.hidePopup(mLayoutPopupDate);
		}
	}

	private void addDate(int field, int value) {
		String strYear = mTxtPopupDateYear.getText().toString().trim();
		String strMonth = mTxtPopupDateMonth.getText().toString().trim();
		String strDay = mTxtPopupDateDay.getText().toString().trim();
		int intYear = 0;
		int intMonth = 1;
		int intDay = 1;
		boolean isEmptyField = true;
		if (!StringUtils.isEmpty(strYear)) {
			intYear = Integer.parseInt(strYear);
			isEmptyField = false;
		}
		if (!StringUtils.isEmpty(strMonth)) {
			intMonth = Integer.parseInt(strMonth);
			isEmptyField = false;
		}
		if (!StringUtils.isEmpty(strDay)) {
			intDay = Integer.parseInt(strDay);
			isEmptyField = false;
		}
		intYear += SukiyaContant.BASE_YEAR;

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, intYear);
		cal.set(Calendar.MONTH, intMonth - 1);
		cal.set(Calendar.DAY_OF_MONTH, intDay);

		switch (field) {
		case Calendar.YEAR:
			cal.add(field, value);

			if (cal.get(Calendar.YEAR) < SukiyaContant.BASE_YEAR) {
				cal.set(Calendar.YEAR, SukiyaContant.BASE_YEAR + 99);
			} else if (cal.get(Calendar.YEAR) > (SukiyaContant.BASE_YEAR + 99)) {
				cal.set(Calendar.YEAR, SukiyaContant.BASE_YEAR);
			}
			break;
		case Calendar.MONTH:
			if (!isEmptyField) {
				cal.add(field, value);
			}

			if (intYear != cal.get(Calendar.YEAR)) {
				cal.set(Calendar.YEAR, intYear);
			}
			break;
		case Calendar.DAY_OF_MONTH:
			int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			if (value > 0) {
				if (!isEmptyField) {
					if (intDay < lastDayOfMonth) {
						cal.add(Calendar.DAY_OF_MONTH, value);
					} else {
						cal.set(Calendar.DAY_OF_MONTH, 1);
					}
				}
			} else {
				if (intDay == 1) {
					cal.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
				} else {
					cal.add(Calendar.DAY_OF_MONTH, value);
				}
			}
			break;
		default:
			break;
		}

		int currentYear = cal.get(Calendar.YEAR) % SukiyaContant.BASE_YEAR;
		mTxtPopupDateYear.setText(String.format("%02d", currentYear));
		mTxtPopupDateMonth.setText(String.format("%02d",
				cal.get(Calendar.MONTH) + 1));
		mTxtPopupDateDay.setText(String.format("%02d",
				cal.get(Calendar.DAY_OF_MONTH)));
	}

	private void gotoLossReport() {
		// Zenshou ntdat 20140417 add ++
		// dismiss process dialog
		this.dismissProcessDialog();

		// Goto S201Activity screen
		Intent intent = new Intent(this, S201Activity.class);

		Bundle bdlS02 = new Bundle();
		bdlS02.putBoolean(S02_KEY, true);
		bdlS02.putString(S02_HINCODE_KEY, strHinCode);
		bdlS02.putString(S02_HINNAME_KEY, strHinName);
		intent.putExtras(bdlS02);

		startActivity(intent);
		overridePendingTransition(R.animator.slide_in_right,
				R.animator.slide_out_left);
		// Zenshou ntdat 20140417 add --

	}

	private void calendarButtonClicked() {
		Calendar calToday = DateUtils.today();

		String strYear = mTxtPopupDateYear.getText().toString().trim();
		String strMonth = mTxtPopupDateMonth.getText().toString().trim();
		String strDay = mTxtPopupDateDay.getText().toString().trim();
		int intYear = 0;
		int intMonth = 1;
		int intDay = 1;
		if (!StringUtils.isEmpty(strYear)) {
			intYear = Integer.parseInt(strYear);
			intYear += SukiyaContant.BASE_YEAR;
		} else {
			intYear = calToday.get(Calendar.YEAR);
		}
		if (!StringUtils.isEmpty(strMonth)) {
			intMonth = Integer.parseInt(strMonth);
		} else {
			intMonth = calToday.get(Calendar.MONDAY) + 1;
		}
		if (!StringUtils.isEmpty(strDay)) {
			intDay = Integer.parseInt(strDay);
		} else {
			intDay = calToday.get(Calendar.DAY_OF_MONTH);
		}

		SukiyaCalendarDialog dialog = new SukiyaCalendarDialog(this);
		dialog.setListener(this);
		dialog.setDate(intYear, intMonth, intDay);
		dialog.show();
	}

	@Override
	public void selectedDate(Dialog dialog, int year, int month, int day) {
		if (dialog != null) {
			dialog.dismiss();
		}
		int intYear = year - SukiyaContant.BASE_YEAR;
		if (intYear < 0) {
			intYear = 0;
		} else if (intYear > 99) {
			intYear = 99;
		}
		mTxtPopupDateYear.setText(String.format("%02d", intYear));
		mTxtPopupDateMonth.setText(String.format("%02d", month));
		mTxtPopupDateDay.setText(String.format("%02d", day));
	}

	private void storeProductList(List<ProductInfo> productList) {
		if (menuInputHistoryId > 0) {
			String strVal = null;
			MenuPositionInfo selectedMenu = super.app.getSelectedMenu();
			super.db = new DatabaseHelper(getApplicationContext());
			if (SukiyaContant.MENU_04.equals(selectedMenu.getCode())) {
				for (ProductInfo productInfo : productList) {
					strVal = super.db.getDate(menuInputHistoryId,
							productInfo.getHinCd(),
							productInfo.getOrgLocationCd(),
							productInfo.getNisugataCd());
					if (strVal != null && strVal.length() == 6) {
						productInfo.setYear(strVal.substring(0, 2));
						productInfo.setMonth(strVal.substring(2, 4));
						productInfo.setDay(strVal.substring(4, 6));
					}
				}
			} else {
				for (ProductInfo productInfo : productList) {
					strVal = super.db.getVal(menuInputHistoryId,
							productInfo.getHinCd(),
							productInfo.getOrgLocationCd(),
							productInfo.getNisugataCd());
					if (!StringUtils.isEmpty(strVal)) {
						Double newAmount = Double.valueOf(strVal);

						productInfo.setActualStock(newAmount);
					}
				}
			}
			super.db.close();
		}
		super.app.setProductList(productList);
	}

	// Zenshou ntdat 20140418 add ++
	private void storeProductListToDB(List<ProductInfo> listProduct) {
		String menuCode = null;
		String startTime = null;
		String endTime = null;
		MenuPositionInfo mnPos = super.app.getSelectedMenu();
		if (mnPos != null) {
			menuCode = mnPos.getCode();

			MenuInfo mnInf = mnPos.getCurrentDisplayInfo();
			if (mnInf != null) {
				startTime = mnInf.getFrom();
				endTime = mnInf.getTo();
			}
		}
		Date currDate = super.app.getCurrentDate();

		super.db = new DatabaseHelper(getApplicationContext());

		// remove old data
		super.db.removeOldProduct(currDate);

		for (ProductInfo productInfo : listProduct) {
			productInfo.setUserAdd(0);
			// productInfo.setMenuType(strMenuType);
			// Add product item into database
			super.db.addProductInfo(productInfo, currDate, menuCode, startTime,
					endTime);
		}
		super.db.close();
	}

}
