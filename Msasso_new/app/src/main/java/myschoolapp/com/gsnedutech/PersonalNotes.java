package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.PersonalNotesObj;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.db.NotesDatabase;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PersonalNotes extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = PersonalNotes.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_color_list)
    RecyclerView rvColorList;
    @BindView(R.id.rv_notes)
    RecyclerView rvNotes;
    @BindView(R.id.ll_add_notes)
    LinearLayout llAddNotes;
    @BindView(R.id.nested_sv_list)
    NestedScrollView nestedScrollView;

    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.tv_message_count)
    TextView tvMessageCount;
    @BindView(R.id.et_desscription)
    EditText etDescription;

    @BindView(R.id.tv_date)
    TextView tvDate;

    @BindView(R.id.tv_note_not_available)
    TextView tvNoteNotAvailable;

    @BindView(R.id.ll_desc)
    LinearLayout llDesc;

    NoteAdapter noteAdapter;
    ColorAdapter colorAdapter;

    int selectedNoteColor = 0;
    boolean flagEdit = false;
    int editPosition=-1;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    List<PersonalNotesObj> listNotes = new ArrayList<>();
//    int[] colorArray = {Color.rgb(255,255,136),Color.rgb(161,226,247),Color.rgb(253,191,208),Color.rgb(196,241,178),
//            Color.rgb(255,156,238),Color.rgb(178,130,255)};

    String[] colorArray = {"#FFFF88","#A1E2F7","#FDBFD0","#C4F1B2","#FF9CEE","#B282FF"};
    NotesDatabase ndb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_notes);
        ButterKnife.bind(this);

        init();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dd = sd.parse(sd.format(new Date()));
            System.out.println(sd.format(dd));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNetworkAvail = false;
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, PersonalNotes.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getNotes();
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }


    private void init() {
        ndb = NotesDatabase.getInstance(this);

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvDate.setText(new SimpleDateFormat("MMMM dd, yyyy").format(new Date()));

        nestedScrollView.setVisibility(View.VISIBLE);

        handleEditTextScrollable(etDescription,R.id.et_desscription);

        findViewById(R.id.iv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nestedScrollView.setVisibility(View.GONE);
                llAddNotes.setVisibility(View.VISIBLE);
            }
        });

        rvColorList.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        colorAdapter = new ColorAdapter();
        rvColorList.setAdapter(colorAdapter);

        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length()>0 && etDescription.getText().toString().length()>0){
                    findViewById(R.id.btn_save).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.btn_save).setVisibility(View.GONE);
                }

            }
        });
        InputFilter[] FilterArray = new InputFilter[1];
        String ss = getResources().getString(R.string.text_count);
        FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(ss));
        etDescription.setFilters(FilterArray);
        tvMessageCount.setText(MessageFormat.format("0/{0}", ss));
        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvMessageCount.setText(MessageFormat.format("{0}/{1}", s.length(),ss));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length()>0 && etTitle.getText().toString().length()>0){
                    findViewById(R.id.btn_save).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.btn_save).setVisibility(View.GONE);
                }
            }
        });


        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flagEdit){
//

                    editNote(etDescription.getText().toString(),etTitle.getText().toString(),colorArray[selectedNoteColor]);
                }else {
//

                    addNotes(etDescription.getText().toString(),etTitle.getText().toString(),colorArray[selectedNoteColor]);
                }
//                getNotes();
                flagEdit=false;
                handleResult(editPosition);
            }
        });

    }


    public static void handleEditTextScrollable(EditText editText, final int resId) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == resId) {
                    ViewParent parent = v.getParent();
                    while (!(parent instanceof NestedScrollView)) {
                        parent = parent.getParent();
                    }
                    parent.requestDisallowInterceptTouchEvent(true);

                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            parent.requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
    }


    private void editNote(String note,String title, String color) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jObj = new JSONObject();
        try{
            jObj.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
            jObj.put("personalNote",note);
            jObj.put("personalNoteId",listNotes.get(editPosition).getPersonalNoteId()+"");
            jObj.put("color",color);
            jObj.put("personalNoteTitle",title);
        }catch (Exception e){

        }

        utils.showLog(TAG,jObj.toString());

        RequestBody body = RequestBody.create(JSON, jObj.toString());


        Request post = new Request.Builder()
                .url(new AppUrls().UpdateStudentPersonalNote)
                .post(body)
                //.headers(MyUtils.addHeaders(sh_Pref))
                .build();

        utils.showLog(TAG, "url " + new AppUrls().UpdateStudentPersonalNote);

        client.newCall(post).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.alertDialog(3,PersonalNotes.this,"Alert","Something Went Wrong!","okay","okay",false);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                utils.showLog(TAG,"response "+resp);
                try {
                    JSONObject json = new JSONObject(resp);
                    if (json.getString("StatusCode").equalsIgnoreCase("200")){
                        getNotes();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void addNotes(String note,String title, String color) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jObj = new JSONObject();
        try{
            jObj.put("personalNote",note);
            jObj.put("studentId",sObj.getStudentId());
            jObj.put("color",color);
            jObj.put("personalNoteTitle",title);
        }catch (Exception e){

        }

        JSONArray jArray = new JSONArray();
        jArray.put(jObj);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
            jsonObject.put("insertRecords",jArray);

        } catch (Exception e) {

        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());


        Request post = new Request.Builder()
                .url(new AppUrls().AddPersonalNote)
                .post(body)
                .build();

        utils.showLog(TAG, "url " + new AppUrls().AddPersonalNote);

        client.newCall(post).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.alertDialog(3,PersonalNotes.this,"Alert","Something Went Wrong!","okay","okay",false);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                try {
                    JSONObject json = new JSONObject(resp);
                    if (json.getString("StatusCode").equalsIgnoreCase("200")){
                        getNotes();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private void deleteNotes(String noteId) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        Request get = new Request.Builder()
                .url(new AppUrls().DeleteStudentPersonalNote+"schemaName="+sh_Pref.getString("schema", "")+"&personalNoteId="+noteId)
                .build();

        utils.showLog(TAG, "url " + new AppUrls().DeleteStudentPersonalNote+"schemaName="+sh_Pref.getString("schema", "")+"&personalNoteId="+noteId);

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.alertDialog(3,PersonalNotes.this,"Alert","Something Went Wrong!","okay","okay",false);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                utils.showLog(TAG,"delete "+resp);
                try {
                    JSONObject json = new JSONObject(resp);
                    if (json.getString("StatusCode").equalsIgnoreCase("200")){
                        getNotes();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    void getNotes(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                utils.showLoader(PersonalNotes.this);
            }
        });

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        Request get = new Request.Builder()
                .url(new AppUrls().GetStudentPersonalNotes+"schemaName="+sh_Pref.getString("schema", "")+"&studentId="+sObj.getStudentId())
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        utils.showLog(TAG, "url " + new AppUrls().GetStudentPersonalNotes+"schemaName="+sh_Pref.getString("schema", "")+"&studentId="+sObj.getStudentId());

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody body = response.body();
                String resp = body.string();

                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("personalNotes");

                        listNotes.clear();
                        listNotes.addAll(new Gson().fromJson(jsonArray.toString(), new TypeToken<List<PersonalNotesObj>>() {
                        }.getType()));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvNotes.setVisibility(View.VISIBLE);
                                noteAdapter = new NoteAdapter();
                                rvNotes.setLayoutManager(new LinearLayoutManager(PersonalNotes.this));
                                rvNotes.setAdapter(noteAdapter);
                            }
                        });


                    } else if (jsonObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(jsonObject)) { //TODO New Changes
                        String message = jsonObject.getString(AppConst.MESSAGE);
                        runOnUiThread(() -> {
                            utils.dismissDialog();
                            MyUtils.forceLogoutUser(toEdit, PersonalNotes.this, message, sh_Pref);
                        });
                        return;
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvNotes.setVisibility(View.GONE);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });
    }

    class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

        @NonNull
        @Override
        public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(PersonalNotes.this).inflate(R.layout.item_notes,parent,false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);

            holder.tvTitle.setText(listNotes.get(position).getPersonalNoteTitle());
            holder.tvNote.setText(listNotes.get(position).getPersonalNote());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd");
            try {
                holder.tvCreatedOn.setText("Created on " + sdf1.format(sdf.parse(listNotes.get(position).getCreatedDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (listNotes.get(position).getUpdatedDate().length()>0){
                try {
                    holder.tvCreatedOn.setText("Updated at " + sdf1.format(sdf.parse(listNotes.get(position).getUpdatedDate())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
//
            if (listNotes.get(position).getColor().length()>4){
                try {
                    holder.llMainBackground.setBackgroundColor(Color.parseColor(listNotes.get(position).getColor()));
                }catch (NumberFormatException e){

                }
            }

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(PersonalNotes.this)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Are you sure you want to delete this note?")
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteNotes(listNotes.get(position).getPersonalNoteId()+"");
                                    dialog.dismiss();

                                }
                            })
                            .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
            });
            holder.ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flagEdit = true;
                    editPosition=position;
                    handleResult(position);
                }
            });

        }

        @Override
        public int getItemCount() {
            return listNotes.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle,tvNote, tvCreatedOn;
            ImageView ivEdit,ivDelete;
            LinearLayout llMainBackground;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.tv_title);
                tvNote = itemView.findViewById(R.id.tv_note);
                ivEdit = itemView.findViewById(R.id.iv_edit);
                ivDelete = itemView.findViewById(R.id.iv_delete);
                llMainBackground = itemView.findViewById(R.id.ll_main_bg);
                tvCreatedOn = itemView.findViewById(R.id.tv_createddate);

            }
        }
    }



    private void handleResult(int position) {
        if (flagEdit){
            nestedScrollView.setVisibility(View.GONE);
            llAddNotes.setVisibility(View.VISIBLE);
            etTitle.setText(listNotes.get(position).getPersonalNoteTitle());
            etDescription.setText(listNotes.get(position).getPersonalNote());
            selectedNoteColor = getColorPosition(listNotes.get(position).getColor());
            colorAdapter.notifyDataSetChanged();
        }else{
            etTitle.setText("");
            etDescription.setText("");
            llAddNotes.setVisibility(View.GONE);
            nestedScrollView.setVisibility(View.VISIBLE);
            selectedNoteColor = 0;
            colorAdapter.notifyDataSetChanged();
            if (noteAdapter!=null)
                noteAdapter.notifyDataSetChanged();
            editPosition=-1;
        }
    }

    private int getColorPosition(String color) {
        int i = 0;
        for (int j=0;j<colorArray.length;j++){
            if (colorArray[j].equalsIgnoreCase(color)){
                i=j;
                break;
            }
        }
        return i;
    }

    class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder>{

        @NonNull
        @Override
        public ColorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(PersonalNotes.this).inflate(R.layout.item_note_color,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ColorAdapter.ViewHolder holder, int position) {

            holder.llColor.setBackgroundColor(Color.parseColor(colorArray[position]));
            if (position==selectedNoteColor){
                llDesc.setBackgroundColor(Color.parseColor(colorArray[position]));
                holder.ivColor.setImageResource(R.drawable.ic_tick_white);
            }else {
                holder.ivColor.setImageResource(0);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedNoteColor = position;
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return colorArray.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivColor;
            LinearLayout llColor;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivColor = itemView.findViewById(R.id.iv_color);
                llColor = itemView.findViewById(R.id.ll_color);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (nestedScrollView.getVisibility() == View.VISIBLE){
            super.onBackPressed();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }else{
//            llAddNotes.setVisibility(View.GONE);
//            nestedScrollView.setVisibility(View.VISIBLE);
            flagEdit=false;
            editPosition=-1;
            handleResult(-1);
        }
    }

}