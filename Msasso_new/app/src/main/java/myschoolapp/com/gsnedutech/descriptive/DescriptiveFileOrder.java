package myschoolapp.com.gsnedutech.descriptive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ImageDisp;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.descriptive.models.DescCategory;
import myschoolapp.com.gsnedutech.descriptive.models.StudentAnswer;

public class DescriptiveFileOrder extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = DescriptiveFileOrder.class.getName();

    DescCategory descQuestionObj;

    MyUtils utils = new MyUtils();

    @BindView(R.id.tv_count)
    TextView tvCount;

    @BindView(R.id.etFileOrder)
    EditText etFileOrder;

    @BindView(R.id.iv_file)
    ImageView ivFile;

    @BindView(R.id.ll_prev)
    LinearLayout llPrev;

    @BindView(R.id.ll_next)
    LinearLayout llNext;

    @BindView(R.id.tv_next)
    TextView tvNext;

    @BindView(R.id.rv_file_order)
    RecyclerView rvFileOrder;

    int qNum = 0;

    List<StudentAnswer> flipObjArrayList = new ArrayList<>();
    RecyclerViewAdapter recyclerViewAdapter;


    String contentType = "", moduleContentId = "", moduleId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.descr_file_order_deatils);
        ButterKnife.bind(this);

        init();

        Snackbar snackbar = Snackbar
                .make(rvFileOrder, "Long Press to drag files to ReOrder", Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snackbar.isShown())
                    snackbar.dismiss();
            }
        });


        snackbar.show();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void init() {

        descQuestionObj = (DescCategory) getIntent().getSerializableExtra("resultObj");
        flipObjArrayList = descQuestionObj.getStudentAnswer();
        rvFileOrder.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), flipObjArrayList);
        rvFileOrder.setAdapter(recyclerViewAdapter);
        ItemTouchHelper.Callback callback = new ItemMoveCallback(recyclerViewAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvFileOrder);
        loadText(qNum);
        findViewById(R.id.tv_submit).setOnClickListener(v -> {
            setFilesInOrder();
        });


        llPrev.setOnClickListener(this);
        llNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_prev:
                if (--qNum<0){
                    qNum = 0;
                }
                if (etFileOrder.getText().toString().length()>0){
                    flipObjArrayList.get(qNum).setFileOrder(Integer.parseInt(etFileOrder.getText().toString()));
                }
                loadText(qNum);
                break;
            case R.id.ll_next:
                if (etFileOrder.getText().toString().length()>0){
                    flipObjArrayList.get(qNum).setFileOrder(Integer.parseInt(etFileOrder.getText().toString()));
                }
                if (!tvNext.getText().toString().equals("Finish")) {
                    if (++qNum >= flipObjArrayList.size()) {
                        qNum = flipObjArrayList.size() - 1;
                    }
                    loadText(qNum);
                }
                else {
                    setFilesInOrder();
                    onBackPressed();
                }
                break;
        }
    }

    private void setFilesInOrder() {
        for ( int i = 0; i<flipObjArrayList.size();i++){
            flipObjArrayList.get(i).setFileOrder(i+1);
        }
        descQuestionObj.setStudentAnswer(flipObjArrayList);
        Intent intent = new Intent(DescriptiveFileOrder.this, DescriptiveQueSubmit.class);
        intent.putExtra("resultObj", descQuestionObj);
        setResult(4321, intent);
        finish();
    }

    private void loadText(int i) {

        llPrev.setVisibility(View.VISIBLE);
        llNext.setVisibility(View.VISIBLE);
        tvNext.setText("Next Page");

        if(i == 0)
        {
            llPrev.setVisibility(View.INVISIBLE);
        }
        if (i== flipObjArrayList.size()-1){
            tvNext.setText("Finish");
        }
        tvCount.setText(""+(i+1)+"/"+flipObjArrayList.size());
        etFileOrder.setText(""+flipObjArrayList.get(i).getFileOrder());
        if (flipObjArrayList.get(i).getPath() != null && !flipObjArrayList.get(i).getPath().isEmpty()&&
                !flipObjArrayList.get(i).getPath().equalsIgnoreCase("")) {
            ivFile.setVisibility(View.VISIBLE);
            Picasso.with(DescriptiveFileOrder.this).load(flipObjArrayList.get(i).getPath()).placeholder(R.drawable.user_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivFile);
        }else{
            ivFile.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.dismissDialog();
    }


    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements ItemMoveCallback.ItemTouchHelperAdapter {

        private Context context;

        public RecyclerViewAdapter(Context context, List<StudentAnswer> arrayList) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_desc_files, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public  void onBindViewHolder(ViewHolder holder, int position) {
            Picasso.with(context).load(flipObjArrayList.get(position).getPath()).into(holder.myText);
            holder.tvFile_name.setOnClickListener(v -> {
                Intent intent = new Intent(DescriptiveFileOrder.this, ImageDisp.class);
                intent.putExtra("path", flipObjArrayList.get(position).getPath());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return flipObjArrayList.size();
        }

        @Override
        public void onRowMoved(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for  (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(flipObjArrayList, i, i + 1);
                }
            } else {
                for  (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(flipObjArrayList, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition,toPosition);
        }

        @Override
        public void onRowSelected(myschoolapp.com.gsnedutech.descriptive.RecyclerViewAdapter.ViewHolder myViewHolder) {

        }

        @Override
        public void onRowClear(myschoolapp.com.gsnedutech.descriptive.RecyclerViewAdapter.ViewHolder myViewHolder) {

        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView myText;
            TextView tvFile_name;
            public ViewHolder(View itemView) {
                super(itemView);
                myText = itemView.findViewById(R.id.iv_file);
                tvFile_name = itemView.findViewById(R.id.tv_file_name);

            }
        }
    }
}