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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetails;
import myschoolapp.com.gsnedutech.Models.StudentHWFile;
import myschoolapp.com.gsnedutech.Util.ImageDisp;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NewPdfViewer;
import myschoolapp.com.gsnedutech.Util.PdfWebViewer;
import myschoolapp.com.gsnedutech.Util.PlayerActivity;
import myschoolapp.com.gsnedutech.Util.YoutubeActivity;

public class AssignmentTeacherReview extends AppCompatActivity {

    @BindView(R.id.rv_submission_files)
    RecyclerView rvSubmissionFiles;
    @BindView(R.id.rv_feed_back)
    RecyclerView rvFeedBack;

    @BindView(R.id.tv_remark)
    TextView tvRemark;
    @BindView(R.id.view_percent_one)
    View viewPercentOne;
    @BindView(R.id.view_percent_two)
    View viewPercentTwo;
    MyUtils utils = new MyUtils();

    HomeWorkDetails hwObj;
    
    List<StudentHWFile> fileList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_teacher_review);
        ButterKnife.bind(this);

        init();
    }

    void init(){
        hwObj = (HomeWorkDetails) getIntent().getSerializableExtra("hwObj");
        fileList.addAll((Collection<? extends StudentHWFile>) getIntent().getSerializableExtra("files"));
        rvSubmissionFiles.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        rvSubmissionFiles.setAdapter(new SubmissionFilesAdapter());
        rvFeedBack.setLayoutManager(new LinearLayoutManager(this));
        rvFeedBack.setAdapter(new FeedBackAdapter());
        tvRemark.setText(hwObj.getTeacherComments());

        float marks = (Float.parseFloat(hwObj.getHwRating())*2)/10;
        float marksTwo = 1-marks;

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                0,
                1,
                marks
        );
        viewPercentOne.setLayoutParams(param);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                1,
                marksTwo
        );
        viewPercentTwo.setLayoutParams(params);

    }

    class SubmissionFilesAdapter extends RecyclerView.Adapter<SubmissionFilesAdapter.ViewHolder>{

        @NonNull
        @Override
        public SubmissionFilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SubmissionFilesAdapter.ViewHolder(LayoutInflater.from(AssignmentTeacherReview.this).inflate(R.layout.item_submission_assign_file,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SubmissionFilesAdapter.ViewHolder holder, int position) {

            holder.tvVersion.setText(fileList.get(position).getVersion()+"");
            holder.itemView.findViewById(R.id.cv_version).setVisibility(View.VISIBLE);
            holder.tvFileName.setText(fileList.get(position).getStudentHWFilePath().split("/")[fileList.get(position).getStudentHWFilePath().split("/").length-1]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fileList.get(position).getStudentHWFilePath().contains("pdf")) {
                        //                        Intent intent = new Intent(StudentHWDetails.this, SomeAct.class);
                        Intent intent = new Intent(AssignmentTeacherReview.this, NewPdfViewer.class);
                        intent.putExtra("url", fileList.get(position).getStudentHWFilePath());
                        startActivity(intent);
                    }else
                    if (fileList.get(position).getStudentHWFilePath().contains("mp4")) {
                        Intent intent = new Intent(AssignmentTeacherReview.this, PlayerActivity.class);
                        intent.putExtra("url", fileList.get(position).getStudentHWFilePath());
                        startActivity(intent);
                    }else
                    if (fileList.get(position).getStudentHWFilePath().contains("youtube")) {
                        Intent intent = new Intent(AssignmentTeacherReview.this, YoutubeActivity.class);

                        String s[] = fileList.get(position).getStudentHWFilePath().split("/");

                        intent.putExtra("videoItem", s[s.length - 1]);
                        startActivity(intent);
                    }else
                    if (fileList.get(position).getStudentHWFilePath().contains("jpg") || fileList.get(position).getStudentHWFilePath().contains("png")) {
                        Intent intent = new Intent(AssignmentTeacherReview.this, ImageDisp.class);
                        intent.putExtra("path", fileList.get(position).getStudentHWFilePath());
                        startActivity(intent);
                    }else
                    if (fileList.get(position).getStudentHWFilePath().contains("doc") || fileList.get(position).getStudentHWFilePath().equalsIgnoreCase("docx") || fileList.get(position).getStudentHWFilePath().equalsIgnoreCase("ppt") || fileList.get(position).getStudentHWFilePath().equalsIgnoreCase("pptx")) {
                        Intent intent = new Intent(AssignmentTeacherReview.this, PdfWebViewer.class);
                        intent.putExtra("url", fileList.get(position).getStudentHWFilePath());
                        startActivity(intent);
                    }else{
                        Toast.makeText(AssignmentTeacherReview.this,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
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

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvVersion = itemView.findViewById(R.id.tv_version);
                tvFileName = itemView.findViewById(R.id.tv_file_name);
            }
        }
    }

    class FeedBackAdapter extends RecyclerView.Adapter<FeedBackAdapter.ViewHolder>{

        @NonNull
        @Override
        public FeedBackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(AssignmentTeacherReview.this).inflate(R.layout.item_file_feedback, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull FeedBackAdapter.ViewHolder holder, int position) {
            holder.tvFileNameFeed.setText(fileList.get(position).getStudentHWFilePath().split("/")[(fileList.get(position).getStudentHWFilePath().split("/").length) - 1]);
            if (fileList.get(position).getTeacherFeedback().equalsIgnoreCase("NA")) {
                holder.tvFeedBack.setText("No feedback as of yet.");
            } else {
                holder.tvFeedBack.setText(fileList.get(position).getTeacherFeedback());
            }
            try {
                String dateTime = new SimpleDateFormat("dd MMM yyyy  hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fileList.get(position).getCreatedDate()));
                holder.tvDate.setText(dateTime.split("  ")[0]);
                holder.tvTime.setText(dateTime.split("  ")[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fileList.get(position).getTeacherEvaluatePath().equalsIgnoreCase("NA")) {
                        Toast.makeText(AssignmentTeacherReview.this, "No files uploaded by teacher", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(AssignmentTeacherReview.this, ImageDisp.class);
                        intent.putExtra("path", fileList.get(position).getTeacherEvaluatePath().replaceAll(" ", "%20"));
                        startActivity(intent);
                    }
                }
            });

            if (fileList.get(position).getEvaluated() != 0) {
                holder.tvVersion.setVisibility(View.VISIBLE);
                holder.tvVersion.setText("" + fileList.get(position).getVersion());
            } else {
                holder.tvVersion.setVisibility(View.GONE);

            }
        }

        @Override
        public int getItemCount() {
            return fileList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvFileNameFeed;
            TextView tvFeedBack;
            TextView tvDate;
            TextView tvTime;
            TextView tvVersion;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFileNameFeed = itemView.findViewById(R.id.tv_fileName);
                tvFeedBack = itemView.findViewById(R.id.tv_teacher_fb);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvTime = itemView.findViewById(R.id.tv_time);
                tvVersion = itemView.findViewById(R.id.tv_version);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}