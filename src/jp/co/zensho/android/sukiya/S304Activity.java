package jp.co.zensho.android.sukiya;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.co.zensho.android.sukiya.bean.API14_ProductCatSel;
import jp.co.zensho.android.sukiya.bean.API15_ProductNameSel;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import jp.co.zensho.android.sukiya.bean.InventoryAnalysisInfo;
import jp.co.zensho.android.sukiya.common.JSONUtils;
import jp.co.zensho.android.sukiya.common.StringUtils;
import jp.co.zensho.android.sukiya.service.CallAPIAsyncTask;
import jp.co.zensho.android.sukiya.service.CallAPIListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class S304Activity extends S00Activity implements OnClickListener,
		CallAPIListener, OnGestureListener, OnTouchListener,
		OnScrollChangedListener {

	private static final String LOG = "S304Activity";
	// Main
	private VScrollView mScrollY;
	private HScrollView mScrollYChild;
	private VScrollView mScrollLeft;
	private HScrollView mScrollRightHeader;
	private LinearLayout mLinearLayoutProgramTable;
	private float mX, mY;
	private GestureDetector mGestureDetector;
	private LinearLayout mInfoLayout; // For disable onTouch
	private LinearLayout mHeader; // For disable onTouch
	private LinearLayout mDividerInfoAndContent; // For disable onTouch
	private LinearLayout mS304LeftSide; // For disable onTouch
	private LinearLayout layoutLeftTable;
	private LinearLayout layoutRightHeaderColumn;
	// Time column height
	private int mWith = 90;
	private int mHeight = 60;

	// constant
	private static final int MAIN_GRID_ROW = 14;
	private static final int MAIN_GRID_COLLUMN = 14;
	private static final int LEFT_TABLE_COLLUMN = 6;

	// Hold date list string get from API42 after format with Japan Calendar
	String[] dateListJPFormat;
	String[][] leftList;
	String[][] mainGrid;

	private Button mBtnBack;
	private TextView mCategorySelection;
	private TextView mProductSelection;

	private List<API14_ProductCatSel> mListCategory;
	private List<API15_ProductNameSel> mListProduct;
	private int mSelectedCategoryPos = -1;
	private int mSelectedProductPos = -1;
	private String mSelectedDivCode = "";
	private String mSelectedHinCode = "";
	private List<InventoryAnalysisInfo> mListInventoryAnalysis;

	// help
	private Button mBtnHelp;
	private TextView mHelp;
	private LinearLayout mHelpLayout;

	// Reload call API 042
	private DialogInterface.OnClickListener retryAPI042DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S304Activity.this.callAPI42();
		}
	};
	// Reload call API 042
	private DialogInterface.OnClickListener retryAPI014DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S304Activity.this.callAPI014();
		}
	};

	// Reload call API 042
	private DialogInterface.OnClickListener retryAPI015DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S304Activity.this.callAPI015();
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.s304);
		generateViews();
		// Main
		// Instancized datelist
		dateListJPFormat = new String[MAIN_GRID_COLLUMN];
		mGestureDetector = new GestureDetector(this.getBaseContext(), this);
		// scroll
		mScrollY = (VScrollView) findViewById(R.id.v_scroll);
		mScrollYChild = (HScrollView) findViewById(R.id.h_scroll);
		mScrollLeft = (VScrollView) findViewById(R.id.s304_v_scroll_left);
		mScrollRightHeader = (HScrollView) findViewById(R.id.h_scroll_right_header);
		mScrollY.setOnScrollChangedListener(this);
		mScrollYChild.setOnScrollChangedListener(this);
		mLinearLayoutProgramTable = (LinearLayout) findViewById(R.id.s304_v_scroll_right);
		mBtnBack = (Button) findViewById(R.id.s304_btn_back);
		mCategorySelection = (TextView) findViewById(R.id.s304_option_1);
		mProductSelection = (TextView) findViewById(R.id.s304_option_2);
		mInfoLayout = (LinearLayout) findViewById(R.id.info_layout);
		mHeader = (LinearLayout) findViewById(R.id.header);
		mDividerInfoAndContent = (LinearLayout) findViewById(R.id.divider_info_content);
		mS304LeftSide = (LinearLayout) findViewById(R.id.s304_left_side);

		// help
		mBtnHelp = (Button) findViewById(R.id.btn_q);
		mHelp = (TextView) findViewById(R.id.help_content);
		mHelpLayout = (LinearLayout) findViewById(R.id.s304_help);
		// help
		mBtnHelp.setOnClickListener(this);
		mHelpLayout.setOnClickListener(this);

		// Set onClick listner
		mBtnBack.setOnClickListener(this);
		mCategorySelection.setOnClickListener(this);
		mProductSelection.setOnClickListener(this);
		mProductSelection.setEnabled(false);

		// Set onTouchListener --> disable
		mS304LeftSide.setOnTouchListener(this);
		mScrollLeft.setOnTouchListener(this);
		mScrollRightHeader.setOnTouchListener(this);
		mInfoLayout.setOnTouchListener(this);
		mHeader.setOnTouchListener(this);
		mDividerInfoAndContent.setOnTouchListener(this);
		mScrollY.setOnTouchListener(this);
		mScrollYChild.setOnTouchListener(this);
		mLinearLayoutProgramTable.setOnTouchListener(this);

		layoutLeftTable = (LinearLayout) findViewById(R.id.v_layout_scroll_left);
		layoutRightHeaderColumn = (LinearLayout) findViewById(R.id.h_layout_right_header_column);

		// CallAPI14
		callAPI014();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {

		if (mBtnBack.equals(v)) {
			finish();
			overridePendingTransition(R.animator.slide_in_left,
					R.animator.slide_out_right);
		} else if (v.equals(mHelpLayout)) {
			super.hidePopup(mHelpLayout);
		} else if (v.equals(mBtnHelp)) {
			super.getHelp(S304Activity.this, StringUtils.S304_HELP_CD);
		} else if (mCategorySelection.equals(v)) {
			try {
				// Convert List<> to String[]
				String[] arrayListCatSel = null;
				if (mListCategory != null && mListCategory.size() > 0) {
					arrayListCatSel = new String[mListCategory.size()];

					API14_ProductCatSel objAPI14 = null;
					for (int i = 0; i < mListCategory.size(); i++) {
						objAPI14 = mListCategory.get(i);
						if (objAPI14 != null) {
							arrayListCatSel[i] = objAPI14.getDivName();
						}
					}

					// Show dialog single choice list of productCat_sel
					// (Category)
					Builder builder = new AlertDialog.Builder(S304Activity.this);
					builder.setSingleChoiceItems(arrayListCatSel,
							mSelectedCategoryPos,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int position) {
									if (arg0 != null) {
										arg0.dismiss();
									}

									mSelectedCategoryPos = position;
									mCategorySelection.setText(mListCategory
											.get(position).getDivName());
									mSelectedDivCode = mListCategory.get(
											position).getDivCD();
									mProductSelection.setText(getResources()
											.getString(R.string.product_name));
									mSelectedProductPos = -1;

									clearData();
									callAPI015();

								}

							});
					super.alert = builder.show();
				} else {
					// show error message "cannot get list product category"
				}
			} catch (Exception e) {
				// Error handling
			}
		} else if (mProductSelection.equals(v)) {
			try {
				// Convert List<> to String[]
				String[] arrayListNameSel = null;
				if (mListProduct != null && mListProduct.size() > 0) {
					arrayListNameSel = new String[mListProduct.size()];

					API15_ProductNameSel objAPI15 = null;
					for (int i = 0; i < mListProduct.size(); i++) {
						objAPI15 = mListProduct.get(i);
						if (objAPI15 != null) {
							arrayListNameSel[i] = objAPI15.getHinName();
						}
					}

					// Show dialog single choice list of productName_sel
					// (Name)
					Builder builder = new AlertDialog.Builder(S304Activity.this);
					builder.setSingleChoiceItems(arrayListNameSel,
							mSelectedProductPos,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int position) {
									if (arg0 != null) {
										arg0.dismiss();
									}

									mSelectedProductPos = position;
									mProductSelection.setText(mListProduct.get(
											position).getHinName());
									mSelectedHinCode = mListProduct.get(
											position).getHinCD();

									// CallAPI042
									clearData();
									callAPI42();
								}

							});
					super.alert = builder.show();
				} else {
					// show error message "cannot get list product category"
				}
			} catch (Exception e) {
				// error handling
			}

		}

	}

	@Override
	public void callAPIFinish(String tag, JSONObject result, ErrorInfo error) {
		super.dismissProcessDialog();

		if (error == null && result != null) {
			try {
				if (!JSONUtils.hasError(result)) {

					// API 14 is called
					if (JSONUtils.API_014_PATH.equals(tag)) {
						mListCategory = JSONUtils.parseListItemCategory(result);
					} else
					// API 15 is called
					if (JSONUtils.API_015_PATH.equals(tag)) {
						mListProduct = JSONUtils.parseListItem(result);
					} else
					// API 42 is called
					if (JSONUtils.API_042_PATH.equals(tag)) {
						Log.e("Test json in activity 304",
								String.valueOf(result));
						mListInventoryAnalysis = JSONUtils
								.parseListInventoryAnalysis(result);
						if (mListInventoryAnalysis != null) {
							Log.e(LOG, mListInventoryAnalysis.size() + "");
						} else {
							Log.i(LOG, "do nothing here");
							// do nothing
						}
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
			// Error in API 014
			if (JSONUtils.API_014_PATH.equals(tag)) {
				retryListener = retryAPI014DialogListener;
			} else if (JSONUtils.API_015_PATH.equals(tag)) {
				retryListener = retryAPI015DialogListener;
			} else
			// Error in API 042
			if (JSONUtils.API_042_PATH.equals(tag)) {
				retryListener = retryAPI042DialogListener;
			}
			// Show error dialog
			super.showDialog(this, message, R.string.try_again, retryListener,
					R.string.cancel, dismissDialogListener);

		} else {
			Bundle extras;
			String hincd;
			String hinnm = null;

			extras = getIntent().getExtras();
			if (extras == null) {
				hincd = null;
				hinnm = null;
			} else {
				hincd = extras.getString(JSONUtils.HIN_CODE);
				hinnm = extras.getString(JSONUtils.HIN_NAME);
			}
			if (JSONUtils.API_014_PATH.equals(tag)) {
				// S40x to here

				if (hincd != null && !StringUtils.EMPTY.equals(hincd)) {
					mSelectedHinCode = hincd;
					// mSelectedHinCode = "1102";
					// mProductSelection.setText(hinnm);
					// mProductSelection.setEnabled(false);
					callAPI42();
				}
			} else if (JSONUtils.API_015_PATH.equals(tag)) {
				mProductSelection.setEnabled(true);
			} else if (JSONUtils.API_042_PATH.equals(tag)) {
				updateUI();
				if (hincd != null && !StringUtils.EMPTY.equals(hincd)) {
					mSelectedHinCode = hincd;
					mSelectedHinCode = "1102";
					Log.i("Hin nm", hinnm);
					mProductSelection.setTextColor(getResources().getColor(
							R.color.grey2));

					mProductSelection.setText(hinnm);

				}
			}
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent me) {
		super.dispatchTouchEvent(me);
		if (inScrollArea(me)) {
			return mGestureDetector.onTouchEvent(me);
		} else {
			return true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// super.onTouchEvent(event);
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		mX = e.getX();
		mY = e.getY();
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int x = (int) (velocityX * -1);
		int y = (int) (velocityY * -1);

		mScrollY.fling(y);
		mScrollYChild.fling(x);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		int x = (int) (mX - e2.getX());
		int y = (int) (mY - e2.getY());

		mScrollY.scrollBy(0, y);
		mScrollYChild.scrollBy(x, 0);

		mX = e2.getX();
		mY = e2.getY();
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return true;
	}

	@Override
	public void onHorizontalScrollChanged(View view, int l, int t, int oldl,
			int oldt) {
		mScrollRightHeader.scrollTo(l, t);
	}

	@Override
	public void onVerticalScrollChanged(View view, int l, int t, int oldl,
			int oldt) {
		mScrollLeft.scrollTo(l, t);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (v.equals(mScrollYChild) || v.equals(mLinearLayoutProgramTable)
				|| v.equals(mScrollY)) {
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				int[] loc = new int[2];
				v.getLocationOnScreen(loc);
				mX = loc[0] + event.getX();
				mY = loc[1] + event.getY();
			}

			return false;
		} else {
			return true;
		}
	}

	// On the internet

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
	private void callAPI015() {
		Log.d(LOG, "callAPI015 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
			params.put(JSONUtils.API_015_PARAMS_KEY, mSelectedDivCode);

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

	// Call API 042
	private void callAPI42() {
		super.showProcessDialog(this, null, null);
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			Map<String, String> params = new HashMap<String, String>();

			params.put(JSONUtils.HIN_CODE, mSelectedHinCode);

			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());
			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_042_PATH, params);
				Log.i("Ngo test API042 selected url", url);
				// Dummy url
				// url =
				// "http://192.168.0.191/zensho.rapinics.jp/api/kpi/zaiko_analyze?tenpo_cd=0001&hin_cd=1";
				new CallAPIAsyncTask(this, JSONUtils.API_042_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(MessageFormat.format("System error: {0}",
						e.getMessage()));
				builder.setCancelable(false);
				builder.setPositiveButton("Try again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								S304Activity.this.callAPI42();
							}
						});
				builder.setNegativeButton("Exist Application",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								S304Activity.this.finish();
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
							S304Activity.this.finish();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	// Update User interface
	private void updateUI() {

		determimeLeftListAndDateList();
		DetermineMainGrid();
		LinearLayout addrow1 = new LinearLayout(getApplicationContext());
		addrow1.setOrientation(LinearLayout.VERTICAL);

		// left area
		for (int i = 0; i < MAIN_GRID_ROW; i++) {
			LinearLayout addView = new LinearLayout(getApplicationContext());
			addView.setOrientation(LinearLayout.HORIZONTAL);
			LayoutParams LLParams = new LayoutParams(mWith * LEFT_TABLE_COLLUMN
					+ 10, mHeight);
			addView.setLayoutParams(LLParams);
			for (int j = 0; j < LEFT_TABLE_COLLUMN; j++) {

				TextView tv_sesson = new TextView(getApplicationContext());
				tv_sesson.setMinHeight(mHeight);
				tv_sesson.setMinWidth(mWith + 2);

				tv_sesson.setTextSize(18);
				tv_sesson.setGravity(Gravity.CENTER);
				if (j == 0) {
					tv_sesson.setTextColor(getResources().getColor(
							R.color.black));
					tv_sesson.setBackground(getResources().getDrawable(
							R.drawable.s304_header_color));
					tv_sesson.setText(leftList[i][j]);
					tv_sesson.setTypeface(tv_sesson.getTypeface(),
							Typeface.BOLD);
				} else {
					tv_sesson.setText(leftList[i][j]);
					// Loss != 0
					if (j == 4 && leftList[i][j] != null
							&& !StringUtils.EMPTY.equals(leftList[i][j])
							&& Double.parseDouble(leftList[i][j]) > 0.0001) {
						tv_sesson.setTextColor(getResources().getColor(
								R.color.white));
						tv_sesson.setBackground(getResources().getDrawable(
								R.drawable.s304_item_color_orange));
					} else {
						tv_sesson.setTextColor(getResources().getColor(
								R.color.black));
						tv_sesson.setBackground(getResources().getDrawable(
								R.drawable.s304_item_color));
					}
				}
				// tv_sesson.setTextColor(Color.BLACK);
				addView.addView(tv_sesson);
			}
			addrow1.addView(addView);
		}
		// layoutLeftTable.removeAllViews();
		layoutLeftTable.addView(addrow1);

		// Right Header

		// layoutRightHeaderColumn.removeAllViews();
		for (int i = 0; i < dateListJPFormat.length; i++) {

			TextView tv_placename3 = new TextView(getApplicationContext());
			tv_placename3.setText(dateListJPFormat[i]);
			tv_placename3.setGravity(Gravity.CENTER_VERTICAL
					| Gravity.CENTER_HORIZONTAL);
			tv_placename3.setTextColor(getResources().getColor(R.color.black));

			tv_placename3.setGravity(Gravity.CENTER);
			tv_placename3.setTextSize(18);
			tv_placename3.setMinWidth(mWith);
			tv_placename3.setMinHeight(mHeight);

			tv_placename3.setTypeface(tv_placename3.getTypeface(),
					Typeface.BOLD);
			tv_placename3.setBackground(getResources().getDrawable(
					R.drawable.s304_header_color));
			LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);
			LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			layout.setLayoutParams(lparams);

			layout.addView(tv_placename3);

			layoutRightHeaderColumn.addView(layout);
		}

		// Main grid
		LinearLayout addrow = new LinearLayout(getApplicationContext());
		addrow.setOrientation(LinearLayout.VERTICAL);
		Log.e("mainGrid.length", mainGrid.length + "");
		for (int i = 0; i < MAIN_GRID_ROW; i++) {
			LinearLayout addView = new LinearLayout(getApplicationContext());
			addView.setOrientation(LinearLayout.HORIZONTAL);
			LayoutParams LLParams = new LayoutParams(mWith * MAIN_GRID_COLLUMN,
					mHeight);
			addView.setLayoutParams(LLParams);
			for (int j = 0; j < MAIN_GRID_COLLUMN; j++) {

				TextView tv_sesson = new TextView(getApplicationContext());
				tv_sesson.setMinHeight(mHeight);
				tv_sesson.setMinWidth(mWith);
				tv_sesson.setText(mainGrid[i][j]);
				tv_sesson.setTextSize(18);
				tv_sesson.setGravity(Gravity.CENTER);

				if (leftList != null && leftList[i][4] != null
						&& !StringUtils.EMPTY.equals(leftList[i][4])
						&& Double.parseDouble(leftList[i][4]) > 0.0001
						&& mainGrid[i][j] != null) {
					// If lost != 0

					tv_sesson.setTextColor(getResources().getColor(
							R.color.white));
					tv_sesson.setBackground(getResources().getDrawable(
							R.drawable.s304_item_color_orange));
				} else {

					tv_sesson.setTextColor(getResources().getColor(
							R.color.black));
					tv_sesson.setBackground(getResources().getDrawable(
							R.drawable.s304_item_color));
				}
				// tv_sesson.setTextColor(Color.BLACK);
				addView.addView(tv_sesson);
			}
			addrow.addView(addView);
		}

		// mLinearLayoutProgramTable.removeAllViews();
		mLinearLayoutProgramTable.addView(addrow);
		mScrollY.fullScroll(View.FOCUS_UP);
		mScrollYChild.fullScroll(View.FOCUS_LEFT);

	}

	private void DetermineMainGrid() {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(1);
		df.setMinimumFractionDigits(0);
		String[][] array = new String[MAIN_GRID_ROW][MAIN_GRID_COLLUMN];
		int rows = array.length;
		int cols = array[0].length;
		Log.e("Number of rows", "Number of rows: " + rows);
		Log.e("Number of columns", "Number of cols: " + cols);

		List<InventoryAnalysisInfo> inventAnaList = mListInventoryAnalysis;
		// int listLength = inventAnaList.size();

		// // Test
		// List<InventoryAnalysisInfo> inventAnaList = new
		// ArrayList<InventoryAnalysisInfo>();
		// for (int i = 0; i < 7; i++) {
		// InventoryAnalysisInfo list = new InventoryAnalysisInfo();
		// inventAnaList.add(list);
		// }
		// inventAnaList.get(0).setNyuko(32);
		// inventAnaList.get(0).setZaiko(83.5);
		//
		// inventAnaList.get(1).setNyuko(56);
		// inventAnaList.get(1).setZaiko(94.1);
		//
		// inventAnaList.get(2).setNyuko(32);
		// inventAnaList.get(2).setZaiko(83);
		//
		// inventAnaList.get(3).setNyuko(16);
		// inventAnaList.get(3).setZaiko(60);
		//
		// inventAnaList.get(4).setNyuko(40);
		// inventAnaList.get(4).setZaiko(72);
		//
		// inventAnaList.get(5).setNyuko(48);
		// inventAnaList.get(5).setZaiko(91.9);
		//
		// inventAnaList.get(6).setNyuko(48);
		// inventAnaList.get(6).setZaiko(98);
		// // Test End
		// array[i][j] = df.format(inventAnaList.get(i).getNyuko());
		if (mListInventoryAnalysis != null && mListInventoryAnalysis.size() > 0) {

			// Loop all over InventoryAnalysis list
			for (int i = 0; i < inventAnaList.size(); i++) {
				for (int j = inventAnaList.size() - 1; j >= i; j--) {
					if (i == 0) {
						array[j][j] = df
								.format(inventAnaList.get(j).getNyuko());
					} else {

						// Check if the right cell and above cell is null. If
						// null
						// -> skip it
						if (array[j][j + 1 - i] != null
								&& array[j - 1][j - i] != null) {

							// Sum of the right cells of the current check cell
							Double sum = 0d;
							for (int temprow = 1; temprow <= i; temprow++) {
								sum += Double.parseDouble(array[j][j + temprow
										- i]);
							}

							// Determine the different of current Zaiko and sum
							// of
							// the right cells
							Double tem = inventAnaList.get(j).getZaiko() - sum;

							// If the different of current Zaiko and sum of the
							// right cells is greater than the value of the
							// above cell Assign the value of the above cell to
							// the current cell
							if (tem > Double.parseDouble(array[j - 1][j - i])) {
								array[j][j - i] = df.format(Double
										.parseDouble(array[j - 1][j - i]));
							}
							// If not, assign the different to the current cell
							else {
								if (tem > 0.001) {
									array[j][j - i] = df.format(tem);
								}
							}
						} else {
							// if the right cell and above cell is null -> skip
							// it
						}
					}
				}
			}
		} else {
			// Do nothing
		}
		mainGrid = array;
	}

	private void determimeLeftListAndDateList() {

		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(1);
		df.setMinimumFractionDigits(0);
		boolean isCheckDifferentYear = false;
		String[][] array = new String[MAIN_GRID_ROW][LEFT_TABLE_COLLUMN];
		int rows = array.length;
		// int cols = array[0].length;
		if (mListInventoryAnalysis != null && mListInventoryAnalysis.size() > 0) {

			InventoryAnalysisInfo item = null;
			String strDate = mListInventoryAnalysis.get(
					mListInventoryAnalysis.size() - 1).getDate();
			String strMonth = strDate.substring(0, 2);
			String strDay = strDate.substring(3);

			// Check is list contains date with different year
			try {
				if (Integer.parseInt(strMonth) == 1
						&& Integer.parseInt(strDay) < 14) {
					isCheckDifferentYear = true;
				}
			} catch (Exception e) {
				Toast.makeText(this, "Loi cast date", Toast.LENGTH_SHORT)
						.show();
			}

			// Determine left list
			for (int row = 0; row < rows; row++) {
				if (row < mListInventoryAnalysis.size()) {

					item = mListInventoryAnalysis.get(row);
					array[row][0] = SetDateString(item.getDate(),
							isCheckDifferentYear);
					dateListJPFormat[row] = array[row][0];

					array[row][1] = df.format(item.getZaiko());
					array[row][2] = df.format(item.getNyuko());

					// Tanaoroshi is determined if only if data of the following
					// day
					// is existed
					if (row < rows - 1) {
						try {
							array[row][3] = determineTanaoroshi(item, row);
						} catch (Exception e) {
							Log.e("Can't determine tanaoroshi",
									"Can determine tanaoroshi");
						}
					}
					array[row][4] = df.format(item.getLoss());
					array[row][5] = df.format(item.getIdo());

				} else {
					// Do nothing leave it blank
				}
			}
		} else {
			// Do nothing
		}
		leftList = array;
	}

	private String SetDateString(String dateString, boolean isCheckDifferentYear) {
		String dateStr = dateString;
		int year = Calendar.getInstance().get(Calendar.YEAR);
		if (Integer.parseInt(dateString.substring(3)) == 12) {
			year = year - 1;
		}

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

		return dateString + "(" + dayOfWeek + ")";
	}

	private String determineTanaoroshi(InventoryAnalysisInfo item, int row) {
		double tanaoroshiValue;
		InventoryAnalysisInfo folowingItem = mListInventoryAnalysis
				.get(row + 1);
		tanaoroshiValue = item.getZaiko() - item.getLoss() + item.getIdo()
				- folowingItem.getZaiko() + folowingItem.getNyuko();
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(1);
		df.setMinimumFractionDigits(0);
		String tanaoroshiStr = df.format(tanaoroshiValue);
		return tanaoroshiStr;
	}

	public boolean inScrollArea(MotionEvent ev) {
		float eventX = ev.getX();
		float eventY = ev.getY();

		int[] arr = new int[2];
		mScrollY.getLocationOnScreen(arr);
		return ((eventX > arr[0] && eventX < (arr[0] + mScrollYChild.getWidth())) && (eventY > arr[1] && eventY < (arr[1] + mScrollYChild
				.getHeight())));
	}

	private void clearData() {
		if (leftList != null) {
			Arrays.fill(leftList, null);
		}
		if (mainGrid != null) {
			Arrays.fill(mainGrid, null);
		}
		if (dateFormat != null) {
			Arrays.fill(dateListJPFormat, null);
		}

		layoutLeftTable.removeAllViews();
		layoutRightHeaderColumn.removeAllViews();
		mLinearLayoutProgramTable.removeAllViews();
	}
}
