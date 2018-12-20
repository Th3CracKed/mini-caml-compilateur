
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.junit.Test;


import util.CompilationException;



public class LeTest {
	
	private final String CHEMIN_RESULTAT = "rsc/testsResultat";
	private final String NOM_FICHIER_SORTIE_ASML = "nomFichierSortieTestASML.asml";
	private final String ARM_RESSOURCE = "ARM/libmincaml.S";
	private final String NOM_FICHIER_SORTIE_ARM_O = "nomFichierSortieTestARM.o";
	private final String NOM_FICHIER_SORTIE_ARM_S = "nomFichierSortieTestARM.s";
	private final String NOM_FICHIER_SORTIE_ARM_EXECUTABLE = "nomFichierSortieTestARM.arm";
	private ArrayList<String> testInvalides;
	private int nbTest;
	private int nbTestOk;
	private int nbTestNok;
	
	@Test
	void test() {	
	  
		cleanResultat();
		
	    File dossier =  new File("rsc/tests");
	    testInvalides =  new ArrayList<>();
	    nbTest = 0;
	    nbTestOk = 0;
	    nbTestNok = 0;
	    this.testWorker(dossier);

	    for(String nomFichier : testInvalides)
	    {
	    	System.out.println(nomFichier);
	    }
	
    	System.out.println("Nombre de tests realises:" + nbTest );
    	System.out.println("Nombre de tests OK:" + nbTestOk );
    	System.out.println("Nombre de tests NOK:" + nbTestNok );

	}
	

	
	private void testWorker(File dossier)
	{
		for(File fichier : dossier.listFiles())
        {
			
			if(fichier.isDirectory())
			{
				testWorker(fichier);
			}else
			{
				nbTest ++;
			//	System.out.print("======   "+fichier.getPath()+" :");
				Boolean estValide =false;	

				if(fichier.getParentFile().getName().equals("valid"))
				{
					estValide = true;
				}else if(fichier.getParentFile().getName().equals("invalid"))
				{
					estValide = false;
				}


				try {

					Main.compiler(fichier.getPath(), NOM_FICHIER_SORTIE_ARM_S, NOM_FICHIER_SORTIE_ASML, false,false,true);
					
					String resultatOcaml = executerDepuisOcaml(fichier);
					String resultatAsml = executerDepuisAsml();
					String resultatArm = executerDepuisArm();
					
				//	System.out.print(" minCaml : "  +resultatOcaml);
				//	System.out.print(" ASML : "  +resultatAsml);
				//	System.out.print(" ARM : "  +resultatArm);
					if(resultatAsml.equals(resultatOcaml) && resultatArm.equals(resultatAsml)  && estValide)
					{
						nbTestOk ++;
						//System.out.println("  OK");
					}else {
						System.out.println("======   "+fichier.getPath()+" :");
						System.out.println(" minCaml : "  +resultatOcaml);
						System.out.println(" ASML : "  +resultatAsml);
						System.out.println(" ARM : "  +resultatArm);
						System.out.println(" NOK");
						String nomDossier = fichier.getPath().replace('/','_').substring(0,fichier.getPath().length()-3);


						Path path = Paths.get(CHEMIN_RESULTAT+"/"+nomDossier);
						try {
							Files.createDirectories(path);
						//	System.out.println(path.toAbsolutePath());
							File fichierResultatAsml =  new File(CHEMIN_RESULTAT+"/"+nomDossier+"/fichier.asml");
							File fichierResultatArm =  new File(CHEMIN_RESULTAT+"/"+nomDossier+"/fichier.s");
							copierFichier(new File(NOM_FICHIER_SORTIE_ASML),fichierResultatAsml);
							copierFichier(new File(NOM_FICHIER_SORTIE_ARM_S),fichierResultatArm);						
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}					

						nbTestNok ++;
						testInvalides.add(fichier.getPath());
					}
					
					
					File fileO =  new File(NOM_FICHIER_SORTIE_ARM_O);
				    File fileExecArm =  new File(NOM_FICHIER_SORTIE_ARM_EXECUTABLE);
				    File fileARmS =  new File(NOM_FICHIER_SORTIE_ARM_S);
				    File fileAsml =  new File(NOM_FICHIER_SORTIE_ASML);
				    File fileExecMinCaml=  new File("executableMinCaml");

				    fileAsml.delete();
				    fileExecMinCaml.delete();
				    fileARmS.delete();	
				    fileExecArm.delete();
				    fileO.delete();

				} catch (CompilationException | FileNotFoundException  e) {
					if(estValide)
					{
						System.out.print("======   "+fichier.getPath()+" :");
						System.out.println(" NOK");
						nbTestNok++;
						testInvalides.add(fichier.getPath());

					}else {
						nbTestOk++;
						//System.out.println("OK");

					}
					
				}
			}
        }
		clean(dossier);

	}
	
	
	private void copierFichier(File fSource, File fDest)
	{
		InputStream in;
		try {
			in = new FileInputStream(fSource);
			OutputStream out = new FileOutputStream(fDest);

	        // Copy the bits from instream to outstream
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        in.close();
	        out.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	
	private void cleanResultat() 
	{
	    String[] commande = {"rm","-r" ,CHEMIN_RESULTAT };
	    
	    Process p;
		try {
			p = Runtime.getRuntime().exec(commande);
		    int exitValue = p.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

		


	
	private void clean(File dossier) {

		for(File fichier : dossier.listFiles())
        {
			
			String ext = getExtension(fichier.getName());
			if(!ext.isEmpty() && (ext.equals("cmo") || ext.equals("cmi") ))
			{
					fichier.delete();
			}
        }
	}
	
	private String getExtension(String nomFichier)
	{
		String extension = "";

		int i = nomFichier.lastIndexOf('.');
		if (i > 0) {
		    extension = nomFichier.substring(i+1);
		}
		return extension;
		

	}
	

	private String executerDepuisArm()
	{
		
		String resultat = "";
		try {		
			
		    String[] commande = {"arm-none-eabi-as","-o",NOM_FICHIER_SORTIE_ARM_O ,NOM_FICHIER_SORTIE_ARM_S,ARM_RESSOURCE};
		    Process p = Runtime.getRuntime().exec(commande);
		    int exitValue = p.waitFor();

		    if (exitValue == 0) 
		    {	
			    String[] commande2 = {"arm-none-eabi-ld","-o", NOM_FICHIER_SORTIE_ARM_EXECUTABLE,NOM_FICHIER_SORTIE_ARM_O};
			    Process p2 = Runtime.getRuntime().exec(commande2);
			    exitValue = p2.waitFor();

			    if (exitValue == 0) 
			    {
			    	  String[] commande3 = {"./"+NOM_FICHIER_SORTIE_ARM_EXECUTABLE};
			    	  Process p3 = Runtime.getRuntime().exec(commande3);
			    	  exitValue = p3.waitFor();
			    	  resultat = getStream(p3.getInputStream());  
			    	  
			    }
		    	
		    	
			  
		    }
		    
		    
		  
		} catch(IOException | InterruptedException ioe) {
		    ioe.printStackTrace();
		} 
		return resultat;
		
	}
	
	
	
	private String executerDepuisAsml()
	{
		
		String resultat = "";
		try {		
			
			String programme = System.getProperty("user.dir")+"/tools/asml";
		    String[] commande = {programme,NOM_FICHIER_SORTIE_ASML};
		    
		    Process p = Runtime.getRuntime().exec(commande);
		    int exitValue = p.waitFor();

		    if (exitValue == 0) 
		    {	
				resultat = getStream(p.getInputStream());  	
		    }	  
		  
		} catch(IOException | InterruptedException ioe) {
		    ioe.printStackTrace();
		} 
		return resultat;
	}
	
	private String executerDepuisOcaml(File fichierMinCaml)
	{
		 String resultat = "";
		
		 
		 try {
			    String[] commande = {"ocamlc",fichierMinCaml.getPath(), "-o" , "executableMinCaml"};
		    
			    Process p = Runtime.getRuntime().exec(commande);

			    int exitValue = p.waitFor();

			    if (exitValue == 0) {
	    			String[] commande2 = {"./"+"executableMinCaml"};
				    Process p2 = Runtime.getRuntime().exec(commande2);
	    			exitValue = p2.waitFor();
				    resultat = getStream(p2.getInputStream());  	
		    	}
			    
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e1) {
				e1.printStackTrace();
			} 
		 	return resultat;
	}
	
	
	
	private String getStream(InputStream stream)
	{
		BufferedReader reader = new BufferedReader( new InputStreamReader(stream));			
  	  	String ligne = "";
  	  	String resultat = "";
	        try {
	            while ((ligne = reader.readLine()) != null) {
	            	resultat += ligne;
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			return resultat;
	}
	
}
