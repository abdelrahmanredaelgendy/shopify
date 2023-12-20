package com.example.shopping.adapters;

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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductsViewHolder> {

    private boolean isAdmin;
    private final List<ProductModel> productsList;
    private final ProductAdapter.OnItemClickListener listener;

    public ProductAdapter(boolean isAdmin, List<ProductModel> productsList, ProductAdapter.OnItemClickListener listener) {
        this.productsList = productsList;
        this.listener = listener;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_list_item, parent, false);
        return new ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        ProductModel product = productsList.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder {
        ImageView productIV, editImageView;
        private final TextView productTV, productPriceTV;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            productIV = itemView.findViewById(R.id.product_iv);
            productTV = itemView.findViewById(R.id.product_tv);
            ImageView deleteImageView = itemView.findViewById(R.id.deleteImageView);
            editImageView = itemView.findViewById(R.id.editImageView);
            productPriceTV = itemView.findViewById(R.id.product_pr);

            if (isAdmin) {
                editImageView.setVisibility(View.VISIBLE);
                deleteImageView.setVisibility(View.VISIBLE);
            } else {
                editImageView.setVisibility(View.GONE);
                deleteImageView.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(productsList.get(position));
                }
            });
            deleteImageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteClick(productsList.get(position), position);
                }
            });
            editImageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEditClick(productsList.get(position));
                }
            });
        }

        public void bind(final ProductModel productModel, final ProductAdapter.OnItemClickListener listener) {
            productTV.setText(productModel.getProductName());
            Glide.with(itemView.getContext()).load(productModel.getProductImageUrl()).into(productIV);
            productPriceTV.setText(String.valueOf(productModel.getProductPrice()));
            itemView.setOnClickListener(v -> listener.onItemClick(productModel));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ProductModel product);

        void onDeleteClick(ProductModel product, int position);

        void onEditClick(ProductModel product);

    }
}
