package com.google.appengine.demos.sticky.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.demos.sticky.client.model.Model;
import com.google.appengine.demos.sticky.client.model.Note;
import com.google.appengine.demos.sticky.client.model.Transformation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ImageBundle;

public class PhotoTransformView extends HorizontalPanel {
    
    private static final Logger LOG = Logger.getLogger(PhotoTransformView.class.toString());

	/**
	 * Declaration of image bundle resources used in this widget.
	 */
	@SuppressWarnings("deprecation")
	public interface Images extends ImageBundle {
		@Resource("crop_icon.gif")
		AbstractImagePrototype headerImageCrop();

		@Resource("flip_horizontal_icon.gif")
		AbstractImagePrototype headerImageFlipH();

		@Resource("flip_vertical_icon.gif")
		AbstractImagePrototype headerImageFlipV();

		@Resource("rotate_90_c_icon.gif")
		AbstractImagePrototype headerImageRotateC();

		@Resource("rotate_90_cc_icon.gif")
		AbstractImagePrototype headerImageRotateCc();
		
		@Resource("i_am_feeling_lucky_icon.gif")
		AbstractImagePrototype headerImageIAmFeelingLucky();
	}

	final private Note note;

	final private Model model;

    private Callback callback;

	public PhotoTransformView(final Model model, final Note note, Callback callback) {

		this.model = model;
		this.note = note;
		this.callback = callback;
		//System.out.println("Created photoTransformView!");
		LOG.log(Level.INFO, "Hash: " + this.note.getHashCode());
		if (this.note.getAuthorName().equals("You") && this.note.getHashCode() != null && this.note.getHashCode() != 0) {
			//System.out.println("I am the owner...");
		    
			final Element elem = getElement();
			elem.setId("image-panel");
			final Images images = GWT.create(Images.class);

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageIAmFeelingLucky().createImage(), "lucky-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							model.updateNoteImage(PhotoTransformView.this.note, PhotoTransformView.this.note.getHashCode(),
									Transformation.LUCKY);
							PhotoTransformView.this.callback.callback();
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageFlipH().createImage(), "flip-h-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
						    model.updateNoteImage(PhotoTransformView.this.note, PhotoTransformView.this.note.getHashCode(),
									Transformation.FLIP_H);
						    PhotoTransformView.this.callback.callback();
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageFlipV().createImage(), "flip-v-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
						    model.updateNoteImage(PhotoTransformView.this.note, PhotoTransformView.this.note.getHashCode(),
									Transformation.FLIP_V);
						    PhotoTransformView.this.callback.callback();
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageRotateC().createImage(), "rotate-c-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
						    model.updateNoteImage(PhotoTransformView.this.note, PhotoTransformView.this.note.getHashCode(),
									Transformation.ROT_C);
						    PhotoTransformView.this.callback.callback();
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageRotateCc().createImage(), "rotate-cc-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
						    model.updateNoteImage(PhotoTransformView.this.note, PhotoTransformView.this.note.getHashCode(),
									Transformation.ROT_CC);
						    PhotoTransformView.this.callback.callback();
						}
					}));
		}
	}
}
