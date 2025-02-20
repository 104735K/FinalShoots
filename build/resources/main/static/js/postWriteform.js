$(document).ready(function(){

	//첨부파일이 있을(없을)경우 remove 이미지가 보이도록(안보이도록) 하는 함수
	function show(){
		$('.remove').css('display' , $('#filevalue').text() ? 'inline-block' : 'none')
			.css({'position' : 'relative', 'top' : '-5px'});
	}


	//미리 show 함수를 실행해서 remove 이미지를 세팅해놓음
	show();

	//첨부파일 옆 x 표시 아이콘 누르면 첨부했던 첨부파일 다시 없애기
	$('.remove').click(function(){
		$('#filevalue').text('');// 화면에서 파일명 제거
		$('#upfile').val(''); // 실제 파일 입력 필드 초기화
		$(this).css('display', 'none');// or $('.remove').toggle();
	});


	//사용자가 첨부파일을 업로드하면 업로드 한 첨부파일의 이름이 나타나게 하는 함수
	$("#upfile").change(function(){
		console.log($(this).val());
		const inputfile = $(this).val().split('\\');
		$('#filevalue').text(inputfile[inputfile.length-1]);
		show();
	});

})



// submit 버튼 클릭 시 유효성 검사 및 AJAX 처리
$("form[name=writeform]").submit(function(event) {
	event.preventDefault(); // 기본 폼 제출 동작을 막음


	// 모든 입력이 정상이라면 폼을 전송
	const formData = new FormData(this);  // this는 현재 폼을 가리킴


	// submit 버튼 비활성화 (중복 제출 방지)
	$('button[type="submit"]').prop('disabled', true);



	// AJAX를 통해 폼 데이터 전송
	$.ajax({
		url: '/Shoots/post/add',  // 서버에 데이터 전송할 URL
		type: 'POST',
		data: formData,
		contentType: false, // 파일 전송시 반드시 false로 설정
		processData: false, // jQuery에서 자동으로 데이터를 처리하지 않도록 설정
		success: function(response) {
			if (response.success) {
				// 성공적으로 게시글이 등록되었으면 리스트 페이지로 리다이렉트
				alert("게시글이 등록되었습니다.");
				// location.href = "list"; //리스트 페이지로 이동

			} else {
				alert("게시글 등록에 실패했습니다. 다시 시도해 주세요.");
			}
		},
		error: function(xhr, status, error) {
			alert("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
			console.error(error);
		},
		complete: function() {
			// 버튼을 다시 활성화 시켜주기
			$('button[type="submit"]').prop('disabled', false);
		}
	});

}) //submit 클릭 버튼시 이벤트 끝




