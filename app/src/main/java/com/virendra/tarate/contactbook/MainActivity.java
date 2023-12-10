package com.virendra.tarate.contactbook;

//import static com.google.firebase.database.core.operation.OperationSource.Source.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    TextInputEditText edtName,edtMobile,edtOtp;
    Button btnOtp,btnSignUp;
    FirebaseAuth mAuth;
    long pressedTime;
    private String verificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        edtName = findViewById(R.id.editName);
        edtMobile = findViewById(R.id.editMobile);
        edtOtp = findViewById(R.id.editOtp);
        btnOtp = findViewById(R.id.btnOtp);
        btnSignUp = findViewById(R.id.btnSignUp);

        //If User is Loged in Then Send it to 2nd Activity
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this, ContactList.class));
            finish();
        }


        //When USer Clic on SignUp/ LogIn Button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtName.getText().toString().isEmpty() || edtMobile.getText().toString().isEmpty() || edtOtp.getText().toString().isEmpty() ){
                    Toast.makeText(MainActivity.this, "Every Field Is Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Method To Verify The OTP
                verifyotp(edtOtp.getText().toString());

            }
        });


        //When Generate OTP Button is Clicked
        btnOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cheack the Mobile Number is Valid or Not
                if(TextUtils.isEmpty(edtMobile.getText().toString())){
                    Toast.makeText(MainActivity.this, "Enter A Valid Mobile Number", Toast.LENGTH_SHORT).show();
                }else{

                    String number = edtMobile.getText().toString();
                    //Method to send Verification OTP
                    sendVerificationCode(number);
                    btnOtp.setText("OTP Sent");
                    btnOtp.setEnabled(false);

                    //Disable the OTP Button for 37 Sec.

                    int otpTime = 37000;
                    Toast.makeText(MainActivity.this, "You Can Resend Otp After 37 Sec", Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Enable The Resend OTP Button After 37 Sec.
                                btnOtp.setText("Resend OTP");
                                btnOtp.setEnabled(true);


                            }
                        },otpTime);
                    }

                }

            }
        });



    }

    //Sending The OTP By using the Entered Mobile Number
    private void sendVerificationCode(String phoneNumber) {
        //Don't Change This Code !!!!!!
        edtOtp.setEnabled(true);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+phoneNumber)       // Phone number to verify
                        .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(MainActivity.this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    //Method to verify is User Entered a Valid OTP or not ?
    private void verifyotp(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        //Feteching The User Information and OTP

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() { //if OTP is Matched with Current User
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                                //Add User Information in Database
                                FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(edtName.getText().toString(), edtMobile.getText().toString(), edtName.getText().toString()+edtMobile.getText().toString()));
                                //starting next activity after sign up
                                //Start next Screen
                                startActivity(new Intent(MainActivity.this, ContactList.class));
                                finish();
                                Toast.makeText(MainActivity.this, "Signed UP Successfully", Toast.LENGTH_SHORT).show();
                                edtOtp.setEnabled(true);


                        }
                    }
                });


    }


    //Don't Change The CallBack
    //Phone Auth Provider sending OTP by cheacking a Verification State
    PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            final String code = credential.getSmsCode();

            if(code !=null){
                verifyotp(code);
            }

        }


        //Overrieded method if Verification is Failed
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            //Toast.makeText(MainActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                //Invalid request
                Toast.makeText(MainActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                //The SMS quota for the project has been exceeded
                Toast.makeText(MainActivity.this, "The SMS quota for the project has been exceeded", Toast.LENGTH_SHORT).show();
            } /*else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    //reCAPTCHA verification attempted with null Activity
                }*/


        }

        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);
            verificationId = s;
        }
    };


    //When USer Clicked on BAck Button
    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();

    }


}