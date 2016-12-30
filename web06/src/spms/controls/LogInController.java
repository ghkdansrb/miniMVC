package spms.controls;

import java.util.Map;

import javax.servlet.http.HttpSession;

import spms.bind.DataBinding;
import spms.dao.MemberDao;
import spms.vo.Member;

public class LogInController implements Controller, DataBinding {

	MemberDao memberDao;

	// memberDao가 private이기때문에 외부에서 주입가능할수있도록 set메소드 선언.
	public LogInController setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
		return this;
	}
	
	@Override
	public Object[] getDataBinders() {
		return new Object[] {
				"loginInfo", spms.vo.Member.class
				};
	}

	@Override
	public String execute(Map<String, Object> model) throws Exception {
		Member loginInfo = (Member) model.get("loginInfo");
		
		if (loginInfo.getEmail() == null) { // 입력폼을 요청할 때
			return "/auth/LogInForm.jsp";

		} else { // 회원 등록을 요청할 때
			Member member = memberDao.exist(loginInfo.getEmail(), loginInfo.getPassword());
			if (member != null) {
				HttpSession session = (HttpSession) model.get("session");
				session.setAttribute("member", member);
				return "redirect:../member/list.do";
			} else {
				return "/auth/LogInFail.jsp";
			}
		}
	}
}
