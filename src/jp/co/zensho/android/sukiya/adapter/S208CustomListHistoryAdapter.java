package jp.co.zensho.android.sukiya.adapter;

import java.util.List;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.bean.API25_ViewHistory;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @author ntdat Class: S208_CustomListItemAdapter
 * 
 *         Define adapter of ListView lv_shipping_history in screen 208 (export)
 * 
 */
public class S208CustomListHistoryAdapter extends BaseAdapter {
	List<API25_ViewHistory> listShippingHistory;
	Context context;

	public S208CustomListHistoryAdapter(Context context,
			List<API25_ViewHistory> listshippinghistory) {
		this.listShippingHistory = listshippinghistory;
		this.context = context;
	}

	@Override
	public int getCount() {
		return listShippingHistory.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listShippingHistory.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return listShippingHistory.indexOf(listShippingHistory.get(arg0));
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;

		if (arg1 == null) {

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			arg1 = mInflater.inflate(R.layout.s208_p_view_history_item, arg2,
					false);
			holder = new ViewHolder();
			holder.txtNo = (TextView) arg1
					.findViewById(R.id.s208_tv_p_history_item_no);
			holder.txtSendDate = (TextView) arg1
					.findViewById(R.id.s208_tv_p_history_item_send_datetime);
			holder.txtShopCode = (TextView) arg1
					.findViewById(R.id.s208_tv_p_history_item_shop_code);
			holder.txtShopName = (TextView) arg1
					.findViewById(R.id.s208_tv_p_history_item_shop_name);
			holder.txtProductName = (TextView) arg1
					.findViewById(R.id.s208_tv_p_history_item_product_name);
			holder.txtQuantity = (TextView) arg1
					.findViewById(R.id.s208_tv_p_history_item_quantity);

			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		API25_ViewHistory historyObj = (API25_ViewHistory) getItem(arg0);
		holder.txtNo.setText(String.valueOf(arg0 + 1));
		holder.txtSendDate.setText(historyObj.getSendDateTime());
		holder.txtShopCode.setText(historyObj.getTenpoCD());
		holder.txtShopName.setText(historyObj.getTenpoName());
		holder.txtProductName.setText(historyObj.getHinName());
		holder.txtQuantity.setText(historyObj.getSendSum() + " "
				+ historyObj.getTaniName());

		return arg1;

	}

	public void updateResults(List<API25_ViewHistory> results) {
		listShippingHistory = results;
		// Update changed
		notifyDataSetChanged();
	}

	private class ViewHolder {
		TextView txtNo;
		TextView txtSendDate;
		TextView txtShopCode;
		TextView txtShopName;
		TextView txtProductName;
		TextView txtQuantity;
	}

}
