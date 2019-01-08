let tab1 = Array.create 2 14 in
let tab2 = Array.create 2 21 in
let tab3 = Array.create 2 (Array.create 2 38) in
tab3.(0) <- tab1;
tab3.(1) <- tab2;
(print_int (tab3.(0).(0)-tab3.(1).(0)))
