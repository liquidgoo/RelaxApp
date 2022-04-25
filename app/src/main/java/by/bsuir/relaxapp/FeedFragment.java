package by.bsuir.relaxapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Array;
import java.util.ArrayList;

public class FeedFragment extends Fragment {

    private static ArrayList<ArrayList<News>> NEWS_SETS = new ArrayList<>();

    private static ListView newsListView;
    private TextView userNameTextViewFeed;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FeedFragment() {   }

    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void findUserNameTextView(View view){
        userNameTextViewFeed = view.findViewById(R.id.userNameTextViewFeed);
    }

    private void fillUserNameTextView(){
        userNameTextViewFeed.setText(ProfileFragment.fullName);
    }

    private void findNewsListView(View view){
        newsListView = view.findViewById(R.id.newsListView);
    }

    private void fillArrayListWithKNews(ArrayList<News> list, int K){
        for (int i = 0; i < K; ++i){
            list.add(new News());
        }
    }

    private void fillNewsSets(){
        if (NEWS_SETS.size() != MainActivity.MOODS_COUNT){
            ArrayList<News> oneNews    = new ArrayList<>(); fillArrayListWithKNews(oneNews, 1);
            ArrayList<News> twoNews    = new ArrayList<>(); fillArrayListWithKNews(twoNews, 2);
            ArrayList<News> threeNews  = new ArrayList<>(); fillArrayListWithKNews(threeNews, 3);
            ArrayList<News> fourNews   = new ArrayList<>(); fillArrayListWithKNews(fourNews, 4);
            ArrayList<News> fiveNews   = new ArrayList<>(); fillArrayListWithKNews(fiveNews, 5);
            ArrayList<News> sixNews    = new ArrayList<>(); fillArrayListWithKNews(sixNews, 6);

            NEWS_SETS.add(oneNews);
            NEWS_SETS.add(twoNews);
            NEWS_SETS.add(threeNews);
            NEWS_SETS.add(fourNews);
            NEWS_SETS.add(fiveNews);
            NEWS_SETS.add(sixNews);
        }
    }

    public static void setAppropriateNewsAdapter(){
        int mood = MainActivity.currentMood;

        NewsAdapter newsAdapter = new NewsAdapter(MainActivity.MAIN_ACTIVITY_CONTEXT, 0, NEWS_SETS.get(mood));
        newsListView.setAdapter(newsAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        MusicFragment.stopMusic();

        fillNewsSets();
        findUserNameTextView(view);
        fillUserNameTextView();
        findNewsListView(view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.MAIN_ACTIVITY_CONTEXT, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.mood_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        MoodAdapter adapter = new MoodAdapter(MainActivity.MAIN_ACTIVITY_CONTEXT);
        recyclerView.setAdapter(adapter);

        setAppropriateNewsAdapter();

        return view;
    }
}