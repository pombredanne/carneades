PM.scheme_text = function(scheme) {
    if(_.isNil(scheme)) {
        return "";
    }
    var match = scheme.match(/[^( ]+ /);
    if(match) {
        return UTILS.escape_html(match[0]);      
    }
    return UTILS.escape_html(scheme);
  
};

PM.display_schemes = function(scheme_id) {
    var theory_view = new PM.TheoryView({model: PM.current_theory, 
                                         current_scheme: scheme_id});
    
    if(PM.current_theory.get('schemes') == undefined) {
        PM.current_theory.fetch({success: function() {
                            theory_view.render();  
                          },
                          error: PM.on_model_error});
    } else {
        theory_view.render();
    }
    
    $('#pm').html(theory_view.$el);
};