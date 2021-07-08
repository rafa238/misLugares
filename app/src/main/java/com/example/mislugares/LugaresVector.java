package com.example.mislugares;

import java.util.ArrayList;
import java.util.List;

public class LugaresVector implements Lugares {
    protected List<Lugar> vectorLugares = ejemploLugares();

    public LugaresVector(){
        this.vectorLugares = ejemploLugares();
    }
    @Override
    public Lugar elemento(int id) {
        return vectorLugares.get(id);
    }

    @Override
    public void anyade(Lugar lugar) {
        vectorLugares.add(lugar);
    }

    @Override
    public int nuevo() {
        //Lugar lugar = new Lugar();
        //vectorLugares.add(lugar);
        return vectorLugares.size()-1;
    }

    @Override
    public void borrar(int id) {
        vectorLugares.remove(id);
    }

    @Override
    public int tamanyo() {
        return vectorLugares.size();
    }

    @Override
    public void actualiza(int id, Lugar lugar) {
        vectorLugares.set(id, lugar);
    }

    public static ArrayList<Lugar> ejemploLugares(){
        ArrayList<Lugar> lugares = new ArrayList<Lugar>();
        lugares.add(new Lugar("Escuela", "C/ Paranimf,1 46730 Gandia Spain", -0.166093, 38.995656, TipoLugar.EDUCACION, 962849300, "http://upv.com", "", 4));
        lugares.add(new Lugar("Bar", "C/ Paranimf,1 46730 Gandia Spain", -0.166093, 38.995656, TipoLugar.BAR, 962849300, "http://bar.com", "Buena", 3));
        return  lugares;
    }

}
