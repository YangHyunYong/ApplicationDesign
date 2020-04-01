package com.example.applicationdesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.applicationdesign.Retrofit.INodeJS;
import com.example.applicationdesign.Retrofit.RetrofitClient;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    EditText edit_email, edit_password;
    Button btn_register, btn_login;

    Boolean check=false;
    String c_name=null;
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
        setContentView(R.layout.activity_login);

        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        btn_login = (Button)findViewById(R.id.login_button);
        btn_register = (Button)findViewById(R.id.register_button);

        edit_email = (EditText)findViewById(R.id.edt_email);
        edit_password = (EditText)findViewById(R.id.edt_password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkDevice(edit_email.getText().toString());
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(edit_email.getText().toString(),edit_password.getText().toString());
            }
        });
    }
    private void registerUser(final String email, final String password) {
        final View enter_name_view = LayoutInflater.from(this).inflate(R.layout.enter_name_layout,null);

        new MaterialStyledDialog.Builder(this)
                .setTitle("회원가입")
                .setDescription("가입할 Email, PW를 입력하셨나요?")
                .setCustomView(enter_name_view)
                .setIcon(R.drawable.ic_action_name)
                .setNegativeText("취소")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveText("가입하기")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditText edit_name = (EditText)enter_name_view.findViewById(R.id.edt_name);
                        compositeDisposable.add(myAPI.registerUser(email,edit_name.getText().toString(),password)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        Toast.makeText(LoginActivity.this, ""+s, Toast.LENGTH_SHORT).show();
                                    }
                                }));
                    }
                }).show();
    }

    private void loginUser(String email, String password) {
        compositeDisposable.add(myAPI.loginUser(email,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if(s.contains("encrypted_password")&&!check) {
                            Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            check=false;
                            Intent intent = new Intent(getApplicationContext(), PairingActivity.class);
                            intent.putExtra("email", edit_email.getText().toString());
                            startActivity(intent);
                        }
                        if(s.contains("encrypted_password")&&check) {
                            Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            check=false;
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("childName",c_name);
                            startActivity(intent);
                        }
                    }
                })
        );
    }

    private void checkDevice(String email) {
        compositeDisposable.add(myAPI.checkDevice(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if(s.contains("User not exists!")) {
                            Toast.makeText(LoginActivity.this, ""+s, Toast.LENGTH_SHORT).show();
                            loginUser(edit_email.getText().toString(), edit_password.getText().toString());
                        }
                        else{
                            check = true;
                            c_name = s;
                            c_name = c_name.substring(1,c_name.length()-1);
                            loginUser(edit_email.getText().toString(), edit_password.getText().toString());
                        }
                    }
                })
        );
    }
}
