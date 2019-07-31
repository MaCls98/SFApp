package com.android.sfapp.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Money extends android.support.v7.widget.AppCompatTextView {

    String rawText;

    public Money(Context context) {
        super(context);

    }

    public Money(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Money(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        rawText = text.toString();
        String prezzo = text.toString();
        try {

            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(',');
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###", symbols);
            prezzo = decimalFormat.format(Integer.parseInt(text.toString()));
        }catch (Exception e){}

        super.setText(prezzo, type);
    }

    @Override
    public CharSequence getText() {

        return rawText;
    }
}
