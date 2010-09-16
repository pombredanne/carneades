/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SearchOptionsDialog.java
 *
 * Created on Sep 16, 2010, 4:19:28 PM
 */

package carneades.editor.uicomponents;

/**
 *
 * @author pal
 */
public class SearchOptionsDialog extends javax.swing.JDialog {

    /** Creates new form SearchOptionsDialog */
    public SearchOptionsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
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

        searchInButtonGroup = new javax.swing.ButtonGroup();
        searchForPanel = new javax.swing.JPanel();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        searchInPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Search Options");

        cancelbutton.setText("Cancel");

        okbutton.setText("OK");

        searchForPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Search for:"));

        jCheckBox3.setSelected(true);
        jCheckBox3.setText("Statements");

        jCheckBox4.setSelected(true);
        jCheckBox4.setText("Arguments");

        javax.swing.GroupLayout searchForPanelLayout = new javax.swing.GroupLayout(searchForPanel);
        searchForPanel.setLayout(searchForPanelLayout);
        searchForPanelLayout.setHorizontalGroup(
            searchForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchForPanelLayout.createSequentialGroup()
                .addComponent(jCheckBox3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox4)
                .addContainerGap(93, Short.MAX_VALUE))
        );
        searchForPanelLayout.setVerticalGroup(
            searchForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jCheckBox3)
                .addComponent(jCheckBox4))
        );

        searchInPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Search in:"));

        searchInButtonGroup.add(searchInCurrentGraphButton);
        searchInCurrentGraphButton.setSelected(true);
        searchInCurrentGraphButton.setText("Current graph");

        searchInButtonGroup.add(searchInAllLkifFilesButton);
        searchInAllLkifFilesButton.setText("All LKIF files");

        javax.swing.GroupLayout searchInPanelLayout = new javax.swing.GroupLayout(searchInPanel);
        searchInPanel.setLayout(searchInPanelLayout);
        searchInPanelLayout.setHorizontalGroup(
            searchInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchInPanelLayout.createSequentialGroup()
                .addComponent(searchInCurrentGraphButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchInAllLkifFilesButton)
                .addContainerGap(84, Short.MAX_VALUE))
        );
        searchInPanelLayout.setVerticalGroup(
            searchInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(searchInCurrentGraphButton)
                .addComponent(searchInAllLkifFilesButton))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(162, Short.MAX_VALUE)
                .addComponent(okbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchForPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchInPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchForPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchInPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelbutton)
                    .addComponent(okbutton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SearchOptionsDialog dialog = new SearchOptionsDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public final javax.swing.JButton cancelbutton = new javax.swing.JButton();
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    public final javax.swing.JButton okbutton = new javax.swing.JButton();
    private javax.swing.JPanel searchForPanel;
    public final javax.swing.JRadioButton searchInAllLkifFilesButton = new javax.swing.JRadioButton();
    private javax.swing.ButtonGroup searchInButtonGroup;
    public final javax.swing.JRadioButton searchInCurrentGraphButton = new javax.swing.JRadioButton();
    private javax.swing.JPanel searchInPanel;
    // End of variables declaration//GEN-END:variables

}
