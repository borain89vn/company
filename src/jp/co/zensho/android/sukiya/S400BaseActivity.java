package jp.co.zensho.android.sukiya;

import jp.co.zensho.android.sukiya.bean.APIModel;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * This class is parent of S401Activity and S402Activity 
 * 
 * @author ltthuc
 *
 */
public abstract class S400BaseActivity extends S00Activity implements
		OnClickListener {
	public APIModel apiModel;  //save list API037,038,039,040,041
	public BaseAdapter adapter;
	public ListView lvS400;
	public Button btnBack;
    // show dialog error
	public DialogInterface.OnClickListener retryAPI0xxDialogListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S400BaseActivity.this.callAPI();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		apiModel = new APIModel();
		btnBack = (Button) findViewById(R.id.btn_back_img2);
		btnBack.setOnClickListener(this);
		generateViews();
		callAPI();

	}
	
	/**
     * set adapter to listview
     * @param id  get API
     */
	public abstract void initAdapter(int id);
   
	/**
     * call API to get json string
     */
	public abstract void callAPI();

	public abstract int getIdKPIInfo();

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_back_img2) {
			this.finish();
			overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right);
		}
	}
}
