package arbresyntaxique;

public class Id {
    private final String id;
    public Id(String id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return id;
    }
    
    public String getId()
    {
        return id;
    }
    
    private static int x = -1;
    public static Id gen() {
        x++;
        return new Id("?v" + x);
    }

}
