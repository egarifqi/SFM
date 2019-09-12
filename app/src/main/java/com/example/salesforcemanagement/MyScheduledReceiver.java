package com.example.salesforcemanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class MyScheduledReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent recievedIntent) {
        // TODO Auto-generated method stub

        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences("MyPref", 0);
        editor = sharedPreferences.edit();
        final DatabaseTokoHandler dbtoko = new DatabaseTokoHandler(context);
        final DatabaseProdukEBPHandler dbEBP = new DatabaseProdukEBPHandler(context);
        final DatabaseMHSHandler dbMHS = new DatabaseMHSHandler(context);
        final DatabaseNPDPromoHandler dbNPDP = new DatabaseNPDPromoHandler(context);

        com.example.salesforcemanagement.StatusSR.clearAll();
        com.example.salesforcemanagement.StatusToko.clearToko();
        Global.clearProduct();
        Globalemina.clearProduct();
        Globalmo.clearProduct();
        Globalputri.clearProduct();
        com.example.salesforcemanagement.TokoBelumDikunjungi.clearBelumDikunjungi();
        Log.e("LOGOUT STATUS", "DELETING LOCAL VARIABLE");
        dbtoko.deleteAll();
        dbEBP.deleteAll();
        dbMHS.deleteAll();
        dbNPDP.deleteAll();
        editor.clear();
        editor.commit();

        Toast.makeText(context, "Session Timeout", Toast.LENGTH_LONG).show();
        Log.e("MyScheduledReceiver", "Intent Fired");

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("truiton.ACTION_FINISH");
        context.sendBroadcast(broadcastIntent);
    }

}
