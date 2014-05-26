package jp.co.zensho.android.sukiya;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.zensho.android.sukiya.bean.API040_MoveLoad;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import jp.co.zensho.android.sukiya.bean.Tenpo;
import jp.co.zensho.android.sukiya.common.JSONUtils;
import jp.co.zensho.android.sukiya.common.StringUtils;
import jp.co.zensho.android.sukiya.common.SukiyaContant;
import jp.co.zensho.android.sukiya.common.SukiyaUtils;
import jp.co.zensho.android.sukiya.common.UnitSpanable;
import jp.co.zensho.android.sukiya.service.CallAPIAsyncTask;
import jp.co.zensho.android.sukiya.service.CallAPIListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * This class is use for displaying Screen 404,405
 * 
 * @author ltthuc
 * 
 */
public class S402Activity extends S400BaseActivity implements CallAPIListener,
		OnClickListener {
	private static final String LOG = "S402Activity";
	List<Tenpo> mshop = new ArrayList<Tenpo>();
	TextView txtTitle;
	ArrayList<API040_MoveLoad> listApi040Model;
	LinearLayout linS40x;
	LayoutInflater mInflater;
	boolean checkJapaneseFinishe = false;

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
		setContentView(R.layout.s402);
		super.onCreate(savedInstanceState);

		linS40x = (LinearLayout) findViewById(R.id.lin_s40xx);
		mInflater = LayoutInflater.from(this);

		/**
		 * help
		 * 
		 * @author vdngo
		 */
		//
		mBtnHelp = (Button) findViewById(R.id.btn_q);
		mHelp = (TextView) findViewById(R.id.help_content);
		mHelpLayout = (LinearLayout) findViewById(R.id.s402_help);
		mBtnHelp.setOnClickListener(this);
		mHelpLayout.setOnClickListener(this);

	}

	@Override
	public void callAPI() {
		txtTitle = (TextView) findViewById(R.id.txt_40x_title);
		switch (getIdKPIInfo()) {
		case SukiyaContant.API041_ID:
			currentId = SukiyaContant.API041_ID;
			txtTitle.setText(getResources().getString(R.string.s402_2_title));
			callAPI040();
			menuPosition = 1;
			break;
		case SukiyaContant.API040_ID:
			currentId = SukiyaContant.API040_ID;
			txtTitle.setText(getResources().getString(R.string.s402_1_title));
			callAPI040();
			menuPosition = 2;
			break;
		}
	}

	@Override
	public void callAPIFinish(String tag, JSONObject result, ErrorInfo error) {

		this.dismissProcessDialog();
		if (error == null && result != null) {
			try {
				if (!JSONUtils.hasError(result)) {
					if (JSONUtils.API_040_PATH.equals(tag)) {
						apiModel.setApi040(JSONUtils.parseApiAPI040Model(
								result, SukiyaContant.API040_ID));
						setRowTable(apiModel.getApi040());
						if (checkJapaneseFinishe == true) {
							// this.dismissProcessDialog();
						}
					}
					if (JSONUtils.API_041_PATH.equals(tag)) {
						apiModel.setApi040(JSONUtils.parseApiAPI040Model(
								result, SukiyaContant.API041_ID));
						setRowTable(apiModel.getApi040());
						if (checkJapaneseFinishe == true) {
						}
					}

					/**
					 * help
					 * 
					 * @author vdngo
					 */
					if (JSONUtils.API_043_PATH.equals(tag)) {

						// this.dismissProcessDialog();
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

	private void callAPI040() {
		Log.d(LOG, "callAPI040 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put("tenpo_cd", super.app.getShopCode());
			params.put("limit", "5");
			String url = null;

			try {
				if (getIdKPIInfo() == SukiyaContant.API040_ID) {

					url = JSONUtils.getAPIUrl(JSONUtils.API_040_PATH, params);

					// show process dialog
					this.showProcessDialog(this, null, null);

					new CallAPIAsyncTask(this, JSONUtils.API_040_PATH)
							.execute(url);
				} else {
					url = JSONUtils.getAPIUrl(JSONUtils.API_041_PATH, params);

					// show process dialog
					this.showProcessDialog(this, null, null);

					new CallAPIAsyncTask(this, JSONUtils.API_041_PATH)
							.execute(url);
				}

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

		Log.d(LOG, "callAPI040 - END");
	}

	@Override
	protected void onStop() {
		this.removeUpdateCurrentDate();
		super.onStop();
	}

	@Override
	public int getIdKPIInfo() {
		int id = 0;
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		if (extras != null) {
			id = extras.getInt(SukiyaContant.KPIINFO_2);
		}
		return id;
	}

	private void setRowTable(ArrayList<API040_MoveLoad> api040) {
		TextView txtS402No = null;
		ImageView imgS402ItemImg;
		TextView txtS402ItemName = null;
		TextView txtS402TotalCount = null;
		ImageView imgbtnInventory = null;
		TableLayout tblS402ShopName = null;
		TableLayout tblS402ShopCount = null;
		int linWeight = 0;
		int tenPoSize = 0;
		for (int i = 0; i < api040.size(); i++) {
			tenPoSize = api040.get(i).getTenpo().size();
			if (tenPoSize > 5) {
				linWeight = dpToPx(250 + 50);
			} else {
				linWeight = dpToPx(180);
			}
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, linWeight);
			params.setMargins(0, 2, 0, 0);
			LinearLayout row = new LinearLayout(this);
			row = (LinearLayout) mInflater.inflate(R.layout.s402_list_item,
					null);
			row.setLayoutParams(params);
			txtS402No = (TextView) row.findViewById(R.id.txt_s402_no);
			imgS402ItemImg = (ImageView) row.findViewById(R.id.img_s402_name);
			txtS402TotalCount = (TextView) row
					.findViewById(R.id.txt_s402_total_count);
			imgbtnInventory = (ImageView) row
					.findViewById(R.id.img_s402_inventory_analysis);
			imgbtnInventory.setTag(i);

			txtS402ItemName = (TextView) row.findViewById(R.id.txt_s402_name);
			tblS402ShopName = (TableLayout) row
					.findViewById(R.id.tbl_s402_shop_name);
			tblS402ShopCount = (TableLayout) row
					.findViewById(R.id.tbl_s402_shop_count);
			txtS402No.setText(String.valueOf(i + 1));
			SukiyaUtils.setImage(this, imgS402ItemImg, api040.get(i)
					.getHin_cd() + ".jpg", R.drawable.no_image_85);
			txtS402ItemName.setText(api040.get(i).getHin_nm());
			txtS402TotalCount.setText(String.valueOf(api040.get(i)
					.getTotal_count()));
			imgbtnInventory.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					int position = Integer.parseInt(v.getTag().toString());
					String hin_cd = apiModel.getApi040().get(position)
							.getHin_cd();
					String hin_nm = apiModel.getApi040().get(position)
							.getHin_nm();
					Log.e("hin name in 4.2", hin_nm);
					Intent i = new Intent(S402Activity.this, S304Activity.class);
					i.putExtra(JSONUtils.HIN_CODE, hin_cd);
					i.putExtra(JSONUtils.HIN_NAME, hin_nm);
					startActivity(i);

				}
			});
			UnitSpanable.getInstance().setSpan(this, txtS402TotalCount,
					getResources().getString(R.string.item_name), 0.8f);
			addRowShopName(api040.get(i), tblS402ShopName, 0, i);
			addRowShopName(api040.get(i), tblS402ShopCount, 1, i);
			linS40x.addView(row);
		}
		checkJapaneseFinishe = true;

	}

	public void addRowShopName(API040_MoveLoad api040, final TableLayout table,
			int id, int position) {
		int size = api040.getTenpo().size();
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				100f / size);
		params.setMargins(0, 2, 0, 0);
		TableRow.LayoutParams txtParams = new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.WRAP_CONTENT);
		txtParams.bottomMargin = 2;
		for (int i = 0; i < size; i++) {
			TableRow tbl = new TableRow(this);
			TextView shop = new TextView(this);

			tbl.setLayoutParams(params);

			tbl.setGravity(Gravity.CENTER);
			shop.setGravity(Gravity.CENTER);
			shop.setTextColor(this.getResources().getColor(R.color.textColor));
			shop.setTypeface(null, Typeface.BOLD);

			if (id == 0) {
				shop.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
				shop.setText(this.splitText(api040.getTenpo().get(i)
						.getTenpo_nm()));

				UnitSpanable.getInstance().setSpan(this, shop,
						this.getResources().getString(R.string.shop), 1f);
			} else {
				shop.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
				shop.setText(String.valueOf(((API040_MoveLoad) api040).getTenpo()
						.get(i).getCount()));
				UnitSpanable.getInstance()
						.setSpan(
								this,
								shop,
								this.getResources().getString(
										R.string.item_name), 0.8f);
			}

			shop.setLayoutParams(txtParams);
			shop.setMaxLines(2);
			tbl.addView(shop);
			table.addView(tbl);
			if (i != size - 1) {
				View v = new View(this);
				v.setLayoutParams(new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT, 2));
				v.setBackgroundColor(this.getResources().getColor(
						R.color.tbl_row_color));
				table.addView(v);
			}
		}
	}

	private String splitText(String text) {
		String result = null;
		int count = 0;
		char[] ch = text.toCharArray();
		int totalBytes = 0;
		boolean detectJapanese = false;
		for (int i = 0; i < text.length(); i++) {
			if (isJapanese(ch[i]) == true) {
				totalBytes = totalBytes
						+ Charset.forName("UTF-8")
								.encode(String.valueOf(ch[i])).limit();
				if (totalBytes > 18) {
					result = text.substring(0, i - 1) + "...";
					detectJapanese = true;
					break;
				}
			}

		}
		if (detectJapanese == false) {
			if (text.length() > 11) {
				if (count > 1) {

				}
				result = text.substring(0, 11) + "...";
			} else {
				result = text;
			}
		}

		return result;
	}

	private boolean isJapanese(char ch) {
		boolean result = false;
		if ((int) ch <= 255) {
			System.out.println("This is english letter");
			result = false;
		} else {
			System.out.println("This is Japnese letter");
			result = true;
		}
		return result;
	}

	public static int dpToPx(int dp) {
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}

	@Override
	public void initAdapter(int id) {
		// TODO Auto-generated method stub

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
				super.getHelp(S402Activity.this, StringUtils.S402_2_HELP_CD);
				break;
			case 2:
				super.getHelp(S402Activity.this, StringUtils.S402_1_HELP_CD);
				break;
			default:
				break;
			}
		}
	}
	//
	// public void inventoryAnalysis(View v) {
	//
	// int position = Integer.parseInt(v.getTag().toString());
	// String hin_cd = apiModel.getApi040().get(position).getHin_cd();
	// Intent i = new Intent(this, S304Activity.class);
	// i.putExtra(JSONUtils.HIN_CODE, hin_cd);
	// startActivity(i);
	// }
}
