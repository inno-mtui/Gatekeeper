package com.example.gatekeeper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReatriveActivity extends AppCompatActivity {
    ListView listView;
    FirebaseDatabase database;
    DatabaseReference ref;
    Drivers drivers;
    FirebaseAuth firebaseAuth;
    Button buttonSearch;
    String day,month,year;
    EditText editDay,editMonth,editYear;
    String retrive;

    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reatrive);

        drivers=new Drivers();
        listView= findViewById(R.id.listView);
        buttonSearch=findViewById(R.id.button_search_entering);
        editDay=findViewById(R.id.search_day);
        editMonth=findViewById(R.id.search_month);
        editYear=findViewById(R.id.search_year);
        database=FirebaseDatabase.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();

        list= new ArrayList<String>();
        adapter= new ArrayAdapter<String>(ReatriveActivity.this,R.layout.drivers_info,R.id.userInfo,list);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup radioRetrive=findViewById(R.id.radio_retrive);
                int idRetrive=radioRetrive.getCheckedRadioButtonId();


                switch (idRetrive){
                    case R.id.entering:
                        retrive="Entering";
                        break;

                    case R.id.radio_spouse:
                        retrive="Leaving";
                        break;

                }
                listView.setAdapter(null);
                day=editDay.getText().toString();
                month=editMonth.getText().toString();
                year=editYear.getText().toString();

                ref=database.getReference().child("drivers_info").child(retrive).child(year).child(month).child(day);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for( DataSnapshot ds:dataSnapshot.getChildren()){
                            drivers=ds.getValue(Drivers.class);
                            if (drivers != null) {
                                list.add(" Plate Number: "+drivers.getPlateNumber()+"\n Name: "+drivers.getDriverName()+" " +drivers.getLastName()+
                                        "\n Date: "+drivers.getDate()+"\n Age Range: "+drivers.getAgeRange()+ "\t\t Companions: " +drivers.getCompanions());
                            }
                            else if(drivers==null){
                                Toast.makeText(ReatriveActivity.this,"There was no data on such date",Toast.LENGTH_SHORT).show();
                            }

                        }
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



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
                startActivity(new Intent(ReatriveActivity.this,ReatriveActivity.class));
                break;
            case R.id.log_out:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(ReatriveActivity.this,LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
