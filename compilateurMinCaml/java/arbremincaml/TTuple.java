package arbremincaml;

import java.util.List;

public class TTuple extends Type {
    private final List<Type> ts;
    
    public TTuple(List<Type> ts)
    {
        this.ts = ts;
    }
    
    public List<Type> getTs()
    {
        return ts;
    }
}
