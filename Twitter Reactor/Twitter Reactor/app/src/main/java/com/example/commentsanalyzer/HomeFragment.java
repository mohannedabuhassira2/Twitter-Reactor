package com.example.commentsanalyzer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment implements CommentsNetworkParser.OnCommentsDownloadedListener {

    private static final int NUM_OF_COMMENTS = 100;

    private CardView recycleViewContainer;
    private RecyclerView recyclerView;
    private CommentsAdapter commentsAdapter;
    private ArrayList<Comment> allComments = new ArrayList<>();
    private ArrayList<Comment> firstTenComments = new ArrayList<>();
    private ProgressBar progressBar;
    private EditText input;
    private TextView commentsTitle;
    private TextView resultsTitle;
    private LinearLayout emojiesLinearLayout;
    private AnimatedPieView animatedPieView;
    private TextView posPercentage;
    private TextView negPercentage;

    private double[] pieChartValues = new double[2];
    private double averageNoOfPos;
    private double averageNoOfNeg;

    private static Bundle savedState = new Bundle();

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        restoreState();
        setUpRecycleView(true);
        showPieChart();

        // Handle the user clicking on the right image in the edit text
        input.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (input.getRight() - input.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(!isNetworkAvailable()) {
                            Toast.makeText(getContext(), "A connection error occurred, please try again later.", Toast.LENGTH_SHORT).show();
                            // Remove the focus on the keyboard
                            input.setEnabled(false);
                            input.setEnabled(true);
                            return true;
                        }
                        progressBar.setVisibility(View.VISIBLE);
                        recycleViewContainer.setVisibility(View.GONE);
                        setResultViewsVisibility(false);
                        CommentsNetworkParser networkParser = new CommentsNetworkParser();
                        networkParser.setCommentsDownloadedListener(HomeFragment.this);
                        networkParser.parseComments(getContext(), input.getText().toString(), NUM_OF_COMMENTS);
                        // Remove the focus on the keyboard
                        input.setEnabled(false);
                        input.setEnabled(true);
                        return true;
                    }
                }
                return false;
            }
        });
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.comments_list);
        progressBar = view.findViewById(R.id.progress_bar);
        input = view.findViewById(R.id.input);
        recycleViewContainer = view.findViewById(R.id.comments_list_container);
        animatedPieView = view.findViewById(R.id.pie_chart);
        commentsTitle = view.findViewById(R.id.comments_list_title);
        resultsTitle = view.findViewById(R.id.results_title);
        emojiesLinearLayout = view.findViewById(R.id.emojis_linear_layout);
        posPercentage = view.findViewById(R.id.pos_percentage);
        negPercentage = view.findViewById(R.id.neg_percentage);
    }

    private void restoreState() {
        if(savedState.containsKey("input")) {
            input.setText(savedState.getString("input"));
        }
        if(savedState.containsKey("all_comments")) {
            allComments = savedState.getParcelableArrayList("all_comments");
            firstTenComments = copyFirstElements(10, allComments);
            setResultViewsVisibility(true);
        }
        if(savedState.containsKey("piechart_values")) {
            pieChartValues = savedState.getDoubleArray("piechart_values");
            showPieChart();
            setResultViewsVisibility(true);
        }
        if(!savedState.containsKey("all_comments") && !savedState.containsKey("piechart_values"))
            setResultViewsVisibility(false);
        if(savedState.containsKey("pos_average")) {
            averageNoOfPos = savedState.getDouble("pos_average");
            averageNoOfNeg = 100 - averageNoOfPos;
            posPercentage.setText(averageNoOfPos + "%");
            negPercentage.setText(averageNoOfNeg + "%");
        }
    }

    private void setUpRecycleView(boolean canRestoreLayoutManagerState) {
        commentsAdapter = new CommentsAdapter(getContext(), firstTenComments, allComments);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        if(savedState.containsKey("layout_manager_state") && canRestoreLayoutManagerState)
            recyclerView.getLayoutManager().onRestoreInstanceState(savedState.getParcelable("layout_manager_state"));
        recyclerView.setAdapter(commentsAdapter);
    }

    @Override
    public void onCommentsDownloaded(final ArrayList<Comment> comments) {
        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    allComments = comments;

                    averageNoOfPos = getAveragedNoOfPbs(allComments);
                    DecimalFormat df = new DecimalFormat("#.00");
                    averageNoOfPos = Double.parseDouble(df.format(averageNoOfPos*100));
                    averageNoOfNeg = 100 - averageNoOfPos;
                    posPercentage.setText(averageNoOfPos + "%");
                    negPercentage.setText(averageNoOfNeg + "%");

                    firstTenComments = copyFirstElements(10, comments);
                    progressBar.setVisibility(View.GONE);
                    recycleViewContainer.setVisibility(View.VISIBLE);
                    setUpRecycleView(false);
                    pieChartValues = new double[]{averageNoOfNeg, averageNoOfPos};
                    showPieChart();
                    setResultViewsVisibility(true);
                }
            });
        }
    }

    private void setResultViewsVisibility(boolean isVisible) {
        recycleViewContainer.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        animatedPieView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        if(isVisible)
            showPieChart();
        commentsTitle.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        resultsTitle.setVisibility(isVisible ? View.VISIBLE: View.GONE);
        emojiesLinearLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        savedState.putString("input", input.getText().toString());
        if(allComments.size()!=0) {
            savedState.putParcelableArrayList("all_comments", allComments);
            Parcelable layoutManagerState = recyclerView.getLayoutManager().onSaveInstanceState();
            savedState.putParcelable("layout_manager_state", layoutManagerState);
            savedState.putDoubleArray("piechart_values", pieChartValues);
            savedState.putDouble("pos_average", averageNoOfPos);
        }
    }

    private void showPieChart() {
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        config.addData(new SimplePieInfo(pieChartValues[0], Color.parseColor("#fc2c03"), "Negative"));
        config.addData(new SimplePieInfo(pieChartValues[1], Color.parseColor("#24fc03"), "Positive"));
        config.duration(750);
        config.drawText(true);
        config.strokeMode(false);
        config.textSize(52);
        config.textMargin(8);
        config.splitAngle(1);
        animatedPieView.applyConfig(config);
        animatedPieView.start();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private ArrayList<Comment> copyFirstElements(int n, ArrayList<Comment> input) {
        ArrayList<Comment> des = new ArrayList<>();
        for (int i = 0; i <n ; i++)
            des.add(input.get(i));
        return des;
    }

    private double getAveragedNoOfPbs(ArrayList<Comment> allComments) {
        double averageNoOfPos = 0;
        for (Comment comment: allComments)
            averageNoOfPos += comment.getPrediction();
        averageNoOfPos /= allComments.size();
        return averageNoOfPos;
    }
}
