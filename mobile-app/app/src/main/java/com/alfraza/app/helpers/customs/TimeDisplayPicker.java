package com.alfraza.app.helpers.customs;

import android.app.TimePickerDialog;
import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

public class TimeDisplayPicker extends AppCompatTextView implements TimePickerDialog.OnTimeSetListener {

    private Context _context;

    public TimeDisplayPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _context = context;
    }

    public TimeDisplayPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        setAttributes();
    }

    public TimeDisplayPicker(Context context) {
        super(context);
        _context = context;
        setAttributes();
    }

    private void setAttributes() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog();
            }
        });
    }

    private void showTimeDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog tp = new TimePickerDialog(_context, this, hour, minute, false);
        tp.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int hour = hourOfDay;
        String AM_PM;
        if (hour < 12) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
            hour = hour - 12;
        }
        setText(String.format(Locale.ENGLISH, "%d:%d %s", hour, minute, AM_PM));
    }
}