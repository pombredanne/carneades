// Copyright (c) 2012 Fraunhofer Gesellschaft
// Licensed under the EUPL V.1.1

goog.provide('carneades.policy_analysis.web.markdown');

PM.markdown_to_html = function(md_text) {
    if(md_text == null) {
        return "";
    }
    var converter = Markdown.getSanitizingConverter(); 
    var html = converter.makeHtml(md_text);
    // as a temporary fix, we remove <p> tags causing problem
    // with the CSS styles
    // if(html.slice(0, 3) == "<p>") {
    //     html = html.slice(3, -4);
    // }
    return html;  
};
