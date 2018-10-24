package co.techmov.checkout.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.appboy.Appboy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.techmov.checkout.R;
import co.techmov.checkout.activities.MainActivity;
import co.techmov.checkout.activities.MapActivity;
import co.techmov.checkout.activities.OrderDetailActivity;
import co.techmov.checkout.beans.OrderBean;
import co.techmov.checkout.beans.ProductBean;
import co.techmov.checkout.entity.Login;
import co.techmov.checkout.entity.ShowView;
import co.techmov.checkout.task.ExecutionMethod;
import co.techmov.checkout.task.VoidActivityTask;
import co.techmov.checkout.task.VoidTask;
import co.techmov.checkout.util.AppboyUtil;
import co.techmov.checkout.util.UIHelper;
import co.techmov.checkout.util.Utils;
import io.realm.Realm;

/**
 * Created by victor on 09-01-15.
 */
public class RestaurantFragment extends Fragment implements ExecutionMethod, View.OnClickListener {

    private View view;
    private Context context;
    private MainActivity activity;
    private TextView restaurant;
    private TextView restaurantAddress;
    private TextView orderDetail;
    private TextView restaurantTotal;
    private OrderBean bean;
    private Login login;
    private ProgressDialog progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_stage_two, container, false);
        activity = (MainActivity) getActivity();
        activity.hideRefresh();
        activity.hideMenu();
        bean = (OrderBean) activity.get("order");
        activity.setTitle(bean.getCode());

        float commision = bean.getRestaurantBean().getCommisionPercentage();

        context = activity.getApplicationContext();
        restaurant = (TextView) view.findViewById(R.id.txtOrderDResName);
        restaurantAddress = (TextView) view.findViewById(R.id.txtOrderDAddress);
        orderDetail = (TextView) view.findViewById(R.id.txtOrderDProducts);
        restaurantTotal = (TextView) view.findViewById(R.id.txtOrderDTotalRes);
        restaurant.setText(bean.getRestaurantBean().getName());
        restaurantAddress.setText(bean.getRestaurantBean().getAddress());
        restaurantTotal.setText("$" + Utils.decimalFormatString(
                bean.getOrderTotal() -((commision/100) * bean.getOrderTotal()) ));

        Realm realm = Realm.getInstance(view.getContext());
        login = realm.where(Login.class).findFirst();
        realm.beginTransaction();
        ShowView showView = realm.where(ShowView.class).equalTo("orderId", bean.getId()).findFirst();
        showView.setFragmentClassName(RestaurantFragment.class.getName());
        realm.commitTransaction();

        StringBuilder builder = new StringBuilder("");
        for(ProductBean bean: this.bean.getProductBeans()){
            builder.append(bean.getQuantity() + " " + bean.getProduct());
            if( bean.getUserComment() != null && !bean.getUserComment().equals("")){
                builder.append("(").append(bean.getUserComment()).append(")");
            }
            builder.append("\n");
        }

        orderDetail.setText((builder.toString()));
        view.findViewById(R.id.button_acept).setOnClickListener(this);
        view.findViewById(R.id.frame_map).setOnClickListener(this);
        view.findViewById(R.id.llOrderDetail).setOnClickListener(this);
        return view;
    }

    public VoidActivityTask getChangeStatusTask(){
        return new VoidActivityTask("/order/change_status", this, 0) {
            @Override
            public void execute(StringBuilder result) {
                try {
                    Log.d("WS-RESULT", result.toString());
                    JSONObject data = new JSONObject(result.toString());
                    if(data !=null && data.getBoolean("status")){
                        data = data.getJSONObject("data");
                        this.resultList = new ArrayList();
                        this.resultList.add(data.getString("comment"));
                    }
                } catch (JSONException e) { e.printStackTrace(); }
            }
        };
    }

    @Override
    public void executeResult(List result, int operationCode) {
        if(operationCode == 0 ){
            if(result!=null && result.size()>0){
                updatePoint();
            }
        }
    }

    LocationManager locationManager;
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            VoidTask pointTask = new VoidTask("/restaurant/update-coordinates");
            pointTask.addParam("restaurant_id", String.valueOf(bean.getRestaurantId()));
            pointTask.addParam("map_coordinates", location.getLatitude() + "," + location.getLongitude());
            pointTask.execute();
            userNotification();
            progress.dismiss();
            Toast.makeText(view.getContext(), "Pedido recogido", Toast.LENGTH_SHORT).show();
            activity.showFragment(new ClientFragment());
            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };

    void userNotification(){
        String pushTitle = getString(R.string.push_title);
        String pushContent = getString(R.string.push_order_picked);
        AppboyUtil appboyUtil = new AppboyUtil();
        appboyUtil.sendMessage(bean.getUser().getEmail(), pushTitle, pushContent);
    }

    void updatePoint(){
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.button_acept) : {
                if (Utils.checkGPSNetworkServices(getActivity(), Appboy.getInstance(getActivity()))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(null);
                    builder.setMessage("¿Seguro que deseas recoger este pedido?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progress = ProgressDialog.show(view.getContext(), null, "Espere...", true);
                            int orderId = bean.getId();
                            int orderStatusId = (4);
                            VoidActivityTask task = getChangeStatusTask();
                            task.addParam("user_id", String.valueOf(login.getUserId()));
                            task.addParam("order_id", String.valueOf(orderId));
                            task.addParam("order_status_id", String.valueOf(orderStatusId));
                            task.addParam("comment", "");
                            task.execute();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
                break;
            }
            case R.id.frame_map : {
                if (Utils.checkGPSNetworkServices(getActivity(),
                        Appboy.getInstance(getActivity()))) {
                    String endPoint = bean.getRestaurantBean().getCoordinates();
                    Log.d("INFO", "starting map activity.. with restaurant coordiate " + endPoint);
                    if(endPoint ==null || endPoint.trim().equals("") || endPoint.trim().equals("0")){
                        UIHelper.alertDialog(activity, null, "Ruta no disponible").show();
                    }else{
                        Intent mapIntent = new Intent(v.getContext(), MapActivity.class);
                        mapIntent.putExtra("haveAllPoints", false);
                        mapIntent.putExtra("endPoint", endPoint);
                        startActivity(mapIntent);
                    }
                }
                break;
            }
            case R.id.llOrderDetail :{
                Intent detailIntent = new Intent(activity, OrderDetailActivity.class);
                detailIntent.putExtra("order_id", bean.getId());
                startActivity(detailIntent);
                break;
            }
        }
    }
}
