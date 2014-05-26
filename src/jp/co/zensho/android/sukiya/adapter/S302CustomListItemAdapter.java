package jp.co.zensho.android.sukiya.adapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.bean.MeterReadingInfo;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class S302CustomListItemAdapter extends ArrayAdapter<MeterReadingInfo> {

	private final Context context;
	private boolean isCurrentYM;;
	private ArrayList<MeterReadingInfo> items;

	public S302CustomListItemAdapter(Context context,
			ArrayList<MeterReadingInfo> itemsArrayList, boolean isCurrYearMonth) {

		super(context, R.layout.s302_list_item, itemsArrayList);

		this.context = context;
		this.isCurrentYM = isCurrYearMonth;
		this.items = itemsArrayList;
		Log.i("item list", itemsArrayList.toString());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 1. Create inflater
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = null;

		if (position == 0 && isCurrentYM) {// First row and at current Year and
											// Month
			rowView = inflater.inflate(R.layout.s302_list_item_first_row,
					parent, false);

		} else {// other rows

			rowView = inflater.inflate(R.layout.s302_list_item, parent, false);
		}

		TextView txtMeter = (TextView) rowView
				.findViewById(R.id.s302_tv_list_meter);
		try {
			txtMeter.setText(items.get(position).getAmount());
		} catch (Exception e) {
			txtMeter.setText("");
		}
		TextView txtUsage = (TextView) rowView
				.findViewById(R.id.s302_tv_list_usage);

		// if (Math.abs(items.get(position).getUsage() - 0.0f) < 0.001) {
		// txtUsage.setText("");
		// } else {
		DecimalFormat df = new DecimalFormat("#0.0");
		txtUsage.setText(String.valueOf(df.format(items.get(position)
				.getUsage())));
		// }

		TextView txtDate = (TextView) rowView
				.findViewById(R.id.s302_tv_list_date);

		Log.i("date ngo", items.get(position).getEgy_date().toString());
		String lasmod = new SimpleDateFormat("yyyy/MM/dd",
				java.util.Locale.getDefault()).format(items.get(position)
				.getEgy_date());
		txtDate.setText(lasmod);
		TextView txtNumber = (TextView) rowView
				.findViewById(R.id.s302_tv_list_number);
		txtNumber.setText(position + 1 + "");

		return rowView;
	}
}
