package myschoolapp.com.gsnedutech.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Arena.AddArenaArticle;
import myschoolapp.com.gsnedutech.Arena.AddArenaAudioClips;
import myschoolapp.com.gsnedutech.Arena.AddArenaVideoClips;
import myschoolapp.com.gsnedutech.Arena.ArAddFlashCards;
import myschoolapp.com.gsnedutech.Arena.ArAddPoll;
import myschoolapp.com.gsnedutech.Arena.ArPollDisplayActivity;
import myschoolapp.com.gsnedutech.Arena.ArStoryArticles;
import myschoolapp.com.gsnedutech.Arena.ArenaAudioDisplay;
import myschoolapp.com.gsnedutech.Arena.ArenaDisplayActivity;
import myschoolapp.com.gsnedutech.Arena.ArenaVideoDisplay;
import myschoolapp.com.gsnedutech.Arena.FlashCardsDisplayNew;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaCategories;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaObj;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Arena.TeacherArenaQuizReview;
import myschoolapp.com.gsnedutech.Arena.Trial.ArAddQuizNew;
import myschoolapp.com.gsnedutech.Arena.ViewAllArticles;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Arena.TeacherArenaArticleListing;
import myschoolapp.com.gsnedutech.StudentArenaRecordings;
import myschoolapp.com.gsnedutech.TeacherHomeNew;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.DrawableUtils;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.ViewAnimation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class TeacherBottomArena extends Fragment {

    private static final String TAG = TeacherBottomArena.class.getName();

    View teacherBottomArenaView;
    Activity mActivity;
    Unbinder unbinder;

    List<ArenaObj> listArenas = new ArrayList<>();

    List<ArenaRecord> listGeneralArenas = new ArrayList<>();
    List<ArenaRecord> listFlashArenas = new ArrayList<>();
    List<ArenaRecord> listQuizArenas = new ArrayList<>();
    List<ArenaRecord> pollsList = new ArrayList<>();

    int offset=0;
    int quizOffset=0;
    int flashOffset=0;
    int pollsOffset=0;

    boolean hasNextPage = false;
    boolean hasQuizNextPage = false;
    boolean hasFlashNextPage = false;
    boolean hasPollsNextPage = false;

    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_stories)
    RecyclerView rvStories;

    @BindView(R.id.rv_quizes)
    RecyclerView rvQuizzes;

    @BindView(R.id.rv_flash_cards)
    RecyclerView rvFlashCards;

    @BindView(R.id.rv_polls)
    RecyclerView rvPolls;

    @BindView(R.id.ll_quizzes)
    LinearLayout llQuizzes;

    @BindView(R.id.ll_flash_cards)
    LinearLayout llFlashCards;

    @BindView(R.id.ll_stories)
    LinearLayout llStories;

    @BindView(R.id.ll_poll)
    LinearLayout llPolls;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    TeacherObj tObj;

    List<ArenaCategories> arenaCategoriesList = new ArrayList<>();
    boolean toggle = false;

    int[] colors = new DrawableUtils().getDarkPalet();

    int audioCount=0,videoCount=0,storyCount=0,flashCount=0,quizCount=0,pollsCount=0;

    public TeacherBottomArena() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        teacherBottomArenaView =  inflater.inflate(R.layout.fragment_teacher_bottom_arena, container, false);
        unbinder = ButterKnife.bind(this,teacherBottomArenaView);
        init();
        return teacherBottomArenaView;
    }

    void init(){

        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);

        rvStories.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
        rvStories.setAdapter(new ArenaAdapter());
        rvQuizzes.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
        rvQuizzes.setAdapter(new QuizzesAdapter());
        rvFlashCards.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
        rvFlashCards.setAdapter(new FlashCardsAdapter());
        rvPolls.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
        rvPolls.setAdapter(new PollsAdapter());

        teacherBottomArenaView.findViewById(R.id.cv_audio_arena).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, StudentArenaRecordings.class);
                intent.putExtra("arenaCategory","4");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        teacherBottomArenaView.findViewById(R.id.cv_video_arena).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, StudentArenaRecordings.class);
                intent.putExtra("arenaCategory","5");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        teacherBottomArenaView.findViewById(R.id.cv_arena_stories).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, StudentArenaRecordings.class);
                intent.putExtra("arenaCategory","6");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        teacherBottomArenaView.findViewById(R.id.cv_flash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, StudentArenaRecordings.class);
                intent.putExtra("arenaCategory","2");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        teacherBottomArenaView.findViewById(R.id.cv_arena_quiz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, StudentArenaRecordings.class);
                intent.putExtra("arenaCategory","1");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        teacherBottomArenaView.findViewById(R.id.cv_arena_polls).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, StudentArenaRecordings.class);
                intent.putExtra("arenaCategory","7");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        teacherBottomArenaView.findViewById(R.id.cv_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, TeacherArenaArticleListing.class);
                intent.putExtra("category","4");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        teacherBottomArenaView.findViewById(R.id.cv_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, TeacherArenaArticleListing.class);
                intent.putExtra("category","5");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        teacherBottomArenaView.findViewById(R.id.cv_story).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, TeacherArenaArticleListing.class);
                intent.putExtra("category","6");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });



        teacherBottomArenaView.findViewById(R.id.cv_flash_cards).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, TeacherArenaArticleListing.class);
                intent.putExtra("category","2");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        teacherBottomArenaView.findViewById(R.id.cv_quiz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, TeacherArenaArticleListing.class);
                intent.putExtra("category","1");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        teacherBottomArenaView.findViewById(R.id.cv_flash_polls).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, TeacherArenaArticleListing.class);
                intent.putExtra("category","7");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        teacherBottomArenaView.findViewById(R.id.tv_view_all_stories).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, ArStoryArticles.class));
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
        teacherBottomArenaView.findViewById(R.id.tv_v_all_q).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, ViewAllArticles.class);
                intent.putExtra("arenaCategory","1");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
        teacherBottomArenaView.findViewById(R.id.tv_v_all_fc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, ViewAllArticles.class);
                intent.putExtra("arenaCategory","2");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
        teacherBottomArenaView.findViewById(R.id.tv_v_all_poll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, ViewAllArticles.class);
                intent.putExtra("arenaCategory","7");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        getTeacherArenas();
        getArenas();

    }

    @Override
    public void onResume() {
//        getArenas();

        arenaCategoriesList.clear();
        arenaCategoriesList.addAll(((TeacherHomeNew) mActivity).arenaCategoriesList);

        ((TeacherHomeNew) mActivity).fbArena.setVisibility(View.VISIBLE);
        ((TeacherHomeNew) mActivity).fbArena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (toggle) {
                    toggle = false;
                    ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_quizes));
                    ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_articles));
                    ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_flash));
                    ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_audio));
                    ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_video));
                    ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_presentations));
                    ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_polls));
//                    ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_document));

                    ((TeacherHomeNew) mActivity).rvMain.setBackgroundColor(Color.TRANSPARENT);
                    ((TeacherHomeNew) mActivity).rvMain.setAlpha(1f);
                    ((TeacherHomeNew) mActivity).rvMain.setClickable(false);

                    int x = ((TeacherHomeNew) mActivity).rvMain.getChildCount();
                    for (int i = 0; i < x; i++) {
                        if (((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_articles
                                || ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_quizes
                                || ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_flash
                                || ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_audio
                                || ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_video
                                || ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_presentations
                                || ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_document
                                || ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_polls
//                                || ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.tv_askdoubt
                        ) {
                            ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).setVisibility(View.GONE);
                        } else {
                            ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).setVisibility(View.VISIBLE);
                        }
                    }

                } else {
                    toggle = true;
                    for (int i = 0; i < arenaCategoriesList.size(); i++) {
                        switch (arenaCategoriesList.get(i).getArenaCategoryName().toLowerCase()) {
                            case "quizes":
                                ViewAnimation.showIn(((TeacherHomeNew) mActivity).findViewById(R.id.ll_quizes));
                                break;
                            case "articles":
                                ViewAnimation.showIn(((TeacherHomeNew) mActivity).findViewById(R.id.ll_articles));
                                break;
                            case "flash cards":
                                ViewAnimation.showIn(((TeacherHomeNew) mActivity).findViewById(R.id.ll_flash));
                                break;
                            case "audio clips":
                                ViewAnimation.showIn(((TeacherHomeNew) mActivity).findViewById(R.id.ll_audio));
                                break;
                            case "video clips":
                                ViewAnimation.showIn(((TeacherHomeNew) mActivity).findViewById(R.id.ll_video));
                                break;
                            case "presentation":
                                ViewAnimation.showIn(((TeacherHomeNew) mActivity).findViewById(R.id.ll_presentations));
                                break;
                            case "poll":
                                ViewAnimation.showIn(((TeacherHomeNew) mActivity).findViewById(R.id.ll_polls));
                                break;
                        }
                    }
//                    ViewAnimation.showIn(((TeacherHomeNew) mActivity).findViewById(R.id.ll_document));

                    ((TeacherHomeNew) mActivity).rvMain.setBackgroundColor(Color.BLACK);
                    ((TeacherHomeNew) mActivity).rvMain.setAlpha(0.85f);
                    ((TeacherHomeNew) mActivity).rvMain.setClickable(true);

                    int x = ((TeacherHomeNew) mActivity).rvMain.getChildCount();
                    for (int i = 0; i < x; i++) {
                        if (((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_articles
                                || ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_quizes
                                || ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_flash ||
                                ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_audio ||
                                ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_video ||
                                ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_presentations ||
                                ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_polls
//                                ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_document ||
                                || ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.fab_arena
                        ) {
                            ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).setVisibility(View.VISIBLE);
                        } else {
                            ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).setVisibility(View.INVISIBLE);
                        }
                    }


                    ((TeacherHomeNew) mActivity).findViewById(R.id.ll_articles).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mActivity.startActivity(new Intent(mActivity, AddArenaArticle.class));
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });

                    ((TeacherHomeNew) mActivity).findViewById(R.id.ll_quizes).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mActivity.startActivity(new Intent(mActivity, ArAddQuizNew.class));
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });

                    ((TeacherHomeNew) mActivity).findViewById(R.id.ll_flash).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mActivity.startActivity(new Intent(mActivity, ArAddFlashCards.class));
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });

                    ((TeacherHomeNew) mActivity).findViewById(R.id.ll_audio).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mActivity.startActivity(new Intent(mActivity, AddArenaAudioClips.class));
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });

                    ((TeacherHomeNew) mActivity).findViewById(R.id.ll_video).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mActivity, AddArenaVideoClips.class);
                            intent.putExtra("add", 1);
                            mActivity.startActivity(intent);
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });

                    ((TeacherHomeNew) mActivity).findViewById(R.id.ll_polls).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mActivity.startActivity(new Intent(mActivity, ArAddPoll.class));
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });

                    ((TeacherHomeNew) mActivity).findViewById(R.id.ll_presentations).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new MyUtils().alertDialog(3, mActivity, "Oops!", "Feature is disable now. Coming Soon! ",
                                    "Close", "", false);
                        }
                    });
                    ((TeacherHomeNew) mActivity).findViewById(R.id.ll_document).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new MyUtils().alertDialog(3, mActivity, "Oops!", "Feature is disable now. Coming Soon! ",
                                    "Close", "", false);
                        }
                    });

                }
                rotateFab(view, toggle);
            }
        });
        super.onResume();
    }

    @Override
    public void onPause() {
        ((TeacherHomeNew) mActivity).fbArena.setVisibility(View.GONE);
        toggle = false;
        ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_articles));
        ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_quizes));
        ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_flash));
        ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_audio));
        ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_video));
        ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_presentations));
        ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_document));
        ViewAnimation.showOut(((TeacherHomeNew) mActivity).findViewById(R.id.ll_polls));
        rotateFab(((TeacherHomeNew) mActivity).fbArena, toggle);
        ((TeacherHomeNew) mActivity).rvMain.setBackgroundColor(Color.TRANSPARENT);
        ((TeacherHomeNew) mActivity).rvMain.setAlpha(1f);
        ((TeacherHomeNew) mActivity).rvMain.setClickable(false);

        int x = ((TeacherHomeNew) mActivity).rvMain.getChildCount();
        for (int i = 0; i < x; i++) {
            if (((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_articles ||
                    ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_quizes ||
                    ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_flash ||
                    ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_audio ||
                    ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_video ||
                    ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_presentations ||
                    ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_document ||
                    ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.ll_polls )
//                    || ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).getId() == R.id.tv_askdoubt)
            {
                ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).setVisibility(View.GONE);
            } else {
                ((TeacherHomeNew) mActivity).rvMain.getChildAt(i).setVisibility(View.VISIBLE);
            }
        }

        super.onPause();
    }


    class ArenaAdapter extends RecyclerView.Adapter<ArenaAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_arena, parent, false));
        }



        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            if (listGeneralArenas.get(position).getArenaName().contains("~~")){
                holder.tvName.setText(listGeneralArenas.get(position).getArenaName().split("~~")[0]);
                String[] title =  listGeneralArenas.get(position).getArenaName().split("~~");
                for (String s : title){
                    if (s.contains("http")){
                        Picasso.with(mActivity).load(s).placeholder(R.drawable.ic_arena_img)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivDisplayImage);
                    }
                }
            }else{
                holder.tvName.setText(listGeneralArenas.get(position).getArenaName());
            }

            if (listGeneralArenas.get(position).getUserRole().equalsIgnoreCase("S")) {
                holder.tvPostedBy.setText("By " + listGeneralArenas.get(position).getStudentName());
            }else {
                holder.tvPostedBy.setText("By " + listGeneralArenas.get(position).getUserName());
            }

            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listGeneralArenas.get(position).getCreatedDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listGeneralArenas.get(position).getArenaCategory().equalsIgnoreCase("4")){
                        Intent intent = new Intent(mActivity, ArenaAudioDisplay.class);
                        intent.putExtra("audioObj", (Serializable) listGeneralArenas.get(position));
                        mActivity.startActivity(intent);
                    }else if (listGeneralArenas.get(position).getArenaCategory().equalsIgnoreCase("5")){
                        Intent intent = new Intent(mActivity, ArenaVideoDisplay.class);
                        intent.putExtra("videoObj", (Serializable) listGeneralArenas.get(position));
                        mActivity.startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    } else {
                        Intent intent = new Intent(mActivity, ArenaDisplayActivity.class);
                        intent.putExtra("storyObj", (Serializable) listGeneralArenas.get(position));
                        mActivity.startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                }
            });

            if (position == (listGeneralArenas.size()-1)){
                utils.showLoader(mActivity);
                if (hasNextPage){
                    offset = offset+25;
                    getGeneralArenas();
                }else {
                    utils.dismissDialog();
                }
            }

        }

        @Override
        public int getItemCount() {
            return listGeneralArenas.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivDisplayImage;
            TextView tvName,tvPostedBy,tvDate;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_description);
                tvPostedBy = itemView.findViewById(R.id.tv_posted_by);
                tvDate = itemView.findViewById(R.id.tv_date);
                ivDisplayImage = itemView.findViewById(R.id.iv_display_image);

            }
        }
    }

    class QuizzesAdapter extends RecyclerView.Adapter<QuizzesAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            return new QuizzesAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_quiz_arena, parent, false));
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_arena, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//            holder.tvTitle.setBackgroundResource(colors[position % colors.length]);
//            if(listQuizArenas.get(position).getArenaName().contains("~~")){
//                holder.tvTitle.setText(listQuizArenas.get(position).getArenaName().split("~~")[0]);
//            }else {
//                holder.tvTitle.setText(listQuizArenas.get(position).getArenaName());
//            }

            holder.setIsRecyclable(false);
            if (listQuizArenas.get(position).getArenaName().contains("~~")){
                holder.tvName.setText(listQuizArenas.get(position).getArenaName().split("~~")[0]);
                String[] title =  listQuizArenas.get(position).getArenaName().split("~~");
                for (String s : title){
                    if (s.contains("http")){
                        Picasso.with(mActivity).load(s).placeholder(R.drawable.ic_arena_img)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivDisplayImage);
                    }
                }
            }else{
                holder.tvName.setText(listQuizArenas.get(position).getArenaName());
            }

            if (listQuizArenas.get(position).getUserRole().equalsIgnoreCase("S")) {
                holder.tvPostedBy.setText("By " + listQuizArenas.get(position).getStudentName());
            }else {
                holder.tvPostedBy.setText("By " + listQuizArenas.get(position).getUserName());
            }

            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listQuizArenas.get(position).getCreatedDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity, TeacherArenaQuizReview.class);
                    intent.putExtra("item",(Serializable) listQuizArenas.get(position));
                    intent.putExtra("status",listQuizArenas.get(position).getArenaStatus());
                    startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });

            if (position == (listQuizArenas.size()-1)) {
                utils.showLoader(mActivity);
                if (hasQuizNextPage) {
                    quizOffset = quizOffset + 25;
                    getArenas();
                } else {
                    utils.dismissDialog();
                }
            }
        }

        @Override
        public int getItemCount() {
            return listQuizArenas.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

//            TextView tvTitle;
//
//            public ViewHolder(@NonNull View itemView) {
//                super(itemView);
//
//                tvTitle = itemView.findViewById(R.id.tv_title);
//            }

            ImageView ivDisplayImage;
            TextView tvName,tvPostedBy,tvDate;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_description);
                tvPostedBy = itemView.findViewById(R.id.tv_posted_by);
                tvDate = itemView.findViewById(R.id.tv_date);
                ivDisplayImage = itemView.findViewById(R.id.iv_display_image);


            }
        }
    }

    class FlashCardsAdapter extends RecyclerView.Adapter<FlashCardsAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            return new FlashCardsAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_quiz_arena, parent, false));
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_arena, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//            holder.tvTitle.setBackgroundResource(colors[position % colors.length]);
//            if(listFlashArenas.get(position).getArenaName().contains("~~link~~")){
//                String s[] = listFlashArenas.get(position).getArenaName().split("~~link~~");
//                holder.tvTitle.setText(s[0]);
//            }else {
//                holder.tvTitle.setText(listFlashArenas.get(position).getArenaName());
//            }


            holder.setIsRecyclable(false);
            if (listFlashArenas.get(position).getArenaName().contains("~~")){
                holder.tvName.setText(listFlashArenas.get(position).getArenaName().split("~~")[0]);
                String[] title =  listFlashArenas.get(position).getArenaName().split("~~");
                for (String s : title){
                    if (s.contains("http")){
                        Picasso.with(mActivity).load(s).placeholder(R.drawable.ic_arena_img)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivDisplayImage);
                    }
                }
            }else{
                holder.tvName.setText(listFlashArenas.get(position).getArenaName());
            }

            if (listFlashArenas.get(position).getUserRole().equalsIgnoreCase("S")) {
                holder.tvPostedBy.setText("By " + listFlashArenas.get(position).getStudentName());
            }else {
                holder.tvPostedBy.setText("By " + listFlashArenas.get(position).getUserName());
            }

            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listFlashArenas.get(position).getCreatedDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(mActivity, FlashCardsDisplayNew.class);
                intent.putExtra("flashObj", (Serializable)listFlashArenas.get(position));
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            });

            if (position == (listFlashArenas.size()-1)) {
                utils.showLoader(mActivity);
                if (hasFlashNextPage) {
                    flashOffset = flashOffset + 25;
                    getFlashArenas();
                } else {
                    utils.dismissDialog();
                }
            }
        }

        @Override
        public int getItemCount() {
            return listFlashArenas.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

//            TextView tvTitle;
//
//            public ViewHolder(@NonNull View itemView) {
//                super(itemView);
//
//                tvTitle = itemView.findViewById(R.id.tv_title);
//            }

            ImageView ivDisplayImage;
            TextView tvName,tvPostedBy,tvDate;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_description);
                tvPostedBy = itemView.findViewById(R.id.tv_posted_by);
                tvDate = itemView.findViewById(R.id.tv_date);
                ivDisplayImage = itemView.findViewById(R.id.iv_display_image);


            }
        }
    }

    class PollsAdapter extends RecyclerView.Adapter<PollsAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_quiz_arena, parent, false));
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_arena, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {



            holder.setIsRecyclable(false);
            if (pollsList.get(position).getArenaName().contains("~~")){
                holder.tvName.setText(pollsList.get(position).getArenaName().split("~~")[0]);
                String[] title =  pollsList.get(position).getArenaName().split("~~");
                for (String s : title){
                    if (s.contains("http")){
                        Picasso.with(mActivity).load(s).placeholder(R.drawable.ic_arena_img)
                                .fit()
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivDisplayImage);
                    }
                }
            }else{
                holder.tvName.setText(pollsList.get(position).getArenaName());
            }

            if (pollsList.get(position).getUserRole().equalsIgnoreCase("S")) {
                holder.tvPostedBy.setText("By " + pollsList.get(position).getStudentName());
            }else {
                holder.tvPostedBy.setText("By " + pollsList.get(position).getUserName());
            }

            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(pollsList.get(position).getCreatedDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(mActivity, ArPollDisplayActivity.class);
                intent.putExtra("poll", (Serializable)pollsList.get(position));
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            });

            if (position == (pollsList.size()-1)) {
                utils.showLoader(mActivity);
                if (hasPollsNextPage) {
                    pollsOffset = pollsOffset + 25;
                    getPolls();
                } else {
                    utils.dismissDialog();
                }
            }
        }

        @Override
        public int getItemCount() {
            return pollsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivDisplayImage;
            TextView tvName,tvPostedBy,tvDate;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_description);
                tvPostedBy = itemView.findViewById(R.id.tv_posted_by);
                tvDate = itemView.findViewById(R.id.tv_date);
                ivDisplayImage = itemView.findViewById(R.id.iv_display_image);


            }
        }
    }

    void getArenas(){

        JSONObject postObject = new JSONObject();
        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
//            postObject.put("studentId", sObj.getStudentId());
//            postObject.put("sectionId", sObj.getClassCourseSectionId());
            postObject.put("arenaType", "Quiz");
            postObject.put("arenaStatus","1");
            postObject.put("myUserId",tObj.getUserId());
            postObject.put("itemCount","25");
            postObject.put("arenaCategory","1");
            postObject.put("offset",quizOffset +"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        utils.showLog(TAG, "post body"+String.valueOf(postObject));
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetArenas)
                .post(body)
                .build();

        utils.showLog(TAG, "url "+ AppUrls.GetArenas);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        if (quizOffset==0){
                            getFlashArenas();
                        }
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String resp = response.body().string();
                    utils.showLog(TAG,"response "+resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray jsonArray = ParentjObject.getJSONArray("arenaRecords");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaRecord>>() {
                            }.getType();

                            if (jsonArray.length()>0) {

                                //pagination for arena items where if list size is 25 then it has more pages
                                if (quizOffset==0){
                                    listQuizArenas.clear();
                                }

                                listQuizArenas.addAll(gson.fromJson(jsonArray.toString(), type));

                                if (listQuizArenas.size()%25==0){
                                    hasQuizNextPage = true;
                                }else {
                                    hasQuizNextPage = false;
                                }
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvQuizzes.getAdapter().notifyDataSetChanged();
                                    }
                                });
                            }
                            else{
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        llFlashCards.setVisibility(View.GONE);
                                        if (listQuizArenas.size()==0){
                                            llQuizzes.setVisibility(View.GONE);
                                            rvQuizzes.setVisibility(View.GONE);
                                        }

                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        if (quizOffset==0){
                            getFlashArenas();
                        }
                    }
                });
            }
        });

    }

    void getFlashArenas(){

        JSONObject postObject = new JSONObject();
        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
//            postObject.put("studentId", sObj.getStudentId());
//            postObject.put("sectionId", sObj.getClassCourseSectionId());
            postObject.put("arenaType", "Quiz");
            postObject.put("arenaStatus","1");
            postObject.put("myUserId",tObj.getUserId());
            postObject.put("itemCount","25");
            postObject.put("arenaCategory","2");
            postObject.put("offset",flashOffset +"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        utils.showLog(TAG, "post body"+String.valueOf(postObject));
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetArenas)
                .post(body)
                .build();

        utils.showLog(TAG, "url "+ AppUrls.GetArenas);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        if (flashOffset==0){
                            getGeneralArenas();
                        }
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String resp = response.body().string();
                    utils.showLog(TAG,"response "+resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray jsonArray = ParentjObject.getJSONArray("arenaRecords");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaRecord>>() {
                            }.getType();

                            if (jsonArray.length()>0) {

                                //pagination for arena items where if list size is 25 then it has more pages
                                if (flashOffset==0){
                                    listFlashArenas.clear();
                                }

                                listFlashArenas.addAll(gson.fromJson(jsonArray.toString(), type));

                                if (listFlashArenas.size()%25==0){
                                    hasFlashNextPage = true;
                                }else {
                                    hasFlashNextPage = false;
                                }
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvFlashCards.getAdapter().notifyDataSetChanged();
                                    }
                                });
                            }
                            else{
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listFlashArenas.size()==0) {
                                            llFlashCards.setVisibility(View.GONE);
                                            rvFlashCards.setVisibility(View.GONE);
                                        }
//                                        llQuizzes.setVisibility(View.GONE);
//                                        rvQuizzes.setVisibility(View.GONE);

                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        if (flashOffset==0){
                            getGeneralArenas();
                        }
                    }
                });
            }
        });

    }

    void getGeneralArenas(){

        JSONObject postObject = new JSONObject();
        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
//            postObject.put("sectionId", sObj.getClassCourseSectionId());
            postObject.put("arenaType", "General");
            postObject.put("arenaStatus","1");
            postObject.put("myUserId",tObj.getUserId());
            postObject.put("itemCount","25");
            postObject.put("offset",offset +"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetArenas)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        utils.dismissDialog();
                        getPolls();
                        llStories.setVisibility(View.GONE);
                        rvStories.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String resp = response.body().string();
                    utils.showLog(TAG,"response "+resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray jsonArray = ParentjObject.getJSONArray("arenaRecords");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaRecord>>() {
                            }.getType();

                            if (jsonArray.length()>0) {
                                if (offset==0){
                                    listGeneralArenas.clear();
                                }
                                listGeneralArenas.addAll(gson.fromJson(jsonArray.toString(), type));

                                if (listGeneralArenas.size()%25==0){
                                    hasNextPage = true;
                                }else {
                                    hasNextPage = false;
                                }

                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listGeneralArenas.size()>0){
                                            rvStories.getAdapter().notifyDataSetChanged();
                                        }else{
                                            llStories.setVisibility(View.GONE);
                                            rvStories.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                            else{
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listGeneralArenas.size()==0) {
                                            llStories.setVisibility(View.GONE);
                                            rvStories.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getPolls();
                    }
                });
            }
        });
    }

    void getPolls(){
        JSONObject postObject = new JSONObject();
        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
//            postObject.put("sectionId", sectionid);
            postObject.put("arenaStatus","1");
            postObject.put("myUserId",tObj.getUserId());
            postObject.put("itemCount","25");
            postObject.put("arenaCategory","7");
            postObject.put("offset",pollsOffset +"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetArenas)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        llPolls.setVisibility(View.GONE);
                        rvPolls.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String resp = response.body().string();
                    utils.showLog(TAG,"response "+resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray jsonArray = ParentjObject.getJSONArray("arenaRecords");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaRecord>>() {
                            }.getType();

                            if (jsonArray.length()>0) {
                                if (pollsOffset==0){
                                    pollsList.clear();
                                }
                                pollsList.addAll(gson.fromJson(jsonArray.toString(), type));

                                if (pollsList.size()%25==0){
                                    hasPollsNextPage = true;
                                }else {
                                    hasPollsNextPage = false;
                                }

                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (pollsList.size()>0){
                                            rvPolls.getAdapter().notifyDataSetChanged();
                                        }else{
                                            llPolls.setVisibility(View.GONE);
                                            rvPolls.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                            else{
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (pollsList.size()==0) {
                                            llPolls.setVisibility(View.GONE);
                                            rvPolls.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });
    }

    void getTeacherArenas(){
        utils.showLoader(mActivity);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            postObject.put("teacherId",tObj.getUserId());
            postObject.put("arenaStatus","0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"url "+new AppUrls().GetArenasforTeacher);
        utils.showLog(TAG,"url obj "+postObject.toString());


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetArenasforTeacher)
                .post(body)
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();

                utils.showLog(TAG,"response "+resp);
                mActivity.runOnUiThread(() -> utils.dismissDialog());
                if (response.body() != null){
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = jsonObject.getJSONArray("arenaRecords");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaObj>>() {
                            }.getType();

                            listArenas.clear();
                            listArenas.addAll(gson.fromJson(array.toString(),type));

                            for (int i=0;i<listArenas.size();i++){
                                switch (listArenas.get(i).getArenaCategory()){
                                    case "1":
                                        quizCount++;
                                        break;
                                    case "2":
                                        flashCount++;
                                        break;
                                    case "3":
//                                        flashCount++;
                                        break;
                                    case "4":
                                        audioCount++;
                                        break;
                                    case "5":
                                        videoCount++;
                                        break;
                                    case "6":
                                        storyCount++;
                                        break;
                                    case "7":
                                        pollsCount++;
                                        break;
                                }
                            }

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((TextView)teacherBottomArenaView.findViewById(R.id.tv_pending_audio)).setText(audioCount+"");
                                    ((TextView)teacherBottomArenaView.findViewById(R.id.tv_pending_video)).setText(videoCount+"");
                                    ((TextView)teacherBottomArenaView.findViewById(R.id.tv_pending_story)).setText(storyCount+"");
                                    ((TextView)teacherBottomArenaView.findViewById(R.id.tv_pending_flash)).setText(flashCount+"");
                                    ((TextView)teacherBottomArenaView.findViewById(R.id.tv_pending_quiz)).setText(quizCount+"");
                                    ((TextView)teacherBottomArenaView.findViewById(R.id.tv_pending_polls)).setText(pollsCount+"");
                                }
                            });

                        }else if (jsonObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(jsonObject)){ //TODO New Changes
                            String message = jsonObject.getString(AppConst.MESSAGE);
                            mActivity.runOnUiThread(() -> {
                                MyUtils.forceLogoutUser(toEdit, mActivity, message, sh_Pref);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });

            }
        });

    }


    public static boolean rotateFab(final View v, boolean rotate) {
        v.animate().setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .rotation(rotate ? 135f : 0f);
        return rotate;
    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        ((TeacherHomeNew) mActivity).findViewById(R.id.fab_arena).setVisibility(View.GONE);
        super.onDetach();
    }
}