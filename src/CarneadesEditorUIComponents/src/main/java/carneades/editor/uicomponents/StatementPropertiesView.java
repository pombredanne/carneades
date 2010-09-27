/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StatementPropertiesView.java
 *
 * Created on Aug 27, 2010, 11:49:02 AM
 */

package carneades.editor.uicomponents;

import java.awt.Color;
import javax.swing.UIManager;

/**
 *
 * @author pal
 */
public class StatementPropertiesView extends javax.swing.JPanel {

    /** Creates new form StatementPropertiesView */
    public StatementPropertiesView() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLabel6 = new javax.swing.JLabel();

        jLabel1.setText("Statement:");

        jLabel2.setText("Proof standard:");

        proofstandardComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dialectical Validity", "Scintilla of Evidence", "Preponderance of Evidence", "Clear and Convincing Evidence", "Beyond Reasonable Doubt" }));

        jLabel3.setText("Status:");

        statusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Stated", "Questioned", "Accepted", "Rejected" }));

        jLabel4.setText("Graph:");

        mapTitleText.setBackground(new java.awt.Color(222, 222, 222));
        mapTitleText.setEditable(false);
        mapTitleText.setDisabledTextColor(new java.awt.Color(1, 1, 1));

        jLabel5.setText("Path:");

        pathText.setBackground(new java.awt.Color(222, 222, 222));
        pathText.setEditable(false);
        pathText.setDisabledTextColor(new java.awt.Color(226, 210, 196));

        statementTextArea.setColumns(20);
        statementTextArea.setLineWrap(true);
        statementTextArea.setRows(5);
        statementTextArea.setWrapStyleWord(true);
        statementTextArea.setMinimumSize(new java.awt.Dimension(100, 50));
        jScrollPane2.setViewportView(statementTextArea);

        jLabel6.setText("Acceptability:");

        acceptableText.setBackground(new java.awt.Color(222, 222, 222));
        acceptableText.setText("Acceptable");

        complementAcceptableText.setBackground(new java.awt.Color(222, 222, 222));
        complementAcceptableText.setText("Complement Acceptable");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pathText, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                            .addComponent(mapTitleText, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(statusComboBox, 0, 235, Short.MAX_VALUE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(proofstandardComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 293, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(complementAcceptableText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                            .addComponent(acceptableText, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pathText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mapTitleText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(4, 4, 4)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(statusComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(proofstandardComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(acceptableText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(complementAcceptableText, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public final javax.swing.JTextField acceptableText = new javax.swing.JTextField();
    public final javax.swing.JTextField complementAcceptableText = new javax.swing.JTextField();
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    public final javax.swing.JTextField mapTitleText = new javax.swing.JTextField();
    public final javax.swing.JTextField pathText = new javax.swing.JTextField();
    public final javax.swing.JComboBox proofstandardComboBox = new javax.swing.JComboBox();
    public final javax.swing.JTextArea statementTextArea = new javax.swing.JTextArea();
    public final javax.swing.JComboBox statusComboBox = new javax.swing.JComboBox();
    private javax.swing.ButtonGroup statusGroup;
    // End of variables declaration//GEN-END:variables

    // our modifications:
    public static StatementPropertiesView viewInstance = new StatementPropertiesView();

    public static synchronized StatementPropertiesView instance()
    {
        return viewInstance;
    }

    public static synchronized void reset()
    {
        viewInstance = new StatementPropertiesView();
    }
}
