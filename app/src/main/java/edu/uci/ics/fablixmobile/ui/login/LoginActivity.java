package edu.uci.ics.fablixmobile.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import edu.uci.ics.fablixmobile.data.NetworkManager;
import edu.uci.ics.fablixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fablixmobile.ui.search.SearchActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static edu.uci.ics.fablixmobile.data.constant.ServerConstant.baseURL;
import static edu.uci.ics.fablixmobile.data.constant.ServerConstant.recaptchaKey;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private CheckBox recaptcha;
    private TextView message;
    private Button loginButton;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */


    private final String TAG = "LOGIN";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        email = binding.email;
        password = binding.password;
        recaptcha = binding.recaptcha;
        message = binding.message;
        loginButton = binding.login;

        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(view -> login());
        recaptcha.setOnClickListener(this::recaptcha);
    }

    @SuppressLint("SetTextI18n")
    private boolean VerifyInputs() {
        if (email.getText().toString().equals("")) {
            message.setText("Please enter your email");
            return false;
        }
        if (password.getText().toString().equals("")) {
            message.setText("Please enter your password");
            return false;
        }
        if (recaptcha.getTag() == null) {
            message.setText("Please do the recaptcha");
            return false;
        }
        return true;
    }

    public void login() {
        if(!VerifyInputs()) return;
        message.setText(""); //empty msg

        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                baseURL + "/api/login",
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.
                    Log.d(TAG, response);
                    //Complete and destroy login activity once successful
                    finish();
                    // initialize the activity(page)/destination
                    Intent searchActivity = new Intent(LoginActivity.this, SearchActivity.class);
                    // activate the list page.
                    startActivity(searchActivity);
                },
                error -> {

                    try {
                        String res = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        JSONObject messageObj = new JSONObject(res);
                        String message = messageObj.getString("message");
                        this.message.setText(message);
                        // Reset captcha
                        recaptcha.setEnabled(true);
                        recaptcha.setChecked(false);
                        Log.d(TAG, res);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("username", email.getText().toString());
                params.put("password", password.getText().toString());
                params.put("g-recaptcha-response", recaptcha.getTag().toString());
                params.put("platform", "mobile");
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent
        loginRequest.setRetryPolicy(new DefaultRetryPolicy( DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ));
        queue.add(loginRequest);
    }


    public void recaptcha(View v) {
        ((CheckBox) v).setChecked(false);
        SafetyNet.getClient(this).verifyWithRecaptcha(recaptchaKey)
                .addOnSuccessListener(this,
                        response -> {
                            // Indicates communication with reCAPTCHA service was
                            // successful.
                            String userResponseToken = response.getTokenResult();
                            if (!userResponseToken.isEmpty()) {
                                recaptcha.setTag(userResponseToken);
                            }
                            recaptcha.setChecked(true);
                            recaptcha.setEnabled(false);
                        })
                .addOnFailureListener(this,
                        e -> {
                            if (e instanceof ApiException) {
                                // An error occurred when communicating with the
                                // reCAPTCHA service. Refer to the status code to
                                // handle the error appropriately.
                                ApiException apiException = (ApiException) e;
                                int statusCode = apiException.getStatusCode();
                                Log.d(TAG, "Error: " + CommonStatusCodes.getStatusCodeString(statusCode));
                            } else {
                                // A different, unknown type of error occurred.
                                Log.d(TAG, "Error: " + e.getMessage());
                            }
                            recaptcha.setChecked(false);
                        });
    }
}