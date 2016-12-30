package spms.controls;

import java.util.Map;

import spms.bind.DataBinding;
import spms.dao.MemberDao;
import spms.vo.Member;

//클라이언트가 보낸 데이터를 프론트 컨트롤러부터 받아야하기 때문에 DataBinding 구현
public class MemberAddController implements Controller, DataBinding {

	MemberDao memberDao;

	// memberDao가 private이기때문에 외부에서 주입가능할수있도록 set메소드 선언.
	public MemberAddController setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
		return this;
	}
	
	@Override
	public Object[] getDataBinders() {
		return new Object[] {"member", spms.vo.Member.class};
		//PageController가 필요한 데이터가 무엇인지 Object타입으로 리턴
		//FrontController에서 통일성을 갖기위해 Object타입의 배열로 리턴	
	}

	@Override
	public String execute(Map<String, Object> model) throws Exception {
		Member member = (Member) model.get("member");
		if (member.getEmail() == null) { // 입력폼을 요청할 때
			return "/member/MemberForm.jsp";

		} else { // 회원 등록을 요청할 때	
			memberDao.insert(member);
			return "redirect:list.do";
		}
	}

}
