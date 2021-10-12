package com.example.commentsanalyzer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentsNetworkParser {

    // The callback interface
    interface OnCommentsDownloadedListener {
        void onCommentsDownloaded(ArrayList<Comment> comments);
    }

    OnCommentsDownloadedListener commentsDownloadedListener;

    public void setCommentsDownloadedListener(OnCommentsDownloadedListener commentsDownloadedListener) {
        this.commentsDownloadedListener = commentsDownloadedListener;
    }

    public void parseComments(final Context context, final String query, final int numOfComments) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (!Python.isStarted())
                    Python.start(new AndroidPlatform(context));

                Python py = Python.getInstance();
                PyObject pyobj = py.getModule("get_twitter_comments");
                PyObject result = pyobj.callAttr("get_comments", query, numOfComments);
                String json = result.toString();
                Log.d("!!!", json);
                commentsDownloadedListener.onCommentsDownloaded(parseJsonString(json));
            }
        });
    }

    private ArrayList<Comment> parseJsonString(String json) {
        ArrayList<Comment> resultComments = new ArrayList<>();
        try {
            JSONArray listOfComments = new JSONArray(json);
            for (int i=0; i<listOfComments.length(); i++) {
                JSONObject currentComment = listOfComments.getJSONObject(i);
                Comment comment = new Comment(currentComment.getLong("id"), currentComment.getString("profile url"),
                        currentComment.getString("name"), currentComment.getString("full comment"),
                        currentComment.getInt("prediction"));
                resultComments.add(comment);
            }
        }catch (JSONException ex){ return null; }
        return resultComments;
    }
}
