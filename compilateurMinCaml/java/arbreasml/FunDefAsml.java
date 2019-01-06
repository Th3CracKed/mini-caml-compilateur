package arbreasml;

import java.util.List;

public abstract class FunDefAsml implements NoeudAsml {
    private final String label;

    public FunDefAsml(String label)
    {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }    
}
