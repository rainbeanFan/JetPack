package cn.rainbean.jetpack.ui.login;

import android.os.Bundle;
import android.view.View;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.rainbean.jetpack.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String APP_ID = "";

    private Tencent tencent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View actionClose = findViewById(R.id.action_close);
        View actionLogin = findViewById(R.id.action_login);

        actionClose.setOnClickListener(this);
        actionLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.action_close){
            finish();
        }else if (view.getId() == R.id.action_login){
            login();
        }
    }

    private void login(){
        if (tencent == null){
            tencent = Tencent.createInstance("",getApplicationContext());
        }
        tencent.login(this, APP_ID, new IUiListener() {
            @Override
            public void onComplete(Object o) {

            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

}
