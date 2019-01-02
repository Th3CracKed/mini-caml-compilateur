package frontend;

import java.util.List;

public class EnvironnementClosure
{
    private final List<String> variablesLibres;
    
    public EnvironnementClosure(List<String> variablesLibres)
    {
        this.variablesLibres = variablesLibres;
    }

    public List<String> getVariablesLibres()
    {
        return variablesLibres;
    }
}
