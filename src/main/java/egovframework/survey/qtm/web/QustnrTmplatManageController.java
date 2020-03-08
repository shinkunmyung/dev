package egovframework.survey.qtm.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.com.cmm.ComDefaultVO;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.rte.fdl.property.EgovPropertyService;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import egovframework.survey.qtm.service.QustnrTmplatManageService;
import egovframework.survey.qtm.service.QustnrTmplatManageVO;
/**
 * 설문템플릿 Controller Class 구현
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
public class QustnrTmplatManageController {

	private static final Logger LOGGER = LoggerFactory.getLogger(QustnrTmplatManageController.class);

	@Autowired
	private DefaultBeanValidator beanValidator;

	/** EgovMessageSource */
    @Resource(name="egovMessageSource")
    EgovMessageSource egovMessageSource;

	@Resource(name = "qustnrTmplatManageService")
	private QustnrTmplatManageService qustnrTmplatManageService;

    /** EgovPropertyService */
    @Resource(name = "propertiesService")
    protected EgovPropertyService propertiesService;

    @RequestMapping(value="/survey/qtm/qustnrTmplatManageMain.do")
    public String qustnrTmplatManageMain(ModelMap model) throws Exception {
    	return "egovframework/survey/qtm/qustnrTmplatManageMain";
    }

    @RequestMapping(value="/survey/qtm/qustnrTmplatManageLeft.do")
    public String qustnrTmplatManageLeft(ModelMap model) throws Exception {
    	return "egovframework/survey/qtm/qustnrTmplatManageLeft";
    }

    /**
     * 개별 배포시 메인메뉴를 조회한다.
     * @param model
     * @return	"/uss/sam/cpy/"
     * @throws Exception
     */
    @RequestMapping(value="/uss/olp/EgovMain.do")
    public String egovMain(ModelMap model) throws Exception {
    	return "egovframework/com/uss/olp/qtm/EgovMain";
    }

    /**
     * 메뉴를 조회한다.
     * @param model
     * @return	"/uss/sam/cpy/EgovLeft"
     * @throws Exception
     */
    @RequestMapping(value="/uss/olp/EgovLeft.do")
    public String egovLeft(ModelMap model) throws Exception {
    	return "egovframework/com/uss/olp/qtm/EgovLeft";
    }

	/**
	 * 설문템플릿 목록을 조회한다.
	 * @param searchVO
	 * @param commandMap
	 * @param qustnrTmplatManageVO
	 * @param model
	 * @return "egovframework/survey/qtm/qustnrTmplatManageList"
	 * @throws Exception
	 */
    @IncludedInfo(name="설문템플릿관리", order = 610 ,gid = 50)
	@RequestMapping(value="/survey/qtm/qustnrTmplatManageList.do")
	public String qustnrTmplatManageList(
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@RequestParam Map<?, ?> commandMap,
			QustnrTmplatManageVO qustnrTmplatManageVO,
    		ModelMap model)
    throws Exception {

		String sCmd = commandMap.get("cmd") == null ? "" : (String)commandMap.get("cmd");

		if(sCmd.equals("del")){
			qustnrTmplatManageService.deleteQustnrTmplatManage(qustnrTmplatManageVO);
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

        List<?> sampleList = qustnrTmplatManageService.selectQustnrTmplatManageList(searchVO);
        model.addAttribute("resultList", sampleList);

        model.addAttribute("searchKeyword", commandMap.get("searchKeyword") == null ? "" : (String)commandMap.get("searchKeyword"));
        model.addAttribute("searchCondition", commandMap.get("searchCondition") == null ? "" : (String)commandMap.get("searchCondition"));

        int totCnt = qustnrTmplatManageService.selectQustnrTmplatManageListCnt(searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
        model.addAttribute("paginationInfo", paginationInfo);


		return "egovframework/survey/qtm/qustnrTmplatManageList";
	}

	/**
	 * 설문템플릿 이미지 목록을 상세조회 조회한다.
	 * @param request
	 * @param response
	 * @param qustnrTmplatManageVO
	 * @param commandMap
	 * @return "egovframework/survey/qtm/qustnrTmplatManageImg"
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value="/survey/qtm/qustnrTmplatManageImg.do")
	public void qustnrTmplatManageImg(
			 HttpServletRequest request,
			 HttpServletResponse response,
			 QustnrTmplatManageVO qustnrTmplatManageVO,
			 @RequestParam Map<?, ?> commandMap
			 )throws Exception {

		Map<?, ?> mapResult = qustnrTmplatManageService.selectQustnrTmplatManageTmplatImagepathnm(qustnrTmplatManageVO);

		byte[] img = (byte[])mapResult.get("QUSTNR_TMPLAT_IMAGE_INFOPATHNM");

		String imgtype = "jpeg";		
		String type = "";

		if(imgtype != null && !"".equals(imgtype)){
		      type="image/"+imgtype;
		}

		response.setHeader("Content-Type", imgtype);
		response.setHeader ("Content-Length", "" + img.length);
		response.getOutputStream().write(img);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}

	/**
	 * 설문템플릿 목록을 상세조회 조회한다.
	 * @param searchVO
	 * @param qustnrTmplatManageVO
	 * @param commandMap
	 * @param model
	 * @return "egovframework/survey/qtm/qustnrTmplatManageDetail"
	 * @throws Exception
	 */
	@RequestMapping(value="/survey/qtm/qustnrTmplatManageDetail.do")
	public String qustnrTmplatManageDetail(
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			QustnrTmplatManageVO qustnrTmplatManageVO,
			@RequestParam Map<?, ?> commandMap,
    		ModelMap model)
    throws Exception {

		String sLocationUrl = "egovframework/survey/qtm/qustnrTmplatManageDetail";

		String sCmd = commandMap.get("cmd") == null ? "" : (String)commandMap.get("cmd");

		if(sCmd.equals("del")){
			qustnrTmplatManageService.deleteQustnrTmplatManage(qustnrTmplatManageVO);
			sLocationUrl = "redirect:/survey/qtm/qustnrTmplatManageList.do";
		}else{
	        List<?> sampleList = qustnrTmplatManageService.selectQustnrTmplatManageDetail(qustnrTmplatManageVO);
	        model.addAttribute("resultList", sampleList);
		}

		return sLocationUrl;
	}

	/**
	 * 설문템플릿를 수정한다.
	 * @param searchVO
	 * @param commandMap
	 * @param qustnrTmplatManageVO
	 * @param model
	 * @return "egovframework/survey/qtm/qustnrTmplatManageModify"
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value="/survey/qtm/qustnrTmplatManageModify.do")
	public String qustnrTmplatManageModify(
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@RequestParam Map<?, ?> commandMap,
			QustnrTmplatManageVO qustnrTmplatManageVO,
    		ModelMap model)
    throws Exception {
		String sLocationUrl = "egovframework/survey/qtm/qustnrTmplatManageModify";

		String sCmd = commandMap.get("cmd") == null ? "" : (String)commandMap.get("cmd");

        List<?> sampleList = qustnrTmplatManageService.selectQustnrTmplatManageDetail(qustnrTmplatManageVO);
        model.addAttribute("resultList", sampleList);


		return sLocationUrl;
	}

	/**
	 * 설문템플릿를 수정처리 한다.
	 * @param multiRequest
	 * @param searchVO
	 * @param commandMap
	 * @param qustnrTmplatManageVO
	 * @param bindingResult
	 * @param model
	 * @return "egovframework/survey/qtm/qustnrTmplatManageModifyActor"
	 * @throws Exception
	 */
	@RequestMapping(value="/survey/qtm/qustnrTmplatManageModifyActor.do")
	public String qustnrTmplatManageModifyActor(
			final MultipartHttpServletRequest multiRequest,
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@RequestParam Map<?, ?> commandMap,
			@ModelAttribute("qustnrTmplatManageVO") QustnrTmplatManageVO qustnrTmplatManageVO,
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

		//서버  validate 체크
        beanValidator.validate(qustnrTmplatManageVO, bindingResult);
		if (bindingResult.hasErrors()){
	        List<?> sampleList = qustnrTmplatManageService.selectQustnrTmplatManageDetail(qustnrTmplatManageVO);
	        model.addAttribute("resultList", sampleList);
			return "egovframework/survey/qtm/qustnrTmplatManageModify";
		}

		//아이디 설정
		qustnrTmplatManageVO.setFrstRegisterId(loginVO.getUniqId());
		qustnrTmplatManageVO.setLastUpdusrId(loginVO.getUniqId());


			final Map<String, MultipartFile> files = multiRequest.getFileMap();

			if (!files.isEmpty()) {
			  for(MultipartFile file : files.values()){
				  LOGGER.info("getName => {}", file.getName() );
				  LOGGER.info("getOriginalFilename => {}", file.getOriginalFilename() );

		          // 파일 수정여부 확인
	          	  if(file.getOriginalFilename() != "") {
			          if(file.getName().equals("qestnrTmplatImage")){
			          	qustnrTmplatManageVO.setQestnrTmplatImagepathnm( file.getBytes() );
			          }
	          	  }
			 }
		    }
    	qustnrTmplatManageService.updateQustnrTmplatManage(qustnrTmplatManageVO);

		return "redirect:/survey/qtm/qustnrTmplatManageList.do";
	}

	/**
	 * 설문템플릿를 등록한다. / 초기등록페이지
	 * @param searchVO
	 * @param commandMap
	 * @param qustnrTmplatManageVO
	 * @param model
	 * @return "egovframework/survey/qtm/qustnrTmplatManageRegist"
	 * @throws Exception
	 */
	@RequestMapping(value="/survey/qtm/qustnrTmplatManageRegist.do")
	public String qustnrTmplatManageRegist(
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			@RequestParam Map<?, ?> commandMap,
			@ModelAttribute("qustnrTmplatManageVO") QustnrTmplatManageVO qustnrTmplatManageVO,
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

		String sLocationUrl = "egovframework/survey/qtm/qustnrTmplatManageRegist";

		String sCmd = commandMap.get("cmd") == null ? "" : (String)commandMap.get("cmd");
		LOGGER.info("cmd => {}", sCmd);

		//아이디 설정
		qustnrTmplatManageVO.setFrstRegisterId(loginVO.getUniqId());
		qustnrTmplatManageVO.setLastUpdusrId(loginVO.getUniqId());

		return sLocationUrl;
	}

	/**
	 * 설문템플릿를 등록 처리 한다.  / 등록처리
	 * @param multiRequest
	 * @param searchVO
	 * @param qustnrTmplatManageVO
	 * @param model
	 * @return "egovframework/survey/qtm/qustnrTmplatManageRegistActor"
	 * @throws Exception
	 */
	@RequestMapping(value="/survey/qtm/qustnrTmplatManageRegistActor.do")
	public String qustnrTmplatManageRegistActor(
			final MultipartHttpServletRequest multiRequest,
			@ModelAttribute("searchVO") ComDefaultVO searchVO,
			QustnrTmplatManageVO qustnrTmplatManageVO,
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

		//아이디 설정
		qustnrTmplatManageVO.setFrstRegisterId(loginVO.getUniqId());
		qustnrTmplatManageVO.setLastUpdusrId(loginVO.getUniqId());

		final Map<String, MultipartFile> files = multiRequest.getFileMap();

		 if (!files.isEmpty()) {
        		for(MultipartFile file : files.values()){
        			LOGGER.info("getName => {}", file.getName() );
        			LOGGER.info("getOriginalFilename => {}", file.getOriginalFilename() );
        	           /*if(file.getName().equals("qestnrTmplatImage")){
        	        	   qustnrTmplatManageVO.setQestnrTmplatImagepathnm( file.getBytes() );
        	           }*/
        			
        	           if((!file.getName().equals("") || !file.getName().equals(null))
        	        	   && (!file.getOriginalFilename().equals("") || !file.getOriginalFilename().equals(null))){           	           		
        	        	   qustnrTmplatManageVO.setQestnrTmplatImagepathnm( file.getBytes() );
           	           }
        		}
	     }

    	//log.info("qestnrTmplatImagepathnm =>" + qustnrTmplatManageVO.getQestnrTmplatImagepathnm() );

    	qustnrTmplatManageService.insertQustnrTmplatManage(qustnrTmplatManageVO);

    	return "redirect:/survey/qtm/qustnrTmplatManageList.do";
	}


}


