package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class NewAsml implements ExpAsml{
    private final VarOuIntAsml e;
    public NewAsml(VarOuIntAsml e)
    {
        this.e = e;
    }    

    public VarOuIntAsml getE() {
        return e;
    }
    
    @Override
    public void accept(VisiteurAsml v) {
        v.visit(this);
    }

    @Override
    public <E> E accept(ObjVisiteurAsml<E> v) {
        return v.visit(this);
    }
    
        /*
exp: 
| IF IDENT EQUAL ident_or_imm THEN asmt ELSE asmt  
| IF IDENT LE ident_or_imm THEN asmt ELSE asmt  
| IF IDENT GE ident_or_imm THEN asmt ELSE asmt  
| IF IDENT FEQUAL IDENT THEN asmt ELSE asmt  
| IF IDENT FLE IDENT THEN asmt ELSE asmt  
    */
}
