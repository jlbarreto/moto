package co.techmov.checkout.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.appboy.Appboy;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.HashMap;
import java.util.List;

import co.techmov.checkout.R;
import co.techmov.checkout.entity.Login;
import co.techmov.checkout.fragments.OrdersFragment;
import co.techmov.checkout.util.Utils;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Ramon Zuniga on 8/20/15.
 */
public class MainActivity extends FragmentActivity {
    private FrameLayout frameLayout;
    private HashMap<String, List> dataList;
    private HashMap<String, Object> dataObject;
    private Menu menu;
    private Realm r;

    public void put(String key, List values){
        if(dataList == null)
            dataList = new HashMap<>();
        dataList.put(key, values);
    }

    public void put(String key, Object object){
        if(dataObject == null)
            dataObject = new HashMap<>();
        dataObject.put(key, object);
    }

    public Object get(String key){
        if(dataObject==null)
            dataObject = new HashMap<>();
        return dataObject.get(key);
    }

    public List getList(String key){
        if(dataList==null)
            dataList = new HashMap<>();
        return dataList.get(key);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Appboy.getInstance(MainActivity.this).openSession(MainActivity.this);

        r = Realm.getInstance(this);

        frameLayout = (FrameLayout) findViewById(R.id.fragment_container);

        setTitle(r.where(Login.class).findFirst().getUsername());

        if (Utils.checkInternetConnection(MainActivity.this,
                Appboy.getInstance(MainActivity.this))) {
            addFragment(new OrdersFragment());
        }

        try{ Iconify.with(new FontAwesomeModule()); }
        catch (IllegalArgumentException e){}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_refresh).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_refresh)
                        .colorRes(R.color.white).actionBarSize());
        menu.findItem(R.id.action_settings).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_ellipsis_v)
                        .colorRes(R.color.white).actionBarSize());
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout)
            logout();
        if(id == R.id.action_myorders)
            startActivity(new Intent(getApplicationContext(), MyOrdersActivity.class));
        return super.onOptionsItemSelected(item);
    }

    public void addFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment).commit();
    }

    public void showFragment(Fragment fragment){
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        trx.replace(R.id.fragment_container, fragment);
        trx.addToBackStack(null);
        trx.commit();
    }

    public void hideRefresh(){
        try{
            menu.findItem(R.id.action_refresh).setVisible(false);
        }catch (NullPointerException e){ }
    }

    public void showRefresh(){
        menu.findItem(R.id.action_refresh).setVisible(true);
    }

    public void hideMenu(){
        try{
            menu.findItem(R.id.action_settings).setVisible(false);
        }catch (NullPointerException e){ }
    }

    public void showMenu(){
        menu.findItem(R.id.action_settings).setVisible(true);
    }

    void logout(){
        RealmResults<Login> results = r.allObjects(Login.class);
        if(results!=null && results.size()>0){
            for(int i=0;i<results.size();i++) {
                r.beginTransaction();
                Login login = results.get(i);
                login.removeFromRealm();
                r.commitTransaction();
            }
        }
        Appboy.getInstance(MainActivity.this).closeSession(MainActivity.this);
        finish();
    }

    @Override
    public void onBackPressed() {
        this.showFragment(new OrdersFragment());
    }
}
