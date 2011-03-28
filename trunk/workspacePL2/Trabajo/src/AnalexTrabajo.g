class Analex extends Lexer;

	options
	{
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
	
	tokens
	{
		// Palabras reservas (unidad de programa)
		PROGRAM="program";
		
		BEGIN="begin";
		END="end";
		
		CONST="const";
		TYPE="type";
		VAR="var";
		
		FUNCTION="function";
		PROCEDURE="procedure";
		// Palabras reservadas (instrucciones compuestas)
		// palabras reservadas en condicionales
		IF="if";
		THEN="then";
		ELSE="else";
				
		// palabras reservadas en iteraciones
		WHILE="while";
		DO="do";
		FOR="for";
		TO="to";
		DOWNTO="downto";
				
		// Palabras reservadas (devolucion resultado)
		//NINGUNA
		
		// Palabras reservadas (visibilidad)
		//NINGUNA
		
		// Palabras reservadas (clase instanciable)
		//NINGUNA
		
		// Palabras reservas (tipos predefinidos simples)
		INTEGER="integer";
		REAL="real";
		BOOLEAN="boolean";
		CHAR="char";
		
		// Palabras reservas (tipos predefinidos compuestos)
		ARRAY="array";
		STRING="string";
		
		// Palabras reservas (tipos no predefinidos)
		RECORD="record";
		
		// Palabras reservadas (literales logicos)
		TRUE="true";
		FALSE="false";
		
		// Palabras reservadas (operadores logicos)
		AND="and";
		OR="or";
		NOT="not";
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
	//LIT_CAR: '\''! (~('\''|'\n'|'\r'|'\t')) '\''!
	//;
	
	// Lexema IDENT (Identificadores)
	// Se activa la comprobacion de palabras reservadas.
	// Las palabras reservadas tienen preferencia a cualquier otro identificador.
	IDENT options {testLiterals=true;}: LETRA(LETRA|DIGITO)*
	;