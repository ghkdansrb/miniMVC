package spms.bind;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Set;

import javax.servlet.ServletRequest;

public class ServletRequestDataBinder {
	//인스턴스를 생성할 필요 없이 클래스이름으로 바로 호출하기위해 static
	
	public static Object bind(ServletRequest request, Class<?> dataType, String dataName) throws Exception {
	//request와 dataType, dataName을 받아 dataObj를 리턴
		
		//들어오는 dataType이 기본타입, date, String 이면 
		if (isPrimitiveType(dataType)) { 
			return createValueObject(dataType, request.getParameter(dataName));
			//해당 타입의 값으로 객체를 생성하고 리턴
		}
		
		//keySet을 통해 Map객체에 담긴 매개변수의 이름들을 가져온다.
		Set<String> paramNames = request.getParameterMap().keySet();
		Object dataObject = dataType.newInstance(); //해당데이터타입의 인스턴스를 생성
		Method m = null; //set메서드를 저장할 변수
		
		//요청 매개변수의 갯수만큼 반복하면서 데이터 객체에 할당.
		for (String paramName : paramNames) {
			m = findSetter(dataType, paramName); //set메서드를 찾는다. 
			if (m != null) { //set메서드가 있다면
				m.invoke(dataObject, createValueObject(m.getParameterTypes()[0], request.getParameter(paramName)));
				//dataObject에 대해 createValueObject의 리턴값을 매개변수로 하는 set메서드를 호출한다.
			}
		}
		return dataObject;
	}

	//데이터타입이 무엇인지에 따라 true or false인지 반환
	private static boolean isPrimitiveType(Class<?> type) {
		if (type.getName().equals("int") || type == Integer.class || type.getName().equals("long") || type == Long.class
				|| type.getName().equals("float") || type == Float.class || type.getName().equals("double")
				|| type == Double.class || type.getName().equals("boolean") || type == Boolean.class
				|| type == Date.class || type == String.class) {
			return true;
		}
		return false;
	}

	//요청 매개변수의 값을 가지고 기본타입의 객체를 만들어준다.
	private static Object createValueObject(Class<?> type, String value) {
		if (type.getName().equals("int") || type == Integer.class) {
			return new Integer(value);
		} else if (type.getName().equals("float") || type == Float.class) {
			return new Float(value);
		} else if (type.getName().equals("double") || type == Double.class) {
			return new Double(value);
		} else if (type.getName().equals("long") || type == Long.class) {
			return new Long(value);
		} else if (type.getName().equals("boolean") || type == Boolean.class) {
			return new Boolean(value);
		} else if (type == Date.class) {
			return java.sql.Date.valueOf(value);
		} else {
			return value;
		}
	}

	//set메소드를 찾는다
	private static Method findSetter(Class<?> type, String name) {
		//넘어온 type의 메소드를 전부 찾는다.
		Method[] methods = type.getMethods();

		String propName = null; //set메소드의 이름을 저장할 변수
		for (Method m : methods) {
			//찾은 메서드중의 set으로 시작하지않는 메소드면 continue(반복문의 다음 조건으로 넘어감)
			if (!m.getName().startsWith("set"))
				continue;
			
			//set메서드 이름을 가져와 "set"을 자른다
			//ex. "setEmail" -> "Email"
			propName = m.getName().substring(3);
			
			//자른 메소드이름(propName)을 소문자로 바꾼 것이 데이터이름을 소문자로 바꾼것과 같으면
			//ex. propName : "Email" -> "email" 와 name : "email" - > "email" 비교
			if (propName.toLowerCase().equals(name.toLowerCase())) {
				return m; //set메소드 이름(m)을 리턴한다.
			}
		}
		return null;
	}
}
