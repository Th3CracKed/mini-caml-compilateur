package frontend;

import arbremincaml.Id;
import arbremincaml.Exp;
import arbremincaml.Let;
import arbremincaml.*;
import arbremincaml.Type;
import java.util.List;
import visiteur.ObjVisitorExp;

/**
 * Visiteur réalisant la réduction des instructions let imbriquées
 */
public class VisiteurLetImbriques extends ObjVisitorExp {
    
    /**
     * Renvoie un noeud let ayant la même sémantique que let id.getIdString() = e1 in e2 tel que e1 ne soit pas une instance de let, de letrec ou de lettuple (ni dans le noeud ni dans ses fils 
     * qui sont des instances de let)
     * @param id l'identifiant du noeud let à créer
     * @param e1 l'expression à affecter à la variable déclarée
     * @param e2 l'expression à droite du mot clé 
     * @return un noeud let ayant la même sémantique que let id.getIdString() = e1 in e2 tel que e1 ne soit pas une instance de let (ni dans le noeud ni dans ses fils 
     * qui sont des instances de let)
     */
    private static Exp insererLet(Id id, Exp e1, Exp e2)
    {
        if(e1 instanceof Let)
        {
            Let e1Let = (Let)e1;
            return new Let(e1Let.getId(), Type.gen(), e1Let.getE1(), insererLet(id, e1Let.getE2(), e2));
        }
        else if(e1 instanceof LetRec)
        {
            LetRec e1LetRec = (LetRec)e1;
            FunDef funDef = e1LetRec.getFd();
            return new LetRec(new FunDef(funDef.getId(), funDef.getType(), funDef.getArgs(), funDef.getE()), insererLet(id, e1LetRec.getE(), e2));
        }
        else if(e1 instanceof LetTuple)
        {
            LetTuple e1LetTuple = (LetTuple)e1;
            return new LetTuple(e1LetTuple.getIds(), e1LetTuple.getTs(), e1LetTuple.getE1(), insererLet(id, e1LetTuple.getE2(), e2));
        }
        else
        {
            return new Let(id, Type.gen(), e1, e2);
        }
    }      
    
    /**
     * Renvoie un noeud let ayant la même sémantique que let a = e1 in e2 (où a est le tuple des variables déclarées) tel que e1 ne soit pas une instance de let, de letrec ou de lettuple (ni dans le noeud ni dans ses fils 
     * qui sont des instances de let)
     * @param id l'identifiant du noeud let à créer
     * @param e1 l'expression à affecter à la variable déclarée
     * @param e2 l'expression à droite du mot clé 
     * @return un noeud let ayant la même sémantique que let id.getIdString() = e1 in e2 tel que e1 ne soit pas une instance de let (ni dans le noeud ni dans ses fils 
     * qui sont des instances de let)
     */
    private static Exp insererLetTuple(List<Id> ids, List<Type> ts, Exp e1, Exp e2)
    {
        if(e1 instanceof Let)
        {
            Let e1Let = (Let)e1;
            return new Let(e1Let.getId(), e1Let.getT(), e1Let.getE1(), insererLetTuple(ids, ts, e1Let.getE2(), e2));
        }
        else if(e1 instanceof LetRec)
        {
            LetRec e1LetRec = (LetRec)e1;
            FunDef funDef = e1LetRec.getFd();
            return new LetRec(new FunDef(funDef.getId(), funDef.getType(), funDef.getArgs(), funDef.getE()), insererLetTuple(ids, ts, e1LetRec.getE(), e2));
        }
        else if(e1 instanceof LetTuple)
        {
            LetTuple e1LetTuple = (LetTuple)e1;
            return new LetTuple(e1LetTuple.getIds(), e1LetTuple.getTs(), e1LetTuple.getE1(), insererLetTuple(ids, ts, e1LetTuple.getE2(), e2));
        }
        else
        {
            return new LetTuple(ids, ts, e1, e2);
        }
    }
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, appelle la méthode insererLet avec comme paramètres l'identifiant de e,
     * et le résultats de l'application du visiteur à e1 et e2
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    
    @Override
    public Exp visit(Let e) {
        Exp e1 = e.getE1().accept(this);
        Exp e2 = e.getE2().accept(this);
        return insererLet(e.getId(), e1, e2); 
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, appelle la méthode insererLetTuple avec comme paramètres 
     * les identifiants des variables déclarées, les types des variables déclarée et le résultats de l'application du visiteur à e1 et e2
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(LetTuple e) {
        Exp e1 = e.getE1().accept(this);
        Exp e2 = e.getE2().accept(this);
        return insererLetTuple(e.getIds(), e.getTs(), e1, e2); 
    }

}
