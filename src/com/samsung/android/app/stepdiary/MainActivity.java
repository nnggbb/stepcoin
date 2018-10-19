/**
 * Copyright (C) 2014 Samsung Electronics Co., Ltd. All rights reserved.
 *
 * Mobile Communication Division,
 * Digital Media & Communications Business, Samsung Electronics Co., Ltd.
 *
 * This software and its documentation are confidential and proprietary
 * information of Samsung Electronics Co., Ltd.  No part of the software and
 * documents may be copied, reproduced, transmitted, translated, or reduced to
 * any electronic medium or machine-readable form without the prior written
 * consent of Samsung Electronics.
 *
 * Samsung Electronics makes no representations with respect to the contents,
 * and assumes no responsibility for any errors that might appear in the
 * software and documents. This publication and the contents hereof are subject
 * to change without notice.
 */

package com.samsung.android.app.stepdiary;

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants.StepCount;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionKey;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionResult;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionType;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    public static final String TAG = "StepDiary";

    @BindView(R.id.total_step_count) TextView mStepCountTv;
    @BindView(R.id.coin_num) TextView mCoin_num;
    @BindView(R.id.date_view) TextView mDayTv;

    private HealthDataStore mStore;
    private StepCountReader mReporter;
    private long mCurrentStartTime;
    private Integer currentCoin;
    private Integer currentStep;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        currentCoin = 0;
        currentStep = 0;

        // Get the start time of today in local
        mCurrentStartTime = StepCountReader.TODAY_START_UTC_TIME;
        mDayTv.setText(getFormattedTime());

        HealthDataService healthDataService = new HealthDataService();
        try {
            healthDataService.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Create a HealthDataStore instance and set its listener
        mStore = new HealthDataStore(this, mConnectionListener);

        // Request the connection to the health data store
        mStore.connectService();
        mReporter = new StepCountReader(mStore, mStepCountObserver);

        mReporter.requestDailyStepCount(mCurrentStartTime);
        getCurrentCoin();
    }

    @Override
    public void onDestroy() {
        mStore.disconnectService();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

//    @OnClick(R.id.move_before)
//    void onClickBeforeButton() {
//        Log.d(TAG, "before clicked");
//    }
//
//    @OnClick(R.id.move_next)
//    void onClickNextButton() {
//        Log.d(TAG, "next clicked");
//    }

    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {
        @Override
        public void onConnected() {
            Log.d(TAG, "onConnected");
            if (isPermissionAcquired()) {
                mReporter.requestDailyStepCount(mCurrentStartTime);
            } else {
                requestPermission();
            }
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            Log.d(TAG, "onConnectionFailed");
            showConnectionFailureDialog(error);
        }

        @Override
        public void onDisconnected() {
            Log.d(TAG, "onDisconnected");
            if (!isFinishing()) {
                mStore.connectService();
            }
        }
    };

    private String getFormattedTime() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd (E)", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(mCurrentStartTime);
    }

    private final StepCountReader.StepCountObserver mStepCountObserver = new StepCountReader.StepCountObserver() {
        @Override
        public void onChanged(int count) {
            updateStepCountView(String.valueOf(count));
        }

        @Override
        public void onBinningDataChanged(List<StepCountReader.StepBinningData> stepBinningDataList) {
            //updateBinningChartView(stepBinningDataList);
        }
    };

    private void updateStepCountView(final String count) {
        // Display the today step count so far
        runOnUiThread(() -> mStepCountTv.setText(count));
        currentStep = Integer.parseInt(count);
        Log.d(TAG, "count = " + count);

        ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setProgress(currentStep) ;

        calcCoin(count);
        updateCoinDisp();
    }

    private void calcCoin(final String count) {
        int earn = Integer.parseInt(count) / 10000;
        if (earn > 0) {
            earn *= 10;
            currentCoin += earn;

            //putCurrentCoin();
            //putCurrentStep();
        }
    }

    private void updateCoinDisp() {
        Log.d(TAG, "update coin disp = " + currentCoin.toString());
        StringBuilder coinDispText = new StringBuilder(8);
        coinDispText.append("$ ");
        coinDispText.append(currentCoin.toString());
        runOnUiThread(() -> mCoin_num.setText(coinDispText));
    }

    private void putCurrentCoin() {
        JSONObject jo = new JSONObject();
        try{
            jo.put("coin", currentCoin.toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        new HttpUtil().execute("PUT", "http://175.118.47.252:8082/api/coin", jo.toString());
    }

    private void putCurrentStep() {
        JSONObject jo = new JSONObject();
        try{
            jo.put("count", currentStep.toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        new HttpUtil().execute("PUT", "http://175.118.47.252:8082/api/steps", jo.toString());
    }

    private void getCurrentCoin() {
        new HttpUtil().execute("GET", "http://175.118.47.252:8082/api/coin");
    }

    private final HealthResultHolder.ResultListener<PermissionResult> mPermissionListener =
            new HealthResultHolder.ResultListener<PermissionResult>() {

        @Override
        public void onResult(PermissionResult result) {
            Map<PermissionKey, Boolean> resultMap = result.getResultMap();
            // Show a permission alarm and clear step count if permissions are not acquired
            if (resultMap.values().contains(Boolean.FALSE)) {
                updateStepCountView("");
                showPermissionAlarmDialog();
            } else {
                // Get the daily step count of a particular day and display it
                mReporter.requestDailyStepCount(mCurrentStartTime);
            }
        }
    };

    private void showPermissionAlarmDialog() {
        if (isFinishing()) {
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(R.string.notice)
                .setMessage(R.string.msg_perm_acquired)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void showConnectionFailureDialog(final HealthConnectionErrorResult error) {
        if (isFinishing()) {
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        if (error.hasResolution()) {
            switch (error.getErrorCode()) {
                case HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED:
                    alert.setMessage(R.string.msg_req_install);
                    break;
                case HealthConnectionErrorResult.OLD_VERSION_PLATFORM:
                    alert.setMessage(R.string.msg_req_upgrade);
                    break;
                case HealthConnectionErrorResult.PLATFORM_DISABLED:
                    alert.setMessage(R.string.msg_req_enable);
                    break;
                case HealthConnectionErrorResult.USER_AGREEMENT_NEEDED:
                    alert.setMessage(R.string.msg_req_agree);
                    break;
                default:
                    alert.setMessage(R.string.msg_req_available);
                    break;
            }
        } else {
            alert.setMessage(R.string.msg_conn_not_available);
        }

        alert.setPositiveButton(R.string.ok, (dialog, id) -> {
            if (error.hasResolution()) {
                error.resolve(MainActivity.this);
            }
        });

        if (error.hasResolution()) {
            alert.setNegativeButton(R.string.cancel, null);
        }

        alert.show();
    }

    private boolean isPermissionAcquired() {
        HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);
        try {
            // Check whether the permissions that this application needs are acquired
            Map<PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(generatePermissionKeySet());
            return !resultMap.values().contains(Boolean.FALSE);
        } catch (Exception e) {
            Log.e(TAG, "Permission request fails.", e);
        }
        return false;
    }

    private void requestPermission() {
        HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);
        try {
            // Show user permission UI for allowing user to change options
            pmsManager.requestPermissions(generatePermissionKeySet(), MainActivity.this)
                    .setResultListener(mPermissionListener);
        } catch (Exception e) {
            Log.e(TAG, "Permission setting fails.", e);
        }
    }

    private Set<PermissionKey> generatePermissionKeySet() {
        Set<PermissionKey> pmsKeySet = new HashSet<>();
        pmsKeySet.add(new PermissionKey(StepCount.HEALTH_DATA_TYPE, PermissionType.READ));
        pmsKeySet.add(new PermissionKey(StepCountReader.STEP_SUMMARY_DATA_TYPE_NAME, PermissionType.READ));
        return pmsKeySet;
    }

    public class HttpUtil extends AsyncTask<String, Void, Void> {
        @Override
        public Void doInBackground(String... params) {
            try {
                Log.d(TAG, "called method as = " + params[0]);
                int isGET = 0, isPUT = 0, isPOST = 0;

                switch (params[0]) {
                    case "GET":
                        isGET = 1;
                        break;
                    case "PUT":
                        isPUT = 1;
                        break;
                    case "POST":
                        isPOST = 1;
                        break;
                    default:
                        break;
                }

                String url = params[1];
                if (params[1].isEmpty()) {
                    Log.d(TAG, "params[1] is NULL");
                    return null;
                }

                URL obj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod(params[0]);
                conn.setDoInput(true);
                if (isPUT == 1 || isPOST == 1) {
                    conn.setDoOutput(true);
                }
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("userId", GetDevicesUUID(MainActivity.this));
                conn.connect();

                if ((isPUT == 1 || isPOST == 1) && !params[2].isEmpty()) {
                    Log.d(TAG, "params = " + params[2]);

                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    BufferedWriter bWriter = new BufferedWriter(out);
                    bWriter.write(params[2]);
                    bWriter.flush();
                    bWriter.close();
                }

                int retCode = conn.getResponseCode();
                Log.d(TAG, "retCode = " + retCode);

                if (retCode == HttpsURLConnection.HTTP_OK) {
                    InputStream inputStream = conn.getInputStream();
                    InputStreamReader streamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader br = new BufferedReader(streamReader);
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    String  returnString = sb.toString();
                    JSONObject json = new JSONObject(returnString);
                    Log.d(TAG, "The response is : " + json.toString());

                    if (isGET == 1) {
                        String savedCurrentCoin = json.getString("coin");
                        if (!savedCurrentCoin.isEmpty()) {
                            Log.d(TAG, "current coin from server is = " + savedCurrentCoin);
                            currentCoin = Integer.parseInt(savedCurrentCoin);
                            updateCoinDisp();
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Successfully updated from server", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        //PUT return
                        if (returnString.contains("updated")) {
                            Log.d(TAG, "PUT current coin is success");
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Successfully updated to server", Toast.LENGTH_SHORT).show());
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error : "+e.getMessage());
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        if (item.getItemId() == R.id.sync_steps) {
            mReporter.requestDailyStepCount(mCurrentStartTime);
        } else if (item.getItemId() == R.id.set_steps) {
            Intent intent = new Intent(this, StepInputPopup.class);
            startActivityForResult(intent, 1);
        } else if (item.getItemId() == R.id.upload_data) {
            putCurrentCoin();
            putCurrentStep();
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                //데이터 받기
                String result = data.getStringExtra("result");
                Log.d(TAG, "inputted step = " + result);
                updateStepCountView(result);
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public static String lineOut() {
        int level = 4;
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        return (" at ("  + traces[level] + ") ");
    }

    private String GetDevicesUUID(Context mContext){
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        Log.d(TAG, "deviceId = " + deviceId);
        return deviceId;
    }
}
