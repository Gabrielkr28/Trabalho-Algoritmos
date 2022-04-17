/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entities;

/**
 *
 * @author Gabriel Krzizanowski
 */
public class PilhaVetor<T> implements Pilha<T> {

    private T info[];
    private Integer limite;
    private Integer tamanho;

    @SuppressWarnings("unchecked")
    public PilhaVetor(int limite) {
        info = (T[]) new Object[limite];
        this.limite = limite;
        this.tamanho = 0;
    }

    @Override
    public void push(T info) {
        if (tamanho.equals(limite)) {
            throw new PilhaCheiaException(null);
        } else {
            this.info[tamanho] = info;
            tamanho++;
        }
    }

    @Override
    public T pop() {
        T valor = peek();
        info[tamanho - 1] = null;
        tamanho--;
        return valor;
    }

    @Override
    public T peek() {
        if (estaVazia()) {
            throw new PilhaVaziaException(null);
        }
        return info[tamanho - 1];
    }

    @Override
    public boolean estaVazia() {
        return tamanho == 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void liberar() {
        info = (T[]) new Object[limite];
        tamanho = 0;

    }

    @Override
    public String toString() {
        String resultado = "";

        for (int i = tamanho; i >= 0; i++) {
            resultado += info[i].toString();
            if (i > 0) {
                resultado = resultado + ",";
            }
        }
        return resultado;
    }

    public void concatenar(PilhaVetor<T> p) {
        for (int i = 0; i < p.tamanho; i++) {
            this.push(p.info[i]);
        }
    }

}
