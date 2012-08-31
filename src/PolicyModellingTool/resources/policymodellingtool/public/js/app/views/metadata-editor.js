// A view to edit metadata
PM.MetadataEditorView = Backbone.View.extend(
    {className: "metadata-editor-view",
     
     events: {
         "blur .metadata-description-input": "description_changed",
         "click .add-metadata-element": "on_add_metadata_element"
     },
     
     initialize: function() {
         this.model.on('change', this.render, this);
         _.bindAll(this, 'render', 'description_changed', 'on_add_metadata_element');
         this.model.store();
         this.elements = {key: "Key",
                         contributor: "Contributor",
                         coverage: "Coverage",
                         creator: "Creator",
                         date: "Date",
                         identifier: "Identifier",
                         language: "Language",
                         publisher: "Publisher",
                         relation: "Relation",
                         rights: "Rights",
                         source: "Source",
                         subject: "Subject",
                         title: "Title",
                         type: "Type"             
                        }; 
     },
     
     render: function() {
         var data = this.model.toJSON();

         this.$el.html(ich.metadataeditor2({description: "Description:",
                                            adda: "Add a",
                                            totheheader: "to the header",
                                            go: "Add"}));
         
         if(data.metadata.attributes.description) {
             this.description().val(
                 data.metadata.attributes.description[data.current_lang]); 
         } 

         this.$('.metadata-description-input').markItUp(mySettings);

         var self = this;
         _.each(data.metadata.attributes,
                function(elements, type) {
                    if(type != 'description') {
                        _.each(elements,
                              function(val) {
                                  self.add_metadata_element(type, val); 
                              });
                    }
                });
         
         _.each(this.elements,
               function(text, val) {
                  self.$('.select-metadata-element').
                       append('<option value="' + val + '">' + text + '</option>');
               });
         
         return this;
     },

     description: function() {
         return this.$('.metadata-description-input');
     },
     
     remove: function() {
         this.model.restore();
         Backbone.View.prototype.remove.call(this);
     },

     description_changed: function() {
         var metadata = _.clone(this.model.get('metadata'));
         metadata.get('description')[this.model.get('current_lang')] 
             = this.description().val();
         this.model.set('metadata', metadata);
         
     },
     
     add_metadata_element: function(type, val) {
         var element_view = new PM.MetadataElementEditorView(
             {model: this.model,
              type: type,
              name: this.elements[type]});
         if(val) {
             this.model.index_element(type, element_view.cid, val);
         }
         element_view.render();
         this.$('.metadata-elements').append(element_view.$el);
     },
     
     on_add_metadata_element: function() {
         var type = this.$('.select-metadata-element').val();
         this.add_metadata_element(type);
     }

    }
);
