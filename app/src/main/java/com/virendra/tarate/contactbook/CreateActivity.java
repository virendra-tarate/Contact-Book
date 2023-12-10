package com.virendra.tarate.contactbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class CreateActivity extends AppCompatActivity {

    EditText edtName, edtMobile;
    Button btnCreate, btnLogOut;
    Random random;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        edtName = findViewById(R.id.edtName);
        edtMobile = findViewById(R.id.edtMobile);
        btnCreate = findViewById(R.id.btnCreate);
        btnLogOut = findViewById(R.id.btnLogOut);
        random = new Random();

        //When Click on Create A New Contact Button
        //Check empty Fields And Validate the Mobile Number.
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtName.getText().toString().isEmpty() || edtMobile.getText().toString().isEmpty()){
                    Toast.makeText(CreateActivity.this, "Both The Fields Are Required", Toast.LENGTH_SHORT).show();
                }
                else if(edtMobile.getText().toString().length() <=9 || edtMobile.getText().toString().length() >10){
                    Toast.makeText(CreateActivity.this, "Enter A Valid Mobile Number", Toast.LENGTH_SHORT).show();
                }
                else if(!edtName.getText().toString().isEmpty() && edtMobile.getText().toString().length() == 10){
                    createContact(edtName.getText().toString(), edtMobile.getText().toString() ,edtName.getText().toString()+edtMobile.getText().toString());
                }
            }
        });


        //Logout user When Log out Button is Clicked
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(CreateActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Toast.makeText(CreateActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    //Method to create A New Contact in Firebase Using Refrence Path of Node
    private void createContact(String name, String mobileNo, String ID) {
        long x = random.nextInt(10000);
        FirebaseDatabase.getInstance().getReference("Contact List/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + name+mobileNo+x).setValue(new User(name, mobileNo, ID + x));
        edtName.setText("");
        edtMobile.setText("");
        Toast.makeText(this, "Contact Created Successfully ", Toast.LENGTH_SHORT).show();

    }
}