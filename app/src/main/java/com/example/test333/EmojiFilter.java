package com.example.test333;

import android.text.InputFilter;
import android.text.Spanned;

public class EmojiFilter {
    public static InputFilter[] getFilter()
    {
        InputFilter EMOJI_FILTER = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int index = start; index < end; index++) {

                    int type = Character.getType(source.charAt(index));

                    if (type == Character.SURROGATE || type==Character.NON_SPACING_MARK
                            || type==Character.OTHER_SYMBOL) {
                        return "";
                    }
                }
                return null;
            }
        };
        return new InputFilter[]{EMOJI_FILTER};
    }
}
