package edu.uci.ics.fablixmobile.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fablixmobile.data.NetworkManager;
import edu.uci.ics.fablixmobile.data.model.Movie;
import edu.uci.ics.fablixmobile.databinding.ActivitySearchBinding;
import edu.uci.ics.fablixmobile.ui.movieList.MovieListActivity;
import edu.uci.ics.fablixmobile.ui.singleMovie.SingleMovieActivity;
import edu.uci.ics.fablixmobile.utils.MakeGradient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static edu.uci.ics.fablixmobile.data.constant.ServerConstant.baseURL;

public class SearchActivity extends AppCompatActivity {

    private TextView searchLogo;
    private SearchView searchBox;
    private ListView autocompleteList;
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

        autocompleteList = binding.autocompleteList;

        searchBox = binding.searchBox;
        searchBox.setIconified(false);
        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                handler.removeCallbacks(delayedAction);
                delayedAction = () -> {
                    getAutoCompleteList(query);
                };
                // Delay 200 to prevent massive requests
                handler.postDelayed(delayedAction, 300);
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

    private void getAutoCompleteList(String query){
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest autoCompleteListRequest = new StringRequest(Request.Method.GET, baseURL + "/api/autocomplete?query="+query, response -> {
            try {
                ArrayList<Movie> movies = new ArrayList<>();
                JSONObject res = new JSONObject(response);
                JSONArray movieListArr = res.getJSONArray("result");
                for (int i = 0; i < movieListArr.length(); ++i) {
                    JSONObject movieObj = (JSONObject) movieListArr.get(i);
                    movies.add(new Movie(
                            movieObj.getString("id"),
                            movieObj.getString("title"),
                            (short) movieObj.getInt("year"),
                            movieObj.getString("director")
                    ));
                }
                renderAutoCompleteList(movies);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            String res = new String(error.networkResponse.data, StandardCharsets.UTF_8);
            Log.d(TAG, res);
        });

        // important: queue.add is where the login request is actually sent
        autoCompleteListRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(autoCompleteListRequest);
    }

    private void renderAutoCompleteList (ArrayList<Movie> movies){
        AutoCompleteListViewAdapter adapter = new AutoCompleteListViewAdapter(this, movies);
        autocompleteList.setAdapter(adapter);
        autocompleteList.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
            Intent singleMovieActivity = new Intent(SearchActivity.this, SingleMovieActivity.class);
            // activate the list page.
            singleMovieActivity.putExtra("id", movie.getId());
            startActivity(singleMovieActivity);
        });
    }

}