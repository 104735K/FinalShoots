$(document).ready(function(){

	let check = 0;


	//첨부파일이 있을(없을)경우 remove 이미지가 보이도록(안보이도록) 하는 함수
	function show(){
		$('.remove').css('display' , $('#fileValue').text() ? 'inline-block' : 'none')
			.css({'position' : 'relative', 'top' : '-5px'});
	}


	//미리 show 함수를 실행해서 remove 이미지를 세팅해놓음
	show();

	//첨부파일 옆 x 표시 아이콘 누르면 첨부했던 첨부파일 다시 없애기
	$('.remove').click(function(){
		$('#fileValue').text('');
		$('#upfile').val('');
		$(this).css('display', 'none');// or $('.remove').toggle();
	});

	$("#upfile").change(function(){
		console.log($(this).val())	//c:\fakepath\upload.png
		const inputfile = $(this).val().split('\\');
		$('#fileValue').text(inputfile[inputfile.length - 1]);
	});
	
	//submit 버튼 클릭할 때 이벤트 부분
	$("form[name=faqform]").submit(function(){
		const $faqTitle = $("#title");
		if($faqTitle.val().trim() == ""){
			alert("제목을 입력하세요");
			$faqTitle.focus();
			return false;
		}
		
		const $faqContent = $("#content");
		if($faqContent.val().trim() == ""){
			alert("내용을 입력하세요");
			$faqContent.focus();
			return false;
		}

		//파일첨부를 변경하지 않으면 $("#filevalue").text()의 파일명을
		//파라미터 'check'라는 이름으로 form에 추가하여 전송합니다.
		if(check == 0){
			const value = $("#fileValue").text();
			const html = `<input type='hidden' value='${value}' name='check'>`;
			console.log(html);
			$(this).append(html);
		}

	})//submit end

	//첨부파일 용량이 10mb 이상이면 경고창 띄우고 첨부 취소
	$("#upfile").change(function(){
		check++;
		const maxSizeInBytes = 10 * 1024 * 1024;
		const file = this.files[0];

		if(file.size > maxSizeInBytes){
			alert("첨부할 파일의 용량은 10MB 이하여야 합니다.");
			$(this).val('');
			$('#filevalue').text('');
		}else
			$('#filevalue').text(file.name);

		show();
		
	})//submit end
	
})//ready end