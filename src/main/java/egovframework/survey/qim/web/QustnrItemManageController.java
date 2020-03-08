package egovframework.survey.qim.web;

import java.util.List;
import java.util.Map;

import egovframework.com.cmm.ComDefaultVO;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.util.EgovUserDetailsHelper;

import egovframework.rte.fdl.property.EgovPropertyService;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import egovframework.survey.qim.service.QustnrItemManageService;
import egovframework.survey.qim.service.QustnrItemManageVO;
import egovframework.survey.qqm.service.QustnrQestnManageService;
import egovframework.survey.qqm.service.QustnrQestnManageVO;

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
/**
 * 설문항목관리를 처리하는 Controller Class 구현
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
 *
 * </pre>
 */

@Controller
public class QustnrItemManageController {

	private static final Logger LOGGER = LoggerFactory.getLogger(QustnrItemManageController.class);

	@Autowired
	private DefaultBeanValidator beanValidator;

	/** EgovMessageSource */
    @Resource(name="egovMessageSource")
    EgovMessageSource egovMessageSource;

	@Resource(name = "qustnrItemManageService")
	private QustnrItemManageService qustnrItemManageService;

    /** EgovPropertyService */
    @Resource(name = "propertiesService")
    protected EgovPropertyService propertiesService;
    
	@Resource(name = "qustnrQestnManageService")
	private QustnrQestnManageService qustnrQestnManageService;

	/**
	 * 설문항목 팝업 목록을 조회한다.
	 * @param searchVO
	 * @param commandMap
	 * @param qustnrItemManageVO
	 * @param model
	 * @return "egovframework/survey/qim/qustnrItemManageListPopup"
	 * @throws Exception
	 */
	@RequestMapping(value="/survey/qim/qustnrItemManageListPopup.do")
	public String egovQustnrItemManageListPopup(
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@RequestParam Map<?, ?> commandMap,
			QustnrItemManageVO qustnrItemManageVO,
    		ModelMap model)
    throws Exception {

		String sCmd = commandMap.get("cmd") == null ? "" : (String)commandMap.get("cmd");
		if(sCmd.equals("del")){
			qustnrItemManageService.deleteQustnrItemManage(qustnrItemManageVO);
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

        List<?> sampleList = qustnrItemManageService.selectQustnrItemManageList(searchVO);
        model.addAttribute("resultList", sampleList);

        model.addAttribute("searchKeyword", commandMap.get("searchKeyword") == null ? "" : (String)commandMap.get("searchKeyword"));
        model.addAttribute("searchCondition", commandMap.get("searchCondition") == null ? "" : (String)commandMap.get("searchCondition"));

        int totCnt = qustnrItemManageService.selectQustnrItemManageListCnt(searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
        model.addAttribute("paginationInfo", paginationInfo);

		return "egovframework/survey/qim/qustnrItemManageListPopup";
	}

	/**
	 * 설문항목 목록을 조회한다.
	 * @param searchVO
	 * @param commandMap
	 * @param qustnrItemManageVO
	 * @param model
	 * @return "egovframework/survey/qim/qustnrItemManageList"
	 * @throws Exception
	 */
	@IncludedInfo(name="항목관리", order = 640 ,gid = 50)
	@RequestMapping(value="/survey/qim/qustnrItemManageList.do")
	public String egovQustnrItemManageList(
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@RequestParam Map<?, ?> commandMap,
			QustnrItemManageVO qustnrItemManageVO,
			@ModelAttribute("qustnrQestnManageVO") QustnrQestnManageVO qustnrQestnManageVO,
    		ModelMap model)
    throws Exception {

		String sSearchMode = commandMap.get("searchMode") == null ? "" : (String)commandMap.get("searchMode");

		//설문문항에 넘어온 건에 대해 조회
		if(sSearchMode.equals("Y")){
			searchVO.setSearchCondition("QUSTNR_QESITM_ID");//qestnrQesitmId
			searchVO.setSearchKeyword(qustnrItemManageVO.getQestnrQesitmId());
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

        List<?> sampleList = qustnrItemManageService.selectQustnrItemManageList(searchVO);
        model.addAttribute("resultList", sampleList);

        model.addAttribute("searchKeyword", commandMap.get("searchKeyword") == null ? "" : (String)commandMap.get("searchKeyword"));
        model.addAttribute("searchCondition", commandMap.get("searchCondition") == null ? "" : (String)commandMap.get("searchCondition"));

        int totCnt = qustnrItemManageService.selectQustnrItemManageListCnt(searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
        model.addAttribute("paginationInfo", paginationInfo);
        List<?> qustnrQestnManageDetail = qustnrQestnManageService.selectQustnrQestnManageDetail(qustnrQestnManageVO);
        model.addAttribute("qustnrQestnManageDetail", qustnrQestnManageDetail);

		return "egovframework/survey/qim/qustnrItemManageList";
	}

	/**
	 * 설문항목 목록을 상세조회 조회한다.
	 * @param searchVO
	 * @param qustnrItemManageVO
	 * @param commandMap
	 * @param model
	 * @return  "/survey/qim/qustnrItemManageDetail"
	 * @throws Exception
	 */
	@RequestMapping(value="/survey/qim/qustnrItemManageDetail.do")
	public String egovQustnrItemManageDetail(
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			QustnrItemManageVO qustnrItemManageVO,
			@RequestParam Map<?, ?> commandMap,
    		ModelMap model)
    throws Exception {

		String sLocationUrl = "egovframework/survey/qim/qustnrItemManageDetail";

		String sCmd = commandMap.get("cmd") == null ? "" : (String)commandMap.get("cmd");

		if(sCmd.equals("del")){
			qustnrItemManageService.deleteQustnrItemManage(qustnrItemManageVO);
			sLocationUrl = "redirect:/survey/qim/qustnrItemManageList.do";
		}else{
	        List<?> sampleList = qustnrItemManageService.selectQustnrItemManageDetail(qustnrItemManageVO);
	        model.addAttribute("resultList", sampleList);
		}

		return sLocationUrl;
	}

	/**
	 * 설문항목를 수정한다.
	 * @param searchVO
	 * @param commandMap
	 * @param qustnrItemManageVO
	 * @param bindingResult
	 * @param model
	 * @return "egovframework/survey/qim/qustnrItemManageModify"
	 * @throws Exception
	 */
	@RequestMapping(value="/survey/qim/qustnrItemManageModify.do")
	public String qustnrItemManageModify(
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@RequestParam Map<?, ?> commandMap,
			@ModelAttribute("qustnrItemManageVO") QustnrItemManageVO qustnrItemManageVO,
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

		String sLocationUrl = "egovframework/survey/qim/qustnrItemManageModify";

		String sCmd = commandMap.get("cmd") == null ? "" : (String)commandMap.get("cmd");

        if(sCmd.equals("save")){

    		//서버  validate 체크
            beanValidator.validate(qustnrItemManageVO, bindingResult);
    		if(bindingResult.hasErrors()){
            	//설문항목(을)를  정보 불러오기
                List<?> listQustnrTmplat = qustnrItemManageService.selectQustnrTmplatManageList(qustnrItemManageVO);
                model.addAttribute("listQustnrTmplat", listQustnrTmplat);
            	//게시물 불러오기
                List<?> sampleList = qustnrItemManageService.selectQustnrItemManageDetail(qustnrItemManageVO);
                model.addAttribute("resultList", sampleList);

                return "egovframework/survey/qim/qustnrItemManageModify";
    		}

    		//아이디 설정
    		qustnrItemManageVO.setFrstRegisterId(loginVO.getUniqId());
    		qustnrItemManageVO.setLastUpdusrId(loginVO.getUniqId());

        	qustnrItemManageService.updateQustnrItemManage(qustnrItemManageVO);
        	sLocationUrl = "redirect:/survey/qim/qustnrItemManageList.do";
        }else{
            List<?> sampleList = qustnrItemManageService.selectQustnrItemManageDetail(qustnrItemManageVO);
            model.addAttribute("resultList", sampleList);

        	//설문항목(을)를  정보 불러오기
            List<?> listQustnrTmplat = qustnrItemManageService.selectQustnrTmplatManageList(qustnrItemManageVO);
            model.addAttribute("listQustnrTmplat", listQustnrTmplat);
        }

		return sLocationUrl;
	}

	/**
	 * 설문항목를 등록한다.
	 * @param searchVO
	 * @param commandMap
	 * @param qustnrItemManageVO
	 * @param bindingResult
	 * @param model
	 * @return "egovframework/survey/qim/qustnrItemManageRegist"
	 * @throws Exception
	 */
	@RequestMapping(value="/survey/qim/qustnrItemManageRegist.do")
	public String qustnrItemManageRegist(
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@RequestParam Map<?, ?> commandMap,
			@ModelAttribute("qustnrItemManageVO") QustnrItemManageVO qustnrItemManageVO,
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

		String sLocationUrl = "egovframework/survey/qim/qustnrItemManageRegist";

		String sCmd = commandMap.get("cmd") == null ? "" : (String)commandMap.get("cmd");
		LOGGER.info("cmd => {}", sCmd);

        if(sCmd.equals("save")){

    		//서버  validate 체크
            beanValidator.validate(qustnrItemManageVO, bindingResult);
    		if(bindingResult.hasErrors()){
            	//설문항목(을)를  정보 불러오기
                List<?> listQustnrTmplat = qustnrItemManageService.selectQustnrTmplatManageList(qustnrItemManageVO);
                model.addAttribute("listQustnrTmplat", listQustnrTmplat);
                return "egovframework/survey/qim/qustnrItemManageRegist";
    		}

    		//아이디 설정
    		qustnrItemManageVO.setFrstRegisterId(loginVO.getUniqId());
    		qustnrItemManageVO.setLastUpdusrId(loginVO.getUniqId());

        	qustnrItemManageService.insertQustnrItemManage(qustnrItemManageVO);
        	sLocationUrl = "redirect:/survey/qim/qustnrItemManageList.do";
        }else{
        	//설문항목(을)를  정보 불러오기
            List<?> listQustnrTmplat = qustnrItemManageService.selectQustnrTmplatManageList(qustnrItemManageVO);
            model.addAttribute("listQustnrTmplat", listQustnrTmplat);
        }
        List<?> qustnrQestnManageDetail = qustnrQestnManageService.selectQustnrQestnManageDetail(qustnrQestnManageVO);
        model.addAttribute("qustnrQestnManageDetail", qustnrQestnManageDetail);

		return sLocationUrl;
	}

}


