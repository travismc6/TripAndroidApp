package com.bitewolf.tripsie;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Travis on 2/17/2015.
 */
public class TripCommentListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<TripComment> comments;

    public TripCommentListAdapter(Activity activity, List<TripComment> comments) {
        this.activity = activity;
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int location) {
        return comments.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        TripComment comment = comments.get(position);


        if (convertView == null)
            convertView = inflater.inflate(R.layout.comment_row, null);

        TextView commentTv = (TextView)convertView.findViewById(R.id.comment_row_comment);
        commentTv.setText(Html.fromHtml("<b>" + comment.Username + ":</b> " + comment.Comment));

        TextView date = (TextView)convertView.findViewById(R.id.comment_row_time);
        date.setText(comment.toString());

        return convertView;
    }

}
