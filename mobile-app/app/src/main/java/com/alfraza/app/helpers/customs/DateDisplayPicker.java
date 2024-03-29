package com.alfraza.app.helpers.customs;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import com.alfraza.app.ActivityRecycler;

public class DateDisplayPicker extends AppCompatTextView implements DatePickerDialog.OnDateSetListener {

    private Context _context;

    public DateDisplayPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _context = context;
    }

    public DateDisplayPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        setAttributes();
    }

    public DateDisplayPicker(Context context) {
        super(context);
        _context = context;
        setAttributes();
    }

    private void setAttributes() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });
    }

    private void showDateDialog() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(_context, this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        setText(String.format("%s/%s/%s", dayOfMonth, monthOfYear + 1, year));
        if (_context instanceof ActivityRecycler) {
            ((ActivityRecycler) _context).setDates();
        }
    }
}