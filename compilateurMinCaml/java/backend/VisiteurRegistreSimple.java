package backend;

import arbreasml.*;
import java.util.HashMap;
import util.CompilationException;
import util.Constantes;
import visiteur.*;

public class VisiteurRegistreSimple implements VisiteurAsml {
    private int indiceRegistreVarSuivant;
    private final HashMap<String, EmplacementMemoire> emplacementsVar;
    
    public VisiteurRegistreSimple()
    {
        emplacementsVar = new HashMap<>();
        indiceRegistreVarSuivant = 0;
    }
    
    public HashMap<String, EmplacementMemoire> getEmplacementsVar()
    {
        return emplacementsVar;
    }

    @Override
    public void visit(LetAsml e) {
        Registre registre = new Registre(registreVarSuivant());
        emplacementsVar.put(e.getIdString(), registre);        
        e.getE1().accept(this);
        e.getE2().accept(this);
    }
    
    private int registreVarSuivant(){
        if(indiceRegistreVarSuivant == Constantes.REGISTRES_VAR_LOCALES[Constantes.REGISTRES_VAR_LOCALES.length-1])
        {
            throw new CompilationException("Les programmes avec plus de 8 variables locales ne sont pas supportés");
        }
        int registre = Constantes.REGISTRES_VAR_LOCALES[indiceRegistreVarSuivant];
        indiceRegistreVarSuivant++;
        return registre;
    }

}
