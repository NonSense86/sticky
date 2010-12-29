/* Copyright (c) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.appengine.demos.sticky.client;

import com.google.appengine.demos.sticky.client.model.Comment;
import com.google.appengine.demos.sticky.client.model.Model;
import com.google.appengine.demos.sticky.client.model.Note;
import com.google.appengine.demos.sticky.client.model.Photo;
import com.google.appengine.demos.sticky.client.model.Surface;
import com.google.appengine.demos.sticky.client.model.Transformation;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A widget for displaying the list of available surfaces to a user and for
 * adding new surfaces.
 *
 */
public class SurfaceListView extends FlowPanel implements Model.DataObserver {
  /**
   * Declaration of image bundle resources used in this widget.
   */
  public interface Images extends ImageBundle {
    @Resource("surface-list-add-hv.gif")
    AbstractImagePrototype surfaceListAddSurfaceButtonHv();

    @Resource("surface-list-add-up.gif")
    AbstractImagePrototype surfaceListAddSurfaceButtonUp();
  }

  /**
   * Provides a way to react to events generated by a {@link SurfaceListView}.
   */
  public interface Observer {

    /**
     * Invoked when the {@link SurfaceListView} is hidden with a call to
     * {@link SurfaceListView#hide()}.
     */
    void onHide();

    /**
     * Invoked when the {@link SurfaceListView} is shown with a call to
     * {@link SurfaceListView#show()}.
     */
    void onShow();
  }

  /**
   * A widget that provides inline editing when the user adds a new surface.
   */
  private class EditView extends FlowPanel implements BlurHandler,
      KeyPressHandler {
    private final TextBox titleTextBox;

    /**
     *
     */
    public EditView() {
      final Document document = Document.get();
      final Element element = getElement();

      final Element rowElement = element.appendChild(document
          .createDivElement());
      rowElement.setClassName("surface-item-edit");

      titleTextBox = new TextBox();
      titleTextBox.setStyleName("surface-item-title");
      add(titleTextBox, rowElement.<com.google.gwt.user.client.Element> cast());
      titleTextBox.addBlurHandler(this);
      titleTextBox.addKeyPressHandler(this);

      final Element authorsElement = rowElement.appendChild(document
          .createDivElement());
      authorsElement.setClassName("surface-item-authors");
      authorsElement.setInnerText("/w Only You.");

      SurfaceListView.this.add(this, listElement
          .<com.google.gwt.user.client.Element> cast());

      titleTextBox.setFocus(true);
    }

    public void onBlur(BlurEvent event) {
      commit();
    }

    public void onKeyPress(KeyPressEvent event) {
      switch (event.getCharCode()) {
      case KeyCodes.KEY_ENTER:
        commit();
        break;
      case KeyCodes.KEY_ESCAPE:
        break;
      }
    }

    private void commit() {
      final String value = titleTextBox.getValue().trim();
      SurfaceListView.this.remove(this);
      if (value.length() > 0) {
        model.createSurface(value);
        hide();
      }
    }
  }

  /**
   * A widget to display information about a surface in a list.
   */
  private class ItemView extends SimplePanel implements ClickHandler {
    private final Element titleElement, authorsElement, noteCountElement;

    private final Surface surface;

    /**
     * @param surface
     *          the surface to be displayed
     */
    public ItemView(Surface surface) {
      this.surface = surface;
      final Document document = Document.get();
      final Element element = getElement();
      final Element rowElement = element.appendChild(document
          .createDivElement());
      titleElement = rowElement.appendChild(document.createSpanElement());
      noteCountElement = rowElement.appendChild(document.createSpanElement());
      authorsElement = rowElement.appendChild(document.createDivElement());

      rowElement.setClassName("surface-item" + getNextStyleNameSuffix());
      titleElement.setClassName("surface-item-title");
      noteCountElement.setClassName("surface-item-count");
      authorsElement.setClassName("surface-item-authors");

      titleElement.setInnerText(surface.getTitle());
      authorsElement.setInnerText(surface.getAuthorNamesAsString());
      noteCountElement.setInnerText("(" + surface.getNoteCount() + " notes)");

      addDomHandler(this, ClickEvent.getType());

      // Add to parent.
      SurfaceListView.this.add(this, listElement
          .<com.google.gwt.user.client.Element> cast());
    }

    public void onClick(ClickEvent event) {
      model.selectSurface(surface);
      hide();
    }
  }

  private final DivElement listElement;

  private int count;

  private final Model model;

  private final Observer observer;

  /**
   * @param model
   *          the model to which the Ui will bind itself
   * @param observer
   *          a single observer to receive callbacks from this object
   */
  public SurfaceListView(Images images, Model model, Observer observer) {
    this.model = model;
    this.observer = observer;

    setVisible(false);
    setStyleName("surface-list");

    final Element element = getElement();
    final Element titleElement = element.appendChild(Document.get()
        .createDivElement());
    titleElement.setClassName("surface-list-title");
    titleElement.setInnerText("Your Sticky Surfaces");

    final Element innerElement = element.appendChild(Document.get()
        .createDivElement());
    innerElement.setClassName("surface-list-list");

    listElement = innerElement.appendChild(Document.get().createDivElement());

    add(Buttons.createPushButtonWithImageStates(images
        .surfaceListAddSurfaceButtonUp().createImage(), images
        .surfaceListAddSurfaceButtonHv().createImage(), "surface-list-add",
        new ClickHandler() {
          public void onClick(ClickEvent event) {
            new EditView();
          }
        }), innerElement.<com.google.gwt.user.client.Element> cast());

    model.addDataObserver(this);
  }

  /**
   * Hides the Ui for this widget and notifies the {@link Observer} accordingly.
   */
  public void hide() {
    setVisible(false);
    observer.onHide();
  }

  public void onNoteCreated(Note note) {
  }

  public void onSurfaceCreated(Surface surface) {
    new ItemView(surface);
  }

  public void onSurfaceNotesReceived(Note[] notes) {
  }

  public void onSurfaceSelected(Surface nowSelected, Surface wasSelected) {
  }

  public void onSurfacesReceived(Surface[] surfaces) {
    for (int i = 0, n = surfaces.length; i < n; i++) {
      new ItemView(surfaces[i]);
    }
  }

  /**
   * Makes the Ui for this widget visible and notifies the {@link Observer}.
   */
  public void show() {
    setVisible(true);
  }

  private String getNextStyleNameSuffix() {
    return ((count++ & 1) == 0) ? "-odd" : "-even";
  }

@Override
public void onCommentAdded(Comment comment) {
    // TODO Auto-generated method stub
    
}

@Override
public void onPhotoTransform(Photo photo, Transformation transformation) {
	// TODO Auto-generated method stub
	
}
}
