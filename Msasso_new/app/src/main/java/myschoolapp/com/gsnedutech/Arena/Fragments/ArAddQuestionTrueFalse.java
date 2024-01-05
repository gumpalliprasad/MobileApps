package myschoolapp.com.gsnedutech.Arena.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Arena.ArAddQuizQuestion;
import myschoolapp.com.gsnedutech.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ArAddQuestionTrueFalse extends Fragment {

    View viewArAddQuestionTrueFalse;
    Unbinder unbinder;

    @BindView(R.id.rv_q_num)
    RecyclerView rVQNum;

    @BindView(R.id.iv_q_image)
    ImageView ivQImage;

    @BindView(R.id.tv_q_image)
    TextView tvQImage;

    @BindView(R.id.et_ques)
    EditText etQues;

    Activity mActivity;

    int currentPos = 0;

    public ArAddQuestionTrueFalse() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewArAddQuestionTrueFalse = inflater.inflate(R.layout.fragment_ar_add_question_true_false, container, false);
        unbinder = ButterKnife.bind(this,viewArAddQuestionTrueFalse);

        init();
        
        return viewArAddQuestionTrueFalse;
    }
    void init(){

        ((ArAddQuizQuestion)mActivity).tvTitle.setText(((ArAddQuizQuestion)mActivity).quizObject.getQuizTitle()+"-"+((ArAddQuizQuestion)mActivity).quizObject.getQuestionType());

        int numQues = ((ArAddQuizQuestion)mActivity).quizObject.getNumberOfQuestions();

      

        rVQNum.setLayoutManager(new LinearLayoutManager(mActivity,RecyclerView.HORIZONTAL,false));
        rVQNum.setAdapter(new QuestionNumberAdapter(numQues));


        ivQImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,2);
            }
        });

        viewArAddQuestionTrueFalse.findViewById(R.id.tv_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (++currentPos<numQues){
                    if (currentPos==(numQues-1)){
                        ((TextView)viewArAddQuestionTrueFalse.findViewById(R.id.tv_next)).setText("Preview");
                    }
                    rVQNum.getAdapter().notifyDataSetChanged();
                    rVQNum.getLayoutManager().scrollToPosition(currentPos);

                    ivQImage.setImageResource(android.R.drawable.ic_menu_gallery);
                    tvQImage.setVisibility(View.VISIBLE);
                    etQues.setText("");

                } else{
                    ((ArAddQuizQuestion)mActivity).stepNo = 4;
                    ((ArAddQuizQuestion)mActivity).init();
                }
            }
        });
    }

    class QuestionNumberAdapter extends RecyclerView.Adapter<QuestionNumberAdapter.ViewHolder>{

        int numberOfQuestions;

        public QuestionNumberAdapter(int numberOfQuestions) {
            this.numberOfQuestions = numberOfQuestions;
        }

        @NonNull
        @Override
        public QuestionNumberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new QuestionNumberAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_question_number,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionNumberAdapter.ViewHolder holder, int position) {
            holder.tvQNum.setText((position+1)+"");

            if (position == currentPos){
                holder.tvQNum.setTypeface(Typeface.create(holder.tvQNum.getTypeface(), Typeface.BOLD));
                holder.tvQNum.setTextColor(Color.BLACK);
            }else {
                holder.tvQNum.setTypeface(Typeface.create(holder.tvQNum.getTypeface(), Typeface.NORMAL));
                holder.tvQNum.setTextColor(Color.parseColor("#40000000"));
            }
        }

        @Override
        public int getItemCount() {
            return numberOfQuestions;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvQNum;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvQNum = itemView.findViewById(R.id.tv_queNo);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){
            if (requestCode==1) {
                Uri uri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ivQImage.setImageBitmap(bitmap);
                tvQImage.setVisibility(View.GONE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}