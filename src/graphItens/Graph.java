package graphItens;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Graph {
    private static class Par {
        int vertice;
        int peso; // ou 'distancia' no contexto do Dijkstra

        Par(int vertice, int peso) {
            this.vertice = vertice;
            this.peso = peso;
        }
    }

    static ArrayList<Integer> melhorSolucaoExata = null;
    static int melhorRaioExato = Integer.MAX_VALUE;

    // Constrói lista de adjacência
    static ArrayList<ArrayList<Par>> construirAdj(int[][] arestas, int V) {
        ArrayList<ArrayList<Par>> adj = new ArrayList<>();
        for (int i = 0; i <= V; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] aresta : arestas) {
            int u = aresta[0], v = aresta[1], peso = aresta[2];
            adj.get(u).add(new Par(v, peso));
            adj.get(v).add(new Par(u, peso));
        }
        return adj;
    }

    // Retorna distâncias mínimas a partir da origem
    static int[] dijkstra(int V, int[][] arestas, int origem) {
        ArrayList<ArrayList<Par>> adj = construirAdj(arestas, V); // Agora retorna ArrayList<ArrayList<Par>>

        // Fila de prioridade [distância, vértice]
        PriorityQueue<Par> fila = new PriorityQueue<>(Comparator.comparingInt(p -> p.peso));

        int[] distancia = new int[V + 1];
        Arrays.fill(distancia, Integer.MAX_VALUE);

        distancia[origem] = 0;
        fila.offer(new Par(origem, 0)); // 'peso' aqui é a distância

        while (!fila.isEmpty()) {
            Par atual = fila.poll();
            int distAtual = atual.peso;
            int u = atual.vertice;

            if (distAtual > distancia[u]) continue;

            for (Par vizinho : adj.get(u)) { // 'vizinho' agora é um 'Par'
                int v = vizinho.vertice;
                int pesoAresta = vizinho.peso;

                if (distancia[u] != Integer.MAX_VALUE && // Garante que 'u' é alcançável
                    distancia[v] > distancia[u] + pesoAresta) {
                // O resto do bloco continua igual
                    distancia[v] = distancia[u] + pesoAresta;
                    fila.offer(new Par(v, distancia[v]));
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
        
        // 1. Cria a matriz de distâncias final
        int[][] dist = new int[V + 1][V + 1];

        // 2. Roda o Dijkstra para cada vértice como origem
        for (int i = 1; i <= V; i++) {
            
            // Roda o Dijkstra a partir da origem 'i'
            // (Usando o método 'dijkstra' que acabamos de corrigir)
            int[] distanciasDe_i = dijkstra(V, edges, i);
            
            // Copia o resultado (distanciasDe_i) para a linha 'i' da matriz
            // System.arraycopy(distanciasDe_i, 1, dist[i], 1, V); 
            // Ou de forma mais simples:
            for (int j = 1; j <= V; j++) {
                dist[i][j] = distanciasDe_i[j];
            }
        }

        return dist; // Retorna a matriz preenchida
    }

    public static void kCentrosExatoRecursivo(int V, int k, int[][] dist, int start, ArrayList<Integer> centrosAtuais) {
        
        // --- CASO BASE: Uma combinação completa foi formada ---
        if (centrosAtuais.size() == k) {
            
            // 1. Processar esta combinação IMEDIATAMENTE
            int raioAtual = 0;
            boolean desconectado = false;

            for (int v = 1; v <= V; v++) {
                int minDist = Integer.MAX_VALUE;
                for (int c : centrosAtuais) {
                    minDist = Math.min(minDist, dist[v][c]);
                }

                // (Usando a correção de bug da sua pergunta anterior)
                if (minDist >= Integer.MAX_VALUE / 2) { 
                    desconectado = true;
                    raioAtual = Integer.MAX_VALUE;
                    break;
                }
                raioAtual = Math.max(raioAtual, minDist);
            }

            // 2. Atualiza a melhor solução global (estática)
            if (!desconectado && raioAtual < melhorRaioExato) {
                melhorRaioExato = raioAtual;
                melhorSolucaoExata = new ArrayList<>(centrosAtuais); // Salva uma cópia
            }
            return; // Fim deste ramo da recursão
        }

        // --- CASO RECURSIVO: Continuar gerando a combinação ---
        // (Note que 'i' começa de 'start', não de 1, para evitar duplicatas)
        for (int i = start; i <= V; i++) {
            centrosAtuais.add(i); // Escolhe o vértice 'i'
            
            // Chama recursivamente para o próximo nível
            kCentrosExatoRecursivo(V, k, dist, i + 1, centrosAtuais);
            
            // Backtrack: remove o vértice 'i' para testar o próximo (i+1) no loop
            centrosAtuais.remove(centrosAtuais.size() - 1); 
        }
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

        int[][] edges = carregarGrafo("./src/graphs/" + arquivo, V_temp, K_temp);

        int V = V_temp[0];
        int K = K_temp[0];

        // calcular matriz de distâncias (djisktra para cada vértice)
        int[][] dist = calcularMatrizDistancias(V, edges);

        System.out.println("distancias calculadas");

        // rodar método exato (força bruta)
        System.out.println("Rodando método exato (sem OOM)...");
        // Reseta as variáveis estáticas (caso rode várias vezes)
        melhorSolucaoExata = null;
        melhorRaioExato = Integer.MAX_VALUE;
        
        // Inicia a busca recursiva
        kCentrosExatoRecursivo(V, K, dist, 1, new ArrayList<>());
        
        System.out.println("Raio ótimo (exato): " + melhorRaioExato);
        System.out.println("Centros escolhidos (Exato): " + melhorSolucaoExata);

        // rodar método aproximado (Gonzalez)
        ArrayList<Integer> centrosAprox = kCentrosAproximado(V, K, dist);
        System.out.println("Centros escolhidos (Aproximado): " + centrosAprox);

    }
}
