package co.techmov.checkout.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appboy.Appboy;
import com.joanzapata.iconify.widget.IconTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.techmov.checkout.R;
import co.techmov.checkout.activities.MainActivity;
import co.techmov.checkout.activities.MapActivity;
import co.techmov.checkout.activities.OrderDetailActivity;
import co.techmov.checkout.beans.OrderBean;
import co.techmov.checkout.beans.ProductBean;
import co.techmov.checkout.entity.Login;
import co.techmov.checkout.entity.OrderData;
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
public class ClientFragment extends Fragment implements ExecutionMethod, View.OnClickListener{
    private View view;
    private TextView client;
    private TextView zone;
    private TextView address;
    private TextView refAddress;
    private TextView orderDetails;
    private OrderBean bean;
    private MainActivity activity;
    private Realm realm;
    private Login login;
    private TextView orderTotal;
    private TextView payChange;
    private String pushTitle;
    private String pushContent;
    private IconTextView iconPayment_method;
    private TextView CustomerPhone;
    private ProgressDialog progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_stage_three, container, false);
        activity = (MainActivity) getActivity();
        activity.hideRefresh();
        activity.hideMenu();
        bean = (OrderBean) activity.get("order");
        activity.setTitle(bean.getCode());

        client = (TextView) view.findViewById(R.id.txtOrderDCustomer);
        zone = (TextView) view.findViewById(R.id.txtOrderDZone);
        address = (TextView) view.findViewById(R.id.txtOrderDAddress);
        refAddress = (TextView) view.findViewById(R.id.txtOrderDRefAddress);
        orderDetails = (TextView) view.findViewById(R.id.txtOrderDProducts);
        orderTotal = (TextView) view.findViewById(R.id.txtOrderDTotalRes);
        payChange = (TextView) view.findViewById(R.id.txtOrderDTotalCustomer);
        CustomerPhone = (TextView) view.findViewById(R.id.txtCustomerPhone);
        iconPayment_method = (IconTextView) view.findViewById(R.id.icon_credit_card);

        client.setText(bean.getUser().getName().concat(" ").concat(bean.getUser().getLastName()));
        zone.setText(bean.getAddressDetailBean().getZoneBean().getZone());
        address.setText(bean.getAddress());
        refAddress.setText(bean.getAddressDetailBean().getReference());
        orderTotal.setText("$"+Utils.decimalFormatString(bean.getTotal()));
        payChange.setText("$"+Utils.decimalFormatString(bean.getPayChange()));



        CustomerPhone.setText(bean.getCustomer_phone());

        realm = Realm.getInstance(view.getContext());
        login = realm.where(Login.class).findFirst();
        realm.beginTransaction();
        ShowView showView = realm.where(ShowView.class).equalTo("orderId", bean.getId()).findFirst();
        showView.setFragmentClassName(ClientFragment.class.getName());
        realm.commitTransaction();

        StringBuilder builder = new StringBuilder("");
        for(ProductBean bean: this.bean.getProductBeans()){
            builder.append("" + bean.getQuantity() + " " + bean.getProduct());
            if( bean.getUserComment() != null && !bean.getUserComment().equals(""))
                builder.append("(").append(bean.getUserComment()).append(")");
            builder.append("\n");
        }

        switch(bean.getPayment_method_id()){
            case "1"://holder.txtOrderpayment_method_id.setText("Tipo pago: efectivo");
                iconPayment_method.setText("{fa-money}");
                iconPayment_method.setTextColor(0xFF008000);


                break;
            case "2"://holder.txtOrderpayment_method_id.setText("Tipo pago: tarjeta ");
                iconPayment_method.setText("{fa-credit-card}");
                iconPayment_method.setTextColor(0xFF337eff);

                break;
            case "3"://txtOrderpayment_method_id.setText("Metodo " + order.getPayment_method_id());
                break;
            case "4"://holder.txtOrderpayment_method_id.setText("Metodo " + order.getPayment_method_id());
                break;
            default: //holder.txtOrderpayment_method_id.setText("Metodo No registrado ");
        }



        orderDetails.setText((builder.toString()));
        view.findViewById(R.id.button_acept).setOnClickListener(this);
        view.findViewById(R.id.button_reject).setOnClickListener(this);
        view.findViewById(R.id.frame_map).setOnClickListener(this);
        view.findViewById(R.id.llOrderDetail).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_acept : {
                if (Utils.checkGPSNetworkServices(getActivity(), Appboy.getInstance(getActivity()))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(null);
                    builder.setMessage("¿Seguro que deseas completar este pedido?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progress = ProgressDialog.show(view.getContext(), null, "Espere...", true);
                            int orderId = bean.getId();
                            int orderStatusId = (5);
                            VoidActivityTask task = getChangeStatusTask(0);
                            task.addParam("motorista_id", String.valueOf(login.getId()));
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
            case R.id.button_reject : {
                if (Utils.checkGPSNetworkServices(getActivity(), Appboy.getInstance(getActivity()))) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    LayoutInflater inflater = activity.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.dialog_reject_reason, null);
                    builder.setView(dialogView);
                    builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText editText = (EditText) dialogView.findViewById(R.id.etxt_rej_reason);
                            String reason = editText.getText().toString();
                            //Validando motivo de la cancelacion del pedido desde la app de a moto
                            if (!reason.isEmpty()||reason.equals(null) )
                                {
                                    int orderId = bean.getId();
                                    int orderStatusId = (7);
                                    dialog.dismiss();
                                    progress = ProgressDialog.show(activity, null, "Espere...", true);

                                    VoidActivityTask task = getChangeStatusTask(2);
                                    task.addParam("motorista_id", String.valueOf(login.getId()));
                                    task.addParam("user_id", String.valueOf(login.getUserId()));
                                    task.addParam("order_id", String.valueOf(orderId));
                                    task.addParam("order_status_id", String.valueOf(orderStatusId));
                                    task.addParam("comment", reason);
                                    task.execute();
                                }
                            else {
                                Context context = getActivity().getApplicationContext();
                                CharSequence text = "Digite la razón para cancelar el pedido!";
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();

                                 }//fin else validacion razón rechazo
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                }
                break;
            }
            case R.id.frame_map : {
                if (Utils.checkGPSNetworkServices(getActivity(),
                        Appboy.getInstance(getActivity()))) {
                    String endPoint = bean.getAddressDetailBean().getCoordinates();
                    Log.d("INFO", "starting map activity.. with user coordiate " + endPoint);
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

    @Override
    public void executeResult(List result, int operationCode) {
        if(operationCode == 0 ){
            if(result!=null && result.size()>0){
                updatePoint();
            }else{
                progress.dismiss();
                Toast.makeText(activity,"Error",Toast.LENGTH_LONG).show();
            }
        }else if(operationCode == 2){
            progress.dismiss();
            Intent homeIntent = new Intent(view.getContext(), MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
    }

    LocationManager locationManager;
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            VoidTask pointTask = new VoidTask("/addresses/update-coordinates");
            pointTask.addParam("address_id", String.valueOf(bean.getAddressId()));
            pointTask.addParam("map_coordinates", location.getLatitude() + "," + location.getLongitude());
            pointTask.execute();
            userNotification();

            realm.beginTransaction();
            OrderData data = realm.createObject(OrderData.class);
            data.setCode(bean.getCode());
            data.setDate(new Date());
            data.setZone(bean.getAddressDetailBean().getZoneBean().getZone());
            realm.commitTransaction();

            progress.dismiss();
            Toast.makeText(view.getContext(), "Orden entregada", Toast.LENGTH_LONG).show();
            Intent homeIntent = new Intent(view.getContext(), MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    public VoidActivityTask getChangeStatusTask(int operationCode){
        return new VoidActivityTask("/order/change_status", this, operationCode) {
            @Override
            public void execute(StringBuilder result) {
                try {
                    JSONObject data = new JSONObject(result.toString());
                    if(data !=null && data.getBoolean("status")){
                        this.resultList = new ArrayList();
                        this.resultList.add(data.getBoolean("status"));
                    }
                } catch (JSONException e) { }
            }
        };
    }

    void userNotification(){
        pushTitle = getString(R.string.push_title);
        pushContent = getString(R.string.push_content);
        AppboyUtil appboyUtil = new AppboyUtil();
        appboyUtil.sendMessage(bean.getUser().getEmail(), pushTitle, pushContent);
    }

    void updatePoint(){
        locationManager= (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
    }

}
