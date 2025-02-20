$(document).ready(function () {
    // // CSRF 설정: 모든 AJAX 요청에 CSRF 토큰 추가
    // $.ajaxSetup({
    //     beforeSend: function (xhr) {
    //         const csrfToken = $("meta[name='_csrf']").attr("content");
    //         const csrfHeader = $("meta[name='_csrf_header']").attr("content");
    //         if (csrfToken && csrfHeader) {
    //             xhr.setRequestHeader(csrfHeader, csrfToken);
    //         }
    //     }
    // });

    let check = 0;

// 폼 제출 이벤트 처리
    $("form[name=modifyform]").submit(function (event) {

        // category가 B일 때만 첨부파일 유효성 검사
        const category = $("input[name='category']").val(); // category 값 가져오기

        if (category === 'B') { // 중고게시판인 경우만 유효성 검사 진행

// 파일첨부를 변경하지 않으면 기존 파일 정보를 전송
            if (check === 0) {
                const existingFilePath = $('#existingFilePath').val();
                const existingFileName = $('#existingFileName').val();

                // 기존 파일 경로와 이름을 hidden input으로 추가
                $(this).append(`<input type='hidden' name='post_file' value='${existingFilePath}'>`);
                $(this).append(`<input type='hidden' name='post_original' value='${existingFileName}'>`);
            }


// 파일첨부를 변경하지 않으면 $('#filevalue').text()의 파일명을
            // 파라미터 'check'라는 이름으로 form에 추가하여 전송합니다.
            if (check == 0) {
                const value = $('#filevalue').text();
                const html = `<input type='hidden' value='${value}' name='check'>`;
                console.log(html);
                $(this).append(html);
            }

            // 첨부파일 삭제 후 유효성 검사 추가
            const removeFile = $('input[name="remove_file"]').val();
            if (removeFile === 'true' && !$('#upfile').val()) {
                // 첨부파일을 삭제하고 파일을 새로 선택하지 않았다면 유효성 검사 실패
                alert("중고게시판에서는 첨부파일을 반드시 첨부해야 합니다.");
                event.preventDefault(); // 폼 제출 중지
            }
        }
    });



    // 파일 선택 시 파일명을 표시
    $("#upfile").change(function () {
        check++;
        const maxSizeInBytes = 20 * 1024 * 1024;
        const file = this.files[0]; // 선택된 파일
        if (file.size > maxSizeInBytes) {
            alert("5MB 이하 크기로 업로드 하세요");
            $(this).val('');
        } else {
            $('#filevalue').text(file.name);	// 파일 이름
        }

        // const inputFile = $(this).val().split('\\'); // 파일 경로 분리
        // $('#filevalue').text(inputFile[inputFile.length - 1]); // 파일명만 표시

        // 삭제된 파일이 있으면 삭제 플래그를 초기화
        $('input[name="remove_file"]').val('false');
        show();
        //$('input[name="remove_file"]').val('false'); // 삭제 플래그 초기화
    })


    // // 첨부파일이 있을(없을) 경우 remove 이미지가 보이도록 설정
    // function show() {
    //     const fileExists = $('#filevalue').text() ? true : false;
    //     $('.remove').css('display', fileExists ? 'inline-block' : 'none')
    //         .css({ 'position': 'relative', 'top': '-5px' });
    // }


    // 첨부파일 제거 버튼 클릭 시 처리
    $('.remove').click(function () {
        $('#filevalue').text(''); // 첨부파일 표시 내용 제거
        $('#upfile').val('');    // 파일 입력 필드 초기화
        $(this).css('display', 'none'); // 삭제 버튼 숨기기
        $('input[name="remove_file"]').val('true'); // 파일 삭제 플래그 설정
    });


    function show() {
        //파일 이름이 있는 경우 remove 이미지를 보이게 하고
        //파일 이름이 없는 경우 remove 이미지 보이지 않게 합니다.
        $('.remove').css('display', $('#filevalue').text() ? 'inline-block' : 'none')
            .css({'position' : 'relative', 'top': '-5px'});
    }


    // 초기 실행 시 remove 이미지 세팅
    show();



});  //ready end




