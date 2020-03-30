package com.example.applicationdesign;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.applicationdesign.Retrofit.INodeJS;
import com.example.applicationdesign.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    EditText edt_childName, edt_sex, edt_childHeight, edt_childWeight, edt_childAge;
    String device = null;
    String userEmail = null;

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        edt_childName = (EditText) findViewById(R.id.edt_childName);
        edt_sex = (EditText)findViewById(R.id.edt_sex);
        edt_childHeight = (EditText) findViewById(R.id.edt_childHeight);
        edt_childWeight = (EditText) findViewById(R.id.edt_childWeight);
        edt_childAge = (EditText) findViewById(R.id.edt_childAge);

        Intent intent = getIntent();
        device = intent.getStringExtra("deviceAddress");
        userEmail = intent.getStringExtra("email");


        setSupportActionBar(toolbar);
        setTitle("내 아이 등록");
        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        Button registerBtn = (Button)findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerChild(userEmail,device, edt_childName.getText().toString(), edt_sex.getText().toString(),edt_childHeight.getText().toString(),edt_childWeight.getText().toString(),edt_childAge.getText().toString());
//                Log.i("email",userEmail);
//                Log.i("device",device);
//                Log.i("name",edt_childName.getText().toString());
//                Log.i("sex",edt_sex.getText().toString());
//                Log.i("he",edt_childHeight.getText().toString());
//                Log.i("we",edt_childWeight.getText().toString());
//                Log.i("age",edt_childAge.getText().toString());
            }
        });
    }

    private void registerChild(final String email, final String device_name, final String c_name, final String c_sex, final String c_height, final String c_weight, final String c_age) {
        compositeDisposable.add(myAPI.registerChild(email,device_name,c_name,c_sex,c_height,c_weight,c_age)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }
                }));
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
