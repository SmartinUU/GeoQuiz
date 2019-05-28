package youdu.com.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_IS_ANSWERED = "IsAnswered";
    private static final int REQUEST_CODE_CHEAT = 0;
    private boolean mIsCheater;

    private Button mTrueButton;
    private Button mFalseButton;
    private TextView mQuestionTextView;
    private ImageButton mNextButton;
    private ImageButton mLastButton;
    private Button mCheatButton;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true, false),
            new Question(R.string.question_oceans, true, false),
            new Question(R.string.question_mideast, false, false),
            new Question(R.string.question_africa, false, false),
            new Question(R.string.question_americas, true, false),
            new Question(R.string.question_asia, true, false)
    };
    /**
     * 当前页数
     */
    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");
        setContentView(R.layout.activity_quiz);
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            boolean[] answeredList = savedInstanceState.getBooleanArray(KEY_IS_ANSWERED);
            for (int i = 0; i < answeredList.length; i++) {
                mQuestionBank[i].setAnswered(answeredList[i]);
            }
        }
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: ");
        outState.putInt(KEY_INDEX, mCurrentIndex);

        boolean[] answeredList = new boolean[mQuestionBank.length];
        for (int i = 0; i < answeredList.length; i++) {
            answeredList[i] = mQuestionBank[i].isAnswered();
        }
        outState.putBooleanArray(KEY_IS_ANSWERED, answeredList);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void initView() {
        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mNextButton = findViewById(R.id.next_button);
        mLastButton = findViewById(R.id.last_button);
        mCheatButton = (Button) findViewById(R.id.cheat_button);

        updateQuestion();

        mTrueButton.setOnClickListener(this);
        mFalseButton.setOnClickListener(this);
        mQuestionTextView.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mLastButton.setOnClickListener(this);
        mCheatButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.true_button:
                checkAnswer(true);
                break;
            case R.id.false_button:
                checkAnswer(false);
                break;
            case R.id.next_button:
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
                break;
            case R.id.last_button:
                if (mCurrentIndex > 0) {
                    mCurrentIndex--;
                } else {
                    mCurrentIndex = mQuestionBank.length - 1;
                }
                updateQuestion();
                break;
            case R.id.cheat_button:
                //启动CheatActivity并把问题答案传递过去
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.actionView(this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
                break;
        }
    }

    /**
     * 当mCurrentIndex变动时候调用，更新问题题目和按钮
     */
    private void updateQuestion() {
        Question question = mQuestionBank[mCurrentIndex];
        String title = (mCurrentIndex + 1) + "." + getString(question.getTextResId());
        mQuestionTextView.setText(title);

        mTrueButton.setEnabled(question.isAnswered() ? false : true);
        mFalseButton.setEnabled(question.isAnswered() ? false : true);
    }

    /**
     * 该题目已被回答过状态更新
     */
    private void updateBtn() {
        mQuestionBank[mCurrentIndex].setAnswered(true);

        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
    }

    /**
     * 判断用户所选答案是否正确
     *
     * @param userPressed 用户点击，true或者false
     */
    private void checkAnswer(boolean userPressed) {
        updateBtn();
        //储存在Question中的答案
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        if (mIsCheater) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressed == answerIsTrue) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }
}
