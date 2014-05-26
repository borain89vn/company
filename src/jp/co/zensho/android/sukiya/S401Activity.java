package jp.co.zensho.android.sukiya;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import jp.co.zensho.android.sukiya.adapter.S401CustomListItemAdapter;
import jp.co.zensho.android.sukiya.adapter.S402CustomListItemAdapter;
import jp.co.zensho.android.sukiya.adapter.S403CustomListItemAdapter;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import jp.co.zensho.android.sukiya.common.JSONUtils;
import jp.co.zensho.android.sukiya.common.StringUtils;
import jp.co.zensho.android.sukiya.common.SukiyaContant;
import jp.co.zensho.android.sukiya.service.CallAPIAsyncTask;
import jp.co.zensho.android.sukiya.service.CallAPIListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This class is use for displaying Screen 401,402,403
 * 
 * @author ltthuc
 * 
 */
public class S401Activity extends S400BaseActivity implements CallAPIListener,
		OnClickListener {
	private static final String LOG = "S401Activity";
	private int currentId;

	/**
	 * Help button
	 * 
	 * @author vdngo
	 */
	// help
	private Button mBtnHelp;
	private TextView mHelp;
	private LinearLayout mHelpLayout;
	private int menuPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.s401);
		super.onCreate(savedInstanceState);
		lvS400 = (ListView) findViewById(R.id.lv_s401_inventory_day);

		/**
		 * help
		 * 
		 * @author vdngo
		 */
		//
		mBtnHelp = (Button) findViewById(R.id.btn_q);
		mHelp = (TextView) findViewById(R.id.help_content);
		mHelpLayout = (LinearLayout) findViewById(R.id.s401_help);
		mBtnHelp.setOnClickListener(this);
		mHelpLayout.setOnClickListener(this);

		initCommonView();
	}

	@Override
	public void callAPIFinish(String tag, JSONObject result, ErrorInfo error) {
		this.dismissProcessDialog();
		if (error == null && result != null) {
			try {
				if (!JSONUtils.hasError(result)) {
					if (JSONUtils.API_037_PATH.equals(tag)) {
						apiModel.setApi037(JSONUtils
								.parseApiAPI037Model(result));

						initAdapter(SukiyaContant.API037_ID);

					}
					if (JSONUtils.API_038_PATH.equals(tag)) {
						apiModel.setApi038(JSONUtils
								.parseApiAPI038Model(result));
						initAdapter(SukiyaContant.API038_ID);
					}
					if (JSONUtils.API_039_PATH.equals(tag)) {
						apiModel.setApi039(JSONUtils
								.parseApiAPI039Model(result));
						initAdapter(SukiyaContant.API039_ID);
					}

					/**
					 * help
					 * 
					 * @author vdngo
					 */
					if (JSONUtils.API_043_PATH.equals(tag)) {
						// Zenshou ntdat 20140407 delete ++
						String help = null;
						try {
							help = JSONUtils.partHelpString(result);
							// Test scroll
							//
							super.showPopup(mHelpLayout);
							mHelp.setText(help.replace("\\n", "\n"));

						} catch (JSONException e) {
							if (error == null) {
								error = new ErrorInfo();
								error.setMessage(MessageFormat.format(
										"System error: {0}", e.getMessage()));
							}
							e.printStackTrace();
						}

						Log.e("Json after parse", help);
						if (help != null) {
							this.helpString = help;
						}
					}

				} else {
					if (error == null) {
						error = JSONUtils.getFirstError(result);
					}
				}
			} catch (JSONException e) {
				if (error == null) {
					error = new ErrorInfo();
					error.setMessage(MessageFormat.format("System error: {0}",
							e.getMessage()));
				}
				e.printStackTrace();
			}
		}
		if (error != null) {
			String message = error.getMessage();
			if (!StringUtils.isEmpty(error.getCode())) {
				message = MessageFormat.format("{0}: {1}", error.getCode(),
						error.getMessage());
			}
			super.showDialog(this, message, R.string.try_again,
					retryAPI0xxDialogListener, R.string.cancel,
					dismissDialogListener);
		}

	}

	@Override
	public void callAPI() {
		switch (getIdKPIInfo()) {
		case SukiyaContant.API037_ID:
			currentId = SukiyaContant.API037_ID;
			callAPI037();
			break;
		case SukiyaContant.API038_ID:
			currentId = SukiyaContant.API038_ID;
			callAPI038();
			break;
		case SukiyaContant.API039_ID:
			currentId = SukiyaContant.API039_ID;
			callAPI039();
			break;
		}

	}

	/**
	 * init View change tile and textHeader for each Screen
	 * 
	 * @param null
	 */
	public void initCommonView() {
		TextView txtTitle = (TextView) findViewById(R.id.txt_title);
		TextView txtHeader2 = (TextView) findViewById(R.id.txt_header_2);
		TextView txtHeader3 = (TextView) findViewById(R.id.txt_header_3);
		Resources res = getResources();
		switch (getIdKPIInfo()) {
		case SukiyaContant.API037_ID:

			menuPosition = 1;
			String[] text = res.getStringArray(R.array.s401_name);
			txtTitle.setText(text[0]);
			txtHeader2.setText(text[1]);
			txtHeader3.setText(text[2]);
			break;
		case SukiyaContant.API038_ID:

			menuPosition = 2;
			String[] text1 = res.getStringArray(R.array.s401_1_name);
			txtTitle.setText(text1[0]);
			txtHeader2.setText(text1[1]);
			txtHeader3.setText(text1[2]);
			break;
		case SukiyaContant.API039_ID:

			menuPosition = 3;
			String[] text2 = res.getStringArray(R.array.s401_2_name);
			txtTitle.setText(text2[0]);
			txtHeader2.setText(text2[1]);
			txtHeader3.setText(text2[2]);
			break;
		}

	}

	private void callAPI037() {
		Log.d(LOG, "callAPI37 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// comment method for using dummy then remove it after finish api
			Map<String, String> params = new HashMap<String, String>();
			params.put("tenpo_cd", super.app.getShopCode());

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_037_PATH, params);

				// show process dialog
				this.showProcessDialog(this, null, null);

				new CallAPIAsyncTask(this, JSONUtils.API_037_PATH).execute(url);

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = MessageFormat.format(
						getString(R.string.msg_003_system_error),
						e.getMessage());
				super.showDialog(this, message, R.string.try_again,
						retryAPI0xxDialogListener, R.string.exit_app,
						exitApplicationDialogListener);
			}

		} else { // Show error dialog
			super.showDialog(this, R.string.msg_004_shop_code_not_found,
					R.string.try_again, retryAPI0xxDialogListener,
					R.string.exit_app, exitApplicationDialogListener);
		}

		Log.d(LOG, "callAPI37 - END");

	}

	private void callAPI038() {
		Log.d(LOG, "callAPI038 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {

			Map<String, String> params = new HashMap<String, String>();
			params.put("tenpo_cd", super.app.getShopCode());

			try {

				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_038_PATH, params);

				// show process dialog
				this.showProcessDialog(this, null, null);

				new CallAPIAsyncTask(this, JSONUtils.API_038_PATH).execute(url);

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = MessageFormat.format(
						getString(R.string.msg_003_system_error),
						e.getMessage());
				super.showDialog(this, message, R.string.try_again,
						retryAPI0xxDialogListener, R.string.exit_app,
						exitApplicationDialogListener);
			}
		} else { // Show error dialog
			super.showDialog(this, R.string.msg_004_shop_code_not_found,
					R.string.try_again, retryAPI0xxDialogListener,
					R.string.exit_app, exitApplicationDialogListener);
		}

		Log.d(LOG, "callAPI38 - END");
	}

	private void callAPI039() {
		Log.d(LOG, "callAPI0389 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// comment method for using dummy then remove it after finish api

			Map<String, String> params = new HashMap<String, String>();
			params.put("tenpo_cd", super.app.getShopCode());
			params.put("limit", "3");

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_039_PATH, params);

				new CallAPIAsyncTask(this, JSONUtils.API_039_PATH).execute(url);

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = MessageFormat.format(
						getString(R.string.msg_003_system_error),
						e.getMessage());
				super.showDialog(this, message, R.string.try_again,
						retryAPI0xxDialogListener, R.string.exit_app,
						exitApplicationDialogListener);
			}
		} else { // Show error dialog
			super.showDialog(this, R.string.msg_004_shop_code_not_found,
					R.string.try_again, retryAPI0xxDialogListener,
					R.string.exit_app, exitApplicationDialogListener);

		}
		Log.d(LOG, "callAPI39 - END");
	}

	@Override
	protected void onStop() {
		this.removeUpdateCurrentDate();
		super.onStop();
	}

	/**
	 * get API ID from selecting textView
	 * 
	 * @param int
	 * 
	 */
	@Override
	public int getIdKPIInfo() {
		// TODO Auto-generated method stub
		int api = 0;
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		if (extras != null) {
			api = extras.getInt(SukiyaContant.KPIINFO);
		}
		return api;
	}

	@Override
	public void initAdapter(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case SukiyaContant.API037_ID:
			this.adapter = new S401CustomListItemAdapter(this,
					apiModel.getApi037());
			lvS400.setAdapter(adapter);

			break;
		case SukiyaContant.API038_ID:
			this.adapter = new S402CustomListItemAdapter(this,
					apiModel.getApi038());
			lvS400.setAdapter(adapter);
			break;
		case SukiyaContant.API039_ID:
			this.adapter = new S403CustomListItemAdapter(this,
					apiModel.getApi039());
			lvS400.setAdapter(adapter);
			break;
		}

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		/**
		 * help
		 * 
		 * @author vdngo
		 */
		if (v.equals(mHelpLayout)) {
			super.hidePopup(mHelpLayout);
		} else if (v.equals(mBtnHelp)) {
			switch (menuPosition) {
			case 1:
				super.getHelp(S401Activity.this, StringUtils.S401_1_HELP_CD);
				break;
			case 2:
				super.getHelp(S401Activity.this, StringUtils.S401_2_HELP_CD);
				break;
			case 3:
				super.getHelp(S401Activity.this, StringUtils.S401_3_HELP_CD);
				break;
			default:
				break;
			}
		}
	}

	public void inventoryAnalysis(View v) {
		Log.i("hin_cd", "Click here");
		int position = Integer.parseInt(v.getTag().toString());
		String hin_cd = "";
		String hin_nm = "";
		Intent i = new Intent(this, S304Activity.class);
		switch (currentId) {
		case SukiyaContant.API037_ID:
			hin_cd = apiModel.getApi037().get(position).getHin_cd();
			hin_nm = apiModel.getApi037().get(position).getHin_nm();
			break;
		case SukiyaContant.API038_ID:
			hin_cd = apiModel.getApi038().get(position).getHin_cd();
			hin_nm = apiModel.getApi038().get(position).getHin_nm();
			break;
		case SukiyaContant.API039_ID:
			hin_cd = apiModel.getApi039().get(position).getHin_cd();
			hin_nm = apiModel.getApi039().get(position).getHin_nm();
			break;
		}

		i.putExtra(JSONUtils.HIN_CODE, hin_cd);
		i.putExtra(JSONUtils.HIN_NAME, hin_nm);
		startActivity(i);
	}
}
