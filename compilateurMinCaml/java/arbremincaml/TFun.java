package arbremincaml;

public class TFun extends Type {

    private Type t1;
    private Type t2;

    public TFun(Type t1, Type t2) {
        setT1(t1);
        setT2(t2);
    }

    public Type getT1() {
        return t1;
    }

    public final void setT1(Type t1) {
        this.t1 = t1;
    }

    public Type getT2() {
        return t2;
    }

    public final void setT2(Type t2) {
        this.t2 = t2;
    }

    @Override
    public String toString() {
        return t1 + " -> " + t2;
    }

}
