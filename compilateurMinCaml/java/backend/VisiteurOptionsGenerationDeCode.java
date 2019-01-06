package backend;

import arbreasml.*;
import frontend.OptionsGenerationCodeArm;
import java.util.Arrays;
import java.util.List;
import util.Constantes;
import visiteur.VisiteurAsml;

public class VisiteurOptionsGenerationDeCode implements VisiteurAsml {

    private final OptionsGenerationCodeArm optionsGenCodeArm;

    public VisiteurOptionsGenerationDeCode() {
        this.optionsGenCodeArm = new OptionsGenerationCodeArm();
    }

    public OptionsGenerationCodeArm getOptionsGenCodeArm() {
        return optionsGenCodeArm;
    }

    @Override
    public void visit(NewAsml e) {
        optionsGenCodeArm.setUtiliseNewOuCreateArray(true);
    }

    @Override
    public void visit(CallAsml e) {
        String nomFonction = e.getIdString();
        List<String> fonctionsSinOuCos = Arrays.asList(Constantes.SIN_ASML, Constantes.COS_ASML);
        List<String> fonctionsCreateArray = Arrays.asList(Constantes.CREATE_ARRAY_ASML, Constantes.CREATE_FLOAT_ARRAY_ASML);
        if (fonctionsSinOuCos.contains(nomFonction)) {
            optionsGenCodeArm.setUtiliseSinOuCos(true);
        } else if (fonctionsCreateArray.contains(nomFonction)) {
            optionsGenCodeArm.setUtiliseNewOuCreateArray(true);
        }
    }
}
