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

            System.out.print("[V = " + V + "; M = " + M + "; K = " + K  + "]");

            while ((linha = br.readLine()) != null) {
                String[] valores = linha.trim().split("\\s+");
                if (valores.length == 3) {
                    int origem = Integer.parseInt(valores[0]);
                    int destino = Integer.parseInt(valores[1]);
                    int peso = Integer.parseInt(valores[2]);
                    edges[pos][0] = origem;
                    edges[pos][1] = destino;
                    edges[pos][2] = peso;
                    // System.out.println("leu " + origem + "  " + destino + " " + peso); DEBUG
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

    // Dijkstra
    public static int[] dijkstra(int[][] matriz, int src, int V) {
        int[] dist = new int[V + 1];
        boolean[] visitado = new boolean[V + 1];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        for (int i = 1; i <= V; i++) {
            int u = -1;
            int menor = INF;
            // escolhe vértice não visitado com menor distância
            for (int j = 1; j <= V; j++) {
                if (!visitado[j] && dist[j] < menor) {
                    menor = dist[j];
                    u = j;
                }
            }
            if (u == -1) break;
            visitado[u] = true;

            // relaxa vizinhos de u
            for (int v = 1; v <= V; v++) {
                if (matriz[u][v] < INF && dist[u] + matriz[u][v] < dist[v]) {
                    dist[v] = dist[u] + matriz[u][v];
                }
            }
        }
        return dist;
    }

    // All-pairs shortest paths com Dijkstra
    public static int[][] allPairsShortestPaths(int V, int[][] matriz) {
        int[][] dist = new int[V + 1][V + 1];
        for (int i = 1; i <= V; i++) {
            dist[i] = dijkstra(matriz, i, V);
        }
        return dist;
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

    // Gerar combinações e avaliar diretamente (sem acumular todas)
    public static void gerarCombinacoesOnTheFly(int n, int k, int start,
            ArrayList<Integer> atual, int[][] dist, int V,
            int[] melhorRaio, ArrayList<Integer> melhorCombinacao) {

        if (atual.size() == k) {
            int raio = avaliarCombinacao(dist, atual, V);
            if (raio < melhorRaio[0]) {
                melhorRaio[0] = raio;
                melhorCombinacao.clear();
                melhorCombinacao.addAll(atual);
            }
            return;
        }

        for (int i = start; i <= n; i++) {
            atual.add(i);
            gerarCombinacoesOnTheFly(n, k, i + 1, atual, dist, V, melhorRaio, melhorCombinacao);
            atual.remove(atual.size() - 1);
        }
    }

    // Encontrar os K-Centros (exato)
    public static ArrayList<Integer> encontrarKCentros2(int[][] dist, int V, int K) {
        int[] melhorRaio = {INF};
        ArrayList<Integer> melhorCombinacao = new ArrayList<>();

        gerarCombinacoesOnTheFly(V, K, 1, new ArrayList<>(), dist, V, melhorRaio, melhorCombinacao);

        return melhorCombinacao;
    }

    // Encontrar os K-Centros (exato)
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

        // System.out.println("Melhor raio encontrado: " + melhorRaio);
        return melhorCombinacao;
    }

    // Método aproximado de Gonzales (greedy)
    public static ArrayList<Integer> gonzalesKCentros(int[][] dist, int V, int K) {
        ArrayList<Integer> centros = new ArrayList<>();

        // Escolhe arbitrariamente o primeiro centro (por exemplo, vértice 1)
        centros.add(1);

        // Itera até ter K centros
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

        // System.out.println("Raio aproximado (Gonzales): " + raio); DEBUG
        return centros;
    }
    
    //ANTIGA MAIN

    public static void main(String[] args) throws IOException {
        String arquivo = "pmed6.txt";
        int[] V_temp = new int[1];
        int[] K_temp = new int[1];

        int[][] edges = carregarGrafo("./src/graphs/" + arquivo, V_temp, K_temp);

        int V = V_temp[0];
        int K = K_temp[0];

        int[][] matriz = construirMatrizAdj(V, edges);
        // floydWarshall(V, dist);
        int[][] dist = allPairsShortestPaths(V, matriz);

        long inicio = System.nanoTime();
        ArrayList<Integer> centros = encontrarKCentros2(dist, V, K);

        long fim = System.nanoTime();
        double somaTempos = (fim - inicio);

        double mediaMs = somaTempos / 1_000_000.0;

        System.out.print(" - Arquivo: " + arquivo);
        // System.out.println("Centros escolhidos (Gonzales): " + centrosGreedy);
        System.out.printf(" - Tempo médio : %.3f ms%n", mediaMs);

        // System.out.println("Centros escolhidos: " + centros);

        // ArrayList<Integer> centrosGreedy = gonzalesKCentros(dist, V, K);
        // System.out.println("Centros escolhidos (Gonzales): " + centrosGreedy);
    }

    // public static void main(String[] args) throws IOException {
    //     int totalGrafos = 40;

    //     System.out.println("-----------------------------------");
    //     for (int g = 1; g <= totalGrafos; g++) {
    //         String arquivo = "pmed" + g + ".txt";
    //         int[] V_temp = new int[1];
    //         int[] K_temp = new int[1];

    //         int[][] edges = carregarGrafo("./K-centros/src/graphs/" + arquivo, V_temp, K_temp);

    //         int V = V_temp[0];
    //         int K = K_temp[0];

    //         int[][] matriz = construirMatrizAdj(V, edges);
    //         int[][] dist = allPairsShortestPaths(V, matriz);

    //         // Executa método aproximado 10 vezes e calcula média
    //         long somaTempos = 0;
    //         ArrayList<Integer> centrosGreedy = null;

    //         for (int rep = 0; rep < 10; rep++) {
    //             long inicio = System.nanoTime();
    //             centrosGreedy = gonzalesKCentros(dist, V, K);
    //             long fim = System.nanoTime();
    //             somaTempos += (fim - inicio);
    //         }

    //         double mediaMs = (somaTempos / 10.0) / 1_000_000.0;

    //         System.out.print(" - Arquivo: " + arquivo);
    //         // System.out.println("Centros escolhidos (Gonzales): " + centrosGreedy);
    //         System.out.printf(" - Tempo médio (10 execuções): %.3f ms%n", mediaMs);
    //         System.out.println("-----------------------------------");
    //     }
    // }
}