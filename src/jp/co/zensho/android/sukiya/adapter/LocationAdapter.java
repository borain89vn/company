package jp.co.zensho.android.sukiya.adapter;

import java.util.List;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.bean.LocationInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class LocationAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater myInflater;
	private final List<LocationInfo> data;
	private final List<String> lstLocationText;
    private final int itemPostionClick;
	public LocationAdapter(Context context, List<LocationInfo> locationList,
			List<String> lstLocName, int itemPostionClick) {
		this.context = context;
		this.myInflater = LayoutInflater.from(context);
		this.data = locationList;
		this.lstLocationText = lstLocName;
		this.itemPostionClick = itemPostionClick;
	}

	@Override
	public int getCount() {
		if (data != null) {
			return data.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		if (data != null && data.size() > arg0) {
			return data.get(arg0);
		}
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder locationView = null;

		if (convertView == null) {
			convertView = this.myInflater.inflate(
					R.layout.location_select_one_choice_item, parent, false);

			locationView = new ViewHolder();
			locationView.label = (TextView) convertView
					.findViewById(R.id.location_name);

			locationView.radioButton = (RadioButton) convertView
					.findViewById(R.id.rbtn_location_choice);
			if(position==itemPostionClick) {
	        	locationView.radioButton.setChecked(true);
	        }
			convertView.setTag(locationView);
		} else {
			locationView = (ViewHolder) convertView.getTag();
		}

		locationView.label.setText(lstLocationText.get(position));

		int n = data.get(position).getInputedProduct();
		int m = data.get(position).getTotalProduct();
		if (n < m) {
			convertView.setBackgroundColor(context.getResources().getColor(R.color.s202_location));
		} else if (n == m && m != 0) {

			convertView.setBackgroundColor(context.getResources().getColor(R.color.calendar_hilite));
		}

		return convertView;
	}

	private class ViewHolder {
		TextView label;
		RadioButton radioButton;
	}
}
