let y = 2 in
  let x = 2 in
    if (x = (let z = 5 in if (y=z) then y else z )) then
      print_int x
    else
      print_int 2
