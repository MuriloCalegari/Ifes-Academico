package calegari.murilo.sistema_academico;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Switch;
import android.widget.TextView;

import br.com.simplepass.loadingbutton.customViews.CircularProgressImageButton;
import calegari.murilo.sistema_academico.utils.Constants;
import calegari.murilo.sistema_academico.qacad.QAcadLoginTask;
import calegari.murilo.qacadscrapper.utils.User;

public class LoginActivity extends AppCompatActivity {

    private CircularProgressImageButton loginButton;
    private TextView loginButtonText;
    private Switch termsAndConditionsSwitch;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        // Makes link to terms and conditions clickable

        TextView termsAndConditionsText = findViewById(R.id.termsAndConditionsText);
        termsAndConditionsText.setMovementMethod(LinkMovementMethod.getInstance());

        termsAndConditionsSwitch = findViewById(R.id.termsAndConditionsSwitch);
    }

    public void checkCredentials(String username, String password) {
        if(!termsAndConditionsSwitch.isChecked()) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.must_accept_terms_and_conditiosn_first), Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            loginButtonText.setVisibility(View.GONE);
            loginButton.startAnimation(() -> null);

            User user = new User(username, password);

            @SuppressLint("StaticFieldLeak")
			QAcadLoginTask qAcadLoginTask = new QAcadLoginTask(user) {
                @SuppressLint("ApplySharedPref")
                @Override
                protected void onPostExecute(Integer result) {
                    super.onPostExecute(result);
                    loginButton.setClickable(false);

                    if(result == Constants.QAcad.RESULT_LOGIN_INVALID) {

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

                    } else if(result == Constants.QAcad.RESULT_CONNECTION_FAILURE) {

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

                    } else if(result == Constants.QAcad.RESULT_SUCCESS) {

                        Bitmap checkIcon = drawableToBitmap(getDrawable(R.drawable.ic_check_white_24dp));
                        loginButton.setBackground(getDrawable(R.drawable.login_button_round_corners_idle));
                        loginButton.doneLoadingAnimation(getResources().getColor(R.color.ok_color), checkIcon); // Displays success icon
                        new Handler().postDelayed(() -> {
                            loginButton.revertAnimation(() -> {
                                loginButtonText.setText(getString(R.string.success)); // Tells user that their login is valid
                                loginButtonText.setVisibility(View.VISIBLE);

                                SharedPreferences sharedPreferences = getSharedPreferences(Constants.Keys.QACAD_USER_INFO_PREFERENCES, MODE_PRIVATE);
                                sharedPreferences.edit()
                                        .putString(Constants.Keys.QACAD_USERNAME_PREFERENCE, username)
                                        .putString(Constants.Keys.QACAD_PASSWORD_PREFERENCE, password)
                                        .commit();

                                String userFullName = this.qAcadScrapper.getUser().getFullName();

                                if(userFullName != null) {
                                    sharedPreferences = getSharedPreferences(Constants.Keys.APP_GLOBALS_PREFERENCES, MODE_PRIVATE);
                                    sharedPreferences.edit()
                                            .putString(Constants.Keys.APP_USERNAME_PREFERENCE, userFullName.split(" ")[0])
                                            .apply();
                                }

                                // If login is successful, send cookieMap to be used in MainActivity
                                MainActivity.qAcadCookieMap = cookieMap;

                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra(Constants.Keys.IS_FIRST_RUN_EVER, true);
                                startActivity(intent);
                                finish();
                                return null;
                            });
                        }, getResources().getInteger(R.integer.login_button_delay_x_to_error));

                    } else if(result == Constants.QAcad.RESULT_UNKNOWN_ERROR) {

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

            qAcadLoginTask.execute();
        }
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
