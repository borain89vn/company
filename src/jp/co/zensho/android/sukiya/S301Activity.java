package jp.co.zensho.android.sukiya;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.co.zensho.android.sukiya.adapter.S301CustomListItemAdapter;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import jp.co.zensho.android.sukiya.bean.ProductExpirationInfo;
import jp.co.zensho.android.sukiya.bean.TagInfo;
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
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class S301Activity extends S00Activity implements CallAPIListener,
		OnClickListener {

	// main
	private ListView mListview = null;
	private ProductExpirationInfo mProductList;
	private int mPositionChoosed = 0;
	private S301CustomListItemAdapter adapter;
	private Button mToggleDisplayAll;
	private Button mToggleDisplayNotNull;
	private Button mBtnBack;
	private Button mBtnConfirm;
	private boolean isDisplayAllData = true;
	private TextView mDeliveryDate;
	// Zenshou ntdat 20140407 add ++
	private TextView tvDate1, tvDate2, tvDate3, tvDate4;
	private TextView tvDate1Sum, tvDate2Sum, tvDate3Sum, tvDate4Sum;
	// Zenshou ntdat 20140407 add --

	// Popup
	private RelativeLayout mLayoutPopupNumber;
	private Button mBtnPopupNumberClose;
	private Button mBtnPopupNumberComplete;
	private TextView mTxtPopupNumberProductAmount;
	private Button mBtnPopupNumberPlus10;
	private Button mBtnPopupNumberPlus1;
	private Button mBtnPopupNumberPlus01;
	private Button mBtnPopupNumberMinus10;
	private Button mBtnPopupNumberMinus1;
	private Button mBtnPopupNumberMinus01;
	private TextView mTvPopupNumberProductName;
	private ImageView mProductPopupImage;
	private TextView mTvPopupUnit;
	private TextView mTvPopupTitle;
	private Button mBtnHelp;
	private TextView mHelp;
	private LinearLayout mHelpLayout;

	// List header
	private TextView mTvNohinDate1;
	private TextView mTvNohinDate2;
	private TextView mTvNohinDate3;
	private TextView mTvZaikoSu;
	private TextView mTvPerStock;

	// Newest
	private SukiyaNumberKeyboard customKeyboard;

	// Part code
	private RelativeLayout mPartCodePopup;
	private EditText mEditTextPartCode;
	private Button mBtnPartCodeCancellation;
	private Button mBtnPartCodeRegist;
	private String mPartCodeInputValue;
	// Zenshou ntdat 20140416 add ++
	private SukiyaNumberKeyboardPartCode customKeyboardPartCode;
	// Zenshou ntdat 20140416 add --

	// Regist part code dialog listener
	protected DialogInterface.OnClickListener registPartCodeDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}

			S301Activity.this.callAPI032();
		}
	};
	// Reload call API 031
	private DialogInterface.OnClickListener retryAPI031DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S301Activity.this.callAPI031();
		}
	};
	// Reload call API 032
	private DialogInterface.OnClickListener retryAPI032DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S301Activity.this.callAPI032();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.s301);
		generateViews();

		// main content
		mListview = (ListView) findViewById(R.id.s301_list_view);
		mToggleDisplayAll = (Button) findViewById(R.id.s301_btn_display_all);
		mToggleDisplayNotNull = (Button) findViewById(R.id.s301_btn_display_not_null);
		mBtnBack = (Button) findViewById(R.id.s301_btn_back);
		mBtnConfirm = (Button) findViewById(R.id.S301_btn_confirm);
		mDeliveryDate = (TextView) findViewById(R.id.page_date_time);
		mBtnHelp = (Button) findViewById(R.id.btn_q);
		// List header
		mTvNohinDate1 = (TextView) findViewById(R.id.s301_tv_nohin_date1);
		mTvNohinDate2 = (TextView) findViewById(R.id.s301_tv_nohin_date2);
		mTvNohinDate3 = (TextView) findViewById(R.id.s301_tv_nohin_date3);
		mTvZaikoSu = (TextView) findViewById(R.id.s301_tv_zaiko_su);
		mTvPerStock = (TextView) findViewById(R.id.s301_tv_per_stock);
		// Zenshou ntdat 20140407 add ++
		tvDate1 = (TextView) findViewById(R.id.s301_tv_date1);
		tvDate2 = (TextView) findViewById(R.id.s301_tv_date2);
		tvDate3 = (TextView) findViewById(R.id.s301_tv_date3);
		tvDate4 = (TextView) findViewById(R.id.s301_tv_date4);

		tvDate1Sum = (TextView) findViewById(R.id.s301_tv_date1_sum);
		tvDate2Sum = (TextView) findViewById(R.id.s301_tv_date2_sum);
		tvDate3Sum = (TextView) findViewById(R.id.s301_tv_date3_sum);
		tvDate4Sum = (TextView) findViewById(R.id.s301_tv_date4_sum);

		// Zenshou ntdat 20140407 add --

		// popup content
		mLayoutPopupNumber = (RelativeLayout) findViewById(R.id.s301_input_popup);
		mTvPopupNumberProductName = (TextView) findViewById(R.id.p2xx_quantity_name);
		mBtnPopupNumberComplete = (Button) findViewById(R.id.p2xx_quantity_btn_complete);
		mBtnPopupNumberClose = (Button) findViewById(R.id.p2xx_quantity_btn_close);
		mTxtPopupNumberProductAmount = (TextView) findViewById(R.id.p2xx_quantity_et_amount);
		mBtnPopupNumberPlus10 = (Button) findViewById(R.id.p2xx_quantity_btn_plus10);
		mBtnPopupNumberPlus1 = (Button) findViewById(R.id.p2xx_quantity_btn_plus1);
		mBtnPopupNumberPlus01 = (Button) findViewById(R.id.p2xx_quantity_btn_plus01);
		mBtnPopupNumberMinus10 = (Button) findViewById(R.id.p2xx_quantity_btn_minus10);
		mBtnPopupNumberMinus1 = (Button) findViewById(R.id.p2xx_quantity_btn_minus1);
		mBtnPopupNumberMinus01 = (Button) findViewById(R.id.p2xx_quantity_btn_minus01);
		mProductPopupImage = (ImageView) findViewById(R.id.p2xx_quantity_image);
		mTvPopupUnit = (TextView) findViewById(R.id.p2xx_quantity_tv_unit);
		mTvPopupUnit.setBackground(getResources().getDrawable(
				R.drawable.s301_unit_sample_disabled));
		mTvPopupTitle = (TextView) findViewById(R.id.p2xx_quantity_tv_title);
		mHelp = (TextView) findViewById(R.id.help_content);
		mHelpLayout = (LinearLayout) findViewById(R.id.s301_help);

		// PartCode
		mPartCodePopup = (RelativeLayout) findViewById(R.id.s301_input_partcode);
		mEditTextPartCode = (EditText) findViewById(R.id.p06_txt_input_part_code);
		mBtnPartCodeRegist = (Button) findViewById(R.id.p06_btn_regist_part_code_input);
		mBtnPartCodeCancellation = (Button) findViewById(R.id.p06_btn_cancel_part_code_input);
		// Zenshou ntdat 20140416 add ++
		customKeyboardPartCode = new SukiyaNumberKeyboardPartCode(this,
				R.id.keyboardviewPartcode, R.xml.keyboard_layout);
		customKeyboardPartCode.registerEditText(R.id.p06_txt_input_part_code);
		// Zenshou ntdat 20140416 add --

		// set listener
		mBtnPopupNumberClose.setOnClickListener(this);
		mBtnPopupNumberPlus10.setOnClickListener(this);
		mBtnPopupNumberPlus1.setOnClickListener(this);
		mBtnPopupNumberPlus01.setOnClickListener(this);
		mBtnPopupNumberMinus10.setOnClickListener(this);
		mBtnPopupNumberMinus1.setOnClickListener(this);
		mBtnPopupNumberMinus01.setOnClickListener(this);
		mBtnPopupNumberComplete.setOnClickListener(this);
		mLayoutPopupNumber.setOnClickListener(this);
		mToggleDisplayAll.setOnClickListener(this);
		mToggleDisplayNotNull.setOnClickListener(this);
		mBtnBack.setOnClickListener(this);
		mBtnConfirm.setOnClickListener(this);
		mBtnPartCodeCancellation.setOnClickListener(this);
		mBtnPartCodeRegist.setOnClickListener(this);
		mBtnHelp.setOnClickListener(this);
		mHelpLayout.setOnClickListener(this);

		// Keyboard
		customKeyboard = new SukiyaNumberKeyboard(this, R.id.keyboardview,
				R.xml.keyboard_layout);
		customKeyboard.registerEditText(R.id.p2xx_quantity_et_amount);

		// Call API031
		callAPI031();

	}

	@Override
	protected void onStop() {
		super.removeUpdateCurrentDate();
		super.onStop();
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
	public void callAPIFinish(String tag, JSONObject result, ErrorInfo error) {
		super.dismissProcessDialog();

		if (error == null && result != null) {
			try {
				if (!JSONUtils.hasError(result)) {
					if (JSONUtils.API_031_PATH.equals(tag)) {
						// Zenshou ntdat 20140407 delete ++
						ProductExpirationInfo productScheduleDeliveryList = null;
						productScheduleDeliveryList = JSONUtils
								.parseProductScheduleDelivery(result);

						if (productScheduleDeliveryList == null
								|| productScheduleDeliveryList.getDNohinZaiko() == null
								|| productScheduleDeliveryList.getDNohinZaiko()
										.size() < 1) {
							mBtnConfirm.setEnabled(false);
						}

						if (productScheduleDeliveryList != null) {

							this.mProductList = productScheduleDeliveryList;
							// this.mProductList.getDNohinZaiko().get(4)
							// .setNohin_su1(null);

						}
						// Zenshou ntdat 20140407 delete --
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
			if (JSONUtils.API_031_PATH.equals(tag)) {
				retryListener = retryAPI031DialogListener;
			} else if (JSONUtils.API_032_PATH.equals(tag)) {
				retryListener = retryAPI032DialogListener;
			}

			// Show error dialog
			super.showDialog(this, message, R.string.try_again, retryListener,
					R.string.cancel, dismissDialogListener);
		} else {
			if (JSONUtils.API_031_PATH.equals(tag)) {
				// Zenshou ntdat 20140407 mod ++
				updateUI();
				// Zenshou ntdat 20140407 mod --
			} else if (JSONUtils.API_032_PATH.equals(tag)) {
				Log.i("Json post", result.toString());
				S301Activity.this.finish();
				overridePendingTransition(R.animator.slide_in_right,
						R.animator.slide_out_left);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mHelpLayout)) {
			super.hidePopup(mHelpLayout);
		} else if (v.equals(mBtnHelp)) {
			super.getHelp(S301Activity.this, StringUtils.S301_HELP_CD);
		} else if (mBtnBack.equals(v)) {
			finish();
			overridePendingTransition(R.animator.slide_in_left,
					R.animator.slide_out_right);
		} else if (mBtnConfirm.equals(v)) {
			mEditTextPartCode.setText(StringUtils.EMPTY);
			super.showPopup(mPartCodePopup);
		} else if (mToggleDisplayAll.equals(v)) {
			if (!isDisplayAllData) {
				mToggleDisplayNotNull.setBackgroundColor(getResources()
						.getColor(R.color.green));
				mToggleDisplayNotNull.setTextColor(getResources().getColor(
						R.color.white));
				mToggleDisplayAll.setTextColor(getResources().getColor(
						R.color.green));
				mToggleDisplayAll
						.setBackgroundResource(R.drawable.s301_toggle_btn);
				isDisplayAllData = true;
				mPositionChoosed = 0;
				updateUI();
			}
		} else if (mToggleDisplayNotNull.equals(v)) {
			if (isDisplayAllData) {
				mToggleDisplayAll.setBackgroundColor(getResources().getColor(
						R.color.green));
				mToggleDisplayAll.setTextColor(getResources().getColor(
						R.color.white));
				mToggleDisplayNotNull.setTextColor(getResources().getColor(
						R.color.green));
				mToggleDisplayNotNull
						.setBackgroundResource(R.drawable.s301_toggle_btn);
				isDisplayAllData = false;
				mPositionChoosed = 0;
				updateUI();
			}
		} else if (mBtnPopupNumberClose.equals(v)) {
			// Close popup number
			super.hidePopup(mLayoutPopupNumber);
		} else if (mBtnPopupNumberComplete.equals(v)) {
			// Confirm
			String mAmount = mTxtPopupNumberProductAmount.getText().toString();
			if (!StringUtils.EMPTY.equals(mAmount)) {
				mProductList.getDNohinZaiko().get(mPositionChoosed)
						.setNohin_su1(Double.parseDouble(mAmount));
			} else {
				mProductList.getDNohinZaiko().get(mPositionChoosed)
						.setNohin_su1(0.0);
			}
			updateUI();
			super.hidePopup(mLayoutPopupNumber);
		} else if (mBtnPartCodeCancellation.equals(v)) {
			super.hidePopup(mPartCodePopup);
		} else if (mBtnPartCodeRegist.equals(v)) {
			this.registPartCodeInputAction();
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
		}

	}

	public void onButton1Click(View v) throws Exception {
		if (v != null) {
			Object objTag = v.getTag();
			if (objTag != null && objTag instanceof TagInfo) {
				TagInfo tag = (TagInfo) objTag;
				if (TagInfo.NUMBER_TAG == tag.getType()) {
					int pos = tag.getIndex1();
					ProductExpirationInfo product = null;

					product = mProductList.getDNohinZaiko().get(pos);
					mPositionChoosed = pos;
					mTvPopupTitle.setText(getResources().getString(
							R.string.p02xx_title));
					// mProductPopupImage
					// .setImageResource(R.drawable.input_item1_ocha);

					SukiyaUtils
							.setImage(S301Activity.this, mProductPopupImage,
									product.getCode() + ".jpg",
									R.drawable.no_image_191);

					mTxtPopupNumberProductAmount.setText(product.getNohin_su1()
							.toString());
					mTvPopupNumberProductName.setText(product.getHinName());
					mTvPopupUnit.setText(product.getTaniName());
					// Log.i("product.getUnit()", product.getUnit());
				}
			}
		}

		super.showPopup(mLayoutPopupNumber);

	}

	private void callAPI031() {
		super.showProcessDialog(this, null, null);
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_031_PATH, params);

				// Dummy url
				// url =
				// "http://192.168.0.191/zensho_dummy/api/scheduled_delivery/list?tenpo_cd=0001";

				new CallAPIAsyncTask(this, JSONUtils.API_031_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(MessageFormat.format("System error: {0}",
						e.getMessage()));
				builder.setCancelable(false);
				builder.setPositiveButton("Try again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								S301Activity.this.callAPI031();
							}
						});
				builder.setNegativeButton("Exist Application",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								S301Activity.this.finish();
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
							S301Activity.this.finish();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	private void callAPI032() {
		if (StringUtils.isEmpty(this.mPartCodeInputValue)) {
			return;
		}
		ProductExpirationInfo productInfoList = mProductList;
		if (productInfoList == null
				|| productInfoList.getDNohinZaiko().size() <= 0) {
			return;
		}
		// make url
		StringBuilder url = new StringBuilder();
		// url.append(JSONUtils.API_HOST_PATH);
		// url.append(JSONUtils.API_032_PATH);
		url.append("http://192.168.0.191/zensho_dummy/api/scheduled_delivery/update"); // Dummy

		// make parameter
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair(JSONUtils.TENPO_CODE, super.app
				.getShopCode()));

		ProductExpirationInfo productInfo = null;
		StringBuilder paramKey = null;
		for (int i = 0; i < productInfoList.getDNohinZaiko().size(); i++) {
			productInfo = productInfoList.getDNohinZaiko().get(i);
			if (productInfo.getNohin_su1() == null) {
				continue;
			}

			// code
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_032_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.CODE).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), String
					.valueOf(productInfo.getCode())));

			// amount
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_032_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.AMOUNT).append("]");
			data.add(new BasicNameValuePair(paramKey.toString(), productInfo
					.getNohin_su1().toString()));

			// Check
			paramKey = new StringBuilder();
			paramKey.append(JSONUtils.API_032_PARAMS_KEY);
			paramKey.append("[").append(i).append("]");
			paramKey.append("[").append(JSONUtils.CHECK).append("]");
			int check;
			if (productInfo.isCheckToggleButton() == true) {
				check = 1;
			} else {
				check = 0;
			}
			data.add(new BasicNameValuePair(paramKey.toString(), String
					.valueOf(check)));

			// // part_cd
			// paramKey = new StringBuilder();
			// paramKey.append(JSONUtils.API_032_PARAMS_KEY);
			// paramKey.append("[").append(i).append("]");
			// paramKey.append("[").append(JSONUtils.PART_CODE).append("]");
			// data.add(new BasicNameValuePair(paramKey.toString(),
			// this.mPartCodeInputValue));

		}
		// Show project dialog
		super.showProcessDialog(this, null, null);

		new PostAPIAsyncTask(S301Activity.this, data, JSONUtils.API_032_PATH)
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

	// Change the value of amount in the input quantity popup layout
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
		if (r.compareTo(new BigDecimal(1000)) >= 0) {
			r = new BigDecimal(currentVal);
		}
		// if (r.compareTo(MAX_AMOUNT_VALUE) >= 0) {
		// currentValb = r;
		// }

		mTxtPopupNumberProductAmount.setText(r.toString());
	}

	// Update User interface
	private void updateUI() {

		// Update Header
		try {
			mTvNohinDate1.setText(mProductList.getNohin_day1() + "("
					+ getWeekday(mProductList.getNohin_day1()) + ")"
					+ getResources().getString(R.string.nohin_date));
		} catch (Exception e) {
			mTvNohinDate1
					.setText(getResources().getString(R.string.nohin_date));
		}
		try {
			mTvNohinDate2.setText(mProductList.getNohin_day2() + "("
					+ getWeekday(mProductList.getNohin_day2()) + ")"
					+ getResources().getString(R.string.nohin_date));
		} catch (Exception e) {
			mTvNohinDate2
					.setText(getResources().getString(R.string.nohin_date));
		}
		try {
			mTvNohinDate3.setText(mProductList.getNohin_day3() + "("
					+ getWeekday(mProductList.getNohin_day3()) + ")"
					+ getResources().getString(R.string.nohin_date));
		} catch (Exception e) {
			mTvNohinDate3
					.setText(getResources().getString(R.string.nohin_date));
		}
		try {
			mTvZaikoSu.setText(mProductList.getNohin_day3() + "("
					+ getWeekday(mProductList.getNohin_day3()) + ")"
					+ getResources().getString(R.string.zaiko_su));

		} catch (Exception e) {
			mTvZaikoSu.setText(getResources().getString(R.string.zaiko_su));
		}
		mTvPerStock.setText(getResources().getString(R.string.per_stock));

		// Update Estimated delivery quantity

		tvDate1.setText(getResources().getString(R.string.double_hyphen));
		tvDate1Sum.setText(getResources().getString(R.string.double_hyphen));
		tvDate2.setText(getResources().getString(R.string.double_hyphen));
		tvDate2Sum.setText(getResources().getString(R.string.double_hyphen));
		tvDate3.setText(getResources().getString(R.string.double_hyphen));
		tvDate3Sum.setText(getResources().getString(R.string.double_hyphen));
		tvDate4.setText(getResources().getString(R.string.double_hyphen));
		tvDate4Sum.setText(getResources().getString(R.string.double_hyphen));

		if (mProductList != null) {
			if (mProductList.getDUriYsk() != null
					&& mProductList.getDUriYsk().size() > 0) {

				int i = mProductList.getDUriYsk().size();
				int quantity = 0;

				switch (i) {
				case 4:
					tvDate4.setText(mProductList.getDUriYsk().get(3).getDate()
							+ "("
							+ getWeekday(mProductList.getDUriYsk().get(3)
									.getDate()) + ")");
					quantity = mProductList.getDUriYsk().get(3).getUriYsk();

					if (quantity == 0) {
						tvDate4Sum.setText(getResources().getString(
								R.string.double_hyphen));
					} else {

						tvDate4Sum.setText(String.valueOf(quantity));
					}
				case 3:
					tvDate3.setText(mProductList.getDUriYsk().get(2).getDate()
							+ "("
							+ getWeekday(mProductList.getDUriYsk().get(2)
									.getDate()) + ")");
					quantity = mProductList.getDUriYsk().get(2).getUriYsk();

					if (quantity == 0) {
						tvDate3Sum.setText(getResources().getString(
								R.string.double_hyphen));
					} else {

						tvDate3Sum.setText(String.valueOf(quantity));
					}
				case 2:
					tvDate2.setText(mProductList.getDUriYsk().get(1).getDate()
							+ "("
							+ getWeekday(mProductList.getDUriYsk().get(1)
									.getDate()) + ")");
					quantity = mProductList.getDUriYsk().get(1).getUriYsk();
					if (quantity == 0) {
						tvDate2Sum.setText(getResources().getString(
								R.string.double_hyphen));
					} else {

						tvDate2Sum.setText(String.valueOf(quantity));
					}
				case 1:
					tvDate1.setText(mProductList.getDUriYsk().get(0).getDate()
							+ "("
							+ getWeekday(mProductList.getDUriYsk().get(0)
									.getDate()) + ")");
					quantity = mProductList.getDUriYsk().get(0).getUriYsk();
					if (quantity == 0) {
						tvDate1Sum.setText(getResources().getString(
								R.string.double_hyphen));
					} else {

						tvDate1Sum.setText(String.valueOf(quantity));
					}
				}
			}
		}
		// Update Estimated delivery time
		mDeliveryDate.setText(mProductList.getNmlTime());

		// Update list
		if (mProductList != null && mProductList.getDNohinZaiko() != null
				&& mProductList.getDNohinZaiko().size() > 0) {

			adapter = new S301CustomListItemAdapter(this,
					mProductList.getDNohinZaiko(), isDisplayAllData);
			mListview.setAdapter(adapter);
			mListview.setSelection(mPositionChoosed);

		}
	}

	private String getWeekday(String strDate) {
		String dateStr = strDate;
		int year = Calendar.getInstance().get(Calendar.YEAR);

		String fullDateStr = dateStr + "/" + year;
		Log.e("fullDateStr", fullDateStr);
		Date date = null;
		String dayOfWeek = "";
		try {
			date = new SimpleDateFormat("MM/dd/yyyy", Locale.JAPAN)
					.parse(fullDateStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK,
					Calendar.SHORT, Locale.JAPAN);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dayOfWeek;
	}

	// // Get week day from date
	// private String getWeekday(String strDate) {
	// Calendar currentCal = DateUtils.today();
	// Calendar operationCal = null;
	// String weekday = "";
	// String[] arrayDate = strDate.split("/");
	// String strMonth = arrayDate[0];
	// String strDay = arrayDate[1];
	// operationCal = new GregorianCalendar(currentCal.get(Calendar.YEAR),
	// Integer.parseInt(strMonth), Integer.parseInt(strDay));
	// weekday = operationCal.getDisplayName(Calendar.DAY_OF_WEEK,
	// Calendar.SHORT, Locale.JAPAN);
	//
	// return weekday;
	// }
}
