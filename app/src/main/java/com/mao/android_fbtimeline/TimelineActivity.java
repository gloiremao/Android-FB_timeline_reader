package com.mao.android_fbtimeline;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TimelineActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static JSONArray timelineData;

    private AccessToken accessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mRecyclerView = (RecyclerView) findViewById(R.id.cardList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            accessToken = (AccessToken) bundle.get("token");
            Log.d("FB", "Token:" + accessToken.toString());
            //request timeline
            if (accessToken != null){
                requestTimeline();

            }
        }


    }

    private void requestTimeline(){
        //FB SDK API call
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/feed",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        JSONObject obj = response.getJSONObject();
                        try {
                            timelineData = obj.getJSONArray("data");

                            String story = timelineData.getJSONObject(0).getString("story");
                            String msg = timelineData.getJSONObject(0).getString("message");
                            String date = timelineData.getJSONObject(0).getString("created_time");
                            Log.d("FB","Get timeline Data: "+story+" "+ msg + " " +date );
                            // specify an adapter (see also next example)
                            mAdapter = new MyAdapter(timelineData);
                            mRecyclerView.setAdapter(mAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();

    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private JSONArray mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            protected TextView card_date;
            protected TextView card_story;
            protected TextView card_msg;

            public ViewHolder(View v) {
                super(v);
                card_date = (TextView) v.findViewById(R.id.card_date);
                card_story = (TextView) v.findViewById(R.id.card_story);
                card_msg = (TextView) v.findViewById(R.id.card_msg);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(JSONArray myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View card_item = LayoutInflater.from(parent.getContext()).inflate(R.layout.postview,parent,false);

            ViewHolder vh = new ViewHolder(card_item);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            try {
                JSONObject post = mDataset.getJSONObject(position);
                holder.card_date.setText(post.getString("created_time"));
                holder.card_story.setText(post.getString("story"));
                holder.card_msg.setText(post.getString("message"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("FB",""+e.toString());
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length();
        }
    }


}
