package co.techmov.checkout.data;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;

import co.techmov.checkout.R;
import co.techmov.checkout.fragments.OrdersFragment;
import co.techmov.checkout.models.Order;

/**
 * Created by developer on 8/20/15.
 */
public class OrdersCustomAdapter extends RecyclerView.Adapter<OrdersCustomAdapter.GenericHolder>{
    private List list;
    private OrdersFragment fragment;

    public OrdersCustomAdapter(List beans, OrdersFragment fragment){
        this.list = beans;
        this.fragment = fragment;
    }

    @Override
    public GenericHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.order, viewGroup, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.goToOrderDetailFragment(v);
            }
        });

        return new GenericHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GenericHolder holder, int position) {
        Order order = (Order) list.get(position);
        holder.txtOrderCustomer.setText(order.getOrderCustomer());
        holder.txtOrderAddress.setText(order.getOrderAddress());
        holder.txtOrderTime.setText(order.getOrderTime());
        holder.txtOrderCode.setText("#" + order.getOrderCode());

        switch(order.getPayment_method_id()){
            case "1"://holder.txtOrderpayment_method_id.setText("Tipo pago: efectivo");
                     holder.iconPayment_method.setText("{fa-money}");
                     holder.iconPayment_method.setTextColor(0xFF008000);

                break;
            case "2"://holder.txtOrderpayment_method_id.setText("Tipo pago: tarjeta ");
                     holder.iconPayment_method.setText("{fa-credit-card}");
                     holder.iconPayment_method.setTextColor(0xFF337eff);
                break;
            case "3":holder.txtOrderpayment_method_id.setText("Metodo " + order.getPayment_method_id());
                break;
            case "4":holder.txtOrderpayment_method_id.setText("Metodo " + order.getPayment_method_id());
                break;
            default: holder.txtOrderpayment_method_id.setText("Metodo No registrado ");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class GenericHolder extends RecyclerView.ViewHolder{
        protected TextView txtOrderCustomer;
        protected TextView txtOrderAddress;
        protected TextView txtOrderTime;
        protected TextView txtOrderCode;
        protected TextView txtOrderpayment_method_id;
        protected IconTextView iconPayment_method;
        protected IconTextView iconCash;
        public GenericHolder(View view){
            super(view);
            txtOrderCustomer = (TextView) view.findViewById(R.id.txtOrderCustomer);
            txtOrderAddress = (TextView) view.findViewById(R.id.txtOrderAddress);
            txtOrderTime = (TextView) view.findViewById(R.id.txtOrderTime);
            txtOrderCode = (TextView) view.findViewById(R.id.txtOrderCode);
            txtOrderpayment_method_id =(TextView) view.findViewById(R.id.payment_method_id);
            iconPayment_method = (IconTextView)view.findViewById(R.id.icon_credit_card);
        }
    }


}