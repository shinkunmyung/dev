package egovframework.survey.qqm.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.com.cmm.ComDefaultCodeVO;
import egovframework.com.cmm.ComDefaultVO;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.service.EgovCmmUseService;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.rte.fdl.property.EgovPropertyService;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import egovframework.survey.qqm.service.QustnrQestnManageService;
import egovframework.survey.qqm.service.QustnrQestnManageVO;
/**
 * 설문문항을 처리하는 Controller Class 구현
 * @author 공통서비스 장동한
 * @since 2009.03.20
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.03.20  장동한          최초 생성
 *   2011.8.26	정진오			IncludedInfo annotation 추가
 *   2017.09.04  김예영          표준프레임워크 v3.7 개선
 *
 * </pre>
 */

@Controller
public class QustnrQestnManageController {

	private static final Logger LOGGER = LoggerFactory.getLogger(QustnrQestnManageController.class);

	@Autowired
	private DefaultBeanValidator beanValidator;

	/** EgovMessageSource */
    @Resource(name="egovMessageSource")
    EgovMessageSource egovMessageSource;

	@Resource(name = "qustnrQestnManageService")
	private QustnrQestnManageService qustnrQestnManageService;

    /** EgovPropertyService */
    @Resource(name = "propertiesService")
    protected EgovPropertyService propertiesService;

	@Resource(name="EgovCmmUseService")
	private EgovCmmUseService cmmUseService;

    /**
     * 설문문항 통계를 조회한다.
     * @param searchVO
     * @param qustnrQestnManageVO
     * @param commandMap
     * @param model
     * @return "egovframework/survey/qqm/qustnrQestnManageStatistics"
     * @throws Exception
     */
    @RequestMapping(value="/survey/qqm/qustnrQestnManageStatistics.do")
	public String egovQustnrQestnManageStatistics(
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			QustnrQestnManageVO qustnrQestnManageVO,
			@RequestParam Map<?, ?> commandMap,
    		ModelMap model)
    throws Exception {

		String sLocationUrl = "egovframework/survey/qqm/qustnrQestnManageStatistics";

        List<?> sampleList = qustnrQestnManageService.selectQustnrQestnManageDetail(qustnrQestnManageVO);
        model.addAttribute("resultList", sampleList);
        
        HashMap<String, String> mapParam = new HashMap<String, String>();
        mapParam.put("qestnrQesitmId", qustnrQestnManageVO.getQestnrQesitmId());
       
        //System.out.println("qustnrQestnManageVO.getQestnTyCode() : "+qustnrQestnManageVO.getQestnTyCode());
        
	    if(qustnrQestnManageVO.getQestnTyCode().equals("2")){ 
	        //주관식 설문통계
	    	List<?> statisticsList2 = qustnrQestnManageService.selectQustnrManageStatistics2(mapParam);
	    	model.addAttribute("statisticsList2", statisticsList2);
       
       }else{ 
    	    //객관식설문통계
    	   	List<?> statisticsList = qustnrQestnManageService.selectQustnrManageStatistics(mapParam);
    	   	model.addAttribute("statisticsList", statisticsList);
		    //객관식과 주관식 문항이 동시에 달렸을 경우 주관식 설문 통계
		    List<?> statisticsList2 = qustnrQestnManageService.selectQustnrManageStatistics2(mapParam);
		    model.addAttribute("statisticsList2", statisticsList2);
       } 
        
	    return sLocationUrl;
	}

	/**
	 * 설문문항 팝업 목록을 조회한다.
	 * @param searchVO
	 * @param qustnrQestnManageVO
	 * @param commandMap
	 * @param model
	 * @return "egovframework/survey/qqm/qustnrQestnManageListPopup"
	 * @throws Exception
	 */
	@RequestMapping(value="/survey/qqm/qustnrQestnManageListPopup.do")
	public String egovQustnrQestnManageListPopup(
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@ModelAttribute("qustnrQestnManageVO") QustnrQestnManageVO qustnrQestnManageVO,
			@RequestParam Map<?, ?> commandMap,
    		ModelMap model)
    throws Exception {

		String sSearchMode = commandMap.get("searchMode") == null ? "" : (String)commandMap.get("searchMode");

		//설문지정보에서 넘어오면 자동검색 설정
		if(sSearchMode.equals("Y")){
			searchVO.setSearchCondition("QESTNR_ID");
			searchVO.setSearchKeyword(qustnrQestnManageVO.getQestnrId());
		}

    	/** EgovPropertyService.sample */
    	searchVO.setPageUnit(propertiesService.getInt("pageUnit"));
    	searchVO.setPageSize(propertiesService.getInt("pageSize"));

    	/** pageing */
    	PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(searchVO.getPageUnit());
		paginationInfo.setPageSize(searchVO.getPageSize());

		searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		searchVO.setLastIndex(paginationInfo.getLastRecordIndex());
		searchVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

        List<?> resultList = qustnrQestnManageService.selectQustnrQestnManageList(searchVO);
        model.addAttribute("resultList", resultList);

        int totCnt = qustnrQestnManageService.selectQustnrQestnManageListCnt(searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
        model.addAttribute("paginationInfo", paginationInfo);

		return "egovframework/survey/qqm/qustnrQestnManageListPopup";
	}

	/**
	 * 설문문항 목록을 조회한다.
	 * @param searchVO
	 * @param qustnrQestnManageVO
	 * @param commandMap
	 * @param model
	 * @return "egovframework/survey/qqm/qustnrQestnManageList"
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@IncludedInfo(name="질문관리", order = 630 ,gid = 50)
	@RequestMapping(value="/survey/qqm/qustnrQestnManageList.do")
	public String egovQustnrQestnManageList(
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@ModelAttribute("qustnrQestnManageVO") QustnrQestnManageVO qustnrQestnManageVO,
			@RequestParam Map<?, ?> commandMap,
    		ModelMap model)
    throws Exception {
    	// 0. Spring Security 사용자권한 처리
    	Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
    	if(!isAuthenticated) {
    		model.addAttribute("message", egovMessageSource.getMessage("fail.common.login"));
        	return "egovframework/com/uat/uia/EgovLoginUsr";
    	}

		//로그인 객체 선언
		LoginVO loginVO = (LoginVO)EgovUserDetailsHelper.getAuthenticatedUser();

		String sCmd = commandMap.get("cmd") == null ? "" : (String)commandMap.get("cmd");
		String sSearchMode = commandMap.get("searchMode") == null ? "" : (String)commandMap.get("searchMode");

		if(sCmd.equals("del")){
			qustnrQestnManageService.deleteQustnrQestnManage(qustnrQestnManageVO);
		}

		//설문지정보에서 넘어오면 자동검색 설정
		if(sSearchMode.equals("Y")){
			searchVO.setSearchCondition("QESTNR_ID");
			searchVO.setSearchKeyword(qustnrQestnManageVO.getQestnrId());
		}

    	/** EgovPropertyService.sample */
    	searchVO.setPageUnit(propertiesService.getInt("pageUnit"));
    	searchVO.setPageSize(propertiesService.getInt("pageSize"));

    	/** pageing */
    	PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(searchVO.getPageUnit());
		paginationInfo.setPageSize(searchVO.getPageSize());

		searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		searchVO.setLastIndex(paginationInfo.getLastRecordIndex());
		searchVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

        List<?> sampleList = qustnrQestnManageService.selectQustnrQestnManageList(searchVO);
        model.addAttribute("resultList", sampleList);

        /** 2017.09.04 model에 addAttribute 추가 */
        model.addAttribute("searchKeyword", commandMap.get("searchKeyword") == null ? "" : (String)commandMap.get("searchKeyword"));
        model.addAttribute("searchCondition", commandMap.get("searchCondition") == null ? "" : (String)commandMap.get("searchCondition"));
        
        int totCnt = qustnrQestnManageService.selectQustnrQestnManageListCnt(searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
        model.addAttribute("paginationInfo", paginationInfo);

		return "egovframework/survey/qqm/qustnrQestnManageList";
	}

	/**
	 * 설문문항 목록을 상세조회 조회한다.
	 * @param searchVO
	 * @param qustnrQestnManageVO
	 * @param commandMap
	 * @param model
	 * @return "egovframework/survey/qqm/qustnrQestnManageDetail"
	 * @throws Exception
	 */
	@RequestMapping(value="/survey/qqm/qustnrQestnManageDetail.do")
	public String egovQustnrQestnManageDetail(
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@ModelAttribute("qustnrQestnManageVO") QustnrQestnManageVO qustnrQestnManageVO,
			@RequestParam Map<?, ?> commandMap,
    		ModelMap model)
    throws Exception {

		String sLocationUrl = "egovframework/survey/qqm/qustnrQestnManageDetail";

		String sCmd = commandMap.get("cmd") == null ? "" : (String)commandMap.get("cmd");

		if(sCmd.equals("del")){
			qustnrQestnManageService.deleteQustnrQestnManage(qustnrQestnManageVO);
			/** 목록으로갈때 검색조건 유지 */
			sLocationUrl = "redirect:/survey/qqm/qustnrQestnManageList.do?";
        	sLocationUrl = sLocationUrl + "searchMode=" + qustnrQestnManageVO.getSearchMode();
        	sLocationUrl = sLocationUrl + "&qestnrId=" + qustnrQestnManageVO.getQestnrId();
        	sLocationUrl = sLocationUrl + "&qestnrTmplatId=" +qustnrQestnManageVO.getQestnrTmplatId();
		}else{
	     	//공통코드 질문유형 조회
	    	ComDefaultCodeVO voComCode = new ComDefaultCodeVO();
	    	voComCode.setCodeId("COM018");
	    	List<?> listComCode = cmmUseService.selectCmmCodeDetail(voComCode);
	    	model.addAttribute("cmmCode018", listComCode);

	        List<?> sampleList = qustnrQestnManageService.selectQustnrQestnManageDetail(qustnrQestnManageVO);
	        model.addAttribute("resultList", sampleList);
		}

		return sLocationUrl;
	}

	/**
	 * 설문문항를 수정한다.
	 * @param searchVO
	 * @param commandMap
	 * @param qustnrQestnManageVO
	 * @param bindingResult
	 * @param model
	 * @return "egovframework/survey/qqm/qustnrQestnManageModify"
	 * @throws Exception
	 */
	@RequestMapping(value="/survey/qqm/qustnrQestnManageModify.do")
	public String qustnrQestnManageModify(
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@RequestParam Map<?, ?> commandMap,
			@ModelAttribute("qustnrQestnManageVO") QustnrQestnManageVO qustnrQestnManageVO,
			BindingResult bindingResult,
    		ModelMap model)
    throws Exception {
    	// 0. Spring Security 사용자권한 처리
    	Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
    	if(!isAuthenticated) {
    		model.addAttribute("message", egovMessageSource.getMessage("fail.common.login"));
        	return "egovframework/com/uat/uia/EgovLoginUsr";
    	}

		//로그인 객체 선언
		LoginVO loginVO = (LoginVO)EgovUserDetailsHelper.getAuthenticatedUser();

		String sLocationUrl = "egovframework/survey/qqm/qustnrQestnManageModify";

		String sCmd = commandMap.get("cmd") == null ? "" : (String)commandMap.get("cmd");

     	//공통코드 질문유형 조회
    	ComDefaultCodeVO voComCode = new ComDefaultCodeVO();
    	voComCode.setCodeId("COM018");
    	List<?> listComCode = cmmUseService.selectCmmCodeDetail(voComCode);
    	model.addAttribute("cmmCode018", listComCode);

        if(sCmd.equals("save")){
    		//서버  validate 체크
            beanValidator.validate(qustnrQestnManageVO, bindingResult);
    		if (bindingResult.hasErrors()){
            	//설문제목가져오기
            	String sQestnrId = commandMap.get("qestnrId") == null ? "" : (String)commandMap.get("qestnrId");
            	String sQestnrTmplatId = commandMap.get("qestnrTmplatId") == null ? "" : (String)commandMap.get("qestnrTmplatId");

            	LOGGER.info("sQestnrId => {}", sQestnrId);
            	LOGGER.info("sQestnrTmplatId => {}", sQestnrTmplatId);
            	if(!sQestnrId.equals("") && !sQestnrTmplatId.equals("")){

            		Map<String, String> mapQustnrManage = new HashMap<String, String>();
            		mapQustnrManage.put("qestnrId", sQestnrId);
            		mapQustnrManage.put("qestnrTmplatId", sQestnrTmplatId);

            		model.addAttribute("qestnrInfo", qustnrQestnManageService.selectQustnrManageQestnrSj(mapQustnrManage));
            	}

                List<?> resultList = qustnrQestnManageService.selectQustnrQestnManageDetail(qustnrQestnManageVO);
                model.addAttribute("resultList", resultList);
            	return "egovframework/survey/qqm/qustnrQestnManageModify";
    		}

    		//아이디 설정
    		qustnrQestnManageVO.setFrstRegisterId(loginVO.getUniqId());
    		qustnrQestnManageVO.setLastUpdusrId(loginVO.getUniqId());

        	qustnrQestnManageService.updateQustnrQestnManage(qustnrQestnManageVO);
        	/** 목록으로갈때 검색조건 유지 */
        	sLocationUrl = "redirect:/survey/qqm/qustnrQestnManageList.do?";
        	sLocationUrl = sLocationUrl + "searchMode=" + qustnrQestnManageVO.getSearchMode();
        	sLocationUrl = sLocationUrl + "&qestnrId=" + qustnrQestnManageVO.getQestnrId();
        	sLocationUrl = sLocationUrl + "&qestnrTmplatId=" +qustnrQestnManageVO.getQestnrTmplatId();
        }else{
            List<?> resultList = qustnrQestnManageService.selectQustnrQestnManageDetail(qustnrQestnManageVO);
            model.addAttribute("resultList", resultList);

        }

		return sLocationUrl;
	}

	/**
	 * 설문문항를 등록한다.
	 * @param searchVO
	 * @param commandMap
	 * @param qustnrQestnManageVO
	 * @param bindingResult
	 * @param model
	 * @return "egovframework/survey/qqm/qustnrQestnManageRegist"
	 * @throws Exception
	 */
	@RequestMapping(value="/survey/qqm/qustnrQestnManageRegist.do")
	public String qustnrQestnManageRegist(
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@RequestParam Map<?, ?> commandMap,
			@ModelAttribute("qustnrQestnManageVO") QustnrQestnManageVO qustnrQestnManageVO,
			BindingResult bindingResult,
    		ModelMap model)
    throws Exception {
    	// 0. Spring Security 사용자권한 처리
    	Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
    	if(!isAuthenticated) {
    		model.addAttribute("message", egovMessageSource.getMessage("fail.common.login"));
        	return "egovframework/com/uat/uia/EgovLoginUsr";
    	}

		//로그인 객체 선언
		LoginVO loginVO = (LoginVO)EgovUserDetailsHelper.getAuthenticatedUser();

		String sLocationUrl = "egovframework/survey/qqm/qustnrQestnManageRegist";

		String sCmd = commandMap.get("cmd") == null ? "" : (String)commandMap.get("cmd");
		LOGGER.info("cmd => {}", sCmd);

     	//공통코드 질문유형 조회
    	ComDefaultCodeVO voComCode = new ComDefaultCodeVO();
    	voComCode.setCodeId("COM018");
    	List<?> listComCode = cmmUseService.selectCmmCodeDetail(voComCode);
    	model.addAttribute("cmmCode018", listComCode);

        if(sCmd.equals("save")){

    		//서버  validate 체크
            beanValidator.validate(qustnrQestnManageVO, bindingResult);
    		if (bindingResult.hasErrors()){
            	//설문제목가져오기
            	String sQestnrId = commandMap.get("qestnrId") == null ? "" : (String)commandMap.get("qestnrId");
            	String sQestnrTmplatId = commandMap.get("qestnrTmplatId") == null ? "" : (String)commandMap.get("qestnrTmplatId");

            	LOGGER.info("sQestnrId => {}", sQestnrId);
            	LOGGER.info("sQestnrTmplatId => {}", sQestnrTmplatId);
            	if(!sQestnrId.equals("") && !sQestnrTmplatId.equals("")){

            		Map<String, String> mapQustnrManage = new HashMap<String, String>();
            		mapQustnrManage.put("qestnrId", sQestnrId);
            		mapQustnrManage.put("qestnrTmplatId", sQestnrTmplatId);

            		model.addAttribute("qestnrInfo", qustnrQestnManageService.selectQustnrManageQestnrSj(mapQustnrManage));
            	}

    			return "egovframework/survey/qqm/qustnrQestnManageRegist";
    		}

    		//아이디 설정
    		qustnrQestnManageVO.setFrstRegisterId(loginVO.getUniqId());
    		qustnrQestnManageVO.setLastUpdusrId(loginVO.getUniqId());
    		/** 목록으로갈때 검색조건 유지 */
        	qustnrQestnManageService.insertQustnrQestnManage(qustnrQestnManageVO);
        	sLocationUrl = "redirect:/survey/qqm/qustnrQestnManageList.do?";
        	sLocationUrl = sLocationUrl + "searchMode=" + qustnrQestnManageVO.getSearchMode();
        	sLocationUrl = sLocationUrl + "&qestnrId=" + qustnrQestnManageVO.getQestnrId();
        	sLocationUrl = sLocationUrl + "&qestnrTmplatId=" +qustnrQestnManageVO.getQestnrTmplatId();

        }else{

        	//설문제목가져오기
        	String sQestnrId = commandMap.get("qestnrId") == null ? "" : (String)commandMap.get("qestnrId");
        	String sQestnrTmplatId = commandMap.get("qestnrTmplatId") == null ? "" : (String)commandMap.get("qestnrTmplatId");

        	LOGGER.info("sQestnrId => {}", sQestnrId);
        	LOGGER.info("sQestnrTmplatId => {}", sQestnrTmplatId);
        	if(!sQestnrId.equals("") && !sQestnrTmplatId.equals("")){

        		Map<String, String> mapQustnrManage = new HashMap<String, String>();
        		mapQustnrManage.put("qestnrId", sQestnrId);
        		mapQustnrManage.put("qestnrTmplatId", sQestnrTmplatId);

        		model.addAttribute("qestnrInfo", qustnrQestnManageService.selectQustnrManageQestnrSj(mapQustnrManage));
        	}

        }

		return sLocationUrl;
	}
}


