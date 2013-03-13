// Copyright (c) 2012 Fraunhofer Gesellschaft
// Licensed under the EUPL V.1.1

goog.provide('carneades.policy_analysis.web.premise_candidate');

PM.PremiseCandidate = Backbone.Model.extend(
    {defaults: function(){
         return {
             editableRole: true,
             premise: {},
             statements: null // a StatementsCollection  
         };
     },
     
     initialize: function(attrs) {
         var memento = new Backbone.Memento(this);
         _.extend(this, memento);
         
         // when one element of the collection changes
         // triggers a change on this model
         attrs.statements.bind('all', this.trigger_change, this);
     },
     
     trigger_change: function() {
         this.trigger('change');
     }
        
    }
);
