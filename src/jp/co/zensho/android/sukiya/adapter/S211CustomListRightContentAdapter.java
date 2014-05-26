package jp.co.zensho.android.sukiya.adapter;

import java.util.List;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.S211Activity;
import jp.co.zensho.android.sukiya.bean.API28_AcceptanceShop_Detail;
import jp.co.zensho.android.sukiya.common.SukiyaUtils;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author ntdat
 * 
 *         Date: 03/19/2014
 * 
 *         Class: S211CustomListRightContentAdapter
 * 
 *         Define adapter of ListView lv_right_content in screen 211
 * 
 * 
 */
public class S211CustomListRightContentAdapter extends BaseAdapter {
	private List<API28_AcceptanceShop_Detail> listRightContent;
	private Context context;
	// private LinearLayout llInputNumber, llDefaultBlank;
	// private Button btnPartCode;
	private S211Activity mActivity;

	public S211CustomListRightContentAdapter(Context context,
			List<API28_AcceptanceShop_Detail> listrightcontent) {
		this.listRightContent = listrightcontent;
		this.context = context;
	}

	public S211CustomListRightContentAdapter(Context context,
			List<API28_AcceptanceShop_Detail> listrightcontent,
			S211Activity activity) {
		this.listRightContent = listrightcontent;
		this.context = context;
		this.mActivity = activity;
	}

	@Override
	public int getCount() {
		return listRightContent.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listRightContent.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return listRightContent.indexOf(listRightContent.get(arg0));
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;

		if (arg1 == null) {

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			arg1 = mInflater.inflate(R.layout.s211_list_right_content_item,
					arg2, false);
			holder = new ViewHolder();
			holder.txtNo = (TextView) arg1
					.findViewById(R.id.s211_tv_right_content_item_no);
			holder.imgProductName = (ImageView) arg1
					.findViewById(R.id.s211_img_right_content_item_product);
			holder.txtProductName = (TextView) arg1
					.findViewById(R.id.s211_tv_right_content_item_name);
			holder.txtQuantity = (TextView) arg1
					.findViewById(R.id.s211_tv_right_content_item_quantity);
			holder.llDelete = (LinearLayout) arg1
					.findViewById(R.id.s211_ll_right_content_item_delete);

			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		final API28_AcceptanceShop_Detail acceptanceDetailObj = (API28_AcceptanceShop_Detail) getItem(arg0);
		holder.txtNo.setText(String.valueOf(arg0 + 1));
		// Define image format
		final StringBuilder imgNameBuilder = new StringBuilder();
		imgNameBuilder.append(acceptanceDetailObj.getHinCD());
		imgNameBuilder.append(".jpg");
		SukiyaUtils.setImage(context, holder.imgProductName,
				imgNameBuilder.toString(), R.drawable.no_image_85);

		holder.txtProductName.setText(acceptanceDetailObj.getHinName());

		StringBuilder builder = new StringBuilder();
		builder.append("<u>");
		builder.append(acceptanceDetailObj.getPreRCVSum());
		builder.append(" ");
		builder.append(acceptanceDetailObj.getTaniName());
		builder.append("</u>");
		holder.txtQuantity.setText(Html.fromHtml(builder.toString()));

		holder.txtQuantity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				S211Activity.nRightPosition = arg0;
				mActivity.etPopupQuantityAmount.setText(acceptanceDetailObj
						.getPreRCVSum());
				mActivity.tvPopupQuantityProductName
						.setText(acceptanceDetailObj.getHinName());
				mActivity.tvPopupQuantityUnit.setText(acceptanceDetailObj
						.getTaniName());
				SukiyaUtils.setImage(context, mActivity.imgPopupQuantity,
						imgNameBuilder.toString(), R.drawable.no_image_191);

				mActivity.tvPopupQuantityUnit.setEnabled(false);
				displayPopupInputNumber();
				// ngo begin
				mActivity.isChangeRightData = true;
				// ngo end
			}
		});

		holder.llDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listRightContent.remove(arg0);
				notifyDataSetChanged();
				// ngo begin
				mActivity.isChangeRightData = true;
				// ngo end
				if (listRightContent.size() <= 0) {
					mActivity.btnSubmit.setEnabled(false);
				}

				if (listRightContent.size() < 4) {
					mActivity.llDefaultBlank.setVisibility(View.VISIBLE);
				}
			}
		});

		return arg1;

	}

	public void updateResults(List<API28_AcceptanceShop_Detail> results) {
		listRightContent = results;
		// Update changed
		notifyDataSetChanged();
	}

	public void displayPopupInputNumber() {
		// Show popup input quantity
		AlphaAnimation alpha_input_open = new AlphaAnimation(0.0f, 1.0f);
		alpha_input_open.setDuration(350);
		mActivity.llPopupQuantity.startAnimation(alpha_input_open);
		mActivity.llPopupQuantity.setVisibility(View.VISIBLE);
	}

	private class ViewHolder {
		TextView txtNo;
		ImageView imgProductName;
		TextView txtProductName;
		TextView txtQuantity;
		LinearLayout llDelete;
	}

}
