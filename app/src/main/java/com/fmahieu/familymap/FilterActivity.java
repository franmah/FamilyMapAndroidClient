package com.fmahieu.familymap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.client.models.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FilterActivity extends AppCompatActivity {
    private final String TAG = "FilterActivity";

    private RecyclerView mRecyclerEventType;
    private OptionAdapter mAdapter;
    private Model mModel = Model.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "activity called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);

        Log.i(TAG, "Creating recycler view...");
        mRecyclerEventType = (RecyclerView) findViewById(R.id.filter_options_recycler_view);
        mRecyclerEventType.setLayoutManager(new LinearLayoutManager(this));

        Log.i(TAG, "creating adapter...");
        mAdapter = new OptionAdapter();
        mRecyclerEventType.setAdapter(mAdapter);

    }

    private class OptionHolder extends RecyclerView.ViewHolder{
        private TextView mEventTypeName;
        private TextView mEventTypeInfo;
        private Switch mEventSwitch;
        private String mEventType;

        public OptionHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.filter_option, parent, false));
            Log.i(TAG,"creating OptionHolder view");

            mEventTypeName = (TextView) itemView.findViewById(R.id.eventType_name);
            mEventTypeInfo = (TextView) itemView.findViewById(R.id.eventType_info);
            mEventSwitch = (Switch) itemView.findViewById(R.id.eventType_switch);

            mEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.i(TAG, "OptionHolder.onCLick: updating event type: " + mEventType);
                    if(mEventSwitch.isChecked()){
                        mModel.updateEventType(mEventType, "t");
                    }
                    else{
                        mModel.updateEventType(mEventType, "f");

                    }
                }
            });
        }

        public void bind(String eventType){
            mEventType = eventType;
            String text = eventType + " " + getString(R.string.events_filter_text);
            mEventTypeName.setText(text);

            text = getString(R.string.filter_by_filter_text) + " " + getString(R.string.EVENTS_filter_text) + " " + eventType.toUpperCase();
            mEventTypeInfo.setText(String.format(text));

            if(mModel.getEventTypes().get(eventType).equals("t")){
                mEventSwitch.setChecked(true);
            }
            else{
                mEventSwitch.setChecked(false);
            }

        }
    }

    private class OptionAdapter extends RecyclerView.Adapter<OptionHolder> {

        private List<String> eventTypes;

        public OptionAdapter() {
           Log.i(TAG,"OptionAdapter constructor called");
           eventTypes = new ArrayList<>(mModel.getEventTypes().keySet());

        }

        @Override
        public OptionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i(TAG,"inflating view");
            LayoutInflater layoutInflater = LayoutInflater.from(FilterActivity.this);

            return new OptionHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(OptionHolder holder, int position) {
            holder.bind(eventTypes.get(position));

        }

        @Override
        public int getItemCount() {
            return eventTypes.size();
        }
    }

}
