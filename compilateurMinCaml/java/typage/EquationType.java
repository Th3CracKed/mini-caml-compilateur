package typage;

import arbremincaml.Type;

public class EquationType {

    private Type t1;
    private Type t2;
    
    public EquationType(Type t1, Type t2) {
        this.setT1(t1);
        this.setT2(t2);
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

    public void echange() {
        Type temp = t1;
        setT1(t2);
        setT2(temp);
    }
    
    @Override
    public String toString()
    {
        return t1+" = "+t2;
    }
}
