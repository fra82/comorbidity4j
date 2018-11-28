package es.imim.ibi.comorbidity4j.server.spring.scheduled;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import es.imim.ibi.comorbidity4j.server.reservlet.ComputeComorbidityServlet;
import es.imim.ibi.comorbidity4j.server.template.TemplateUtils;
import es.imim.ibi.comorbidity4j.server.util.ServerExecConfig;

@Component
public class ResultCleanerTask { // extends TimerTask
	
	private static Integer execCounter = 0;
	
	@Scheduled(fixedRate = 3600000)
	public void run() {
		
		if(!ServerExecConfig.isOnline) {
			return;
		}
		
		execCounter++;
		
		System.out.println(execCounter + " > Cleaning result folder!");
		
		try {
			File[] fileList = (new File(ComputeComorbidityServlet.basePathResultStorage)).listFiles();

			int fileCount = 0;
			int deletedFileCount = 0;
			for(File flistElem : fileList) {

				if(flistElem != null && flistElem.exists() && flistElem.isFile()) {
					try {
						
						if(!flistElem.getName().endsWith(".html") || !flistElem.getName().startsWith("res")) {
							
						}
						
						fileCount++;
						
						long diff = (new Date()).getTime() - flistElem.lastModified();

						if (diff > 1 * 24 * 60 * 60 * 1000) {
							System.out.println(execCounter + " > Deleting file: " + flistElem.getName() + "...");
							try {
								// flistElem.delete();
								
								FileUtils.write(flistElem, TemplateUtils.generateHTMLcommonHeader(false, flistElem.getName()) + TemplateUtils.generateHTMLcustomMessage("Comorbidity4Web: the results of this comorbidity analysis have been permanently deleted.<br/>"
										+ "<br/>Comorbidity4j results are automatically permanently deleted from the server 24 hours after their generation or before if the deletion is triggered by the user."));
								deletedFileCount++;
								System.out.println(execCounter + " > Deleted file: " + flistElem.getName());
							}
							catch(Exception e) {
								System.out.println(execCounter + " > ERROR DELETING " + flistElem.getName());
								e.printStackTrace();
							}
						}
					}
					catch(Exception e) {
						System.out.println(execCounter + " > ERROR DEALING WITH FILE " + flistElem.getName());
						e.printStackTrace();
					}
				}
			}
			
			System.out.println(execCounter + " > Exec completed > files checked: " + fileCount + " - files deleted: " + deletedFileCount);
		}
		catch(Exception e) {
			System.out.println(execCounter + " > ERROR GLOBAL - RESULT DELETION");
			e.printStackTrace();
		}
	}
	
	
	@Scheduled(fixedRate = 600000)
	public void printSysInfo() {
		
		if(!ServerExecConfig.isOnline) {
			return;
		}
		
		try {
			Runtime instance = Runtime.getRuntime();
			long totalMemoryMb = instance.totalMemory() / (1024 * 1024);
			
			long heapSizeMb = instance.totalMemory(); 

	        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
	        long heapMaxSizeMb = instance.maxMemory();

	         // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
	        long heapFreeSizeMb = instance.freeMemory(); 
	        
	        System.out.println(execCounter + " > *****************************");
	        System.out.println(execCounter + " > heapsize: "+ heapSizeMb + "Mb, heapmaxsize: " + heapMaxSizeMb + "Mb, heapFreesize: " + heapFreeSizeMb + " Mb.");
	        System.out.println(execCounter + " > *****************************");

		}
		catch(Exception e) {
			System.out.println(execCounter + " > ERROR GLOBAL - PRINT SYS INFO");
			e.printStackTrace();
		}
	}
}
