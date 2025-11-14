package algorithms;

import java.util.*;

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
 * "https://www.geeksforgeeks.org/dsa/greedy-approximate-algorithm-for-k-centers-problem">Bag.java</a>
 * </p>
 * 
 * @author Robert Sedgewick
 * @author Kevin Wayne
 * 
 */

class GFG{

static int maxindex(int[] dist, int n)
{
    int mi = 0;
    for(int i = 0; i < n; i++)
    {
        if (dist[i] > dist[mi])
            mi = i;
    }
    return mi;
}

static void selectKcities(int n, int weights[][], 
                          int k)
{
    int[] dist = new int[n];
    ArrayList<Integer> centers = new ArrayList<>();
    for(int i = 0; i < n; i++) 
    {
        dist[i] = Integer.MAX_VALUE;
    }

    // Index of city having the
    // maximum distance to it's
    // closest center
    int max = 0;
    for(int i = 0; i < k; i++)
    {
        centers.add(max);
        for(int j = 0; j < n; j++)
        {
            
            // Updating the distance
            // of the cities to their
            // closest centers
            dist[j] = Math.min(dist[j], 
                               weights[max][j]);
        }

        // Updating the index of the
        // city with the maximum
        // distance to it's closest center
        max = maxindex(dist, n);
    }

    // Printing the maximum distance
    // of a city to a center
    // that is our answer
    System.out.println(dist[max]);

    // Printing the cities that
    // were chosen to be made
    // centers
    for(int i = 0; i < centers.size(); i++) 
    {
        System.out.print(centers.get(i) + " ");
    }
    System.out.print("\n");
}

// Driver Code
public static void main(String[] args)
{
    int n = 4;
    int[][] weights = new int[][]{ { 0, 4, 8, 5 }, 
                                   { 4, 0, 10, 7 }, 
                                   { 8, 10, 0, 9 }, 
                                   { 5, 7, 9, 0 } };
    int k = 2;

    // Function Call
    selectKcities(n, weights, k);
}
}

