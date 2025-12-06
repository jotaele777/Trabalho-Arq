package Simulador;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileParser {

    public static Map<Short, String> loadMP(MainMemory MP, String filepath) {
        Map<Short, String> instructionTable = new HashMap<>();
        
        // Variável para rastrear o endereço atual da memória (0, 1, 2...)
        int currentAddress = 0; 

        try {
            BufferedReader bf = null;
            try {
                bf = getBufferedReader(filepath);
            } catch (IOException e) {
                bf = new BufferedReader(new FileReader(filepath));
            }
            
            String line;
            Assembler assembler = new Assembler();
            
            while ((line = bf.readLine()) != null) {
                line = line.trim();
                
                // Ignora linhas vazias e comentários
                if (line.isEmpty() || line.startsWith("//") || line.startsWith(";")) {
                    continue;
                }
                
                // Remove comentários inline (ex: "LODD 10 // comentário")
                int commentIndex = line.indexOf("//");
                if (commentIndex != -1) {
                    line = line.substring(0, commentIndex).trim();
                }
                commentIndex = line.indexOf(";");
                if (commentIndex != -1) {
                    line = line.substring(0, commentIndex).trim();
                }
                
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    // Verifica se é instrução binária direta (0s e 1s) ou Assembly
                    if (line.matches("[01\\s]+")) {
                        String binary = line.replaceAll("\\s+", "");
                        if (binary.length() > 0) {
                            short machineCode = (short) Integer.parseInt(binary, 2);
                            
                            // CORREÇÃO AQUI: Passamos o endereço atual e o código
                            MP.add(currentAddress, machineCode);
                            currentAddress++; // Incrementa para a próxima linha
                            
                            System.out.println("Loaded binary: " + binary + " -> " + machineCode);
                        }
                    } else {
                        // É Assembly
                        short machineCode = assembler.assembleAndLoad(line);
                        
                        // CORREÇÃO AQUI: Passamos o endereço atual e o código
                        MP.add(currentAddress, machineCode);
                        instructionTable.put(machineCode, line);
                        currentAddress++; // Incrementa para a próxima linha
                        
                        System.out.println("Loaded assembly: " + line + " -> " + machineCode);
                    }
                    
                } catch (Exception e) {
                    System.err.println("Erro ao processar instrução: " + line);
                    System.err.println("Erro: " + e.getMessage());
                }
            }
            
            bf.close();
            System.out.println("Carregamento concluído. Total de instruções: " + currentAddress);
            System.out.println();

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
        return instructionTable;
    }

    public static int[] getControlMemory() {
        int [] output = new int[256];
        String line;
        int index = 0;
        try {
            // Tenta carregar do resources ou do sistema de arquivos
            BufferedReader bf = null;
            try {
                 bf = getBufferedReader("/dataFiles/controlMemory.txt");
            } catch (IOException e) {
                 // Fallback para arquivo local se não achar no resources
                 bf = new BufferedReader(new FileReader("dataFiles/controlMemory.txt"));
            }

            while ((line = bf.readLine()) != null && index < 256) {
                String binary = line.replaceAll("\\s+", "");
                if (binary.length() >= 32) { // Garante que tem bits suficientes
                    binary = binary.substring(0, 32);
                    long converted = Long.parseLong(binary, 2);
                    output[index] = (int)converted;
                    index++;
                }
            }
        } catch (IOException e) {
            System.err.println("ERRO: Falha ao ler memória de controle (controlMemory.txt).");
            // Se falhar, retorna array vazio mas não mata o programa, para permitir debug
            return new int[256]; 
        }
        return output;
    }

    public static String [] getMicroProgramCode(String filename) {
        List<String> programLines = new ArrayList<>();
        String line;

        try {
            BufferedReader bf = null;
            try {
                bf = getBufferedReader(filename);
            } catch (Exception e) {
                bf = new BufferedReader(new FileReader(filename));
            }

            while ((line = bf.readLine()) != null) {
                programLines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Falha ao carregar código do microprograma (apenas visualização).");
            return new String[0];
        }
        return programLines.toArray(new String[0]);
    }

    private static BufferedReader getBufferedReader(String resourcePath) throws IOException {
        InputStream inputStream = FileParser.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Recurso não encontrado: " + resourcePath);
        }
        return new BufferedReader(new InputStreamReader(inputStream));
    }
    
    // Método auxiliar removido pois não é mais usado (getMacroInstructions), 
    // já que o loadMP faz tudo agora.
}