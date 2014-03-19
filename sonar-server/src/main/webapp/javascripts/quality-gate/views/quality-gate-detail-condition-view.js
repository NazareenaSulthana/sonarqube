// Generated by CoffeeScript 1.7.1
(function() {
  var __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define(['backbone.marionette', 'handlebars'], function(Marionette, Handlebars) {
    var QualityGateDetailConditionView;
    return QualityGateDetailConditionView = (function(_super) {
      __extends(QualityGateDetailConditionView, _super);

      function QualityGateDetailConditionView() {
        return QualityGateDetailConditionView.__super__.constructor.apply(this, arguments);
      }

      QualityGateDetailConditionView.prototype.tagName = 'tr';

      QualityGateDetailConditionView.prototype.template = Handlebars.compile(jQuery('#quality-gate-detail-condition-template').html());

      QualityGateDetailConditionView.prototype.spinner = '<i class="spinner"></i>';

      QualityGateDetailConditionView.prototype.modelEvents = {
        'change:id': 'render'
      };

      QualityGateDetailConditionView.prototype.ui = {
        periodSelect: '[name=period]',
        operatorSelect: '[name=operator]',
        warningInput: '[name=warning]',
        errorInput: '[name=error]',
        actionsBox: '.quality-gate-condition-actions',
        updateButton: '.update-condition'
      };

      QualityGateDetailConditionView.prototype.events = {
        'click @ui.updateButton': 'saveCondition',
        'click .delete-condition': 'deleteCondition',
        'click .add-condition': 'saveCondition',
        'click .cancel-add-condition': 'cancelAddCondition',
        'keyup :input': 'enableUpdate',
        'change :input': 'enableUpdate'
      };

      QualityGateDetailConditionView.prototype.initialize = function() {
        return this.populateMetric();
      };

      QualityGateDetailConditionView.prototype.populateMetric = function() {
        var metric, metricKey;
        metricKey = this.model.get('metric');
        metric = _.findWhere(this.options.app.metrics, {
          key: metricKey
        });
        this.model.set({
          metric: metric
        }, {
          silent: true
        });
        return this.model.set({
          isDiffMetric: metric.key.indexOf('new_') === 0
        }, {
          silent: true
        });
      };

      QualityGateDetailConditionView.prototype.onRender = function() {
        this.ui.periodSelect.val(this.model.get('period') || '0');
        this.ui.operatorSelect.val(this.model.get('op'));
        this.ui.warningInput.val(this.model.get('warning'));
        this.ui.errorInput.val(this.model.get('error'));
        this.ui.periodSelect.select2({
          allowClear: false,
          minimumResultsForSearch: 999,
          width: '200px'
        });
        this.ui.operatorSelect.select2({
          allowClear: false,
          minimumResultsForSearch: 999,
          width: '150px'
        });
        if (this.model.isNew()) {
          return this.ui.periodSelect.select2('open');
        }
      };

      QualityGateDetailConditionView.prototype.showSpinner = function() {
        jQuery(this.spinner).prependTo(this.ui.actionsBox);
        return this.ui.actionsBox.find(':not(.spinner)').hide();
      };

      QualityGateDetailConditionView.prototype.hideSpinner = function() {
        this.ui.actionsBox.find('.spinner').remove();
        return this.ui.actionsBox.find(':not(.spinner)').show();
      };

      QualityGateDetailConditionView.prototype.saveCondition = function() {
        this.showSpinner();
        this.model.set({
          period: this.ui.periodSelect.val(),
          op: this.ui.operatorSelect.val(),
          warning: this.ui.warningInput.val(),
          error: this.ui.errorInput.val()
        });
        return this.model.save().always((function(_this) {
          return function() {
            _this.ui.updateButton.prop('disabled', true);
            return _this.hideSpinner();
          };
        })(this)).done((function(_this) {
          return function() {
            return _this.options.collectionView.updateConditions();
          };
        })(this));
      };

      QualityGateDetailConditionView.prototype.deleteCondition = function() {
        if (confirm(t('quality_gates.delete_condition.confirm.message'))) {
          this.showSpinner();
          return this.model["delete"]().done((function(_this) {
            return function() {
              _this.options.collectionView.collection.remove(_this.model);
              _this.options.collectionView.updateConditions();
              return _this.close();
            };
          })(this));
        }
      };

      QualityGateDetailConditionView.prototype.cancelAddCondition = function() {
        return this.close();
      };

      QualityGateDetailConditionView.prototype.enableUpdate = function() {
        return this.ui.updateButton.prop('disabled', false);
      };

      QualityGateDetailConditionView.prototype.serializeData = function() {
        var period;
        period = _.findWhere(this.options.app.periods, {
          key: this.model.get('period')
        });
        return _.extend(QualityGateDetailConditionView.__super__.serializeData.apply(this, arguments), {
          canEdit: this.options.app.canEdit,
          periods: this.options.app.periods,
          periodText: period != null ? period.text : void 0
        });
      };

      return QualityGateDetailConditionView;

    })(Marionette.ItemView);
  });

}).call(this);
