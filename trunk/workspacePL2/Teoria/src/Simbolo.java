import antlr.ASTFactory;
import antlr.CommonAST;
import antlr.collections.AST;


public class Simbolo {

	private String nombre;
	private AST declaracion;
	
	
	
	// Se clona el arbol de la declaración para que no coja las declaraciones hermanas 
	public Simbolo(String n,AST dec){
		nombre=n;
		ASTFactory fac=new ASTFactory();
		declaracion=fac.dupTree(dec); //clona el arbol ignorando a los hermanos
				
	}
	
	public String getNombre(){
		return nombre;
	}
	
	public AST getDeclaracion(){
	
		return declaracion;
	}
	
	public void setNombre(String n){
		nombre=n;
	}
	
	public void setDeclaracion(AST dec){
		
		declaracion=dec;
	}
	

}
