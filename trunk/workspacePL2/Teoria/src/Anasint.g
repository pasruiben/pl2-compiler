header{
	import java.util.*;
	import antlr.*;
	import javax.swing.JOptionPane;
	import javax.swing.JPanel;

}

class Anasint extends Parser;

options{
	buildAST=true;
}

tokens{
	PROGRAMA;
	VISIBLE;
	OCULTO;
	NO_INST;
	ATRIBUTO;
	METODO;
	PROTOTIPO;
	PARAMETRO;
	PARAMETROS;
	EXPRESIONES;
	RESULTADO;
	DEFINICION;
	VACIO;
    VARIABLE_LOCAL;
	VARIABLES_LOCALES;
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
	//////////////////ATRIBUTOS NECESARIOS////////////////////////////////////////////////////////////
	PilaAmbitos pilaAmbitos=new PilaAmbitos();
	JPanel jContentPane=new JPanel();
	
	///////////////////////////FUNCIONES TIPO A : MANEJO DE AMBITOS////////////////////////////////////
	 public void ambitoAbrirPrograma(){
		if(!pilaAmbitos.isEmpty())
			System.out.println("ERROR, LA PILA DE AMBITOS NO ESTo VACoA AL COMIENZO");
		Ambito ambito=new Ambito("programa","PROGRAMA",null,null);
		pilaAmbitos.apilarAmbito(ambito);
		
	}
	
	public void ambitoAbrir(AST nombre,String tipo){
	
	Ambito ambito=new Ambito(nombre.getText(),tipo,null,pilaAmbitos.ambitoActual())	;
	pilaAmbitos.apilarAmbito(ambito);

	}
	
	public void ambitoCerrar(){
		if(pilaAmbitos.isEmpty())
			System.out.println("ERROR, LA PILA ESTo VACoA");
		pilaAmbitos.desapilarAmbito();

	}
	
	public void ambitoCerrarPrograma(){
		if(pilaAmbitos.isEmpty())
			System.out.println("ERROR, LA PILA ESTo VACoA");
	    if(!pilaAmbitos.ambitoActual().getTipo().equals("PROGRAMA"))
		    System.out.println("ERROR, EL AMBITO ACTUAL NO ES PROGRAMA");
		else{
			pilaAmbitos.desapilarAmbito();
			}
		    
	}
///////////////////////////FUNCIONES TIPO B : INSTALACIoN DE DECLARACIONES////////////////////////////////////
 public AST creaDeclaracionModulo(AST nombreModulo,AST definicionModulo){
 	String nombre=nombreModulo.getText();
 	AST arbol=#(#[MODULO,"modulo"],nombreModulo,definicionModulo);
 	Simbolo simbolo=new Simbolo(nombre,arbol);
 	int error=pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
 	if(error==-1){
 		JOptionPane.showMessageDialog(
								jContentPane,
								nombre+" : ERROR, YA HAY UN SoMBOLO INSTALADO CON EL MISMO NOMBRE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
 		System.exit(1);
 	}
 	return arbol;
 }
 
 
 public AST creaDeclaracionClase(AST nombreClase,AST cualificadorClase,AST definicionClase){
 	String nombre=nombreClase.getText();
 	AST arbol=#(#[CLASE,"clase"],nombreClase,cualificadorClase,definicionClase);
 	Simbolo simbolo=new Simbolo(nombre,arbol);
 	int error=pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
 	if(error==-1){

 		JOptionPane.showMessageDialog(
								jContentPane,
								nombre+" : ERROR, YA HAY UN SoMBOLO INSTALADO CON EL MISMO NOMBRE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
 		 		System.exit(1);
 	}
 	return arbol;
 }
 
 
 public AST creaDeclaracionMetodo(AST declaracionMetodo,AST cualificadorMetodo){
 	AST arbol=#(#[METODO,"metodo"],declaracionMetodo,cualificadorMetodo);
 	String nombre=arbol.getFirstChild().getFirstChild().getText();
 	Simbolo simbolo=new Simbolo(nombre,arbol);
 	int error=pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
 	if(error==-1){
 		JOptionPane.showMessageDialog(
								jContentPane,
								nombre+" : ERROR, YA HAY UN SoMBOLO INSTALADO CON EL MISMO NOMBRE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
 		 		System.exit(1);
 	}
 	return arbol;
 }
 
 public AST creaDeclaracionAtributo(AST nombreAtributo,AST tipoAtributo,AST cualificadorAtributo){
	 String nombre=nombreAtributo.getText();

 	AST arbol=#(#[ATRIBUTO,"atributo"],nombreAtributo,tipoAtributo,cualificadorAtributo);
 	Simbolo simbolo=new Simbolo(nombre,arbol);
 	int error=pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
 	if(error==-1){
 		JOptionPane.showMessageDialog(
								jContentPane,
								nombre+" : ERROR, YA HAY UN SoMBOLO INSTALADO CON EL MISMO NOMBRE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
 		 		System.exit(1);
 	}
 	return arbol;
 }
 
 public AST creaDeclaracionParametro(AST nombreParametro,AST tipoParametro){
	 String nombre=nombreParametro.getText();
 	AST arbol=#(#[PARAMETRO,"parametro"],nombreParametro,tipoParametro);
 	Simbolo simbolo=new Simbolo(nombre,arbol);
 	int error=pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
 	if(error==-1){
 		JOptionPane.showMessageDialog(
								jContentPane,
								nombre+" : ERROR, YA HAY UN SoMBOLO INSTALADO CON EL MISMO NOMBRE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
 		 		System.exit(1);
 	}
 	return arbol;
 }
 
 
 public AST creaDeclaracionVariableLocal(AST nombreVariable,AST tipoVariable){
	 String nombre=nombreVariable.getText();
 	AST arbol=#(#[VARIABLE_LOCAL,"variable_local"],nombreVariable,tipoVariable);
 	Simbolo simbolo=new Simbolo(nombre,arbol);
 	int error=pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
 	if(error==-1){
 		JOptionPane.showMessageDialog(
								jContentPane,
								nombre+" : ERROR, YA HAY UN SoMBOLO INSTALADO CON EL MISMO NOMBRE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
 		 		System.exit(1);
 	}
 	return arbol;
 }
 
///////////////////////////FUNCIONES TIPO C : TRATAMIENTO ACCESOS SIMPLES/////////////////////////////////		
	public AST ambitoTratarAccesoSimple(AST ident){
	
	AST declaracionAcceso=null;
	AST arbol=null;
	String nombre=ident.getText();
	Simbolo simbolo=pilaAmbitos.buscarSimbolo(nombre);
	
	if(simbolo!=null){
		declaracionAcceso=simbolo.getDeclaracion();
		
		
		
	}

	else{
		JOptionPane.showMessageDialog(
								jContentPane,
								"ERROR AL RESOLVER SIMBOLO: "+nombre+" NO TIENE DECLARACION ASOCIADA",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
		
		System.exit(1);

		
	}
	

	ASTFactory ast=new ASTFactory(); //se duplica porque falla el crear elementos de clases p.e. crear(Elemento)
	arbol=#(#[ACCESO_SIMPLE,"acceso_simple"],ident,ast.dupTree(declaracionAcceso)); 
	
		
	return arbol;
	
}



///////////////////////////FUNCIONES TIPO D : TRATAMIENTO DE IDENTIFICADORES DE TIPO/////////////////////////////	
	public AST ambitoTratarIdentTipo(AST ident){
	String nombre=ident.getText();
	Ambito amb=pilaAmbitos.ambitoActual();
	while(amb!=null && amb.getTipo()!="MODULO")
		amb=amb.getContenedor();
	
	if(amb==null){
		JOptionPane.showMessageDialog(
								jContentPane,
								"ERROR, EL TIPO " +nombre+" NO ES UNA CLASE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
		
		System.exit(1);

	}
	Simbolo s=amb.getDeclaracion(nombre);
	if(s==null){
			JOptionPane.showMessageDialog(
								jContentPane,
								"ERROR, EL TIPO " +nombre+" NO ES UNA CLASE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
					System.exit(1);

	}
	ASTFactory ast=new ASTFactory();
	
	return ast.dupTree(s.getDeclaracion());
	}		
}
	
/////////////////////////////////////////////////////////
// DECLARACION DE MODULO          
/////////////////////////////////////////////////////////
 
declaracion_modulo! : 
	{ambitoAbrirPrograma();}
   n:nombre_modulo 
	{ambitoAbrir(#n,"MODULO");}	
   d:definicion_modulo EOF
 	{ambitoCerrar();
	 #declaracion_modulo = creaDeclaracionModulo(#n,#d); 
	 ambitoCerrarPrograma();
	}

   ;
 
nombre_modulo : MODULO! IDENT ;

definicion_modulo :  
   lista_declaraciones_clases
          ;             

lista_declaraciones_clases :    
   (declaracion_clase)*
   ;

             
///////////////////////////////////////////////////////////
// DECLARACIoN DE CLASE          
///////////////////////////////////////////////////////////
 
declaracion_clase ! : 
   c:cualificador_clase 
   n:nombre_clase
	{ambitoAbrir(#n,"CLASE");}  
   d:definicion_clase 
  	{ambitoCerrar();
	 #declaracion_clase = creaDeclaracionClase(#n,#c,#d); 
	}
   ;

cualificador_clase! :  
    INST {#cualificador_clase = #(#[INST]);}
   |     {#cualificador_clase = #(#[NO_INST,"no_inst"]);}
   ;

nombre_clase : CLASE! IDENT ;

definicion_clase : 
   LLAVE_ABIERTA! 
      declaraciones_elemento_clase 
   LLAVE_CERRADA! 
   ; 

declaraciones_elemento_clase : 
     (declaracion_elemento_clase)* ;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
// DECLARACIoN DE ELEMENTO CLASE: MoTODO O ATRIBUTO          
/////////////////////////////////////////////////////////////////////////////////////////////////////////////

declaracion_elemento_clase! : 
    c:cualificador_elemento_clase 
    ((IDENT PARENTESIS_ABIERTO) => 
       a:declaracion_metodo 
       { ambitoCerrar();
	   #declaracion_elemento_clase = creaDeclaracionMetodo(#a,#c); 
	 }
    |  t:tipo   i:IDENT  PUNTO_Y_COMA
       { #declaracion_elemento_clase = creaDeclaracionAtributo(#i,#t,#c); })
    ;

// Cualificador de visibilidad
cualificador_elemento_clase! :  
   OCULTO {#cualificador_elemento_clase = #(#[OCULTO]);}
   | {#cualificador_elemento_clase = #(#[VISIBLE,"visible"]);}
   ;

declaracion_metodo :
   prototipo_metodo
   definicion_metodo
   ;
   
prototipo_metodo! :   
   i:IDENT
	{ambitoAbrir(#i,"METODO");}
   PARENTESIS_ABIERTO 
   p:declaracion_parametros 
   PARENTESIS_CERRADO 
   (DEV t:tipo 
   {#prototipo_metodo = #(#[PROTOTIPO,"prototipo"],#i,#(#[PARAMETROS,"parametros"],#p),#(#[RESULTADO,"resultado"],#t));}
   | {#prototipo_metodo = #(#[PROTOTIPO,"prototipo"],#i,#(#[PARAMETROS,"parametros"],#p),#(#[RESULTADO,"resultado"],#[VACIO, "vacio"]));}
   )
   ;

declaracion_parametros : 
      declaracion_parametro (COMA! declaracion_parametro)* 
    |
    ;

 ///////////////////////////////////////////////////////////
// DECLARACIoN DE PARoMETRO         
///////////////////////////////////////////////////////////   
 
declaracion_parametro!: t:tipo i:IDENT 
    {#declaracion_parametro = creaDeclaracionParametro(#i,#t);}
    ;

definicion_metodo! : 
   LLAVE_ABIERTA
      d:declaraciones_variables_locales  
      b:bloque     
   LLAVE_CERRADA
   {#definicion_metodo=   #(#[DEFINICION,"definicion"],#(#[VARIABLES_LOCALES,"variables_locales"],#d), #b);}
   ;

declaraciones_variables_locales  : 
    (declaracion_variables_locales tipo IDENT) => 
       declaracion_variables_locales  declaraciones_variables_locales 
   |(declaracion_variables_locales) =>  
       declaracion_variables_locales 
   | 
   ;
   
declaracion_variables_locales  : 
   t:tipo! lista_nombres_variables_locales[#t] PUNTO_Y_COMA! ;

lista_nombres_variables_locales [AST t] : 
   nombre_variable_local[#t] (COMA! nombre_variable_local[#t])* ;
  
 /////////////////////////////////////////////////////////////////
// DECLARACIoN DE VARIABLE LOCAL         
//////////////////////////////////////////////////////////////////   
 
nombre_variable_local! [AST t] : 
   i:IDENT 
   { #nombre_variable_local = creaDeclaracionVariableLocal(#i,#t);}
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
   ;
 
asignacion! : 
   e1:expresion ASIGNACION e2:expresion 
   { #asignacion = #(#[ASIGNACION],#e1,#e2); }
   ;
    
 
retorno! : DEV e:expresion 
   { #retorno = #(#[DEV],#e); }
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
 
condicion : SI^ PARENTESIS_ABIERTO! expresion PARENTESIS_CERRADO! ENTONCES! 
               bloque 
           (SINO! bloque)? FINSI! ;

 
iteracion : MIENTRAS^ PARENTESIS_ABIERTO! expresion PARENTESIS_CERRADO! HACER! 
               bloque 
            FINMIENTRAS! ;    

bloque!: i:instrucciones 
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

expresion_nivel_6 :
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
           {#acceso=#(#[ACCESO_OBJETO,"acceso_objeto"],ambitoTratarAccesoSimple(#i1),#i2);}
 | i:IDENT  
           {#acceso=ambitoTratarAccesoSimple(#i);}
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
 | i:IDENT {#tipo=ambitoTratarIdentTipo(#i);}

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
   	    {#formacion = #(#[FORMACION,"formacion"],#(#[LISTA_ENTEROS,"lista_enteros"],#l),ambitoTratarIdentTipo(#i));}
);

lista_enteros :  LIT_ENTERO (COMA! LIT_ENTERO)*;
