package portfolio.trachtenberg;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Random;

import portfolio.math.trachtenberg.R;

public class LearnActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String FIRSTCHAR_REMAINDER = "firstchar_remainder";
    private static final String ANSWER_STRING = "answer_string";
    private static final String LEARN_STEP = "learn_step";
    private static final String INDEX_COUNT = "index_count";
    private static final String EQUATION = "equation";
    private static final String ANSWER_PROGRESSION = "answer_progression";
    private static final String HINTHELP = "hinthelp";
    public static final String HINT = "hint";
    private Button nextButton;
    private Button button;
    private Button button1;
    private Button button2;
    private Button button3;
    private TextView explanationText;
    private TextView equationText;
    private TextView bottomArrow;
    private TextView answerText;
    private TextView equationTextView;
    private TextView buttonResultTextView;
    private TextView hintResultTextView;
    private TextView answerProgress;
    private TextView hintQuestionTextView;
    private int learnPage;
    private int answerIndex;
    private int indexCount;
    private int move;
    private int remainderHint;
    private int moveCount;
    private String answerString;
    private String equationString;
    public SharedPreferences sharedPreferences;
    private Spannable sb;
    private Random mRnd;
    private boolean tT;

// The onCreate method sets up the activity on creation.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the device display
        Display d = getWindowManager().getDefaultDisplay();
        // Set the content view based on the device rotation
        if (d.getRotation() == Surface.ROTATION_0 || d.getRotation() == Surface.ROTATION_180) {
            setContentView(R.layout.activity_main_rotated);
        } else {
            setContentView(R.layout.activity_main);
        }

        // Find views by ID
        nextButton = findViewById(R.id.next_button);
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setClickable(true);
        explanationText = findViewById(R.id.explanation_text);
        equationText = findViewById(R.id.equation_text);
        bottomArrow = findViewById(R.id.bottom_arrow);
        answerText = findViewById(R.id.answer_text);

        // Set the text of explanationText to the appropriate string from the resources
        explanationText.setText(getResources().getStringArray(R.array.explanationTextList)[learnPage]);

        tT = false;
        if (findViewById(R.id.button) != null) {
            // If there is a button, set tT to true and find more views by ID
            tT = true;
            button = findViewById(R.id.button);
            button1 = findViewById(R.id.button2);
            button2 = findViewById(R.id.button3);
            button3 = findViewById(R.id.button4);
            equationTextView = findViewById(R.id.equation_textView);
            answerProgress = findViewById(R.id.answer_progression);
            buttonResultTextView = findViewById(R.id.button_result);
            hintResultTextView = findViewById(R.id.hint_result);
            hintQuestionTextView = findViewById(R.id.hint_question);

            // Set up the ad view and initialize some variables
            MobileAds.initialize(this, "ca-app-pub-6173744039687391~7033034874");
            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            mRnd = new Random();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }

        if (savedInstanceState != null) {
            if (tT) {
                // If tT is true and there is a saved instance state, retrieve and set some variables
                indexCount = savedInstanceState.getInt(INDEX_COUNT, 0);
                equationString = savedInstanceState.getString(EQUATION, "");
                equationTextView.setText(equationString);
                answerString = savedInstanceState.getString(ANSWER_STRING);
                if (answerString != null) {
                    answerProgress.setText(answerString.substring(answerString.length() - 1 - indexCount));
                }
                int remainder = sharedPreferences.getInt(FIRSTCHAR_REMAINDER, 0);
                if (remainder > 0) {
                    String s = remainder + " + ";
                    hintResultTextView.setText(s);
                }
                buttonQuestion();
                setMove();
            }
            learnPage = savedInstanceState.getInt(LEARN_STEP, 0);
            setLearnSteep(2);
        } else if (tT) {
            // If tT is true and there is no saved instance state, call the getEquation method
            getEquation();
        }
    }

    // Create the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (tT) {
            // If tT is true, inflate the settings menu
            getMenuInflater().inflate(R.menu.menu_settings, menu);
        } else {
            // Otherwise, inflate the practice menu
            getMenuInflater().inflate(R.menu.menu_practice, menu);
        }
        return true;
    }

    // Handle menu item clicks
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.practice_menu):
                // If the practice menu item is clicked, start the PracticeActivity
                startActivity(new Intent(this, PracticeActivity.class));
                break;
            case (R.id.settings_menu_tablet):
                // If the settings menu item is clicked, start the SettingsActivity
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // This method determines which step in learning/training to take based on the View passed to it.
    public void learnTrach(View v) {
        // If the tag associated with the View is "add", go to the next page, otherwise go the previous page.
        if (v.getTag().toString().equals("add")) {
            // Go to the next page.
            setLearnSteep(0);
        } else {
            // Go back a page.
            setLearnSteep(1);
        }
    }

    // This method sets the progress in the learning/training program based on num.
    public void setLearnSteep(int num) {
        // If num is 0, increment the learning page.
        if (num == 0) {
            learnPage++;
        } else if (num == 1) {
            // Otherwise, decrement the learning page if it is greater than zero. Additionally, make the "next" button clickable.
            if (learnPage > 0) {
                learnPage--;
                nextButton.setClickable(true);
            }
        }
        // Update the learning page contents if we are still within the allowed page range.
        if (learnPage < 17) {
            // Set the associated text for various views and update the accent color for certain letters.
            explanationText.setText(getResources().getStringArray(R.array.explanationTextList)[learnPage]);
            answerText.setText(getResources().getStringArray(R.array.answerTextList)[learnPage]);
            bottomArrow.setText(getResources().getStringArray(R.array.bottomArrowList)[learnPage]);
            if (!answerText.getText().toString().equals("")) {
                setAccentColor();
                equationText.setText(sb);
            }
        } else {
            // If we are at the end of the learning/training program, set the "next" button to unclickable and start the Practice Activity.
            nextButton.setClickable(false);
            if (!tT) {
                startActivity(new Intent(this, PracticeActivity.class));
            }
        }
    }

    // Determines the color of certain characters in the equationText view.
    public void setAccentColor() {
        String string = "123456 x 789";
        String answerString = answerText.getText().toString();
        String[] numberString = answerString.split(" of ");
        String letterString = numberString[1].substring(0, 1);
        String second = numberString[1].substring(4, 5);
        sb = new SpannableString(string);
        sb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)),
                string.indexOf(letterString), string.indexOf(letterString) + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)),
                string.indexOf(second), string.indexOf(second) + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
