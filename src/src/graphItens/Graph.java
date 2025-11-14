package src.graphItens;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

/**
 * Gráfico não direcionado, usando lista de adjacência.
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
 * "https://algs4.cs.princeton.edu/41graph/Graph.java.html">Graph.java</a>
 * </p>
 * 
 * @author Robert Sedgewick
 * @author Kevin Wayne
 * 
 */
public class Graph {
    private static final String NEWLINE = System.getProperty("line.separator");

    private final int V; // número de vértices
    private int E; // número de arestas
    public Bag<Integer>[] adj; // lista de adjacência

    // Cria um grafo com V vértices e 0 arestas
    public Graph(int V) {
        if (V < 0)
            throw new IllegalArgumentException("Number of vertices must be non-negative");
        this.V = V;
        this.E = 0;
        adj = (Bag<Integer>[]) new Bag[V + 1];
        for (int v = 1; v <= V; v++) {
            adj[v] = new Bag<Integer>();
        }

    }

    // Cria um grafo a partir de um FileGraph
    public Graph(FileGraph fg) {
        try {

            int[] header = fg.ReadLine();
            if (header == null || header.length < 2) {
                throw new IllegalArgumentException("Arquivo inválido: primeira linha deve conter V e E");
            }

            this.V = header[0]; // número de vértices
            this.E = 0;

            if (V < 0)
                throw new IllegalArgumentException("Número de vértices em um Graph não pode ser negativo");

            if (E < 0)
                throw new IllegalArgumentException("Número de arestas em um Graph não pode ser negativo");

            // inicializa as listas de adjacência com V+1 posições (índices 1 a V)
            adj = (Bag<Integer>[]) new Bag[V + 1];
            for (int v = 1; v <= V; v++) {
                adj[v] = new Bag<Integer>();
            }

            // lê as arestas
            int[] edge;
            while ((edge = fg.ReadLine()) != null) {
                if (edge.length < 2) {
                    throw new IllegalArgumentException("Arquivo inválido");
                }
                int v = edge[0];
                int w = edge[1];

                addEdge(v, w);
            }

            if (header[1] != E) {
                throw new IllegalArgumentException("Número de arestas lidas diferente do esperado");
            }

        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("invalid input format in Graph constructor", e);
        }
    }

    // Cria um novo grafo que é uma cópia profunda de G
    public Graph(Graph G) {
        this.V = G.V();
        this.E = G.E();
        if (V < 0)
            throw new IllegalArgumentException("Number of vertices must be non-negative");

        adj = (Bag<Integer>[]) new Bag[V + 1];
        for (int v = 1; v <= V; v++) {
            adj[v] = new Bag<Integer>();
        }

        // mantém a ordem das listas de adjacência
        for (int v = 1; v <= G.V(); v++) {
            Stack<Integer> reverse = new Stack<Integer>();
            for (int w : G.adj[v]) {
                reverse.push(w);
            }
            for (int w : reverse) {
                adj[v].add(w);
            }
        }
    }

    // retorna a lista de adjacência
    public Bag<Integer>[] getAdj() {
        return this.adj;
    }

    // converte Bag[] em Set[] para uso no TarjanAdj
    @SuppressWarnings("unchecked")
    public Set<Integer>[] getAdjSet() {
        Set<Integer>[] sets = new HashSet[V + 1];
        for (int i = 1; i <= V; i++) {
            sets[i] = new HashSet<>();
            for (int v : adj[i]) {
                sets[i].add(v);
            }
        }
        return sets;
    }

    // retorna o número de vértices
    public int V() {
        return V;
    }

    // retorna o número de arestas
    public int E() {
        return E;
    }

    private void validateVertex(int v) {
        if (v < 1 || v > V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    // adiciona a aresta v-w ao grafo
    public void addEdge(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        E++;

        // adiciona w à lista de v e v à lista de w
        adj[v].add(w);
        adj[w].add(v);
    }

    // retorna os vértices adjacentes a v
    public Iterable<Integer> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    // retorna o grau do vértice v
    public int degree(int v) {
        validateVertex(v);
        return adj[v].size();
    }

    // Busca em Profundidade Recursiva marcando os vértices encontrados
    private void dfsUtil(int v, boolean[] visited) {
        visited[v] = true;
        // percorre todos os vértices adjacentes a v
        for (int neighbor : adj[v]) {
            // se o vértice ainda não foi visitado, chama dfsUtil recursivamente
            if (!visited[neighbor]) {
                dfsUtil(neighbor, visited);
            }
        }
    }

    public boolean isConnected() {
        if (V <= 1)
            return true; // grafos com 0 ou 1 vertices sao conexos

        boolean[] visited = new boolean[V + 1];
        int i;
        // encontra um vértice com grau maior que 0
        for (i = 1; i <= V; i++) {
            if (adj[i] != null && adj[i].size() > 0)
                break;
        }

        // inicia DFS a partir desse vértice com grau maior que 0
        dfsUtil(i, visited);

        // verifica se todos os vértices com grau maior que 0 foram visitados
        for (int j = 1; j <= V; j++) {
            if (!visited[j])
                return false;
        }
        return true;
    }

    public void removeEdge(int v, int w) {
        // Remove a aresta entre v e w, se existir
        validateVertex(v);
        validateVertex(w);

        boolean found = false;
        Bag<Integer> newV = new Bag<>();

        // Reconstrói a lista de adjacência de v sem w
        for (int x : adj[v]) {
            if (x != w)
                newV.add(x);
            else
                found = true;
        }
        adj[v] = newV;

        // Reconstrói a lista de adjacência de w sem v
        Bag<Integer> newW = new Bag<>();
        for (int x : adj[w]) {
            if (x != v)
                newW.add(x);
        }
        adj[w] = newW;

        if (found) {
            E--;
        }
    }

    public boolean isEulerian() {
        if (!isConnected()) {
            return false; // não é conexo
        }

        // conta quantos vértices têm grau ímpar
        int oddCount = 0;
        for (int v = 1; v <= V; v++) {
            if (degree(v) % 2 != 0) {
                oddCount++;
            }
        }

        // Euleriano se todos os graus forem pares
        return oddCount == 0;
    }

    public boolean isSemiEulerian() {
        if (!isConnected()) {
            return false;
        }

        int oddCount = 0;
        for (int v = 1; v <= V; v++) {
            if (degree(v) % 2 != 0) {
                oddCount++;
            }
        }

        // Semi-Euleriano se exatamente 2 vértices têm grau ímpar
        return oddCount == 2;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " vertices, " + E + " edges " + NEWLINE);
        for (int v = 1; v <= V; v++) {
            s.append(v + ": ");
            for (int w : adj[v]) {
                s.append(w + " ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

}