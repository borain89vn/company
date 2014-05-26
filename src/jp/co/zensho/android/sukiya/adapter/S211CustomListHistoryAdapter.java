package jp.co.zensho.android.sukiya.adapter;

import java.util.List;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.bean.API29_ViewHistory;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @author ntdat
 * 
 *         Class: S211_CustomListItemAdapter
 * 
 *         Define adapter of ListView lv_acceptance_history in screen 211
 * 
 * 
 */
public class S211CustomListHistoryAdapter extends BaseAdapter {
	List<API29_ViewHistory> listAcceptanceHistory;
	Context context;

	public S211CustomListHistoryAdapter(Context context,
			List<API29_ViewHistory> listacceptancehistory) {
		this.listAcceptanceHistory = listacceptancehistory;
		this.context = context;
	}

	@Override
	public int getCount() {
		return listAcceptanceHistory.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listAcceptanceHistory.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return listAcceptanceHistory.indexOf(listAcceptanceHistory.get(arg0));
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;

		if (arg1 == null) {

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			arg1 = mInflater.inflate(R.layout.s211_p_view_history_item, arg2,
					false);
			holder = new ViewHolder();
			holder.txtNo = (TextView) arg1
					.findViewById(R.id.s211_tv_p_history_item_no);
			holder.txtDataDate = (TextView) arg1
					.findViewById(R.id.s211_tv_p_history_item_data_datetime);
			holder.txtShopCode = (TextView) arg1
					.findViewById(R.id.s211_tv_p_history_item_shop_code);
			holder.txtShopName = (TextView) arg1
					.findViewById(R.id.s211_tv_p_history_item_shop_name);
			holder.txtProductName = (TextView) arg1
					.findViewById(R.id.s211_tv_p_history_item_product_name);
			holder.txtQuantity = (TextView) arg1
					.findViewById(R.id.s211_tv_p_history_item_quantity);

			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		API29_ViewHistory historyObj = (API29_ViewHistory) getItem(arg0);
		holder.txtNo.setText(String.valueOf(arg0 + 1));
		holder.txtDataDate.setText(historyObj.getDataDateTime());
		holder.txtShopCode.setText(historyObj.getTenpoCD());
		holder.txtShopName.setText(historyObj.getTenpoName());
		holder.txtProductName.setText(historyObj.getHinName());
		holder.txtQuantity.setText(historyObj.getRcvSum() + " "
				+ historyObj.getTaniName());
		return arg1;

	}

	public void updateResults(List<API29_ViewHistory> results) {
		listAcceptanceHistory = results;
		// Update changed
		notifyDataSetChanged();
	}

	private class ViewHolder {
		TextView txtNo;
		TextView txtDataDate;
		TextView txtShopCode;
		TextView txtShopName;
		TextView txtProductName;
		TextView txtQuantity;
	}

}
