header
{
	import java.util.*;
	import antlr.*;
	import javax.swing.JOptionPane;
	import javax.swing.JPanel;
}

class Anasint extends Parser;

options
{
	buildAST=true;
	k=3;
}

tokens
{
	PROGRAMA;
	DECLARACION;
	CONSTANTE;
	DEC_TIPO;
	TIPO;
	VARIABLE;
	FUNC;
	CUERPO;
	RANGO_ENTERO;
	RANGO_CARACTER;
	ACCESO_SIMPLE;
	ACCESO_REGISTRO;
	ACCESO_ARRAY;
	ACCESO_ARRAY_REGISTRO;
	LLAMADA;
	ARGUMENTOS;
	ASIG;
	CONTENIDO;
	INSTRUCCION;
	CABECERA;
	CAMPO;
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
  
	public AST creaDeclaracionConstante(AST nombreConstante, AST expresion)
	{
	    String nombre = nombreConstante.getText();
	
	    AST arbol = #(#[CONSTANTE,"constante"], astFactory.dupTree(nombreConstante), astFactory.dupTree(expresion));
	
	    Simbolo simbolo = new Simbolo(nombre,arbol);
	
	    int error = pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
	
	    if(error==-1)
	    {
	            JOptionPane.showMessageDialog(jContentPane, nombre+
	            " : ERROR, YA HAY UN SÍMBOLO INSTALADO CON EL MISMO NOMBRE", "Error", JOptionPane.INFORMATION_MESSAGE);
	            System.exit(1);
	    }
	
	    return arbol;
	}
	
	public AST creaDeclaracionRegistro(AST nombreRegistro, AST dec_campos)
    {
	    String nombre = nombreRegistro.getText();
	
	    AST arbol = #(#[DEC_TIPO,"registro"], astFactory.dupTree(nombreRegistro), dec_campos);
	
	    Simbolo simbolo = new Simbolo(nombre,arbol);
	
	    int error = pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
	
	    if(error==-1)
	    {
	        JOptionPane.showMessageDialog(jContentPane, nombre+" : ERROR, YA HAY UN SÍMBOLO INSTALADO CON EL MISMO NOMBRE",
	                                                "Error", JOptionPane.INFORMATION_MESSAGE);
	        System.exit(1);
	    }
	
	    return arbol;
    }
    
    AST creaDeclaracionVariable(AST cabecera, AST tipo, AST listaVars)
    {
		//árbol resultado, será una lista de hermanos con nodo raíz cabecera y 2 hijos (ident y tipo)
		AST result = astFactory.dupTree(cabecera);
		AST ultimoHermano = result;
		
		//crea nodo var
		AST var = new CommonAST();
		var.setText(listaVars.getText());
		var.setType(listaVars.getType());
		
		//añade el nodo tipo
		var.setNextSibling(astFactory.dupTree(tipo));
		
		//ya tenemos la primera variable/parametro lista
		result.setFirstChild(var);
		
		String nombre = listaVars.getText();
		AST arbol = result;
		Simbolo simbolo = new Simbolo(nombre,arbol);
		int error = pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
		
		if (error == -1)
		{
	        JOptionPane.showMessageDialog(jContentPane, nombre+" : ERROR, YA HAY UN SÍMBOLO INSTALADO CON EL MISMO NOMBRE",
	        								"Error", JOptionPane.INFORMATION_MESSAGE);
	        System.exit(1);
		}
		
		//mientras queden variables/parametros
		while (listaVars.getNextSibling() != null)
		{
			//construimos su nodo inicial
			AST otraVariable = astFactory.dupTree(cabecera);
			
			//cogemos la siguiente
			listaVars = listaVars.getNextSibling();
			
			//crea nodo var
			var = new CommonAST();
			var.setText(listaVars.getText());
			var.setType(listaVars.getType());
			
			//añade el nodo tipo
			var.setNextSibling(astFactory.dupTree(tipo));
			
			//ya tenemos otra variable/parametro lista
			otraVariable.setFirstChild(var);
			
			nombre = listaVars.getText();
			arbol = otraVariable;
			simbolo = new Simbolo(nombre,arbol);
			
			error = pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
			
			if (error==-1)
			{
				JOptionPane.showMessageDialog(jContentPane, nombre+" : ERROR, YA HAY UN SÍMBOLO INSTALADO CON EL MISMO NOMBRE",
			        								"Error", JOptionPane.INFORMATION_MESSAGE);
				System.exit(1);
			}
	        //que se coloca como hermana en la lista de variables resultado
	        ultimoHermano.setNextSibling(otraVariable);
	
	        ultimoHermano = otraVariable;
        }

        return result;
	}
                
    
	
 
///////////////////////////FUNCIONES TIPO C : TRATAMIENTO ACCESOS SIMPLES/////////////////////////////////		
	
	public AST ambitoTratarAccesoSimple(AST ident)
	{
		AST declaracionAcceso=null;
		AST arbol=null;
		String nombre=ident.getText();
		Simbolo simbolo=pilaAmbitos.buscarSimbolo(nombre);
		
		if(simbolo!=null)
		{
			declaracionAcceso=simbolo.getDeclaracion();	
		}
		else
		{
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
	
	public AST ambitoTratarIdentTipo(AST ident)
	{
		String nombre=ident.getText();
		Ambito amb=pilaAmbitos.ambitoActual();
		
		while(amb!=null && amb.getTipo()!="PROGRAMA")
			amb=amb.getContenedor();
		
		if(amb==null)
		{
			JOptionPane.showMessageDialog(jContentPane, "ERROR, EL TIPO " +nombre+" NO ES UNA CLASE",
									"Error", JOptionPane.INFORMATION_MESSAGE);
			System.exit(1);
		}
		
		Simbolo s=amb.getDeclaracion(nombre);
		
		if(s==null)
		{
			JOptionPane.showMessageDialog(jContentPane,"ERROR, EL TIPO " +nombre+" NO ES UNA CLASE",
									"Error", JOptionPane.INFORMATION_MESSAGE);
			System.exit(1);
		}
		
		ASTFactory ast=new ASTFactory();
		
		return ast.dupTree(s.getDeclaracion());
	}
}
	
/////////////////////////////////////////////////////////
// DECLARACION DE PROGRAMA          
/////////////////////////////////////////////////////////
 
programa! :
	{ambitoAbrirPrograma();}
	d:declaraciones
	c:cuerpo
	{
		ambitoCerrarPrograma();
		#programa = #(#[PROGRAMA,"programa"],#d,#c);
	}
	;


/////////////////////////////////////////////////////////
// ZONA DE DECLARACIONES         
/////////////////////////////////////////////////////////
declaraciones! : 
	c:constantes
	t:tipos
	v:variables
	{#declaraciones = #(#[DECLARACION,"declaracion"],#c,#t,#v);}
	;
	
constantes : CONST^ (declaraciones_constantes)+  
	  	   |
		   ;
		   		   
declaraciones_constantes! : i:IDENT IGUAL! e:expresion PUNTO_Y_COMA!
							{#declaraciones_constantes = creaDeclaracionConstante(#i,#e);}
							//{#declaraciones_constantes = #(#[CONSTANTE,"constante"],#i,#e);}
						  ;

tipos : TYPE^ (declaraciones_tipos)+  
	  |
	  ;

declaraciones_tipos! : i:IDENT IGUAL! REGISTRO! c:campos END!
						{#declaraciones_tipos = creaDeclaracionRegistro(#i, #c);}
					   //{#declaraciones_tipos = #(#[DEC_TIPO,"dec_tipo"],#i,#c);} 
				     ;

campos : (campo)+
	   ;
	   
campo : tipo IDENT PUNTO_Y_COMA!
		{#campo = #(#[CAMPO,"campo"],##);}
	  ;

variables : VAR^ (dec_var)+
		  |
		  ;
		  
dec_var : variable PUNTO_Y_COMA!
	    | funcion
	    ;
		  
variable! : t:tipo l:lista_vars
		  //{#variable = #(#[VARIABLE,"variable"],##);}
		  {#variable = creaDeclaracionVariable(#(#[VARIABLE,"variable"]),#t,#l);}
		 ;
		 
lista_vars : IDENT (COMA! IDENT)*
		   ;

funcion : cabecera_funcion cuerpo_funcion
			{#funcion = #(#[FUNC,"funcion"],##);}
		;

cabecera_funcion : 
	FUNCION! (tipo)? IDENT PARENTESIS_ABIERTO! (declaraciones_parametros)* PARENTESIS_CERRADO!  
	{#cabecera_funcion = #(#[CABECERA,"cabecera"],##);}
	;
	
cuerpo_funcion : instrucciones END!
				{#cuerpo_funcion = #(#[CUERPO,"cuerpo"],##);}	
			   ;
		
	
declaraciones_parametros : tipo IDENT (COMA! tipo IDENT)*
							{#declaraciones_parametros = #(#[ARGUMENTOS,"argumentos"],##);} 
						 ;


/////////////////////////////////////////////////////////
// CUERPO DEL PROGRAMA         
/////////////////////////////////////////////////////////	

cuerpo : BEGIN! instrucciones END! 
		{#cuerpo = #(#[CUERPO,"cuerpo"],##);}
	   ;


/////////////////////////////////////////////////////////
// TIPOS         
/////////////////////////////////////////////////////////	

tipo : tipo_simple {#tipo = #(#[TIPO,"tipo"],##);}
	 | tipo_compuesto {#tipo = #(#[TIPO,"tipo"],##);}
	 | IDENT {#tipo = #(#[TIPO,"tipo"],##);}
	 ;

tipo_simple : rango
			| ENTERO
			| REAL
			| BOOLEANO
			| CARACTER	 
			;

rango : (MENOS)? LIT_ENTERO PUNTO! PUNTO! (MENOS)? LIT_ENTERO 
		{#rango = #(#[RANGO_ENTERO,"rango_entero"],##);}
	  | LIT_CARACTER PUNTO! PUNTO! LIT_CARACTER
	    {#rango = #(#[RANGO_CARACTER,"rango_caracter"],##);}
	  ;

tipo_compuesto : array
			   | cadena 
			   ;
			    
array : ARRAY^ declaracion_array ;

declaracion_array : 
	CORCHETE_ABIERTO! indice COMA! indice CORCHETE_CERRADO! OF! tipo_simple
	;

indice : ((MENOS)? LIT_ENTERO PUNTO PUNTO) => rango 
	   | expresion
	   ;
	   
cadena : CADENA^ declaracion_cadena ;

declaracion_cadena : CORCHETE_ABIERTO! indice CORCHETE_CERRADO! 
				   |
				   ;


/////////////////////////////////////////////////////////
// INSTRUCCIONES     
/////////////////////////////////////////////////////////	

instrucciones : (instruccion)* 
			  ;

instruccion : instruccion_simple PUNTO_Y_COMA! {#instruccion = #(#[INSTRUCCION,"instruccion"],##);}
			| instruccion_compuesta {#instruccion = #(#[INSTRUCCION,"instruccion"],##);}
			;

instruccion_simple : (IDENT IGUAL expresion) => declaraciones_constantes
				   | (tipo IDENT) => variable
				   | (expresion ASIGNACION) => asignacion
				   | llamada_rutina
				   ;

instruccion_compuesta : condicional
				   	  | iteracion
				      | iteracion_acotada
					  ;

asignacion! : e1:expresion ASIGNACION e2:expresion
			{#asignacion = #(#[ASIG,"asignacion"],#e1,#e2);} 
		    ;			

llamada_rutina : acceso PARENTESIS_ABIERTO! argumentos PARENTESIS_CERRADO!
				{#llamada_rutina = #(#[LLAMADA,"llamada"],##);}
			   | rutina_especial
			   ;
			   
argumentos : IDENT (COMA! IDENT)*
			{#argumentos = #(#[ARGUMENTOS,"argumentos"],##);}
		   | 
		   ;
			   
rutina_especial : LEER PARENTESIS_ABIERTO! expresion PARENTESIS_CERRADO!
			    | ESCRIBIR PARENTESIS_ABIERTO! expresion (COMA! expresion)* PARENTESIS_CERRADO!
			    ;		
			   
condicional : SI^ expresion contenido SINO^ contenido
			;
		 	
contenido : instruccion_simple PUNTO_Y_COMA! {#contenido = #(#[CONTENIDO,"contenido"],##);} 
		  | bloque {#contenido = #(#[CONTENIDO,"contenido"],##);} 
		  ;
		 	
bloque : BEGIN! instrucciones END!
	   ;
			
iteracion : MIENTRAS^ expresion HACER! contenido
		  ;

iteracion_acotada : DESDE^ asignacion iteracion_tipo
				  ;

iteracion_tipo : HASTA! IDENT HACER! contenido
			   | ATRAS! IDENT HACER! contenido
			   ;

			   
/////////////////////////////////////////////////////////////
// EXPRESIONES 
////////////////////////////////////////////////////////////

expresiones : (expresion)+
			;

expresion : 
   expresion_nivel_1 (OR^ expresion_nivel_1)*  ;
   
expresion_nivel_1 : 
   expresion_nivel_2 (AND^ expresion_nivel_2)* ;
   
expresion_nivel_2 : 
   NOT^ expresion_nivel_2
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
	MENOS^ expresion_nivel_6
	| expresion_nivel_7
	;

expresion_nivel_7:
	PARENTESIS_ABIERTO! expresion PARENTESIS_CERRADO!
  | (IDENT PARENTESIS_ABIERTO) => llamada_rutina
  | acceso
  | LIT_ENTERO
  | LIT_REAL
  | LIT_CARACTER
  | CADENA
  | CIERTO
  | FALSO
  ;    
			
acceso! : (IDENT PUNTO) => i1:IDENT PUNTO a1:acceso1
			{#acceso = #(#[ACCESO_REGISTRO,"acceso_registro"],#i1,#a1);}
		| (IDENT CORCHETE_ABIERTO indice CORCHETE_CERRADO PUNTO) => i2:IDENT CORCHETE_ABIERTO! ind1:indice CORCHETE_CERRADO! PUNTO! a2:acceso
        	{#acceso = #(#[ACCESO_ARRAY_REGISTRO,"acceso_array_registro"],#i2, #ind1, #a2);}
        | (IDENT CORCHETE_ABIERTO) => i3:IDENT CORCHETE_ABIERTO! ind2:indice CORCHETE_CERRADO!
        	{#acceso = #(#[ACCESO_ARRAY,"acceso_array"],#i3, #ind2);}
        | i:IDENT
        	{#acceso = #(#[ACCESO_SIMPLE,"acceso_simple"],#i);}
        ;  
        
acceso1! : (IDENT PUNTO) => i1:IDENT PUNTO a1:acceso1
			{#acceso1 = #(#[ACCESO_REGISTRO,"acceso_registro"],#i1,#a1);}
		 | (IDENT CORCHETE_ABIERTO indice CORCHETE_CERRADO PUNTO) => i2:IDENT CORCHETE_ABIERTO! ind1:indice CORCHETE_CERRADO! PUNTO! a2:acceso1
        	{#acceso1 = #(#[ACCESO_ARRAY_REGISTRO,"acceso_array_registro"],#i2, #ind1, #a2);}
         | (IDENT CORCHETE_ABIERTO) => i3:IDENT CORCHETE_ABIERTO! ind2:indice CORCHETE_CERRADO!
        	{#acceso1 = #(#[ACCESO_ARRAY,"acceso_array"],#i3, #ind2);}
         | i:IDENT
        	{#acceso1 = #(#[ACCESO_SIMPLE,"acceso_simple"],#i);}
         ;          