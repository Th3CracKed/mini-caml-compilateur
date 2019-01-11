package backend;

/**
 * Classe dont les instances indiquent quelles fonctions externes il faut générer (parmi le cosinus, le sinus, la fonction allouant de la mémoire et le fonction
 * créant un tableau) avec le fichier ARM généré
 */
public class OptionsGenerationCodeArm {    
    private boolean utiliseSinOuCos;
    private boolean utiliseNewOuCreateArray;

    /**
     * Créé une instance de option génération indiquant qu'il ne faut pas générer de fonctions externes avec le fichier ARM généré
     */
    public OptionsGenerationCodeArm()
    {
        setUtiliseSinOuCos(false);
        setUtiliseNewOuCreateArray(false);
    }
    
    /**
     * Renvoie vrai si il faut généré les fonction sin et cos dans le fichier ARM et faux sinon
     * @return vrai si il faut généré les fonction sin et cos dans le fichier ARM et faux sinon
     */
    public boolean getUtiliseSinOuCos() {
        return utiliseSinOuCos;
    }

    /**
     * Définit utiliseSinOuCos comme nouvelle valeur du booléen indiquant si il faut généré les fonction sin et cos dans le fichier ARM
     * @param utiliseSinOuCos la nouvelle valeur du booléen indiquant si il faut généré les fonction sin et cos dans le fichier ARM
     */
    public final void setUtiliseSinOuCos(boolean utiliseSinOuCos) {
        this.utiliseSinOuCos = utiliseSinOuCos;
    }

    /**
     * Renvoie vrai si il faut généré les fonction pour allouer de la mémoire et pour créer un tableau dans le fichier ARM et faux sinon
     * @return vrai si il faut généré les fonction pour allouer de la mémoire et pour créer un tableau dans le fichier ARM et faux sinon
     */
    public boolean getUtiliseNewOuCreateArray() {
        return utiliseNewOuCreateArray;
    }

    /**
     * Définit utiliseNewOuCreateArray comme nouvelle valeur du booléen indiquant si il faut généré les fonction pour allouer de la mémoire et pour créer un tableau dans le fichier ARM
     * @param utiliseNewOuCreateArray la nouvelle valeur du booléen indiquant si il faut généré les fonction pour allouer de la mémoire et pour créer un tableau dans le fichier ARM
     */
    public final void setUtiliseNewOuCreateArray(boolean utiliseNewOuCreateArray) {
        this.utiliseNewOuCreateArray = utiliseNewOuCreateArray;
    }
}
