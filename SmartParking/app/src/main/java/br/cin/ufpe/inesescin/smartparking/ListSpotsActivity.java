package br.cin.ufpe.inesescin.smartparking;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import br.cin.ufpe.inesescin.smartparking.asyncTasks.EmptySpacesAsync;
import br.cin.ufpe.inesescin.smartparking.asyncTasks.OnEmptySpacesReceivedListener;
import br.cin.ufpe.inesescin.smartparking.util.PermissionRequest;

public class ListSpotsActivity extends AppCompatActivity implements OnEmptySpacesReceivedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_spots);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        if(!PermissionRequest.checkLocationPermission(this)){
            PermissionRequest.requestLocationPermission(this);
        }
        final Handler handler = new Handler();
        EmptySpacesAsync esa = new EmptySpacesAsync(this);
        esa.execute();
        new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 2 * 1000); // every 2 seconds
                EmptySpacesAsync esa = new EmptySpacesAsync(ListSpotsActivity.this);
                esa.execute();
            }
        }.run();

    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, SailsMapActivity.class);
        startActivity(intent);
    }


    @Override
    public void OnEmptySpacesReceivedListener(String[] result) {
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText(result[0]);
        TextView textView4 = (TextView) findViewById(R.id.textView4);
        textView4.setText(result[1]);
        TextView textView6 = (TextView) findViewById(R.id.textView6);
        textView6.setText(result[2]);
        TextView textView8 = (TextView) findViewById(R.id.textView8);
        textView8.setText(result[3]);
        TextView textView10 = (TextView) findViewById(R.id.textView10);
        textView10.setText(result[4]);
        TextView textView13 = (TextView) findViewById(R.id.textView13);
        textView13.setText(result[5]);
        TextView textView14 = (TextView) findViewById(R.id.textView14);
        textView14.setText(result[6]);
    }
}
