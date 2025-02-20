// 전역 변수 추가
let currentCategory = 'A';  // 기본값은 자유게시판
let currentPage = 1;        // 현재 페이지


// 페이지가 로드될 때 자유게시판(A)의 게시글을 불러오는 부분
// 페이지 로드 시 URL에서 category, page, search_word를 읽어오기
$(document).ready(function() {
    const urlParams = new URLSearchParams(window.location.search);
    const category = urlParams.get('category') || 'A';
    const page = parseInt(urlParams.get('page')) || 1;
    const searchWord = urlParams.get('search_word') || '';

    // 카테고리와 검색어로 게시글 목록을 불러오기
    switchCategory(category, page, searchWord);
    //switchCategory('A'); //
});



// 검색 버튼 클릭 시 호출되는 함수
function searchPosts(category, page = 1) {
    //const searchWord = $('input[name="search_word"]').val();  // 입력된 검색어
    let searchWord = '';

    // 카테고리별 검색어 입력 필드에서 값 가져오기
    if (category === 'A') {
        searchWord = $('#searchWordA').val();
    } else if (category === 'B') {
        searchWord = $('#searchWordB').val();
    }
    // 카테고리 및 페이지 정보 갱신
    switchCategory(category, page, searchWord);  // 카테고리, 페이지, 검색어를 포함한 요청
}


// 검색어 리셋 함수
function resetSearch(category) {
    if (category === 'A') {
        $('#searchWordA').val('');  // A게시판 검색어 초기화
    } else if (category === 'B') {
        $('#searchWordB').val('');  // B게시판 검색어 초기화
    }

    // URL에서 검색어 파라미터 제거
    const newUrl = `?category=${category}&page=1`;
    history.pushState(null, '', newUrl);

    // 검색어 초기화 후 전체 게시글 다시 불러오기
    switchCategory(category, 1, '');
}

// 카테고리 변경 시 게시글 목록을 비동기적으로 불러오는 함수
function switchCategory(category, page = 1, searchWord = '') {

    currentCategory = category;  // 선택한 카테고리 저장
    currentPage = page;          // 현재 페이지 저장

// URL 업데이트 (페이지 번호와 카테고리 정보 반영)
    const newUrl = `?category=${category}&page=${page}&search_word=${encodeURIComponent(searchWord)}`;
    history.pushState(null, '', newUrl);  // 페이지 URL 업데이트

    // 기존 활성화 상태 초기화
    $(".nav-link").removeClass("active");
    // 클릭된 탭에 활성화 상태 추가
    $(`#tab${category}`).addClass("active");


    // 카테고리별로 해당 tab만 보이도록 설정
    if (category === 'A') {
        $('#A').addClass('active show');
        $('#B').removeClass('active show');
    } else {
        $('#B').addClass('active show');
        $('#A').removeClass('active show');
    }

    // 게시글 목록 비우기
    $('#postListA').empty();
    $('#postListB').empty();



    $.ajax({
        url: '/Shoots/post/list_ajax',
        type: 'GET',
        data: {
            category: category,  // 카테고리 정보 전송
            //state: 'ajax',  // AJAX 요청임을 알려주는 파라미터
            page: page,  // // 선택한 페이지 번호 반영
            limit: 10,  // 한 페이지당 10개씩 게시글을 표시
            search_word: searchWord // 검색어
        },
        dataType : 'json',
        success: function(response) {
            // 서버로부터 받은 JSON 응답 처리
            updatePostList(response, category);
            updatePagination(response.listcount, page);  // 페이징 업데이트


        },
        error: function(xhr, status, error) {
            console.error("AJAX 요청 실패: " + error);
            alert("게시글 목록을 불러오는 데 실패했습니다. 다시 시도해주세요.");
        }
    });
}

function updatePagination(totalCount, currentPage) {
    let totalPages = Math.ceil(totalCount / 10);
    let startPage = Math.max(1, currentPage - 2);
    let endPage = Math.min(totalPages, currentPage + 2);
    let paginationHtml = '<div class="center-block mt-5"><ul class="pagination justify-content-center">';

    // 이전 버튼
    if (currentPage <= 1) {
        paginationHtml += '<li class="page-item"><a class="page-link gray">이전&nbsp;</a></li>';
    } else {
        paginationHtml += `<li class="page-item"><a href="javascript:void(0);" class="page-link" onclick="switchCategory('${currentCategory}', ${currentPage - 1});">이전&nbsp;</a></li>`;
    }

    // 페이지 번호 버튼
    for (let i = startPage; i <= endPage; i++) {
        if (i === currentPage) {
            paginationHtml += `<li class="page-item active"><a href="javascript:void(0);" class="page-link">${i}</a></li>`;
        } else {
            paginationHtml += `<li class="page-item"><a href="javascript:void(0);" class="page-link" onclick="switchCategory('${currentCategory}', ${i});">${i}</a></li>`;
        }
    }

    // 다음 버튼
    if (currentPage >= totalPages) {
        paginationHtml += '<li class="page-item"><a class="page-link gray">&nbsp;다음</a></li>';
    } else {
        paginationHtml += `<li class="page-item"><a href="javascript:void(0);" class="page-link" onclick="switchCategory('${currentCategory}', ${currentPage + 1});">&nbsp;다음</a></li>`;
    }

    paginationHtml += '</ul></div>';

    // 페이징 영역 업데이트
    $(".pagination").html(paginationHtml);
}



// 게시글 목록 업데이트 함수
function updatePostList(data, category) {

    var postList = data.postlist;

    // 각 카테고리별로 적절한 tbody나 div를 선택
    var tableBody = $('table tbody');
    if (category === 'A') {
        tableBody = $('#postListA');  // 자유게시판
    } else if (category === 'B') {
        tableBody = $('#postListB');  // 중고게시판
    }

    tableBody.empty();  // 기존 내용 비우기


    // 새로 받은 게시글 목록을 테이블에 추가
    postList.forEach(function(post) {
        var row = $('<tr>');
        row.append('<td>' + post.post_idx + '</td>');


        // 중고게시판(카테고리B)의 경우 -> 파일첨부(미리보기),가격 추가
        if (category === 'B') {

            //중고게시판 리스트에서 첨부파일 이미지 미리보기
            if (post.post_file && /\.(jpg|jpeg|png|gif)$/i.test(post.post_file)) {
                // 게시글 상세보기와 동일한 경로 형식 사용
                var imageUrl = '/Shoots/upload' + post.post_file.replace(/\//g, '\\'); // /upload -> \upload 형태로 변환
                row.append('<td class="jtdI"><img src="' + imageUrl + '" style="width: 150px; height: 150px; object-fit: cover;"></td>');
            } else { //중고게시판은 첨부파일 반드시 넣어야해서 사실 필요없음
                row.append('<td class="jtdI">-</td>');  // 이미지가 없으면 대시(-)로 표시
            }

            //var postTitle = (post.report_status !== 'unblock')
            //var postTitle = ($("#report_status").val() !== 'unblock')
            var postUnblock = (post.report_status !== 'unblock')
                ? '<span style="color: #DA0130;">차단된 게시글 입니다.</span>'
                : '<td class="jtdt"><a href="detail?num=' + post.post_idx + '">' +
                (post.title.length > 20 ? post.title.substring(0, 20) + '...' : post.title);


            row.append(postUnblock
                + '&nbsp;&nbsp;' + '<span style="color: orange;">[' + post.commentCount + ']</span>' + '</a>'
                + '<br><br>' + post.price + '원' + '<br><br>'
                // + '상태: '
                + (post.status === 'available'
                    ? '<span style="color: #115FFC;">거래 가능</span>'
                    : (post.status === 'completed'
                        ? '<span style="color: #DA0130;">판매 완료</span>'
                        : ''))
                + '</td>');

            //row.append('<td class = "jtdw">' + post.user_id + '</td>');
            let userId = post.user_id.length > 12 ? post.user_id.substring(0, 12) + '...' : post.user_id;
            row.append('<td class="jtdw">' + userId + '</td>');

            row.append('<td class = "jtdr">' + post.register_date + '</td>');
            row.append('<td class = "jtdc">' + post.readcount + '</td>');


        } // if (category === 'B')

        else {  // category A
            var postUnblock = (post.report_status !== 'unblock')
                ? '<span style="color: #DA0130;">차단된 게시글 입니다.</span>'
                : '<a href="detail?num=' + post.post_idx + '">'
                    + (post.title.length > 20 ? post.title.substring(0, 20) + '...' : post.title);

            row.append('<td>' + postUnblock
                + '&nbsp;&nbsp;'  + '<span style="color: orange;">[' + post.commentCount + ']</span>' + '</a></td>');

            //row.append('<td>' + post.user_id + '</td>');
            let userId = post.user_id.length > 12 ? post.user_id.substring(0, 12) + '...' : post.user_id;
            row.append('<td class="jtdw">' + userId + '</td>');
            row.append('<td>' + post.register_date + '</td>');
            row.append('<td>' + post.readcount + '</td>');
        }

        row.append('</tr>');
        tableBody.append(row);
    }); //postList.forEach(function(post) end


} //function updatePostList(data, category) end



// 글쓰기 버튼 클릭 시 '글쓰기' 페이지로 이동
function postWrite() {
    location.href = "write";  // 카테고리 파라미터를 함께 전달
}

// 로그인이 필요한 경우 글쓰기 버튼 클릭 시
function postWriteN() {
    alert("로그인 후 이용 가능합니다.");
    location.href = "../login";
}







// 게시글 목록 요청 및 업데이트 함수
function ajax(sdata) {
    console.log(sdata);

    //isRequestInProgress = true;

    $.ajax({
        data: sdata,
        url: "/Shoots/post/list_ajax",  // URL은 Spring Boot 컨텍스트 경로에 맞게 수정 필요
        dataType: "json",
        cache: false,
        // beforeSend : function(xhr)
        // {   //데이터를 전송하기 전에 헤더에 csrf값을 설정합니다.
        //     xhr.setRequestHeader(header, token);
        // },
        success: function(data) {
            console.log(data);

            // 기존 tbody 내용 제거
            $("table tbody").remove();

            if (data.listcount > 0) {
                $("thead").show();
                updatePostList(data); // 게시글 목록 업데이트
                //generatePagination(data); // 페이지네이션 업데이트
            } else {
                $("thead").hide();
                //$(".pagination").empty();
                $("table").append("<tbody><tr><td colspan='5' style='text-align: center;'>게시글이 존재하지 않습니다</td></tr></tbody>");
            }
        },
        error: function(xhr, status, error) {
            console.log("에러", error);
            console.log("응답 상태:", xhr.status);  // HTTP 상태 코드 확인
            console.log("응답 내용:", xhr.responseText);  // 응답 내용 확인
            $("thead").hide();
            //$(".pagination").empty();
            $("table").append("<tbody><tr><td colspan='5' style='text-align: center;'>데이터를 불러오는 중 오류가 발생했습니다. 다시 시도해주세요.</td></tr></tbody>");
        },
        complete: function() {
            // isRequestInProgress = false;
        }
    });
}













