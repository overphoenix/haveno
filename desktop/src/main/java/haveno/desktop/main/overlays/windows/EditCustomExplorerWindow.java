/*
 * This file is part of Haveno.
 *
 * Haveno is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Haveno is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Haveno. If not, see <http://www.gnu.org/licenses/>.
 */

package haveno.desktop.main.overlays.windows;

import haveno.common.util.Tuple2;
import haveno.core.locale.Res;
import haveno.core.payment.validation.LengthValidator;
import haveno.core.user.BlockChainExplorer;
import haveno.core.util.validation.UrlInputValidator;
import haveno.desktop.components.AutoTooltipButton;
import haveno.desktop.components.AutoTooltipLabel;
import haveno.desktop.components.InputTextField;
import haveno.desktop.main.overlays.Overlay;
import haveno.desktop.util.Layout;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.ArrayList;

import static haveno.desktop.util.FormBuilder.addInputTextField;
import static haveno.desktop.util.FormBuilder.addMultilineLabel;
import static haveno.desktop.util.FormBuilder.addTitledGroupBg;
import static haveno.desktop.util.FormBuilder.getTopLabelWithVBox;
import static javafx.beans.binding.Bindings.createBooleanBinding;

public class EditCustomExplorerWindow extends Overlay<EditCustomExplorerWindow> {

    private InputTextField nameInputTextField, txUrlInputTextField;
    private UrlInputValidator urlInputValidator;
    private BlockChainExplorer currentExplorer;
    private ListView<BlockChainExplorer> listView;

    public EditCustomExplorerWindow(String coin,
                                    BlockChainExplorer currentExplorer,
                                    ArrayList<BlockChainExplorer> availableExplorers) {
        this.currentExplorer = currentExplorer;
        listView = new ListView<>();
        listView.setItems(FXCollections.observableArrayList(availableExplorers));
        headLine = coin + " " + Res.get("settings.preferences.editCustomExplorer.headline");
    }

    public BlockChainExplorer getEditedBlockChainExplorer() {
        return new BlockChainExplorer(nameInputTextField.getText(),
                txUrlInputTextField.getText());
    }

    @Override
    public void show() {

        width = 1000;
        createGridPane();
        addHeadLine();
        addContent();
        addButtons();

        urlInputValidator = new UrlInputValidator();
        txUrlInputTextField.setValidator(urlInputValidator);
        nameInputTextField.setValidator(new LengthValidator(1, 50));

        actionButton.disableProperty().bind(createBooleanBinding(() -> {
                    String name = nameInputTextField.getText();
                    String txUrl = txUrlInputTextField.getText();

                    // Otherwise we require that input is valid
                    return !nameInputTextField.getValidator().validate(name).isValid ||
                            !txUrlInputTextField.getValidator().validate(txUrl).isValid;
                },
                nameInputTextField.textProperty(), txUrlInputTextField.textProperty()));

        applyStyles();
        display();
    }

    @Override
    protected void createGridPane() {
        gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(64, 64, 64, 64));
        gridPane.setPrefWidth(width);
        ColumnConstraints columnConstraints1 = new ColumnConstraints();
        ColumnConstraints columnConstraints2 = new ColumnConstraints();
        columnConstraints1.setPercentWidth(45);
        columnConstraints2.setPercentWidth(55);
        gridPane.getColumnConstraints().addAll(columnConstraints1, columnConstraints2);
    }

    private void addContent() {
        Label mlm = addMultilineLabel(gridPane, rowIndex++, Res.get("settings.preferences.editCustomExplorer.description"), 0);
        GridPane.setColumnSpan(mlm, 2);
        GridPane.setMargin(mlm, new Insets(40, 0, 0, 0));

        Button button = new AutoTooltipButton(">>");
        button.setOnAction(e -> {
            BlockChainExplorer blockChainExplorer = listView.getSelectionModel().getSelectedItem();
            if (blockChainExplorer != null) {
                nameInputTextField.setText(blockChainExplorer.name);
                txUrlInputTextField.setText(blockChainExplorer.txUrl);
            }
        });
        button.setStyle("-fx-pref-width: 50px; -fx-pref-height: 30; -fx-padding: 3 3 3 3;");
        VBox vBox = new VBox(button);
        vBox.setAlignment(Pos.CENTER);
        final Tuple2<Label, VBox> topLabelWithVBox = getTopLabelWithVBox(Res.get("settings.preferences.editCustomExplorer.available"), listView);
        listView.setPrefWidth(300);
        HBox hBox = new HBox(topLabelWithVBox.second, vBox);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(20);
        hBox.setMaxHeight(200);
        gridPane.add(hBox, 0, rowIndex);
        GridPane.setColumnIndex(hBox, 0);
        GridPane.setValignment(hBox, VPos.TOP);
        GridPane.setMargin(hBox, new Insets(10, 0, 0, 0));

        listView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<BlockChainExplorer> call(ListView<BlockChainExplorer> list) {
                ListCell<BlockChainExplorer> cell = new ListCell<>() {
                    final Label label = new AutoTooltipLabel();
                    final AnchorPane pane = new AnchorPane(label);
                    @Override
                    public void updateItem(final BlockChainExplorer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            label.setText(item.name);
                            setGraphic(pane);
                        } else {
                            setGraphic(null);
                        }
                    }
                };

                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2) {
                            BlockChainExplorer blockChainExplorer = listView.getSelectionModel().getSelectedItem();
                            nameInputTextField.setText(blockChainExplorer.name);
                            txUrlInputTextField.setText(blockChainExplorer.txUrl);
                        }
                    }
                });
                return cell;
            }
        });

        GridPane autoConfirmGridPane = new GridPane();
        autoConfirmGridPane.setPrefHeight(150);
        GridPane.setMargin(autoConfirmGridPane, new Insets(10, 0, 0, 0));
        gridPane.add(autoConfirmGridPane, 1, rowIndex);
        addTitledGroupBg(autoConfirmGridPane, 0, 6, Res.get("settings.preferences.editCustomExplorer.chosen"), 0);
        int localRowIndex = 0;
        nameInputTextField = addInputTextField(autoConfirmGridPane, ++localRowIndex, Res.get("settings.preferences.editCustomExplorer.name"), Layout.FIRST_ROW_DISTANCE);
        nameInputTextField.setPrefWidth(Layout.INITIAL_WINDOW_WIDTH);
        txUrlInputTextField = addInputTextField(autoConfirmGridPane, ++localRowIndex, Res.get("settings.preferences.editCustomExplorer.txUrl"));
        nameInputTextField.setText(currentExplorer.name);
        txUrlInputTextField.setText(currentExplorer.txUrl);
    }
}
