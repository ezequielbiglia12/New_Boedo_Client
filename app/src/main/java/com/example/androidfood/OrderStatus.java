package com.example.androidfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidfood.Common.Common;
import com.example.androidfood.Interface.ItemClickListener;
import com.example.androidfood.Model.Request;
import com.example.androidfood.ViewHolder.OrderviewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatus extends AppCompatActivity {


    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderviewHolder> adapter;
    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //firebase init
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


      loadOrders(Common.currentuser.getPhone());

    }

    private void loadOrders(String phone) {
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>().setQuery(requests.orderByChild("telefono").equalTo(phone), Request.class).build();

        adapter=new FirebaseRecyclerAdapter<Request, OrderviewHolder>(options) {
           @Override
           protected void onBindViewHolder(@NonNull OrderviewHolder holder, int position, @NonNull Request model) {
               holder.txtOrderId.setText(adapter.getRef(position).getKey());
               holder.txtOrderEstados.setText(convertCodeToStatus(model.getEstados()));
               holder.txtOrderDireccion.setText(model.getDireccion());
               holder.txtOrderTelefono.setText(model.getTelefono());
               holder.setItemClickListener(new ItemClickListener() {
                   @Override
                   public void onClick(View view, int position, boolean isLongClick) {

                   }
               });


           }

            private String convertCodeToStatus(String status) {
               if (status.equals("0"))
                   return "En lugar";
               else if (status.equals("1"))
                   return "En camino";
               else
                   return "Enviado";
            }

            @NonNull
           @Override
           public OrderviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout,parent,false);
               return new OrderviewHolder(view);
           }
       };
        //Adaptador
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }
}
