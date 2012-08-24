// A view to edit metadata
PM.MetadataEditorView = Backbone.View.extend(
    {className: "metadata-editor-view",
     
     events: {
         "keyup .metadata-description-input" : "description_changed"
     },
     
     initialize: function() {
         this.model.on('change', this.render, this);
         _.bindAll(this, 'render', 'description_changed');
         this.model.store();
     },
     
     render: function() {
         var data = this.model.toJSON();

         this.$el.html(ich.metadataeditor2());
         this.description().val
         (data.metadata.description[data.current_lang]);
         
         this.$('.metadata-description-input').markItUp(mySettings);
         
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
         metadata.description[this.model.get('current_lang')] = this.description().val();
         this.model.set('metadata', metadata);
         
         return false;
     },

     update_data: function() {
         this.description_changed();
     }
     
    }
);
