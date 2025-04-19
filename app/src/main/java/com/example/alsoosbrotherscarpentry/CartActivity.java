package com.example.alsoosbrotherscarpentry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private ArrayList<Product> cartProducts = new ArrayList<>();  // Cart products array list
    private Account currentAccount;  // Account instance to hold user info
    private TextView totalPriceTextView;  // To display the total price

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize RecyclerView for displaying products in cart
        RecyclerView cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));  // Set the layout manager to Vertical list

        // Initialize total price TextView
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        // Retrieve the logged-in username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("loggedInUsername", "");  // Retrieve the logged-in username

        // Check if username is not empty
        if (username.isEmpty()) {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
            finish();  // Close CartActivity if no user is logged in
            return;
        }

        // Create the Account instance for the user
        currentAccount = new Account(username, "");

        // Load cart data for the current user
        loadCart();

        // Set custom adapter directly in RecyclerView
        CartRecyclerAdapter cartRecyclerAdapter = new CartRecyclerAdapter(cartProducts, this);
        cartRecyclerView.setAdapter(cartRecyclerAdapter);

        // Update total price initially
        updateTotalPrice();

        // Confirm order button logic
        Button confirmOrderButton = findViewById(R.id.confirmOrderButton);
        confirmOrderButton.setOnClickListener(v -> {
            // Decrease product quantities when the order is confirmed
            processOrder();
        });

        // Back to Main button logic
        Button backToMainButton = findViewById(R.id.backToMainButton);
        backToMainButton.setOnClickListener(v -> {
            // Navigate back to MainActivity
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Close CartActivity
        });
    }

    private void loadCart() {
        // Retrieve the cart products for the logged-in user from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("cartProducts_" + currentAccount.getUsername(), ""); // Use the username to get the cart
        if (!cartJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Product>>() {}.getType();
            cartProducts = gson.fromJson(cartJson, type);  // Convert the JSON string to ArrayList<Product>
        }

        if (cartProducts == null) {
            cartProducts = new ArrayList<>();  // Initialize an empty list if no cart data exists
        }
    }

    private void processOrder() {
        // Decrease the quantity of products in the cart after the order is confirmed
        for (Product product : cartProducts) {
            if (product.getQuantity() > 0) {
                // Reduce the quantity in the main product list
                for (Product mainProduct : MainActivity.productList) {
                    if (mainProduct.getName().equals(product.getName())) {
                        // Decrease the stock quantity
                        int newQuantity = mainProduct.getQuantity() - product.getQuantity();
                        mainProduct.setQuantity(newQuantity);  // Update the product quantity
                    }
                }
                product.setQuantity(0); // Set cart product quantity to 0 after purchase
            }
        }

        // Save the updated cart data back to SharedPreferences
        saveCart();

        // Clear the cart after order confirmation
        cartProducts.clear();  // Clear the cart list

        // Save the empty cart data back to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String emptyCartJson = gson.toJson(cartProducts);  // Empty cart
        editor.putString("cartProducts_" + currentAccount.getUsername(), emptyCartJson);  // Save empty cart under the user's username
        editor.apply();  // Apply changes

        // Save the updated product list after the order
        saveProductList(); // Save the updated product list to SharedPreferences

        // Show confirmation message
        Toast.makeText(CartActivity.this, "Order confirmed! Thank you for your purchase.", Toast.LENGTH_SHORT).show();
        finish();  // Close CartActivity and return to the previous screen
    }

    // Method to save updated product list to SharedPreferences
    private void saveProductList() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String productJson = gson.toJson(MainActivity.productList);  // Convert the product list to JSON
        editor.putString("productList", productJson);  // Save the updated product list
        editor.apply();  // Apply changes
    }

    private void saveCart() {
        // Convert the cart list to JSON and save it to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String cartJson = gson.toJson(cartProducts);  // Convert the cartProducts list to JSON
        editor.putString("cartProducts_" + currentAccount.getUsername(), cartJson);  // Save the cart under the user's username
        editor.apply();  // Apply changes
    }

    // Update the total price of the cart
    private void updateTotalPrice() {
        double totalPrice = 0;
        for (Product product : cartProducts) {
            totalPrice += product.getNewPrice() * product.getQuantity();
        }
        totalPriceTextView.setText("Total Price: ₪" + totalPrice);
    }

    // Custom RecyclerView Adapter directly in CartActivity
    public static class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder> {
        private final ArrayList<Product> cartProducts;
        private final Context context;

        public CartRecyclerAdapter(ArrayList<Product> cartProducts, Context context) {
            this.cartProducts = cartProducts;
            this.context = context;
        }

        @Override
        public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
            return new CartViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CartViewHolder holder, int position) {
            Product product = cartProducts.get(position);
            holder.productName.setText(product.getName());
            holder.productPrice.setText("₪" + product.getNewPrice());
            holder.productQuantity.setText("Quantity: " + product.getQuantity());

            // Load image
            holder.productImage.setImageResource(product.getImageResId()); // Assuming product.getImageResId() holds the image resource ID

            // Increase quantity button
            holder.increaseButton.setOnClickListener(v -> {
                if (product.getQuantity() < MainActivity.productList.get(position).getQuantity()) {
                    product.setQuantity(product.getQuantity() + 1);  // Increase quantity
                    notifyItemChanged(position); // Only refresh the updated item
                    ((CartActivity) context).updateTotalPrice(); // Update the total price after change
                } else {
                    Toast.makeText(context, "Maximum quantity reached", Toast.LENGTH_SHORT).show();  // Show a message if the user tries to exceed the available stock
                }
            });

            // Decrease quantity button
            holder.decreaseButton.setOnClickListener(v -> {
                if (product.getQuantity() > 1) {
                    product.setQuantity(product.getQuantity() - 1);  // Decrease quantity
                    notifyItemChanged(position); // Only refresh the updated item
                    ((CartActivity) context).updateTotalPrice(); // Update the total price after change
                } else {
                    Toast.makeText(context, "Minimum quantity is 1", Toast.LENGTH_SHORT).show();  // Show a message if the quantity is 1
                }
            });

            // Remove product button
            holder.removeButton.setOnClickListener(v -> {
                cartProducts.remove(position);  // Remove product from the list
                notifyItemRemoved(position); // Only remove the item
                ((CartActivity) context).updateTotalPrice(); // Update the total price after product removal
            });
        }

        @Override
        public int getItemCount() {
            return cartProducts.size();
        }

        public static class CartViewHolder extends RecyclerView.ViewHolder {
            TextView productName, productPrice, productQuantity;
            Button increaseButton, decreaseButton, removeButton;
            ImageView productImage;  // ImageView to show the product image

            public CartViewHolder(View itemView) {
                super(itemView);
                productName = itemView.findViewById(R.id.cartProductNameTextView);
                productPrice = itemView.findViewById(R.id.cartProductPriceTextView);
                productQuantity = itemView.findViewById(R.id.cartProductQuantityTextView);
                productImage = itemView.findViewById(R.id.cartProductImage);  // Initialize ImageView
                increaseButton = itemView.findViewById(R.id.increaseQuantityButton);
                decreaseButton = itemView.findViewById(R.id.decreaseQuantityButton);
                removeButton = itemView.findViewById(R.id.removeProductButton);
            }
        }
    }
}
