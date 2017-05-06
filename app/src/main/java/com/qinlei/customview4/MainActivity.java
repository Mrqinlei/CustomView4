package com.qinlei.customview4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private FivePointLoadingView fivePointLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fivePointLoadingView = (FivePointLoadingView) findViewById(R.id.five);
    }

    public void end(View view) {
        fivePointLoadingView.end();
    }

    public void start(View view) {
        fivePointLoadingView.start();
    }
}
