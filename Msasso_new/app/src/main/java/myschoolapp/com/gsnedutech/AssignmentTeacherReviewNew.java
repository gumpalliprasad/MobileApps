package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetails;
import myschoolapp.com.gsnedutech.Models.StudentHWFile;
import myschoolapp.com.gsnedutech.Util.NewPdfViewer;
import myschoolapp.com.gsnedutech.Util.PdfWebViewer;
import myschoolapp.com.gsnedutech.Util.PlayerActivity;
import myschoolapp.com.gsnedutech.Util.YoutubeActivity;

public class AssignmentTeacherReviewNew extends AppCompatActivity {

    HomeWorkDetails hwObj;
    List<StudentHWFile> fileList = new ArrayList<>();
    
    @BindView(R.id.rv_student_submissions)
    RecyclerView rvStudentSubmissions;
    @BindView(R.id.rv_feed_back)
    RecyclerView rvFeedBack;
    @BindView(R.id.tv_feed_back)
    TextView tvFeedBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_teacher_review)
    LinearLayout llTeacherReview;

    List<String> listComments = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_teacher_review_new);

        ButterKnife.bind(this);
        
        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    void init() {
        hwObj = (HomeWorkDetails) getIntent().getSerializableExtra("hwObj");
        fileList.addAll((Collection<? extends StudentHWFile>) getIntent().getSerializableExtra("files"));

        tvTitle.setText(getIntent().getStringExtra("type")+"/"+hwObj.getSubjectName());

        switch (Integer.parseInt(hwObj.getHwRating())) {

            case 2:
                tvFeedBack.setText("Average");
                llTeacherReview.setBackgroundResource(R.drawable.bg_hw_feedback_poor);
                break;
            case 3:
                tvFeedBack.setText("Good");
                llTeacherReview.setBackgroundResource(R.drawable.bg_hw_feedback_good);
                break;
            case 4:
                tvFeedBack.setText("Excellent");
                llTeacherReview.setBackgroundResource(R.drawable.bg_hw_feedback_os);
                break;
            case 5:
                tvFeedBack.setText("OutStanding");
                llTeacherReview.setBackgroundResource(R.drawable.bg_hw_feedback_os);
                break;

            default:
                tvFeedBack.setText("Poor");
                llTeacherReview.setBackgroundResource(R.drawable.bg_hw_feedback_poor);
                break;
        }


        rvStudentSubmissions.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        rvStudentSubmissions.setAdapter(new SubmissionFilesAdapter());

//        for (int i=0;i<fileList.size();i++){
//            if (fileList.get(i).getTeacherFeedback()!=null && !fileList.get(i).getTeacherFeedback().equalsIgnoreCase("NA")){
//                listComments.add(fileList.get(i));
//            }
//        }


        if (hwObj.getTeacherComments()!=null && !hwObj.getTeacherComments().equalsIgnoreCase("NA")){
            listComments.add(hwObj.getTeacherComments());
        }

        if (listComments.size()>0) {
            rvFeedBack.setLayoutManager(new LinearLayoutManager(this));
            rvFeedBack.setAdapter(new FeedBackAdapter());
        }else{
            rvFeedBack.setVisibility(View.GONE);
            findViewById(R.id.tv_no_comments).setVisibility(View.VISIBLE);
        }
    }

    class SubmissionFilesAdapter extends RecyclerView.Adapter<SubmissionFilesAdapter.ViewHolder>{

        @NonNull
        @Override
        public SubmissionFilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SubmissionFilesAdapter.ViewHolder(LayoutInflater.from(AssignmentTeacherReviewNew.this).inflate(R.layout.item_hw_file_sub,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SubmissionFilesAdapter.ViewHolder holder, int position) {

//            holder.tvVersion.setText(fileList.get(position).getVersion()+"");
//            holder.itemView.findViewById(R.id.cv_version).setVisibility(View.VISIBLE);

            if (fileList.get(position).getEvaluated()!=0){
                holder.flIncorrect.setVisibility(View.VISIBLE);
            }else {
                holder.flIncorrect.setVisibility(View.GONE);
            }

            if (fileList.get(position).getStudentHWFilePath().contains(".pdf")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_pdf);
            }else if(fileList.get(position).getStudentHWFilePath().contains(".jpg") || fileList.get(position).getStudentHWFilePath().contains(".png") || fileList.get(position).getStudentHWFilePath().contains(".jpeg")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_jpg);
            }else if(fileList.get(position).getStudentHWFilePath().contains(".mp4")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_mp4);
            }else{
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_doc);
            }

            if (fileList.get(position).getStudentHWFilePath().contains(".pdf")) {
                holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_pdf);
            }else
            if (fileList.get(position).getStudentHWFilePath().contains(".mp4")) {
                                holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_mp4);
            }else
            if (fileList.get(position).getStudentHWFilePath().contains("youtube")) {
                                holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_mp4);
            }else
            if (fileList.get(position).getStudentHWFilePath().contains(".jpg") || fileList.get(position).getStudentHWFilePath().contains(".png")) {
                                holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_jpg);
            }else
            if (fileList.get(position).getStudentHWFilePath().contains(".doc") || fileList.get(position).getStudentHWFilePath().equalsIgnoreCase(".docx") || fileList.get(position).getStudentHWFilePath().equalsIgnoreCase(".ppt") || fileList.get(position).getStudentHWFilePath().equalsIgnoreCase(".pptx")) {
                                holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_others);
            }else {
                                holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_others);
            }

            holder.tvFileName.setText(fileList.get(position).getStudentHWFilePath().split("/")[fileList.get(position).getStudentHWFilePath().split("/").length-1]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fileList.get(position).getStudentHWFilePath().contains(".pdf")) {
                        //                        Intent intent = new Intent(StudentHWDetails.this, SomeAct.class);

                                        holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_pdf);

                        Intent intent = new Intent(AssignmentTeacherReviewNew.this, NewPdfViewer.class);
                        intent.putExtra("url", fileList.get(position).getStudentHWFilePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (fileList.get(position).getStudentHWFilePath().contains(".mp4")) {
                                        holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_mp4);
                        Intent intent = new Intent(AssignmentTeacherReviewNew.this, PlayerActivity.class);
                        intent.putExtra("url", fileList.get(position).getStudentHWFilePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (fileList.get(position).getStudentHWFilePath().contains("youtube")) {
                                        holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_mp4);
                        Intent intent = new Intent(AssignmentTeacherReviewNew.this, YoutubeActivity.class);

                        String s[] = fileList.get(position).getStudentHWFilePath().split("/");

                        intent.putExtra("videoItem", s[s.length - 1]);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (fileList.get(position).getStudentHWFilePath().contains(".jpg") || fileList.get(position).getStudentHWFilePath().contains(".png")) {
                                        holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_jpg);
                        Intent intent = new Intent(AssignmentTeacherReviewNew.this, AssignmentFileReview.class);
                        intent.putExtra("path", (Serializable)fileList.get(position));
                        intent.putExtra("title",tvTitle.getText().toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (fileList.get(position).getStudentHWFilePath().contains(".doc") || fileList.get(position).getStudentHWFilePath().equalsIgnoreCase(".docx") || fileList.get(position).getStudentHWFilePath().equalsIgnoreCase(".ppt") || fileList.get(position).getStudentHWFilePath().equalsIgnoreCase(".pptx")) {
                                        holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_others);
                        Intent intent = new Intent(AssignmentTeacherReviewNew.this, PdfWebViewer.class);
                        intent.putExtra("url", fileList.get(position).getStudentHWFilePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else{
                        Toast.makeText(AssignmentTeacherReviewNew.this,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return fileList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFileName,tvVersion;
            ImageView ivFile;
            FrameLayout flIncorrect;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
//                tvVersion = itemView.findViewById(R.id.tv_version);
                tvFileName = itemView.findViewById(R.id.tv_file_name);
                ivFile = itemView.findViewById(R.id.iv_file);
                flIncorrect = itemView.findViewById(R.id.fl_incorrect);
            }
        }
    }

    class FeedBackAdapter extends RecyclerView.Adapter<FeedBackAdapter.ViewHolder>{

        @NonNull
        @Override
        public FeedBackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(AssignmentTeacherReviewNew.this).inflate(R.layout.item_file_comments,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull FeedBackAdapter.ViewHolder holder, int position) {
            holder.tvFeedBack.setText(listComments.get(position));
            holder.tvComment.setText("Comment "+(position+1));


//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (fileList.get(position).getTeacherEvaluatePath().equalsIgnoreCase("NA")) {
//                        Toast.makeText(AssignmentTeacherReviewNew.this, "No files uploaded by teacher", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Intent intent = new Intent(AssignmentTeacherReviewNew.this, ImageDisp.class);
//                        intent.putExtra("path", fileList.get(position).getTeacherEvaluatePath().replaceAll(" ", "%20"));
//                        startActivity(intent);
//                    }
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return listComments.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvComment,tvFeedBack;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvComment = itemView.findViewById(R.id.tv_comment);
                tvFeedBack = itemView.findViewById(R.id.tv_teacher_fb);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}