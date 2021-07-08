package com.example.mislugares;

public interface Lugares {
    Lugar elemento(int id);
    void anyade(Lugar lugar);
    int nuevo();
    void borrar(int id);
    int tamanyo();
    void actualiza(int id, Lugar lugar);
}
