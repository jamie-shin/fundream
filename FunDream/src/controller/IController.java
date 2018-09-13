package controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import model.Category;
import model.Member;
import model.Project;
import service.CategoryService;
import service.MemberService;
import service.ProjectService;
import service.RewardService;

@Controller
public class IController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private RewardService rewardService;
	
	@Autowired
	private CategoryService categoryService;
	
	// 관리자 메인 화면 요청
	@RequestMapping("IBE_MANAGER.do")
	public void IBE_MANAGER() {}
	
	// 관리자 - 카테고리 목록 화면 요청
	@RequestMapping("ICS_LIST.do")
	public ModelAndView ICS_LIST() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("pCategoryList", categoryService.getCategoryListByType(1));
		mav.addObject("rCategoryList", categoryService.getCategoryListByType(2));
		mav.setViewName("ICS_LIST");
		return mav;
	}
	
	// 관리자 - 카테고리 생성
	@RequestMapping("ICI.do")
	public @ResponseBody int ICI(String ct_name, String ct_type) {
		Category category = new Category();
		category.setCt_name(ct_name);
		try {
			category.setCt_type(Integer.parseInt(ct_type));
		} catch (Exception e) {
			return -1;
		}
		int result = categoryService.insertCategory(category);
		if(result == 1) {
			return category.getCt_index();
		}
		else {
			return 0;
		}
	}
	
	// 관리자 - 카테고리 삭제
	@RequestMapping("ICD.do")
	public @ResponseBody String ICD(int ct_index) {
		System.out.println("삭제할 카테고리 인덱스 : " + ct_index);
		List<Project> projectList = projectService.getProjectsByCategory(ct_index);
		if(projectList.size() > 0 || projectList != null) {  // 해당 카테고리 인덱스로 조회한 프로젝트가 있을 때
			return "disable";
		}
		else {
			int result = categoryService.deleteOneCategory(ct_index);
			System.out.println("수정 결과 : " + result);
			if(result == 1) {
				return "true";
			}
			else {
				return "false";
			}
		}
	}
	
	// 관리자 - 카테고리명 수정
	@RequestMapping("ICU.do")
	public @ResponseBody String ICU(int ct_index, String ct_name) {
		System.out.println("수정할 카테고리 인덱스 : " + ct_index);
		Category category = new Category();
		category.setCt_index(ct_index);
		category.setCt_name(ct_name);
		int result = categoryService.updateOneCategory(category);
		System.out.println("수정 결과 : " + result);
		if(result == 1) {
			return "true";
		}
		else {
			return "false";
		}
	}

	
	// 관리자 - 회원 목록 화면 요청
	@RequestMapping("IMS_LISTFORM.do")
	public ModelAndView IMS_LISTFORM() {
		ModelAndView mav = new ModelAndView();
		List<Member> memberList = memberService.getAllMembers();
		mav.addObject("memberList", memberList);
		mav.setViewName("IMS_LISTFORM");
		return mav;
	}
	
	// 관리자 - 전체 회원 검색
	@RequestMapping("IMS_ALLMEMBERS.do")
	public @ResponseBody Map IMS_ALLMEMBERS(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("resultMap", memberService.getAllMembers());

		Set<String> keys = resultMap.keySet();
		for(String key : keys) {
			System.out.println(key + " : " + resultMap.get(key));
		}
		
		return resultMap;
	}
	
	// 관리자 - 회원 타입별 검색
	@RequestMapping(value="IMS_SEARCH.do", method=RequestMethod.POST)
	public @ResponseBody Map IMS_SEARCH(@RequestParam(required=false) int sc_type, @RequestParam(required=false) int sc_detail, @RequestParam(required=false) String keyword) {
		
		HashMap<String, Object> searchMap = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		if(!keyword.isEmpty()) {
			searchMap.put("keyword", keyword);
		}
		
		switch(sc_type) {
		case 0:
			resultMap.put("resultMap", memberService.getMembersByKeyword(searchMap));
			break;
		// 관리자 여부 조회
		case 1:
			switch(sc_detail) {
			case 1:  // 관리자 조회
				searchMap.put("m_manager", 1);
				resultMap.put("resultMap" ,memberService.getMembersByManager(searchMap));
				break;
			case 2:  // 일반회원 조회
				searchMap.put("m_manager", 2);
				resultMap.put("resultMap" ,memberService.getMembersByManager(searchMap));
				break;
			}
			break;
		// 활동 여부 조회
		case 2:
			switch(sc_detail) {
			case 1:  // 활동 중 회원 조회
				searchMap.put("m_valid", 1);
				resultMap.put("resultMap" ,memberService.getMembersByValid(searchMap));
				break;
			case 2:  // 탈퇴 회원 조회
				searchMap.put("m_valid", 2);
				resultMap.put("resultMap" ,memberService.getMembersByValid(searchMap));
				break;
			case 3:  // 이용 정지 회원 조회
				searchMap.put("m_valid", 3);
				resultMap.put("resultMap" ,memberService.getMembersByValid(searchMap));
				break;
			}
			break;
		}
		
		Set<String> keys = resultMap.keySet();
		for(String key : keys) {
			System.out.println(key + " : " + resultMap.get(key));
		}
		
		return resultMap;
	}
	
	// 관리자 - 회원 상세보기 화면 요청
	@RequestMapping("IMS_DETAILFORM.do")
	public ModelAndView IMS_DETAILFORM(String m_id_str) {
		int m_id = 0;
		try {
			m_id = Integer.parseInt(m_id_str);
		}
		catch (Exception e) {
			System.out.println("숫자 변환 불가");
		}
		ModelAndView mav = new ModelAndView();
		mav.addObject("member", memberService.selectOneMemberById(m_id));
		mav.addObject("projectList", projectService.getProjectById(m_id));
		mav.setViewName("IMS_DETAILFORM");
		return mav;
	}
	
	// 관리자 - 관리자 변경
	@RequestMapping("IMU_GRANT.do")
	public @ResponseBody int IMU_GRANT(int m_id, int m_manager) {
		Member member = memberService.selectOneMemberById(m_id);
		member.setM_manager(m_manager);
		
		int result = memberService.updateMember(member);
		if(result == 1) {
			return memberService.selectOneMemberById(m_id).getM_manager();
		}
		return 0;
	}
	
	// 관리자 - 회원 유형 변경
	@RequestMapping("IMU_SLEEP.do")
	public @ResponseBody int IMU_SLEEP(int m_valid, int m_id) {
		Member member = memberService.selectOneMemberById(m_id);
		member.setM_valid(m_valid);
		
		int result = memberService.updateMember(member);
		if(result == 1) {
			return memberService.selectOneMemberById(m_id).getM_valid();
		}
		return 0;
	}
	
}