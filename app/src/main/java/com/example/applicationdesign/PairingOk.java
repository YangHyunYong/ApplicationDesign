package com.example.applicationdesign;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PairingOk extends AppCompatActivity {

    TextView deviceName;
    String userEmail = null;
    String deviceAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing_ok);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("내 기기 찾기");
        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        deviceName = (TextView)findViewById(R.id.deviceName);
        Intent intent = getIntent();
        final String device = intent.getStringExtra("deviceName");
        userEmail = intent.getStringExtra("email");
        deviceAddress = intent.getStringExtra("deviceAddress");

        deviceName.setText("연결된 기기: " + device);

        Button connectBtn = (Button)findViewById(R.id.connectbtn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                intent.putExtra("deviceName",device);
                intent.putExtra("deviceAddress",deviceAddress);
                intent.putExtra("email",userEmail);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
