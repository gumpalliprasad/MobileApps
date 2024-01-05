package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Util.MyUtils;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.rv_chats)
    RecyclerView rvChats;

    MyUtils utils = new MyUtils();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
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

        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvChats.setLayoutManager(manager);
        rvChats.setAdapter(new ChatAdapter());
        rvChats.addItemDecoration(new DividerItemDecoration(rvChats.getContext(), DividerItemDecoration.VERTICAL));
    }


    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

        @NonNull
        @Override
        public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ChatActivity.this).inflate(R.layout.item_chat,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {

            if (position==0){
                holder.vOnline.setVisibility(View.GONE);
                holder.tvDate.setVisibility(View.VISIBLE);
                holder.tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ChatActivity.this, ChatMessageActivity.class));
                    ChatActivity.this.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 5;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate;
            View vOnline;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tv_date);
                vOnline = itemView.findViewById(R.id.view_online);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}