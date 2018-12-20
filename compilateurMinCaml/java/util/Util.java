package util;

import java.util.HashMap;

public class Util {
    public static <K,V> HashMap<K,V> creerSingletonHashMap(K cle, V valeur) // a utiliser dans visiteurallocationregistre simple
    {
        HashMap<K,V> hashMap = new HashMap<>();
        hashMap.put(cle, valeur);
        return hashMap;
    }
    
    public static <K,V> HashMap<K,V> fusionnerHashMap(HashMap<K,V>... hashMaps) // a utiliser dans visiteurallocationregistre simple
    {
        HashMap<K,V> resultat = new HashMap<>();
        for(HashMap<K,V> hashMap : hashMaps)
        {
            resultat.putAll(hashMap);
        }        
        return resultat;
    }
}
