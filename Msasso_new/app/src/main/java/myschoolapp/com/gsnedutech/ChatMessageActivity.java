package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatMessageActivity extends AppCompatActivity {

    @BindView(R.id.rv_messages)
    RecyclerView rvMessages;

    boolean toggle = false;



    ChatAdapter adapter;

    int c=100;
    MyUtils utils = new MyUtils();
    ImageView emojiButton;
    EmojiconEditText emojiconEditText;
    EmojIconActions  emojIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        ButterKnife.bind(this);

        init();
    }

    private void init() {

//        EmojIconActions emojIcon= new EmojIconActions(this, rootView, emojiconEditText,
//                emojiImageView); 
//
//
        LinearLayout rootView = findViewById(R.id.rootview);
        emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        emojiButton = findViewById(R.id.emoji_btn);
        emojIcon = new EmojIconActions(this, rootView, emojiconEditText, emojiButton);
        emojIcon.ShowEmojIcon();



        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
//                Log.e("Keyboard", "open");

            }

            @Override
            public void onKeyboardClose() {
//                Log.e("Keyboard", "close");

            }
        });
        emojIcon.addEmojiconEditTextList(emojiconEditText);
        emojIcon.setUseSystemEmoji(true);
//        emojiconEditText.setUseSystemEmoji(true);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        rvMessages.setLayoutManager(manager);
        adapter = new ChatAdapter();
        rvMessages.setAdapter(adapter);

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emojiconEditText.getText().toString().length()>0){
                    c++;
                    emojiconEditText.setText("");
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           adapter.notifyDataSetChanged();
                           rvMessages.getLayoutManager().scrollToPosition((c-1));
                       }
                   });
                }
            }
        });
    }


    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

        @Override
        public int getItemViewType(int position) {
           if (position%2==0){
               return 0;
           }else {
               return 1;
           }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType==0){
                return new ViewHolder(LayoutInflater.from(ChatMessageActivity.this).inflate(R.layout.item_message_send,parent,false));
            }else {
                return new ViewHolder(LayoutInflater.from(ChatMessageActivity.this).inflate(R.layout.item_message_recieve,parent,false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            if (position%2==0){
                holder.tvMessage.setText("Good Morning ma’am, I have a doubt in Chapter. Who hound of baskerville");
            }else{
                holder.tvMessage.setText("Good Morning Ashish, it’s from plus or minus infinity");
            }
        }

        @Override
        public int getItemCount() {
            return c;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvMessage;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvMessage = itemView.findViewById(R.id.tv_message);
            }
        }
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public  void showKeyboard(){
        InputMethodManager inputMethodManager =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(emojiconEditText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        emojiconEditText.requestFocus();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}