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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentHWFile;
import myschoolapp.com.gsnedutech.Util.ImageDisp;
import uk.co.senab.photoview.PhotoViewAttacher;

public class AssignmentFileReview extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_comments)
    RecyclerView rvComments;


    StudentHWFile file;
    
    List<StudentHWFile> listFile = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_file_review);
        ButterKnife.bind(this);
        
        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    
    void init(){
        
        file = (StudentHWFile) getIntent().getSerializableExtra("path");

        listFile.add(file);


        tvTitle.setText(getIntent().getStringExtra("title"));
        
        final ImageView iv = findViewById(R.id.iv);
        String url = "";
        Picasso.with(AssignmentFileReview.this).load(file.getStudentHWFilePath()).placeholder(R.drawable.progress_animation)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(iv, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                PhotoViewAttacher photoAttacher;
                photoAttacher= new PhotoViewAttacher(iv);
                photoAttacher.update();
            }

            @Override
            public void onError() {
                Toast.makeText(AssignmentFileReview.this,"Oops there was a problem!",Toast.LENGTH_SHORT).show();
            }
        });

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(new FeedBackAdapter());
    }
    class FeedBackAdapter extends RecyclerView.Adapter<FeedBackAdapter.ViewHolder>{

        @NonNull
        @Override
        public FeedBackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FeedBackAdapter.ViewHolder(LayoutInflater.from(AssignmentFileReview.this).inflate(R.layout.item_file_comments,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull FeedBackAdapter.ViewHolder holder, int position) {
            holder.tvFeedBack.setText(listFile.get(position).getTeacherFeedback());
            holder.tvComment.setText("Comment "+(position+1));

            if (listFile.get(position).getTeacherEvaluatePath()!=null && !listFile.get(position).getTeacherEvaluatePath().equalsIgnoreCase("NA")){
                holder.tvComment.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_teach_sub_student),null);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listFile.get(position).getTeacherEvaluatePath().equalsIgnoreCase("NA")) {
                        Toast.makeText(AssignmentFileReview.this, "No files uploaded by teacher", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(AssignmentFileReview.this, ImageDisp.class);
                        intent.putExtra("path", listFile.get(position).getTeacherEvaluatePath().replaceAll(" ", "%20"));
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return listFile.size();
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