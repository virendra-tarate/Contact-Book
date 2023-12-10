package com.virendra.tarate.contactbook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchAdapter extends FirebaseRecyclerAdapter<User, SearchAdapter.searchAdapter> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public SearchAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull searchAdapter holder, final int position, @NonNull User model) {

        //Setting Data To Each Contact from Firebase

        holder.mobile.setText(model.getMobileNumber());
        holder.name.setText(model.getName());
        holder.img.setImageResource(R.drawable.ic_account);
        holder.update.setImageResource(R.drawable.ic_edt);
        holder.delete.setImageResource(R.drawable.ic_delete);

        //Clicking Update Button
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Display the Dialog for Updation
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.img.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_popup))
                        .setExpanded(true, 1200)
                        .create();

                View v = dialogPlus.getHolderView();
                TextInputEditText name = v.findViewById(R.id.edtNameUpdate);
                TextInputEditText mobile = v.findViewById(R.id.edtMobileUpdate);
                Button updateContact = v.findViewById(R.id.btnUpdate);

                name.setText(model.getName());
                mobile.setText(model.getMobileNumber());

                dialogPlus.show();

                //Updating Data in Firebase

                updateContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Map With Key Value Pair to Push Data in Firebase with their Unique ID

                        Map<String, Object> map = new HashMap<>();
                        map.put("name", name.getText().toString());
                        map.put("mobileNumber", mobile.getText().toString());

                        //Updating Data By Finding Their children Node

                        FirebaseDatabase.getInstance().getReference("Contact List/").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(getRef(position).getKey()).updateChildren(map)

                                .addOnSuccessListener(new OnSuccessListener<Void>() { //Data Added Successfully
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.name.getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                })

                                .addOnFailureListener(new OnFailureListener() { //Failed While Updating A Data
                                    @Override
                                    public void onFailure(Exception e) {
                                        Toast.makeText(holder.name.getContext(), "Error While Updating Contact", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                });

                    }
                });


            }
        });

        //Delete Contact Button is Clicked
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Alert Dialog Box for Warn User
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.name.getContext());
                builder.setTitle("Do You Want To Delet Contact");
                builder.setMessage("Deleted Data Can not Recover After Deleation !");

                builder.setCancelable(false);


                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() { //Deleting Data
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Contact List/").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(getRef(position).getKey()).removeValue();

                        Toast.makeText(holder.name.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();

                    }
                });

                builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() { //when Cancle Button is Pressed
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                //Show the Dialog
                builder.show();



            }
        });


    }

    @NonNull
    @Override
    public searchAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_holder, parent, false);
        return new SearchAdapter.searchAdapter(v);
    }

    public class searchAdapter extends RecyclerView.ViewHolder{

        TextView mobile, name;
        ImageView img,update,delete;
        //Parametrized constructor with Holder View as a Parameter
        public searchAdapter(@NonNull View itemView) {
            super(itemView);

            //Getting all required Fields from UI
            mobile = itemView.findViewById(R.id.txtMobile);
            name = itemView.findViewById(R.id.txtName);
            img = itemView.findViewById(R.id.contactImg);
            update = itemView.findViewById(R.id.imgEdit);
            delete = itemView.findViewById(R.id.imgDelete);

        }
    }
}
