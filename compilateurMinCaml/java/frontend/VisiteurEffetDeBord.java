package frontend;

import arbremincaml.*;
import util.NotYetImplementedException;
import visiteur.Visitor;

public class VisiteurEffetDeBord implements Visitor {

        private boolean aUnEffetDeBord;

        public VisiteurEffetDeBord() {
            setAUnEffetDeBord(false);
        }

        public boolean getAUnEffetDeBord() {
            return aUnEffetDeBord;
        }

        private void setAUnEffetDeBord(boolean aUnEffetDeBord) {
            this.aUnEffetDeBord = aUnEffetDeBord;
        }

        @Override
        public void visit(App e) {
            setAUnEffetDeBord(true);
        }
        
        @Override
        public void visit(LetRec e) {
            e.getE().accept(this);
        }

        @Override
        public void visit(Put e) {
            throw new NotYetImplementedException(); // setAUnEffetDeBord(true);
        }
    }