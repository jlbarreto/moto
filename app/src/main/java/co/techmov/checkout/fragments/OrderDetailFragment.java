package co.techmov.checkout.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.appboy.Appboy;
import com.joanzapata.iconify.widget.IconTextView;

import co.techmov.checkout.R;
import co.techmov.checkout.activities.MainActivity;
import co.techmov.checkout.activities.MapActivity;
import co.techmov.checkout.activities.OrderDetailActivity;
import co.techmov.checkout.beans.OrderBean;
import co.techmov.checkout.beans.ProductBean;
import co.techmov.checkout.entity.ShowView;
import co.techmov.checkout.util.UIHelper;
import co.techmov.checkout.util.Utils;
import io.realm.Realm;
import io.realm.exceptions.RealmException;
/**
 * Created by Ramon Zuniga on 8/21/15.
 */
public class OrderDetailFragment extends Fragment implements View.OnClickListener{
    private View view;
    private MainActivity activity;
    private OrderBean bean;
    private TextView customer;
    private TextView zone;
    private TextView address;
    private TextView refAddress;
    private TextView orderDetail;
    private TextView restaurant;
    private TextView restaurantAddress;
    private TextView restaurantTotal;
    private TextView customerTotal;
    private TextView customerChange;
    private IconTextView iconPayment_method;
    private String endPoint;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_stage_one, container, false);
        activity = (MainActivity) getActivity();
        activity.hideRefresh();
        activity.hideMenu();
        bean = (OrderBean) activity.get("order");
        activity.setTitle(bean.getCode());
        customer = (TextView) view.findViewById(R.id.txtOrderDCustomer);
        zone = (TextView) view.findViewById(R.id.txtOrderDZone);
        address = (TextView) view.findViewById(R.id.txtOrderDAddress);
        refAddress = (TextView) view.findViewById(R.id.txtOrderDRefAddress);
        orderDetail = (TextView) view.findViewById(R.id.txtOrderProducts);
        restaurant = (TextView) view.findViewById(R.id.txtOrderDResName);
        restaurantAddress = (TextView) view.findViewById(R.id.txtOrderDResAddress);
        restaurantTotal = (TextView) view.findViewById(R.id.txtOrderDResTotal);
        customerTotal = (TextView) view.findViewById(R.id.txtOrderDCustomerTotal);
        customerChange = (TextView) view.findViewById(R.id.txtOrderDCustomerChange);
        iconPayment_method = (IconTextView) view.findViewById(R.id.icon_credit_card);
        endPoint = bean.getAddressDetailBean().getCoordinates();

        Realm r = Realm.getInstance(view.getContext());
        try{
            r.beginTransaction();
            ShowView showView = r.createObject(ShowView.class);
            showView.setOrderId(bean.getId());
            showView.setFragmentClassName(OrderDetailFragment.class.getName());
            r.commitTransaction();
        }catch (RealmException e){
            e.printStackTrace();
            r.cancelTransaction();
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


        ///iconPayment_method2.setText("{fa-money}");
        customer.setText("Delivery Pidafacil  ");//Nombre del cliente antes de aceptar el pedido
        zone.setText(bean.getAddressDetailBean().getZoneBean().getZone());
        address.setText(bean.getAddress());
        refAddress.setText(bean.getAddressDetailBean().getReference());

        StringBuilder builder = new StringBuilder("");
        for(ProductBean bean: this.bean.getProductBeans()){
            builder.append("" + bean.getQuantity() + " " + bean.getProduct());
            if( bean.getUserComment() != null && !bean.getUserComment().equals(""))
                builder.append("(").append(bean.getUserComment()).append(")");
            builder.append("\n");
        }

        orderDetail.setText((builder.toString()));
        restaurant.setText(bean.getRestaurantBean().getName());
        restaurantAddress.setText(bean.getRestaurantBean().getAddress());
        float commision = bean.getRestaurantBean().getCommisionPercentage();
        restaurantTotal.setText("$"+Utils.decimalFormatString(bean.getOrderTotal() - ((commision/100) * bean.getOrderTotal()) ));
        customerTotal.setText("$"+ Utils.decimalFormatString(bean.getTotal()));
        customerChange.setText("$"+Utils.decimalFormatString(bean.getPayChange()));

        view.findViewById(R.id.button_acept).setOnClickListener(this);
        view.findViewById(R.id.frame_map).setOnClickListener(this);
        view.findViewById(R.id.llOrderDetail).setOnClickListener(this);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MENU", " Item "+ item.getItemId());
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.frame_map : {
                if (Utils.checkGPSNetworkServices(getActivity(),
                        Appboy.getInstance(getActivity()))) {
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
            case R.id.button_acept :{
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(null);
                builder.setMessage("¿Seguro que deseas aceptar este pedido?");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(view.getContext(), "Pedido aceptado", Toast.LENGTH_SHORT).show();
                        activity.showFragment(new RestaurantFragment());
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            }
        }
    }
}
