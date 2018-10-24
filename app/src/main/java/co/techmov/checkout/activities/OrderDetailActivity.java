package co.techmov.checkout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.json.JSONException;

import java.util.List;

import co.techmov.checkout.R;
import co.techmov.checkout.beans.BasicProductBean;
import co.techmov.checkout.components.CustomLinearLayoutManager;
import co.techmov.checkout.data.ProductsOrderAdapter;
import co.techmov.checkout.task.VoidTask;
import co.techmov.checkout.util.JsonHelper;

/**
 * Created by victor on 09-07-15.
 */
public class OrderDetailActivity extends FragmentActivity {
    private RecyclerView products;
    private CircularProgressView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_products);
        setTitle("");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        loading = (CircularProgressView) findViewById(R.id.loading_);
        products = (RecyclerView) findViewById(R.id.rec_products);
        products.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String orderId = String.valueOf(bundle.getInt("order_id"));

        VoidTask task = new VoidTask("/order/get");
        task.addParam("order_id", orderId);
        task.setPostExecute(new VoidTask.PostExecute() {
            @Override
            public void execute(StringBuilder response) {
                Log.d("WS-RESULT", response.toString());
                try {
                    org.json.JSONObject data = new org.json.JSONObject(response.toString());
                    org.json.JSONObject d = data.getJSONObject("data");
                    List<BasicProductBean> beans = JsonHelper.parseSimpleProducts(d);
                    setTitle(d.getString("order_cod"));
                    products.setAdapter(new ProductsOrderAdapter(beans));
                    loading.setVisibility(View.GONE);
                    findViewById(R.id.content_).setVisibility(View.VISIBLE);
                } catch (JSONException e) { }
            }
        });
        task.execute();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
