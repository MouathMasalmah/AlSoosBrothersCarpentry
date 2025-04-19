package com.example.alsoosbrotherscarpentry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final ArrayList<Product> cartProducts;
    private final Context context;

    // Constructor
    public CartAdapter(ArrayList<Product> cartProducts, Context context) {
        this.cartProducts = cartProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        Product product = cartProducts.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText("₪" + product.getNewPrice());
        holder.productQuantity.setText("Quantity: " + product.getQuantity());

        // Increase quantity button
        holder.increaseButton.setOnClickListener(v -> {
            product.setQuantity(product.getQuantity() + 1);
            notifyItemChanged(position); // Only refresh the updated item
        });

        // Decrease quantity button
        holder.decreaseButton.setOnClickListener(v -> {
            if (product.getQuantity() > 1) {
                product.setQuantity(product.getQuantity() - 1);
                notifyItemChanged(position); // Only refresh the updated item
            }
        });

        // Remove product button
        holder.removeButton.setOnClickListener(v -> {
            cartProducts.remove(position);  // Remove product from the list
            notifyItemRemoved(position); // Only remove the item
        });
    }

    @Override
    public int getItemCount() {
        return cartProducts.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity;
        Button increaseButton, decreaseButton, removeButton;

        public CartViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.cartProductNameTextView);
            productPrice = itemView.findViewById(R.id.cartProductPriceTextView);
            productQuantity = itemView.findViewById(R.id.cartProductQuantityTextView);
            increaseButton = itemView.findViewById(R.id.increaseQuantityButton);
            decreaseButton = itemView.findViewById(R.id.decreaseQuantityButton);
            removeButton = itemView.findViewById(R.id.removeProductButton);
        }
    }
}
