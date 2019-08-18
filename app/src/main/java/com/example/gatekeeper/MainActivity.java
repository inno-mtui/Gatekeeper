package com.example.gatekeeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private ImageView iv;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv_scan);
        iv = findViewById(R.id.imageView_scan);
        Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.mytransition);
        tv.startAnimation(myAnim);
        iv.startAnimation(myAnim);
        final Intent intent = new Intent(this, LoginActivity.class);
        Thread timer = new Thread() {
          public void run(){
                try

            {
                sleep(5000);
            } catch(
            InterruptedException e)

            {
                e.printStackTrace();
            }
                finally

            {

                startActivity(intent);
                finish();
            }
        }
    };
    timer.start();

    }
}


