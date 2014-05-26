
package jp.co.zensho.android.sukiya.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jp.co.zensho.android.sukiya.R;
import jp.co.zensho.android.sukiya.bean.ErrorInfo;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadZipImageAsyncTask extends AsyncTask<String, Long, Void> {
    private Context context;
    private DownloadZipImageListener listener;
    private ProgressDialog dialog;
    private ErrorInfo error;

    public DownloadZipImageAsyncTask(Context context, DownloadZipImageListener listener) {
        this.context = context;
        this.listener = listener;
    }
    
    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(this.context);
        dialog.setMessage(context.getString(R.string.msg_005_loading));
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setProgress(0);
        dialog.setMax(100);
        dialog.show();
        
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        dialog.setProgress(values[0].intValue());
        super.onProgressUpdate(values);
    }

    @Override
    protected Void doInBackground(String... params) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.d("Download file", "Start download file.");
                // Find the root of the external storage.
                File root = android.os.Environment.getExternalStorageDirectory();
                // make zenshou folder data
                File zenshouRootFolder = new File(root, "zenshou_data");
                if (!zenshouRootFolder.exists() || !zenshouRootFolder.isDirectory()) {
                    zenshouRootFolder.mkdirs();
                }
               
                // make zip image
                File zipFile = new File(zenshouRootFolder, "image.zip");
                if (zipFile.exists()) {
                    zipFile.delete();
                   // deleteDir(new File(zenshouRootFolder.toString()+"/images"));
                }
                
                int fileLength = connection.getContentLength();
                
                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(zipFile);

                byte data[] = new byte[4096];
                int count;
                int total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((long)((total * 100) / fileLength));
                    Log.d("Download file", (int) ((total * 100) / fileLength) + "%...");
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                Log.d("Download file", "Finish download file.");

                // download finish
                // unzip
                Log.d("Download file", "Start unzip file.");
                //deleteDir(new File(zenshouRootFolder.toString()+"/images"));
                this.unzip(zipFile, zenshouRootFolder);
                Log.d("Download file", "Finish unzip file.");
            } else {
                if (error == null) {
                    error = new ErrorInfo();
                    error.setMessage(connection.getResponseMessage());
                }
            }
        } catch (Exception e) {
            if (error == null) {
                error = new ErrorInfo();
                error.setMessage(e.getMessage());
            }
            e.printStackTrace();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        this.dialog.dismiss();
        this.dialog = null;
        if (this.listener != null) {
            this.listener.downloadZipImageFinish(error);
        }
        super.onPostExecute(result);
    }

    private void unzip(File zipFile, File location) throws IOException {
        this.dialog.setProgress(0);
        this.dialog.setMax(100);
        
        FileInputStream fin = new FileInputStream(zipFile);
        BufferedInputStream bfin = new BufferedInputStream(fin);
        ZipInputStream zin = new ZipInputStream(bfin);
        ZipEntry ze = null;
        File childFile = null;
        FileOutputStream fout = null;
        BufferedOutputStream bfout = null;
        int n;
        long fileLenght = zipFile.length();
        long total = 0;
        while ((ze = zin.getNextEntry()) != null) {
            Log.d("Download file", "Unzipping " + ze.getName());

            if (ze.isDirectory()) {
                this.dirChecker(ze.getName(), location);
            } else {
                childFile = new File(location, ze.getName());
                this.makeParentFolder(childFile);
                fout = new FileOutputStream(childFile, false);
                bfout = new BufferedOutputStream(fout);
                byte b[] = new byte[1024];
                while ((n = zin.read(b, 0, 1024)) >= 0) {
                    bfout.write(b, 0, n);
                    total += n;
                    publishProgress((long)((total * 100) / fileLenght));
                }

                zin.closeEntry();
                bfout.flush();
                bfout.close();
                fout.close();
            }

        }
        zin.close();
        bfin.close();
        fin.close();

        this.dialog.setProgress(100);
    }

    private void dirChecker(String dir, File location) {
        File f = new File(location, dir);

        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }

    private void makeParentFolder(File f) {
        File parentFile = f.getParentFile();

        if (parentFile != null && !parentFile.exists()) {
            List<File> directoryList = new ArrayList<File>();
            while (parentFile != null && !parentFile.exists()) {
                Log.d("Download file", "Add dir: " + parentFile.getAbsolutePath());
                directoryList.add(parentFile);

                parentFile = parentFile.getParentFile();
            }

            for (int i = directoryList.size() - 1; i >= 0; i--) {
                Log.d("Download file", "Make dir: " + directoryList.get(i).getAbsolutePath());
                directoryList.get(i).mkdirs();
            }
        }
    }
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                   // return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }
}
