package com.example.webforest.testing.Fragment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.webforest.testing.Interface.ItemClickListener;
import com.example.webforest.testing.Model.Category;
import com.example.webforest.testing.Start;
import com.example.webforest.testing.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.example.webforest.testing.R;

public class CategoryFragment extends Fragment {
    RecyclerView listCategory;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;
    DatabaseReference categories;
    View view;


    public static CategoryFragment newInstance() {
        CategoryFragment categoryFragment = new CategoryFragment();
        return categoryFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categories = FirebaseDatabase.getInstance().getReference("Category");
    }

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_category, container, false);
        listCategory = view.findViewById(R.id.category_recyclerView);
        layoutManager = new LinearLayoutManager(container.getContext());
        listCategory.setLayoutManager(layoutManager);
        listCategory.setHasFixedSize(true);
        loadCategories();
        listCategory.setAdapter(adapter);
        return view;
    }

    private void loadCategories() {
        adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(
                Category.class,
                R.layout.category_layout,
                CategoryViewHolder.class,
                categories

        ) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, final Category model, int position) {
                if (model == null)
                    Log.d("aaaaaaaa", "populateViewHolder: empty");
                viewHolder.category_name.setText(model.getName());
                Picasso.with(getActivity()).load(model.getImage()).into(viewHolder.category_image);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongPressed) {
                        Intent intent = new Intent(getActivity(), Start.class);
                        com.example.webforest.testing.Common.Common.CategoryId = adapter.getRef(position).getKey();
                        com.example.webforest.testing.Common.Common.CategoryName=model.getName();
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
            }
        };
    }

}