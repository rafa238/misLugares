package com.example.mislugares;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class VistaLugarActivity extends AppCompatActivity {
    private ImageView imageView;
    final static int RESULTADO_EDITAR = 1;
    final static int RESULTADO_GALERIA = 2;
    final static int RESULTADO_FOTO = 3;
    private long id;
    private Lugar lugar;
    private Uri uriUltimaFoto;
    private final int SOLICITUD_PERMISO_ALMACENAMIENTO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_lugar);
        Bundle extras = getIntent().getExtras();
        id = extras.getLong("id", -1);
        lugar = MainActivity.lugares.elemento((int) id);

        actualizarVistas();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULTADO_EDITAR){
            actualizarVistas();
            findViewById(R.id.scrollView1).invalidate();
        }else if(requestCode == RESULTADO_GALERIA){
            if(resultCode == Activity.RESULT_OK){
                lugar.setFoto(data.getDataString());
                ponerFoto(imageView, lugar.getFoto());
            }else{
                Toast.makeText(this, "Foto no cargada", Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == RESULTADO_FOTO){
            if (resultCode == Activity.RESULT_OK && uriUltimaFoto!=null) {
                lugar.setFoto(uriUltimaFoto.toString());
                ponerFoto(imageView, lugar.getFoto());
            } else {
                Toast.makeText(this, "Error en captura", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vista_lugar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.accion_compartir:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, lugar.getNombre() + " - "+ lugar.getUrl());
                startActivity(intent);
                return true;
            case R.id.accion_llegar:
                verMapa(null);
                return true;
            case R.id.accion_editar:
                Intent i = new Intent(this, EdicionLugarActivity.class);
                i.putExtra("id", id);
                startActivityForResult(i, RESULTADO_EDITAR);
                return true;
            case R.id.accion_borrar:
                borrarLugar((int)id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void verMapa(View view){
        Uri uri;
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();
        if(lat != 0 || lon != 0){
            uri = Uri.parse("geo:" + lat + "," + lon);
        }else{
            uri = Uri.parse("geo:0,0?q=" + lugar.getDireccion());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void llamadaTelefono(View view){
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + lugar.getTelefono())));
    }

    public void pgWeb(View view){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(lugar.getUrl())));
    }
    public void borrarLugar(final int id){
        new AlertDialog.Builder(this)
                .setTitle("Borrar lugar")
                .setMessage("¿Estas seguro de que quieres eliminar este lugar")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.lugares.borrar(id);
                        VistaLugarActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void galeria(View view){
        //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);#permite tomar imagenes y resguardarlas
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            String accion;
            if (android.os.Build.VERSION.SDK_INT >= 19) { // API 19 - Kitkat
                accion = Intent.ACTION_OPEN_DOCUMENT;
            } else {
                accion = Intent.ACTION_PICK;
            }
            Intent intent = new Intent(accion, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, RESULTADO_GALERIA);
        } else {
            solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE, "Debes aceptar el permiso del almacenamiento", SOLICITUD_PERMISO_ALMACENAMIENTO, this);
        }
    }

    private void ponerFoto(ImageView imageView, String uri) {
        if(uri != null){
            //imageView.setImageURI(Uri.parse(uri)); Pra reducir la calidad de la imagen y que no haya problemas de memoria
            imageView.setImageBitmap(reduceBitmap(this, uri, 1024, 1024));
        }else{
            imageView.setImageBitmap(null);
        }
    }

    public static Bitmap reduceBitmap(Context contexto, String uri, int maxAncho, int maxAlto){
        try{
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(contexto.getContentResolver().openInputStream(Uri.parse(uri)), null, options);
            options.inSampleSize = (int) Math.max(Math.ceil(options.outWidth/maxAncho), Math.ceil(options.outWidth / maxAlto));
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(contexto.getContentResolver().openInputStream(Uri.parse(uri)), null, options);
        }catch(FileNotFoundException e){
            Toast.makeText(contexto, "Imagen no encontrada", Toast.LENGTH_LONG).show();
            return null;
        }
    }
    public Uri tomarFoto(int codidoSolicitud) {
        Activity actividad = this;
        try {
            Uri uriUltimaFoto;
            File file = File.createTempFile(
                    "img_" + (System.currentTimeMillis()/ 1000), ".jpg" ,
                    actividad.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            if (Build.VERSION.SDK_INT >= 24) {
                uriUltimaFoto = FileProvider.getUriForFile(
                        actividad, "es.upv.jtomas.mislugares.fileProvider", file);
            } else {
                uriUltimaFoto = Uri.fromFile(file);
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra (MediaStore.EXTRA_OUTPUT, uriUltimaFoto);
            actividad.startActivityForResult(intent, codidoSolicitud);
            return uriUltimaFoto;
        } catch (IOException ex) {
            Toast.makeText(actividad, "No se cargó ninguna foto!",
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public void tomarFoto(View view) {
       if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
           uriUltimaFoto = tomarFoto(RESULTADO_FOTO);
       } else {
            solicitarPermiso(Manifest.permission.READ_EXTERNAL_STORAGE, "Debes aceptar el permiso del almacenamiento", SOLICITUD_PERMISO_ALMACENAMIENTO, this);
       }
    }

    private void solicitarPermiso(final String permiso, String justificacion, final int requestCode, final Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad, permiso)){
            new AlertDialog.Builder(actividad).setTitle("Solicitud de permisos")
                    .setMessage(justificacion)
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SOLICITUD_PERMISO_ALMACENAMIENTO && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permiso aceptado, gracias! :)", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sin el permiso no nos es posible realizar esta accion!", Toast.LENGTH_SHORT).show();
        }
    }

    public void eliminarFoto(View view){
        lugar.setFoto(null);
        ponerFoto(imageView, null);
    }

    public void actualizarVistas(){
        TextView nombre = (TextView) findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre());
        ImageView logo_tipo = (ImageView) findViewById(R.id.logo_tipo);
        logo_tipo.setImageResource(lugar.getTipo().getRecurso());
        TextView tipo = (TextView) findViewById(R.id.tipo);
        tipo.setText(lugar.getTipo().getTexto());
        if (lugar.getDireccion().isEmpty()) {
            findViewById(R.id.direccion).setVisibility(View.GONE);
            findViewById(R.id.logo_direccion).setVisibility(View.GONE);
        } else {
            findViewById(R.id.telefono).setVisibility(View.VISIBLE);
            findViewById(R.id.logo_telefono).setVisibility(View.VISIBLE);
            TextView direccion = (TextView) findViewById(R.id.direccion);
            direccion.setText(lugar.getDireccion());
        }
        if (lugar.getTelefono() == 0) {
            findViewById(R.id.telefono).setVisibility(View.GONE);
            findViewById(R.id.logo_telefono).setVisibility(View.GONE);
        } else {
            findViewById(R.id.telefono).setVisibility(View.VISIBLE);
            findViewById(R.id.logo_telefono).setVisibility(View.VISIBLE);
            TextView telefono = (TextView) findViewById(R.id.telefono);
            telefono.setText(Integer.toString(lugar.getTelefono()));
        }
        if (lugar.getUrl().isEmpty()) {
            findViewById(R.id.url).setVisibility(View.GONE);
            findViewById(R.id.logo_url).setVisibility(View.GONE);
        } else {
            findViewById(R.id.url).setVisibility(View.VISIBLE);
            findViewById(R.id.logo_url).setVisibility(View.VISIBLE);
            TextView url = (TextView) findViewById(R.id.url);
            url.setText(lugar.getUrl());
        }
        if (lugar.getComentario().isEmpty()) {
            findViewById(R.id.comentario).setVisibility(View.GONE);
            findViewById(R.id.logo_comentario).setVisibility(View.GONE);
        } else {
            findViewById(R.id.comentario).setVisibility(View.VISIBLE);
            findViewById(R.id.logo_comentario).setVisibility(View.VISIBLE);
            TextView comentario = (TextView) findViewById(R.id.comentario);
            comentario.setText(lugar.getComentario());
        }
        TextView fecha = (TextView) findViewById(R.id.fecha);
        fecha.setText(DateFormat.getDateInstance().format(new Date(lugar.getFecha())));
        TextView hora = (TextView) findViewById(R.id.hora);
        hora.setText(DateFormat.getTimeInstance().format(new Date(lugar.getFecha())));
        final RatingBar valoracion = (RatingBar) findViewById(R.id.valoracion);
        valoracion.setRating(lugar.getValoracion());
        valoracion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float valor, boolean fromUser) {
                lugar.setValoracion(valor);
            }
        });
        imageView = (ImageView) findViewById(R.id.foto);
        ponerFoto(imageView, lugar.getFoto());
    }
}
