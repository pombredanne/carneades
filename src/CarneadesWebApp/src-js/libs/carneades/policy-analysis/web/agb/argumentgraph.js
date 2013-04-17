// Copyright (c) 2012 Fraunhofer Gesellschaft
// Licensed under the EUPL V.1.1

goog.provide('carneades.policy_analysis.web.agb.argumentgraph');

// Sets the argument graph url. This causes the argument graph to
// displayed.
AGB.set_argumentgraph_url = function(db)
{
    $.address.value(AGB.argumentgraph_url(db));    
};

// Returns the relative URL of the argument graph page
AGB.argumentgraph_url = function(db)
{
    return '/arguments/outline/' + IMPACT.project + '/' + db;
};

// Returns the HTML content of the argument graph page
AGB.argumentgraph_html = function(db, data) 
{
    AGB.normalize(data);
    data.db = db;
    data.metadata_text = AGB.format_metadata(data.metadata[0]);
    data.hasdescription = PM.has_description(data.metadata[0]);
    data.description_text = AGB.description_text(data.metadata[0]);
    AGB.set_mainissues_text(data.main_issues);
    data.references = data.metadata.filter(function (ref) { return ref.key; });
    data.hasreferences = data.references.length > 0;
    AGB.set_references_text(data.references);
    data.title = data.metadata[0].title ? data.metadata[0].title[0] : $.i18n.prop('pmt_menu_arguments');
    data.outline_text = AGB.outline_text(data.outline, db);
    data.pmt_main_issues = $.i18n.prop('pmt_main_issues');
    data.pmt_outline = $.i18n.prop('pmt_outline');
    data.pmt_references = $.i18n.prop('pmt_references');

    data = PM.merge_menu_props(data);
    data = PM.merge_ag_menu_props(data); 

    data.lang = IMPACT.lang;
    
    var argumentgraph_html = ich.argumentgraph(data);
    return argumentgraph_html.filter('#argumentgraph');
};

// Displays the argument graph page
AGB.display_argumentgraph = function(db)
{
    PM.arguments.fetch();
    PM.statements.fetch();
    
    if($.address.value() == "/arguments") {
        // forces URL with specified argument graph without pushing
        // a new value in the history
        $.address.history(false);
        AGB.set_argumentgraph_url(db);
        $.address.history(true);
        return;
    }

    PM.ajax_get(IMPACT.wsurl + '/argumentgraph-info/' + IMPACT.project + '/' + db,
                function(data) {
                    $('#browser').html(AGB.argumentgraph_html(db, data));
                    $('#export').click(
                        function (event){
                            window.open('/carneadesws/export/{0}/{1}'.format(IMPACT.project, db),
                                        'CAF XML');
                            return false; 
                        });
                    AGB.enable_ag_edition(db);
                },
                PM.on_error);
};

// Formats the text for the main issues
AGB.set_mainissues_text = function(mainissues)
{
    $.each(mainissues, 
           function(index, issue) {
               issue.statement_nb = index + 1;
               issue.statement_text = AGB.statement_text(issue);
           });
    return mainissues;
};

// Formats the text for the references
AGB.set_references_text = function(metadata)
{
    $.each(metadata, 
           function(index, md) {
               md.metadata_text = AGB.format_metadata(md);
           });
};

// Returns the text for the outline
AGB.outline_text = function(tree, db, index)
{
    var text = "";
    var node = tree[0];
    var children = tree[1];
    
    if(node === "root") {
        text = "<ul>";
    } else if (node.hasOwnProperty('premises')) {
        var direction_text = node.pro ? "pro" : "con";
        text = '{0} <span class="content">{1}</span> <ul>'.format(direction_text, AGB.argument_link(db, node.id, AGB.argument_text(node, index)));
    } else {
        var stmt_text = AGB.statement_text(node, index);
        text = "{0} <ul>".format(AGB.statement_link(db, node.id, stmt_text));
    }

    $.each(children,
           function(index, child) {
               text += "<li>{0}</li>".format(AGB.outline_text(child, db, index + 1));
           });
    text += "</ul>";

    return text;
};

// Activates the edition of the argument graph
AGB.enable_ag_edition = function(db) {
    // $('#ageditormenu').remove();
    $('#menus').append(ich.ageditormenuon({'new_statement_text': $.i18n.prop('pmt_new_statement'),
                                           'new_argument_text': $.i18n.prop('pmt_new_argument')
                                          }));
    $('#newstatement').click(_.bind(AGB.show_statement_editor, AGB,
                                    {save_callback: function() {
                                         AGB.display_argumentgraph(IMPACT.db);
                                         return false;
                                     }}));
    $('#newargument').click(AGB.new_argument);
    $('.evaluate').click(_.bind(AGB.evaluate, AGB, _.bind(AGB.display_argumentgraph, AGB, db)));
    // $('.vote').click(catb.views.pmt.vote.vote);
    
    return false;
};

// Returns true if the atom represented as a string is grounded
// (hack)
AGB.is_grounded = function(atom) {
    return atom.indexOf("?") == -1;
};

AGB.new_argument = function(conclusion) {
    var argument = undefined;
    if(conclusion) {
        argument = new PM.Argument({conclusion: conclusion});
    } else {
        argument = new PM.Argument();
    }

    var argument_candidate = new PM.ArgumentCandidate({argument: argument,
                                                       statements: PM.statements,
                                                       schemes: PM.schemes,
                                                       current_lang: IMPACT.lang});

    var argument_editor_view = new PM.ArgumentEditorView({model: argument_candidate,
                                                          title: 'New Argument'});
    
    argument_editor_view.render();
    $('#argumenteditor').html(argument_editor_view.$el);
    
    return false;
};

AGB.evaluate = function(callback) {
    PM.ajax_post(IMPACT.wsurl + '/evaluate-argument-graph/' + IMPACT.project + '/' + IMPACT.db, {},
                function(data) {
                    PM.notify('Evaluation finished');
                    if(_.isFunction(callback)) {
                        callback(); 
                    }
                },
                IMPACT.user, IMPACT.password, PM.on_error);    
};
