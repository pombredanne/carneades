// A view to represent a theory
PM.TheoryView = Backbone.View.extend(
    {className: "theory-view",
     
     events: {
     },
     
     initialize: function(attrs) {
         this.model.on('change', this.render, this);
         this.current_scheme = attrs.current_scheme;
//         _.bindAll(this, 'render', 'funcname');
     },
     
     render: function() {
         var data = this.model.toJSON();

         if(data.header.description && data.header.description[IMPACT.lang]) {
             data.description_text = data.header.description[IMPACT.lang];    
         }
         
         data.outline_text = PM.theory_outline_text(data.schemes, 'schemes');
         data.table_of_contents = "Table of contents";
         data.schemes_text = this.schemes_text();
         
         this.$el.html(ich.theory(data));
         
         if(this.current_scheme != undefined) {
             $.scrollTo($('#' + this.current_scheme));
         }
         
         return this;
     },
     
     schemes_text: function() {
         var data = this.model.toJSON();
         var text = "";
         
         _.each(data.schemes, function(scheme) {
                    text += '<div id="{0}">'.format(scheme.id);
                    text += '<h3>{0}</h3>'.format(scheme.header.title);
                    if(scheme.header.description && scheme.header.description[IMPACT.lang]) {
                        text += '<p>{0}</p>'.format(PM.markdown_to_html(schemes.header.description[IMPACT.lang]));
                    }
                    PM.set_metadata_has_properties(scheme.header);
                    scheme.header.header_hastitle = false;
                    // get the whole html, see http://jquery-howto.blogspot.de/2009/02/how-to-get-full-html-string-including.html
                    var md = ($('<div>').append(ich.metadata(scheme.header))).remove().html();
                    text += '<p>{0}</p>'.format(md);
                    text += PM.markdown_to_html(PM.scheme_content_text(data.language, scheme));
                    text += '</div>';
                });

         return text;
     }
     
    }
);
