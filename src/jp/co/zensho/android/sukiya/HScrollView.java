package jp.co.zensho.android.sukiya;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class HScrollView extends HorizontalScrollView {
	private OnScrollChangedListener listener;

	public HScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public HScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HScrollView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return false;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (listener != null) {
			listener.onHorizontalScrollChanged(this, l, t, oldl, oldt);
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}

	public void setOnScrollChangedListener(OnScrollChangedListener l) {
		this.listener = l;
	}
}
