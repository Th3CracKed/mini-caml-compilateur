package arbremincaml;

public abstract class OperateurBinaire extends Exp {
    private final Exp e1;
    private final Exp e2;

    public OperateurBinaire(Exp e1, Exp e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    public Exp getE1() {
        return e1;
    }

    public Exp getE2() {
        return e2;
    }    
}
