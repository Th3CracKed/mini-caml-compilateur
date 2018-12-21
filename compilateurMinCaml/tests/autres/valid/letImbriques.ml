let a =
    (let x = (let y = 1 in y + 2) in x + 3)
    in print_int a
(* doit donner l'expression suivante si on applique uniquement la reduction des let imbriques : 
let y = 1 in
let x = y + 2 in
let a = x + 3 in
() *)