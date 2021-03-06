package com.example.androidfood;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidfood.Common.Common;
import com.example.androidfood.Database.Database;
import com.example.androidfood.FinishOrders.ThanksForOrder;
import com.example.androidfood.FinishOrders.ThanksForOrderDelivery;
import com.example.androidfood.Helper.RecyclerItemTouchHelper;
import com.example.androidfood.Infomacion.Aviso;
import com.example.androidfood.Infomacion.Horarios;
import com.example.androidfood.Interface.RecyclerItemTouchHelperListener;
import com.example.androidfood.Model.MyResponse;
import com.example.androidfood.Model.Notification;
import com.example.androidfood.Model.Order;
import com.example.androidfood.Model.Request;
import com.example.androidfood.Model.Sender;
import com.example.androidfood.Model.Token;
import com.example.androidfood.Remote.APIService;
import com.example.androidfood.ViewHolder.CartAdapter;
import com.example.androidfood.ViewHolder.CartViewHolder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txtTotalPrice;
    Button btnPlace;

    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;

    APIService mService;

    RelativeLayout rootLayout;

    RadioGroup radioGroup;

    RadioButton rbGoToMarket, rbSendToAddress;

    LinearLayout llDataSendDirection;

    //Mercadopago
   // static MercadoPagoPaymentConfiguration configuration=new MercadoPagoPaymentConfiguration()


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary)); //cambiar color de la barra del telefono

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//Botoon para volver hacia atras. se encuentra en action bar

        mService = Common.getFCMClient();


        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //init
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = findViewById(R.id.rootLayout);


        //swipe para eliminar
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        txtTotalPrice = findViewById(R.id.total);
        btnPlace = findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(v -> {

            Calendar openingHour = Calendar.getInstance(); // Se utilziara cuando el lcoal se encuentre cerrado. Horario de abierto y cerrado.
            openingHour.set(Calendar.HOUR_OF_DAY, 6);
            openingHour.set(Calendar.MINUTE, 0);
            openingHour.set(Calendar.SECOND, 0);
            openingHour.set(Calendar.MILLISECOND, 0);

            Calendar closingHour = Calendar.getInstance();
            closingHour.set(Calendar.HOUR_OF_DAY, 22);
            closingHour.set(Calendar.MINUTE, 0);
            closingHour.set(Calendar.SECOND, 0);
            closingHour.set(Calendar.MILLISECOND, 0);

            Calendar openingHour1 = Calendar.getInstance(); // Se utilziara cuando el lcoal se encuentre cerrado. Horario de abierto y cerrado en el dia domingo.
            openingHour1.set(Calendar.DAY_OF_WEEK,1);
            openingHour1.set(Calendar.HOUR_OF_DAY, 6);
            openingHour1.set(Calendar.MINUTE, 0);
            openingHour1.set(Calendar.SECOND, 0);
            openingHour1.set(Calendar.MILLISECOND, 0);

            Calendar closingHour1 = Calendar.getInstance();
            closingHour1.set(Calendar.DAY_OF_WEEK,1);
            closingHour1.set(Calendar.HOUR_OF_DAY, 14);
            closingHour1.set(Calendar.MINUTE, 0);
            closingHour1.set(Calendar.SECOND, 0);
            closingHour1.set(Calendar.MILLISECOND, 0);


            Calendar currentTime = Calendar.getInstance();
            int dayOfWeek = currentTime.get(Calendar.DAY_OF_WEEK);
            if( dayOfWeek % 7 > 1  && currentTime.before(closingHour) && currentTime.after(openingHour) || currentTime.before(closingHour1) && currentTime.after(openingHour1)){


                if (cart.size() > 0) {
                    showAlertDialog();
                    radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                        switch (i) {
                            case R.id.rdiShipToSucursal:
                                llDataSendDirection.setVisibility(View.GONE);
                                break;
                            case R.id.rdiShipToAddress:
                                llDataSendDirection.setVisibility(View.VISIBLE);
                                break;
                        }
                    });
                } else
                    Toast.makeText(Cart.this, "Su carro esta vacio!!!", Toast.LENGTH_SHORT).show();
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this);
                LayoutInflater factory = LayoutInflater.from(Cart.this);
                final View view = factory.inflate(R.layout.sample, null);
                builder.setView(view);
                builder.setTitle("New boedo esta cerrado,abrira a las 6 AM");
                builder.setMessage("Mientras tanto puede mirar nuestro catalogo o ver los horarios");
                builder.setPositiveButton("Mirar horarios", (dialog, which) -> {
                    Intent cart = new Intent(Cart.this, Horarios.class);
                    //cart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(cart);
                });
                builder.setNegativeButton("De acuerdo", (dialog, which) -> {

                    // Do nothing
                    dialog.dismiss();
                });

                AlertDialog alert = builder.create();
                alert.show();
            }




        });

        loadListFood();


    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Un paso mas!!");
        alertDialog.setMessage("Seleccione las opciones.")
                .setPositiveButton("Si",null);

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment, null);

        llDataSendDirection = order_address_comment.findViewById(R.id.order_address_comment_ll_data_send_direction);
        radioGroup = order_address_comment.findViewById(R.id.radioGroupOrders);
        rbGoToMarket = order_address_comment.findViewById(R.id.rdiShipToSucursal);
        rbSendToAddress = order_address_comment.findViewById(R.id.rdiShipToAddress);
        final MaterialEditText edtAdress = order_address_comment.findViewById(R.id.edtAddress);
        final MaterialEditText edtComment = order_address_comment.findViewById(R.id.edtComment);
        final MaterialEditText edtentrecalles = order_address_comment.findViewById(R.id.edtentrecalles);
        final MaterialEditText edtpisodepartamento = order_address_comment.findViewById(R.id.edtpisoodepartamento);
        final MaterialEditText edtLocalidad = order_address_comment.findViewById(R.id.edtlocalidad);


        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Ordenar", (dialog, which) -> {


        })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        alertDialog.setView(order_address_comment);
        AlertDialog dialog=alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);//con este codigo el dialogo no se cerrara cuando se presione fuera del dialogo
        dialog.setCancelable(false);//no se podra cerrar el dialogo presionando el boton de atras del telefono
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Request request = new Request(
                        Common.currentuser.getPhone(),
                        Common.currentuser.getName(),
                        Common.currentuser.getApellido(),
                        Common.currentuser.getEmail(),
                        edtAdress.getText().toString(),
                        edtentrecalles.getText().toString(),
                        edtpisodepartamento.getText().toString(),
                        edtLocalidad.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        "0", // seria los estados
                        edtComment.getText().toString(),
                        cart

                );

                if (rbSendToAddress.isChecked()) {

                    if (edtAdress.length() == 0) {
                        edtAdress.setError("Ingrese su Direccion");
                    } else if (edtentrecalles.length() == 0) {
                        edtentrecalles.setError("Ingrese entrecalles");
                    } else if (edtLocalidad.length() == 0) {
                        edtLocalidad.setError("Ingrese su Localidad");
                    }

                     else {

                        //enviar a firebase
                        //usaremos System.CurrentMilli para llave

                        String order_number = String.valueOf(System.currentTimeMillis());
                        requests.child(order_number).setValue(request);

                        Intent cart = new Intent(Cart.this, ThanksForOrderDelivery.class);
                        startActivity(cart);
                        //Toast.makeText(Cart.this, "Muchas gracias por su orden.", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                        sendNotificationOrder(order_number);

                        //Eliminar carro
                        //new Database(getBaseContext()).cleanCart();
                        new Database(getBaseContext()).cleanCart(Common.currentuser.getPhone());
                        finish();
                    }
                }

                    if (rbGoToMarket.isChecked()) {
                        //Intent intent = new Intent(Cart.this, MercadoPago.class);
                        //startActivity(intent);

                        String order_number = String.valueOf(System.currentTimeMillis());
                        requests.child(order_number).setValue(request);

                        Intent cart = new Intent(Cart.this, ThanksForOrder.class);
                        startActivity(cart);
                        //Toast.makeText(Cart.this, "Muchas gracias por su orden.", Toast.LENGTH_SHORT).show();

                        sendNotificationOrder(order_number);
                        dialog.dismiss();

                        new Database(getBaseContext()).cleanCart(Common.currentuser.getPhone());
                        finish();
                    }
                   // alertDialog.show();


            }



            private void sendNotificationOrder(final String order_number) {
                DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
                Query data = tokens.orderByChild("serverToken").equalTo(true);
                data.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

                            Token serverToken = postSnapShot.getValue(Token.class);

                            Notification notification = new Notification("New Boedo", "Tienes una nueva orden" + order_number);
                            Sender content = new Sender(serverToken.getToken(), notification);

                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                            if (response.code() == 200) {
                                                if (response.body().success == 1) {

                                                    Toast.makeText(Cart.this, "Muchas gracias por su orden.", Toast.LENGTH_SHORT).show();

                                                    finish();
                                                } else {
                                                    Toast.makeText(Cart.this, "Hubo un problema", Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR", "FALLA" + t.getMessage());

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }


        });

        alertDialog.setView(order_address_comment);

        /*alertDialog.setNegativeButton("NO", (dialog, which) -> {
        });

        alertDialog.setView(order_address_comment);

        alertDialog.show();

        //validateRadioButtons();*/

    }

    private void validateRadioButtons() {
/*        if (rbGoToMarket.isChecked()){
            llDataSendDirection.setVisibility(View.GONE);
        }else if (rbSendToAddress.isChecked()){
            llDataSendDirection.setVisibility(View.VISIBLE);
        }*/

        int isSelected = radioGroup.getCheckedRadioButtonId();

        if (isSelected == 1) {

        } else {

        }
    }


    private void loadListFood() {
        //cart = new Database(this).getCarts();
        cart = new Database(this).getCarts(Common.currentuser.getPhone());
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Calcular precio total
        int total = 0;
        for (Order order : cart)
            total += (Integer.parseInt(order.getPrecio())) * (Integer.parseInt(order.getCantidad()));
        Locale locale = new Locale("es", "AR");//simbolo de moneda
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder) {
            String name = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductoNombre();

            final Order deleteItem = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()); // item
            final int deleteIndex = viewHolder.getAdapterPosition(); // position

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).remoFormCart(deleteItem.getProductoId(), Common.currentuser.getPhone());

            //Calcular precio total
            int total = 0;
            //List<Order> orders = new Database(getBaseContext()).getCarts();
            List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentuser.getPhone());
            for (Order item : orders)
                total += (Integer.parseInt(item.getPrecio())) * (Integer.parseInt(item.getCantidad()));
            Locale locale = new Locale("es", "AR");//simbolo de moneda
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            txtTotalPrice.setText(fmt.format(total));

            //sanckbar
            Snackbar snackbar = Snackbar.make(rootLayout, name + " Eliminado del carro", Snackbar.LENGTH_LONG);
            snackbar.setAction("Agregar de nuevo", v -> {
                adapter.restoreItem(deleteItem, deleteIndex);
                new Database(getBaseContext()).addToCart(deleteItem);


                //Calcular precio total
                int total1 = 0;
                //List<Order> orders = new Database(getBaseContext()).getCarts();//////////////////////////////////////////
                List<Order> orders1 = new Database(getBaseContext()).getCarts(Common.currentuser.getPhone());
                for (Order item : orders1)
                    total1 += (Integer.parseInt(item.getPrecio())) * (Integer.parseInt(item.getCantidad()));
                Locale locale1 = new Locale("es", "AR");//simbolo de moneda
                NumberFormat fmt1 = NumberFormat.getCurrencyInstance(locale1);

                txtTotalPrice.setText(fmt1.format(total1));

            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
