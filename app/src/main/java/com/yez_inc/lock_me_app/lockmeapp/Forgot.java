package com.yez_inc.lock_me_app.lockmeapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.yez_inc.lock_me_app.R;
import com.yez_inc.lock_me_app.lockmeapp.locks.LockListener;
import com.yez_inc.lock_me_app.lockmeapp.utils.LockManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Forgot extends FrameLayout {
    Context context = getContext().getApplicationContext();
    EditText email, answer1, answer2;
    Spinner question1, question2;
    boolean isForSetting;
    Button send, verify;
    LockManager lockManager;
    private LockListener mListener;

    public Forgot(Context context) {
        super(context);
        onCreate();
    }

    public Forgot(Context context, LockManager Manager, boolean _isForSettings) {
        super(context);
        lockManager = Manager;
        isForSetting = _isForSettings;
        onCreate();
    }

    void onCreate() {
        inflate(context, R.layout.forgot, this);
        email = (EditText) findViewById(R.id.email);
        answer1 = (EditText) findViewById(R.id.answer1);
        answer2 = (EditText) findViewById(R.id.answer2);
        question1 = (Spinner) findViewById(R.id.question1);
        question2 = (Spinner) findViewById(R.id.question2);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(context, R.array.questions, R.layout.question_item);
        adapter.setDropDownViewResource(R.layout.question_item);
        question1.setAdapter(adapter);
        question2.setAdapter(adapter);
        send = (Button) findViewById(R.id.send);
        verify = (Button) findViewById(R.id.verify);
        if (isForSetting) {
            send.setText(R.string.ok);
            verify.setText(R.string.ok);
            email.setText(lockManager.getEmail());
            question1.setSelection(lockManager.getQuestion1());
            question2.setSelection(lockManager.getQuestion2());
            answer1.setText(lockManager.getAnswer1());
            answer2.setText(lockManager.getAnswer2());
        }
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
        verify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isForSetting) save_questions();
                else verify();
            }
        });
    }

    void send() {
        String _email = email.getText().toString();
        if (Patterns.EMAIL_ADDRESS.matcher(_email).matches()) {
            if (isForSetting) {
                lockManager.setEmail(_email);
                sendCallBack(getContext().getString(R.string.data_saved));
            } else if (_email.equals(lockManager.getEmail())) {
                if (isNetworkAvailable()) {
                    send_email();
                } else
                    sendCallBack(getContext().getString(R.string.connexionLost));
            } else
                sendCallBack(getContext().getString(R.string.EmailIsIncorrect));
        } else
            sendCallBack(getContext().getString(R.string.EmailFormat));
    }

    void verify() {
        String _answer1 = answer1.getText().toString();
        String _answer2 = answer2.getText().toString();
        int _question1 = question1.getSelectedItemPosition();
        int _question2 = question2.getSelectedItemPosition();
        if (_answer1.equals(lockManager.getAnswer1()) && _answer2.equals(lockManager.getAnswer2()) && _question1 == lockManager.getQuestion1() && _question2 == lockManager.getQuestion2()) {
            call_New_lock();
        } else
            sendCallBack(getContext().getString(R.string.incorrect_questions));
    }

    void save_questions() {
        String _answer1 = answer1.getText().toString();
        String _answer2 = answer2.getText().toString();
        if (_answer1.isEmpty() || _answer2.isEmpty())
            sendCallBack(getContext().getString(R.string.answer_questions));
        else {
            sendCallBack(getContext().getString(R.string.data_saved));
            lockManager.setAnswer1(_answer1);
            lockManager.setAnswer2(_answer2);
            lockManager.setQuestion1(question1.getSelectedItemPosition());
            lockManager.setQuestion2(question2.getSelectedItemPosition());
        }
    }

    boolean isNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    void send_email() {

    }

    void call_New_lock() {

    }

    void sendCallBack(String action) {
        if (mListener != null) {
            mListener.onLockInteraction(action);
        }
    }

    public void setOnForgotInteractionListener(LockListener lockListener) {
        mListener = lockListener;
    }
}
