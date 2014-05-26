package jp.co.zensho.android.sukiya.adapter;

import java.util.List;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.S201Activity;
import jp.co.zensho.android.sukiya.bean.S201_ListLossReport;
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
 * @author ntdat Class: S201_CustomListItemAdapter
 * 
 *         Define adapter of ListView lv_loss_report in screen 201
 * 
 */
public class S201CustomListItemAdapter extends BaseAdapter {
	private List<S201_ListLossReport> listLossReport;
	private Context context;
	private S201Activity mActivity;

	public S201CustomListItemAdapter(Context context,
			List<S201_ListLossReport> listlostreport) {
		this.listLossReport = listlostreport;
		this.context = context;
	}

	public S201CustomListItemAdapter(Context context,
			List<S201_ListLossReport> listlostreport, S201Activity activity) {
		this.listLossReport = listlostreport;
		this.context = context;
		this.mActivity = activity;
	}

	@Override
	public int getCount() {
		return listLossReport.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listLossReport.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return listLossReport.indexOf(listLossReport.get(arg0));
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;

		if (arg1 == null) {

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			arg1 = mInflater.inflate(R.layout.s201_list_item, arg2, false);
			holder = new ViewHolder();
			holder.txtNo = (TextView) arg1
					.findViewById(R.id.s201_tv_list_item_no);
			holder.imgProductName = (ImageView) arg1
					.findViewById(R.id.s201_img_list_item_product);
			holder.txtProductName = (TextView) arg1
					.findViewById(R.id.s201_tv_list_item_name);
			holder.txtLossReason = (TextView) arg1
					.findViewById(R.id.s201_tv_list_item_loss_reason);
			holder.txtQuantity = (TextView) arg1
					.findViewById(R.id.s201_tv_list_item_quantity);
			holder.llDelete = (LinearLayout) arg1
					.findViewById(R.id.s201_ll_list_item_delete);

			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		final S201_ListLossReport s201ListObj = (S201_ListLossReport) getItem(arg0);
		holder.txtNo.setText(String.valueOf(arg0 + 1));
		SukiyaUtils.setImage(context, holder.imgProductName,
				s201ListObj.getHinCD() + ".jpg", R.drawable.no_image_85);
		holder.txtProductName.setText(s201ListObj.getHinName());
		holder.txtLossReason.setText(s201ListObj.getLossReason());

		// Make text underline
		StringBuilder builder = new StringBuilder();
		builder.append("<u>");
		builder.append(s201ListObj.getLossSum());
		builder.append(" ");
		builder.append(s201ListObj.getTaniName());
		builder.append("</u>");
		holder.txtQuantity.setText(Html.fromHtml(builder.toString()));

		holder.txtQuantity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mActivity.nPosition = arg0;
				mActivity.isListItemNumberClicked = true;
				mActivity.etPopupQuantityAmount.setText(s201ListObj
						.getLossSum());
				mActivity.tvPopupQuantityUnit.setText(s201ListObj.getTaniName());

				// Set product name in input number pop-up
				SukiyaUtils.setImage(context,
						mActivity.imgPopupQuantityProduct,
						s201ListObj.getHinCD() + ".jpg",
						R.drawable.no_image_191);
				mActivity.tvPopupQuantityProductName.setText(s201ListObj
						.getHinName());
				mActivity.callAPI017(s201ListObj.getHinCD());

			}
		});

		holder.llDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listLossReport.remove(arg0);
				if (listLossReport.size() <= 0) {
					mActivity.btnInputPartCode.setEnabled(false);
				}

				if (listLossReport.size() < 5) {
					mActivity.llDefaultBlank.setVisibility(View.VISIBLE);
				}
				notifyDataSetChanged();
			}
		});
		return arg1;

	}

	public void updateResults(List<S201_ListLossReport> results) {
		listLossReport = results;
		// Update changed
		notifyDataSetChanged();
	}

	private class ViewHolder {
		TextView txtNo;
		ImageView imgProductName;
		TextView txtProductName;
		TextView txtLossReason;
		TextView txtQuantity;
		LinearLayout llDelete;
	}

}
