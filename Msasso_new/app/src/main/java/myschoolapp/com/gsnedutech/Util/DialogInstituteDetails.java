package myschoolapp.com.gsnedutech.Util;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import myschoolapp.com.gsnedutech.Arena.AddArenaArticle;
import myschoolapp.com.gsnedutech.Arena.AddArenaAudioClips;
import myschoolapp.com.gsnedutech.Arena.AddArenaVideoClips;
import myschoolapp.com.gsnedutech.Arena.ArAddFlashCards;
import myschoolapp.com.gsnedutech.Arena.ArAddPoll;
import myschoolapp.com.gsnedutech.Arena.ArPollDisplayActivity;
import myschoolapp.com.gsnedutech.Arena.ArenaAudioDisplay;
import myschoolapp.com.gsnedutech.Arena.ArenaDisplayActivity;
import myschoolapp.com.gsnedutech.Arena.ArenaVideoDisplay;
import myschoolapp.com.gsnedutech.Arena.FlashCardsDisplayNew;
import myschoolapp.com.gsnedutech.Arena.TeacherArenaArticleReview;
import myschoolapp.com.gsnedutech.Arena.TeacherArenaFlashCardReview;
import myschoolapp.com.gsnedutech.Arena.TeacherArenaQuizReview;
import myschoolapp.com.gsnedutech.Arena.TeacherPollReview;
import myschoolapp.com.gsnedutech.Arena.Trial.ArAddQuizNew;
import myschoolapp.com.gsnedutech.Models.CollegeInfo;
import myschoolapp.com.gsnedutech.Models.ListCombined;
import myschoolapp.com.gsnedutech.R;


public class DialogInstituteDetails {

    List<CollegeInfo> listBranches = new ArrayList<>();
    List<ListCombined> list = new ArrayList<>();
    Context context;
    Dialog dialog = null;
    RecyclerView rvBranches;

    public DialogInstituteDetails(Context context, List<CollegeInfo> listBranches) {
        this.listBranches = listBranches;
        this.context = context;
    }

    public void show(){
        dialog = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_inst_details);
        dialog.show();

        init();
    }

    void init(){

        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        rvBranches = dialog.findViewById(R.id.rv_branches);
        rvBranches.setLayoutManager(new LinearLayoutManager(context));
        rvBranches.setAdapter(new AdapterClass());

        handleList();

        dialog.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int c=0;
                JSONArray sections = new JSONArray();
                for (int i=0;i<listBranches.size();i++){
                    for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                        for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                            for (int l=0;l<listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size();l++){
                                if (listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).isSelected()){
                                    c++;
                                    JSONObject obj = new JSONObject();

                                    try {
                                        obj.put("sectionId",listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).getSectionId());
                                        obj.put("classId",listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassId());
                                        obj.put("courseId",listBranches.get(i).getCourses().get(j).getCourseId());
                                        obj.put("branchId",listBranches.get(i).getBranchId());

                                        sections.put(obj);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    }
                }

                if (c==0){
                    Toast.makeText(context,"Please select a section to continue.",Toast.LENGTH_SHORT).show();
                }else {

                    if (context instanceof TeacherArenaArticleReview){
                        ((TeacherArenaArticleReview)context).arenaReview("1",sections);
                    }
                    if (context instanceof TeacherArenaQuizReview){
                        ((TeacherArenaQuizReview)context).arenaReview("1",sections);
                    }
                    if (context instanceof TeacherArenaFlashCardReview){
                        ((TeacherArenaFlashCardReview)context).arenaReview("1",sections);
                    }
                    if (context instanceof TeacherPollReview){
                        ((TeacherPollReview)context).arenaReview("1",sections);
                    }
                    if (context instanceof AddArenaAudioClips){
                        ((AddArenaAudioClips)context).arenaReview("1",sections);
                    }
                    if (context instanceof AddArenaVideoClips){
                        ((AddArenaVideoClips)context).arenaReview("1",sections);
                    }
                    if (context instanceof AddArenaArticle){
                        ((AddArenaArticle)context).arenaReview("1",sections);
                    }
                    if (context instanceof ArAddQuizNew){
                        Fragment f = ((ArAddQuizNew)context).getFragmentManager().findFragmentById(R.id.frame);
                        ((ArAddQuizNew)context).qpFrag.arenaReview("1", sections);
                    }
                    if (context instanceof ArAddFlashCards){
                        ((ArAddFlashCards)context).arenaReview("1",sections);
                    }
                    if (context instanceof ArAddPoll){
                        ((ArAddPoll)context).arenaReview("1",sections);
                    }
                    if (context instanceof ArPollDisplayActivity){
                        ((ArPollDisplayActivity)context).arenaReview("1",sections);
                    }
                    dialog.dismiss();
                }

            }
        });

    }


    public void handleListNew() {

        dialog = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_inst_details);
        rvBranches = dialog.findViewById(R.id.rv_branches);
        rvBranches.setLayoutManager(new LinearLayoutManager(context));
        rvBranches.setAdapter(new AdapterClass());

        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int c=0;
                JSONArray sections = new JSONArray();
                for (int i=0;i<listBranches.size();i++){
                    for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                        for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                            for (int l=0;l<listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size();l++){
                                if (listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).isSelected() && listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).getAssignedId().equalsIgnoreCase("0")){
                                    c++;
                                    JSONObject obj = new JSONObject();

                                    try {
                                        obj.put("sectionId",listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).getSectionId());
                                        obj.put("classId",listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassId());
                                        obj.put("courseId",listBranches.get(i).getCourses().get(j).getCourseId());
                                        obj.put("branchId",listBranches.get(i).getBranchId());

                                        sections.put(obj);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    }
                }

                if (c==0){
                    Toast.makeText(context,"Please select a section to continue.",Toast.LENGTH_SHORT).show();
                }else {
                    if (context instanceof ArenaAudioDisplay){
                        ((ArenaAudioDisplay)context).arenaReview("1",sections);
                    }
                    if (context instanceof ArenaVideoDisplay){
                        ((ArenaVideoDisplay)context).arenaReview("1",sections);
                    }
                    if (context instanceof TeacherArenaArticleReview){
                        ((TeacherArenaArticleReview)context).arenaReview("1",sections);
                    }
                    if (context instanceof TeacherArenaQuizReview){
                        ((TeacherArenaQuizReview)context).arenaReview("1",sections);
                    }
                    if (context instanceof TeacherPollReview){
                        ((TeacherPollReview)context).arenaReview("1",sections);
                    }
                    if (context instanceof FlashCardsDisplayNew){
                        ((FlashCardsDisplayNew)context).arenaReview("1",sections);
                    }
                    if (context instanceof ArenaDisplayActivity){
                        ((ArenaDisplayActivity)context).arenaReview("1",sections);
                    }
                    if (context instanceof ArPollDisplayActivity){
                        ((ArPollDisplayActivity)context).arenaReview("1",sections);
                    }

                }
            }
        });

        list.clear();

        ListCombined lc;

        for (int i=0;i<listBranches.size();i++) {
            for (int j = 0; j < listBranches.get(i).getCourses().size(); j++) {
                for (int k = 0; k < listBranches.get(i).getCourses().get(j).getClasses().size(); k++) {
                    for (int l = 0; l < listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size(); l++) {
                        if (listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).isSelected()){
                            lc = new ListCombined();
                            lc.setBranchId(listBranches.get(i).getBranchId());
                            lc.setCourseId(listBranches.get(i).getCourses().get(j).getCourseId());
                            lc.setClassId(listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassId());
                            lc.setSectionId(listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).getSectionId());

                            handleCheckSectionsNew(lc,true);
                        }
                    }
                }
            }
        }


        handleList();

        dialog.show();

    }

    void handleCheckSectionsNew(ListCombined lcObj, boolean opt){
        int c=0;
        for (int i=0;i<listBranches.size();i++){
            if (lcObj.getBranchId().equalsIgnoreCase(listBranches.get(i).getBranchId())){
                for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                    if (lcObj.getCourseId().equalsIgnoreCase(listBranches.get(i).getCourses().get(j).getCourseId())){
                        for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                            if (lcObj.getClassId().equalsIgnoreCase(listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassId())){
                                for (int l=0;l<listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size();l++){
                                    if (listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).isSelected()){
                                        c++;
                                    }
                                }
                                if (c==listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size()){
                                    listBranches.get(i).getCourses().get(j).getClasses().get(k).setSelected(opt);
                                }
                            }
                        }
                    }
                }
            }
        }

        //callback for courses
        c=0;
        for (int i=0;i<listBranches.size();i++){
            if (listBranches.get(i).getBranchId().equalsIgnoreCase(lcObj.getBranchId())){
                for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                    if (listBranches.get(i).getCourses().get(j).getCourseId().equalsIgnoreCase(lcObj.getCourseId())){
                        for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                            if (listBranches.get(i).getCourses().get(j).getClasses().get(k).isSelected()){
                                c++;
                            }
                        }
                        if (c==listBranches.get(i).getCourses().get(j).getClasses().size()){
                            listBranches.get(i).getCourses().get(j).setSelected(opt);
                            c=0;
                        }
                    }
                }
            }
        }

        c=0;
        for (int i=0;i<listBranches.size();i++) {
            if (listBranches.get(i).getBranchId().equalsIgnoreCase(lcObj.getBranchId())) {
                for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                    if (listBranches.get(i).getCourses().get(j).isSelected()){
                        c++;
                    }
                }

                if (c==listBranches.get(i).getCourses().size()){
                    listBranches.get(i).setSelected(opt);
                }
            }
        }
    }

    private void handleList() {

        ListCombined lc;

        list.clear();

        for (int i=0;i<listBranches.size();i++){
            if (listBranches.get(i).isShow()){
                lc = new ListCombined();
                lc.setBranchId(listBranches.get(i).getBranchId());
                lc.setBranchName(listBranches.get(i).getBranchName());
                lc.setSelected(listBranches.get(i).isSelected());
                lc.setShow(listBranches.get(i).isShow());
                if (!list.contains(lc)){
                    list.add(lc);
                }
                for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                    if (listBranches.get(i).getCourses().get(j).isShow()) {
                        lc = new ListCombined();
                        lc.setBranchId(listBranches.get(i).getBranchId());
                        lc.setBranchName(listBranches.get(i).getBranchName());
                        lc.setCourseId(listBranches.get(i).getCourses().get(j).getCourseId());
                        lc.setCourseName(listBranches.get(i).getCourses().get(j).getCourseName());
                        lc.setShow(listBranches.get(i).getCourses().get(j).isShow());
                        lc.setSelected(listBranches.get(i).getCourses().get(j).isSelected());
                        if (!list.contains(lc)){
                            list.add(lc);
                        }
                        for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                            if (listBranches.get(i).getCourses().get(j).getClasses().get(k).isShow()) {
                                lc = new ListCombined();
                                lc.setBranchId(listBranches.get(i).getBranchId());
                                lc.setBranchName(listBranches.get(i).getBranchName());
                                lc.setCourseId(listBranches.get(i).getCourses().get(j).getCourseId());
                                lc.setCourseName(listBranches.get(i).getCourses().get(j).getCourseName());
                                lc.setClassId(listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassId());
                                lc.setClassName(listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassName());
                                lc.setSelected(listBranches.get(i).getCourses().get(j).getClasses().get(k).isSelected());
                                lc.setShow(listBranches.get(i).getCourses().get(j).getClasses().get(k).isShow());
                                if (!list.contains(lc)){
                                    list.add(lc);
                                }
                                for (int l=0;l<listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size();l++){
                                    if (listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).isShow()) {
                                        lc = new ListCombined();
                                        lc.setBranchId(listBranches.get(i).getBranchId());
                                        lc.setBranchName(listBranches.get(i).getBranchName());
                                        lc.setCourseId(listBranches.get(i).getCourses().get(j).getCourseId());
                                        lc.setCourseName(listBranches.get(i).getCourses().get(j).getCourseName());
                                        lc.setClassId(listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassId());
                                        lc.setClassName(listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassName());
                                        lc.setSectionId(listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).getSectionId());
                                        lc.setSectionName(listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).getSectionName());
                                        lc.setShow(listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).isShow());
                                        lc.setSelected(listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).isSelected());
                                        if (!list.contains(lc)){
                                            list.add(lc);
                                        }
                                    }
                                }
                            }

                        }
                    }

                }
            }


        }

        rvBranches.getAdapter().notifyDataSetChanged();

    }

    class AdapterClass extends RecyclerView.Adapter<AdapterClass.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_exp_list_header,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            if (list.get(position).getCourseId()==null || list.get(position).getCourseId().equalsIgnoreCase("")){
                holder.ivHeader.setVisibility(View.VISIBLE);
//                holder.llMain.setBackgroundColor(Color.parseColor("#cfcfcf"));
                holder.llMain.setBackgroundResource(R.drawable.bg_exp_head);
                holder.tvHeader.setText(list.get(position).getBranchName());
                holder.ivHeader.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                holder.cbSelection.setChecked(list.get(position).isSelected());

                holder.ivHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleShow(list.get(position),"branches");
                    }
                });

                holder.cbSelection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (compoundButton.isPressed()){
                            handleCheck(list.get(position),b,"branches");
                        }
                    }
                });

            }else if (list.get(position).getClassId()==null || list.get(position).getClassId().equalsIgnoreCase("")){
                holder.ivHeader.setVisibility(View.VISIBLE);
//                holder.llMain.setBackgroundColor(Color.parseColor("#cfcfcf"));
                holder.llMain.setBackgroundResource(R.drawable.bg_exp_head);
                holder.tvHeader.setText(list.get(position).getCourseName());
                holder.ivHeader.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                holder.cbSelection.setChecked(list.get(position).isSelected());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(30, 10, 0, 10);
                holder.llMain.setLayoutParams(params);

                holder.ivHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleShow(list.get(position),"courses");
                    }
                });

                holder.cbSelection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (compoundButton.isPressed()){
                            handleCheck(list.get(position),b,"courses");
                        }
                    }
                });

            }else if (list.get(position).getSectionId()==null || list.get(position).getSectionId().equalsIgnoreCase("")){
                holder.ivHeader.setVisibility(View.VISIBLE);
//                holder.llMain.setBackgroundColor(Color.parseColor("#cfcfcf"));
                holder.llMain.setBackgroundResource(R.drawable.bg_exp_head);
                holder.tvHeader.setText(list.get(position).getClassName());
                holder.ivHeader.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                holder.cbSelection.setChecked(list.get(position).isSelected());

                holder.ivHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleShow(list.get(position),"classes");
                    }
                });

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(60, 10, 0, 10);
                holder.llMain.setLayoutParams(params);

                holder.cbSelection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (compoundButton.isPressed()){
                            handleCheck(list.get(position),b,"classes");
                        }
                    }
                });

            }else if (list.get(position).getSectionId()!=null && !list.get(position).getSectionId().equalsIgnoreCase("")){
                holder.ivHeader.setVisibility(View.GONE);
                holder.llMain.setBackgroundColor(Color.WHITE);
                holder.tvHeader.setText(list.get(position).getSectionName());
                holder.tvHeader.setTextColor(Color.BLACK);
                holder.cbSelection.setButtonTintList(ColorStateList.valueOf(Color.LTGRAY));
                holder.cbSelection.setChecked(list.get(position).isSelected());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(90, 10, 0, 10);
                holder.llMain.setLayoutParams(params);

                holder.cbSelection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (compoundButton.isPressed()){
                            handleCheck(list.get(position),b,"sections");
                        }
                    }
                });

            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivHeader;
            TextView tvHeader;
            CheckBox cbSelection;
            LinearLayout llMain;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivHeader = itemView.findViewById(R.id.iv_header);
                tvHeader = itemView.findViewById(R.id.tv_header);
                cbSelection = itemView.findViewById(R.id.cb_selection);
                llMain = itemView.findViewById(R.id.ll_main);
            }
        }
    }

    private void handleCheck(ListCombined lcObj, boolean opt, String type) {
        switch(type){
            case "branches":

                for (int i=0;i<listBranches.size();i++){
                    if (listBranches.get(i).getBranchId().equalsIgnoreCase(lcObj.getBranchId())){
                        listBranches.get(i).setSelected(opt);
                        for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                            listBranches.get(i).getCourses().get(j).setSelected(opt);
                            for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                                listBranches.get(i).getCourses().get(j).getClasses().get(k).setSelected(opt);
                                for (int l=0;l<listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size();l++){
                                    listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).setSelected(opt);
                                }
                            }
                        }
                    }
                }

                break;
            case "courses":

                for (int i=0;i<listBranches.size();i++){
                    if (listBranches.get(i).getBranchId().equalsIgnoreCase(lcObj.getBranchId())){
                        for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                            if (listBranches.get(i).getCourses().get(j).getCourseId().equalsIgnoreCase(lcObj.getCourseId())){
                                listBranches.get(i).getCourses().get(j).setSelected(opt);
                                for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                                    listBranches.get(i).getCourses().get(j).getClasses().get(k).setSelected(opt);
                                    for (int l=0;l<listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size();l++){
                                        listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).setSelected(opt);
                                    }
                                }
                            }
                        }
                    }
                }

                //callback for branches
                if (opt){
                    for (int i=0;i<listBranches.size();i++) {
                        if (listBranches.get(i).getBranchId().equalsIgnoreCase(lcObj.getBranchId())) {
                            for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                                if (listBranches.get(i).getCourses().get(j).getCourseId().equalsIgnoreCase(lcObj.getCourseId())){
                                    listBranches.get(i).getCourses().get(j).setSelected(opt);
                                }
                            }
                        }
                    }

                    int c=0;
                    for (int i=0;i<listBranches.size();i++) {
                        if (listBranches.get(i).getBranchId().equalsIgnoreCase(lcObj.getBranchId())) {
                            for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                                if (listBranches.get(i).getCourses().get(j).isSelected()){
                                    c++;
                                }
                            }

                            if (c==listBranches.get(i).getCourses().size()){
                                listBranches.get(i).setSelected(opt);
                            }
                        }
                    }

                }else {
                    for (int i=0;i<listBranches.size();i++) {
                        if (listBranches.get(i).getBranchId().equalsIgnoreCase(lcObj.getBranchId())) {
                            listBranches.get(i).setSelected(opt);
                        }
                    }
                }

                break;

            case "classes":
                for (int i=0;i<listBranches.size();i++){
                    if (listBranches.get(i).getBranchId().equalsIgnoreCase(lcObj.getBranchId())){
                        for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                            if (listBranches.get(i).getCourses().get(j).getCourseId().equalsIgnoreCase(lcObj.getCourseId())){
                                for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                                    if (listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassId().equalsIgnoreCase(lcObj.getClassId())){
                                        listBranches.get(i).getCourses().get(j).getClasses().get(k).setSelected(opt);
                                        for (int l=0;l<listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size();l++){
                                            listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).setSelected(opt);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if(opt){

                    //callback for courses
                    int c=0;

                    for (int i=0;i<listBranches.size();i++){
                        if (listBranches.get(i).getBranchId().equalsIgnoreCase(lcObj.getBranchId())){
                            for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                                if (listBranches.get(i).getCourses().get(j).getCourseId().equalsIgnoreCase(lcObj.getCourseId())){
                                    for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                                        if (listBranches.get(i).getCourses().get(j).getClasses().get(k).isSelected()){
                                            c++;
                                        }
                                    }
                                    if (c==listBranches.get(i).getCourses().get(j).getClasses().size()){
                                        listBranches.get(i).getCourses().get(j).setSelected(opt);
                                        c=0;
                                    }
                                }
                            }
                        }
                    }


                    //callback for branches
                    c=0;
                    for (int i=0;i<listBranches.size();i++) {
                        if (listBranches.get(i).getBranchId().equalsIgnoreCase(lcObj.getBranchId())) {
                            for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                                if (listBranches.get(i).getCourses().get(j).isSelected()){
                                    c++;
                                }
                            }

                            if (c==listBranches.get(i).getCourses().size()){
                                listBranches.get(i).setSelected(opt);
                            }
                        }
                    }

                }else {
                    for (int i=0;i<listBranches.size();i++) {
                        if (listBranches.get(i).getBranchId().equalsIgnoreCase(lcObj.getBranchId())) {
                            listBranches.get(i).setSelected(opt);
                            for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                                if (listBranches.get(i).getCourses().get(j).getCourseId().equalsIgnoreCase(lcObj.getCourseId())){
                                    listBranches.get(i).getCourses().get(j).setSelected(opt);
                                }
                            }
                        }
                    }
                }

                break;

            case "sections":

                for (int i=0;i<listBranches.size();i++){
                    if (listBranches.get(i).getBranchId().equalsIgnoreCase(lcObj.getBranchId())){
                        for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                            if (listBranches.get(i).getCourses().get(j).getCourseId().equalsIgnoreCase(lcObj.getCourseId())){
                                for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                                    if (listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassId().equalsIgnoreCase(lcObj.getClassId())){
                                        for (int l=0;l<listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size();l++){
                                            if (listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).getSectionId().equalsIgnoreCase(lcObj.getSectionId())){
                                                listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).setSelected(opt);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (opt){

                    //callback for classes
                    int c=0;
                    for (int i=0;i<listBranches.size();i++){
                        if (lcObj.getBranchId().equalsIgnoreCase(listBranches.get(i).getBranchId())){
                            for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                                if (lcObj.getCourseId().equalsIgnoreCase(listBranches.get(i).getCourses().get(j).getCourseId())){
                                    for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                                        if (lcObj.getClassId().equalsIgnoreCase(listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassId())){
                                            for (int l=0;l<listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size();l++){
                                                if (listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).isSelected()){
                                                    c++;
                                                }
                                            }
                                            if (c==listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size()){
                                                listBranches.get(i).getCourses().get(j).getClasses().get(k).setSelected(opt);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //callback for courses
                    c=0;
                    for (int i=0;i<listBranches.size();i++){
                        if (listBranches.get(i).getBranchId().equalsIgnoreCase(lcObj.getBranchId())){
                            for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                                if (listBranches.get(i).getCourses().get(j).getCourseId().equalsIgnoreCase(lcObj.getCourseId())){
                                    for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                                        if (listBranches.get(i).getCourses().get(j).getClasses().get(k).isSelected()){
                                            c++;
                                        }
                                    }
                                    if (c==listBranches.get(i).getCourses().get(j).getClasses().size()){
                                        listBranches.get(i).getCourses().get(j).setSelected(opt);
                                        c=0;
                                    }
                                }
                            }
                        }
                    }

                    //callback for branches
                    c=0;
                    for (int i=0;i<listBranches.size();i++) {
                        if (listBranches.get(i).getBranchId().equalsIgnoreCase(lcObj.getBranchId())) {
                            for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                                if (listBranches.get(i).getCourses().get(j).isSelected()){
                                    c++;
                                }
                            }

                            if (c==listBranches.get(i).getCourses().size()){
                                listBranches.get(i).setSelected(opt);
                            }
                        }
                    }

                }else {
                    for (int i=0;i<listBranches.size();i++) {
                        if (listBranches.get(i).getBranchId().equalsIgnoreCase(lcObj.getBranchId())) {
                            listBranches.get(i).setSelected(opt);
                            for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                                if (listBranches.get(i).getCourses().get(j).getCourseId().equalsIgnoreCase(lcObj.getCourseId())){
                                    listBranches.get(i).getCourses().get(j).setSelected(opt);
                                    for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                                        if (listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassId().equalsIgnoreCase(lcObj.getClassId())){
                                            listBranches.get(i).getCourses().get(j).getClasses().get(k).setSelected(opt);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                break;
        }
        handleList();
    }

    void handleShow(ListCombined lcObj,String type){
        switch (type){
            case "branches":
                for (int i=0;i<listBranches.size();i++){
                    if (lcObj.getBranchId().equalsIgnoreCase(listBranches.get(i).getBranchId())){
                        for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                            listBranches.get(i).getCourses().get(j).setShow(!(listBranches.get(i).getCourses().get(j).isShow()));
                        }
                    }
                }
                break;
            case "courses":
                for (int i=0;i<listBranches.size();i++){
                    if (lcObj.getBranchId().equalsIgnoreCase(listBranches.get(i).getBranchId())) {
                        for (int j = 0; j < listBranches.get(i).getCourses().size(); j++) {
                            if (lcObj.getCourseId().equalsIgnoreCase(listBranches.get(i).getCourses().get(j).getCourseId())) {
                                for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                                    listBranches.get(i).getCourses().get(j).getClasses().get(k).setShow(!(listBranches.get(i).getCourses().get(j).getClasses().get(k).isShow()));
                                }
                            }
                        }
                    }
                }
                break;
            case "classes":
                for (int i=0;i<listBranches.size();i++){
                    if (lcObj.getBranchId().equalsIgnoreCase(listBranches.get(i).getBranchId())) {
                        for (int j = 0; j < listBranches.get(i).getCourses().size(); j++) {
                            if (lcObj.getCourseId().equalsIgnoreCase(listBranches.get(i).getCourses().get(j).getCourseId())) {
                                for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                                    if(lcObj.getClassId().equalsIgnoreCase(listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassId())){
                                        for (int l=0;l<listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size();l++){
                                            listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).setShow(!(listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).isShow()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
        }
        handleList();
    }

}
