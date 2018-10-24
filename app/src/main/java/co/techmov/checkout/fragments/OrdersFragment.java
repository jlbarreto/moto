package co.techmov.checkout.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appboy.Appboy;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import co.techmov.checkout.R;
import co.techmov.checkout.activities.MainActivity;
import co.techmov.checkout.beans.AddressDetailBean;
import co.techmov.checkout.beans.OrderBean;
import co.techmov.checkout.beans.OrderStatusBean;
import co.techmov.checkout.beans.ProductBean;
import co.techmov.checkout.beans.RestaurantBean;
import co.techmov.checkout.beans.UserBean;
import co.techmov.checkout.beans.ZoneBean;
import co.techmov.checkout.data.OrdersCustomAdapter;
import co.techmov.checkout.entity.Login;
import co.techmov.checkout.entity.ShowView;
import co.techmov.checkout.models.Order;
import co.techmov.checkout.task.ExecutionMethod;
import co.techmov.checkout.task.VoidActivityTask;
import co.techmov.checkout.task.VoidTask;
import co.techmov.checkout.util.Utils;
import io.realm.Realm;

/**
 * Created by Ramon Zuniga on 8/20/15.
 */
public class OrdersFragment extends Fragment implements ExecutionMethod{
    private Context context;
    private View view;
    private RecyclerView recyclerView;
    private CircularProgressView progressView;
    private List<OrderBean> beans;
    private Realm r;
    private MainActivity activity;
    private int motoristaId = 0;
    private TextView message;
    private Login login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_orders, container, false);
        message = (TextView) view.findViewById(R.id.message_);
        setHasOptionsMenu(true);
        context = view.getContext();
        activity = (MainActivity) getActivity();
        progressView = (CircularProgressView) view.findViewById(R.id.loading_);
        recyclerView = (RecyclerView) view.findViewById(R.id.rec_orders_);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        r = Realm.getInstance(context);
        login = r.where(Login.class).findFirst();
        motoristaId = login.getId();
        activity.setTitle(login.getUsername());

        if(activity.getList("orders")==null){
            load();
        }else{
            this.beans = activity.getList("orders");
            configureAdapter(this.beans);
        }

        return view;
    }

    void configureAdapter(List result){
        progressView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        ArrayList<Order> orders = new ArrayList<>();
        for(Object o: result) {
            OrderBean bean = (OrderBean) o;
            Order order = new Order();
            order.setOrderCustomer("Delivery Pidafacil");//AQUI SE MANEJA NOMBRE USUARIO PEDIDO PRINCIPAL
            order.setOrderAddress(bean.getAddress());
            order.setOrderTime(bean.getUpdateAt().split(" ")[1]);
            order.setOrderCode(bean.getCode());
            order.setPayment_method_id(bean.getPayment_method_id());//Metodo de pago
            order.setCustomer_phone(bean.getCustomer_phone());
            orders.add(order);
        }

        OrdersCustomAdapter adapter = new OrdersCustomAdapter(orders, this);
        recyclerView.setAdapter(adapter);
    }

    public void goToOrderDetailFragment(View viewClicked){
        int position = recyclerView.getChildPosition(viewClicked);
        OrderBean bean = beans.get(position);

        ShowView showView = Realm.getInstance(view.getContext())
                .where(ShowView.class).equalTo("orderId", bean.getId()).findFirst();
        if(showView!=null){
            try {
                Fragment fragment = (Fragment) Class.forName(showView.getFragmentClassName()).newInstance();
                ((MainActivity) getActivity()).put("order", bean);
                ((MainActivity) getActivity()).showFragment(fragment);
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            ((MainActivity) getActivity()).put("order", bean);
            ((MainActivity) getActivity()).showFragment(new OrderDetailFragment());
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_refresh){
            if (Utils.checkInternetConnection(getActivity(),
                    Appboy.getInstance(getActivity()))) {
                recyclerView.setVisibility(View.GONE);
                progressView.setVisibility(View.VISIBLE);
                message.setVisibility(View.GONE);
                load();
            }
            return true;
        }
        return false;
    }

    void load(){
        VoidActivityTask task = new VoidActivityTask("/motorista/orders", this, 0) {
            @Override
            public void execute(StringBuilder result) {
                try {
                    JSONObject data = new JSONObject(result.toString());
                    Log.d("ORDERS", data.toString());
                    if(data.getBoolean("status")==true){
                        beans = new ArrayList<>();
                        JSONArray arr = data.getJSONArray("data");
                        for(int i=0;i<arr.length();i++){
                            Utils u = Utils.getInstance();
                            org.json.JSONObject o = (JSONObject) arr.get(i);
                            OrderBean bean = new OrderBean();
                            bean.setId(o.getInt("order_id"));
                            bean.setPayment_method_id(o.getString("payment_method_id"));//tipo de pago
                            bean.setCustomer_phone(o.getString("customer_phone"));
                            bean.setRestaurantId(o.getInt("restaurant_id"));
                            bean.setCode(o.getString("order_cod"));
                            bean.setAddress(o.getString("address"));
                            bean.setOrderTotal(u.toFloat(o.get("order_total")));
                            bean.setShippingCharge(u.toFloat(o.get("shipping_charge")));
                            bean.setPayChange(u.toFloat(o.get("pay_change")));
                            bean.setCreateAt(o.getString("created_at"));
                            bean.setUpdateAt(o.getString("updated_at"));
                            bean.setAddressId(o.getInt("address_id"));
                            JSONObject addr = o.getJSONObject("address_detail");
                            JSONObject zone = addr.getJSONObject("zone");
                            bean.setAddressDetailBean(new AddressDetailBean(addr.getInt("user_id"),
                                    addr.getInt("zone_id"), new ZoneBean(zone.getInt("zone_id"),
                                    zone.getString("zone"), zone.getInt("municipality_id")),
                                    addr.getString("reference"),addr.getString("map_coordinates")));
                            JSONObject user = o.getJSONObject("user");

                            String userName;
                            String userLastName = "";

                            if (o.getString("customer").equals("null") ||
                                    o.getString("customer").length() < 2) {
                                userName = user.getString("name");
                                userLastName = user.getString("last_name");
                            } else {
                                userName = o.getString("customer");
                            }
                            bean.setUser(new UserBean(user.getInt("user_id"),
                                    userName,
                                    userLastName,
                                    user.getString("email")));
                            JSONObject rest = o.getJSONObject("restaurant");
                            bean.setRestaurantBean(new RestaurantBean(rest.getString("name"),
                                    u.toFloat(rest.getString("commission_percentage")), rest.getString("address"),
                                    rest.getString("phone"), rest.getString("map_coordinates")));
                            JSONObject status =  o.getJSONObject("status");
                            bean.setOrderStatusBean(new OrderStatusBean(status.getInt("order_status_id"),
                                    status.getString("comment"), status.getString("created_at")));
                            bean.setTotal(u.toFloat(o.getString("total")));
                            bean.setPayToRestaurant(u.toFloat(o.getString("pay_to_restaurant")));
                            JSONArray prods = o.getJSONArray("products");
                            for(int j=0;j<prods.length();j++){
                                JSONObject prod = prods.getJSONObject(j);
                                bean.add(new ProductBean(prod.getString("product"), prod.getInt("quantity"),
                                        u.toFloat(prod.getString("unit_price")), u.toFloat(prod.getString("total_price")),
                                        prod.getString("comment")));
                            }

                            if(bean.getOrderStatusBean().getId() == 3 || bean.getOrderStatusBean().getId()==4)
                                beans.add(bean);
                        }
                        this.resultList = beans;
                    }
                } catch (JSONException e) {
                    Log.d("PARSE-ERROR", e.getMessage());
                }
            }
        };

        task.addParam("motorista_id", String.valueOf(motoristaId));
        task.execute();
    }

    @Override
    public void executeResult(List result, int operationCode) {
        if(result!=null && result.size()>0){
            message.setVisibility(View.GONE);
            activity.put("orders", result);
            configureAdapter(result);
        }else if(result!=null) {
            if(result.size() == 0){
                recyclerView.setVisibility(View.GONE);
                progressView.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                message.setText(getString(R.string.no_orders_assigned));
            }
        }
    }
}
