package jp.co.zensho.android.sukiya.adapter;

import java.util.ArrayList;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.bean.API039_LossAmount;
import jp.co.zensho.android.sukiya.common.SukiyaUtils;
import jp.co.zensho.android.sukiya.common.UnitSpanable;
import jp.co.zensho.android.sukiya.common.ViewHolder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for setting data to listview in screen 403
 * 
 * @author ltthuc
 * 
 */

public class S403CustomListItemAdapter extends ArrayAdapter<API039_LossAmount> {

	public final Context context;
	ArrayList<API039_LossAmount> listApi039Model;

	public S403CustomListItemAdapter(Context context,
			ArrayList<API039_LossAmount> itemsArrayList) {
		super(context, R.layout.s401_list_item, itemsArrayList);
		this.context = context;
		this.listApi039Model = itemsArrayList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = mInflater.inflate(R.layout.s401_list_item, parent,
					false);
			holder = new ViewHolder();
			holder.txtS401No = (TextView) convertView
					.findViewById(R.id.txt_s401_no);
			holder.txtS401ItemName = (TextView) convertView
					.findViewById(R.id.txt_s401_name);
			holder.txtS401Quantity = (TextView) convertView
					.findViewById(R.id.txt_s401_quantity);
			holder.imgbtnInventory = (ImageView) convertView
					.findViewById(R.id.img_s401_inventory_analysis);
			holder.imgS401ItemImage = (ImageView) convertView
					.findViewById(R.id.img_s401_name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.imgbtnInventory.setTag(position);
		SukiyaUtils.setImage(context, holder.imgS401ItemImage, listApi039Model
				.get(position).getHin_cd() + ".jpg", R.drawable.no_image_85);
		holder.txtS401Quantity.setText(listApi039Model.get(position)
				.getAmount());
		UnitSpanable.getInstance().setSpan(context, holder.txtS401Quantity,
				context.getResources().getString(R.string.yen), 0.8f);
		holder.txtS401No.setText(String.valueOf(position + 1));
		holder.txtS401ItemName.setText(listApi039Model.get(position)
				.getHin_nm());
		return convertView;
	}

}
