package arbreasml;

public abstract class IfAsml implements ExpAsml {
    private final AsmtAsml eSiVrai;
    private final AsmtAsml eSiFaux;
    private final VarAsml e1;
    
    public IfAsml(VarAsml e1, AsmtAsml eSiVrai, AsmtAsml eSiFaux)
    {
        this.e1 = e1;
        this.eSiVrai = eSiVrai;
        this.eSiFaux = eSiFaux;
    }

    public AsmtAsml getESiVrai() {
        return eSiVrai;
    }

    public AsmtAsml getESiFaux() {
        return eSiFaux;
    }
    
    public VarAsml getE1() {
        return e1;
    }
}
