package jp.co.zensho.android.sukiya;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.zensho.android.sukiya.adapter.StartDailyCheckboxAdapter;
import jp.co.zensho.android.sukiya.bean.DailyCheckboxValue;
import jp.co.zensho.android.sukiya.bean.KPIInfo;
import jp.co.zensho.android.sukiya.bean.KPIRangeInfo;
import jp.co.zensho.android.sukiya.bean.MenuInfo;
import jp.co.zensho.android.sukiya.bean.MenuPositionInfo;
import jp.co.zensho.android.sukiya.common.DateUtils;
import jp.co.zensho.android.sukiya.common.StringUtils;
import jp.co.zensho.android.sukiya.common.SukiyaContant;
import jp.co.zensho.android.sukiya.database.helper.DatabaseHelper;
import jp.co.zensho.android.sukiya.database.model.CategoryInfo;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class S01Activity extends S00Activity implements OnClickListener {
	private static final String LOG = "S01Activity";
	private static final String MAIN_MENU_ID_PREFIX = "menu_button_";
	private static final int UPDATE_MENU_TIME = 1000 * 30;

	private TextView mTxtPopupTitle;
	private Button mBtnStartDailySubmit;

	private LinearLayout mLinearLayoutKPIInfo1;
	private LinearLayout mLinearLayoutKPIInfo3;
	private LinearLayout mLinearLayoutKPIInfo4;
	private LinearLayout mLinearLayoutKPIInfo5;
	private LinearLayout mLinearLayoutKPIInfo6;
	private LinearLayout mLinearLayoutPopupStartDaily;
	private LinearLayout mLinearLayoutBackgroundPopupStartDaily;
	// Zenchou datnt1 03/12/2104 add ++
	private LinearLayout mLinearLayoutPopupReportTypeSelection;
	private Button mBtnTypeSelectionExport, mBtnTypeSelectionImport;
	// Zenchou datnt1 03/12/2014 add --

	// vdngo 03/18/2014 begin
	private LinearLayout mLinearLayoutMeterTypeSelection;
	private Button mBtnGasAndWaterSelection;
	private Button mBtnElectricitySelection;
	// vdngo 03/18/2014 end

	private GridView gvDailyStartButtonList;
	private StartDailyCheckboxAdapter menuCheckBoxAdapter;

	private TimerTask updateMenuTask;
	final Runnable updateMenuRunnable = new Runnable() {
		public void run() {
			updateMenuListGui();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.s01);
		Log.d(LOG, "[updateCurrentDate]onCreate - START");

		generateViews();

		mLinearLayoutPopupStartDaily = (LinearLayout) findViewById(R.id.linearlayout_popup_start_daily);
		mLinearLayoutPopupStartDaily.setVisibility(View.INVISIBLE);
		// Zenchou datnt1 03/12/2104 add ++
		mLinearLayoutPopupReportTypeSelection = (LinearLayout) findViewById(R.id.linearlayout_popup_report_type_selection);
		mBtnTypeSelectionExport = (Button) findViewById(R.id.p2xx_btn_type_selection_export);
		mBtnTypeSelectionImport = (Button) findViewById(R.id.p2xx_btn_type_selection_import);
		// Zenchou datnt1 03/12/2014 add --

		// vdngo 03/18/2014 begin
		mLinearLayoutMeterTypeSelection = (LinearLayout) findViewById(R.id.p03_meter_selection);
		mLinearLayoutMeterTypeSelection.setOnClickListener(this);
		mBtnGasAndWaterSelection = (Button) findViewById(R.id.s303_btn_gas_water);
		mBtnGasAndWaterSelection.setOnClickListener(this);
		mBtnElectricitySelection = (Button) findViewById(R.id.s303_btn_electricity);
		mBtnElectricitySelection.setOnClickListener(this);
		// vdngo 03/18/2014 end
		mTxtPopupTitle = (TextView) findViewById(R.id.textview_title_start_daily);

		mLinearLayoutBackgroundPopupStartDaily = (LinearLayout) findViewById(R.id.linearlayout_popup_start_dailay_content);
		mLinearLayoutBackgroundPopupStartDaily.setOnClickListener(this);

		gvDailyStartButtonList = (GridView) findViewById(R.id.gvButtonList);

		mBtnStartDailySubmit = (Button) findViewById(R.id.btn_start_daily_submit);
		mBtnStartDailySubmit.setOnClickListener(this);

		// KPI body info
		mLinearLayoutKPIInfo1 = (LinearLayout) findViewById(R.id.kpi_info_body_01);
		mLinearLayoutKPIInfo3 = (LinearLayout) findViewById(R.id.kpi_info_body_03);
		mLinearLayoutKPIInfo4 = (LinearLayout) findViewById(R.id.kpi_info_body_04);
		mLinearLayoutKPIInfo5 = (LinearLayout) findViewById(R.id.kpi_info_body_05);
		mLinearLayoutKPIInfo6 = (LinearLayout) findViewById(R.id.kpi_info_body_06);
		mLinearLayoutKPIInfo1.setOnClickListener(this);
		mLinearLayoutKPIInfo3.setOnClickListener(this);
		mLinearLayoutKPIInfo4.setOnClickListener(this);
		mLinearLayoutKPIInfo5.setOnClickListener(this);
		mLinearLayoutKPIInfo6.setOnClickListener(this);
		// set action into menu button
		int index = 0;
		LinearLayout menuButton = null;
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 4; j++) {
				index = ((i - 1) * 4) + j;
				menuButton = this.getMainButtonAtIndex(index);
				if (menuButton != null) {
					menuButton.setOnClickListener(this);
				}
			}
		}

		// Zenchou datnt1 03/12/2104 add ++
		mLinearLayoutPopupReportTypeSelection.setOnClickListener(this);
		mBtnTypeSelectionExport.setOnClickListener(this);
		mBtnTypeSelectionImport.setOnClickListener(this);
		// Zenchou datnt1 03/12/2104 add --
		Log.d(LOG, "[updateCurrentDate]onCreate - END");
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLinearLayoutPopupStartDaily.setVisibility(View.INVISIBLE);
		// Zenshou datnt1 03/12/2014 add ++
		mLinearLayoutPopupReportTypeSelection.setVisibility(View.INVISIBLE);
		// Zenshou datnt1 03/12/2014 --

		// vdngo begin
		if (mLinearLayoutMeterTypeSelection.getVisibility() == View.VISIBLE) {
			super.hidePopup(mLinearLayoutMeterTypeSelection);
		}
		if (!mBtnElectricitySelection.isEnabled()) {
			mBtnElectricitySelection.setEnabled(true);
		}
		if (!mBtnGasAndWaterSelection.isEnabled()) {
			mBtnGasAndWaterSelection.setEnabled(true);
		}
		
		// vdngo end
		// get last update
		this.getLastupdate();

		// show menu
		this.updateLayout();

		this.startTimerUpdateMenu();
	}

	@Override
	protected void onStop() {
		Log.d(LOG, "[updateCurrentDate] onStop Start");
		super.removeUpdateCurrentDate();

		this.stopTimerUpdateMenu();
		super.onStop();
		Log.d(LOG, "[updateCurrentDate] onStop End");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.top, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		if (super.canCloseMenu() || view == null) {
			return;
		}
		if (view.equals(mLinearLayoutBackgroundPopupStartDaily)) {
			if (View.VISIBLE == mLinearLayoutPopupStartDaily.getVisibility()) {
				super.hidePopup(mLinearLayoutPopupStartDaily);
			}
			return;
		} else if (view.equals(mBtnStartDailySubmit)) {
			this.gotoS02Activity();
			return;
		} else if (view.equals(mLinearLayoutKPIInfo1)) {
			Intent intentS401 = new Intent(S01Activity.this, S401Activity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(SukiyaContant.KPIINFO, SukiyaContant.API037_ID);
			intentS401.putExtras(bundle);
			startActivity(intentS401);
		} else if (view.equals(mLinearLayoutKPIInfo3)) {
			Intent intentS401 = new Intent(S01Activity.this, S401Activity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(SukiyaContant.KPIINFO, SukiyaContant.API038_ID);
			intentS401.putExtras(bundle);
			startActivity(intentS401);
		} else if (view.equals(mLinearLayoutKPIInfo4)) {
			Intent intentS401 = new Intent(S01Activity.this, S401Activity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(SukiyaContant.KPIINFO, SukiyaContant.API039_ID);
			intentS401.putExtras(bundle);
			startActivity(intentS401);
		} else if (view.equals(mLinearLayoutKPIInfo5)) {
			Intent intentS402 = new Intent(S01Activity.this, S402Activity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(SukiyaContant.KPIINFO_2, SukiyaContant.API041_ID);
			intentS402.putExtras(bundle);
			startActivity(intentS402);
		} else if (view.equals(mLinearLayoutKPIInfo6)) {
			Intent intentS402 = new Intent(S01Activity.this, S402Activity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(SukiyaContant.KPIINFO_2, SukiyaContant.API040_ID);
			intentS402.putExtras(bundle);
			startActivity(intentS402);
			// Zenshou datnt1 03/12/2014 add ++
		} else if (view.equals(mBtnTypeSelectionExport)) {
			gotoS208Activity();
		} else if (view.equals(mBtnTypeSelectionImport)) {
			gotoS211Activity();
		} else if (view.equals(mLinearLayoutPopupReportTypeSelection)) {
			super.hidePopup(mLinearLayoutPopupReportTypeSelection);
		}
		// Zenshou datnt1 03/12/2014 add --

		// vdngo 03/18/2014 begin
		else if (view.equals(mBtnGasAndWaterSelection)) {
			// super.hidePopup(mLinearLayoutMeterTypeSelection);

			mBtnElectricitySelection.setEnabled(false);
			gotoS302Activity();
		} else if (view.equals(mBtnElectricitySelection)) {
			// super.hidePopup(mLinearLayoutMeterTypeSelection);
			mBtnGasAndWaterSelection.setEnabled(false);

			gotoS303Activity();
		} else if (view.equals(mLinearLayoutMeterTypeSelection)) {
			super.hidePopup(mLinearLayoutMeterTypeSelection);
		}// vdngo 03/18/2014 end
		else {
			String strViewId = getResources()
					.getResourceEntryName(view.getId());
			if (!StringUtils.isEmpty(strViewId)) {
				if (strViewId.startsWith(MAIN_MENU_ID_PREFIX)) {
					if (View.VISIBLE == mLinearLayoutPopupStartDaily
							.getVisibility()) {
						return;
					}
					// main menu button clicked
					int index = Integer.parseInt(strViewId
							.substring(MAIN_MENU_ID_PREFIX.length())) - 1;
					int x = (index / 4) + 1;
					int y = (index % 4) + 1;

					MenuPositionInfo menuPos = this.getMenuPositionInfo(y, x);
					if (menuPos != null
							&& menuPos.getCurrentDisplayInfo() != null) {
						if (SukiyaContant.MENU_01.equals(menuPos.getCode())
								|| SukiyaContant.MENU_02.equals(menuPos
										.getCode())
								|| SukiyaContant.MENU_03.equals(menuPos
										.getCode())) {
							super.app.setSelectedMenu(menuPos);
							this.checkInputHistory();
						} else if (SukiyaContant.MENU_04.equals(menuPos
								.getCode())) {
							super.app.setSelectedMenu(menuPos);

							this.checkInputHistory();
						}
						// vdngo 03/18/2014 begin
						else if (SukiyaContant.MENU_05
								.equals(menuPos.getCode())) {
							gotoS301Activity();
						}

						// vdngo 04/2/2014 begin
						else if (SukiyaContant.MENU_09
								.equals(menuPos.getCode())) {
							gotoS304Activity();
						}
						// vdngo 04/2/2014 end

						else if (SukiyaContant.MENU_11
								.equals(menuPos.getCode())) {
							super.showPopup(mLinearLayoutMeterTypeSelection);
						}
						// vdngo 03/18/2014 end
						// Zenshou datnt1 03/10/2014 add ++
						else if (SukiyaContant.MENU_06
								.equals(menuPos.getCode())) {
							super.app.setSelectedMenu(menuPos);

							gotoS201Activity();
						} else if (SukiyaContant.MENU_07.equals(menuPos
								.getCode())) {
							super.app.setSelectedMenu(menuPos);

							// Show pop-up report type selection
							super.showPopup(mLinearLayoutPopupReportTypeSelection);
						} else if (SukiyaContant.MENU_08.equals(menuPos
								.getCode())) {
							super.app.setSelectedMenu(menuPos);

							gotoS205Activity();
						}
						// Zenshou datnt1 03/10/2014 add --

					}
				}
			}
		}
	}

	private void showMenuPopup() {
		String popupTitle = null;
		MenuPositionInfo menuPos = super.app.getSelectedMenu();
		if (menuPos != null) {
			if (SukiyaContant.MENU_01.equals(menuPos.getCode())) {
				popupTitle = getString(R.string.tana_eiho);
			} else if (SukiyaContant.MENU_03.equals(menuPos.getCode())) {
				popupTitle = getString(R.string.tana_hozai);
			} else if (menuPos.getCurrentDisplayInfo() != null) {
				String repeatState = menuPos.getCurrentDisplayInfo()
						.getRepeatState();
				if (SukiyaContant.TANA_DAILY.equals(repeatState)) {
					popupTitle = getString(R.string.tana_daily);
				} else if (SukiyaContant.TANA_WEEKLY.equals(repeatState)) {
					popupTitle = getString(R.string.tana_weekly);
				} else if (SukiyaContant.TANA_MONTHLY.equals(repeatState)) {
					popupTitle = getString(R.string.tana_monthly);
				}
			}
		}
		popupTitle = MessageFormat.format(
				getString(R.string.title_screen_01_pupop), popupTitle);
		mTxtPopupTitle.setText(popupTitle);

		// scroll to top
		List<DailyCheckboxValue> dailyCheckboxList = super.app
				.getDailyCheckboxList();

		super.db = new DatabaseHelper(getApplicationContext());
		List<CategoryInfo> categoryHistoryList = super.db
				.getAllCategoryHistory();
		if (dailyCheckboxList != null) {
			for (DailyCheckboxValue value : dailyCheckboxList) {
				value.setSelected(false);
				value.setCanUnselect(true);
				if (categoryHistoryList != null
						&& categoryHistoryList.size() > 0) {
					for (CategoryInfo categoryInfo : categoryHistoryList) {
						if (categoryInfo.getId().equals(value.getCode())) {
							value.setSelected(true);
							value.setCanUnselect(false);
							break;
						}
					}
				}
			}
		}
		super.db.close();

		menuCheckBoxAdapter = new StartDailyCheckboxAdapter(this,
				dailyCheckboxList);
		gvDailyStartButtonList.setAdapter(menuCheckBoxAdapter);
		menuCheckBoxAdapter.notifyDataSetChanged();
		gvDailyStartButtonList.invalidateViews();

		super.showPopup(mLinearLayoutPopupStartDaily);
	}

	private void updateLayout() {
		Log.d(LOG, "updateLayout - START");
		// Update current date
		TextView dateTextView = (TextView) findViewById(R.id.current_date);
		if (dateTextView != null) {
			dateFormat = new SimpleDateFormat(StringUtils.YYYYMMDD_DISP,
					Locale.JAPAN);
			dateTextView.setText(dateFormat.format(super.app.getCurrentDate()));
		}

		// Update Shop name
		TextView shopNameTextView = (TextView) findViewById(R.id.shop_name);
		if (shopNameTextView != null) {
			shopNameTextView.setText(super.app.getShopName());
		}

		// Update KPI info
		KPIInfo kpiInfo = super.app.getKpiInfo();
		KPIRangeInfo kpiRangeInfo = super.app.getKpiRangeInfo();
		if (kpiInfo != null && kpiRangeInfo != null) {
			// 在庫日
			TextView currentStockDays = (TextView) findViewById(R.id.s01_txt_current_stock_days);
			TextView prevStockDays = (TextView) findViewById(R.id.s01_txt_prev_stock_days);
			this.setupKPIItem(currentStockDays, prevStockDays,
					kpiInfo.getZaikonissu(), kpiInfo.getZaikonissuPrev(),
					kpiRangeInfo.getZaikonissuMin(),
					kpiRangeInfo.getZaikonissuMax(), StringUtils.MINUS);

			// 原価率
			TextView currentCostRate = (TextView) findViewById(R.id.s01_txt_current_cost_rate);
			TextView prevCostRate = (TextView) findViewById(R.id.s01_txt_prev_cost_rate);
			this.setupKPIItem(currentCostRate, prevCostRate,
					kpiInfo.getGenkaritsu(), kpiInfo.getGenkaritsuPrev(),
					kpiRangeInfo.getGenkaritsuMin(),
					kpiRangeInfo.getGenkaritsuMax(), StringUtils.MINUS);

			// 原価率
			TextView currentUnmatch = (TextView) findViewById(R.id.s01_txt_current_unmatch);
			TextView prevUnmatch = (TextView) findViewById(R.id.s01_txt_prev_unmatch);
			this.setupKPIItem(currentUnmatch, prevUnmatch,
					kpiInfo.getUnmatch(), kpiInfo.getUnmatchPrev(),
					kpiRangeInfo.getUnmatchMin(), kpiRangeInfo.getUnmatchMax(),
					StringUtils.ZERO);

			// ロス
			TextView currentLossAmount = (TextView) findViewById(R.id.s01_txt_current_loss_amount);
			TextView prevLossAmount = (TextView) findViewById(R.id.s01_txt_prev_loss_amount);
			this.setupKPIItem(currentLossAmount, prevLossAmount,
					kpiInfo.getLoss(), kpiInfo.getLossPrev(),
					kpiRangeInfo.getLossMin(), kpiRangeInfo.getLossMax(),
					StringUtils.ZERO);

			// 入荷
			TextView currentInStock = (TextView) findViewById(R.id.s01_txt_current_in_stock);
			TextView prevInStock = (TextView) findViewById(R.id.s01_txt_prev_in_stock);
			this.setupKPIItem(currentInStock, prevInStock, kpiInfo.getRcv(),
					kpiInfo.getRcvPrev(), kpiRangeInfo.getRcvMin(),
					kpiRangeInfo.getRcvMax(), StringUtils.ZERO);

			// 入荷
			TextView currentShipping = (TextView) findViewById(R.id.s01_txt_kpi_snd);
			TextView prevShipping = (TextView) findViewById(R.id.s01_txt_kpi_snd_prev);
			this.setupKPIItem(currentShipping, prevShipping, kpiInfo.getSnd(),
					kpiInfo.getSndPrev(), kpiRangeInfo.getSndMin(),
					kpiRangeInfo.getSndMax(), StringUtils.ZERO);
		}

		this.updateMenuListGui();
		Log.d(LOG, "updateLayout - END");
	}

	private void updateMenuListGui() {
		Log.d(LOG, "updateMenuListGui - START");
		// Update Menu list
		Calendar c = Calendar.getInstance();
		c.setTime(this.app.getCurrentDate());

		int currentDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		int currentDay = c.get(Calendar.DAY_OF_MONTH);
		Drawable menuBackground = null;
		Drawable menuBackgroundNote = null;
		AnimationDrawable frameAnimation = null;
		StateListDrawable backgroundStateList = null;
		Drawable current = null;
		int index = 0;
		boolean canCheckPriority = false;
		LinearLayout buttonView = null;
		TextView labelButton = null;
		MenuPositionInfo menuPos = null;
		String strLastupdate = null;
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 4; j++) {
				index = ((i - 1) * 4) + j;

				buttonView = this.getMainButtonAtIndex(index);
				if (buttonView != null) {
					buttonView.setVisibility(View.INVISIBLE);

					menuPos = this.getMenuPositionInfo(j, i);
					if (menuPos != null) {
						if (menuPos.getList() != null
								&& menuPos.getList().size() > 0) {
							menuPos.setCurrentDisplayInfo(null);
							for (MenuInfo menuInfo : menuPos.getList()) {
								canCheckPriority = false;

								if (1 == menuInfo.getDaily()) {
									canCheckPriority = true;
									menuInfo.setRepeatState(SukiyaContant.TANA_DAILY);
								} else if (1 == menuInfo.getSun()
										&& Calendar.SUNDAY == currentDayOfWeek) {
									canCheckPriority = true;
									menuInfo.setRepeatState(SukiyaContant.TANA_WEEKLY);
								} else if (1 == menuInfo.getMon()
										&& Calendar.MONDAY == currentDayOfWeek) {
									canCheckPriority = true;
									menuInfo.setRepeatState(SukiyaContant.TANA_WEEKLY);
								} else if (1 == menuInfo.getTue()
										&& Calendar.TUESDAY == currentDayOfWeek) {
									canCheckPriority = true;
									menuInfo.setRepeatState(SukiyaContant.TANA_WEEKLY);
								} else if (1 == menuInfo.getWed()
										&& Calendar.WEDNESDAY == currentDayOfWeek) {
									canCheckPriority = true;
									menuInfo.setRepeatState(SukiyaContant.TANA_WEEKLY);
								} else if (1 == menuInfo.getThu()
										&& Calendar.THURSDAY == currentDayOfWeek) {
									canCheckPriority = true;
									menuInfo.setRepeatState(SukiyaContant.TANA_WEEKLY);
								} else if (1 == menuInfo.getFri()
										&& Calendar.FRIDAY == currentDayOfWeek) {
									canCheckPriority = true;
									menuInfo.setRepeatState(SukiyaContant.TANA_WEEKLY);
								} else if (1 == menuInfo.getSat()
										&& Calendar.SATURDAY == currentDayOfWeek) {
									canCheckPriority = true;
									menuInfo.setRepeatState(SukiyaContant.TANA_WEEKLY);
								} else if (currentDay == menuInfo.getMonthly()) {
									canCheckPriority = true;
									menuInfo.setRepeatState(SukiyaContant.TANA_MONTHLY);
								}

								if (canCheckPriority) {
									if (menuPos.getCurrentDisplayInfo() == null
											|| menuPos.getCurrentDisplayInfo()
													.getPriority() > menuInfo
													.getPriority()
											|| !menuPos.getCurrentDisplayInfo()
													.isEnable()) {
										menuPos.setCurrentDisplayInfo(menuInfo);
									}
								}
							}

							if (menuPos.getCurrentDisplayInfo() != null) {
								// Set first text for menu
								// Set second text for menu
								labelButton = this
										.getTextViewOnMainButtonAtIndex(index,
												2);
								if (labelButton != null) {
									strLastupdate = menuPos.getLastUpdate();

									if (!StringUtils.isEmpty(strLastupdate)) {
										labelButton.setVisibility(View.VISIBLE);
										labelButton.setText(strLastupdate);
									} else {
										labelButton
												.setVisibility(View.INVISIBLE);
									}
								}
								// Set third text for menu
								labelButton = this
										.getTextViewOnMainButtonAtIndex(index,
												3);
								if (labelButton != null) {
									labelButton.setVisibility(View.VISIBLE);
									labelButton
											.setText(menuPos
													.getCurrentDisplayInfo()
													.getLabel());
								}

								// Enable menu button
								buttonView.setEnabled(menuPos
										.getCurrentDisplayInfo().isEnable());

								buttonView.setVisibility(View.VISIBLE);
							}

							if (buttonView.getVisibility() == View.VISIBLE
									&& buttonView.isEnabled()) {
								if (menuPos.isShowNote(super.app
										.getCurrentDate())) {
									menuBackgroundNote = getResources()
											.getDrawable(
													R.drawable.selector_btn_main_menu_note);
									buttonView
											.setBackground(menuBackgroundNote);

									backgroundStateList = (StateListDrawable) buttonView
											.getBackground();
									current = backgroundStateList.getCurrent();
									if (current instanceof AnimationDrawable) {
										frameAnimation = (AnimationDrawable) current;
										// frameAnimation.start();
										frameAnimation.setVisible(true, true);
									}
								} else {
									Log.d(LOG, "Menu: " + menuPos.getCode()
											+ " disable animation");
									menuBackground = getResources()
											.getDrawable(
													R.drawable.selector_btn_main_menu);
									buttonView.setBackground(menuBackground);
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean isKPIBlueColor(Double curValue, Double minValue,
			Double maxValue) {
		Log.d(LOG, "isKPIBlueColor - START");
		double dCurValue = ((curValue == null) ? 0.0f : curValue);
		double dMinValue = ((minValue == null) ? 0.0f : minValue);
		double dMaxValue = ((maxValue == null) ? 0.0f : maxValue);
		Log.d(LOG, "dCurValue: " + dCurValue + " / dMinValue: " + dMinValue
				+ " / dMaxValue: " + dMaxValue);

		if (minValue == null && maxValue == null) {
			Log.d(LOG, "isKPIBlueColor - END / Blue");
			return true; // Blue
		} else if (minValue == null && maxValue != null) {
			if (dCurValue <= dMaxValue) {
				Log.d(LOG, "isKPIBlueColor - END / Orange");
				return false; // Orange
			} else {
				Log.d(LOG, "isKPIBlueColor - END / Blue");
				return true; // Blue
			}
		} else if (minValue != null && maxValue == null) {
			if (dMinValue <= dCurValue) {
				Log.d(LOG, "isKPIBlueColor - END / Orange");
				return false; // Orange
			} else {
				Log.d(LOG, "isKPIBlueColor - END / Blue");
				return true; // Blue
			}
		} else {
			if (dMinValue <= dCurValue && dCurValue <= dMaxValue) {
				Log.d(LOG, "isKPIBlueColor - END / Orange");
				return false; // Orange
			} else {
				Log.d(LOG, "isKPIBlueColor - END / Blue");
				return true; // Blue
			}
		}
	}

	private boolean isKPIBlueColor(Integer curValue, Integer minValue,
			Integer maxValue) {
		Log.d(LOG, "isKPIBlueColor - START");
		int dCurValue = ((curValue == null) ? 0 : curValue);
		int dMinValue = ((minValue == null) ? 0 : minValue);
		int dMaxValue = ((maxValue == null) ? 0 : maxValue);
		Log.d(LOG, "dCurValue: " + dCurValue + " / dMinValue: " + dMinValue
				+ " / dMaxValue: " + dMaxValue);

		if (minValue == null && maxValue == null) {
			Log.d(LOG, "isKPIBlueColor - END / Blue");
			return true; // Blue
		} else if (minValue == null && maxValue != null) {
			if (dCurValue <= dMaxValue) {
				Log.d(LOG, "isKPIBlueColor - END / Orange");
				return false; // Orange
			} else {
				Log.d(LOG, "isKPIBlueColor - END / Blue");
				return true; // Blue
			}
		} else if (minValue != null && maxValue == null) {
			if (dMinValue <= dCurValue) {
				Log.d(LOG, "isKPIBlueColor - END / Orange");
				return false; // Orange
			} else {
				Log.d(LOG, "isKPIBlueColor - END / Blue");
				return true; // Blue
			}
		} else {
			if (dMinValue <= dCurValue && dCurValue <= dMaxValue) {
				Log.d(LOG, "isKPIBlueColor - END / Orange");
				return false; // Orange
			} else {
				Log.d(LOG, "isKPIBlueColor - END / Blue");
				return true; // Blue
			}
		}
	}

	private void setupKPIItem(TextView curItem, TextView preItem,
			Double curValue, Double preValue, Double minValue, Double maxValue,
			String defValue) {
		Log.d(LOG, "setupKPIItem - START");
		// Setup default display
		String disCurValue = defValue;
		if (curValue != null) {
			disCurValue = String.valueOf(curValue);
		}

		String disPreValue = StringUtils.EMPTY;
		if (preValue != null) {
			disPreValue = MessageFormat.format("({0})", preValue);
		}

		// Setup color
		int color = getResources().getColor(R.color.orange);
		if (this.isKPIBlueColor(curValue, minValue, maxValue)) {
			color = getResources().getColor(R.color.blue);
		}

		// setup current text field
		if (curItem != null) {
			curItem.setText(disCurValue);
			curItem.setTextColor(color);
		}

		// setup current text field
		if (preItem != null) {
			preItem.setText(disPreValue);
			preItem.setTextColor(color);
		}
		Log.d(LOG, "setupKPIItem - END");
	}

	private void setupKPIItem(TextView curItem, TextView preItem,
			Integer curValue, Integer preValue, Integer minValue,
			Integer maxValue, String defValue) {
		Log.d(LOG, "setupKPIItem - START");
		// Setup default display
		String disCurValue = defValue;
		if (curValue != null) {
			disCurValue = String.valueOf(curValue);
		}

		String disPreValue = StringUtils.EMPTY;
		if (preValue != null) {
			disPreValue = MessageFormat.format("({0})", preValue);
		}

		// Setup color
		int color = getResources().getColor(R.color.orange);
		if (this.isKPIBlueColor(curValue, minValue, maxValue)) {
			color = getResources().getColor(R.color.blue);
		}

		// setup current text field
		if (curItem != null) {
			curItem.setText(disCurValue);
			curItem.setTextColor(color);
		}

		// setup previous text field
		if (preItem != null) {
			preItem.setText(disPreValue);
			preItem.setTextColor(color);
		}
		Log.d(LOG, "setupKPIItem - END");
	}

	private void startTimerUpdateMenu() {
		Log.d(LOG, "startTimerUpdateMenu - START");
		Timer timer = new Timer();
		this.updateMenuTask = new TimerTask() {
			@Override
			public void run() {
				Date d = new Date();
				SimpleDateFormat df = new SimpleDateFormat(
						StringUtils.HHMMSS_DISP, Locale.JAPAN);
				Log.d(LOG, "Call update menu grid: " + df.format(d));
				runOnUiThread(updateMenuRunnable);
			}
		};
		timer.schedule(this.updateMenuTask, UPDATE_MENU_TIME, UPDATE_MENU_TIME);
		Log.d(LOG, "startTimerUpdateMenu - END");
	}

	private void stopTimerUpdateMenu() {
		Log.d(LOG, "stopTimerUpdateMenu - START");
		if (this.updateMenuTask != null) {
			this.updateMenuTask.cancel();
			this.updateMenuTask = null;
		}
		Log.d(LOG, "stopTimerUpdateMenu - END");
	}

	private void gotoS02Activity() {
		// dismiss process dialog
		this.dismissProcessDialog();

		// Goto S02Activity screen
		Intent intent = new Intent(this, S02Activity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.slide_in_right,
				R.animator.slide_out_left);
	}

	// Zenshou datnt1 03/10/2014 add ++
	// Go to loss report screen
	private void gotoS201Activity() {
		// dismiss process dialog
		this.dismissProcessDialog();

		// Goto S201Activity screen
		Intent intent = new Intent(S01Activity.this, S201Activity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.slide_in_right,
				R.animator.slide_out_left);
	}

	// Go to short report screen (added: 03/11/2014)
	private void gotoS205Activity() {
		// dismiss process dialog
		this.dismissProcessDialog();

		// Goto S205Activity screen
		Intent intent = new Intent(S01Activity.this, S205Activity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.slide_in_right,
				R.animator.slide_out_left);
	}

	// Go to shipping (export) report screen (added: 03/12/2014)
	private void gotoS208Activity() {
		// dismiss process dialog
		this.dismissProcessDialog();

		// Goto S208Activity screen
		Intent intent = new Intent(S01Activity.this, S208Activity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.slide_in_right,
				R.animator.slide_out_left);
	}

	// Go to shipping (import) report screen (added: 03/18/2014)
	private void gotoS211Activity() {
		// dismiss process dialog
		this.dismissProcessDialog();

		// Goto S211Activity screen
		Intent intent = new Intent(S01Activity.this, S211Activity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.slide_in_right,
				R.animator.slide_out_left);
	}

	// Zenshou datnt1 03/10/2014 add --

	// vdngo 03/18/2014 begin
	private void gotoS301Activity() {
		// dismiss process dialog
		this.dismissProcessDialog();

		// Goto S02Activity screen
		Intent intent = new Intent(this, S301Activity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.slide_in_right,
				R.animator.slide_out_left);
	}

	private void gotoS304Activity() {
		// dismiss process dialog
		this.dismissProcessDialog();

		// Goto S02Activity screen
		Intent intent = new Intent(this, S304Activity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.slide_in_right,
				R.animator.slide_out_left);
	}

	private void gotoS302Activity() {
		// dismiss process dialog
		this.dismissProcessDialog();

		// Goto S02Activity screen
		Intent intent = new Intent(this, S302Activity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.slide_in_right,
				R.animator.slide_out_left);
	}

	private void gotoS303Activity() {
		// dismiss process dialog
		this.dismissProcessDialog();

		// Goto S02Activity screen
		Intent intent = new Intent(this, S303Activity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.slide_in_right,
				R.animator.slide_out_left);
	}

	// vdngo 03/18/2014 end
	private void getLastupdate() {
		List<MenuPositionInfo> menuPosList = super.app.getMenuPositionList();
		if (menuPosList != null && menuPosList.size() > 0) {
			super.db = new DatabaseHelper(getApplicationContext());

			List<MenuInfo> menuList = null;
			String lastupdate = null;
			StringBuilder menuId = null;
			for (MenuPositionInfo menuPositionInfo : menuPosList) {
				if (SukiyaContant.MENU_02.equals(menuPositionInfo.getCode())) {
					menuList = menuPositionInfo.getList();
					if (menuList != null && menuList.size() > 0) {
						for (MenuInfo menuInfo : menuList) {
							menuId = new StringBuilder();
							menuId.append(menuPositionInfo.getCode());
							menuId.append(menuInfo.getTimeCode());
							menuId.append(menuInfo.getPriority());

							lastupdate = super.db.getLastupdateMenu(menuId
									.toString());
							menuInfo.setLastupdate(lastupdate);
						}
					}
				} else {
					lastupdate = super.db.getLastupdateMenu(menuPositionInfo
							.getCode());
					menuPositionInfo.setLastUpdate(lastupdate);
				}
			}
			super.db.close();
		}
	}

	private void checkInputHistory() {
		MenuPositionInfo pos = super.app.getSelectedMenu();
		MenuInfo menu = pos.getCurrentDisplayInfo();

		String clearance = SukiyaContant.TANA_EIHO;
		if (pos != null) {
			if (SukiyaContant.MENU_01.equals(pos.getCode())) {
				clearance = SukiyaContant.TANA_EIHO;
			} else if (SukiyaContant.MENU_02.equals(pos.getCode())
					&& menu != null) {
				String repeatState = menu.getRepeatState();
				if (SukiyaContant.TANA_DAILY.equals(repeatState)) {
					clearance = SukiyaContant.TANA_DAILY;
				} else if (SukiyaContant.TANA_WEEKLY.equals(repeatState)) {
					clearance = SukiyaContant.TANA_WEEKLY;
				} else if (SukiyaContant.TANA_MONTHLY.equals(repeatState)) {
					clearance = SukiyaContant.TANA_MONTHLY;
				}
			} else if (SukiyaContant.MENU_03.equals(pos.getCode())) {
				clearance = SukiyaContant.TANA_HOZAI;
			} else if (SukiyaContant.MENU_04.equals(pos.getCode())) {
				clearance = SukiyaContant.TANA_SHOMIKIGEN;
			}
		}

		boolean viewPreInputMode = false;
		String menuCode = pos.getCode();
		String timeCode = menu.getTimeCode();
		int priority = menu.getPriority();
		String strDate = DateUtils.getYYYYMMDD(super.app.getCurrentDate());
		String from = menu.getFrom();
		String to = menu.getTo();

		super.db = new DatabaseHelper(getApplicationContext());
		int historyId = super.db.getMenuInputHistoryId(menuCode, clearance,
				timeCode, priority, strDate, from, to);

		if (historyId > -1) {
			// Has menu history data
			viewPreInputMode = super.db.isSubmitedDataMenu(menuCode, clearance,
					timeCode, priority, strDate, from, to);
		} else {
			// No history menu data
			super.db.setInputDataToMenu(menuCode, clearance, timeCode,
					priority, strDate, from, to);
		}
		super.db.close();

		if (viewPreInputMode) {
			pos.setHistoryId(historyId);
			super.app.setDisplayOnly(true);

			Intent intent = new Intent(S01Activity.this, S04Activity.class);
			startActivity(intent);
			overridePendingTransition(R.animator.slide_in_right,
					R.animator.slide_out_left);
		} else {
			super.app.setDisplayOnly(false);

			historyId = super.db.getMenuInputHistoryId(menuCode, clearance,
					timeCode, priority, strDate, from, to);
			pos.setHistoryId(historyId);

			if (SukiyaContant.MENU_04.equals(menuCode)) {
				Intent intent = new Intent(S01Activity.this, S02Activity.class);
				startActivity(intent);
				overridePendingTransition(R.animator.slide_in_right,
						R.animator.slide_out_left);
			} else {
				// Update popup title
				showMenuPopup();
			}
		}
	}
}
