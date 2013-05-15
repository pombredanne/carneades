// Copyright (c) 2012 Fraunhofer Gesellschaft
// Licensed under the EUPL V.1.1

goog.provide('carneades.policy_analysis.web.arguments');

PM.arguments_url = function(db) {
    return 'arguments/outline/' + IMPACT.project + '/' + db;    
};

PM.set_arguments_url = function(db) {
    $.address.value(PM.arguments_url(db));  
};

// this is the main entry point to display
// either the outline, the map, an argument or a statement
PM.display_arguments = function(project, db, type, id) {
    PM.load_project(project);

    if(_.isNil(db)) {
        db = IMPACT.db;
    }
    if(_.isNil(db) || db == undefined || db == 'undefined') {
        $('#pm').html(arguments_html.filter("#arguments"));
        $('#pm').append('<div>Please enter some facts to see the arguments.</div>');
        PM.activate('#arguments-item');
        PM.attach_lang_listener();
        
        return;
    }
    
    // PM.show_menu({text: PM.project.get('title'),
    //               link: "#/project/" + project},
    //              PM.agb_menu(db));

    var arguments_html = ich.arguments(PM.merge_menu_props({}));
    
    IMPACT.db = db;
    
    if(!_.isNil(project)) {
        IMPACT.project = project;
    }
    
    $('#pm').html(arguments_html.filter("#arguments"));
    PM.activate('#arguments-item');
    PM.attach_lang_listener();

    if (type == "statement")  {
        AGB.display_statement(db, id);
    } else if(type == "argument") {
        AGB.display_argument(db, id);
    } else if(type == "map") {
        AGB.display_map(db);
    } else if (type == "vote") {
        carneades.policy_analysis.web.views.pmt.vote.display();
    } else if(type == "export") { 
        PM.export_ag(db);
        AGB.display_argumentgraph(db);
    } else if (type == "evaluate") {
        AGB.evaluate(function () {
            PM.set_arguments_url(db);
        });
    } else if (type == "copy-case") {
        PM.copy_case(db);
    } else { // outline
        AGB.display_argumentgraph(db);        
    }  

};

PM.export_ag = function(db) {
    window.open('/carneadesws/export/{0}/{1}'.format(IMPACT.project, db), 'CAF XML');
};

PM.current_mainissueatompredicate = function() {
    var current_issue = PM.current_issue();
    if(current_issue == undefined) {
        return undefined;
    }
    var match = current_issue.atom.match(/\(([^ ]+) /);
    var mainissueatompredicate = "";
    if(match) {
        mainissueatompredicate = match[1];
    } else {
        return undefined;
    }

    return mainissueatompredicate;
};

PM.current_case_pollid = function() {
    var pollid = PM.get_cookies()['pollid-' + IMPACT.db];
    if(!pollid) {
        return undefined;
    }

    return parseInt(pollid, 10);
};

PM.copy_case = function(db) {
    if(confirm($.i18n.prop('pmt_copy_current_case'))) {
        PM.ajax_post(IMPACT.wsurl + '/copy-case/' + IMPACT.project + '/' + db,
                     {},
                     function(data) {
                         PM.set_arguments_url(data.db);
                         PM.notify($.i18n.prop('pmt_now_viewing_copy'));
                     },
                     IMPACT.user,
                     IMPACT.password,
                     PM.on_error
                   );
    } else {
        PM.set_arguments_url(db);
    }
};
