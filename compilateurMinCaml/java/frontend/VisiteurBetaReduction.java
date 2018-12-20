package frontend;

import arbremincaml.Id;
import arbremincaml.Var;
import arbremincaml.Exp;
import arbremincaml.Let;
import java.util.HashMap;
import visiteur.ObjVisitorExp;

public class VisiteurBetaReduction extends ObjVisitorExp {
    
    private final HashMap<String, Var> valeursVariable;
    
    public VisiteurBetaReduction()
    {
        valeursVariable = new HashMap<>();
    }

    @Override
    public Let visit(Let e) {
      Exp e1 = e.getE1().accept(this);
      Id id = e.getId();
      String idString = id.getIdString();
      if(e1 instanceof Var)
      {
          valeursVariable.put(idString, (Var)e1);
      }
      Exp e2 = e.getE2().accept(this);
      valeursVariable.remove(idString); // la methode remove de la classe HashMap supprime l'element s'il est present et ne fait rien sinon
      return new Let(id, e.getT(), e1 , e2);
    }

    @Override
    public Exp visit(Var e){
        Exp valeurVariable = valeursVariable.get(e.getId().getIdString());
        return (valeurVariable == null)?e:valeurVariable;
    }
}
