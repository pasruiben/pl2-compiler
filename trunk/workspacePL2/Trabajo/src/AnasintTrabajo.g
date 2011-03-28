header
{
	import java.util.*;
	import antlr.*;
}

class Anasint extends Parser;

options{
	buildAST=true;
}

tokens{
	//PROGRAMA;
	//VISIBLE;
	//OCULTO;
	//NO_INST;
	//ATRIBUTO;
	//METODO;
	//PROTOTIPO;
	//PARAMETRO;
	//PARAMETROS;
	EXPRESIONES;
	//RESULTADO;
	//DEFINICION;
	//VACIO;
    //VARIABLE_LOCAL;
	//VARIABLES_LOCALES;
	INSTRUCCION;
	INSTRUCCIONES;
	MENOSUNARIO;
	LLAMADA; 
	ACCESO_TABLA;
	ACCESO_OBJETO;
	ACCESO_SIMPLE;
	LISTA_ENTEROS;
}

{		
}
	
/////////////////////////////////////////////////////////
// DECLARACION DE PROGRAMA       
/////////////////////////////////////////////////////////
 
program ! : 
   c:cabecera PUNTO_Y_COMA
   d:declaraciones_program
   e:cuerpo_program EOF
   { #program = #(#[PROGRAM,"program"],#c, #d, #e); } 
   ;
 
cabecera : PROGRAM! IDENT ;

declaraciones_program :  
   lista_declaraciones_const
   lista_declaraciones_type
   lista_declaraciones_var
   lista_declaraciones_funcyproc	
          ;             

lista_declaraciones_const :
	CONST
	(declaracion_const)+
	;
	
lista_declaraciones_type :
	TYPE
	(declaracion_type)+
	;
	
lista_declaraciones_var :
	VAR
	(declaracion_var)+
	;
	
lista_declaraciones_funcyproc :
	(declaracion_funcyproc)+
	;
	
cuerpo_program : 
	BEGIN instrucciones END
	;
	
	
///////////////////////////////////////////////////////////
// DECLARACION DE CONST, TYPE Y VAR         
///////////////////////////////////////////////////////////	
	
declaracion_const ! :
	IDENT IGUAL IDENT PUNTO_Y_COMA
	;
	
declaracion_type ! :
	IDENT IGUAL RECORD campos END
	;
	
campos : (campo)+
	;
	
campo : IDENT DOS_PUNTOS IDENT PUNTO_Y_COMA
	;

declaracion_var ! :
	variables DOS_PUNTOS IDENT PUNTO_Y_COMA
	;
	
variables : (IDENT)+
	;
	
///////////////////////////////////////////////////////////
// DECLARACION DE FUNCIONES Y PROCEDIMIENTOS          
///////////////////////////////////////////////////////////

declaracion_funcyproc ! :
	funcion
	| procedimiento
	;
	
funcion :
	cabecera_func
	declaraciones_func
	cuerpo_func
	;

cabecera_func : 
	FUNCTION IDENT parametros DOS_PUNTOS IDENT
	;
	
parametros : 
	PARENTESIS_ABIERTO 
	parametro (COMA parametro)+
	PARENTESIS CERRADO
	;
	
parametro :
	IDENT
	DOS_PUNTOS
	IDENT
	;
	
declaraciones_func : 
	declaracion_const
	declaracion_var
	;
	
cuerpo_func :
	BEGIN instrucciones END
	;
	
procedimiento :
	cabecera_proc
	declaraciones_func
	cuerpo_func
	;

cabecera_proc : 
	FUNCTION IDENT parametros 
	;	
	
   
///////////////////////////////////////////////////////////
// INSTRUCCIONES 
///////////////////////////////////////////////////////////
instrucciones : (instruccion)* ;

instruccion! : 
   i:instruccion_simple PUNTO_Y_COMA
   {#instruccion=#(#[INSTRUCCION,"instruccion"],#i);}
 | j:instruccion_compuesta 
   {#instruccion=#(#[INSTRUCCION,"instruccion"],#j);}
 ;

instruccion_simple : 
    (expresion ASIGNACION) => asignacion
  | llamada_metodo
  | retorno
  ;

instruccion_compuesta : 
     condicion
   | iteracion
   | iteracion_acotada
   ;
 
asignacion! : 
   e1:expresion ASIGNACION e2:expresion 
   { #asignacion = #(#[ASIGNACION],#e1,#e2); }
   ;
    
 
retorno! : IDENT
	;

 
llamada_metodo! :  
   i:acceso 
   PARENTESIS_ABIERTO 
           s:lista_expresiones 
   PARENTESIS_CERRADO 
   { #llamada_metodo = #(#[LLAMADA,"llamada"],#i,#(#[EXPRESIONES,"expresiones"],#s)); }
   ;

lista_expresiones :  expresion (COMA! expresion)*
                  |
                  ;
 
condicion : IF^ expresion THEN! 
               contenido 
           (ELSE! contenido)? 
           ;
 
iteracion : WHILE^ expresion DO!  
            contenido 
          ;    
          
iteracion_acotada : FOR asignacion formato
		;
		
formato : 
	TO IDENT DO contenido
  | DOWNTO IDENT DO contenido
  ;	

contenido : instruccion_simple
		  | bloque
		  ;

bloque!: 
	BEGIN
	i:instrucciones 
  	END
  	{#bloque=#(#[INSTRUCCIONES,"instrucciones"],#i);}
;

/////////////////////////////////////////////////////////////
// EXPRESIONES 
////////////////////////////////////////////////////////////

expresion : 
   expresion_nivel_1 (O^ expresion_nivel_1)*  ;
   
expresion_nivel_1 : 
   expresion_nivel_2 (Y^ expresion_nivel_2)* ;
   
expresion_nivel_2 : 
   NO^ expresion_nivel_2
   | expresion_nivel_3 ;   

expresion_nivel_3 : 
   expresion_nivel_4 
  ((MAYOR^|MAYOR_IGUAL^|MENOR^|MENOR_IGUAL^|IGUAL^|DISTINTO^) 
    expresion_nivel_4)? ;

expresion_nivel_4 :
     expresion_nivel_5 ((MAS^|MENOS^) expresion_nivel_5)* ;
   
expresion_nivel_5 :
      expresion_nivel_6 ((POR^|DIVISION^) expresion_nivel_6)* ;

expresion_nivel_6! :
	MENOS i:expresion_nivel_6
	{#expresion_nivel_6=#(#[MENOSUNARIO,"menosunario"],#i);}
	| j:expresion_nivel_7 {#expresion_nivel_6=#j;}
	;

expresion_nivel_7:
	PARENTESIS_ABIERTO! expresion PARENTESIS_CERRADO!
  | (acceso PARENTESIS_ABIERTO) => llamada_metodo
  | (acceso CORCHETE_ABIERTO) => acceso_tabla
  | acceso
  | LIT_ENTERO
  | LIT_REAL
  | LIT_CAR
  | CIERTO
  | FALSO
  ;    
//////////////////////////////////////////////////////////////
// ACCESOS A TABLAS
//////////////////////////////////////////////////////////////
acceso_tabla!: c:acceso CORCHETE_ABIERTO d:lista_expresiones_nv CORCHETE_CERRADO
 	{#acceso_tabla=#(#[ACCESO_TABLA,"acceso_tabla"],#c,#(#[EXPRESIONES,"expresiones"],#d));}
;

//////////////////////////////////////////////////////////////
// ACCESOS SIMPLES Y A OBJETOS
//////////////////////////////////////////////////////////////
 
acceso!: 
   (IDENT PUNTO)=> i1:IDENT PUNTO i2:IDENT
           {#acceso=#(#[ACCESO_OBJETO,"acceso_objeto"],#(#[ACCESO_SIMPLE,"acceso_simple"],#i1),#i2);}
 | i:IDENT  
           {#acceso=#(#[ACCESO_SIMPLE,"acceso_simple"],#i);}
 ;

lista_expresiones_nv :  
    expresion (COMA! expresion)*
                  ;

//////////////////////////////////////////////////////////
// TIPOS
/////////////////////////////////////////////////////////
tipo : 
   tipo_predefinido_simple 
 | tipo_predefinido_compuesto
 | IDENT
 ;

tipo_predefinido_simple :
     ENTERO 
   | REAL   
   | LOGICO
   | CARACTER
   ;

tipo_predefinido_compuesto :
   formacion
   ;
 
formacion! : FORMACION l:lista_enteros
         ( t:tipo_predefinido_simple
	    {#formacion = #(#[FORMACION,"formacion"],#(#[LISTA_ENTEROS,"lista_enteros"],#l),#t);}
            | i:IDENT
   	    {#formacion = #(#[FORMACION,"formacion"],#(#[LISTA_ENTEROS,"lista_enteros"],#l),#i);}
);

lista_enteros :  LIT_ENTERO (COMA! LIT_ENTERO)*;
