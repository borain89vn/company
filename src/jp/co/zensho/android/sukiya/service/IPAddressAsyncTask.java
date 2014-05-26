package jp.co.zensho.android.sukiya.service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.os.AsyncTask;

public class IPAddressAsyncTask extends AsyncTask<Void, Void, String> {
    private IPAddressListener listener;
    
    public IPAddressAsyncTask(IPAddressListener l) {
        this.listener = l;
    }
    
    @Override
    protected String doInBackground(Void... params) {
        String ipv4 = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    ipv4 = inetAddress.getHostAddress();

                    // for getting IPV4 format
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4)) {
                        return ipv4;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            listener.loadIPAddressFinish(result);
        }
        super.onPostExecute(result);
    }

    
}
