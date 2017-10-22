package offek.com.showapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Shows off the most basic usage
 */
public class CalendarActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    @Bind(R.id.calendarView)
    MaterialCalendarView widget;
    View btnSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);

        btnSelect = findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.date1 = null;
                HomeActivity.date2 = null;
                //Log.v("Date:",getSelectedDatesString());

                if (widget.getSelectedDates().size() > 1) {
                    Log.v("Calendar","has 2 dates");
                    HomeActivity.date1 = widget.getSelectedDates().get(0);
                    HomeActivity.date2 = widget.getSelectedDates().get(1);
                    //HomeActivity.updateSearchWithDates(date1,date2,false);
                }
                else if (widget.getSelectedDates().size() == 1) {
                    Log.v("Calendar","has 1 date");
                    HomeActivity.date1 = widget.getSelectedDates().get(0);
                    /*if (date1.getDate().equals(CalendarDay.today().getDate()))
                        HomeActivity.updateSearchWithDates(date1,null,true);
                    else
                        HomeActivity.updateSearchWithDates(date1,null,false);*/
                }
                else {
                    HomeActivity.date1 = null;
                    HomeActivity.date2 = null;
                    //HomeActivity.updateSearchWithDates(date,null,true);
                }

                Intent intent = new Intent();
                setResult(RESULT_FIRST_USER,intent );
                finish();
            }
        });

        Calendar c=new GregorianCalendar();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
        c.add(Calendar.DATE, 90);
        Date d=c.getTime();
        widget.state().edit()
                .setMinimumDate(CalendarDay.today())
                .setMaximumDate(d)
                .commit();
        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);
        widget.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        //textView.setText(getSelectedDatesString());
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        //noinspection ConstantConditions
        //getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));
    }

    /*private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDates().get(0);
        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());

    }*/
}
