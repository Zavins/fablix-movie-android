package edu.uci.ics.fabflixmobile.data;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.data.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<User> login(String username, String password, Context ctx) {

        try {
            // use the same network queue across our application
            final RequestQueue queue = NetworkManager.sharedManager(ctx).queue;
            // request type is POST
            final StringRequest loginRequest = new StringRequest(
                    Request.Method.POST,
                    "http://10.0.2.2:8080/cs122b_spring21_project2_login_cart_example_war/api/login",
                    response -> {
                        // TODO: should parse the json response to redirect to appropriate functions
                        //  upon different response value.
                        Log.d("login.success", response);
                        // initialize the activity(page)/destination
                    },
                    error -> {
                        // error
                        Log.d("login.error", error.toString());
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    // POST request form data
                    final Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);

                    return params;
                }
            };
            // important: queue.add is where the login request is actually sent
            queue.add(loginRequest);

            User fakeUser =
                    new User(
                            java.util.UUID.randomUUID().toString(),
                            username);
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}