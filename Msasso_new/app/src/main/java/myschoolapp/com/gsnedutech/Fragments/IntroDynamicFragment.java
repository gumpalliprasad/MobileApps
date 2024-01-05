package myschoolapp.com.gsnedutech.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class IntroDynamicFragment extends Fragment {

    View viewIntroDynamicFragment;
    Unbinder unbinder;

    @BindView(R.id.animation_view)
    LottieAnimationView animationView;
    @BindView(R.id.tv_header_intro)
    TextView tvHeaderIntro;
    @BindView(R.id.tv_desc_intro)
    TextView tvDescIntro;

    int pos;


    public IntroDynamicFragment(int pos) {
        this.pos = pos;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewIntroDynamicFragment =  inflater.inflate(R.layout.fragment_intro_dynamic, container, false);
        unbinder = ButterKnife.bind(this, viewIntroDynamicFragment);

        init();

        return viewIntroDynamicFragment;
    }

    private void init() {
        switch (pos){
            case 0:

                animationView.setAnimation("book.json");
                animationView.playAnimation();
                tvHeaderIntro.setText("READ");
                tvDescIntro.setText("Voluptate adipisicing in nulla incididunt aute eiusmod. Ullamco in aliquip");
                break;
            case 1:
                animationView.setAnimation("study_line.json");
                animationView.playAnimation();
                tvHeaderIntro.setText("Personalised Learning");
                tvDescIntro.setText("Voluptate adipisicing in nulla incididunt aute eiusmod. Ullamco in aliquip");
                break;
            case 2:
                animationView.setAnimation("analytics.json");
                animationView.playAnimation();
                tvHeaderIntro.setText("Student Analytics");
                tvDescIntro.setText("Voluptate adipisicing in nulla incididunt aute eiusmod. Ullamco in aliquip");
                break;
        }
    }




}