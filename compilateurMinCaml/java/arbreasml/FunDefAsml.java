package arbreasml;

import java.util.List;

public abstract class FunDefAsml implements NoeudAsml {
    private final String label;
    private final List<VarAsml> arguments;

    public FunDefAsml(String label, List<VarAsml> arguments)
    {
        this.label = label;
        this.arguments = arguments;
    }
    
    public String getLabel() {
        return label;
    }
        
    public List<VarAsml> getArguments() {
        return arguments;
    }
    
}
