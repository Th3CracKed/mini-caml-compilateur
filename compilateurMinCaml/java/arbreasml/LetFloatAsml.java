package arbreasml;

import java.util.ArrayList;
import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class LetFloatAsml extends FunDefAsml {
    private final float valeur;
    
    public LetFloatAsml(String label, float valeur) {
        super(label, new ArrayList<>());
        this.valeur = valeur;
    }

    public float getValeur() {
        return valeur;
    }
    
    @Override
    public void accept(VisiteurAsml v) {
        v.visit(this);
    }

    @Override
    public <E> E accept(ObjVisiteurAsml<E> v) {
        return v.visit(this);
    }
}
