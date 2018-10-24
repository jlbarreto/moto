package co.techmov.checkout.data;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.techmov.checkout.R;
import co.techmov.checkout.beans.BasicProductBean;

/**
 * Created by victor on 04-14-15.
 */
public class ProductsOrderAdapter extends RecyclerView.Adapter<ProductsOrderAdapter.OrderHolder> {

    private List beans;

    public ProductsOrderAdapter(List beans) {
        this.beans = beans;
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_detail, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderHolder holder, int position) {
        BasicProductBean bean = (BasicProductBean) beans.get(position);
        holder.textViewName.setText(bean.getDetail());

        if(bean.getIngredients()==null || bean.getComments().trim().equals(""))
            holder.textViewComment.setVisibility(View.GONE);
        else
            holder.textViewComment.setText(bean.getComments());

        if(bean.getIngredients()==null || bean.getIngredients().trim().equals(""))
            holder.textViewIngredients.setVisibility(View.GONE);
        else
            holder.textViewIngredients.setText((bean.getIngredients()
                    .substring(0,bean.getIngredients().length()-1)));

        if(bean.getConditions()==null || bean.getConditions().trim().equals(""))
            holder.textViewConditions.setVisibility(View.GONE);
        else
            holder.textViewConditions.setText(bean.getConditions()
                    .substring(0,bean.getConditions().length()-1));
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    public class OrderHolder extends RecyclerView.ViewHolder{

        protected TextView textViewName;
        protected TextView textViewComment;
        protected TextView textViewIngredients;
        protected TextView textViewConditions;

        public OrderHolder(View view){
            super(view);
            this.textViewName = (TextView) view.findViewById(R.id.txtProductName);
            this.textViewIngredients = (TextView) view.findViewById(R.id.txtProductIngredients);
            this.textViewComment = (TextView) view.findViewById(R.id.txtProductComment);
            this.textViewConditions = (TextView) view.findViewById(R.id.txtProductConditions);
        }

    }
}
