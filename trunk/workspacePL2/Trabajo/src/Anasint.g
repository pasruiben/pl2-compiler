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
	DECLARACIONES;
	CONSTANTE;
	CAMPO;
	FUNCION;
	PROCEDIMIENTO;
	VACIO;
	CABECERA;
	CUERPO;
	RUTINAS;
	PARAMETROS;
	LLAMADA;
	ACCESO_ARRAY;
	ACCESO_ARRAY_REGISTRO;
	ACCESO_SIMPLE;
	ACCESO_REGISTRO;
	TIPO;
	IDENTS;
	INDICES;
	DIMENSIONES;
	CONDICION;
	BLOQUE_IF;
	BLOQUE_ELSE;
	CABECERA_FOR;
	BLOQUE_FOR;
	VARIABLE;
	VARIABLE_RESULTADO;
	RANGO_ENTERO;
	PARAMETRO;
	REGISTRO;
	EXPRESIONES;
}

{		
	//////////////////ATRIBUTOS NECESARIOS////////////////////////////////////////////////////////////
	PilaAmbitos pilaAmbitos = new PilaAmbitos();
	JPanel jContentPane=new JPanel();
	
	///////////////////////////FUNCIONES TIPO A : MANEJO DE AMBITOS////////////////////////////////////
	public void ambitoAbrirPrograma()
	{
		if(!pilaAmbitos.isEmpty())
			System.out.println("ERROR, LA PILA DE AMBITOS NO ESTÁ VACÍA AL COMIENZO");
			
		Ambito ambito = new Ambito("programa","PROGRAMA",null,null);
		pilaAmbitos.apilarAmbito(ambito);	
	}
	
	public void ambitoAbrirRutina(AST nombre)
	{
		if (pilaAmbitos.ambitoActual().getTipo() != "PROGRAMA")
			System.out.println("ERROR, LA PILA DE AMBITOS NO ES CORRECTA");
		
		Ambito ambito = new Ambito(nombre.getText(),"RUTINA",null,pilaAmbitos.ambitoActual());
		pilaAmbitos.apilarAmbito(ambito);
	}
	
	public void ambitoCerrarRutina()
	{
		if(pilaAmbitos.isEmpty())
			System.out.println("ERROR, LA PILA ESTÁ VACÍA");
			
//System.out.println(pilaAmbitos.ambitoActual());			
			
		if(!pilaAmbitos.ambitoActual().getTipo().equals("RUTINA"))
		    System.out.println("ERROR, EL AMBITO ACTUAL NO ES RUTINA");
		else
			pilaAmbitos.desapilarAmbito();
	}
	
	public void ambitoCerrarPrograma()
	{
		if(pilaAmbitos.isEmpty())
			System.out.println("ERROR, LA PILA ESTÁ VACÍA");
			
//System.out.println(pilaAmbitos.ambitoActual());			
			
	    if(!pilaAmbitos.ambitoActual().getTipo().equals("PROGRAMA"))
		    System.out.println("ERROR, EL AMBITO ACTUAL NO ES PROGRAMA");
		else
			pilaAmbitos.desapilarAmbito();				   
	}
	
	///////////////////////////FUNCIONES TIPO B : INSTALACIÓN DE DECLARACIONES////////////////////////////////////
 	public AST creaDeclaracionProcedimiento(AST cabecera_procedimiento, AST dec_const_var, AST cuerpo)
 	{
 		AST arbol=#(#[PROCEDIMIENTO,"procedimiento"], astFactory.dupTree(cabecera_procedimiento), astFactory.dupTree(dec_const_var), astFactory.dupTree(cuerpo));
 		String nombre = arbol.getFirstChild().getFirstChild().getText();
 		
 		Simbolo simbolo = new Simbolo(nombre, arbol);
 		
 		int error = pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
 	
 		if(error==-1)
 		{
 			JOptionPane.showMessageDialog(
								jContentPane,
								nombre+" : ERROR, YA HAY UN SÍMBOLO INSTALADO CON EL MISMO NOMBRE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
 		}
 		
 		return arbol;
	}
	
 	public AST creaDeclaracionFuncion(AST cabecera_funcion, AST dec_const_var, AST cuerpo)
 	{
 		AST arbol=#(#[FUNCION,"funcion"], astFactory.dupTree(cabecera_funcion), astFactory.dupTree(dec_const_var), astFactory.dupTree(cuerpo));
 		String nombre = arbol.getFirstChild().getFirstChild().getText();
 		Simbolo simbolo = new Simbolo(nombre, arbol);
 		
 		int error = pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
 	
 		if(error==-1)
 		{
 			JOptionPane.showMessageDialog(
								jContentPane,
								nombre+" : ERROR, YA HAY UN SÍMBOLO INSTALADO CON EL MISMO NOMBRE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
 		}
 		
 		return arbol;
	}	
 	
 	public AST creaDeclaracionConstante(AST nombreConstante, AST expresion)
 	{
 		String nombre = nombreConstante.getText();
 		
 		AST arbol = #(#[CONSTANTE,"constante"], astFactory.dupTree(nombreConstante), astFactory.dupTree(expresion));
 		
 		Simbolo simbolo = new Simbolo(nombre,arbol);
 		
 		int error = pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
 		
 		if(error==-1)
 		{
 			JOptionPane.showMessageDialog(
								jContentPane,
								nombre+" : ERROR, YA HAY UN SÍMBOLO INSTALADO CON EL MISMO NOMBRE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
 		}
 		
 		return arbol;
 	}
 	
 	 public AST creaDeclaracionVariableResultado(AST nombreVariable, AST tipo)
 	{
 		String nombre = nombreVariable.getText();
 		
 		AST arbol = #(#[VARIABLE_RESULTADO,"variable_resultado"], astFactory.dupTree(nombreVariable), astFactory.dupTree(tipo));
 		
 		Simbolo simbolo = new Simbolo(nombre,arbol);
 		
 		int error = pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
 		
 		if(error==-1)
 		{
 			JOptionPane.showMessageDialog(
								jContentPane,
								nombre+" : ERROR, YA HAY UN SÍMBOLO INSTALADO CON EL MISMO NOMBRE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
 		}
 		
 		return arbol;
 	}
 	
 	public AST creaDeclaracionRegistro(AST nombreRegistro, AST dec_campos)
 	{
 		String nombre = nombreRegistro.getText();
 		
 		AST arbol = #(#[REGISTRO,"registro"], astFactory.dupTree(nombreRegistro), dec_campos);
 		
 		Simbolo simbolo = new Simbolo(nombre,arbol);
 		
 		int error = pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
 		
 		if(error==-1)
 		{
 			JOptionPane.showMessageDialog(
								jContentPane,
								nombre+" : ERROR, YA HAY UN SÍMBOLO INSTALADO CON EL MISMO NOMBRE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
 		}
 		
 		return arbol;
 	}
 	
 	//dado un tipo y una lista de idents de variables/parametros de ese tipo, crea la lista de árboles de declaracion
	AST creaDeclaracionLista(AST tipo, AST listaIdents, AST cabecera)
	{				
		//árbol resultado, será una lista de hermanos con nodo raíz cabecera y 2 hijos (ident y tipo)
		AST result = astFactory.dupTree(cabecera);
		AST ultimoHermano = result;
		
		//crea nodo ident
		AST ident = new CommonAST();
		ident.setText(listaIdents.getText());
		ident.setType(listaIdents.getType());
		
		//añade el nodo tipo
		ident.setNextSibling(astFactory.dupTree(tipo));
		
		//ya tenemos la primera variable/parametro lista
		result.setFirstChild(ident);
		
		//
		String nombre = listaIdents.getText();
 		AST arbol = result;
 		Simbolo simbolo = new Simbolo(nombre,arbol);
 		int error = pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
 	
 		if (error == -1)
 		{
 			JOptionPane.showMessageDialog(
								jContentPane,
								nombre+" : ERROR, YA HAY UN SÍMBOLO INSTALADO CON EL MISMO NOMBRE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
 		 	
 		 	System.exit(1);
 		}
		
		//mientras queden variables/parametros
		while (listaIdents.getNextSibling() != null)
		{
			//construimos su nodo inicial
			AST otraVariable = astFactory.dupTree(cabecera);			
			
			//cogemos la siguiente
			listaIdents = listaIdents.getNextSibling();
			
			//crea nodo ident
			ident = new CommonAST();
			ident.setText(listaIdents.getText());
			ident.setType(listaIdents.getType());
			
			//añade el nodo tipo
			ident.setNextSibling(astFactory.dupTree(tipo));
			
			//ya tenemos otra variable/parametro lista
			otraVariable.setFirstChild(ident);
			
			//
			nombre = listaIdents.getText();
 			arbol = otraVariable;
 			simbolo = new Simbolo(nombre,arbol);
 			
 			error = pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
 	
 			if (error==-1)
 			{
 				JOptionPane.showMessageDialog(
								jContentPane,
								nombre+" : ERROR, YA HAY UN SÍMBOLO INSTALADO CON EL MISMO NOMBRE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
 		 	
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
		AST declaracionAcceso = null;
		AST arbol = null;
		String nombre = ident.getText();
		Simbolo simbolo = pilaAmbitos.buscarSimbolo(nombre);
	
		if(simbolo != null)
			declaracionAcceso = simbolo.getDeclaracion();
		else
		{
			JOptionPane.showMessageDialog(
								jContentPane,
								"ERROR AL RESOLVER SIMBOLO: "+nombre+" NO TIENE DECLARACION ASOCIADA",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
		
			System.exit(1);
		}
	
		ASTFactory ast = new ASTFactory(); //se duplica porque falla el crear elementos de clases p.e. crear(Elemento)
		arbol = #(#[ACCESO_SIMPLE,"acceso_simple"], ident, ast.dupTree(declaracionAcceso)); 
		
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
			JOptionPane.showMessageDialog(
								jContentPane,
								"ERROR, EL TIPO " +nombre+" NO ES UN REGISTRO",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
		
			System.exit(1);
		}
		Simbolo s=amb.getDeclaracion(nombre);
	
		if(s==null)
		{
			JOptionPane.showMessageDialog(
								jContentPane,
								"ERROR, EL TIPO " +nombre+" NO ES UN REGISTRO",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
					System.exit(1);

		}
		ASTFactory ast=new ASTFactory();
	
		return ast.dupTree(s.getDeclaracion());
	}	
}

//PROGRAMA

programa: {ambitoAbrirPrograma();} cabecera declaraciones cuerpo EOF!
	{
		ambitoCerrarPrograma();
	 	#programa = #(#[PROGRAMA, "programa"], ##); 
	}
   ;
 
//CABECERA
 
cabecera: PROGRAM! IDENT PUNTO_Y_COMA!
	;

//DECLARACIONES

declaraciones: dec_constantes dec_registros dec_variables dec_rutinas
	{ #declaraciones = #(#[DECLARACIONES, "declaraciones"], ##); }
	;
	
//DECLARACIÓN DE CONSTANTES
	
dec_constantes: CONST^ (dec_constante)+
	|
	;
	
dec_constante: i:IDENT IGUAL! e:expresion PUNTO_Y_COMA!
	{ #dec_constante = creaDeclaracionConstante(#i, #e);}
	//{ #dec_constante = #(#[CONSTANTE, "constante"], ##); }
	;
	
//DECLARACIÓN DE TIPOS
	
dec_registros: TYPE^ (dec_registro)+
	|
	;
	
dec_registro: i:IDENT IGUAL! RECORD! d:dec_campos END!
	{ #dec_registro = creaDeclaracionRegistro(#i, #d);}
	;
	
dec_campos: (dec_campo)+
	;
	
dec_campo: IDENT DOS_PUNTOS! tipo PUNTO_Y_COMA!
	{ #dec_campo = #(#[CAMPO, "campo"], ##); }
	;
	
//DECLARACIÓN DE VARIABLES
	
dec_variables: VAR^ (dec_lista_variables)+
	|
	;
	
dec_lista_variables! : l:lista_idents DOS_PUNTOS! t:tipo PUNTO_Y_COMA!
	{ //#dec_lista_variables = #(#[VARIABLES_TIPO, "variables_tipo"], #(#[TIPO, "tipo"], #t), #(#[IDENTS, "idents"], #l)); 
		#dec_lista_variables = creaDeclaracionLista(#t, #l, #(#[VARIABLE,"variable"]));
	}	
	;
	
lista_idents: IDENT (COMA! IDENT)*
	;
	
//DECLARACIÓN DE RUTINAS
	
dec_rutinas: (dec_rutina)+ { #dec_rutinas = #(#[RUTINAS, "rutinas"], ##); }
	|	
	;

dec_rutina: dec_procedimiento
	| dec_funcion
	;

dec_procedimiento: ca:cabecera_procedimiento d:dec_const_var cu:cuerpo {ambitoCerrarRutina();}
	//{ #dec_procedimiento = #(#[PROCEDIMIENTO, "procedimiento"], ##); }
	{ #dec_procedimiento = creaDeclaracionProcedimiento(#ca, #d, #cu); }
	;
	
cabecera_procedimiento!: PROCEDURE! i:IDENT {ambitoAbrirRutina(#i);} PARENTESIS_ABIERTO! d:dec_parametros PARENTESIS_CERRADO!
	{ #cabecera_procedimiento = #(#[CABECERA, "cabecera"], #i, #d, #(#[VACIO,"VACIO"])); }
	;

dec_const_var: dec_constantes dec_variables
	{ #dec_const_var = #(#[DECLARACIONES, "declaraciones"], ##); }
	;
	
dec_funcion: ca:cabecera_funcion d:dec_const_var cu:cuerpo {ambitoCerrarRutina();}
	//{ #dec_funcion = #(#[FUNCION, "funcion"], ##); }
	{ #dec_funcion = creaDeclaracionFuncion(#ca, #d, #cu); }
	;
	
cabecera_funcion: FUNCTION! i:IDENT {ambitoAbrirRutina(#i);} PARENTESIS_ABIERTO! dec_parametros PARENTESIS_CERRADO! DOS_PUNTOS! t:tipo
	{creaDeclaracionVariableResultado(#i, #t);}
	{ #cabecera_funcion = #(#[CABECERA, "cabecera"], ##); }
	;
	
dec_parametros: dec_lista_parametros (COMA! dec_lista_parametros)* { #dec_parametros = #(#[PARAMETROS, "parametros"], ##); }
	| { #dec_parametros = #(#[PARAMETROS, "parametros"], ##); }
	;
	
dec_lista_parametros! : l:lista_idents DOS_PUNTOS! t:tipo
	{ //#dec_lista_parametros = #(#[PARAMETROS_TIPO, "parametros_tipo"], #(#[TIPO, "tipo"], #t), #(#[IDENTS, "idents"], #l)); 
		#dec_lista_parametros = creaDeclaracionLista(#t, #l, #(#[PARAMETRO,"parametro"]));
	}	
	;

//CUERPO
	
cuerpo: BEGIN! (instruccion)* END!
	{ #cuerpo = #(#[CUERPO, "cuerpo"], ##); }
	;
	
//EXPRESIONES

expresion: 
   expresion_nivel_1 (OR^ expresion_nivel_1)*  ;
   
expresion_nivel_1: 
   expresion_nivel_2 (AND^ expresion_nivel_2)* ;
   
expresion_nivel_2: 
   NOT^ expresion_nivel_2
   | expresion_nivel_3 ;   

expresion_nivel_3: 
   expresion_nivel_4 
  ((MAYOR^|MAYOR_IGUAL^|MENOR^|MENOR_IGUAL^|IGUAL^|DISTINTO^) 
    expresion_nivel_4)? ;

expresion_nivel_4:
     expresion_nivel_5 ((MAS^|MENOS^) expresion_nivel_5)* ;
   
expresion_nivel_5:
      expresion_nivel_6 ((POR^|DIVISION_ENTERA^|DIVISION_REAL^|MOD^) expresion_nivel_6)* ;

expresion_nivel_6:
	MENOS^ expresion_nivel_6
	| expresion_nivel_7
	;

expresion_nivel_7:
	PARENTESIS_ABIERTO! expresion PARENTESIS_CERRADO!
  | (IDENT PARENTESIS_ABIERTO) => llamada_rutina
  | acceso
  | LIT_ENTERO
  | LIT_REAL
  | LIT_CAR
  | CADENA
  | TRUE
  | FALSE
  ;    
  
 lista_expresiones: expresion (COMA! expresion)* {#lista_expresiones = #(#[EXPRESIONES,"expresiones"],##);}
	|
	;
  
//ACCESOS
  
acceso!: (IDENT PUNTO) => i1:IDENT PUNTO a1:acceso1
		{#acceso = #(#[ACCESO_REGISTRO,"acceso_registro"],ambitoTratarAccesoSimple(#i1),#a1);}
	| (IDENT CORCHETE_ABIERTO indices CORCHETE_CERRADO PUNTO) => i2:IDENT CORCHETE_ABIERTO! ind1:indices CORCHETE_CERRADO! PUNTO! a2:acceso1
		{#acceso = #(#[ACCESO_ARRAY_REGISTRO,"acceso_array_registro"],ambitoTratarAccesoSimple(#i2), #ind1, #a2);}
	| (IDENT CORCHETE_ABIERTO) => i3:IDENT CORCHETE_ABIERTO! ind2:indices CORCHETE_CERRADO! 
		{#acceso = #(#[ACCESO_ARRAY,"acceso_array"],ambitoTratarAccesoSimple(#i3), #ind2);}
	| i:IDENT  
		{#acceso = ambitoTratarAccesoSimple(#i);}
	;
	
acceso1!: (IDENT PUNTO) => i1:IDENT PUNTO a1:acceso1
		{#acceso1 = #(#[ACCESO_REGISTRO,"acceso_registro"],#i1,#a1);}
	| (IDENT CORCHETE_ABIERTO indices CORCHETE_CERRADO PUNTO) => i2:IDENT CORCHETE_ABIERTO! ind1:indices CORCHETE_CERRADO! PUNTO! a2:acceso1
		{#acceso1 = #(#[ACCESO_ARRAY_REGISTRO,"acceso_array_registro"],#i2, #ind1, #a2);}
	| (IDENT CORCHETE_ABIERTO) => i3:IDENT CORCHETE_ABIERTO! ind2:indices CORCHETE_CERRADO! 
		{#acceso1 = #(#[ACCESO_ARRAY,"acceso_array"],#i3, #ind2);}
	| i:IDENT  
		{#acceso1 = #i;}
	;
	
indices: expresion (COMA! expresion)* {#indices = #(#[INDICES,"indices"],##);}
	;

//TIPOS

tipo: simple
	| (expresion RANGO) => tipo_rango
	| definido
	| compuesto
	;
	
tipo_array: simple
	| (expresion RANGO) => tipo_rango
	| definido
	;
	
simple: INTEGER
	| REAL
	| BOOLEAN
	| CHAR	
	;
	
tipo_rango: (LIT_CAR) => rango_caracter
	| rango_entero
	;
	
rango_caracter: LIT_CAR RANGO^ LIT_CAR
	;

rango_entero: expresion RANGO^ expresion
	;

//rango_entero: extremo_rango RANGO^ extremo_rango
	//;
	
extremo_rango: MENOS^ LIT_ENTERO
	| l:LIT_ENTERO	{#extremo_rango = #(#[MAS,"+"], #l);}
	;
	
compuesto: array
	| string
	;
	
definido: i:IDENT 
	{#definido = ambitoTratarIdentTipo(#i);}
	;
	
array: ARRAY^ CORCHETE_ABIERTO! dimensiones_array CORCHETE_CERRADO! OF! tipo_array
	;
	
dimensiones_array: dimension (COMA! dimension)* {#dimensiones_array = #(#[DIMENSIONES,"dimensiones"],##);}
	;
	
dimension: (expresion RANGO) => rango_entero
	| expresion
	;
	
string!: (STRING CORCHETE_ABIERTO) => s:STRING (CORCHETE_ABIERTO l:LIT_ENTERO CORCHETE_CERRADO) {#string = #(s,l);}
	| s2:STRING {#string = #(s2, #[LIT_ENTERO,"255"]);}
	;
	
//INSTRUCCIONES

instruccion: instruccion_simple
	| instruccion_compuesta
	;
	
instruccion_simple: (expresion ASIGNACION) => asignacion
	| instr_llamada_rutina
	| salida
	| entrada
	;
	
instruccion_compuesta: condicional
	| iteracion
	| iteracion_acotada
	;
	
asignacion: expresion ASIGNACION^ expresion PUNTO_Y_COMA!
	;
	
instr_llamada_rutina: llamada_rutina PUNTO_Y_COMA!
	;
	
llamada_rutina!: a:acceso PARENTESIS_ABIERTO! l:lista_expresiones PARENTESIS_CERRADO!
	{#llamada_rutina = #(#[LLAMADA, "llamada"], #a, #l);}
	;
	
salida: WRITE^ PARENTESIS_ABIERTO! lista_expresiones PARENTESIS_CERRADO! PUNTO_Y_COMA!
	;
	
entrada: READ^ PARENTESIS_ABIERTO! expresion PARENTESIS_CERRADO! PUNTO_Y_COMA!
	;
	
condicional: IF^ condicion THEN! bloque_if (ELSE! bloque_else)?
	;
	
condicion: expresion {## = #(#[CONDICION, "condicion"], ##);}
	;
	
bloque_if: bloque {## = #(#[BLOQUE_IF, "bloque_if"], ##);}
	;
	
bloque_else: bloque {## = #(#[BLOQUE_ELSE, "bloque_else"], ##);}
	;
	
iteracion: WHILE^ condicion DO! bloque
	;
	
iteracion_acotada: FOR^ cabecera_for DO! bloque_for
	;
	
bloque_for: bloque {## = #(#[BLOQUE_FOR, "bloque_for"], ##);}
	;
	
cabecera_for: acceso ASIGNACION! expresion (TO | DOWNTO) expresion {## = #(#[CABECERA_FOR, "cabecera_for"], ##);}
	;
	
bloque: instruccion_simple 
	| cuerpo
	;