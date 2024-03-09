package com.example.rfidreadersample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.hdms.HDMSManager;
import com.android.hdms.scanner.ScanManager;
import com.example.rfidreadersample.entity.TagInfo;
import com.example.rfidreadersample.util.GlobalClient;
import com.example.rfidreadersample.util.PowerUtils;
import com.gg.reader.api.dal.GClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final String mSoc = PowerUtils.getSystemProp("ro.dfs.soc", "qcom");
    private boolean isClient = false;
    private Button navigateToRfReader;
    private GClient client = GlobalClient.getClient();

    private ScanManager mScanManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initConnected();

        navigateToRfReader = findViewById(R.id.rf_reader_button);
        navigateToRfReader.setOnClickListener(v -> readOrWrite());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mScanManager = (ScanManager) HDMSManager.getInstance().getModuleManager(HDMSManager.MODULE_TYPE.SCANNER);
    }

    public void readOrWrite() {
        if (isClient) {
            Intent intent = new Intent(this, ReadOrWriteActivity.class);
            intent.putExtra("isClient", isClient);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Not connected", Toast.LENGTH_LONG).show();
        }
    }

    public void initConnected() {
        String param = mSoc.equals("sprd") ? PowerUtils.COMM_PARAM_SRPD : PowerUtils.COMM_PARAM_QCOM;
        if (GlobalClient.getClient().openAndroidSerial(param, 0)) {
            isClient = true;
            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
        } else {
            isClient = false;
            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }
}
