package visiteur;

import arbremincaml.OperateurBinaire;

public class DonneesOperateurBinaire<E>
{
    private E e1;
    private E e2;
        
    public DonneesOperateurBinaire(OperateurBinaire e, ObjVisitor<E> visiteur)
    {
        setE1(e.getE1().accept(visiteur));
        setE2(e.getE2().accept(visiteur));
    }

    public E getE1() {
        return e1;
    }
    
    private final void setE1(E e1) {
        this.e1 = e1;
    }

    private final void setE2(E e2) {
        this.e2 = e2;
    }
    
    public E getE2() {
        return e2;
    }
    
    public void echangerDonnees()
    {
        E temp = e1;
        setE1(e2);
        setE2(temp);
    }   
}
