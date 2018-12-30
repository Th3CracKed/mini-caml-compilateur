.text
.global _start
_min_caml_create_array:
    MOV R2, R0            @ met la taille restante a initialiser dans r2 
    LDR R0, =debut_zone_dynamique            @charge l'adresse du pointeur sur le debut de la prochaine zone a allouer dans r0 
    LDR R0, [R0]            @charge le pointeur sur le debut de la prochaine zone a allouer dans r0 
    MOV R3, R0            @stocke l'adresse du prochain mot memoire a initialiser dans r3
create_array_boucle:
    STR R1, [R3]            @initialise le prochain mot memoire avec la valeur du deuxieme parametre de la fonction
    SUB R2, R2, #1            @ decremente la taille restante a initialiser
    ADD R3, R3, #4            @stocke l'adresse du prochain mot memoire a initialiser dans r3 
    CMP R2, #0            @compare la taille restante a initialiser a 0
    BGT create_array_boucle            @si la taille restante a initialiser est strictement positive, aller a create_array_boucle
    LDR R2, =debut_zone_dynamique            @charge l'adresse du pointeur sur le debut de la prochaine zone a allouer dans r2
    STR R3, [R2]            @ecrit la nouvelle valeur du pointeur sur le debut de la prochaine zone a allouer
    BX LR            @aller a l'adresse dans lr (instruction return)

_f22:
        PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
        SUB FP, SP, #4
        SUB SP, SP, #48
        CMP R1, #0
        BNE sinon78
        LDR R4, =1
        STR R4, [FP, #0]
        B finSi79
sinon78:
        LDR R4, =0
        STR R4, [FP, #0]
finSi79:
        LDR R4, =1
        STR R4, [FP, #-4]
        LDR R4, [FP, #0]
        LDR R5, [FP, #-4]
        CMP R4, R5
        BNE sinon80
        LDR R0, =0
        B finSi81
sinon80:
        SUB R4, R1, #1
        STR R4, [FP, #-8]
        LDR R6, [FP, #-8]
        LDR R4, [R0, R6, LSL #2]
        STR R4, [FP, #-12]
        SUB R4, R1, #1
        STR R4, [FP, #-16]
        LDR R4, [FP, #-16]
        CMP R4, #0
        BNE sinon82
        LDR R4, =1
        STR R4, [FP, #-20]
        B finSi83
sinon82:
        LDR R4, =0
        STR R4, [FP, #-20]
finSi83:
        LDR R4, =1
        STR R4, [FP, #-24]
        LDR R4, [FP, #-20]
        LDR R5, [FP, #-24]
        CMP R4, R5
        BNE sinon84
        LDR R4, =0
        STR R4, [FP, #-28]
        B finSi85
sinon84:
        LDR R4, [FP, #-16]
        SUB R4, R4, #1
        STR R4, [FP, #-32]
        LDR R6, [FP, #-32]
        LDR R4, [R0, R6, LSL #2]
        STR R4, [FP, #-36]
        LDR R4, [FP, #-16]
        SUB R4, R4, #1
        STR R4, [FP, #-40]
        PUSH {R0, R1, R2, R3, R12, LR}
        MOV R0, R0
        LDR R4, [FP, #-40]
        MOV R1, R4
        BL _f22        
        MOV R7, R0
        POP {R0, R1, R2, R3, R12, LR}
        MOV R4, R7
        STR R4, [FP, #-44]
        LDR R4, [FP, #-36]
        LDR R5, [FP, #-44]
        ADD R4, R4, R5
        STR R4, [FP, #-28]
finSi85:
        LDR R4, [FP, #-12]
        LDR R5, [FP, #-28]
        ADD R0, R4, R5
finSi81:
        ADD SP, SP, #48
        POP {R4, R5, R6, R7, R8, R9, R10, FP}
        BX LR

_start:
        PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
        SUB FP, SP, #4
        SUB SP, SP, #36
        LDR R4, =42
        STR R4, [FP, #0]
        LDR R4, =1
        STR R4, [FP, #-4]
        PUSH {R0, R1, R2, R3, R12, LR}
        LDR R4, [FP, #0]
        MOV R0, R4
        LDR R4, [FP, #-4]
        MOV R1, R4
        BL _min_caml_create_array        
        MOV R7, R0
        POP {R0, R1, R2, R3, R12, LR}
        MOV R4, R7
        STR R4, [FP, #-8]
        LDR R4, =0
        STR R4, [FP, #-12]
        LDR R4, =1
        STR R4, [FP, #-16]
        LDR R4, [FP, #-12]
        LDR R5, [FP, #-16]
        CMP R4, R5
        BNE sinon86
        LDR R4, =0
        STR R4, [FP, #-20]
        B finSi87
sinon86:
        LDR R4, [FP, #-8]
        LDR R4, [R4, #164]
        STR R4, [FP, #-24]
        LDR R4, =41
        STR R4, [FP, #-28]
        PUSH {R0, R1, R2, R3, R12, LR}
        LDR R4, [FP, #-8]
        MOV R0, R4
        LDR R4, [FP, #-28]
        MOV R1, R4
        BL _f22        
        MOV R7, R0
        POP {R0, R1, R2, R3, R12, LR}
        MOV R4, R7
        STR R4, [FP, #-32]
        LDR R4, [FP, #-24]
        LDR R5, [FP, #-32]
        ADD R4, R4, R5
        STR R4, [FP, #-20]
finSi87:
        PUSH {R0, R1, R2, R3, R12, LR}
        LDR R4, [FP, #-20]
        MOV R0, R4
        BL min_caml_print_int        
        MOV R7, R0
        POP {R0, R1, R2, R3, R12, LR}
        MOV R0, R7
        ADD SP, SP, #36
        POP {R4, R5, R6, R7, R8, R9, R10, FP}
        B min_caml_exit

.data
.balign 4
zone_dynamique: .skip 400000
debut_zone_dynamique: .word zone_dynamique
