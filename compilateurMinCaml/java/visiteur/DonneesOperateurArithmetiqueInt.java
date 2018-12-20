package visiteur;

import arbreasml.*;

public class DonneesOperateurArithmetiqueInt<E> extends DonneesOperateurArithmetiqueAsml<E> {        
    public DonneesOperateurArithmetiqueInt(OperateurArithmetiqueIntAsml e, ObjVisiteurAsml<E> visiteur)
    {
        super(e, visiteur);
        setE2(e.getE2().accept(visiteur));
    }
}
