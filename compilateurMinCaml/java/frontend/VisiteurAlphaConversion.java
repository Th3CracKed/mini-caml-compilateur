package frontend;

import arbreasml.FunDefConcreteAsml;
import arbremincaml.Var;
import arbremincaml.Id;
import arbremincaml.Let;
import java.util.HashMap;
import util.Constantes;
import visiteur.Visitor;

public class VisiteurAlphaConversion implements Visitor {

    private final HashMap<String, String> idsVariable;
    
    public VisiteurAlphaConversion()
    {
        idsVariable = new HashMap<>();
        for(String nomFonction : Constantes.FONCTION_EXTERNES_MINCAML)
        {
            idsVariable.put(nomFonction, nomFonction);
        }
        for(String nomFonction : Constantes.FONCTION_EXTERNES_ASML)
        {
            idsVariable.put(nomFonction, nomFonction);
        }
        idsVariable.put(FunDefConcreteAsml.NOM_FONCTION_MAIN, FunDefConcreteAsml.NOM_FONCTION_MAIN);
    }
    
    @Override
    public void visit(Let e) {      
      e.getE1().accept(this);
      Id id = e.getId();
      String ancienIdString = id.getIdString();
      String nouvelIdString = ancienIdString;
      if(idsVariable.containsKey(ancienIdString))
      {
          nouvelIdString = Id.genIdString();
      }
      id.setIdString(nouvelIdString);
      idsVariable.put(ancienIdString, nouvelIdString);
      e.getE2().accept(this);
      idsVariable.remove(ancienIdString);
    }

    @Override
    public void visit(Var e){
        e.getId().setIdString(idsVariable.get(e.getId().getIdString()));
    }  
}


