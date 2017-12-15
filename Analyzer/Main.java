package Analyzer;

/**
 * Lancement de tests
 * 
 * @author Valentin Bourcier
 */
public class Main
{

    public static void main(String[] args)
    {
        Command cmd = new Command(args);
        cmd.execute();
    }
}
