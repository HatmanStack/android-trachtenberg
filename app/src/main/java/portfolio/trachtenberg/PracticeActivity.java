package portfolio.trachtenberg;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Arrays;
import java.util.Random;

import portfolio.math.trachtenberg.R;


public class PracticeActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String INDEX_COUNT = "index_count";
    private static final String EQUATION = "equation";
    private static final String ANSWER_PROGRESSION = "answer_progression";
    private static final String ANSWER_STRING = "answer_string";
    public static final String HINT = "hint";
    private static final String HINTHELP = "hinthelp";
    private static final String FIRSTCHAR_REMAINDER = "firstchar_remainder";
    private Button button;
    private Button button1;
    private Button button2;
    private Button button3;
    private TextView equationTextView;
    private TextView buttonResultTextView;
    private TextView hintResultTextView;
    private TextView answerProgress;
    private TextView hintQuestionTextView;
    private String answerString;
    private String equationString;
    private int answerIndex;
    private int indexCount;
    private int move;
    private int remainderHint;
    private int moveCount;
    private Random mRnd;
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get the device display
        Display d = getWindowManager().getDefaultDisplay();
        
        // Set the content view based on the device rotation
        setContentView(R.layout.activity_practice);
        
        // Find the necessary views and initialize variables
        button = findViewById(R.id.button);
        button1 = findViewById(R.id.button2);
        button2 = findViewById(R.id.button3);
        button3 = findViewById(R.id.button4);
        equationTextView = findViewById(R.id.equation_textView);
        answerProgress = findViewById(R.id.answer_progression);
        buttonResultTextView = findViewById(R.id.button_result);
        hintResultTextView = findViewById(R.id.hint_result);
        hintQuestionTextView = findViewById(R.id.hint_question);
        MobileAds.initialize(this, "ca-app-pub-6173744039687391~7033034874");
        //AdView mAdView = findViewById(R.id.adView);                       DISABLED ADS
        //AdRequest adRequest = new AdRequest.Builder().build();            DISABLED ADS
        //mAdView.loadAd(adRequest);                                        DISABLED ADS
        mRnd = new Random();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        
        // Retrieve instance state (if any) and update views accordingly
        if (savedInstanceState != null) {
            indexCount = savedInstanceState.getInt(INDEX_COUNT, 0);
            equationString = savedInstanceState.getString(EQUATION, "");
            equationTextView.setText(equationString);
            answerString = savedInstanceState.getString(ANSWER_STRING);
            if (answerString != null) {
                answerProgress.setText(answerString.substring(answerString.length() - 1 - indexCount));
            }
            int remainder = sharedPreferences.getInt(FIRSTCHAR_REMAINDER, 0);
            if(remainder > 0){
                String s = remainder + " + ";
                hintResultTextView.setText(s);
            }
            buttonQuestion();
            setMove();
        } else {
            getEquation();
        }
    
        // Set up the back button display
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Create the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Handle menu item clicks
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.learn_menu):
                // If the practice menu item is clicked, start the PracticeActivity
                startActivity(new Intent(this, LearnActivity.class));
                break;
            case (R.id.settings_menu):
                // If the settings menu item is clicked, start the SettingsActivity
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(sharedPreferences.getBoolean(HINT, false)){
            hintResultTextView.setVisibility(View.VISIBLE);
            hintQuestionTextView.setVisibility(View.VISIBLE);
        } else {
            hintQuestionTextView.setVisibility(View.INVISIBLE);
            hintResultTextView.setVisibility(View.INVISIBLE);
        }
    }

    // Gets a new equation to display to the user.
    public void getEquation() {
        int[] answer = operatorEquation();
        indexCount = 0;
        move = 0;
        moveCount = 1;
        answerProgress.setText("");
        answerString = String.valueOf(answer[0] * answer[1]);
        equationString = answer[0] + " * " + answer[1];
        equationTextView.setText(equationString);
        buttonQuestion();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putInt(INDEX_COUNT, 0).apply();
        setMove();
    }

    public int[] operatorEquation(){
        int[] components = new int[2];
        components[0] = mRnd.nextInt(10000);
        components[1] = mRnd.nextInt(1000);
        while (components[0] < 1000 || components[1] < 100) {
            components[1]= mRnd.nextInt(10000);
            components[0] = mRnd.nextInt(1000);
        }
        return components;
    }


    // Method for setting up four random answer choices for a multiple choice question
    public void buttonQuestion() {
        answerIndex = mRnd.nextInt(4); // Choose a random index for the correct answer
        int[] buttonAnswers = new int[4]; // Create an array for the answer choices
        Arrays.fill(buttonAnswers, -1); // Initialize array with -1
        int incorrect; 
        int answer = -1;
        for (int i = 0; i < 4; i++) { // Loop through the answer choices
            if (i == answerIndex) {
                answer = Integer.parseInt(
                    answerString.substring(answerString.length() - 1 - indexCount, answerString.length() - indexCount));
                buttonAnswers[i] = answer; // Assign correct answer to indexed button
            }
        }
        for (int i = 0; i < buttonAnswers.length; i++) { // Loop through the answer choices again
            incorrect = mRnd.nextInt(10);
            while (incorrect == buttonAnswers[0] || incorrect == buttonAnswers[1] ||
                    incorrect == buttonAnswers[2] || incorrect == buttonAnswers[3]) { // Ensure that incorrect answer is distinct from the correct answer
                incorrect = mRnd.nextInt(10);
            }
            if (buttonAnswers[i] != answer && i != answerIndex) { // Check if this button is neither assigned nor the correct answer
                buttonAnswers[i] = incorrect; // Assign random incorrect answer to button
            }
        }
        // Update button text with assigned answers
        button.setText(String.valueOf(buttonAnswers[0]));
        button1.setText(String.valueOf(buttonAnswers[1]));
        button2.setText(String.valueOf(buttonAnswers[2]));
        button3.setText(String.valueOf(buttonAnswers[3]));
    }

    // Method for setting up hints for a multiplication practice session
    public void practiceHint(int fsIndex, int ssIndex) {
        // Extract values to multiply
        String firstString = String.valueOf(equationTextView.getText().toString().split(" * ")[0]);
        String secondString = String.valueOf(equationTextView.getText().toString().split(" * ")[2]);

        // Extract digits of the values
        String firstStringChar = String.valueOf(firstString.charAt(fsIndex));
        String secondStringChar = String.valueOf(secondString.charAt(ssIndex));

        // Generate question text for hint
        String questionString = firstStringChar + " * " + secondStringChar;
        hintQuestionTextView.setText(questionString);

        // Highlight digits in original equation for hint if preference is set to true
        if (sharedPreferences.getBoolean(HINT, false)) {
            SpannableString sbFirst = new SpannableString(equationTextView.getText().toString());
            sbFirst.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), fsIndex,
                    fsIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sbFirst.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), ssIndex + 7,
                    ssIndex + 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            equationTextView.setText(sbFirst);
        } else {
            equationTextView.setText(equationString);
        }

        // Compute the correct result and generate a hint string
        String result = Integer.valueOf(firstStringChar) * Integer.valueOf(secondStringChar) + "";
        if (result.length() == 1) {
            result = "0" + result;
        }
        String charResultString;
        if (Arrays.asList(0, 1, 3, 4, 6, 8, 9, 11, 13, 16, 18, 21).contains(move)) {
            charResultString = result.substring(1);
        } else {
            charResultString = result.substring(0, 1);
        }
        remainderHint += Integer.valueOf(charResultString);

        // Append hint string to current hint result text
        charResultString = charResultString + " + ";
        if (Arrays.asList(0, 3, 8, 14, 19, 22, 23).contains(move)) {
            charResultString = charResultString.replace(" + ", "");
        }
        charResultString = hintResultTextView.getText().toString() + charResultString;
        hintResultTextView.setText(charResultString);

        // Update move counter
        move++;
    }

    // Method to move to the next hint in a multiplication practice session
    public void nextHint(View v) {
        if (moveCount > move) {
            setIndex();
        }
    }

    //This method sets the value of fsIndex and ssIndex based on the value of "move".
    public void setIndex() {
        int fsIndex = 3; // Initialize fsIndex to 3
        int ssIndex = 2; // Initialize ssIndex to 2
        
        int[] movesIndexes = {2, 2, 2, 1, 2, 1, 0, 2, 1, 0, 1, 0, 0}; // An array of move indexes
        
        // Iterate through the movesIndexes array
        for (int i = 0; i < movesIndexes.length; i++) {
            // If the value of "move" matches the current index or the current index plus 4
            if (move == i || move == i + 4) {
                // If the index is equal to the current index plus 4 and a hint has been seen, skip this iteration
                if (move == i + 4 && sharedPreferences.getBoolean(HINT, false)) continue;
                
                // If the index is equal to 1 and the hint help has not been seen, show a toast message
                if (move == 1 && sharedPreferences.getBoolean(HINTHELP, true)) {
                    Toast.makeText(this, "Touch hint to get next Step", Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putBoolean(HINTHELP, false).apply();
                }
                
                // Update fsIndex and ssIndex based on the current index
                fsIndex = i > movesIndexes.length - 3 ? movesIndexes[movesIndexes.length - 2] - i % 2 : movesIndexes[i];
                ssIndex = i < 6 ? 2 : i < 9 ? 1 : i < 11 ? 0 : movesIndexes[i + 1];
                
                // Call the practiceHint method with the updated fsIndex and ssIndex values
                practiceHint(fsIndex, ssIndex);
                
                // Exit the loop
                break;
            }
        }
    }

    //This method sets the value of "move" based on the value of indexCount.
    public void setMove() {
        int[] movesCount = {0, 4, 9, 15, 20, 23, 24}; // An array of move counts
        
        // If the value of indexCount is greater than 0
        if (indexCount > 0) {
            // Iterate through the movesCount array starting from the second element
            for (int i = 1; i < movesCount.length; i++) {
                // If the value of indexCount matches the current element
                if (indexCount == i) {
                    // Set the value of "move" based on the previous element in the movesCount array
                    move = movesCount[i - 1] != 0 ? movesCount[i - 1] : 1;
                    
                    // Set the value of moveCount based on the current element in the movesCount array minus 1
                    moveCount = movesCount[i] - 1;
                    
                    // Exit the loop
                    break;
                }
            }
        }
        
        // If a hint has been seen, call the setIndex method
        if (sharedPreferences.getBoolean(HINT, false)) {
            setIndex();
        }
    }

    public void chooseAnswer(View v) {
        // calls the pickAnswer function with the tag of the button as a parameter
        pickAnswer(v.getTag().toString());
    }
    
    public void pickAnswer(String buttonTag) {
        // declares and initializes a Boolean variable to hold the answer status
        Boolean answerStatus;
        
        // checks if hint is enabled and button tag is not the correct answer
        if(sharedPreferences.getBoolean(HINT, false) && move < 9 && !buttonTag.equals(Integer.toString(answerIndex))){
            // displays a toast message and returns if hint is not available
            Toast.makeText(this, "Touch the Hint to Receive More Hints", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // checks if button tag is equal to the correct answer
        if (buttonTag.equals(Integer.toString(answerIndex))) {
            // checks if hint is not available or if the user has made enough moves to use the hint
            if(!sharedPreferences.getBoolean(HINT, false) || moveCount > move) {
                // sets the index to the correct answer index if hint is available
                while (moveCount > move) {
                    setIndex();
                }
            }
            // declares and initializes a variable to hold the remainder hint string
            String remainderString = "";
            // declares and initializes a variable to hold the first character of the remainder hint
            int firstCharRemainderHint = 0;
            // constructs the remainder hint string and gets the first character of the remainder hint
            if(remainderHint > 9) {
                remainderString = String.valueOf(remainderHint).substring(0, 1) + " + ";
                firstCharRemainderHint = Integer.valueOf(remainderString.replace(" + ", ""));
                sharedPreferences.edit().putInt(FIRSTCHAR_REMAINDER, firstCharRemainderHint).apply();
            } else {
                sharedPreferences.edit().putInt(FIRSTCHAR_REMAINDER, 0).apply();
            }
            // sets the remainder hint for display
            remainderHint = firstCharRemainderHint;
            hintResultTextView.setText(remainderString);
            // sets the answer status to true and updates the display
            buttonResultTextView.setText(R.string.correct);
            answerProgress.setText(answerString.substring(answerString.length() - 1 - indexCount));
            indexCount++;
            sharedPreferences.edit().putInt(INDEX_COUNT, indexCount).apply();
            // sets the next move if the user has not completed guessing the answer
            if(answerString.length() > indexCount) {
                setMove();
            }
            answerStatus = true;
        } else {
            // sets the answer status to false and updates the display
            buttonResultTextView.setText(R.string.wrong);
            answerStatus = false;
        }
        // sets the alpha animation for the results and hint views
        buttonResultTextView.setAlpha(1f);
        hintQuestionTextView.setAlpha(0f);
        hintResultTextView.setAlpha(0f);
        ObjectAnimator resultFadeOut = ObjectAnimator.ofFloat(buttonResultTextView, "alpha", 0f);
        ObjectAnimator hintQuestion = ObjectAnimator.ofFloat(hintQuestionTextView, "alpha", 1f);
        ObjectAnimator hintResult = ObjectAnimator.ofFloat(hintResultTextView, "alpha", 1f);
        AnimatorSet hintSet = new AnimatorSet();
        hintSet.playTogether(hintQuestion, hintResult);
        resultFadeOut.setDuration(1000);
        // checks if the user has completed guessing the answer
        if(indexCount == answerString.length()) {
            // sets the alpha animation and text for the result view and gets the equation
            resultFadeOut.setDuration(10000);
            hintSet.setDuration(10000);
            buttonResultTextView.setText(answerString);
            getEquation();
        } else {
            // shows the button question if the answer is correct
            if(answerStatus) {
                buttonQuestion();
            }
        }
        hintSet.start();
        resultFadeOut.start();
    }
    
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
    
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        // saves the state of the activity to the bundle

        bundle.putString(ANSWER_PROGRESSION, answerProgress.getText().toString());
        bundle.putString(ANSWER_STRING, answerString);
        bundle.putString(EQUATION, equationString);
        bundle.putInt(INDEX_COUNT, indexCount);

        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
