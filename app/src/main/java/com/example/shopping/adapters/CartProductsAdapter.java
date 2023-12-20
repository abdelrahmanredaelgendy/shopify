package com.example.shopping.adapters;

import android.content.Context;
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

public class CartProductsAdapter extends RecyclerView.Adapter<CartProductsAdapter.ProductsViewHolder> {

    private Context context;
    private final List<ProductModel> productsList;
    private final CartProductsAdapter.OnItemClickListener listener;

    public CartProductsAdapter(Context context, List<ProductModel> productsList, CartProductsAdapter.OnItemClickListener listener) {
        this.productsList = productsList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public CartProductsAdapter.ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_cart_list_item, parent, false);
        return new CartProductsAdapter.ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartProductsAdapter.ProductsViewHolder holder, int position) {
        ProductModel product = productsList.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageIv, deleteProductIv, increaseProductIv, decreaseProductIv;
        private final TextView productNameTv, productPriceTv, productCountTv;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTv = itemView.findViewById(R.id.product_name_tv);
            productPriceTv = itemView.findViewById(R.id.product_price_tv);
            productCountTv = itemView.findViewById(R.id.product_count_tv);

            productImageIv = itemView.findViewById(R.id.product_image_iv);
            deleteProductIv = itemView.findViewById(R.id.product_delete_iv);
            increaseProductIv = itemView.findViewById(R.id.increase_count_iv);
            decreaseProductIv = itemView.findViewById(R.id.decrease_count_iv);

            // Add delete click listener
            deleteProductIv.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteClick(productsList.get(position), position);
                }
            });

            increaseProductIv.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onIncreaseProductClick(productsList.get(position), position);
                }
            });

            decreaseProductIv.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDecreaseProductClick(productsList.get(position), position);
                }
            });
        }

        public void bind(final ProductModel productModel, final CartProductsAdapter.OnItemClickListener listener) {
            productNameTv.setText(productModel.getProductName());
            Glide.with(itemView.getContext()).load(productModel.getProductImageUrl()).into(productImageIv);
            productPriceTv.setText(String.valueOf(productModel.getProductPrice() * productModel.getProductCartCount()));
            productCountTv.setText(String.valueOf(productModel.getProductCartCount()));
            itemView.setOnClickListener(v -> listener.onItemClick(productModel));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ProductModel product);

        void onDeleteClick(ProductModel product, int position);

        void onIncreaseProductClick(ProductModel product, int position);

        void onDecreaseProductClick(ProductModel product, int position);

    }
}
