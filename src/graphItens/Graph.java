package graphItens;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Graph {

    private static final int INF = 1000000000;

    // Leitura do arquivo de grafo
    public static int[][] carregarGrafo(String filepath, int[] totalVertices, int[] totalCentros) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String linha = br.readLine();
            String[] valores_iniciais = linha.trim().split("\\s+");
            int V = Integer.parseInt(valores_iniciais[0]);
            int M = Integer.parseInt(valores_iniciais[1]);
            int K = Integer.parseInt(valores_iniciais[2]);

            totalVertices[0] = V;
            totalCentros[0] = K;

            int[][] edges = new int[M][3];
            int pos = 0;

            System.out.println("V = " + V + " M = " + M + " K = " + K );

            while ((linha = br.readLine()) != null) {
                String[] valores = linha.trim().split("\\s+");
                if (valores.length == 3) {
                    int origem = Integer.parseInt(valores[0]);
                    int destino = Integer.parseInt(valores[1]);
                    int peso = Integer.parseInt(valores[2]);
                    edges[pos][0] = origem;
                    edges[pos][1] = destino;
                    edges[pos][2] = peso;
                    System.out.println("leu " + origem + "  " + destino + " " + peso);
                    pos++;
                }
            }
            return edges;
        }
    }

    // Construir matriz de adjacência
    public static int[][] construirMatrizAdj(int V, int[][] edges) {
        int[][] dist = new int[V + 1][V + 1]; // 1-based

        for (int i = 1; i <= V; i++) {
            Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
        }

        for (int[] e : edges) {
            int u = e[0];
            int v = e[1];
            int w = e[2];
            dist[u][v] = w;
            dist[v][u] = w; // se for não-direcionado
        }

        return dist;
    }

    // Floyd-Warshall
    public static void floydWarshall(int V, int[][] dist) {
        for (int k = 1; k <= V; k++) {
            for (int i = 1; i <= V; i++) {
                for (int j = 1; j <= V; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
    }

    // Gerar combinações de centros
    public static void gerarCombinacoes(int n, int k, int start,
        ArrayList<Integer> atual, ArrayList<ArrayList<Integer>> resultado) {
        if (atual.size() == k) {
            resultado.add(new ArrayList<>(atual));
            return;
        }
        for (int i = start; i <= n; i++) {
            atual.add(i);
            gerarCombinacoes(n, k, i + 1, atual, resultado);
            atual.remove(atual.size() - 1);
        }
    }

    // Avaliar uma combinação de centros
    public static int avaliarCombinacao(int[][] dist, ArrayList<Integer> centros, int V) {
        int maxDist = 0;
        for (int v = 1; v <= V; v++) {
            int minDist = INF;
            for (int c : centros) {
                minDist = Math.min(minDist, dist[v][c]);
            }
            maxDist = Math.max(maxDist, minDist);
        }
        return maxDist;
    }


    // Encontrar os K-Centros
    public static ArrayList<Integer> encontrarKCentros(int[][] dist, int V, int K) {
        ArrayList<ArrayList<Integer>> combinacoes = new ArrayList<>();
        gerarCombinacoes(V, K, 1, new ArrayList<>(), combinacoes);

        int melhorRaio = INF;
        ArrayList<Integer> melhorCombinacao = null;

        for (ArrayList<Integer> centros : combinacoes) {
            int raio = avaliarCombinacao(dist, centros, V);
            if (raio < melhorRaio) {
                melhorRaio = raio;
                melhorCombinacao = centros;
            }
        }

        System.out.println("Melhor raio encontrado: " + melhorRaio);
        return melhorCombinacao;
    }

    // Método aproximado de Gonzales (greedy)
    public static ArrayList<Integer> gonzalesKCentros(int[][] dist, int V, int K) {
        ArrayList<Integer> centros = new ArrayList<>();

        // 1. Escolhe arbitrariamente o primeiro centro (por exemplo, vértice 1)
        centros.add(1);

        // 2. Itera até ter K centros
        while (centros.size() < K) {
            int melhorVertice = -1;
            int maiorDist = -1;

            // Para cada vértice, calcula a distância ao centro mais próximo
            for (int v = 1; v <= V; v++) {
                int minDist = INF;
                for (int c : centros) {
                    minDist = Math.min(minDist, dist[v][c]);
                }
                // Escolhe o vértice mais distante dos centros atuais
                if (minDist > maiorDist) {
                    maiorDist = minDist;
                    melhorVertice = v;
                }
            }

            centros.add(melhorVertice);
        }

        // Calcula o raio obtido
        int raio = 0;
        for (int v = 1; v <= V; v++) {
            int minDist = INF;
            for (int c : centros) {
                minDist = Math.min(minDist, dist[v][c]);
            }
            raio = Math.max(raio, minDist);
        }

        System.out.println("Raio aproximado (Gonzales): " + raio);
        return centros;
    }

    public static void main(String[] args) throws IOException {
        String arquivo = "pmed1.txt";
        int[] V_temp = new int[1];
        int[] K_temp = new int[1];

        int[][] edges = carregarGrafo("./K-centros/src/graphs/" + arquivo, V_temp, K_temp);

        int V = V_temp[0];
        int K = K_temp[0];

        System.out.println("V_xt " + V + " K_xt " + K );

        int[][] dist = construirMatrizAdj(V, edges);
        floydWarshall(V, dist);

        ArrayList<Integer> centros = encontrarKCentros(dist, V, K);

        System.out.println("Centros escolhidos: " + centros);

        ArrayList<Integer> centrosGreedy = gonzalesKCentros(dist, V, K);
        System.out.println("Centros escolhidos (Gonzales): " + centrosGreedy);
    }
}