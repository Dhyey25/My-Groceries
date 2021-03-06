package com.example.android.mygroceries;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.mygroceries.adapters.MyCartAdapter;
import com.example.android.mygroceries.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MyCartFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;
    TextView overTotalAmount;
    RecyclerView recyclerView;
    MyCartAdapter myCartAdapter;
    List<MyCartModel> cartModelList;


    public MyCartFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_cart, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        overTotalAmount = root.findViewById(R.id.total_price);
        /*LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mMessageReceiver,new IntentFilter("MyTotalAmount"));*/

        cartModelList = new ArrayList<>();
        myCartAdapter = new MyCartAdapter(getActivity(),cartModelList);
        recyclerView.setAdapter(myCartAdapter);

        db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("CurrentUser").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                        String documentId = documentSnapshot.getId();

                        MyCartModel myCartModel = documentSnapshot.toObject(MyCartModel.class);
                        myCartModel.setDocumentId(documentId);

                        cartModelList.add(myCartModel);
                        myCartAdapter.notifyDataSetChanged();
                    }

                    calculateTotalAmount(cartModelList);

                }
            }
        });

        return root;
    }

    private void calculateTotalAmount(List<MyCartModel> cartModelList) {
        double totalAmount = 0.0;
        for (MyCartModel myCartModel : cartModelList){
            totalAmount += myCartModel.getTotalPrice();
        }

        overTotalAmount.setText("Total Amount :" + totalAmount);
    }

    /*public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int totalBill = intent.getIntExtra("totalAmount",0);
            overTotalAmount.setText("Total Bill: "+totalBill+" Rs");
        }
    };*/
}