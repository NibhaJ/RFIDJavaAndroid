package com.example.rfidreadersample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rfidreadersample.util.GlobalClient;
import com.example.rfidreadersample.util.PowerUtils;


//public class MainActivity extends AppCompatActivity {
//    private final String mSoc = PowerUtils.getSystemProp("ro.dfs.soc", "qcom");
//    private boolean isClient = false;
//    private TextView navigateToRfReader;
//    //private GClient client = GlobalClient.getClient();
//
//    //private ScanManager mScanManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        initConnected();
//
//        navigateToRfReader = findViewById(R.id.textViewTitle);
//        navigateToRfReader.setOnClickListener(v -> readOrWrite());
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        //mScanManager = (ScanManager) HDMSManager.getInstance().getModuleManager(HDMSManager.MODULE_TYPE.SCANNER);
//    }
//
//    public void readOrWrite() {
//        if (isClient) {
//            Intent intent = new Intent(this, ReadOrWriteActivity.class);
//            intent.putExtra("isClient", isClient);
//            startActivity(intent);
//        } else {
//            Toast.makeText(MainActivity.this, "Not connected", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    public void initConnected() {
//        String param = mSoc.equals("sprd") ? PowerUtils.COMM_PARAM_SRPD : PowerUtils.COMM_PARAM_QCOM;
//        if (GlobalClient.getClient().openAndroidSerial(param, 0)) {
//            isClient = true;
//            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
//        } else {
//            isClient = false;
//            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
//        }
//    }
//}


import android.view.View;


public class MainActivity extends AppCompatActivity {

    private boolean isClient = false;
    private final String mSoc = PowerUtils.getSystemProp("ro.dfs.soc", "qcom");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConnected();
        setContentView(R.layout.activity_main);

        // Set click listeners for each tile
        findViewById(R.id.tile1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTileClicked(v.getId());
            }
        });
        findViewById(R.id.tile2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTileClicked(v.getId());
            }
        });
        findViewById(R.id.tile3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTileClicked(v.getId());
            }
        });
        findViewById(R.id.tile4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTileClicked(v.getId());
            }
        });
    }

    // Function to handle tile clicks
    private void onTileClicked(int viewId) {
        if (isClient) {
            // Perform action associated with the clicked tile
            if (viewId == R.id.tile1) {
                // Tile 1 clicked
                readOrWrite();
            } else if (viewId == R.id.tile2) {
                // Tile 2 clicked
                // Perform action for tile 2
            } else if (viewId == R.id.tile3) {
                // Tile 3 clicked
                // Perform action for tile 3
            } else if (viewId == R.id.tile4) {
                // Tile 4 clicked
                // Perform action for tile 4
            }
        } else {
            Toast.makeText(MainActivity.this, "Not connected", Toast.LENGTH_LONG).show();
        }
    }

    // Function to navigate to ReadOrWriteActivity
    private void readOrWrite() {
        Intent intent = new Intent(this, ReadOrWriteActivity.class);
        intent.putExtra("isClient", isClient);
        startActivity(intent);
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
