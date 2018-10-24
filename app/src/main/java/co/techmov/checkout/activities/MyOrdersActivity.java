package co.techmov.checkout.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import co.techmov.checkout.R;
import co.techmov.checkout.data.SuccessOrdersAdapter;
import co.techmov.checkout.entity.Login;
import co.techmov.checkout.entity.OrderData;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by victor on 09-10-15.
 */
public class MyOrdersActivity extends FragmentActivity {
    private RecyclerView recyclerViewOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorders);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerViewOrders = (RecyclerView) findViewById(R.id.rec_myorders_);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        Realm r = Realm.getInstance(this);
        setTitle(r.where(Login.class).findFirst().getUsername() + " - " + getString(R.string.orders_completed));
        RealmResults results = r.where(OrderData.class).findAll();
        results.sort("date", false);
        Iterator it = results.iterator();
        boolean hastItems = it.hasNext();
        ArrayList list = new ArrayList();
        while(it.hasNext())
            list.add(it.next());

        recyclerViewOrders.setAdapter(new SuccessOrdersAdapter(list));

        if(!hastItems){
            findViewById(R.id.message_).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.message_)).setText(getString(R.string.no_orders));
            recyclerViewOrders.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
