package ac.uk.napier.onemax;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Directory / File listing
 * @author cs378
 *
 */
public class FileGetter {

	private static String suffix;
	private static String prefix;

public static String[] getDirNamesRecursive(String dirName) {
		
		
		ArrayList<String> filesArray = new ArrayList<String>();
		if(dirName == null || dirName.equals("")){
			dirName = ".";//current directory
		}
					
			File dir = new File(dirName);
			File f = new File(dir.getAbsolutePath());
			for (File f1 : f.listFiles()){
				if ( f1.isDirectory() ) {
					filesArray.add(f1.getAbsolutePath() + "\\");
	                for(String name : getDirNamesRecursive(f1.getAbsolutePath())){
	                	filesArray.add(name);
	                }
	                //System.out.println( "Dir:" + f.getAbsoluteFile() );
	            }
			}
		int i = 0;
		String[] allfiles = new String[filesArray.size()];
		for(String name : filesArray){
			allfiles[i] = name;
			i++;
		}
		return allfiles;
		
	}

	/**
	 * Retrieves an array of fileNames in the specified directory
	 * or the current if "." or null is used
	 * @param dirName
	 * @param fileExtension
	 * @return
	 */
	public static String[] getFileNames(String dirName, final String prefix, final String suffix) {
		String[] files = null;	
		if(dirName == null || dirName.equals("")){
			dirName = ".";//current directory
		}
		FileGetter.prefix = prefix;
		FileGetter.suffix = suffix;
		try {
			File dir = new File(dirName);		
			files = dir.list(new FilenameFilter(){

				@Override
				public boolean accept(File dir, String name) {
					if(name.endsWith(suffix) && name.startsWith(prefix)){
						return true;
					}else{
						return false;
					}
				}			
			});
		} catch (Exception e) {			
			//e.printStackTrace();
		}		
		
		return files;
	}

	public static File[] getFiles(String dirName, final String prefix, final String suffix) {
		File[] files = null;	
		if(dirName == null || dirName.equals("")){
			dirName = ".";//current directory
		}
		FileGetter.prefix = prefix;
		FileGetter.suffix = suffix;
		try {
			File dir = new File(dirName);
			files = dir.listFiles(new FilenameFilter(){

				@Override
				public boolean accept(File dir, String name) {
					if(name.endsWith(suffix) && name.startsWith(prefix)){
						return true;
					}else{
						return false;
					}
				}				
			});
		} catch (Exception e) {			
			//e.printStackTrace();
		}										
		return files;
	}

}
