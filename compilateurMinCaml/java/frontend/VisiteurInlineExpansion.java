package frontend;

import arbremincaml.Var;
import arbremincaml.Exp;
import arbremincaml.App;
import util.Constantes;
import util.NotYetImplementedException;
import visiteur.ObjVisitorExp;

public class VisiteurInlineExpansion extends ObjVisitorExp {

    @Override
    public Exp visit(App e)
    {
        if(!Constantes.FONCTION_EXTERNES_MINCAML.contains(((Var)e.getE()).getId().getIdString()))
        {
            throw new NotYetImplementedException(); // la fonctionnalité n'est pas implementee pour les fonctions non externes
        }
        return super.visit(e);
    }
}
