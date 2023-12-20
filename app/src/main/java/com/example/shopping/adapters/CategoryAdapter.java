package com.example.shopping.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopping.R;
import com.example.shopping.model.CategoryModel;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.SubCatViewHolder> {

    private Context context;
    private final List<CategoryModel> subCategories;
    private final OnItemClickListener listener;
    private SharedPreferences sharedPreferences;


    public CategoryAdapter(Context context, List<CategoryModel> subCategories, OnItemClickListener listener) {
        this.subCategories = subCategories;
        this.listener = listener;
        this.context = context;
        sharedPreferences = context.getSharedPreferences("adminPref", Context.MODE_PRIVATE);

    }

    @NonNull
    @Override
    public SubCatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_list_item, parent, false);
        return new SubCatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCatViewHolder holder, int position) {
        CategoryModel subCategory = subCategories.get(position);
        holder.bind(subCategory, listener);
    }

    @Override
    public int getItemCount() {
        return subCategories.size();
    }

    public class SubCatViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryIV, editImageView;
        private final TextView categoryTV;

        public SubCatViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIV = itemView.findViewById(R.id.category_iv);
            categoryTV = itemView.findViewById(R.id.category_tv);
            ImageView deleteImageView = itemView.findViewById(R.id.deleteImageView);
            editImageView = itemView.findViewById(R.id.editImageView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(subCategories.get(position));
                }
            });

            // Add delete click listener
            deleteImageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteClick(subCategories.get(position), position);
                }
            });

            editImageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEditClick(subCategories.get(position));
                }
            });

            boolean admin = sharedPreferences.getBoolean("admin", false);
            deleteImageView.setVisibility(!admin ? View.GONE : View.VISIBLE);
            editImageView.setVisibility(!admin ? View.GONE : View.VISIBLE);
        }

        public void bind(final CategoryModel categoryModel, final OnItemClickListener listener) {
            categoryTV.setText(categoryModel.getCatName());
            Glide.with(itemView.getContext()).load(categoryModel.getCatImageUrl()).into(categoryIV);
            itemView.setOnClickListener(v -> listener.onItemClick(categoryModel));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(CategoryModel subCategory);

        void onDeleteClick(CategoryModel category, int position);

        void onEditClick(CategoryModel category);

    }
}
