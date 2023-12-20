package com.example.shopping.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopping.R;
import com.example.shopping.model.ProductModel;

import java.util.List;

public class OrderProductsAdapter extends RecyclerView.Adapter<OrderProductsAdapter.OrderProductsViewHolder> {
    private final List<ProductModel> ordersList;
    private final OnItemClickListener listener;

    public OrderProductsAdapter(List<ProductModel> ordersList, OnItemClickListener listener) {
        this.ordersList = ordersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_products_list_item, parent, false);
        return new OrderProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProductsViewHolder holder, int position) {
        ProductModel orderItem = ordersList.get(position);
        holder.bind(orderItem, listener);
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public class OrderProductsViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productIv;
        private final TextView productNameTv;
        private final TextView productCountTv;
        private final TextView productTotalPriceTv;

        public OrderProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            productIv = itemView.findViewById(R.id.product_iv);
            productNameTv = itemView.findViewById(R.id.product_name_tv);
            productCountTv = itemView.findViewById(R.id.product_count_tv);
            productTotalPriceTv = itemView.findViewById(R.id.product_total_price_tv);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(ordersList.get(position));
                }
            });
        }

        @SuppressLint("SetTextI18n")
        public void bind(final ProductModel productModel, final OnItemClickListener listener) {
            Glide.with(itemView).load(productModel.getProductImageUrl()).into(productIv);
            productNameTv.setText(productModel.getProductName());
            productCountTv.setText("Products Count: " + productModel.getProductCartCount());
            productTotalPriceTv.setText(productModel.getProductCartCount() * productModel.getProductPrice() + " EGP");

            itemView.setOnClickListener(v -> listener.onItemClick(productModel));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ProductModel orderItem);
    }
}
