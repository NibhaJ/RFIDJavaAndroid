package com.example.rfidreadersample.util;

import java.util.ArrayList;
import java.util.List;

public class Frequency {

    public static List<String> indexGetFre(int index) {
        switch (index) {
            case 0:
                return createFrequency(920.625, 924.375, 0.250);
            case 1:
                return createFrequency(840.625, 844.375, 0.250);
            case 2:
                List<String> temp = new ArrayList<String>();
                temp.addAll(createFrequency(840.625, 844.375, 0.250));
                temp.addAll(createFrequency(920.625, 924.375, 0.250));
                return temp;
            case 3:
                return createFrequency(902.750, 927.250, 0.500);
            case 4:
                return createFrequency(865.700, 868.100, 0.600);
            case 5:
                return createFrequency(916.800, 920.400, 1.2);
            case 6:
                return createFrequency(922.250, 927.750, 0.500);
            case 7:
                return createFrequency(923.125, 925.125, 0.250);
            case 8:
                return createFrequency(866.600, 867.400, 0.200);
            case 9:
                return createFrequency(802.750, 998.750, 1.0);
            default:
                return null;
        }
    }

    public static List<String> indexGetChildFre(int index) {
        switch (index) {
            case 0:
                ArrayList<String> child0 = new ArrayList<>();
                child0.add("920.625-922.375");
                child0.add("922.625-924.375");
                return child0;
            case 1:
                ArrayList<String> child1 = new ArrayList<>();
                child1.add("840.625-844.375");
                return child1;
            case 2:
                ArrayList<String> child2 = new ArrayList<>();
                child2.add("840.625-924.375");
                return child2;
            case 3:
                ArrayList<String> child3 = new ArrayList<>();
                child3.add("902.750-910.250");
                child3.add("911.750-918.250");
                child3.add("919.750-927.250");
                return child3;
            case 4:
                ArrayList<String> child4 = new ArrayList<>();
                child4.add("866.300-868.000");
                return child4;
            case 5:
                ArrayList<String> child5 = new ArrayList<>();
                child5.add("916.800-920.400");
                return child5;
            case 6:
                ArrayList<String> child6 = new ArrayList<>();
                child6.add("922.250-927.750");
                return child6;
            case 7:
                ArrayList<String> child7 = new ArrayList<>();
                child7.add("923.125-925.125");
                return child7;
            case 8:
                ArrayList<String> child8 = new ArrayList<>();
                child8.add("866.600-867.400");
                return child8;
            case 9:
                ArrayList<String> child9 = new ArrayList<>();
                child9.add("802.750-811.750");
                child9.add("812.750-821.750");
                child9.add("822.750-831.750");
                child9.add("832.750-841.750");
                child9.add("842.750-851.750");
                child9.add("852.750-861.750");
                child9.add("862.750-871.750");
                child9.add("872.750-881.750");
                child9.add("882.750-891.750");
                child9.add("892.750-901.750");
                child9.add("902.750-911.750");
                child9.add("912.750-921.750");
                child9.add("922.750-931.750");
                child9.add("932.750-941.750");
                child9.add("942.750-951.750");
                child9.add("952.750-961.750");
                child9.add("962.750-971.750");
                child9.add("972.750-981.750");
                child9.add("982.750-991.750");
                child9.add("992.750-998.750");
                return child9;
            default:
                return null;
        }
    }

    public static List<String> createFrequency(double start, double end, double step) {
        List<String> temp = new ArrayList<String>();
        temp.add(formatStr(start));
        for (double i = start; i < end; i += step) {
            start = start + step;
            temp.add(formatStr(start));
        }
        return temp;
    }

    public static String formatStr(double value) {
        return String.format("%.3f", value);
    }


}
