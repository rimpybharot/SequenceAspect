
import java.io.File;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import org.apache.commons.io.FileUtils;

import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class Compile {

	public void compile(String source1, String packageName1) throws Exception {


		File source = new File(source1);
		
		
		String original = System.getProperty("java.home");
		
		System.setProperty("java.home", "C:\\Program Files\\Java\\jdk1.8.0_112");

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new
				DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager =
				compiler.getStandardFileManager(diagnostics, Locale.getDefault(),
						null);

		String packageName = packageName1;

		if(source.exists()){
			File dest = new File("src//"+packageName);

			List<JavaFileObject> javaObjects =
					scanRecursivelyForJavaObjects(source, fileManager, dest);



			if (javaObjects.size() == 0) {
				throw new Exception("There are no source files to compile in " + source.getAbsolutePath());
			}


			CompilationTask compilerTask = compiler.getTask(null,
					fileManager, diagnostics, null , null, javaObjects) ;

			if (compilerTask.call()) {
				System.out.println("Compilation Success");
			
			}
			else {
				for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
					System.err.format("Error on line %d in %s",
							diagnostic.getLineNumber(), diagnostic);
				}
				throw new Exception("Could not compile project");
			}
			System.setProperty("java.home", original);
			
			JarCreator jarcreator = new JarCreator();
			jarcreator.run();
		}

	}

	private static List<JavaFileObject> scanRecursivelyForJavaObjects(File source, StandardJavaFileManager fileManager, File dest) throws IOException {

		List<JavaFileObject> javaObjects = new LinkedList<JavaFileObject>();
		if (source.isDirectory()){
			File[] files = source.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					javaObjects.addAll(scanRecursivelyForJavaObjects(file, fileManager, dest));
				}
				else if (file.isFile() &&
						file.getName().toLowerCase().endsWith(".java")) {
					FileUtils.copyFileToDirectory(file, dest);
					javaObjects.add(readJavaObject(file, fileManager));
				}
			}
		}


		return javaObjects;
	}

	private static JavaFileObject readJavaObject(File file, StandardJavaFileManager fileManager) {
		Iterable<? extends JavaFileObject> javaFileObjects =
				fileManager.getJavaFileObjects(file);
		Iterator<? extends JavaFileObject> it = javaFileObjects.iterator();
		if (it.hasNext()) {
			return it.next();
		}
		throw new RuntimeException("Could not load " +
				file.getAbsolutePath() + " java file object");
	}
}