package edu.pe.appsimplemaps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.net.URI;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

private Button btnmaps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnmaps = findViewById(R.id.btnmaps);
        btnmaps.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
Intent mapIntent = new Intent(Intent.ACTION_VIEW);
mapIntent.setData(Uri.parse("geo:0,0?q=12.066887,-77.035825(IDAT-LIMA CENTRO)"));
mapIntent.setPackage("com.google.android.apps.maps");
if(mapIntent.resolveActivity(getPackageManager()) !=null){

}
    }
}