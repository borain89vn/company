package jp.co.zensho.android.sukiya;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.co.zensho.android.sukiya.adapter.S302CustomListItemAdapter;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import jp.co.zensho.android.sukiya.bean.MeterReadingInfo;
import jp.co.zensho.android.sukiya.calendar.OnSelectCalendarDailogListener;
import jp.co.zensho.android.sukiya.calendar.SukiyaMonthYearDialog;
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
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class S302Activity extends S00Activity implements OnClickListener,
		OnItemClickListener, CallAPIListener, OnSelectCalendarDailogListener {

	// main
	private ListView mWaterListView;
	private ListView mGasListView;
	private TextView mDatePullDown;
	private TextView mWaterCountTv;
	private TextView mGasCountTv;
	private TextView mBtnBack;
	private S302CustomListItemAdapter mwaterAdapter;
	private S302CustomListItemAdapter mgasAapter;
	// help
	private Button mBtnHelp;
	private TextView mHelp;
	private LinearLayout mHelpLayout;

	// Popup
	private RelativeLayout mMeterInputOverlay;
	private TextView mTitleOfInputType;
	private EditText mTvMeterAmount;
	private TextView mTvMeterType;
	private TextView mTvNumber1;
	private TextView mTvNumber2;
	private TextView mTvNumber3;
	private TextView mTvNumber4;
	private TextView mTvNumber5;
	private TextView mTvNumber6;
	private Button mBtnIncrease1;
	private Button mBtnIncrease2;
	private Button mBtnIncrease3;
	private Button mBtnIncrease4;
	private Button mBtnIncrease5;
	private Button mBtnIncrease6;
	private Button mBtnDecrease1;
	private Button mBtnDecrease2;
	private Button mBtnDecrease3;
	private Button mBtnDecrease4;
	private Button mBtnDecrease5;
	private Button mBtnDecrease6;
	private TextView mTvUsage;
	private TextView mLastMeter;
	private Button mBtnCloseOverlay;
	private Button mBtnConfirmOverlay;
	private String mStrNewMeter = "";
	private String mDatePullDownSelected;

	// Egy_date holder
	private String mEgyDateHolder;

	// Part code
	private RelativeLayout mPartCodePopup;
	private EditText mEditTextPartCode;
	private Button mBtnPartCodeCancellation;
	private Button mBtnPartCodeRegist;
	private String mPartCodeInputValue;
	// Zenshou ntdat 20140416 add ++
	private SukiyaNumberKeyboardPartCode customKeyboardPartCode;
	private SukiyaNumberKeyboardPartCode customKeyboardPartCode_amount;
	// Zenshou ntdat 20140416 add --

	// Calendar popup
	private int mCurrentYear = 0;
	private int mCurrentMonth = 0;

	// Hold data of the meter
	private MeterReadingInfo mMeterReadingInfo;

	// Regist part code dialog listener
	protected DialogInterface.OnClickListener registPartCodeDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}

			S302Activity.this.callAPI036();
		}
	};

	// / Reload call API 033
	private DialogInterface.OnClickListener retryAPI033DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S302Activity.this.callAPI033();
		}
	};
	// Reload call API 036
	private DialogInterface.OnClickListener retryAPI036DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S302Activity.this.callAPI036();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.s302);
		generateViews();

		// main
		mGasListView = (ListView) findViewById(R.id.s302_list_view_gas);
		mWaterListView = (ListView) findViewById(R.id.s302_list_view_water);
		mDatePullDown = (TextView) findViewById(R.id.s302_date_selection);

		// help
		mBtnHelp = (Button) findViewById(R.id.btn_q);
		mHelp = (TextView) findViewById(R.id.help_content);
		mHelpLayout = (LinearLayout) findViewById(R.id.s302_help);
		// Get current time
		Calendar current = Calendar.getInstance();
		String year = String.valueOf(current.get(Calendar.YEAR));
		String month = String.valueOf(current.get(Calendar.MONTH) + 1);

		mCurrentYear = current.get(Calendar.YEAR);
		mCurrentMonth = current.get(Calendar.MONTH) + 1;
		if (month.length() < 2) {
			month = "0" + month;
		}

		mDatePullDownSelected = String.valueOf(year) + month;

		mDatePullDown.setText(String.valueOf(year)
				+ getResources().getString(R.string.year) + month
				+ getResources().getString(R.string.month));

		mWaterCountTv = (TextView) findViewById(R.id.s302_tv_water_count);
		mGasCountTv = (TextView) findViewById(R.id.s302_tv_gas_count);
		mBtnBack = (TextView) findViewById(R.id.s302_btn_back);

		// Popup
		mMeterInputOverlay = (RelativeLayout) findViewById(R.id.meter_input_overlay);
		mTitleOfInputType = (TextView) findViewById(R.id.p_s30203_tv_title_popup);
		mTvMeterType = (TextView) findViewById(R.id.p_s30203_tv_meter_type);
		mTvMeterAmount = (EditText) findViewById(R.id.p_s30203_tv_amount);
		mBtnIncrease1 = (Button) findViewById(R.id.p_s30203_btn_plus1_first);
		mBtnIncrease2 = (Button) findViewById(R.id.p_s30203_btn_plus1_second);
		mBtnIncrease3 = (Button) findViewById(R.id.p_s30203_btn_plus1_third);
		mBtnIncrease4 = (Button) findViewById(R.id.p_s30203_btn_plus1_fourth);
		mBtnIncrease5 = (Button) findViewById(R.id.p_s30203_btn_plus1_fifth);
		mBtnIncrease6 = (Button) findViewById(R.id.p_s30203_btn_plus1_sixth);
		mBtnDecrease1 = (Button) findViewById(R.id.p_s30203_btn_minus1_first);
		mBtnDecrease2 = (Button) findViewById(R.id.p_s30203_btn_minus1_second);
		mBtnDecrease3 = (Button) findViewById(R.id.p_s30203_btn_minus1_third);
		mBtnDecrease4 = (Button) findViewById(R.id.p_s30203_btn_minus1_fourth);
		mBtnDecrease5 = (Button) findViewById(R.id.p_s30203_btn_minus1_fifth);
		mBtnDecrease6 = (Button) findViewById(R.id.p_s30203_btn_minus1_sixth);
		mTvNumber1 = (TextView) findViewById(R.id.p_s30203_tv_first_number);
		mTvNumber2 = (TextView) findViewById(R.id.p_s30203_tv_second_number);
		mTvNumber3 = (TextView) findViewById(R.id.p_s30203_tv_third_number);
		mTvNumber4 = (TextView) findViewById(R.id.p_s30203_tv_fourth_number);
		mTvNumber5 = (TextView) findViewById(R.id.p_s30203_tv_fifth_number);
		mTvNumber6 = (TextView) findViewById(R.id.p_s30203_tv_sixth_number);
		mTvUsage = (TextView) findViewById(R.id.p_s30203_tv_popup_usage);
		mLastMeter = (TextView) findViewById(R.id.p_s30203_tv_last_meter_overlay);
		mBtnCloseOverlay = (Button) findViewById(R.id.p_s30203_btn_popup_close);
		mBtnConfirmOverlay = (Button) findViewById(R.id.p_s30203_btn_popup_confirm);

		// PartCode
		mPartCodePopup = (RelativeLayout) findViewById(R.id.s301_input_partcode);
		mEditTextPartCode = (EditText) findViewById(R.id.p06_txt_input_part_code);
		mBtnPartCodeRegist = (Button) findViewById(R.id.p06_btn_regist_part_code_input);
		mBtnPartCodeCancellation = (Button) findViewById(R.id.p06_btn_cancel_part_code_input);
		// Zenshou ntdat 20140416 add ++
		customKeyboardPartCode = new SukiyaNumberKeyboardPartCode(this,
				R.id.keyboardviewPartcode, R.xml.keyboard_layout);
		customKeyboardPartCode.registerEditText(R.id.p06_txt_input_part_code);
		customKeyboardPartCode_amount = new SukiyaNumberKeyboardPartCode(this,
				R.id.keyboardviewAmount, R.xml.keyboard_layout);
		customKeyboardPartCode_amount.registerEditText(R.id.p_s30203_tv_amount);
		// Zenshou ntdat 20140416 add --

		// help
		mBtnHelp.setOnClickListener(this);
		mHelpLayout.setOnClickListener(this);
		// Set onclick listener
		mBtnBack.setOnClickListener(this);
		mDatePullDown.setOnClickListener(this);
		// Click on background -> hide popup
		mMeterInputOverlay.setOnClickListener(this);
		mBtnIncrease1.setOnClickListener(this);
		mBtnIncrease2.setOnClickListener(this);
		mBtnIncrease3.setOnClickListener(this);
		mBtnIncrease4.setOnClickListener(this);
		mBtnIncrease5.setOnClickListener(this);
		mBtnIncrease6.setOnClickListener(this);
		mBtnDecrease1.setOnClickListener(this);
		mBtnDecrease2.setOnClickListener(this);
		mBtnDecrease3.setOnClickListener(this);
		mBtnDecrease4.setOnClickListener(this);
		mBtnDecrease5.setOnClickListener(this);
		mBtnDecrease6.setOnClickListener(this);
		mBtnCloseOverlay.setOnClickListener(this);
		mBtnConfirmOverlay.setOnClickListener(this);

		mBtnPartCodeCancellation.setOnClickListener(this);
		mBtnPartCodeRegist.setOnClickListener(this);

		// Set onItemClickListner
		mGasListView.setOnItemClickListener(this);
		mWaterListView.setOnItemClickListener(this);

		// Call api
		callAPI033();

	}

	@Override
	protected void onStop() {
		super.removeUpdateCurrentDate();
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		TextView[] listTV = { mTvNumber1, mTvNumber2, mTvNumber3, mTvNumber4,
				mTvNumber5, mTvNumber6 };
		// Detect the number is changealbe or not
		boolean isChangeable = false;
		// Count the number of digits change
		int numberChange = 0;
		// Flag in the case we clicked increase button
		boolean isIncrease = false;
		// Flag in the case we clicked decrease button
		boolean isDecrease = false;

		// Hold click position in the meter
		int clickPosition = -1;
		// hold value of one number in the meter
		int tempNum;

		// Perform increasing clicked
		switch (v.getId()) {
		case R.id.p_s30203_btn_plus1_sixth:
			isIncrease = true;
			clickPosition = 5;
			tempNum = Integer.parseInt((String) mTvNumber6.getText());
			if (tempNum != 9) {
				mTvNumber6.setText(String.valueOf(tempNum + 1));
				isChangeable = true;
				break;
			} else {
				numberChange++;
			}

		case R.id.p_s30203_btn_plus1_fifth:
			isIncrease = true;
			if (numberChange == 0) { // Detect if it's clicked, not in the
										// previous case
				clickPosition = 4;
			}
			tempNum = Integer.parseInt((String) mTvNumber5.getText());
			if (tempNum != 9) {
				mTvNumber5.setText(String.valueOf(tempNum + 1));
				isChangeable = true;
				break;
			} else {
				numberChange++;
			}
		case R.id.p_s30203_btn_plus1_fourth:
			isIncrease = true;
			if (numberChange == 0) {
				clickPosition = 3;
			}
			tempNum = Integer.parseInt((String) mTvNumber4.getText());
			if (tempNum != 9) {
				mTvNumber4.setText(String.valueOf(tempNum + 1));
				isChangeable = true;
				break;
			} else {
				numberChange++;
			}
		case R.id.p_s30203_btn_plus1_third:
			isIncrease = true;
			if (numberChange == 0) {
				clickPosition = 2;
			}
			tempNum = Integer.parseInt((String) mTvNumber3.getText());
			if (tempNum != 9) {
				mTvNumber3.setText(String.valueOf(tempNum + 1));
				isChangeable = true;
				break;
			} else {
				numberChange++;
			}
		case R.id.p_s30203_btn_plus1_second:
			isIncrease = true;
			if (numberChange == 0) {
				clickPosition = 1;
			}
			tempNum = Integer.parseInt((String) mTvNumber2.getText());
			if (tempNum != 9) {
				mTvNumber2.setText(String.valueOf(tempNum + 1));
				isChangeable = true;
				break;
			} else {
				numberChange++;
			}
		case R.id.p_s30203_btn_plus1_first:
			isIncrease = true;
			if (numberChange == 0) {
				clickPosition = 0;
			}
			tempNum = Integer.parseInt((String) mTvNumber1.getText());
			if (tempNum != 9) {
				mTvNumber1.setText(String.valueOf(tempNum + 1));
				isChangeable = true;
				break;
			} else {
				numberChange++;
			}

		}

		// if one of the increasing button is clicked
		if (isIncrease) {
			if (isChangeable == false || numberChange == 0) {
				// Do nothing
			} else {
				for (int i = 0; i < numberChange; i++) {
					listTV[clickPosition - i].setText("0");

				}

			}
			// Determine usage amount in popup layout real-time
			DetermineUsageAmount();
		}

		// Perform decreasing clicked
		switch (v.getId()) {
		case R.id.p_s30203_btn_minus1_sixth:
			isDecrease = true;
			clickPosition = 5;
			tempNum = Integer.parseInt((String) mTvNumber6.getText());
			if (tempNum != 0) {
				mTvNumber6.setText(String.valueOf(tempNum - 1));
				isChangeable = true;
				break;
			} else {
				numberChange++;
			}

		case R.id.p_s30203_btn_minus1_fifth:
			isDecrease = true;
			if (numberChange == 0) { // Detect if it's clicked, not in the
										// previous case
				clickPosition = 4;
			}
			tempNum = Integer.parseInt((String) mTvNumber5.getText());
			if (tempNum != 0) {
				mTvNumber5.setText(String.valueOf(tempNum - 1));
				isChangeable = true;
				break;
			} else {
				numberChange++;
			}
		case R.id.p_s30203_btn_minus1_fourth:
			isDecrease = true;
			if (numberChange == 0) {
				clickPosition = 3;
			}
			tempNum = Integer.parseInt((String) mTvNumber4.getText());
			if (tempNum != 0) {
				mTvNumber4.setText(String.valueOf(tempNum - 1));
				isChangeable = true;
				break;
			} else {
				numberChange++;
			}
		case R.id.p_s30203_btn_minus1_third:
			isDecrease = true;
			if (numberChange == 0) {
				clickPosition = 2;
			}
			tempNum = Integer.parseInt((String) mTvNumber3.getText());
			if (tempNum != 0) {
				mTvNumber3.setText(String.valueOf(tempNum - 1));
				isChangeable = true;
				break;
			} else {
				numberChange++;
			}
		case R.id.p_s30203_btn_minus1_second:
			isDecrease = true;
			if (numberChange == 0) {
				clickPosition = 1;
			}
			tempNum = Integer.parseInt((String) mTvNumber2.getText());
			if (tempNum != 0) {
				mTvNumber2.setText(String.valueOf(tempNum - 1));
				isChangeable = true;
				break;
			} else {
				numberChange++;
			}
		case R.id.p_s30203_btn_minus1_first:
			isDecrease = true;
			if (numberChange == 0) {
				clickPosition = 0;
			}
			tempNum = Integer.parseInt((String) mTvNumber1.getText());
			if (tempNum != 0) {
				mTvNumber1.setText(String.valueOf(tempNum - 1));
				isChangeable = true;
				break;
			} else {
				numberChange++;
			}

		}

		// if one of the decreasing button is clicked
		if (isDecrease) {
			if (isChangeable == false || numberChange == 0) {
				// Do nothing
			} else {
				for (int i = 0; i < numberChange; i++) {
					listTV[clickPosition - i].setText("9");

				}
			}

			// Determine usage amount in popup layout real-time
			DetermineUsageAmount();
		}
		if (v.equals(mHelpLayout)) {
			super.hidePopup(mHelpLayout);
		} else if (v.equals(mBtnHelp)) {
			super.getHelp(S302Activity.this, StringUtils.S302_HELP_CD);
		} else if (mDatePullDown.equals(v)) {
			SukiyaMonthYearDialog monthYearDialog = new SukiyaMonthYearDialog(
					S302Activity.this);
			monthYearDialog.setForcusMonth(mCurrentMonth);
			monthYearDialog.setListener(this);
			// monthYearDialog.set
			monthYearDialog.setTodayToMaxDate();
			monthYearDialog.setForcusYear(mCurrentYear);
			monthYearDialog.show();
		} else if (mBtnBack.equals(v)) {
			finish();
			overridePendingTransition(R.animator.slide_in_left,
					R.animator.slide_out_right);
		} else if (mBtnCloseOverlay.equals(v)) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mTvMeterAmount.getWindowToken(), 0);
			super.hidePopup(mMeterInputOverlay);
		} else if (mMeterInputOverlay.equals(v)) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mTvMeterAmount.getWindowToken(), 0);
			super.hidePopup(mMeterInputOverlay);
		} else if (mBtnConfirmOverlay.equals(v)) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mTvMeterAmount.getWindowToken(), 0);
			super.hidePopup(mMeterInputOverlay);
			mEditTextPartCode.setText(StringUtils.EMPTY);
			super.showPopup(mPartCodePopup);
		} else if (mBtnPartCodeCancellation.equals(v)) {
			super.hidePopup(mPartCodePopup);
		} else if (mBtnPartCodeRegist.equals(v)) {
			this.registPartCodeInputAction();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> a, View v, int position, long arg3) {
		if (position == 0) {
			switch (a.getId()) {
			case R.id.s302_list_view_gas:
				mTitleOfInputType.setText(getResources()
						.getString(R.string.gas));
				mTvMeterType.setText(getResources().getString(R.string.gas));

				String lastGasMeter = MeterReadingInfo
						.getLastMeterAmount(mMeterReadingInfo
								.getGasListMeterInfo());
				Log.i("check last Gas meter", lastGasMeter);
				mLastMeter.setText(lastGasMeter);
				mTvMeterAmount.setText(mMeterReadingInfo.getGas_meter_num());

				String ngo = mMeterReadingInfo.getGasListMeterInfo()
						.get(position).getAmount();

				SetUpMeterAmount(ngo);
				DetermineUsageAmount();
				break;
			case R.id.s302_list_view_water:
				mTitleOfInputType.setText(getResources().getString(
						R.string.water));
				mTvMeterType.setText(getResources().getString(R.string.water));

				String lastWaterMeter = MeterReadingInfo
						.getLastMeterAmount(mMeterReadingInfo
								.getWaterListMeterInfo());
				mLastMeter.setText(lastWaterMeter);
				Log.i("lastWaterMeter", lastWaterMeter);

				mTvMeterAmount.setText(mMeterReadingInfo.getWater_meter_num());
				String ngo1 = mMeterReadingInfo.getWaterListMeterInfo()
						.get(position).getAmount();
				SetUpMeterAmount(ngo1);
				DetermineUsageAmount();
				break;
			}
			super.showPopup(mMeterInputOverlay);

		}
	}

	@Override
	public void callAPIFinish(String tag, JSONObject result, ErrorInfo error) {
		super.dismissProcessDialog();

		if (error == null && result != null) {
			try {
				if (!JSONUtils.hasError(result)) {
					if (JSONUtils.API_033_PATH.equals(tag)) {
						MeterReadingInfo meterReading = JSONUtils
								.parseGasAndWaterMeterReadingInfo(result);

						mMeterReadingInfo = meterReading;

					} else if (JSONUtils.API_043_PATH.equals(tag)) {
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

			DialogInterface.OnClickListener retryListener = null;
			if (JSONUtils.API_033_PATH.equals(tag)) {
				retryListener = retryAPI033DialogListener;
			} else if (JSONUtils.API_036_PATH.equals(tag)) {
				retryListener = retryAPI036DialogListener;
			}

			// Show error dialog
			super.showDialog(this, message, R.string.try_again, retryListener,
					R.string.cancel, dismissDialogListener);
		} else {
			if (JSONUtils.API_033_PATH.equals(tag)) {
				updateUT();
			} else if (JSONUtils.API_036_PATH.equals(tag)) {
				super.dismissProcessDialog();
				super.hidePopup(mPartCodePopup);
				S302Activity.this.callAPI033();
			}
		}

	}

	// Call API033
	private void callAPI033() {
		super.showProcessDialog(this, null, null);
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			Map<String, String> params = new HashMap<String, String>();

			params.put(JSONUtils.YYYYMM, mDatePullDownSelected);
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_033_PATH, params);
				// Dummy
				// url =
				// "http://192.168.0.191/zensho_dummy/api/meter/list1?tenpo_cd=0001&yyyymm=201403";
				new CallAPIAsyncTask(this, JSONUtils.API_033_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(MessageFormat.format("System error: {0}",
						e.getMessage()));
				builder.setCancelable(false);
				builder.setPositiveButton("Try again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								S302Activity.this.callAPI033();
							}
						});
				builder.setNegativeButton("Exist Application",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								S302Activity.this.finish();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Shop code not found.");
			builder.setCancelable(false);
			builder.setNegativeButton("Exist Application",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							S302Activity.this.finish();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	// CallAPI 036
	private void callAPI036() {

		// Wait to confirm partcode
		// if (StringUtils.isEmpty(this.mPartCodeInputValue)) {
		// return;
		// }

		// make url
		StringBuilder url = new StringBuilder();
		url.append(JSONUtils.API_HOST_PATH);
		url.append(JSONUtils.API_036_PATH);
		// url.append("http://192.168.0.191/zensho_dummy/api/meter/update"); //
		// Dummy

		// make parameter
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair(JSONUtils.TENPO_CODE, super.app
				.getShopCode()));
		String meterType = mTvMeterType.getText().toString();

		if (meterType.equals(getResources().getString(R.string.gas))) {
			data.add(new BasicNameValuePair(JSONUtils.METER_CATEGORY, "1"));
			Date egyDate = mMeterReadingInfo.getGasListMeterInfo().get(0)
					.getEgy_date();
			mEgyDateHolder = new SimpleDateFormat("yyyyMMdd",
					java.util.Locale.getDefault()).format(egyDate);
			data.add(new BasicNameValuePair(JSONUtils.EGY_DATE, mEgyDateHolder));
		} else {
			Date egyDate = mMeterReadingInfo.getWaterListMeterInfo().get(0)
					.getEgy_date();
			mEgyDateHolder = new SimpleDateFormat("yyyyMMdd",
					java.util.Locale.getDefault()).format(egyDate);
			data.add(new BasicNameValuePair(JSONUtils.EGY_DATE, mEgyDateHolder));
			data.add(new BasicNameValuePair(JSONUtils.METER_CATEGORY, "2"));
		}

		data.add(new BasicNameValuePair(JSONUtils.METER_NUM, mTvMeterAmount
				.getText().toString()));

		data.add(new BasicNameValuePair(JSONUtils.AMOUNT, mStrNewMeter));

		Log.i("Egy Date", mEgyDateHolder);
		data.add(new BasicNameValuePair(JSONUtils.PART_CODE,
				this.mPartCodeInputValue));

		// Show project dialog
		super.showProcessDialog(this, null, null);
		new PostAPIAsyncTask(S302Activity.this, data, JSONUtils.API_036_PATH)
				.execute(url.toString());

	}

	// Update User interface
	private void updateUT() {
		// To set kind of list-view
		String date = (String) mDatePullDown.getText();
		String yearMonth = date.substring(0, 4) + date.substring(5, 7);
		Log.i("MonthYear", yearMonth);
		boolean isCurrentYYYYMM = isCurrentYearMonth();

		mgasAapter = new S302CustomListItemAdapter(this,
				mMeterReadingInfo.getGasListMeterInfo(), isCurrentYYYYMM);
		mwaterAdapter = new S302CustomListItemAdapter(this,
				mMeterReadingInfo.getWaterListMeterInfo(), isCurrentYYYYMM);
		mWaterListView.setAdapter(mwaterAdapter);
		mGasListView.setAdapter(mgasAapter);

		DecimalFormat df = new DecimalFormat("#0.0");
		mWaterCountTv.setText(String.valueOf(df.format(mMeterReadingInfo
				.getWater_usage())));
		mGasCountTv.setText(String.valueOf(df.format(mMeterReadingInfo
				.getGas_usage())));

	}

	// check if the date which is chosen form datePicker is current date or not
	private boolean isCurrentYearMonth() {
		String date = (String) mDatePullDown.getText();
		String yearMonth = date.substring(0, 4) + date.substring(5, 7);

		String currentYear = String.valueOf(Calendar.getInstance().get(
				Calendar.YEAR));
		String currentMonth = String.valueOf(Calendar.getInstance().get(
				Calendar.MONTH) + 1);
		if (currentMonth.length() < 2) {
			currentMonth = "0" + currentMonth;
		}
		String currentMY = currentYear + currentMonth;
		if (yearMonth.equals(currentMY)) {
			return true;
		}
		return false;
	}

	// Setup the meter of the meter input layout form the last meter get from
	// API 033
	private void SetUpMeterAmount(String lastMeter) {
		TextView[] listTV = { mTvNumber1, mTvNumber2, mTvNumber3, mTvNumber4,
				mTvNumber5, mTvNumber6 };
		String doubleStr = "";
		if (!StringUtils.EMPTY.equals(lastMeter)) {
			doubleStr = String.format(Locale.getDefault(), "%07.1f",
					Double.parseDouble(lastMeter));
		} else {
			doubleStr = String.format(Locale.getDefault(), "%07.1f", 0.0d);
		}
		Log.i("Test parse double-ngo", doubleStr);
		for (int i = 0; i < listTV.length; i++) {

			if (i == 5) {
				listTV[5].setText(doubleStr.substring(6));
			} else {
				listTV[i].setText(doubleStr.substring(i, i + 1));
			}

		}

	}

	// Determine the usage amount in meter input popup
	private void DetermineUsageAmount() {
		TextView[] listTV = { mTvNumber1, mTvNumber2, mTvNumber3, mTvNumber4,
				mTvNumber5, mTvNumber6 };
		mStrNewMeter = StringUtils.EMPTY;
		for (int i = 0; i < listTV.length; i++) {
			mStrNewMeter += listTV[i].getText();
			if (i == 4) {
				mStrNewMeter += '.';
			}
		}
		String strOlderMeter = (String) mLastMeter.getText();

		Double mUsage;
		if (!mStrNewMeter.equals(StringUtils.EMPTY)) {
			if (strOlderMeter.equals(StringUtils.EMPTY)) {

				mUsage = Double.parseDouble(mStrNewMeter);
			} else {
				mUsage = Double.parseDouble(mStrNewMeter)
						- Double.parseDouble(strOlderMeter);
			}

			double finalValue = Math.round(mUsage * 100.0) / 100.0;

			mTvUsage.setText(String.valueOf(finalValue));
		} else {
			mTvUsage.setText("0");
		}
	}

	// Regist part code input
	private void registPartCodeInputAction() {
		String message = null;

		String value = mEditTextPartCode.getText().toString().trim();
		if (!StringUtils.isEmpty(value)) {
			try {
				// Integer.parseInt(value);
				this.mPartCodeInputValue = value;

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

	@Override
	public void selectedDate(Dialog dialog, int year, int month, int day) {
		if (dialog != null) {
			dialog.dismiss();
		}
		mCurrentYear = year;
		mCurrentMonth = month;
		String monthText = String.valueOf(month);
		if (month < 10) {
			monthText = "0" + monthText;
		}
		mDatePullDownSelected = String.valueOf(year) + monthText;
		String dateText = year + this.getResources().getString(R.string.year)
				+ monthText + this.getResources().getString(R.string.month);
		mDatePullDown.setText(dateText);
		S302Activity.this.callAPI033();
	}
}
