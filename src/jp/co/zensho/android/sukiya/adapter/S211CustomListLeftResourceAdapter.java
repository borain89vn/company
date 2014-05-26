package jp.co.zensho.android.sukiya.adapter;

import java.util.List;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.bean.API27_AcceptanceShop;
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
 *         Date: 03/19/2014
 * 
 *         Class: S211CustomListLeftResourceAdapter
 * 
 *         Define adapter of ListView lv_left_resource in screen 211
 * 
 * 
 */
public class S211CustomListLeftResourceAdapter extends BaseAdapter {
	List<API27_AcceptanceShop> listLeftResource;
	Context context;

	public S211CustomListLeftResourceAdapter(Context context,
			List<API27_AcceptanceShop> listleftresource) {
		this.listLeftResource = listleftresource;
		this.context = context;
	}

	@Override
	public int getCount() {
		return listLeftResource.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listLeftResource.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return listLeftResource.indexOf(listLeftResource.get(arg0));
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;

		if (arg1 == null) {

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			arg1 = mInflater.inflate(R.layout.s211_list_left_resource_item,
					arg2, false);
			holder = new ViewHolder();
			holder.txtNo = (TextView) arg1
					.findViewById(R.id.s211_tv_left_resource_item_no);
			holder.txtShopCode = (TextView) arg1
					.findViewById(R.id.s211_tv_left_resource_item_shop_code);
			holder.txtShopName = (TextView) arg1
					.findViewById(R.id.s211_tv_left_resource_item_shop_name);
			holder.txtSendDateTime = (TextView) arg1
					.findViewById(R.id.s211_tv_left_resource_item_send_datetime);
			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		API27_AcceptanceShop acceptanceObj = (API27_AcceptanceShop) getItem(arg0);
		holder.txtNo.setText(String.valueOf(arg0 + 1));
		holder.txtShopCode.setText(acceptanceObj.getTenpoCD());
		holder.txtShopName.setText(acceptanceObj.getTenpoName());
		holder.txtSendDateTime.setText(acceptanceObj.getSendDateTime());

		//arg1.setBackgroundResource(R.drawable.selector_green_grey_border_acceptance_left);

		return arg1;

	}

	private class ViewHolder {
		TextView txtNo;
		TextView txtShopCode;
		TextView txtShopName;
		TextView txtSendDateTime;
	}

}
