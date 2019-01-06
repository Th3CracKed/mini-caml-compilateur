package frontend;

import arbremincaml.*;
import java.util.HashMap;
import java.util.List;
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
      Var ancienRenommage = valeursVariable.get(idString);
      if(e1 instanceof Var)
      {
          valeursVariable.put(idString, (Var)e1);
      }
      Exp e2 = e.getE2().accept(this);
      valeursVariable.put(idString, ancienRenommage);
      return new Let(id, Type.gen(), e1 , e2);
    }
    
    @Override
    public LetTuple visit(LetTuple e) {
      Exp e1 = e.getE1().accept(this);
      List<Id> ids = e.getIds();
      HashMap<String, Var> anciensRenommages = new HashMap<>();
      if(e1 instanceof Tuple)
      {          
          for(int i = 0 ; i < ids.size(); i++)
          {
                Exp composante = ((Tuple) e1).getEs().get(i);
                if(composante instanceof Var)
                {
                    String idString = ids.get(i).getIdString();
                    anciensRenommages.put(idString, valeursVariable.get(idString));
                    valeursVariable.put(idString, (Var)composante);
                }
          }
      }
      Exp e2 = e.getE2().accept(this);
      valeursVariable.putAll(anciensRenommages);
      return new LetTuple(ids, e.getTs(), e1 , e2);
    }

    @Override
    public Exp visit(Var e){
        Exp valeurVariable = valeursVariable.get(e.getId().getIdString());
        return (valeurVariable == null)?e:valeurVariable;
    }
}
