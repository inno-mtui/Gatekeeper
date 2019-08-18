package com.example.gatekeeper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RetriveLeavingActivity extends AppCompatActivity {
    ListView listView;
    FirebaseDatabase database;
    DatabaseReference ref;
    Drivers drivers;
    FirebaseAuth firebaseAuth;

    ArrayList<String> list;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrive_leaving);

        drivers=new Drivers();
        listView= findViewById(R.id.listView);
        database=FirebaseDatabase.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        ref=database.getReference().child("drivers_info").child("Entering");
        list= new ArrayList<String>();
        adapter= new ArrayAdapter<String>(RetriveLeavingActivity.this,R.layout.drivers_info,R.id.userInfo,list);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for( DataSnapshot ds:dataSnapshot.getChildren()){
                    drivers=ds.getValue(Drivers.class);
                    list.add(" Plate Number: "+drivers.getPlateNumber()+"\n Name: "+drivers.getDriverName()+" " +drivers.getLastName()+
                            "\n Date: "+drivers.getDate()+"\n Age Range: "+drivers.getAgeRange()+ "\t\t Companions: " +drivers.getCompanions());

                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.show_drivers:
                startActivity(new Intent(RetriveLeavingActivity.this,ReatriveActivity.class));
                break;
            case R.id.log_out:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(RetriveLeavingActivity.this,LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    }
