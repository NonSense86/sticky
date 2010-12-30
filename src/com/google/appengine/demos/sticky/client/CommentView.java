package com.google.appengine.demos.sticky.client;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.demos.sticky.client.model.Author;
import com.google.appengine.demos.sticky.client.model.Comment;
import com.google.appengine.demos.sticky.client.model.Model;
import com.google.appengine.demos.sticky.client.model.Note;
import com.google.appengine.demos.sticky.client.model.Surface;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CommentView extends VerticalPanel implements KeyDownHandler, Model.DataObserver {
    
    private Note note;
    
    private Model model;
    
    private final FlexTable tblComments = new FlexTable();
    
    private final TextArea taComment = new TextArea();
    
    private List<Comment> comments = new ArrayList<Comment>();

    
    public CommentView(Model model, Note note) {
        this.model = model;
        this.note = note;
        
        this.getElement().setClassName("comment");
        this.tblComments.setTitle("Comments");
        this.taComment.getElement().setClassName("note-content");
        this.taComment.addKeyDownHandler(this);
        
        this.add(tblComments);
        this.add(taComment);
        if (note.getComments() != null) {
            for (Comment c : note.getComments()) {
                this.comments.add(c);
                this.addComment(c);
            }
        }
        this.model.addDataObserver(this);
    }
    
    @Override
    public void onKeyDown(KeyDownEvent event) {
        int keyCode = event.getNativeKeyCode();
        
        if (keyCode == KeyCodes.KEY_ENTER && !event.isShiftKeyDown()) {
            final String text = this.taComment.getText();
            if(text.trim().length() > 0) {
	            this.taComment.setText("");
	            
	            final Comment comment = new Comment(this.note.getKey(), this.model.getCurrentAuthor().getName(), text);
	           // this.addComment(comment);
	            this.comments.add(comment);
	            this.model.updateNoteContent(this.note, this.comments);
            }
        }
    }
    
    @Override
    public void onCommentAdded(Comment comment) {
        if (this.note.getKey().equals(comment.getNote())) {
            this.addComment(comment);
        }
    }
    
    @Override
    public void onNoteCreated(Note note) { }

    @Override
    public void onSurfaceCreated(Surface surface) { }

    @Override
    public void onSurfaceNotesReceived(Note[] notes) { }

    @Override
    public void onSurfaceSelected(Surface nowSelected, Surface wasSelected) { }

    @Override
    public void onSurfacesReceived(Surface[] surfaces) { }
    
    private void addComment(Comment comment) {
        int count = this.tblComments.getRowCount();
        this.tblComments.setText(count, 0, Author.getShortName(comment.getAuthor()) + ": ");
        this.tblComments.setText(count, 1, comment.getText());
    }
}