let option = 1; // 유지할 정렬 옵션

//선택한 등록순과 최신순을 수정, 삭제, 추가 후에도 유지되도록 하기위한 변수로 사용됩니다
//댓글 목록을 불러오는 함수
function getList(state) {
  console.log(state);
  option = state; // state는 정렬 옵션을 받음
  $.ajax({
    type: "post",
    url: "../comment/list",
    data: {
      "post_idx": $("#post_idx").val(),
      state: state
    },
    dataType: "json", //응답 데이터는 JSON 형식으로 처리됨
    success: function(rdata) { //댓글 목록의 수 (listcount)
      $('#count').text(rdata.listcount).css('font-family', 'arial,sans-serif');
      let red1 = (state === 1) ? 'red' : 'gray'; //등록순
      let red2 = (state === 2) ? 'red' : 'gray'; //최신순

      let output =`
        <li class='comment-order-item ${red1}'>
          <a href='javascript:getList(1)' class='comment-order-button'>등록순</a>
        </li>
        <li class='comment-order-item ${red2}'>
          <a href='javascript:getList(2)' class='comment-order-button'>최신순</a>
        </li>`;
      $('.comment-order-list').html(output);

      output = ''; // 초기화
      if (rdata.commentlist.length) {
      rdata.commentlist.forEach(Comment => {

        let isSecret = Comment.is_secret === 'Y'; //비밀 댓글
        let isPostOwner = $("#loginid").val() === $(".user_id").text(); //로그인한 사람 아이디와 게시글 작성자의 아이디가 같을때
        let isCommentOwner = $("#loginid").val() === Comment.user_id; //로그인한 사람 아이디와 댓글 작성자의 아이디가 같을때
        let isAdmin = $("#loginid").val() === 'admin'; //로그인한 사람 아이디가 관리자일때



          let commentUnblock = (Comment.report_status !== 'unblock')
              ? '<span style="color: #DA0130;">차단된 댓글 입니다.</span>'
              : Comment.content;


          let displayContent = isSecret && !(isPostOwner || isCommentOwner || isAdmin)
              ? '🔒비밀댓글입니다.'
              : commentUnblock;

          // 비밀댓글인지(displayContent) 먼저 판단 후 >> commentUnblock

        // 비밀댓글 스타일 적용
        let displayContentStyle = isSecret ? 'color: gray;' : '';



        // 부모 댓글 처리
        let replyClass = (Comment.comment_ref_id) ? 'comment-list-item--reply' : ''; // 답글 여부
        let src = Comment.user_file ? `../userupload/${Comment.user_file}` : '../img/info.png';

        // 답글 버튼은 원본 댓글에만 표시
        let replyButton = (!Comment.comment_ref_id) ?
            `<a href='javascript:replyform(${Comment.comment_idx})' class='comment-info-button'>답글쓰기</a>` : '';

        // 댓글 작성자가 로그인한 사용자일 경우, 수정/삭제 버튼 표시
        // let toolButtons = $("#loginid").val() == Comment.user_id ? `
        let toolButtons = ($("#loginid").val() == Comment.user_id || $("#loginid").val() == 'admin')? ` 
            <div class='comment-tool'>
                <div title='더보기' class='comment-tool-button'> 
                    <div>&#46;&#46;&#46;</div>
                </div>
                <div id='comment-list-item-layer${Comment.comment_idx}' class='LayerMore'>
                    <ul class='layer-list'>
                        <li class='layer-item'>
                            <a href='javascript:updateForm(${Comment.comment_idx})' class='layer-button'>수정</a>
                            <a href='javascript:del(${Comment.comment_idx})' class='layer-button'>삭제</a>
                        </li>
                    </ul>
                </div>
            </div>` : '';

        //reportButton 댓글쪽

          let Secret = Comment.is_secret === 'Y';
          let postOwner = $("#loginid").val() === $(".user_id").text(); //로그인한 사람 아이디와 게시글 작성자의 아이디가 같을때
          let commentOwner = $("#loginid").val() === Comment.user_id; //로그인한 사람 아이디와 댓글 작성자의 아이디가 같을때
          let admin = $("#loginid").val() === 'admin'; //로그인한 사람 아이디가 관리자일때

          // 가독성을 위해 풀어쓰기?
          //댓글 신고 버튼이 보이는 조건1 (commentReport)
          let commentReport = (!Secret && !commentOwner) || (Secret && (!commentOwner && (postOwner || admin)));
          // 일반댓글인경우 - 로그인한 사람 아이디와 게시글 작성자의 아이디가 같을때
          // 비밀댓글인 경우 - 로그인한 사람 아이디와 댓글 작성자의 아이디가 다르고,(and) 로그인한 사람 아이디와 게시글 작성자의 아이디가 같거나(or) 로그인한 사람 아이디가 관리자일때

          //댓글 신고 버튼이 보이는 조건2 (Comment.report_status === 'unblock') / unblock: 댓글상태가 차단이 아닌경우
          // 신고버튼이 보이는 조건1,조건2 모두 해당하는 경우 >> 신고버튼이 보임
        let reportButton = (commentReport && Comment.report_status === 'unblock')  ? `
            <button class="commentReportButton" data-comment-idx="${Comment.comment_idx}" 
                    data-comment-content="${Comment.content}"
                    data-writer="${Comment.writer}" data-tidx="${Comment.writer}" 
                    data-toggle="modal" data-target=".c-report-modal" style="color:red; border:none">
                <img src='../img/report.png' style="width:15px; height:15px">
            </button>` : '';

        //댓글은 ref_id 가 null, 답글은 ref_id가 댓글의 comment_id 값을 참조
		//답글은 ref_id가 null이 아니니까 출력하면 안되지
		// 댓글 처리
        output += (Comment.comment_ref_id != null) ? '' : `
        <li id='${Comment.comment_idx}' class='comment-list-item ${replyClass}'>
            <div class='comment-nick-area'>
                <!-- <img src='${src}' alt='profile picture' style = "width : 35px; height : 35px"> -->
                <div class='comment-box'>
                    <div class='comment-nick-box'>
                        <div class='comment-nick-info'>
                            <div class='comment-nickname'>${Comment.user_id}</div>
                        </div>
                    </div>
                    <div class='comment-text-box'>
                        <p class='comment-text-view'>
                        
                            <span class='text-comment' style='${displayContentStyle}'>${displayContent}</span>
                            ${reportButton}
                        </p>
                    </div>
                    <div class='comment-info-box'>
                        <span class='comment-info-date'>${Comment.register_date}</span>
                        ${replyButton}
                    </div>
                    ${toolButtons}
                </div>
            </div>
        </li>`;
        console.log(128 + "=" + output);

        // 답글 처리: 부모 댓글에 대한 답글을 출력
        rdata.commentlist.forEach(childComment => {

            let isSecretC = childComment.is_secret === 'Y';
            let isPostOwnerC = $("#loginid").val() === $(".user_id").text(); //로그인한 사람 아이디와 게시글 작성자의 아이디가 같을때
            let isCommentOwnerC = $("#loginid").val() === childComment.user_id; //로그인한 사람 아이디와 비밀댓글 작성자의 아이디가 같을때
            let isAdminC = $("#loginid").val() === 'admin'; //로그인한 사람 아이디가 관리자일때

            if (childComment.comment_ref_id === Comment.comment_idx) {
                let childSrc = childComment.user_file ? `../userupload/${childComment.user_file}` : '../img/info.png';
                /*
                정규식 /(@[\w\u00C0-\u017F]+)/g:
				@: '@' 기호를 탐지.
				[\w\u00C0-\u017F]+: 단어 문자(\w)와 유니코드 문자 범위(\u00C0-\u017F, 라틴 문자를 포함)로 구성된 문자열. 이는 다양한 언어의 사용자 이름을 지원하기 위함.
				+: 이름이 하나 이상의 문자로 이루어진 경우에만 매칭.
				g: 전역 플래그로 문자열 전체에서 모든 일치를 탐색.
				<span class='mention'>$1</span>:
				$1: 정규식에서 매칭된 첫 번째 캡처 그룹((@[\w\u00C0-\u017F]+)).
				<span class='mention'>: HTML 태그를 추가하여 스타일 지정 가능.
				\u4e00-\u9fff: 중국어, 일본어, 한자의 유니코드 범위.
				\uac00-\ud7af: 한글 음절의 유니코드 범위..: 점을 포함하도록 추가.
				\w: 영문자, 숫자, 밑줄을 포함한 단어 문자를 찾습니다.
				\u00C0-\u017F: 라틴 문자를 포함한 유니코드 범위 (유럽 언어 지원).
				-: 하이픈을 포함하도록 추가.
                */
                 // @parentUsername 부분을 파란색으로 스타일링
        let formattedContent = childComment.content.replace(/(@[\w\u00C0-\u017F\uac00-\ud7af\u4e00-\u9fff.-]+)/g, "<span class='mention'>$1</span>");


                let childCommentUnblock = (childComment.report_status !== 'unblock')
                    ? '<span style="color: #DA0130;">차단된 댓글 입니다.</span>'
                    : formattedContent;


                let childDisplayContent = isSecretC && !(isPostOwnerC || isCommentOwnerC || isAdminC)
                    ? '🔒비밀댓글입니다.'
                    : childCommentUnblock;


                //  비밀댓글인지 판단 >> 차단 댓글인지 판단 >> formattedContent 적용


                // 비밀댓글 스타일 적용
                let childDisplayContentStyle = isSecretC ? 'color: gray;' : '';
                // 답글의 더보기 버튼 및 수정/삭제 버튼 처리
        let childToolButtons = ($("#loginid").val() === childComment.user_id || $("#loginid").val() == 'admin') ? ` 
            <div class='comment-tool'>
                <div title='더보기' class='comment-tool-button'> 
                    <div>&#46;&#46;&#46;</div>
                </div>
                <div id='comment-list-item-layer${childComment.comment_idx}' class='LayerMore'>
                    <ul class='layer-list'>
                        <li class='layer-item'>
                            <a href='javascript:updateForm(${childComment.comment_idx})' class='layer-button'>수정</a>
                            <a href='javascript:del(${childComment.comment_idx})' class='layer-button'>삭제</a>
                        </li>
                    </ul>
                </div>
            </div>` : '';


        //reportButton 답글쪽

                let secretC = childComment.is_secret === 'Y';
                let postOwnerC = $("#loginid").val() === $(".user_id").text(); //로그인한 사람 아이디와 게시글 작성자의 아이디가 같을때
                let commentOwnerC = $("#loginid").val() === childComment.user_id; //로그인한 사람 아이디와 댓글(답글) 작성자의 아이디가 같을때
                let adminC = $("#loginid").val() === 'admin'; //로그인한 사람 아이디가 관리자일때

                // 가독성을 위해 풀어쓰기?
                //답글 신고 버튼이 보이는 조건1 (childReport)
                let childReport = (!secretC && !commentOwnerC) || (secretC && (!commentOwnerC && (postOwnerC || adminC)));
                // 일반답글인경우 - 로그인한 사람 아이디와 게시글 작성자의 아이디가 같을때
                // 비밀답글인 경우 - 로그인한 사람 아이디와 답글 작성자의 아이디가 다르고,(and) 로그인한 사람 아이디와 게시글 작성자의 아이디가 같거나(or) 로그인한 사람 아이디가 관리자일때

                //답글 신고 버튼이 보이는 조건2 (childComment.report_status === 'unblock') / unblock: 댓글상태가 차단이 아닌경우
                // 신고버튼이 보이는 조건1,조건2 모두 해당하는 경우 >> 신고버튼이 보임
            reportButton = (childReport && childComment.report_status === 'unblock') ? `
            <button class="commentReportButton" data-comment-idx="${childComment.comment_idx}" 
                    data-comment-content="${childComment.content}"
                    data-writer="${childComment.writer}" data-tidx="${childComment.writer}" 
                    data-toggle="modal" data-target=".c-report-modal" style="color:red; border:none">
                <img src='../img/report.png' style="width:15px; height:15px">
            </button>` : '';

                output += `
                <li id='${childComment.comment_idx}' class='comment-list-item comment-list-item--reply'>
                    <div class='comment-nick-area'>
                        <!-- <img src='${childSrc}' alt='profile picture' style = "width : 35px; height : 35px"> -->
                        <div class='comment-box'>
                        
                            <div class='comment-nick-box'>
                                <div class='comment-nick-info'>
                                    <div class='comment-nickname'>${childComment.user_id}</div>
                                </div>
                            </div>
                            <div class='comment-text-box'>
                                <p class='comment-text-view' style="display: inline; align-items: center;">
                                    <span class='text-comment' style='${childDisplayContentStyle}'>${childDisplayContent}</span>
                                    ${reportButton}
                                </p>
                            </div>
                            <div class='comment-info-box'>
                                <span class='comment-info-date'>${childComment.register_date}</span>
                            </div>
                            ${childToolButtons}
                        </div>
                    </div>
                </li>`;
            } //if (childComment.comment_ref_id === Comment.comment_id) 끝
        }); //답글 foreach문 끝
    });
} //if (rdata.commentlist.length) 끝
      //   else {
      //     // 댓글이 없을 경우 "댓글이 없습니다" 문구 추가
      //     output = "<li class='no-comments'>댓글이 없습니다.</li>";
      // }

      console.log(output);
      $('.comment-list').html(output); //댓글 데이터를 HTML로 변환하여 화면에 출력

      //댓글이 없으면 댓글 목록과 정렬 메뉴를 비움
      if (!rdata.commentlist.length) {
        $('.comment-list, .comment-order-list').empty();
      }

    }
  });

} //getList 함수 끝 (댓글 목록 뽑아오는 함수)



$(document).on('click', '#titleReport', function() {
    const reported = $('.title').text();
    const modalHtml = `
                                <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="modalTitle">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <h5 class="modal-title" id="modalTitle">${reported} 신고</h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                            </div>
                                            <div class="modal-body">
                                                <select name="title" required>
                                                    <option disabled selected hidden>신고 사유를 선택해 주세요</option>
                                                    <option value="욕설, 혐오 표현 등이 포함된 댓글">욕설, 혐오 표현 등이 포함된 글</option>
                                                    <option value="갈등 조장하는 댓글">갈등 조장하는 글</option>
                                                    <option value="게시글과 관계 없는 내용">게시글과 관계 없는 내용</option>
                                                    <option value="도배 목적의 댓글">도배 목적의 글</option>
                                                    <option value="성적 컨텐츠가 포함된 댓글">성적 컨텐츠가 포함된 글</option>
                                                </select><br><br>
                                                추가 내용(100자 이내)<br>
                                                <textarea maxlength="100" id="modalEtcContent" style="margin: 10px; width: 300px; height: 100px;"> </textarea>
                                            </div>
                                            <div class="modal-footer">
                                                <input type="hidden" id="modalReported" value="${reported}">
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                                                
                                                <button type="button" class="btn btn-primary" 
                                                onclick="ReportSubmitButton({'category': 'POST', 'reportedUser' : '${reported}'})">
                                                    신고하기
                                                </button>                                            
                                            </div>
                                        </div>
                                    </div>
                                </div>
                               `
    $('#exampleModal').remove();

    var tempDiv = document.createElement('div');
    tempDiv.innerHTML = modalHtml;
    document.body.appendChild(tempDiv.firstElementChild);

    const modalElement = document.getElementById('exampleModal');
    const modal = new bootstrap.Modal(modalElement);
    modal.show();
});

$(document).on('click', '.commentReportButton', function() {
    const commentNickname = $(this).closest('.comment-box').find('.comment-nickname');
    const dataCommentIdx= $(this).data('comment-idx');
    const dataCommentContent = $(this).data('comment-content');
    const modalHtml = `
                                <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="modalTitle">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <h5 class="modal-title" id="modalTitle">${commentNickname.text()}님의 댓글 신고</h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                            </div>
                                            <div class="modal-body">
                                                <select name="title" required>
                                                    <option disabled selected hidden>신고 사유를 선택해 주세요</option>
                                                    <option value="욕설, 혐오 표현 등이 포함된 댓글">욕설, 혐오 표현 등이 포함된 댓글</option>
                                                    <option value="갈등 조장하는 댓글">갈등 조장하는 댓글</option>
                                                    <option value="게시글과 관계 없는 내용">게시글과 관계 없는 내용</option>
                                                    <option value="도배 목적의 댓글">도배 목적의 댓글</option>
                                                    <option value="성적 컨텐츠가 포함된 댓글">성적 컨텐츠가 포함된 댓글</option>
                                                </select><br><br>
                                                추가 내용(100자 이내)<br>
                                                <textarea maxlength="100" id="modalEtcContent" style="margin: 10px; width: 300px; height: 100px;"> </textarea>
                                            </div>
                                            <div class="modal-footer">
                                                <input type="hidden" id="modalReported" value="${commentNickname.text()}">
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                                                
                                                <button type="button" class="btn btn-primary" 
                                                onclick="ReportSubmitButton({'category': 'COMMENT', 'commentIdx': ${dataCommentIdx}, 'reportedUser' : '${dataCommentContent}'})">
                                                    신고하기
                                                </button>                                            
                                            </div>
                                        </div>
                                    </div>
                                </div>
                               `
    $('#exampleModal').remove();

    var tempDiv = document.createElement('div');
    tempDiv.innerHTML = modalHtml;
    document.body.appendChild(tempDiv.firstElementChild);

    const modalElement = document.getElementById('exampleModal');
    const modal = new bootstrap.Modal(modalElement);
    modal.show();
    comment_idx
    const commentIdx = $(this).data('comment-idx');
    const target = $(this).data('user_id');
    console.log('===> commentIdx:', commentIdx);
    console.log('===> writer:', target);

    // 모달창에 값 설정
    $('.report_ref_id').val(commentIdx); // 신고 댓글 ID
    $('.target').val(target); // 신고 대상자 ID

}) //댓글 선택자 값 부여 함수 끝

function ReportSubmitButton(paramData){
    const category = paramData?.category ?? '';
    const selectedOption = $('select[name="title"]').val();

    if(selectedOption == null){
        alert('신고 사유를 선택해주세요.');
        return false;
    }
    var reqData = {
        reportedUser : paramData?.reportedUser,
        category : category,
        content : selectedOption,
        detail : $('#modalEtcContent').val(),
        PostIdx : $('#post_idx')?.val() ?? 0,
        CommentIdx : paramData?.commentIdx ?? 0
    };
    fetchReport(reqData);
}

function fetchReport(reqData) {
    //fetch start
    //category in ('POST','COMMENT','USER')
    fetch('/Shoots/insertReport',{
        method:'POST',
        headers: {
            'Content-Type' : 'application/json'
        },
        body : JSON.stringify(reqData)
    })
        .then(resp => resp.json())
        .then(data => {
            var alertMsg = data.msg + "";
            if(data.reportCnt != null)
                alertMsg += "\n 신고 누적 횟수 : " + data.reportCnt;
            alert(alertMsg);
            location.reload();
        })
        .catch(error => alert("에러 뜸 : " + error))
    //fetch end
};

// 더보기 - 수정 클릭한 경우에 수정 폼을 보여줍니다.
function updateForm(comment_idx) {
  $(".comment-tool, .LayerMore").hide(); // 댓글 수정시 더보기 및 수정 삭제 영역 숨김
  let $comment_idx = $('#' + comment_idx);
  const content = $comment_idx.find('.text-comment').text(); // 선택한 댓글 내용
  $comment_idx.find('.comment-nick-area').hide(); // 댓글 닉네임 영역 숨김
  $comment_idx.append($('.comment-list+.comment-write').clone()); // 기본 글쓰기 폼 복사하여 추가

  // 수정 폼의 <textarea>에 내용을 나타냅니다.
  $comment_idx.find('.comment-write textarea').val(content);
  // 수정 완료 버튼 및 취소 버튼 보이기
  //'.btn-register' 영역에 수정할 글 번호를 속성 'data-id'에 나타내고 클래스 수정완료버튼'update'를 추가합니다.
  $comment_idx.find('.btn-register').attr('data-id', comment_idx).addClass('update').text('수정완료');
  //폼에서 취소를 사용할 수 있도록 보이게 합니다.
  $comment_idx.find('.btn-cancel').show();
  // 글자 수 표시
  $comment_idx.find('.comment-write-area-count').text(`${content.length}/200`);
}

 //더보기 -> 삭제 클릭한 경우 실행하는 함수
function del(comment_idx) {//num : 댓글 번호
  if (!confirm('정말 삭제하시겠습니까?')) {
    $('#comment-list-item-layer' + comment_idx).hide(); // '수정 삭제' 영역 숨김
    return;
  }
  $.ajax({
      type: 'post',
    url: '../comment/delete',
    data: {
        comment_idx: comment_idx
    },

    success: function(rdata) {
      if (rdata === 1) {
        getList(option); // 댓글 리스트 다시 불러오기
      }
    }
  });
}



//답글 달기 폼
function replyform(comment_idx) {

  //수정 삭제 영역 선택 후 답글쓰기를 클릭한 경우
  $(".LayerMore").hide(); // 수정 삭제 영역 숨김
  
  let $comment_idx = $('#' + comment_idx);
  
  // 부모 댓글의 작성자 이름 가져오기
  const parentUsername = $comment_idx.find('.comment-nickname').text();
  
  //선택한 글 뒤에 답글 폼을 추가합니다.
  $comment_idx.after(`<li class="comment-list-item comment-list-item--reply"></li>`);
  
  // 글쓰기 영역 복사
  let replyForm = $('.comment-list+.comment-write').clone();
  
  //복사한 폼을 답글 영역에 추가
  let $comment_idx_next = $comment_idx.next().html(replyForm);
  
  // 답글 폼의 <textarea>에 '답글을 남겨보세요' placeholder 설정 및 @작성자 입력
  $comment_idx_next.find('textarea')
						  .attr('placeholder', '답글을 남겨보세요')
						  .val(`@${parentUsername} `)
						  .focus(); // 포커스를 텍스트 영역으로 이동
  
  //답글 폼의 'btn-cancel'을 보여주고 클래스 'reply-cancel'를 추가합니다.
  $comment_idx_next.find('.btn-cancel').show().addClass('reply-cancel');
  
  //답글 폼의 '.btn-register'에 클래스 'reply' 추가합니다.
  // 속성 'data-ref'에 부모 댓글 ID를 설정합니다.
  //등록을 답글 완료로 변경합니다.
  $comment_idx_next.find('.btn-register')
           .addClass('reply')
           .attr({ 'data-ref': comment_idx }) // 부모 댓글의 comment_id를 'data-ref'로 설정
           .text('답글완료');
  //댓글 폼 보이지 않습니다.
  $("body > div > div.comment-area > div.comment-write").hide(); // 댓글 폼 숨기기
}

$(function() {
  getList(option); // 처음 로드될 때는 등록순 정렬

    // 댓글 입력시 글자수 표시
 $('.comment-area').on('keyup','.comment-write-area-text', function() {
	 const length=$(this).val().length;
	 $(this).prev().text(length+'/200');
 }); // keyup','.comment-write-area-text', function()

  //댓글 등록을 클릭하면 데이터베이스에 저장 -> 저장 성공 후에 리스트 불러옵니다.
  $('ul + .comment-write .btn-register').click(function() {
    const content = $('.comment-write-area-text').val();
    if (!content) {
      alert("댓글을 입력하세요");
      return;
    }

      // $('input[name="isSecret"]').on('change', function() {
      //     if ($(this).prop('checked')) {
      //         console.log("비밀댓글이 체크되었습니다.");
      //     } else {
      //         console.log("비밀댓글이 체크 해제되었습니다.");
      //     }
      // });


      // 비밀댓글 체크박스 상태를 'Y' 또는 'N'으로 설정
      const is_secret = $('#is_secret').prop('checked') ? 'Y' : 'N';


      $.ajax({
      url: '../comment/add',
      data: {
        id: $("#loginid").val(), // 로그인 사용자 ID
        content: content,
          writer: $("#idx").val(), // 댓글 작성자 ID
        post_idx: $("#post_idx").val(), // 게시글 ID
          is_secret: is_secret,  // 체크된 경우 'Y', 아닌 경우 'N'

        comment_ref_id: null // 원본 댓글은 comment_ref_id가 null
      },
      type: 'post',
      success: function(rdata) {
        if (rdata === 1) {
          getList(option); // 댓글 리스트 갱신
        }
      }
    });

    $('.comment-write-area-text').val(''); // textarea 초기화
    $('.comment-write-area-count').text('0/200'); // 입력한 글 카운트 초기화
  }); // $('ul + .comment-write .btn-register').click(function() {


  // 더보기를 클릭한 경우 (댓글 및 답글 공통 처리)
	$(".comment-list").on('click', '.comment-tool-button', function() {
		//더보기를 클릭하면 수정과 삭제 영역이 나타나고 다시 클릭하면 사라져요
		$(this).next().toggle();

		//클릭 한 곳만 수정 삭제 영역이 나타나도록 합니다.
		// 다른 모든 "더보기" 버튼의 수정/삭제 영역을 숨김
		$(".comment-tool-button").not(this).next().hide();
	})



    // 수정 후 수정완료를 클릭한 경우 (댓글 및 답글 공통 처리)
	$('.comment-area').on('click','.update',function(){

		const content = $(this).parent().parent().find('textarea').val();
		if(!content){ //내용없이 등록 클릭한 경우
			alert("수정할 글을 입력하세요");
			return;
		}
		const comment_idx = $(this).attr('data-id');

        // 비밀댓글 체크박스 상태를 'Y' 또는 'N'으로 설정
        const is_secret = $('#is_secret').prop('checked') ? 'Y' : 'N';

		$.ajax({
			url:'../comment/update',
			data:{comment_idx:comment_idx,
                content:content,
                is_secret: is_secret,  // 체크된 경우 'Y', 아닌 경우 'N'
            },
            type: 'post',
			success:function(rdata){
				if(rdata===1)
					getList(option);
			} // success
		}); //ajax
	}) // 수정 후 수정완료를 클릭한 경우


    // 수정 후 취소 버튼을 클릭한 경우
	$('.comment-area').on('click','.btn-cancel',function(){
		// 댓글 번호를 구합니다.
		const comment_idx = $(this).next().attr('data-id');
		const selector = '#' + comment_idx;

		//.comment-write 영역 삭제 합니다.
		$(selector + ' .comment-write').remove();

		//숨겨두었던 .comment-nick-area 영역 보여줍니다.
		$(selector + '>.comment-nick-area').css('display','block');

		// 수정 폼이 있는 상태에서 더보기를 클릭할 수 없도록 더 보기 영역을 숨겼는데 취소를 선택하면 보여주도록 합니다.
		$(".comment-tool").show();

	}) // 수정 후 취소 버튼을 클릭한 경우


	//답글완료 클릭한 경우
	$('.comment-area').on('click', '.reply', function(){
		const content= $(this).parent().parent().find('.comment-write-area-text').val();
		if(!content){ //내용없이 답글완료 클릭한 경우
			alert("답글을 입력하세요");
			return;
		}

        // 비밀댓글 체크박스 상태를 'Y' 또는 'N'으로 설정
        const is_secret = $('#is_secret').prop('checked') ? 'Y' : 'N';

    $.ajax({
      type: 'post',
      url: '../comment/reply',
      data: {
        id: $("#loginid").val(),
        writer: $("#idx").val(),
        content: content,
        post_idx: $("#post_idx").val(),
          is_secret: is_secret,  // 체크된 경우 'Y', 아닌 경우 'N'
        comment_ref_id: $(this).attr('data-ref') // 부모 댓글의 comment_idx를 comment_ref_id로 설정v @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
      },
      success: function(rdata) {
        if (rdata === 1)
          getList(option); // 댓글 리스트 갱신
      }
    });// ajax

	//답글 폼 보여줍니다.
    $("body > div > div.comment-area > div.comment-write").show(); // 댓글 폼 보이기
  }); //답글완료 클릭한 경우

  // 답글쓰기 후 취소 버튼을 클릭한 경우
	$('.comment-area').on('click','.reply-cancel', function(){
		$(this).parent().parent().parent().remove();
		$(".comment-tool").show(); // 더 보기 영역 보이도록 합니다.

		//댓글 폼 보여줍니다.
		$("body> div > div.comment-area > div.comment-write").show();
	})//답글쓰기 후 취소 버튼을 클릭한 경우

	//답글쓰기 클릭 후 계속 누르는 것을 방지하기 위한 작업
	$('.comment-area').on('click','.comment-info-button', function(event) {
		//답변쓰기 폼이 있는 상태에서 더보기를 클릭할 수 없도록 더 보기 영역을 숨겨요
		$(".comment-tool").hide();

		//답글쓰기 폼의 갯수를 구합니다.
		const length=$(".comment-area .btn-register.reply").length;
		if(length===1){ //답글쓰기 폼이 한 개가 존재하면 anchor 태그(<a>)의 기본 이벤트를 막아
					   //또 다른 답글쓰기 폼이 나타나지 않도록 합니다.
		event.preventDefault();
		}
	})//답글쓰기 클릭 후 계속 누르는 것을 방지하기 위한 작업


});





$(document).ready(function() {


    $('#delete-post-btn').click(function() {
        // 삭제 확인 알림창
        if (confirm("게시글을 삭제하시겠습니까?")) {
            // 삭제 요청 보내기
            var num = $('#post_idx').val(); // 게시글 ID 가져오기
            $.ajax({
                type: "POST",
                url: "../post/delete", // 게시글 삭제 URL
                data: {
                    num: num // 삭제할 게시글 ID
                },
                success: function(response) {
                    alert("게시글이 삭제되었습니다.");
                    // 삭제 후 목록 페이지로 이동
                    window.location.href = '../post/list';
                },
                error: function() {
                    alert("게시글 삭제에 실패했습니다. 다시 시도해주세요.");
                }
            });
        } else {
            alert("게시글 삭제를 취소했습니다.");
        }
    });
});