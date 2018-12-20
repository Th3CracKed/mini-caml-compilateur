package arbremincaml;

public class TVar extends Type {
    private final String v;
    public TVar(String v) {
        this.v = v;
    }
    
    public String getV()
    {
        return v;
    }
    
    @Override
    public String toString() {
        return v; 
    }
}
