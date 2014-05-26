package jp.co.zensho.android.sukiya;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.co.zensho.android.sukiya.bean.DailyCheckboxValue;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import jp.co.zensho.android.sukiya.bean.GroupProduct;
import jp.co.zensho.android.sukiya.bean.LocationInfo;
import jp.co.zensho.android.sukiya.bean.MenuInfo;
import jp.co.zensho.android.sukiya.bean.MenuPositionInfo;
import jp.co.zensho.android.sukiya.bean.ProductInfo;
import jp.co.zensho.android.sukiya.bean.TagInfo;
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
import jp.co.zensho.android.sukiya.service.PostAPIAsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class S04Activity extends S00Activity implements OnClickListener,
		CallAPIListener, OnSelectCalendarDailogListener {
	private static final String LOG = "S04Activity";

	private LayoutInflater myInflater;
	private TextView mTxtTitle;
	private Button mBtnComplete;
	private Button mBtnBack;
	private LinearLayout mLayoutContentScroll;

	// popup number
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

	// popup input part code
	private RelativeLayout mLayoutPopupPartCodeInput;
	private EditText mTxtPartCodeInput;
	private Button mBtnPartCodeInputCancel;
	private Button mBtnPartCodeInputRegist;

	private String clearanceState;
	private List<GroupProduct> productList;
	private String[] mItems;
	private int currentProductSelectIndex;
	private int currentLocationSelectIndex;
	private int currentDateSelectIndex;
	private String partCodeInputValue;
	private SukiyaNumberKeyboard customKeyboard;
	private SukiyaNumberKeyboard customKeyboardPartCode;
	private int menuInputHistoryId;
	// Zenshou ntdat 20140417 add ++
	public static String S04_HINCODE_KEY = "hin_code";
	public static String S04_HINNAME_KEY = "hin_name";
	public static String S04_KEY = "FROM_S02";
	private String strHinName = "";
	private String strHinCode = "";
	private String[] arrLocationCode;
	// add by thuc
	// help
	private Button mBtnHelp;
	private TextView mHelp;
	private LinearLayout mHelpLayout;
	private String helpCode = null;

	// Zenshou ntdat 20140417 add --
	// Regist part code dialog listener
	protected DialogInterface.OnClickListener registPartCodeDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}

			S04Activity.this.registPartCodeAction();
		}
	};

	// Reload shop info dialog listener
	private DialogInterface.OnClickListener gotoLossReportDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S04Activity.this.gotoLossReport();
		}
	};

	// Show part code dialog listener
	private DialogInterface.OnClickListener showPartCodeDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S04Activity.this.showPopupPartCodeInput();
		}
	};

	// Reload shop info dialog listener
	private DialogInterface.OnClickListener retryAPI008DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S04Activity.this.callAPI008();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// check clearance state
		String clearanceTitle = StringUtils.EMPTY;
		MenuPositionInfo selectedMenu = super.app.getSelectedMenu();
		clearanceState = SukiyaContant.TANA_EIHO;
		if (selectedMenu != null) {
			menuInputHistoryId = selectedMenu.getHistoryId();
			if (SukiyaContant.MENU_01.equals(selectedMenu.getCode())) {
				clearanceTitle = getString(R.string.tana_eiho);
				clearanceState = SukiyaContant.TANA_EIHO;
				helpCode = StringUtils.S021_CONFIRM_HELP_CD;
			} else if (SukiyaContant.MENU_02.equals(selectedMenu.getCode())
					&& selectedMenu.getCurrentDisplayInfo() != null) {
				String repeatState = selectedMenu.getCurrentDisplayInfo()
						.getRepeatState();
				helpCode = StringUtils.S021_CONFIRM_HELP_CD;
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
				clearanceTitle = getString(R.string.tana_hozai);
				clearanceState = SukiyaContant.TANA_HOZAI;
				helpCode = StringUtils.S023_CONFIRM_HELP_CD;
			} else if (SukiyaContant.MENU_04.equals(selectedMenu.getCode())) {
				clearanceTitle = getString(R.string.tana_shomikigen);
				clearanceState = SukiyaContant.TANA_SHOMIKIGEN;
				helpCode = StringUtils.S024_CONFIRM_HELP_CD;
			}
		}

		if (SukiyaContant.TANA_HOZAI.equals(clearanceState)) {
			setContentView(R.layout.s04_hozai);
		} else if (SukiyaContant.TANA_SHOMIKIGEN.equals(clearanceState)) {
			setContentView(R.layout.s104_shomikigen);
		} else {
			setContentView(R.layout.s04);
		}

		generateViews();
		this.myInflater = LayoutInflater.from(this);

		// Update title
		mTxtTitle = (TextView) findViewById(R.id.s04_txt_title);
		String strTitle = MessageFormat.format(
				getString(R.string.title_screen_04), clearanceTitle);
		mTxtTitle.setText(strTitle);

		mBtnComplete = (Button) findViewById(R.id.s04_btn_complete);
		mBtnComplete.setOnClickListener(this);

		mBtnBack = (Button) findViewById(R.id.s04_btn_back);
		mBtnBack.setOnClickListener(this);

		mLayoutContentScroll = (LinearLayout) findViewById(R.id.s04_lay_confirm_product_content_scroll);
		mBtnHelp = (Button) findViewById(R.id.btn_q);
		mHelp = (TextView) findViewById(R.id.help_content);
		mHelpLayout = (LinearLayout) findViewById(R.id.s02_help);
		mBtnHelp.setOnClickListener(this);
		mHelpLayout.setOnClickListener(this);
		// init popup number view
		this.initPopupNumber();

		// init date popup
		this.initPopupDate();

		// init popup part code input
		this.initPopupPartCode();

		List<LocationInfo> locationList = super.app.getLocationList();
		if (locationList != null && locationList.size() > 0) {
			mItems = new String[locationList.size()];
			arrLocationCode = new String[locationList.size()];

			LocationInfo location = null;
			for (int i = 0; i < locationList.size(); i++) {
				location = locationList.get(i);
				if (location != null) {
					mItems[i] = location.getName();
					arrLocationCode[i] = location.getCode();
				}
			}
		}

		this.initData();
	}

	// add by thuc
	private boolean checkExistProductItem(int position, String locCDToChanged,
			List<ProductInfo> lstProductInfo) {
		boolean isExist = false;
		ProductInfo productChangedLoc = lstProductInfo.get(position);
		// Map<ProductInfo, Integer> map = new HashMap<ProductInfo, Integer>();
		int size = lstProductInfo.size();

		// 04.22 LuongGV update wrong checking logic || Start
		// Find duplicate
		for (int i = 0; i < size; i++) {
			ProductInfo curProduct = lstProductInfo.get(i);
			if (!SukiyaContant.TANA_HOZAI.equals(clearanceState)) {
				if (i != position
						&& curProduct.getHinCd().equals(
								productChangedLoc.getHinCd())
						&& curProduct.getNisugataCd().equals(
								productChangedLoc.getNisugataCd())
						&& curProduct.getLocationCd().equals(locCDToChanged)) {
					isExist = true;
					return isExist;
				}
			} else {
				if (i != position
						&& curProduct.getHinCd().equals(
								productChangedLoc.getHinCd())
						&& curProduct.getLocationCd().equals(locCDToChanged)) {
					isExist = true;
					return isExist;
				}
			}
		}

		// 04.22 LuongGV update wrong checking logic || End

		return isExist;
	}

	/*
	 * 04.22 LuongGV deleted Start public int getPositionProduct() { int size =
	 * this.productList.size(); int position = 0; int total = 0; for (int i = 0;
	 * i < size; i++) {
	 *
	 * if (i == this.currentProductSelectIndex) { position = total +
	 * this.currentLocationSelectIndex; break; } total = total +
	 * this.productList.get(i).getProductList().size(); } return position; }
	 */

	private void initData() {
		if (super.app.isDisplayOnly()) {
			mBtnComplete.setEnabled(false);

			callAPI008();
		} else {
			List<ProductInfo> mainProductList = super.app.getProductList();
			if (productList == null) {
				productList = new ArrayList<GroupProduct>();
			}

			if (!SukiyaContant.TANA_SHOMIKIGEN.equals(clearanceState)
					&& mainProductList != null && mainProductList.size() > 0) {
				Integer index = 0;
				GroupProduct groupProduct = null;

				Map<String, Integer> productIdIndex = new HashMap<String, Integer>();

				for (ProductInfo productInfo : mainProductList) {
					if (productIdIndex.containsKey(productInfo.getHinCd())) {
						index = productIdIndex.get(productInfo.getHinCd());

						groupProduct = productList.get(index);
						groupProduct.addProduct(productInfo);
					} else {
						groupProduct = new GroupProduct();
						groupProduct.setProductId(productInfo.getHinCd());
						groupProduct.setProductName(productInfo.getHinNm());
						groupProduct
								.setProductImage(productInfo.getImageName());
						groupProduct.setUnit(productInfo.getDefaultTaniNm());

						groupProduct.addProduct(productInfo);

						this.productList.add(groupProduct);
						productIdIndex.put(productInfo.getHinCd(),
								(this.productList.size() - 1));
					}
				}
			}
			this.refreshView();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.confirm, menu);
		return true;
	}

	@Override
	protected void onStop() {
		super.removeUpdateCurrentDate();
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		// Zenshou ntdat 20140418 ++
		// switch (v.getId()) {
		// case R.id.s04_lay_add:
		//
		// getProductRowItem(new ProductInfo(), 1, 1);
		// break;
		// case R.id.s04_lay_del:
		// Toast.makeText(getApplicationContext(), "Delete",
		// Toast.LENGTH_SHORT).show();
		// break;
		//
		// default:
		// break;
		// }
		// Zenshou ntdat 20140418 --
		if (v != null) {
			if (v.equals(mHelpLayout)) {
				super.hidePopup(mHelpLayout);
			} else if (v.equals(mBtnHelp)) {
				super.getHelp(S04Activity.this, helpCode);
			} else if (mBtnPopupNumberClose.equals(v)) {
				this.closeNumberPopup();
			} else if (mBtnPopupNumberComplete.equals(v)) {
				this.fillInputNumberValue();
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
			} else if (v.equals(this.mBtnComplete)) {
				this.registAction();
			} else if (v.equals(this.mBtnBack)) {
				finish();
				overridePendingTransition(R.animator.slide_in_left,
						R.animator.slide_out_right);
			} else if (v.equals(this.mBtnPartCodeInputCancel)) {
				super.hidePopup(mLayoutPopupPartCodeInput);
			} else if (v.equals(this.mBtnPartCodeInputRegist)) {
				this.registPartCodeInputAction();
			} else {
				if (super.app.isDisplayOnly())
					return;

				Object tag = v.getTag();

				if (tag != null && tag instanceof TagInfo
						&& this.productList != null) {
					TagInfo tagInfo = (TagInfo) tag;
					if (TagInfo.DATE_TAG == tagInfo.getType()) {
						this.currentDateSelectIndex = tagInfo.getIndex1();
						List<ProductInfo> productList = super.app
								.getProductList();
						if (this.currentDateSelectIndex >= 0
								&& this.currentDateSelectIndex < productList
										.size()) {
							ProductInfo productInfo = productList
									.get(currentDateSelectIndex);
							showDatePopup(productInfo);
						}
					} else if (TagInfo.NUMBER_TAG == tagInfo.getType()) {
						this.currentProductSelectIndex = tagInfo.getIndex1();
						this.currentLocationSelectIndex = tagInfo.getIndex2();

						if (this.currentProductSelectIndex >= 0
								&& this.currentProductSelectIndex < this.productList
										.size()) {
							GroupProduct groupProduct = this.productList
									.get(this.currentProductSelectIndex);
							if (groupProduct.getProductList() != null
									&& this.currentLocationSelectIndex >= 0
									&& this.currentLocationSelectIndex < groupProduct
											.getProductList().size()) {
								ProductInfo productInfo = groupProduct
										.getProductList()
										.get(this.currentLocationSelectIndex);
								this.showNumerPopup(productInfo);
							}
						}
					} else if (TagInfo.LOCATION_TAG == tagInfo.getType()) {
						this.currentProductSelectIndex = tagInfo.getIndex1();
						this.currentLocationSelectIndex = tagInfo.getIndex2();
						// add by thuc

						if (this.currentProductSelectIndex >= 0
								&& this.currentProductSelectIndex < this.productList
										.size()) {
							GroupProduct groupProduct = this.productList
									.get(this.currentProductSelectIndex);
							if (groupProduct.getProductList() != null
									&& this.currentLocationSelectIndex >= 0
									&& this.currentLocationSelectIndex < groupProduct
											.getProductList().size()) {
								ProductInfo productInfo = groupProduct
										.getProductList()
										.get(this.currentLocationSelectIndex);

								this.showLocationDailog(productInfo
										.getLocationCd());
							}
						}
					} else if (TagInfo.ADD_PRODUCT_TAG == tagInfo.getType()) {
						this.currentProductSelectIndex = tagInfo.getIndex1();
						this.currentLocationSelectIndex = tagInfo.getIndex2();

						if (this.currentProductSelectIndex >= 0
								&& this.currentProductSelectIndex < this.productList
										.size()) {
							GroupProduct groupProduct = this.productList
									.get(this.currentProductSelectIndex);
							if (groupProduct.getProductList() != null
									&& this.currentLocationSelectIndex >= 0
									&& this.currentLocationSelectIndex < groupProduct
											.getProductList().size()) {
								List<ProductInfo> productList = groupProduct
										.getProductList();
								ProductInfo productInfo = productList
										.get(this.currentLocationSelectIndex);

								ProductInfo newProduct = new ProductInfo();
								newProduct.setDay(productInfo.getDay());
								newProduct.setDefaultKsnCnst(productInfo
										.getDefaultKsnCnst());
								newProduct.setDefaultKsnKbn(productInfo
										.getDefaultKsnKbn());
								newProduct.setDefaultLocationCd(productInfo
										.getDefaultLocationCd());
								newProduct.setDefaultNisugataCd(productInfo
										.getDefaultNisugataCd());
								newProduct.setDefaultTaniCd(productInfo
										.getDefaultTaniCd());
								newProduct.setDefaultTaniNm(productInfo
										.getDefaultTaniNm());
								newProduct.setDspOrder(productInfo
										.getDspOrder());
								newProduct.setExpirationDays(productInfo
										.getExpirationDays());
								newProduct.setExpirationText(productInfo
										.getExpirationText());
								newProduct.setHinCd(productInfo.getHinCd());
								newProduct.setHinNm(productInfo.getHinNm());
								newProduct.setImageName(productInfo
										.getImageName());
								newProduct.setIriSu(productInfo.getIriSu());
								newProduct.setKsnCnst(productInfo.getKsnCnst());
								newProduct.setKsnKbn(productInfo.getKsnKbn());
								newProduct.setMemo(productInfo.getMemo());
								newProduct.setMenuType(productInfo
										.getMenuType());
								newProduct.setMonth(productInfo.getMonth());
								newProduct.setNisugataCd(productInfo
										.getNisugataCd());
								newProduct.setNisugataImageName(productInfo
										.getNisugataImageName());
								newProduct.setNisugataNm(productInfo
										.getNisugataNm());
								newProduct.setLocationCd(productInfo
										.getLocationCd());
								newProduct.setOrgLocationCd(productInfo
										.getOrgLocationCd());
								newProduct.setTaniCd(productInfo.getTaniCd());
								newProduct.setTaniNm(productInfo.getTaniNm());
								newProduct.setTheoreticalStock(productInfo
										.getTheoreticalStock());
								newProduct.setUnitStock(productInfo
										.getUnitStock());
								newProduct.setUserAdd(1);
								newProduct.setYear(productInfo.getYear());

								// productList.add(
								// this.currentLocationSelectIndex + 1,
								// newProduct);
								productList.add(productList.size(), newProduct);

								super.app.getProductList().add(newProduct);

								String menuCode = null;
								String startTime = null;
								String endTime = null;
								MenuPositionInfo mnPos = super.app
										.getSelectedMenu();
								if (mnPos != null) {
									menuCode = mnPos.getCode();

									MenuInfo mnInf = mnPos
											.getCurrentDisplayInfo();
									if (mnInf != null) {
										startTime = mnInf.getFrom();
										endTime = mnInf.getTo();
									}
								}
								Date currDate = super.app.getCurrentDate();

								super.db = new DatabaseHelper(
										getApplicationContext());
								super.db.addProductInfo(newProduct, currDate,
										menuCode, startTime, endTime);
								super.db.close();

								refreshView();
							}
						}
					} else if (TagInfo.DEL_PRODUCT_TAG == tagInfo.getType()) {
						this.currentProductSelectIndex = tagInfo.getIndex1();
						this.currentLocationSelectIndex = tagInfo.getIndex2();
						GroupProduct groupProduct = this.productList
								.get(this.currentProductSelectIndex);
						if (groupProduct.getProductList() != null
								&& this.currentLocationSelectIndex >= 0
								&& this.currentLocationSelectIndex < groupProduct
										.getProductList().size()) {
							List<ProductInfo> productList = groupProduct
									.getProductList();
							ProductInfo productInfo = new ProductInfo();
							productInfo = productList
									.get(currentLocationSelectIndex);
							productList.remove(this.currentLocationSelectIndex);
							super.app.getProductList().remove(productInfo);

							super.db = new DatabaseHelper(
									getApplicationContext());
							super.db.deleteProductInfo(productInfo.getKey());
							super.db.close();

							refreshView();
						}
					}
				}
			}
		}
	}

	@Override
	public void callAPIFinish(String tag, JSONObject result, ErrorInfo error) {
		this.dismissProcessDialog();

		if (error == null && result != null) {
			try {
				if (JSONUtils.hasError(result)) {
					if (error == null) {
						error = JSONUtils.getFirstError(result);
					}
				} else {
					if (JSONUtils.API_008_PATH.equals(tag)) {
						List<ProductInfo> productList = JSONUtils
								.parseProductList(result);
						this.storeProductList(productList);
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
			super.dismissProcessDialog();

			String message = error.getMessage();
			if (!StringUtils.isEmpty(error.getCode())) {
				message = MessageFormat.format("{0}: {1}", error.getCode(),
						error.getMessage());
			}

			// show error dialog
			super.showDialog(this, message, R.string.try_again,
					registPartCodeDialogListener, R.string.cancel,
					dismissDialogListener);
		} else {
			if (JSONUtils.API_006_PATH.equals(tag)) {
				super.dismissProcessDialog();

				this.gotoTopScreen();
			} else if (JSONUtils.API_008_PATH.equals(tag)) {
				super.dismissProcessDialog();
				this.refreshView();
			} else if (!JSONUtils.API_043_PATH.equals(tag)) {
				this.saveRegistMenu();

				this.CallAPI006();
			}
		}
	}

	private void refreshView() {
		if (mLayoutContentScroll != null) {
			mLayoutContentScroll.removeAllViews();

			if (SukiyaContant.TANA_SHOMIKIGEN.equals(clearanceState)) {
				List<ProductInfo> productList = super.app.getProductList();
				if (productList != null && productList.size() > 0) {
					LinearLayout row = null;
					ProductInfo productInfo = null;
					for (int i = 0; i < productList.size(); i++) {
						productInfo = productList.get(i);

						row = getProductRowShomikigenItem(productInfo, i);
						mLayoutContentScroll.addView(row);
					}
				}
			} else {
				if (this.productList != null && this.productList.size() > 0) {
					LinearLayout row = null;
					GroupProduct groupProduct = null;
					for (int i = 0; i < this.productList.size(); i++) {
						groupProduct = this.productList.get(i);
						row = getProductGroupRowItem(groupProduct, i);

						mLayoutContentScroll.addView(row);
					}
				}
			}
		}
	}

	private void showNumerPopup(ProductInfo product) {
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

			// show popup number
			super.showPopup(mLayoutPopupNumber);
		}
	}

	private void closeNumberPopup() {
		// Close popup number
		super.hidePopup(mLayoutPopupNumber);
	}

	private void fillInputNumberValue() {
		if (this.productList == null || this.productList.size() == 0) {
			return;
		}

		String currentVal = mTxtPopupNumberProductAmount.getText().toString();
		if (StringUtils.isEmpty(currentVal)) {
			currentVal = StringUtils.ZERO;
		}
		Double newAmount = Double.valueOf(currentVal);

		// get product
		if (this.currentProductSelectIndex >= 0
				&& this.currentProductSelectIndex < this.productList.size()) {
			GroupProduct groupProduct = this.productList
					.get(this.currentProductSelectIndex);
			List<ProductInfo> groupProductList = groupProduct.getProductList();
			if (groupProductList != null
					&& this.currentLocationSelectIndex >= 0
					&& this.currentLocationSelectIndex <= groupProductList
							.size()) {
				ProductInfo productInfo = groupProductList
						.get(this.currentLocationSelectIndex);
				if (productInfo != null) {
					productInfo.setActualStock(newAmount);
					// Save to Database
					super.db = new DatabaseHelper(getApplicationContext());

					// super.db.saveNumber(currentVal, menuInputHistoryId,
					// productInfo.getHinCd(),
					// productInfo.getOrgLocationCd(),
					// productInfo.getNisugataCd());

					// Zenshou ntdat 20140422 add ++
					String menuCode = null;
					String startTime = null;
					String endTime = null;
					MenuPositionInfo mnPos = app.getSelectedMenu();
					if (mnPos != null) {
						menuCode = mnPos.getCode();

						MenuInfo mnInf = mnPos.getCurrentDisplayInfo();
						if (mnInf != null) {
							startTime = mnInf.getFrom();
							endTime = mnInf.getTo();
						}
					}
					Date currDate = app.getCurrentDate();
					super.db.updateProductInfo(productInfo.getKey(),
							productInfo, currDate, menuCode, startTime, endTime);
					// Zenshou ntdat 20140422 add --

					super.db.close();

					double total = 0f;
					for (ProductInfo item : groupProductList) {
						Log.d(LOG, "Product: " + item.getHinCd()
								+ " / Current: " + item.getActualStock()
								+ " / Unit: " + item.getUnitStock());
						if (item.getUnitStock() != null) {
							total += item.getUnitStock();
						}
					}
					groupProduct.setTotal(total);

					// refresh group product
					if (this.mLayoutContentScroll != null) {
						if (this.currentProductSelectIndex >= 0
								&& this.currentProductSelectIndex < this.mLayoutContentScroll
										.getChildCount()) {
							View view = this.mLayoutContentScroll
									.getChildAt(this.currentProductSelectIndex);
							if (view != null) {
								LinearLayout locationContainer = (LinearLayout) view
										.findViewById(R.id.s04_lay_product_location_container);
								if (locationContainer != null) {
									if (this.currentLocationSelectIndex >= 0
											&& this.currentLocationSelectIndex < locationContainer
													.getChildCount()) {
										View subView = locationContainer
												.getChildAt(this.currentLocationSelectIndex);
										if (subView != null) {
											TextView amount = (TextView) subView
													.findViewById(R.id.s04_txt_col_3);
											if (amount != null) {
												amount.setText(strDouble(newAmount));
											}
										}
									}
								}

								TextView productTotal = (TextView) view
										.findViewById(R.id.txt_product_total_review_item);
								if (productTotal != null
										&& groupProduct.getTotal() != null) {
									productTotal.setText(strDouble(groupProduct
											.getTotal()));
								}
							}
						}
					}
				}
			}
		}

		// Close popup number
		this.closeNumberPopup();
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

	private TagInfo makeLocationTag(int rowIndex, int subRowIndex) {
		return new TagInfo(TagInfo.LOCATION_TAG, rowIndex, subRowIndex);
	}

	private TagInfo makeAmountTag(int rowIndex, int subRowIndex) {
		return new TagInfo(TagInfo.NUMBER_TAG, rowIndex, subRowIndex);
	}

	private TagInfo makeAddProductTag(int rowIndex, int subRowIndex) {
		return new TagInfo(TagInfo.ADD_PRODUCT_TAG, rowIndex, subRowIndex);
	}

	private TagInfo makeDelProductTag(int rowIndex, int subRowIndex) {
		return new TagInfo(TagInfo.DEL_PRODUCT_TAG, rowIndex, subRowIndex);
	}

	private TagInfo makeDateTag(int rowIndex) {
		return new TagInfo(TagInfo.DATE_TAG, rowIndex);
	}

	private void showLocationDailog(String locationCode) {
		int index = 0;
		if (locationCode != null) {

			List<LocationInfo> locationList = super.app.getLocationList();
			if (locationList != null && locationList.size() > 0) {
				LocationInfo locationInfo = null;
				for (int i = 0; i < locationList.size(); i++) {
					locationInfo = locationList.get(i);
					if (locationCode.equals(locationInfo.getCode())) {
						index = i;
						break;
					}
				}
			}
		}
		Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(mItems, index,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (checkExistProductItem(currentLocationSelectIndex,
								app.getLocationList().get(arg1).getCode(),
								productList.get(currentProductSelectIndex)
										.getProductList()) == false) {
							S04Activity.this.fillLocationIndex(arg1);
							arg0.dismiss();
						} else {
							Toast.makeText(getApplicationContext(),
									getString(R.string.msg_016_loc_exist),
									Toast.LENGTH_LONG).show();
						}
						// this.selectedLocationAtIndex(arg1);
					}

				});
		super.alert = builder.show();
	}

	private void fillLocationIndex(int index) {
		if (this.productList == null || this.productList.size() == 0) {
			return;
		}

		List<LocationInfo> locationList = super.app.getLocationList();
		LocationInfo locationInfo = null;
		if (index >= 0 && index < locationList.size()) {
			locationInfo = locationList.get(index);
		}
		if (locationInfo == null) {
			return;
		}

		// get product
		if (this.currentProductSelectIndex >= 0
				&& this.currentProductSelectIndex < this.productList.size()) {
			GroupProduct groupProduct = this.productList
					.get(this.currentProductSelectIndex);
			List<ProductInfo> groupProductList = groupProduct.getProductList();
			if (groupProductList != null
					&& this.currentLocationSelectIndex >= 0
					&& this.currentLocationSelectIndex <= groupProductList
							.size()) {
				ProductInfo productInfo = groupProductList
						.get(this.currentLocationSelectIndex);
				if (productInfo != null) {
					productInfo.setLocationCd(locationInfo.getCode());

					// refresh group product
					if (this.mLayoutContentScroll != null) {
						if (this.currentProductSelectIndex >= 0
								&& this.currentProductSelectIndex < this.mLayoutContentScroll
										.getChildCount()) {
							View view = this.mLayoutContentScroll
									.getChildAt(this.currentProductSelectIndex);
							if (view != null) {
								LinearLayout locationContainer = (LinearLayout) view
										.findViewById(R.id.s04_lay_product_location_container);
								if (locationContainer != null) {
									if (this.currentLocationSelectIndex >= 0
											&& this.currentLocationSelectIndex < locationContainer
													.getChildCount()) {
										View subView = locationContainer
												.getChildAt(this.currentLocationSelectIndex);
										if (subView != null) {
											TextView location = (TextView) subView
													.findViewById(R.id.s04_txt_col_2);
											if (location != null) {
												location.setText(locationInfo
														.getName());
											}
										}
									}
								}
							}
						}
					}
					// Zenshou ntdat 20140422 add ++
					String menuCode = null;
					String startTime = null;
					String endTime = null;
					MenuPositionInfo mnPos = app.getSelectedMenu();
					if (mnPos != null) {
						menuCode = mnPos.getCode();

						MenuInfo mnInf = mnPos.getCurrentDisplayInfo();
						if (mnInf != null) {
							startTime = mnInf.getFrom();
							endTime = mnInf.getTo();
						}
					}
					Date currDate = app.getCurrentDate();
					DatabaseHelper db = new DatabaseHelper(
							getApplicationContext());
					db.updateProductInfo(productInfo.getKey(), productInfo,
							currDate, menuCode, startTime, endTime);
					db.close();
					// Zenshou ntdat 20140422 add --
				}
			}
		}
	}

	private void registPartCodeInputAction() {
		String message = null;

		String value = mTxtPartCodeInput.getText().toString().trim();
		if (!StringUtils.isEmpty(value)) {
			try {
				this.partCodeInputValue = value;

				super.showDialog(this,
						R.string.msg_009_part_code_input_confirm, R.string.yes,
						registPartCodeDialogListener, R.string.no,
						dismissDialogListener);
			} catch (NumberFormatException nfe) {
				message = getString(R.string.msg_008_part_code_input_not_number);
			}
		} else {
			message = getString(R.string.msg_007_part_code_input_not_empty);
		}

		if (!StringUtils.isEmpty(message)) {
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
	}

	private void showPopupPartCodeInput() {
		this.partCodeInputValue = StringUtils.EMPTY;
		mTxtPartCodeInput.setText(StringUtils.EMPTY);

		super.showPopup(mLayoutPopupPartCodeInput);
	}

	private void CallAPI006() {
		List<DailyCheckboxValue> dailyCheckboxList = super.app
				.getDailyCheckboxList();
		if (dailyCheckboxList != null && dailyCheckboxList.size() > 0) {
			StringBuilder url = new StringBuilder();
			url.append(JSONUtils.API_HOST_PATH);
			url.append(JSONUtils.API_006_PATH);

			List<NameValuePair> data = new ArrayList<NameValuePair>();
			data.add(new BasicNameValuePair(JSONUtils.TENPO_CODE, super.app
					.getShopCode()));

			super.db = new DatabaseHelper(getApplicationContext());
			DailyCheckboxValue value;
			int j = 0;
			int checkedValue = 0;
			StringBuilder paramKey = null;
			for (int i = 0; i < dailyCheckboxList.size(); i++) {
				value = dailyCheckboxList.get(i);
				if (value != null) {
					if (value.isSelected()) {
						// update database
						super.db.saveCategoryHistory(value.getCode());
						value.setCanUnselect(false);

						checkedValue = 1;
					} else {
						checkedValue = 0;
					}

					paramKey = new StringBuilder();
					paramKey.append(JSONUtils.API_006_PARAMS_KEY);
					paramKey.append("[").append(j).append("]");
					paramKey.append("[").append(JSONUtils.SND_HIN_CODE)
							.append("]");
					data.add(new BasicNameValuePair(paramKey.toString(), String
							.valueOf(value.getCode())));

					paramKey = new StringBuilder();
					paramKey.append(JSONUtils.API_006_PARAMS_KEY);
					paramKey.append("[").append(j).append("]");
					paramKey.append("[").append(JSONUtils.CHECKED).append("]");
					data.add(new BasicNameValuePair(paramKey.toString(), String
							.valueOf(checkedValue)));

					j++;
				}
			}
			super.db.close();

			new PostAPIAsyncTask(this, data, JSONUtils.API_006_PATH)
					.execute(url.toString());
		}
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
					this.refreshView();
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

	private void callAPI009() {
		if (StringUtils.isEmpty(this.partCodeInputValue)) {
			return;
		}
		List<ProductInfo> productInfoList = super.app.getProductList();
		if (productInfoList == null || productInfoList.size() <= 0) {
			return;
		}
		// make url
		StringBuilder url = new StringBuilder();
		url.append(JSONUtils.API_HOST_PATH);
		url.append(JSONUtils.API_009_PATH);

		// make parameter
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair(JSONUtils.TENPO_CODE, super.app
				.getShopCode()));
		data.add(new BasicNameValuePair(JSONUtils.CLEARANCE_TYPE,
				this.clearanceState));

		ProductInfo productInfo = null;
		StringBuilder paramKey = null;
		for (int i = 0; i < productInfoList.size(); i++) {
			productInfo = productInfoList.get(i);

			// tenpo_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TENPO_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super.app
					.getShopCode()));

			// egy_date
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.EGY_DATE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super
					.strCurrentDate()));

			// kento_date
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.KENTO_DATE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super
					.strRealtDate()));

			// time_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TIME_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super
					.getTimeCode()));

			// hin_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.HIN_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getHinCd()));

			// nisugata_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.NISUGATA_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getNisugataCd()));

			// org_location_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.ORG_LOCATION_CD).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getOrgLocationCd()));

			// location_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.LOCATION_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getLocationCd()));

			// zaiko_su
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.ZAIKO_SU).append("]");
			if (productInfo.getActualStock() == null) {
				data.add(new BasicNameValuePair(paramKey.toString(), "-1"));
			} else {
				data.add(new BasicNameValuePair(paramKey.toString(),
						strDouble(productInfo.getActualStock())));
			}

			// tana_zaiko
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TANA_ZAIKO).append("]");
			if (productInfo.getActualStock() == null) {
				data.add(new BasicNameValuePair(paramKey.toString(), "-1"));
			} else {
				data.add(new BasicNameValuePair(paramKey.toString(),
						strDouble(productInfo.getActualStock())));
			}

			// part_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.PART_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(),
					this.partCodeInputValue));

			// default_tani_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.DEFAULT_TANI_CODE)
					.append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getDefaultTaniCd()));

			// default_ksn_kbn
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.DEFAULT_KSN_KBN).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getDefaultKsnKbn()));

			// default_ksn_cnst
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.DEFAULT_KSN_CNST).append("]");
			if (productInfo.getDefaultKsnCnst() == null) {
				data.add(new BasicNameValuePair(paramKey.toString(),
						StringUtils.EMPTY));
			} else {
				data.add(new BasicNameValuePair(paramKey.toString(),
						productInfo.getDefaultKsnCnst().toString()));
			}

			// tani_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TANI_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getTaniCd()));

			// ksn_kbn
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.KSN_KBN).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getKsnKbn()));

			// ksn_cnst
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.KSN_CNST).append("]");
			if (productInfo.getKsnCnst() == null) {
				data.add(new BasicNameValuePair(paramKey.toString(),
						StringUtils.EMPTY));
			} else {
				data.add(new BasicNameValuePair(paramKey.toString(),
						productInfo.getKsnCnst().toString()));
			}

			// default_nisugata_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.DEFAULT_NISUGATA_CODE)
					.append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getDefaultNisugataCd()));
			// theoretical_stock
			
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.THEORETICAL_STOCK)
					.append("]");
		   //2014/05/22 add by ltthuc	
			if(productInfo.getTheoreticalStock()==null){
				productInfo.setTheoreticalStock(0d);
			}
			//end add 
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo.getTheoreticalStock().toString()));
		}
		// Show project dialog
		this.showProcessDialog(this, null, null);

		new PostAPIAsyncTask(this, data, JSONUtils.API_009_PATH).execute(url
				.toString());
	}

	private void callAPI011() {
		if (StringUtils.isEmpty(this.partCodeInputValue)) {
			return;
		}
		List<ProductInfo> productInfoList = super.app.getProductList();
		if (productInfoList == null || productInfoList.size() <= 0) {
			return;
		}
		// make url
		StringBuilder url = new StringBuilder();
		url.append(JSONUtils.API_HOST_PATH);
		url.append(JSONUtils.API_011_PATH);

		// make parameter
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair(JSONUtils.TENPO_CODE, super.app
				.getShopCode()));

		ProductInfo productInfo = null;
		StringBuilder paramKey = null;
		for (int i = 0; i < productInfoList.size(); i++) {
			productInfo = productInfoList.get(i);

			// tenpo_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_011_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TENPO_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super.app
					.getShopCode()));

			// kento_date
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_011_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.KENTO_DATE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super
					.strRealtDate()));

			// hin_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_011_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.HIN_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getHinCd()));

			// nisugata_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_011_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.NISUGATA_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getNisugataCd()));

			// org_location_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_011_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.ORG_LOCATION_CD).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getOrgLocationCd()));

			// location_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_011_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.LOCATION_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getLocationCd()));

			// tana_zaiko
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_011_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TANA_ZAIKO).append("]");
			if (productInfo.getActualStock() == null) {
				data.add(new BasicNameValuePair(paramKey.toString(), "-1"));
			} else {
				data.add(new BasicNameValuePair(paramKey.toString(),
						strDouble(productInfo.getActualStock())));
			}

			// ksn_kbn
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.KSN_KBN).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getKsnKbn()));

			// ksn_cnst
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_009_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.KSN_CNST).append("]");
			if (productInfo.getKsnCnst() == null) {
				data.add(new BasicNameValuePair(paramKey.toString(),
						StringUtils.EMPTY));
			} else {
				data.add(new BasicNameValuePair(paramKey.toString(),
						productInfo.getKsnCnst().toString()));
			}

			// part_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_011_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.PART_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(),
					this.partCodeInputValue));

			// default_tani_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_011_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.DEFAULT_TANI_CODE)
					.append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getDefaultTaniCd()));

			// default_ksn_kbn
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_011_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.DEFAULT_KSN_KBN).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getDefaultKsnKbn()));

			// default_ksn_cnst
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_011_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.DEFAULT_KSN_CNST).append("]");
			if (productInfo.getDefaultKsnCnst() == null) {
				data.add(new BasicNameValuePair(paramKey.toString(),
						StringUtils.EMPTY));
			} else {
				data.add(new BasicNameValuePair(paramKey.toString(),
						productInfo.getDefaultKsnCnst().toString()));
			}

			// tani_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_011_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TANI_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getTaniCd()));

			// ksn_kbn
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_011_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.KSN_KBN).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getKsnKbn()));

			// ksn_cnst
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_011_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.KSN_CNST).append("]");
			if (productInfo.getKsnCnst() == null) {
				data.add(new BasicNameValuePair(paramKey.toString(),
						StringUtils.EMPTY));
			} else {
				data.add(new BasicNameValuePair(paramKey.toString(),
						productInfo.getKsnCnst().toString()));
			}

			// default_nisugata_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_011_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.DEFAULT_NISUGATA_CODE)
					.append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getDefaultNisugataCd()));
		}
		// Show project dialog
		this.showProcessDialog(this, null, null);

		new PostAPIAsyncTask(this, data, JSONUtils.API_011_PATH).execute(url
				.toString());
	}

	private void callAPI013() {
		if (StringUtils.isEmpty(this.partCodeInputValue)) {
			return;
		}
		List<ProductInfo> productInfoList = super.app.getProductList();
		if (productInfoList == null || productInfoList.size() <= 0) {
			return;
		}
		// make url
		StringBuilder url = new StringBuilder();
		url.append(JSONUtils.API_HOST_PATH);
		url.append(JSONUtils.API_013_PATH);

		// make parameter
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair(JSONUtils.TENPO_CODE, super.app
				.getShopCode()));

		ProductInfo productInfo = null;
		StringBuilder paramKey = null;
		for (int i = 0; i < productInfoList.size(); i++) {
			productInfo = productInfoList.get(i);

			// tenpo_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_013_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TENPO_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super.app
					.getShopCode()));

			// kento_date
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_013_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.KENTO_DATE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super
					.strRealtDate()));

			// hin_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_013_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.HIN_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getHinCd()));

			// shomikigen
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_013_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.SHOMIKIGEN).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getShortDateString()));

			// part_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_013_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.PART_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(),
					this.partCodeInputValue));

		}
		// Show project dialog
		this.showProcessDialog(this, null, null);

		new PostAPIAsyncTask(this, data, JSONUtils.API_013_PATH).execute(url
				.toString());
	}

	private void gotoTopScreen() {
		String toastMessage = getString(R.string.msg_012_registed);
		Toast toast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		Intent intent = new Intent(this, S01Activity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		overridePendingTransition(R.animator.slide_in_left,
				R.animator.slide_out_right);
	}

	private void registAction() {
		int errorCode = 0;
		String errorStr = "";
		List<ProductInfo> productInfoList = super.app.getProductList();
		if (productInfoList != null && productInfoList.size() > 0) {
			if (SukiyaContant.TANA_SHOMIKIGEN.equals(clearanceState)) {
				for (ProductInfo productInfo : productInfoList) {
					if (productInfo.isEmptyDate()) {
						errorCode = 2;
						break;
					}
				}
			} else {
				for (int i = 0; i < productInfoList.size() ; i++) {
					ProductInfo pOf_I = productInfoList.get(i);
					if (pOf_I.getActualStock() == null
							|| pOf_I.getActualStock() < 0) {
						errorCode = 2;
						break;
					}
					// start from the next item after strings[i]
					// since the ones before are checked
					for (int j = i + 1; j < productInfoList.size(); j++) {
						ProductInfo pOf_J = productInfoList.get(j);
						// no need for if ( i == j ) here
						if (StringUtils.isEmpty(pOf_I.getLocationCd())) {
							errorCode = 1;
							break;
						} else if (pOf_I.getHinCd().equals(pOf_J.getHinCd())
								&& pOf_I.getNisugataCd().equals(
										pOf_J.getNisugataCd())
								&& pOf_I.getLocationCd().equals(
										pOf_J.getLocationCd())) {
							errorCode = 3;
							errorStr = MessageFormat
									.format(getString(R.string.msg_017_duplicate_record),
											pOf_I.getHinNm(),
											pOf_I.getNisugataNm());
							break;
						}
					} // for j
				} // for i
			}
		}
		if (SukiyaContant.TANA_SHOMIKIGEN.equals(clearanceState)) {
			if (errorCode == 2) {
				super.showDialog(this, R.string.msg_015_has_product_not_input,
						R.string.yes, showPartCodeDialogListener, 0, null);
			} else {
				this.showPopupPartCodeInput();
			}
		} else {
			if (errorCode == 1) {
				String toastMessage = getString(R.string.msg_010_please_input_location);
				Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
			} else if (errorCode == 2) {
				String toastMessage = getString(R.string.msg_011_please_input_stock);
				Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
			} else if (errorCode == 3) {
				Toast.makeText(this, errorStr, Toast.LENGTH_SHORT).show();
			} else {
				this.showPopupPartCodeInput();
			}
		}
	}

	private void saveRegistMenu() {
		SimpleDateFormat sdf = new SimpleDateFormat(
				StringUtils.DATE_TIME_LAST_UPDATE, Locale.JAPAN);
		Date today = new Date();

		String menuId = super.app.getSelectedMenu().getCode();
		if (SukiyaContant.MENU_02.equals(menuId)) {
			MenuInfo menuInfo = super.app.getSelectedMenu()
					.getCurrentDisplayInfo();
			if (menuId != null) {
				StringBuilder builder = new StringBuilder();
				builder.append(menuId);
				builder.append(menuInfo.getTimeCode());
				builder.append(menuInfo.getPriority());

				menuId = builder.toString();
			}
		}
		String lastUpdate = sdf.format(today);

		MenuPositionInfo pos = super.app.getSelectedMenu();
		MenuInfo menu = pos.getCurrentDisplayInfo();

		String menuCode = pos.getCode();
		String timeCode = menu.getTimeCode();
		int priority = menu.getPriority();
		String strDate = DateUtils.getYYYYMMDD(super.app.getCurrentDate());
		String from = menu.getFrom();
		String to = menu.getTo();

		super.db = new DatabaseHelper(getApplicationContext());
		super.db.updateMenuHistory(menuId, lastUpdate);
		super.db.setSubmitedDataMenu(menuCode, this.clearanceState, timeCode,
				priority, strDate, from, to);
		super.db.close();
	}

	private LinearLayout getProductGroupRowItem(GroupProduct groupProduct,
			int index) {
		List<ProductInfo> locationList = null;
		ProductInfo productInfo = null;
		locationList = groupProduct.getProductList();

		TextView productTitle = null;
		ImageView productImage = null;
		TextView productTotal = null;
		TextView productUnit1 = null;
		TextView productUnit2 = null;
		TextView productUnit3 = null;
		LinearLayout layoutLocationContent = null;
		LinearLayout locationViewItem = null;
		LinearLayout row = new LinearLayout(this);
		int blankViewWidth = 695;
		if (SukiyaContant.TANA_HOZAI.equals(clearanceState)) {
			blankViewWidth = 516;
			row = (LinearLayout) this.myInflater.inflate(
					R.layout.s104_product_row_item_hozai, null);
		} else {
			blankViewWidth = 695;
			row = (LinearLayout) this.myInflater.inflate(
					R.layout.product_review_item, null);
		}

		productTitle = (TextView) row
				.findViewById(R.id.txt_product_name_review);
		if (productTitle != null) {
			productTitle.setText(groupProduct.getProductName());
		}

		productImage = (ImageView) row
				.findViewById(R.id.img_product_image_review_item);
		super.setImage(productImage, groupProduct.getProductImage(),
				R.drawable.no_image_191);

		productUnit1 = (TextView) row
				.findViewById(R.id.txt_product_unit_1_review_item);
		if (productUnit1 != null) {
			productUnit1.setText(groupProduct.getUnit());
		}

		productUnit2 = (TextView) row
				.findViewById(R.id.txt_product_unit_2_review_item);
		if (productUnit2 != null) {
			productUnit2.setText(groupProduct.getUnit());
		}

		productUnit3 = (TextView) row
				.findViewById(R.id.txt_product_unit_3_review_item);
		if (productUnit3 != null) {
			productUnit3.setText(groupProduct.getUnit());
		}

		Double total = Double.parseDouble(StringUtils.ZERO);
		layoutLocationContent = (LinearLayout) row
				.findViewById(R.id.s04_lay_product_location_container);
		if (layoutLocationContent != null && locationList != null
				&& locationList.size() > 0) {
			int j = 0;
			for (j = 0; j < locationList.size(); j++) {
				productInfo = locationList.get(j);

				if (productInfo.getUnitStock() != null) {
					total += productInfo.getUnitStock();
				}

				locationViewItem = getProductRowItem(productInfo, index, j);

				if (j > 0) {
					locationViewItem.setPadding(0, 2, 0, 0);
				}

				layoutLocationContent.addView(locationViewItem);
			}
			// add blank view
			if (j == 1) {
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						blankViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
				layoutParams.setMargins(0, 2, 0, 0);

				locationViewItem = new LinearLayout(this);
				locationViewItem.setPadding(2, 0, 0, 0);
				locationViewItem.setBackgroundResource(R.color.white);
				locationViewItem.setLayoutParams(layoutParams);
				layoutLocationContent.addView(locationViewItem);
			}
		}
		groupProduct.setTotal(total);

		productTotal = (TextView) row
				.findViewById(R.id.txt_product_total_review_item);
		if (productTotal != null && groupProduct.getTotal() != null) {
			productTotal.setText(strDouble(groupProduct.getTotal()));
		}

		return row;
	}

	private LinearLayout getProductRowShomikigenItem(ProductInfo productInfo,
			int index) {
		TextView productTitle = null;
		ImageView productImage = null;
		TextView productDate = null;
		LinearLayout productDateButton = null;
		TextView countInput = null;
		TextView expirationDate = null;
		LinearLayout row = new LinearLayout(this);
		row = (LinearLayout) this.myInflater.inflate(
				R.layout.s104_product_row_item_shomikigen, null);

		productTitle = (TextView) row
				.findViewById(R.id.txt_product_name_review);
		if (productTitle != null) {
			productTitle.setText(productInfo.getHinNm());
		}

		productImage = (ImageView) row
				.findViewById(R.id.img_product_image_review_item);
		super.setImage(productImage, productInfo.getImageName(),
				R.drawable.no_image_191);

		productDate = (TextView) row
				.findViewById(R.id.txt_product_date_review_item);
		if (productDate != null) {
			productDate.setText(productInfo
					.getFullDateString(getApplicationContext()));
		}

		productDateButton = (LinearLayout) row
				.findViewById(R.id.lay_product_date_review_item);
		if (productDateButton != null) {
			productDateButton.setTag(makeDateTag(index));
			productDateButton.setOnClickListener(this);
		}

		countInput = (TextView) row
				.findViewById(R.id.txt_product_count_input_review_item);
		if (countInput != null) {
			if (productInfo.getIriSu() == null) {
				countInput.setText(StringUtils.EMPTY);
			} else {
				countInput.setText(productInfo.getIriSu().toString());
			}
		}

		expirationDate = (TextView) row
				.findViewById(R.id.txt_product_expiration_date_review_item);
		if (expirationDate != null) {
			expirationDate.setText(productInfo.getExpirationText());
		}

		return row;
	}

	private LinearLayout getProductRowItem(ProductInfo productInfo,
			int section, int index) {
		LinearLayout locarionRow = null;
		TextView txtLocationCol1 = null;
		TextView txtLocationCol2 = null;
		LinearLayout btnLocationCol2 = null;
		TextView txtLocationCol3 = null;
		LinearLayout btnLocationCol3 = null;
		TextView txtLocationCol4 = null;
		// Zenshou ntdat 20140418 add ++
		LinearLayout llAdd = null;
		LinearLayout llDel = null;
		ImageView btnAdd = null;
		ImageView btnDel = null;
		// Zenshou ntdat 20140418 add --

		String locationName = null;
		locarionRow = new LinearLayout(this);
		if (SukiyaContant.TANA_HOZAI.equals(clearanceState)) {
			locarionRow = (LinearLayout) this.myInflater.inflate(
					R.layout.s104_location_row_item_hozai, null);
		} else {
			locarionRow = (LinearLayout) this.myInflater.inflate(
					R.layout.s04_subview_location_item, null);
		}

		txtLocationCol1 = (TextView) locarionRow
				.findViewById(R.id.s04_txt_col_1);
		if (txtLocationCol1 != null && productInfo.getNisugataNm() != null) {
			txtLocationCol1.setText(productInfo.getNisugataNm());
		}

		txtLocationCol2 = (TextView) locarionRow
				.findViewById(R.id.s04_txt_col_2);
		if (txtLocationCol2 != null) {
			locationName = super.app.getLoactionName(productInfo
					.getLocationCd());
			txtLocationCol2.setText(locationName);
		}
		btnLocationCol2 = (LinearLayout) locarionRow
				.findViewById(R.id.s04_lay_col_2);
		if (btnLocationCol2 != null) {
			btnLocationCol2.setTag(makeLocationTag(section, index));
			btnLocationCol2.setOnClickListener(this);
		}

		txtLocationCol3 = (TextView) locarionRow
				.findViewById(R.id.s04_txt_col_3);
		if (txtLocationCol3 != null && productInfo.getActualStock() != null) {
			txtLocationCol3.setText(strDouble(productInfo.getActualStock()));
		}
		btnLocationCol3 = (LinearLayout) locarionRow
				.findViewById(R.id.s04_lay_col_3);
		if (btnLocationCol3 != null) {
			btnLocationCol3.setTag(makeAmountTag(section, index));
			btnLocationCol3.setOnClickListener(this);
		}

		txtLocationCol4 = (TextView) locarionRow
				.findViewById(R.id.s04_txt_col_4);
		if (txtLocationCol4 != null) {
			txtLocationCol4.setText(productInfo.getTaniNm());
		}
		// Zenshou ntdat 20140418 add ++
		llAdd = (LinearLayout) locarionRow.findViewById(R.id.s04_lay_add);
		llDel = (LinearLayout) locarionRow.findViewById(R.id.s04_lay_del);
		btnAdd = (ImageView) locarionRow.findViewById(R.id.s04_btn_add);
		btnDel = (ImageView) locarionRow.findViewById(R.id.s04_btn_del);
		if (btnAdd != null) {
			btnAdd.setTag(makeAddProductTag(section, index));
			btnAdd.setOnClickListener(this);
		}
		if (btnDel != null) {
			btnDel.setTag(makeDelProductTag(section, index));
			btnDel.setOnClickListener(this);
		}
		if (productInfo.getUserAdd() == 1) {
			llAdd.setVisibility(View.GONE);
			llDel.setVisibility(View.VISIBLE);
		}
		// Zenshou ntdat 20140418 add --

		return locarionRow;
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

	public void initPopupPartCode() {
		mLayoutPopupPartCodeInput = (RelativeLayout) findViewById(R.id.p06_input_part_code);
		mTxtPartCodeInput = (EditText) mLayoutPopupPartCodeInput
				.findViewById(R.id.p06_txt_input_part_code);
		mBtnPartCodeInputCancel = (Button) mLayoutPopupPartCodeInput
				.findViewById(R.id.p06_btn_cancel_part_code_input);
		mBtnPartCodeInputRegist = (Button) mLayoutPopupPartCodeInput
				.findViewById(R.id.p06_btn_regist_part_code_input);

		customKeyboardPartCode = new SukiyaNumberKeyboard(this,
				R.id.keyboardviewPartcode, R.xml.keyboard_layout);
		customKeyboardPartCode.setmPartCodeMode(true);
		customKeyboardPartCode.registerEditText(R.id.p06_txt_input_part_code);

		// Set listener
		mBtnPartCodeInputCancel.setOnClickListener(this);
		mBtnPartCodeInputRegist.setOnClickListener(this);
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
			List<ProductInfo> productList = super.app.getProductList();
			if (productList != null) {
				if (this.currentDateSelectIndex >= 0
						&& this.currentDateSelectIndex <= productList.size()) {
					ProductInfo product = productList
							.get(this.currentDateSelectIndex);
					if (product != null) {
						product.setYear(strYear);
						product.setMonth(strMonth);
						product.setDay(strDay);

						String strDate = String.format("%s%s%s", strYear,
								strMonth, strDay);
						super.db = new DatabaseHelper(getApplicationContext());

						super.db.saveDate(strDate, menuInputHistoryId,
								product.getHinCd(), product.getOrgLocationCd(),
								product.getNisugataCd());

						super.db.close();

						refreshView();
					}
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
		Bundle bdlS04 = new Bundle();
		bdlS04.putBoolean(S04_KEY, true);
		bdlS04.putString(S04_HINCODE_KEY, strHinCode);
		bdlS04.putString(S04_HINNAME_KEY, strHinName);
		intent.putExtras(bdlS04);
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
		Log.e("Check date", intDay + "");
		SukiyaCalendarDialog dialog = new SukiyaCalendarDialog(this);
		dialog.setListener(this);
		dialog.setDate(intYear, intMonth, intDay);
		dialog.show();
	}

	private void registPartCodeAction() {
		if (SukiyaContant.TANA_SHOMIKIGEN.equals(clearanceState)) {
			this.callAPI013();
		} else if (SukiyaContant.TANA_HOZAI.equals(clearanceState)) {
			this.callAPI011();
		} else {
			this.callAPI009();
		}
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

	private void storeProductList(List<ProductInfo> mainProductList) {
		if (menuInputHistoryId > 0) {
			String strVal = null;
			MenuPositionInfo selectedMenu = super.app.getSelectedMenu();
			super.db = new DatabaseHelper(getApplicationContext());
			if (SukiyaContant.MENU_04.equals(selectedMenu.getCode())) {
				for (ProductInfo productInfo : mainProductList) {
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
				for (ProductInfo productInfo : mainProductList) {
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
		super.app.setProductList(mainProductList);

		if (productList == null) {
			productList = new ArrayList<GroupProduct>();
		}

		if (!SukiyaContant.TANA_SHOMIKIGEN.equals(clearanceState)
				&& mainProductList != null && mainProductList.size() > 0) {
			Integer index = 0;
			GroupProduct groupProduct = null;

			Map<String, Integer> productIdIndex = new HashMap<String, Integer>();

			for (ProductInfo productInfo : mainProductList) {
				if (productIdIndex.containsKey(productInfo.getHinCd())) {
					index = productIdIndex.get(productInfo.getHinCd());

					groupProduct = productList.get(index);
					groupProduct.addProduct(productInfo);
				} else {
					groupProduct = new GroupProduct();
					groupProduct.setProductId(productInfo.getHinCd());
					groupProduct.setProductName(productInfo.getHinNm());
					groupProduct.setProductImage(productInfo.getImageName());
					groupProduct.setUnit(productInfo.getDefaultTaniNm());

					groupProduct.addProduct(productInfo);

					this.productList.add(groupProduct);
					productIdIndex.put(productInfo.getHinCd(),
							(this.productList.size() - 1));
				}
			}
		}
	}

	// Zenshou ntdat 20140417 add ++
	@Override
	protected void onResume() {
		// hide popup date
		super.hidePopup(mLayoutPopupDate);
		super.onResume();
	}
	// Zenshou ntdat 20140417 add --
}
