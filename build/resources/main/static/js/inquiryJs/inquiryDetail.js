$(function() { //ready 함수
    const loginid = $("#loginid").val();  // 로그인한 유저의 id
    const inquiry_idx = $("#inquiry_idx").val(); // 문의글 번호.

	$('#inquiryDelete').click(function(event){ //문의글 삭제 버튼 누르면 삭제하는 메서드
		event.preventDefault();
		if (confirm("정말 문의글을 삭제하시겠습니까?")) {
			$("#deleteInquiryForm").submit();
		}
	});


    $(".ic").each(function() { //문의댓글을 c:foreach 반복문으로 뽑아내서 여러개가 나오기 때문에 각 댓글들마다 코드 실행시키기 위해 each 함수 사용
        const commentwriter = $(this).find(".ic_writer").val();  // 각 댓글의 작성자 id

        if (loginid === commentwriter) {
            $(this).find(".ic_modify").css('display', 'inline-block');  // 수정 버튼 보이기
            $(this).find(".ic_delete").css('display', 'inline-block');  // 삭제 버튼 보이기
        }
    }); //each 함수 끝
    
    
    $(".ic_delete").click(function(){ //댓글 삭제 버튼 누르면 해당 i_commoent_id 값에 해당하는 문의댓글 삭제
		const deletenum = $(this).val();
		const i_comment_idx = $(this).closest(".ic").find(".ic_num").val();

		if (confirm("정말 문의댓글을 삭제하시겠습니까?")) {
			$.ajax({
				url: '../inquiryComment/delete',
				type: "POST",
				data: {
					"i_comment_idx": i_comment_idx
				},
				success: function (response) {
					// 서버로부터 성공적인 응답을 받은 경우 처리
					alert("문의댓글을 성공적으로 삭제했습니다.");
					location.href = 'detail?inquiry_idx=' + inquiry_idx;
				},
				error: function (xhr, status, error) {
					// 오류 처리
					console.log("수정 실패:", error);
					alert("문의댓글을 삭제하지 못했습니다.");
				}
			}); //ajax 끝
		} // if문 끝 (confirm - 확인=true 일때)

	}) //문의댓글 삭제 click() 끝
    
    
    $(".ic_modify").click(function(){ //수정 버튼 누르면 해당 수정,삭제 버튼 사라지고 수정완료 , 취소 버튼이 생기게
		
		const $modifybutton = $(this);
		const $deletebutton = $(this).closest(".ic").find(".ic_delete")
		
		 const modifyCompleteButton = $('<button>', {
            type: "button",
            class: "btn btn-success ic_modifyComplete",
            text: "수정완료"
        });
        
        //수정취소 버튼 생성
        const modifyCancelButton = $('<button>', {
            type: "button",
            class: "btn btn-danger ic_modifyCancel",
            text: "취소"
        });
		
		
		//수정, 삭제 버튼 숨기기
		$modifybutton.hide();
		$deletebutton.hide();
		
		//기존 댓글의 내용을 originalContent 라고 선언. 후에 숨김 (수정 버튼 누르면 기존 내용 숨기고 텍스트 박스 나오게)
		const originalContent = $(this).closest(".ic").find(".ic_content")
		originalContent.hide();
		
		//새 댓글 내용창을 textarea로 만들고 기존 댓글내용 뒤에 갖다 붙임 (기존 내용은 숨겨서 기존 자리에 대체된거로 보임)
		const newContent = $('<textarea>', {
			text: $(originalContent).text(),
			class : "new_ic_content",
			name : "new_ic_content"
		})
		
		originalContent.after(newContent);
		
		//수정, 삭제버튼 숨긴 뒤 만들어둔 수정완료, 수정취소 버튼을 div(buttonfront) 부분 뒤에 갖다 붙임.
		//선택자가 긴 이유는, 그냥 buttonfront.append 로 붙이면 모든 댓글에 다 수정완료 버튼이 생겨버림.
		$modifybutton.closest(".ic").find(".buttonfront").append(modifyCompleteButton, modifyCancelButton);


		modifyCancelButton.click(function () { //수정취소 버튼 누르면 숨겼던 수정, 삭제 버튼 다시 나오게 하고 수정완료, 수정취소버튼 삭제함
			modifyCompleteButton.remove();
			modifyCancelButton.remove();

			$modifybutton.show();
			$deletebutton.show();

			newContent.remove();
			originalContent.show();
		});
		
		$(".ic_modifyComplete").click(function() {  //수정완료 버튼을 누를시 실행하는 ajax.
		    const modifyButton = $(this);
		    const i_comment_idx = modifyButton.closest(".ic").find(".ic_num").val(); // 댓글 ID
		    const newContent = modifyButton.closest(".ic").find(".new_ic_content").val(); // 새 댓글 내용
		
		    $.ajax({
		        url: '../inquiryComment/inquiryCommentModify',
		        type: "POST",
		        data: {
		            "i_comment_idx": i_comment_idx,
		            "content": newContent,
		        },
		        success: function(response) {
		            // 서버로부터 성공적인 응답을 받은 경우 처리
		            alert("문의댓글을 성공적으로 수정했습니다.");
		            location.href = 'detail?inquiry_idx=' + inquiry_idx;
		        },
		        error: function(xhr, status, error) {
		            // 오류 처리
		            console.log("수정 실패:", error);
		            alert("문의댓글을 수정하지 못했습니다.");
		        }
		    }); //ajax 끝
		    
		    
		}); //수정완료 메서드 끝

		
	}) //'수정' 버튼 누르면 작동하는 메서드 끝
    
    
}); //ready() 끝