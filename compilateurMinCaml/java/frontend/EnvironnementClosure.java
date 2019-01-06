package frontend;

import java.util.HashSet;
import java.util.List;

public class EnvironnementClosure
{
    private final List<String> variablesLibres;
    private final HashSet<String> fonctionsAppelees;
    
    public EnvironnementClosure(List<String> variablesLibres, HashSet<String> fonctionsAppelees)
    {
        this.variablesLibres = variablesLibres;
        this.fonctionsAppelees = fonctionsAppelees;
    }

    public List<String> getVariablesLibres()
    {
        return variablesLibres;
    }
    
    public HashSet<String> getFonctionsAppelees() {
        return fonctionsAppelees;
    }
}
