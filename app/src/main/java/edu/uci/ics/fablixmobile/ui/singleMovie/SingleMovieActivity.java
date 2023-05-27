package edu.uci.ics.fablixmobile.ui.singleMovie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fablixmobile.data.NetworkManager;
import edu.uci.ics.fablixmobile.data.model.Movie;
import edu.uci.ics.fablixmobile.databinding.ActivitySingleMovieBinding;
import edu.uci.ics.fablixmobile.utils.ArrayHelper;
import edu.uci.ics.fablixmobile.utils.MakeGradient;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import static edu.uci.ics.fablixmobile.data.constant.ServerConstant.baseURL;

public class SingleMovieActivity extends AppCompatActivity {
    private TextView movieLogo;
    private TextView title;
    private TextView rating;
    private TextView year;
    private TextView director;
    private TextView genres;
    private TextView stars;
    private final String TAG = "SINGLE-MOVIE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySingleMovieBinding binding = ActivitySingleMovieBinding.inflate(getLayoutInflater());
        movieLogo = binding.movieLogo;
        title = binding.title;
        rating = binding.rating;
        year = binding.year;
        director = binding.director;
        genres = binding.genres;
        stars = binding.stars;

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        MakeGradient.make(movieLogo);
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());
        getMovie(id);

    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void renderMovie(final Movie movie) {
        title.setText(movie.getName());
        rating.setText("Rating: "+ String.format("%.2f", movie.getRating()));
        year.setText("Year: "+ movie.getYear());
        director.setText("Director: " +movie.getDirector());
        genres.setText("Genres: " + ArrayHelper.toString(movie.getGenres()));
        stars.setText("Stars: "+ ArrayHelper.toString(movie.getStars()));

    }

    private void getMovie(String id) {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest singleMovieRequest = new StringRequest(Request.Method.GET, prepareURL(baseURL + "/api/movie", id), response -> {
            try {
                JSONObject movieObj = new JSONObject(response);
                Movie movie = new Movie(
                        movieObj.getString("id"),
                        movieObj.getString("title"),
                        movieObj.getDouble("rating"),
                        (short) movieObj.getInt("year"),
                        movieObj.getString("director"),
                        ArrayHelper.FromJSON(movieObj.getJSONArray("genres")),
                        ArrayHelper.FromJSON(movieObj.getJSONArray("stars"))
                );
                // Render the movie or perform further operations
                renderMovie(movie);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            String res = new String(error.networkResponse.data, StandardCharsets.UTF_8);
            Log.d(TAG, res);
        });

        // Set retry policy and add the request to the queue
        singleMovieRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(singleMovieRequest);
    }


    private String prepareURL(String baseURL, String id) {
        Uri.Builder builder = Uri.parse(baseURL).buildUpon();
        builder.appendQueryParameter("id", id);
        Log.d(TAG, builder.toString());
        return builder.toString();
    }
}