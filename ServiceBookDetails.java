package com.car.cartique;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.car.cartique.app.AppController;

public class ServiceBookDetails extends AppCompatActivity {

    TextView txtDetailName;
    NetworkImageView imageView;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_booking_form_activity);

        txtDetailName = findViewById(R.id.txtdetailName);
        imageView = findViewById(R.id.detailPic);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        txtDetailName.setText(name);
        imageView.setImageUrl(intent.getStringExtra("image"), imageLoader);

    }
}
