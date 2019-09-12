package com.example.salesforcemanagement.callbacks;

import com.example.salesforcemanagement.model.Note;
import com.example.salesforcemanagement.model.Notelisting;

/**
 * Created by ixi.Dv on 22/07/2018.
 */
public interface NoteEventListenerlisting {
    /**
     * call wen note clicked.
     *
     * @param note: note item
     */
    void onNoteClick(Notelisting note);

    /**
     * call wen long Click to note.
     *
     * @param note : item
     */
    void onNoteLongClick(Notelisting note);
}
