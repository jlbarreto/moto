package co.techmov.checkout.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import co.techmov.checkout.R;
import co.techmov.checkout.util.AppboyUtil;

/**
 * Created by victor on 08-26-15.
 */
public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000*2);
                    toMainActivity();
                } catch (InterruptedException e) { }
            }
        }).start();
    }

    void toMainActivity(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

}
