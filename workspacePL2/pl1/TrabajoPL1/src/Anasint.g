header{
	import java.util.Hashtable;	
}

///////////////////////////////
// Analizador léxico
///////////////////////////////

class Analex extends Lexer;

protected NUEVA_LINEA: "\r\n" {newline();};

BLANCO: (' '|'\t'|NUEVA_LINEA) {$setType(Token.SKIP);};

protected DIGITO: '0'..'9';

NUMERO: (DIGITO)+ ;

PYC: ';' ;
MAS: '+' ;
MENOS: '-' ;
POR: '*' ;
DIV: '/' ;
PA: '(' ;
PC: ')' ;


///////////////////////////////
// Analizador sintáctico
///////////////////////////////

class Anasint extends Parser;

{Hashtable variables = new Hashtable(); }

entrada: (instruccion)* EOF;

asignacion {int e;} 
   		: i: IDENT ASIG e = expr PYC
   		  { System.out.println("Asignacion: "+i.getText() + "vale " +e);}
   		;

instruccion {int e;} 
		: e=expr PYC
		{System.out.println("Expresion: "+e);}
		;
expr returns [int res=0] {int e1,e2;}
		: e1=exp_mult {res=e1;}
		  ((MAS e2=exp_mult {res=res+e2;}) | (MENOS e2=exp_mult {res=res-e2;}))*
		;
exp_mult returns [int res=0] {int e1,e2;}
		: e1=exp_base {res=e1;}
		  ((POR e2=exp_base {res=res*e2;}) | (DIV e2=exp_base {res=res/2;}))*
		;
exp_base returns [int res=0] {int e;}
		: n:NUMERO
		 {res = valorNumero (n);}
		| PA e=expr PC {res=e;}
		| i:IDENT {res=valorVariable(i);}
		;
				