package edu.uci.ics.fablixmobile.ui.movieList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.uci.ics.fablixmobile.data.NetworkManager;
import edu.uci.ics.fablixmobile.data.model.Movie;
import edu.uci.ics.fablixmobile.databinding.ActivityMovielistBinding;
import edu.uci.ics.fablixmobile.ui.singleMovie.SingleMovieActivity;
import edu.uci.ics.fablixmobile.utils.ArrayHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static edu.uci.ics.fablixmobile.data.constant.ServerConstant.baseURL;

public class MovieListActivity extends AppCompatActivity {

    private ListView listView;
    private int pageNum = 1;
    private HashMap<String, String> params;
    private FloatingActionButton nextButton;
    private FloatingActionButton prevButton;
    private final String TAG = "MOVIE-LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovielistBinding binding = ActivityMovielistBinding.inflate(getLayoutInflater());
        listView = binding.list;
        prevButton = binding.prev;
        nextButton = binding.next;

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        params = new HashMap<>();
        params.put("title", title);
        params.put("page", pageNum+"");
        getMovies(params);

        prevButton.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);
        prevButton.setOnClickListener(view -> clickPrev());
        nextButton.setOnClickListener(view -> clickNext());
    }

    private void clickPrev(){
        pageNum -= 1;
        params.put("page", pageNum + "");
        getMovies(params);
    }

    private void clickNext(){
        pageNum += 1;
        params.put("page", pageNum + "");
        getMovies(params);
    }


    private void renderMovies(final ArrayList<Movie> movies, int numPages) {
        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
            Intent singleMovieActivity = new Intent(MovieListActivity.this, SingleMovieActivity.class);
            // activate the list page.
            singleMovieActivity.putExtra("id", movie.getId());
            startActivity(singleMovieActivity);
        });
        Toast.makeText(this, "Page " + pageNum, Toast.LENGTH_SHORT).show();
        prevButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        if (pageNum <= 1){
            prevButton.setVisibility(View.INVISIBLE);
        }

        if (pageNum >= numPages){
            nextButton.setVisibility(View.INVISIBLE);
        }
    }

    private void getMovies(Map<String, String> params) {
        prevButton.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest movieListRequest = new StringRequest(Request.Method.GET, prepareURL(baseURL + "/api/movies", params), response -> {
            try {
                ArrayList<Movie> movies = new ArrayList<>();
                JSONObject res = new JSONObject(response);
                JSONArray movieListArr = res.getJSONArray("result");
                for (int i = 0; i < movieListArr.length(); ++i) {
                    JSONObject movieObj = (JSONObject) movieListArr.get(i);
                    movies.add(new Movie(
                            movieObj.getString("id"),
                            movieObj.getString("title"),
                            Double.parseDouble(movieObj.getString("rating")),
                            (short) movieObj.getInt("year"),
                            movieObj.getString("director"),
                            ArrayHelper.FromJSON(movieObj.getJSONArray("genres")),
                            ArrayHelper.FromJSON(movieObj.getJSONArray("stars"))
                    ));
                }
                renderMovies(movies, res.getInt("numPages"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            String res = new String(error.networkResponse.data, StandardCharsets.UTF_8);
            Log.d(TAG, res);
        });

        // important: queue.add is where the login request is actually sent
        movieListRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(movieListRequest);
    }

    private String prepareURL(String baseURL, Map<String, String> params) {
        String usePrevious = params.getOrDefault("usePrevious", "0");
        String count = params.getOrDefault("count", "10");
        String title = params.getOrDefault("title", "");
        String year = params.getOrDefault("year", "");
        String director = params.getOrDefault("director", "");
        String starName = params.getOrDefault("starName", "");
        String genre = params.getOrDefault("genre", "");
        String page = params.getOrDefault("page", "1");
        String sortBy = params.getOrDefault("sortBy", "rdta");
        String advanced = params.getOrDefault("advanced", "");

        Uri.Builder builder = Uri.parse(baseURL).buildUpon();
        builder.appendQueryParameter("usePrevious", usePrevious);
        builder.appendQueryParameter("count", count);
        builder.appendQueryParameter("title", title);
        builder.appendQueryParameter("year", year);
        builder.appendQueryParameter("director", director);
        builder.appendQueryParameter("starName", starName);
        builder.appendQueryParameter("genre", genre);
        builder.appendQueryParameter("page", page);
        builder.appendQueryParameter("sortBy", sortBy);
        builder.appendQueryParameter("advanced", advanced);
        Log.d(TAG, builder.toString());
        return builder.toString();
    }
}