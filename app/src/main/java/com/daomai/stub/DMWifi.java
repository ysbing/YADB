package com.daomai.stub;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class DMWifi extends Activity {

    private static final long DEFAULT_CONNECTION_TIMEOUT_MS = 10000; // 10 giây
    private WifiManager wifiManager;
    private Handler handler;
    private boolean isConnected = false;
    private long connectionTimeoutMs = DEFAULT_CONNECTION_TIMEOUT_MS;
    private WifiConfiguration wifiConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());

        // Kiểm tra và yêu cầu quyền cần thiết
        // checkPermissions();

        // Nhận các tham số từ lệnh ADB
        String ssid = getIntent().getStringExtra("ssid");
        String password = getIntent().getStringExtra("password");
        String timeoutStr = getIntent().getStringExtra("timeout");

        // Cài đặt thời gian chờ từ tham số, nếu có
        if (timeoutStr != null) {
            try {
                connectionTimeoutMs = Long.parseLong(timeoutStr);
            } catch (NumberFormatException e) {
                Log.e("DMWifi", "Giá trị thời gian chờ không hợp lệ, sử dụng giá trị mặc định.");
            }
        }

        if (ssid != null) {
            connectToWifi(ssid, password);
        } else {
            Log.e("DMWifi", "SSID không được cung cấp.");
        }
    }

    private void connectToWifi(String ssid, String password) {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // Kiểm tra và bật Wi-Fi nếu nó đang tắt
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        // Cấu hình Wi-Fi
        wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        if (password != null && !password.isEmpty()) {
            wifiConfig.preSharedKey = String.format("\"%s\"", password);
        } else {
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        // Xóa cấu hình mạng cũ nếu có
        try {
            for (WifiConfiguration config : wifiManager.getConfiguredNetworks()) {
                if (config.SSID != null && config.SSID.equals(wifiConfig.SSID)) {
                    wifiManager.removeNetwork(config.networkId);
                    break;
                }
            }
        } catch (SecurityException e) {
            Log.e("DMWifi", "Lỗi khi truy cập cấu hình mạng: " + e.getMessage());
            Toast.makeText(this, "Không có quyền truy cập cấu hình mạng.", Toast.LENGTH_LONG).show();
            return;
        }

        // Đăng ký BroadcastReceiver để theo dõi trạng thái kết nối
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);

        // Kết nối đến Wi-Fi
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        // Kiểm tra kết nối sau thời gian chờ
        handler.postDelayed(() -> {
            if (!isConnected) {
                // Quên mạng và thông báo lỗi nếu không kết nối được trong thời gian chờ
                if (wifiConfig != null) {
                    try {
                        for (WifiConfiguration config : wifiManager.getConfiguredNetworks()) {
                            if (config.SSID != null && config.SSID.equals(wifiConfig.SSID)) {
                                wifiManager.removeNetwork(config.networkId);
                                break;
                            }
                        }
                    } catch (SecurityException e) {
                        Log.e("DMWifi", "Lỗi khi xóa cấu hình mạng: " + e.getMessage());
                    }
                }
                Log.e("DMWifi", "Không thể kết nối đến Wi-Fi trong " + (connectionTimeoutMs / 1000) + " giây.");
                Toast.makeText(DMWifi.this, "Không thể kết nối đến Wi-Fi trong " + (connectionTimeoutMs / 1000) + " giây.", Toast.LENGTH_LONG).show();
            }
            // Kết thúc Activity
            finish();
        }, connectionTimeoutMs);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (checkSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
                    NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                    if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        isConnected = true;
                        // Nếu đã kết nối, hủy bỏ thời gian chờ
                        handler.removeCallbacksAndMessages(null);
                        // Kết thúc Activity
                        finish();
                    }
                } else {
                    Log.e("DMWifi", "Không có quyền ACCESS_NETWORK_STATE.");
                }
            }
        }
    };

    private void checkPermissions() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy bỏ BroadcastReceiver khi Activity bị hủy
        unregisterReceiver(receiver);
    }
}
