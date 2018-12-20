package arbremincaml;

public abstract class Type {
    private static int x = 0;
    public static Type gen() {
        return new TVar("?" + x++);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName(); 
    }
}

