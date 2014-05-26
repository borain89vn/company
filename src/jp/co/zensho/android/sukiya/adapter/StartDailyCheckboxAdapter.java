package jp.co.zensho.android.sukiya.adapter;

import java.util.List;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.bean.DailyCheckboxValue;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ToggleButton;

public class StartDailyCheckboxAdapter extends BaseAdapter implements OnClickListener {
    private Context context;
    private LayoutInflater myInflater;
    private final List<DailyCheckboxValue> data;
    
    public StartDailyCheckboxAdapter(Context c, List<DailyCheckboxValue> list) {
        this.context = c;
        this.myInflater = LayoutInflater.from(c);
        this.data = list;
    }
    
    @Override
    public int getCount() {
        if (this.data != null) {
            return this.data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (this.data != null && position >= 0 && position < this.data.size()) {
            return this.data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ToggleButton buttonView;

        if (convertView == null) {
            buttonView = new ToggleButton(context);

            // get layout from mobile.xml
            buttonView = (ToggleButton) this.myInflater.inflate(R.layout.select_button_item, null);
        } else {
            buttonView = (ToggleButton) convertView;
        }
        buttonView.setTag(100 + position);

        DailyCheckboxValue value = this.data.get(position);
        if (value != null) {
            buttonView.setText(value.getName());
            buttonView.setTextOn(value.getName());
            buttonView.setTextOff(value.getName());
            buttonView.setChecked(value.isSelected());
            
            buttonView.setOnClickListener(this);
        }
        return buttonView;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof Integer) {
            int position = ((Integer) tag) - 100;

            if (this.data != null && position >= 0 && position < this.data.size()) {
                DailyCheckboxValue value = this.data.get(position);
                if (value != null) {
                    if (value.isSelected()) {
                        if (value.isCanUnselect()) {
                            value.setSelected(false);
                        } else {
                            value.setSelected(true);
                            
                            ToggleButton button = (ToggleButton) v;
                            button.setChecked(value.isSelected());
                        }
                    } else {
                        value.setSelected(true);
                    }
                }
            }
        }
    }

}
