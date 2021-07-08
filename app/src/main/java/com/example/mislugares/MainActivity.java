package com.example.mislugares;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private final int SOLICITUD_PERMISO_LOCALIZACION = 1;

    private LocationManager manejador;
    private Location mejorLocaliz;
    private RecyclerView recyclerView;
    public AdaptadorLugares adaptador;
    private RecyclerView.LayoutManager layoutManager;
    public static Lugares lugares = new LugaresVector();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        adaptador = new AdaptadorLugares(lugares);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                lanzarVistaLugar(pos);
            }
        });
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptador);
        //localizacion
        manejador = (LocationManager) getSystemService(LOCATION_SERVICE);
        ultimaLocalizacion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //activarProovedores();
    }

    private void ultimaLocalizacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(manejador.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //actualizaMejorLocaliz(manejador.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            }
            if (manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                //actualizaMejorLocaliz(manejador.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
            }
        } else {
            solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION, "Sin el permiso no se puede mostrar la distancia a los lugares.", SOLICITUD_PERMISO_LOCALIZACION, this);
        }
    }

    public void lanzarAcercaDe(View view){
        Intent intent = new Intent(MainActivity.this, AcercaDeActivity.class);
        startActivity(intent);
    }

    public void lanzarPreferencias(View view){
        Intent intent = new Intent(this, PreferenciasActivity.class);
        startActivity(intent);
    }

    public void lanzarVistaLugar(View view){
        final EditText entrada = new EditText(this);
        entrada.setText("0");
        new AlertDialog.Builder(this)
                .setTitle("Seleccion e lugar")
                .setMessage("indica su id:")
                .setView(entrada)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long id = Long.parseLong(entrada.getText().toString());
                        Intent i = new Intent(MainActivity.this, VistaLugarActivity.class);
                        i.putExtra("id", id);
                        startActivity(i);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public static void solicitarPermiso(final String permiso, String justificacion, final int requestCode, final Activity actividad){
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad, permiso)){
            new android.app.AlertDialog.Builder(actividad).setTitle("Solicitud de permiso").setMessage(justificacion)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(actividad, new String[]{permiso}, requestCode);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(actividad, new String[]{permiso}, requestCode);
        }
    }

    public void lanzarVistaLugar(long id){
        Intent i = new Intent(MainActivity.this, VistaLugarActivity.class);
        i.putExtra("id", id);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            lanzarPreferencias(null);
            return true;
        }
        if(id == R.id.menu_buscar){
            lanzarVistaLugar(null);
            return true;
        }
        if(id == R.id.acercade){
            lanzarAcercaDe(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == SOLICITUD_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ultimaLocalizacion();
                //activarProovedores();
                adaptador.notifyDataSetChanged();
            }
        }
    }
}
