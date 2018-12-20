package visiteur;

import arbreasml.*;

public class DonneesOperateurArithmetiqueAsml<E> {
    private E e1;
    private E e2;    
    
    public DonneesOperateurArithmetiqueAsml(OperateurArithmetiqueAsml e, ObjVisiteurAsml<E> visiteur)
    {
       this.e1 = e.getE1().accept(visiteur);
    }

    public E getE1() {
        return e1;
    }
    
    public E getE2() {
        return e2;
    }

    private void setE1(E e1) {
        this.e1 = e1;
    }
    
    protected void setE2(E e2) {
        this.e2 = e2;
    }    

    public void echangerDonnees() {
        E temp = e1;
        setE1(e2);
        setE2(temp);
    }
}
