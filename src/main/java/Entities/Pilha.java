/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entities;

/**
 *
 * @author Gabriel Krzizanowski
 */
public interface Pilha<T> {

	public void push(T info);
	
	public T pop();
	
	public T peek();
	
	public boolean estaVazia();
	
	public void liberar();
}