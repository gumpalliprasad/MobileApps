package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Util.MyUtils;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.rv_categories)
    RecyclerView rvCategories;
    MyUtils utils = new MyUtils();

    int selectedItem=0;

    List<String> listCtegories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        init();
    }

    private void init() {

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listCtegories.add("All");
        listCtegories.add("Live");
        listCtegories.add("Assignments");
        listCtegories.add("K-Hub Courses");
        rvCategories.setLayoutManager(new LinearLayoutManager(SearchActivity.this,RecyclerView.HORIZONTAL,false));
        rvCategories.setAdapter(new CategoryAdapter());
    }


    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

        @NonNull
        @Override
        public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CategoryAdapter.ViewHolder(LayoutInflater.from(SearchActivity.this).inflate(R.layout.item_chapter_topic,parent,false));
        }


        @Override
        public long getItemId(int position) {
            return  position;
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            holder.tvChp.setText(listCtegories.get(position));

            if (position==selectedItem){
                holder.tvChp.setBackgroundResource(R.drawable.bg_grad_chap_select);
                holder.tvChp.setTextColor(Color.WHITE);
            }else {
                holder.tvChp.setBackgroundResource(R.drawable.bg_border_10);
                holder.tvChp.setTextColor(Color.BLACK);

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedItem = position;
                    notifyDataSetChanged();
                    rvCategories.getLayoutManager().scrollToPosition(position);
                }
            });

        }

        @Override
        public int getItemCount() {
            return listCtegories.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvChp;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvChp = itemView.findViewById(R.id.tv_chp);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}