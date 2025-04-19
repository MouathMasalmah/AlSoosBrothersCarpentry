package com.example.alsoosbrotherscarpentry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize UI components
        TextView productNameTextView = findViewById(R.id.productNameTextView);
        TextView productDescriptionTextView = findViewById(R.id.productDescriptionTextView);
        TextView productPriceTextView = findViewById(R.id.productPriceTextView);
        TextView productQuantityTextView = findViewById(R.id.productQuantityTextView);
        ImageView productImageView = findViewById(R.id.productImageView);
        Button addToCartButton = findViewById(R.id.addToCartButton);
        Button goBackButton = findViewById(R.id.goBackButton);

        // Get product details from Intent
        String productName = getIntent().getStringExtra("productName");
        float productPrice = getIntent().getFloatExtra("productPrice", 0);
        String productDescription = getIntent().getStringExtra("productDescription");
        int productQuantity = getIntent().getIntExtra("productQuantity", 0);
        int productImageResId = getIntent().getIntExtra("productImageResId", 0); // Get image resource ID

        // Set product details in UI components
        productNameTextView.setText(productName);
        productDescriptionTextView.setText(productDescription);
        productPriceTextView.setText("₪" + productPrice);
        productQuantityTextView.setText("Available: " + productQuantity);
        productImageView.setImageResource(productImageResId);  // Set the image resource

        // Check if the product is out of stock and show a message if needed
        if (productQuantity == 0) {
            productQuantityTextView.setText("Out of Stock");
            addToCartButton.setEnabled(false);  // Disable the "Add to Cart" button if out of stock
        }

        // Set OnClickListener for "Add to Cart" button
        addToCartButton.setOnClickListener(v -> {

            if (productQuantity > 0) {
                addToCart(new Product(productName, 0, productPrice, productQuantity, productDescription, productImageResId));

                // Show dialog asking user if they want to continue shopping or go to cart
                showCartConfirmationDialog();
            } else {
                Toast.makeText(ProductDetailActivity.this, "Sorry, this product is out of stock.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set OnClickListener for "Go Back" button
        goBackButton.setOnClickListener(v -> {
            // Navigate back to MainActivity
            Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Close the ProductDetailActivity
        });
    }

    // Add product to cart
    private void addToCart(Product product) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        Gson gson = new Gson();

        // Retrieve the logged-in username
        String loggedInUsername = sharedPreferences.getString("loggedInUsername", "");

        // Use the correct key for cart products based on logged-in username
        String cartJson = sharedPreferences.getString("cartProducts_" + loggedInUsername, "");  // Correctly use the logged-in username

        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        ArrayList<Product> cartProducts = gson.fromJson(cartJson, type);

        if (cartProducts == null) {
            cartProducts = new ArrayList<>();
        }

        // Check if the product is already in the cart, if so, increase the quantity
        boolean productExists = false;
        for (Product cartProduct : cartProducts) {
            if (cartProduct.getName().equals(product.getName())) {
                cartProduct.setQuantity(cartProduct.getQuantity() + 1); // Increase quantity
                productExists = true;
                break;
            }
        }

        // If the product is not in the cart, add it to the cart
        if (!productExists) {
            product.setQuantity(1);  // Set initial quantity
            cartProducts.add(product);
        }

        // Save updated cart
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cartProducts_" + loggedInUsername, gson.toJson(cartProducts));  // Save the cart for the specific logged-in user
        editor.apply();

        // Show confirmation message
        Toast.makeText(ProductDetailActivity.this, "Product Added to Cart!", Toast.LENGTH_SHORT).show();
    }

    // Show dialog after adding to cart
    private void showCartConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Added to Cart")
                .setMessage("Would you like to continue shopping or go to your cart?")
                .setPositiveButton("Go to Cart", (dialog, which) -> {
                    // Navigate to CartActivity
                    Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Continue Shopping", (dialog, which) -> {
                    // Dismiss the dialog and stay on the current activity
                    dialog.dismiss();
                });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
