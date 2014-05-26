package jp.co.zensho.android.sukiya.calendar;

import java.util.EventObject;

@SuppressWarnings("serial")
public class CalendarSelectionEvent extends EventObject {
    protected CalendarView calendarView;
    protected DateInfo dateInfo;

    public CalendarSelectionEvent(CalendarView calendarView
            , DateInfo dateInfo) {
        super(calendarView);
        this.calendarView = calendarView;
        this.dateInfo = dateInfo;
    }

    public CalendarView getCalendarView() {
        return this.calendarView;
    }

    public DateInfo getDateInfo() {
        return this.dateInfo;
    }
}
