# EstudioDrools
PROYECTO DE VALIDACIONES CON DROOLS

flowchart TD

A[Inicio función marcaConAtributosCSEI] --> B[Inicializar contadores: nMarcados=0, nRegistrosLeidos=0]

B --> C[Recorrer registros datAux (x=1..N)]

C --> D{¿datAux[x] cumple condición inicial? <br/> (FAEL='S' y DECLA='N') <br/> O (nEsce=3,7,10 y DECLA='N')}
D -- No --> I[Incrementar nRegistrosLeidos] --> C
D -- Sí --> E[Recorrer atributos sDatosAtributos (y=1..J2)]

E --> F{¿RUT coincide y atributo = 'CSEI'?}
F -- Sí --> G[sw_marcamos=1 <br/> salir del loop atributos]
F -- No --> E

G --> H{sw_marcamos=1 ?}
H -- Sí --> H1[datAux[x]->v_TIENE_CSEI = 'S' <br/> nMarcados++]
H -- No --> H2[datAux[x]->v_TIENE_CSEI = 'N']

H1 --> I
H2 --> I

I[Incrementar nRegistrosLeidos <br/> si múltiplo de 80000 → mostrar progreso] --> C

C -->|Fin del loop| J[Mostrar total marcados con CSEI]
J --> K[Fin función]

