package com.example.gatekeeper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class RecognitionActivity extends AppCompatActivity {

    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUESTS = 0;

    private static final String TAG = RecognitionActivity.class.getSimpleName();

    private Uri imageUri;
    private TextView detectedTextView;
    public Button button_gallery;
    public Button button_photo;
    public EditText editLicence;
    public EditText driverName;
    public EditText lastName;
    public Button buttonAddLicence;
    private FirebaseAuth firebaseAuth;
    private Button buttonEntering;
    public String gender;
    public String ageRange,companion;
    Drivers drivers;
    String year;
    String month;
    int day;
   // DatabaseHelper myDb;
   FirebaseDatabase database;
    DatabaseReference myRef;
    Calendar calendar;
    Date thisDate;



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUESTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // FIXME: Handle this case the user denied to grant the permissions
                }
                break;
            }
            default:
                // TODO: Take care of this case later
                break;
        }
    }

    public void requestPermissions() {
        List<String> requiredPermissions = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.CAMERA);
        }

        if (!requiredPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    requiredPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUESTS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);
        button_gallery = findViewById(R.id.choose_from_gallery);
        button_photo = findViewById(R.id.take_a_photo);
        editLicence = findViewById(R.id.edit_licence);
        buttonAddLicence=findViewById(R.id.button_add_license);
        buttonEntering=findViewById(R.id.button_enter);
        driverName=findViewById(R.id.text_drivers_name);
        lastName=findViewById(R.id.text_last_name);
        calendar=Calendar.getInstance();


         thisDate=new Date();

        year= String.valueOf(calendar.get(Calendar.YEAR));
        month= String.valueOf(calendar.get(Calendar.MONTH));
        day=calendar.get(Calendar.DAY_OF_MONTH);
        // dayOfWeek= String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));


        drivers=new Drivers();

         database = FirebaseDatabase.getInstance();


        requestPermissions();

        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
        FirebaseUser user=firebaseAuth.getCurrentUser();

// Adding the entries for entering and leaving
        buttonEntering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = database.getReference().child("drivers_info").child("Entering").child(year).child(month).child(String.valueOf(day));
                RadioGroup radioGender=findViewById(R.id.radio_gender);
                RadioGroup radioAge=findViewById(R.id.radio_age);
                RadioGroup radioCompanion=findViewById(R.id.radio_companion);
                int idGender=radioGender.getCheckedRadioButtonId();
                int idAge=radioAge.getCheckedRadioButtonId();
                int idCompanion=radioCompanion.getCheckedRadioButtonId();

                if(TextUtils.isEmpty(editLicence.getText().toString()))
                {
                    Toast.makeText(RecognitionActivity.this,"Please Enter Plate Number first",Toast.LENGTH_SHORT).show();

                }

                switch (idGender){
                    case R.id.radio_male:
                        gender="Male";
                        break;

                    case R.id.radio_female:
                        gender="Female";
                        break;
                }
                switch (idAge){
                    case R.id.radio_18:
                        ageRange="18-25";
                        break;

                    case R.id.radio_26:
                        ageRange="26-40";
                        break;
                    case R.id.radio_41:
                        ageRange="41 and Older";
                        break;
                }
                switch (idCompanion){
                    case R.id.radio_alone:
                        companion="Alone";
                        break;

                    case R.id.radio_spouse:
                        companion="With Spouse";
                        break;
                    case R.id.radio_friends:
                        companion="With Friends";
                        break;
                    case R.id.radio_family:
                        companion="With Family";
                        break;
                }



                drivers.setPlateNumber(editLicence.getText().toString());
                drivers.setDriverName(driverName.getText().toString().trim());
                drivers.setLastName(lastName.getText().toString().trim());
                drivers.setGender(gender);
                drivers.setAgeRange(ageRange);
                drivers.setCompanions(companion);
                drivers.setDate(thisDate.toString());

                myRef.push().setValue(drivers);
                Toast.makeText(RecognitionActivity.this, "Data is inserted", Toast.LENGTH_SHORT).show();

                editLicence.setText(" ");
                driverName.setText(" ");
                lastName.setText(" ");

                radioAge.clearCheck();
                radioCompanion.clearCheck();
                radioGender.clearCheck();

            }
        });

        buttonAddLicence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = database.getReference().child("drivers_info").child("Leaving").child(year).child(month).child(String.valueOf(day));
                RadioGroup radioGender=findViewById(R.id.radio_gender);
                RadioGroup radioAge=findViewById(R.id.radio_age);
                RadioGroup radioCompanion=findViewById(R.id.radio_companion);
                int idGender=radioGender.getCheckedRadioButtonId();
                int idAge=radioAge.getCheckedRadioButtonId();
                int idCompanion=radioCompanion.getCheckedRadioButtonId();

                if(TextUtils.isEmpty(editLicence.getText().toString()))
                {
                    Toast.makeText(RecognitionActivity.this,"Please Enter Plate Number first",Toast.LENGTH_SHORT).show();

                }

                switch (idGender){
                    case R.id.radio_male:
                        gender="Male";
                        break;

                    case R.id.radio_female:
                        gender="Female";
                        break;
                }
                switch (idAge){
                    case R.id.radio_18:
                        ageRange="18-25";
                        break;

                    case R.id.radio_26:
                        ageRange="26-40";
                        break;
                    case R.id.radio_41:
                        ageRange="41 and Older";
                        break;
                }
                switch (idCompanion){
                    case R.id.radio_alone:
                        companion="Alone";
                        break;

                    case R.id.radio_spouse:
                        companion="With Spouse";
                        break;
                    case R.id.radio_friends:
                        companion="With Friends";
                        break;
                    case R.id.radio_family:
                        companion="With Family";
                        break;
                }


               drivers.setPlateNumber(editLicence.getText().toString() );
               drivers.setDriverName(driverName.getText().toString().trim());
                drivers.setLastName(lastName.getText().toString().trim());
                drivers.setGender(gender);
                drivers.setAgeRange(ageRange);
                drivers.setCompanions(companion);
                drivers.setDate(thisDate.toString());

               myRef.push().setValue(drivers);
                Toast.makeText(RecognitionActivity.this, "Data is inserted", Toast.LENGTH_SHORT).show();


                editLicence.setText(" ");
                driverName.setText(" ");
                lastName.setText(" ");

                radioAge.clearCheck();
                radioCompanion.clearCheck();
                radioGender.clearCheck();


            }
        });

        button_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_GALLERY);
            }
        });

        button_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = System.currentTimeMillis() + ".jpg";

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, filename);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });

        detectedTextView = findViewById(R.id.detected_text);
        detectedTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    private void inspectFromBitmap(Bitmap bitmap) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        try {
            if (!textRecognizer.isOperational()) {
                new AlertDialog.
                        Builder(this).
                        setMessage("Text recognizer could not be set up on your device").show();
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
            List<TextBlock> textBlocks = new ArrayList<>();
            for (int i = 0; i < origTextBlocks.size(); i++) {
                TextBlock textBlock = origTextBlocks.valueAt(i);
                textBlocks.add(textBlock);
            }
            Collections.sort(textBlocks, new Comparator<TextBlock>() {
                @Override
                public int compare(TextBlock o1, TextBlock o2) {
                    int diffOfTops = o1.getBoundingBox().top - o2.getBoundingBox().top;
                    int diffOfLefts = o1.getBoundingBox().left - o2.getBoundingBox().left;
                    if (diffOfTops != 0) {
                        return diffOfTops;
                    }
                    return diffOfLefts;
                }
            });

            StringBuilder detectedText = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {
                if (textBlock != null && textBlock.getValue() != null) {
                    detectedText.append(textBlock.getValue());
                    detectedText.append("\n");
                }
            }

            detectedTextView.setText(detectedText);
            String licenceString = detectedTextView.getText().toString();
            editLicence.setText(licenceString);

        } finally {
            textRecognizer.release();
        }
    }

    private void inspect(Uri uri) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 2;
            options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
            bitmap = BitmapFactory.decodeStream(is, null, options);
            inspectFromBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Failed to find the file: " + uri, e);
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.w(TAG, "Failed to close InputStream", e);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    inspect(data.getData());
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (imageUri != null) {
                        inspect(imageUri);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
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
                startActivity(new Intent(RecognitionActivity.this,ReatriveActivity.class));
                break;
            case R.id.log_out:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(RecognitionActivity.this,LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
