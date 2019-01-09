.text
.global _start
_f18:
    PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
    SUB FP, SP, #4
    SUB SP, SP, #20
    LDR R8, =1
    LDR R9, =1
    CMP R0, R9
    BGT sinon80
    LDR R10, =1
    B finSi81
.ltorg
sinon80:
    LDR R10, =0
finSi81:
    CMP R10, R8
    BNE sinon82
    MOV R0, R0
    B finSi83
.ltorg
sinon82:
    LDR R9, =2
    SUB R12, R0, R9
    LDR R6, =1
    LDR R4, =1
    STR R4, [FP, #0]
    LDR R5, [FP, #0]
    CMP R12, R5
    BGT sinon84
    LDR R4, =1
    STR R4, [FP, #-4]
    B finSi85
.ltorg
sinon84:
    LDR R4, =0
    STR R4, [FP, #-4]
finSi85:
    LDR R4, [FP, #-4]
    CMP R4, R6
    BNE sinon86
    MOV R8, R12
    B finSi87
.ltorg
sinon86:
    LDR R8, =2
    SUB R9, R12, R8
    PUSH {R0, R1, R2, R3, R12, LR}
    MOV R0, R9
    BL _f18    
    MOV R7, R0
    POP {R0, R1, R2, R3, R12, LR}
    MOV R10, R7
    LDR R4, =1
    STR R4, [FP, #0]
    LDR R5, [FP, #0]
    SUB R4, R12, R5
    STR R4, [FP, #-8]
    PUSH {R0, R1, R2, R3, R12, LR}
    LDR R4, [FP, #-8]
    MOV R0, R4
    BL _f18    
    MOV R7, R0
    POP {R0, R1, R2, R3, R12, LR}
    MOV R4, R7
    STR R4, [FP, #-12]
    LDR R4, [FP, #-12]
    ADD R8, R4, R10
finSi87:
    LDR R9, =1
    SUB R10, R0, R9
    LDR R9, =1
    LDR R12, =1
    CMP R10, R12
    BGT sinon88
    LDR R6, =1
    B finSi89
.ltorg
sinon88:
    LDR R6, =0
finSi89:
    CMP R6, R9
    BNE sinon90
    MOV R12, R10
    B finSi91
.ltorg
sinon90:
    LDR R12, =2
    SUB R4, R10, R12
    STR R4, [FP, #0]
    PUSH {R0, R1, R2, R3, R12, LR}
    LDR R4, [FP, #0]
    MOV R0, R4
    BL _f18    
    MOV R7, R0
    POP {R0, R1, R2, R3, R12, LR}
    MOV R4, R7
    STR R4, [FP, #-4]
    LDR R4, =1
    STR R4, [FP, #-8]
    LDR R5, [FP, #-8]
    SUB R4, R10, R5
    STR R4, [FP, #-12]
    PUSH {R0, R1, R2, R3, R12, LR}
    LDR R4, [FP, #-12]
    MOV R0, R4
    BL _f18    
    MOV R7, R0
    POP {R0, R1, R2, R3, R12, LR}
    MOV R4, R7
    STR R4, [FP, #-16]
    LDR R4, [FP, #-16]
    LDR R5, [FP, #-4]
    ADD R12, R4, R5
finSi91:
    ADD R0, R12, R8
finSi83:
    ADD SP, SP, #20
    POP {R4, R5, R6, R7, R8, R9, R10, FP}
    BX LR
.ltorg

_start:
    PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
    SUB FP, SP, #4
    SUB SP, SP, #16
    LDR R8, =30
    LDR R9, =1
    LDR R10, =1
    CMP R8, R10
    BGT sinon92
    LDR R12, =1
    B finSi93
.ltorg
sinon92:
    LDR R12, =0
finSi93:
    CMP R12, R9
    BNE sinon94
    MOV R10, R8
    B finSi95
.ltorg
sinon94:
    LDR R10, =2
    SUB R6, R8, R10
    PUSH {R0, R1, R2, R3, R12, LR}
    MOV R0, R6
    BL _f18    
    MOV R7, R0
    POP {R0, R1, R2, R3, R12, LR}
    MOV R4, R7
    STR R4, [FP, #0]
    LDR R4, =1
    STR R4, [FP, #-4]
    LDR R5, [FP, #-4]
    SUB R4, R8, R5
    STR R4, [FP, #-8]
    PUSH {R0, R1, R2, R3, R12, LR}
    LDR R4, [FP, #-8]
    MOV R0, R4
    BL _f18    
    MOV R7, R0
    POP {R0, R1, R2, R3, R12, LR}
    MOV R4, R7
    STR R4, [FP, #-12]
    LDR R4, [FP, #-12]
    LDR R5, [FP, #0]
    ADD R10, R4, R5
finSi95:
    PUSH {R0, R1, R2, R3, R12, LR}
    MOV R0, R10
    BL min_caml_print_int    
    MOV R7, R0
    POP {R0, R1, R2, R3, R12, LR}
    MOV R0, R7
    ADD SP, SP, #16
    POP {R4, R5, R6, R7, R8, R9, R10, FP}
    B min_caml_exit
.ltorg

