package jp.co.zensho.android.sukiya;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.zensho.android.sukiya.adapter.S208CustomListHistoryAdapter;
import jp.co.zensho.android.sukiya.adapter.S208CustomListItemAdapter;
import jp.co.zensho.android.sukiya.adapter.S208CustomListShopAdapter;
import jp.co.zensho.android.sukiya.bean.API14_ProductCatSel;
import jp.co.zensho.android.sukiya.bean.API15_ProductNameSel;
import jp.co.zensho.android.sukiya.bean.API17_QuantityUnit;
import jp.co.zensho.android.sukiya.bean.API25_ViewHistory;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import jp.co.zensho.android.sukiya.bean.S208_ListShippingReport;
import jp.co.zensho.android.sukiya.bean.Shop;
import jp.co.zensho.android.sukiya.calendar.OnSelectCalendarDailogListener;
import jp.co.zensho.android.sukiya.calendar.SukiyaMonthYearDialog;
import jp.co.zensho.android.sukiya.common.DatePickerConstant;
import jp.co.zensho.android.sukiya.common.DateUtils;
import jp.co.zensho.android.sukiya.common.JSONUtils;
import jp.co.zensho.android.sukiya.common.StringUtils;
import jp.co.zensho.android.sukiya.common.SukiyaContant;
import jp.co.zensho.android.sukiya.common.SukiyaNumberKeyboard;
import jp.co.zensho.android.sukiya.common.SukiyaNumberKeyboardPartCode;
import jp.co.zensho.android.sukiya.common.SukiyaUtils;
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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author ntdat
 * 
 *         Class: S208Acitivy
 * 
 *         Define 移動報告（出荷） screen (shipping report (export))
 * 
 */
public class S208Activity extends S00Activity implements OnClickListener,
		CallAPIListener, OnSelectCalendarDailogListener {
	private static String LOG = "S208Activity";
	private static final String HIN_BKN = "0";

	/**
	 * Android controls
	 */
	// s208 main screen
	private LinearLayout llInputQuantity;
	public LinearLayout llDefaultBlank;
	private ListView lvShippingReport;
	private Button btnAddToList;
	private RelativeLayout rlShopSel, rlProductNameSel;
	public RelativeLayout rlProductCatSel;
	private TextView tvProductNameSel;
	public TextView tvShopSel, tvProductCatSel;
	private ImageView imgShopSel, imgProductNameSel;
	public ImageView imgProductCatSel;

	// group 4 header buttons
	private TextView tvHeaderTitle;
	private Button btnBack, btnViewHistory, btnStopResigter;
	public Button btnInputPartCode;

	// pop-up quantity input
	public LinearLayout llPopupQuantity;
	private Button btnPopupQuantityClose, btnPlus10, btnPlus1, btnPlus01,
			btnMinus10, btnMinus1, btnMinus01, btnComplete;
	public EditText etPopupQuantityAmount;
	public TextView tvPopupQuantiyProductName, tvPopupQuantityUnit;
	private TextView tvQuantityNo, tvQuantityUnit, tvPopupQuantityTitle;
	public ImageView imgPopupQuantityImage;
	private SukiyaNumberKeyboard customKeyboard;

	// pop-up view history
	private LinearLayout llPopupViewHistory, llDateSelection;
	private Button btnPopupHistoryClose;
	private TextView tvShippingHistoryMonthSelection,
			tvShippingHistoryYearSelection, tvHistoryMonthShipping,
			tvHistoryMonthShippingCnt;
	private ListView lvShippingHistory;

	// pop-up part code
	private RelativeLayout rlPopupPartCode;
	private Button btnPopupPartCodeCancel, btnPopupPartCodeRegister;
	private EditText etPartCodeInputValue;
	private SukiyaNumberKeyboardPartCode customKeyboardPartCode;

	// pop-up search shop
	public LinearLayout llPopupShopSearch;
	public EditText etPopupShopSearchKeyInput;
	private Button btnPopupShopSearchClose, btnPopupShopSearch;
	private ListView lvShopSearchWithKeyWord, lvShopSearchFull;

	// Class variables
	private List<API14_ProductCatSel> listCatSel;
	private List<API15_ProductNameSel> listNameSel;
	private List<API17_QuantityUnit> listQuantityUnit;
	private List<API17_QuantityUnit> listQuantityUnitEdit;
	private List<API17_QuantityUnit> listTempQuantity;
	private List<API25_ViewHistory> listShippingHistory;
	private List<S208_ListShippingReport> listShippingReport;
	private List<Shop> listShopSearchFull;
	private List<Shop> listShopSearchWithKey;

	private S208CustomListItemAdapter shippingReportAdapter;
	private S208CustomListHistoryAdapter shippingHistoryAdapter;
	private S208CustomListShopAdapter shopSearchFullAdapter;
	private S208CustomListShopAdapter shopSearchWithKeyAdapter;

	private int nSelectedCatPos = 0;
	private int nSelectedNamePos = 0;
	private int nSelectedQuantityUnitPos = 0;

	// Calendar
	private Calendar cal;
	private String strHistoryDateParam;
	private int nCurrentYear, nCurrentMonth;

	// Send shop code get from list shop search
	public String strShopCode;
	public int nPosition;
	public boolean isListItemNumberClicked = false;
	public boolean isPopupHistoryShowed = false;
	public String strCurrentDivCD = "";
	public String strCurrentHinCD = "";

	/**
	 * Help button
	 * 
	 * @author vdngo
	 */
	// help
	private Button mBtnHelp;
	private TextView mHelp;
	private LinearLayout mHelpLayout;

	// Listener
	// Register part code dialog listener
	protected DialogInterface.OnClickListener registPartCodeDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}

			callAPI026();
		}
	};

	// Back to previous screen listener (for stop register button)
	protected DialogInterface.OnClickListener backToPrevScreenListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}

			finish();
			overridePendingTransition(R.animator.slide_in_left,
					R.animator.slide_out_right);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.s208);

		// Generate view from S00 (include menu)
		generateViews();

		DatePickerConstant.setCurrentDateOnView();
		// Change format current date to set params into API25
		cal = DateUtils.today();
		// Get current month and current year
		nCurrentMonth = cal.get(Calendar.MONTH) + 1;
		nCurrentYear = cal.get(Calendar.YEAR);

		StringBuilder builderDateParam = new StringBuilder();
		builderDateParam.append(String.format("%tY", cal));
		builderDateParam.append(String.format("%02d", nCurrentMonth));
		strHistoryDateParam = builderDateParam.toString();
		listShippingReport = new ArrayList<S208_ListShippingReport>();
		initView();

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Call APIs
		callAPI014();
		callAPI023();
	}

	@Override
	protected void onStop() {
		this.removeUpdateCurrentDate();
		super.onStop();
	}

	@Override
	public void selectedDate(Dialog dialog, int year, int month, int day) {
		if (dialog != null) {
			dialog.dismiss();
		}
		nCurrentMonth = month;
		nCurrentYear = year;

		tvShippingHistoryMonthSelection.setText(String.valueOf(month));
		tvShippingHistoryYearSelection.setText(String.valueOf(year));

		tvHistoryMonthShipping.setText(String.valueOf(month));
		// Set history date parameter
		StringBuilder builderDateParam = new StringBuilder();
		builderDateParam.append(tvShippingHistoryYearSelection.getText()
				.toString());
		builderDateParam.append(String.format("%02d", month));
		strHistoryDateParam = builderDateParam.toString();

		callAPI025(strHistoryDateParam);

	}

	@Override
	public void onBackPressed() {
		if (customKeyboard.isCustomKeyboardVisible()) {
			customKeyboard.hideCustomKeyboard();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View v) {

		/**
		 * help
		 * 
		 * @author vdngo
		 */
		if (v.equals(mHelpLayout)) {
			super.hidePopup(mHelpLayout);
		} else if (v.equals(mBtnHelp)) {
			super.getHelp(S208Activity.this, StringUtils.S208_HELP_CD);
		}

		switch (v.getId()) {
		case R.id.s208_rl_shop_sel:
			callPopupShopSearch();
			break;

		case R.id.p208_btn_submit_shop_search:
			String strKeyword = etPopupShopSearchKeyInput.getText().toString();
			if (!strKeyword.isEmpty()) {
				callAPI024(strKeyword, super.app.getShopCode());
			} else {
				// Handle error empty shop search keyword
			}
			hideKeyboard(etPopupShopSearchKeyInput);

			break;

		case R.id.p208_btn_close_shop_search:
			// Close pop-up shop search
			hideKeyboard(etPopupShopSearchKeyInput);
			super.hidePopup(llPopupShopSearch);
			break;

		case R.id.s208_rl_product_cat_sel:
			try {
				// Convert List<> to String[]
				String[] arrayListCatSel = null;
				if (listCatSel != null && listCatSel.size() > 0) {
					arrayListCatSel = new String[listCatSel.size()];

					API14_ProductCatSel objAPI14 = null;
					for (int i = 0; i < listCatSel.size(); i++) {
						objAPI14 = listCatSel.get(i);
						if (objAPI14 != null) {
							arrayListCatSel[i] = objAPI14.getDivName();
						}
					}

					// Show dialog single choice list of productCat_sel
					// (Category)
					Builder builder = new AlertDialog.Builder(S208Activity.this);
					builder.setSingleChoiceItems(arrayListCatSel,
							nSelectedCatPos,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int position) {
									if (arg0 != null) {
										arg0.dismiss();
									}

									nSelectedCatPos = position;
									tvProductCatSel.setText(listCatSel.get(
											position).getDivName());
									if (!StringUtils.EMPTY
											.equals(tvProductCatSel.getText()
													.toString())) {
										strCurrentDivCD = listCatSel.get(
												nSelectedCatPos).getDivCD();
										callAPI015(strCurrentDivCD);

										// Set relative control to default
										// value(disable some)
										tvProductNameSel
												.setText(getString(R.string.product_name));
										tvQuantityNo
												.setText(getString(R.string.input_number));
										tvQuantityUnit.setText("");

										// Disable some
										setQuantityEnabled(llInputQuantity,
												tvQuantityNo, false);
										btnAddToList.setEnabled(false);

									}

								}

							});
					super.alert = builder.show();
				} else {
					// show error message "cannot get list product category"
				}
			} catch (Exception e) {
				// Error handling
			}
			break;

		case R.id.s208_rl_product_name_sel:
			try {
				// Convert List<> to String[]
				String[] arrayListNameSel = null;
				if (listNameSel != null && listNameSel.size() > 0) {
					arrayListNameSel = new String[listNameSel.size()];

					API15_ProductNameSel objAPI15 = null;
					for (int i = 0; i < listNameSel.size(); i++) {
						objAPI15 = listNameSel.get(i);
						if (objAPI15 != null) {
							arrayListNameSel[i] = objAPI15.getHinName();
						}
					}
					// Show dialog single choice list of productName_sel
					// (Name)
					Builder builder = new AlertDialog.Builder(S208Activity.this);
					builder.setSingleChoiceItems(arrayListNameSel,
							nSelectedNamePos,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int position) {
									if (arg0 != null) {
										arg0.dismiss();
									}

									nSelectedNamePos = position;
									tvProductNameSel.setText(listNameSel.get(
											position).getHinName());
									if (!StringUtils.EMPTY
											.equals(tvProductNameSel.getText()
													.toString())) {
										// llQuantityInput.setEnabled(true);
										tvQuantityNo
												.setText(getString(R.string.input_number));
										tvQuantityUnit.setText("");
										setQuantityEnabled(llInputQuantity,
												tvQuantityNo, true);
									}
								}

							});
					super.alert = builder.show();
				} else {
					// show error message "cannot get list product category"
				}
			} catch (Exception e) {
				// error handling
			}
			break;

		case R.id.s208_btn_add_to_list:
			S208_ListShippingReport shippingReportObj = new S208_ListShippingReport();
			shippingReportObj.setDivCD(listCatSel.get(nSelectedCatPos)
					.getDivCD());
			shippingReportObj.setShopName(tvShopSel.getText().toString());
			shippingReportObj.setHinName(tvProductNameSel.getText().toString());
			shippingReportObj.setSndSum(tvQuantityNo.getText().toString());
			shippingReportObj.setTaniName(tvQuantityUnit.getText().toString());
			shippingReportObj.setTaniCD(listQuantityUnit.get(
					nSelectedQuantityUnitPos).getTaniCD());
			shippingReportObj.setHinCD(listNameSel.get(nSelectedNamePos)
					.getHinCD());
			listShippingReport.add(shippingReportObj);

			if (listShippingReport.size() > 0) {
				shippingReportAdapter = new S208CustomListItemAdapter(
						getApplicationContext(), listShippingReport,
						S208Activity.this);
				shippingReportAdapter.notifyDataSetChanged();
				lvShippingReport.setAdapter(shippingReportAdapter);
				// set button input part code enable
				btnInputPartCode.setEnabled(true);
			}

			if (listShippingReport.size() > 5) {
				llDefaultBlank.setVisibility(View.GONE);
			}

			// Set controls to default value and enable status
			setControlToDefault();
			break;

		case R.id.h2xx_btn_back:
			finish();
			overridePendingTransition(R.animator.slide_in_left,
					R.animator.slide_out_right);
			break;

		case R.id.s208_ll_quantity_unit_input:
			isListItemNumberClicked = false;
			if (!getString(R.string.input_number).equals(
					tvQuantityNo.getText().toString())) {
				etPopupQuantityAmount
						.setText(tvQuantityNo.getText().toString());
				tvPopupQuantityUnit
						.setText(tvQuantityUnit.getText().toString());
			} else {
				etPopupQuantityAmount.setText("");
				tvPopupQuantityUnit.setText("");
			}

			strCurrentHinCD = listNameSel.get(nSelectedNamePos).getHinCD();
			callAPI017(strCurrentHinCD);
			break;

		case R.id.p2xx_quantity_tv_unit:
			try {
				if (isListItemNumberClicked == false) {
					listTempQuantity = listQuantityUnit;
				} else {
					listTempQuantity = listQuantityUnitEdit;
				}
				// Convert List<> to String[]
				String[] arrayListQuantityUnit = null;
				if (listTempQuantity != null && listTempQuantity.size() > 0) {
					arrayListQuantityUnit = new String[listTempQuantity.size()];

					API17_QuantityUnit objAPI14 = null;
					for (int i = 0; i < listTempQuantity.size(); i++) {
						objAPI14 = listTempQuantity.get(i);
						if (objAPI14 != null) {
							arrayListQuantityUnit[i] = objAPI14.getTaniName();
						}
					}

					// Show dialog single choice list of productCat_sel
					// (Category)
					Builder builder = new AlertDialog.Builder(S208Activity.this);
					builder.setSingleChoiceItems(arrayListQuantityUnit,
							nSelectedQuantityUnitPos,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int position) {
									if (arg0 != null) {
										arg0.dismiss();
									}

									nSelectedQuantityUnitPos = position;
									tvPopupQuantityUnit
											.setText(listTempQuantity.get(
													position).getTaniName());

								}

							});
					super.alert = builder.show();
				} else {
					// show error message "cannot get list product category"
				}
			} catch (Exception e) {
				// Error handling
			}
			break;

		case R.id.p2xx_quantity_btn_close:
			// Close pop-up input quantity
			super.hidePopup(llPopupQuantity);
			break;

		case R.id.p2xx_quantity_btn_minus01:
			addProductAmount(SukiyaContant.VALUE_MINUS_01);
			break;

		case R.id.p2xx_quantity_btn_minus1:
			addProductAmount(SukiyaContant.VALUE_MINUS_1);
			break;

		case R.id.p2xx_quantity_btn_minus10:
			addProductAmount(SukiyaContant.VALUE_MINUS_10);
			break;

		case R.id.p2xx_quantity_btn_plus01:
			addProductAmount(SukiyaContant.VALUE_01);
			break;

		case R.id.p2xx_quantity_btn_plus1:
			addProductAmount(SukiyaContant.VALUE_1);
			break;

		case R.id.p2xx_quantity_btn_plus10:
			addProductAmount(SukiyaContant.VALUE_10);
			break;

		case R.id.p2xx_quantity_btn_complete:
			if (isListItemNumberClicked == false) {
				tvQuantityNo
						.setText(etPopupQuantityAmount.getText().toString());
				tvQuantityUnit
						.setText(tvPopupQuantityUnit.getText().toString());
			} else {
				if (listShippingReport.size() > 0) {
					listShippingReport.get(nPosition).setSndSum(
							etPopupQuantityAmount.getText().toString());
					listShippingReport.get(nPosition).setTaniName(
							tvPopupQuantityUnit.getText().toString());
					shippingReportAdapter.updateResults(listShippingReport);
				}
			}

			// Close pop-up input quantity
			super.hidePopup(llPopupQuantity);
			if (!tvQuantityNo.getText().toString().equals("")
					&& !tvQuantityUnit.getText().toString().equals("")
					&& rlProductNameSel.isEnabled()) {
				btnAddToList.setEnabled(true);
			}

			break;

		case R.id.h2xx_btn_view_history:
			callPopupHistory();
			break;

		case R.id.p208_ll_view_history_date_select:
			// showDialog(DATE_DIALOG_ID);
			SukiyaMonthYearDialog monthYearDialog = new SukiyaMonthYearDialog(
					S208Activity.this);
			monthYearDialog.setListener(this);
			monthYearDialog.setTodayToMaxDate();
			// monthYearDialog.setForcusCurrentYear();
			monthYearDialog.setForcusMonth(nCurrentMonth);
			monthYearDialog.setForcusYear(nCurrentYear);
			monthYearDialog.show();
			break;

		case R.id.p208_btn_close:
			isPopupHistoryShowed = false;
			// Close popup view history
			super.hidePopup(llPopupViewHistory);
			break;

		case R.id.h2xx_btn_stop_register:
			super.showDialog(this, R.string.msg_201_stop_register_confirm,
					R.string.yes, backToPrevScreenListener, R.string.no,
					dismissDialogListener);
			break;

		case R.id.h2xx_btn_submit:
			// Show pop-up input part code
			super.showPopup(rlPopupPartCode);
			break;

		case R.id.p06_btn_regist_part_code_input:
			registPartCodeInputAction();
			break;

		case R.id.p06_btn_cancel_part_code_input:
			// Close pop-up input part code
			super.hidePopup(rlPopupPartCode);
			break;

		default:
			break;
		}

	}

	@Override
	public void callAPIFinish(String tag, JSONObject result, ErrorInfo error) {
		// Dismiss progress dialog
		this.dismissProcessDialog();
		if (error == null && result != null) {
			try {
				if (!JSONUtils.hasError(result)) {
					if (JSONUtils.API_014_PATH.equals(tag)) {
						listCatSel = JSONUtils.parseListItemCategory(result);
					}
					if (JSONUtils.API_015_PATH.equals(tag)) {
						listNameSel = JSONUtils.parseListItem(result);

					}
					if (JSONUtils.API_017_PATH.equals(tag)) {
						if (isListItemNumberClicked == false) {
							listQuantityUnit = JSONUtils
									.parseListItemUnit(result);
						} else {
							listQuantityUnitEdit = JSONUtils
									.parseListItemUnit(result);
						}

					}
					if (JSONUtils.API_023_PATH.equals(tag)) {
						listShopSearchFull = JSONUtils.parseListShop(result);
					}

					if (JSONUtils.API_024_PATH.equals(tag)) {
						listShopSearchWithKey = JSONUtils.parseListShop(result);
					}

					if (JSONUtils.API_025_PATH.equals(tag)) {
						tvHistoryMonthShippingCnt.setText(result
								.getString("count"));
						listShippingHistory = JSONUtils
								.parseListShippingHistory(result);
					}

					/**
					 * help
					 * 
					 * @author vdngo
					 */
					if (JSONUtils.API_043_PATH.equals(tag)) {
						// Zenshou ntdat 20140407 delete ++
						String help = null;
						try {
							help = JSONUtils.partHelpString(result);
							// Test scroll
							//
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

			// show error dialog
			super.showDialog(this, message, R.string.try_again,
					registPartCodeDialogListener, R.string.cancel,
					dismissDialogListener);
		} else {
			if (JSONUtils.API_015_PATH.equals(tag)) {
				setControlEnabled(rlProductNameSel, tvProductNameSel,
						imgProductNameSel, true);
			}

			if (JSONUtils.API_017_PATH.equals(tag)) {
				// Set image and text for product when show pop-up
				if (isListItemNumberClicked == false) {
					SukiyaUtils.setImage(this, imgPopupQuantityImage,
							strCurrentHinCD + ".jpg", R.drawable.no_image_191);
					tvPopupQuantiyProductName.setText(tvProductNameSel
							.getText().toString());
				}

				// Show pop-up
				super.showPopup(llPopupQuantity);
			}

			if (JSONUtils.API_023_PATH.equals(tag)) {
				setControlEnabled(rlShopSel, tvShopSel, imgShopSel, true);
			}

			if (JSONUtils.API_024_PATH.equals(tag)) {
				// Update listShop after search with new keyword
				if (listShopSearchWithKey != null) {
					if (shopSearchWithKeyAdapter == null) {

						shopSearchWithKeyAdapter = new S208CustomListShopAdapter(
								getApplicationContext(), listShopSearchWithKey,
								S208Activity.this);
						shopSearchWithKeyAdapter.notifyDataSetChanged();
						lvShopSearchWithKeyWord
								.setAdapter(shopSearchWithKeyAdapter);

					} else {
						shopSearchWithKeyAdapter
								.updateResults(listShopSearchWithKey);
					}
					shopSearchWithKeyAdapter.notifyDataSetChanged();
					lvShopSearchWithKeyWord.invalidateViews();
				}
			}

			if (JSONUtils.API_025_PATH.equals(tag)) {
				// Update listLossHistory after date changed
				if (listShippingHistory != null) {
					if (shippingHistoryAdapter == null) {
						shippingHistoryAdapter = new S208CustomListHistoryAdapter(
								getApplicationContext(), listShippingHistory);
						shippingHistoryAdapter.notifyDataSetChanged();
						lvShippingHistory.setAdapter(shippingHistoryAdapter);
					} else {
						shippingHistoryAdapter
								.updateResults(listShippingHistory);
					}
					shippingHistoryAdapter.notifyDataSetChanged();
					lvShippingHistory.invalidateViews();
				} else {
					listShippingHistory = new ArrayList<API25_ViewHistory>();
					if (shippingHistoryAdapter != null) {
						shippingHistoryAdapter
								.updateResults(listShippingHistory);
					}

				}

				if (isPopupHistoryShowed == false) {
					super.showPopup(llPopupViewHistory);
					isPopupHistoryShowed = true;
				}
			}

			if (JSONUtils.API_026_PATH.equals(tag)) {
				// Return to top screen
				finish();
				overridePendingTransition(R.animator.slide_in_left,
						R.animator.slide_out_right);
			}
		}
	}

	// Initialize android controls and define actions
	private void initView() {
		/**
		 * Initialize controls
		 */
		// s208 main screen
		tvShopSel = (TextView) findViewById(R.id.s208_tv_shop_sel);
		tvProductCatSel = (TextView) findViewById(R.id.s208_tv_product_cat_sel);
		tvProductNameSel = (TextView) findViewById(R.id.s208_tv_product_name_sel);
		llInputQuantity = (LinearLayout) findViewById(R.id.s208_ll_quantity_unit_input);
		llDefaultBlank = (LinearLayout) findViewById(R.id.s208_ll_default_items_area);
		rlShopSel = (RelativeLayout) findViewById(R.id.s208_rl_shop_sel);
		rlProductCatSel = (RelativeLayout) findViewById(R.id.s208_rl_product_cat_sel);
		rlProductNameSel = (RelativeLayout) findViewById(R.id.s208_rl_product_name_sel);
		lvShippingReport = (ListView) findViewById(R.id.s208_lv_shipping_report);
		imgShopSel = (ImageView) findViewById(R.id.s208_img_shop_sel);
		imgProductCatSel = (ImageView) findViewById(R.id.s208_img_product_cat_sel);
		imgProductNameSel = (ImageView) findViewById(R.id.s208_img_product_name_sel);
		btnAddToList = (Button) findViewById(R.id.s208_btn_add_to_list);

		// group 4 header buttons
		tvHeaderTitle = (TextView) findViewById(R.id.h2xx_tv_header_title);
		btnBack = (Button) findViewById(R.id.h2xx_btn_back);
		btnViewHistory = (Button) findViewById(R.id.h2xx_btn_view_history);
		btnStopResigter = (Button) findViewById(R.id.h2xx_btn_stop_register);
		btnInputPartCode = (Button) findViewById(R.id.h2xx_btn_submit);

		// pop-up quantity input
		llPopupQuantity = (LinearLayout) findViewById(R.id.p208_ll_input_number);
		etPopupQuantityAmount = (EditText) findViewById(R.id.p2xx_quantity_et_amount);
		tvPopupQuantityUnit = (TextView) findViewById(R.id.p2xx_quantity_tv_unit);
		tvQuantityNo = (TextView) findViewById(R.id.s208_tv_quantity_number);
		tvQuantityUnit = (TextView) findViewById(R.id.s208_tv_quantity_unit);
		tvPopupQuantiyProductName = (TextView) findViewById(R.id.p2xx_quantity_name);
		tvPopupQuantityTitle = (TextView) findViewById(R.id.p2xx_quantity_tv_title);
		imgPopupQuantityImage = (ImageView) findViewById(R.id.p2xx_quantity_image);
		btnPlus01 = (Button) findViewById(R.id.p2xx_quantity_btn_plus01);
		btnPlus1 = (Button) findViewById(R.id.p2xx_quantity_btn_plus1);
		btnPlus10 = (Button) findViewById(R.id.p2xx_quantity_btn_plus10);
		btnMinus01 = (Button) findViewById(R.id.p2xx_quantity_btn_minus01);
		btnMinus1 = (Button) findViewById(R.id.p2xx_quantity_btn_minus1);
		btnMinus10 = (Button) findViewById(R.id.p2xx_quantity_btn_minus10);
		btnComplete = (Button) findViewById(R.id.p2xx_quantity_btn_complete);
		btnPopupQuantityClose = (Button) findViewById(R.id.p2xx_quantity_btn_close);
		customKeyboard = new SukiyaNumberKeyboard(this, R.id.keyboardview,
				R.xml.keyboard_layout);
		customKeyboard.registerEditText(R.id.p2xx_quantity_et_amount);

		// pop-up view history
		llPopupViewHistory = (LinearLayout) findViewById(R.id.p208_ll_view_history);
		llDateSelection = (LinearLayout) findViewById(R.id.p208_ll_view_history_date_select);
		btnPopupHistoryClose = (Button) findViewById(R.id.p208_btn_close);
		tvShippingHistoryYearSelection = (TextView) findViewById(R.id.p208_tv_view_history_year);
		tvShippingHistoryMonthSelection = (TextView) findViewById(R.id.p208_tv_view_history_month);
		tvHistoryMonthShipping = (TextView) findViewById(R.id.p208_tv_month_shipping);
		tvHistoryMonthShippingCnt = (TextView) findViewById(R.id.p208_tv_month_shipping_cnt);
		lvShippingHistory = (ListView) findViewById(R.id.p208_lv_shipping_report_history);

		// pop-up part code
		rlPopupPartCode = (RelativeLayout) findViewById(R.id.p06_input_part_code);
		btnPopupPartCodeCancel = (Button) findViewById(R.id.p06_btn_cancel_part_code_input);
		btnPopupPartCodeRegister = (Button) findViewById(R.id.p06_btn_regist_part_code_input);
		etPartCodeInputValue = (EditText) findViewById(R.id.p06_txt_input_part_code);
		customKeyboardPartCode = new SukiyaNumberKeyboardPartCode(this,
				R.id.keyboardviewPartcode, R.xml.keyboard_layout);
		customKeyboardPartCode.registerEditText(R.id.p06_txt_input_part_code);

		// pop-up shop search
		llPopupShopSearch = (LinearLayout) findViewById(R.id.p208_ll_shop_search);
		etPopupShopSearchKeyInput = (EditText) findViewById(R.id.p208_et_input_shop_search);
		btnPopupShopSearchClose = (Button) findViewById(R.id.p208_btn_close_shop_search);
		btnPopupShopSearch = (Button) findViewById(R.id.p208_btn_submit_shop_search);
		lvShopSearchWithKeyWord = (ListView) findViewById(R.id.p208_lv_shop_search_left);
		lvShopSearchFull = (ListView) findViewById(R.id.p208_lv_shop_search_right);

		/**
		 * help
		 * 
		 * @author vdngo
		 */
		//
		mBtnHelp = (Button) findViewById(R.id.btn_q);
		mHelp = (TextView) findViewById(R.id.help_content);
		mHelpLayout = (LinearLayout) findViewById(R.id.s208_help);
		mBtnHelp.setOnClickListener(this);
		mHelpLayout.setOnClickListener(this);

		/**
		 * Set control actions
		 */
		// s208 main screen
		setControlEnabled(rlShopSel, tvShopSel, imgShopSel, false);
		setControlToDefault();

		rlShopSel.setOnClickListener(this);
		rlProductCatSel.setOnClickListener(this);
		rlProductNameSel.setOnClickListener(this);
		llInputQuantity.setOnClickListener(this);
		btnAddToList.setOnClickListener(this);

		// group 4 header buttons
		btnInputPartCode.setEnabled(false);

		tvHeaderTitle.setText(getString(R.string.h2xx_shipping_report_title));
		btnBack.setOnClickListener(this);
		btnViewHistory.setOnClickListener(this);
		btnStopResigter.setOnClickListener(this);
		btnInputPartCode.setOnClickListener(this);

		// pop-up quantity input
		tvPopupQuantityTitle
				.setText(getString(R.string.p_input_number_title_shipping));

		btnPopupQuantityClose.setOnClickListener(this);
		tvPopupQuantityUnit.setOnClickListener(this);
		btnPlus01.setOnClickListener(this);
		btnPlus1.setOnClickListener(this);
		btnPlus10.setOnClickListener(this);
		btnMinus01.setOnClickListener(this);
		btnMinus1.setOnClickListener(this);
		btnMinus10.setOnClickListener(this);
		btnComplete.setOnClickListener(this);

		// pop-up view history
		tvShippingHistoryYearSelection.setText(String.format("%tY", cal));
		tvShippingHistoryMonthSelection.setText(String.valueOf(nCurrentMonth));
		tvHistoryMonthShipping.setText(String.valueOf(nCurrentMonth));
		llDateSelection.setOnClickListener(this);
		btnPopupHistoryClose.setOnClickListener(this);

		// pop-up part code
		btnPopupPartCodeCancel.setOnClickListener(this);
		btnPopupPartCodeRegister.setOnClickListener(this);

		// pop-up shop search
		btnPopupShopSearchClose.setOnClickListener(this);
		btnPopupShopSearch.setOnClickListener(this);
	}

	// Call API14 to get productCat_sel
	public void callAPI014() {
		Log.d(LOG, "callAPI014 - START");
		// Check if shopcode exist
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_014_PATH, params);
				Log.d(LOG, "URL: " + url);

				this.showProcessDialog(this, null, null);
				new CallAPIAsyncTask(this, JSONUtils.API_014_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = e.getMessage();
				Log.d(LOG, "callAPI14: ERROR " + message);

			}
		} else {
			// error: shopcode null
			Log.d(LOG, "callAPI14: ERROR shopcode null");
		}
		Log.d(LOG, "callAPI014 - END");
	}

	// Call API15 to get productName_Sel
	public void callAPI015(String strDivCD) {
		Log.d(LOG, "callAPI015 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
			params.put(JSONUtils.API_015_PARAMS_KEY, strDivCD);

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_015_PATH, params);
				Log.d(LOG, "URL: " + url);

				this.showProcessDialog(this, null, null);
				new CallAPIAsyncTask(this, JSONUtils.API_015_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = e.getMessage();
				Log.d(LOG, "callAPI15: ERROR " + message);

			}
		} else {
			// error: shopcode null
			Log.d(LOG, "callAPI15: ERROR shopcode null");
		}
		Log.d(LOG, "callAPI015 - END");
	}

	// Get quantity unit
	public void callAPI017(String strHinCD) {
		Log.d(LOG, "callAPI017 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.API_017_PARAMS_KEY, strHinCD);

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_017_PATH, params);
				Log.d(LOG, "URL: " + url);

				this.showProcessDialog(this, null, null);
				new CallAPIAsyncTask(this, JSONUtils.API_017_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = e.getMessage();
				Log.d(LOG, "callAPI17: ERROR " + message);

			}
		} else {
			// error: shopcode null
			Log.d(LOG, "callAPI17: ERROR shopcode null");
		}
		Log.d(LOG, "callAPI017 - END");
	}

	// Call API23 to get list shop code
	private void callAPI023() {
		Log.d(LOG, "callAPI023 - START");
		// Check if shopcode exist
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
			params.put(JSONUtils.API_023_PARAMS_KEY, "50");

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_023_PATH, params);
				Log.d(LOG, "URL: " + url);

				this.showProcessDialog(this, null, null);
				new CallAPIAsyncTask(this, JSONUtils.API_023_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = e.getMessage();
				Log.d(LOG, "callAPI23: ERROR " + message);

			}
		} else {
			// error: shopcode null
			Log.d(LOG, "callAPI23: ERROR shopcode null");
		}
		Log.d(LOG, "callAPI023 - END");
	}

	// Call API24 to get list shop code
	private void callAPI024(String strKeyword, String strTenpoCD) {
		Log.d(LOG, "callAPI024 - START");
		// Check if shopcode exist
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.API_024_PARAMS_KEY, strKeyword);
			params.put(JSONUtils.TENPO_CODE, strTenpoCD);

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_024_PATH, params);
				Log.d(LOG, "URL: " + url);

				this.showProcessDialog(this, null, null);
				new CallAPIAsyncTask(this, JSONUtils.API_024_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = e.getMessage();
				Log.d(LOG, "callAPI24: ERROR " + message);

			}
		} else {
			// error: shopcode null
			Log.d(LOG, "callAPI24: ERROR shopcode null");
		}
		Log.d(LOG, "callAPI024 - END");
	}

	// Call API25 to get list shipping history
	private void callAPI025(String date) {

		Log.d(LOG, "callAPI025 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
			params.put(JSONUtils.API_018_PARAMS_KEY, date);

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_025_PATH, params);
				Log.d(LOG, "URL: " + url);

				this.showProcessDialog(this, null, null);
				new CallAPIAsyncTask(this, JSONUtils.API_025_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = e.getMessage();
				Log.d(LOG, "callAPI025: ERROR " + message);

			}
		} else {
			// error: shopcode null
			Log.d(LOG, "callAPI025: ERROR shopcode null");
		}
		Log.d(LOG, "callAPI025 - END");
	}

	// call API026 to update shipping
	private void callAPI026() {
		if (StringUtils.isEmpty(this.etPartCodeInputValue.getText().toString())) {
			return;
		}
		if (listShippingReport == null || listShippingReport.size() <= 0) {
			return;
		}
		// make url
		StringBuilder url = new StringBuilder();
		url.append(JSONUtils.API_HOST_PATH);
		url.append(JSONUtils.API_026_PATH);

		// make parameter
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair(JSONUtils.TENPO_CODE, super.app
				.getShopCode()));

		S208_ListShippingReport shippingReport = null;
		StringBuilder paramKey = null;
		for (int i = 0; i < listShippingReport.size(); i++) {
			shippingReport = listShippingReport.get(i);

			// egy_date
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_026_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.EGY_DATE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super
					.strCurrentDate()));

			// time_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_026_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TIME_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super
					.getTimeCode()));

			// tenpo_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_026_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_026_SND_TENPO)
					.append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super.app
					.getShopCode()));

			// rcv_tenpo (receive shop cd)
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_026_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_026_RCV_TENPO)
					.append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), strShopCode));

			// snd_date (send date)
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_026_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_026_SND_DATE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super
					.strCurrentDate()));

			// snd_time (send time)
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_026_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_026_SND_TIME).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(),
					getCurrentTime()));

			// hin_kbn
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_026_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_026_HIN_KBN).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), HIN_BKN));

			// hin_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_026_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.HIN_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), shippingReport
					.getHinCD()));

			// snd_su
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_026_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_026_SND_SU).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), shippingReport
					.getSndSum()));

			// tani_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_026_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TANI_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), shippingReport
					.getTaniCD()));

			// part_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_026_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.PART_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(),
					etPartCodeInputValue.getText().toString()));
		}

		// Show project dialog
		this.showProcessDialog(this, null, null);

		new PostAPIAsyncTask(this, data, JSONUtils.API_026_PATH).execute(url
				.toString());
	}

	private void addProductAmount(String value) {
		String currentVal = etPopupQuantityAmount.getText().toString();
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
		etPopupQuantityAmount.setText(r.toString());
	}

	// View shipping history
	private void callPopupHistory() {
		callAPI025(strHistoryDateParam);
	}

	// View shop search pop-up
	private void callPopupShopSearch() {
		// Show pop-up shop search
		AlphaAnimation alpha_shop_search_open = new AlphaAnimation(0.0f, 1.0f);
		alpha_shop_search_open.setDuration(350);
		llPopupShopSearch.startAnimation(alpha_shop_search_open);
		llPopupShopSearch.setVisibility(View.VISIBLE);

		if (listShopSearchFull != null) {
			shopSearchFullAdapter = new S208CustomListShopAdapter(
					getApplicationContext(), listShopSearchFull,
					S208Activity.this);
			shopSearchFullAdapter.notifyDataSetChanged();
			lvShopSearchFull.setAdapter(shopSearchFullAdapter);
		}
	}

	private void registPartCodeInputAction() {
		String message = null;

		String value = etPartCodeInputValue.getText().toString().trim();
		if (!StringUtils.isEmpty(value)) {
			try {
				// Integer.parseInt(value);
				// this.partCodeInputValue = value;

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

	private String getCurrentTime() {
		StringBuilder builderCurTime = new StringBuilder();

		// Time now (Integer format)
		Time now = new Time(Time.getCurrentTimezone());
		now.setToNow();

		// Set hour, minute, second to right format (1 => 01)
		builderCurTime.append(String.format("%tH", cal)); // hour: 16
		builderCurTime.append(String.format("%tM", cal)); // minute: 04
		builderCurTime.append(String.format("%tS", cal)); // second: 04

		return builderCurTime.toString();

	}

	public void setControlEnabled(RelativeLayout rl, TextView tv,
			ImageView img, boolean b) {
		rl.setEnabled(b);
		tv.setEnabled(b);
		img.setEnabled(b);
	}

	private void setQuantityEnabled(LinearLayout ll, TextView tv, boolean b) {
		ll.setEnabled(b);
		tv.setEnabled(b);
	}

	private void setControlToDefault() {
		tvProductCatSel.setText(getString(R.string.product_category));
		tvProductNameSel.setText(getString(R.string.product_name));
		tvShopSel.setText(getString(R.string.shop_name));
		tvQuantityNo.setText(getString(R.string.input_number));
		tvQuantityUnit.setText("");
		etPopupQuantityAmount.setText("");
		tvPopupQuantityUnit.setText("");

		setControlEnabled(rlProductNameSel, tvProductNameSel,
				imgProductNameSel, false);
		setControlEnabled(rlProductCatSel, tvProductCatSel, imgProductCatSel,
				false);
		setQuantityEnabled(llInputQuantity, tvQuantityNo, false);
		btnAddToList.setEnabled(false);
	}

	public void hideKeyboard(EditText etKeyInput) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etKeyInput.getWindowToken(), 0);
	}
}
