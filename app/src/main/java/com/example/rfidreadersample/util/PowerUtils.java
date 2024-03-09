package com.example.rfidreadersample.util;

import android.os.Build;
import android.util.Log;

import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PowerUtils {
    private static final String TAG = "PowerUtils";
    public static final int VCC_GP_DOWN = 0;
    public static final int VCC_RFID_DOWN = 4;
    public static final int VCC_RFID_UP = 5;
    public static final int VCC_PSAM_DOWN = 6;
    public static final int VCC_PSAM_UP = 7;
    public static final int VCC_ID_DOWN = 8;
    public static final int VCC_ID_UP = 9;
    public static final int VCC_MISC_DOWN = 10;
    public static final int VCC_MISC_UP = 11;
    public static final int VCC_OTG_DOWN = 12;
    public static final int VCC_OTG_UP = 13;

    public static final int VCC_DOWN_FORCE = 24;
    public static final int VCC_UP_FORCE = 25;

    public final static String COMM_PARAM_SRPD = "/dev/ttyS0:115200";
    public final static String COMM_PARAM_QCOM = "/dev/ttyHS1:115200";

    private static final boolean isSprdA12 = getSystemProp("ro.dfs.soc", "qcom").equals("sprd")
            && Build.VERSION.SDK_INT == 31;

    private static final String POGO_VBATT_SEL_FILE_PATH = "/sys/class/pigpig/pogo_vcc_sel/value";

    private static final String POGO_OTG_5V_EN = "/sys/class/leds/pogo_otg_5v_en/brightness";
    private static final String OTG_5V_EN = "/sys/class/leds/otg_5v_en/brightness";
    private static final String RFID_POWER_EN = "/sys/class/leds/rfid_pwr_en/brightness";

    public static String getSystemProp(String prop, String def){
        try{
            Class SystemProperties = Class.forName("android.os.SystemProperties");
            Method method = SystemProperties.getMethod("get", String.class, String.class);
            Object ret = method.invoke(null, prop, def);

            Log.d(TAG, "get prop : " + ret.toString());
            return (String)ret;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void powerCtrl(int type){
        try {
            if(isSprdA12){
                FileOutputStream fOVbatPogoOtg = new FileOutputStream(POGO_OTG_5V_EN);
                FileOutputStream fOVbatOtg5v = new FileOutputStream(OTG_5V_EN);
                FileOutputStream fOVbatRfidPwr = new FileOutputStream(RFID_POWER_EN);
                switch (type){
                    case VCC_RFID_DOWN:
                        fOVbatPogoOtg.write(Integer.toString(0).getBytes());
                        fOVbatOtg5v.write(Integer.toString(0).getBytes());
                        fOVbatRfidPwr.write(Integer.toString(1).getBytes());
                        break;
                    case VCC_RFID_UP:
                        fOVbatPogoOtg.write(Integer.toString(1).getBytes());
                        fOVbatOtg5v.write(Integer.toString(1).getBytes());
                        fOVbatRfidPwr.write(Integer.toString(0).getBytes());
                        break;
                    default:
                        // nothing
                }
                fOVbatPogoOtg.close();
                fOVbatOtg5v.close();
                fOVbatRfidPwr.close();
            }else{
                FileOutputStream fOVbatSel = new FileOutputStream(POGO_VBATT_SEL_FILE_PATH);
                fOVbatSel.write(Integer.toString(type).getBytes());
                fOVbatSel.close();
            }
            Log.d(TAG,"Power control type: " + type);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
