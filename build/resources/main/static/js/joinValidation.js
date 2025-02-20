$(function(){
    let checkid=false;	 //아이디의 정규식 체크하기 위한 변수로 기본값은 false, 규칙에 맞게 입력하면 true값을 갖습니다.
    let checkemail=false;//이메일의 정규식 체크하기 위한 변수로 기본값은 false, 규칙에 맞게 입력하면 true값을 갖습니다.

    $("input[name=user_id], input[name=business_id]").on('keyup',function(){

        //[A-Za-z0-9_]의 의미 => \w
        const pattern = /^\w{2,30}$/;
        const id = $(this).val();

        if(!pattern.test(id)){
            $("#id-message").css('color','red')
                .html("영문이나 숫자를 이용한 2 ~ 30 글자의 아이디를 사용해 주세요.");
            checkid = false;
            return;
        }

        // 소셜 로그인 아이디들과 겹치지 못하도록 아이디 생성 막아두기
        if (/^n_|^g_|^k_/.test(id)) {
            $("#id-message").css('color','red')
                .html("올바른 아이디가 아닙니다.");
            checkid = false;
            return;
        }

        $.ajax({  //아이디 중복검사 : 개인회원
            url	: "idcheck",
            data : {"id" : id},
            success	: function(resp){
                if(resp == "-1"){//db에 해당 id가 없는 경우
                    $("#id-message").css('color','green').html("사용 가능한 아이디 입니다.");
                    checkid=true;
                }else{//db에 해당 id가 있는 경우(1)
                    $("#id-message").css('color','blue').html("사용중인 아이디 입니다.");
                    checkid=false;
                }
            }
        });//아이디중복검사 : 개인회원 ajax 끝

        $.ajax({  //아이디 중복검사 : 기업회원
            url	: "business_idcheck",
            data : {"id" : id},
            success	: function(resp){
                if(resp == "-1"){//db에 해당 id가 없는 경우
                    $("#businessId-message").css('color','green').html("사용 가능한 아이디 입니다.");
                    checkid=true;
                }else{//db에 해당 id가 있는 경우(1)
                    $("#businessId-message").css('color','blue').html("사용중인 아이디 입니다.");
                    checkid=false;
                }
            }
        });//아이디중복검사 : 개인회원 ajax 끝

    })//id keyup 끝

    $("#regular_email").on('keyup',function(){ //개인회원의 이메일 형식, 고유성 검사
        //[A-Za-z0-9_]와 동일한 것이 \w입니다.
        //+는 1회 이상 반복을 의미하고 {1,}와 동일합니다.
        //\w+ 는 [A-Za-z0-9_]를 1개이상 사용하라는 의미입니다.
        const pattern = /^\w+@\w+[.][A-Za-z0-9]{3}$/;
        const email = $(this).val();

        if(!pattern.test(email)){
            $("#email-message").css('color','red')
                .html("올바른 이메일을 작성해 주세요.");
            checkemail=false;
        }else { //이메일 형식이 올바를 시, 이메일 중복체크
            $("#email-message").css('color', 'green')
                .html("이메일이 형식에 맞습니다.");
            checkemail = true;

            $.ajax({  //이메일 중복검사 : 개인회원
                url: "emailcheck",
                data: {"email": email},
                success: function (resp) {
                    if (resp == "-1") {//db에 해당 id가 없는 경우
                        $("#email-message").css('color', 'green').html("사용 가능한 이메일 입니다.");
                        checkemail = true;
                    } else {//db에 해당 id가 있는 경우(1)
                        $("#email-message").css('color', 'blue').html("사용중인 이메일 입니다.");
                        checkemail = false;
                    }
                }
            });//이메일 중복검사 ajax 끝

        }
    })//개인회원 email keyup 이벤트 처리 끝


    $("#business_email").on('keyup',function(){ //기업회원의 이메일 형식, 고유성 검사
        //[A-Za-z0-9_]와 동일한 것이 \w입니다.
        //+는 1회 이상 반복을 의미하고 {1,}와 동일합니다.
        //\w+ 는 [A-Za-z0-9_]를 1개이상 사용하라는 의미입니다.
        const pattern = /^\w+@\w+[.][A-Za-z0-9]{3}$/;
        const email = $(this).val();

        if(!pattern.test(email)){
            $("#businessEmail-message").css('color','red')
                .html("올바른 이메일을 작성해 주세요.");
            checkemail=false;
        }else { //이메일 형식이 올바를 시, 기업회원 이메일 중복체크
            $("#businessEmail-message").css('color', 'green')
                .html("이메일이 형식에 맞습니다.");
            checkemail = true;

            $.ajax({  //이메일 중복검사
                url: "business_emailcheck",
                data: {"email": email},
                success: function (resp) {
                    if (resp == "-1") {//db에 해당 id가 없는 경우
                        $("#businessEmail-message").css('color', 'green').html("사용 가능한 이메일 입니다.");
                        checkemail = true;
                    } else {//db에 해당 id가 있는 경우(1)
                        $("#businessEmail-message").css('color', 'blue').html("사용중인 이메일 입니다.");
                        checkemail = false;
                    }
                }
            });//이메일 중복검사 ajax 끝

        }
    })//기업회원 email keyup 이벤트 처리 끝


    //개인회원가입 버튼 제출시 유효성 검사
    $('form[name="regularJoinProcess"]').off('submit').submit(function(){
        const telPattern = /^[0-9]{8,13}$/;
        if (!telPattern.test($("input[name='tel']").val())) {
            alert("전화번호를 확인해 주세요.");
            $("input[name=tel]").val('').focus();
            return false;
        }

        const juminPattern = /^[0-9]{2}[0-1]{1}[0-9]{1}[0-3]{1}[0-9]{1}$/;
        if (!juminPattern.test($("input[name='jumin']").val())) {
            alert("주민등록번호를 확인해 주세요.");
            $("input[name='jumin']").val('').focus();
            return false;
        }


        if(!$.isNumeric($("input[name='jumin']").val())){
            alert("주민등록번호에는 숫자를 입력해 주세요.");
            $("input[name='jumin']").val('').focus();
            return false;
        }

        if(!$.isNumeric($("input[name='gender']").val())){
            alert("주민번호 뒷자리에는 숫자를 입력해 주세요");
            $("input[name='gender']").val('').focus();
            return false;
        }

        if (!["1", "2", "3", "4"].includes($("input[name='gender']").val())) {
            alert("주민번호 뒷자리는 1, 2, 3, 4 중 하나여야 합니다");
            $("input[name='gender']").val('').focus();
            return false;
        }

        if(!$.isNumeric($("input[name='tel']").val())){
            alert("전화번호는 숫자로 입력해 주세요");
            $("input[name='tel']").val('').focus();
            return false;
        }

        if ($("input[name='tel']").val().length !== 11) {
            alert("전화번호가 11자리가 맞는지 확인해 주세요.");
            $("input[name='tel']").val('').focus();
            return false;
        }

        if(!checkid){
            alert("사용 가능한 id를 입력해 주세요.");
            $("input[name=user_id]").val('').focus();
            $("#id-message").text('');
            return false;
        }

        if(!checkemail){
            alert("이메일 형식을 확인해 주세요.");
            $("input[name=email]").focus();
            return false;
        }
    })// 개인회원가입 submit 끝


    //기업회원가입 버튼 제출시 유효성 검사
    $('form[name="businessJoinProcess"]').off('submit').submit(function(){
        const telPattern = /^[0-9]{8,13}$/;
        if (!telPattern.test($("#businessTel").val())) {
            alert("전화번호를 확인해 주세요.");
            $("#businessTel").val('').focus();
            return false;
        }


        if(!$.isNumeric($("input[name='post']").val())){
            alert("우편번호는 숫자로 입력해 주세요");
            $("input[name='post']").val('').focus();
            return false;
        }

        if(!checkid){
            alert("사용 가능한 id를 입력해 주세요.");
            $("input[name=business_id]").val('').focus();
            $("#id-message").text('');
            return false;
        }

        if(!$.isNumeric($("input[name='business_number']").val())){
            alert("사업자번호를 숫자로 입력해 주세요");
            $("input[name='business_number']").val('').focus();
            return false;
        }

        if(!$.isNumeric($("#businessTel").val())){
            alert("전화번호는 숫자로 입력해 주세요");
            $("#businessTel").val('').focus();
            return false;
        }


        if(!checkemail){
            alert("이메일 형식을 확인해 주세요.");
            $("input[name=business_email]").focus();
            return false;
        }
    })// 기업회원가입 submit 끝


    $("#postcode").click(function(){ //우편번호 버튼 누르면 다음맵 띄우는 함수
        Postcode();
    })

    function Postcode() { //우편번호,주소 삽입하는 함수
        new daum.Postcode({
            oncomplete: function(data) {
                console.log(data.zonecode)
                // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

                // 도로명 주소의 노출 규칙에 따라 주소를 조합한다.
                // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
                var fullRoadAddr = data.roadAddress; // 도로명 주소 변수
                var extraRoadAddr = ''; // 도로명 조합형 주소 변수

                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                    extraRoadAddr += data.bname;
                }
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if(data.buildingName !== '' && data.apartment === 'Y'){
                    extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 도로명, 지번 조합형 주소가 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                if(extraRoadAddr !== ''){
                    extraRoadAddr = ' (' + extraRoadAddr + ')';
                }
                // 도로명, 지번 주소의 유무에 따라 해당 조합형 주소를 추가한다.
                if(fullRoadAddr !== ''){
                    fullRoadAddr += extraRoadAddr;
                }


                // 우편번호와 주소 정보를 해당 필드에 넣는다.
                $('#post').val(data.zonecode);
                $('#address').val(fullRoadAddr);

            }
        }).open();
    }//function Postcode()


})//ready