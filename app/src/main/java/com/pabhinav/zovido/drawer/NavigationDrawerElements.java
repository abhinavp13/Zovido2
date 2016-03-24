package com.pabhinav.zovido.drawer;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.TextView;

import com.pabhinav.zovido.R;

/**
 * @author pabhinav
 */
public class NavigationDrawerElements {

    private Context context;
    private View headerView;
    private TextView savedLogsCounterTextView;
    private TextView recentLogsCounterTextView;
    private TextView agentNameTextView;

    public NavigationDrawerElements(Context context){
        this.context = context;
    }

    public void getHeaderView(){
        if(headerView == null){
            NavigationView navigationView = (NavigationView)((Activity) context).findViewById(R.id.nav_view);
            headerView = navigationView.getHeaderView(0);
        }
    }

    public TextView getSavedLogCounter(){

        if(savedLogsCounterTextView == null) {
            if (headerView == null) {
                getHeaderView();
            }
            savedLogsCounterTextView = (TextView) headerView.findViewById(R.id.saved_count_text_view);
        }
        return savedLogsCounterTextView;
    }

    public TextView getRecentLogCounter(){

        if(recentLogsCounterTextView == null) {
            if (headerView == null) {
                getHeaderView();
            }
            recentLogsCounterTextView = (TextView) headerView.findViewById(R.id.recent_count_text_view);
        }
        return recentLogsCounterTextView;
    }

    public TextView getAgentNameTextView(){
        if(agentNameTextView == null){
            if(headerView == null){
                getHeaderView();
            }
            agentNameTextView = (TextView) headerView.findViewById(R.id.agent_name_text_view);
        }
        return agentNameTextView;
    }

}
