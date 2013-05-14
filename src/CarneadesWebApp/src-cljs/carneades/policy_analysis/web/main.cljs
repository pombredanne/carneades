(ns carneades.policy-analysis.web.main
  ;; makes Cljsbuild 0.3.0 happy
  (:require ;; it seems minus characters are not correctly mapped to underscore
            ;; when referencing a JS file
            [carneades.policy_analysis.web.agb.statement_editor]
            [carneades.policy_analysis.web.agb.argument]
            [carneades.policy_analysis.web.agb.premise]
            [carneades.policy_analysis.web.agb.statement]
            [carneades.policy_analysis.web.agb.argumentgraph]
            [carneades.policy_analysis.web.agb.metadata]
            [carneades.policy_analysis.web.agb.agb_utils]
            [carneades.policy_analysis.web.agb.map]
            [carneades.policy_analysis.web.ajax]
            [carneades.policy_analysis.web.issues]
            [carneades.policy_analysis.web.collections.schemes]
            [carneades.policy_analysis.web.collections.statements]
            [carneades.policy_analysis.web.collections.statements_info]
            [carneades.policy_analysis.web.collections.statement_polls]
            [carneades.policy_analysis.web.collections.premises_candidates]
            [carneades.policy_analysis.web.collections.argument_polls]
            [carneades.policy_analysis.web.collections.arguments]
            [carneades.policy_analysis.web.collections.arguments_info]
            [carneades.policy_analysis.web.collections.metadata_list]
            [carneades.policy_analysis.web.collections.projects]
            [carneades.policy_analysis.web.introduction]
            [carneades.policy_analysis.web.admin]
            [carneades.policy_analysis.web.menu]
            [carneades.policy_analysis.web.arguments]
            [carneades.policy_analysis.web.facts]
            [carneades.policy_analysis.web.metadata]
            [carneades.policy_analysis.web.models.argument]
            [carneades.policy_analysis.web.models.statement]
            [carneades.policy_analysis.web.models.scheme]
            [carneades.policy_analysis.web.models.metadata_candidate]
            [carneades.policy_analysis.web.models.theory]
            [carneades.policy_analysis.web.models.debate_poll]
            [carneades.policy_analysis.web.models.argument_poll]
            [carneades.policy_analysis.web.models.conclusion_candidate]
            [carneades.policy_analysis.web.policies]
            [carneades.policy_analysis.web.models.ag_info]
            [carneades.policy_analysis.web.models.premise_candidate]
            [carneades.policy_analysis.web.models.metadata]
            [carneades.policy_analysis.web.models.sct]
            [carneades.policy_analysis.web.models.scheme_candidate]
            [carneades.policy_analysis.web.models.argument_candidate]
            [carneades.policy_analysis.web.models.statement_poll]
            [carneades.policy_analysis.web.models.project]
            [carneades.policy_analysis.web.models.statement_info]
            [carneades.policy_analysis.web.models.argument_info]
            [carneades.policy_analysis.web.utils]
            [carneades.policy_analysis.web.markdown]
            [carneades.policy_analysis.web.views.metadata_editor]
            [carneades.policy_analysis.web.views.sct_question]
            [carneades.policy_analysis.web.views.argument_editor]
            [carneades.policy_analysis.web.views.sct_issues]
            [carneades.policy_analysis.web.views.premises_candidates]
            [carneades.policy_analysis.web.views.theory]
            [carneades.policy_analysis.web.views.sct_helper]
            [carneades.policy_analysis.web.views.conclusion_candidate]
            [carneades.policy_analysis.web.views.formatting_helper]
            [carneades.policy_analysis.web.views.premise_candidate]
            [carneades.policy_analysis.web.views.metadata_element_editor]
            [carneades.policy_analysis.web.views.sct_claim]
            [carneades.policy_analysis.web.views.scheme_candidate]
            [carneades.policy_analysis.web.views.metadata_helper]
            [carneades.policy_analysis.web.views.scheme_helper]
            [carneades.policy_analysis.web.views.sct_intro]

            ))
