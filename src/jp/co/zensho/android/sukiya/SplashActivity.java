package jp.co.zensho.android.sukiya;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.zensho.android.sukiya.bean.DailyCheckboxValue;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import jp.co.zensho.android.sukiya.bean.LocationInfo;
import jp.co.zensho.android.sukiya.bean.MenuPositionInfo;
import jp.co.zensho.android.sukiya.common.JSONUtils;
import jp.co.zensho.android.sukiya.common.StringUtils;
import jp.co.zensho.android.sukiya.database.helper.DatabaseHelper;
import jp.co.zensho.android.sukiya.database.model.CategoryInfo;
import jp.co.zensho.android.sukiya.database.model.SukiyaSetting;
import jp.co.zensho.android.sukiya.service.CallAPIAsyncTask;
import jp.co.zensho.android.sukiya.service.CallAPIListener;
import jp.co.zensho.android.sukiya.service.DownloadZipImageAsyncTask;
import jp.co.zensho.android.sukiya.service.DownloadZipImageListener;
import jp.co.zensho.android.sukiya.service.IPAddressAsyncTask;
import jp.co.zensho.android.sukiya.service.IPAddressListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;

public class SplashActivity extends S00Activity implements IPAddressListener,
		CallAPIListener, DownloadZipImageListener {

	private static final String LOG = "SplashActivity";

	// Call Wifi setting dialog listener
	private DialogInterface.OnClickListener wifiSettingDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
			startActivity(intent);
		}
	};

	// Call Load IP Address dialog listener
	private DialogInterface.OnClickListener reloadIPDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			SplashActivity.this.loadIPAddress();
		}
	};

	// Call API001 dialog listener
	private DialogInterface.OnClickListener retryAPI001DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			SplashActivity.this.callAPI001();
		}
	};

	// Call API001-1 dialog listener
	private DialogInterface.OnClickListener retryAPI001_1DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			SplashActivity.this.downloadZipImageFile();
		}
	};

	// Call API002 dialog listener
	private DialogInterface.OnClickListener retryAPI002DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			SplashActivity.this.callAPI002();
		}
	};

	// Call API003 dialog listener
	private DialogInterface.OnClickListener retryAPI003DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			SplashActivity.this.callAPI003();
		}
	};

	// Call API004 dialog listener
	private DialogInterface.OnClickListener retryAPI004DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			SplashActivity.this.callAPI004();
		}
	};

	// Call API005 dialog listener
	private DialogInterface.OnClickListener retryAPI005DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			SplashActivity.this.callAPI005();
		}
	};

	// Call API007 dialog listener
	private DialogInterface.OnClickListener retryAPI007DialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			SplashActivity.this.callAPI007();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		Log.d(LOG, "onCreate - START");
		// load setting
		super.db = new DatabaseHelper(getApplicationContext());
		super.app.setInfo(super.db.getSetting());
		super.db.close();
		Log.d(LOG, "onCreate - END");
	}

	@Override
	protected void onResume() {
		// Check network
		if (super.isConnected()) {
			// has network connection
			// Show process
			this.showProcessDialog(this, null, null);

			// Load IP Address
			this.loadIPAddress();
		} else {
			// no network connection
			super.showDialog(this, R.string.msg_001_no_intenet,
					R.string.wifi_setting, wifiSettingDialogListener,
					R.string.exit_app, exitApplicationDialogListener);
		}
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

	@Override
	protected void onStop() {
		Log.d(LOG, "[updateCurrentDate] onStop Start");
		super.removeUpdateCurrentDate();
		Log.d(LOG, "[updateCurrentDate] onStop END");
		super.onStop();
	}

	@Override
	public void loadIPAddressFinish(String ipAddress) {
		Log.d(LOG, "loadIPAddressFinish - START");
		Log.d(LOG, "loadIPAddressFinish - IP Address: " + ipAddress);
		if (StringUtils.isEmpty(ipAddress)) {
			// not found ipAddress
			super.showDialog(this, R.string.msg_002_no_ip_address,
					R.string.try_again, reloadIPDialogListener,
					R.string.exit_app, exitApplicationDialogListener);
		} else {
			super.app.setRemoteAddress(ipAddress);

			// Call API001 to load shop info
			this.callAPI001();
		}
		Log.d(LOG, "loadIPAddressFinish - END");
	}

	@Override
	public void callAPIFinish(String tag, JSONObject result, ErrorInfo error) {
		Log.d(LOG, "callAPIFinish - START");
		Log.d(LOG, "callAPIFinish - TAG: " + tag);
		if (error == null && result != null) {
			try {
				if (!JSONUtils.hasError(result)) {
					if (JSONUtils.API_001_PATH.equals(tag)) {
						// finish call API001
						String shopCode = result
								.getString(JSONUtils.TENPO_CODE);
						String shopName = result
								.getString(JSONUtils.TENPO_NAME);

						// save shop info
						SukiyaSetting info = super.app.getInfo();
						info.setShopCode(shopCode);
						info.setShopName(shopName);
						super.app.setInfo(info);

						super.db = new DatabaseHelper(getApplicationContext());
						super.db.updateSetting(info);
						super.db.close();
					} else if (JSONUtils.API_002_PATH.equals(tag)) {
						// finish call API002
						List<MenuPositionInfo> menuList = JSONUtils
								.parseMenuList(result);
						super.app.setMenuPositionList(menuList);
					} else if (JSONUtils.API_003_PATH.equals(tag)) {
						// finish call API003
						super.app.setKpiInfo(JSONUtils.parseKPIInfo(result));
					} else if (JSONUtils.API_004_PATH.equals(tag)) {
						// finish call API004
						super.app.setKpiRangeInfo(JSONUtils
								.parseKPIRangeInfo(result));
					} else if (JSONUtils.API_005_PATH.equals(tag)) {
						// finish call API005
						List<DailyCheckboxValue> categoryList = JSONUtils
								.parseCategoryInfoList(result);

						// Check history on db
						super.db = new DatabaseHelper(getApplicationContext());
						List<CategoryInfo> categoryHistoryList = super.db
								.getAllCategoryHistory();
						if (categoryList != null && categoryHistoryList != null
								&& categoryHistoryList.size() > 0) {
							for (CategoryInfo categoryInfo : categoryHistoryList) {
								for (DailyCheckboxValue value : categoryList) {
									if (categoryInfo.getId().equals(
											value.getCode())) {
										value.setSelected(true);
										value.setCanUnselect(false);
										break;
									}
								}
							}
						}
						super.db.close();

						super.app.setDailyCheckboxList(categoryList);
					} else if (JSONUtils.API_007_PATH.equals(tag)) {
						// finish call API007
						List<LocationInfo> locationList = JSONUtils
								.parseLocationList(result);
						super.app.setLocationList(locationList);
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

			DialogInterface.OnClickListener positiveListener = null;
			if (JSONUtils.API_001_PATH.equals(tag)) {
				positiveListener = retryAPI001DialogListener;
			} else if (JSONUtils.API_002_PATH.equals(tag)) {
				positiveListener = retryAPI002DialogListener;
			} else if (JSONUtils.API_003_PATH.equals(tag)) {
				positiveListener = retryAPI003DialogListener;
			} else if (JSONUtils.API_004_PATH.equals(tag)) {
				positiveListener = retryAPI004DialogListener;
			} else if (JSONUtils.API_005_PATH.equals(tag)) {
				positiveListener = retryAPI005DialogListener;
			} else if (JSONUtils.API_007_PATH.equals(tag)) {
				positiveListener = retryAPI007DialogListener;
			}

			// show error dialog
			super.showDialog(this, message, R.string.try_again,
					positiveListener, R.string.exit_app,
					exitApplicationDialogListener);
		} else {
			if (JSONUtils.API_001_PATH.equals(tag)) {
				// Next init step
				this.callAPI002();
			} else if (JSONUtils.API_002_PATH.equals(tag)) {
				// Next init step
				this.callAPI003();
			} else if (JSONUtils.API_003_PATH.equals(tag)) {
				// Next init step
				this.callAPI004();
			} else if (JSONUtils.API_004_PATH.equals(tag)) {
				// Next init step
				this.callAPI005();
			} else if (JSONUtils.API_005_PATH.equals(tag)) {
				// Next init step
				this.callAPI007();
			} else if (JSONUtils.API_007_PATH.equals(tag)) {
				// Next init step
				this.downloadZipImageFile();
			}
		}
		Log.d(LOG, "callAPIFinish - END");
	}

	@Override
	public void downloadZipImageFinish(ErrorInfo error) {
		Log.d(LOG, "downloadZipImageFinish - START");
		if (error != null) {
			String message = error.getMessage();
			if (!StringUtils.isEmpty(error.getCode())) {
				message = MessageFormat.format("{0}: {1}", error.getCode(),
						error.getMessage());
			}

			super.showDialog(this, message, R.string.try_again,
					retryAPI001_1DialogListener, R.string.exit_app,
					exitApplicationDialogListener);
		} else {
			// download zip file complete
			SukiyaSetting info = super.app.getInfo();
			info.setDownloadZipState(1); // complete

			super.app.setInfo(info);

			super.db = new DatabaseHelper(getApplicationContext());
			super.db.updateSetting(info);
			super.db.close();

			this.gotoTopView();
		}
		Log.d(LOG, "downloadZipImageFinish - END");
	}

	private void gotoTopView() {
		Log.d(LOG, "gotoTopView - START");
		// show process dialog
		this.dismissProcessDialog();

		Intent intent = new Intent(getApplication(), S01Activity.class);
		startActivity(intent);
		this.finish();
		Log.d(LOG, "gotoTopView - END");
	}

	private void loadIPAddress() {
        Log.d(LOG, "loadIPAddress - START");
        new IPAddressAsyncTask(this).execute();
        Log.d(LOG, "loadIPAddress - END");
	}

	private void callAPI001() {
		Log.d(LOG, "callAPI001 - START");
		// Call API001
		String shopCode = super.app.getShopCode();
		String shopName = super.app.getShopName();
		Log.d(LOG, "callAPI001 - ShopCode: " + shopCode + " / ShopName: "
				+ shopName);

		if (shopCode == null && shopName == null) {
			// Get shop info from API
			if (!StringUtils.isEmpty(super.app.getRemoteAddress())) {
				Map<String, String> params = new HashMap<String, String>();
				params.put(JSONUtils.IP_ADDRESS, super.app.getRemoteAddress());

				try {
					String url = JSONUtils.getAPIUrl(JSONUtils.API_001_PATH,
							params);
					Log.d(LOG, "URL: " + url);

					// show process dialog
					this.showProcessDialog(this, null, null);

					new CallAPIAsyncTask(this, JSONUtils.API_001_PATH)
							.execute(url);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();

					// show error dialog
					String strMessage = MessageFormat.format(
							getString(R.string.msg_003_system_error),
							e.getMessage());
					super.showDialog(this, strMessage, R.string.try_again,
							retryAPI001DialogListener, R.string.exit_app,
							exitApplicationDialogListener);
				}
			} else {
				// show error dialog
				super.showDialog(this, R.string.msg_002_no_ip_address,
						R.string.try_again, reloadIPDialogListener,
						R.string.exit_app, exitApplicationDialogListener);
			}
		} else {
			this.callAPI002();
		}
		Log.d(LOG, "callAPI001 - END");
	}

	private void callAPI002() {
		Log.d(LOG, "callAPI002 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get Menu info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_002_PATH, params);

				// show process dialog
				this.showProcessDialog(this, null, null);

				new CallAPIAsyncTask(this, JSONUtils.API_002_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = MessageFormat.format(
						getString(R.string.msg_003_system_error),
						e.getMessage());
				super.showDialog(this, message, R.string.try_again,
						retryAPI002DialogListener, R.string.exit_app,
						exitApplicationDialogListener);
			}
		} else {
			// Show error dialog
			super.showDialog(this, R.string.msg_004_shop_code_not_found,
					R.string.try_again, retryAPI001DialogListener,
					R.string.exit_app, exitApplicationDialogListener);
		}
		Log.d(LOG, "callAPI002 - END");
	}

	private void callAPI003() {
		Log.d(LOG, "callAPI003 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get KPI info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_003_PATH, params);

				// show process dialog
				this.showProcessDialog(this, null, null);

				new CallAPIAsyncTask(this, JSONUtils.API_003_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = MessageFormat.format(
						getString(R.string.msg_003_system_error),
						e.getMessage());
				super.showDialog(this, message, R.string.try_again,
						retryAPI003DialogListener, R.string.exit_app,
						exitApplicationDialogListener);
			}
		} else {
			// Show error dialog
			super.showDialog(this, R.string.msg_004_shop_code_not_found,
					R.string.try_again, retryAPI001DialogListener,
					R.string.exit_app, exitApplicationDialogListener);
		}
		Log.d(LOG, "callAPI003 - END");
	}

	private void callAPI004() {
		Log.d(LOG, "callAPI004 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get KPI info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_004_PATH, params);

				// show process dialog
				this.showProcessDialog(this, null, null);

				new CallAPIAsyncTask(this, JSONUtils.API_004_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = MessageFormat.format(
						getString(R.string.msg_003_system_error),
						e.getMessage());
				super.showDialog(this, message, R.string.try_again,
						retryAPI004DialogListener, R.string.exit_app,
						exitApplicationDialogListener);
			}
		} else {
			// Show error dialog
			super.showDialog(this, R.string.msg_004_shop_code_not_found,
					R.string.try_again, retryAPI001DialogListener,
					R.string.exit_app, exitApplicationDialogListener);
		}
		Log.d(LOG, "callAPI004 - END");
	}

	private void callAPI005() {
		Log.d(LOG, "callAPI005 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get KPI info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_005_PATH, params);

				// show process dialog
				this.showProcessDialog(this, null, null);

				new CallAPIAsyncTask(this, JSONUtils.API_005_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = MessageFormat.format(
						getString(R.string.msg_003_system_error),
						e.getMessage());
				super.showDialog(this, message, R.string.try_again,
						retryAPI005DialogListener, R.string.exit_app,
						exitApplicationDialogListener);
			}
		} else {
			// Show error dialog
			super.showDialog(this, R.string.msg_004_shop_code_not_found,
					R.string.try_again, retryAPI001DialogListener,
					R.string.exit_app, exitApplicationDialogListener);
		}
		Log.d(LOG, "callAPI005 - END");
	}

	private void callAPI007() {
		Log.d(LOG, "callAPI007 - START");
		if (!StringUtils.isEmpty(super.app.getShopCode())) {
			// Get KPI info from API
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_007_PATH, params);

				// show process dialog
				this.showProcessDialog(this, null, null);

				new CallAPIAsyncTask(this, JSONUtils.API_007_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = MessageFormat.format(
						getString(R.string.msg_003_system_error),
						e.getMessage());
				super.showDialog(this, message, R.string.try_again,
						retryAPI007DialogListener, R.string.exit_app,
						exitApplicationDialogListener);
			}
		} else {
			// Show error dialog
			super.showDialog(this, R.string.msg_004_shop_code_not_found,
					R.string.try_again, retryAPI001DialogListener,
					R.string.exit_app, exitApplicationDialogListener);
		}
		Log.d(LOG, "callAPI007 - END");
	}

	private void downloadZipImageFile() {
		super.dismissProcessDialog();
		Log.d(LOG, "downloadZipImageFile - START");
		int zipImageState = super.app.getDownloadZipState();
		if (zipImageState != 1) {
			if (!StringUtils.isEmpty(super.app.getShopCode())) {
				Map<String, String> params = new HashMap<String, String>();
				params.put(JSONUtils.TENPO_CODE, super.app.getShopCode());

				try {
					String stringUrl = JSONUtils.getAPIUrl(
							JSONUtils.API_001_IMAGE_PATH, params);

					new DownloadZipImageAsyncTask(this, this)
							.execute(stringUrl);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();

					// Show error dialog
					String message = MessageFormat.format(
							getString(R.string.msg_003_system_error),
							e.getMessage());
					super.showDialog(this, message, R.string.try_again,
							retryAPI001_1DialogListener, R.string.exit_app,
							exitApplicationDialogListener);
				}
			} else {
				// show error dialog
				super.showDialog(this, R.string.msg_004_shop_code_not_found,
						R.string.try_again, retryAPI001DialogListener,
						R.string.exit_app, exitApplicationDialogListener);
			}
		} else {
			this.gotoTopView();
		}
		Log.d(LOG, "downloadZipImageFile - END");
	}
}
