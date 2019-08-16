package com.example.sfmtesting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportActivity extends AppCompatActivity {

    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_CODE_1 = 11;
    public static final int REQUEST_CODE_2 = 22;
    private static final String TAG = MainActivity.class.getSimpleName();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ArrayList<Integer> salesorder = new ArrayList<Integer>();
    ArrayList<Integer> deliveryorder = new ArrayList<Integer>();
    ArrayList<Integer> calllist = new ArrayList<Integer>();
    ArrayList<Integer> eclist = new ArrayList<Integer>();
    ArrayList<Date> orderdate = new ArrayList<Date>();
    ArrayList<Boolean> routelist = new ArrayList<Boolean>();
    //    @BindView(R.id.img_profile)
    ImageView imgProfile;
    EditText dateOfBirthET;
    EditText endDateET;
    TextView textSummaryEmpty;
    LinearLayout layoutSummaryEmpty;
    String selectedDate;
    String selectedEndDate;
    LinearLayout summaryData;
    DatePickerDialog dialogStart, dialogEnd;

    int totalcall;
    int totalec;
    int sototal;
    int dototal;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(ReportActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
//        imgProfile = findViewById(R.id.img_profile);
        pref = getSharedPreferences("MyPref", 0);
        editor = pref.edit();

        final ArrayList<Summary> summaries = new ArrayList<Summary>();

        final ListView listView = findViewById(R.id.mListsummary);

        final String sales_id = pref.getString("sales_id", "");

        final TextView namasales = findViewById(R.id.namesales);
        TextView namadc = findViewById(R.id.namadc);
        namasales.setText(pref.getString("sales_name", ""));
        namadc.setText(pref.getString("dc_name", ""));
        ImagePickerActivity.clearCache(ReportActivity.this);

        dateOfBirthET = findViewById(R.id.tv);
        endDateET = findViewById(R.id.tv2);
        textSummaryEmpty = findViewById(R.id.text_summary_empty);
        layoutSummaryEmpty = findViewById(R.id.layout_summary_empty);

        final ConstraintLayout constraintLayout = findViewById(R.id.header_summary);

//        listViewAdapter.notifyDataSetChanged();

//        final FragmentManager fm = getSupportFragmentManager();

        dateOfBirthET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int yearstart = c.get(Calendar.YEAR);
                int monthstart = c.get(Calendar.MONTH);
                int daystart = c.get(Calendar.DAY_OF_MONTH);
                dialogStart = new DatePickerDialog(ReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String selectedDate = year+"-"+(month+1)+"-"+dayOfMonth;

                        dateOfBirthET.setText(selectedDate);
                    }
                }, yearstart, monthstart, daystart);
                dialogStart.show();
            }
        });

        endDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int yearend = c.get(Calendar.YEAR);
                int monthend = c.get(Calendar.MONTH);
                int dayend = c.get(Calendar.DAY_OF_MONTH);
                dialogEnd = new DatePickerDialog(ReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String selectedDate = year+"-"+(month+1)+"-"+dayOfMonth;

                        endDateET.setText(selectedDate);
                    }
                }, yearend, monthend, dayend);
                dialogEnd.show();
//                AppCompatDialogFragment newFragment = new DatePickerFragment2();
//                newFragment.setTargetFragment(AccountFragment.this, REQUEST_CODE_2);
//                newFragment.show(fm, "datePicker2");
            }
        });

        final TextView call = findViewById(R.id.call);
        final TextView ec = findViewById(R.id.nilaivisit);
        final TextView totalso = findViewById(R.id.nilaiso);
        final TextView totaldo = findViewById(R.id.nilaido);
        final LinearLayout summaryData = findViewById(R.id.summary_data);

        Button summary = findViewById(R.id.btn2);
        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                final String start = dateOfBirthET.getText().toString();
                String end = endDateET.getText().toString();
                Date startDate = new Date();
                Date endDate = new Date();

                totalcall = 0;
                totalec = 0;
                sototal = 0;
                dototal = 0;
                salesorder.clear();
                deliveryorder.clear();


                summaries.clear();

                if(start.length() == 0 || end.length() == 0){
                    Toast.makeText(getApplicationContext(),"Pilihan tanggal tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        startDate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
                        Log.e("Tanggal Mulai", "" + startDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        endDate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
                        Log.e("Tanggal Selesai", "" + endDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String url = "https://sfa-api.pti-cosmetics.com/v_all_summary?sales_id=eq." + sales_id;
                    final Date finalStartDate = startDate;
                    final Date finalEndDate = endDate;
                    AndroidNetworking.get(url)
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsJSONArray(new JSONArrayRequestListener() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    JSONObject jo;
                                    Summary s = new Summary();
                                    boolean check = false;
                                    int index = 0;
                                    try {
                                        for (int i = 0; i < response.length(); i++) {
                                            jo = response.getJSONObject(i);
                                            String date = jo.getString("write_date");
                                            int call = jo.getInt("call");
                                            int notcall = jo.getInt("notcall");
                                            int ec = jo.getInt("ec");
                                            int notec = jo.getInt("notec");
                                            int penjualan = jo.getInt("penjualan_total");
                                            int delivorder = jo.getInt("pencapaian_total");
                                            int mcp = jo.getInt("mcp");
                                            boolean inroute = jo.getBoolean("inroute");


                                            Date tanggal = new SimpleDateFormat("yyyy-MM-dd").parse(date);

                                            if ((finalStartDate.before(tanggal) || finalStartDate.equals(tanggal)) && (finalEndDate.after(tanggal) || finalEndDate.equals(tanggal))) {
                                                s.setTanggal(tanggal);
                                                s.setCall(call);
                                                s.setNotCall(notcall);
                                                s.setEC(ec);
                                                s.setNotEC(notec);
                                                s.setPenjualan(penjualan);
                                                s.setTotal_do(delivorder);
                                                s.setInroute(inroute);
                                                s.setMcp(mcp);


//                                            Log.e("Data Masuk", s.toString());
                                                summaries.add(s);

                                                totalcall += call;
                                                totalec += ec;
                                                calllist.add(call);
                                                eclist.add(ec);
                                                routelist.add(inroute);
                                                salesorder.add(penjualan);
                                                deliveryorder.add(delivorder);
                                                orderdate.add(tanggal);
//                                            sototal[0] += penjualan;
//                                            dototal[0] += delivorder;

//                                            sototal[0] += summaries.get(index).getPenjualan();
//                                            dototal[0] += summaries.get(index).getTotal_do();

                                                index++;

                                                Log.e("Total SO", "Rp. " + sototal + ", Penjualan : " + penjualan);
//                                            listViewAdapter.notifyDataSetChanged();
//                                            Log.e("adapter", ""+listViewAdapter.getItem(i).toString());
                                            }
                                        }

                                        if (salesorder.size() > 0 && deliveryorder.size() > 0) {
                                            sototal += salesorder.get(0);
                                            dototal += deliveryorder.get(0);
                                        }

                                        for (int j = 1; j < salesorder.size(); j++) {
                                            Log.e("WOW", orderdate.get(j).toString());
                                            if (!orderdate.get(j).equals(orderdate.get(j - 1))) {
                                                sototal += salesorder.get(j);
                                                dototal += deliveryorder.get(j);
                                                Log.e("Penjualan", salesorder.get(j) + ", SO : " + sototal);
                                            }
                                        }

                                        call.setText(String.valueOf(totalcall));
                                        ec.setText(String.valueOf(totalec));
                                        Locale localeID = new Locale("in", "ID");
                                        NumberFormat formatRP = NumberFormat.getCurrencyInstance(localeID);
                                        totalso.setText(formatRP.format(sototal));
                                        totaldo.setText(formatRP.format(dototal));
//                                    Log.e("TextView", call.getText().toString()+"  "+ec.getText().toString()+"  "+totalso.getText().toString()+"  "+totaldo.getText().toString());
                                        constraintLayout.setVisibility(View.VISIBLE);
//                                    Log.e("adapter", ""+listViewAdapter.getItem(i).toString());
                                        final ListViewAdapter listViewAdapter = new ListViewAdapter(summaries);
                                        listView.setAdapter(listViewAdapter);
                                        if(summaries.size() == 0){
                                            layoutSummaryEmpty.setVisibility(View.VISIBLE);
                                            summaryData.setVisibility(View.GONE);
                                        } else {
                                            layoutSummaryEmpty.setVisibility(View.GONE);
                                            summaryData.setVisibility(View.VISIBLE);
                                        }

                                    } catch (JSONException e) {
                                        Log.e("CANT PARSE JSON", e.getMessage());
//                                Toast.makeText(getBaseContext(), "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                //ERROR
                                @Override
                                public void onError(ANError anError) {
                                    anError.printStackTrace();
                                    Log.e("PARSING ERROR", anError.getResponse() + " Error :" + anError.getMessage() + " - " + anError.getErrorDetail());
//                            Toast.makeText(getBaseContext(), "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }

//                listViewAdapter.notifyDataSetChanged();
            }
        });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_CODE_1 && resultCode == Activity.RESULT_OK) {
//            selectedDate = data.getStringExtra("selectedDate");
//            dateOfBirthET.setText(selectedDate);
//        }
//        if (requestCode == REQUEST_CODE_2 && resultCode == Activity.RESULT_OK) {
//            selectedEndDate = data.getStringExtra("selectedEndDate");
//            endDateET.setText(selectedEndDate);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof HistoricalFragment.OnFragmentInteractionListener) {
//            mListener = (HistoricalFragment.OnFragmentInteractionListener) context;
//        } //else {
//        //throw new RuntimeException(context.toString() + "must implement OnFragmentListener");
//        // }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    public interface OnFragmentInteractionListener {
//        void onFragmentInteraction(Uri uri);
//    }

    public class Summary {
        private int call;
        private int notCall;
        private int EC;
        private int notEC;
        private int penjualan;
        private int total_do;
        private int mcp;
        private boolean inroute;
        private Date tanggal;

        public int getCall() {
            return call;
        }

        public void setCall(int call) {
            this.call = call;
        }

        public int getNotCall() {
            return notCall;
        }

        public void setNotCall(int notCall) {
            this.notCall = notCall;
        }

        public int getEC() {
            return EC;
        }

        public void setEC(int EC) {
            this.EC = EC;
        }

        public int getNotEC() {
            return notEC;
        }

        public void setNotEC(int notEC) {
            this.notEC = notEC;
        }

        public int getPenjualan() {
            return penjualan;
        }

        public void setPenjualan(int penjualan) {
            this.penjualan = penjualan;
        }

        public int getMcp() {
            return mcp;
        }

        public void setMcp(int mcp) {
            this.mcp = mcp;
        }

        public boolean getInroute() {
            return inroute;
        }

        public void setInroute(boolean inroute) {
            this.inroute = inroute;
        }

        public Date getTanggal() {
            return tanggal;
        }

        public void setTanggal(Date tanggal) {
            this.tanggal = tanggal;
        }

        public int getTotal_do() {
            return total_do;
        }

        public void setTotal_do(int total_do) {
            this.total_do = total_do;
        }

        @NonNull
        @Override
        public String toString() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return "Summary " + format.format(getTanggal()) + " :\nCall : " + getCall() + "\nEC : "
                    + getEC() + "\n Dalam Rute : " + getInroute() + "\n SO : " + getPenjualan()
                    + "\nDO : " + getTotal_do();
        }
    }

    public class ViewHolder {
        TextView txtTanggal;
        TextView txtDalamRute;
        TextView txtCallIn;
        TextView txtECIn;
        TextView txtTotalSO;
        TextView txtTotalDO;
    }

    public class ListViewAdapter extends BaseAdapter implements Filterable {
        public ArrayList<Summary> s = new ArrayList<Summary>();
//        Context c;

        public ListViewAdapter(ArrayList<Summary> summaries) {
//            this.c = c;
            this.s = summaries;
        }

        @Override
        public int getCount() {
            return s.size();
        }

        @Override
        public Object getItem(int position) {
            return s.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = (View) LayoutInflater.from(ReportActivity.this).inflate(R.layout.model_row_account_summary, parent, false);
                holder.txtTanggal = convertView.findViewById(R.id.account_date);
                holder.txtDalamRute = convertView.findViewById(R.id.account_inroute);
                holder.txtCallIn = convertView.findViewById(R.id.account_call_inroute);
//            TextView txtCallOut = convertView.findViewById(R.id.account_call_outroute);
                holder.txtECIn = convertView.findViewById(R.id.account_ec_inroute);
//            TextView txtECOut = convertView.findViewById(R.id.account_ec_outroute);
                holder.txtTotalSO = convertView.findViewById(R.id.account_total_so);
                holder.txtTotalDO = convertView.findViewById(R.id.account_total_do);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.txtTanggal.setText("2000-01-01");
                holder.txtDalamRute.setText("");
                holder.txtCallIn.setText("");
//                holder.txtCallOut.setText("");
                holder.txtECIn.setText("");
//                viewHolder.txtECOut.setText("");
                holder.txtTotalSO.setText("");
                holder.txtTotalDO.setText("");
            }

            final Summary summary_acc = s.get(position);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tanggal = sdf.format(orderdate.get(position));

//            if (summary_acc.getInroute()) {
            holder.txtTanggal.setText(tanggal);
            if (routelist.get(position)) {
                holder.txtDalamRute.setText("Yes");
            } else {
                holder.txtDalamRute.setText("No");
            }
            holder.txtCallIn.setText("" + calllist.get(position));
//                txtCallOut.setText("");
            holder.txtECIn.setText("" + eclist.get(position));
//                txtECOut.setText("");
            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRP = NumberFormat.getCurrencyInstance(localeID);
            holder.txtTotalSO.setText(formatRP.format(salesorder.get(position)));
            holder.txtTotalDO.setText(formatRP.format(deliveryorder.get(position)));

            Log.e("Textview Listview_" + position, holder.txtCallIn.getText().toString() + "  "
                    + holder.txtECIn.getText().toString() + "  " + holder.txtTotalSO.getText().toString() + "  "
                    + holder.txtTotalDO.getText().toString() + holder.txtTanggal.getText().toString());

//            } else {
//                txtTanggal.setText(tanggal);
//                txtCallIn.setText("");
////                txtCallOut.setText("" + summary_acc.getCall());
//                txtCallIn.setText("");
////                txtECOut.setText("" + summary_acc.getEC());
//                txtTotalSO.setText("" + summary_acc.getPenjualan());
//                txtTotalDO.setText("-");
//            }
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return null;
        }
    }

}