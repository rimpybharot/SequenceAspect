

import java.io.BufferedInputStream;
import java.io.*;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.*;

public class JarCreator {
	public void run() throws IOException
	{
		
	  Manifest manifest = new Manifest();
	  manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
//	  manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "MyCode");
	  JarOutputStream target = new JarOutputStream(new FileOutputStream("output.jar"), manifest);
	  File classDir = new File("bin//");
	  File srcdir = new File("src//");
//	  jardir.mkdir();
	  add(classDir, target);
	  add(srcdir, target);

	  target.close();
	}

	private void add(File source, JarOutputStream target) throws IOException
	{
	  BufferedInputStream in = null;
	  try
	  {
	    if (source.isDirectory())
	    {
	      String name = source.getPath().replace("\\", "/");
	      if (!name.isEmpty())
	      {
	        if (!name.endsWith("/"))
	          name += "/";
	        JarEntry entry = new JarEntry(name);
	        entry.setTime(source.lastModified());
	        target.putNextEntry(entry);
	        target.closeEntry();
	      }
	      for (File nestedFile: source.listFiles())
	        add(nestedFile, target);
	      return;
	    }

	    JarEntry entry = new JarEntry(source.getPath().replace("\\", "/"));
	    entry.setTime(source.lastModified());
	    target.putNextEntry(entry);
	    in = new BufferedInputStream(new FileInputStream(source));

	    byte[] buffer = new byte[1024];
	    while (true)
	    {
	      int count = in.read(buffer);
	      if (count == -1)
	        break;
	      target.write(buffer, 0, count);
	    }
	    target.closeEntry();
	  }
	  finally
	  {
	    if (in != null)
	      in.close();
	  }
	}

}
