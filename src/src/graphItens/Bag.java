package src.graphItens;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Estrutura de dados Bag implementada com lista encadeada.
 * 
 * <p>
 * Este código foi adaptado por Pedro Félix, Matheus Coxir e Gabriel Diniz.
 * </p>
 * 
 * <p>
 * A referência de base foi desenvolvida pelos autores originais listados
 * abaixo.
 * <br>
 * Fonte: <a href=
 * "https://algs4.cs.princeton.edu/13stacks/Bag.java.html">Bag.java</a>
 * </p>
 * 
 * @author Robert Sedgewick
 * @author Kevin Wayne
 * 
 */
public class Bag<T extends Comparable<T>> implements Iterable<T> {
    private Node<T> first; // início da lista encadeada
    private int n; // Grau de saída

    // Nó da lista encadeada
    private static class Node<T> {
        private T item;
        private Node<T> next;
    }

    public Bag() {
        first = null;
        n = 0;
    }

    public boolean isEmpty() {
        return first == null;
    }

    // retorna o número de itens/ grau do elemento
    public int size() {
        return n;
    }

    // adiciona o item na ordem crescente
    public void add(T item) {
        Node<T> newNode = new Node<>();
        newNode.item = item;

        // Inserção ordenada
        if (first == null || item.compareTo(first.item) <= 0) {
            newNode.next = first;
            first = newNode;
        } else {
            Node<T> current = first;
            while (current.next != null && item.compareTo(current.next.item) > 0) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
        n++;
    }

    // retorna um iterador que percorre os itens na ordem crescente
    public Iterator<T> iterator() {
        return new LinkedIterator(first);
    }

    // iterador da lista encadeada
    private class LinkedIterator implements Iterator<T> {
        private Node<T> current;

        public LinkedIterator(Node<T> first) {
            current = first;
        }

        public boolean hasNext() {
            return current != null;
        }

        public T next() {
            if (!hasNext())
                throw new NoSuchElementException();
            T item = current.item;
            current = current.next;
            return item;
        }
    }
}