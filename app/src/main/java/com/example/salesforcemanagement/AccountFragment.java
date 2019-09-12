package com.example.salesforcemanagement;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

//import com.example.sfa.utils.FileCompressor;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    private static final int REQUEST_CODE_1 = 11;
    private static final int REQUEST_CODE_2 = 22;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private ArrayList<Date> orderdate = new ArrayList<>();
    private ArrayList<Boolean> routelist = new ArrayList<>();
    @BindView(R.id.img_profile)
    ImageView imgProfile;
    private EditText dateOfBirthET;
    private EditText endDateET;
    private ArrayList<Integer> salesorder = new ArrayList<>();
    private ArrayList<Integer> deliveryorder = new ArrayList<>();
    private ArrayList<Integer> calllist = new ArrayList<>();
    private ArrayList<Integer> eclist = new ArrayList<>();
    Toolbar toolbar;

    public AccountFragment() {
        // Required empty public constructor
    }

    @SuppressLint({"CommitPrefEdits", "SimpleDateFormat"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(Objects.requireNonNull(getActivity()));
        toolbar = view.findViewById(R.id.toolbar);
        imgProfile = view.findViewById(R.id.img_profile);
        pref = Objects.requireNonNull(getContext()).getSharedPreferences("MyPref", 0);
        editor = pref.edit();

        final ArrayList<Summary> summaries = new ArrayList<>();

        final ListView listView = view.findViewById(R.id.mListsummary);

        final String sales_id = pref.getString("sales_id", "");

        final TextView namasales = view.findViewById(R.id.namesales);
        TextView namadc = view.findViewById(R.id.namadc);
        namasales.setText(pref.getString("sales_name", ""));
        namadc.setText(pref.getString("dc_name", ""));
        com.example.salesforcemanagement.ImagePickerActivity.clearCache(getActivity());

        dateOfBirthET = view.findViewById(R.id.tv);
        endDateET = view.findViewById(R.id.tv2);

        final ConstraintLayout constraintLayout = view.findViewById(R.id.header_summary);


        final FragmentManager fm = getActivity().getSupportFragmentManager();

        dateOfBirthET.setOnClickListener(v -> {
            AppCompatDialogFragment newFragment = new DatePickerFragment();
            newFragment.setTargetFragment(AccountFragment.this, REQUEST_CODE_1);
            newFragment.show(fm, "datePicker1");
        });

        endDateET.setOnClickListener(v -> {
            AppCompatDialogFragment newFragment = new DatePickerFragment2();
            newFragment.setTargetFragment(AccountFragment.this, REQUEST_CODE_2);
            newFragment.show(fm, "datePicker2");
        });

        final TextView call = view.findViewById(R.id.call);
        final TextView ec = view.findViewById(R.id.nilaivisit);
        final TextView totalso = view.findViewById(R.id.nilaiso);
        final TextView totaldo = view.findViewById(R.id.nilaido);

        final int[] totalcall = {0};
        final int[] totalec = {0};
        final int[] sototal = {0};
        final int[] dototal = {0};

        Button summary = view.findViewById(R.id.btn2);
        summary.setOnClickListener(view1 -> {
            final String start = dateOfBirthET.getText().toString();
            String end = endDateET.getText().toString();
            Date startDate = new Date();
            Date endDate = new Date();

            totalcall[0] = 0;
            totalec[0] = 0;
            sototal[0] = 0;
            dototal[0] = 0;

            summaries.clear();

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

            String url = "http://10.3.181.177:3000/v_summary?sales_id=eq." + sales_id + "";
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
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jo = response.getJSONObject(i);
                                    String date = jo.getString("date");
                                    int call1 = jo.getInt("call");
                                    int notcall = jo.getInt("notcall");
                                    int ec1 = jo.getInt("ec");
                                    int notec = jo.getInt("notec");
                                    int penjualan = jo.getInt("total_penjualan");
                                    int delivorder = jo.getInt("total_do");
                                    int mcp = jo.getInt("mcp");
                                    boolean inroute = jo.getBoolean("inroute");


                                    Date tanggal = new SimpleDateFormat("yyyy-MM-dd").parse(date);

                                    if ((finalStartDate.before(tanggal) || finalStartDate.equals(tanggal)) && (finalEndDate.after(tanggal) || finalEndDate.equals(tanggal))) {
                                        s.setTanggal(tanggal);
                                        s.setCall(call1);
                                        s.setNotCall(notcall);
                                        s.setEC(ec1);
                                        s.setNotEC(notec);
                                        s.setPenjualan(penjualan);
                                        s.setTotal_do(delivorder);
                                        s.setInroute(inroute);
                                        s.setMcp(mcp);


                                        summaries.add(s);

                                        totalcall[0] += call1;
                                        totalec[0] += ec1;
                                        calllist.add(call1);
                                        eclist.add(ec1);
                                        routelist.add(inroute);
                                        salesorder.add(penjualan);
                                        deliveryorder.add(delivorder);
                                        orderdate.add(tanggal);


                                        Log.e("Total SO", "Rp. " + sototal[0] + ", Penjualan : " + penjualan);
                                    }
                                }

                                if (salesorder.size() > 0 && deliveryorder.size() > 0) {
                                    sototal[0] += salesorder.get(0);
                                    dototal[0] += deliveryorder.get(0);
                                }

                                for (int j = 1; j < salesorder.size(); j++) {
                                    Log.e("WOW", orderdate.get(j).toString());
                                    if (!orderdate.get(j).equals(orderdate.get(j - 1))) {
                                        sototal[0] += salesorder.get(j);
                                        dototal[0] += deliveryorder.get(j);
                                        Log.e("Penjualan", salesorder.get(j) + ", SO : " + sototal[0]);
                                    }
                                }

                                call.setText(String.valueOf(totalcall[0]));
                                ec.setText(String.valueOf(totalec[0]));
                                Locale localeID = new Locale("in", "ID");
                                NumberFormat formatRP = NumberFormat.getCurrencyInstance(localeID);
                                totalso.setText(formatRP.format(sototal[0]));
                                totaldo.setText(formatRP.format(dototal[0]));
                                constraintLayout.setVisibility(View.VISIBLE);
                                final ListViewAdapter listViewAdapter = new ListViewAdapter(summaries);
                                listView.setAdapter(listViewAdapter);

                            } catch (JSONException e) {
                                Log.e("CANT PARSE JSON", e.getMessage());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.e("PARSING ERROR", "Error :" + anError.getMessage());
                        }
                    });

        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_1 && resultCode == Activity.RESULT_OK) {
            String selectedDate = data.getStringExtra("selectedDate");
            dateOfBirthET.setText(selectedDate);
        }
        if (requestCode == REQUEST_CODE_2 && resultCode == Activity.RESULT_OK) {
            String selectedEndDate = data.getStringExtra("selectedEndDate");
            endDateET.setText(selectedEndDate);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

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

        void setNotCall(int notCall) {
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

        void setNotEC(int notEC) {
            this.notEC = notEC;
        }

        int getPenjualan() {
            return penjualan;
        }

        void setPenjualan(int penjualan) {
            this.penjualan = penjualan;
        }

        public int getMcp() {
            return mcp;
        }

        void setMcp(int mcp) {
            this.mcp = mcp;
        }

        public boolean getInroute() {
            return inroute;
        }

        public void setInroute(boolean inroute) {
            this.inroute = inroute;
        }

        Date getTanggal() {
            return tanggal;
        }

        void setTanggal(Date tanggal) {
            this.tanggal = tanggal;
        }

        int getTotal_do() {
            return total_do;
        }

        void setTotal_do(int total_do) {
            this.total_do = total_do;
        }

        @NonNull
        @Override
        public String toString() {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
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
        public ArrayList<Summary> s;

        public ListViewAdapter(ArrayList<Summary> summaries) {
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

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.model_row_account_summary, parent, false);
                holder.txtTanggal = convertView.findViewById(R.id.account_date);
                holder.txtDalamRute = convertView.findViewById(R.id.account_inroute);
                holder.txtCallIn = convertView.findViewById(R.id.account_call_inroute);
                holder.txtECIn = convertView.findViewById(R.id.account_ec_inroute);
                holder.txtTotalSO = convertView.findViewById(R.id.account_total_so);
                holder.txtTotalDO = convertView.findViewById(R.id.account_total_do);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.txtTanggal.setText("2000-01-01");
                holder.txtDalamRute.setText("");
                holder.txtCallIn.setText("");
                holder.txtECIn.setText("");
                holder.txtTotalSO.setText("");
                holder.txtTotalDO.setText("");
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tanggal = sdf.format(orderdate.get(position));

            holder.txtTanggal.setText(tanggal);
            if (routelist.get(position)) {
                holder.txtDalamRute.setText("Yes");
            } else {
                holder.txtDalamRute.setText("No");
            }
            holder.txtCallIn.setText("" + calllist.get(position));
            holder.txtECIn.setText("" + eclist.get(position));
            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRP = NumberFormat.getCurrencyInstance(localeID);
            holder.txtTotalSO.setText(formatRP.format(salesorder.get(position)));
            holder.txtTotalDO.setText(formatRP.format(deliveryorder.get(position)));

            Log.e("Textview Listview_" + position, holder.txtCallIn.getText().toString() + "  "
                    + holder.txtECIn.getText().toString() + "  " + holder.txtTotalSO.getText().toString() + "  "
                    + holder.txtTotalDO.getText().toString() + holder.txtTanggal.getText().toString());
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return null;
        }
    }

}
