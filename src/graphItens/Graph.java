package graphItens;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Graph {
    // Constrói lista de adjacência
    static ArrayList<ArrayList<ArrayList<Integer>>> construirAdj(int[][] arestas, int V) {
        ArrayList<ArrayList<ArrayList<Integer>>> adj = new ArrayList<>();

        for (int i = 0; i <= V; i++) {
            adj.add(new ArrayList<>());
        }

        // Preencher lista de adjacência
        for (int[] aresta : arestas) {
            int u = aresta[0];
            int v = aresta[1];
            int peso = aresta[2];

            ArrayList<Integer> e1 = new ArrayList<>();
            e1.add(v);
            e1.add(peso);
            adj.get(u).add(e1);

            ArrayList<Integer> e2 = new ArrayList<>();
            e2.add(u); e2.add(peso);
            adj.get(v).add(e2); // adiciona a volta (grafo não direcionado)
        }

        return adj;
    }

    // Retorna distâncias mínimas a partir da origem
    static int[] dijkstra(int V, int[][] arestas, int origem) {

        ArrayList<ArrayList<ArrayList<Integer>>> adj = construirAdj(arestas, V);

        // Fila de prioridade [distância, vértice]
        PriorityQueue<ArrayList<Integer>> fila = new PriorityQueue<>(Comparator.comparingInt(a -> a.get(0)));

        int[] distancia = new int[V + 1];
        Arrays.fill(distancia, Integer.MAX_VALUE);

        distancia[origem] = 0;
        ArrayList<Integer> inicio = new ArrayList<>();
        inicio.add(0);
        inicio.add(origem);
        fila.offer(inicio);

        while (!fila.isEmpty()) {
            ArrayList<Integer> atual = fila.poll();
            int distAtual = atual.get(0);
            int u = atual.get(1);

            // Skip de entradas desatualizadas
            if (distAtual > distancia[u]) continue;

            for (ArrayList<Integer> vizinho : adj.get(u)) {
                int v = vizinho.get(0);
                int peso = vizinho.get(1);

                if (distancia[v] > distancia[u] + peso) {
                    distancia[v] = distancia[u] + peso;
                    ArrayList<Integer> temp = new ArrayList<>();
                    temp.add(distancia[v]);
                    temp.add(v);
                    fila.offer(temp);
                }
            }
        }

        return distancia;
    }

    public static void gerarCombinacoes(int n, int k, int start,
        ArrayList<Integer> atual, ArrayList<ArrayList<Integer>> resultado) {
        if (atual.size() == k) {
            resultado.add(new ArrayList<>(atual));
            return;
        }

        // 1-based
        for (int i = start; i <= n; i++) {
            atual.add(i);
            gerarCombinacoes(n, k, i + 1, atual, resultado);
            atual.remove(atual.size() - 1);
        }
    }

    // Leitura do arquivo de grafo
    public static int[][] carregarGrafo(String filepath, int[] totalVertices, int[] totalCentros) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

            // obter numero de vértices e numero de arestas do grafo
            String linha = br.readLine();
            String[] valores_iniciais = linha.trim().split("\\s+");
            int V = Integer.parseInt(valores_iniciais[0]);
            int M = Integer.parseInt(valores_iniciais[1]);
            int K = Integer.parseInt(valores_iniciais[2]);

            // obter pelos vetores para a função retornar apenas a matriz de adjacencia
            totalVertices[0] = V;
            totalCentros[0] = K;

            // System.out.println("Valores lidos: " + V + " " + M + " " + K); //debug
            int[][] edges = new int[M][3]; // origem -> destino + peso
            int pos = 0;

            // ler e montar a matriz de adjacencia do grafo
            while ((linha = br.readLine()) != null) {
                String[] valores = linha.trim().split("\\s+");
                if (valores.length == 3) {
                    int origem = Integer.parseInt(valores[0]);
                    int destino = Integer.parseInt(valores[1]);
                    int peso = Integer.parseInt(valores[2]);
                    edges[pos][0] = origem;
                    edges[pos][1] = destino;
                    edges[pos][2] = peso;
                    pos++;
                }
            }
            return edges;
        }
    }


    // Calcula matriz de distâncias entre todos os pares de vértices usando Floyd-Warshall
    public static int[][] calcularMatrizDistancias(int V, int[][] edges) {
        int INF = Integer.MAX_VALUE / 2; // evitar overflow
        int[][] dist = new int[V + 1][V + 1];

        // inicializar matriz
        for (int i = 1; i <= V; i++) {
            Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
        }

        // carregar arestas
        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            dist[u][v] = Math.min(dist[u][v], w);
            dist[v][u] = Math.min(dist[v][u], w); // grafo não-direcionado
        }

        // Floyd-Warshall
        for (int k = 1; k <= V; k++) {
            for (int i = 1; i <= V; i++) {
                for (int j = 1; j <= V; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        return dist;
    }

    public static ArrayList<Integer> kCentrosExato(int V, int k, int[][] dist) {
        ArrayList<ArrayList<Integer>> combinacoes = new ArrayList<>();
        gerarCombinacoes(V, k, 1, new ArrayList<>(), combinacoes); // gera todas as combinações

        ArrayList<Integer> melhor = null;
        int melhorRaio = Integer.MAX_VALUE;

        for (ArrayList<Integer> centros : combinacoes) {
            int raio = 0;
            boolean desconectado = false;

            // calcula o raio dessa combinação
            for (int v = 1; v <= V; v++) {
                int minDist = Integer.MAX_VALUE;
                for (int c : centros) {
                    minDist = Math.min(minDist, dist[v][c]);
                }
                if (minDist == Integer.MAX_VALUE) {
                    // vértice inalcançável → raio infinito
                    desconectado = true;
                    raio = Integer.MAX_VALUE;
                    break;
                }
                raio = Math.max(raio, minDist);
            }

            // atualiza melhor solução
            if (!desconectado && raio < melhorRaio) {
                melhorRaio = raio;
                melhor = new ArrayList<>(centros); // copia para evitar referência
            }
        }

        System.out.println("Raio ótimo (exato): " + melhorRaio);
        return melhor;
    }

    public static ArrayList<Integer> kCentrosAproximado(int V, int K, int[][] dist) {
        ArrayList<Integer> centros = new ArrayList<>();
        centros.add(1); // primeiro centro arbitrário

        while (centros.size() < K) {
            int maisDistante = -1;
            int maxDist = -1;
            for (int v = 1; v <= V; v++) {
                int minDist = Integer.MAX_VALUE;
                for (int c : centros) {
                    minDist = Math.min(minDist, dist[v][c]);
                }
                if (minDist > maxDist) {
                    maxDist = minDist;
                    maisDistante = v;
                }
            }
            centros.add(maisDistante);
        }

        // calcular raio final
        int raio = 0;
        for (int v = 1; v <= V; v++) {
            int minDist = Integer.MAX_VALUE;
            for (int c : centros) {
                minDist = Math.min(minDist, dist[v][c]);
            }
            raio = Math.max(raio, minDist);
        }
        System.out.println("Raio aproximado: " + raio);

        return centros;
    }

    public static void main(String[] args) throws IOException {
        String arquivo = "pmed1.txt";
        int[] V_temp = new int[1];
        int[] K_temp = new int[1];

        // carregar o grafo

        int[][] edges = carregarGrafo("./K-centros/src/graphs/" + arquivo, V_temp, K_temp);

        int V = V_temp[0];
        int K = K_temp[0];

        // calcular matriz de distâncias (djisktra para cada vértice)
        int[][] dist = calcularMatrizDistancias(V, edges);

        System.out.println("distancias calculadas");

        // rodar método exato (força bruta)
        ArrayList<Integer> centrosExatos = kCentrosExato(V, K, dist);
        System.out.println("Centros escolhidos (Exato): " + centrosExatos);

        // rodar método aproximado (Gonzalez)
        ArrayList<Integer> centrosAprox = kCentrosAproximado(V, K, dist);
        System.out.println("Centros escolhidos (Aproximado): " + centrosAprox);

    }
}
