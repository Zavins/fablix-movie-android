package edu.uci.ics.fablixmobile.ui.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.uci.ics.fablixmobile.R;
import edu.uci.ics.fablixmobile.data.model.Movie;

import java.util.ArrayList;

public class AutoCompleteListViewAdapter extends ArrayAdapter<Movie> {
    private final ArrayList<Movie> movies;

    // View lookup cache
    private static class ViewHolder {
        TextView title;
    }

    public AutoCompleteListViewAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.autocomplete_row, movies);
        this.movies = movies;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the movie item for this position
        Movie movie = movies.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.autocomplete_row, parent, false);
            viewHolder.title = convertView.findViewById(R.id.title);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.title.setText(movie.getName() +" (" + movie.getYear()+")");
        return convertView;
    }
}