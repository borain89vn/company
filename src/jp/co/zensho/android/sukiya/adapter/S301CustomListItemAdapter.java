package jp.co.zensho.android.sukiya.adapter;

import java.util.ArrayList;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.bean.ProductExpirationInfo;
import jp.co.zensho.android.sukiya.bean.TagInfo;
import jp.co.zensho.android.sukiya.common.StringUtils;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class S301CustomListItemAdapter extends
		ArrayAdapter<ProductExpirationInfo> {
	private final Context context;
	private ArrayList<ProductExpirationInfo> itemsArrayList;
	// private ToggleButton btnButton2;
	private boolean isdisplayalldata = true;

	public S301CustomListItemAdapter(Context context,
			ArrayList<ProductExpirationInfo> itemsArrayList,
			boolean isDisplayAllData) {

		super(context, R.layout.s301_list_item, itemsArrayList);

		this.context = context;
		this.itemsArrayList = itemsArrayList;
		this.isdisplayalldata = isDisplayAllData;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		// 1. Create inflater
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// View rowView = null;
		Log.i("check ",
				String.valueOf(isdisplayalldata
						|| itemsArrayList.get(position).getNohin_su1() != null));

		Log.i("check ", String.valueOf(isdisplayalldata));

		Log.i("check ", String.valueOf(itemsArrayList.get(position)
				.getNohin_su1() != null));

		if (isdisplayalldata
				|| itemsArrayList.get(position).getNohin_su1() != null) {

			if (convertView == null || convertView.getTag() == null) {
				viewHolder = new ViewHolder();

				convertView = inflater.inflate(R.layout.s301_list_item, parent,
						false);
				viewHolder.txtproductNumber = (TextView) convertView
						.findViewById(R.id.s301_tv_list_item_number);

				viewHolder.txtproductname = (TextView) convertView
						.findViewById(R.id.s301_tv_item_list_product_name);
				// viewHolder.imageView = (ImageView) convertView
				// .findViewById(R.id.s301_list_item_product_image);
				viewHolder.txtUnit = (TextView) convertView
						.findViewById(R.id.s301_tv_item_unit);

				viewHolder.txtItemPerStock = (TextView) convertView
						.findViewById(R.id.s301_tv_item_list_per_stock);
				viewHolder.txtItemSum = (TextView) convertView
						.findViewById(R.id.s301_tv_item_list_zaiko_su);
				viewHolder.txtItemNohin1 = (TextView) convertView
						.findViewById(R.id.s301_tv_item_list_nohin_su1);

				viewHolder.txtItemNohin2 = (TextView) convertView
						.findViewById(R.id.s301_tv_item_list_nohin_su2);
				viewHolder.txtItemNohin3 = (TextView) convertView
						.findViewById(R.id.s301_tv_item_list_nohin_su3);
				viewHolder.btnButton1 = (Button) convertView
						.findViewById(R.id.s301_list_item_product_button);
				viewHolder.btnButton2 = (ToggleButton) convertView
						.findViewById(R.id.s301_list_item_product_button2);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			Log.e("test null",
					String.valueOf(viewHolder) + " " + String.valueOf(position));

			viewHolder.btnButton1.setTag(new TagInfo(TagInfo.NUMBER_TAG,
					position));

			viewHolder.btnButton2
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {

							if (isChecked == false) {
								itemsArrayList.get(position)
										.setCheckToggleButton(false);

							} else {
								itemsArrayList.get(position)
										.setCheckToggleButton(true);
							}

						}
					});

			viewHolder.txtproductNumber.setText(String.valueOf(position + 1));
			viewHolder.txtproductname.setText(itemsArrayList.get(position)
					.getHinName());
			// imageView.setImageResource(R.drawable.input_item1_ocha);
			// SukiyaUtils.setImage(context, viewHolder.imageView,
			// itemsArrayList
			// .get(position).getName() + ".jpg", R.drawable.no_image_164);

			viewHolder.txtUnit.setText(itemsArrayList.get(position)
					.getTaniName());

			if (itemsArrayList.get(position).getPer_stock() != null) {
				viewHolder.txtItemPerStock.setText(itemsArrayList.get(position)
						.getPer_stock().toString());
			} else {
				viewHolder.txtItemPerStock.setText(StringUtils.EMPTY);
			}

			if (itemsArrayList.get(position).getZaikosu() != null) {
				viewHolder.txtItemSum.setText(itemsArrayList.get(position)
						.getZaikosu().toString());
			} else {
				viewHolder.txtItemSum.setText(StringUtils.EMPTY);
			}

			if (itemsArrayList.get(position).getNohin_su1() != null) {
				viewHolder.txtItemNohin1.setText(itemsArrayList.get(position)
						.getNohin_su1().toString());
			} else {
				viewHolder.txtItemNohin1.setText(StringUtils.EMPTY);
			}

			if (itemsArrayList.get(position).getNohin_su2() != null) {
				viewHolder.txtItemNohin2.setText(itemsArrayList.get(position)
						.getNohin_su2().toString());
			} else {
				viewHolder.txtItemNohin2.setText(StringUtils.EMPTY);
			}

			if (itemsArrayList.get(position).getNohin_su3() != null) {
				viewHolder.txtItemNohin3.setText(itemsArrayList.get(position)
						.getNohin_su3().toString());
			} else {
				viewHolder.txtItemNohin3.setText(StringUtils.EMPTY);

			}

			if (viewHolder.txtItemNohin1.getText() == null
					|| viewHolder.txtItemNohin1.getText().equals(
							StringUtils.EMPTY)) {
				viewHolder.btnButton1.setVisibility(View.INVISIBLE);
				viewHolder.btnButton2.setVisibility(View.INVISIBLE);
			} else {
				viewHolder.btnButton1.setVisibility(View.VISIBLE);
				viewHolder.btnButton2.setVisibility(View.VISIBLE);
			}

			// Retain check in listview on scroll
			if (itemsArrayList.get(position).isCheckToggleButton() == true) {

				viewHolder.btnButton2.setChecked(true);
			} else {
				viewHolder.btnButton2.setChecked(false);
			}

		} else {
			// // Add empty row in the listView to maintain position of
			// list-view
			// // and product list is the same
			// viewHolder = (ViewHolder) convertView.getTag();
			convertView = inflater.inflate(R.layout.s301_list_item_empty_view,
					parent, false);
			convertView.setTag(null);
		}

		return convertView;
	}

	static class ViewHolder {
		TextView txtproductNumber;
		TextView txtproductname;
		// ImageView imageView;
		TextView txtUnit;
		TextView txtItemPerStock;
		TextView txtItemSum;
		TextView txtItemNohin1;
		TextView txtItemNohin2;
		TextView txtItemNohin3;
		Button btnButton1;
		ToggleButton btnButton2;
	}
}
