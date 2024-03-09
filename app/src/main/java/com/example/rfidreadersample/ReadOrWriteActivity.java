package com.example.rfidreadersample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.hdms.HDMSManager;
import com.android.hdms.scanner.ScanManager;


import com.example.rfidreadersample.entity.TagInfo;

import com.example.rfidreadersample.util.GlobalClient;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerTag6bLog;
import com.gg.reader.api.dal.HandlerTag6bOver;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.dal.HandlerTagGJbLog;
import com.gg.reader.api.dal.HandlerTagGJbOver;
import com.gg.reader.api.dal.HandlerTagGbLog;
import com.gg.reader.api.dal.HandlerTagGbOver;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogBase6bInfo;
import com.gg.reader.api.protocol.gx.LogBase6bOver;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.LogBaseGJbInfo;
import com.gg.reader.api.protocol.gx.LogBaseGJbOver;
import com.gg.reader.api.protocol.gx.LogBaseGbInfo;
import com.gg.reader.api.protocol.gx.LogBaseGbOver;
import com.gg.reader.api.protocol.gx.MsgBaseInventory6b;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryGJb;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryGb;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.gg.reader.api.protocol.gx.Param6bReadUserdata;
import com.gg.reader.api.protocol.gx.ParamEpcReadReserved;
import com.gg.reader.api.protocol.gx.ParamEpcReadTid;
import com.gg.reader.api.protocol.gx.ParamEpcReadUserdata;
import com.gg.reader.api.protocol.gx.ParamGbReadUserdata;
import com.example.rfidreadersample.entity.TagInfo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ReadOrWriteActivity extends Activity {



    private GClient client = GlobalClient.getClient();
    private boolean isClient = false;
    private Map<String, TagInfo> tagInfoMap = new LinkedHashMap<String, TagInfo>();//去重数据源
    private final List<TagInfo> tagInfoList = new ArrayList<TagInfo>();//适配器所需数据源
    private Long index = 1l;//索引
    private RecyclerViewAdapter adapter;
    private ParamEpcReadTid tidParam = null;
    private ParamEpcReadUserdata userParam = null;
    private ParamEpcReadReserved reserveParam = null;
    private Param6bReadUserdata user6bParam = null;
    private boolean[] isChecked = new boolean[]{false, false, false};//标识读0-epc与1-user
    private Handler mHandler = new Handler();
    private Handler soundHandler = new Handler();
    private Runnable r = null;
    private Runnable timeTask = null;
    private int time = 0;
    private boolean isSound = true;
    MenuItem gMenuItem = null;
    private boolean isReader = false;
    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    private boolean keyPress = false;
    // Declare views
    private Button read;
    private Button stop;
    private Button clean;
    private RadioGroup way;
    private RadioGroup type;
    private RadioButton single;
    private RadioButton loop;
    private RadioButton c;
    private RadioButton b;
    private TextView readCount;
    private TextView tagCount;
    private TextView speed;
    private TextView timeCount;
    private LinearLayout tabHead;
    ScanManager mScanManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_write);
        // Initialize views
        read = findViewById(R.id.read);
        stop = findViewById(R.id.stop);
        clean = findViewById(R.id.clean);
        way = findViewById(R.id.way);
        type = findViewById(R.id.type);
        single = findViewById(R.id.single);
        loop = findViewById(R.id.loop);
        c = findViewById(R.id.c);
        b = findViewById(R.id.b);
        readCount = findViewById(R.id.readCount);
        tagCount = findViewById(R.id.tagCount);
        speed = findViewById(R.id.speed);
        timeCount = findViewById(R.id.timeCount);
        tabHead = findViewById(R.id.tabHead);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        isClient = getIntent().getBooleanExtra("isClient", false);
        if (isClient) {
            subHandler(GlobalClient.getClient());
        }
        initRecycleView();
        //UtilSound.initSoundPool(this);

        mScanManager = (ScanManager) HDMSManager.getInstance().getModuleManager(HDMSManager.MODULE_TYPE.SCANNER);
        read.setOnClickListener(v -> {
            readCard();
        });
        stop.setOnClickListener(v -> {
            stopRead();
        });
        clean.setOnClickListener(v -> {
            cleanData();
        });
    }
    public void cleanData() {
        if (isClient) {
            initPane();
        } else {
            Toast.makeText(ReadOrWriteActivity.this, getResources().getString(R.string.ununited), Toast.LENGTH_LONG).show();
        }
    }
    public void initRecycleView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView rv = (RecyclerView) findViewById(R.id.recycle);
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new DividerItemDecoration(this, 1));
        adapter = new RecyclerViewAdapter(tagInfoList);
        rv.setAdapter(adapter);
    }
    public void subHandler(GClient client) {
        client.onTagEpcLog = new HandlerTagEpcLog() {
            public void log(String readerName, LogBaseEpcInfo info) {
                if (0 == info.getResult()) {
                    synchronized (tagInfoList) {
                        pooled6cData(info);
                    }
                }
            }
        };
        client.onTagEpcOver = new HandlerTagEpcOver() {
            public void log(String readerName, LogBaseEpcOver info) {
                handlerStop.sendEmptyMessage(new Message().what = 1);
            }
        };
        client.onTag6bLog = new HandlerTag6bLog() {
            public void log(String readerName, LogBase6bInfo info) {
                if (info.getResult() == 0) {
                    synchronized (tagInfoList) {
                        pooled6bData(info);
                    }
                }
            }
        };
        client.onTag6bOver = new HandlerTag6bOver() {
            public void log(String readerName, LogBase6bOver info) {
                handlerStop.sendEmptyMessage(new Message().what = 1);
            }
        };
        client.onTagGbLog = new HandlerTagGbLog() {
            public void log(String readerName, LogBaseGbInfo info) {
                if (info.getResult() == 0) {
                    synchronized (tagInfoList) {
                        pooledGbData(info);
                    }
                }
            }
        };
        client.onTagGbOver = new HandlerTagGbOver() {
            public void log(String readerName, LogBaseGbOver info) {
                handlerStop.sendEmptyMessage(new Message().what = 1);
            }
        };
        client.onTagGJbLog = new HandlerTagGJbLog() {
            @Override
            public void log(String s, LogBaseGJbInfo logBaseGJbInfo) {
                if (logBaseGJbInfo.getResult() == 0) {
                    synchronized (tagInfoList) {
                        pooledGJbData(logBaseGJbInfo);
                    }
                }
            }
        };
        client.onTagGJbOver = new HandlerTagGJbOver() {
            @Override
            public void log(String s, LogBaseGJbOver logBaseGJbOver) {
                Log.e("HandlerTagGJbOver", "-------------HandlerTagGJbOver");
                handlerStop.sendEmptyMessage(new Message().what = 1);
            }
        };

    }



    public void readCard() {
        if (isClient) {
            if (!isReader) {
                initPane();
                if (type.getCheckedRadioButtonId() == R.id.c) {
                    MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
                    msg.setAntennaEnable(EnumG.AntennaNo_1);
                    if (way.getCheckedRadioButtonId() == R.id.single) {
                        msg.setInventoryMode(EnumG.InventoryMode_Single);
                    } else {
                        msg.setInventoryMode(EnumG.InventoryMode_Inventory);
                    }
                    if (isChecked[0]) {
                        tidParam = new ParamEpcReadTid();
                        tidParam.setMode(EnumG.ParamTidMode_Auto);
                        tidParam.setLen(6);
                        msg.setReadTid(tidParam);
                    }
                    if (isChecked[1]) {
                        userParam = new ParamEpcReadUserdata();
                        userParam.setStart(0);
                        userParam.setLen(6);
                        msg.setReadUserdata(userParam);
                    }
                    if (isChecked[2]) {
                        reserveParam = new ParamEpcReadReserved();
                        reserveParam.setStart(0);
                        reserveParam.setLen(4);
                        msg.setReadReserved(reserveParam);
                    }
                    client.sendSynMsg(msg);
                    if (0x00 == msg.getRtCode()) {
                        Toast.makeText(ReadOrWriteActivity.this, "Start", Toast.LENGTH_LONG).show();
                        isReader = true;
                        computedSpeed();
                        soundTask();
                    } else {
                        handlerStop.sendEmptyMessage(1);
                        Toast.makeText(ReadOrWriteActivity.this, msg.getRtMsg(), Toast.LENGTH_LONG).show();


                    }
                } else if (type.getCheckedRadioButtonId() == R.id.b) {
                    MsgBaseInventory6b msg = new MsgBaseInventory6b();
                    msg.setAntennaEnable(EnumG.AntennaNo_1);
                    if (way.getCheckedRadioButtonId() == R.id.single) {
                        msg.setInventoryMode(EnumG.InventoryMode_Single);
                    } else {
                        msg.setInventoryMode(EnumG.InventoryMode_Inventory);
                    }
                    if (isChecked[1]) {
                        user6bParam = new Param6bReadUserdata();
                        user6bParam.setStart(0);
                        user6bParam.setLen(10);
                        msg.setReadUserdata(user6bParam);
                    }
                    msg.setArea(EnumG.ReadMode6b_Tid);
                    client.sendSynMsg(msg);
                    if (0x00 == msg.getRtCode()) {
                        Toast.makeText(ReadOrWriteActivity.this, "Start", Toast.LENGTH_LONG).show();
                        isReader = true;
                        computedSpeed();
                        soundTask();
                    } else {
                        handlerStop.sendEmptyMessage(1);
                        Toast.makeText(ReadOrWriteActivity.this, msg.getRtMsg(), Toast.LENGTH_LONG).show();

                    }
                } else if (type.getCheckedRadioButtonId() == R.id.gb) {
                    MsgBaseInventoryGb msg = new MsgBaseInventoryGb();
                    msg.setAntennaEnable(EnumG.AntennaNo_1);
                    if (way.getCheckedRadioButtonId() == R.id.single) {
                        msg.setInventoryMode(EnumG.InventoryMode_Single);
                    } else {
                        msg.setInventoryMode(EnumG.InventoryMode_Inventory);
                    }
                    if (isChecked[0]) {
                        ParamEpcReadTid tid = new ParamEpcReadTid();
                        tid.setMode(EnumG.ParamTidMode_Auto);
                        tid.setLen(6);
                        msg.setReadTid(tid);
                    }
                    if (isChecked[1]) {
                        ParamGbReadUserdata gbReadUserdata = new ParamGbReadUserdata();
                        gbReadUserdata.setStart(4);//字
                        gbReadUserdata.setLen(1);//字
                        gbReadUserdata.setChildArea(0x30);
                        msg.setReadUserdata(gbReadUserdata);
                    }
                    client.sendSynMsg(msg);
                    if (0x00 == msg.getRtCode()) {
                        Toast.makeText(ReadOrWriteActivity.this, "Start", Toast.LENGTH_LONG).show();
                        isReader = true;
                        computedSpeed();
                        soundTask();
                    } else {
                        handlerStop.sendEmptyMessage(1);
                        Toast.makeText(ReadOrWriteActivity.this, msg.getRtMsg(), Toast.LENGTH_LONG).show();
                    }
                }else if (type.getCheckedRadioButtonId() == R.id.gjb) {
                    MsgBaseInventoryGJb msg = new MsgBaseInventoryGJb();
                    msg.setAntennaEnable(EnumG.AntennaNo_1);
                    if (way.getCheckedRadioButtonId() == R.id.single) {
                        msg.setInventoryMode(EnumG.InventoryMode_Single);
                    } else {
                        msg.setInventoryMode(EnumG.InventoryMode_Inventory);
                    }
                    if (isChecked[0]) {
                        ParamEpcReadTid tid = new ParamEpcReadTid();
                        tid.setMode(EnumG.ParamTidMode_Auto);
                        tid.setLen(6);
                        msg.setReadTid(tid);
                    }
                    if (isChecked[1]) {
                        userParam = new ParamEpcReadUserdata();
                        userParam.setStart(0);
                        userParam.setLen(2);
                        msg.setReadUserdata(userParam);
                    }

                    client.sendSynMsg(msg);
                    if (0x00 == msg.getRtCode()) {
                        Toast.makeText(ReadOrWriteActivity.this, "Start", Toast.LENGTH_LONG).show();
                        isReader = true;
                        computedSpeed();
                        soundTask();
                    } else {
                        handlerStop.sendEmptyMessage(1);
                        Toast.makeText(ReadOrWriteActivity.this, msg.getRtMsg(), Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                stopRead();
                Toast.makeText(ReadOrWriteActivity.this, getResources().getString(R.string.read_card_being), Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(ReadOrWriteActivity.this, getResources().getString(R.string.ununited), Toast.LENGTH_LONG).show();
        }
    }
    public Map<String, TagInfo> pooled6cData(LogBaseEpcInfo info) {
        if (tagInfoMap.containsKey(info.getTid() + info.getEpc())) {
            TagInfo tagInfo = tagInfoMap.get(info.getTid() + info.getEpc());
            Long count = tagInfoMap.get(info.getTid() + info.getEpc()).getCount();
            count++;
            tagInfo.setRssi(info.getRssi() + "");
            tagInfo.setReservedData(info.getReserved());
            tagInfo.setUserData(info.getUserdata());
            tagInfo.setCount(count);
            tagInfoMap.put(info.getTid() + info.getEpc(), tagInfo);
        } else {
            TagInfo tag = new TagInfo();
            tag.setIndex(index);
            tag.setType("6C");
            tag.setEpc(info.getEpc());
            tag.setCount(1l);
            tag.setUserData(info.getUserdata());
            tag.setReservedData(info.getReserved());
            tag.setTid(info.getTid());
            tag.setRssi(info.getRssi() + "");
            tag.setReadTime(new Date());
            tagInfoMap.put(info.getTid() + info.getEpc(), tag);
            index++;
        }

        return tagInfoMap;
    }

    //去重6B
    public Map<String, TagInfo> pooled6bData(LogBase6bInfo info) {
        if (tagInfoMap.containsKey(info.getTid())) {
            TagInfo tagInfo = tagInfoMap.get(info.getTid());
            Long count = tagInfoMap.get(info.getTid()).getCount();
            count++;
            tagInfo.setRssi(info.getRssi() + "");
            tagInfo.setCount(count);
            tagInfoMap.put(info.getTid(), tagInfo);
        } else {
            TagInfo tag = new TagInfo();
            tag.setIndex(index);
            tag.setType("6B");
            tag.setCount(1l);
            tag.setUserData(info.getUserdata());
            if (info.getTid() != null) {
                tag.setTid(info.getTid());
            }
            tag.setRssi(info.getRssi() + "");
            tagInfoMap.put(info.getTid(), tag);
            index++;
        }

        return tagInfoMap;
    }

    //去重GB
    public Map<String, TagInfo> pooledGbData(LogBaseGbInfo info) {
        if (tagInfoMap.containsKey(info.getTid() + info.getEpc())) {
            TagInfo tagInfo = tagInfoMap.get(info.getTid() + info.getEpc());
            Long count = tagInfoMap.get(info.getTid() + info.getEpc()).getCount();
            count++;
            tagInfo.setRssi(info.getRssi() + "");
            tagInfo.setCount(count);
            tagInfoMap.put(info.getTid() + info.getEpc(), tagInfo);
        } else {
            TagInfo tag = new TagInfo();
            tag.setIndex(index);
            tag.setType("GB");
            tag.setEpc(info.getEpc());
            tag.setCount(1l);
            tag.setUserData(info.getUserdata());
            tag.setTid(info.getTid());
            tag.setRssi(info.getRssi() + "");
            tagInfoMap.put(info.getTid() + info.getEpc(), tag);
            index++;
        }

        return tagInfoMap;
    }

    //去重GjB
    public Map<String, TagInfo> pooledGJbData(LogBaseGJbInfo info) {
        if (tagInfoMap.containsKey(info.getTid() + info.getEpc())) {
            TagInfo tagInfo = tagInfoMap.get(info.getTid() + info.getEpc());
            Long count = tagInfoMap.get(info.getTid() + info.getEpc()).getCount();
            count++;
            tagInfo.setRssi(info.getRssi() + "");
            tagInfo.setCount(count);
            tagInfoMap.put(info.getTid() + info.getEpc(), tagInfo);
        } else {
            TagInfo tag = new TagInfo();
            tag.setIndex(index);
            tag.setType("GJB");
            tag.setEpc(info.getEpc());
            tag.setCount(1L);
            tag.setUserData(info.getUserdata());
            tag.setTid(info.getTid());
            tag.setRssi(info.getRssi() + "");
            tag.setReadTime(new Date());
            tagInfoMap.put(info.getTid() + info.getEpc(), tag);
            index++;
        }
        return tagInfoMap;
    }
    long rateValue = 0;
    private void computedSpeed() {
        Map<String, Long> rateMap = new Hashtable<String, Long>();
        r = new Runnable() {
            @Override
            public void run() {
                String toTime = secToTime(++time);
                timeCount.setText(toTime + " (s)");
                long before = 0;
                long after = 0;
                Long afterValue = rateMap.get("after");
                if (null != afterValue) {
                    before = afterValue;
                }
                synchronized (tagInfoList) {
                    tagInfoList.clear();
                    tagInfoList.addAll(tagInfoMap.values());
                }
                adapter.notifyData(tagInfoList);
                long readCounts = getReadCount(tagInfoList);
                readCount.setText(readCounts + "");
                tagCount.setText(tagInfoList.size() + "");
                rateMap.put("after", readCounts);
                after = readCounts;
                if (after >= before) {
                    rateValue = after - before;
                    speed.setText(rateValue + " (t/s)");
                }
                //每隔1s循环执行run方法
                mHandler.postDelayed(this, 1000);
            }
        };
        //延迟一秒执行
        mHandler.postDelayed(r, 1000);
    }

    private void soundTask() {
        timeTask = new Runnable() {
            @Override
            public void run() {
                if (rateValue != 0) {
                    //UtilSound.play(1, 0);
                }
                soundHandler.postDelayed(this, 20);
            }
        };
        soundHandler.postDelayed(timeTask, 0);
    }

    //初始化面板
    private void initPane() {
        index = 1l;
        time = 0;
        rateValue = 0;
        tagInfoMap.clear();
        tagInfoList.clear();
        adapter.notifyData(tagInfoList);
        tagCount.setText(0 + "");
        readCount.setText(0 + "");
        timeCount.setText("00:00:00" + " (s)");
        speed.setText(0 + " (t/s)");
        adapter.setThisPosition(null);

    }

    //更新面板
    private void upDataPane() {
        tagInfoList.clear();
        tagInfoList.addAll(tagInfoMap.values());
        adapter.notifyData(tagInfoList);
        readCount.setText(getReadCount(tagInfoList) + "");
        tagCount.setText(tagInfoList.size() + "");
    }


    private long getReadCount(List<TagInfo> tagInfoList) {
        long readCount = 0;
        for (int i = 0; i < tagInfoList.size(); i++) {
            readCount += tagInfoList.get(i).getCount();
        }
        return readCount;
    }


    public String secToTime(long time) {
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        time = time * 1000;
        String hms = formatter.format(time);
        return hms;
    }

    final Handler handlerStop = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mHandler.removeCallbacks(r);
                    soundHandler.removeCallbacks(timeTask);
                    upDataPane();
                    isReader = false;
                    break;

            }
            super.handleMessage(msg);
        }
    };
    public void stopRead() {
        if (isClient) {
            MsgBaseStop msgStop = new MsgBaseStop();
            client.sendSynMsg(msgStop);
            if (0x00 == msgStop.getRtCode()) {
                isReader = false;
                Toast.makeText(ReadOrWriteActivity.this, "Stop Success", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(ReadOrWriteActivity.this, "Stop fail", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(ReadOrWriteActivity.this, getResources().getString(R.string.ununited), Toast.LENGTH_LONG).show();
        }
    }
}
