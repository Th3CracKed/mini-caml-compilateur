package arbreasml;

public abstract class OperateurArithmetiqueFloatAsml extends OperateurArithmetiqueAsml{
    private final VarAsml e2;

    public OperateurArithmetiqueFloatAsml(VarAsml e1, VarAsml e2) {
        super(e1);
        this.e2 = e2;
    }

    public VarAsml getE2() {
        return e2;
    }   
}
