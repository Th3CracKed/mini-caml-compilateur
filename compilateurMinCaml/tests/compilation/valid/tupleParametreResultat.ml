let rec f x =
    let (a, b) = x in (a-1, b+1)
in
    let (c,d) = f (20,22)
in print_int d
