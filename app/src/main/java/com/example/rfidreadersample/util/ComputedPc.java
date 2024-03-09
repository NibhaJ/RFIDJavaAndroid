package com.example.rfidreadersample.util;

import android.widget.EditText;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;

public class ComputedPc {
    //写入EPC长度
    public static int getEPCLength(EditText value) {
        String taEPCDataText = value.getText().toString().trim();
        int iLength = 0;
        if (taEPCDataText.length() % 4 == 0) {
            iLength = taEPCDataText.length() / 4;
        } else if (taEPCDataText.length() % 4 > 0) {
            iLength = taEPCDataText.length() / 4 + 1;
        }
        return iLength;
    }

    //PC值
    public static String getPc(int pcLen) {
        int iLength = pcLen;
        int iPc = iLength << 11;
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.put(iPc);
        buffer.position(16);
        byte[] bTmp = new byte[2];
        buffer.get(bTmp);
        String Pc = HexUtils.bytes2HexString(bTmp);
        return Pc;
    }

    //PC值 0100 0200
    public static String getGbPc(int pcLen) {
        String Pc = "";
        if (pcLen < 10) {
            Pc = "0" + pcLen + "00";
        } else if (pcLen < 100) {
            Pc = pcLen + "00";
        }
        return Pc;
    }
}
