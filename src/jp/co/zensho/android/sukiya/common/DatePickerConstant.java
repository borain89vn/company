package jp.co.zensho.android.sukiya.common;

import java.lang.reflect.Field;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;

/**
 * 
 * @author ntdat
 * 
 *         Class: DatePickerConstant
 * 
 *         Define custom date picker: limit max date and just show month and
 *         year (without day)
 */
public class DatePickerConstant {
	public static int year, month, day;

	/**
	 * Show dialog date picker
	 */
	public static DatePickerDialog customDatePicker(Context context,
			long maxdate, DatePickerDialog.OnDateSetListener datePickerListener) {
		DatePickerDialog dpd = new DatePickerDialog(context,
				datePickerListener, year, month, day);
		dpd.getDatePicker().setMaxDate(maxdate);
		try {
			Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
			for (Field datePickerDialogField : datePickerDialogFields) {
				if (datePickerDialogField.getName().equals("mDatePicker")) {
					datePickerDialogField.setAccessible(true);
					DatePicker datePicker = (DatePicker) datePickerDialogField
							.get(dpd);
					Field datePickerFields[] = datePickerDialogField.getType()
							.getDeclaredFields();
					for (Field datePickerField : datePickerFields) {
						if ("mDayPicker".equals(datePickerField.getName())
								|| "mDaySpinner".equals(datePickerField
										.getName())) {
							datePickerField.setAccessible(true);
							Object dayPicker = new Object();
							dayPicker = datePickerField.get(datePicker);
							((View) dayPicker).setVisibility(View.GONE);
						}
					}
				}
			}
		} catch (Exception ex) {
		}
		return dpd;
	}

	public static void setCurrentDateOnView() {
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
	}

}
