package calegari.murilo.ifes_academico;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Handler;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import br.com.simplepass.loadingbutton.customViews.CircularProgressImageButton;
import calegari.murilo.ifes_academico.utils.Constants;
import calegari.murilo.ifes_academico.utils.QAcadIntegration.QAcadCheckLoginTask;
import calegari.murilo.qacadscrapper.utils.User;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private CoordinatorLayout coordinatorLayout;
    private CircularProgressImageButton loginButton;
    private TextView loginButtonText;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        coordinatorLayout = findViewById(R.id.rootCoordinatorLayout);

        TextInputEditText usernameText = findViewById(R.id.usernameEditText);
        TextInputEditText passwordText = findViewById(R.id.passwordEditText);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setBackground(getDrawable(R.drawable.login_button_round_corners_idle));
        loginButtonText = findViewById(R.id.loginButtonText);

        passwordText.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkCredentials(usernameText.getText().toString(), passwordText.getText().toString());
                return true;
            }
            return false;
        });

        loginButton.setOnClickListener((view) -> checkCredentials(usernameText.getText().toString(), passwordText.getText().toString()));

    }

    public void checkCredentials(String username, String password) {
        loginButtonText.setVisibility(View.GONE);
        loginButton.startAnimation(() -> null);

        User user = new User(username, password);

        @SuppressLint("StaticFieldLeak")
        QAcadCheckLoginTask qAcadCheckLoginTask = new QAcadCheckLoginTask(user) {
            @SuppressLint("ApplySharedPref")
            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                loginButton.setClickable(false);

                // TODO Wait for an update of LoadingButtonAndroid library that will fix calling doneLoadingAnimation more than once with different bitmaps

                if(result == Constants.RESULT_LOGIN_INVALID) {

                    Bitmap errorIcon = drawableToBitmap(getDrawable(R.drawable.ic_error_white_24dp));
                    loginButton.setBackground(getDrawable(R.drawable.login_button_round_corners_error));
                    loginButton.doneLoadingAnimation(getResources().getColor(R.color.danger_color), errorIcon); // Displays error icon
                    new Handler().postDelayed(() -> {
                        loginButton.revertAnimation(() -> {
                            loginButtonText.setText(getString(R.string.invalid_login)); // Tells user that its login is invalid
                            loginButtonText.setVisibility(View.VISIBLE);
                            setLoginButtonToIdle();
                            return null;
                        });
                    }, getResources().getInteger(R.integer.login_button_delay_x_to_error));

                } else if (result == Constants.RESULT_CONNECTION_FAILURE) {

                    Bitmap errorIcon = drawableToBitmap(getDrawable(R.drawable.ic_error_white_24dp));
                    loginButton.setBackground(getDrawable(R.drawable.login_button_round_corners_error));
                    loginButton.doneLoadingAnimation(getResources().getColor(R.color.danger_color), errorIcon); // Displays error icon
                    new Handler().postDelayed(() -> {
                        loginButton.revertAnimation(() -> {
                            loginButtonText.setText(getString(R.string.connection_failure)); // Tells user that a connection failure has happened
                            loginButtonText.setVisibility(View.VISIBLE);

                            setLoginButtonToIdle();
                            return null;
                        });
                    }, getResources().getInteger(R.integer.login_button_delay_x_to_error));

                } else if (result == Constants.RESULT_SUCCESS) {

                    Bitmap checkIcon = drawableToBitmap(getDrawable(R.drawable.ic_check_white_24dp));
                    loginButton.setBackground(getDrawable(R.drawable.login_button_round_corners_idle));
                    loginButton.doneLoadingAnimation(getResources().getColor(R.color.ok_color), checkIcon); // Displays success icon
                    new Handler().postDelayed(() -> {
                        loginButton.revertAnimation(() -> {
                            loginButtonText.setText(getString(R.string.success)); // Tells user that their login is valid
                            loginButtonText.setVisibility(View.VISIBLE);

                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.USER_INFO_PREFERENCES, MODE_PRIVATE);
                            sharedPreferences.edit()
                                    .putString(Constants.USERNAME_PREFERENCE, username)
                                    .putString(Constants.PASSWORD_PREFERENCE, password)
                                    .commit();

                            // If login is successful, send cookieMap to be used in MainActivity
                            MainActivity.qAcadCookieMap = cookieMap;

                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                            finish();
                            return null;
                        });
                    }, getResources().getInteger(R.integer.login_button_delay_x_to_error));

                } else if (result == Constants.RESULT_UNKNOWN_ERROR) {

                    Bitmap errorIcon = drawableToBitmap(getDrawable(R.drawable.ic_error_white_24dp));
                    loginButton.setBackground(getDrawable(R.drawable.login_button_round_corners_error));
                    loginButton.doneLoadingAnimation(getResources().getColor(R.color.danger_color), errorIcon); // Displays error icon
                    new Handler().postDelayed(() -> {
                        loginButton.revertAnimation(() -> {
                            loginButtonText.setText(getString(R.string.unknown_error)); // Tells user that an unknown error has occurred
                            loginButtonText.setVisibility(View.VISIBLE);

                            setLoginButtonToIdle();
                            return null;
                        });
                    }, getResources().getInteger(R.integer.login_button_delay_x_to_error));

                }
            }
        };

        qAcadCheckLoginTask.execute();
    }

    private void setLoginButtonToIdle() {
        new Handler().postDelayed(() -> {
                    loginButton.setBackground(getDrawable(R.drawable.login_button_round_corners_idle));
                    loginButton.setClickable(true);
                    loginButtonText.setText(getString(R.string.login));
                },
                getResources().getInteger(R.integer.login_button_delay_error_to_idle)
        );
    }


    // Thanks to https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
