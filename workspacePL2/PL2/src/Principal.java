///////////////////////////////
// Principal.java (clase principal)
///////////////////////////////
import java.io.*;
import antlr.collections.*;
import antlr.debug.misc.*;
import antlr.*;

public class Principal 
{
	public static void main(String args[]) {
		try 
		{
			FileInputStream fis = new FileInputStream("src/bateriaBorja");
			Analex analex = new Analex(fis);
			Anasint anasint = new Anasint(analex);
			AST arbol = null;
			anasint.programa();
			arbol = anasint.getAST();
			ASTFrame frame= new ASTFrame("src/bateriaBorja",arbol);
			frame.setVisible(true);
		}
		catch(ANTLRException ae) 
		{
			System.err.println(ae.getMessage());
		}
		catch(FileNotFoundException fnfe) 
		{
			System.err.println("No se encontr� el fichero");
		}
	}
}