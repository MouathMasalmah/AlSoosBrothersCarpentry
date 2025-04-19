package com.example.alsoosbrotherscarpentry;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private final ArrayList<Product> productList;

    public ProductAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productNameTextView.setText(product.getName());

        // Apply strike-through to old price
        SpannableString oldPrice = new SpannableString("₪" + product.getOldPrice());
        oldPrice.setSpan(new StrikethroughSpan(), 0, oldPrice.length(), 0);
        holder.productOldPriceTextView.setText(oldPrice);

        holder.productNewPriceTextView.setText("₪" + product.getNewPrice());
        holder.productQuantityTextView.setText("Available: " + product.getQuantity());

        // Load product image using image resource ID
        holder.productImageView.setImageResource(product.getImageResId());  // Directly set image from resources

        // Set an OnClickListener to navigate to ProductDetailActivity when clicked
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productName", product.getName());
            intent.putExtra("productDescription", product.getDescription());
            intent.putExtra("productPrice", product.getNewPrice());
            intent.putExtra("productQuantity", product.getQuantity());
            intent.putExtra("productImageResId", product.getImageResId()); // Pass image resource ID
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView productNameTextView;
        TextView productOldPriceTextView;
        TextView productNewPriceTextView;
        TextView productQuantityTextView;
        ImageView productImageView;  // Added ImageView for product image

        public ProductViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productOldPriceTextView = itemView.findViewById(R.id.productOldPriceTextView);
            productNewPriceTextView = itemView.findViewById(R.id.productNewPriceTextView);
            productQuantityTextView = itemView.findViewById(R.id.productQuantityTextView);
            productImageView = itemView.findViewById(R.id.productImageView);  // Initialize ImageView
        }
    }
}
