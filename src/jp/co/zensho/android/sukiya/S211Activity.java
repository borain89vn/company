package jp.co.zensho.android.sukiya;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.zensho.android.sukiya.adapter.S211CustomListHistoryAdapter;
import jp.co.zensho.android.sukiya.adapter.S211CustomListLeftResourceAdapter;
import jp.co.zensho.android.sukiya.adapter.S211CustomListRightContentAdapter;
import jp.co.zensho.android.sukiya.bean.API27_AcceptanceShop;
import jp.co.zensho.android.sukiya.bean.API28_AcceptanceShop_Detail;
import jp.co.zensho.android.sukiya.bean.API29_ViewHistory;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import jp.co.zensho.android.sukiya.calendar.OnSelectCalendarDailogListener;
import jp.co.zensho.android.sukiya.calendar.SukiyaMonthYearDialog;
import jp.co.zensho.android.sukiya.common.DatePickerConstant;
import jp.co.zensho.android.sukiya.common.DateUtils;
import jp.co.zensho.android.sukiya.common.JSONUtils;
import jp.co.zensho.android.sukiya.common.StringUtils;
import jp.co.zensho.android.sukiya.common.SukiyaContant;
import jp.co.zensho.android.sukiya.common.SukiyaNumberKeyboard;
import jp.co.zensho.android.sukiya.common.SukiyaNumberKeyboardPartCode;
import jp.co.zensho.android.sukiya.service.CallAPIAsyncTask;
import jp.co.zensho.android.sukiya.service.CallAPIListener;
import jp.co.zensho.android.sukiya.service.PostAPIAsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
 *         Class: S211Activity
 * 
 *         Define 移動報告（入荷） screen (shipping report (import))
 */
public class S211Activity extends S00Activity implements OnClickListener,
		OnItemClickListener, CallAPIListener, OnSelectCalendarDailogListener {
	private static String LOG = "S211Activity";
	private static final String HIN_BKN = "0";

	// S211 main controls
	public LinearLayout llDefaultBlank;
	private ListView lvLeftAcceptanceSource, lvRightAcceptanceContent;

	// Group 4 header buttons
	private TextView tvHeaderTitle;
	private Button btnBack, btnViewHistory, btnStopRegister;
	public Button btnSubmit;

	// Pop-up view history
	private TextView tvAcceptanceHistoryMonthSelected,
			tvAcceptanceHistoryYearSelected, tvHistoryMonthAcceptance,
			tvHistoryMonthAcceptanceCnt;
	private LinearLayout llPopupViewHistory, llDateSelection;
	private Button btn_history_close;
	private ListView lvAcceptanceHistory;

	// Ngo sua begin
	public boolean isChangeRightData = false;
	public View lastItemSelected = null;
	public String strDate;
	// Ngo sua end

	// Pop-up input number
	public LinearLayout llPopupQuantity;
	private Button btnPopupQuantityClose, btnPlus10, btnPlus1, btnPlus01,
			btnMinus10, btnMinus1, btnMinus01, btnComplete;
	public ImageView imgPopupQuantity;
	public EditText etPopupQuantityAmount;
	public TextView tvPopupQuantityUnit, tvPopupQuantityProductName;
	private TextView tvPopupQuantityTitle;
	private SukiyaNumberKeyboard customKeyboard;

	// Pop-up part code
	private RelativeLayout rlPopupPartCode;
	private Button btnPopupPartCodeCancel, btnPopupPartCodeRegister;
	private EditText etPartCodeInputValue;
	private SukiyaNumberKeyboardPartCode customKeyboardPartCode;

	// Class variables
	private List<API27_AcceptanceShop> listLeftResource;
	private List<API28_AcceptanceShop_Detail> listRightContent;
	private List<API29_ViewHistory> listAcceptanceHistory;

	private S211CustomListLeftResourceAdapter leftResourceAdapter;
	private S211CustomListRightContentAdapter rightContentAdapter;
	private S211CustomListHistoryAdapter acceptanceHistoryAdapter;

	// Calendar
	private Calendar cal;
	private String strHistoryDateParam;
	private int nCurrentYear, nCurrentMonth;

	// Send shop code get from list left resource
	private String strShopCode;
	private boolean isPopupHistoryShowed = false;
	public static String[] arrStrPreRCVSum;
	public static int nRightPosition = -1;
	public static int nLeftPosition = -1;

	// Checked code and date exist variables (for avoid duplicated click on the
	// same item in list left)
	public static String strCheckedCode;
	public static String strCheckedDate;

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

			callAPI030();
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
		setContentView(R.layout.s211);

		// Generate view from S00 (include menu)
		generateViews();
		DatePickerConstant.setCurrentDateOnView();
		// Change format current date to set params into API29
		cal = DateUtils.today();
		// Get current month and current year
		nCurrentMonth = cal.get(Calendar.MONTH) + 1;
		nCurrentYear = cal.get(Calendar.YEAR);

		StringBuilder builderDateParam = new StringBuilder();
		builderDateParam.append(String.format("%tY", cal));
		builderDateParam.append(String.format("%02d", nCurrentMonth));
		strHistoryDateParam = builderDateParam.toString();
		listRightContent = new ArrayList<API28_AcceptanceShop_Detail>();
		initView();

	}

	@Override
	protected void onResume() {
		super.onResume();
		callAPI027();
		// callAPI029(strHistoryDateParam);
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

		tvAcceptanceHistoryMonthSelected.setText(String.valueOf(month));
		tvAcceptanceHistoryYearSelected.setText(String.valueOf(year));

		tvHistoryMonthAcceptance.setText(String.valueOf(month));
		// Set history date parameter
		StringBuilder builderDateParam = new StringBuilder();
		builderDateParam.append(tvAcceptanceHistoryYearSelected.getText()
				.toString());
		builderDateParam.append(String.format("%02d", month));
		strHistoryDateParam = builderDateParam.toString();

		// Recall API29 to show selected month data
		callAPI029(strHistoryDateParam);

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
			super.getHelp(S211Activity.this, StringUtils.S211_HELP_CD);
		}

		switch (v.getId()) {
		case R.id.h2xx_btn_back:
			finish();
			overridePendingTransition(R.animator.slide_in_left,
					R.animator.slide_out_right);
			break;

		case R.id.h2xx_btn_view_history:
			callPopUpHistory();
			break;

		case R.id.p211_ll_view_history_date_select:
			SukiyaMonthYearDialog monthYearDialog = new SukiyaMonthYearDialog(
					S211Activity.this);
			monthYearDialog.setListener(this);
			monthYearDialog.setTodayToMaxDate();
			// monthYearDialog.setForcusCurrentYear();
			monthYearDialog.setForcusMonth(nCurrentMonth);
			monthYearDialog.setForcusYear(nCurrentYear);
			monthYearDialog.show();
			break;

		case R.id.p211_btn_close:
			isPopupHistoryShowed = false;
			// Close pop-up view history
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

		case R.id.p06_btn_cancel_part_code_input:
			// Close pop-up input part code
			super.hidePopup(rlPopupPartCode);
			break;

		case R.id.p06_btn_regist_part_code_input:
			registPartCodeInputAction();
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
			// Close pop-up input quantity
			if (nRightPosition >= 0) {
				listRightContent.get(nRightPosition).setPreRCVSum(
						etPopupQuantityAmount.getText().toString());
				rightContentAdapter.updateResults(listRightContent);
				super.hidePopup(llPopupQuantity);
			}

			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, final View arg1,
			final int arg2, long arg3) {
		switch (arg0.getId()) {
		case R.id.s211_lv_left_acceptance_resource:
			nLeftPosition = arg2;
			strShopCode = listLeftResource.get(arg2).getTenpoCD();
			String strOriginalDate = listLeftResource.get(arg2)
					.getSendDateTime();
			strDate = strOriginalDate.replaceAll("[-: ]", "");
			strCheckedCode = strShopCode;
			strCheckedDate = strDate;
			if (listRightContent.size() > 0) {
				for (int i = 0; i < listRightContent.size(); i++) {
					if (listRightContent.get(i).getCheckedTenpoCD()
							.equals(strCheckedCode)
							&& listRightContent.get(i).getCheckedDate()
									.equals(strCheckedDate)) {
						arg1.setSelected(true);
						return;
					}
				}
			}
			// vdngo begin
			// Check if data in right is changed to show prompt
			if (isChangeRightData) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(getResources().getString(
						R.string.edit_clean_promt));
				builder.setCancelable(false);
				builder.setPositiveButton(
						getResources().getString(R.string.ok_promt),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								arg1.setSelected(true);
								lastItemSelected = arg1;
								listRightContent.clear();
								isChangeRightData = false;
								callAPI028(strShopCode, strDate);
							}
						});
				builder.setNegativeButton(
						getResources().getString(R.string.cancel_prompt),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								lastItemSelected.setSelected(true);
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			} else {
				arg1.setSelected(true);
				lastItemSelected = arg1;
				listRightContent.clear();
				callAPI028(strShopCode, strDate);
			}

			// vdngo end
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
					if (JSONUtils.API_027_PATH.equals(tag)) {
						// listLeftResource = new
						// ArrayList<API27_AcceptanceShop_Obj>();
						listLeftResource = JSONUtils.parseListFromShop(result);
					}

					if (JSONUtils.API_028_PATH.equals(tag)) {
						JSONUtils.parseListRCVItem(result, listRightContent);
					}

					if (JSONUtils.API_029_PATH.equals(tag)) {
						// listAcceptanceHistory = new
						// ArrayList<API29_ViewHistory_Obj>();
						tvHistoryMonthAcceptanceCnt.setText(result
								.getString("count"));
						listAcceptanceHistory = JSONUtils
								.parseListAcceptanceHistory(result);
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

			if (JSONUtils.API_027_PATH.equals(tag)) {
				// Display list view left resource
				displayLeftResourceData();

			}

			if (JSONUtils.API_028_PATH.equals(tag)) {
				// Display list right content
				if (listRightContent != null) {
					rightContentAdapter = new S211CustomListRightContentAdapter(
							getApplicationContext(), listRightContent,
							S211Activity.this);
					rightContentAdapter.notifyDataSetChanged();
					lvRightAcceptanceContent.setAdapter(rightContentAdapter);

					// set button input part code enable
					btnSubmit.setEnabled(true);

					// hide default blank items area to show correct design
					if (listRightContent.size() > 4) {
						llDefaultBlank.setVisibility(View.GONE);
					}
				}
			}

			if (JSONUtils.API_029_PATH.equals(tag)) {
				// Update listAcceptanceHistory after date changed
				if (listAcceptanceHistory != null) {
					if (acceptanceHistoryAdapter == null) {
						acceptanceHistoryAdapter = new S211CustomListHistoryAdapter(
								getApplicationContext(), listAcceptanceHistory);
						acceptanceHistoryAdapter.notifyDataSetChanged();
						lvAcceptanceHistory
								.setAdapter(acceptanceHistoryAdapter);
					} else {
						acceptanceHistoryAdapter
								.updateResults(listAcceptanceHistory);
					}
					acceptanceHistoryAdapter.notifyDataSetChanged();
					lvAcceptanceHistory.invalidateViews();
				} else {
					listAcceptanceHistory = new ArrayList<API29_ViewHistory>();
					if (acceptanceHistoryAdapter != null) {
						acceptanceHistoryAdapter
								.updateResults(listAcceptanceHistory);
					}

				}

				if (isPopupHistoryShowed == false) {
					super.showPopup(llPopupViewHistory);
					isPopupHistoryShowed = true;
				}

			}

			if (JSONUtils.API_030_PATH.equals(tag)) {
				// Return to top screen
				finish();
				overridePendingTransition(R.animator.slide_in_left,
						R.animator.slide_out_right);
			}
		}
	}

	// Show history pop-up
	private void callPopUpHistory() {
		callAPI029(strHistoryDateParam);
	}

	// Initialize android controls and define actions
	private void initView() {
		/**
		 * Initialize controls
		 */
		// S211 main controls
		llDefaultBlank = (LinearLayout) findViewById(R.id.s211_ll_default_items_area);
		lvLeftAcceptanceSource = (ListView) findViewById(R.id.s211_lv_left_acceptance_resource);
		lvRightAcceptanceContent = (ListView) findViewById(R.id.s211_lv_right_acceptance_content);

		// Group 4 header buttons
		tvHeaderTitle = (TextView) findViewById(R.id.h2xx_tv_header_title);
		btnBack = (Button) findViewById(R.id.h2xx_btn_back);
		btnViewHistory = (Button) findViewById(R.id.h2xx_btn_view_history);
		btnStopRegister = (Button) findViewById(R.id.h2xx_btn_stop_register);
		btnSubmit = (Button) findViewById(R.id.h2xx_btn_submit);

		// Pop-up view history
		tvAcceptanceHistoryMonthSelected = (TextView) findViewById(R.id.p211_tv_view_history_month);
		tvAcceptanceHistoryYearSelected = (TextView) findViewById(R.id.p211_tv_view_history_year);
		tvHistoryMonthAcceptance = (TextView) findViewById(R.id.p211_tv_month_acceptance);
		tvHistoryMonthAcceptanceCnt = (TextView) findViewById(R.id.p211_tv_month_acceptance_cnt);
		llPopupViewHistory = (LinearLayout) findViewById(R.id.p211_ll_view_history);
		llDateSelection = (LinearLayout) findViewById(R.id.p211_ll_view_history_date_select);
		lvAcceptanceHistory = (ListView) findViewById(R.id.p211_lv_acceptance_history);
		btn_history_close = (Button) findViewById(R.id.p211_btn_close);

		// Pop-up part code
		rlPopupPartCode = (RelativeLayout) findViewById(R.id.p06_input_part_code);
		btnPopupPartCodeCancel = (Button) findViewById(R.id.p06_btn_cancel_part_code_input);
		btnPopupPartCodeRegister = (Button) findViewById(R.id.p06_btn_regist_part_code_input);
		etPartCodeInputValue = (EditText) findViewById(R.id.p06_txt_input_part_code);
		customKeyboardPartCode = new SukiyaNumberKeyboardPartCode(this,
				R.id.keyboardviewPartcode, R.xml.keyboard_layout);
		customKeyboardPartCode.registerEditText(R.id.p06_txt_input_part_code);

		// Pop-up input number
		llPopupQuantity = (LinearLayout) findViewById(R.id.p211_ll_input_number);
		imgPopupQuantity = (ImageView) findViewById(R.id.p2xx_quantity_image);
		etPopupQuantityAmount = (EditText) findViewById(R.id.p2xx_quantity_et_amount);
		tvPopupQuantityProductName = (TextView) findViewById(R.id.p2xx_quantity_name);
		tvPopupQuantityUnit = (TextView) findViewById(R.id.p2xx_quantity_tv_unit);
		tvPopupQuantityTitle = (TextView) findViewById(R.id.p2xx_quantity_tv_title);
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

		/**
		 * Controls actions
		 */
		// S211 main controls
		lvLeftAcceptanceSource.setOnItemClickListener(this);

		// Group 4 header buttons
		btnSubmit.setEnabled(false);

		tvHeaderTitle.setText(getString(R.string.h2xx_shipping_import_title));
		btnBack.setOnClickListener(this);
		btnViewHistory.setOnClickListener(this);
		btnStopRegister.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);

		// Pop-up view history
		tvAcceptanceHistoryMonthSelected.setText(String.valueOf(nCurrentMonth));
		tvHistoryMonthAcceptance.setText(String.valueOf(nCurrentMonth));
		tvAcceptanceHistoryYearSelected.setText(String.format("%tY", cal));

		btn_history_close.setOnClickListener(this);
		llDateSelection.setOnClickListener(this);

		// Pop-up part code
		btnPopupPartCodeCancel.setOnClickListener(this);
		btnPopupPartCodeRegister.setOnClickListener(this);

		// Pop-up input number
		tvPopupQuantityTitle
				.setText(getString(R.string.p_input_number_title_acceptance));

		btnPopupQuantityClose.setOnClickListener(this);
		tvPopupQuantityUnit.setOnClickListener(this);
		btnPlus01.setOnClickListener(this);
		btnPlus1.setOnClickListener(this);
		btnPlus10.setOnClickListener(this);
		btnMinus01.setOnClickListener(this);
		btnMinus1.setOnClickListener(this);
		btnMinus10.setOnClickListener(this);
		btnComplete.setOnClickListener(this);

		/**
		 * help
		 * 
		 * @author vdngo
		 */
		//
		mBtnHelp = (Button) findViewById(R.id.btn_q);
		mHelp = (TextView) findViewById(R.id.help_content);
		mHelpLayout = (LinearLayout) findViewById(R.id.s211_help);
		mBtnHelp.setOnClickListener(this);
		mHelpLayout.setOnClickListener(this);

	}

	// Call api 27 to get list in left resource ListView
	private void callAPI027() {

		Log.d(LOG, "callAPI027 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_027_PATH, params);
				Log.d(LOG, "URL: " + url);

				this.showProcessDialog(this, null, null);
				new CallAPIAsyncTask(this, JSONUtils.API_027_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = e.getMessage();
				Log.d(LOG, "callAPI27: ERROR " + message);

			}
		} else {
			// error: shopcode null
			Log.d(LOG, "callAPI27: ERROR shopcode null");
		}
		Log.d(LOG, "callAPI027 - END");
	}

	// Call api 28 to get list in right content ListView
	private void callAPI028(String code, String date) {

		Log.d(LOG, "callAPI028 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
			params.put(JSONUtils.API_028_PARAMS_KEY_CODE, code);
			params.put(JSONUtils.API_028_PARAMS_KEY_DATE, date);

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_028_PATH, params);
				Log.d(LOG, "URL: " + url);

				this.showProcessDialog(this, null, null);
				new CallAPIAsyncTask(this, JSONUtils.API_028_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = e.getMessage();
				Log.d(LOG, "callAPI28: ERROR " + message);

			}
		} else {
			// error: shopcode null
			Log.d(LOG, "callAPI28: ERROR shopcode null");
		}
		Log.d(LOG, "callAPI028 - END");
	}

	// Acceptance history
	private void callAPI029(String date) {

		Log.d(LOG, "callAPI029 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
			params.put(JSONUtils.API_018_PARAMS_KEY, date);

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_029_PATH, params);
				Log.d(LOG, "URL: " + url);

				this.showProcessDialog(this, null, null);
				new CallAPIAsyncTask(this, JSONUtils.API_029_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = e.getMessage();
				Log.d(LOG, "callAPI29: ERROR " + message);

			}
		} else {
			// error: shopcode null
			Log.d(LOG, "callAPI29: ERROR shopcode null");
		}
		Log.d(LOG, "callAPI029 - END");
	}

	// call API030 to update acceptance
	private void callAPI030() {
		if (StringUtils.isEmpty(this.etPartCodeInputValue.getText().toString())) {
			return;
		}
		if (listRightContent == null || listRightContent.size() <= 0) {
			return;
		}
		// make url
		StringBuilder url = new StringBuilder();
		url.append(JSONUtils.API_HOST_PATH);
		url.append(JSONUtils.API_030_PATH);

		// make parameter
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair(JSONUtils.TENPO_CODE, super.app
				.getShopCode()));

		API28_AcceptanceShop_Detail acceptanceReport = null;
		StringBuilder paramKey = null;
		for (int i = 0; i < listRightContent.size(); i++) {
			acceptanceReport = listRightContent.get(i);

			// egy_date
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_030_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.EGY_DATE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super
					.strCurrentDate()));

			// time_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_030_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TIME_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super
					.getTimeCode()));

			// tenpo_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_030_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_030_SND_TENPO)
					.append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super.app
					.getShopCode()));

			// rcv_tenpo (receive shop cd)
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_030_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_030_RCV_TENPO)
					.append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), strShopCode));

			// data_date (send date)
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_030_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_030_DATA_DATE)
					.append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super
					.strCurrentDate()));

			// data_time (send time)
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_030_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_030_DATA_TIME)
					.append("]");
			data.add(new BasicNameValuePair(paramKey.toString(),
					getCurrentTime()));

			// hin_kbn
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_030_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_030_HIN_KBN).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), HIN_BKN));

			// hin_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_030_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.HIN_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(),
					acceptanceReport.getHinCD()));

			// pre_rcv_su
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_030_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_030_PRE_SU).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(),
					arrStrPreRCVSum[i]));

			// pre_tani
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_030_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_030_PRE_TANI).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(),
					acceptanceReport.getTaniCD()));

			// rcv_su
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_030_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_030_RCV_SU).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(),
					acceptanceReport.getPreRCVSum()));

			// tani_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_030_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TANI_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(),
					acceptanceReport.getTaniCD()));

			// part_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_030_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.PART_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(),
					etPartCodeInputValue.getText().toString()));
		}

		// Show project dialog
		this.showProcessDialog(this, null, null);

		new PostAPIAsyncTask(this, data, JSONUtils.API_030_PATH).execute(url
				.toString());
	}

	private void displayLeftResourceData() {
		if (listLeftResource != null) {
			// Set size for array string pre_rcv_sum (max is size of
			// listLeftResource)

			Log.e("left list size", String.valueOf(listLeftResource.size()));

			// Set adapter
			leftResourceAdapter = new S211CustomListLeftResourceAdapter(
					getApplicationContext(), listLeftResource);
			leftResourceAdapter.notifyDataSetChanged();
			lvLeftAcceptanceSource.setAdapter(leftResourceAdapter);
		}
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

}
