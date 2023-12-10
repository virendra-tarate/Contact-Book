package com.virendra.tarate.contactbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ContactList extends AppCompatActivity {

    ImageView imgCreate;
    RecyclerView recyclerView;
    SearchAdapter searchAdapter;
    SearchView searchView;
    long pressedTime;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        //Assigning UI to each element

        imgCreate = findViewById(R.id.imgCreate);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        searchView = findViewById(R.id.edtSearch);


        //Clicking Create New Contact Image
        //Going to Create a New Contact
        imgCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(ContactList.this, CreateActivity.class);
                startActivity(ii);
            }
        });



        //Swipe Refresh
        //When All Contacts Are Refreashed
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContacts();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        //Method to get Contact from Backend(Firebase)
        //getting all contacts
        getContacts();


        //When Typed Something in Search View
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                //Search method
                serachName(search);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                serachName(search);
                return false;
            }
        });


    }

    private void getContacts() {

        //FirebaseRecyclerOptions to get all Contacts in Recycler view

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Contact List/").child(FirebaseAuth.getInstance().getCurrentUser().getUid()), User.class)
                .build();


        //Display the Contact
        searchAdapter = new SearchAdapter(options);
        recyclerView.setAdapter(searchAdapter);
        searchAdapter.startListening();

    }


    //Method to search a Contact by using its Name
    void serachName(String str) {


        //FirebaseRecyclerOption to search an Element from its Child

        FirebaseRecyclerOptions<User> options
                = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Contact List/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("name").startAt(str).endAt(str+"~"), User.class)
                .build();


        //Display the Search Result
        searchAdapter = new SearchAdapter(options);
        searchAdapter.startListening();;
        recyclerView.setAdapter(searchAdapter);


    }

    //load the all Contacts When Screen is Visible
    @Override
    protected void onStart() {
        super.onStart();
        searchAdapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    //Quit When User Pressed Back Button
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