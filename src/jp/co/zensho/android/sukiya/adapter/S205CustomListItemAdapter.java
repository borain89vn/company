package jp.co.zensho.android.sukiya.adapter;

import java.util.List;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.bean.S205_ListShortReport;
import jp.co.zensho.android.sukiya.common.SukiyaUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author ntdat Class: S205_CustomListItemAdapter
 * 
 *         Define adapter of ListView lv_short_report in screen 205
 * 
 */
public class S205CustomListItemAdapter extends BaseAdapter {
	private List<S205_ListShortReport> listShortReport;
	private Context context;
	private Button btnPartCode;
	private LinearLayout llDefaulBlank;

	public S205CustomListItemAdapter(Context context,
			List<S205_ListShortReport> listshortreport) {
		this.listShortReport = listshortreport;
		this.context = context;
	}

	public S205CustomListItemAdapter(Context context,
			List<S205_ListShortReport> listshortreport, Button btnpartcode,
			LinearLayout lldefaultblank) {
		this.listShortReport = listshortreport;
		this.context = context;
		this.btnPartCode = btnpartcode;
		this.llDefaulBlank = lldefaultblank;
	}

	@Override
	public int getCount() {
		return listShortReport.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listShortReport.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return listShortReport.indexOf(listShortReport.get(arg0));
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;

		if (arg1 == null) {

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			arg1 = mInflater.inflate(R.layout.s205_list_item, arg2, false);
			holder = new ViewHolder();
			holder.txtNo = (TextView) arg1
					.findViewById(R.id.s205_tv_list_item_no);
			holder.imgProductName = (ImageView) arg1
					.findViewById(R.id.s205_img_list_item_product);
			holder.txtProductName = (TextView) arg1
					.findViewById(R.id.s205_tv_list_item_name);
			holder.txtShortReason = (TextView) arg1
					.findViewById(R.id.s205_tv_list_item_short_reason);
			holder.txtHour = (TextView) arg1
					.findViewById(R.id.s205_tv_list_item_hour);
			holder.llDelete = (LinearLayout) arg1
					.findViewById(R.id.s205_ll_list_item_delete);
			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		S205_ListShortReport s205ListObj = (S205_ListShortReport) getItem(arg0);
		holder.txtNo.setText(String.valueOf(arg0 + 1));
		SukiyaUtils.setImage(context, holder.imgProductName,
				s205ListObj.getHinCD() + ".jpg", R.drawable.no_image_191);
		holder.txtProductName.setText(s205ListObj.getHinName());
		holder.txtShortReason.setText(s205ListObj.getShortReason());
		holder.txtHour.setText(s205ListObj.getHour());

		holder.llDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listShortReport.remove(arg0);
				notifyDataSetChanged();

				if (listShortReport.size() <= 0) {
					btnPartCode.setEnabled(false);
				}

				if (listShortReport.size() < 5) {
					llDefaulBlank.setVisibility(View.VISIBLE);
				}
			}
		});
		return arg1;

	}

	private class ViewHolder {
		TextView txtNo;
		ImageView imgProductName;
		TextView txtProductName;
		TextView txtShortReason;
		TextView txtHour;
		LinearLayout llDelete;
	}

}
