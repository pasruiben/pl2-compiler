class Analex extends Lexer;
options{
// Importacion del vocabulario de tokens desde el analizador
// sintactico (Anasint.g)
 importVocab=Anasint;
// Por defecto no se activa la comprobacion de literales
// declarados en la seccion tokens
testLiterals=false;
//2 simbolos de anticipacion para tomar decisiones
//(los tokens DP y ASIG justifican su necesidad)
k=2;
}
tokens{
	
// Palabras reservas (unidad de programa)
MODULO="modulo";

//Palabra reservada para la creacion de objetos
CREAR="crear";

//Palabra reservada para imprimir por pantalla
//ESCRIBIR="escribir";

// Palabras reservadas (instrucciones compuestas)
// palabras reservadas en condicionales
SI="si";
ENTONCES="entonces";
SINO="sino";
FINSI="finsi";
// palabras reservadas en iteraciones
MIENTRAS="mientras";
HACER="hacer";
FINMIENTRAS="finmientras";
// Palabras reservadas (devolucion resultado)
DEV="dev";
// Palabras reservadas (visibilidad)
OCULTO="oculto";
// Palabras reservadas (clase instanciable)
INST="inst";
// Palabras reservas (tipos predefinidos simples)
ENTERO="entero";
REAL="real";
LOGICO="logico";
CARACTER="caracter";
// Palabras reservas (tipos predefinidos compuestos)
FORMACION="formacion";
// Palabras reservas (tipos no predefinidos)
CLASE="clase";
// Palabras reservadas (literales logicos)
CIERTO="cierto";
FALSO="falso";
// Palabras reservadas (operadores logicos)
Y="y";
O="o";
NO="no";

}
//Tokens inutiles para el analisis sintactico
//(B)lancos y (T)abuladores
BT : (' '|'\t') {$setType(Token.SKIP);} ;
//(S)altos de (L)inea
SL : "\r\n" {newline();$setType(Token.SKIP);} ;

// Comentario de linea
COMENT_LIN: "//" (('\r')+ ~('\n') | ~('\r') )* "\r\n" {newline();$setType(Token.SKIP);} ;

// Signos de puntuacion
DOS_PUNTOS : ':'; //(D)os (P)untos
PARENTESIS_ABIERTO : '('; // (P)arentesis (A)bierto
PARENTESIS_CERRADO : ')'; // (P)arentesis (C)errado
LLAVE_ABIERTA: '{'; // (LL)ave (A)bierta
LLAVE_CERRADA: '}'; // (LL)ave (C)errada
CORCHETE_ABIERTO: '['; // (COR)chete (A)bierto
CORCHETE_CERRADO: ']'; // (COR)chete (C)errado
COMA: ','; // (CO)ma
PUNTO_Y_COMA: ';'; // (PU)nto y (C)oma
PUNTO:'.'; // (PU)nto

// Operadores aritmeticos
MAS: '+';
MENOS: '-';
POR: '*';
DIVISION: '/';

// Operadores relacionales
MENOR:'<';
MENOR_IGUAL:"<=";
MAYOR:'>';
MAYOR_IGUAL:">=";
IGUAL: '=';
DISTINTO: "!=";

// Asignacion
ASIGNACION : ":=" ;

// Lexemas auxiliares
protected DIGITO: ('0'..'9');
protected LETRA: ('a'..'z'|'A'..'Z');


// Literales Enteros y Reales
NUMERO : ((DIGITO)+ '.') => (DIGITO)+ '.' (DIGITO)+ {$setType(LIT_REAL);}
| ((DIGITO)+) => (DIGITO)+ {$setType(LIT_ENTERO);}
;
//Literales Caracter
LIT_CAR: '\''! (~('\''|'\n'|'\r'|'\t')) '\''!;
// Lexema IDENT (Identificadores)
// Se activa la comprobacion de palabras reservadas.
// Las palabras reservadas tienen preferencia a cualquier otro identificador.
IDENT options {testLiterals=true;}: LETRA(LETRA|DIGITO)* ;




