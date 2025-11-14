package src.graphItens;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileGraph {
    private String path;
    private String outputPath;
    private BufferedReader br;

    public FileGraph(String path, String outputPath) {
        this.path = path;
        this.outputPath = outputPath;
        initReader();
    }

    public FileGraph(String path) {
        this.path = path;
        this.outputPath = "";
        initReader();
    }

    private void initReader() {
        try {
            br = new BufferedReader(new FileReader(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] ReadLine() {
        try {
            String line = br.readLine();
            if (line != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 2) {
                    int a = Integer.parseInt(parts[0]);
                    int b = Integer.parseInt(parts[1]);
                    return new int[] { a, b };
                }
            } else {
                close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            if (br != null)
                br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public String toString() {
        return "FileGraph [path=" + path + ", outputPath=" + outputPath + "]";
    }
}