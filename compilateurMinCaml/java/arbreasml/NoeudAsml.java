package arbreasml;

import visiteur.*;

public interface NoeudAsml
{
    void accept(VisiteurAsml v);

    <E> E accept(ObjVisiteurAsml<E> v);
}
