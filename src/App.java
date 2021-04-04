import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import java.io.File;  
import java.io.FileNotFoundException;
import java.util.Scanner; 

public class App {
    private static Random rand = new Random();
    public static int[][] alunos; // Configuração inicial
    public static int[][] populacao;
    public static int[][] intermediaria;
    public static int TAM_POPULACAO = 5;
    public static int TAM_ALUNOS;
    
    public static void main(String[] args) throws Exception {
        //int melhor;
        initAlunos("duplos4.txt");
        printAlunos();  
        initPopulacao();
        for (int g=0; g<1000; g++)
        {
            System.out.println("Geração: " + (g+1));
            calculaAptidao();
            //if (g%500 == 0)
                //printPopulacao(); 
            int c = getMelhor(); 
            boolean ideal = checkIdeal(c);
            if (ideal)
                break;
            crossover();
            populacao = intermediaria.clone();
            if(rand.nextInt(5)==0) {
                mutacao();
            }	          
        }
        printPopulacao(); 
    }

    public static boolean checkIdeal(int cromossomo)
    {
        if (populacao[cromossomo][TAM_ALUNOS] == 0)
            return true;
        return false;
    }

    public static void initAlunos(String filename)
    {
        try {
            File config = new File(filename);
            Scanner s = new Scanner(config);
            TAM_ALUNOS = Integer.parseInt(s.nextLine());
            alunos = new int[TAM_ALUNOS*2][TAM_ALUNOS];
            for(int i = 0; i < TAM_ALUNOS*2; i++)
            {
                if (s.hasNextLine())
                {
                    String outraEscola = " B";
                    if (i >= TAM_ALUNOS)
                        outraEscola = " A";
                    String[] afinidades = s.nextLine().split(outraEscola);
                    for(int j = 1; j < afinidades.length; j++ )
                        alunos[i][j-1] = Integer.parseInt(afinidades[j].trim()) - 1;
                }
            }

            s.close();
          } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void printAlunos()
    {
        System.out.println("\nConfiguração inicial: ");
        // Escola A
        for(int i = 0; i < TAM_ALUNOS; i++)
        {
            System.out.print("A"+(i+1)+": ");
            for(int j = 0; j < TAM_ALUNOS; j++)
            {
                System.out.print("B"+(alunos[i][j]+1)+" ");
            }
            System.out.println();
        }
        // Escola B
        for(int i = TAM_ALUNOS; i < TAM_ALUNOS*2; i++)
        {
            System.out.print("B"+(i-TAM_ALUNOS+1)+": ");
            for(int j = 0; j < TAM_ALUNOS; j++)
            {
                System.out.print("A"+(alunos[i][j]+1)+" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void initPopulacao()
    {
        populacao = new int[TAM_POPULACAO][TAM_ALUNOS+1];
        intermediaria = new int[TAM_POPULACAO][TAM_ALUNOS+1];
        Set<Integer> disponivel;

        for(int i = 0; i < TAM_POPULACAO; i++)
        {
            //Populacao = [i]
            disponivel = getDisponiveis();
            for(int j = 0; j < TAM_ALUNOS; j++)
            {
                //{ A = [j], B = [i][j] }
                boolean contains = false;
                while(!contains)
                {
                    int aluno2 = rand.nextInt(TAM_ALUNOS);
                    contains = disponivel.contains(aluno2);
                    if (contains)
                    {
                        populacao[i][j] = aluno2;
                        disponivel.remove(aluno2);
                    }
                }
            }
        }
    }

    private static Set<Integer> getDisponiveis()
    {
        Set<Integer> disponivel = new HashSet<>(TAM_ALUNOS);
        for(int i = 0; i < TAM_ALUNOS; i++)
            disponivel.add(i);
        return disponivel;
    }

    public static void calculaAptidao()
    {
        for (int i = 0; i < TAM_POPULACAO; i++)
                populacao[i][TAM_ALUNOS] = calculaAptidaoCromossomo(i);
    }

    public static int calculaAptidaoCromossomo(int cromossomo)
    {  
        //Populacao = [cromossomo]
        //{ A = [j], B = [i][j] }
        int aptidao = 0;        
        for(int j = 0; j < TAM_ALUNOS; j++)
        {
            int alunoA = j;
            int alunoB = populacao[cromossomo][j];
            aptidao += getPesoSala(alunoA, alunoB);
        }
        return aptidao;
    }

    private static int getPesoSala(int alunoA, int alunoB)
    {
        // TAM_ALUNOS = 2
        // [0] A0
        // [1] A1
        // [2] B0
        // [3] B1

        int pesoA = getPesoColegas(alunoA, alunoB+TAM_ALUNOS);
        int pesoB = getPesoColegas(alunoB+TAM_ALUNOS, alunoA);
        //System.out.println("Aluno 1: "+alunoA+", Aluno 2: "+alunoB+" | Peso 1: "+pesoA+"; Peso 2: "+pesoB+" | Resultado: "+ (pesoA+pesoB));
        return pesoA + pesoB;       
    }

    private static int getPesoColegas(int aluno1, int aluno2)
    {
        int peso = 0;
        for(int j = 0; j < TAM_ALUNOS; j++)
            if (alunos[aluno1][j] == aluno2)
            {
                peso = j;
                break;
            }   
        return peso;
    }

    public static void printPopulacao()
    {
        int j;
        System.out.println("Populacao:");
        for(int i = 0; i < TAM_POPULACAO; i++)
        {
            System.out.print("C"+(i+1)+": ");
            for(j = 0; j < TAM_ALUNOS; j++)
            {
                System.out.print("[A"+(j+1)+",B"+(populacao[i][j]+1)+"] ");
            }
            System.out.println("F.A.: "+populacao[i][j]);
        }
    }

    public static int getMelhor()
    {
        int melhorCromossomo = Integer.MAX_VALUE;
        int melhorAptidao = Integer.MAX_VALUE;
        for(int i = 0; i < TAM_POPULACAO; i++)
        {
            int aptidao = populacao[i][TAM_ALUNOS];
            if (aptidao < melhorAptidao)
            {
                melhorAptidao = aptidao;
                melhorCromossomo = i;
            }   
        }
        System.out.println("Melhor cromossomo: ["+melhorAptidao+"] Aptidao: "+melhorAptidao);
        for(int i = 0; i < TAM_ALUNOS; i++)
            intermediaria[0][i] = populacao[melhorCromossomo][i];
        return melhorCromossomo;
    }

    public static int torneio(){
        int cromossomoA = rand.nextInt(TAM_POPULACAO);
        int cromossomoB = rand.nextInt(TAM_POPULACAO);        
        
        //populacao[Cromossomo][alunoA] = [alunoB]
        if(populacao[cromossomoA][TAM_ALUNOS] < populacao[cromossomoB][TAM_ALUNOS])
            return cromossomoA;
        else
            return cromossomoB;
    }

    public static void crossover()
    {         
        for (int j=1; j<TAM_POPULACAO; j=j+2)
        {
            int ind1 = torneio();
            int ind2 = torneio();
            for (int k=0; k<TAM_ALUNOS/2; k++)
            {
                intermediaria [j][k]= populacao [ind1][k];
                intermediaria [j+1][k]= populacao [ind2][k];
            }
            for (int k=TAM_ALUNOS/2; k<TAM_ALUNOS; k++)
            {
                intermediaria [j][k]= populacao [ind2][k];
                intermediaria [j+1][k]= populacao [ind1][k];
            }
        }
    }

    public static void mutacao(){
        int quant = rand.nextInt(3)+1;
        for(int i = 0; i<quant; i++){
            int cromossomo = rand.nextInt(TAM_POPULACAO);
            int quarto1 = rand.nextInt(TAM_ALUNOS);
            int quarto2 = rand.nextInt(TAM_ALUNOS);
        
            System.out.println("Cromossomo " + (cromossomo+1) + " sofreu MUTAÇÃO nos quartos " + (quarto1+1) + " e " + (quarto2+1));
            int alunoB1 = populacao[cromossomo][quarto1];
            int alunoB2 = populacao[cromossomo][quarto2];
            int aux = alunoB1;
            populacao[cromossomo][quarto1] = alunoB2;
            populacao[cromossomo][quarto2] = aux;
              
        }
        
    }
      
}
