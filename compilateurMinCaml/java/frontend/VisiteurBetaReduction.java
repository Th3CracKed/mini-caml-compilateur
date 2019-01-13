package frontend;

import arbremincaml.*;
import java.util.HashMap;
import java.util.List;
import visiteur.ObjVisitorExp;

/**
 * Visiteur réalisant la beta-reduction d'un programme MinCaml.
 */
public class VisiteurBetaReduction extends ObjVisitorExp {
    
    private final HashMap<String, Var> valeursVariable;
    
    /**
    * Créé un visiteur réalisant la beta-reduction d'un programme MinCaml.
    */
    public VisiteurBetaReduction()
    {
        valeursVariable = new HashMap<>();
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, si e1 une variable, associe l'alias e1 à la variable e avant de visiter
     * e2 puis restaure l'éventuel ancien alias qui était associé à e avant de visiter e2
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
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
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, réalise le même traitement que la méthode visit s'appliquant à 
     * des instances de Let mais en associant un alias (puis en restaurant l'ancien) si nécessaire à plusieurs variables au lieu d'une seule
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
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

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, si un alias est associé à e, renvoie cet alias et sinon renvoie e
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(Var e){
        Exp valeurVariable = valeursVariable.get(e.getId().getIdString());
        return (valeurVariable == null)?e:valeurVariable;
    }
}
