package jp.co.zensho.android.sukiya.calendar;

import android.app.Dialog;

public interface OnSelectCalendarDailogListener {
    void selectedDate(Dialog dialog, int year, int month, int day);
}
