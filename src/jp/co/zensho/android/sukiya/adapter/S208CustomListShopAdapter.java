package jp.co.zensho.android.sukiya.adapter;

import java.util.List;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.S208Activity;
import jp.co.zensho.android.sukiya.bean.Shop;
import jp.co.zensho.android.sukiya.common.StringUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author ntdat
 * 
 *         Date: 03/19/2014
 * 
 *         Class: S208_CustomListShopAdapter
 * 
 *         Define adapter of ListView lv_right at pop-up shop search in screen
 *         208
 * 
 * 
 */
public class S208CustomListShopAdapter extends BaseAdapter {
	private List<Shop> listShop;
	private Context context;
	private S208Activity mActivity;

	public S208CustomListShopAdapter(Context context,
			List<Shop> listshippinghistory) {
		this.listShop = listshippinghistory;
		this.context = context;
	}

	public S208CustomListShopAdapter(Context context,
			List<Shop> listshippinghistory, S208Activity activity) {
		this.listShop = listshippinghistory;
		this.context = context;
		this.mActivity = activity;
	}

	@Override
	public int getCount() {
		return listShop.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listShop.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return listShop.indexOf(listShop.get(arg0));
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;

		if (arg1 == null) {

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			arg1 = mInflater.inflate(R.layout.s208_p_shop_item, arg2, false);
			holder = new ViewHolder();
			holder.txtNo = (TextView) arg1
					.findViewById(R.id.s208_tv_p_shop_item_no);
			holder.txtShopCode = (TextView) arg1
					.findViewById(R.id.s208_tv_p_shop_item_shop_code);
			holder.txtShopName = (TextView) arg1
					.findViewById(R.id.s208_tv_p_shop_item_shop_name);
			holder.btnChoose = (Button) arg1
					.findViewById(R.id.s208_btn_p_shop_item_choose);

			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		final Shop shopObj = (Shop) getItem(arg0);
		holder.txtNo.setText(String.valueOf(arg0 + 1));
		holder.txtShopCode.setText(shopObj.getTenpoCD());
		holder.txtShopName.setText(shopObj.getTenpoName());
		holder.btnChoose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mActivity.tvShopSel.setText(shopObj.getTenpoName());
				mActivity.strShopCode = shopObj.getTenpoCD();
				if (!StringUtils.EMPTY.equals(mActivity.tvShopSel.getText()
						.toString())) {
					mActivity.setControlEnabled(mActivity.rlProductCatSel,
							mActivity.tvProductCatSel,
							mActivity.imgProductCatSel, true);
				}
				closePopup();
			}
		});

		return arg1;

	}

	public void updateResults(List<Shop> results) {
		listShop = results;
		// Update changed
		notifyDataSetChanged();
	}

	private void closePopup() {
		// Close pop-up shop search
		AlphaAnimation alpha_shop_search_close = new AlphaAnimation(1.0f, 0.0f);
		alpha_shop_search_close.setDuration(350);
		mActivity.llPopupShopSearch.startAnimation(alpha_shop_search_close);
		mActivity.llPopupShopSearch.setVisibility(View.INVISIBLE);
		
		mActivity.hideKeyboard(mActivity.etPopupShopSearchKeyInput);
	}

	private class ViewHolder {
		TextView txtNo;
		TextView txtShopCode;
		TextView txtShopName;
		Button btnChoose;
	}

}
