package util;

public class Couple<A, B> {
    private final A composante1;
    private final B composante2;
    
    public Couple(A composante1, B composante2)
    {
        this.composante1 = composante1;
        this.composante2 = composante2;
    }

    public A getComposante1() {
        return composante1;
    }

    public B getComposante2() {
        return composante2;
    }
}
