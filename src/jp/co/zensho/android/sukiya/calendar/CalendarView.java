package jp.co.zensho.android.sukiya.calendar;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.common.AnimationHelper;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class CalendarView extends LinearLayout {
	protected static final String TAG = CalendarView.class.getSimpleName();

	private static final Animation inFromLeft = AnimationHelper
			.inFromLeftAnimation();
	private static final Animation outToRight = AnimationHelper
			.outToRightAnimation();
	private static final Animation inFromRight = AnimationHelper
			.inFromRightAnimation();
	private static final Animation outToLeft = AnimationHelper
			.outToLeftAnimation();

	private static final Animation inFromTop = AnimationHelper
			.inFromTopAnimation();
	private static final Animation outToBottom = AnimationHelper
			.outToBottomAnimation();
	private static final Animation inFromBottom = AnimationHelper
			.inFromBottomAnimation();
	private static final Animation outToTop = AnimationHelper
			.outToTopAnimation();

	private static final int DIRECTION_HORIZONTAL = 0;
	private static final int DIRECTION_VERTICAL = 1;

	protected java.util.Calendar calendar = java.util.Calendar.getInstance();
	protected java.util.Calendar iniCal = java.util.Calendar.getInstance();

	protected ViewFlipper viewFlipper;
	protected GestureDetector detector;
	protected MonthlyCalendarView mViewPrevious;
	protected MonthlyCalendarView mViewNext;
	protected float lastTouchX;
	protected float lastTouchY;
	protected TextView txtHeader;
	// vdngo begin
	// private Context context;
	// vdngo end

	protected int c_backgroud = Color.parseColor("#f0ffffff");
	protected int c_foregroud = Color.parseColor("#ff000000");
	protected int c_dark = Color.parseColor("#6456648f");
	protected int c_hilite = Color.parseColor("#ffffffff");
	protected int c_light = Color.parseColor("#64c6d4ef");
	protected int c_holidaty = Color.parseColor("#ffFF0000");
	protected int c_saturday = Color.parseColor("#ff0000FF");
	protected int c_selected = Color.parseColor("#64FFA500");

	protected ArrayList<OnCalendarSelectionListener> listenerList = new ArrayList<OnCalendarSelectionListener>();

	protected static DateFormatSymbols symbols = new DateFormatSymbols(
			Locale.JAPAN);
	protected static final String[] monthNames = symbols.getMonths();

	public CalendarView(Context context) {
		super(context);
		Log.e("test", "Vao public CalendarView(Context context) ");
		this.viewFlipper = new FixableViewFlipper(context);
		this.init(context, null);
		// this.context = context;

	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.e("test", "Vao CalendarView(Context context, AttributeSet attrs) ");
		this.viewFlipper = new FixableViewFlipper(context);
		this.init(context, attrs);
	}

	public java.util.Calendar getCalandar() {
		return this.calendar;
	}

	public void setCalandar(Calendar iniCal, Calendar calandar) {
		this.calendar = calandar;
		// vdngo 04/08/2014
		//
		this.iniCal.set(Calendar.YEAR, iniCal.get(Calendar.YEAR));
		this.iniCal.set(Calendar.MONTH, iniCal.get(Calendar.MONTH));
		this.iniCal.set(Calendar.DAY_OF_MONTH,
				iniCal.get(Calendar.DAY_OF_MONTH));
		setHeader(this.calendar.get(Calendar.YEAR),
				this.calendar.get(Calendar.MONTH));
		Log.e("test `in setCalandar calandar",
				String.valueOf(calandar.get(Calendar.DAY_OF_MONTH)));
		Log.e("test `in setCalandar this.calendar",
				String.valueOf(this.calendar.get(Calendar.DAY_OF_MONTH)));

		// vdngo end
		if (this.viewFlipper != null) {
			MonthlyCalendarView view = (MonthlyCalendarView) this.viewFlipper
					.getChildAt(this.viewFlipper.getDisplayedChild());
			if (view != null) { // viewはnullもあり得る
				view.setCalendar(this.iniCal, this.calendar);
				// if (this.calendar.equals(calandar)) {
				//
				// view.setToDay(this.calendar.get(Calendar.DAY_OF_MONTH));
				// }
			}
		}
	}

	// public void setCalendar(int year, int month, int day) {
	// this.calendar.clear();
	// this.calendar.set(year, month - 1, day);
	//
	// Log.i("Check view flipper", String.valueOf(viewFlipper));
	// if (this.viewFlipper != null) {
	// MonthlyCalendarView view = (MonthlyCalendarView) this.viewFlipper
	// .getChildAt(this.viewFlipper.getDisplayedChild());
	// if (view != null) { // viewはnullもあり得る
	// view.setCalendar(this.calendar);
	// }
	// }
	// }

	private void init(Context context, AttributeSet attrs) {
		// フレームワークリソースから色を取得
		Resources res = context.getResources();
		if (res != null) {
			this.c_backgroud = res.getColor(R.color.calendar_background);
			this.c_foregroud = res.getColor(R.color.calendar_foreground);
			this.c_dark = res.getColor(R.color.calendar_dark);
			this.c_hilite = res.getColor(R.color.calendar_hilite);
			this.c_light = res.getColor(R.color.calendar_light);
			this.c_holidaty = res.getColor(R.color.calendar_holiday);
			this.c_saturday = res.getColor(R.color.calendar_saturday);
			this.c_selected = res.getColor(R.color.calendar_selected);
		}

		// XMLから属性読み込み
		// if (attrs != null) {
		// try {
		// // date = "yyyy/MM"
		// String dateStr = attrs.getAttributeValue(null, "date");
		//
		// // if ( dateStr != null && dateStr.length() > 0 )
		// if (!StringUtils.isEmpty(dateStr)) {
		// SimpleDateFormat format = new SimpleDateFormat("yyyy/MM",
		// Locale.JAPAN);
		// Date date = format.parse(dateStr);
		// Calendar cal = Calendar.getInstance();
		// cal.setTime(date);
		// this.setCalendar(cal.get(Calendar.YEAR),
		// cal.get(Calendar.MONTH) + 1,
		// cal.get(Calendar.DAY_OF_MONTH));
		// }
		//
		// // background = "#f0ffffff" (#ARGB)
		// String backgroundStr = attrs.getAttributeValue(null,
		// "background");
		//
		// if (!StringUtils.isEmpty(backgroundStr)) {
		// this.c_backgroud = Color.parseColor(backgroundStr);
		// }
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
		// }

		this.setClickable(true);

		this.setOrientation(LinearLayout.VERTICAL);
		this.setGravity(Gravity.TOP | Gravity.CENTER);
		this.setBackgroundColor(this.c_backgroud);
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		// ナビゲーションバー
		LinearLayout navBar = new LinearLayout(context);
		navBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		navBar.setPadding(1, 1, 1, 1);
		navBar.setBackgroundColor(this.c_backgroud);
		{
			// << ボタン
			Button btnBack = new Button(context, attrs);
			btnBack.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, 5)); // 末尾のパラメタはweight
			btnBack.setBackgroundColor(Color.parseColor("#00000000"));
			btnBack.setText("<<");
			btnBack.setFocusable(false);
			btnBack.setFocusableInTouchMode(false);

			btnBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					calendar.add(Calendar.MONTH, -1);
					// calendar.set(calendar.ge);
					showPreviousMonth(DIRECTION_HORIZONTAL);
				}
			});

			navBar.addView(btnBack);

			// ヘッダ (年月を表示する)
			this.txtHeader = new TextView(context, attrs);
			this.txtHeader.setGravity(Gravity.CENTER);
			// this.txtHeader.setBackgroundColor(this.c_backgroud);
			this.txtHeader.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 80)); // 末尾のパラメタはweight
			this.txtHeader.setTextColor(this.c_foregroud);
			this.txtHeader.setBackgroundColor(Color.parseColor("#00000000"));// this.c_backgroud);
			this.txtHeader.setTextSize(20f);
			this.txtHeader.setTypeface(Typeface.SANS_SERIF);

			// this.setHeader(this.calendar.get(java.util.Calendar.YEAR),
			// this.calendar.get(java.util.Calendar.MONTH));
			// Log.e("Cadelar test vdngo", calendar.get(Calendar.YEAR) + ", "
			// + calendar.get(Calendar.MONTH));

			this.txtHeader.setFocusable(true);
			this.txtHeader.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CalendarView.this.setYearAndMonthFromDialog();
				}
			});
			navBar.addView(this.txtHeader);

			// >> ボタン
			Button btnFwd = new Button(context, attrs);
			btnFwd.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, 5)); // 末尾のパラメタはweight
			btnFwd.setBackgroundColor(Color.parseColor("#00000000"));
			btnFwd.setFocusable(false);
			btnFwd.setFocusableInTouchMode(false);
			btnFwd.setText(">>");
			btnFwd.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (calendar.get(Calendar.YEAR)  <= 2099 && calendar.get(Calendar.MONTH) != 11) {
						Log.e("testmonth", calendar.toString());
						calendar.add(Calendar.MONTH, 1);
						showNextMonth(DIRECTION_HORIZONTAL);
					}
				}
			});
			navBar.addView(btnFwd);
		}
		this.addView(navBar);

		// ビューフリッパーの追加
		// this.viewFlipper = new FixableViewFlipper(context);
		this.viewFlipper.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		{
			// カレンダービューの追加
			this.mViewPrevious = new MonthlyCalendarView(context);
			// this.mViewPrevious.setCalendar(this.calendar);
			this.mViewPrevious.setBackgroundColor(this.c_backgroud);
			this.viewFlipper.addView(this.mViewPrevious);

			// カレンダービューの追加
			this.mViewNext = new MonthlyCalendarView(context);
			// this.mViewNext.setCalendar(this.calendar);
			this.mViewNext.setBackgroundColor(this.c_backgroud);
			this.viewFlipper.addView(this.mViewNext);
		}

		this.addView(this.viewFlipper);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction() & 0xff; // MotionEvent.ACTION_MASK
		switch (action) {
		case MotionEvent.ACTION_DOWN: // モーション開始
			lastTouchX = event.getX();
			lastTouchY = event.getY();
			break;
		case MotionEvent.ACTION_CANCEL: // モーション失敗
			return true;
		case MotionEvent.ACTION_UP: // モーションアップ
			// Log.d("viewFlipper", "++ otionEvent.ACTION_CANCEL ++ " +
			// event.toString());
			float currentX = event.getX();
			float currentY = event.getY();
			this.flickOrTaptoDate(currentX, currentX - lastTouchX, currentY,
					currentY - lastTouchY);
			break;
		}
		try {
			Thread.sleep(16);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
		// return super.onTouchEvent(event);
	}

	/**
	 * フリックかタップかにより画面を変えます
	 * 
	 * @param currentX
	 *            現在のX座標
	 * @param velocityX
	 *            X座標の移動量
	 * @param currentY
	 *            現在のY座標
	 * @param velocityY
	 *            Y座標の移動量
	 */
	private boolean flickOrTaptoDate(float currentX, float velocityX,
			float currentY, float velocityY) {

		// 基準の移動距離はViewConfigurationを使用する
		ViewConfiguration viewConfiguration = ViewConfiguration
				.get(getContext());
		int minimumFlingVelocity = viewConfiguration
				.getScaledMinimumFlingVelocity();

		velocityX = Math.abs(velocityX);
		velocityY = Math.abs(velocityY);

		if (velocityX < minimumFlingVelocity
				&& velocityY < minimumFlingVelocity) {

			// 変量 < 閾値の場合タップ
			MonthlyCalendarView currentView = (MonthlyCalendarView) viewFlipper
					.getCurrentView();
			if (currentView != null) {
				boolean result = currentView.performClick(); // 対象ビューのクリックを発生させる
				performSelectionCalendar(currentView);
				return result;
			}
		} else {
			// 変量 >= 閾値の場合フリック

			// X軸とY軸の変量の大きい方にアニメーション
			int direction = (velocityY >= velocityX) ? DIRECTION_VERTICAL
					: DIRECTION_HORIZONTAL;

			// 次月(上、又は左にモーション)? 前月(下又は右にモーション)?
			boolean showNext = (direction == DIRECTION_VERTICAL) ? (currentY < lastTouchY)
					: (currentX < lastTouchX);

			// カレンダー加減算
			calendar.add(Calendar.MONTH, showNext ? 1 : -1);

			// 適切な月に移動
			if (showNext) {
				showNextMonth(direction);
			} else {
				showPreviousMonth(direction);
			}
		}
		return true;
	}

	/**
	 * カレンダーの特定日が選択された際の処理を記述します
	 * 
	 * @param currentView
	 *            　対象になっているカレンダービューをセットします
	 */
	protected void performSelectionCalendar(MonthlyCalendarView currentView) {
		DateInfo dateInfo = currentView.getSelectedDateInfo();

		if (dateInfo != null) {
			// Log.d(TAG, "onCalendarSelection: dateInfo=" + dateInfo );
			int size = this.listenerList.size();
			if (size > 0) {
				for (int i = size - 1; i >= 0; i--) {
					this.listenerList.get(i).onCalendarSelection(
							new CalendarSelectionEvent(this, dateInfo));
				}
			}
		}

	}

	/**
	 * カレンダーの特定日を選択します
	 * 
	 * @param year
	 *            年をセット
	 * @param month
	 *            月をセット
	 * @param day
	 *            日をセット
	 */
	public void selectCalendar(int year, int month, int day) {
		MonthlyCalendarView current = (MonthlyCalendarView) this.viewFlipper
				.getCurrentView();
		if (current != null) {
			current.setCalendar(year, month, day);
			DateInfo dateInfo = current.getSelectedDateInfo();
			// Log.d(TAG, "onCalendarSelection: dateInfo=" + dateInfo );
			int size = this.listenerList.size();
			if (size > 0) {
				for (int i = size - 1; i >= 0; i--) {
					this.listenerList.get(i).onCalendarSelection(
							new CalendarSelectionEvent(this, dateInfo));
				}
			}
		}
	}

	/**
	 * 現在選択されている日付情報を取得します
	 * 
	 * @return MonthlyCalendarView.DateInfo 選択されている日付情報が戻ります
	 */
	public DateInfo getDateInfo() {
		MonthlyCalendarView currentView = (MonthlyCalendarView) viewFlipper
				.getCurrentView();
		return currentView.getSelectedDateInfo();
	}

	/**
	 * カレンダ選択監視リスナを追加します
	 * 
	 * @param listener
	 *            リスナをセット
	 */
	public void addOnCalendarSelectionListener(
			OnCalendarSelectionListener listener) {
		this.listenerList.add(listener);
	}

	/**
	 * カレンダ選択リスナを取り除きます
	 * 
	 * @param listener
	 *            リスナをセット
	 */
	public void removeOnCalendarSelectionListener(
			OnCalendarSelectionListener listener) {
		this.listenerList.remove(listener);
	}

	/**
	 * 年、月を設定します
	 */
	protected void setYearAndMonthFromDialog() {
		MonthlyCalendarView cView = (MonthlyCalendarView) this.viewFlipper
				.getChildAt(this.viewFlipper.getDisplayedChild());
		final int currentYear = cView.getCalendar().get(Calendar.YEAR);
		final int currentMonth = cView.getCalendar().get(Calendar.MONTH) + 1;// 月は0オリジン

		SukiyaMonthYearDialog dialog = new SukiyaMonthYearDialog(
				this.getContext());
		dialog.setForcusMonth(currentMonth);
		dialog.setForcusYear(currentYear);
		dialog.setListener(new OnSelectCalendarDailogListener() {

			@Override
			public void selectedDate(Dialog dialog, int year, int month, int day) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if (currentYear != year || currentMonth != month) {
					showYearMonth(year, month);
				}
			}
		});
		dialog.show();
	}

	/**
	 * 前月を表示する
	 * 
	 * @param direction
	 *            モーションの方向をセット(DIRECTION_VERTICAL|DIRECTION_HORIZONTAL)
	 */
	private void showPreviousMonth(int direction) {
		int index = this.viewFlipper.getDisplayedChild() == 0 ? 1 : 0;
		MonthlyCalendarView calendarView = (MonthlyCalendarView) this.viewFlipper
				.getChildAt(index);
		if (calendarView != null) {
			// this.calendar.set(Calendar.DATE, null);
			calendarView.setCalendar(this.iniCal, this.calendar);
			Log.i("showPreviousMonth",
					String.valueOf(this.calendar.get(Calendar.DAY_OF_MONTH)));
			this.setHeader(this.calendar.get(java.util.Calendar.YEAR),
					calendar.get(java.util.Calendar.MONTH));
			if (direction == DIRECTION_VERTICAL) {
				this.viewFlipper.setInAnimation(inFromTop);
				this.viewFlipper.setOutAnimation(outToBottom);
			} else {
				this.viewFlipper.setInAnimation(inFromLeft);
				this.viewFlipper.setOutAnimation(outToRight);
			}
			this.viewFlipper.showPrevious();
		}
	}

	/**
	 * 次月を表示する
	 * 
	 * @param direction
	 *            モーションの方向をセット(DIRECTION_VERTICAL|DIRECTION_HORIZONTAL)
	 */
	private void showNextMonth(int direction) {
		int index = this.viewFlipper.getDisplayedChild() == 0 ? 1 : 0;
		MonthlyCalendarView calendarView = (MonthlyCalendarView) this.viewFlipper
				.getChildAt(index);
		if (calendarView != null) {
			calendarView.setCalendar(this.iniCal, this.calendar);
			this.setHeader(this.calendar.get(java.util.Calendar.YEAR),
					calendar.get(java.util.Calendar.MONTH));

			if (direction == DIRECTION_VERTICAL) {
				this.viewFlipper.setInAnimation(inFromBottom);
				this.viewFlipper.setOutAnimation(outToTop);
			} else {
				this.viewFlipper.setInAnimation(inFromRight);
				this.viewFlipper.setOutAnimation(outToLeft);
			}
			this.viewFlipper.showNext();
		}
	}

	/**
	 * 対象の年月を表示します
	 * 
	 * @param year
	 *            年をセット
	 * @param month
	 *            月をセット
	 */
	private void showYearMonth(int year, int month) {
		int currentYear = this.calendar.get(java.util.Calendar.YEAR);
		int currentMonth = this.calendar.get(java.util.Calendar.MONTH) + 1;

		if (year == currentYear && month == currentMonth)
			return;

		this.calendar.set(Calendar.YEAR, year);
		this.calendar.set(Calendar.MONTH, month - 1);
		if (year > currentYear) {
			this.showNextMonth(DIRECTION_VERTICAL);
		} else {
			if (year < currentYear) {
				this.showPreviousMonth(DIRECTION_VERTICAL);
			} else {
				if (month > currentMonth) {
					this.showNextMonth(DIRECTION_VERTICAL);
				} else {
					this.showPreviousMonth(DIRECTION_VERTICAL);
				}
			}
		}
	}

	/**
	 * ヘッダの年月を設定します
	 * 
	 * @param year
	 *            年をセット
	 * @param month
	 *            月をセット
	 */
	private void setHeader(int year, int month) {
		this.txtHeader.setText(year + "年" + monthNames[month]);
	}
}
