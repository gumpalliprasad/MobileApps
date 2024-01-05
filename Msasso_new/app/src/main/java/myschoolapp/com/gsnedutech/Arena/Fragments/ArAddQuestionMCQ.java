package myschoolapp.com.gsnedutech.Arena.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Arena.ArAddQuizQuestion;
import myschoolapp.com.gsnedutech.Arena.Models.ArQuizMCQ;
import myschoolapp.com.gsnedutech.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ArAddQuestionMCQ extends Fragment implements View.OnClickListener {

   View viewArAddQuestionMCQ;
   Unbinder unbinder;

   @BindView(R.id.rv_options)
    RecyclerView rvOptions;

   @BindView(R.id.rv_q_num)
    RecyclerView rVQNum;

   @BindView(R.id.iv_q_image)
    ImageView ivQImage;

   @BindView(R.id.tv_q_image)
    TextView tvQImage;

   @BindView(R.id.et_ques)
    EditText etQues;

   Activity mActivity;

   List<ArQuizMCQ> listQuestions = new ArrayList<>();

   int currentPos = 0;

    OptionsAdapter.ViewHolder currentHolder = null;

    int optionPos = 0;

    public ArAddQuestionMCQ() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewArAddQuestionMCQ = inflater.inflate(R.layout.fragment_ar_add_question_m_c_q, container, false);
        unbinder = ButterKnife.bind(this,viewArAddQuestionMCQ);

        init();

        return  viewArAddQuestionMCQ;
    }

    void init(){

        ((ArAddQuizQuestion)mActivity).tvTitle.setText(((ArAddQuizQuestion)mActivity).quizObject.getQuizTitle()+"-"+((ArAddQuizQuestion)mActivity).quizObject.getQuestionType());

        int numQues = ((ArAddQuizQuestion)mActivity).quizObject.getNumberOfQuestions();
        for (int i=0;i<numQues;i++){
            listQuestions.add(new ArQuizMCQ());
        }


        rVQNum.setLayoutManager(new LinearLayoutManager(mActivity,RecyclerView.HORIZONTAL,false));
        rVQNum.setAdapter(new QuestionNumberAdapter(numQues));

        rvOptions.setLayoutManager(new GridLayoutManager(mActivity,2));
        rvOptions.setAdapter(new OptionsAdapter());


        ivQImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,2);
            }
        });

        viewArAddQuestionMCQ.findViewById(R.id.tv_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (++currentPos<numQues){
                    if (currentPos==(numQues-1)){
                        ((TextView)viewArAddQuestionMCQ.findViewById(R.id.tv_next)).setText("Preview");
                    }else {
                        unSelectAll();
                    }
                    rVQNum.getAdapter().notifyDataSetChanged();
                    rVQNum.getLayoutManager().scrollToPosition(currentPos);

                    rvOptions.setLayoutManager(new GridLayoutManager(mActivity,2));
                    rvOptions.setAdapter(new OptionsAdapter());
                    ivQImage.setImageResource(android.R.drawable.ic_menu_gallery);
                    tvQImage.setVisibility(View.VISIBLE);
                    etQues.setText("");

                } else{
                    ((ArAddQuizQuestion)mActivity).stepNo = 4;
                    ((ArAddQuizQuestion)mActivity).quizObject.setListMCQ(listQuestions);
                    ((ArAddQuizQuestion)mActivity).init();
                }
            }
        });

        viewArAddQuestionMCQ.findViewById(R.id.cv_a).setOnClickListener(this);
        viewArAddQuestionMCQ.findViewById(R.id.cv_b).setOnClickListener(this);
        viewArAddQuestionMCQ.findViewById(R.id.cv_c).setOnClickListener(this);
        viewArAddQuestionMCQ.findViewById(R.id.cv_d).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        unSelectAll();
        switch (view.getId()){

            case R.id.cv_a:
                View v = rvOptions.getLayoutManager().findViewByPosition(0);
                v.findViewById(R.id.view_correct).setVisibility(View.VISIBLE);
                ((TextView)viewArAddQuestionMCQ.findViewById(R.id.tv_a)).setBackgroundColor(Color.parseColor("#1FA655"));
                break;
            case R.id.cv_b:
                View v1 = rvOptions.getLayoutManager().findViewByPosition(1);
                v1.findViewById(R.id.view_correct).setVisibility(View.VISIBLE);
                ((TextView)viewArAddQuestionMCQ.findViewById(R.id.tv_b)).setBackgroundColor(Color.parseColor("#1FA655"));
                break;
            case R.id.cv_c:
                View v2 = rvOptions.getLayoutManager().findViewByPosition(2);
                v2.findViewById(R.id.view_correct).setVisibility(View.VISIBLE);
                ((TextView)viewArAddQuestionMCQ.findViewById(R.id.tv_c)).setBackgroundColor(Color.parseColor("#1FA655"));
                break;
            case R.id.cv_d:
                View v3 = rvOptions.getLayoutManager().findViewByPosition(3);
                v3.findViewById(R.id.view_correct).setVisibility(View.VISIBLE);
                ((TextView)viewArAddQuestionMCQ.findViewById(R.id.tv_d)).setBackgroundColor(Color.parseColor("#1FA655"));
                break;

        }

    }

    private void unSelectAll() {
        View v = rvOptions.getLayoutManager().findViewByPosition(0);
        v.findViewById(R.id.view_correct).setVisibility(View.GONE);
        View v1 = rvOptions.getLayoutManager().findViewByPosition(1);
        v1.findViewById(R.id.view_correct).setVisibility(View.GONE);
        View v2 = rvOptions.getLayoutManager().findViewByPosition(2);
        v2.findViewById(R.id.view_correct).setVisibility(View.GONE);
        View v3 = rvOptions.getLayoutManager().findViewByPosition(3);
        v3.findViewById(R.id.view_correct).setVisibility(View.GONE);
        ((TextView)viewArAddQuestionMCQ.findViewById(R.id.tv_a)).setBackgroundColor(Color.parseColor("#ffffff"));
        ((TextView)viewArAddQuestionMCQ.findViewById(R.id.tv_b)).setBackgroundColor(Color.parseColor("#ffffff"));
        ((TextView)viewArAddQuestionMCQ.findViewById(R.id.tv_c)).setBackgroundColor(Color.parseColor("#ffffff"));
        ((TextView)viewArAddQuestionMCQ.findViewById(R.id.tv_d)).setBackgroundColor(Color.parseColor("#ffffff"));

    }

    class QuestionNumberAdapter extends RecyclerView.Adapter<QuestionNumberAdapter.ViewHolder>{

        int numberOfQuestions;

        public QuestionNumberAdapter(int numberOfQuestions) {
            this.numberOfQuestions = numberOfQuestions;
        }

        @NonNull
        @Override
        public QuestionNumberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_question_number,parent,false));
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

    class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder>{


        @NonNull
        @Override
        public OptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.layout_cv_arena_quiz,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull OptionsAdapter.ViewHolder holder, int position) {
            switch (position){
                case 0:
                    holder.tvOption.setText("A");
                    holder.tvOption.setText(listQuestions.get(currentPos).getOptionA());
                    if (listQuestions.get(currentPos).getOptionAImage()!=null){
                        holder.ivOption.setVisibility(View.VISIBLE);
                        holder.tvSelectImage.setVisibility(View.GONE);
                        holder.ivOption.setImageBitmap(BitmapFactory.decodeFile(listQuestions.get(currentPos).getOptionAImage().getPath()));
                    }else {
                        holder.ivOption.setVisibility(View.GONE);
                        holder.tvSelectImage.setVisibility(View.VISIBLE);
                    }
                    break;
                case 1:
                    holder.tvOption.setText("B");
                    holder.tvOption.setText(listQuestions.get(currentPos).getOptionB());
                    if (listQuestions.get(currentPos).getOptionBImage()!=null){
                        holder.ivOption.setVisibility(View.VISIBLE);
                        holder.tvSelectImage.setVisibility(View.GONE);
                        holder.ivOption.setImageBitmap(BitmapFactory.decodeFile(listQuestions.get(currentPos).getOptionBImage().getPath()));
                    }else {
                        holder.ivOption.setVisibility(View.GONE);
                        holder.tvSelectImage.setVisibility(View.VISIBLE);
                    }
                    break;
                case 2:
                    holder.tvOption.setText("C");
                    holder.tvOption.setText(listQuestions.get(currentPos).getOptionC());
                    if (listQuestions.get(currentPos).getOptionCImage()!=null){
                        holder.ivOption.setVisibility(View.VISIBLE);
                        holder.tvSelectImage.setVisibility(View.GONE);
                        holder.ivOption.setImageBitmap(BitmapFactory.decodeFile(listQuestions.get(currentPos).getOptionCImage().getPath()));
                    }else {
                        holder.ivOption.setVisibility(View.GONE);
                        holder.tvSelectImage.setVisibility(View.VISIBLE);
                    }
                    break;
                case 3:
                    holder.tvOption.setText("D");
                    holder.tvOption.setText(listQuestions.get(currentPos).getOptionD());
                    if (listQuestions.get(currentPos).getOptionDImage()!=null){
                        holder.ivOption.setVisibility(View.VISIBLE);
                        holder.tvSelectImage.setVisibility(View.GONE);
                        holder.ivOption.setImageBitmap(BitmapFactory.decodeFile(listQuestions.get(currentPos).getOptionDImage().getPath()));
                    }else {
                        holder.ivOption.setVisibility(View.GONE);
                        holder.tvSelectImage.setVisibility(View.VISIBLE);
                    }
                    break;
            }



            holder.etOption.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    switch (position){
                        case 0:
                            listQuestions.get(currentPos).setOptionA(editable.toString());
                            break;
                        case 1:
                            listQuestions.get(currentPos).setOptionB(editable.toString());
                            break;
                        case 2:
                            listQuestions.get(currentPos).setOptionC(editable.toString());
                            break;
                        case 3:
                            listQuestions.get(currentPos).setOptionD(editable.toString());
                            break;
                    }


                }
            });

            holder.tvSelectImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentHolder = holder;
                    optionPos = position;
                    Intent intent_upload = new Intent();
                    intent_upload.setType("image/*");
                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent_upload,1);
                }
            });

        }

        @Override
        public int getItemCount() {
            return 4;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvOption,tvSelectImage;
            EditText etOption;
            ImageView ivOption;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvOption = itemView.findViewById(R.id.tv_option_name);
                tvSelectImage = itemView.findViewById(R.id.tv_select_image);
                etOption = itemView.findViewById(R.id.et_option_text);
                ivOption = itemView.findViewById(R.id.iv_selected_image);

            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){
            if (requestCode==1){

                Uri uri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentHolder.ivOption.setImageBitmap(bitmap);
                currentHolder.ivOption.setVisibility(View.VISIBLE);
                currentHolder.tvSelectImage.setVisibility(View.GONE);
//                switch (optionPos){
//                    case 0:
//                        listQuestions.get(currentPos).setOptionAImage(new File(uri.toString()));
//                        break;
//                    case 1:
//                        listQuestions.get(currentPos).setOptionBImage(new File(uri.toString()));
//                        break;
//                    case 2:
//                        listQuestions.get(currentPos).setOptionCImage(new File(uri.toString()));
//                        break;
//                    case 3:
//                        listQuestions.get(currentPos).setOptionDImage(new File(uri.toString()));
//                        break;
//                }
            }else{

                Uri uri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ivQImage.setImageBitmap(bitmap);
                tvQImage.setVisibility(View.GONE);

                listQuestions.get(currentPos).setQuesImage(uri);
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