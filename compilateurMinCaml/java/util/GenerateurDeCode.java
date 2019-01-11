package util;

import java.io.PrintStream;

/**
 * Classe mère des visiteurs générant du code (qui n'est pas elle-même un visiteur). Cette classe permet de factoriser la gestion de l'indentation et de l'écriture dans
 * le fichier dans lequel le code doit être généré pour les visiteur générant du code (VisiteurGenererCodeAsml et VisiteurGenererCodeArm).
 */
public abstract class GenerateurDeCode {    
    
    protected static final int NB_ESPACES_PAR_TABULATION = 4;
    private final PrintStream fichierSortie;
    private int niveauIndentation;
    private boolean indentationActivee;
    
    /**
     * Créé un générateur de code qui écrira dans le fichier fichierSortie
     * @param fichierSortie le fichier dans lequel le code sera généré
     */
    public GenerateurDeCode(PrintStream fichierSortie)
    {
        this.fichierSortie = fichierSortie;
        niveauIndentation = 0;
        setIndentationActivee(true);
    }   
    
    /**
     * Incrémente le niveau d'indentation de 1
     */
    protected void augmenterNiveauIndentation()
    {
        niveauIndentation++;
    }
    
    /**
     * Décrémente le niveau d'indentation de 1
     */
    protected void diminuerNiveauIndentation()
    {
        niveauIndentation--;
    }
    
    /**
     * Active (si indentationActivee est faux) ou désactive l'indentation (si indentationActive est faux)
     * @param indentationActivee le booléen indiquant s'il faut activer ou désactiver l'indentation
     */
    protected final void setIndentationActivee(boolean indentationActivee)
    {
        this.indentationActivee = indentationActivee;
    }  
    
    /**
     * Renvoie une chaine contenant l'indentation courante c'est-à-dire niveauIndentation*NB_ESPACES_PAR_TABULATION espace
     * @return une chaine contenant l'indentation courante
     */
    protected String indentation()
    {
        String indent = "";
        for(int i = 1 ; i <= niveauIndentation*NB_ESPACES_PAR_TABULATION && indentationActivee ; i++)
        {
            indent += " ";
        }
        return indent;
    }
    
    /**
     * Ecrit la répresentation de object sous forme de chaîne dans le fichier fichierSortie
     * @param object l'objet à écrire dans le fichier fichierSortie
     */
    protected void ecrire(Object object)
    {        
        fichierSortie.print(object);
    }
    
    /**
     * Ecrit l'indentation courante (résultat de la méthode indentation) suivi la répresentation de object sous forme de chaîne dans le fichier fichierSortie
     * @param object l'objet à écrire dans le fichier fichierSortie
     */
    protected void ecrireAvecIndentation(Object object)
    {
        ecrire(indentation()+object);
    }
}
