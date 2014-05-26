package jp.co.zensho.android.sukiya.service;

import jp.co.zensho.android.sukiya.bean.ErrorInfo;

public interface DownloadZipImageListener {
    void downloadZipImageFinish(ErrorInfo error);
}
