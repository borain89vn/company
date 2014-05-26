package jp.co.zensho.android.sukiya.adapter;

import java.util.List;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.bean.API18_ViewHistory;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @author ntdat Class: S201_CustomListItemAdapter
 * 
 *         Define adapter of ListView lv_loss_report in screen 201
 * 
 */
public class S201CustomListHistoryAdapter extends BaseAdapter {
	List<API18_ViewHistory> listLossHistory;
	Context context;

	public S201CustomListHistoryAdapter(Context context,
			List<API18_ViewHistory> listlosthistory) {
		this.listLossHistory = listlosthistory;
		this.context = context;
	}

	@Override
	public int getCount() {
		return listLossHistory.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listLossHistory.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return listLossHistory.indexOf(listLossHistory.get(arg0));
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;

		if (arg1 == null) {

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			arg1 = mInflater.inflate(R.layout.s201_p_view_history_item, arg2,
					false);
			holder = new ViewHolder();
			holder.txtNo = (TextView) arg1
					.findViewById(R.id.s201_tv_p_history_item_no);
			holder.txtSubmitDate = (TextView) arg1
					.findViewById(R.id.s201_tv_p_history_item_submit_date);
			holder.txtProductName = (TextView) arg1
					.findViewById(R.id.s201_tv_p_history_item_hin_name);
			holder.txtLossReason = (TextView) arg1
					.findViewById(R.id.s201_tv_p_history_item_loss_reason);
			holder.txtQuantity = (TextView) arg1
					.findViewById(R.id.s201_tv_p_history_item_quantity);

			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		API18_ViewHistory historyObj = (API18_ViewHistory) getItem(arg0);
		holder.txtNo.setText(String.valueOf(arg0 + 1));
		holder.txtSubmitDate.setText(historyObj.getSubmitDate());
		holder.txtProductName.setText(historyObj.getHinName());
		holder.txtLossReason.setText(historyObj.getLossReason());
		holder.txtQuantity.setText(historyObj.getLossSum() + " "
				+ historyObj.getTaniName());
		return arg1;

	}
	
	public void updateResults(List<API18_ViewHistory> results) {
        listLossHistory = results;
        // update changed
        notifyDataSetChanged();
    }

	private class ViewHolder {
		TextView txtNo;
		TextView txtSubmitDate;
		TextView txtProductName;
		TextView txtLossReason;
		TextView txtQuantity;
	}

}
