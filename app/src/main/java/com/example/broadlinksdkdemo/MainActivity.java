package com.example.broadlinksdkdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private Context context = MainActivity.this;

    // Probe List
    private Button mBtnProbeListButton;
    private String selectDeviceMac;
    private DeviceInfo selectedDevice;
    private ArrayList<DeviceInfo> deviceArrayList;

    // EasyConfig
    private Button mBtnEasyConfigV2, mBtnEasyConfigV1;
    private EditText mEtWifiSSIDEditText, mEtWifiPasswordEditText;

    // SP2 Control
    private Button mBtnSwitchOn, mBtnSwitchOff;

    // RM2 Control
    private Button mBtnRM2Refresh, mBtnRM2Study, mBtnRM2Code, mBtnRM2Send;
    private TextView mTvRM2CodeResult;
    private String mRM2SendData;

    // RM1 Control
    private Button mBtnRM1Auth, mBtnRM1Study, mBtnRM1Code, mBtnRM1Send;
    private TextView mTvRM1CodeResult;
    private String mRM1SendData;
    private int mCurrentRM1Password;

    // A1 Control
    private Button mBtnA1Control;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    public void initView() {
        NetworkUtil networkUtil = new NetworkUtil(MainActivity.this);
        networkUtil.startScan();
        String ssid = networkUtil.getWiFiSSID();
        mEtWifiSSIDEditText.setText(ssid);
    }

    public void initUI() {
        mBtnProbeListButton = (Button) findViewById(R.id.btn_probe_list);
        mBtnSwitchOn = (Button) findViewById(R.id.btn_switch_on);
        mBtnSwitchOff = (Button) findViewById(R.id.btn_switch_off);
        mEtWifiSSIDEditText = (EditText) findViewById(R.id.et_wifi_ssid);
        mEtWifiPasswordEditText = (EditText) findViewById(R.id.et_wifi_password);
        mBtnEasyConfigV2 = (Button) findViewById(R.id.btn_smartConfig_v2);
        mBtnEasyConfigV1 = (Button) findViewById(R.id.btn_smartConfig_v1);

        mBtnRM2Refresh = (Button) findViewById(R.id.btn_rm2_refresh);
        mBtnRM2Study = (Button) findViewById(R.id.btn_rm2_study);
        mBtnRM2Code = (Button) findViewById(R.id.btn_rm2_code);
        mBtnRM2Send = (Button) findViewById(R.id.btn_rm2_send);
        mTvRM2CodeResult = (TextView) findViewById(R.id.tv_rm2_code_result);

        mBtnRM1Auth = (Button) findViewById(R.id.btn_rm1_auth);
        mBtnRM1Study = (Button) findViewById(R.id.btn_rm1_study);
        mBtnRM1Code = (Button) findViewById(R.id.btn_rm1_code);
        mBtnRM1Send = (Button) findViewById(R.id.btn_rm1_send);
        mTvRM1CodeResult = (TextView) findViewById(R.id.tv_rm1_code_result);

        mBtnA1Control = (Button) findViewById(R.id.btn_a1_control);
    }

    public void initListeners() {
        mBtnProbeListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                probeList();
            }
        });
        mBtnSwitchOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("SP2ON_Mac", selectDeviceMac);
                SP2On(selectDeviceMac);

            }
        });
        mBtnSwitchOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("SP2Off_Mac", selectDeviceMac);
                SP2Off(selectDeviceMac);
            }
        });
        mBtnEasyConfigV2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        String ssid = mEtWifiSSIDEditText.getText().toString();
                        String password = mEtWifiPasswordEditText.getText().toString();
                        easyConfig(ssid, password, true);
                    }
                }).start();

            }
        });
        mBtnEasyConfigV1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        String ssid = mEtWifiSSIDEditText.getText().toString();
                        String password = mEtWifiPasswordEditText.getText().toString();
                        easyConfig(ssid, password, false);
                    }
                }).start();

            }
        });

        mBtnRM2Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                RM2Refresh(selectDeviceMac);
            }
        });
        mBtnRM2Study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                RM2StudyMode(selectDeviceMac);
            }
        });
        mBtnRM2Code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvRM2CodeResult.setText("");
                RM2Code(selectDeviceMac);
            }
        });
        mBtnRM2Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RM2Send(selectDeviceMac);
            }
        });

        mBtnRM1Auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                RM1Auth(selectDeviceMac);
            }
        });
        mBtnRM1Study.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                RM1StudyMode(selectDeviceMac);
            }
        });
        mBtnRM1Code.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mTvRM1CodeResult.setText("");
                RM1Code(selectDeviceMac);
            }
        });
        mBtnRM1Send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RM1Send(selectDeviceMac);
            }
        });

        mBtnA1Control.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(context, DeviceListActivity.class);
                startActivity(intent);
            }
        });
    }

    //----------------------------------------------------------------------------------------------------
    // API - UI Integration
    //----------------------------------------------------------------------------------------------------

    // Probe List
    // Retrieve a list of devices within same WiFi
    public void probeList() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                int selectedPosition = ((AlertDialog) arg0).getListView().getCheckedItemPosition();
                selectedDevice = deviceArrayList.get(selectedPosition);
                selectDeviceMac = selectedDevice.getMac();
                addDevice();
                Log.e("selectDeviceMac", selectDeviceMac);
                deviceArrayList.clear();
            }
        };

        deviceArrayList = BroadlinkAPI.getInstance().getProbeList();
        if (deviceArrayList == null || deviceArrayList.size() <= 0) {
            Toast.makeText(MainActivity.this, R.string.toast_probe_no_device, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] deviceNamesAndMac = new String[deviceArrayList.size()];
        for (int i = 0; i < deviceArrayList.size(); i++) {
            DeviceInfo device = deviceArrayList.get(i);

            String displayString = device.getName() + "\nType: " + device.getType() + "\nMAC: " + device.getMac() + "\nPassword:" + device.getPassword() + "\nKey: " + device.getKey();
            if (device.getSubdevice() > 0)
                displayString += "Sub-devices: " + device.getSubdevice();
            deviceNamesAndMac[i] = displayString;
        }
        new AlertDialog.Builder(context)
                .setSingleChoiceItems(deviceNamesAndMac, 0, null)
                .setPositiveButton(R.string.alert_button_confirm, listener)
                .show();
    }

    public void addDevice() {
        JsonObject out = BroadlinkAPI.getInstance().addDevice(selectedDevice);
        mCurrentRM1Password = selectedDevice.getPassword();

        if (selectedDevice.type.equalsIgnoreCase(BroadlinkConstants.RM2))
            BroadlinkAPI.getInstance().RM2Refresh(selectedDevice.mac);
    }

    public void SP2On(String mac) {
        boolean success = BroadlinkAPI.getInstance().SP2On(mac);
    }

    public void SP2Off(String mac) {
        boolean success = BroadlinkAPI.getInstance().SP2Off(mac);
    }

    private void RM2Refresh(String mac) {
        float temperature = BroadlinkAPI.getInstance().RM2Refresh(mac);
        if (temperature == BroadlinkConstants.INVALID_TEMPERATURE) {
            Toast.makeText(context, R.string.toast_rm2_refresh_fail, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(context, "RM2 Temperature: " + temperature + "℃", Toast.LENGTH_SHORT).show();
    }

    private void RM2StudyMode(String mac) {
        boolean success = BroadlinkAPI.getInstance().RM2StudyMode(mac);
        if (success)
            Toast.makeText(context, R.string.toast_rm2_study_success, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, R.string.toast_rm2_study_fail, Toast.LENGTH_SHORT).show();
    }

    private void RM2Code(String mac) {
        String codeString = BroadlinkAPI.getInstance().RM2Code(mac);
        if (codeString == null) {
            Toast.makeText(context, R.string.toast_rm2_code_fail, Toast.LENGTH_SHORT).show();
            return;
        }

        mRM2SendData = codeString;
        mTvRM2CodeResult.setText(mRM2SendData);
        Toast.makeText(context, R.string.toast_rm2_code_success, Toast.LENGTH_SHORT).show();
        Log.e("RM2StudyCode", codeString);
    }

    private void RM2Send(String mac) {
        boolean success = BroadlinkAPI.getInstance().RM2Send(mac, mRM2SendData);
    }

    private void RM1Auth(String mac) {
        float temperature = BroadlinkAPI.getInstance().RM1Auth(mac, mCurrentRM1Password);
        if (temperature == BroadlinkConstants.INVALID_TEMPERATURE) {
            Toast.makeText(context, R.string.toast_rm1_auth_fail, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(context, "RM1 Auth Temperature: " + temperature + "℃", Toast.LENGTH_SHORT).show();
    }

    private void RM1StudyMode(String mac) {
        boolean success = BroadlinkAPI.getInstance().RM1StudyMode(mac);
        if (success)
            Toast.makeText(context, R.string.toast_rm1_study_success, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, R.string.toast_rm1_study_fail, Toast.LENGTH_SHORT).show();
    }

    private void RM1Code(String mac) {
        String codeString = BroadlinkAPI.getInstance().RM1Code(mac);
        if (codeString == null) {
            Toast.makeText(context, R.string.toast_rm1_code_fail, Toast.LENGTH_SHORT).show();
            return;
        }

        mRM1SendData = codeString;
        mTvRM1CodeResult.setText(mRM1SendData);
        Toast.makeText(context, R.string.toast_rm1_code_success, Toast.LENGTH_SHORT).show();
        Log.e("RM2StudyCode", codeString);
    }

    private void RM1Send(String mac) {
        boolean success = BroadlinkAPI.getInstance().RM1Send(mac, mRM1SendData);
    }

    public void easyConfig(String ssid, String password, boolean isVersion2) {
        boolean success = BroadlinkAPI.getInstance().easyConfig(ssid, password, isVersion2);

        //Success
        if (success) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, R.string.toast_probe_to_show_new_device, Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        //Error
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
