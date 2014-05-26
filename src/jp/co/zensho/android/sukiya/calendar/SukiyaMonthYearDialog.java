package jp.co.zensho.android.sukiya.calendar;

import java.util.Calendar;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.common.DateUtils;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SukiyaMonthYearDialog extends Dialog implements
		android.view.View.OnClickListener {
	private OnSelectCalendarDailogListener mListener;
	private Calendar mMaxDate;
	private int mCurrentSelectYear = 0;
	private int mCurrentSelectMonth = 0;
	private Context mContext;
	private int mCurrentBrowseYear = 0;

	private TextView mTxtYear;
	private Button[] mBtnMonth;

	public SukiyaMonthYearDialog(Context context) {
		super(context);
		mContext = context;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_month_year);

		mTxtYear = (TextView) findViewById(R.id.date_picker_year_txt);

		Button increase1 = (Button) findViewById(R.id.dialog_mon_year_previous_year);
		Button decrease1 = (Button) findViewById(R.id.dialog_mon_year_next_year);
		mBtnMonth = new Button[12];
		mBtnMonth[0] = (Button) findViewById(R.id.button1);
		mBtnMonth[1] = (Button) findViewById(R.id.button2);
		mBtnMonth[2] = (Button) findViewById(R.id.button3);
		mBtnMonth[3] = (Button) findViewById(R.id.button4);
		mBtnMonth[4] = (Button) findViewById(R.id.button5);
		mBtnMonth[5] = (Button) findViewById(R.id.button6);
		mBtnMonth[6] = (Button) findViewById(R.id.button7);
		mBtnMonth[7] = (Button) findViewById(R.id.button8);
		mBtnMonth[8] = (Button) findViewById(R.id.button9);
		mBtnMonth[9] = (Button) findViewById(R.id.button10);
		mBtnMonth[10] = (Button) findViewById(R.id.button11);
		mBtnMonth[11] = (Button) findViewById(R.id.button12);

		increase1.setOnClickListener(this);
		decrease1.setOnClickListener(this);

		for (int i = 0; i < 12; i++) {
			mBtnMonth[i].setOnClickListener(this);
		}
		if (mCurrentSelectYear == 0) {
			mCurrentSelectYear = Calendar.getInstance().get(Calendar.YEAR);
			mCurrentBrowseYear = mCurrentSelectYear;
		}
		if (mCurrentSelectMonth == 0) {
			mCurrentSelectMonth = Calendar.getInstance().get(Calendar.MONTH);
		}
		// updateMonthYear();
		setUpMonthButtons();
	}

	private void setUpMonthButtons() {
		String monthStr;
		for (int i = 0; i < 12; i++) {
			monthStr = String.valueOf(i + 1);
			monthStr += mContext.getResources().getString(R.string.month);
			mBtnMonth[i].setText(monthStr);
		}

	}

	public void setForcusMonth(int month) {
		mCurrentSelectMonth = month;
	};

	public void setListener(OnSelectCalendarDailogListener listener) {
		this.mListener = listener;
	}

	public void setForcusCurrentYear() {
		Calendar cal = DateUtils.today();
		setForcusYear(cal.get(Calendar.YEAR));
	}

	public void setForcusYear(int year) {
		if (year > 0 && year < 2100) {
			mCurrentSelectYear = year;
			mCurrentBrowseYear = mCurrentSelectYear;
		}
		updateMonthYearState();
	}

	public void setTodayToMaxDate() {
		mMaxDate = DateUtils.today();
	}

	public void setMaxDate(int year, int month) {
		mMaxDate = DateUtils.today();
		mMaxDate.set(Calendar.YEAR, year);
		mMaxDate.set(Calendar.MONTH, month);
	}

	private void updateMonthButtonState() {
		int maxMonthActive = 12;

		if (mMaxDate != null) {
			if (mCurrentSelectYear == mMaxDate.get(Calendar.YEAR)) {
				maxMonthActive = mMaxDate.get(Calendar.MONTH);
			}
		}
		for (int i = 0; i < 12; i++) {
			if (i <= maxMonthActive) {
				mBtnMonth[i].setEnabled(true);
			} else {
				mBtnMonth[i].setEnabled(false);
			}
		}
	}

	private void updateMonthYearState() {
		updateYear();
		if (mCurrentSelectYear == mCurrentBrowseYear) {
			mBtnMonth[mCurrentSelectMonth - 1].setTextColor(mContext
					.getResources().getColor(R.color.white));
			mBtnMonth[mCurrentSelectMonth - 1].setBackgroundColor(mContext
					.getResources().getColor(R.color.green));
		} else {
			mBtnMonth[mCurrentSelectMonth - 1].setTextColor(mContext
					.getResources().getColor(R.color.black));
			mBtnMonth[mCurrentSelectMonth - 1].setBackgroundColor(mContext
					.getResources().getColor(R.color.white));
		}
	}

	private void updateYear() {
		String monthText = mContext.getResources().getString(R.string.year);
		String str = String.format("%s" + monthText, mCurrentSelectYear);
		mTxtYear.setText(String.valueOf(str));
		updateMonthButtonState();
	}

	private void selectMonth(int month) {
		if (mListener != null) {
			mListener.selectedDate(this, mCurrentSelectYear, month, 1);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_mon_year_next_year:
			if (mCurrentSelectYear < 2099) {
				mCurrentSelectYear++;
				if (mMaxDate != null) {
					if (mMaxDate.get(Calendar.YEAR) < mCurrentSelectYear) {
						mCurrentSelectYear = mMaxDate.get(Calendar.YEAR);
					}
				}
				updateMonthYearState();
				updateMonthButtonState();
			}
			break;
		case R.id.dialog_mon_year_previous_year:
			mCurrentSelectYear--;
			if (mCurrentSelectYear < 0) {
				mCurrentSelectYear = 0;
			}
			updateMonthYearState();
			updateMonthButtonState();
			break;
		case R.id.button1:
			selectMonth(1);
			break;
		case R.id.button2:
			selectMonth(2);
			break;
		case R.id.button3:
			selectMonth(3);
			break;
		case R.id.button4:
			selectMonth(4);
			break;
		case R.id.button5:
			selectMonth(5);
			break;
		case R.id.button6:
			selectMonth(6);
			break;
		case R.id.button7:
			selectMonth(7);
			break;
		case R.id.button8:
			selectMonth(8);
			break;
		case R.id.button9:
			selectMonth(9);
			break;
		case R.id.button10:
			selectMonth(10);
			break;
		case R.id.button11:
			selectMonth(11);
			break;
		case R.id.button12:
			selectMonth(12);
			break;
		default:
			break;
		}
	}
}
