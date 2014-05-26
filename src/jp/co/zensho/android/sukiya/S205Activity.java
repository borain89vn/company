package jp.co.zensho.android.sukiya;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.zensho.android.sukiya.adapter.S205CustomListHistoryAdapter;
import jp.co.zensho.android.sukiya.adapter.S205CustomListItemAdapter;
import jp.co.zensho.android.sukiya.bean.API14_ProductCatSel;
import jp.co.zensho.android.sukiya.bean.API15_ProductNameSel;
import jp.co.zensho.android.sukiya.bean.API20_ShortReason;
import jp.co.zensho.android.sukiya.bean.API21_ViewHistory;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import jp.co.zensho.android.sukiya.bean.S205_ListShortReport;
import jp.co.zensho.android.sukiya.calendar.OnSelectCalendarDailogListener;
import jp.co.zensho.android.sukiya.calendar.SukiyaMonthYearDialog;
import jp.co.zensho.android.sukiya.common.DatePickerConstant;
import jp.co.zensho.android.sukiya.common.DateUtils;
import jp.co.zensho.android.sukiya.common.JSONUtils;
import jp.co.zensho.android.sukiya.common.StringUtils;
import jp.co.zensho.android.sukiya.common.SukiyaNumberKeyboardPartCode;
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
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
 *         Class: Short report
 * 
 */
public class S205Activity extends S00Activity implements OnClickListener,
		CallAPIListener, OnSelectCalendarDailogListener {
	private static String LOG = "S205Activity";
	/**
	 * Android controls
	 */
	// S205 Main controls
	private ListView lvShortReport;
	private Button btnAddToList;
	private LinearLayout llDefaultBlank;
	private RelativeLayout rlProductCatSel, rlProductNameSel, rlShortReasonSel,
			rlHour;
	private TextView tvProductCatSel, tvProductNameSel, tvShortReasonSel,
			tvHour;
	private ImageView imgProductNameSel, imgShortReasonSel, imgHour;

	// Group 4 header buttons
	private TextView tvHeaderTitle;
	private Button btnBack, btnViewHistory, btnStopRegister, btnInputPartCode;

	// Pop-up view history
	private LinearLayout llPopupViewHistory, llDateSelection;
	private Button btn_p_view_history_close;
	private TextView tvShortHistoryMonthSelection, tvShortHistoryYearSelection;
	private ListView lvShortHistory;

	// Pop-up input part code
	// private LinearLayout llPopupPartCode;
	private RelativeLayout rlPopupPartCode;
	private Button btnPopupPartCodeCancel, btnPopupPartCodeRegister;
	private EditText etPartCodeInputValue;
	private SukiyaNumberKeyboardPartCode customKeyboardPartCode;

	// Class variables
	private List<API14_ProductCatSel> listCatSel;
	private List<API15_ProductNameSel> listNameSel;
	private List<API20_ShortReason> listShortReason;
	private List<API21_ViewHistory> listShortHistory;
	private List<S205_ListShortReport> listShortReport;

	private S205CustomListItemAdapter shortReportAdapter;
	private S205CustomListHistoryAdapter shortHistoryAdapter;

	private int nSelectedCatPos = 0;
	private int nSelectedNamePos = 0;
	private int nSelectedShortReasonPos = 0;
	private int nSelectedHourPos = 0;

	// Calendar
	Calendar cal;
	private String strHistoryDateParam;
	private int nCurrentYear, nCurrentMonth;

	/**
	 * Help button
	 * 
	 * @author vdngo
	 */
	// help
	private Button mBtnHelp;
	private TextView mHelp;
	private LinearLayout mHelpLayout;

	// Check pop-up history is opening
	private boolean isPopupHistoryShowed = false;

	// Listener
	// Register part code dialog listener
	protected DialogInterface.OnClickListener registPartCodeDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}

			callAPI022();
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
		setContentView(R.layout.s205);

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

		initView();
		listShortReport = new ArrayList<S205_ListShortReport>();
	}

	@Override
	protected void onResume() {
		super.onResume();
		callAPI014();
		callAPI020();
		// callAPI021(strHistoryDateParam);
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

		tvShortHistoryMonthSelection.setText(String.valueOf(month));
		tvShortHistoryYearSelection.setText(String.valueOf(year));

		// Set history date parameter
		StringBuilder builderDateParam = new StringBuilder();
		builderDateParam.append(tvShortHistoryYearSelection.getText()
				.toString());
		builderDateParam.append(String.format("%02d", month));
		strHistoryDateParam = builderDateParam.toString();

		callAPI021(strHistoryDateParam);

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
			super.getHelp(S205Activity.this, StringUtils.S205_HELP_CD);
		}

		switch (v.getId()) {
		case R.id.s205_rl_product_cat_sel:
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
					Builder builder = new AlertDialog.Builder(S205Activity.this);
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
										callAPI015();
										setControlEnabled(rlProductNameSel,
												tvProductNameSel,
												imgProductNameSel, true);

										// set default for name
										tvProductNameSel
												.setText(getString(R.string.product_name));
										tvHour.setText(Html
												.fromHtml(getString(R.string.short_report_hour)));
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

		case R.id.s205_rl_product_name_sel:
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
					Builder builder = new AlertDialog.Builder(S205Activity.this);
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
										// etHourInput.setEnabled(true);
										setControlEnabled(rlShortReasonSel,
												tvShortReasonSel,
												imgShortReasonSel, true);
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

		case R.id.s205_rl_short_reason_sel:
			try {
				// Convert List<> to String[]
				String[] arrayListShortRes = null;
				if (listShortReason != null && listShortReason.size() > 0) {
					arrayListShortRes = new String[listShortReason.size()];

					API20_ShortReason objAPI20 = null;
					for (int i = 0; i < listShortReason.size(); i++) {
						objAPI20 = listShortReason.get(i);
						if (objAPI20 != null) {
							arrayListShortRes[i] = objAPI20.getShortReason();
						}
					}

					// Show dialog single choice list of loss reason
					Builder builder = new AlertDialog.Builder(S205Activity.this);
					builder.setSingleChoiceItems(arrayListShortRes,
							nSelectedShortReasonPos,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									if (arg0 != null) {
										arg0.dismiss();
									}

									nSelectedShortReasonPos = arg1;
									tvShortReasonSel.setText(listShortReason
											.get(arg1).getShortReason());

									if (!StringUtils.EMPTY
											.equals(tvShortReasonSel.getText()
													.toString())) {
										setControlEnabled(rlHour, tvHour,
												imgHour, true);
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

		case R.id.s205_rl_hour:
			try {
				// Convert List<> to String[]
				final String[] arrayListHour = new String[24];
				for (int i = 0; i < 24; i++) {
					arrayListHour[i] = String.valueOf(i);
				}

				if (arrayListHour.length > 0) {
					// Show dialog single choice list of loss reason
					Builder builder = new AlertDialog.Builder(S205Activity.this);
					builder.setSingleChoiceItems(arrayListHour,
							nSelectedHourPos,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									if (arg0 != null) {
										arg0.dismiss();
									}

									nSelectedHourPos = arg1;
									tvHour.setText(arrayListHour[arg1]);
									btnAddToList.setEnabled(true);
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

		case R.id.s205_btn_add_to_list:
			// String strCurrentCat = tvProductCatSel.getText().toString();
			String strCurrentName = tvProductNameSel.getText().toString();
			String strCurrentReason = tvShortReasonSel.getText().toString();
			String strCurrentHour = tvHour.getText().toString();

			if (listShortReport.size() > 0) {
				for (int i = 0; i < listShortReport.size(); i++) {
					if (strCurrentName.equals(listShortReport.get(i)
							.getHinName())
							&& strCurrentReason.equals(listShortReport.get(i)
									.getShortReason())
							&& strCurrentHour.equals(listShortReport.get(i)
									.getHour())) {
						showToast(getString(R.string.short_duplicated_added_item_message));
						return;
					}
				}
			}

			S205_ListShortReport shortReportObj = new S205_ListShortReport();
			shortReportObj.setShortReasonCD(listShortReason.get(
					nSelectedShortReasonPos).getReasonCD());
			shortReportObj.setHinCD(listNameSel.get(nSelectedNamePos)
					.getHinCD());
			shortReportObj.setHinName(tvProductNameSel.getText().toString());
			shortReportObj
					.setShortReason(tvShortReasonSel.getText().toString());
			shortReportObj.setHour(tvHour.getText().toString());
			listShortReport.add(shortReportObj);

			if (listShortReport.size() > 0) {
				shortReportAdapter = new S205CustomListItemAdapter(
						getApplicationContext(), listShortReport,
						btnInputPartCode, llDefaultBlank);
				shortReportAdapter.notifyDataSetChanged();
				lvShortReport.setAdapter(shortReportAdapter);

				// set button input part code enable
				btnInputPartCode.setEnabled(true);
			}

			if (listShortReport.size() > 5) {
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

		case R.id.h2xx_btn_view_history:
			callPopUpHistory();
			break;

		case R.id.p205_ll_view_history_date_select:
			// showDialog(DATE_DIALOG_ID);
			SukiyaMonthYearDialog monthYearDialog = new SukiyaMonthYearDialog(
					S205Activity.this);
			monthYearDialog.setListener(this);
			monthYearDialog.setTodayToMaxDate();
			// monthYearDialog.setForcusCurrentYear();
			monthYearDialog.setForcusMonth(nCurrentMonth);
			monthYearDialog.setForcusYear(nCurrentYear);
			monthYearDialog.show();
			break;

		case R.id.p205_btn_close:
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
						// listCatSel = new
						// ArrayList<API14_ProductCatSel_Obj>();
						listCatSel = JSONUtils.parseListItemCategory(result);
					}

					if (JSONUtils.API_015_PATH.equals(tag)) {
						// listNameSel = new
						// ArrayList<API15_ProductNameSel_Obj>();
						listNameSel = JSONUtils.parseListItem(result);
					}

					if (JSONUtils.API_020_PATH.equals(tag)) {
						// listShortReason = new
						// ArrayList<API20_ShortReason_Obj>();
						listShortReason = JSONUtils
								.parseListShortReason(result);
					}

					if (JSONUtils.API_021_PATH.equals(tag)) {
						// listShortHistory = new
						// ArrayList<API21_ViewHistory_Obj>();
						listShortHistory = JSONUtils
								.parseListShortHistory(result);
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

			if (JSONUtils.API_021_PATH.equals(tag)) {
				// Update listLossHistory after date changed
				if (listShortHistory != null) {
					if (shortHistoryAdapter == null) {
						shortHistoryAdapter = new S205CustomListHistoryAdapter(
								getApplicationContext(), listShortHistory);
						shortHistoryAdapter.notifyDataSetChanged();
						lvShortHistory.setAdapter(shortHistoryAdapter);
					} else {
						shortHistoryAdapter.updateResults(listShortHistory);
					}
					shortHistoryAdapter.notifyDataSetChanged();
					lvShortHistory.invalidateViews();
				} else {
					listShortHistory = new ArrayList<API21_ViewHistory>();
					if (shortHistoryAdapter != null) {
						shortHistoryAdapter.updateResults(listShortHistory);
					}

				}

				if (isPopupHistoryShowed == false) {
					super.showPopup(llPopupViewHistory);
					isPopupHistoryShowed = true;
				}

			}

			if (JSONUtils.API_022_PATH.equals(tag)) {
				// Return to top screen
				finish();
				overridePendingTransition(R.animator.slide_in_left,
						R.animator.slide_out_right);
			}
		}
	}

	// Call API14 to get productCat_sel
	private void callAPI014() {
		Log.d(LOG, "callAPI014 - START");
		// Check if shopcode exist
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put("shop_code", super.app.getShopCode());

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_014_PATH, params);
				Log.d(LOG, "URL: " + url);

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
	private void callAPI015() {
		Log.d(LOG, "callAPI015 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
			params.put(JSONUtils.API_015_PARAMS_KEY,
					listCatSel.get(nSelectedCatPos).getDivCD());

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_015_PATH, params);
				Log.d(LOG, "URL: " + url);

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

	// Get short reason
	private void callAPI020() {
		Log.d(LOG, "callAPI020 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {

			try {
				String url = JSONUtils.getAPIUrl(JSONUtils.API_020_PATH, null);
				Log.d(LOG, "URL: " + url);

				new CallAPIAsyncTask(this, JSONUtils.API_020_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = e.getMessage();
				Log.d(LOG, "callAPI20: ERROR " + message);

			}
		} else {
			// error: shopcode null
			Log.d(LOG, "callAPI20: ERROR shopcode null");
		}
		Log.d(LOG, "callAPI020 - END");
	}

	// Short history
	private void callAPI021(String date) {

		Log.d(LOG, "callAPI021 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
			params.put(JSONUtils.API_018_PARAMS_KEY, date);

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_021_PATH, params);
				Log.d(LOG, "URL: " + url);

				this.showProcessDialog(this, null, null);
				new CallAPIAsyncTask(this, JSONUtils.API_021_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = e.getMessage();
				Log.d(LOG, "callAPI21: ERROR " + message);

			}
		} else {
			// error: shopcode null
			Log.d(LOG, "callAPI21: ERROR shopcode null");
		}
		Log.d(LOG, "callAPI021 - END");
	}

	// call API022 to update short
	private void callAPI022() {
		if (StringUtils.isEmpty(this.etPartCodeInputValue.getText().toString())) {
			return;
		}
		if (listShortReport == null || listShortReport.size() <= 0) {
			return;
		}
		// make url
		StringBuilder url = new StringBuilder();
		url.append(JSONUtils.API_HOST_PATH);
		url.append(JSONUtils.API_022_PATH);

		// make parameter
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair(JSONUtils.TENPO_CODE, super.app
				.getShopCode()));

		S205_ListShortReport shortReport = null;
		StringBuilder paramKey = null;
		for (int i = 0; i < listShortReport.size(); i++) {
			shortReport = listShortReport.get(i);

			// tenpo_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_022_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TENPO_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super.app
					.getShopCode()));

			// egy_date
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_022_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.EGY_DATE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super
					.strCurrentDate()));

			// time_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_022_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TIME_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super
					.getTimeCode()));

			// hin_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_022_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.HIN_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), shortReport
					.getHinCD()));

			// short reason
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_022_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_022_SHORT_REASON)
					.append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), shortReport
					.getShortReasonCD()));

			// hour
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_022_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_022_HOUR).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), shortReport
					.getHour()));

			// part_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_022_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.PART_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(),
					etPartCodeInputValue.getText().toString()));
		}

		// Show project dialog
		this.showProcessDialog(this, null, null);

		new PostAPIAsyncTask(this, data, JSONUtils.API_022_PATH).execute(url
				.toString());
	}

	// Initialize android controls and define actions
	private void initView() {
		// S205 main controls
		tvProductCatSel = (TextView) findViewById(R.id.s205_tv_product_cat_sel);
		tvProductNameSel = (TextView) findViewById(R.id.s205_tv_product_name_sel);
		tvShortReasonSel = (TextView) findViewById(R.id.s205_tv_short_reason_sel);
		tvHour = (TextView) findViewById(R.id.s205_tv_hour);
		lvShortReport = (ListView) findViewById(R.id.s205_lv_short_report);
		btnAddToList = (Button) findViewById(R.id.s205_btn_add_to_list);
		llDefaultBlank = (LinearLayout) findViewById(R.id.s205_ll_default_items_area);
		rlProductCatSel = (RelativeLayout) findViewById(R.id.s205_rl_product_cat_sel);
		rlProductNameSel = (RelativeLayout) findViewById(R.id.s205_rl_product_name_sel);
		rlShortReasonSel = (RelativeLayout) findViewById(R.id.s205_rl_short_reason_sel);
		rlHour = (RelativeLayout) findViewById(R.id.s205_rl_hour);
		imgProductNameSel = (ImageView) findViewById(R.id.s205_img_product_name_sel);
		imgShortReasonSel = (ImageView) findViewById(R.id.s205_img_short_reason_sel);
		imgHour = (ImageView) findViewById(R.id.s205_img_hour);

		// Group 4 header buttons
		tvHeaderTitle = (TextView) findViewById(R.id.h2xx_tv_header_title);
		btnBack = (Button) findViewById(R.id.h2xx_btn_back);
		btnViewHistory = (Button) findViewById(R.id.h2xx_btn_view_history);
		btnStopRegister = (Button) findViewById(R.id.h2xx_btn_stop_register);
		btnInputPartCode = (Button) findViewById(R.id.h2xx_btn_submit);

		// Pop-up view history
		llPopupViewHistory = (LinearLayout) findViewById(R.id.p205_ll_view_history);
		llDateSelection = (LinearLayout) findViewById(R.id.p205_ll_view_history_date_select);
		btn_p_view_history_close = (Button) findViewById(R.id.p205_btn_close);
		tvShortHistoryMonthSelection = (TextView) findViewById(R.id.p205_tv_view_history_month);
		tvShortHistoryYearSelection = (TextView) findViewById(R.id.p205_tv_view_history_year);
		lvShortHistory = (ListView) findViewById(R.id.p205_lv_short_report_history);

		// Pop-up input part code
		// llPopupPartCode = (LinearLayout)
		// findViewById(R.id.p205_ll_part_code);
		rlPopupPartCode = (RelativeLayout) findViewById(R.id.p06_input_part_code);
		btnPopupPartCodeCancel = (Button) findViewById(R.id.p06_btn_cancel_part_code_input);
		btnPopupPartCodeRegister = (Button) findViewById(R.id.p06_btn_regist_part_code_input);
		etPartCodeInputValue = (EditText) findViewById(R.id.p06_txt_input_part_code);
		customKeyboardPartCode = new SukiyaNumberKeyboardPartCode(this,
				R.id.keyboardviewPartcode, R.xml.keyboard_layout);
		customKeyboardPartCode.registerEditText(R.id.p06_txt_input_part_code);

		/**
		 * help
		 * 
		 * @author vdngo
		 */
		//
		mBtnHelp = (Button) findViewById(R.id.btn_q);
		mHelp = (TextView) findViewById(R.id.help_content);
		mHelpLayout = (LinearLayout) findViewById(R.id.s205_help);
		mBtnHelp.setOnClickListener(this);
		mHelpLayout.setOnClickListener(this);

		/**
		 * Actions
		 */
		// S205 main controls
		// setControlEnabled(rlProductNameSel, tvProductNameSel,
		// imgProductNameSel, false);
		// setControlEnabled(rlShortReasonSel, tvShortReasonSel,
		// imgShortReasonSel, false);
		// setControlEnabled(rlHour, tvHour, imgHour, false);
		// btnAddToList.setEnabled(false);
		setControlToDefault();

		rlProductCatSel.setOnClickListener(this);
		rlProductNameSel.setOnClickListener(this);
		rlShortReasonSel.setOnClickListener(this);
		rlHour.setOnClickListener(this);
		btnAddToList.setOnClickListener(this);

		// Group 4 header buttons
		btnInputPartCode.setEnabled(false);

		tvHeaderTitle.setText(getString(R.string.h2xx_short_report_title));
		btnBack.setOnClickListener(this);
		btnViewHistory.setOnClickListener(this);
		btnStopRegister.setOnClickListener(this);
		btnInputPartCode.setOnClickListener(this);

		// Pop-up view history
		btn_p_view_history_close.setOnClickListener(this);
		llDateSelection.setOnClickListener(this);
		tvShortHistoryYearSelection.setText(String.format("%tY", cal));
		tvShortHistoryMonthSelection.setText(String.valueOf(nCurrentMonth));

		// Pop-up input part code
		btnPopupPartCodeCancel.setOnClickListener(this);
		btnPopupPartCodeRegister.setOnClickListener(this);
	}

	private void callPopUpHistory() {
		// Show pop-up view history
		// super.showPopup(llPopupViewHistory);

		// Add data into list loss history
		// if (listShortHistory != null) {
		// shortHistoryAdapter = new S205CustomListHistoryAdapter(
		// getApplicationContext(), listShortHistory);
		// shortHistoryAdapter.notifyDataSetChanged();
		// lvShortHistory.setAdapter(shortHistoryAdapter);
		// }
		callAPI021(strHistoryDateParam);
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

	private void setControlEnabled(RelativeLayout rl, TextView tv,
			ImageView img, boolean b) {
		rl.setEnabled(b);
		tv.setEnabled(b);
		img.setEnabled(b);
	}

	private void setControlToDefault() {
		tvProductCatSel.setText(getString(R.string.product_category));
		tvProductNameSel.setText(getString(R.string.product_name));
		tvShortReasonSel.setText(getString(R.string.short_reason));
		tvHour.setText(Html.fromHtml(getString(R.string.short_report_hour)));

		nSelectedCatPos = 0;
		nSelectedNamePos = 0;
		nSelectedShortReasonPos = 0;
		nSelectedHourPos = 0;

		setControlEnabled(rlProductNameSel, tvProductNameSel,
				imgProductNameSel, false);
		setControlEnabled(rlShortReasonSel, tvShortReasonSel,
				imgShortReasonSel, false);
		setControlEnabled(rlHour, tvHour, imgHour, false);
		btnAddToList.setEnabled(false);
	}
}
