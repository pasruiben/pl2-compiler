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
		// Declaraciones
		CONST="CONST";
		TYPE="TYPE";
		VAR="VAR";
		
		FUNCION="FUNCION";
		
		BEGIN="BEGIN";
		END="END";
				
		// Tipos simples
		ENTERO="ENTERO";
		REAL="REAL";
		BOOLEANO="BOOLEANO";
		CARACTER="CARACTER";
		
		// Tipos compuestos
		ARRAY="ARRAY";
		CADENA="CADENA";
		OF="OF";
		
		// Tipos definidos por usuario
		REGISTRO="REGISTRO";
		
		// Leer de taclado e imprimir por pantalla
		LEER="LEER";
		ESCRIBIR="ESCRIBIR";
		
		// Estructuras condicionales
		SI="SI";
		SINO="SINO";
		
		// Iteraciones
		MIENTRAS="MIENTRAS";
		HACER="HACER";
		DESDE="DESDE";
		HASTA="HASTA";
		ATRAS="ATRAS";
		
		//Operadores enteros
		DIV="DIV";
		MOD="MOD";
	
		// Literales logicos
		CIERTO="CIERTO";
		FALSO="FALSO";
		
		// Operadores logicos
		AND="AND";
		OR="OR";
		NOT="NOT";
	
	}
	
	//Tokens inutiles para el analisis sintactico
	//(B)lancos y (T)abuladores
	BT : (' '|'\t') {$setType(Token.SKIP);} ;
	
	//(S)altos de (L)inea
	SL : "\r\n" {newline();$setType(Token.SKIP);} ;
	
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
	NUMERO : ((DIGITO)+ '.' (DIGITO)+) => (DIGITO)+ '.' (DIGITO)+ {$setType(LIT_REAL);}
	| ((DIGITO)+) => (DIGITO)+ {$setType(LIT_ENTERO);}
	;
	//Literales Caracter
	CADENA : ('"' (~'"') '"') => '"' (~'"') '"' {$setType(LIT_CARACTER);}
	   | '"' (~'"')* '"'
	   | ("\'" (~'\'') "\'") => "\'" (~'\'') "\'" {$setType(LIT_CARACTER);}
	   | "\'" (~'\'')* "\'"
	   ;
	
	// Lexema IDENT (Identificadores)
	// Se activa la comprobacion de palabras reservadas.
	// Las palabras reservadas tienen preferencia a cualquier otro identificador.
	IDENT options {testLiterals=true;}: LETRA(LETRA|DIGITO)* ;
