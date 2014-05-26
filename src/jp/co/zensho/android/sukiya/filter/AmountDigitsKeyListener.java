
package jp.co.zensho.android.sukiya.filter;

import jp.co.zensho.android.sukiya.common.StringUtils;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;

public class AmountDigitsKeyListener extends DigitsKeyListener {
    private int beforeDecimal = 3;
    private int afterDecimal = 2;

    public AmountDigitsKeyListener() {
        super(false, true);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
            Spanned dest, int dstart, int dend) {
        String temp = dest + source.toString();
        
        if (temp.equals(".")) {
            return "0.";
        }
        else if (temp.toString().indexOf(".") == -1) {
            // no decimal point placed yet
            if (temp.length() > beforeDecimal) {
                return StringUtils.EMPTY;
            }
        } else {
            temp = temp.substring(temp.indexOf(".") + 1);
            if (temp.length() > afterDecimal) {
                return StringUtils.EMPTY;
            }
        }

        return super.filter(source, start, end, dest, dstart, dend);
    }
    
}
