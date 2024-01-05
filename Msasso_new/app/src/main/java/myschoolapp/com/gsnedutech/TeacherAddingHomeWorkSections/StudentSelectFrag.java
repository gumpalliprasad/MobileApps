package myschoolapp.com.gsnedutech.TeacherAddingHomeWorkSections;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.TeacherHwStudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StudentSelectFrag extends Fragment {

    View viewStudentSelectFrag;
    Unbinder unbinder;

    Activity mActivity;

    @BindView(R.id.rv_students)
    RecyclerView rvStudents;

    @BindView(R.id.rv_selected_students)
    RecyclerView rvSelectedStudents;

    @BindView(R.id.et_search)
    EditText etSearch;

    MyUtils utils = new MyUtils();

    List<TeacherHwStudentObj> studList = new ArrayList<>();
    List<TeacherHwStudentObj> selectedStudList = new ArrayList<>();

    public StudentSelectFrag() {
        // Required empty public constructor
    }

    StudentAdapter adapter;
    SelectedStudentsAdapter selectedAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewStudentSelectFrag = inflater.inflate(R.layout.fragment_student_select, container, false);
        unbinder = ButterKnife.bind(this,viewStudentSelectFrag);

        init();

        return viewStudentSelectFrag;
    }
    void init(){
        viewStudentSelectFrag.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TeacherHWAddingWithSections)mActivity).loadFrag(2);

                ((TeacherHWAddingWithSections)mActivity).newHwObj.getListAssigned().get(((TeacherHWAddingWithSections)mActivity).pos).setStudList(studList);
            }
        });
        viewStudentSelectFrag.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TeacherHWAddingWithSections)mActivity).loadFrag(2);

                ((TeacherHWAddingWithSections)mActivity).newHwObj.getListAssigned().get(((TeacherHWAddingWithSections)mActivity).pos).setStudList(studList);
            }
        });

        studList.addAll(((TeacherHWAddingWithSections)mActivity).newHwObj.getListAssigned().get(((TeacherHWAddingWithSections)mActivity).pos).getStudList());
        selectedStudList.addAll(((TeacherHWAddingWithSections)mActivity).newHwObj.getListAssigned().get(((TeacherHWAddingWithSections)mActivity).pos).getStudList());


        rvSelectedStudents.setLayoutManager(new LinearLayoutManager(mActivity,RecyclerView.HORIZONTAL,false));
        selectedAdapter = new SelectedStudentsAdapter();
        rvSelectedStudents.setAdapter(selectedAdapter);

        rvStudents.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new StudentAdapter(studList);
        rvStudents.setAdapter(adapter);
        rvStudents.addItemDecoration(new DividerItemDecoration(rvStudents.getContext(), DividerItemDecoration.VERTICAL));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.getFilter().filter(editable.toString());
            }
        });
    }


    class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> implements Filterable {

        List<TeacherHwStudentObj> listStudents;
        List<TeacherHwStudentObj> list_filtered;

        public StudentAdapter(List<TeacherHwStudentObj> listStudents) {
            this.listStudents = listStudents;
            list_filtered = studList;
        }

        @NonNull
        @Override
        public StudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new StudentAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_teacher_student_selection,parent,false));
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull StudentAdapter.ViewHolder holder, int position) {



            holder.tvStudentName.setText(list_filtered.get(position).getStudentName());
            holder.tvRollNumber.setText(list_filtered.get(position).getRollnumber());

            Picasso.with(mActivity).load(new AppUrls().GetstudentProfilePic + list_filtered.get(position).getProfilePic()).placeholder(R.drawable.user_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivProfPic);

            if (list_filtered.get(position).isChecked()){
                holder.cbOption.setChecked(true);
            }else{
                holder.cbOption.setChecked(false);
            }

            holder.cbOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        list_filtered.get(position).setChecked(true);
                        int index = studList.indexOf(list_filtered.get(position));
                        studList.get(index).setChecked(true);
                        if (!selectedStudList.contains(list_filtered.get(position))){
                            selectedStudList.add(list_filtered.get(position));
                        }
                    }
                    else{
                        list_filtered.get(position).setChecked(false);
                        int index = studList.indexOf(list_filtered.get(position));
                        studList.get(index).setChecked(false);

                        for (int i=0;i<selectedStudList.size();i++){
                            if (selectedStudList.get(i).getStudentId().equalsIgnoreCase(list_filtered.get(position).getStudentId())){
                                selectedStudList.remove(i);
                            }
                        }
                    }

                    selectedAdapter.notifyDataSetChanged();
                }
            });
        }


        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String string = constraint.toString();
                    utils.showLog("TAG", string);
                    if (string.isEmpty()) {
                        list_filtered = studList;
                    } else {
                        List<TeacherHwStudentObj> filteredList = new ArrayList<>();
                        for (TeacherHwStudentObj s : studList) {
                            if (s.getStudentName().toLowerCase().contains(string.toLowerCase()) || s.getRollnumber().toLowerCase().contains(string.toLowerCase())) {
                                filteredList.add(s);
                            }
                        }
                        list_filtered = filteredList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = list_filtered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    list_filtered = (List<TeacherHwStudentObj>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        @Override
        public int getItemCount() {
            return list_filtered.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvStudentName,tvRollNumber;
            ImageView ivProfPic;
            CheckBox cbOption;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivProfPic = itemView.findViewById(R.id.iv_student_image);
                tvStudentName = itemView.findViewById(R.id.tv_student_name);
                tvRollNumber = itemView.findViewById(R.id.tv_rollnum);
                cbOption = itemView.findViewById(R.id.cb_option);

            }
        }
    }

    class SelectedStudentsAdapter extends RecyclerView.Adapter<SelectedStudentsAdapter.ViewHolder>{

        @NonNull
        @Override
        public SelectedStudentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SelectedStudentsAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_selected_student,parent,false));

        }

        @Override
        public void onBindViewHolder(@NonNull SelectedStudentsAdapter.ViewHolder holder, int position) {

            holder.tvStudentName.setText(selectedStudList.get(position).getStudentName());

            Picasso.with(mActivity).load(new AppUrls().GetstudentProfilePic + selectedStudList.get(position).getProfilePic()).placeholder(R.drawable.user_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivProfPic);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i=0;i<studList.size();i++){
                        if (studList.get(i).getStudentId().equalsIgnoreCase(selectedStudList.get(position).getStudentId())){
                            studList.get(i).setChecked(false);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return selectedStudList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvStudentName;
            ImageView ivProfPic;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivProfPic = itemView.findViewById(R.id.iv_prof_pic);
                tvStudentName = itemView.findViewById(R.id.tv_student_name);
            }
        }
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