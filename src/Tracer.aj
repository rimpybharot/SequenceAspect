
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

import org.aspectj.lang.reflect.MethodSignature;


public aspect Tracer {
	static boolean running;
	public static String sequence = "";

	ArrayList<String> caller = new ArrayList<String>();

	pointcut traceMethods()               
	: 	(execution(* *.*(..)))
	&& if(running)
	;


	before() : traceMethods() {
		caller.add(0, "InitialActor");

		if(
				(thisJoinPoint.getThis()!=null)
				&& 
				(thisJoinPointStaticPart.getKind()=="method-execution")
				&& (thisJoinPoint.getTarget()!=null)
				
				){


			String method = thisJoinPointStaticPart.getSignature().toString();
			MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
			Method method1 = signature.getMethod();


			boolean first = true;
			String paramTypes = new String();

			for(Parameter p : method1.getParameters()){

				if(!first){
					paramTypes += "," + p.getType().getSimpleName();
				}
				else{
					paramTypes += p.getType().getSimpleName();
					first = false;
				}
			}

			String returnString = "";


			if((method1.getReturnType() != null)
					&&
					(method1.getReturnType()!=void.class)){
				returnString = thisJoinPoint.getTarget().getClass().getSimpleName()
						+ "-->"
						+ caller.get(caller.size()-1);
			}

			if(!returnString.contains("-->")){
				returnString= "";

			}

			String returnType = method.subSequence(0, method.indexOf(" ")).toString();
			sequence += "\n"+caller.get(caller.size()-1)
			+ " ->"
			+
			thisJoinPoint.getTarget().getClass().getSimpleName()
			+ ":"  + thisJoinPointStaticPart.getSignature().getName()
			+ "("+paramTypes+"):"
			+ returnType
			+"\n" + returnString
			+ "\nactivate " + thisJoinPoint.getTarget().getClass().getSimpleName()
			;
			caller.add(caller.size(), thisJoinPoint.getTarget().getClass().getSimpleName() );
		}
	}


	after() : traceMethods() {
		
		if(
				(thisJoinPoint.getThis()!=null)
		&& 
		(thisJoinPointStaticPart.getKind()=="method-execution")
		&& (thisJoinPoint.getTarget()!=null)
		){		sequence += "\ndeactivate " + caller.get(caller.size()-1) + "\n";


		caller.remove(caller.size()-1);
		}
	}

	public static String getSequence() {

		return sequence;
	}





}
