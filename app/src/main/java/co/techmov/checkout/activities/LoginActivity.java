package co.techmov.checkout.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.appboy.Appboy;

import org.json.JSONException;
import org.json.JSONObject;

import co.techmov.checkout.R;
import co.techmov.checkout.entity.Login;
import co.techmov.checkout.task.VoidTask;
import co.techmov.checkout.util.UIHelper;
import co.techmov.checkout.util.Utils;
import io.realm.Realm;

/**
 * Created by victor on 08-26-15.
 */
public class LoginActivity extends Activity {
    private EditText editTextUser;
    private EditText editTextPass;
    Realm r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        r = Utils.getRealmInstance(this);

        Login login = r.where(Login.class).findFirst();
        if(login!=null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        editTextUser = (EditText) findViewById(R.id.txt_user);
        editTextPass = (EditText) findViewById(R.id.txt_pass);
        findViewById(R.id.button_enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.checkInternetConnection(LoginActivity.this,
                        Appboy.getInstance(getApplicationContext()))) {
                    enterClick();
                }
            }
        });
    }

    private void enterClick(){
        VoidTask task = new VoidTask("/motorista/login");
        task.addParam("username", editTextUser.getText().toString());
        task.addParam("password", editTextPass.getText().toString());
        task.setPostExecute(new VoidTask.PostExecute() {
            @Override
            public void execute(StringBuilder response) {
                Log.d("WS-RESULT", response.toString());
                try {
                    JSONObject resp = new JSONObject(response.toString());
                    Boolean status = resp.getBoolean("status");
                    if (status != null && status == true) {
                        resp = resp.getJSONObject("data");
                        r.beginTransaction();
                        Login login = r.createObject(Login.class);
                        login.setId(resp.getInt("motorista_id"));
                        login.setUserId(resp.getInt("ref_user_id"));
                        login.setName(resp.getString("nombre"));
                        login.setLastname(resp.getString("apellido"));
                        login.setUsername(resp.getString("username"));
                        login.setState(resp.getString("estado").equals("1") ? true : false);
                        r.commitTransaction();
                        Appboy.getInstance(getApplicationContext()).changeUser(login.getName());
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else if (status == null || status == false) {
                        UIHelper.alertDialog(LoginActivity.this,
                                getString(R.string.error),
                                getString(R.string.login_error)).show();
                    }
                } catch (JSONException e) {
                    Log.d("PARSE-ERR", e.getMessage());
                }
            }
        });
        task.execute();
    }

}
