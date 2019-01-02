.text
.global _start
_min_caml_create_array:
    LSL R0, R0, #2          @ multiplier r0 par 4 pour que r0 ait pour valeur le nombre d'octet a allouer
    MOV R2, R0            @ met la taille restante a initialiser dans r2 
    LDR R3, =debut_zone_dynamique            @ charge l'adresse du pointeur sur le debut de la prochaine zone a allouer dans r3 
    LDR R3, [R3]            @ charge le pointeur sur le debut de la prochaine zone a allouer dans r3 
create_array_boucle:
    STR R1, [R3]            @ initialise le prochain mot memoire avec la valeur du deuxieme parametre de la fonction
    SUB R2, R2, #4            @ decremente la taille restante a initialiser de 4
    ADD R3, R3, #4            @ stocke l'adresse du prochain mot memoire a initialiser dans r3 
    CMP R2, #0            @ compare la taille restante a initialiser a 0
    BGT create_array_boucle            @ si la taille restante a initialiser est strictement positive, aller a create_array_boucle
_allouer_memoire:
    MOV R2, R0            @ met la taille restante a allouer dans r2 
    LDR R0, =debut_zone_dynamique            @ charge l'adresse du pointeur sur le debut de la prochaine zone a allouer dans r0 
    LDR R0, [R0]            @ charge le pointeur sur le debut de la prochaine zone a allouer dans r0 
    ADD R3, R0, R2          @ stocke la nouvelle valeur du pointeur sur le debut de la prochaine zone a allouer dans r3 (son ancienne valeur a laquelle on ajoute la taille allouee)
    LDR R2, =debut_zone_dynamique            @ charge l'adresse du pointeur sur le debut de la prochaine zone a allouer dans r2
    STR R3, [R2]            @ecrit la nouvelle valeur du pointeur sur le debut de la prochaine zone a allouer
    BX LR            @aller a l'adresse dans lr (instruction return)

_f58:
        PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
        SUB FP, SP, #4
        SUB SP, SP, #8
        LDR R4, [R0, #4]
        STR R4, [FP, #0]
        PUSH {R0, R1, R2, R3, R12, LR}
        LDR R0, [SP, #4]
        BL _f33        
        MOV R7, R0
        POP {R0, R1, R2, R3, R12, LR}
        MOV R4, R7
        STR R4, [FP, #-4]
        PUSH {R0, R1, R2, R3, R12, LR}
        LDR R4, [FP, #0]
        MOV R0, R4
        LDR R4, [FP, #-4]
        MOV R1, R4
        LDR R4, [FP, #0]
        LDR R4, [R4]
        MOV LR, PC
        BX R4        
        MOV R7, R0
        POP {R0, R1, R2, R3, R12, LR}
        MOV R0, R7
        ADD SP, SP, #8
        POP {R4, R5, R6, R7, R8, R9, R10, FP}
        BX LR

_f52:
        PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
        SUB FP, SP, #4
        SUB SP, SP, #4
        PUSH {R0, R1, R2, R3, R12, LR}
        LDR R0, [SP, #4]
        BL _f29        
        MOV R7, R0
        POP {R0, R1, R2, R3, R12, LR}
        MOV R4, R7
        STR R4, [FP, #0]
        PUSH {R0, R1, R2, R3, R12, LR}
        LDR R4, [FP, #0]
        MOV R0, R4
        BL _f37        
        MOV R7, R0
        POP {R0, R1, R2, R3, R12, LR}
        MOV R0, R7
        ADD SP, SP, #4
        POP {R4, R5, R6, R7, R8, R9, R10, FP}
        BX LR

_f37:
        PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
        SUB FP, SP, #4
        SUB SP, SP, #4
        LDR R4, =1
        STR R4, [FP, #0]
        LDR R5, [FP, #0]
        SUB R0, R0, R5
        ADD SP, SP, #4
        POP {R4, R5, R6, R7, R8, R9, R10, FP}
        BX LR

_f33:
        PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
        SUB FP, SP, #4
        SUB SP, SP, #4
        LDR R4, =1
        STR R4, [FP, #0]
        LDR R5, [FP, #0]
        ADD R0, R0, R5
        ADD SP, SP, #4
        POP {R4, R5, R6, R7, R8, R9, R10, FP}
        BX LR

_f29:
        PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
        SUB FP, SP, #4
        ADD R0, R0, R0
        POP {R4, R5, R6, R7, R8, R9, R10, FP}
        BX LR

_start:
        PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
        SUB FP, SP, #4
        SUB SP, SP, #44
        PUSH {R0, R1, R2, R3, R12, LR}
        LDR R0, =4
        BL _allouer_memoire        
        MOV R7, R0
        POP {R0, R1, R2, R3, R12, LR}
        MOV R4, R7
        STR R4, [FP, #0]
        LDR R4, =_f52
        MOV R4, R4
        STR R4, [FP, #-4]
        LDR R4, [FP, #0]
        LDR R5, [FP, #-4]
        STR R5, [R4, #0]
        LDR R4, =0
        STR R4, [FP, #-8]
        LDR R4, [FP, #0]
        MOV R4, R4
        STR R4, [FP, #0]
        PUSH {R0, R1, R2, R3, R12, LR}
        LDR R0, =8
        BL _allouer_memoire        
        MOV R7, R0
        POP {R0, R1, R2, R3, R12, LR}
        MOV R4, R7
        STR R4, [FP, #-16]
        LDR R4, =_f58
        MOV R4, R4
        STR R4, [FP, #-20]
        LDR R4, [FP, #-16]
        LDR R5, [FP, #-20]
        STR R5, [R4, #0]
        LDR R4, =0
        STR R4, [FP, #-24]
        LDR R4, [FP, #-16]
        LDR R5, [FP, #0]
        STR R5, [R4, #4]
        LDR R4, =0
        STR R4, [FP, #-28]
        LDR R4, [FP, #-16]
        MOV R4, R4
        STR R4, [FP, #-32]
        LDR R4, =123
        STR R4, [FP, #-36]
        PUSH {R0, R1, R2, R3, R12, LR}
        LDR R4, [FP, #-32]
        MOV R0, R4
        LDR R4, [FP, #-36]
        MOV R1, R4
        LDR R4, [FP, #-32]
        LDR R4, [R4]
        MOV LR, PC
        BX R4        
        MOV R7, R0
        POP {R0, R1, R2, R3, R12, LR}
        MOV R4, R7
        STR R4, [FP, #-40]
        PUSH {R0, R1, R2, R3, R12, LR}
        LDR R4, [FP, #-40]
        MOV R0, R4
        BL min_caml_print_int        
        MOV R7, R0
        POP {R0, R1, R2, R3, R12, LR}
        MOV R0, R7
        ADD SP, SP, #44
        POP {R4, R5, R6, R7, R8, R9, R10, FP}
        B min_caml_exit

.data
.balign 4
zone_dynamique: .skip 400000
debut_zone_dynamique: .word zone_dynamique
