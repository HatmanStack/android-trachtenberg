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
        Display d = getWindowManager().getDefaultDisplay();
        if(d.getRotation() == Surface.ROTATION_0 || d.getRotation() == Surface.ROTATION_180) {
            setContentView(R.layout.activity_practice);
        }else {
            setContentView(R.layout.activity_practice_90);
        }
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
        //ca-app-pub-3940256099942544/6300978111
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mRnd = new Random();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

    public void getEquation() {
        int[] answer = operatorEquation();
        indexCount = 0;
        move = 0;
        moveCount = 1;
        answerProgress.setText("");
        answerString = String.valueOf(answer[0] * answer[1]);
        equationString = answer[0] + " * " + answer[1];
        sharedPreferences.edit().putString(EQUATION, equationString).apply();
        equationTextView.setText(equationString);
        buttonQuestion();
        setMove();
    }

    public void buttonQuestion() {
        answerIndex = mRnd.nextInt(4);
        int[] buttonAnswers = new int[4];
        for (int i = 0; i < buttonAnswers.length; i++) {
            buttonAnswers[i] = -1;
        }
        int incorrect;
        int answer = -1;
        for (int i = 0; i < 4; i++) {
            if (i == answerIndex) {
                answer = Integer.parseInt(answerString.substring(answerString.length() - 1 - indexCount, answerString.length() - indexCount));
                buttonAnswers[i] = answer;
            }
        }
        for (int i = 0; i < buttonAnswers.length; i++) {
            incorrect = mRnd.nextInt(10);
            while (incorrect == buttonAnswers[0] || incorrect == buttonAnswers[1] ||
                    incorrect == buttonAnswers[2] || incorrect == buttonAnswers[3]) {
                incorrect = mRnd.nextInt(10);
            }
            if(buttonAnswers[i] != answer && i != answerIndex) {
                buttonAnswers[i] = incorrect;
            }
        }
        button.setText(String.valueOf(buttonAnswers[0]));
        button1.setText(String.valueOf(buttonAnswers[1]));
        button2.setText(String.valueOf(buttonAnswers[2]));
        button3.setText(String.valueOf(buttonAnswers[3]));
    }


    public void practiceHint(int fsIndex, int ssIndex){
        String firstString = String.valueOf(equationTextView.getText().toString().split(" * ")[0]);
        String secondString = String.valueOf(equationTextView.getText().toString().split(" * ")[2]);

        String firstStringChar = String.valueOf(firstString.charAt(fsIndex));
        String secondStringChar = String.valueOf(secondString.charAt(ssIndex));

        String questionString = firstStringChar + " * " + secondStringChar;
        hintQuestionTextView.setText(questionString);
        if(sharedPreferences.getBoolean(HINT, false)){
            SpannableString sbFirst = new SpannableString(equationTextView.getText().toString());
            sbFirst.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), fsIndex,
                    fsIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sbFirst.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), ssIndex + 7,
                    ssIndex + 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            equationTextView.setText(sbFirst);
        }else {
            equationTextView.setText(equationString);
        }

        String result = Integer.valueOf(firstStringChar) * Integer.valueOf(secondStringChar) + "";
        if(result.length() == 1){
            result = "0" + result;
        }

        String charResultString;
        if(move == 0 || move == 1 || move == 3 || move == 4 || move == 6 || move == 8 || move == 9 || move == 11 || move == 13
                || move == 16 || move == 18 || move == 21 ){
            charResultString = result.substring(1);
        } else {
            charResultString = result.substring(0, 1);
        }
        remainderHint += Integer.valueOf(charResultString);
        charResultString = charResultString + " + ";
        if(move == 0 || move == 3 || move == 8 || move == 14 || move == 19 || move == 22 || move == 23){
            charResultString = charResultString.replace(" + ", "");
        }
        charResultString = hintResultTextView.getText().toString() + charResultString;
        hintResultTextView.setText(charResultString);

        move ++;
    }

    public void nextHint(View v){
        if(moveCount > move) {
            setIndex();
        }
    }

    public void setIndex(){
        int fsIndex = 3;
        int ssIndex = 2;
        if(move == 1 || move == 5){
            if(move == 1 && sharedPreferences.getBoolean(HINTHELP, true)){
                Toast.makeText(this, "Touch hint to get next Step", Toast.LENGTH_SHORT).show();
                sharedPreferences.edit().putBoolean(HINTHELP, false).apply();
            }
            fsIndex = 2;
            ssIndex = 2;
        }
        if(move == 0 || move == 2){
            fsIndex = 3;
            ssIndex = 2;
        }
        if(move == 3 || move == 7){
            fsIndex = 3;
            ssIndex = 1;
        }
        if(move == 4 || move == 10){
            fsIndex = 1;
            ssIndex = 2;
        }
        if(move == 6 || move == 12){
            fsIndex = 2;
            ssIndex = 1;
        }
        if(move == 8 || move == 14){
            fsIndex = 3;
            ssIndex = 0;
        }
        if(move == 9 || move == 15){
            fsIndex = 0;
            ssIndex = 2;
        }
        if(move == 11 || move == 17){
            fsIndex = 1;
            ssIndex = 1;
        }
        if(move == 13 || move == 19){
            fsIndex = 2;
            ssIndex = 0;
        }
        if(move == 16 || move == 20){
            fsIndex = 0;
            ssIndex = 1;
        }
        if(move == 18 || move == 22){
            fsIndex = 1;
            ssIndex = 0;
        }
        if(move == 21 || move == 23){
            fsIndex = 0;
            ssIndex = 0;
        }
        practiceHint(fsIndex, ssIndex);
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

    @Override
    protected void onDestroy(){
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void setMove(){
        if(indexCount == 1){
            move = 1;
            moveCount = 4;
        }
        if(indexCount == 2){
            moveCount = 9;
            move = 4;
        }
        if(indexCount == 3){
            moveCount = 15;
            move = 9;
        }
        if(indexCount == 4){
            moveCount = 20;
            move = 15;
        }
        if(indexCount == 5){
            moveCount = 23;
            move = 20;
        }
        if(indexCount == 6){
            moveCount = 24;
            move = 23;
        }
        if(sharedPreferences.getBoolean(HINT, false)) {
            setIndex();
        }
    }

    public void chooseAnswer(View v){
        pickAnswer(v.getTag().toString());
    }

    public void pickAnswer(String buttonTag) {
        Boolean answerStatus;
        if(sharedPreferences.getBoolean(HINT, false) && move < 9 && !buttonTag.equals(Integer.toString(answerIndex))){
            Toast.makeText(this, "Touch the Hint to Receive More Hints", Toast.LENGTH_SHORT).show();
            return;
        }

        if (buttonTag.equals(Integer.toString(answerIndex))) {
            if(!sharedPreferences.getBoolean(HINT, false) || moveCount > move) {
                while (moveCount > move) {
                    setIndex();
                }
            }
            String remainderString = "";
            int firstCharRemainderHint = 0;
            if(remainderHint > 9){
                remainderString = String.valueOf(remainderHint).substring(0, 1) + " + ";
                firstCharRemainderHint = Integer.valueOf(remainderString.replace(" + ", ""));
                sharedPreferences.edit().putInt(FIRSTCHAR_REMAINDER, firstCharRemainderHint).apply();
            }else {
                sharedPreferences.edit().putInt(FIRSTCHAR_REMAINDER, 0).apply();
            }
            remainderHint = firstCharRemainderHint;
            hintResultTextView.setText(remainderString);
            buttonResultTextView.setText(R.string.correct);
            answerProgress.setText(answerString.substring(answerString.length() - 1 - indexCount));
            indexCount++;
            if(answerString.length() > indexCount) {
                setMove();
            }
            answerStatus = true;
        } else {
            buttonResultTextView.setText(R.string.wrong);
            answerStatus = false;
        }
        buttonResultTextView.setAlpha(1f);
        hintQuestionTextView.setAlpha(0f);
        hintResultTextView.setAlpha(0f);
        ObjectAnimator resultFadeOut = ObjectAnimator.ofFloat(buttonResultTextView, "alpha", 0f);
        ObjectAnimator hintQuestion = ObjectAnimator.ofFloat(hintQuestionTextView, "alpha", 1f);
        ObjectAnimator hintResult = ObjectAnimator.ofFloat(hintResultTextView, "alpha", 1f);
        AnimatorSet hintSet = new AnimatorSet();
        hintSet.playTogether(hintQuestion, hintResult);
        resultFadeOut.setDuration(1000);
        if(indexCount == answerString.length()) {
            resultFadeOut.setDuration(10000);
            hintSet.setDuration(10000);
            buttonResultTextView.setText(answerString);
            getEquation();
        }else {
            if(answerStatus){
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
        bundle.putString(ANSWER_PROGRESSION, answerProgress.getText().toString());
        bundle.putString(EQUATION, equationString);
        bundle.putString(ANSWER_STRING, answerString);
        bundle.putInt(INDEX_COUNT, indexCount);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case (R.id.learn_menu):
                NavUtils.navigateUpFromSameTask(this);
                break;
            case (R.id.settings_menu):
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case (R.id.home):
                NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
