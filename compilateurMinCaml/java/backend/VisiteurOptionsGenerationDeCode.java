package backend;

import arbreasml.*;
import java.util.Arrays;
import java.util.List;
import util.Constantes;
import visiteur.VisiteurAsml;

/**
 * Visiteur créant une instance de OptionsGenerationCodeArm contenant les options adaptées pour la généréation de code ARM
 */
public class VisiteurOptionsGenerationDeCode implements VisiteurAsml {

    private final OptionsGenerationCodeArm optionsGenCodeArm;

    /**
     * Créé un visiteur créant une instance de OptionsGenerationCodeArm contenant les options adaptées pour la généréation de code ARM
     */
    public VisiteurOptionsGenerationDeCode() {
        this.optionsGenCodeArm = new OptionsGenerationCodeArm();
    }

    /**
     * Renvoie une instance de OptionsGenerationCodeArm contenant les options adaptées pour la généréation de code ARM
     * @return une instance de OptionsGenerationCodeArm contenant les options adaptées pour la généréation de code ARM
     */
    public OptionsGenerationCodeArm getOptionsGenCodeArm() {
        return optionsGenCodeArm;
    }

    /**
     * Visite le noeud e. Dans ce cas, modifie les options de génération de code pour que les fonction d'allocation de mémoire et de création de tableau soit incluses 
     * dans le fichier ARM généré
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(NewAsml e) {
        optionsGenCodeArm.setUtiliseNewOuCreateArray(true);
    }

    /**
     * Visite le noeud e. Dans ce cas, si la fonction appelée est sinus ou cosinus, modifie les options de génération de code pour que les fonction d'allocation de mémoire
     * et de création de tableau soit incluses dans le fichier ARM généré. Si la fonction appelée est celle pour créer un tableau d'entier ou de flottant, modifie les options
     * de génération de code pour que les fonction d'allocation de mémoire et de création de tableau soit incluses dans le fichier ARM généré
     * @param e le noeud à visiter 
     */
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
