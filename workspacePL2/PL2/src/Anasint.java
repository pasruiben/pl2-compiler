// $ANTLR : "Anasint.g" -> "Anasint.java"$

	import java.util.*;
	import antlr.*;
	import javax.swing.JOptionPane;
	import javax.swing.JPanel;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

public class Anasint extends antlr.LLkParser       implements AnasintTokenTypes
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
	
	public void ambitoAbrirFuncion(AST nombre)
	{
		if (pilaAmbitos.ambitoActual().getTipo() != "PROGRAMA")
			System.out.println("ERROR, LA PILA DE AMBITOS NO ES CORRECTA");
		
		Ambito ambito = new Ambito(nombre.getText(),"FUNCION",null,pilaAmbitos.ambitoActual());
		pilaAmbitos.apilarAmbito(ambito);
	}
	
	public void ambitoCerrarFuncion()
	{
		if(pilaAmbitos.isEmpty())
			System.out.println("ERROR, LA PILA ESTA VACIA");		
			
			//System.out.println(pilaAmbitos.ambitoActual().getTipo());
			
		if(!pilaAmbitos.ambitoActual().getTipo().equals("FUNCION"))
		    System.out.println("ERROR, EL AMBITO ACTUAL NO ES FUNCION");
		else
			pilaAmbitos.desapilarAmbito();
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
	
	    AST arbol = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(CONSTANTE,"constante")).add(astFactory.dupTree(nombreConstante)).add(astFactory.dupTree(expresion)));
	
	    Simbolo simbolo = new Simbolo(nombre,arbol);
	
		System.out.println("Constante: "+nombre);
	
	    int error = pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
	
	    if(error==-1)
	    {
	            JOptionPane.showMessageDialog(jContentPane, nombre+
	            " : ERROR, YA HAY UN SIMBOLO INSTALADO CON EL MISMO NOMBRE", "Error", JOptionPane.INFORMATION_MESSAGE);
	            System.exit(1);
	    }
	
	    return arbol;
	}
	
	public AST creaDeclaracionRegistro(AST nombreRegistro, AST dec_campos)
    {
	    String nombre = nombreRegistro.getText();
	
	    AST arbol = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DEC_TIPO,"registro")).add(astFactory.dupTree(nombreRegistro)).add(dec_campos));
	
		System.out.println("Registro: "+nombreRegistro);
	
	    Simbolo simbolo = new Simbolo(nombre,arbol);
	
	    int error = pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
	
	    if(error==-1)
	    {
	        JOptionPane.showMessageDialog(jContentPane, nombre+" : ERROR, YA HAY UN SiMBOLO INSTALADO CON EL MISMO NOMBRE",
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
		
		System.out.println("Ultimo hermano: "+ultimoHermano);
		
		//crea nodo var
		AST var = new CommonAST();
		var.setText(listaVars.getText());
		var.setType(listaVars.getType());
		
		System.out.println("Variable: "+var.getText()+ " Tipo: "+var.getType());
		
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
	        JOptionPane.showMessageDialog(jContentPane, nombre+" : ERROR, YA HAY UN SiMBOLO INSTALADO CON EL MISMO NOMBRE",
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
				JOptionPane.showMessageDialog(jContentPane, nombre+" : ERROR, YA HAY UN SiMBOLO INSTALADO CON EL MISMO NOMBRE",
			        								"Error", JOptionPane.INFORMATION_MESSAGE);
				System.exit(1);
			}
	        //que se coloca como hermana en la lista de variables resultado
	        ultimoHermano.setNextSibling(otraVariable);
	
	        ultimoHermano = otraVariable;
        }

        return result;
	}
                
    public AST creaDeclaracionFuncion(AST cabecera_funcion, AST cuerpo)
 	{
 		AST arbol=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(FUNC,"funcion")).add(astFactory.dupTree(cabecera_funcion)).add(astFactory.dupTree(cuerpo)));
 		String nombre = arbol.getFirstChild().getFirstChild().getText();
 		Simbolo simbolo = new Simbolo(nombre, arbol);
 		
 		int error = pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
 	
 		if(error==-1)
 		{
 			JOptionPane.showMessageDialog(
								jContentPane,
								nombre+" : ERROR, YA HAY UN SIMBOLO INSTALADO CON EL MISMO NOMBRE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
 		}
 		
 		return arbol;
	}
	
	public AST creaDeclaracionRetorno(AST nombreVariable, AST tipo)
 	{
 		String nombre = nombreVariable.getText();
 		
 		AST arbol = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(RETORNO,"retorno")).add(astFactory.dupTree(nombreVariable)).add(astFactory.dupTree(tipo)));
 		
 		Simbolo simbolo = new Simbolo(nombre,arbol);
 		
 		int error = pilaAmbitos.ambitoActual().setDeclaracion(simbolo);
 		
 		if(error==-1)
 		{
 			JOptionPane.showMessageDialog(
								jContentPane,
								nombre+" : ERROR, YA HAY UN SIMBOLO INSTALADO CON EL MISMO NOMBRE",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
 		}
 		
 		return arbol;
 	}
	
 
///////////////////////////FUNCIONES TIPO C : TRATAMIENTO ACCESOS SIMPLES/////////////////////////////////		
	
	public AST ambitoTratarAccesoSimple(AST ident)
	{	
		AST declaracionAcceso=null;
		AST arbol=null;
		String nombre=ident.getText();
		Simbolo simbolo=pilaAmbitos.buscarSimbolo(nombre);
		
		System.out.println("Acceso simple: "+nombre);

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
		arbol=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ACCESO_SIMPLE,"acceso_simple")).add(ident).add(ast.dupTree(declaracionAcceso))); 
			
		return arbol;
	}

///////////////////////////FUNCIONES TIPO D : TRATAMIENTO DE IDENTIFICADORES DE TIPO/////////////////////////////	
	
	public AST ambitoTratarIdentTipo(AST ident)
	{
		String nombre=ident.getText();
		Ambito amb=pilaAmbitos.ambitoActual();
		
		System.out.println("Ambito: "+amb.getTipo());
		
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

protected Anasint(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public Anasint(TokenBuffer tokenBuf) {
  this(tokenBuf,3);
}

protected Anasint(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public Anasint(TokenStream lexer) {
  this(lexer,3);
}

public Anasint(ParserSharedInputState state) {
  super(state,3);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void programa() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST programa_AST = null;
		AST d_AST = null;
		AST c_AST = null;
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				ambitoAbrirPrograma();
			}
			declaraciones();
			d_AST = (AST)returnAST;
			cuerpo();
			c_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				programa_AST = (AST)currentAST.root;
				
						ambitoCerrarPrograma();
						programa_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(PROGRAMA,"programa")).add(d_AST).add(c_AST));
					
				currentAST.root = programa_AST;
				currentAST.child = programa_AST!=null &&programa_AST.getFirstChild()!=null ?
					programa_AST.getFirstChild() : programa_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = programa_AST;
	}
	
	public final void declaraciones() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaraciones_AST = null;
		AST c_AST = null;
		AST t_AST = null;
		AST v_AST = null;
		
		try {      // for error handling
			constantes();
			c_AST = (AST)returnAST;
			tipos();
			t_AST = (AST)returnAST;
			variables();
			v_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				declaraciones_AST = (AST)currentAST.root;
				declaraciones_AST = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(DECLARACION,"declaracion")).add(c_AST).add(t_AST).add(v_AST));
				currentAST.root = declaraciones_AST;
				currentAST.child = declaraciones_AST!=null &&declaraciones_AST.getFirstChild()!=null ?
					declaraciones_AST.getFirstChild() : declaraciones_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = declaraciones_AST;
	}
	
	public final void cuerpo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cuerpo_AST = null;
		
		try {      // for error handling
			match(BEGIN);
			instrucciones();
			astFactory.addASTChild(currentAST, returnAST);
			match(END);
			if ( inputState.guessing==0 ) {
				cuerpo_AST = (AST)currentAST.root;
				cuerpo_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CUERPO,"cuerpo")).add(cuerpo_AST));
				currentAST.root = cuerpo_AST;
				currentAST.child = cuerpo_AST!=null &&cuerpo_AST.getFirstChild()!=null ?
					cuerpo_AST.getFirstChild() : cuerpo_AST;
				currentAST.advanceChildToEnd();
			}
			cuerpo_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = cuerpo_AST;
	}
	
	public final void constantes() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constantes_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CONST:
			{
				AST tmp3_AST = null;
				tmp3_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp3_AST);
				match(CONST);
				{
				int _cnt8807=0;
				_loop8807:
				do {
					if ((LA(1)==IDENT)) {
						declaraciones_constantes();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt8807>=1 ) { break _loop8807; } else {throw new NoViableAltException(LT(1), getFilename());}
					}
					
					_cnt8807++;
				} while (true);
				}
				constantes_AST = (AST)currentAST.root;
				break;
			}
			case TYPE:
			case VAR:
			case BEGIN:
			{
				constantes_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			} else {
			  throw ex;
			}
		}
		returnAST = constantes_AST;
	}
	
	public final void tipos() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tipos_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case TYPE:
			{
				AST tmp4_AST = null;
				tmp4_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp4_AST);
				match(TYPE);
				{
				int _cnt8811=0;
				_loop8811:
				do {
					if ((LA(1)==IDENT)) {
						declaraciones_tipos();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt8811>=1 ) { break _loop8811; } else {throw new NoViableAltException(LT(1), getFilename());}
					}
					
					_cnt8811++;
				} while (true);
				}
				tipos_AST = (AST)currentAST.root;
				break;
			}
			case VAR:
			case BEGIN:
			{
				tipos_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_3);
			} else {
			  throw ex;
			}
		}
		returnAST = tipos_AST;
	}
	
	public final void variables() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variables_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case VAR:
			{
				AST tmp5_AST = null;
				tmp5_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp5_AST);
				match(VAR);
				{
				int _cnt8819=0;
				_loop8819:
				do {
					if ((_tokenSet_4.member(LA(1)))) {
						dec_var();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt8819>=1 ) { break _loop8819; } else {throw new NoViableAltException(LT(1), getFilename());}
					}
					
					_cnt8819++;
				} while (true);
				}
				variables_AST = (AST)currentAST.root;
				break;
			}
			case BEGIN:
			{
				variables_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = variables_AST;
	}
	
	public final void declaraciones_constantes() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaraciones_constantes_AST = null;
		Token  i = null;
		AST i_AST = null;
		AST e_AST = null;
		
		try {      // for error handling
			i = LT(1);
			i_AST = astFactory.create(i);
			match(IDENT);
			match(IGUAL);
			expresion();
			e_AST = (AST)returnAST;
			match(PUNTO_Y_COMA);
			if ( inputState.guessing==0 ) {
				declaraciones_constantes_AST = (AST)currentAST.root;
				declaraciones_constantes_AST = creaDeclaracionConstante(i_AST,e_AST);
				currentAST.root = declaraciones_constantes_AST;
				currentAST.child = declaraciones_constantes_AST!=null &&declaraciones_constantes_AST.getFirstChild()!=null ?
					declaraciones_constantes_AST.getFirstChild() : declaraciones_constantes_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		returnAST = declaraciones_constantes_AST;
	}
	
	public final void expresion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_AST = null;
		
		try {      // for error handling
			expresion_nivel_1();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop8886:
			do {
				if ((LA(1)==OR)) {
					AST tmp8_AST = null;
					tmp8_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp8_AST);
					match(OR);
					expresion_nivel_1();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop8886;
				}
				
			} while (true);
			}
			expresion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_6);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_AST;
	}
	
	public final void declaraciones_tipos() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaraciones_tipos_AST = null;
		Token  i = null;
		AST i_AST = null;
		AST c_AST = null;
		
		try {      // for error handling
			i = LT(1);
			i_AST = astFactory.create(i);
			match(IDENT);
			match(IGUAL);
			match(REGISTRO);
			campos();
			c_AST = (AST)returnAST;
			match(END);
			if ( inputState.guessing==0 ) {
				declaraciones_tipos_AST = (AST)currentAST.root;
				declaraciones_tipos_AST = creaDeclaracionRegistro(i_AST, c_AST);
				currentAST.root = declaraciones_tipos_AST;
				currentAST.child = declaraciones_tipos_AST!=null &&declaraciones_tipos_AST.getFirstChild()!=null ?
					declaraciones_tipos_AST.getFirstChild() : declaraciones_tipos_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_7);
			} else {
			  throw ex;
			}
		}
		returnAST = declaraciones_tipos_AST;
	}
	
	public final void campos() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST campos_AST = null;
		
		try {      // for error handling
			{
			int _cnt8815=0;
			_loop8815:
			do {
				if ((_tokenSet_8.member(LA(1)))) {
					campo();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt8815>=1 ) { break _loop8815; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt8815++;
			} while (true);
			}
			campos_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			} else {
			  throw ex;
			}
		}
		returnAST = campos_AST;
	}
	
	public final void campo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST campo_AST = null;
		
		try {      // for error handling
			tipo();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp12_AST = null;
			tmp12_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp12_AST);
			match(IDENT);
			match(PUNTO_Y_COMA);
			if ( inputState.guessing==0 ) {
				campo_AST = (AST)currentAST.root;
				campo_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CAMPO,"campo")).add(campo_AST));
				currentAST.root = campo_AST;
				currentAST.child = campo_AST!=null &&campo_AST.getFirstChild()!=null ?
					campo_AST.getFirstChild() : campo_AST;
				currentAST.advanceChildToEnd();
			}
			campo_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			} else {
			  throw ex;
			}
		}
		returnAST = campo_AST;
	}
	
	public final void tipo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tipo_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ENTERO:
			case REAL:
			case BOOLEANO:
			case CARACTER:
			case MENOS:
			case LIT_ENTERO:
			case LIT_CARACTER:
			{
				tipo_simple();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					tipo_AST = (AST)currentAST.root;
					tipo_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(TIPO,"tipo")).add(tipo_AST));
					currentAST.root = tipo_AST;
					currentAST.child = tipo_AST!=null &&tipo_AST.getFirstChild()!=null ?
						tipo_AST.getFirstChild() : tipo_AST;
					currentAST.advanceChildToEnd();
				}
				tipo_AST = (AST)currentAST.root;
				break;
			}
			case ARRAY:
			case CADENA:
			{
				tipo_compuesto();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					tipo_AST = (AST)currentAST.root;
					tipo_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(TIPO,"tipo")).add(tipo_AST));
					currentAST.root = tipo_AST;
					currentAST.child = tipo_AST!=null &&tipo_AST.getFirstChild()!=null ?
						tipo_AST.getFirstChild() : tipo_AST;
					currentAST.advanceChildToEnd();
				}
				tipo_AST = (AST)currentAST.root;
				break;
			}
			case IDENT:
			{
				i = LT(1);
				i_AST = astFactory.create(i);
				astFactory.addASTChild(currentAST, i_AST);
				match(IDENT);
				if ( inputState.guessing==0 ) {
					tipo_AST = (AST)currentAST.root;
					tipo_AST = ambitoTratarIdentTipo(i_AST);
					currentAST.root = tipo_AST;
					currentAST.child = tipo_AST!=null &&tipo_AST.getFirstChild()!=null ?
						tipo_AST.getFirstChild() : tipo_AST;
					currentAST.advanceChildToEnd();
				}
				tipo_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_11);
			} else {
			  throw ex;
			}
		}
		returnAST = tipo_AST;
	}
	
	public final void dec_var() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_var_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			case ENTERO:
			case REAL:
			case BOOLEANO:
			case CARACTER:
			case MENOS:
			case LIT_ENTERO:
			case LIT_CARACTER:
			case ARRAY:
			case CADENA:
			{
				variable();
				astFactory.addASTChild(currentAST, returnAST);
				match(PUNTO_Y_COMA);
				dec_var_AST = (AST)currentAST.root;
				break;
			}
			case FUNCION:
			{
				funcion();
				astFactory.addASTChild(currentAST, returnAST);
				dec_var_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_12);
			} else {
			  throw ex;
			}
		}
		returnAST = dec_var_AST;
	}
	
	public final void variable() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variable_AST = null;
		AST t_AST = null;
		AST l_AST = null;
		
		try {      // for error handling
			tipo();
			t_AST = (AST)returnAST;
			lista_vars();
			l_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				variable_AST = (AST)currentAST.root;
				variable_AST = creaDeclaracionVariable((AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(VARIABLE,"variable"))),t_AST,l_AST);
				currentAST.root = variable_AST;
				currentAST.child = variable_AST!=null &&variable_AST.getFirstChild()!=null ?
					variable_AST.getFirstChild() : variable_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_13);
			} else {
			  throw ex;
			}
		}
		returnAST = variable_AST;
	}
	
	public final void funcion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST funcion_AST = null;
		AST ca_AST = null;
		AST cu_AST = null;
		
		try {      // for error handling
			cabecera_funcion();
			ca_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			cuerpo_funcion();
			cu_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				ambitoCerrarFuncion();
			}
			if ( inputState.guessing==0 ) {
				funcion_AST = (AST)currentAST.root;
				funcion_AST = creaDeclaracionFuncion(ca_AST,cu_AST);
				currentAST.root = funcion_AST;
				currentAST.child = funcion_AST!=null &&funcion_AST.getFirstChild()!=null ?
					funcion_AST.getFirstChild() : funcion_AST;
				currentAST.advanceChildToEnd();
			}
			funcion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_12);
			} else {
			  throw ex;
			}
		}
		returnAST = funcion_AST;
	}
	
	public final void lista_vars() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_vars_AST = null;
		
		try {      // for error handling
			AST tmp15_AST = null;
			tmp15_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp15_AST);
			match(IDENT);
			{
			_loop8824:
			do {
				if ((LA(1)==COMA)) {
					match(COMA);
					AST tmp17_AST = null;
					tmp17_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp17_AST);
					match(IDENT);
				}
				else {
					break _loop8824;
				}
				
			} while (true);
			}
			lista_vars_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_13);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_vars_AST;
	}
	
	public final void cabecera_funcion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cabecera_funcion_AST = null;
		AST t_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			match(FUNCION);
			{
			if ((_tokenSet_8.member(LA(1))) && (_tokenSet_14.member(LA(2)))) {
				tipo();
				t_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==IDENT) && (LA(2)==PARENTESIS_ABIERTO)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			i = LT(1);
			i_AST = astFactory.create(i);
			astFactory.addASTChild(currentAST, i_AST);
			match(IDENT);
			if ( inputState.guessing==0 ) {
				ambitoAbrirFuncion(i_AST);
			}
			match(PARENTESIS_ABIERTO);
			{
			_loop8829:
			do {
				if ((_tokenSet_8.member(LA(1)))) {
					declaraciones_parametros();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop8829;
				}
				
			} while (true);
			}
			match(PARENTESIS_CERRADO);
			if ( inputState.guessing==0 ) {
				creaDeclaracionRetorno(i_AST, t_AST);
			}
			if ( inputState.guessing==0 ) {
				cabecera_funcion_AST = (AST)currentAST.root;
				cabecera_funcion_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CABECERA,"cabecera")).add(cabecera_funcion_AST));
				currentAST.root = cabecera_funcion_AST;
				currentAST.child = cabecera_funcion_AST!=null &&cabecera_funcion_AST.getFirstChild()!=null ?
					cabecera_funcion_AST.getFirstChild() : cabecera_funcion_AST;
				currentAST.advanceChildToEnd();
			}
			cabecera_funcion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
		returnAST = cabecera_funcion_AST;
	}
	
	public final void cuerpo_funcion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cuerpo_funcion_AST = null;
		
		try {      // for error handling
			instrucciones();
			astFactory.addASTChild(currentAST, returnAST);
			match(END);
			if ( inputState.guessing==0 ) {
				cuerpo_funcion_AST = (AST)currentAST.root;
				cuerpo_funcion_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CUERPO,"cuerpo")).add(cuerpo_funcion_AST));
				currentAST.root = cuerpo_funcion_AST;
				currentAST.child = cuerpo_funcion_AST!=null &&cuerpo_funcion_AST.getFirstChild()!=null ?
					cuerpo_funcion_AST.getFirstChild() : cuerpo_funcion_AST;
				currentAST.advanceChildToEnd();
			}
			cuerpo_funcion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_12);
			} else {
			  throw ex;
			}
		}
		returnAST = cuerpo_funcion_AST;
	}
	
	public final void declaraciones_parametros() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaraciones_parametros_AST = null;
		
		try {      // for error handling
			tipo();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp22_AST = null;
			tmp22_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp22_AST);
			match(IDENT);
			{
			_loop8833:
			do {
				if ((LA(1)==COMA)) {
					match(COMA);
					tipo();
					astFactory.addASTChild(currentAST, returnAST);
					AST tmp24_AST = null;
					tmp24_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp24_AST);
					match(IDENT);
				}
				else {
					break _loop8833;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				declaraciones_parametros_AST = (AST)currentAST.root;
				declaraciones_parametros_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGUMENTOS,"argumentos")).add(declaraciones_parametros_AST));
				currentAST.root = declaraciones_parametros_AST;
				currentAST.child = declaraciones_parametros_AST!=null &&declaraciones_parametros_AST.getFirstChild()!=null ?
					declaraciones_parametros_AST.getFirstChild() : declaraciones_parametros_AST;
				currentAST.advanceChildToEnd();
			}
			declaraciones_parametros_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_16);
			} else {
			  throw ex;
			}
		}
		returnAST = declaraciones_parametros_AST;
	}
	
	public final void instrucciones() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instrucciones_AST = null;
		
		try {      // for error handling
			{
			_loop8854:
			do {
				if ((_tokenSet_17.member(LA(1)))) {
					instruccion();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop8854;
				}
				
			} while (true);
			}
			instrucciones_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			} else {
			  throw ex;
			}
		}
		returnAST = instrucciones_AST;
	}
	
	public final void tipo_simple() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tipo_simple_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case MENOS:
			case LIT_ENTERO:
			case LIT_CARACTER:
			{
				rango();
				astFactory.addASTChild(currentAST, returnAST);
				tipo_simple_AST = (AST)currentAST.root;
				break;
			}
			case ENTERO:
			{
				AST tmp25_AST = null;
				tmp25_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp25_AST);
				match(ENTERO);
				tipo_simple_AST = (AST)currentAST.root;
				break;
			}
			case REAL:
			{
				AST tmp26_AST = null;
				tmp26_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp26_AST);
				match(REAL);
				tipo_simple_AST = (AST)currentAST.root;
				break;
			}
			case BOOLEANO:
			{
				AST tmp27_AST = null;
				tmp27_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp27_AST);
				match(BOOLEANO);
				tipo_simple_AST = (AST)currentAST.root;
				break;
			}
			case CARACTER:
			{
				AST tmp28_AST = null;
				tmp28_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp28_AST);
				match(CARACTER);
				tipo_simple_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_11);
			} else {
			  throw ex;
			}
		}
		returnAST = tipo_simple_AST;
	}
	
	public final void tipo_compuesto() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tipo_compuesto_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ARRAY:
			{
				array();
				astFactory.addASTChild(currentAST, returnAST);
				tipo_compuesto_AST = (AST)currentAST.root;
				break;
			}
			case CADENA:
			{
				cadena();
				astFactory.addASTChild(currentAST, returnAST);
				tipo_compuesto_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_11);
			} else {
			  throw ex;
			}
		}
		returnAST = tipo_compuesto_AST;
	}
	
	public final void rango() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rango_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case MENOS:
			case LIT_ENTERO:
			{
				{
				switch ( LA(1)) {
				case MENOS:
				{
					AST tmp29_AST = null;
					tmp29_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp29_AST);
					match(MENOS);
					break;
				}
				case LIT_ENTERO:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				AST tmp30_AST = null;
				tmp30_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp30_AST);
				match(LIT_ENTERO);
				match(PUNTO);
				match(PUNTO);
				{
				switch ( LA(1)) {
				case MENOS:
				{
					AST tmp33_AST = null;
					tmp33_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp33_AST);
					match(MENOS);
					break;
				}
				case LIT_ENTERO:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				AST tmp34_AST = null;
				tmp34_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp34_AST);
				match(LIT_ENTERO);
				if ( inputState.guessing==0 ) {
					rango_AST = (AST)currentAST.root;
					rango_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(RANGO_ENTERO,"rango_entero")).add(rango_AST));
					currentAST.root = rango_AST;
					currentAST.child = rango_AST!=null &&rango_AST.getFirstChild()!=null ?
						rango_AST.getFirstChild() : rango_AST;
					currentAST.advanceChildToEnd();
				}
				rango_AST = (AST)currentAST.root;
				break;
			}
			case LIT_CARACTER:
			{
				AST tmp35_AST = null;
				tmp35_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp35_AST);
				match(LIT_CARACTER);
				match(PUNTO);
				match(PUNTO);
				AST tmp38_AST = null;
				tmp38_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp38_AST);
				match(LIT_CARACTER);
				if ( inputState.guessing==0 ) {
					rango_AST = (AST)currentAST.root;
					rango_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(RANGO_CARACTER,"rango_caracter")).add(rango_AST));
					currentAST.root = rango_AST;
					currentAST.child = rango_AST!=null &&rango_AST.getFirstChild()!=null ?
						rango_AST.getFirstChild() : rango_AST;
					currentAST.advanceChildToEnd();
				}
				rango_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_18);
			} else {
			  throw ex;
			}
		}
		returnAST = rango_AST;
	}
	
	public final void array() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST array_AST = null;
		
		try {      // for error handling
			AST tmp39_AST = null;
			tmp39_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp39_AST);
			match(ARRAY);
			declaracion_array();
			astFactory.addASTChild(currentAST, returnAST);
			array_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_11);
			} else {
			  throw ex;
			}
		}
		returnAST = array_AST;
	}
	
	public final void cadena() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cadena_AST = null;
		
		try {      // for error handling
			AST tmp40_AST = null;
			tmp40_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp40_AST);
			match(CADENA);
			declaracion_cadena();
			astFactory.addASTChild(currentAST, returnAST);
			cadena_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_11);
			} else {
			  throw ex;
			}
		}
		returnAST = cadena_AST;
	}
	
	public final void declaracion_array() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_array_AST = null;
		
		try {      // for error handling
			match(CORCHETE_ABIERTO);
			indices();
			astFactory.addASTChild(currentAST, returnAST);
			match(CORCHETE_CERRADO);
			match(OF);
			tipo_simple();
			astFactory.addASTChild(currentAST, returnAST);
			declaracion_array_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_11);
			} else {
			  throw ex;
			}
		}
		returnAST = declaracion_array_AST;
	}
	
	public final void indices() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST indices_AST = null;
		
		try {      // for error handling
			indice();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop8845:
			do {
				if ((LA(1)==COMA)) {
					match(COMA);
					indice();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop8845;
				}
				
			} while (true);
			}
			indices_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_19);
			} else {
			  throw ex;
			}
		}
		returnAST = indices_AST;
	}
	
	public final void indice() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST indice_AST = null;
		
		try {      // for error handling
			boolean synPredMatched8849 = false;
			if (((LA(1)==MENOS||LA(1)==LIT_ENTERO||LA(1)==LIT_CARACTER) && (LA(2)==LIT_ENTERO||LA(2)==PUNTO) && (LA(3)==PUNTO))) {
				int _m8849 = mark();
				synPredMatched8849 = true;
				inputState.guessing++;
				try {
					{
					{
					switch ( LA(1)) {
					case MENOS:
					{
						match(MENOS);
						break;
					}
					case LIT_ENTERO:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					match(LIT_ENTERO);
					match(PUNTO);
					match(PUNTO);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched8849 = false;
				}
				rewind(_m8849);
inputState.guessing--;
			}
			if ( synPredMatched8849 ) {
				rango();
				astFactory.addASTChild(currentAST, returnAST);
				indice_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_20.member(LA(1))) && (_tokenSet_21.member(LA(2))) && (_tokenSet_22.member(LA(3)))) {
				expresion();
				astFactory.addASTChild(currentAST, returnAST);
				indice_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_23);
			} else {
			  throw ex;
			}
		}
		returnAST = indice_AST;
	}
	
	public final void declaracion_cadena() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_cadena_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CORCHETE_ABIERTO:
			{
				match(CORCHETE_ABIERTO);
				indice();
				astFactory.addASTChild(currentAST, returnAST);
				match(CORCHETE_CERRADO);
				declaracion_cadena_AST = (AST)currentAST.root;
				break;
			}
			case IDENT:
			{
				declaracion_cadena_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_11);
			} else {
			  throw ex;
			}
		}
		returnAST = declaracion_cadena_AST;
	}
	
	public final void instruccion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instruccion_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			case PARENTESIS_ABIERTO:
			case ENTERO:
			case REAL:
			case BOOLEANO:
			case CARACTER:
			case MENOS:
			case LIT_ENTERO:
			case LIT_CARACTER:
			case ARRAY:
			case CADENA:
			case LEER:
			case ESCRIBIR:
			case NOT:
			case LIT_REAL:
			case CIERTO:
			case FALSO:
			{
				instruccion_simple();
				astFactory.addASTChild(currentAST, returnAST);
				match(PUNTO_Y_COMA);
				if ( inputState.guessing==0 ) {
					instruccion_AST = (AST)currentAST.root;
					instruccion_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INSTRUCCION,"instruccion")).add(instruccion_AST));
					currentAST.root = instruccion_AST;
					currentAST.child = instruccion_AST!=null &&instruccion_AST.getFirstChild()!=null ?
						instruccion_AST.getFirstChild() : instruccion_AST;
					currentAST.advanceChildToEnd();
				}
				instruccion_AST = (AST)currentAST.root;
				break;
			}
			case SI:
			case MIENTRAS:
			case DESDE:
			{
				instruccion_compuesta();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					instruccion_AST = (AST)currentAST.root;
					instruccion_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INSTRUCCION,"instruccion")).add(instruccion_AST));
					currentAST.root = instruccion_AST;
					currentAST.child = instruccion_AST!=null &&instruccion_AST.getFirstChild()!=null ?
						instruccion_AST.getFirstChild() : instruccion_AST;
					currentAST.advanceChildToEnd();
				}
				instruccion_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
		returnAST = instruccion_AST;
	}
	
	public final void instruccion_simple() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instruccion_simple_AST = null;
		
		try {      // for error handling
			boolean synPredMatched8858 = false;
			if (((LA(1)==IDENT) && (LA(2)==IGUAL) && (_tokenSet_20.member(LA(3))))) {
				int _m8858 = mark();
				synPredMatched8858 = true;
				inputState.guessing++;
				try {
					{
					match(IDENT);
					match(IGUAL);
					expresion();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched8858 = false;
				}
				rewind(_m8858);
inputState.guessing--;
			}
			if ( synPredMatched8858 ) {
				declaraciones_constantes();
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_simple_AST = (AST)currentAST.root;
			}
			else {
				boolean synPredMatched8860 = false;
				if (((_tokenSet_8.member(LA(1))) && (_tokenSet_14.member(LA(2))) && (_tokenSet_24.member(LA(3))))) {
					int _m8860 = mark();
					synPredMatched8860 = true;
					inputState.guessing++;
					try {
						{
						tipo();
						match(IDENT);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched8860 = false;
					}
					rewind(_m8860);
inputState.guessing--;
				}
				if ( synPredMatched8860 ) {
					variable();
					astFactory.addASTChild(currentAST, returnAST);
					instruccion_simple_AST = (AST)currentAST.root;
				}
				else {
					boolean synPredMatched8862 = false;
					if (((_tokenSet_20.member(LA(1))) && (_tokenSet_25.member(LA(2))) && (_tokenSet_26.member(LA(3))))) {
						int _m8862 = mark();
						synPredMatched8862 = true;
						inputState.guessing++;
						try {
							{
							expresion();
							match(ASIGNACION);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched8862 = false;
						}
						rewind(_m8862);
inputState.guessing--;
					}
					if ( synPredMatched8862 ) {
						asignacion();
						astFactory.addASTChild(currentAST, returnAST);
						instruccion_simple_AST = (AST)currentAST.root;
					}
					else if ((LA(1)==IDENT||LA(1)==LEER||LA(1)==ESCRIBIR) && (LA(2)==PARENTESIS_ABIERTO||LA(2)==PUNTO||LA(2)==CORCHETE_ABIERTO) && (_tokenSet_27.member(LA(3)))) {
						llamada_rutina();
						astFactory.addASTChild(currentAST, returnAST);
						instruccion_simple_AST = (AST)currentAST.root;
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					}}
				}
				catch (RecognitionException ex) {
					if (inputState.guessing==0) {
						reportError(ex);
						recover(ex,_tokenSet_13);
					} else {
					  throw ex;
					}
				}
				returnAST = instruccion_simple_AST;
			}
			
	public final void instruccion_compuesta() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instruccion_compuesta_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case SI:
			{
				condicional();
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_compuesta_AST = (AST)currentAST.root;
				break;
			}
			case MIENTRAS:
			{
				iteracion();
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_compuesta_AST = (AST)currentAST.root;
				break;
			}
			case DESDE:
			{
				iteracion_acotada();
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_compuesta_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
		returnAST = instruccion_compuesta_AST;
	}
	
	public final void asignacion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST asignacion_AST = null;
		AST e1_AST = null;
		AST e2_AST = null;
		
		try {      // for error handling
			expresion();
			e1_AST = (AST)returnAST;
			AST tmp48_AST = null;
			tmp48_AST = astFactory.create(LT(1));
			match(ASIGNACION);
			expresion();
			e2_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				asignacion_AST = (AST)currentAST.root;
				asignacion_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ASIG,"asignacion")).add(e1_AST).add(e2_AST));
				currentAST.root = asignacion_AST;
				currentAST.child = asignacion_AST!=null &&asignacion_AST.getFirstChild()!=null ?
					asignacion_AST.getFirstChild() : asignacion_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_28);
			} else {
			  throw ex;
			}
		}
		returnAST = asignacion_AST;
	}
	
	public final void llamada_rutina() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST llamada_rutina_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			{
				acceso();
				astFactory.addASTChild(currentAST, returnAST);
				match(PARENTESIS_ABIERTO);
				lista_expresiones();
				astFactory.addASTChild(currentAST, returnAST);
				match(PARENTESIS_CERRADO);
				if ( inputState.guessing==0 ) {
					llamada_rutina_AST = (AST)currentAST.root;
					llamada_rutina_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(LLAMADA,"llamada")).add(llamada_rutina_AST));
					currentAST.root = llamada_rutina_AST;
					currentAST.child = llamada_rutina_AST!=null &&llamada_rutina_AST.getFirstChild()!=null ?
						llamada_rutina_AST.getFirstChild() : llamada_rutina_AST;
					currentAST.advanceChildToEnd();
				}
				llamada_rutina_AST = (AST)currentAST.root;
				break;
			}
			case LEER:
			case ESCRIBIR:
			{
				rutina_especial();
				astFactory.addASTChild(currentAST, returnAST);
				llamada_rutina_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_29);
			} else {
			  throw ex;
			}
		}
		returnAST = llamada_rutina_AST;
	}
	
	public final void condicional() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST condicional_AST = null;
		
		try {      // for error handling
			AST tmp51_AST = null;
			tmp51_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp51_AST);
			match(SI);
			expresion();
			astFactory.addASTChild(currentAST, returnAST);
			contenido();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case SINO:
			{
				AST tmp52_AST = null;
				tmp52_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp52_AST);
				match(SINO);
				contenido();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case IDENT:
			case END:
			case PARENTESIS_ABIERTO:
			case ENTERO:
			case REAL:
			case BOOLEANO:
			case CARACTER:
			case MENOS:
			case LIT_ENTERO:
			case LIT_CARACTER:
			case ARRAY:
			case CADENA:
			case LEER:
			case ESCRIBIR:
			case SI:
			case MIENTRAS:
			case DESDE:
			case NOT:
			case LIT_REAL:
			case CIERTO:
			case FALSO:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			condicional_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
		returnAST = condicional_AST;
	}
	
	public final void iteracion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iteracion_AST = null;
		
		try {      // for error handling
			AST tmp53_AST = null;
			tmp53_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp53_AST);
			match(MIENTRAS);
			expresion();
			astFactory.addASTChild(currentAST, returnAST);
			match(HACER);
			contenido();
			astFactory.addASTChild(currentAST, returnAST);
			iteracion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
		returnAST = iteracion_AST;
	}
	
	public final void iteracion_acotada() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iteracion_acotada_AST = null;
		
		try {      // for error handling
			AST tmp55_AST = null;
			tmp55_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp55_AST);
			match(DESDE);
			asignacion();
			astFactory.addASTChild(currentAST, returnAST);
			iteracion_tipo();
			astFactory.addASTChild(currentAST, returnAST);
			iteracion_acotada_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
		returnAST = iteracion_acotada_AST;
	}
	
	public final void acceso() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_AST = null;
		Token  i1 = null;
		AST i1_AST = null;
		AST a1_AST = null;
		Token  i2 = null;
		AST i2_AST = null;
		AST ind1_AST = null;
		AST a2_AST = null;
		Token  i3 = null;
		AST i3_AST = null;
		AST ind2_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			boolean synPredMatched8908 = false;
			if (((LA(1)==IDENT) && (LA(2)==PUNTO))) {
				int _m8908 = mark();
				synPredMatched8908 = true;
				inputState.guessing++;
				try {
					{
					match(IDENT);
					match(PUNTO);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched8908 = false;
				}
				rewind(_m8908);
inputState.guessing--;
			}
			if ( synPredMatched8908 ) {
				i1 = LT(1);
				i1_AST = astFactory.create(i1);
				match(IDENT);
				AST tmp56_AST = null;
				tmp56_AST = astFactory.create(LT(1));
				match(PUNTO);
				acceso1();
				a1_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					acceso_AST = (AST)currentAST.root;
					acceso_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ACCESO_REGISTRO,"acceso_registro")).add(ambitoTratarAccesoSimple(i1_AST)).add(a1_AST));
					currentAST.root = acceso_AST;
					currentAST.child = acceso_AST!=null &&acceso_AST.getFirstChild()!=null ?
						acceso_AST.getFirstChild() : acceso_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else {
				boolean synPredMatched8910 = false;
				if (((LA(1)==IDENT) && (LA(2)==CORCHETE_ABIERTO) && (_tokenSet_20.member(LA(3))))) {
					int _m8910 = mark();
					synPredMatched8910 = true;
					inputState.guessing++;
					try {
						{
						match(IDENT);
						match(CORCHETE_ABIERTO);
						indices();
						match(CORCHETE_CERRADO);
						match(PUNTO);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched8910 = false;
					}
					rewind(_m8910);
inputState.guessing--;
				}
				if ( synPredMatched8910 ) {
					i2 = LT(1);
					i2_AST = astFactory.create(i2);
					match(IDENT);
					match(CORCHETE_ABIERTO);
					indices();
					ind1_AST = (AST)returnAST;
					match(CORCHETE_CERRADO);
					match(PUNTO);
					acceso();
					a2_AST = (AST)returnAST;
					if ( inputState.guessing==0 ) {
						acceso_AST = (AST)currentAST.root;
						acceso_AST = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(ACCESO_ARRAY_REGISTRO,"acceso_array_registro")).add(ambitoTratarAccesoSimple(i2_AST)).add(ind1_AST).add(a2_AST));
						currentAST.root = acceso_AST;
						currentAST.child = acceso_AST!=null &&acceso_AST.getFirstChild()!=null ?
							acceso_AST.getFirstChild() : acceso_AST;
						currentAST.advanceChildToEnd();
					}
				}
				else {
					boolean synPredMatched8912 = false;
					if (((LA(1)==IDENT) && (LA(2)==CORCHETE_ABIERTO) && (_tokenSet_20.member(LA(3))))) {
						int _m8912 = mark();
						synPredMatched8912 = true;
						inputState.guessing++;
						try {
							{
							match(IDENT);
							match(CORCHETE_ABIERTO);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched8912 = false;
						}
						rewind(_m8912);
inputState.guessing--;
					}
					if ( synPredMatched8912 ) {
						i3 = LT(1);
						i3_AST = astFactory.create(i3);
						match(IDENT);
						match(CORCHETE_ABIERTO);
						indices();
						ind2_AST = (AST)returnAST;
						match(CORCHETE_CERRADO);
						if ( inputState.guessing==0 ) {
							acceso_AST = (AST)currentAST.root;
							acceso_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ACCESO_ARRAY,"acceso_array")).add(ambitoTratarAccesoSimple(i3_AST)).add(ind2_AST));
							currentAST.root = acceso_AST;
							currentAST.child = acceso_AST!=null &&acceso_AST.getFirstChild()!=null ?
								acceso_AST.getFirstChild() : acceso_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else if ((LA(1)==IDENT) && (_tokenSet_29.member(LA(2)))) {
						i = LT(1);
						i_AST = astFactory.create(i);
						match(IDENT);
						if ( inputState.guessing==0 ) {
							acceso_AST = (AST)currentAST.root;
							acceso_AST = ambitoTratarAccesoSimple(i_AST);
							currentAST.root = acceso_AST;
							currentAST.child = acceso_AST!=null &&acceso_AST.getFirstChild()!=null ?
								acceso_AST.getFirstChild() : acceso_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					}}
				}
				catch (RecognitionException ex) {
					if (inputState.guessing==0) {
						reportError(ex);
						recover(ex,_tokenSet_29);
					} else {
					  throw ex;
					}
				}
				returnAST = acceso_AST;
			}
			
	public final void lista_expresiones() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_expresiones_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			case PARENTESIS_ABIERTO:
			case MENOS:
			case LIT_ENTERO:
			case LIT_CARACTER:
			case CADENA:
			case LEER:
			case ESCRIBIR:
			case NOT:
			case LIT_REAL:
			case CIERTO:
			case FALSO:
			{
				expresion();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop8868:
				do {
					if ((LA(1)==COMA)) {
						match(COMA);
						expresion();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop8868;
					}
					
				} while (true);
				}
				if ( inputState.guessing==0 ) {
					lista_expresiones_AST = (AST)currentAST.root;
					lista_expresiones_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(LISTA_EXPRESIONES,"lista_expresiones")).add(lista_expresiones_AST));
					currentAST.root = lista_expresiones_AST;
					currentAST.child = lista_expresiones_AST!=null &&lista_expresiones_AST.getFirstChild()!=null ?
						lista_expresiones_AST.getFirstChild() : lista_expresiones_AST;
					currentAST.advanceChildToEnd();
				}
				lista_expresiones_AST = (AST)currentAST.root;
				break;
			}
			case PARENTESIS_CERRADO:
			{
				lista_expresiones_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_30);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_expresiones_AST;
	}
	
	public final void rutina_especial() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rutina_especial_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LEER:
			{
				AST tmp63_AST = null;
				tmp63_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp63_AST);
				match(LEER);
				match(PARENTESIS_ABIERTO);
				expresion();
				astFactory.addASTChild(currentAST, returnAST);
				match(PARENTESIS_CERRADO);
				rutina_especial_AST = (AST)currentAST.root;
				break;
			}
			case ESCRIBIR:
			{
				AST tmp66_AST = null;
				tmp66_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp66_AST);
				match(ESCRIBIR);
				match(PARENTESIS_ABIERTO);
				expresion();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop8871:
				do {
					if ((LA(1)==COMA)) {
						match(COMA);
						expresion();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop8871;
					}
					
				} while (true);
				}
				match(PARENTESIS_CERRADO);
				rutina_especial_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_29);
			} else {
			  throw ex;
			}
		}
		returnAST = rutina_especial_AST;
	}
	
	public final void contenido() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST contenido_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			case PARENTESIS_ABIERTO:
			case ENTERO:
			case REAL:
			case BOOLEANO:
			case CARACTER:
			case MENOS:
			case LIT_ENTERO:
			case LIT_CARACTER:
			case ARRAY:
			case CADENA:
			case LEER:
			case ESCRIBIR:
			case NOT:
			case LIT_REAL:
			case CIERTO:
			case FALSO:
			{
				instruccion_simple();
				astFactory.addASTChild(currentAST, returnAST);
				match(PUNTO_Y_COMA);
				if ( inputState.guessing==0 ) {
					contenido_AST = (AST)currentAST.root;
					contenido_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CONTENIDO,"contenido")).add(contenido_AST));
					currentAST.root = contenido_AST;
					currentAST.child = contenido_AST!=null &&contenido_AST.getFirstChild()!=null ?
						contenido_AST.getFirstChild() : contenido_AST;
					currentAST.advanceChildToEnd();
				}
				contenido_AST = (AST)currentAST.root;
				break;
			}
			case BEGIN:
			{
				bloque();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					contenido_AST = (AST)currentAST.root;
					contenido_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CONTENIDO,"contenido")).add(contenido_AST));
					currentAST.root = contenido_AST;
					currentAST.child = contenido_AST!=null &&contenido_AST.getFirstChild()!=null ?
						contenido_AST.getFirstChild() : contenido_AST;
					currentAST.advanceChildToEnd();
				}
				contenido_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_31);
			} else {
			  throw ex;
			}
		}
		returnAST = contenido_AST;
	}
	
	public final void bloque() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST bloque_AST = null;
		
		try {      // for error handling
			match(BEGIN);
			instrucciones();
			astFactory.addASTChild(currentAST, returnAST);
			match(END);
			bloque_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_31);
			} else {
			  throw ex;
			}
		}
		returnAST = bloque_AST;
	}
	
	public final void iteracion_tipo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iteracion_tipo_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case HASTA:
			{
				match(HASTA);
				resto_iteracion();
				astFactory.addASTChild(currentAST, returnAST);
				iteracion_tipo_AST = (AST)currentAST.root;
				break;
			}
			case ATRAS:
			{
				match(ATRAS);
				resto_iteracion();
				astFactory.addASTChild(currentAST, returnAST);
				iteracion_tipo_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
		returnAST = iteracion_tipo_AST;
	}
	
	public final void resto_iteracion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST resto_iteracion_AST = null;
		
		try {      // for error handling
			valor_final();
			astFactory.addASTChild(currentAST, returnAST);
			match(HACER);
			contenido();
			astFactory.addASTChild(currentAST, returnAST);
			resto_iteracion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
		returnAST = resto_iteracion_AST;
	}
	
	public final void valor_final() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST valor_final_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			{
				AST tmp76_AST = null;
				tmp76_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp76_AST);
				match(IDENT);
				valor_final_AST = (AST)currentAST.root;
				break;
			}
			case LIT_ENTERO:
			{
				AST tmp77_AST = null;
				tmp77_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp77_AST);
				match(LIT_ENTERO);
				valor_final_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_32);
			} else {
			  throw ex;
			}
		}
		returnAST = valor_final_AST;
	}
	
	public final void expresiones() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresiones_AST = null;
		
		try {      // for error handling
			{
			int _cnt8883=0;
			_loop8883:
			do {
				if ((_tokenSet_20.member(LA(1)))) {
					expresion();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt8883>=1 ) { break _loop8883; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt8883++;
			} while (true);
			}
			expresiones_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = expresiones_AST;
	}
	
	public final void expresion_nivel_1() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_1_AST = null;
		
		try {      // for error handling
			expresion_nivel_2();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop8889:
			do {
				if ((LA(1)==AND)) {
					AST tmp78_AST = null;
					tmp78_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp78_AST);
					match(AND);
					expresion_nivel_2();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop8889;
				}
				
			} while (true);
			}
			expresion_nivel_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_33);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_nivel_1_AST;
	}
	
	public final void expresion_nivel_2() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_2_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NOT:
			{
				AST tmp79_AST = null;
				tmp79_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp79_AST);
				match(NOT);
				expresion_nivel_2();
				astFactory.addASTChild(currentAST, returnAST);
				expresion_nivel_2_AST = (AST)currentAST.root;
				break;
			}
			case IDENT:
			case PARENTESIS_ABIERTO:
			case MENOS:
			case LIT_ENTERO:
			case LIT_CARACTER:
			case CADENA:
			case LEER:
			case ESCRIBIR:
			case LIT_REAL:
			case CIERTO:
			case FALSO:
			{
				expresion_nivel_3();
				astFactory.addASTChild(currentAST, returnAST);
				expresion_nivel_2_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_34);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_nivel_2_AST;
	}
	
	public final void expresion_nivel_3() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_3_AST = null;
		
		try {      // for error handling
			expresion_nivel_4();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case IGUAL:
			case MAYOR:
			case MAYOR_IGUAL:
			case MENOR:
			case MENOR_IGUAL:
			case DISTINTO:
			{
				{
				switch ( LA(1)) {
				case MAYOR:
				{
					AST tmp80_AST = null;
					tmp80_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp80_AST);
					match(MAYOR);
					break;
				}
				case MAYOR_IGUAL:
				{
					AST tmp81_AST = null;
					tmp81_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp81_AST);
					match(MAYOR_IGUAL);
					break;
				}
				case MENOR:
				{
					AST tmp82_AST = null;
					tmp82_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp82_AST);
					match(MENOR);
					break;
				}
				case MENOR_IGUAL:
				{
					AST tmp83_AST = null;
					tmp83_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp83_AST);
					match(MENOR_IGUAL);
					break;
				}
				case IGUAL:
				{
					AST tmp84_AST = null;
					tmp84_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp84_AST);
					match(IGUAL);
					break;
				}
				case DISTINTO:
				{
					AST tmp85_AST = null;
					tmp85_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp85_AST);
					match(DISTINTO);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				expresion_nivel_4();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case IDENT:
			case PUNTO_Y_COMA:
			case COMA:
			case PARENTESIS_ABIERTO:
			case PARENTESIS_CERRADO:
			case BEGIN:
			case ENTERO:
			case REAL:
			case BOOLEANO:
			case CARACTER:
			case MENOS:
			case LIT_ENTERO:
			case LIT_CARACTER:
			case ARRAY:
			case CORCHETE_CERRADO:
			case CADENA:
			case ASIGNACION:
			case LEER:
			case ESCRIBIR:
			case HACER:
			case HASTA:
			case ATRAS:
			case OR:
			case AND:
			case NOT:
			case LIT_REAL:
			case CIERTO:
			case FALSO:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			expresion_nivel_3_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_34);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_nivel_3_AST;
	}
	
	public final void expresion_nivel_4() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_4_AST = null;
		
		try {      // for error handling
			expresion_nivel_5();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop8897:
			do {
				if ((LA(1)==MENOS||LA(1)==MAS) && (_tokenSet_35.member(LA(2))) && (_tokenSet_36.member(LA(3)))) {
					{
					switch ( LA(1)) {
					case MAS:
					{
						AST tmp86_AST = null;
						tmp86_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp86_AST);
						match(MAS);
						break;
					}
					case MENOS:
					{
						AST tmp87_AST = null;
						tmp87_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp87_AST);
						match(MENOS);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					expresion_nivel_5();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop8897;
				}
				
			} while (true);
			}
			expresion_nivel_4_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_37);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_nivel_4_AST;
	}
	
	public final void expresion_nivel_5() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_5_AST = null;
		
		try {      // for error handling
			expresion_nivel_6();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop8901:
			do {
				if ((LA(1)==POR||LA(1)==DIVISION)) {
					{
					switch ( LA(1)) {
					case POR:
					{
						AST tmp88_AST = null;
						tmp88_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp88_AST);
						match(POR);
						break;
					}
					case DIVISION:
					{
						AST tmp89_AST = null;
						tmp89_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp89_AST);
						match(DIVISION);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					expresion_nivel_6();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop8901;
				}
				
			} while (true);
			}
			expresion_nivel_5_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_38);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_nivel_5_AST;
	}
	
	public final void expresion_nivel_6() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_6_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case MENOS:
			{
				AST tmp90_AST = null;
				tmp90_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp90_AST);
				match(MENOS);
				expresion_nivel_6();
				astFactory.addASTChild(currentAST, returnAST);
				expresion_nivel_6_AST = (AST)currentAST.root;
				break;
			}
			case IDENT:
			case PARENTESIS_ABIERTO:
			case LIT_ENTERO:
			case LIT_CARACTER:
			case CADENA:
			case LEER:
			case ESCRIBIR:
			case LIT_REAL:
			case CIERTO:
			case FALSO:
			{
				expresion_nivel_7();
				astFactory.addASTChild(currentAST, returnAST);
				expresion_nivel_6_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_29);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_nivel_6_AST;
	}
	
	public final void expresion_nivel_7() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_7_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case PARENTESIS_ABIERTO:
			{
				match(PARENTESIS_ABIERTO);
				expresion();
				astFactory.addASTChild(currentAST, returnAST);
				match(PARENTESIS_CERRADO);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			case LIT_ENTERO:
			{
				AST tmp93_AST = null;
				tmp93_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp93_AST);
				match(LIT_ENTERO);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			case LIT_REAL:
			{
				AST tmp94_AST = null;
				tmp94_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp94_AST);
				match(LIT_REAL);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			case LIT_CARACTER:
			{
				AST tmp95_AST = null;
				tmp95_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp95_AST);
				match(LIT_CARACTER);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			case CADENA:
			{
				AST tmp96_AST = null;
				tmp96_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp96_AST);
				match(CADENA);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			case CIERTO:
			{
				AST tmp97_AST = null;
				tmp97_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp97_AST);
				match(CIERTO);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			case FALSO:
			{
				AST tmp98_AST = null;
				tmp98_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp98_AST);
				match(FALSO);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			default:
				boolean synPredMatched8905 = false;
				if (((LA(1)==IDENT||LA(1)==LEER||LA(1)==ESCRIBIR) && (LA(2)==PARENTESIS_ABIERTO||LA(2)==PUNTO||LA(2)==CORCHETE_ABIERTO) && (_tokenSet_27.member(LA(3))))) {
					int _m8905 = mark();
					synPredMatched8905 = true;
					inputState.guessing++;
					try {
						{
						match(IDENT);
						match(PARENTESIS_ABIERTO);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched8905 = false;
					}
					rewind(_m8905);
inputState.guessing--;
				}
				if ( synPredMatched8905 ) {
					llamada_rutina();
					astFactory.addASTChild(currentAST, returnAST);
					expresion_nivel_7_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==IDENT) && (_tokenSet_36.member(LA(2))) && (_tokenSet_39.member(LA(3)))) {
					acceso();
					astFactory.addASTChild(currentAST, returnAST);
					expresion_nivel_7_AST = (AST)currentAST.root;
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_29);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_nivel_7_AST;
	}
	
	public final void acceso1() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso1_AST = null;
		Token  i1 = null;
		AST i1_AST = null;
		AST a1_AST = null;
		Token  i2 = null;
		AST i2_AST = null;
		AST ind1_AST = null;
		AST a2_AST = null;
		Token  i3 = null;
		AST i3_AST = null;
		AST ind2_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			boolean synPredMatched8915 = false;
			if (((LA(1)==IDENT) && (LA(2)==PUNTO))) {
				int _m8915 = mark();
				synPredMatched8915 = true;
				inputState.guessing++;
				try {
					{
					match(IDENT);
					match(PUNTO);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched8915 = false;
				}
				rewind(_m8915);
inputState.guessing--;
			}
			if ( synPredMatched8915 ) {
				i1 = LT(1);
				i1_AST = astFactory.create(i1);
				match(IDENT);
				AST tmp99_AST = null;
				tmp99_AST = astFactory.create(LT(1));
				match(PUNTO);
				acceso1();
				a1_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					acceso1_AST = (AST)currentAST.root;
					acceso1_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ACCESO_REGISTRO,"acceso_registro")).add(i1_AST).add(a1_AST));
					currentAST.root = acceso1_AST;
					currentAST.child = acceso1_AST!=null &&acceso1_AST.getFirstChild()!=null ?
						acceso1_AST.getFirstChild() : acceso1_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else {
				boolean synPredMatched8917 = false;
				if (((LA(1)==IDENT) && (LA(2)==CORCHETE_ABIERTO) && (_tokenSet_20.member(LA(3))))) {
					int _m8917 = mark();
					synPredMatched8917 = true;
					inputState.guessing++;
					try {
						{
						match(IDENT);
						match(CORCHETE_ABIERTO);
						indices();
						match(CORCHETE_CERRADO);
						match(PUNTO);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched8917 = false;
					}
					rewind(_m8917);
inputState.guessing--;
				}
				if ( synPredMatched8917 ) {
					i2 = LT(1);
					i2_AST = astFactory.create(i2);
					match(IDENT);
					match(CORCHETE_ABIERTO);
					indices();
					ind1_AST = (AST)returnAST;
					match(CORCHETE_CERRADO);
					match(PUNTO);
					acceso1();
					a2_AST = (AST)returnAST;
					if ( inputState.guessing==0 ) {
						acceso1_AST = (AST)currentAST.root;
						acceso1_AST = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(ACCESO_ARRAY_REGISTRO,"acceso_array_registro")).add(i2_AST).add(ind1_AST).add(a2_AST));
						currentAST.root = acceso1_AST;
						currentAST.child = acceso1_AST!=null &&acceso1_AST.getFirstChild()!=null ?
							acceso1_AST.getFirstChild() : acceso1_AST;
						currentAST.advanceChildToEnd();
					}
				}
				else {
					boolean synPredMatched8919 = false;
					if (((LA(1)==IDENT) && (LA(2)==CORCHETE_ABIERTO) && (_tokenSet_20.member(LA(3))))) {
						int _m8919 = mark();
						synPredMatched8919 = true;
						inputState.guessing++;
						try {
							{
							match(IDENT);
							match(CORCHETE_ABIERTO);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched8919 = false;
						}
						rewind(_m8919);
inputState.guessing--;
					}
					if ( synPredMatched8919 ) {
						i3 = LT(1);
						i3_AST = astFactory.create(i3);
						match(IDENT);
						match(CORCHETE_ABIERTO);
						indices();
						ind2_AST = (AST)returnAST;
						match(CORCHETE_CERRADO);
						if ( inputState.guessing==0 ) {
							acceso1_AST = (AST)currentAST.root;
							acceso1_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ACCESO_ARRAY,"acceso_array")).add(i3_AST).add(ind2_AST));
							currentAST.root = acceso1_AST;
							currentAST.child = acceso1_AST!=null &&acceso1_AST.getFirstChild()!=null ?
								acceso1_AST.getFirstChild() : acceso1_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else if ((LA(1)==IDENT) && (_tokenSet_29.member(LA(2)))) {
						i = LT(1);
						i_AST = astFactory.create(i);
						match(IDENT);
						if ( inputState.guessing==0 ) {
							acceso1_AST = (AST)currentAST.root;
							acceso1_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ACCESO_SIMPLE,"acceso_simple")).add(i_AST));
							currentAST.root = acceso1_AST;
							currentAST.child = acceso1_AST!=null &&acceso1_AST.getFirstChild()!=null ?
								acceso1_AST.getFirstChild() : acceso1_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					}}
				}
				catch (RecognitionException ex) {
					if (inputState.guessing==0) {
						reportError(ex);
						recover(ex,_tokenSet_29);
					} else {
					  throw ex;
					}
				}
				returnAST = acceso1_AST;
			}
			
			
			public static final String[] _tokenNames = {
				"<0>",
				"EOF",
				"<2>",
				"NULL_TREE_LOOKAHEAD",
				"PROGRAMA",
				"DECLARACION",
				"CONSTANTE",
				"DEC_TIPO",
				"TIPO",
				"VARIABLE",
				"FUNC",
				"CUERPO",
				"RANGO_ENTERO",
				"RANGO_CARACTER",
				"ACCESO_SIMPLE",
				"ACCESO_REGISTRO",
				"ACCESO_ARRAY",
				"ACCESO_ARRAY_REGISTRO",
				"LLAMADA",
				"ARGUMENTOS",
				"ASIG",
				"CONTENIDO",
				"INSTRUCCION",
				"CABECERA",
				"CAMPO",
				"RETORNO",
				"LISTA_EXPRESIONES",
				"CONST",
				"IDENT",
				"IGUAL",
				"PUNTO_Y_COMA",
				"TYPE",
				"REGISTRO",
				"END",
				"VAR",
				"COMA",
				"FUNCION",
				"PARENTESIS_ABIERTO",
				"PARENTESIS_CERRADO",
				"BEGIN",
				"ENTERO",
				"REAL",
				"BOOLEANO",
				"CARACTER",
				"MENOS",
				"LIT_ENTERO",
				"PUNTO",
				"LIT_CARACTER",
				"ARRAY",
				"CORCHETE_ABIERTO",
				"CORCHETE_CERRADO",
				"OF",
				"CADENA",
				"ASIGNACION",
				"LEER",
				"ESCRIBIR",
				"SI",
				"SINO",
				"MIENTRAS",
				"HACER",
				"DESDE",
				"HASTA",
				"ATRAS",
				"OR",
				"AND",
				"NOT",
				"MAYOR",
				"MAYOR_IGUAL",
				"MENOR",
				"MENOR_IGUAL",
				"DISTINTO",
				"MAS",
				"POR",
				"DIVISION",
				"LIT_REAL",
				"CIERTO",
				"FALSO"
			};
			
			protected void buildTokenTypeASTClassMap() {
				tokenTypeToASTClassMap=null;
			};
			
			private static final long[] mk_tokenSet_0() {
				long[] data = { 2L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
			private static final long[] mk_tokenSet_1() {
				long[] data = { 549755813888L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
			private static final long[] mk_tokenSet_2() {
				long[] data = { 569083166720L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
			private static final long[] mk_tokenSet_3() {
				long[] data = { 566935683072L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
			private static final long[] mk_tokenSet_4() {
				long[] data = { 4995150312898560L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
			private static final long[] mk_tokenSet_5() {
				long[] data = { 570425344000L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
			private static final long[] mk_tokenSet_6() {
				long[] data = { 7563162153734111234L, 7170L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
			private static final long[] mk_tokenSet_7() {
				long[] data = { 567204118528L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
			private static final long[] mk_tokenSet_8() {
				long[] data = { 4995081593421824L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
			private static final long[] mk_tokenSet_9() {
				long[] data = { 8589934592L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
			private static final long[] mk_tokenSet_10() {
				long[] data = { 4995090183356416L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
			private static final long[] mk_tokenSet_11() {
				long[] data = { 268435456L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
			private static final long[] mk_tokenSet_12() {
				long[] data = { 4995700068712448L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
			private static final long[] mk_tokenSet_13() {
				long[] data = { 1073741824L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
			private static final long[] mk_tokenSet_14() {
				long[] data = { 668503338123264L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
			private static final long[] mk_tokenSet_15() {
				long[] data = { 1572247897947242496L, 7170L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
			private static final long[] mk_tokenSet_16() {
				long[] data = { 4995356471328768L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
			private static final long[] mk_tokenSet_17() {
				long[] data = { 1572247889357307904L, 7170L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
			private static final long[] mk_tokenSet_18() {
				long[] data = { 1125934535016448L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
			private static final long[] mk_tokenSet_19() {
				long[] data = { 1125899906842624L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
			private static final long[] mk_tokenSet_20() {
				long[] data = { 58740446909693952L, 7170L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
			private static final long[] mk_tokenSet_21() {
				long[] data = { -9162872336444030976L, 8191L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
			private static final long[] mk_tokenSet_22() {
				long[] data = { -1657324764072509438L, 8191L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
			private static final long[] mk_tokenSet_23() {
				long[] data = { 1125934266580992L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
			private static final long[] mk_tokenSet_24() {
				long[] data = { 58810851087351808L, 7170L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
			private static final long[] mk_tokenSet_25() {
				long[] data = { -9154991071455870976L, 8191L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
			private static final long[] mk_tokenSet_26() {
				long[] data = { -9154990796577964032L, 8191L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
			private static final long[] mk_tokenSet_27() {
				long[] data = { 58740721787600896L, 7170L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
			private static final long[] mk_tokenSet_28() {
				long[] data = { 6917529028714823680L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
			private static final long[] mk_tokenSet_29() {
				long[] data = { -1660209882583793662L, 8191L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
			private static final long[] mk_tokenSet_30() {
				long[] data = { 274877906944L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_30 = new BitSet(mk_tokenSet_30());
			private static final long[] mk_tokenSet_31() {
				long[] data = { 1716363086023098368L, 7170L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_31 = new BitSet(mk_tokenSet_31());
			private static final long[] mk_tokenSet_32() {
				long[] data = { 576460752303423488L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_32 = new BitSet(mk_tokenSet_32());
			private static final long[] mk_tokenSet_33() {
				long[] data = { -1660209883120664574L, 7170L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_33 = new BitSet(mk_tokenSet_33());
			private static final long[] mk_tokenSet_34() {
				long[] data = { -1660209883120664574L, 7171L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_34 = new BitSet(mk_tokenSet_34());
			private static final long[] mk_tokenSet_35() {
				long[] data = { 58740446909693952L, 7168L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_35 = new BitSet(mk_tokenSet_35());
			private static final long[] mk_tokenSet_36() {
				long[] data = { -1659576563886194686L, 8191L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_36 = new BitSet(mk_tokenSet_36());
			private static final long[] mk_tokenSet_37() {
				long[] data = { -1660209882583793662L, 7295L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_37 = new BitSet(mk_tokenSet_37());
			private static final long[] mk_tokenSet_38() {
				long[] data = { -1660209882583793662L, 7423L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_38 = new BitSet(mk_tokenSet_38());
			private static final long[] mk_tokenSet_39() {
				long[] data = { -73282879486L, 8191L, 0L, 0L};
				return data;
			}
			public static final BitSet _tokenSet_39 = new BitSet(mk_tokenSet_39());
			
			}
