/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PremisePropertiesView.java
 *
 * Created on Aug 27, 2010, 3:20:08 PM
 */

package carneades.editor.uicomponents;

/**
 *
 * @author pal
 */
public class PremisePropertiesView extends javax.swing.JPanel {

    /** Creates new form PremisePropertiesView */
    public PremisePropertiesView() {
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

        proConGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        jLabel1.setText("Type:");

        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Premise", "Assumption", "Exception" }));

        negatedCheckBox.setText("Negated");

        mapTitleText.setBackground(new java.awt.Color(222, 222, 222));
        mapTitleText.setEditable(false);
        mapTitleText.setDisabledTextColor(new java.awt.Color(1, 1, 1));

        pathText.setBackground(new java.awt.Color(222, 222, 222));
        pathText.setEditable(false);
        pathText.setDisabledTextColor(new java.awt.Color(226, 210, 196));

        jLabel6.setText("Path:");

        jLabel5.setText("Graph:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pathText, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                            .addComponent(mapTitleText, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(typeComboBox, 0, 181, Short.MAX_VALUE))
                    .addComponent(negatedCheckBox))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pathText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mapTitleText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(4, 4, 4)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(negatedCheckBox)
                .addContainerGap(123, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSeparator jSeparator1;
    public final javax.swing.JTextField mapTitleText = new javax.swing.JTextField();
    public final javax.swing.JCheckBox negatedCheckBox = new javax.swing.JCheckBox();
    public final javax.swing.JTextField pathText = new javax.swing.JTextField();
    private javax.swing.ButtonGroup proConGroup;
    public final javax.swing.JComboBox typeComboBox = new javax.swing.JComboBox();
    // End of variables declaration//GEN-END:variables

    // our modifications:
    public static PremisePropertiesView viewInstance = new PremisePropertiesView();

    public static synchronized PremisePropertiesView instance()
    {
        return viewInstance;
    }

    public static synchronized void reset()
    {
        viewInstance = new PremisePropertiesView();
    }
}