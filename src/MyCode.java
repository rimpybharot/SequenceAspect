
import java.io.File;

import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class MyCode {

	public static void main(String[] args) throws Exception {
		Tracer.running=false;
		String original = System.getProperty("java.home");

		String mainFileName = args[1].toString();
		String packageName = null;

		if(args.length>2 ){
			packageName = args[2].toString();
		}
		else{
			packageName = "";

		}

		Compile c = new Compile();
		c.compile(args[0], packageName);


		String mainClassWithPackage  = mainFileName;

		if(packageName != ""){
			System.out.println("here");

			mainClassWithPackage =  packageName+"."+mainFileName;

		}
		else{
			mainClassWithPackage  = mainFileName;
		}


		System.out.println(mainClassWithPackage);
		
		
		String classDir = "bin\\"+packageName;
		File classesDir = new File(classDir);
		URL url = classesDir.toURI().toURL();
		
		System.out.println(url);

//
//	    ClassLoader parentClassLoader = MyClassLoader.class.getClassLoader();
//	    MyClassLoader myclassLoader = new MyClassLoader(parentClassLoader);
//	    
//	    


        


//	    myclassLoader.loadClass(mainClassWithPackage , url);
	    
		Tracer.running=true;
		
		/*
		 * 
		 */
		
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        Class<?> loadedMyClass = classLoader.loadClass(mainClassWithPackage);

        System.out.println("Loaded class name: " + loadedMyClass.getName());


		Method meth = loadedMyClass.getMethod("main", String[].class);
		String[] params = null;
		meth.invoke(null, (Object) params);
        
        /*
         * 
         */
		
		
//		Class<?> cls = Class.forName(mainClassWithPackage);
//		Method meth = cls.getMethod("main", String[].class);
//		String[] params = null;
//		meth.invoke(null, (Object) params);

		String fileName = "output.png";
		System.out.println("Output file located at " + fileName);
		SourceStringReader reader = new SourceStringReader("@startuml\nactor InitialActor"
				+ Tracer.getSequence()
				+"\n@enduml");

		System.clearProperty("java.home");
		System.setProperty("java.home", original);
		Tracer.running=false;

		FileOutputStream output = new FileOutputStream(new File(fileName));
		reader.generateImage(output, new FileFormatOption(FileFormat.PNG));
		//		}
	}

}




