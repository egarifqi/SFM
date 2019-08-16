package com.example.sfmtesting;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.sfmtesting.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView DisplayDateTime;
    Calendar calander;
    SimpleDateFormat simpledateformat;
    String Date;
    long progress;
    ProgressBar progressBar;

    public HomeFragment() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home_new, container, false);
        ImageView Kunjungan = view.findViewById(R.id.kunjungan);
        ImageView ToDoList = view.findViewById(R.id.todolist);
        ImageView Report = view.findViewById(R.id.report);
        ImageView Logout = view.findViewById(R.id.logouthome);
//        CalendarView calendarView = view.findViewById(R.id.calendar);
//        calendarView.setFocusedMonthDateColor(Color.RED);
//        calendarView.setUnfocusedMonthDateColor(Color.BLACK);
//        calendarView.addD
        pref = getActivity().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        String sales_id = pref.getString("sales_id", "");
        progressBar = (ProgressBar) view.findViewById(R.id.pBar3);
        DisplayDateTime = view.findViewById(R.id.tanggalsummaryvisit);

        calander = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("M");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
        Date = simpledateformat.format(calander.getTime());
        final String bulan = sdf.format(calander.getTime());
        final String tahun = sdf2.format(calander.getTime());
        DisplayDateTime.setText(Date);
        editor.putString("write_date", Date);
        editor.commit();

        TextView callTV = view.findViewById(R.id.call_mcp);
        TextView nameTV = view.findViewById(R.id.nameUser);
//        TextView callnotTV = view.findViewById(R.id.call_notmcp);
        TextView TV = view.findViewById(R.id.mcp);
        TextView ecTV = view.findViewById(R.id.ec_mcp);
//        TextView ecnotTV = view.findViewById(R.id.ec_notmcp);
        TextView notecTV = view.findViewById(R.id.not_ec);
        TextView calloutrouteTV = view.findViewById(R.id.call_outroute);
        TextView ecoutrouteTV = view.findViewById(R.id.ec_outroute);
        TextView penjualanTV = view.findViewById(R.id.penjualan_statusvisit);
        final TextView targetwardahTV = view.findViewById(R.id.pptwardah);
        final TextView targeteminaTV = view.findViewById(R.id.pptemina);
        final TextView targetmoTV = view.findViewById(R.id.pptmo);
        final TextView targetputriTV = view.findViewById(R.id.pptputri);
        final TextView targettotalTV = view.findViewById(R.id.targett);
        final TextView pencapaianTV = view.findViewById(R.id.pencapaian);
        final TextView persenTV = (TextView) view.findViewById(R.id.persen);

        callTV.setText(String.valueOf(pref.getInt("calldr",com.example.sfmtesting.StatusSR.dalamRute)));
        nameTV.setText(pref.getString("sales_name",com.example.sfmtesting.StatusSR.namaSR));
//        callnotTV.setText(String.valueOf(StatusSR.totalCall));
        TV.setText(String.valueOf(pref.getInt("datamcp", com.example.sfmtesting.StatusSR.callPlan)));
        ecTV.setText(String.valueOf(pref.getInt("ecdr",com.example.sfmtesting.StatusSR.ECdalamRute)));
//        ecnotTV.setText(String.valueOf(StatusSR.totalEC));
        notecTV.setText(String.valueOf(pref.getInt("necdr",com.example.sfmtesting.StatusSR.totalNotEC)));
        calloutrouteTV.setText(String.valueOf(pref.getInt("calllr",com.example.sfmtesting.StatusSR.luarRute)));
        ecoutrouteTV.setText(String.valueOf(pref.getInt("eclr",com.example.sfmtesting.StatusSR.ECluarRute)));
        Locale localeID = new Locale("in", "ID");
        final NumberFormat formatRP = NumberFormat.getCurrencyInstance(localeID);
        penjualanTV.setText(formatRP.format(pref.getInt("ordertotal",com.example.sfmtesting.StatusSR.totalOrder)));

        StatusSR.dalamRute = pref.getInt("calldr",com.example.sfmtesting.StatusSR.dalamRute);
        StatusSR.callPlan = pref.getInt("datamcp", com.example.sfmtesting.StatusSR.callPlan);
        StatusSR.ECdalamRute = pref.getInt("ecdr",com.example.sfmtesting.StatusSR.ECdalamRute);
        StatusSR.totalNotEC = pref.getInt("necdr",com.example.sfmtesting.StatusSR.totalNotEC);
        StatusSR.luarRute = pref.getInt("calllr",com.example.sfmtesting.StatusSR.luarRute);
        StatusSR.ECluarRute = pref.getInt("eclr",com.example.sfmtesting.StatusSR.ECluarRute);
        StatusSR.totalOrder = pref.getInt("ordertotal", StatusSR.totalOrder);

        String login_url = "https://sfa-api.pti-cosmetics.com/v_all_summary?sales_id=eq." + sales_id+"&&inroute=eq.true";
        AndroidNetworking.get(login_url).setPriority(Priority.HIGH).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jo;
                int totaltarget;
                int totalpenjualan = 0;
                int wardah = 0;
                int mo = 0;
                int emina = 0;
                int putri = 0;

                try {
                    int target_w = response.getJSONObject(0).getInt("target_wardah");
                    int target_m = response.getJSONObject(0).getInt("target_make_over");
                    int target_e = response.getJSONObject(0).getInt("target_emina");
                    int target_p = response.getJSONObject(0).getInt("target_putri");

                    for (int i = 0; i < response.length(); i++) {
                        jo = response.getJSONObject(i);
                        String bulanjson = jo.getString("month");
                        String tahunjson = jo.getString("year");
                        int penjualan_w = jo.getInt("pencapaian_wardah");
                        int penjualan_m = jo.getInt("pencapaian_make_over");
                        int penjualan_e = jo.getInt("pencapaian_emina");
                        int penjualan_p = jo.getInt("pencapaian_putri");

//                        int nominal = Integer.parseInt(target);
//                        totaltarget += target;
                        if (bulan.equals(bulanjson) && tahun.equals(tahunjson)) {
                            totalpenjualan += penjualan_w +penjualan_e + penjualan_m + penjualan_p;
                            wardah += penjualan_w;
                            mo += penjualan_m;
                            emina += penjualan_e;
                            putri += penjualan_p;
                        }

                    }
                    totaltarget = target_e+target_m+target_p+target_w;
                    targettotalTV.setText(formatRP.format(totaltarget));
                    pencapaianTV.setText(formatRP.format(totalpenjualan));
                    targetwardahTV.setText(formatRP.format(wardah)+" / "+formatRP.format(target_w));
                    targetmoTV.setText(formatRP.format(mo)+" / "+formatRP.format(target_m));
                    targeteminaTV.setText(formatRP.format(emina)+" / "+formatRP.format(target_e));
                    targetputriTV.setText(formatRP.format(putri)+" / "+formatRP.format(target_p));

                    Thread.sleep(200);

                    if (totaltarget > 0){
                        progress = (long)(totalpenjualan/(totaltarget/100));
                    } else {
                        progress = 0;
                    }

                    persenTV.setText(progress+"%");

                    if(progress <= 100){
                        progress = progress/2;
                    }
                    else {
                        progress = progress*3/8;
                    }
                    progressBar.setProgress((int) progress);


                } catch (JSONException e) {
                    Log.e("CANT PARSE JSON", e.getMessage());
//                                Toast.makeText(getBaseContext(), "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //ERROR
            @Override
            public void onError(ANError anError) {
                anError.printStackTrace();
                Log.e("PARSING ERROR", "Error :"+anError.getMessage());
//                            Toast.makeText(getBaseContext(), "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        Kunjungan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), com.example.sfmtesting.KunjunganActivity.class);
                getActivity().startActivity(intent);
            }
        });

        ToDoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListTokoOrderActivity.class);
                getActivity().startActivity(intent);
            }
        });

        Report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), com.example.sfmtesting.ReportActivity.class);
                getActivity().startActivity(intent);
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Dialog);

                if (com.example.sfmtesting.StatusSR.dalamRute + com.example.sfmtesting.StatusSR.totalNotCall >= com.example.sfmtesting.StatusSR.callPlan) {

                    dialog.setCancelable(true);
                    dialog.setTitle("LOGOUT");
                    dialog.setMessage("Apakah Anda yakin? Anda tidak bisa lagi melihat pencapaian hari ini jika logout");
                    dialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            LoggingOut loggingOut = new LoggingOut();
                            loggingOut.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        }
                    });

                    dialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();

                } else {
                    dialog.setMessage("Masih ada toko yang belum dikunjungi atau masih tertunda.\nApakah anda akan melakukan kunjungan ke toko-toko tersebut?");
                    dialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(getContext(), com.example.sfmtesting.KunjunganActivity.class);
                            startActivity(intent);
                        }
                    });

                    dialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            Intent intent = new Intent(getContext(), com.example.sfmtesting.KunjunganYangBelumActivity.class);
                            startActivity(intent);
                        }
                    });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                }
            }
        });

        return view;
    }

    private class LoggingOut extends AsyncTask<String, Void, String>{

        final DatabaseTokoHandler dbtoko = new DatabaseTokoHandler(getContext());
        final DatabaseProdukEBPHandler dbEBP = new DatabaseProdukEBPHandler(getContext());
        final DatabaseMHSHandler dbMHS = new DatabaseMHSHandler(getContext());
        final DatabaseNPDPromoHandler dbNPDP = new DatabaseNPDPromoHandler(getContext());
        final ArrayList<com.example.sfmtesting.TokoDalamRute> tdr = dbtoko.getAllToko();

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog =  new ProgressDialog(getContext());
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Sedang logout...\nMohon tunggu sebentar...");
            dialog.show();
            Log.e("LOGOUT STATUS", "STARTING...");
//            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            com.example.sfmtesting.StatusSR.clearAll();
            com.example.sfmtesting.StatusToko.clearToko();
            Global.clearProduct();
            Globalemina.clearProduct();
            Globalmo.clearProduct();
            Globalputri.clearProduct();
            com.example.sfmtesting.TokoBelumDikunjungi.clearBelumDikunjungi();
            Log.e("LOGOUT STATUS", "DELETING LOCAL VARIABLE");
            dbtoko.deleteAll();
            dbEBP.deleteAll();
            dbMHS.deleteAll();
            dbNPDP.deleteAll();
            editor.clear();
            editor.commit();


            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            dialog.dismiss();
            Log.e("LOGOUT STATUS", "DONE");
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            AlertDialog.Builder popup = new AlertDialog.Builder(getContext());
            popup.setTitle("Log Out");
            popup.setMessage("Log out berhasil!");
            popup.setCancelable(false);
            popup.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
            popup.show();
//            super.onPostExecute(s);
        }
    }

}
