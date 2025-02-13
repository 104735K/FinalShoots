package com.Shoots.service;

import com.Shoots.domain.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;

public interface PostService {


    public void setAvailable(int post_idx);

    public void setCompleted(int post_idx);

    //report_status block으로 바꾸기
    void setBlock(int id);


//    static void getCompleted(String status) {
//    }
//
//    static void getAvailable(String status) {
//
//    }

    //글의 갯수 구하기
    public int getListCount(String category, String search_word);

    // 글 목록 보기
    public List<Post> getPostList(int page, int limit, String category, String search_word, String searchWord);

    public List<Post> getAdminPostList(String search_word, int page, int limit);

    //업로드된 파일(MultipartFile)을 주어진 폴더(saveFolder)에 저장하고, 데이터베이스에 저장할 파일 경로를 반환
    default public String saveUploadFile(MultipartFile uploadfile, String saveFolder) throws Exception {
        String originalFilename = uploadfile.getOriginalFilename();
        String fileDBName = fileDBName(originalFilename, saveFolder);

        //파일 저장
        uploadfile.transferTo(new File(saveFolder + fileDBName));

        return fileDBName;
    }


    //데이터베이스에 저장할 파일 이름(경로 포함)을 생성
    default public String fileDBName(String fileName, String saveFolder) {
        String dateFolder = createFolderByDate(saveFolder);
        String fileExtension = getFileExtension(fileName);
        String refileName = generateUniqueFileName(fileExtension);
        return File.separator + dateFolder + File.separator + refileName;
    }


    default public int[] getCurrentDate() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int date = now.getDayOfMonth();

        return new int[]{year, month, date};
    }


    //현재 날짜를 기준으로 폴더를 생성
    default public String createFolderByDate(String baseFolder) {
        int[] cuurentDate = getCurrentDate();
        int year = cuurentDate[0];
        int month = cuurentDate[1];
        int date = cuurentDate[2];

        String dateFolder = year + "-" + month + "-" + date;
        String fullFolderPath = baseFolder + File.separator + dateFolder;
        File path = new File(fullFolderPath);

        if(!path.exists()) {
            path.mkdirs();
        }
        return dateFolder;
    }


    //파일 이름에서 확장자를 추출
    default public String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        return (index > 0) ? fileName.substring(index+1) : "";
    }


    //날짜 및 난수를 기반으로 고유 파일 이름을 생성
    default public String generateUniqueFileName(String fileExtension) {
        int[] currentDate = getCurrentDate();
        int year = currentDate[0];
        int month = currentDate[1];
        int date = currentDate[2];

        Random r = new Random();
        int random = r.nextInt(100000000);

        return "bbs" + year + month + date + random + "." + fileExtension;
    }


    // 글 등록하기 - 게시글 데이터를 데이터베이스에 저장
    void insertPost(Post post);

    //특정 글 번호(num)의 조회수를 업데이트
    void setReadCountUpdate(int num);

    //특정 글 번호(num)의 상세 정보를 반환
    Post getDetail(int num);

    //특정 글 번호(postNum)의 작성자가 현재 사용자와 같은지 확인
    boolean isPostWriter(int postNum);
    // boolean isPostWriter(int postNum);

    //주어진 게시글 데이터를 수정
    int postModify(Post postdata);

    //특정 글 번호(num)의 게시글을 삭제
    int postDelete(int num);

    //삭제해야 할 파일 목록을 반환
    List<String> getDeleteFileList();

    //특정 파일 이름(filename)을 기준으로 파일을 삭제
    void deleteFileList(String filename);

    //관리자용 listcount
    int getAdminListCount(String search_word);

    //MyPage 내가 쓴 post list
    List<Post> getMyPostList(int id);

    //MyPage 내가 쓴 post count
    int getMyPostListCount(int id);

    //admin chart용 count, register_date
    public List<Map<String, Object>> getPostCount();

    //admin chart용 category별 post 개수
    List<Map<String, Object>> getCategoryCount();

}
