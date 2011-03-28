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
 	AST arbol=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(MODULO,"modulo")).add(nombreModulo).add(definicionModulo));
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
 	AST arbol=(AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(CLASE,"clase")).add(nombreClase).add(cualificadorClase).add(definicionClase));
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
 	AST arbol=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METODO,"metodo")).add(declaracionMetodo).add(cualificadorMetodo));
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

 	AST arbol=(AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(ATRIBUTO,"atributo")).add(nombreAtributo).add(tipoAtributo).add(cualificadorAtributo));
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
 	AST arbol=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(PARAMETRO,"parametro")).add(nombreParametro).add(tipoParametro));
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
 	AST arbol=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(VARIABLE_LOCAL,"variable_local")).add(nombreVariable).add(tipoVariable));
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
	arbol=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ACCESO_SIMPLE,"acceso_simple")).add(ident).add(ast.dupTree(declaracionAcceso))); 
	
		
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

protected Anasint(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public Anasint(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected Anasint(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public Anasint(TokenStream lexer) {
  this(lexer,1);
}

public Anasint(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void declaracion_modulo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_modulo_AST = null;
		AST n_AST = null;
		AST d_AST = null;
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				ambitoAbrirPrograma();
			}
			nombre_modulo();
			n_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				ambitoAbrir(n_AST,"MODULO");
			}
			definicion_modulo();
			d_AST = (AST)returnAST;
			AST tmp1_AST = null;
			tmp1_AST = astFactory.create(LT(1));
			match(Token.EOF_TYPE);
			if ( inputState.guessing==0 ) {
				declaracion_modulo_AST = (AST)currentAST.root;
				ambitoCerrar();
					 declaracion_modulo_AST = creaDeclaracionModulo(n_AST,d_AST); 
					 ambitoCerrarPrograma();
					
				currentAST.root = declaracion_modulo_AST;
				currentAST.child = declaracion_modulo_AST!=null &&declaracion_modulo_AST.getFirstChild()!=null ?
					declaracion_modulo_AST.getFirstChild() : declaracion_modulo_AST;
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
		returnAST = declaracion_modulo_AST;
	}
	
	public final void nombre_modulo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST nombre_modulo_AST = null;
		
		try {      // for error handling
			match(MODULO);
			AST tmp3_AST = null;
			tmp3_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp3_AST);
			match(IDENT);
			nombre_modulo_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = nombre_modulo_AST;
	}
	
	public final void definicion_modulo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST definicion_modulo_AST = null;
		
		try {      // for error handling
			lista_declaraciones_clases();
			astFactory.addASTChild(currentAST, returnAST);
			definicion_modulo_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = definicion_modulo_AST;
	}
	
	public final void lista_declaraciones_clases() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_declaraciones_clases_AST = null;
		
		try {      // for error handling
			{
			_loop1460:
			do {
				if ((LA(1)==INST||LA(1)==CLASE)) {
					declaracion_clase();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop1460;
				}
				
			} while (true);
			}
			lista_declaraciones_clases_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_declaraciones_clases_AST;
	}
	
	public final void declaracion_clase() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_clase_AST = null;
		AST c_AST = null;
		AST n_AST = null;
		AST d_AST = null;
		
		try {      // for error handling
			cualificador_clase();
			c_AST = (AST)returnAST;
			nombre_clase();
			n_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				ambitoAbrir(n_AST,"CLASE");
			}
			definicion_clase();
			d_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				declaracion_clase_AST = (AST)currentAST.root;
				ambitoCerrar();
					 declaracion_clase_AST = creaDeclaracionClase(n_AST,c_AST,d_AST); 
					
				currentAST.root = declaracion_clase_AST;
				currentAST.child = declaracion_clase_AST!=null &&declaracion_clase_AST.getFirstChild()!=null ?
					declaracion_clase_AST.getFirstChild() : declaracion_clase_AST;
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
		returnAST = declaracion_clase_AST;
	}
	
	public final void cualificador_clase() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cualificador_clase_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case INST:
			{
				AST tmp4_AST = null;
				tmp4_AST = astFactory.create(LT(1));
				match(INST);
				if ( inputState.guessing==0 ) {
					cualificador_clase_AST = (AST)currentAST.root;
					cualificador_clase_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(tmp4_AST)));
					currentAST.root = cualificador_clase_AST;
					currentAST.child = cualificador_clase_AST!=null &&cualificador_clase_AST.getFirstChild()!=null ?
						cualificador_clase_AST.getFirstChild() : cualificador_clase_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case CLASE:
			{
				if ( inputState.guessing==0 ) {
					cualificador_clase_AST = (AST)currentAST.root;
					cualificador_clase_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(NO_INST,"no_inst")));
					currentAST.root = cualificador_clase_AST;
					currentAST.child = cualificador_clase_AST!=null &&cualificador_clase_AST.getFirstChild()!=null ?
						cualificador_clase_AST.getFirstChild() : cualificador_clase_AST;
					currentAST.advanceChildToEnd();
				}
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
		returnAST = cualificador_clase_AST;
	}
	
	public final void nombre_clase() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST nombre_clase_AST = null;
		
		try {      // for error handling
			match(CLASE);
			AST tmp6_AST = null;
			tmp6_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp6_AST);
			match(IDENT);
			nombre_clase_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_3);
			} else {
			  throw ex;
			}
		}
		returnAST = nombre_clase_AST;
	}
	
	public final void definicion_clase() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST definicion_clase_AST = null;
		
		try {      // for error handling
			match(LLAVE_ABIERTA);
			declaraciones_elemento_clase();
			astFactory.addASTChild(currentAST, returnAST);
			match(LLAVE_CERRADA);
			definicion_clase_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = definicion_clase_AST;
	}
	
	public final void declaraciones_elemento_clase() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaraciones_elemento_clase_AST = null;
		
		try {      // for error handling
			{
			_loop1467:
			do {
				if ((_tokenSet_4.member(LA(1)))) {
					declaracion_elemento_clase();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop1467;
				}
				
			} while (true);
			}
			declaraciones_elemento_clase_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		returnAST = declaraciones_elemento_clase_AST;
	}
	
	public final void declaracion_elemento_clase() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_elemento_clase_AST = null;
		AST c_AST = null;
		AST a_AST = null;
		AST t_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			cualificador_elemento_clase();
			c_AST = (AST)returnAST;
			{
			boolean synPredMatched1471 = false;
			if (((LA(1)==IDENT))) {
				int _m1471 = mark();
				synPredMatched1471 = true;
				inputState.guessing++;
				try {
					{
					match(IDENT);
					match(PARENTESIS_ABIERTO);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched1471 = false;
				}
				rewind(_m1471);
inputState.guessing--;
			}
			if ( synPredMatched1471 ) {
				declaracion_metodo();
				a_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					declaracion_elemento_clase_AST = (AST)currentAST.root;
					ambitoCerrar();
						   declaracion_elemento_clase_AST = creaDeclaracionMetodo(a_AST,c_AST); 
						
					currentAST.root = declaracion_elemento_clase_AST;
					currentAST.child = declaracion_elemento_clase_AST!=null &&declaracion_elemento_clase_AST.getFirstChild()!=null ?
						declaracion_elemento_clase_AST.getFirstChild() : declaracion_elemento_clase_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else if ((_tokenSet_6.member(LA(1)))) {
				tipo();
				t_AST = (AST)returnAST;
				i = LT(1);
				i_AST = astFactory.create(i);
				match(IDENT);
				AST tmp9_AST = null;
				tmp9_AST = astFactory.create(LT(1));
				match(PUNTO_Y_COMA);
				if ( inputState.guessing==0 ) {
					declaracion_elemento_clase_AST = (AST)currentAST.root;
					declaracion_elemento_clase_AST = creaDeclaracionAtributo(i_AST,t_AST,c_AST);
					currentAST.root = declaracion_elemento_clase_AST;
					currentAST.child = declaracion_elemento_clase_AST!=null &&declaracion_elemento_clase_AST.getFirstChild()!=null ?
						declaracion_elemento_clase_AST.getFirstChild() : declaracion_elemento_clase_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
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
		returnAST = declaracion_elemento_clase_AST;
	}
	
	public final void cualificador_elemento_clase() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cualificador_elemento_clase_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case OCULTO:
			{
				AST tmp10_AST = null;
				tmp10_AST = astFactory.create(LT(1));
				match(OCULTO);
				if ( inputState.guessing==0 ) {
					cualificador_elemento_clase_AST = (AST)currentAST.root;
					cualificador_elemento_clase_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(tmp10_AST)));
					currentAST.root = cualificador_elemento_clase_AST;
					currentAST.child = cualificador_elemento_clase_AST!=null &&cualificador_elemento_clase_AST.getFirstChild()!=null ?
						cualificador_elemento_clase_AST.getFirstChild() : cualificador_elemento_clase_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case IDENT:
			case ENTERO:
			case REAL:
			case LOGICO:
			case CARACTER:
			case FORMACION:
			{
				if ( inputState.guessing==0 ) {
					cualificador_elemento_clase_AST = (AST)currentAST.root;
					cualificador_elemento_clase_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(VISIBLE,"visible")));
					currentAST.root = cualificador_elemento_clase_AST;
					currentAST.child = cualificador_elemento_clase_AST!=null &&cualificador_elemento_clase_AST.getFirstChild()!=null ?
						cualificador_elemento_clase_AST.getFirstChild() : cualificador_elemento_clase_AST;
					currentAST.advanceChildToEnd();
				}
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
				recover(ex,_tokenSet_6);
			} else {
			  throw ex;
			}
		}
		returnAST = cualificador_elemento_clase_AST;
	}
	
	public final void declaracion_metodo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_metodo_AST = null;
		
		try {      // for error handling
			prototipo_metodo();
			astFactory.addASTChild(currentAST, returnAST);
			definicion_metodo();
			astFactory.addASTChild(currentAST, returnAST);
			declaracion_metodo_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_7);
			} else {
			  throw ex;
			}
		}
		returnAST = declaracion_metodo_AST;
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
			case LOGICO:
			case CARACTER:
			{
				tipo_predefinido_simple();
				astFactory.addASTChild(currentAST, returnAST);
				tipo_AST = (AST)currentAST.root;
				break;
			}
			case FORMACION:
			{
				tipo_predefinido_compuesto();
				astFactory.addASTChild(currentAST, returnAST);
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
					tipo_AST=ambitoTratarIdentTipo(i_AST);
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
				recover(ex,_tokenSet_8);
			} else {
			  throw ex;
			}
		}
		returnAST = tipo_AST;
	}
	
	public final void prototipo_metodo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST prototipo_metodo_AST = null;
		Token  i = null;
		AST i_AST = null;
		AST p_AST = null;
		AST t_AST = null;
		
		try {      // for error handling
			i = LT(1);
			i_AST = astFactory.create(i);
			match(IDENT);
			if ( inputState.guessing==0 ) {
				ambitoAbrir(i_AST,"METODO");
			}
			AST tmp11_AST = null;
			tmp11_AST = astFactory.create(LT(1));
			match(PARENTESIS_ABIERTO);
			declaracion_parametros();
			p_AST = (AST)returnAST;
			AST tmp12_AST = null;
			tmp12_AST = astFactory.create(LT(1));
			match(PARENTESIS_CERRADO);
			{
			switch ( LA(1)) {
			case DEV:
			{
				AST tmp13_AST = null;
				tmp13_AST = astFactory.create(LT(1));
				match(DEV);
				tipo();
				t_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					prototipo_metodo_AST = (AST)currentAST.root;
					prototipo_metodo_AST = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(PROTOTIPO,"prototipo")).add(i_AST).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PARAMETROS,"parametros")).add(p_AST))).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(RESULTADO,"resultado")).add(t_AST))));
					currentAST.root = prototipo_metodo_AST;
					currentAST.child = prototipo_metodo_AST!=null &&prototipo_metodo_AST.getFirstChild()!=null ?
						prototipo_metodo_AST.getFirstChild() : prototipo_metodo_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case LLAVE_ABIERTA:
			{
				if ( inputState.guessing==0 ) {
					prototipo_metodo_AST = (AST)currentAST.root;
					prototipo_metodo_AST = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(PROTOTIPO,"prototipo")).add(i_AST).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PARAMETROS,"parametros")).add(p_AST))).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(RESULTADO,"resultado")).add(astFactory.create(VACIO,"vacio")))));
					currentAST.root = prototipo_metodo_AST;
					currentAST.child = prototipo_metodo_AST!=null &&prototipo_metodo_AST.getFirstChild()!=null ?
						prototipo_metodo_AST.getFirstChild() : prototipo_metodo_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
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
		returnAST = prototipo_metodo_AST;
	}
	
	public final void definicion_metodo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST definicion_metodo_AST = null;
		AST d_AST = null;
		AST b_AST = null;
		
		try {      // for error handling
			AST tmp14_AST = null;
			tmp14_AST = astFactory.create(LT(1));
			match(LLAVE_ABIERTA);
			declaraciones_variables_locales();
			d_AST = (AST)returnAST;
			bloque();
			b_AST = (AST)returnAST;
			AST tmp15_AST = null;
			tmp15_AST = astFactory.create(LT(1));
			match(LLAVE_CERRADA);
			if ( inputState.guessing==0 ) {
				definicion_metodo_AST = (AST)currentAST.root;
				definicion_metodo_AST=   (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DEFINICION,"definicion")).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(VARIABLES_LOCALES,"variables_locales")).add(d_AST))).add(b_AST));
				currentAST.root = definicion_metodo_AST;
				currentAST.child = definicion_metodo_AST!=null &&definicion_metodo_AST.getFirstChild()!=null ?
					definicion_metodo_AST.getFirstChild() : definicion_metodo_AST;
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
		returnAST = definicion_metodo_AST;
	}
	
	public final void declaracion_parametros() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_parametros_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			case ENTERO:
			case REAL:
			case LOGICO:
			case CARACTER:
			case FORMACION:
			{
				declaracion_parametro();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop1478:
				do {
					if ((LA(1)==COMA)) {
						match(COMA);
						declaracion_parametro();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop1478;
					}
					
				} while (true);
				}
				declaracion_parametros_AST = (AST)currentAST.root;
				break;
			}
			case PARENTESIS_CERRADO:
			{
				declaracion_parametros_AST = (AST)currentAST.root;
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
				recover(ex,_tokenSet_9);
			} else {
			  throw ex;
			}
		}
		returnAST = declaracion_parametros_AST;
	}
	
	public final void declaracion_parametro() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_parametro_AST = null;
		AST t_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			tipo();
			t_AST = (AST)returnAST;
			i = LT(1);
			i_AST = astFactory.create(i);
			match(IDENT);
			if ( inputState.guessing==0 ) {
				declaracion_parametro_AST = (AST)currentAST.root;
				declaracion_parametro_AST = creaDeclaracionParametro(i_AST,t_AST);
				currentAST.root = declaracion_parametro_AST;
				currentAST.child = declaracion_parametro_AST!=null &&declaracion_parametro_AST.getFirstChild()!=null ?
					declaracion_parametro_AST.getFirstChild() : declaracion_parametro_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			} else {
			  throw ex;
			}
		}
		returnAST = declaracion_parametro_AST;
	}
	
	public final void declaraciones_variables_locales() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaraciones_variables_locales_AST = null;
		
		try {      // for error handling
			boolean synPredMatched1483 = false;
			if (((_tokenSet_6.member(LA(1))))) {
				int _m1483 = mark();
				synPredMatched1483 = true;
				inputState.guessing++;
				try {
					{
					declaracion_variables_locales();
					tipo();
					match(IDENT);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched1483 = false;
				}
				rewind(_m1483);
inputState.guessing--;
			}
			if ( synPredMatched1483 ) {
				declaracion_variables_locales();
				astFactory.addASTChild(currentAST, returnAST);
				declaraciones_variables_locales();
				astFactory.addASTChild(currentAST, returnAST);
				declaraciones_variables_locales_AST = (AST)currentAST.root;
			}
			else {
				boolean synPredMatched1485 = false;
				if (((_tokenSet_6.member(LA(1))))) {
					int _m1485 = mark();
					synPredMatched1485 = true;
					inputState.guessing++;
					try {
						{
						declaracion_variables_locales();
						}
					}
					catch (RecognitionException pe) {
						synPredMatched1485 = false;
					}
					rewind(_m1485);
inputState.guessing--;
				}
				if ( synPredMatched1485 ) {
					declaracion_variables_locales();
					astFactory.addASTChild(currentAST, returnAST);
					declaraciones_variables_locales_AST = (AST)currentAST.root;
				}
				else if ((_tokenSet_11.member(LA(1)))) {
					declaraciones_variables_locales_AST = (AST)currentAST.root;
				}
				else {
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
			returnAST = declaraciones_variables_locales_AST;
		}
		
	public final void bloque() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST bloque_AST = null;
		AST i_AST = null;
		
		try {      // for error handling
			instrucciones();
			i_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				bloque_AST = (AST)currentAST.root;
				bloque_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INSTRUCCIONES,"instrucciones")).add(i_AST));
				currentAST.root = bloque_AST;
				currentAST.child = bloque_AST!=null &&bloque_AST.getFirstChild()!=null ?
					bloque_AST.getFirstChild() : bloque_AST;
				currentAST.advanceChildToEnd();
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
		returnAST = bloque_AST;
	}
	
	public final void declaracion_variables_locales() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_variables_locales_AST = null;
		AST t_AST = null;
		
		try {      // for error handling
			tipo();
			t_AST = (AST)returnAST;
			lista_nombres_variables_locales(t_AST);
			astFactory.addASTChild(currentAST, returnAST);
			match(PUNTO_Y_COMA);
			declaracion_variables_locales_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_13);
			} else {
			  throw ex;
			}
		}
		returnAST = declaracion_variables_locales_AST;
	}
	
	public final void lista_nombres_variables_locales(
		AST t
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_nombres_variables_locales_AST = null;
		
		try {      // for error handling
			nombre_variable_local(t);
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop1489:
			do {
				if ((LA(1)==COMA)) {
					match(COMA);
					nombre_variable_local(t);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop1489;
				}
				
			} while (true);
			}
			lista_nombres_variables_locales_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_nombres_variables_locales_AST;
	}
	
	public final void nombre_variable_local(
		AST t
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST nombre_variable_local_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			i = LT(1);
			i_AST = astFactory.create(i);
			match(IDENT);
			if ( inputState.guessing==0 ) {
				nombre_variable_local_AST = (AST)currentAST.root;
				nombre_variable_local_AST = creaDeclaracionVariableLocal(i_AST,t);
				currentAST.root = nombre_variable_local_AST;
				currentAST.child = nombre_variable_local_AST!=null &&nombre_variable_local_AST.getFirstChild()!=null ?
					nombre_variable_local_AST.getFirstChild() : nombre_variable_local_AST;
				currentAST.advanceChildToEnd();
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
		returnAST = nombre_variable_local_AST;
	}
	
	public final void instrucciones() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instrucciones_AST = null;
		
		try {      // for error handling
			{
			_loop1493:
			do {
				if ((_tokenSet_16.member(LA(1)))) {
					instruccion();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop1493;
				}
				
			} while (true);
			}
			instrucciones_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_12);
			} else {
			  throw ex;
			}
		}
		returnAST = instrucciones_AST;
	}
	
	public final void instruccion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instruccion_AST = null;
		AST i_AST = null;
		AST j_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			case PARENTESIS_ABIERTO:
			case DEV:
			case NO:
			case MENOS:
			case LIT_ENTERO:
			case LIT_REAL:
			case LIT_CAR:
			case CIERTO:
			case FALSO:
			{
				instruccion_simple();
				i_AST = (AST)returnAST;
				AST tmp19_AST = null;
				tmp19_AST = astFactory.create(LT(1));
				match(PUNTO_Y_COMA);
				if ( inputState.guessing==0 ) {
					instruccion_AST = (AST)currentAST.root;
					instruccion_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INSTRUCCION,"instruccion")).add(i_AST));
					currentAST.root = instruccion_AST;
					currentAST.child = instruccion_AST!=null &&instruccion_AST.getFirstChild()!=null ?
						instruccion_AST.getFirstChild() : instruccion_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case SI:
			case MIENTRAS:
			{
				instruccion_compuesta();
				j_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					instruccion_AST = (AST)currentAST.root;
					instruccion_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INSTRUCCION,"instruccion")).add(j_AST));
					currentAST.root = instruccion_AST;
					currentAST.child = instruccion_AST!=null &&instruccion_AST.getFirstChild()!=null ?
						instruccion_AST.getFirstChild() : instruccion_AST;
					currentAST.advanceChildToEnd();
				}
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
				recover(ex,_tokenSet_17);
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
			boolean synPredMatched1497 = false;
			if (((_tokenSet_18.member(LA(1))))) {
				int _m1497 = mark();
				synPredMatched1497 = true;
				inputState.guessing++;
				try {
					{
					expresion();
					match(ASIGNACION);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched1497 = false;
				}
				rewind(_m1497);
inputState.guessing--;
			}
			if ( synPredMatched1497 ) {
				asignacion();
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_simple_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==IDENT)) {
				llamada_metodo();
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_simple_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==DEV)) {
				retorno();
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_simple_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
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
				condicion();
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
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
		returnAST = instruccion_compuesta_AST;
	}
	
	public final void expresion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_AST = null;
		
		try {      // for error handling
			expresion_nivel_1();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop1511:
			do {
				if ((LA(1)==O)) {
					AST tmp20_AST = null;
					tmp20_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp20_AST);
					match(O);
					expresion_nivel_1();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop1511;
				}
				
			} while (true);
			}
			expresion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_19);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_AST;
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
			AST tmp21_AST = null;
			tmp21_AST = astFactory.create(LT(1));
			match(ASIGNACION);
			expresion();
			e2_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				asignacion_AST = (AST)currentAST.root;
				asignacion_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(tmp21_AST)).add(e1_AST).add(e2_AST));
				currentAST.root = asignacion_AST;
				currentAST.child = asignacion_AST!=null &&asignacion_AST.getFirstChild()!=null ?
					asignacion_AST.getFirstChild() : asignacion_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		returnAST = asignacion_AST;
	}
	
	public final void llamada_metodo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST llamada_metodo_AST = null;
		AST i_AST = null;
		AST s_AST = null;
		
		try {      // for error handling
			acceso();
			i_AST = (AST)returnAST;
			AST tmp22_AST = null;
			tmp22_AST = astFactory.create(LT(1));
			match(PARENTESIS_ABIERTO);
			lista_expresiones();
			s_AST = (AST)returnAST;
			AST tmp23_AST = null;
			tmp23_AST = astFactory.create(LT(1));
			match(PARENTESIS_CERRADO);
			if ( inputState.guessing==0 ) {
				llamada_metodo_AST = (AST)currentAST.root;
				llamada_metodo_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(LLAMADA,"llamada")).add(i_AST).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(EXPRESIONES,"expresiones")).add(s_AST))));
				currentAST.root = llamada_metodo_AST;
				currentAST.child = llamada_metodo_AST!=null &&llamada_metodo_AST.getFirstChild()!=null ?
					llamada_metodo_AST.getFirstChild() : llamada_metodo_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_20);
			} else {
			  throw ex;
			}
		}
		returnAST = llamada_metodo_AST;
	}
	
	public final void retorno() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST retorno_AST = null;
		AST e_AST = null;
		
		try {      // for error handling
			AST tmp24_AST = null;
			tmp24_AST = astFactory.create(LT(1));
			match(DEV);
			expresion();
			e_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				retorno_AST = (AST)currentAST.root;
				retorno_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(tmp24_AST)).add(e_AST));
				currentAST.root = retorno_AST;
				currentAST.child = retorno_AST!=null &&retorno_AST.getFirstChild()!=null ?
					retorno_AST.getFirstChild() : retorno_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		returnAST = retorno_AST;
	}
	
	public final void condicion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST condicion_AST = null;
		
		try {      // for error handling
			AST tmp25_AST = null;
			tmp25_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp25_AST);
			match(SI);
			match(PARENTESIS_ABIERTO);
			expresion();
			astFactory.addASTChild(currentAST, returnAST);
			match(PARENTESIS_CERRADO);
			match(ENTONCES);
			bloque();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case SINO:
			{
				match(SINO);
				bloque();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case FINSI:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(FINSI);
			condicion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
		returnAST = condicion_AST;
	}
	
	public final void iteracion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iteracion_AST = null;
		
		try {      // for error handling
			AST tmp31_AST = null;
			tmp31_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp31_AST);
			match(MIENTRAS);
			match(PARENTESIS_ABIERTO);
			expresion();
			astFactory.addASTChild(currentAST, returnAST);
			match(PARENTESIS_CERRADO);
			match(HACER);
			bloque();
			astFactory.addASTChild(currentAST, returnAST);
			match(FINMIENTRAS);
			iteracion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
		returnAST = iteracion_AST;
	}
	
	public final void acceso() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_AST = null;
		Token  i1 = null;
		AST i1_AST = null;
		Token  i2 = null;
		AST i2_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			boolean synPredMatched1536 = false;
			if (((LA(1)==IDENT))) {
				int _m1536 = mark();
				synPredMatched1536 = true;
				inputState.guessing++;
				try {
					{
					match(IDENT);
					match(PUNTO);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched1536 = false;
				}
				rewind(_m1536);
inputState.guessing--;
			}
			if ( synPredMatched1536 ) {
				i1 = LT(1);
				i1_AST = astFactory.create(i1);
				match(IDENT);
				AST tmp36_AST = null;
				tmp36_AST = astFactory.create(LT(1));
				match(PUNTO);
				i2 = LT(1);
				i2_AST = astFactory.create(i2);
				match(IDENT);
				if ( inputState.guessing==0 ) {
					acceso_AST = (AST)currentAST.root;
					acceso_AST=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ACCESO_OBJETO,"acceso_objeto")).add(ambitoTratarAccesoSimple(i1_AST)).add(i2_AST));
					currentAST.root = acceso_AST;
					currentAST.child = acceso_AST!=null &&acceso_AST.getFirstChild()!=null ?
						acceso_AST.getFirstChild() : acceso_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else if ((LA(1)==IDENT)) {
				i = LT(1);
				i_AST = astFactory.create(i);
				match(IDENT);
				if ( inputState.guessing==0 ) {
					acceso_AST = (AST)currentAST.root;
					acceso_AST=ambitoTratarAccesoSimple(i_AST);
					currentAST.root = acceso_AST;
					currentAST.child = acceso_AST!=null &&acceso_AST.getFirstChild()!=null ?
						acceso_AST.getFirstChild() : acceso_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_21);
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
			case NO:
			case MENOS:
			case LIT_ENTERO:
			case LIT_REAL:
			case LIT_CAR:
			case CIERTO:
			case FALSO:
			{
				expresion();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop1504:
				do {
					if ((LA(1)==COMA)) {
						match(COMA);
						expresion();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop1504;
					}
					
				} while (true);
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
				recover(ex,_tokenSet_9);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_expresiones_AST;
	}
	
	public final void expresion_nivel_1() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_1_AST = null;
		
		try {      // for error handling
			expresion_nivel_2();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop1514:
			do {
				if ((LA(1)==Y)) {
					AST tmp38_AST = null;
					tmp38_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp38_AST);
					match(Y);
					expresion_nivel_2();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop1514;
				}
				
			} while (true);
			}
			expresion_nivel_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_22);
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
			case NO:
			{
				AST tmp39_AST = null;
				tmp39_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp39_AST);
				match(NO);
				expresion_nivel_2();
				astFactory.addASTChild(currentAST, returnAST);
				expresion_nivel_2_AST = (AST)currentAST.root;
				break;
			}
			case IDENT:
			case PARENTESIS_ABIERTO:
			case MENOS:
			case LIT_ENTERO:
			case LIT_REAL:
			case LIT_CAR:
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
				recover(ex,_tokenSet_23);
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
			case MAYOR:
			case MAYOR_IGUAL:
			case MENOR:
			case MENOR_IGUAL:
			case IGUAL:
			case DISTINTO:
			{
				{
				switch ( LA(1)) {
				case MAYOR:
				{
					AST tmp40_AST = null;
					tmp40_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp40_AST);
					match(MAYOR);
					break;
				}
				case MAYOR_IGUAL:
				{
					AST tmp41_AST = null;
					tmp41_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp41_AST);
					match(MAYOR_IGUAL);
					break;
				}
				case MENOR:
				{
					AST tmp42_AST = null;
					tmp42_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp42_AST);
					match(MENOR);
					break;
				}
				case MENOR_IGUAL:
				{
					AST tmp43_AST = null;
					tmp43_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp43_AST);
					match(MENOR_IGUAL);
					break;
				}
				case IGUAL:
				{
					AST tmp44_AST = null;
					tmp44_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp44_AST);
					match(IGUAL);
					break;
				}
				case DISTINTO:
				{
					AST tmp45_AST = null;
					tmp45_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp45_AST);
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
			case PUNTO_Y_COMA:
			case PARENTESIS_CERRADO:
			case COMA:
			case ASIGNACION:
			case O:
			case Y:
			case CORCHETE_CERRADO:
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
				recover(ex,_tokenSet_23);
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
			_loop1522:
			do {
				if ((LA(1)==MAS||LA(1)==MENOS)) {
					{
					switch ( LA(1)) {
					case MAS:
					{
						AST tmp46_AST = null;
						tmp46_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp46_AST);
						match(MAS);
						break;
					}
					case MENOS:
					{
						AST tmp47_AST = null;
						tmp47_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp47_AST);
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
					break _loop1522;
				}
				
			} while (true);
			}
			expresion_nivel_4_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_24);
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
			_loop1526:
			do {
				if ((LA(1)==POR||LA(1)==DIVISION)) {
					{
					switch ( LA(1)) {
					case POR:
					{
						AST tmp48_AST = null;
						tmp48_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp48_AST);
						match(POR);
						break;
					}
					case DIVISION:
					{
						AST tmp49_AST = null;
						tmp49_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp49_AST);
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
					break _loop1526;
				}
				
			} while (true);
			}
			expresion_nivel_5_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_25);
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
		AST i_AST = null;
		AST j_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case MENOS:
			{
				AST tmp50_AST = null;
				tmp50_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp50_AST);
				match(MENOS);
				expresion_nivel_6();
				i_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					expresion_nivel_6_AST = (AST)currentAST.root;
					expresion_nivel_6_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MENOSUNARIO,"menosunario")).add(i_AST));
					currentAST.root = expresion_nivel_6_AST;
					currentAST.child = expresion_nivel_6_AST!=null &&expresion_nivel_6_AST.getFirstChild()!=null ?
						expresion_nivel_6_AST.getFirstChild() : expresion_nivel_6_AST;
					currentAST.advanceChildToEnd();
				}
				expresion_nivel_6_AST = (AST)currentAST.root;
				break;
			}
			case IDENT:
			case PARENTESIS_ABIERTO:
			case LIT_ENTERO:
			case LIT_REAL:
			case LIT_CAR:
			case CIERTO:
			case FALSO:
			{
				expresion_nivel_7();
				j_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					expresion_nivel_6_AST = (AST)currentAST.root;
					expresion_nivel_6_AST=j_AST;
					currentAST.root = expresion_nivel_6_AST;
					currentAST.child = expresion_nivel_6_AST!=null &&expresion_nivel_6_AST.getFirstChild()!=null ?
						expresion_nivel_6_AST.getFirstChild() : expresion_nivel_6_AST;
					currentAST.advanceChildToEnd();
				}
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
				recover(ex,_tokenSet_20);
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
				AST tmp53_AST = null;
				tmp53_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp53_AST);
				match(LIT_ENTERO);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			case LIT_REAL:
			{
				AST tmp54_AST = null;
				tmp54_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp54_AST);
				match(LIT_REAL);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			case LIT_CAR:
			{
				AST tmp55_AST = null;
				tmp55_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp55_AST);
				match(LIT_CAR);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			case CIERTO:
			{
				AST tmp56_AST = null;
				tmp56_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp56_AST);
				match(CIERTO);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			case FALSO:
			{
				AST tmp57_AST = null;
				tmp57_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp57_AST);
				match(FALSO);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			default:
				boolean synPredMatched1530 = false;
				if (((LA(1)==IDENT))) {
					int _m1530 = mark();
					synPredMatched1530 = true;
					inputState.guessing++;
					try {
						{
						acceso();
						match(PARENTESIS_ABIERTO);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched1530 = false;
					}
					rewind(_m1530);
inputState.guessing--;
				}
				if ( synPredMatched1530 ) {
					llamada_metodo();
					astFactory.addASTChild(currentAST, returnAST);
					expresion_nivel_7_AST = (AST)currentAST.root;
				}
				else {
					boolean synPredMatched1532 = false;
					if (((LA(1)==IDENT))) {
						int _m1532 = mark();
						synPredMatched1532 = true;
						inputState.guessing++;
						try {
							{
							acceso();
							match(CORCHETE_ABIERTO);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched1532 = false;
						}
						rewind(_m1532);
inputState.guessing--;
					}
					if ( synPredMatched1532 ) {
						acceso_tabla();
						astFactory.addASTChild(currentAST, returnAST);
						expresion_nivel_7_AST = (AST)currentAST.root;
					}
					else if ((LA(1)==IDENT)) {
						acceso();
						astFactory.addASTChild(currentAST, returnAST);
						expresion_nivel_7_AST = (AST)currentAST.root;
					}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_20);
				} else {
				  throw ex;
				}
			}
			returnAST = expresion_nivel_7_AST;
		}
		
	public final void acceso_tabla() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_tabla_AST = null;
		AST c_AST = null;
		AST d_AST = null;
		
		try {      // for error handling
			acceso();
			c_AST = (AST)returnAST;
			AST tmp58_AST = null;
			tmp58_AST = astFactory.create(LT(1));
			match(CORCHETE_ABIERTO);
			lista_expresiones_nv();
			d_AST = (AST)returnAST;
			AST tmp59_AST = null;
			tmp59_AST = astFactory.create(LT(1));
			match(CORCHETE_CERRADO);
			if ( inputState.guessing==0 ) {
				acceso_tabla_AST = (AST)currentAST.root;
				acceso_tabla_AST=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ACCESO_TABLA,"acceso_tabla")).add(c_AST).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(EXPRESIONES,"expresiones")).add(d_AST))));
				currentAST.root = acceso_tabla_AST;
				currentAST.child = acceso_tabla_AST!=null &&acceso_tabla_AST.getFirstChild()!=null ?
					acceso_tabla_AST.getFirstChild() : acceso_tabla_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_20);
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_tabla_AST;
	}
	
	public final void lista_expresiones_nv() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_expresiones_nv_AST = null;
		
		try {      // for error handling
			expresion();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop1539:
			do {
				if ((LA(1)==COMA)) {
					match(COMA);
					expresion();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop1539;
				}
				
			} while (true);
			}
			lista_expresiones_nv_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_26);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_expresiones_nv_AST;
	}
	
	public final void tipo_predefinido_simple() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tipo_predefinido_simple_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ENTERO:
			{
				AST tmp61_AST = null;
				tmp61_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp61_AST);
				match(ENTERO);
				tipo_predefinido_simple_AST = (AST)currentAST.root;
				break;
			}
			case REAL:
			{
				AST tmp62_AST = null;
				tmp62_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp62_AST);
				match(REAL);
				tipo_predefinido_simple_AST = (AST)currentAST.root;
				break;
			}
			case LOGICO:
			{
				AST tmp63_AST = null;
				tmp63_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp63_AST);
				match(LOGICO);
				tipo_predefinido_simple_AST = (AST)currentAST.root;
				break;
			}
			case CARACTER:
			{
				AST tmp64_AST = null;
				tmp64_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp64_AST);
				match(CARACTER);
				tipo_predefinido_simple_AST = (AST)currentAST.root;
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
				recover(ex,_tokenSet_8);
			} else {
			  throw ex;
			}
		}
		returnAST = tipo_predefinido_simple_AST;
	}
	
	public final void tipo_predefinido_compuesto() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tipo_predefinido_compuesto_AST = null;
		
		try {      // for error handling
			formacion();
			astFactory.addASTChild(currentAST, returnAST);
			tipo_predefinido_compuesto_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_8);
			} else {
			  throw ex;
			}
		}
		returnAST = tipo_predefinido_compuesto_AST;
	}
	
	public final void formacion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST formacion_AST = null;
		AST l_AST = null;
		AST t_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			AST tmp65_AST = null;
			tmp65_AST = astFactory.create(LT(1));
			match(FORMACION);
			lista_enteros();
			l_AST = (AST)returnAST;
			{
			switch ( LA(1)) {
			case ENTERO:
			case REAL:
			case LOGICO:
			case CARACTER:
			{
				tipo_predefinido_simple();
				t_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					formacion_AST = (AST)currentAST.root;
					formacion_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(FORMACION,"formacion")).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(LISTA_ENTEROS,"lista_enteros")).add(l_AST))).add(t_AST));
					currentAST.root = formacion_AST;
					currentAST.child = formacion_AST!=null &&formacion_AST.getFirstChild()!=null ?
						formacion_AST.getFirstChild() : formacion_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case IDENT:
			{
				i = LT(1);
				i_AST = astFactory.create(i);
				match(IDENT);
				if ( inputState.guessing==0 ) {
					formacion_AST = (AST)currentAST.root;
					formacion_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(FORMACION,"formacion")).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(LISTA_ENTEROS,"lista_enteros")).add(l_AST))).add(ambitoTratarIdentTipo(i_AST)));
					currentAST.root = formacion_AST;
					currentAST.child = formacion_AST!=null &&formacion_AST.getFirstChild()!=null ?
						formacion_AST.getFirstChild() : formacion_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_8);
			} else {
			  throw ex;
			}
		}
		returnAST = formacion_AST;
	}
	
	public final void lista_enteros() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_enteros_AST = null;
		
		try {      // for error handling
			AST tmp66_AST = null;
			tmp66_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp66_AST);
			match(LIT_ENTERO);
			{
			_loop1547:
			do {
				if ((LA(1)==COMA)) {
					match(COMA);
					AST tmp68_AST = null;
					tmp68_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp68_AST);
					match(LIT_ENTERO);
				}
				else {
					break _loop1547;
				}
				
			} while (true);
			}
			lista_enteros_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_27);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_enteros_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"PROGRAMA",
		"VISIBLE",
		"OCULTO",
		"NO_INST",
		"ATRIBUTO",
		"METODO",
		"PROTOTIPO",
		"PARAMETRO",
		"PARAMETROS",
		"EXPRESIONES",
		"RESULTADO",
		"DEFINICION",
		"VACIO",
		"VARIABLE_LOCAL",
		"VARIABLES_LOCALES",
		"INSTRUCCION",
		"INSTRUCCIONES",
		"MENOSUNARIO",
		"LLAMADA",
		"ACCESO_TABLA",
		"ACCESO_OBJETO",
		"ACCESO_SIMPLE",
		"LISTA_ENTEROS",
		"MODULO",
		"IDENT",
		"INST",
		"CLASE",
		"LLAVE_ABIERTA",
		"LLAVE_CERRADA",
		"PARENTESIS_ABIERTO",
		"PUNTO_Y_COMA",
		"PARENTESIS_CERRADO",
		"DEV",
		"COMA",
		"ASIGNACION",
		"SI",
		"ENTONCES",
		"SINO",
		"FINSI",
		"MIENTRAS",
		"HACER",
		"FINMIENTRAS",
		"O",
		"Y",
		"NO",
		"MAYOR",
		"MAYOR_IGUAL",
		"MENOR",
		"MENOR_IGUAL",
		"IGUAL",
		"DISTINTO",
		"MAS",
		"MENOS",
		"POR",
		"DIVISION",
		"CORCHETE_ABIERTO",
		"LIT_ENTERO",
		"LIT_REAL",
		"LIT_CAR",
		"CIERTO",
		"FALSO",
		"CORCHETE_CERRADO",
		"PUNTO",
		"ENTERO",
		"REAL",
		"LOGICO",
		"CARACTER",
		"FORMACION"
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
		long[] data = { 1610612738L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 1073741824L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 2147483648L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 268435520L, 248L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 4294967296L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 268435456L, 248L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 4563402816L, 248L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 2415919104L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 34359738368L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 171798691840L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { -1080573007870558208L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 41785736822784L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { -1080573007870558208L, 249L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 17179869184L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 154618822656L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { -1080573012165525504L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { -1080531226428702720L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { -1080582426733838336L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 463856467968L, 2L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 576109372439003136L, 2L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 1152570133332361216L, 2L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { 70832600645632L, 2L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 211570089000960L, 2L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { 35677417154543616L, 2L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = { 143763808211435520L, 2L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = { 0L, 2L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = { 268435456L, 120L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	
	}
