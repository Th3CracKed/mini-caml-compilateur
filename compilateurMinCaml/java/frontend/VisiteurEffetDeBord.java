package frontend;

import arbremincaml.*;
import visiteur.Visitor;

/**
 * Visiteur déterminant si une expression à un effet de bord (on considére que tout appel de fonction ou écriture dans un tableaua un effet de bord)
 */
public class VisiteurEffetDeBord implements Visitor {

        private boolean aUnEffetDeBord;

        /**
         * Créé un visiteur déterminant si une expression à un effet de bord
         */
        public VisiteurEffetDeBord() {
            setAUnEffetDeBord(false);
        }

        /**
         * Renvoie vrai si l'expression a un effet de bord et faux sinon
         * @return vrai si l'expression a un effet de bord et faux sinon
         */
        public boolean getAUnEffetDeBord() {
            return aUnEffetDeBord;
        }

        /**
         * Définit si l'expression a un effet de bord
         * @param aUnEffetDeBord le booléeen définissant si l'expression a un effet de bord
         */
        private void setAUnEffetDeBord(boolean aUnEffetDeBord) {
            this.aUnEffetDeBord = aUnEffetDeBord;
        }

        /**
        * Visite le noeud e. Dans ce cas, indique que l'expression a un effet de bord
        * @param e le noeud à visiter
        */
        @Override
        public void visit(App e) {
            setAUnEffetDeBord(true);
        }
        
        /**
        * Visite le noeud e. Dans ce cas, visite uniquement l'expression après le mot clé in du noeud LetRec car la creation d'une fonction n'a jamais d'effet de bord 
        * (c'est son appel qui peut en avoir un)
        * @param e le noeud à visiter
        */
        @Override
        public void visit(LetRec e) {
            e.getE().accept(this);
        }

        /**
        * Visite le noeud e. Dans ce cas, indique que l'expression a un effet de bord
        * @param e le noeud à visiter
        */
        @Override
        public void visit(Put e) {
            setAUnEffetDeBord(true);
        }
    }