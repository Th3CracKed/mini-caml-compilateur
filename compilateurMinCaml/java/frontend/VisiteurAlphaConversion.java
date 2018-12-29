package frontend;

import arbremincaml.*;
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
        for(String nomFonction : Constantes.MOTS_RESERVES_ASML)
        {
            idsVariable.put(nomFonction, nomFonction);
        }
        for(String nomFonction : Constantes.FONCTION_EXTERNES_ASML)
        {
            idsVariable.put(nomFonction, nomFonction);
        }
        for(String nomFonction : Constantes.FONCTION_EXTERNES_ARM)
        {
            idsVariable.put(nomFonction, nomFonction);
        }
        idsVariable.put(Constantes.NOM_FONCTION_MAIN_ASML, Constantes.NOM_FONCTION_MAIN_ASML);
        idsVariable.put(Constantes.NOM_FONCTION_MAIN_ARM, Constantes.NOM_FONCTION_MAIN_ARM);
    }
    
    public VisiteurAlphaConversion(HashMap<String, String> renommage)
    {
        this();
        idsVariable.putAll(renommage);
    }
    
    @Override
    public void visit(Let e) {   
      Id id = e.getId();
      String ancienIdString = id.getIdString();
      String nouvelIdString = Id.genIdString();
      String ancienRenommage = idsVariable.get(ancienIdString);
      id.setIdString(nouvelIdString);
      idsVariable.put(ancienIdString, nouvelIdString);   
      e.getE1().accept(this);
      e.getE2().accept(this);
      idsVariable.put(ancienIdString, ancienRenommage);
    }
    
    @Override
    public void visit(LetRec e) {   
      FunDef funDef = e.getFd();
      Id id = funDef.getId();
      String ancienIdString = id.getIdString();
      String nouvelIdString = Id.genLabelString();
      String ancienRenommage = idsVariable.get(ancienIdString);
      id.setIdString(nouvelIdString);
      idsVariable.put(ancienIdString, nouvelIdString);   
      HashMap<String, String> anciensIdsStringArgs = new HashMap<>();
      for(Id argument : funDef.getArgs())
      {
            String ancienIdStringArg = argument.getIdString();
            String nouvelIdStringArg = Id.genIdString();
            String ancienRenommageArg = idsVariable.get(ancienIdStringArg);
            argument.setIdString(nouvelIdStringArg);
            idsVariable.put(ancienIdStringArg, nouvelIdStringArg);
            anciensIdsStringArgs.put(ancienIdStringArg, ancienRenommageArg);
      }
      funDef.getE().accept(this);         
      idsVariable.putAll(anciensIdsStringArgs);
      e.getE().accept(this);
      idsVariable.put(ancienIdString, ancienRenommage);
    }

    @Override
    public void visit(Var e){
        String nouvelId = idsVariable.get(e.getId().getIdString());
        if(nouvelId != null)
        {
            e.getId().setIdString(nouvelId);
        }
    }  
}


