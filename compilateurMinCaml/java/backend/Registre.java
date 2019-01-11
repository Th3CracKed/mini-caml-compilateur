package backend;

/**
 * Classe représentant un registre utilisée par l'allocation de registre et la génération de code ARM
 */
public class Registre extends EmplacementMemoire {
    private final int numeroRegistre;
    
    /**
     * Créé une instance de Registre représentant le registre de numéro numeroRegistre (RnuméroRegistre)
     * @param numeroRegistre le numéro du registre
     */
    public Registre(int numeroRegistre)
    {
        this.numeroRegistre = numeroRegistre;
    }
    
    /**
     * Renvoie la représentation du registre sous forme de chaîne
     * @return la représentation du registre sous forme de chaîne
     */
    @Override
    public String toString()
    {
        return "R"+this.getNumeroRegistre();
    }

    /**
     * Renvoie le numéro du registre
     * @return le numéro du registre
     */
    public int getNumeroRegistre() {
        return numeroRegistre;
    }
}
