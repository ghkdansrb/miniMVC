package spms.controls;

import java.util.Map;

public interface Controller {
  String execute(Map<String, Object> model) throws Exception;
  //리턴값을 가지고 view를 이용한다.
  //Map : 키값형태로 담을수있다.
  //입력된값을 map으로 넘겨받는다............
  //
}
