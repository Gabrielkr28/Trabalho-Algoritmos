/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

/**
 *
 * @author Pichau
 */
public class ListaEncadeada<T> {
    private NoLista<T> primeiro;

    public ListaEncadeada() {
    }

    public NoLista getPrimeiro() {
        return primeiro;
    }

    public void inserir(T info) {
        NoLista novoNo = new NoLista();
        novoNo.setInfo(info);
        novoNo.setProximo(primeiro);
        this.primeiro = novoNo;
    }

    public void exibir() {
        NoLista algumNo = new NoLista();
        algumNo = primeiro;

        while(algumNo != null) {
            System.out.println(algumNo.getInfo());
            algumNo = algumNo.getProximo();
        }
    }

    public boolean estaVazia() {
        return primeiro == null;
    }

    public NoLista buscar(T valor) {
        NoLista algumNo = primeiro;

        while(algumNo != null) {
            if(algumNo.getInfo() == valor) {
                return algumNo;
            }

            algumNo = algumNo.getProximo();
        }

        return null;
    }

    public void retirar(T valor) {
        NoLista anterior = null;
        NoLista algumNo = primeiro;

        while(algumNo != null && algumNo.getInfo() != valor) {
            anterior = algumNo;
            algumNo = algumNo.getProximo();
        }

        if(algumNo != null) {
            if(anterior == null) {
                primeiro = algumNo.getProximo();
            } else {
                anterior.setProximo(algumNo.getProximo());
            }
        }
    }

    public int obterComprimento() {
        int count = 0;
        NoLista proximo = primeiro;

        while(proximo != null) {
            if(proximo == primeiro) {
                proximo = primeiro.getProximo();
            } else {
                proximo = proximo.getProximo();
            }

            count++;
        }

        return count;
    }

//    public NoLista<T> obterNo(int index){
//        if(index > obterComprimento() ||index < 0) {
//            throw new IndexOutOfBoundsException();
//        }
//
//        NoLista<T> atualNo = primeiro;
//        NoLista<T> noRetornado = new NoLista<T>();
//
//        boolean noExiste = false;
//        int count = 0;
//        int ultimaPosicao = obterComprimento() - 1;
//
//        while(noExiste != true) {
//            if(count == index) {
//                noExiste = true;
//                noRetornado =  atualNo;
//            }
//
//            atualNo = atualNo.getProximo();
//            count++;
//        }
//
//        return noRetornado;
//    }
    
    public NoLista<T> obterNo(int index){

        NoLista<T> atualNo = primeiro;
        NoLista<T> noRetornado = null;

        boolean noExiste = false;
        int count = 0;

        while(noExiste != true) {
            if(count > index || index < 0) {
                throw new IndexOutOfBoundsException("indice invalida");
            }

            if(count == index) {
                noExiste = true;
                noRetornado = atualNo;
            }

            atualNo = atualNo.getProximo();
            count++;
        }

        return noRetornado;
    }

    @Override
    public String toString() {
        NoLista atualNo = primeiro;
        String lista = "";

        while(atualNo != null) {
            lista += atualNo.getInfo() + ", ";

            atualNo = atualNo.getProximo();
        }

        lista = lista.substring(0, lista.length() - 1);
        return lista;
    }
}

