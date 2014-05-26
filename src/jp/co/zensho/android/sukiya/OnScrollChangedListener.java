package jp.co.zensho.android.sukiya;

import android.view.View;

public interface OnScrollChangedListener {
    void onHorizontalScrollChanged(View view, int l, int t, int oldl, int oldt);
    void onVerticalScrollChanged(View view, int l, int t, int oldl, int oldt);
}
