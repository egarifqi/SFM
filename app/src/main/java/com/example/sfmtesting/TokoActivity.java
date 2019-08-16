package com.example.sfmtesting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

public class TokoActivity extends AppCompatActivity {

    SharedPreferences pref, prefToko;
    SharedPreferences.Editor editor, editorToko;
    boolean check = false;

    public class Program{
        private String nama;
        private String brand;
        private int p_ytd;
        private int p_mtd;
        private int t_ytd;
        private int t_mtd;
        private int totaltarget;
        private String startdate;
        private String enddate;
        private int pmtdnp;
        private int tmtdnp;

        public String getNama() {return nama;}

        public void setNama(String nama){this.nama = nama;}

        public String getBrand() {return brand;}

        public void setBrand(String brand){this.brand = brand;}

        public int getP_ytd() {return p_ytd;}

        public void setP_ytd(int p_ytd){this.p_ytd = p_ytd;}

        public int getT_ytd() {return t_ytd;}

        public void setT_ytd(int t_ytd){this.t_ytd = t_ytd;}

        public int getP_mtd() {return p_mtd;}

        public void setP_mtd(int p_mtd){this.p_mtd = p_mtd;}

        public int getT_mtd() {return t_mtd;}

        public void setT_mtd(int t_mtd){this.t_mtd = t_mtd;}

        public String getStartdate() {return startdate;}

        public void setStartdate(String startdate){this.startdate = startdate;}

        public String getEnddate() {return enddate;}

        public void setEnddate(String enddate){this.enddate = enddate;}

        public int getTotaltarget() {return totaltarget;}

        public void setTotaltarget(int totaltarget){this.totaltarget = totaltarget;}

        public int getPmtdnp() {return pmtdnp;}

        public void setPmtdnp(int pmtdnp){this.pmtdnp = pmtdnp;}

        public int getTmtdnp() {return tmtdnp;}

        public void setTmtdnp(int tmtdnp){this.tmtdnp = tmtdnp;}
    }

    public class Toko {
        /*
        INSTANCE FIELDS
        */
        private int id;
        private String storename;
        private String phone;
        private String outlettype;
        private String frequency;
        private String program;
        private String brand;
        /*
        GETTERS AND SETTERS
        */


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getStorename() {
            return storename;
        }

        public void setStorename(String storename) {
            this.storename = storename;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getOutlettype() {
            return outlettype;
        }

        public void setOutlettype(String outlettype) {
            this.outlettype = outlettype;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public String getProgram() {
            return program;
        }

        public void setProgram(String program) {
            this.program = program;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

    }

    public class ViewHolder{
        LinearLayout ytd;
        LinearLayout mtd;
        LinearLayout mtdnonprog;
        LinearLayout total;
        TextView nama_program;
        TextView periode_program;
        TextView brand_program;
        TextView target_program;
        TextView nominal_ytd;
        TextView target_ytd;
        TextView nominal_mtd;
        TextView target_mtd;
        TextView persentase_ytd;
        TextView persentase_mtd;
        TextView nominal_mtd_np;
        TextView target_mtd_np;
        TextView persentase_mtd_np;
    }

    public class ListViewAdapter extends BaseAdapter implements Serializable {
        public ArrayList<Program> currentList;
        Context c;
        ArrayList<Program> spacecrafts;
        Dialog dialog;

        public ListViewAdapter(Context c, ArrayList<Program> spacecrafts) {
            this.c = c;
            this.spacecrafts = spacecrafts;
            this.currentList = spacecrafts;
        }

        @Override
        public int getCount() {
            return spacecrafts.size();
        }

        @Override
        public Object getItem(int i) {
            return spacecrafts.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ViewHolder holder = new ViewHolder();
            if (view == null) {

                view = LayoutInflater.from(c).inflate(R.layout.model_row_program_toko, viewGroup, false);

                holder.ytd = (LinearLayout) view.findViewById(R.id.layoutytd);
                holder.mtd = (LinearLayout) view.findViewById(R.id.layoutmtd);
                holder.mtdnonprog = (LinearLayout) view.findViewById(R.id.layoutmtdnonprogram);
                holder.total = (LinearLayout) view.findViewById(R.id.layouttotal);
                holder.nama_program = (TextView) view.findViewById(R.id.namaprogram);
                holder.periode_program = (TextView) view.findViewById(R.id.periodeprogram);
                holder.brand_program = (TextView) view.findViewById(R.id.brandprogram);
                holder.target_program = (TextView) view.findViewById(R.id.targetprogram);
                holder.nominal_ytd = (TextView) view.findViewById(R.id.nominalytd);
                holder.target_ytd = (TextView) view.findViewById(R.id.targetytd);
                holder.nominal_mtd = (TextView) view.findViewById(R.id.nominalmtd);
                holder.target_mtd = (TextView) view.findViewById(R.id.targetmtd);
                holder.persentase_ytd = (TextView) view.findViewById(R.id.persentaseytd);
                holder.persentase_mtd = (TextView) view.findViewById(R.id.persentasemtd);
                holder.nominal_mtd_np = (TextView) view.findViewById(R.id.nominalbulan);
                holder.target_mtd_np = (TextView) view.findViewById(R.id.targetbulan);
                holder.persentase_mtd_np = (TextView) view.findViewById(R.id.persentasebulan);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
                holder.nama_program.setText("");
                holder.periode_program.setText("");
                holder.brand_program.setText("");
                holder.target_program.setText("");
                holder.nominal_ytd.setText("");
                holder.target_ytd.setText("");
                holder.persentase_ytd.setText("");
                holder.nominal_mtd.setText("");
                holder.target_mtd.setText("");
                holder.persentase_mtd.setText("");
                holder.nominal_mtd_np.setText("");
                holder.target_mtd_np.setText("");
                holder.persentase_mtd_np.setText("");
            }
            final Program p = (Program) this.getItem(i);
            holder.nama_program.setText(p.getNama());
            holder.brand_program.setText(p.getBrand());
            if (p.getBrand().equals("-")){
                holder.ytd.setVisibility(View.GONE);
                holder.mtd.setVisibility(View.GONE);
                holder.total.setVisibility(View.GONE);
                holder.periode_program.setText("-");
                holder.nominal_mtd_np.setText(""+p.getP_mtd());
                holder.target_mtd_np.setText(""+p.getT_mtd());
                if (p.getT_mtd() > 0){
                    holder.persentase_mtd_np.setText("("+p.getP_mtd()/p.getT_mtd()+"%)");
                } else {
                    holder.persentase_mtd_np.setText("("+"0"+"%)");
                }
            } else {
                holder.mtdnonprog.setVisibility(View.GONE);
                holder.nominal_ytd.setText(""+p.getP_ytd());
                holder.target_ytd.setText(""+p.getT_ytd());
                holder.persentase_ytd.setText("("+p.getP_ytd()/p.getT_ytd()+"%)");
                holder.periode_program.setText(p.getStartdate() +" - "+p.getEnddate());
                holder.target_program.setText(""+p.getTotaltarget());
                holder.nominal_mtd.setText(""+p.getP_mtd());
                holder.target_mtd.setText(""+p.getT_mtd());
                holder.persentase_mtd.setText("("+p.getP_mtd()/p.getT_mtd()+"%)");
            }



            return view;
        }

        public void refresh() {
            notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toko_new);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        prefToko = getApplicationContext().getSharedPreferences("TokoPref", 0);
        editorToko = prefToko.edit();
        String filterprogram = prefToko.getString("ref", "");
        String namatoko = prefToko.getString("partner_name", "");

//        TabLayout tabLayout = findViewById(R.id.tab_layouttoko);
//        tabLayout.addTab(tabLayout.newTab().setText(namatoko));

        LinearLayout visit = findViewById(R.id.takingorder);
        LinearLayout retur = findViewById(R.id.retur);
        LinearLayout delivery = findViewById(R.id.delivery);
        LinearLayout kondisitoko = findViewById(R.id.kondisitoko);
        Button selesai = findViewById(R.id.button_simpantoko);
        final TextView headertoko = findViewById(R.id.nameToko);
        final TextView notelp = findViewById(R.id.noTelp);
        TextView nohp = findViewById(R.id.noHP);
        final TextView channel = findViewById(R.id.channel);
//        final TextView storetype = findViewById(R.id.tipeoutlet);
        final TextView frekuensi = findViewById(R.id.frekuensi);
//        TextView program = findViewById(R.id.program);
//        TextView brand = findViewById(R.id.brand);

        final ListView listprogram = findViewById(R.id.listprogram);
        final ListViewAdapter[] adapter = new ListViewAdapter[1];

        final ArrayList<Toko> tokos = new ArrayList<>();
        final ArrayList<Program> programs = new ArrayList<>();

        Button tunda = findViewById(R.id.button_tundatoko);

        selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (com.example.sfmtesting.StatusToko.namatoko != null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(TokoActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.form_notec, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(true);
                    dialog.setTitle("Informasi");

                    dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(TokoActivity.this, TokoActivity.class);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(TokoActivity.this, ReasonNotECActivitty.class);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    Intent intent = new Intent(TokoActivity.this, KunjunganActivity.class);
                    startActivity(intent);
//                    dialog.dismiss();
                }
            }
        });
        tunda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(TokoActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.form_tunda, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setTitle("Informasi");

                dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TokoActivity.this, TokoActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TokoActivity.this, ReasonActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        String login_url = "https://sfa-api.pti-cosmetics.com/partner_detail?partner_ref=eq." + filterprogram;
        Log.e("url", login_url);
        AndroidNetworking.get(login_url).setPriority(Priority.HIGH).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jo;
                Toko s;
                Program p;
                int pencapaian = 0;
                int target = 0;
                int pytd = 0;
                int tytd = 0;
                int pmtd = 0;
                int tmtd = 0;
                int total = 0;
                String start_date = "";
                String end_date = "";
                try {
                    for (int i = 0; i < response.length(); i++) {
                        jo = response.getJSONObject(i);
                        int id = jo.getInt("partner_id");
                        String namastore = jo.getString("partner_name");
                        String nohp = jo.getString("phone");

                        String storetype = jo.getString("store_type_name");
                        String frekuensi = jo.getString("frekuensi");
                        String brand = jo.getString("brand");
                        String program = jo.getString("program_name");
                        if (program.equals("-")){
                            pencapaian = jo.getInt("pencapaian_mtd");
                            target = jo.getInt("target_bulan_ini");
                        } else {
                            total = jo.getInt("program_target_tahun");
                            start_date = jo.getString("start_date");
                            end_date = jo.getString("end_date");
                            pytd = jo.getInt("pencapaian_ytd");
                            tytd = jo.getInt("program_target_ytd");
                            pmtd = jo.getInt("pencapaian_mtd");
                            tmtd = jo.getInt("program_target_mtd");
                        }
                        Log.e("program", program + " / " + pencapaian +  " / " + target + " / " +  total + " / " +  start_date + " / " +  end_date + " / " +  pytd + " / " +  tytd + " / " +  pmtd + " / " +  tmtd);
                        s = new Toko();
                        s.setId(id);
                        s.setStorename(namastore);
                        s.setPhone(nohp);
                        s.setOutlettype(storetype);
                        s.setFrequency(frekuensi);
                        s.setBrand(brand);
                        s.setProgram(program);
                        tokos.add(s);
                        p = new Program();
                        if (s.getProgram().equals("-")){
                            p.setNama("Tidak mengikuti program");
                            p.setBrand(brand);
                            p.setP_mtd(pencapaian);
                            p.setT_mtd(target);
                        } else {
                            p.setNama(program);
                            p.setBrand(brand);
                            p.setP_mtd(pmtd);
                            p.setT_mtd(tmtd);
                            p.setP_ytd(pytd);
                            p.setT_ytd(tytd);
                            p.setStartdate(start_date);
                            p.setEnddate(end_date);
                            p.setTotaltarget(total);
                        }
                        programs.add(p);
                    }

                    headertoko.setText(tokos.get(0).getStorename());
                    if (!tokos.get(0).getPhone().equals("0")&&!tokos.get(0).getPhone().equals("null")){
                        notelp.setText(tokos.get(0).getPhone());
                    } else {
                        notelp.setText("");
                    }
                    channel.setText(tokos.get(0).getOutlettype());
                    frekuensi.setText(tokos.get(0).getFrequency());

                    final ListViewAdapter lvadapter = new ListViewAdapter(TokoActivity.this, programs);
                    lvadapter.notifyDataSetChanged();
                    listprogram.setAdapter(lvadapter);


                } catch (JSONException e) {
                    Log.e("CANT PARSE JSON", e.getMessage());
//                                Toast.makeText(getBaseContext(), "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
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

        visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(TokoActivity.this);
                LayoutInflater inflater = getLayoutInflater();
//                View dialogView = inflater.inflate(R.layout.form_takingorder, null);
//                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setTitle("TAKING ORDER");
                dialog.setMessage("Apakah anda akan melakukan TAKING ORDER?");

                dialog.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("IYA", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        Intent intent = new Intent(TokoActivity.this, TakingOrderBrandActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        retur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(TokoActivity.this);
                LayoutInflater inflater = getLayoutInflater();
//                View dialogView = inflater.inflate(R.layout.form_takingorder, null);
//                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setTitle("RETUR");
                dialog.setMessage("Apakah anda akan melakukan RETUR?");

                dialog.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("IYA", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        Intent intent = new Intent(TokoActivity.this, ReturBrandActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TokoActivity.this, BarangDOActivity.class);
                startActivity(intent);
            }
        });
        kondisitoko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TokoActivity.this, KondisiTokoActivity.class);
                startActivity(intent);
            }
        });


    }

    public String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while (itr.hasNext()) {
            String key = itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }

    ProgressDialog dialog;

    @Override
    public void onBackPressed() {
    }

}
