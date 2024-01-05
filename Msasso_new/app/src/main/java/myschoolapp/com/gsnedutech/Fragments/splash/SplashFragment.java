package myschoolapp.com.gsnedutech.Fragments.splash;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.UserSelection;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashFragment extends Fragment {
    SharedPreferences shPref;
    SharedPreferences.Editor toEdit;
    MyUtils myUtils = new MyUtils();

    EditText etCollege;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.college_code, container, false);
        init(viewFragment);
        return viewFragment;
    }

    public void init(View viewFragment){
        shPref = requireActivity().getSharedPreferences(
                AppUrls.shCredentials,
                Context.MODE_PRIVATE
        );
        toEdit = shPref.edit();
        TextView btNext = viewFragment.findViewById(R.id.btn_college_next);
        etCollege = viewFragment.findViewById(R.id.et_college);
        btNext.setOnClickListener(v -> {
            if (etCollege != null && etCollege.getText().toString().length() > 5) {
                getCollegeCode(etCollege.getText().toString());
            } else {
                alertDialog("Please enter valid College Code");
            }
        });
    }

    private void getCollegeCode(String collegeCode) {
        myUtils.showLoader(requireContext());
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        Request get = new Request.Builder()
                .url(AppUrls.INST_COLLEGE_CODE + "" + collegeCode)
                .build();

        myUtils.showLog("TAG", "getCollegeCode request - " + AppUrls.INST_COLLEGE_CODE + "" + collegeCode);
        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    myUtils.dismissDialog();
                    alertDialog("Please enter valid College Code");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                requireActivity().runOnUiThread(() -> {
                    myUtils.dismissDialog();
                });
                try {
                    if (response.body() != null) {
                        try {
                            String jsonResponse = response.body().string();
                            JSONObject parentObject = new JSONObject(jsonResponse);
                            if (parentObject.getString("StatusCode")
                                    .equals("200")
                            ) {
                                if(parentObject.has("schemaName"))
                                toEdit.putString(AppConst.SCHEMA,
                                        parentObject.getString("schemaName")
                                );

                                if(parentObject.has("logoImage"))
                                toEdit.putString(
                                        AppConst.COLLEGE_LOGO,
                                        parentObject.getString("logoImage")
                                );

                                if(parentObject.has("logoImage"))
                                toEdit.putString(
                                        AppConst.BACKGROUND_COLLEGE_LOGO,
                                        parentObject.getString(
                                                "logoImage"
                                        )
                                );
                                toEdit.commit();
                                requireActivity().runOnUiThread (() -> {
                                    Intent i =
                                            new Intent(requireActivity(), UserSelection.class);
                                    requireActivity().startActivity(i);
                                    requireActivity().overridePendingTransition(
                                            R.anim.trans_left_in,
                                            R.anim.trans_left_out
                                    );
                                });
                            } else {
                                requireActivity().runOnUiThread (() -> {
                                    alertDialog("Please enter valid College Code");
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        requireActivity().runOnUiThread (() -> {
                            alertDialog("Please enter valid College Code");
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void alertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(message)
                .setTitle(requireActivity().getResources().getString(R.string.app_name))
                .setCancelable(false)
                .setPositiveButton("OK",
                        (dialog, id) -> {
                            dialog.dismiss();
                        });
        //.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        etCollege.setText("");
    }
}
