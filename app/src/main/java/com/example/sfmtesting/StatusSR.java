package com.example.sfmtesting;

public class StatusSR {
    public static String namaSR = "";
    public static String idSR = "";
    public static int totalCall = 0;
    public static int totalNotCall = 0;
    public static int totalEC = 0;
    public static int totalNotEC = 0;
    public static int subtotal = 0;
    public static int totalOrder = 0;
    public static int callPlan;
    public static int dalamRute = 0;
    public static int luarRute = 0;
    public static int ECdalamRute = 0;
    public static int ECluarRute = 0;
    public static int id_inc = 1;
    public static int wardahAch = 0;
    public static int eminaAch = 0;
    public static int makeoverAch = 0;
    public static int putriAch = 0;

    public static void clearAll() {
        namaSR = "";
        idSR = "";
        totalOrder = 0;
        totalNotCall = 0;
        totalEC = 0;
        totalCall = 0;
        totalNotEC = 0;
        subtotal = 0;
        callPlan = 0;
        id_inc = 1;
        dalamRute = 0;
        luarRute = 0;
        ECdalamRute = 0;
        ECluarRute = 0;
    }

}
