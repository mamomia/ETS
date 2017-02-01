package com.umt.ameer.ets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umt.ameer.ets.appdata.Constants;
import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.appdata.SessionManager;
import com.umt.ameer.ets.extras.CustomButtonBaseActivity;
import com.umt.ameer.ets.extras.RequestMethod;
import com.umt.ameer.ets.extras.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends CustomButtonBaseActivity {

    private SessionManager session;
    private String TAG = "Login Activity";
    private EditText inputEmail, inputPassword;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(LoginActivity.this);
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        new GlobalSharedPrefs(this);
        session = new SessionManager(this);

        TextView forgetPass = (TextView) findViewById(R.id.tvForgetPasswordLogin);
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivityForResult(new Intent(LoginActivity.this, ForgotPasswordActivity.class), 0);
//                overridePendingTransition(R.anim.lefttoright, R.anim.stable);
            }
        });

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        Button btnSignIn = (Button) findViewById(R.id.btn_signin);

        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        setupUI(findViewById(R.id.rootLayoutLogin));
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        new LoginTask().execute(inputEmail.getText().toString().trim(), inputPassword.getText().toString().trim());
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }

    private class LoginTask extends AsyncTask<String, String, String> {

        private ProgressDialog progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(LoginActivity.this);
            progressBar.setMessage("Signing in, Please wait...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setIndeterminate(true);
            progressBar.setCancelable(false);
            progressBar.show();
        }

        @Override
        protected String doInBackground(String... params) {

            RestClient client = new RestClient(Constants.LOGIN_URL);
            client.AddParam("email", params[0]);
            client.AddParam("password", params[1]);

            try {
                client.Execute(RequestMethod.GET);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String result = client.getResponse();
            try {
                progressBar.dismiss();
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                Log.d("Login result", status);
                if (status.equalsIgnoreCase("success")) {
                    String arrayStr = jsonObject.getString("user_info");
                    JSONObject object3 = new JSONObject(arrayStr);

                    String email = object3.getString("email");

                    String arrayStr2 = jsonObject.getString("area");
                    JSONObject object4 = new JSONObject(arrayStr2);

                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_ID_KEY, object3.getString("id")).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_NAME_KEY, object3.getString("emp_name")).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_EMAIL_KEY, email).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_PHONE_KEY, object3.getString("phone_no")).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_JOIN_DATE_KEY, object3.getString("join_date")).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_STATUS_KEY, object3.getString("emp_status")).apply();

                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_DP_KEY, object3.getString("emp_dp")).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_SUPERIOR_ID_KEY, object3.getString("employee_id")).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_STATUS_BREAK_CONTENT_KEY, object3.getString("break_content")).apply();

                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_RADIUS_KEY, object4.getString("radius")).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_RADIUS_CENTER_KEY, object4.getString("center_point")).apply();

                    session.createLoginSession(email, params[1]);
                    startActivityForResult(new Intent(LoginActivity.this, DashboardActivity.class), 0);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "User does not exist", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}