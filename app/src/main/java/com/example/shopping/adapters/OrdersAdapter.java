package com.example.shopping.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.R;
import com.example.shopping.model.CartModel;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.SubCatViewHolder> {
    private final List<CartModel> ordersList;
    private final OnItemClickListener listener;

    public OrdersAdapter(List<CartModel> ordersList, OrdersAdapter.OnItemClickListener listener) {
        this.ordersList = ordersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrdersAdapter.SubCatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_list_item, parent, false);
        return new OrdersAdapter.SubCatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.SubCatViewHolder holder, int position) {
        CartModel orderItem = ordersList.get(position);
        holder.bind(orderItem, listener);
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public class SubCatViewHolder extends RecyclerView.ViewHolder {
        private final TextView orderIdTv;
        private final TextView productsCountTv;
        private final TextView totalPriceTv;

        public SubCatViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTv = itemView.findViewById(R.id.order_id_tv);
            productsCountTv = itemView.findViewById(R.id.products_count_tv);
            totalPriceTv = itemView.findViewById(R.id.order_total_price_tv);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(ordersList.get(position));
                }
            });
        }

        @SuppressLint("SetTextI18n")
        public void bind(final CartModel cartModel, final OnItemClickListener listener) {
            orderIdTv.setText(cartModel.getUid());
            productsCountTv.setText("Products Count: " + cartModel.getProductsCount());
            totalPriceTv.setText(cartModel.getTotalPrice() + " EGP");

            itemView.setOnClickListener(v -> listener.onItemClick(cartModel));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(CartModel orderItem);
    }
}
