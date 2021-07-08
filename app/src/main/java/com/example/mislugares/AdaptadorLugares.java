package com.example.mislugares;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

class AdaptadorLugares extends RecyclerView.Adapter<AdaptadorLugares.ViewHolder> {
    protected Lugares lugares;
    protected View.OnClickListener onClickListener;

    public void setOnItemClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }
    public AdaptadorLugares(Lugares lugares){
        this.lugares = lugares;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.elemento_lista, viewGroup, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Lugar lugar = lugares.elemento(i);
        viewHolder.personaliza(lugar);
    }

    @Override
    public int getItemCount() {
        return lugares.tamanyo();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nombre, direccion;
        public ImageView foto;
        public RatingBar valoracion;
        public ViewHolder(View itemView){
            super(itemView);
            nombre = (TextView)itemView.findViewById(R.id.nombre);
            direccion = (TextView)itemView.findViewById(R.id.direccion);
            foto = (ImageView)itemView.findViewById(R.id.foto);
            valoracion = (RatingBar)itemView.findViewById(R.id.valoracion);
        }

        public void personaliza(Lugar lugar) {
            nombre.setText(lugar.getNombre());
            direccion.setText(lugar.getDireccion());
            int id;
            switch(lugar.getTipo()) {
                case RESTAURANTE:id = R.drawable.restaurante; break;
                case BAR:    id = R.drawable.bar;     break;
                case COPAS:   id = R.drawable.copas;    break;
                case ESPECTACULO:id = R.drawable.espectaculos; break;
                case HOTEL:   id = R.drawable.hotel;    break;
                case COMPRAS:  id = R.drawable.compras;   break;
                case EDUCACION: id = R.drawable.educacion;  break;
                case DEPORTE:  id = R.drawable.deporte;   break;
                case NATURALEZA: id = R.drawable.naturaleza; break;
                case GASOLINERA: id = R.drawable.gasolinera; break;
                default: id= R.drawable.otros;}
            foto.setImageResource(id);
            foto.setScaleType(ImageView.ScaleType.FIT_END);
            valoracion.setRating(lugar.getValoracion());
        }
    }
}
