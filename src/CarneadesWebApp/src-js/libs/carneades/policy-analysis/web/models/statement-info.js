// Copyright (c) 2013 Fraunhofer Gesellschaft
// Licensed under the EUPL V.1.1

goog.provide('carneades.policy_analysis.web.models.statement_info');

PM.StatementInfo = Backbone.Model.extend(
    {url: function() {
         return IMPACT.wsurl + '/statement-info/' + IMPACT.project + '/' + IMPACT.db;
     }
     
    }
);

