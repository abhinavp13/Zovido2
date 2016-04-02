package com.pabhinav.zovido.adpater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.pabhinav.zovido.R;
import com.pabhinav.zovido.activities.CallFeedbackActivity;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.pojo.CallLogsDataParcel;
import com.pabhinav.zovido.util.Constants;
import com.pabhinav.zovido.util.Utils;

/**
 * @author pabhinav
 */
public class CallLogsRecyclerViewAdapter extends RecyclerView.Adapter<CallLogsRecyclerViewAdapter.CallLogsDataParcelHolder> {

    public Context context;
    
    public CallLogsRecyclerViewAdapter(Context context){
        this.context = context;
    }
    
    @Override
    public CallLogsDataParcelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_log_recycler_view_item, parent, false);

        return new CallLogsDataParcelHolder(view);
    }

    @Override
    public void onBindViewHolder(CallLogsDataParcelHolder holder, int position) {

        CallLogsDataParcel callLogsDataParcel;
        if(ZovidoApplication.getInstance() != null
                && ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().size() > position){
            callLogsDataParcel = ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().get(position);
        } else {
            return;
        }
        
        /** Can't do anything with null call log **/
        if(callLogsDataParcel == null || holder == null){
            return;
        }
        if(holder.name == null
                || holder.phoneNumber == null
                || holder.timestamp == null
                || holder.callDuration == null
                || holder.callType == null
                || holder.loadingTickTextView == null
                || holder.tickImage == null) {
            return;
        }

        /** Call person's Name for tile **/
        if(callLogsDataParcel.getName() == null || callLogsDataParcel.getName().length() == 0){
            holder.name.setText(context.getResources().getString(R.string.unknown));
        } else {
            holder.name.setText(callLogsDataParcel.getName());
        }

        /** Phone number for tile **/
        holder.phoneNumber.setText(callLogsDataParcel.getPhoneNumber());

        /** Timestamp for tile **/
        holder.timestamp.setText(callLogsDataParcel.getCallDate());

        /** Duration for tile **/
        holder.callDuration.setText(callLogsDataParcel.getCallDuration());

        /** Call type for tile **/
        switch (callLogsDataParcel.getCallType()) {
            case Constants.incoming:
                holder.callType.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.incoming_call));
                break;
            case Constants.outgoing:
                holder.callType.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.outgoing_call));
                break;
            default:
                holder.callType.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.missed_call));
                break;
        }

        /** Ticks for tile **/
        holder.loadingTickTextView.setVisibility(View.INVISIBLE);
        if(callLogsDataParcel.isShowTick()){
            holder.tickImage.setVisibility(View.VISIBLE);
            holder.tickImage.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.saved_tick_black));
        } else if(callLogsDataParcel.isUploadedTick()){
            holder.tickImage.setVisibility(View.VISIBLE);
            holder.tickImage.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.saved_tick_blue));
        } else {
            if(ZovidoApplication.getInstance().getIsLoadingTick()){
                holder.loadingTickTextView.setVisibility(View.VISIBLE);
            }
            holder.tickImage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if(ZovidoApplication.getInstance() != null){
            return ZovidoApplication.getInstance().getCallLogsDataParcelArrayListInstance().size();
        }
        return 0;
    }

    public static class CallLogsDataParcelHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView timestamp;
        TextView phoneNumber;
        TextView callDuration;
        ImageView callType;
        ImageView tickImage;
        TextView loadingTickTextView;


        public CallLogsDataParcelHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            phoneNumber = (TextView) itemView.findViewById(R.id.phone_number);
            callDuration = (TextView) itemView.findViewById(R.id.duration);
            callType = (ImageView) itemView.findViewById(R.id.call_type);
            tickImage = (ImageView) itemView.findViewById(R.id.saved_tick_image_view);
            loadingTickTextView = (TextView) itemView.findViewById(R.id.loading_tick_text_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getContext() != null) {
                Intent intent = Utils.prepareCallLogDataParcelIntent(v.getContext(), getPosition(), CallFeedbackActivity.class);
                if(intent != null) {
                    v.getContext().startActivity(intent);
                    ((Activity)v.getContext()).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
            }
        }
    }

}
