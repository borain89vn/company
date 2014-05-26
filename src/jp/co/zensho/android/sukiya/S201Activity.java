package jp.co.zensho.android.sukiya;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.zensho.android.sukiya.adapter.S201CustomListHistoryAdapter;
import jp.co.zensho.android.sukiya.adapter.S201CustomListItemAdapter;
import jp.co.zensho.android.sukiya.bean.API14_ProductCatSel;
import jp.co.zensho.android.sukiya.bean.API15_ProductNameSel;
import jp.co.zensho.android.sukiya.bean.API16_LossReason;
import jp.co.zensho.android.sukiya.bean.API17_QuantityUnit;
import jp.co.zensho.android.sukiya.bean.API18_ViewHistory;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import jp.co.zensho.android.sukiya.bean.S201_ListLossReport;
import jp.co.zensho.android.sukiya.calendar.OnSelectCalendarDailogListener;
import jp.co.zensho.android.sukiya.calendar.SukiyaMonthYearDialog;
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
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
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
 * @author ntdat Screen Loss Report
 * 
 */
public class S201Activity extends S00Activity implements OnClickListener,
		CallAPIListener, OnSelectCalendarDailogListener {
	private static final String LOG = "S201Activity";
	/**
	 * Android controls
	 */
	// S201 Main controls
	private Button btnAddToList;
	private ListView lvLossReport;
	private LinearLayout llQuantityInput;
	public LinearLayout llDefaultBlank;
	private TextView tvQuantityNo, tvQuantityUnit, tvProductCatSel,
			tvProductNameSel, tvLossReasonSel;
	private RelativeLayout rlProductCatSel, rlProductNameSel, rlLossReasonSel;
	private ImageView imgProductNameSel, imgLossReasonSel;

	// Group 4 header buttons
	private Button btnBack, btnViewHistory, btnStopRegister;
	public Button btnInputPartCode;

	// Pop-up input quantity
	public LinearLayout llPopupQuantity;
	private Button btnPopupQuantityClose, btnPlus10, btnPlus1, btnPlus01,
			btnMinus10, btnMinus1, btnMinus01, btnComplete;
	public EditText etPopupQuantityAmount;
	public TextView tvPopupQuantityProductName, tvPopupQuantityUnit;
	private TextView tvPopupQuantityTitle;
	public ImageView imgPopupQuantityProduct;
	private SukiyaNumberKeyboard customKeyboard;

	// Pop-up view history
	private LinearLayout llPopupViewHistory, llDateSelection;
	private Button btnPopupHistoryClose;
	private ListView lvLossHistory;
	private TextView tvLossHistoryMonthSelection, tvLossHistoryYearSelection,
			tvLossHistoryMoneySum;

	// Pop-up input part-code
	private RelativeLayout rlPopupPartCode;
	private Button btnPopupPartCodeCancel, btnPopupPartCodeRegister;
	private EditText etPartCodeInputValue;
	private SukiyaNumberKeyboardPartCode customKeyboardPartCode;

	// Class variables
	private List<API14_ProductCatSel> listCatSel;
	private List<API15_ProductNameSel> listNameSel;
	private List<API16_LossReason> listLossReason;
	private List<S201_ListLossReport> listLossReport;
	private List<API17_QuantityUnit> listQuantityUnit;
	private List<API17_QuantityUnit> listQuantityUnitEdit;
	private List<API17_QuantityUnit> listTempQuantity;
	private List<API18_ViewHistory> listLossHistory;

	private S201CustomListItemAdapter lossReportAdapter;
	private S201CustomListHistoryAdapter lossHistoryAdapter;

	private int nSelectedCatPos = 0;
	private int nSelectedNamePos = 0;
	private int nSelectedLossReasonPos = 0;
	private int nSelectedQuantityUnitPos = 0;
	private int nLossSumarry = 0;

	// Calendar
	private Calendar cal;
	private String strHistoryDateParam;
	private int nCurrentYear, nCurrentMonth;

	// Static variables (to get value from adapter)
	public int nPosition;
	public boolean isListItemNumberClicked = false;
	public boolean isPopupHistoryShowed = false;
	public boolean isLossReasonClicked = false;
	private boolean isFromS02 = false;
	private boolean isFromS04 = false;
	private String strCurrentDivCD = "";
	private String strCurrentHinCD = "";
	private String strIntentHinCD = "";
	private String strIntentHinName = "";

	/**
	 * Help button
	 * 
	 * @author vdngo
	 */
	// help
	private Button mBtnHelp;
	private TextView mHelp;
	private LinearLayout mHelpLayout;

	// Define listener
	// Register part code dialog listener
	protected DialogInterface.OnClickListener registPartCodeDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}

			callAPI019();
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
		setContentView(R.layout.s201);
		// Generate view from S00 (include menu)
		generateViews();

		// Call APIs
		callAPI014();
		callAPI016();

		// Change format current date to set params into API18
		cal = DateUtils.today();
		// Get current month and current year
		nCurrentMonth = cal.get(Calendar.MONTH) + 1;
		nCurrentYear = cal.get(Calendar.YEAR);

		StringBuilder builderDateParam = new StringBuilder();
		builderDateParam.append(String.format("%tY", cal));
		builderDateParam.append(String.format("%02d", nCurrentMonth));
		strHistoryDateParam = builderDateParam.toString();
		// Initialize class variables
		listLossReport = new ArrayList<S201_ListLossReport>();

		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Call APIs
		// callAPI014();
		// callAPI016();
		// callAPI018(strHistoryDateParam);
	}

	@Override
	protected void onStop() {
		this.removeUpdateCurrentDate();
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		if (customKeyboard.isCustomKeyboardVisible()) {
			customKeyboard.hideCustomKeyboard();
		} else if (customKeyboardPartCode.isCustomKeyboardVisible()) {
			customKeyboardPartCode.hideCustomKeyboard();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void selectedDate(Dialog dialog, int year, int month, int day) {
		if (dialog != null) {
			dialog.dismiss();
		}

		nCurrentMonth = month;
		nCurrentYear = year;

		tvLossHistoryMonthSelection.setText(String.valueOf(month));
		tvLossHistoryYearSelection.setText(String.valueOf(year));

		// Set history date parameter
		StringBuilder builderDateParam = new StringBuilder();
		builderDateParam
				.append(tvLossHistoryYearSelection.getText().toString());
		builderDateParam.append(String.format("%02d", month));
		strHistoryDateParam = builderDateParam.toString();

		callAPI018(strHistoryDateParam);

	}

	@Override
	public void onClick(View view) {
		/**
		 * help
		 * 
		 * @author vdngo
		 */
		if (view.equals(mHelpLayout)) {
			super.hidePopup(mHelpLayout);
		} else if (view.equals(mBtnHelp)) {
			super.getHelp(S201Activity.this, StringUtils.S201_HELP_CD);
		}

		switch (view.getId()) {
		case R.id.s201_rl_product_cat_sel:
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
					Builder builder = new AlertDialog.Builder(S201Activity.this);
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
										if (nSelectedCatPos < listCatSel.size()) {
											strCurrentDivCD = listCatSel.get(
													nSelectedCatPos).getDivCD();
											callAPI015(strCurrentDivCD);
										}

										// set default for relative value
										tvProductNameSel
												.setText(getString(R.string.product_name));
										tvQuantityNo
												.setText(getString(R.string.input_number));
										tvQuantityUnit.setText("");
										setQuantityEnabled(llQuantityInput,
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

		case R.id.s201_rl_product_name_sel:
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
					Builder builder = new AlertDialog.Builder(S201Activity.this);
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

										if (isLossReasonClicked == true) {
											setQuantityEnabled(llQuantityInput,
													tvQuantityNo, true);
										}
										// Return default value for relative
										// controls
										tvQuantityNo
												.setText(getString(R.string.input_number));
										tvQuantityUnit.setText("");
										setControlEnabled(rlLossReasonSel,
												tvLossReasonSel,
												imgLossReasonSel, true);
										btnAddToList.setEnabled(false);
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

		case R.id.s201_rl_loss_reason_sel:
			try {
				isLossReasonClicked = true;
				// Convert List<> to String[]
				String[] arrayListLossRes = null;
				if (listLossReason != null && listLossReason.size() > 0) {
					arrayListLossRes = new String[listLossReason.size()];

					API16_LossReason objAPI16 = null;
					for (int i = 0; i < listLossReason.size(); i++) {
						objAPI16 = listLossReason.get(i);
						if (objAPI16 != null) {
							arrayListLossRes[i] = objAPI16.getLossReason();
						}
					}

					// Show dialog single choice list of loss reason
					if (isFromS02 == true || isFromS04 == true) {
						nSelectedLossReasonPos = 10;
					}
					Builder builder = new AlertDialog.Builder(S201Activity.this);
					builder.setSingleChoiceItems(arrayListLossRes,
							nSelectedLossReasonPos,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									if (arg0 != null) {
										arg0.dismiss();
									}

									nSelectedLossReasonPos = arg1;
									tvLossReasonSel.setText(listLossReason.get(
											arg1).getLossReason());

									if (!StringUtils.EMPTY
											.equals(tvLossReasonSel.getText()
													.toString())) {
										setQuantityEnabled(llQuantityInput,
												tvQuantityNo, true);
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

		case R.id.s201_btn_add_to_list:
			// String strCurrentCat = tvProductCatSel.getText().toString();
			String strCurrentName = tvProductNameSel.getText().toString();
			// String strCurrentNo = tvQuantityNo.getText().toString();
			String strCurrentReason = tvLossReasonSel.getText().toString();
			String strCurrentTani = tvQuantityUnit.getText().toString();

			if (listLossReport.size() > 0) {
				for (int i = 0; i < listLossReport.size(); i++) {
					if (strCurrentName.equals(listLossReport.get(i)
							.getHinName())
							&& strCurrentReason.equals(listLossReport.get(i)
									.getLossReason())
							&& strCurrentTani.equals(listLossReport.get(i)
									.getTaniName())) {
						showToast(getString(R.string.loss_duplicated_added_item_message));
						return;
					}
				}
			}

			S201_ListLossReport lossReportObj = new S201_ListLossReport();
			lossReportObj.setLossReasonCD(listLossReason.get(
					nSelectedLossReasonPos).getReasonCD());
			lossReportObj.setDivCD(listCatSel.get(nSelectedCatPos).getDivCD());
			lossReportObj.setTaniName(tvQuantityUnit.getText().toString());
			lossReportObj.setHinName(tvProductNameSel.getText().toString());
			if (isFromS02 == true || isFromS04 == true) {
				lossReportObj.setHinCD(strIntentHinCD);
			} else {
				lossReportObj.setHinCD(listNameSel.get(nSelectedNamePos)
						.getHinCD());
			}

			lossReportObj.setLossReason(tvLossReasonSel.getText().toString());
			lossReportObj.setLossSum(tvQuantityNo.getText().toString());
			lossReportObj.setTaniCD(listQuantityUnit.get(
					nSelectedQuantityUnitPos).getTaniCD());

			listLossReport.add(lossReportObj);
			int size = listLossReport.size();
			if (size > 0) {
				lossReportAdapter = new S201CustomListItemAdapter(
						getApplicationContext(), listLossReport,
						S201Activity.this);
				lossReportAdapter.notifyDataSetChanged();
				lvLossReport.setAdapter(lossReportAdapter);

				// set button input part code enable
				btnInputPartCode.setEnabled(true);

				if (size > 5) {
					llDefaultBlank.setVisibility(View.GONE);
				}

				// Set controls to default value and enable status
				setControlToDefault();
			}

			break;

		case R.id.h2xx_btn_back:
			finish();
			overridePendingTransition(R.animator.slide_in_left,
					R.animator.slide_out_right);
			break;

		case R.id.h2xx_btn_view_history:
			callPopUpHistory();
			break;

		case R.id.p201_ll_view_history_date_select:
			SukiyaMonthYearDialog monthYearDialog = new SukiyaMonthYearDialog(
					S201Activity.this);
			monthYearDialog.setListener(this);
			monthYearDialog.setTodayToMaxDate();
			// monthYearDialog.setForcusCurrentYear();
			monthYearDialog.setForcusMonth(nCurrentMonth);
			monthYearDialog.setForcusYear(nCurrentYear);
			monthYearDialog.show();
			break;

		case R.id.p201_btn_close:
			// Close pop-up view history
			isPopupHistoryShowed = false;
			super.hidePopup(llPopupViewHistory);
			break;

		case R.id.s201_ll_quantity_unit_input:
			isListItemNumberClicked = false;
			if (listNameSel != null) {
				if (nSelectedNamePos < listNameSel.size()) {
					strCurrentHinCD = listNameSel.get(nSelectedNamePos)
							.getHinCD();
				}
			}

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
			if (isFromS02 == true || isFromS04 == true) {
				callAPI017(strIntentHinCD);
			} else {
				callAPI017(strCurrentHinCD);
			}

			break;

		case R.id.h2xx_btn_stop_register:
			super.showDialog(this, R.string.msg_201_stop_register_confirm,
					R.string.yes, backToPrevScreenListener, R.string.no,
					dismissDialogListener);
			break;

		case R.id.h2xx_btn_submit:
			// Display pop-up input part code
			super.showPopup(rlPopupPartCode);
			break;

		case R.id.p06_btn_regist_part_code_input:
			registPartCodeInputAction();
			break;

		case R.id.p06_btn_cancel_part_code_input:
			// Close pop-up input part code
			super.hidePopup(rlPopupPartCode);
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

					API17_QuantityUnit objAPI17 = null;
					for (int i = 0; i < listTempQuantity.size(); i++) {
						objAPI17 = listTempQuantity.get(i);
						if (objAPI17 != null) {
							arrayListQuantityUnit[i] = objAPI17.getTaniName();
						}
					}

					// Show dialog single choice list of productCat_sel
					// (Category)
					Builder builder = new AlertDialog.Builder(S201Activity.this);
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
									if (isListItemNumberClicked == false) {
										tvPopupQuantityUnit
												.setText(listQuantityUnit.get(
														position).getTaniName());
									} else {
										tvPopupQuantityUnit
												.setText(listQuantityUnitEdit
														.get(position)
														.getTaniName());
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
				for (int i = 0; i < listLossReport.size(); i++) {
					String strLoopName = listLossReport.get(i).getHinName();
					String strLoopReason = listLossReport.get(i)
							.getLossReason();
					String strLoopTani = listLossReport.get(i).getTaniName();
					String strSelectedName = listLossReport.get(nPosition)
							.getHinName();
					String strSelectedReason = listLossReport.get(nPosition)
							.getLossReason();
					String strSelectedTani = tvPopupQuantityUnit.getText()
							.toString();
					String strSelectedNumber = listLossReport.get(nPosition)
							.getLossSum();
					String strChangedNumber = etPopupQuantityAmount.getText()
							.toString();

					if (strLoopName.equals(strSelectedName)
							&& strLoopReason.equals(strSelectedReason)
							&& strLoopTani.equals(strSelectedTani)
							&& strSelectedNumber.equals(strChangedNumber)) {
						showToast(getString(R.string.loss_duplicated_added_item_message));

						return;
					}
				}
				if (listLossReport.size() > 0) {
					listLossReport.get(nPosition).setLossSum(
							etPopupQuantityAmount.getText().toString());
					listLossReport.get(nPosition).setTaniName(
							tvPopupQuantityUnit.getText().toString());
					lossReportAdapter.updateResults(listLossReport);
				}
			}

			// Close pop-up input quantity
			super.hidePopup(llPopupQuantity);
			if (!StringUtils.EMPTY.equals(tvQuantityNo.getText().toString())
					&& !StringUtils.EMPTY.equals(tvQuantityUnit.getText()
							.toString()) && rlLossReasonSel.isEnabled()) {
				btnAddToList.setEnabled(true);
			}

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

					if (JSONUtils.API_016_PATH.equals(tag)) {
						// listLossReason = new
						// ArrayList<API16_LossReason_Obj>();
						listLossReason = JSONUtils.parseListLossReason(result);

					}

					if (JSONUtils.API_017_PATH.equals(tag)) {
						listQuantityUnit = new ArrayList<API17_QuantityUnit>();
						if (isListItemNumberClicked == false) {
							listQuantityUnit = JSONUtils
									.parseListItemUnit(result);
						} else {
							listQuantityUnitEdit = JSONUtils
									.parseListItemUnit(result);
						}
					}

					if (JSONUtils.API_018_PATH.equals(tag)) {
						// listLossHistory = new
						// ArrayList<API18_ViewHistory_Obj>();
						nLossSumarry = result.getInt("total");
						// Set default format: 0,000 for money sum
						DecimalFormat df = new DecimalFormat("#,###");
						tvLossHistoryMoneySum.setText(df.format(nLossSumarry));

						listLossHistory = JSONUtils
								.parseListLossHistory(result);
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

			if (JSONUtils.API_015_PATH.equals(tag)) {
				setControlEnabled(rlProductNameSel, tvProductNameSel,
						imgProductNameSel, true);

			}

			if (JSONUtils.API_017_PATH.equals(tag)) {
				if (isListItemNumberClicked == false) {
					// Set product name in input number pop-up
					if (isFromS02 == true || isFromS04 == true) {
						SukiyaUtils.setImage(S201Activity.this,
								imgPopupQuantityProduct, strIntentHinCD
										+ ".jpg", R.drawable.no_image_191);
					} else {
						SukiyaUtils.setImage(S201Activity.this,
								imgPopupQuantityProduct, strCurrentHinCD
										+ ".jpg", R.drawable.no_image_191);
					}
					tvPopupQuantityProductName.setText(tvProductNameSel
							.getText().toString());
				}
				// show pop-up input number
				// super.showPopup(llPopupQuantity);
				AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
				alpha.setDuration(350);
				llPopupQuantity.startAnimation(alpha);
				llPopupQuantity.setVisibility(View.VISIBLE);

			}

			if (JSONUtils.API_018_PATH.equals(tag)) {
				// listLossHistory = new ArrayList<API18_ViewHistory_Obj>();
				// Update listLossHistory after date changed
				if (listLossHistory != null) {
					if (lossHistoryAdapter == null) {
						lossHistoryAdapter = new S201CustomListHistoryAdapter(
								getApplicationContext(), listLossHistory);
						lossHistoryAdapter.notifyDataSetChanged();
						lvLossHistory.setAdapter(lossHistoryAdapter);
					} else {
						lossHistoryAdapter.updateResults(listLossHistory);
					}
					lossHistoryAdapter.notifyDataSetChanged();
					lvLossHistory.invalidateViews();

				} else {
					listLossHistory = new ArrayList<API18_ViewHistory>();
					if (lossHistoryAdapter != null) {
						lossHistoryAdapter.updateResults(listLossHistory);
					}

				}

				if (isPopupHistoryShowed == false) {
					super.showPopup(llPopupViewHistory);
					isPopupHistoryShowed = true;
				}

			}

			if (JSONUtils.API_019_PATH.equals(tag)) {
				// Return to top screen
				finish();
				overridePendingTransition(R.animator.slide_in_left,
						R.animator.slide_out_right);
			}
		}
	}

	// Initialize android controls and define actions
	private void initView() {
		// S201 Main view
		tvProductCatSel = (TextView) findViewById(R.id.s201_tv_product_cat_sel);
		tvProductNameSel = (TextView) findViewById(R.id.s201_tv_product_name_sel);
		tvLossReasonSel = (TextView) findViewById(R.id.s201_tv_loss_reason_sel);
		llQuantityInput = (LinearLayout) findViewById(R.id.s201_ll_quantity_unit_input);
		llDefaultBlank = (LinearLayout) findViewById(R.id.s201_ll_default_items_area);
		rlProductCatSel = (RelativeLayout) findViewById(R.id.s201_rl_product_cat_sel);
		rlProductNameSel = (RelativeLayout) findViewById(R.id.s201_rl_product_name_sel);
		rlLossReasonSel = (RelativeLayout) findViewById(R.id.s201_rl_loss_reason_sel);
		tvQuantityNo = (TextView) findViewById(R.id.s201_tv_quantity_number);
		tvQuantityUnit = (TextView) findViewById(R.id.s201_tv_quantity_unit);
		imgProductNameSel = (ImageView) findViewById(R.id.s201_img_product_name_sel);
		imgLossReasonSel = (ImageView) findViewById(R.id.s201_img_loss_reason_sel);
		lvLossReport = (ListView) findViewById(R.id.s201_lv_loss_report);
		btnAddToList = (Button) findViewById(R.id.s201_btn_add_to_list);

		// Group 4 header buttons
		btnBack = (Button) findViewById(R.id.h2xx_btn_back);
		btnViewHistory = (Button) findViewById(R.id.h2xx_btn_view_history);
		btnStopRegister = (Button) findViewById(R.id.h2xx_btn_stop_register);
		btnInputPartCode = (Button) findViewById(R.id.h2xx_btn_submit);

		// pop-up input quantity
		llPopupQuantity = (LinearLayout) findViewById(R.id.p201_ll_input_number);
		etPopupQuantityAmount = (EditText) findViewById(R.id.p2xx_quantity_et_amount);
		tvPopupQuantityUnit = (TextView) findViewById(R.id.p2xx_quantity_tv_unit);
		tvPopupQuantityProductName = (TextView) findViewById(R.id.p2xx_quantity_name);
		tvPopupQuantityTitle = (TextView) findViewById(R.id.p2xx_quantity_tv_title);
		imgPopupQuantityProduct = (ImageView) findViewById(R.id.p2xx_quantity_image);
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
		llPopupViewHistory = (LinearLayout) findViewById(R.id.p201_ll_view_history);
		llDateSelection = (LinearLayout) findViewById(R.id.p201_ll_view_history_date_select);
		btnPopupHistoryClose = (Button) findViewById(R.id.p201_btn_close);
		lvLossHistory = (ListView) findViewById(R.id.p201_lv_loss_report_history);
		tvLossHistoryMonthSelection = (TextView) findViewById(R.id.p201_tv_view_history_month);
		tvLossHistoryYearSelection = (TextView) findViewById(R.id.p201_tv_view_history_year);
		tvLossHistoryMoneySum = (TextView) findViewById(R.id.p201_tv_money_value);

		// pop-up part code
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
		mHelpLayout = (LinearLayout) findViewById(R.id.s201_help);
		mBtnHelp.setOnClickListener(this);
		mHelpLayout.setOnClickListener(this);

		/**
		 * Set click action
		 */
		// S201 main view
		setControlToDefault();
		// Zenshou ntdat 20140417 add ++
		Bundle bdlFromIntent = getIntent().getExtras();
		if (bdlFromIntent != null) {
			isFromS02 = bdlFromIntent.getBoolean(S02Activity.S02_KEY);
			strIntentHinName = bdlFromIntent
					.getString(S02Activity.S02_HINNAME_KEY);
			strIntentHinCD = bdlFromIntent
					.getString(S02Activity.S02_HINCODE_KEY);
			isFromS04 = bdlFromIntent.getBoolean(S04Activity.S04_KEY);
			strIntentHinName = bdlFromIntent
					.getString(S04Activity.S04_HINNAME_KEY);
			strIntentHinCD = bdlFromIntent
					.getString(S04Activity.S04_HINCODE_KEY);
			if (isFromS02 == true || isFromS04 == true) {
				btnBack.setVisibility(View.INVISIBLE);
				setControlEnabled(rlProductNameSel, tvProductNameSel,
						imgProductNameSel, true);
				setControlEnabled(rlLossReasonSel, tvLossReasonSel,
						imgLossReasonSel, true);
				setQuantityEnabled(llQuantityInput, tvQuantityNo, true);
				tvProductNameSel.setText(strIntentHinName);
				tvLossReasonSel.setText(getString(R.string.out_of_date_reason));
			}
		}
		// Zenshou ntdat 20140417 add --

		btnAddToList.setOnClickListener(this);
		llQuantityInput.setOnClickListener(this);
		rlProductCatSel.setOnClickListener(this);
		rlProductNameSel.setOnClickListener(this);
		rlLossReasonSel.setOnClickListener(this);

		// Group 4 header buttons
		btnInputPartCode.setEnabled(false);

		btnBack.setOnClickListener(this);
		btnViewHistory.setOnClickListener(this);
		btnStopRegister.setOnClickListener(this);
		btnInputPartCode.setOnClickListener(this);

		// pop-up view history
		btnPopupHistoryClose.setOnClickListener(this);
		llDateSelection.setOnClickListener(this);
		tvLossHistoryYearSelection.setText(String.format("%tY", cal));
		tvLossHistoryMonthSelection.setText(String.valueOf(nCurrentMonth));

		// pop-up input quantity
		tvPopupQuantityTitle
				.setText(getString(R.string.p_input_number_title_loss));

		btnPopupQuantityClose.setOnClickListener(this);
		tvPopupQuantityUnit.setOnClickListener(this);
		btnPlus01.setOnClickListener(this);
		btnPlus1.setOnClickListener(this);
		btnPlus10.setOnClickListener(this);
		btnMinus01.setOnClickListener(this);
		btnMinus1.setOnClickListener(this);
		btnMinus10.setOnClickListener(this);
		btnComplete.setOnClickListener(this);

		// popup input part code
		btnPopupPartCodeCancel.setOnClickListener(this);
		btnPopupPartCodeRegister.setOnClickListener(this);
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

	// Get loss reason
	private void callAPI016() {
		Log.d(LOG, "callAPI016 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {

			try {
				String url = JSONUtils.getAPIUrl(JSONUtils.API_016_PATH, null);
				Log.d(LOG, "URL: " + url);

				this.showProcessDialog(this, null, null);
				new CallAPIAsyncTask(this, JSONUtils.API_016_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = e.getMessage();
				Log.d(LOG, "callAPI16: ERROR " + message);

			}
		} else {
			// error: shopcode null
			Log.d(LOG, "callAPI16: ERROR shopcode null");
		}
		Log.d(LOG, "callAPI016 - END");
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

	private void callAPI018(String date) {

		Log.d(LOG, "callAPI018 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
			params.put(JSONUtils.API_018_PARAMS_KEY, date);

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_018_PATH, params);
				Log.d(LOG, "URL: " + url);

				this.showProcessDialog(this, null, null);
				new CallAPIAsyncTask(this, JSONUtils.API_018_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = e.getMessage();
				Log.d(LOG, "callAPI18: ERROR " + message);

			}
		} else {
			// error: shopcode null
			Log.d(LOG, "callAPI18: ERROR shopcode null");
		}
		Log.d(LOG, "callAPI018 - END");
	}

	// call API019 to update loss
	private void callAPI019() {
		if (StringUtils.isEmpty(this.etPartCodeInputValue.getText().toString())) {
			return;
		}
		if (listLossReport == null || listLossReport.size() <= 0) {
			return;
		}
		// make url
		StringBuilder url = new StringBuilder();
		url.append(JSONUtils.API_HOST_PATH);
		url.append(JSONUtils.API_019_PATH);

		// make parameter
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair(JSONUtils.TENPO_CODE, super.app
				.getShopCode()));

		S201_ListLossReport lossReport = null;
		StringBuilder paramKey = null;
		for (int i = 0; i < listLossReport.size(); i++) {
			lossReport = listLossReport.get(i);

			// tenpo_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_019_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TENPO_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super.app
					.getShopCode()));

			// egy_date
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_019_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.EGY_DATE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super
					.strCurrentDate()));

			// time_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_019_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TIME_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), super
					.getTimeCode()));

			// hin_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_019_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.HIN_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), lossReport
					.getHinCD()));

			// loss reason
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_019_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_019_LOSS_REASON)
					.append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), lossReport
					.getLossReasonCD()));

			// loss sum
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_019_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.API_019_LOSS_SUM).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), lossReport
					.getLossSum()));

			// tani_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_019_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.TANI_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), lossReport
					.getTaniCD()));

			// part_cd
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_019_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.PART_CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(),
					etPartCodeInputValue.getText().toString()));
		}

		// Show project dialog
		this.showProcessDialog(this, null, null);

		new PostAPIAsyncTask(this, data, JSONUtils.API_019_PATH).execute(url
				.toString());
	}

	private void callPopUpHistory() {
		callAPI018(strHistoryDateParam);

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

	private void setControlEnabled(RelativeLayout rl, TextView tv,
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
		tvLossReasonSel.setText(getString(R.string.loss_reason));
		tvQuantityNo.setText(getString(R.string.input_number));
		tvQuantityUnit.setText("");
		etPopupQuantityAmount.setText("");
		tvPopupQuantityUnit.setText("");

		nSelectedCatPos = 0;
		nSelectedNamePos = 0;
		nSelectedLossReasonPos = 0;
		nSelectedQuantityUnitPos = 0;

		isLossReasonClicked = false;

		setControlEnabled(rlProductNameSel, tvProductNameSel,
				imgProductNameSel, false);
		setControlEnabled(rlLossReasonSel, tvLossReasonSel, imgLossReasonSel,
				false);
		setQuantityEnabled(llQuantityInput, tvQuantityNo, false);
		btnAddToList.setEnabled(false);
	}
}
