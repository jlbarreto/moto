package co.techmov.checkout.data;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import co.techmov.checkout.R;
import co.techmov.checkout.entity.OrderData;

/**
 * Created by developer on 8/20/15.
 */
public class SuccessOrdersAdapter extends RecyclerView.Adapter<SuccessOrdersAdapter.GenericHolder>{
    private List list;

    public SuccessOrdersAdapter(List beans){
        this.list = beans;
    }

    @Override
    public GenericHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_order_data, viewGroup, false);
        return new SuccessOrdersAdapter.GenericHolder(view);
    }

    @Override
    public void onBindViewHolder(GenericHolder holder, int position) {
        OrderData data = (OrderData) list.get(position);
        holder.txtOrderCode.setText("Codigo: #" + data.getCode());
        holder.txtOrderDate.setText(new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(data.getDate()));
        holder.txtOrderZone.setText("Zona: " + data.getZone());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class GenericHolder extends RecyclerView.ViewHolder{
        protected TextView txtOrderCode;
        protected TextView txtOrderDate;
        protected TextView txtOrderZone;
        public GenericHolder(View view){
            super(view);
            txtOrderZone = (TextView) view.findViewById(R.id.txtOrderZone);
            txtOrderDate = (TextView) view.findViewById(R.id.txtOrderDate);
            txtOrderCode = (TextView) view.findViewById(R.id.txtOrderCode);
        }
    }
}