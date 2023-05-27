package edu.uci.ics.fablixmobile.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import edu.uci.ics.fablixmobile.databinding.ActivitySearchBinding;
import edu.uci.ics.fablixmobile.ui.movieList.MovieListActivity;
import edu.uci.ics.fablixmobile.utils.MakeGradient;

public class SearchActivity extends AppCompatActivity {

    private TextView searchLogo;
    private SearchView searchBox;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable delayedAction;

    private final String TAG = "SEARCH";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        searchLogo = binding.searchLogo;
        MakeGradient.make(searchLogo);

        searchBox = binding.searchBox;
        searchBox.setIconified(false);
        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                handler.removeCallbacks(delayedAction);
                delayedAction = () -> {Log.d("onQueryTextChange", query);};
                // Delay 200 to prevent massive requests
                handler.postDelayed(delayedAction, 400);
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent movieActivity = new Intent(SearchActivity.this, MovieListActivity.class);
                // activate the list page.
                movieActivity.putExtra("title", query);
                startActivity(movieActivity);
                return false;
            }

        });
    }

}