package util;

import java.io.PrintStream;

public abstract class GenerateurDeCode {    
    
    protected static final int NB_ESPACES_PAR_TABULATION = 4;
    private final PrintStream fichierSortie;
    private int niveauIndentation;
    private boolean indentationActivee;
    
    public GenerateurDeCode(PrintStream fichierSortie)
    {
        this.fichierSortie = fichierSortie;
        niveauIndentation = 0;
        setIndentationActivee(true);
    }   
    
    protected void augmenterNiveauIndentation()
    {
        niveauIndentation++;
    }
    
    protected void diminuerNiveauIndentation()
    {
        niveauIndentation--;
    }
    
    protected final void setIndentationActivee(boolean indentationActivee)
    {
        this.indentationActivee = indentationActivee;
    }  
    
    protected String indentation()
    {
        String indent = "";
        for(int i = 1 ; i <= niveauIndentation*NB_ESPACES_PAR_TABULATION && indentationActivee ; i++)
        {
            indent += " ";
        }
        return indent;
    }
    
    protected void ecrire(Object object)
    {        
        fichierSortie.print(object);
    }
    
    protected void ecrireAvecIndentation(Object object)
    {
        ecrire(indentation()+object);
    }
}
