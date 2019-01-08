let rec f x = 
  let (a, b) = x in print_int (a.(0) + a.(1) + b.(0) + b.(1))
in
  f ((Array.create 2 42), (Array.create 2 41))
