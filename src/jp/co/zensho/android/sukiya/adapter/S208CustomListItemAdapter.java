package jp.co.zensho.android.sukiya.adapter;

import java.util.List;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.S208Activity;
import jp.co.zensho.android.sukiya.bean.S208_ListShippingReport;
import jp.co.zensho.android.sukiya.common.SukiyaUtils;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author ntdat
 * 
 *         Date: 03/19/2014
 * 
 *         Class: S208_CustomListItemAdapter
 * 
 *         Define adapter of ListView lv_shipping_report in screen 208
 * 
 */
public class S208CustomListItemAdapter extends BaseAdapter {
	private List<S208_ListShippingReport> listShippingReport;
	private Context context;
	private S208Activity mActivity;

	public S208CustomListItemAdapter(Context context,
			List<S208_ListShippingReport> listshippingreport) {
		this.listShippingReport = listshippingreport;
		this.context = context;
	}

	public S208CustomListItemAdapter(Context context,
			List<S208_ListShippingReport> listshippingreport,
			S208Activity activity) {
		this.listShippingReport = listshippingreport;
		this.context = context;
		this.mActivity = activity;
	}

	@Override
	public int getCount() {
		return listShippingReport.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listShippingReport.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return listShippingReport.indexOf(listShippingReport.get(arg0));
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;

		if (arg1 == null) {

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			arg1 = mInflater.inflate(R.layout.s208_list_item, arg2, false);
			holder = new ViewHolder();
			holder.txtNo = (TextView) arg1
					.findViewById(R.id.s208_tv_list_item_no);
			holder.txtShopName = (TextView) arg1
					.findViewById(R.id.s208_tv_list_item_shop);
			holder.imgProductName = (ImageView) arg1
					.findViewById(R.id.s208_img_list_item_product);
			holder.txtProductName = (TextView) arg1
					.findViewById(R.id.s208_tv_list_item_name);
			holder.txtQuantity = (TextView) arg1
					.findViewById(R.id.s208_tv_list_item_quantity);
			holder.llDelete = (LinearLayout) arg1
					.findViewById(R.id.s208_ll_list_item_delete);
			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		final S208_ListShippingReport s208ListObj = (S208_ListShippingReport) getItem(arg0);
		holder.txtNo.setText(String.valueOf(arg0 + 1));
		holder.txtShopName.setText(s208ListObj.getShopName());
		SukiyaUtils.setImage(context, holder.imgProductName,
				s208ListObj.getHinCD() + ".jpg", R.drawable.no_image_85);
		holder.txtProductName.setText(s208ListObj.getHinName());

		StringBuilder builder = new StringBuilder();
		builder.append("<u>");
		builder.append(s208ListObj.getSndSum());
		builder.append(" ");
		builder.append(s208ListObj.getTaniName());
		builder.append("</u>");
		holder.txtQuantity.setText(Html.fromHtml(builder.toString()));

		holder.txtQuantity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mActivity.nPosition = arg0;
				mActivity.isListItemNumberClicked = true;
				mActivity.etPopupQuantityAmount.setText(s208ListObj.getSndSum());
				mActivity.tvPopupQuantityUnit.setText(s208ListObj.getTaniName());
				SukiyaUtils.setImage(context, mActivity.imgPopupQuantityImage,
						s208ListObj.getHinCD() + ".jpg",
						R.drawable.no_image_191);
				mActivity.tvPopupQuantiyProductName.setText(s208ListObj
						.getHinName());
				mActivity.callAPI017(s208ListObj.getHinCD());

			}
		});

		holder.llDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listShippingReport.remove(arg0);
				notifyDataSetChanged();

				if (listShippingReport.size() <= 0) {
					mActivity.btnInputPartCode.setEnabled(false);
				}

				if (listShippingReport.size() < 5) {
					mActivity.llDefaultBlank.setVisibility(View.VISIBLE);
				}
			}
		});
		return arg1;

	}

	public void updateResults(List<S208_ListShippingReport> results) {
		listShippingReport = results;
		// Update changed
		notifyDataSetChanged();
	}

	private class ViewHolder {
		TextView txtNo;
		TextView txtShopName;
		ImageView imgProductName;
		TextView txtProductName;
		TextView txtQuantity;
		LinearLayout llDelete;
	}

}
