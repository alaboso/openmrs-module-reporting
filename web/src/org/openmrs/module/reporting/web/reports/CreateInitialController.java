package org.openmrs.module.reporting.web.reports;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.dimension.Dimension;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.util.ReportUtil;
import org.openmrs.module.report.util.ReportUtil.InitialDataElement;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * Automatically creates initial cohort definitions, dimensions, and indicators
 */
@Controller
@RequestMapping("/module/reporting/reports/createInitial")
public class CreateInitialController {

	@RequestMapping(method=RequestMethod.GET)
	public void showOptions(ModelMap model) {
		/*
		{
			List<CohortDefinition> available = new ArrayList<CohortDefinition>();
			List<CohortDefinition> already = new ArrayList<CohortDefinition>();
			Set<String> alreadyNames = new HashSet<String>();
			for (CohortDefinition def : Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(true)) {
				alreadyNames.add(def.getName());
			}
			for (CohortDefinition def : ReportUtil.getInitialCohortDefinitions()) {
				if (alreadyNames.contains(def.getName()))
					already.add(def);
				else
					available.add(def);
			}
			model.addAttribute("cohortDefinitions", available);
			model.addAttribute("cohortDefinitionsAlready", already);
		}
		{
			List<Dimension> available = new ArrayList<Dimension>();
			List<Dimension> already = new ArrayList<Dimension>();
			Set<String> alreadyNames = new HashSet<String>();
			for (Dimension def : Context.getService(IndicatorService.class).getAllDimensions(true)) {
				alreadyNames.add(def.getName());
			}
			for (Dimension def : ReportUtil.getInitialDimensions()) {
				if (alreadyNames.contains(def.getName()))
					already.add(def);
				else
					available.add(def);
			}
			model.addAttribute("dimensions", available);
			model.addAttribute("dimensionsAlready", already);
		}
		
		model.addAttribute("indicators", ReportUtil.getInitialIndicators());
		*/
		Set<String> alreadyNames = new HashSet<String>();
		for (CohortDefinition def : Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(true)) {
			alreadyNames.add(CohortDefinition.class.getName() + " " + def.getName());
		}
		for (Dimension def : Context.getService(IndicatorService.class).getAllDimensions(true)) {
			alreadyNames.add(Dimension.class.getName() + " " + def.getName());
		}
		for (Indicator def : Context.getService(IndicatorService.class).getAllIndicators(true)) {
			alreadyNames.add(Indicator.class.getName() + " " + def.getName());
		}
		
		List<InitialDataElement> toCreate = ReportUtil.getInitialDataElements();
		List<InitialDataElement> already = new ArrayList<InitialDataElement>();
		for (Iterator<InitialDataElement> i = toCreate.iterator(); i.hasNext(); ) {
			InitialDataElement e = i.next();
			if (alreadyNames.contains(e.getClazz().getName() + " " + e.getName())) {
				i.remove();
				already.add(e);
			}
		}

		model.addAttribute("toCreate", toCreate);
		model.addAttribute("already", already);
	}
	
	
	@RequestMapping(method=RequestMethod.POST)
	public String handleSubmit(WebRequest request,
	                           @RequestParam(value="create", required=false) Set<String> toCreate) {
		List<String> errorMessages = new ArrayList<String>();
		for (InitialDataElement e : ReportUtil.getInitialDataElements()) {
			if (toCreate.contains(e.getClazz().getName() + " " + e.getName())) {
				try {
					e.apply();
				} catch (Exception ex) {
					errorMessages.add(ex.getMessage());
				}
			}
		}
		
		if (errorMessages.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (String err : errorMessages) {
				sb.append(err).append("<br/>");
			}
			request.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, sb.toString(), WebRequest.SCOPE_SESSION);
		}

		return "redirect:createInitial.form";
	}
		
}