package com.pabhinav.zovido.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.customviews.FlickerAnimationView;
import com.pabhinav.zovido.customviews.SceneAnimationView;
import com.pabhinav.zovido.util.FlickerAnimationDrawables;

/**
 * @author pabhinav
 */
public class OnBoardTwoFragmentTab extends Fragment {

    private FlickerAnimationView flickerAnimationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.on_board_fragment_two, container, false);

        /**  Start the flicker animation **/
        new SceneAnimationView((ImageView) rootView.findViewById(R.id.flicker_tab_two), new FlickerAnimationDrawables().getTabsDrawable(), 35);

        return rootView;
    }

    @Override
    public void onDestroy(){
        if(flickerAnimationView != null) {
            flickerAnimationView.stopFlicking();
        }
        super.onDestroy();
    }
}
