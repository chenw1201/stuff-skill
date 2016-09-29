package jp.wicresoft.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.wicresoft.impl.SearchImpl;
import jp.wicresoft.info.IndexViewInfo;
import jp.wicresoft.info.SkillSearchInfo;

@Controller
public class SearchController {

	@Autowired
	SearchImpl searchImpl;
	
	Logger logger = LoggerFactory.getLogger(NewController.class);
	
	@RequestMapping(value={"/search"})
	public String loadPage(Model model) {
		model.addAttribute("page", "search");
		SkillSearchInfo skill = new SkillSearchInfo();
		skill.setLsSkill(new ArrayList<String>());
		skill.getLsSkill().add("0");
		model.addAttribute("skillSearchInfo", skill);
		model.addAttribute("skillOptions", searchImpl.getSkillNameList());
		return "search";
	}
	
	@RequestMapping(value={"/search_result"})
	public String loadPage(@Valid SkillSearchInfo skillSearchInfo, Model model) {
		logger.info("Search for: " + skillSearchInfo.getLsSkill());
		model.addAttribute("page", "search");
		
		try {
			List<Long> conditionList = new ArrayList<Long>();
			for (String skillIdStr : skillSearchInfo.getLsSkill()) {
				conditionList.add(Long.parseLong(skillIdStr));
			}
			if (conditionList.isEmpty()) {
				// 検索条件がない場合
				model.addAttribute("skillSearchInfo", skillSearchInfo);
				model.addAttribute("skillOptions", searchImpl.getSkillNameList());
				return "search";
			}
			List<IndexViewInfo> indexViewInfos = searchImpl.getSearchResultBySkill(conditionList);
			if (!indexViewInfos.isEmpty()) {
				// 結果を出す
				model.addAttribute("stuffMetas", indexViewInfos);
				model.addAttribute("category", "検索結果　");
				return "stuff_info";
			}
		} catch (Exception e) {
			logger.info(e.getLocalizedMessage());
		}
		model.addAttribute("skillSearchInfo", skillSearchInfo);
		model.addAttribute("skillOptions", searchImpl.getSkillNameList());
		
		return "search";
	}
}
