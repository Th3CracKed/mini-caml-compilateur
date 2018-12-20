package arbreasml;

public abstract class MemAsml implements ExpAsml {
    private final VarAsml tableau;
    private final VarOuIntAsml indice;

    public MemAsml(VarAsml tableau, VarOuIntAsml indice) {
        this.tableau = tableau;
        this.indice = indice;
    }   
    
    public VarAsml getTableau() {
        return tableau;
    }

    public VarOuIntAsml getIndice() {
        return indice;
    }
    
}
