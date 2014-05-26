package jp.co.zensho.android.sukiya;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class VScrollView extends ScrollView {
    private OnScrollChangedListener listener;
    
    public VScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VScrollView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
    
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (listener != null) {
            listener.onVerticalScrollChanged(this, l, t, oldl, oldt);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }
    
    public void setOnScrollChangedListener(OnScrollChangedListener l) {
        this.listener = l;
    }
}
