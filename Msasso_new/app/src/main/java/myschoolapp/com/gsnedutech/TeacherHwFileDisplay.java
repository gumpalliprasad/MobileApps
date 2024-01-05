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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.HomeWorkFilesDetail;
import myschoolapp.com.gsnedutech.Util.ImageDisp;
import myschoolapp.com.gsnedutech.Util.NewPdfViewer;
import myschoolapp.com.gsnedutech.Util.PdfWebViewer;
import myschoolapp.com.gsnedutech.Util.PlayerActivity;
import myschoolapp.com.gsnedutech.Util.YoutubeActivity;

public class TeacherHwFileDisplay extends AppCompatActivity {

    @BindView(R.id.rv_files)
    RecyclerView rvFiles;


    List<HomeWorkFilesDetail> listFiles = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_hw_file_display);

        ButterKnife.bind(this);
        
        init();
    }

    private void init() {
        
        listFiles.addAll((Collection<? extends HomeWorkFilesDetail>) getIntent().getSerializableExtra("files"));

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rvFiles.setLayoutManager(new GridLayoutManager(this, 2));
        rvFiles.setAdapter(new FilesAdapter());

    }

    class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder>{

        @NonNull
        @Override
        public FilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FilesAdapter.ViewHolder(LayoutInflater.from(TeacherHwFileDisplay.this).inflate(R.layout.item_assign_file,parent,false));

        }

        @Override
        public void onBindViewHolder(@NonNull FilesAdapter.ViewHolder holder, int position) {
            holder.tvFileName.setText(listFiles.get(position).getFileName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listFiles.get(position).getFilePath().contains("pdf")) {
                        //                        Intent intent = new Intent(StudentHWDetails.this, SomeAct.class);
                        Intent intent = new Intent(TeacherHwFileDisplay.this, NewPdfViewer.class);
                        intent.putExtra("url", listFiles.get(position).getFilePath());
                        startActivity(intent);
                    }else
                    if (listFiles.get(position).getFilePath().contains("mp4")) {
                        Intent intent = new Intent(TeacherHwFileDisplay.this, PlayerActivity.class);
                        intent.putExtra("url", listFiles.get(position).getFilePath());
                        startActivity(intent);
                    }else
                    if (listFiles.get(position).getFilePath().contains("youtube")) {
                        Intent intent = new Intent(TeacherHwFileDisplay.this, YoutubeActivity.class);

                        String s[] = listFiles.get(position).getFilePath().split("/");

                        intent.putExtra("videoItem", s[s.length - 1]);
                        startActivity(intent);
                    }else
                    if (listFiles.get(position).getFilePath().contains("jpg") || listFiles.get(position).getFilePath().contains("png")) {
                        Intent intent = new Intent(TeacherHwFileDisplay.this, ImageDisp.class);
                        intent.putExtra("path", listFiles.get(position).getFilePath());
                        startActivity(intent);
                    }else
                    if (listFiles.get(position).getFilePath().contains("doc") || listFiles.get(position).getFilePath().equalsIgnoreCase("docx") || listFiles.get(position).getFilePath().equalsIgnoreCase("ppt") || listFiles.get(position).getFilePath().equalsIgnoreCase("pptx")) {
                        Intent intent = new Intent(TeacherHwFileDisplay.this, PdfWebViewer.class);
                        intent.putExtra("url", listFiles.get(position).getFilePath());
                        startActivity(intent);
                    }else{
                        Toast.makeText(TeacherHwFileDisplay.this,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}