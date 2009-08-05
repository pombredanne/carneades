/*
 * StandardView.fx
 *
 * Created on 24.06.2009, 12:37:59
 */

package carneadesgui.view;

import javafx.scene.paint.Color;

// general imports
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Alert;

// control imports
import carneadesgui.GC.*;

// model imports
import carneadesgui.model.Argument;
import carneadesgui.model.Argument.*;

// view imports
import carneadesgui.view.InspectorPanel;
import carneadesgui.view.MasterEditButtonPanel;
import carneadesgui.view.GraphListView;
import carneadesgui.view.ToolBar;
import carneadesgui.view.GraphUpdate;


/**
* The standard GUI view that is currently used.
*/
public class StandardView extends CarneadesView {

	var graphPanel: GraphPanel = GraphPanel {
		constraintX: inspectorPanelWidth + mainPanelSpacing
		constraintY: toolBarHeight
		graph: bind currentGraph
		control: bind control
	}

	var graphListView: GraphListView = GraphListView {
		control: bind control
		view: bind this
	}


	var masterEditButtonPanel: MasterEditButtonPanel = MasterEditButtonPanel {
		mode: bind mode
		control: bind control
	}

	var inspectorPanel: InspectorPanel = InspectorPanel {
		mode: bind mode
		control: bind control
	};

	var toolBar: ToolBar = ToolBar {
		control: bind control
	}

	override function update(u: GraphUpdate) {
		if (u.changedAttribute or u.selection or u.listSelection)
			inspectorPanel.update(u);
		if (u.selection or u.graphSelection or u.listView)
			graphListView.update(u);
	}

	override function focusOn(e: GraphElement) {
		graphPanel.focusOn(e);
	}

	override function editStatement(s: Statement) {
		mode = inspectorStatementMode;
		inspectorPanel.editStatement(s);
	}

	override function editArgument(a: Argument) {
		mode = inspectorArgumentMode;
		inspectorPanel.editArgument(a);
	}

	override function editPremise(p: Premise) {
		mode = inspectorPremiseMode;
		inspectorPanel.editPremise(p);
	}

	override function editGraph(a: ArgumentGraph) {
		mode = inspectorGraphMode;
		inspectorPanel.editGraph(a);
	}

	override function editNothing() {
		inspectorPanel.reset();
	}

	override function alert(t: String) {
		Alert.inform(t);
	}

	override function unSelectAll() {
		graphListView.unSelectAll();
	}

	var rightSideBar: VBox = VBox {
		layoutInfo: LayoutInfo {
			width: bind inspectorPanelWidth
			height: bind appHeight - toolBarHeight
		}
		content: bind [
			graphListView,
			masterEditButtonPanel,
			inspectorPanel
		]
	}

	override var view = Stage {
		title: "Carneades GUI Classic"
		width: bind appWidth with inverse
		height: bind appHeight with inverse
		visible: bind active
		scene: Scene {
			fill: Color.BLACK
			content: [
				LayoutRect {
					fill: panelBackground
					width: bind appWidth
					height: bind appHeight - verticalWindowMismatch
				},
				// the vertical box containing the (optional) upper toolbar and the rest of the GUI.
				VBox {
					spacing: mainPanelSpacing
					content: bind [
						toolBar,
						// the horizontal layout of display and inspectors.
						HBox {
							layoutInfo: LayoutInfo {
								width: bind appWidth
								height: bind appHeight - toolBarHeight
							}
							spacing: mainPanelSpacing
							content: bind [
								graphPanel,
								rightSideBar
							]
						}
					]
				}
			]
		}
	}
}

