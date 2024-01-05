package myschoolapp.com.gsnedutech.Util;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import myschoolapp.com.gsnedutech.R;


public class MultiSpinner extends androidx.appcompat.widget.AppCompatEditText {

    List<String> list;

    List<String> select = new ArrayList<>();

    Context context;

    public MultiSpinner(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }


    public void setList(List<String> list){
        this.list = list;
    }

    public String getSelection(){
        return MultiSpinner.this.getText().toString();
    }

    public void setSelection(String s){
        MultiSpinner.this.setText(s);
    }

    private void initView(Context context) {
        select.clear();
        this.setPadding(10,10,10,20);
        this.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_expand_more_black),null);
        this.setBackgroundResource(R.drawable.thin_underline);
        this.setFocusable(false);
        this.setHint("Select");
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = MultiSpinner.this.getText().toString();
                String[] selection = new String[s.length()];
                for (int i = 0;i<s.length();i++){
                    selection[i] = s.charAt(i)+"";
                }
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_multi_spinner);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                RecyclerView rv = dialog.findViewById(R.id.rv_options);
                rv.setLayoutManager(new LinearLayoutManager(context));
                rv.setAdapter(new OpAdapter(context,selection));

                dialog.findViewById(R.id.btn_select).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Collections.sort(select);
                        String str = "";
                        for (String s : select){
                            str = str+s;
                        }
                        MultiSpinner.this.setText(str);
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.btn_close).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        });
    }

    class OpAdapter extends RecyclerView.Adapter<OpAdapter.ViewHolder>{

        Context ctx;
        String[] selected;

        public OpAdapter(Context context, String[] selection) {
            ctx = context;
            selected = selection;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_option,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.cbOption.setText(list.get(position));
            for (int i=0;i<selected.length;i++){
               if (selected[i].equalsIgnoreCase(list.get(position))){
                   holder.cbOption.setChecked(true);
                   if (!select.contains(list.get(position))){
                       select.add(list.get(position));
                   }
               }
            }
            holder.cbOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        if (!select.contains(list.get(position))){
                            select.add(list.get(position));
                        }
                    }else {
                        if (select.contains(list.get(position))){
                            select.remove(list.get(position));
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox cbOption;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                cbOption = itemView.findViewById(R.id.cb_op);
            }
        }
    }


}
