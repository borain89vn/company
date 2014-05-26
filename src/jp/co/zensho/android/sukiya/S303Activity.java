package jp.co.zensho.android.sukiya;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.co.zensho.android.sukiya.adapter.S303CustomListItemAdapter;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import jp.co.zensho.android.sukiya.bean.MeterReadingInfo;
import jp.co.zensho.android.sukiya.bean.MeterType;
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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class S303Activity extends S00Activity implements OnClickListener,
		CallAPIListener, OnItemClickListener {

	// Main
	private ListView mElectricityThisYearListView;
	private ListView mElectricityLastYearListView;
	private Button mBtn100v;
	private Button mBtn200v;
	private Button mBtn300v;
	private Button mBtn400v;
	private Button mBtnBack;
	private TextView mDatePullDown;
	private ArrayList<MeterType> mlistMeter;
	private MeterReadingInfo mMeterReadingInfo;
	private boolean mIsUpdateMeterType = false;
	private boolean mIsUpdateListView = false;
	private TextView mMeterType1;
	private TextView mMeterCode1;
	private TextView mMeterType2;
	private TextView mMeterCode2;
	private S303CustomListItemAdapter mLastYearAdapter;
	private S303CustomListItemAdapter mThisYearAapter;
	private TextView mTvThisYear;
	private TextView mTvLastYear;
	private int mCurrentMeterCode = 0;
	String[] mYearList = null;
	private int currentLocationIndexSelect = 0;
	private String mSelectedYear;

	// help
	private Button mBtnHelp;
	private TextView mHelp;
	private LinearLayout mHelpLayout;

	// Popup
	private RelativeLayout mMeterInputOverlay;
	private TextView mTitleOfInputType;
	private TextView mMeterTypePopup;
	private EditText mTvMeterAmount;
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

	// Regist part code dialog listener
	protected DialogInterface.OnClickListener registPartCodeDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}

			S303Activity.this.callAPI036();
		}
	};

	// Reload call API 034
	private DialogInterface.OnClickListener retryAPI034DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S303Activity.this.callAPI034();
		}
	};
	// Reload call API 035
	private DialogInterface.OnClickListener retryAPI035DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S303Activity.this.callAPI035();
		}
	};

	// Reload call API 036
	private DialogInterface.OnClickListener retryAPI036DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S303Activity.this.callAPI036();
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.s303);

		generateViews();

		// Main
		mElectricityThisYearListView = (ListView) findViewById(R.id.s303_list_view_this_year);
		mElectricityLastYearListView = (ListView) findViewById(R.id.s303_list_view_last_year);
		mTvThisYear = (TextView) findViewById(R.id.s303_tv_this_year);
		mTvLastYear = (TextView) findViewById(R.id.s303_tv_last_year);
		mBtn100v = (Button) findViewById(R.id.s303_btn_100v);
		mBtn200v = (Button) findViewById(R.id.s303_btn_200v);
		mBtn300v = (Button) findViewById(R.id.s303_btn_300v);
		mBtn400v = (Button) findViewById(R.id.s303_btn_400v);
		mBtnBack = (Button) findViewById(R.id.s303_btn_back);
		mMeterType1 = (TextView) findViewById(R.id.s303_tv_meter_type_1);
		mMeterType2 = (TextView) findViewById(R.id.s303_tv_meter_type_2);
		mMeterCode1 = (TextView) findViewById(R.id.s303_tv_meter_code_1);
		mMeterCode2 = (TextView) findViewById(R.id.s303_tv_meter_code_2);
		mDatePullDown = (TextView) findViewById(R.id.s303_date_selection);

		// help
		mBtnHelp = (Button) findViewById(R.id.btn_q);
		mHelp = (TextView) findViewById(R.id.help_content);
		mHelpLayout = (LinearLayout) findViewById(R.id.s303_help);

		mDatePullDown.setText(String.valueOf(Calendar.getInstance().get(
				Calendar.YEAR))
				+ getResources().getString(R.string.year));
		mSelectedYear = String.valueOf(Calendar.getInstance()
				.get(Calendar.YEAR));

		// Popup
		mMeterInputOverlay = (RelativeLayout) findViewById(R.id.meter_input_overlay);
		mTitleOfInputType = (TextView) findViewById(R.id.p_s30203_tv_title_popup);
		mMeterTypePopup = (TextView) findViewById(R.id.p_s30203_tv_meter_type);
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

		// set OnclickListener
		mBtn100v.setOnClickListener(this);
		mBtn200v.setOnClickListener(this);
		mBtn300v.setOnClickListener(this);
		mBtn400v.setOnClickListener(this);
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
		mElectricityThisYearListView.setOnItemClickListener(this);

		// Call API
		callAPI034();

	}

	@Override
	protected void onStop() {
		super.removeUpdateCurrentDate();
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		final int backgroundClicked = R.drawable.s303_toggle_btn;
		final int backGroundNotClicked = getResources().getColor(R.color.green);
		final int textColorClicked = getResources().getColor(R.color.green);

		final int textColorNotClicked = getResources().getColor(R.color.white);

		if (v.equals(mHelpLayout)) {
			super.hidePopup(mHelpLayout);
		} else if (v.equals(mBtnHelp)) {
			super.getHelp(S303Activity.this, StringUtils.S303_HELP_CD);
		} else if (mBtn100v.equals(v)) {
			if (mCurrentMeterCode != mlistMeter.get(0).getCode()) {
				mBtn100v.setBackgroundResource(backgroundClicked);
				mBtn100v.setTextColor(textColorClicked);

				mBtn200v.setBackgroundColor(backGroundNotClicked);
				mBtn200v.setTextColor(textColorNotClicked);
				mBtn300v.setBackgroundColor(backGroundNotClicked);
				mBtn300v.setTextColor(textColorNotClicked);
				mBtn400v.setBackgroundColor(backGroundNotClicked);
				mBtn400v.setTextColor(textColorNotClicked);

				mMeterType1.setText(mlistMeter.get(0).getName());
				mMeterType2.setText(mlistMeter.get(0).getName());
				mCurrentMeterCode = mlistMeter.get(0).getCode();
				S303Activity.this.callAPI035();
			}

		} else if (mBtn200v.equals(v)) {
			if (mCurrentMeterCode != mlistMeter.get(1).getCode()) {
				mBtn200v.setBackgroundResource(backgroundClicked);
				mBtn200v.setTextColor(textColorClicked);

				mBtn100v.setBackgroundColor(backGroundNotClicked);
				mBtn100v.setTextColor(textColorNotClicked);
				mBtn300v.setBackgroundColor(backGroundNotClicked);
				mBtn300v.setTextColor(textColorNotClicked);
				mBtn400v.setBackgroundColor(backGroundNotClicked);
				mBtn400v.setTextColor(textColorNotClicked);
				mMeterType1.setText(mlistMeter.get(1).getName());
				mMeterType2.setText(mlistMeter.get(1).getName());
				mCurrentMeterCode = mlistMeter.get(1).getCode();

				S303Activity.this.callAPI035();
			}
		} else if (mBtn300v.equals(v)) {
			if (mCurrentMeterCode != mlistMeter.get(2).getCode()) {
				mBtn300v.setBackgroundResource(backgroundClicked);
				mBtn300v.setTextColor(textColorClicked);

				mBtn200v.setBackgroundColor(backGroundNotClicked);
				mBtn200v.setTextColor(textColorNotClicked);
				mBtn100v.setBackgroundColor(backGroundNotClicked);
				mBtn100v.setTextColor(textColorNotClicked);
				mBtn400v.setBackgroundColor(backGroundNotClicked);
				mBtn400v.setTextColor(textColorNotClicked);
				mMeterType1.setText(mlistMeter.get(2).getName());
				mMeterType2.setText(mlistMeter.get(2).getName());
				mCurrentMeterCode = mlistMeter.get(2).getCode();

				S303Activity.this.callAPI035();
			}
		} else if (mBtn400v.equals(v)) {
			if (mCurrentMeterCode != mlistMeter.get(3).getCode()) {
				mBtn400v.setBackgroundResource(backgroundClicked);
				mBtn400v.setTextColor(textColorClicked);

				mBtn200v.setBackgroundColor(backGroundNotClicked);
				mBtn200v.setTextColor(textColorNotClicked);
				mBtn300v.setBackgroundColor(backGroundNotClicked);
				mBtn300v.setTextColor(textColorNotClicked);
				mBtn100v.setBackgroundColor(backGroundNotClicked);
				mBtn100v.setTextColor(textColorNotClicked);
				mMeterType1.setText(mlistMeter.get(3).getName());
				mMeterType2.setText(mlistMeter.get(3).getName());
				mCurrentMeterCode = mlistMeter.get(3).getCode();

				S303Activity.this.callAPI035();
			}
		} else if (mBtnBack.equals(v)) {
			finish();
			overridePendingTransition(R.animator.slide_in_left,
					R.animator.slide_out_right);
		} else if (mDatePullDown.equals(v)) {
			DatePullDownSelectionClick();
		} else if (mBtnCloseOverlay.equals(v)) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mTvMeterAmount.getWindowToken(), 0);
			super.hidePopup(mMeterInputOverlay);
		} else if (mBtnConfirmOverlay.equals(v)) {
			super.hidePopup(mMeterInputOverlay);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mTvMeterAmount.getWindowToken(), 0);
			mEditTextPartCode.setText(StringUtils.EMPTY);
			super.showPopup(mPartCodePopup);
		} else if (mMeterInputOverlay.equals(v)) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mTvMeterAmount.getWindowToken(), 0);
			super.hidePopup(mMeterInputOverlay);
		} else if (mBtnPartCodeCancellation.equals(v)) {
			super.hidePopup(mPartCodePopup);
		} else if (mBtnPartCodeRegist.equals(v)) {
			this.registPartCodeInputAction();
		}

		// Increase and Decrease in Popup layout handler
		TextView[] listTV = { mTvNumber1, mTvNumber2, mTvNumber3, mTvNumber4,
				mTvNumber5, mTvNumber6 };
		// Detect the number is changeable or not
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

		if (isDecrease) {
			if (isChangeable == false || numberChange == 0) {
				// Do nothing
			} else {
				for (int i = 0; i < numberChange; i++) {
					listTV[clickPosition - i].setText("9");

				}
			}

			// if one of the decreasing button is clicked
			DetermineUsageAmount();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		if (position == 0) {

			mTitleOfInputType.setText(getResources().getString(
					R.string.electricity));
			mMeterTypePopup.setText(getResources().getString(
					R.string.electricity));

			mTvMeterAmount.setText(mMeterReadingInfo.getThis_year_meter_num());
			String ngo = mMeterReadingInfo.getElectricityListMeterThisYear()
					.get(position).getAmount();

			String lastElectricityMeter = MeterReadingInfo
					.getLastMeterAmount(mMeterReadingInfo
							.getElectricityListMeterThisYear());
			SetUpMeterAmount(ngo);
			mLastMeter.setText(lastElectricityMeter);
			DetermineUsageAmount();
			super.showPopup(mMeterInputOverlay);

		}

	}

	@Override
	public void callAPIFinish(String tag, JSONObject result, ErrorInfo error) {
		super.dismissProcessDialog();

		if (error == null && result != null) {
			try {
				if (!JSONUtils.hasError(result)) {
					if (JSONUtils.API_034_PATH.equals(tag)) {
						ArrayList<MeterType> listMeter = JSONUtils
								.parseListMeterType(result);

						this.mlistMeter = listMeter;
					} else if (JSONUtils.API_035_PATH.equals(tag)) {
						MeterReadingInfo meterReading = JSONUtils
								.parseElectricityReadingInfo(result);

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
			if (JSONUtils.API_034_PATH.equals(tag)) {
				retryListener = retryAPI034DialogListener;
			} else if (JSONUtils.API_035_PATH.equals(tag)) {
				retryListener = retryAPI035DialogListener;
			} else if (JSONUtils.API_036_PATH.equals(tag)) {
				retryListener = retryAPI036DialogListener;
			}

			// Show error dialog
			super.showDialog(this, message, R.string.try_again, retryListener,
					R.string.cancel, dismissDialogListener);

		} else {
			if (JSONUtils.API_034_PATH.equals(tag)) {
				mIsUpdateMeterType = true;
				updateUI();

				S303Activity.this.callAPI035();

			} else if (JSONUtils.API_035_PATH.equals(tag)) {
				mIsUpdateListView = true;
				updateUI();
			} else if (JSONUtils.API_036_PATH.equals(tag)) {
				super.dismissProcessDialog();
				super.hidePopup(mPartCodePopup);
				S303Activity.this.callAPI035();
			}
		}

	}

	// Update User interface
	private void updateUI() {

		if (mIsUpdateMeterType) {
			mIsUpdateMeterType = false;
			switch (mlistMeter.size()) {
			case 1:
				mBtn100v.setText(mlistMeter.get(0).getName());
				mBtn200v.setVisibility(View.INVISIBLE);
				mBtn300v.setVisibility(View.INVISIBLE);
				mBtn400v.setVisibility(View.INVISIBLE);
				break;
			case 2:
				mBtn100v.setText(mlistMeter.get(0).getName());
				mBtn200v.setText(mlistMeter.get(1).getName());
				mBtn300v.setVisibility(View.INVISIBLE);
				mBtn400v.setVisibility(View.INVISIBLE);
				break;
			case 3:
				mBtn100v.setText(mlistMeter.get(0).getName());
				mBtn200v.setText(mlistMeter.get(1).getName());
				mBtn300v.setText(mlistMeter.get(2).getName());
				mBtn400v.setVisibility(View.INVISIBLE);
				break;
			case 4:
				mBtn100v.setText(mlistMeter.get(0).getName());
				mBtn200v.setText(mlistMeter.get(1).getName());
				mBtn300v.setText(mlistMeter.get(2).getName());
				mBtn400v.setText(mlistMeter.get(3).getName());
				break;
			default:
				break;
			}
			mMeterType1.setText(mlistMeter.get(0).getName());
			mMeterType2.setText(mlistMeter.get(0).getName());
			mCurrentMeterCode = mlistMeter.get(0).getCode();

		} else if (mIsUpdateListView == true) {
			mIsUpdateListView = false;
			// set adapter
			// To set kind of list-view
			String date = (String) mDatePullDown.getText();
			String year = date.substring(0, 4);
			mTvThisYear.setText(year);
			mTvLastYear.setText(String.valueOf(Integer.parseInt(year) - 1));
			mMeterCode1.setText(mMeterReadingInfo.getLast_year_meter_num());
			mMeterCode2.setText(mMeterReadingInfo.getThis_year_meter_num());
			boolean isCurrentYYYY = isCurrentYear(year);
			mThisYearAapter = new S303CustomListItemAdapter(this,
					mMeterReadingInfo.getElectricityListMeterThisYear(),
					isCurrentYYYY, false);
			mLastYearAdapter = new S303CustomListItemAdapter(this,
					mMeterReadingInfo.getElectricityListMeterLastYear(),
					isCurrentYYYY, true);
			mElectricityThisYearListView.setAdapter(mThisYearAapter);
			mElectricityLastYearListView.setAdapter(mLastYearAdapter);

		}

	}

	// Check if string year is the current year or not
	private boolean isCurrentYear(String year) {
		String currentYear = String.valueOf(Calendar.getInstance().get(
				Calendar.YEAR));
		if (currentYear.equals(year)) {
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
		// String doubleStr = df.format(Double.parseDouble(lastMeter));
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

	// callAPI 034
	private void callAPI034() {
		super.showProcessDialog(this, null, null);
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_034_PATH, params);

				new CallAPIAsyncTask(this, JSONUtils.API_034_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(MessageFormat.format("System error: {0}",
						e.getMessage()));
				builder.setCancelable(false);
				builder.setPositiveButton("Try again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								S303Activity.this.callAPI034();
							}
						});
				builder.setNegativeButton("Exist Application",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								S303Activity.this.finish();
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
							S303Activity.this.finish();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	// Call API 035
	private void callAPI035() {
		super.showProcessDialog(this, null, null);
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			Map<String, String> params = new HashMap<String, String>();

			params.put(JSONUtils.METER_CD, String.valueOf(mCurrentMeterCode));

			params.put(JSONUtils.YYYY, mSelectedYear);

			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_035_PATH, params);
				Log.i("Ngo test selected url", url);
				// Dummy url
				// url =
				// "http://192.168.0.191/zensho_dummy/api/meter/list2?tenpo_cd=0001&yyyy=2014&meter_type_code=1";
				new CallAPIAsyncTask(this, JSONUtils.API_035_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(MessageFormat.format("System error: {0}",
						e.getMessage()));
				builder.setCancelable(false);
				builder.setPositiveButton("Try again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								S303Activity.this.callAPI035();
							}
						});
				builder.setNegativeButton("Exist Application",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								S303Activity.this.finish();
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
							S303Activity.this.finish();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	// Call API 036
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

		data.add(new BasicNameValuePair(JSONUtils.METER_CATEGORY, "3"));

		data.add(new BasicNameValuePair(JSONUtils.METER_CD, String
				.valueOf(mCurrentMeterCode)));

		data.add(new BasicNameValuePair(JSONUtils.METER_NUM, mTvMeterAmount
				.getText().toString()));

		data.add(new BasicNameValuePair(JSONUtils.AMOUNT, mStrNewMeter));

		Date egyDate = mMeterReadingInfo.getElectricityListMeterThisYear()
				.get(0).getEgy_date();
		mEgyDateHolder = new SimpleDateFormat("yyyyMMdd",
				java.util.Locale.getDefault()).format(egyDate);
		data.add(new BasicNameValuePair(JSONUtils.EGY_DATE, mEgyDateHolder));

		Log.i("Egy Date", mEgyDateHolder);
		data.add(new BasicNameValuePair(JSONUtils.PART_CODE,
				this.mPartCodeInputValue));

		// Show project dialog
		this.showProcessDialog(this, null, null);

		new PostAPIAsyncTask(S303Activity.this, data, JSONUtils.API_036_PATH)
				.execute(url.toString());

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

	private void DatePullDownSelectionClick() {
		mYearList = new String[10];
		String[] YearListDisplay = new String[10];
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 0; i < 10; i++) {
			mYearList[i] = String.valueOf(year - i);
			YearListDisplay[i] = String.format(
					"%s" + getResources().getString(R.string.year),
					mYearList[i]);
		}

		Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(YearListDisplay,
				this.currentLocationIndexSelect,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int position) {
						if (arg0 != null) {
							arg0.dismiss();
						}
						currentLocationIndexSelect = position;
						String strYearPullDown = String.format("%s"
								+ getResources().getString(R.string.year),
								mYearList[position]);
						mDatePullDown.setText(strYearPullDown);
						mSelectedYear = mYearList[position];
						S303Activity.this.callAPI035();
					}

				});
		super.alert = builder.show();

	}
}
