let rec f x = 
  let (a, b) = x in print_int ((let (c, d) = a in c + d)+(let (e, f) = b in e + f))
in
  let r = f ((1, 2), (3, 5)) in ()

