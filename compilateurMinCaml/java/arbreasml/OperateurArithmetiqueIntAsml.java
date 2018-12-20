package arbreasml;

public abstract class OperateurArithmetiqueIntAsml extends OperateurArithmetiqueAsml{
    private final VarOuIntAsml e2;

    public OperateurArithmetiqueIntAsml(VarAsml e1, VarOuIntAsml e2) {
        super(e1);
        this.e2 = e2;
    }

    public VarOuIntAsml getE2() {
        return e2;
    }  
}
