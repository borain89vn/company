package jp.co.zensho.android.sukiya;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

public class PagerHorizontalScrollView extends HorizontalScrollView {
	private int sCurrentPage = 0;
    private int sTotalPage = 0;

	// 現在ページ数を表示するための TextView
	private TextView mTextViewCurrentPage = null;
    private TextView mTextViewTotalPage = null;

	// １ページあたりの幅
	private static final int cPageWidth = 1200;

	public PagerHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public PagerHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PagerHorizontalScrollView(Context context) {
		super(context);
	}

	public void setTextViewCurrentPage(TextView v) {
		mTextViewCurrentPage = v;
	}

    public void setTextViewTotalPage(TextView v) {
        mTextViewTotalPage = v;
    }

	@Override
	public void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		int offset = this.computeHorizontalScrollOffset();
		int range = this.computeHorizontalScrollRange();

        int currentPage = 0;
        int totalPage = 0;
        if (this.computeHorizontalScrollRange() <= 23) {
            currentPage = 0;
            totalPage = 0;
        } else {
            currentPage = offset / cPageWidth + 1;
            totalPage = range / cPageWidth + 1;
        }

		if (sCurrentPage != currentPage) {
			//ページ数が変わったときだけ描画する
			sCurrentPage = currentPage;
			if (mTextViewCurrentPage != null) {
				mTextViewCurrentPage.setText(String.valueOf(currentPage));
			}
		}

        if (sTotalPage != totalPage) {
            //ページ数が変わったときだけ描画する
            sTotalPage = totalPage;
            if (mTextViewTotalPage != null) {
                mTextViewTotalPage.setText(String.valueOf(sTotalPage));
            }
        }

		// Log.d("PagerHorizontalScrollView", "offset = " + offset +
		// ", range = " + scrollRange + ", width = " + width + ", toend = " +
		// toend);

	}

}
