/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 3/9/19 5:42 PM
 *
 */

package myschoolapp.com.gsnedutech.Util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import myschoolapp.com.gsnedutech.R;


/**
 * Created by ankititjunkies on 20/03/18.
 */

public class MonthYearPickerDialog extends DialogFragment {

    private static final int MAX_YEAR = 2099;
    private DatePickerDialog.OnDateSetListener listener;

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Calendar cal = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.dialog_month_year_picker, null);
        final NumberPicker monthPicker = dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = dialog.findViewById(R.id.picker_year);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setDisplayedValues( new String[] { "Jan", "Feb", "Mar", "Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"} );
        monthPicker.setValue(cal.get(Calendar.MONTH) + 1);

        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(2000);
        yearPicker.setMaxValue(2100);
        yearPicker.setValue(year);

        dialog.findViewById(R.id.ic_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonthYearPickerDialog.this.getDialog().cancel();
            }
        });
        dialog.findViewById(R.id.tv_proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
                MonthYearPickerDialog.this.getDialog().cancel();
            }
        });

        builder.setView(dialog);
        return builder.create();
    }
}