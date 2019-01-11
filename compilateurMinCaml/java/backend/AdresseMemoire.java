package backend;

/**
 * Classe représentant une adresse mémoire (contenant un décalage par rapport à une adresse de base, qui est souvent la valeur du registre FP ou SP). Elle est utilisée
 * par l'allocation de registre et la génération de code ARM
 */
public class AdresseMemoire extends EmplacementMemoire {
    
    private final int decalage;
    
    /**
     * Créé une instance de AdresseMémoire représentant l'adresse mémoire de decalage decalage par rapport à l'adresse de base
     * @param decalage le décalage de l'adresse par rapport à l'adresse de base
     */
    public AdresseMemoire(int decalage)
    {
        this.decalage = decalage;
    }
    
    /**
     * Renvoie la représentation de l'adresse mémoire sous forme de chaîne
     * @return la représentation de l'adresse mémoire sous forme de chaîne
     */
    @Override
    public String toString()
    {
        return "[FP"+((this.getDecalage() == 0)?"":", #"+this.getDecalage())+"]";
    }

    /**
     * Renvoie le décalage de l'adresse par rapport à l'adresse de base
     * @return le décalage de l'adresse par rapport à l'adresse de base
     */
    public int getDecalage() {
        return decalage;
    }
}
