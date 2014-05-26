package jp.co.zensho.android.sukiya.common;

import jp.co.zensho.android.sukiya.R;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * Class is used for appending String such as "日" "円" "店" "件"
 * 
 * @author ltthuc
 *
 */
public class UnitSpanable {
    
	private static UnitSpanable instance = null;
	public UnitSpanable () {
		
	}
	public static UnitSpanable getInstance() {
		if (instance == null) {
			instance =new UnitSpanable();
		}
		return instance;
	}
	public void setSpan(Context context,TextView textView, String unit, float size) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		SpannableString spString = new SpannableString(unit);
		spString.setSpan(new TextAppearanceSpan(context, R.style.unit), 0,
				unit.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spString.setSpan(new RelativeSizeSpan(size), 0, unit.length(), 0);
		builder.append(textView.getText());
		builder.append(spString);
		textView.setText(builder, BufferType.SPANNABLE);

	}
}
