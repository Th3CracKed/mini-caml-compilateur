package visiteur;

import arbremincaml.OperateurBinaire;

/**
 * Classe permettant de factoriser le code des opérateur binaire en MinCaml (add, sub, fadd, fsub, fmul, fdiv, eq et le).
 * @param <E> le type des valeurs renvoyées par le visiteur passé en paramètre au constructeur de cette classe
 */
public class DonneesOperateurBinaire<E>
{
    private E e1;
    private E e2;
        
    /**
     * Créé une instance de DonneesOperateurBinaire contenant dans ses attributs e1 et e2 respectivement le résultat de l'application du visiteur visiteur 
     * sur le premier et le second opérande de l'opérateur
     * @param e
     * @param visiteur 
     */
    public DonneesOperateurBinaire(OperateurBinaire e, ObjVisitor<E> visiteur)
    {
        setE1(e.getE1().accept(visiteur));
        setE2(e.getE2().accept(visiteur));
    }

    /**
     * Renvoie le résultat de l'application du visiteur visiteur sur le premier opérande de l'opérateur
     * @return le résultat de l'application du visiteur visiteur sur le premier opérande de l'opérateur
     */
    public E getE1() {
        return e1;
    }
    
    /**
     * Définit e1 comme le résultat de l'application du visiteur visiteur sur le premier opérande de l'opérateur
     * @param e1 le résultat de l'application du visiteur visiteur sur le premier opérande de l'opérateur
     */
    private void setE1(E e1) {
        this.e1 = e1;
    }

    /**
     * Définit e2 comme le résultat de l'application du visiteur visiteur sur le second opérande de l'opérateur
     * @param e2 le résultat de l'application du visiteur visiteur sur le second opérande de l'opérateur
     */
    private void setE2(E e2) {
        this.e2 = e2;
    }
    
    /**
     * renvoie le résultat de l'application du visiteur visiteur sur le second opérande de l'opérateur
     * @return le résultat de l'application du visiteur visiteur sur le second opérande de l'opérateur
     */
    public E getE2() {
        return e2;
    }
    
    /**
     * Echange la valeur des attributs e1 et e2
     */
    public void echangerDonnees()
    {
        E temp = e1;
        setE1(e2);
        setE2(temp);
    }   
}
