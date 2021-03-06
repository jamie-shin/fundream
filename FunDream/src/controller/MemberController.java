package controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import model.Favorite;
import model.Fund;
import model.Member;
import model.Project;
import service.FavoriteService;
import service.FundService;
import service.MemberService;
import service.ProjectService;

@Controller
public class MemberController {

	private final String send_email = "FunDream2018@gmail.com";
	private final String send_email_pw = "1q2w3e4r`";

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private FundService fundService;
	
	@Autowired
	private FavoriteService favoriteService;
	
	@RequestMapping("MSE_JOINFORM.do") // 회원가입창 요청
	public void MSE_JOINFORM() {
	}

	// 이메일 인증코드 발송 메소드(리턴값 : 인증코드)
	public String SendCodeToEmail(String inputEmail) {
		// 랜덤 코드 8자리 생성
		Random rm = new Random();
		StringBuffer code = new StringBuffer();
		for (int i = 0; i < 8; i++) {
			if (rm.nextBoolean()) {
				code.append((char) ((int) rm.nextInt(26) + 97));
			} else {
				code.append(rm.nextInt(10));
			}
		}

		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory,fallback", "false");
		props.setProperty("mail.smtp.quitwait", "false");

		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(send_email, send_email_pw);
			}
		};

		Session session = Session.getDefaultInstance(props, auth);

		MimeMessage message = new MimeMessage(session);
		try {
			message.setSender(new InternetAddress(send_email));
			message.setSubject("FunDream 사이트 회원가입 인증코드");
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(inputEmail));

			Multipart mp = new MimeMultipart();
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText("CODE : " + code);
			mp.addBodyPart(mbp1);

			MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
			mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
			mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
			mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
			mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
			mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
			CommandMap.setDefaultCommandMap(mc);

			message.setContent(mp);
			Transport.send(message);

		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code.toString();
	}

	@RequestMapping("MSE_SENDC.do") // 이메일 인증 버튼
	public ModelAndView MSE_SENDC(@RequestParam("inputEmail") String inputEmail, Model model) {
		System.out.println(inputEmail);
		String code = SendCodeToEmail(inputEmail);
		ModelAndView mav = new ModelAndView();
		mav.addObject("inputEmail", inputEmail);
		mav.addObject("sendCode", code);
		mav.setViewName("MSE_CHECKC");
		System.out.println(code);
		return mav;
	}

	@RequestMapping("MSS_CHECKM.do") // 회원가입에서 이메일 인증전 중복검사
	public @ResponseBody String MSS_CHECKM(@RequestParam("inputEmail") String inputEmail) {
		System.out.println("이메일체크 : " + inputEmail);
		String checkEmail;
		Member m = memberService.selectOneMemberByEmail(inputEmail);
		if (m == null) {
			checkEmail = "1"; // 가입 가능한 이메일
		} else {
			checkEmail = "2"; // DB에 존재하는 이메일로 가입 불가능
		}
		System.out.println("가입 가능 : " + checkEmail);
		return checkEmail;
	}

	@RequestMapping(value="MSI_JOIN.do", method=RequestMethod.POST) // 회원가입완료 버튼
	public @ResponseBody String MSI_JOIN(HttpServletRequest request, @RequestParam("m_img") MultipartFile file) throws Exception {
/*		String url = null;
		// 파일 업로드 부분
		request.setCharacterEncoding("UTF-8");
		String realFolder = "";
		String filename1 = "";
		int maxSize = 1024 * 1024 * 5;
		String savefile = "img";
		ServletContext scontext = request.getServletContext();
		realFolder = scontext.getRealPath(savefile);

		try {
			MultipartRequest multi = new MultipartRequest(request, realFolder, maxSize, "UTF-8",
					new DefaultFileRenamePolicy());
			String m_email = multi.getParameter("m_email");
			String m_pwd = multi.getParameter("m_pwd");
			String m_name = multi.getParameter("m_name");
			String m_phone = multi.getParameter("m_phone");
			String m_birth = multi.getParameter("m_birth");
			String m_gender = multi.getParameter("m_gender");
			String m_nick = multi.getParameter("m_nick");
			Enumeration<?> files = multi.getFileNames();
			String file1 = (String) files.nextElement();
			filename1 = multi.getFilesystemName(file1);

			String fullpath = realFolder + "\\" + filename1;
			String applicationPath = request.getServletContext().getRealPath("img");
			String path = "img/" + filename1;
			// 파일형식이 jpg 또느 png일 경우에만 DB저장

			Member member = new Member();
			member.setM_email(m_email);
			member.setM_pwd(m_pwd);
			member.setM_name(m_name);
			member.setM_phone(m_phone);
			member.setM_birth(new SimpleDateFormat("yyyy-MM-dd").parse(m_birth));
			member.setM_gender(Integer.parseInt(m_gender));
			member.setM_nick(m_nick);
			if (filename1 == null) {
				member.setM_img("img/user.png");
			} else {
				System.out.println("이미지 파일 : " + filename1);
				String s = filename1.replace(".", ",");
				String[] f = s.split(",");
				if (f[f.length - 1].equalsIgnoreCase("jpg") || f[f.length - 1].equalsIgnoreCase("png") || f[f.length - 1].equalsIgnoreCase("jpeg")) {
					member.setM_img(path);
				} else {// 형식이 올바르지 않을경우 경고창 이후 history.go(-1)
					url = "redirect:alert1.do";
				}
			}
			System.out.println(member);
			memberService.insertMember(member);
			url = "redirect:MIE_LOGINFORM.do";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;*/
		
		String type = request.getParameter("type");
		String m_email = request.getParameter("m_email");
		String m_pwd = request.getParameter("m_pwd");
		String m_name = request.getParameter("m_name");
		String m_phone = request.getParameter("m_phone");
		String m_birth = request.getParameter("m_birth");
		String m_gender_str = request.getParameter("m_gender");
		String m_nick = request.getParameter("m_nick");
		
		System.out.println("========== 회원가입정보 =========");
		System.out.println("type : " + type);
		System.out.println("m_email : " + m_email);
		System.out.println("m_pwd : " + m_pwd);
		System.out.println("m_name : " + m_name);
		System.out.println("m_phone : " + m_phone);
		System.out.println("m_gender : " + m_gender_str);
		System.out.println("m_nick : " + m_nick);
		System.out.println("========== 회원가입정보 끝 =========");
		
		Member member = new Member();
		member.setM_email(m_email);
		member.setM_pwd(m_pwd);
		member.setM_name(m_name);
		member.setM_phone(m_phone);
		member.setM_birth(new SimpleDateFormat("yyyy-MM-dd").parse(m_birth));
		member.setM_gender(Integer.parseInt(m_gender_str));
		member.setM_nick(m_nick);
		
		int result = memberService.insertMember(member, type, file,request);
		
		if(result == 1) return "success";
		return "fail";
	}

	@RequestMapping("MSE_SETPWFORM.do") // 비밀번호 찾기화면 요청
	public void MSE_SETPWFORM() {
	}

	@RequestMapping("MSS_CHECKN.do") // 비밀번호 찾기에서 이메일 인증 전 이메일,이름 유무검사
	public @ResponseBody String MSS_CHECKN(String email, String name) {
		System.out.println("회원 체크) email : " + email + " /  name : " + name);
		Member m = memberService.selectOneMemberByEmail2(email, name);
		if (m != null) {
			String code = SendCodeToEmail(email);
			return code;
		} else { // 이메일로 조회했을 때 회원정보가 없거나, 이메일로 조회한 회원의 연락처와 입력한 연락처가 다른 경우
			return null;
		}
	}

	@RequestMapping("MSE_SETPW.do") // 비밀번호 찾기(이메일인증 코드 확인),필요없을듯.... jquery에서 확인...
	public void MSE_CODE(String email) {
		System.out.println("비밀번호 찾기 이메일 : " + email);
	}

	@RequestMapping("MSS_SETPW.do") // DB접속해서 맞는지 확인
	public @ResponseBody String MSS_SETPW(String email, String name) {
		System.out.println("DB확인) 이메일 : " + email + " / 연락처 : " + name);
		Member m = memberService.selectOneMemberByEmail(email);
		System.out.println("입력된 이름 : " + name);
		if (m != null) {
			String db_Name = m.getM_name();
			System.out.println("저장된 이름 : " + db_Name);
			if (db_Name.trim().equals(name.trim())) {
				String code = SendCodeToEmail(email);
				System.out.println(code + " / 회원있고 번호일치함");
				return code;
			} else {
				System.out.println("회원있지만 번호불일치함!");
				return null;
			}
		} else {
			System.out.println("회원없음!!");
			return null;
		}
	}

	@RequestMapping("MSE_NEWPWFORM.do") // 새비밀번호 설정화면 요청(추가한거)
	public void MSE_NEWPWFORM() {
	}

	@RequestMapping("MSU_SETPW.do") // 새비밀번호 설정
	public String MSU_SETPW(String email, String inputPwd, String inputPwdCheck) {
		System.out.println("PW 변경할 이메일 : " + email);
		System.out.println("입력한 비밀번호 : " + inputPwd);
		System.out.println("확인 비밀번호 : " + inputPwdCheck);
		String redirect = "";
		if (inputPwd.trim().equals(inputPwdCheck.trim())) {
			Member member = memberService.selectOneMemberByEmail(email);
			System.out.println("비번새설정 : " + member);
			member.setM_pwd(inputPwd);
			int result_pwd = memberService.updatePassword(member);
			if (result_pwd == 1) {
				System.out.println("변경 성공!");
				redirect = "redirect:MSE_NEWPWFORM.do";
			} else {
				System.out.println("변경 실패...");
			}
		} else {
			System.out.println("비밀번호 일치하지 않음!!");
		}
		return redirect;
	}

	@RequestMapping("MIE_LOGINFORM.do") // 메뉴바에서 로그인버튼 눌렀을떄
	public String MIE_LOGINFORM(HttpServletRequest req, HttpServletResponse repo) {
		String url = "";
		String loginEmail = (String) req.getSession().getAttribute("m_email");
		System.out.println(loginEmail);
		if (loginEmail == null) {
			url = "MIE_LOGINFORM";
		} else {
			url = "MAIN";
		}
		return url;
	}
	
	 @RequestMapping(value="MIS_LOGIN.do", method=RequestMethod.POST, produces="application/text;charset=UTF-8") // 로그인창에서 로그인버튼
	    public @ResponseBody String MIS_LOGIN(HttpServletRequest request, HttpServletResponse reponse, HttpSession session, String m_email, String m_pwd) throws UnsupportedEncodingException {
	     
		 request.setCharacterEncoding("UTF-8");
		 reponse.setCharacterEncoding("UTF-8");
		 
		 String checkEmail ="";
	        System.out.println("이메일: "+m_email);
	        System.out.println("비밀번호: "+m_pwd);
	        int result = memberService.MIS_LOGIN(m_email, m_pwd);
	 
	        System.out.println("RESULT: " + result);
	 
	        if (result == 1 || result == 3 || result == 5 || result == 6) {
	            if (result == 1) {
	                checkEmail = "xemail";//("msg", "해당 이메일로 등록된 회원이 없습니다.");
	            } else if (result == 3) {
	            	checkEmail = "xpwd"; //("msg", "비밀번호를 확인해 주세요.");
	            } else if (result == 5) {
	               checkEmail="leave";//("msg", "탈퇴 처리된 아이디입니다.");
	            } else if (result == 6) {
	                checkEmail="invalid"; //"msg", "이용 정지된 아이디입니다.");
	            }

	        } 
	        else {
	            session.setAttribute("m_email", m_email);
	            Member m = memberService.selectOneMemberByEmail(m_email);
	            String m_img = m.getM_img();
	            int m_id = m.getM_id();
	            String m_name = m.getM_name();
	            session.setAttribute("m_img", m_img);
	            session.setAttribute("m_id", m_id);
	            if (result == 2) {
	                // String m_manager= "m_manager";
	                session.setAttribute("m_manager", 1);
	                checkEmail = m.getM_nick();
	                System.out.println(session);
	            } else if (result == 4) {
	                session.setAttribute("m_manager", 2);
	                checkEmail = m.getM_nick();
	            }
	        }
	        return checkEmail;
	    }

	@RequestMapping("MOE.do") // 메뉴바에서 로그아웃버튼
	public String MOE(HttpSession session) {
		session.removeAttribute("m_email");
		session.removeAttribute("m_id");
		session.removeAttribute("m_img");
		session.removeAttribute("m_manager");
		return "redirect:MAIN.do";
	}

	@RequestMapping("MUE_CHECKPW.do") // 내정보 수정 눌렀을 시 비밀번호 확인창
	public void MUE_CHECKPW() {}

	@RequestMapping("MUS_CHECKPW.do")  // DB에 저장된 비밀번호와 사용자가 입력한 비밀번호가 일치하는지 확인
	public @ResponseBody String MUS_CHECKPW(String m_email, String m_pwd_input) {
		System.out.println(m_email + " : " + m_pwd_input);
		int result = memberService.MUS_CHECKPW(m_pwd_input, m_email);
		System.out.println("비빌번호 확인 결과 : " + result);
		if (result == 1) return "success";
		else return "fail";
	}
	
	@RequestMapping("MUE_MODIFYFORM.do") // 내정보수정 화면 이동
	public ModelAndView MUE_MODIFYFORM(String m_email) {
		ModelAndView mav = new ModelAndView();
		Member member = memberService.selectOneMemberByEmail(m_email);
		System.out.println("member: " + member);
		String phone = member.getM_phone();
		String[] array = phone.split("-");
		mav.addObject("phone", array);
		mav.addObject("member", member);
		mav.setViewName("MUE_MODIFYFORM");
		return mav;
	}

	@RequestMapping("MUU_MODIFY.do") // 정보수정 확인
	public @ResponseBody String MUU_MODIFY(HttpServletRequest request, @RequestParam("m_img") MultipartFile file) {

		String type = "member";
		String m_email = request.getParameter("m_email");
		Member member = memberService.selectOneMemberByEmail(m_email);

		String m_pwd = request.getParameter("m_pwd");
		int result_pwd = 0;
		if(!m_pwd.equals("null")) {
			member.setM_pwd(m_pwd);
			result_pwd = memberService.updatePassword(member);
		}
		
		String m_phone = request.getParameter("m_phone");
		if(!m_phone.isEmpty()) member.setM_phone(m_phone);
		
		String m_nick = request.getParameter("m_nick");
		if(!m_nick.isEmpty()) member.setM_nick(m_nick);
		
		System.out.println("========== 회원 수정 정보 =========");
		System.out.println("type : " + type);
		System.out.println("m_email : " + m_email);
		System.out.println("m_pwd : " + m_pwd);
		System.out.println("m_phone : " + m_phone);
		System.out.println("m_nick : " + m_nick);
		System.out.println("========== 회원 수정 정보 끝 =========");
		
		int result = memberService.updateMember(member, type, file,request);
		
		if(result == 1) {
			// 입력한 비밀번호가 null이 아니고 일반 정보 수정 결과 값과 비밀번호 수정 결과 값이 모두 1인 경우
			if(!m_pwd.equals("null") && result_pwd == 1) return "success";
			// 입력한 비밀번호가 null이고 일반 정보 수정 결과 값이 1일 경우
			else if(m_pwd.equals("null") && result_pwd == 0) return "success";
		}
		return "fail";
		
	}

	@RequestMapping("MUU_LEAVE.do") // 회원 탈퇴
	public String MUU_LEAVE(String m_email, HttpSession session) {
		System.out.println("이메일 : " + m_email);
		memberService.MUU_LEAVE(m_email);
		session.removeAttribute("m_email");
		session.removeAttribute("m_id");
		session.removeAttribute("m_img");
		session.removeAttribute("m_manager");
		System.out.println("성공1");
		return "redirect:MAIN.do";
	}
	
	//해당 아이디에 만든프로젝트가 존재하는지
	@RequestMapping("checkProject.do")
	public @ResponseBody String checkProject(int m_id) {
		List<Project> projectList = projectService.getProjectById(m_id);
		if(projectList.size() > 0) {
			return "1";
		}
		return "null";
	}
	
	//해당 아이디에 후원한프로젝트가 있는지
	@RequestMapping("FundCheck.do")
	public @ResponseBody String FundCheck(int m_id) {
		List<Fund> f_list = fundService.selectAllFundByM_id(m_id);
		if(f_list.size()>0) {
			return "have";
		}
		return"none";
	}
	
	// 내 관심 프로젝트 조회
	@RequestMapping("MLS_LIKE.do")
	public ModelAndView MLS_LIKE(HttpServletRequest req) {
		ModelAndView mav = new ModelAndView();
		int m_id = (Integer)req.getSession().getAttribute("m_id");
		
		Member member = memberService.selectOneMemberById(m_id);
		mav.addObject("loginMember", member);
		
		List<Favorite> favoriteList = favoriteService.selectFavoritesById(m_id);
		List<Project> myFavoriteList = new ArrayList<>();
		
		if(!favoriteList.isEmpty()) {
			mav.addObject("favoriteList", favoriteList);
			for(int i = 0; i < favoriteList.size(); i++) {
				int p_index = favoriteList.get(i).getP_index();
				Project project = projectService.getOneProject(p_index);
				myFavoriteList.add(project);
			}
			mav.addObject("myFavoriteList", myFavoriteList);
			System.out.println("========관심 프로젝트 시작========");
			for(Favorite f : favoriteList) {
				System.out.println(f);
			}
			System.out.println("========관심 프로젝트 종료========");
		}
		else {
			mav.addObject("msg", "noFavorite");
		}
		
		mav.setViewName("MLS_LIKE");	
		
		return mav;
		
	}
	
	// 멤버 이미지 다운르도 
	@RequestMapping("downloadM.do")
	public View downloadM(String m_id_str) {
		//해당게시물의 파일정보를 이용해서 파일을 가져옴	
		int m_id = Integer.parseInt(m_id_str);
		String type = "member";
		File attachFile = memberService.getAttachFile(m_id, type);
		//커스텀 뷰인 DownloadView를 이용해서 전달
		View view = new DownloadView(attachFile);
		return view;
	}
	
}
