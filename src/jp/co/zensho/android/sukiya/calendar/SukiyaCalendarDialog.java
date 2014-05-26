package jp.co.zensho.android.sukiya.calendar;

import java.util.Calendar;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.common.DateUtils;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;

public class SukiyaCalendarDialog extends Dialog implements
		OnCalendarSelectionListener {
	private CalendarView calendarView;
	private OnSelectCalendarDailogListener listener;

	public SukiyaCalendarDialog(Context context) {
		super(context);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_calendar);

		calendarView = (CalendarView) findViewById(R.id.Calendar);
		calendarView.addOnCalendarSelectionListener(this);
	}

	@Override
	public void onCalendarSelection(CalendarSelectionEvent event) {
		if (this.listener != null) {
			DateInfo date = event.getDateInfo();
			this.listener.selectedDate(this, date.year, date.month, date.day);
		}
	}

	public void setDate(int year, int month, int day) {
		Calendar cal = DateUtils.today();
		if (year < 0 || year > 2099) {
			year = cal.get(Calendar.YEAR);
		}
		if (month <= 0 || month > 12) {
			month = cal.get(Calendar.MONTH) + 1;
		}
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		if (day <= 0 || day > lastDayOfMonth) {
			day = cal.get(Calendar.DAY_OF_MONTH);
		}
		cal.set(Calendar.DAY_OF_MONTH, day);
		this.calendarView.setCalandar(cal,cal);
	}

	public void setListener(OnSelectCalendarDailogListener listener) {
		this.listener = listener;
	}
}
