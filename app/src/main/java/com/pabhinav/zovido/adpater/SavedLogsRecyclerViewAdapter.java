package com.pabhinav.zovido.adpater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.activities.CallDetailsActivity;
import com.pabhinav.zovido.activities.CallFeedbackActivity;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.pojo.CallLogsDataParcel;
import com.pabhinav.zovido.pojo.SavedLogsDataParcel;
import com.pabhinav.zovido.util.Utils;

/**
 * @author pabhinav
 */
public class SavedLogsRecyclerViewAdapter extends RecyclerView.Adapter<SavedLogsRecyclerViewAdapter.SavedLogsDataParcelHolder> {

    public Context context;

    public SavedLogsRecyclerViewAdapter(Context context, View rootView){
        this.context = context;
    }

    @Override
    public SavedLogsDataParcelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_log_recycler_view_item, parent, false);

        return new SavedLogsDataParcelHolder(view, this);
    }

    @Override
    public void onBindViewHolder(SavedLogsDataParcelHolder holder, int position) {

        if(ZovidoApplication.getInstance() == null 
                || ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size() == 0){
            return;
        }
        
        SavedLogsDataParcel savedLogsDataParcel = ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().get(position);

        if(savedLogsDataParcel == null){
            return;
        }
        
        /** Name **/
        if(savedLogsDataParcel.getName() == null){
            holder.name.setText("");
        } else {
            holder.name.setText(savedLogsDataParcel.getName());
        }
        
        /** Phone Number **/
        if(savedLogsDataParcel.getPhoneNumber() == null){
            holder.phoneNumber.setText("");
        } else {
            holder.phoneNumber.setText(savedLogsDataParcel.getPhoneNumber());
        }
        
        /** Date Time **/
        if(savedLogsDataParcel.getDate() == null && savedLogsDataParcel.getTime() == null){
            holder.timestamp.setText("");
        } else if(savedLogsDataParcel.getDate() == null){
            holder.timestamp.setText(savedLogsDataParcel.getTime());
        } else if(savedLogsDataParcel.getTime() == null){
            holder.timestamp.setText(savedLogsDataParcel.getDate());
        } else {
            holder.timestamp.setText(savedLogsDataParcel.getDate() + " " + savedLogsDataParcel.getTime());
        }
        
        /** Duration **/
        if(savedLogsDataParcel.getDuration() == null){
            holder.callDuration.setText("");
        } else {
            holder.callDuration.setText(savedLogsDataParcel.getDuration());
        }
        
        /** Purpose **/
        if(savedLogsDataParcel.getPurpose() == null || savedLogsDataParcel.getPurpose().length() == 0){
            holder.purpose.setText(context.getString(R.string.purpose_empty));
        } else {
            holder.purpose.setText(String.format("%s%s", context.getString(R.string.purpose_colon), savedLogsDataParcel.getPurpose()));
        }

        /** Product **/
        if(savedLogsDataParcel.getProduct() == null || savedLogsDataParcel.getProduct().length() == 0){
            holder.product.setText(context.getString(R.string.product_empty));
        } else {
            holder.product.setText(String.format("%s%s",context.getString(R.string.product_colon) , savedLogsDataParcel.getProduct()));
        }

        /** Sport **/
        if(savedLogsDataParcel.getSport() == null || savedLogsDataParcel.getSport().length() == 0){
            holder.sport.setText(context.getString(R.string.sport_empty));
        } else {
            holder.sport.setText(String.format("%s%s", context.getString(R.string.sport_colon), savedLogsDataParcel.getSport()));
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if(ZovidoApplication.getInstance() == null){
            return 0;
        }
        size =  ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size();
        return size;
    }

    public static class SavedLogsDataParcelHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView timestamp;
        TextView phoneNumber;
        TextView purpose;
        TextView product;
        TextView sport;
        TextView callDuration;
        LinearLayout editLinearLayout;
        RelativeLayout deleteRelativeLayout;
        RelativeLayout modifyRelativeLayout;
        SavedLogsRecyclerViewAdapter savedLogsRecyclerViewAdapter;

        private boolean toggleEditSettings;

        public SavedLogsDataParcelHolder(View itemView, SavedLogsRecyclerViewAdapter savedLogsRecyclerViewAdapter) {
            super(itemView);

            this.savedLogsRecyclerViewAdapter = savedLogsRecyclerViewAdapter;

            toggleEditSettings = false;
            name = (TextView) itemView.findViewById(R.id.saved_user_name);
            timestamp = (TextView) itemView.findViewById(R.id.saved_timestamp);
            phoneNumber = (TextView) itemView.findViewById(R.id.saved_phone_number);
            purpose = (TextView) itemView.findViewById(R.id.purpose_saved_text_vew);
            product = (TextView) itemView.findViewById(R.id.product_saved_text_view);
            sport = (TextView) itemView.findViewById(R.id.sport_saved_text_view);
            callDuration = (TextView) itemView.findViewById(R.id.saved_duration);
            editLinearLayout = (LinearLayout) itemView.findViewById(R.id.edit_linear_layout);
            deleteRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.delete_relative_layout);
            modifyRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.modify_relative_layout);

            deleteRelativeLayout.setOnClickListener(this);
            modifyRelativeLayout.setOnClickListener(this);
            itemView.setOnClickListener(this);


            if(itemView.getContext() != null){
                itemView.setBackgroundDrawable(itemView.getContext().getResources().getDrawable(R.drawable.tile_background));
            }
        }

        @Override
        public void onClick(View v) {

            if(ZovidoApplication.getInstance() == null
                    || ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size() == 0){
                return;
            }

            if(v.getId() == R.id.delete_relative_layout){

                SavedLogsDataParcel savedLogsDataParcel = ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().get(getPosition());

                /** Remove from database **/
                int deletedrows = ZovidoApplication.getInstance().getSqliteDb().deleteSavedLogData(savedLogsDataParcel);

                if(deletedrows == 0){
                    return;
                }

                /** Update tick in call logs **/
                for(int i = 0; i<ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().size(); i++){
                    if(Utils.equalsCallAndSavedLog(ZovidoApplication.getInstance()
                                    .getCallLogsDataParcelArrayListInstance().get(i), savedLogsDataParcel)){

                        CallLogsDataParcel callLogsDataParcel = ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().get(i);
                        callLogsDataParcel.setShowTick(false);

                        ZovidoApplication.getInstance().updateCallLogsDataParcelIfExists(callLogsDataParcel);

                        break;
                    }
                }

                /** Remove from global listing **/
                ZovidoApplication.getInstance().removeSavedLogsDataParcelIfExists(savedLogsDataParcel);

                /** If adapter is empty....show no saved logs **/
                if(ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size() == 0){
                    TextView loadingTextView = (TextView)(((Activity)v.getContext()).findViewById(R.id.saved_log_loading_text_view_behind));
                    loadingTextView.setText("No Saved Logs");
                    loadingTextView.setVisibility(View.VISIBLE);
                    ((Activity)v.getContext()).findViewById(R.id.empty_image).setVisibility(View.VISIBLE);
                    ((Activity)v.getContext()).findViewById(R.id.avloadingIndicatorViewSavedLog).setVisibility(View.INVISIBLE);
                }

                /** update saved log counter in drawer **/
                if(v.getContext() != null || v.getContext() instanceof CallDetailsActivity){
                    ZovidoApplication.getInstance().getDrawerNavigationViewElements((CallDetailsActivity) v.getContext()).getSavedLogCounter().setText(String.valueOf(ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size()));
                }

                /** Update Adapter **/
                if(savedLogsRecyclerViewAdapter != null){
                    savedLogsRecyclerViewAdapter.notifyDataSetChanged();
                }


            } else if(v.getId() == R.id.modify_relative_layout){

                if(v.getContext() != null) {
                    Intent intent = Utils.prepareSavedLogDataParcelIntent(v.getContext(), getPosition(), CallFeedbackActivity.class);
                    if(intent != null) {
                        v.getContext().startActivity(intent);
                        ((Activity)v.getContext()).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    }
                }

                return;
            }

            /** Else it is edit settings clicked **/
            if(!toggleEditSettings){
                editLinearLayout.setVisibility(View.VISIBLE);
            } else {
                editLinearLayout.setVisibility(View.GONE);
            }
            toggleEditSettings = !toggleEditSettings;
        }
    }

}
