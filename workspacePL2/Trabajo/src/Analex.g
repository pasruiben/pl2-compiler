class Analex extends Lexer;

options
{
	importVocab = Anasint;
	testLiterals = false;
	
	k = 3;
}


//Palabras reservadas 
tokens{
	PROGRAM = "PROGRAM";
	
	CONST = "CONST";
	TYPE = "TYPE";
	VAR = "VAR";
	
	RECORD = "RECORD";
	
	PROCEDURE = "PROCEDURE";
	FUNCTION = "FUNCTION";
	
	BEGIN = "BEGIN";
	END = "END";
	
	INTEGER = "INTEGER";
	REAL = "REAL";
	BOOLEAN = "BOOLEAN";
	CHAR = "CHAR";
	
	ARRAY = "ARRAY";
	OF = "OF";
	STRING = "STRING";
	
	WRITE = "WRITE";
	READ = "READ";
	
	IF = "IF";
	THEN = "THEN";
	ELSE = "ELSE";
	WHILE = "WHILE";
	DO = "DO";
	FOR = "FOR";
	TO = "TO";
	DOWNTO = "DOWNTO";
	
	TRUE = "TRUE";
	FALSE = "FALSE";
	
	AND = "AND";
	OR = "OR";
	NOT = "NOT";
}

//Comentario de lea
COMENT_LIN: "//" (('\r')+ ~('\n') | ~('\r') )* "\r\n" {newline();$setType(Token.SKIP);} ;

//Tokens ctico
//Blancos y tabuladores
BT: (' '|'\t') {$setType(Token.SKIP);} ;
//Saltos de linea
SL: "\r\n" {newline();$setType(Token.SKIP);} ;

//Signos de puntuacin
DOS_PUNTOS : ':'; 
PARENTESIS_ABIERTO : '(';
PARENTESIS_CERRADO : ')';
CORCHETE_ABIERTO: '[';
CORCHETE_CERRADO: ']';
COMA: ',';
PUNTO_Y_COMA: ';';
PUNTO: '.';
RANGO: "..";

//Lexemas auxiliares
protected DIGITO: ('0'..'9');
protected LETRA: ('a'..'z'|'A'..'Z');

//Literales Enteros y Reales
NUMERO : ((DIGITO)+ '.' DIGITO) => (DIGITO)+ '.' (DIGITO)+ {$setType(LIT_REAL);}
| ((DIGITO)+) => (DIGITO)+ {$setType(LIT_ENTERO);}
;

//Literales Cater
LIT_CAR: '\''! (~('\''|'\n'|'\r'|'\t')) '\''!;

//Cadenas
CADENA: '\''! (~('\''|'\n'|'\r'|'\t'))* '\''!; 

//Operadores aritticos
MAS: '+';
MENOS: '-';
POR: '*';
DIVISION_ENTERA: "DIV";
DIVISION_REAL: '/';
MOD: "MOD";

//Operadores relacionales
MENOR:'<';
MENOR_IGUAL:"<=";
MAYOR:'>';
MAYOR_IGUAL:">=";
IGUAL: '=';
DISTINTO: "<>";

//Asignacin
ASIGNACION : ":=" ;

//Lexema IDENT (Identificadores)
//Se activa la comprobacin de palabras reservadas.
//Las palabras reservadas tienen preferencia a cualquier otro identificador.
IDENT options {testLiterals=true;}: LETRA(LETRA|DIGITO)* ;

