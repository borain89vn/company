package jp.co.zensho.android.sukiya.service;

import jp.co.zensho.android.sukiya.bean.ErrorInfo;

import org.json.JSONObject;

public interface CallAPIListener {
    void callAPIFinish(String tag, JSONObject result, ErrorInfo error);
}
