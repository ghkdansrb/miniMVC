package spms.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spms.bind.DataBinding;
import spms.bind.ServletRequestDataBinder;
import spms.controls.Controller;

@SuppressWarnings("serial")
@WebServlet("*.do")
public class DispatcherServlet extends HttpServlet {
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		String servletPath = request.getServletPath(); // 요청되는 url을 가져온다.
		try {

			HashMap<String, Object> model = new HashMap<String, Object>();
			ServletContext sc = this.getServletContext();

			// Login을 위한 session
			model.put("session", request.getSession());

			Controller pageController = (Controller) sc.getAttribute(servletPath);

			// pageController가 DataBinding에 들어갈수 있으면 true
			if (pageController instanceof DataBinding) {
				 // DataBinding을 구현한 PageController만 메소드(prepareRequestData)를 실행
				  prepareRequestData(request, model, (DataBinding)pageController);
			}

			// 컨트롤러 호출을 통해 View 이름을 리턴받는다.
			String viewUrl = pageController.execute(model);
			// Map안의 내용을 request의 attribute에 셋팅
			for (String key : model.keySet()) {
				// 모델의 keySet 만큼 돌린다.
				// 모델의 key 값 = String
				request.setAttribute(key, model.get(key));
			}

			// viewUrl이 redirect로 시작하면 redirect하고 아니면 include한다.
			if (viewUrl.startsWith("redirect:")) {
				response.sendRedirect(viewUrl.substring(9));
				return;
			} else {
				RequestDispatcher rd = request.getRequestDispatcher(viewUrl);
				rd.include(request, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", e);
			RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
			rd.forward(request, response);
		}
	}

	// model 객체를 만들어주는 메소드
	private void prepareRequestData(HttpServletRequest request, HashMap<String, Object> model, DataBinding dataBinding)
			throws Exception {
		Object[] dataBinders = dataBinding.getDataBinders();
		
		// 만들어야 할 객체타입과 객체이름
		String dataName = null;
		Class<?> dataType = null; // 클래스타입을 모르므로 제너릭을 ? 로
		Object dataObj = null;

		// 데이터이름과 데이터타입을 꺼내기 위해 2씩 증가시킨다.
		for (int i = 0; i < dataBinders.length; i += 2) {
			dataName = (String) dataBinders[i]; // i번째는 데이터이름
			dataType = (Class<?>) dataBinders[i + 1]; // i+1번째는 데이터 타입
			// bind메소드 호출 -> 데이터타입과 이름, request안의 데이터값을 바인딩
			dataObj = ServletRequestDataBinder.bind(request, dataType, dataName);
			model.put(dataName, dataObj); // model에 put
		}

	}
}
