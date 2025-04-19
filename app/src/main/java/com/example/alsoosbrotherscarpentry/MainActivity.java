package com.example.alsoosbrotherscarpentry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.SearchView; // Add the correct import for SearchView
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ProductAdapter itemAdapter;
    static ArrayList<Product> productList;
    private ArrayList<Product> filteredProductList;  // To store filtered products

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView
        RecyclerView itemRecyclerView = findViewById(R.id.itemRecyclerView);

        // Set up RecyclerView
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize product list and add sample data
        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();

        // Load the product list from SharedPreferences
        loadProductListFromSharedPreferences();

        // Initialize adapter with product data
        itemAdapter = new ProductAdapter(this, filteredProductList);

        // Set adapter to RecyclerView
        itemRecyclerView.setAdapter(itemAdapter);

        // Initialize the FloatingActionButton
        // Floating Action Button
        FloatingActionButton fab = findViewById(R.id.fab);

        // Set click listener for the FloatingActionButton
        fab.setOnClickListener(v -> showOrderConfirmationDialog());

        // Initialize SearchView
        // SearchView for product filtering
        SearchView searchView = findViewById(R.id.searchView);

        // Set query text listener for search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search filtering when the user submits the query
                filterProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search filtering dynamically as the user types
                filterProducts(newText);
                return true;
            }
        });
    }

    private void loadProductListFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String json = sharedPreferences.getString("productList", ""); // Get the JSON string

        // If there's no data, use an empty list
        if (!json.isEmpty()) {
            Gson gson = new Gson();
            Type productListType = new TypeToken<ArrayList<Product>>(){}.getType();
            productList = gson.fromJson(json, productListType); // Deserialize JSON to product list
        } else {
            // Sample product data for a carpentry shop (used if no data exists in SharedPreferences)
            productList.add(new Product("Wooden Workbench", 350, 250, 5, "A heavy-duty wooden workbench perfect for all your carpentry needs.", R.drawable.workbench_image));
            productList.add(new Product("Wooden Chair", 120, 80, 15, "A classic wooden chair with a comfortable seat, perfect for your dining room.", R.drawable.chair_image));
            productList.add(new Product("Wooden Bookshelf", 200, 150, 10, "A sturdy wooden bookshelf with ample storage for books and decor.", R.drawable.bookshelf_image));
            productList.add(new Product("Wooden Coffee Table", 180, 120, 8, "A sleek and stylish wooden coffee table made from premium oak wood.", R.drawable.coffee_table_image));
            productList.add(new Product("Wooden Wardrobe", 700, 500, 3, "A spacious wooden wardrobe made from premium wood, perfect for your bedroom.", R.drawable.wardrobe_image));
            productList.add(new Product("Wooden Dining Table", 600, 450, 4, "A sturdy and elegant wooden dining table that seats up to 6 people.", R.drawable.dining_table_image));
            productList.add(new Product("Wooden Side Table", 150, 100, 10, "A simple yet elegant wooden side table, perfect for any room in the house.", R.drawable.side_table_image));
            productList.add(new Product("Wooden Storage Cabinet", 400, 300, 6, "A multi-purpose wooden storage cabinet for organizing tools or household items.", R.drawable.storage_cabinet_image));
            productList.add(new Product("Adjustable Wooden Stool", 75, 50, 20, "An adjustable height wooden stool, perfect for workspaces and kitchens.", R.drawable.wooden_stool_image));
            productList.add(new Product("Wooden Shelving Unit", 250, 180, 8, "A modern wooden shelving unit to display your plants, books, or decorative items.", R.drawable.shelving_unit_image));
            productList.add(new Product("Wooden Bed Frame", 800, 600, 3, "A solid wooden bed frame designed for comfort and style, available in multiple sizes.", R.drawable.bed_frame_image));
            productList.add(new Product("Wooden Desk", 350, 250, 7, "A functional wooden desk with ample storage space for your home office.", R.drawable.wooden_desk_image));
        }

        // Initially, we show all products in the filtered list
        filteredProductList.addAll(productList);
    }

    private void showOrderConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Your Order")
                .setMessage("Would you like to complete the order or continue shopping?")
                .setPositiveButton("Complete Order", (dialog, which) -> {
                    // Navigate to CartActivity or complete the order
                    Toast.makeText(this, "Order Confirmed!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Continue Shopping", (dialog, which) -> {
                    // Just dismiss the dialog and stay on MainActivity
                    dialog.dismiss();
                });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void filterProducts(String query) {
        filteredProductList.clear();  // Clear current filtered list

        if (query.isEmpty()) {
            // If query is empty, show all products
            filteredProductList.addAll(productList);
        } else {
            // Filter products based on the name (case-insensitive)
            for (Product product : productList) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredProductList.add(product);
                }
            }
        }

        // Notify adapter to update the RecyclerView with filtered data
        itemAdapter.notifyDataSetChanged();
    }


}
