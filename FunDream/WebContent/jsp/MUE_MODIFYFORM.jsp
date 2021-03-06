<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="css/ChangeInformation.css">
<title>FunDream_Login</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script src="https://rawgit.com/enyo/dropzone/master/dist/dropzone.js"></script>
<link rel="stylesheet" href="https://rawgit.com/enyo/dropzone/master/dist/dropzone.css">
<script type="text/javascript">	
	$(function(){

		// 비밀번호 검증(8~20자리, 공백불가, 영문&숫자&특수문자조합, 같은 문자 3번 이상 사용불가)
		$(document).find('#inputPwd').on('change',function(){
			var pwd = $('#inputPwd').val();
			var pwdCheck = $('#inputPwdCheck').val();
			
			if(pwdCheck==""){
				var num = pwd.search(/[0-9]/g);
				var eng = pwd.search(/[a-z]/ig);
				var spe = pwd.search(/[`~!@@#$%^&*|₩₩₩'₩";:₩/?]/gi);

				if(pwd.length < 8 || pwd.length > 20){
				 	$('#msgPwd').text("8자리 ~ 20자리 이내로 입력해주세요.");
				 	$('#inputPwd').val("");
					return false;
				}
				if(pwdCheck.search(/₩s/) != -1){
					$('#msgPwd').text("비밀번호는 공백없이 입력해주세요.");
				 	$('#inputPwd').val("");
					return false;
				}
				if(num < 0 || eng < 0 || spe < 0){
					$('#msgPwd').text("영문, 숫자, 특수문자를 혼합하여 입력해주세요.");
				 	$('#inputPwd').val("");
					return false;
				}
				if(/(\w)\1\1\1/.test(pwd)){
					$('#msgPwd').text('444같은 문자를 3번 이상 사용하실 수 없습니다.');
				 	$('#inputPwd').val("");
					return false;
				}
			}
			if(pwd!="" && pwdCheck!="" && pwd!=pwdCheck){
				$('#msgPwd').text('비밀번호가 일치하지 않습니다.');
			 	$('#inputPwd').val("");
			 	$('#inputPwdCheck').val("");
				return false;
			}
			$('#msgPwd').text('사용가능한 비밀번호입니다.');
			return true;			
		});
		
		// 비밀번호 확인 검증(8~20자리, 공백불가, 영문&숫자&특수문자조합, 같은 문자 3번 이상 사용불가)
		$(document).find('#inputPwdCheck').on('change',function(){
			var pwdCheck = $('#inputPwdCheck').val();
			var pwd = $('#inputPwd').val();
			
			if(pwd==""){
				var num_c = pwdCheck.search(/[0-9]/g);
				var eng_c = pwdCheck.search(/[a-z]/ig);
				var spe_c = pwdCheck.search(/[`~!@@#$%^&*|₩₩₩'₩";:₩/?]/gi);

				if(pwdCheck.length < 8 || pwdCheck.length > 20){
					$('#msgCheckPwd').text("8자리 ~ 20자리 이내로 입력해주세요.");
				 	$('#inputPwdCheck').val("");
					return false;
				}
				if(pwdCheck.search(/₩s/) != -1){
					$('#msgCheckPwd').text("비밀번호는 공백없이 입력해주세요.");
				 	$('#inputPwdCheck').val("");
					return false;
				}
				if(num_c < 0 || eng_c < 0 || spe_c < 0){
					$('#msgCheckPwd').text("영문, 숫자, 특수문자를 혼합하여 입력해주세요.");
				 	$('#inputPwdCheck').val("");
					return false;
				}
				if(/(\w)\1\1\1/.test(pwdCheck)){
					$('#msgCheckPwd').text('444같은 문자를 3번 이상 사용하실 수 없습니다.');
				 	$('#inputPwdCheck').val("");
					return false;
				}
			}
			if(pwd!="" && pwdCheck!="" && pwd!=pwdCheck){
				$('#msgCheckPwd').text('비밀번호가 일치하지 않습니다.');
			 	$('#inputPwd').val("");
			 	$('#inputPwdCheck').val("");
				return false;
			}
			$('#msgCheckPwd').text('사용가능한 비밀번호입니다.');
			return true;			
		});
		
		// 이름 검증(한글만 가능, 숫자&영문&특수문자 불가)
		$(document).find('#inputName').keyup(function(event){
			 var kor = /[a-z0-9]|[ \[\]{}()<>?|`~!@#$%^&*-_+=,.;:\"'\\]/g;
			 var name = $(this).val();
			 if(kor.test(name)){
				 $('#msgName').text("한글만 입력 가능합니다.");
			 	$(this).val(name.replace(kor,''));
			 }
			 else{
				 $('#msgName').text("");
			 }
		});
		
		// 연락처, 생년월일 숫자 길이 제한
		$(document).find("#inputPhone2").keyup(function(event){
			var p2 = $(this).val();
			if($("#inputPhone2").val().length >= 4){
				if($(this).val().length > 4){
					$(this).val(p2.substr(0,4));
				}
				$("#inputPhone3").focus();
				return false;
			}
		});
		$(document).find("#inputPhone3").keyup(function(event){
			var p3 = $(this).val();
			if($("#inputPhone3").val().length >= 4){
				if($(this).val().length > 4){
					$(this).val(p3.substr(0,4));
				}
				return false;
			}
		});

		// 닉네임 검증(한글&영문&숫자 가능, 특수문자만 불가)
		$(document).find('#inputNick').keyup(function(event){
			var spec = /[`~!@@#$%^&*|₩₩₩'₩";:₩/?]/gi;
			nick = $(this).val();
			if(spec.test(nick)){
				$('#msgNick').text('특수문자는 사용 불가능합니다.');
				$(this).val(nick.replace(spec,''));
			}
			else{
				$('#msgNick').text("");
			}
		});
		
		// 회원가입 버튼 클릭 시
		$(document).find('#updateSubmit').on('click',function(){
			var m_email = $('#inputEmail').val();
			var m_pwd = $('#inputPwd').val();
			var m_pwdCheck = $('#inptuPwdCheck').val();
			var m_name = $('#inputName').val();
			var phone1 = $('#inputPhone1').val();
			var phone2 = $('#inputPhone2').val();
			var phone3 = $('#inputPhone3').val();
			var m_phone = phone1 + "-" + phone2 + "-" + phone3;
			var m_nick = $('#inputNick').val();
			var m_img = $('#inputImg').attr('src');
			
			if(m_pwd != "" && m_pwdCheck == ""){
				$('#msgCheckPwd').text('비밀번호 확인을 입력하세요');
				return false;
			}
			if(m_pwd == "" && m_pwdCheck != ""){
				$('#msgPwd').text('비밀번호를 입력하세요');
				return false;
			}
			if(m_name == ""){
				$('#msgName').text('이름을 입력하세요.');
				return false;
			}
			if(m_name != ""){
				$('#msgName').text('');
			}
			if(phone1 == "" || phone2 == "" || phone3 == ""){
				$('#msgPhone').text('연락처를 입력하세요.');
				return false;
			}
			if(phone1 != "" && phone2 != "" && phone3 != ""){
				$('#msgPhone').text('');
				return false;
			}
			if(m_nick == ""){
				$('#msgNick').text('닉네임을 입력하세요.');
				return false;
			}
			if(m_nick != ""){
				$('#msgNick').text('');
				return false;
			}
			
			switch(m_pwd){
			case "":
				$('#m_pwd').val("null");
				break;
			default:
				$('#m_pwd').val(m_pwd);
				break;
			}
			$('#m_email').val(m_email);
			$('#m_name').val(m_name);
			$('#m_phone').val(m_phone);
			$('#m_nick').val(m_nick);
			$('#m_img').val(m_img);
			console.log('이미지링크 : ' + $('#m_img').val());
			return true;
		});
		
		
		$(document).find('#leaveBtn').on('click',function(){
			var m_email = $("#m_email").val();
			var leaveConfirm = confirm('정말로 탈퇴하시겠습니까? 탈퇴한 이메일로는 6개월 간 재가입이 불가능합니다.');
			if (leaveConfirm){
				alert('탈퇴 처리 되었습니다.');
				location.href='MUU_LEAVE.do?m_email='+m_email;
			} else {
				history.go(0);
			}
		}); 
	
	    $("#imgInp").on('change', function(){
	 	   readURL(this);
		});
	    
	    $(document).find("#updateSubmit").on('click', function(){
	    	var formEle = $('#updateForm')[0];
	    	var formData = new FormData(formEle);
	    	
	    	$.ajax({
	    		url : "MUU_MODIFY.do",
	    		type : "POST",
	    		enctype : "multipart/form-data",
	    		data : formData,
	    		processData : false,
	    		contentType : false,
	    		success : function(data){
	    			switch(data){
	    			case "success" :
	    				alert("정보 수정이 완료되었습니다.");
	    				location.href="MAIN.do";
	    				break;
	    			default : 
	    				console.log("정보 수정을 실패하였습니다.");
	    				break;
	    			}
	    		},
	    		error : function(){
	    			console.log("정보 수정 에러");
	    		}
	    	});
	    });

	});

	function readURL(input) {
        if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
                $('#blah').attr('src', e.target.result);
            }
          reader.readAsDataURL(input.files[0]);
        }
    }

</script>

</head>
<body class="changebody">
	<form action="" id="updateForm" method="post" enctype="multipart/form-data" accept-charset="UTF-8">
		<input type="hidden" name="m_img" id="m_img" value="">
		<input type="hidden" name="m_nick" id="m_nick" value="">
		<input type="hidden" name="m_email" id="m_email" value="${member.m_email}">
		<input type="hidden" name="m_pwd" id="m_pwd" value="">
		<input type="hidden" name="m_phone" id="m_phone" value="">

		<div class="changebox">
			
			<p>프로필 이미지</p>
			<br> <img src="${member.m_img}" name="" id="inputImg" class="logo"> <br>
			<br><br><br><br><br><br>
			<p>변경 이미지</p><small id="msgImg"></small>
			<br><input type='file' id="imgInp" name="m_img"/><br>
			<br><img id="blah" src="#" alt="your image" class="logo"/><br>
			<br><br><br><br><br><br>
			<p>닉네임</p><small id="msgNick"></small>
			<input type="text" name="" id="inputNick" value="${member.m_nick }" placeholder="nickname">
			<p>이메일</p><small id="msgEmail"></small>
			<input type="email" name="" id="inputEmail" readonly value="${member.m_email }" placeholder="가져온 이메일"> 
			<p>이름</p><small id="msgName"></small>
			<input type="text" name="" id="inputName" value="${member.m_name }"  placeholder="가져온이름">
			<p>변경할 비밀번호</p><small id="msgPwd"></small>
			<input type="password" name="" id="inputPwd" placeholder="Password">
			<p>비밀번호 확인</p><small id="msgCheckPwd"></small>
			<input type="password" name="" id="inputPwdCheck" placeholder="Password Check">
			<p>연락처</p><small id="msgPhone"></small>
			<select name="" id="inputPhone1">
				<option value="010" <c:if test="${phone[0] eq 010 }">selected</c:if>>010</option>
				<option value="011" <c:if test="${phone[0] eq 011 }">selected</c:if>>011</option>
				<option value="016" <c:if test="${phone[0] eq 016 }">selected</c:if>>016</option>
				<option value="017" <c:if test="${phone[0] eq 017 }">selected</c:if>>017</option>
				<option value="018" <c:if test="${phone[0] eq 018 }">selected</c:if>>018</option>
			</select> 
			<strong>-</strong> <input type="text" name="" id="inputPhone2" value="${phone[1] }" style="width: 70px;">
			<strong>-</strong> <input type="text" name="" id="inputPhone3" value="${phone[2] }" style="width: 70px;">
			<br>
			<!-- <input type="button" class="uploadbtn" name="" value="업로드" onclick=""> -->
			<br><br><br>
			<input type="button" class="modibtn" id="updateSubmit" value="수정"> 
			<input type="button" class="cancelbtn" name="" value="취소" onclick="location.href='MAIN.do'">
			<input type="button" class="outbtn" id="leaveBtn" name="" value="탈퇴">
		</div>
	</form>
	<jsp:include page="Header.jsp"/>
</body>
</html>