package jp.co.zensho.android.sukiya.adapter;

import java.util.List;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.bean.API21_ViewHistory;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @author ntdat Class: S205_CustomListItemAdapter
 * 
 *         Define adapter of ListView lv_short_report in screen 205
 * 
 */
public class S205CustomListHistoryAdapter extends BaseAdapter {
	List<API21_ViewHistory> listShortHistory;
	Context context;

	public S205CustomListHistoryAdapter(Context context,
			List<API21_ViewHistory> listshorthistory) {
		this.listShortHistory = listshorthistory;
		this.context = context;
	}

	@Override
	public int getCount() {
		return listShortHistory.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listShortHistory.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return listShortHistory.indexOf(listShortHistory.get(arg0));
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;

		if (arg1 == null) {

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			arg1 = mInflater.inflate(R.layout.s205_p_view_history_item, arg2,
					false);
			holder = new ViewHolder();
			holder.txtNo = (TextView) arg1
					.findViewById(R.id.s205_tv_p_history_item_no);
			holder.txtSubmitDate = (TextView) arg1
					.findViewById(R.id.s205_tv_p_history_item_submit_date);
			holder.txtProductName = (TextView) arg1
					.findViewById(R.id.s205_tv_p_history_item_hin_name);
			holder.txtShortReason = (TextView) arg1
					.findViewById(R.id.s205_tv_p_history_item_short_reason);
			holder.txtHour = (TextView) arg1
					.findViewById(R.id.s205_tv_p_history_item_hour);

			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		API21_ViewHistory historyObj = (API21_ViewHistory) getItem(arg0);
		holder.txtNo.setText(String.valueOf(arg0 + 1));
		holder.txtSubmitDate.setText(historyObj.getSubmitDate());
		holder.txtProductName.setText(historyObj.getHinName());
		holder.txtShortReason.setText(historyObj.getShortReason());
		holder.txtHour.setText(historyObj.getHour());
		return arg1;

	}

	public void updateResults(List<API21_ViewHistory> results) {
		listShortHistory = results;
		// Update changed
		notifyDataSetChanged();
	}

	private class ViewHolder {
		TextView txtNo;
		TextView txtSubmitDate;
		TextView txtProductName;
		TextView txtShortReason;
		TextView txtHour;
	}

}
