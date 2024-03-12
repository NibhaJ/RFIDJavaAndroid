package com.example.rfidreadersample.util;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.rfidreadersample.MainActivity;
import com.example.rfidreadersample.entity.TagInfo;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


public class RFIDReader {
    private boolean isClient = false;
    private final String mSoc = PowerUtils.getSystemProp("ro.dfs.soc", "qcom");

    private final GClient client = GlobalClient.getClient();
    //private boolean isClient = false;
    private final Map<String, TagInfo> tagInfoMap = new LinkedHashMap<>();
    //private final List<TagInfo> tagInfoList = new ArrayList<>();
    private Long index = 1L;
    private boolean isReader = false;
    private final Handler mHandler = new Handler();
    private final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    private int time = 0;

    public RFIDReader(boolean isClient) {
        initConnected();
        this.isClient = isClient;
        if (isClient) {
            subHandler(GlobalClient.getClient());
        }
        index = 1L;
        time = 0;
        tagInfoMap.clear();
    }
    public void initConnected() {
        String param = mSoc.equals("sprd") ? PowerUtils.COMM_PARAM_SRPD : PowerUtils.COMM_PARAM_QCOM;
        if (GlobalClient.getClient().openAndroidSerial(param, 0)) {
            isClient = true;
          //  Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
        } else {
            isClient = false;
           // Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }
    public Map<String, TagInfo>  readCard() {
        if (isClient) {
            if (!isReader) {
               // initPane();
                MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
                msg.setAntennaEnable(EnumG.AntennaNo_1);
                msg.setInventoryMode(EnumG.InventoryMode_Single);
                client.sendSynMsg(msg);

                return tagInfoMap;
            } else {
                stopRead();
               // Toast.makeText(ReadOrWriteActivity.this, getResources().getString(R.string.read_card_being), Toast.LENGTH_LONG).show();
            }
        } else {
            //Toast.makeText(ReadOrWriteActivity.this, getResources().getString(R.string.ununited), Toast.LENGTH_LONG).show();
        }
       return null;
    }

    private void subHandler(GClient client) {
        client.onTagEpcLog = new HandlerTagEpcLog() {
            public void log(String readerName, LogBaseEpcInfo info) {
                if (0 == info.getResult()) {
                    synchronized (tagInfoMap) {
                        pooledEpcData(info);
                    }
                }
            }
        };

        client.onTagEpcOver = new HandlerTagEpcOver() {
            public void log(String readerName, LogBaseEpcOver info) {
                handlerStop.sendEmptyMessage(1);
            }
        };
    }

    public void stopRead() {
        MsgBaseStop msgStop = new MsgBaseStop();
        client.sendSynMsg(msgStop);
        if (0x00 == msgStop.getRtCode()) {
            isReader = false;
           // Toast.makeText(ReadOrWriteActivity.this, "Stop Success", Toast.LENGTH_LONG).show();
        } else {
           // Toast.makeText(ReadOrWriteActivity.this, "Stop fail", Toast.LENGTH_LONG).show();
        }
    }

    private final Handler handlerStop = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mHandler.removeCallbacksAndMessages(null);
                upDataPane();
                isReader = false;
            }
            super.handleMessage(msg);
        }
    };

    private void upDataPane() {
        // Update UI or perform other actions here
    }

    private void pooledEpcData(LogBaseEpcInfo info) {
        String key = info.getTid() + info.getEpc();
        if (tagInfoMap.containsKey(key)) {
            TagInfo tagInfo = tagInfoMap.get(key);
            Long count = tagInfo.getCount();
            count++;
            tagInfo.setRssi(String.valueOf(info.getRssi()));
            tagInfo.setReservedData(info.getReserved());
            tagInfo.setUserData(info.getUserdata());
            tagInfo.setCount(count);
            tagInfoMap.put(key, tagInfo);
        } else {
            TagInfo tag = new TagInfo();
            tag.setIndex(index);
            tag.setType("EPC");
            tag.setEpc(info.getEpc());
            tag.setCount(1L);
            tag.setUserData(info.getUserdata());
            tag.setReservedData(info.getReserved());
            tag.setTid(info.getTid());
            tag.setRssi(String.valueOf(info.getRssi()));
            tag.setReadTime(new Date());
            tagInfoMap.put(key, tag);
            index++;
        }
    }
}
