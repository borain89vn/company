package jp.co.zensho.android.sukiya;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.zensho.android.sukiya.application.SukiyaApplication;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import jp.co.zensho.android.sukiya.bean.MenuPositionInfo;
import jp.co.zensho.android.sukiya.common.JSONUtils;
import jp.co.zensho.android.sukiya.common.StringUtils;
import jp.co.zensho.android.sukiya.common.SukiyaUtils;
import jp.co.zensho.android.sukiya.database.helper.DatabaseHelper;
import jp.co.zensho.android.sukiya.database.model.SukiyaSetting;
import jp.co.zensho.android.sukiya.service.CallAPIAsyncTask;
import jp.co.zensho.android.sukiya.service.CallAPIListener;
import jp.co.zensho.android.sukiya.service.DownloadZipImageAsyncTask;
import jp.co.zensho.android.sukiya.service.DownloadZipImageListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Process;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class S00Activity extends Activity implements CallAPIListener {
	protected static final BigDecimal MAX_AMOUNT_VALUE = new BigDecimal("1000");

	private static final String LOG = "S00Activity";

	private TimerTask updateCurrentDateTimerTask;

	protected SukiyaApplication app;
	protected ProgressDialog progress;
	protected AlertDialog alert;
	protected Typeface meiryoFace;

	// Database Helper
	protected DatabaseHelper db;

	protected Button mBtnMenu;

	protected LinearLayout mMainContentLinearLayout;
	protected SimpleDateFormat dateFormat;

	protected ListView mListView;
	protected List<MenuItem> menuList;
	protected ArrayAdapter<MenuItem> menuListAdapter;
	protected static final long menuAnimationDuration = 300L;
	protected static final int menuMovement = 240;
	protected TranslateAnimation animationMenuOpen;
	protected TranslateAnimation animationMenuClose;

	// vdngo
	SharedPreferences prefs;
	protected String helpString = "";
	private Context context;
	private boolean isTestConnection = false;

	// Zenshou ntdat 20140401 add ++
	protected Toast toast;
	// Zenshou ntdat 20140401 add --

	// Dismiss dialog listener
	protected DialogInterface.OnClickListener dismissDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
		}
	};

	// Exit application dialog listener
	protected DialogInterface.OnClickListener exitApplicationDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			Process.killProcess(Process.myPid());

		}
	};

	// Reload shop info dialog listener
	private DialogInterface.OnClickListener reloadShopInfoDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S00Activity.this.updateShopInfo();
		}
	};

	// Update main menu dialog listener
	private DialogInterface.OnClickListener redownloadZipImageDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog != null) {
				dialog.dismiss();
			}
			S00Activity.this.downloadZipImage();
		}
	};

	final Runnable updateDateRunnable = new Runnable() {
		public void run() {
			updateDateView();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setContentView(R.layout.activity_sukiya_base);
		Log.d(LOG, "onCreate - START");

		/**
		 * @author vdngo
		 */
		// SharedPreferences prefs = this.getSharedPreferences(
		// JSONUtils.HOST_SELECTION, Context.MODE_PRIVATE);
		prefs = this.getSharedPreferences(JSONUtils.HOST_SELECTION,
				Context.MODE_PRIVATE);
		String host = prefs.getString(JSONUtils.HOST_SELECTION, null);
		if (host != null) {
			JSONUtils.API_HOST_PATH = host;
		}

		this.app = (SukiyaApplication) this.getApplication();
		this.meiryoFace = Typeface.createFromAsset(this.getAssets(),
				"fonts/meiryo.ttc");

		Log.d(LOG, "onCreate - END");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sukiya_base, menu);
		return true;
	}

	@Override
	protected void onResume() {
		Log.d(LOG, "S00Activity onResume");
		super.onResume();

		// Current date
		this.updateCurrentDate();
	}

	private void updateCurrentDate() {
		Log.d(LOG, "[updateCurrentDate]updateCurrentDate - START");
		// Cancel current timer
		if (updateCurrentDateTimerTask != null) {
			updateCurrentDateTimerTask.cancel();
			updateCurrentDateTimerTask = null;
		}

		// calc current date
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Date refreshTime = null;
		if (calendar.get(Calendar.HOUR_OF_DAY) < 9) {
			// refresh time
			calendar.set(Calendar.HOUR_OF_DAY, 9);

			refreshTime = calendar.getTime();

			// current date
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);

			this.app.setCurrentDate(calendar.getTime());
		} else {
			// current date
			calendar.set(Calendar.HOUR_OF_DAY, 0);

			Date curDte = calendar.getTime();
			this.app.setCurrentDate(curDte);

			// refresh time
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 9);

			refreshTime = calendar.getTime();
		}
		runOnUiThread(updateDateRunnable);

		DateFormat df = DateFormat.getDateTimeInstance();
		Log.d(LOG,
				"[updateCurrentDate]Current date: "
						+ df.format(this.app.getCurrentDate()));
		Log.d(LOG, "[updateCurrentDate]Refresh date: " + df.format(refreshTime));

		// update schedule
		updateCurrentDateTimerTask = new TimerTask() {
			@Override
			public void run() {
				S00Activity.this.updateCurrentDate();
			}
		};
		Timer timer = new Timer();
		timer.schedule(updateCurrentDateTimerTask, refreshTime);
		Log.d(LOG, "[updateCurrentDate]updateCurrentDate - END");

	}

	protected void generateViews() {
		Log.d(LOG, "generateViews - START");
		mMainContentLinearLayout = (LinearLayout) findViewById(R.id.main_content);

		mBtnMenu = (Button) findViewById(R.id.btn_menu);
		mBtnMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.d(LOG, "Menu button clicked - START");
				if (mMainContentLinearLayout.getAnimation() != null) {
					// アニメーション中なので何もしない
				} else {
					LinearLayout mLinearLayoutPopupStartDaily = (LinearLayout) S00Activity.this
							.findViewById(R.id.linearlayout_popup_start_daily);
					if (mLinearLayoutPopupStartDaily != null) {
						if (View.INVISIBLE == mLinearLayoutPopupStartDaily
								.getVisibility()) {
							final int mainW = mMainContentLinearLayout
									.getWidth();
							final int mainH = mMainContentLinearLayout
									.getHeight();
							final int openEndX = menuMovement;

							if (mMainContentLinearLayout.getLeft() == 0) {
								openMenu(mainW, mainH, openEndX);
							} else {
								closeMenu(mainW, mainH, openEndX);
							}
						}
					} else {
						final int mainW = mMainContentLinearLayout.getWidth();
						final int mainH = mMainContentLinearLayout.getHeight();
						final int openEndX = menuMovement;

						if (mMainContentLinearLayout.getLeft() == 0) {
							openMenu(mainW, mainH, openEndX);
						} else {
							closeMenu(mainW, mainH, openEndX);
						}
					}
				}
				Log.d(LOG, "Menu button clicked - END");
			}

		});

		menuList = new ArrayList<MenuItem>();

		MenuItem menuItem1 = new MenuItem();
		menuItem1.menuName = getString(R.string.lbl_slidermenu_01);

		menuList.add(menuItem1);

		MenuItem menuItem2 = new MenuItem();
		menuItem2.menuName = getString(R.string.lbl_slidermenu_02);
		menuList.add(menuItem2);

		MenuItem menuItem3 = new MenuItem();
		menuItem3.menuName = getString(R.string.lbl_slidermenu_03);
		menuList.add(menuItem3);

		MenuItem menuItem4 = new MenuItem();
		menuItem4.menuName = getString(R.string.lbl_slidermenu_04);
		menuList.add(menuItem4);

		menuListAdapter = new MenuListAdapter(this, R.layout.menu_list_row,
				menuList);

		mListView = (ListView) findViewById(R.id.menu_list);
		mListView.setAdapter(menuListAdapter);
		mListView.setScrollingCacheEnabled(false);
		mListView.setBackgroundColor(Color.argb(0, 0, 0, 0));
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(LOG, "Menu item clicked - START");
				Log.d(LOG, "position: " + position + " / id: " + id);
				final int mainW = mMainContentLinearLayout.getWidth();
				final int mainH = mMainContentLinearLayout.getHeight();
				final int openEndX = menuMovement;

				Log.d("Click menu: ", "tapping menu " + position);
				switch (position) {
				case 0: // refresh shop info
					S00Activity.this.updateShopInfo();
					break;
				case 1: // refresh menu info
					S00Activity.this.downloadZipImage();
					break;
				case 2:
					S00Activity.this.showAppVersion();
					break;
				case 3:
					S00Activity.this.hostSelection();
					break;
				default:
					break;
				}

				closeMenu(mainW, mainH, openEndX);
				Log.d(LOG, "Menu item clicked - END");
			}

		});

		// Update current date
		TextView dateTextView = (TextView) findViewById(R.id.current_date);
		if (dateTextView != null && this.app.getCurrentDate() != null) {
			dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.JAPAN);
			dateTextView.setText(dateFormat.format(this.app.getCurrentDate()));
		}

		// Update Shop name
		TextView shopNameTextView = (TextView) findViewById(R.id.shop_name);
		if (shopNameTextView != null) {
			shopNameTextView.setText(this.app.getShopName());
		}
		Log.d(LOG, "generateViews - END");
	}

	public boolean canCloseMenu() {
		Log.d(LOG, "canCloseMenu - START");
		final int mainW = mMainContentLinearLayout.getWidth();
		final int mainH = mMainContentLinearLayout.getHeight();
		final int openEndX = menuMovement;

		if (mMainContentLinearLayout.getLeft() == 0) {
			Log.d(LOG, "canCloseMenu - END");
			return false;
		} else {
			closeMenu(mainW, mainH, openEndX);
			Log.d(LOG, "canCloseMenu - END");
			return true;
		}
	}

	public void closeMenu(final int mainW, final int mainH, final int openEndX) {
		Log.d(LOG, "closeMenu - START");
		Log.d(LOG, "mainW: " + mainW + " / mainH: " + mainH + " / openEndX: "
				+ openEndX);
		mListView.setEnabled(false);
		TranslateAnimation animMenuClose = new TranslateAnimation(0, openEndX,
				0, 0);

		animMenuClose.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mMainContentLinearLayout.layout(0, 0, mainW, mainH);// 物理的にも移動
				mMainContentLinearLayout.setAnimation(null);// これをしないとアニメーション完了後にチラつく
				// mListView.setVisibility(View.GONE);

			}

		});

		animMenuClose.setDuration(menuAnimationDuration);
		mMainContentLinearLayout.startAnimation(animMenuClose);
		Log.d(LOG, "closeMenu - END");
	}

	public void openMenu(final int mainW, final int mainH, final int openEndX) {
		Log.d(LOG, "openMenu - START");
		Log.d(LOG, "mainW: " + mainW + " / mainH: " + mainH + " / openEndX: "
				+ openEndX);
		// mListView.setVisibility(View.VISIBLE);
		TranslateAnimation animMenuOpen = new TranslateAnimation(0, -openEndX,
				0, 0);
		animMenuOpen.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mMainContentLinearLayout.layout(-openEndX, 0,
						-openEndX + mainW, mainH);// 物理的にも移動
				mMainContentLinearLayout.setAnimation(null);// これをしないとアニメーション完了後にチラつく
				mListView.setEnabled(true);
			}

		});

		animMenuOpen.setDuration(menuAnimationDuration);
		mMainContentLinearLayout.startAnimation(animMenuOpen);
		Log.d(LOG, "openMenu - END");
	}

	/**
	 * Check network connection.
	 *
	 * @author lncong
	 * @return TRUE: Connected / FALSE: No connection
	 */
	public boolean isConnected() {
		Log.d(LOG, "isConnected - START");
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			Log.d(LOG, "isConnected - END");
			return true;
		} else {
			Log.d(LOG, "isConnected - END");
			return false;
		}
	}

	public void setMeiryoFont(int viewId) {
		Log.d(LOG, "setMeiryoFont - START");
		View view = super.findViewById(viewId);
		if (view != null) {
			if (view instanceof TextView) {
				TextView textView = (TextView) view;
				textView.setTypeface(this.meiryoFace);
			} else if (view instanceof Button) {
				Button button = (Button) view;
				button.setTypeface(this.meiryoFace);
			}
		}
		Log.d(LOG, "setMeiryoFont - END");
	}

	private void updateShopInfo() {
		Log.d(LOG, "updateShopInfo - START");
		// Get shop info from API
		if (!StringUtils.isEmpty(this.app.getRemoteAddress())) {
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.IP_ADDRESS, this.app.getRemoteAddress());

			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_001_PATH, params);

				// show process dialog
				this.showProcessDialog(this, null, null);
				new CallAPIAsyncTask(new CallAPIListener() {
					@Override
					public void callAPIFinish(String tag, JSONObject result,
							ErrorInfo error) {
						// dismiss process dialog
						S00Activity.this.dismissProcessDialog();
						if (error == null && result != null) {
							try {
								if (!JSONUtils.hasError(result)) {
									// finish call API001
									String shopCode = result.getString(JSONUtils.TENPO_CODE);
									String shopName = result
											.getString(JSONUtils.TENPO_NAME);

									S00Activity.this.updateShopInfoGUI(
											shopCode, shopName);
								} else {
									if (error == null) {
										error = JSONUtils.getFirstError(result);
									}
								}
							} catch (JSONException e) {
								if (error == null) {
									error = new ErrorInfo();
									error.setMessage(MessageFormat.format(
											"System error: {0}", e.getMessage()));
								}
								e.printStackTrace();
							}
						}

						if (error != null) {
							String message = error.getMessage();
							if (!StringUtils.isEmpty(error.getCode())) {
								message = MessageFormat.format("{0}: {1}",
										error.getCode(), error.getMessage());
							}

							// Show error dialog
							S00Activity.this.showDialog(S00Activity.this,
									message, R.string.try_again,
									reloadShopInfoDialogListener,
									R.string.cancel, dismissDialogListener);
						}
					}
				}, null).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = MessageFormat.format(
						getString(R.string.msg_003_system_error),
						e.getMessage());
				this.showDialog(this, message, R.string.try_again,
						reloadShopInfoDialogListener, R.string.cancel,
						dismissDialogListener);
			}
		} else {
			// Show error dialog
			this.showDialog(this, R.string.msg_002_no_ip_address, R.string.ok,
					dismissDialogListener, 0, null);
		}
		Log.d(LOG, "updateShopInfo - END");
	}

	private void updateShopInfoGUI(String shopCode, String shopName) {
		Log.d(LOG, "updateShopInfoGUI - START");
		if (!StringUtils.isEmpty(shopCode) && !StringUtils.isEmpty(shopName)) {
			// save to SharedPreferences
			SharedPreferences sharedPref = PreferenceManager
					.getDefaultSharedPreferences(this);
			Editor editor = sharedPref.edit();
			editor.putString("shop_code", shopCode);
			editor.putString("shop_name", shopName);
			editor.commit();

			this.app.getInfo().setShopCode(shopCode);
			this.app.getInfo().setShopName(shopName);

			// Update Shop name
			TextView shopNameTextView = (TextView) findViewById(R.id.shop_name);
			if (shopNameTextView != null) {
				shopNameTextView.setText(this.app.getShopName());
			}
		}
		Log.d(LOG, "updateShopInfoGUI - END");
	}

	private void downloadZipImage() {
		if (!StringUtils.isEmpty(this.app.getShopCode())) {
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.TENPO_CODE, this.app.getShopCode());

			try {
				String stringUrl = JSONUtils.getAPIUrl(
						JSONUtils.API_001_IMAGE_PATH, params);

				new DownloadZipImageAsyncTask(this,
						new DownloadZipImageListener() {
							@Override
							public void downloadZipImageFinish(ErrorInfo error) {
								if (error != null) {
									String message = error.getMessage();
									if (!StringUtils.isEmpty(error.getCode())) {
										message = MessageFormat.format(
												"{0}: {1}", error.getCode(),
												error.getMessage());
									}

									S00Activity.this.showDialog(
											S00Activity.this, message,
											R.string.try_again,
											redownloadZipImageDialogListener,
											R.string.cancel,
											dismissDialogListener);
								} else {
									// download zip file complete
									SukiyaSetting info = S00Activity.this.app
											.getInfo();
									info.setDownloadZipState(1); // complete

									S00Activity.this.app.setInfo(info);

									S00Activity.this.db = new DatabaseHelper(
											getApplicationContext());
									S00Activity.this.db.updateSetting(info);
									S00Activity.this.db.close();
								}
							}
						}).execute(stringUrl);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				// Show error dialog
				String message = MessageFormat.format(
						getString(R.string.msg_003_system_error),
						e.getMessage());
				this.showDialog(this, message, R.string.try_again,
						redownloadZipImageDialogListener, R.string.cancel,
						dismissDialogListener);
			}
		} else {
			// show error dialog
			this.showDialog(this, R.string.msg_004_shop_code_not_found,
					R.string.try_again, redownloadZipImageDialogListener,
					R.string.cancel, dismissDialogListener);
		}
	}

	private void showAppVersion() {
		try {
			Context context = getApplicationContext();
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			String message = getString(R.string.msg_013_application_version);
			message = MessageFormat.format(message, packageInfo.versionName);
			this.showDialog(this, message, R.string.ok, dismissDialogListener,
					0, null);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @author vdngo
	 */
	private void hostSelection() {
		showDiaglogPassword(this, getResources().getString(R.string.config));
	}

	private void updateDateView() {
		// Update current date
		TextView dateTextView = (TextView) findViewById(R.id.current_date);
		if (dateTextView != null) {
			dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.JAPAN);
			dateTextView.setText(dateFormat.format(this.app.getCurrentDate()));
		}
	}

	protected LinearLayout getMainButtonAtIndex(int index) {
		Log.d(LOG, "index: " + index);
		int viewId = getResources().getIdentifier("menu_button_" + index, "id",
				getPackageName());
		LinearLayout buttonView = (LinearLayout) findViewById(viewId);
		return buttonView;
	}

	/**
	 * @author vdngo
	 * @param activity
	 * @param password
	 */
	private void showDiaglogPassword(final Activity activity,
			final String password) {
		this.dismissProcessDialog();

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		alertDialog
				.setTitle(getResources().getString(R.string.password_prompt));

		final EditText input = new EditText(activity);
		input.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		input.setBackground(getResources().getDrawable(R.drawable.grey_border));
		input.setPadding(10, 10, 10, 10);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(10, 0, 0, 10);
		input.setLayoutParams(lp);
		alertDialog.setView(input);

		// Setting Positive "Yes" Button
		alertDialog.setPositiveButton(getResources().getString(R.string.next),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String pass = input.getText().toString();

						if (pass.equals(password)) {
							Toast.makeText(getApplicationContext(),
									"Password Matched", Toast.LENGTH_SHORT)
									.show();
							showDiaglogHostSelection(activity);
						} else {
							showDiaglogPassword(activity, password);
							Toast.makeText(getApplicationContext(),
									"Wrong Password!", Toast.LENGTH_SHORT)
									.show();

						}
					}
				});

		alertDialog.show();
	}

	/**
	 * @author vdngo
	 * @param activity
	 * @param password
	 */
	private void showDiaglogHostSelection(final Activity activity) {
		this.dismissProcessDialog();

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		alertDialog.setTitle(getResources().getString(R.string.host_prompt));

		// Setting Dialog Message
		// alertDialog.setMessage(getResources().getString(R.string.host_prompt));
		final EditText input = new EditText(activity);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setBackground(getResources().getDrawable(R.drawable.grey_border));
		input.setPadding(10, 10, 10, 10);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(10, 0, 0, 10);
		input.setLayoutParams(lp);
		alertDialog.setView(input);

		// Setting Positive "Yes" Button
		alertDialog.setPositiveButton(
				getResources().getString(R.string.button_setting),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String host_input = input.getText().toString();
						if (StringUtils.EMPTY.equals(host_input)) {
							dialog.dismiss();
							showDialog(activity, R.string.config,
									R.string.ok_promt,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// Do nothing

										}
									}, 0, null);
						} else {
							dialog.dismiss();
							if (!host_input.contains("http")) {
								host_input = "http://" + host_input;
							}
							boolean isUrl = ping(
									host_input
											+ JSONUtils.API_002_PATH + "?"
											+ JSONUtils.TENPO_CODE + "="
											+ app.getShopCode(), 10000);
							Log.e("url",
									host_input
											+ JSONUtils.API_002_PATH + "?"
											+ JSONUtils.TENPO_CODE + "="
											+ app.getShopCode());
							if (isUrl) {
								Editor editor = prefs.edit();
								editor.putString(JSONUtils.HOST_SELECTION,host_input );
								editor.commit();
								JSONUtils.API_HOST_PATH = host_input;
							} else {
								showDialog(
										S00Activity.this,
										R.string.invalid_host,
										R.string.ok_promt,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
												showDiaglogHostSelection(activity);

											}
										}, R.string.cancel_prompt,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub

											}
										});
							}

						}
					}
				});
		// Setting Negative "NO" Button
		alertDialog.setNegativeButton(
				getResources().getString(R.string.button_cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to execute after dialog
						dialog.dismiss();
					}
				});

		// closed

		// Showing Alert Message
		alertDialog.show();
	}

	protected TextView getTextViewOnMainButtonAtIndex(int btIndex, int tvIndex) {
		int viewId = getResources().getIdentifier(
				"menu_button_" + btIndex + "_label_" + tvIndex, "id",
				getPackageName());
		TextView textView = (TextView) findViewById(viewId);
		return textView;
	}

	protected MenuPositionInfo getMenuPositionInfo(int x, int y) {
		List<MenuPositionInfo> menuList = this.app.getMenuPositionList();
		if (menuList != null && menuList.size() > 0) {
			for (MenuPositionInfo menuPos : menuList) {
				if (x == menuPos.getX() && y == menuPos.getY()) {
					return menuPos;
				}
			}
		}
		return null;
	}

	protected void showDialog(Activity activity, int messageId,
			int positiveStringId,
			DialogInterface.OnClickListener positiveListener,
			int negativeStringId,
			DialogInterface.OnClickListener negativeListener) {
		// dismiss process dialog
		this.dismissProcessDialog();

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(false);
		builder.setMessage(messageId);
		if (positiveListener != null) {
			builder.setPositiveButton(positiveStringId, positiveListener);
		}
		if (negativeListener != null) {
			builder.setNegativeButton(negativeStringId, negativeListener);
		}
		this.alert = builder.create();
		this.alert.show();
	}

	protected void showDialog(Activity activity, String messageContent,
			int positiveStringId,
			DialogInterface.OnClickListener positiveListener,
			int negativeStringId,
			DialogInterface.OnClickListener negativeListener) {
		// dismiss process dialog
		this.dismissProcessDialog();

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(false);
		builder.setMessage(messageContent);
		if (positiveListener != null) {
			builder.setPositiveButton(positiveStringId, positiveListener);
		}
		if (negativeListener != null) {
			builder.setNegativeButton(negativeStringId, negativeListener);
		}
		this.alert = builder.create();
		this.alert.show();
	}

	protected void showProcessDialog(Activity activity, String title,
			String message) {
		if (this.progress == null || !this.progress.isShowing()) {
			if (StringUtils.isEmpty(title)) {
				title = getString(R.string.working);
			}
			if (StringUtils.isEmpty(message)) {
				message = getString(R.string.msg_005_loading);
			}
			this.progress = ProgressDialog.show(activity, title, message, true,
					false);
		}
	}

	protected void dismissProcessDialog() {
		if (this.progress != null && this.progress.isShowing()) {
			this.progress.dismiss();
		}
	}

	protected void setImage(ImageView view, String imageName, int defaultId) {
		SukiyaUtils.setImage(this, view, imageName, defaultId);
	}

	protected String strDouble(Double d) {
		if (d != null) {
			final DecimalFormat df = new DecimalFormat("0.00");
			return df.format(d);
		}
		return null;
	}

	protected String strCurrentDate() {
		SimpleDateFormat df = new SimpleDateFormat(StringUtils.YYYYMMDD,
				Locale.JAPAN);
		return df.format(this.app.getCurrentDate());
	}

	protected String strRealtDate() {
		SimpleDateFormat df = new SimpleDateFormat(StringUtils.YYYYMMDD,
				Locale.JAPAN);
		return df.format(new Date());
	}

	protected String strRealtTime() {
		SimpleDateFormat df = new SimpleDateFormat(StringUtils.HHMMSS,
				Locale.JAPAN);
		return df.format(new Date());
	}

	protected String getTimeCode() {
		Calendar calendar = Calendar.getInstance();
		int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		int timeCode = 3;
		if (currentHour >= 19) {
			timeCode = 2;
		} else if (currentHour >= 15) {
			timeCode = 1;
		} else if (currentHour >= 9) {
			timeCode = 0;
		}
		return String.valueOf(timeCode);
	}

	protected void showPopup(View view) {
		if (view != null && View.INVISIBLE == view.getVisibility()) {
			AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
			alpha.setDuration(350);
			view.startAnimation(alpha);
			view.setVisibility(View.VISIBLE);
		}
	}

	protected void hidePopup(View view) {
		if (view != null && View.VISIBLE == view.getVisibility()) {
			AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
			alpha.setDuration(350);
			view.startAnimation(alpha);
			view.setVisibility(View.INVISIBLE);
		}
	}

	protected void removeUpdateCurrentDate() {
		// Cancel current timer
		if (updateCurrentDateTimerTask != null) {
			updateCurrentDateTimerTask.cancel();
			updateCurrentDateTimerTask = null;
		}
	}

	static class ViewHolder {
		LinearLayout menuParentLayout;

		LinearLayout menuLayout;
		TextView menuLabelView;

	}

	private class MenuItem {
		public String menuName = "";
	}

	private class MenuListAdapter extends ArrayAdapter<MenuItem> {
		private LayoutInflater inflater;
		private int layoutId;

		public MenuListAdapter(Context context, int layoutId,
				List<MenuItem> objects) {
			super(context, 0, objects);
			this.inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.layoutId = layoutId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// final ViewHolder holder;
			ViewHolder holder;

			if (convertView == null) {
				convertView = inflater.inflate(layoutId, parent, false);
				holder = new ViewHolder();

				holder.menuParentLayout = (LinearLayout) convertView
						.findViewById(R.id.menu_layout);
				holder.menuLayout = (LinearLayout) convertView
						.findViewById(R.id.menu_layout_menu);
				holder.menuLabelView = (TextView) convertView
						.findViewById(R.id.menu_label);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			MenuItem menuItem = getItem(position);
			holder.menuLabelView.setText(menuItem.menuName);
			return convertView;
		}

	}

	// Zenshou ntdat 20140401 add ++
	public void showToast(String message) {
		if (toast != null)
			toast.cancel();

		toast = Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT);
		toast.show();
	}

	// Zenshou ntdat 20140401 add --

	// vdngo begin

	protected void getHelp(Context context, String helpCode) {
		this.context = context;
		callAPI043(helpCode);

	}

	private void callAPI043(final String helpCode) {
		showProcessDialog(this, null, null);
		if (!StringUtils.isEmpty(app.getShopCode())) {
			Map<String, String> params = new HashMap<String, String>();
			params.put(JSONUtils.HELP_CODE, helpCode);
			try {
				String url = JSONUtils
						.getAPIUrl(JSONUtils.API_043_PATH, params);

				// Dummy url
				// url =
				// "http://192.168.0.191/zensho_dummy/api/scheduled_delivery/list?tenpo_cd=0001";

				Log.e("Json", String.valueOf(url));
				new CallAPIAsyncTask(this, JSONUtils.API_043_PATH).execute(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(MessageFormat.format("System error: {0}",
						e.getMessage()));
				builder.setCancelable(false);
				builder.setPositiveButton("Try again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								callAPI043(helpCode);
							}
						});
				builder.setNegativeButton("Exist Application",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Shop code not found.");
			builder.setCancelable(false);
			builder.setNegativeButton("Exist Application",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							finish();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	@Override
	public void callAPIFinish(String tag, JSONObject result, ErrorInfo error) {
		dismissProcessDialog();
		Log.e("Json", String.valueOf(result));
	}

	// private boolean testConnection(String host_path) {
	// // String temp_host =
	// // "http://192.168.0.191/zensho.rapinics.jp/api/master/item_category";
	// String temp_host = host_path;
	// try {
	// final HttpClient client = new DefaultHttpClient();
	// // String uri = Uri.parse(host_path);
	// Log.e("Host path", temp_host);
	// URI uri1 = URI.create(temp_host);
	// HttpResponse response;
	// Log.e("Log uri ", temp_host);
	// response = client.execute(new HttpGet(uri1));
	//
	// // Log.e("Log uri ", temp_host);
	// StatusLine statusLine = response.getStatusLine();
	// if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
	// } else {
	// return true;
	// }
	// } catch (Exception e) {
	// return false;
	// }
	// return true;
	// }
	private static boolean checkIfURLExists(String targetUrl) {
		HttpURLConnection httpUrlConn;
		try {
			httpUrlConn = (HttpURLConnection) new URL(targetUrl)
					.openConnection();

			Log.e("1", "1");
			httpUrlConn.setRequestMethod("HEAD");
			Log.e("2", "2");
			// Set timeouts in milliseconds
			httpUrlConn.setConnectTimeout(30000);
			Log.e("3", "3");
			httpUrlConn.setReadTimeout(30000);
			Log.e("4", "4");
			Log.e("5", "5");
			return (httpUrlConn.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return false;
		}
	}

	public static boolean ping(String url, int timeout) {
		url = url.replaceFirst("https", "http"); // Otherwise an exception may
													// be thrown on invalid SSL
													// certificates.

		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			HttpURLConnection connection = (HttpURLConnection) new URL(url)
					.openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			return (200 <= responseCode && responseCode <= 399);
		} catch (IOException exception) {
			return false;
		}
	}
	// vdngo end
}
