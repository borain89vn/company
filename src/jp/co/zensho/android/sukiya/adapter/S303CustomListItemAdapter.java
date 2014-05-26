package jp.co.zensho.android.sukiya.adapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.bean.MeterReadingInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class S303CustomListItemAdapter extends ArrayAdapter<MeterReadingInfo> {

	private final Context mContext;
	private boolean isCurrentY;;
	private boolean isLastYearList;

	private final ArrayList<MeterReadingInfo> itemsArrayList;

	public S303CustomListItemAdapter(Context context,
			ArrayList<MeterReadingInfo> itemsArrayList, boolean isCurrYear,
			boolean isLastYearList) {

		super(context, R.layout.s303_list_item, itemsArrayList);

		this.mContext = context;

		this.itemsArrayList = itemsArrayList;
		this.isCurrentY = isCurrYear;
		this.isLastYearList = isLastYearList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 1. Create inflater
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = null;
		// First row and at current Year but not in last year list
		if (position == 0 && !isLastYearList && isCurrentY == true) {

			rowView = inflater.inflate(R.layout.s303_list_item_first_row,
					parent, false);
			TextView txtMeter = (TextView) rowView
					.findViewById(R.id.s303_tv_list_meter);
			txtMeter.setText(itemsArrayList.get(0).getAmount());

		} else {

			rowView = inflater.inflate(R.layout.s303_list_item, parent, false);
			TextView txtMeter = (TextView) rowView
					.findViewById(R.id.s303_tv_list_meter);
			txtMeter.setText(itemsArrayList.get(position).getAmount());

		}

		TextView txtDate = (TextView) rowView
				.findViewById(R.id.s303_tv_list_date);
		TextView txtUsage = (TextView) rowView
				.findViewById(R.id.s303_tv_list_usage);
		TextView txtNumber = (TextView) rowView
				.findViewById(R.id.s303_tv_list_number);
		TextView txtRatio = (TextView) rowView
				.findViewById(R.id.s303_tv_list_ratio);

		String lasmod = new SimpleDateFormat("yyyy/MM/dd",
				java.util.Locale.getDefault()).format(itemsArrayList.get(
				position).getEgy_date());

		txtDate.setText(lasmod);
		txtNumber.setText(String.valueOf(position + 1));

		// if (Math.abs(itemsArrayList.get(position).getUsage() - 0.0f) < 0.001)
		// {
		// txtUsage.setText("");
		// } else {
		DecimalFormat df = new DecimalFormat("#0.0");
		txtUsage.setText(df.format(itemsArrayList.get(position).getUsage()));
		// }

		// if (Math.abs(itemsArrayList.get(position).getRatio() - 0.0f) < 0.001)
		// {
		// txtRatio.setText("");
		// } else {
		// DecimalFormat df = new DecimalFormat("#0.0");
		txtRatio.setText(df.format(itemsArrayList.get(position).getRatio()));
		// }

		return rowView;
	}
}
