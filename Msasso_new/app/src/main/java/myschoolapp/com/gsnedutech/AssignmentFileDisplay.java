package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import myschoolapp.com.gsnedutech.Models.HomeWorkFilesDetail;
import myschoolapp.com.gsnedutech.Models.StudentHWFile;
import myschoolapp.com.gsnedutech.Util.ImageDisp;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NewPdfViewer;
import myschoolapp.com.gsnedutech.Util.PdfWebViewer;
import myschoolapp.com.gsnedutech.Util.PlayerActivity;
import myschoolapp.com.gsnedutech.Util.YoutubeActivity;

public class    AssignmentFileDisplay extends AppCompatActivity {

    List<HomeWorkFilesDetail> listFiles = new ArrayList<>();
    List<StudentHWFile> listStudentSubmissions = new ArrayList<>();

    MyUtils utils = new MyUtils();


    HomeWorkDetails hwObj;

    @BindView(R.id.rv_files)
    RecyclerView rvFiles;

    @BindView(R.id.ll_teacher_review)
    LinearLayout llTeacherReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_file_display);
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

        rvFiles.setLayoutManager(new GridLayoutManager(this,2));


        if (getIntent().hasExtra("studentFiles")){

            //show teacher review card
            hwObj = (HomeWorkDetails) getIntent().getSerializableExtra("hwObj");


            if(hwObj.getHwStatus().equalsIgnoreCase("COMPLETED") || hwObj.getHwStatus().equalsIgnoreCase("REASSIGN")){
                llTeacherReview.setVisibility(View.VISIBLE);

                llTeacherReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(AssignmentFileDisplay.this,AssignmentTeacherReview.class);
                        intent.putExtra("files",(Serializable)listStudentSubmissions);
                        intent.putExtra("hwObj",(Serializable)hwObj);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                });
            }

            listStudentSubmissions.addAll((Collection<? extends StudentHWFile>) getIntent().getSerializableExtra("studentFiles"));
            rvFiles.setAdapter(new FilesSubmissionAdapter());

//            if (hwObj.getHwStatus().equalsIgnoreCase("REASSIGNED")){
//                findViewById(R.id.ll_reassigned).setVisibility(View.VISIBLE);
//            }

        }else {

            listFiles.addAll((Collection<? extends HomeWorkFilesDetail>) getIntent().getSerializableExtra("files"));
            rvFiles.setAdapter(new FilesAdapter());

        }
    }

    class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder>{

        @NonNull
        @Override
        public FilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(AssignmentFileDisplay.this).inflate(R.layout.item_assign_file,parent,false));

        }

        @Override
        public void onBindViewHolder(@NonNull FilesAdapter.ViewHolder holder, int position) {
            holder.tvFileName.setText(listFiles.get(position).getFileName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listFiles.get(position).getFilePath().contains("pdf")) {
                        //                        Intent intent = new Intent(StudentHWDetails.this, SomeAct.class);
                        Intent intent = new Intent(AssignmentFileDisplay.this, NewPdfViewer.class);
                        intent.putExtra("url", listFiles.get(position).getFilePath());
                        startActivity(intent);
                    }else
                    if (listFiles.get(position).getFilePath().contains("mp4")) {
                        Intent intent = new Intent(AssignmentFileDisplay.this, PlayerActivity.class);
                        intent.putExtra("url", listFiles.get(position).getFilePath());
                        startActivity(intent);
                    }else
                    if (listFiles.get(position).getFilePath().contains("youtube")) {
                        Intent intent = new Intent(AssignmentFileDisplay.this, YoutubeActivity.class);

                        String s[] = listFiles.get(position).getFilePath().split("/");

                        intent.putExtra("videoItem", s[s.length - 1]);
                        startActivity(intent);
                    }else
                    if (listFiles.get(position).getFilePath().contains("jpg") || listFiles.get(position).getFilePath().contains("png")) {
                        Intent intent = new Intent(AssignmentFileDisplay.this, ImageDisp.class);
                        intent.putExtra("path", listFiles.get(position).getFilePath());
                        startActivity(intent);
                    }else
                    if (listFiles.get(position).getFilePath().contains("doc") || listFiles.get(position).getFilePath().equalsIgnoreCase("docx") || listFiles.get(position).getFilePath().equalsIgnoreCase("ppt") || listFiles.get(position).getFilePath().equalsIgnoreCase("pptx")) {
                        Intent intent = new Intent(AssignmentFileDisplay.this, PdfWebViewer.class);
                        intent.putExtra("url", listFiles.get(position).getFilePath());
                        startActivity(intent);
                    }else{
                        Toast.makeText(AssignmentFileDisplay.this,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return listFiles.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvFileName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFileName = itemView.findViewById(R.id.tv_file_name);
            }
        }
    }

    class FilesSubmissionAdapter extends RecyclerView.Adapter<FilesSubmissionAdapter.ViewHolder>{

        @NonNull
        @Override
        public FilesSubmissionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(AssignmentFileDisplay.this).inflate(R.layout.item_assign_file,parent,false));

        }

        @Override
        public void onBindViewHolder(@NonNull FilesSubmissionAdapter.ViewHolder holder, int position) {
            holder.tvVersion.setText(listStudentSubmissions.get(position).getVersion()+"");
            holder.itemView.findViewById(R.id.cv_version).setVisibility(View.VISIBLE);

            holder.tvFileName.setText(listStudentSubmissions.get(position).getStudentHWFilePath().split("/")[listStudentSubmissions.get(position).getStudentHWFilePath().split("/").length-1]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (listStudentSubmissions.get(position).getStudentHWFilePath().contains("pdf")) {
                        //                        Intent intent = new Intent(StudentHWDetails.this, SomeAct.class);
                        Intent intent = new Intent(AssignmentFileDisplay.this, NewPdfViewer.class);
                        intent.putExtra("url", listStudentSubmissions.get(position).getStudentHWFilePath());
                        startActivity(intent);
                    }else
                    if (listStudentSubmissions.get(position).getStudentHWFilePath().contains("mp4")) {
                        Intent intent = new Intent(AssignmentFileDisplay.this, PlayerActivity.class);
                        intent.putExtra("url", listStudentSubmissions.get(position).getStudentHWFilePath());
                        startActivity(intent);
                    }else
                    if (listStudentSubmissions.get(position).getStudentHWFilePath().contains("youtube")) {
                        Intent intent = new Intent(AssignmentFileDisplay.this, YoutubeActivity.class);

                        String s[] = listStudentSubmissions.get(position).getStudentHWFilePath().split("/");

                        intent.putExtra("videoItem", s[s.length - 1]);
                        startActivity(intent);
                    }else
                    if (listStudentSubmissions.get(position).getStudentHWFilePath().contains("jpg") || listStudentSubmissions.get(position).getStudentHWFilePath().contains("png")) {
                        Intent intent = new Intent(AssignmentFileDisplay.this, ImageDisp.class);
                        intent.putExtra("path", listStudentSubmissions.get(position).getStudentHWFilePath());
                        startActivity(intent);
                    }else
                    if (listStudentSubmissions.get(position).getStudentHWFilePath().contains("doc") || listStudentSubmissions.get(position).getStudentHWFilePath().equalsIgnoreCase("docx") || listStudentSubmissions.get(position).getStudentHWFilePath().equalsIgnoreCase("ppt") || listStudentSubmissions.get(position).getStudentHWFilePath().equalsIgnoreCase("pptx")) {
                        Intent intent = new Intent(AssignmentFileDisplay.this, PdfWebViewer.class);
                        intent.putExtra("url", listStudentSubmissions.get(position).getStudentHWFilePath());
                        startActivity(intent);
                    }else{
                        Toast.makeText(AssignmentFileDisplay.this,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return listStudentSubmissions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvFileName,tvVersion;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFileName = itemView.findViewById(R.id.tv_file_name);
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