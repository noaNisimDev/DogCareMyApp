package com.example.dogcaremyapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Login_Activity extends AppCompatActivity {

    private Button login_BTN_loginregister;
    private TextInputLayout login_phone;


    //[START Firebase_declare_auth]
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private FirebaseAuth mAuth;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private static final String TAG = "PhoneAuthActivity";

    private String verificationCode = "";
    private String israelPrefix = "+972 ";


    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //clear screen - no titles
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        mAuth = FirebaseAuth.getInstance();
        //check if user is already logged in - go to main screen
        if (mAuth.getCurrentUser() != null) {
            launchActivity(Main_Activity.class);
        }
        //else - initiate log in screen
        else {
            setContentView(R.layout.login_activity);

            mAuth.setLanguageCode("he");
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    Log.d(TAG, "onVerificationCompleted:" + credential);


                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Log.w(TAG, "onVerificationFailed", e);

                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        // ...
                        //TODO - add special error notification (toast/layout)
                        Log.d(TAG, " Invalid request. cannot log in");
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                        // ...
                        //TODO - add special error notification (toast/layout)
                        Log.d(TAG, " The SMS quota for the project has been exceeded");

                    }

                    // Show a message and update the UI
                    // ...
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Log.d(TAG, "onCodeSent:" + verificationId);
                    // Save verification ID and resending token so we can use them later
                    mVerificationId = verificationId;
                    mResendToken = token;
                    showVerificationDialog(verificationId);
                    //TODO - dealing with 60s timeout.. add resend code
                }
            };


            findViews();

            login_BTN_loginregister.setEnabled(false);
            login_BTN_loginregister.setAlpha(0.5f);
            login_phone.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int count, int before, int i) {
                    if(charSequence.length() == 10 && charSequence.toString().matches("[0-9]+")) {
                        login_BTN_loginregister.setEnabled(true);
                        login_BTN_loginregister.setAlpha(1);
                    }
                    else{
                        login_BTN_loginregister.setEnabled(false);
                        login_BTN_loginregister.setAlpha(0.5f);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
           /* //disable login button until check login phone
            //TODO 2
            String phoneNumber = login_phone.getEditText().getText().toString();
            login_BTN_loginregister.setEnabled(false);
            login_BTN_loginregister.setAlpha(.5f);

            while (phoneNumber.length() == 10 && phoneNumber.matches("[0-9]")) {
                login_BTN_loginregister.setEnabled(true);
                login_BTN_loginregister.setAlpha(1f);
            }
*/

            login_BTN_loginregister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phoneNumber = login_phone.getEditText().getText().toString();

                    String formattedNumber = PhoneNumberUtils.formatNumber(phoneNumber);
                    formattedNumber = israelPrefix.concat(String.copyValueOf(formattedNumber.toCharArray(), 1, formattedNumber.length() - 1));

                    Log.d(TAG, formattedNumber);


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            formattedNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            Login_Activity.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks
                }
            });
        }

    }

    private void showVerificationDialog(final String verificationId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter verification code");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                verificationCode = input.getText().toString();
                verifyPhoneNumberWithCode(verificationId, verificationCode);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            //TODO - 1 create new user
                            //TODO - 2 save to firebase
                            launchActivity(CreateNewDog_Activity.class);


                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }


    private void launchActivity(Class activityName) {

        Intent intent = new Intent(this, activityName);
        startActivity(intent);
    }

    private void findViews() {
        login_BTN_loginregister = findViewById(R.id.login_BTN_loginregister);
        login_phone = findViewById(R.id.login_phone);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }


}