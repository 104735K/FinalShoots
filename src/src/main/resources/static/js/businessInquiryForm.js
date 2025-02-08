$(function() {
	
	let check = 0;

	//첨부파일이 있을(없을)경우 remove 이미지가 보이도록(안보이도록) 하는 함수
	function show(){ 
	$('.remove').css('display' , $('#filevalue').text() ? 'inline-block' : 'none')
	.css({'position' : 'relative', 'top' : '-5px'});
	}
	
	
	//미리 show 함수를 실행해서 remove 이미지를 세팅해놓음
	show();

	//첨부파일 옆 x 표시 아이콘 누르면 첨부했던 첨부파일 다시 없애기
	$('.remove').click(function(){
		$('#filevalue').text('');
		$('#upfile').val('');
		$(this).css('display', 'none');// or $('.remove').toggle();
	});
	
	
	//사용자가 첨부파일을 업로드하면 업로드 한 첨부파일의 이름이 나타나게 하는 함수
	$("#upfile").change(function(){
		console.log($(this).val());
		const inputfile = $(this).val().split('\\');
		$('#filevalue').text(inputfile[inputfile.length-1]);
		show();
	});

	$('form[name=modifyform]').submit(function () {
		const hiddenFileName = $('#hiddenFileName').val();
		const upfileValue = $('#upfile').val();

		// 새 파일을 업로드하지 않았을 경우
		if (!upfileValue && hiddenFileName) {
			$('<input>')
				.attr({
					type: 'hidden',
					name: 'check',
					value: hiddenFileName
				})
				.appendTo($(this));
		}
	});


	//문의글쓰기에서 '등록' 눌렀을때 제목/내용이 공백일 경우 창 띄움. + 첨부파일에 check 란 이름 붙여서 form 에 같이 전송함
	$("form[name=inquiryForm], form[name=modifyForm]").submit(function() {

		const $title = $("#title");
		if ($title.val().trim() == "") {
			alert("제목을 입력해 주세요");
			$title.focus();
			return false;
		}//if
		
		const $content = $("#content");
		if ($content.val().trim() == "") {
			alert("내용을 입력해 주세요");
			$content.focus();
			return false;
		}//if


		//파일첨부를 변경하지 않으면 $("#filevalue").text()의 파일명을
		//파라미터 'check'라는 이름으로 form에 추가하여 전송합니다.
		if(check == 0){
			const value = $("#filevalue").text();
			const html = `<input type='hidden' value='${value}' name='check'>`;
			console.log(html);
			$(this).append(html);
		}

	})//submit


	//글쓰기 중 취소 누르면 confirm 창 뜨고 확인 시 뒤로가기, 취소시 현상유지
	$(".cancelBtn").click(function(){
		const $title = $("#title");
		const $content = $("#content");
		
		if (!($title.val().trim() == "") || !($content.val().trim() == "") ) {
			if(confirm("정말 문의글 작성을 취소하시겠습니까?")) {
			location.href = "../business/dashboard?tab=inquiry";
			}
		} else {location.href = "../business/dashboard?tab=inquiry"}
	}) //click 끝

	//글쓰기 중 취소 누르면 confirm 창 뜨고 확인 시 뒤로가기, 취소시 현상유지
	$(".cancelModifyBtn").click(function(){
		const $title = $("#title");
		const $content = $("#content");

		if (!($title.val().trim() == "") || !($content.val().trim() == "") ) {
			if(confirm("정말 문의글 수정을 취소하시겠습니까?")) {
				location.href = "../business/dashboard?tab=inquiry";
			}
		} else {
			location.href = "../business/dashboard?tab=inquiry";
		}
	}) //click 끝
	
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
	});
	
	
})