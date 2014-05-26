package jp.co.zensho.android.sukiya.calendar;

import java.util.EventListener;

public interface OnCalendarSelectionListener extends EventListener {
    void onCalendarSelection(CalendarSelectionEvent event);
}
